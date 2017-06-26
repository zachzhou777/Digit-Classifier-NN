//*****************************************************************************
// Digit Classifier Neural Network
// By Zachary Zhou
//*****************************************************************************

#include "main.h"

// Debounce states used for debouncing SW1
typedef enum {
	DEBOUNCE_ONE,
	DEBOUNCE_1ST_ZERO,
	DEBOUNCE_2ND_ZERO,
	DEBOUNCE_PRESSED
} DEBOUNCE_STATES;

// Boolean flags for each interrupt
volatile bool ALERT_TIMER0A_UPDATE;
volatile bool ALERT_TIMER0B_UPDATE;

// Use a 2D array to keep track of which pixels should lit up. Think of this as a Boolean 
// array, and each of the individual bits in a uint32_t data type represents a Boolean variable. 
// Each uint32_t element can be thought of as a vertical line on the screen that is 32 pixels tall
uint32_t pixel_map[SCREEN_HEIGHT/32][SCREEN_WIDTH];

// Used as the image argument to lcd_draw_image()
const uint8_t pixel = 1;


//*****************************************************************************
// ISRs.
//*****************************************************************************
void TIMER0A_Handler(void) {	
	ALERT_TIMER0A_UPDATE = true;
	TIMER0->ICR |= TIMER_ICR_TATOCINT;
}

void TIMER0B_Handler(void) {
	ALERT_TIMER0B_UPDATE = true;
	TIMER0->ICR |= TIMER_ICR_TBTOCINT;
}


//*****************************************************************************
// Functions for enabling/disabling interrupts; used in initialize_hardware().
//*****************************************************************************
void DisableInterrupts(void) {
	__asm {
		CPSID  I
	}
}

void EnableInterrupts(void) {
	__asm {
		CPSIE  I
	}
}


//*****************************************************************************
// Initializes and configures all the relevant hardware.
//*****************************************************************************
void initialize_hardware(void) {
  DisableInterrupts();
	
	// Initialize serial debug
	init_serial_debug(true, true);
	
	// Configure timers
	gp_timer_config_16(TIMER0_BASE, TIMER_TAMR_TAMR_PERIOD, true, true, 
		FIVE_MS_INTERVALS, PRESCALER);
	gp_timer_config_16(TIMER0_BASE, TIMER_TBMR_TBMR_PERIOD, false, true, 
		TEN_MS_INTERVALS, PRESCALER);
	
	// Configure GPIO pins
	lcd_config_gpio();
	
	// Configure LCD touchscreen
	lcd_config_screen();
	lcd_clear_screen(LCD_COLOR_BLACK);
	ft6x06_init();
	
	// Configure Launchpad GPIO pins connected to LEDs and push buttons
	lp_io_init();
	
  EnableInterrupts();
}


//*****************************************************************************
// Detects when SW2 has been pressed. Use SW2 instead of SW1 because for 
// whatever reason, the board seems to confuse touchscreen interaction for SW1 
// presses, but doesn't do the same for SW2.
//*****************************************************************************
bool sw2_pressed(void) {
	static DEBOUNCE_STATES state = DEBOUNCE_ONE;
	bool pin_logic_level;
	
	pin_logic_level = lp_io_read_pin(SW2_BIT);
	
	// Debounce the button with an FSM
	switch (state) {
		case DEBOUNCE_ONE: {
			if(pin_logic_level) state = DEBOUNCE_ONE;
			else state = DEBOUNCE_1ST_ZERO;
			break;
		}
		case DEBOUNCE_1ST_ZERO: {
			if(pin_logic_level) state = DEBOUNCE_ONE;
			else state = DEBOUNCE_2ND_ZERO;
			break;
		}
		case DEBOUNCE_2ND_ZERO: {
			if(pin_logic_level) state = DEBOUNCE_ONE;
			else state = DEBOUNCE_PRESSED;
			break;
		}
		case DEBOUNCE_PRESSED: {
			if(pin_logic_level) state = DEBOUNCE_ONE;
			else state = DEBOUNCE_PRESSED;
			break;
		}
		default: while (1) {};
	}
	
	if(state == DEBOUNCE_2ND_ZERO) return true;
	else return false;
}


//*****************************************************************************
// 
//*****************************************************************************
void map_coordinates(uint16_t x, uint16_t y) {
	pixel_map[y/32][x] |= 1 << (y % 32);
}


//*****************************************************************************
// 
//*****************************************************************************
bool read_from_map(uint16_t x, uint16_t y) {
	return pixel_map[y/32][x] & 1 << (y % 32);
}


//*****************************************************************************
// Draws touchscreen input to the screen.
//*****************************************************************************
void draw_digit() {
	uint16_t x, y;
	for (x = 0; x < SCREEN_WIDTH; x++) {
		for (y = 0 ; y < SCREEN_HEIGHT; y++) {
			if (read_from_map(x, y)) {
				lcd_draw_image(SCREEN_WIDTH - x, 1, SCREEN_HEIGHT - y, 1, &pixel, LCD_COLOR_GREEN, LCD_COLOR_GREEN);
			}
		}
	}
}


//*****************************************************************************
// Find boundaries surrounding the digit drawn on the screen.
//*****************************************************************************
void find_boundaries(uint16_t *upper_bound, uint16_t *lower_bound, uint16_t *left_bound, uint16_t *right_bound) {
	uint16_t upper_temp = SCREEN_HEIGHT, lower_temp = 0, 
	left_temp = SCREEN_WIDTH, right_temp = 0;
	
	uint16_t x, y;
	for (x = 0 ; x < SCREEN_WIDTH; x++) {
		for (y = 0; y < SCREEN_HEIGHT; y++) {
			if (read_from_map(x, y)) {
				if (y < upper_temp) upper_temp = y;
				if (y > lower_temp) lower_temp = y;
				if (x < left_temp) left_temp = x;
				if (x > right_temp) right_temp = x;
			}
		}
	}
	
	*upper_bound = upper_temp;
	*lower_bound = lower_temp;
	*left_bound = left_temp;
	*right_bound = right_temp;
}


//*****************************************************************************
// Draws the boundaries surrounding the digit as four red lines. Primarily 
// used for debugging to ensure the bounds are correct.
//*****************************************************************************
void draw_boundaries(uint16_t upper_bound, uint16_t lower_bound, uint16_t left_bound, uint16_t right_bound) {
	uint16_t i;
	
	for (i = 0; i < SCREEN_WIDTH; i++) {
		if (!read_from_map(i, upper_bound)) {
			lcd_draw_image(SCREEN_WIDTH - i, 1, SCREEN_HEIGHT - upper_bound, 1, &pixel, LCD_COLOR_RED, LCD_COLOR_RED);
		}
		if (!read_from_map(i, lower_bound)) {
			lcd_draw_image(SCREEN_WIDTH - i, 1, SCREEN_HEIGHT - lower_bound, 1, &pixel, LCD_COLOR_RED, LCD_COLOR_RED);
		}
	}
	for (i = 0; i < SCREEN_HEIGHT; i++) {
		if (!read_from_map(left_bound, i)) {
			lcd_draw_image(SCREEN_WIDTH - left_bound, 1, SCREEN_HEIGHT - i, 1, &pixel, LCD_COLOR_RED, LCD_COLOR_RED);
		}
		if (!read_from_map(right_bound, i)) {
			lcd_draw_image(SCREEN_WIDTH - right_bound, 1, SCREEN_HEIGHT - i, 1, &pixel, LCD_COLOR_RED, LCD_COLOR_RED);
		}
	}
}


//*****************************************************************************
// Creates input for the neural network by normalizing the touchscreen data. 
// If calloc() cannot allocate the needed memory, terminates the program.
//*****************************************************************************
bool *create_nn_input(uint16_t upper_bound, uint16_t lower_bound, uint16_t left_bound, uint16_t right_bound) {
	bool *nn_input = calloc(256, sizeof(bool));
	if (NULL == nn_input) return NULL;
	
	double delta_x = (right_bound - left_bound) / 16.0, delta_y = (lower_bound - upper_bound) / 16.0;
	
	uint16_t x, y, i, j;
	
	// Iterate through all coordinates within the boundaries
	for (x = left_bound ; x <= right_bound; x++) {
		for (y = upper_bound; y <= lower_bound; y++) {
			// If a pixel within the boundaries is lit up, map it to 'nn_input'
			if (read_from_map(x, y)) {
				// Iterate through "columns" within 'nn_input'
				for (i = 0; i < 16; i++) {
					if ((left_bound + floor(i*delta_x) <= x) && (x <= left_bound + ceil((i+1) * delta_x))) {
						// Iterate through "rows" within 'nn_input'
						for (j = 0; j < 16; j++) {
							if ((upper_bound + floor(j*delta_y) <= y) && (y <= upper_bound + ceil((j+1)*delta_y))) {
								nn_input[16*j + i] = true;
							}
						}
					}
				}
			}
		}
	}
	
	return nn_input;
}


//*****************************************************************************
// 
//*****************************************************************************
double ***create_weights(void) {
	// Allocate heap space for the weights
	uint16_t i;
	double ***weights = malloc(2 * sizeof(double **));
	weights[0] = malloc(NUM_HIDDEN_UNITS * sizeof(double *));
	weights[1] = malloc(NUM_OUTPUT_UNITS * sizeof(double *));
	for (i = 0; i < NUM_HIDDEN_UNITS; i++) {
		weights[0][i] = malloc((NUM_INPUT_UNITS + 1) * sizeof(double));
	}
	for (i = 0; i < NUM_OUTPUT_UNITS; i++) {
		weights[1][i] = malloc((NUM_HIDDEN_UNITS + 1) * sizeof(double));
	}
	
	// Set the weights accordingly
	// TODO
	
	return weights;
}


void free_weights(double ***weights) {
	
}


//*****************************************************************************
// Prints 'nn_input' as a square matrix.
//*****************************************************************************
void test_nn_input(bool *nn_input) {
	printf("-------------START-------------\n");
	uint16_t i;
	for (i = 0; i < 256; i++) {
		if (!(i % 16) && i) printf("\n");		// Print newline every 16 characters
		printf(nn_input[i] ? "X " : "_ ");
	}
	printf("\n--------------END--------------\n");
}


//*****************************************************************************
// Classify an instance.
//*****************************************************************************
int classify(bool *nn_input, double ***weights) {
	uint16_t i, j, sum, max, classification;
	double *hidden_layer_outputs = malloc(NUM_HIDDEN_UNITS * sizeof(double));
	double *output_layer_outputs = malloc(NUM_OUTPUT_UNITS * sizeof(double));
	for (i = 0; i < NUM_HIDDEN_UNITS; i++) {
		sum = 0;
		for (j = 0; j < NUM_INPUT_UNITS; j++) {
			if (nn_input[j]) sum += weights[0][i][j];
		}
		sum += weights[0][i][NUM_INPUT_UNITS];	// Bias node
		hidden_layer_outputs[i] = sum;
	}
	for (i = 0; i < NUM_OUTPUT_UNITS; i++) {
		sum = 0;
		for (j = 0; j < NUM_HIDDEN_UNITS; j++) {
			sum += weights[1][i][j] * hidden_layer_outputs[j];
		}
		sum += weights[1][i][NUM_HIDDEN_UNITS];	// Bias node
		output_layer_outputs[i] = sum;
	}
	classification = 0;
	max = output_layer_outputs[0];
	for (i = 1; i < NUM_OUTPUT_UNITS; i++) {
		if (output_layer_outputs[i] > max) {
			classification = i;
			max = output_layer_outputs[i];
		}
	}
	
	// Free allocated memory
	free(hidden_layer_outputs);
	free(output_layer_outputs);
	
	return classification;
}


//*****************************************************************************
// Main function.
//*****************************************************************************
int main(void) {	
	initialize_hardware();
	
	uint16_t x, y, upper_bound, lower_bound, left_bound, right_bound;
	bool *nn_input;
	double ***weights = create_weights();
	
	while (true) {
		if (ALERT_TIMER0A_UPDATE) {
			if (ft6x06_read_td_status()) {
				// The touchscreen functions treat the bottom-right corner of the screen 
				// as the origin. For ease of access, I want 'x' and 'y' to represent 
				// the coordinates with respect to the upper-left corner as the origin
				x = SCREEN_WIDTH - ft6x06_read_x();
				y = SCREEN_HEIGHT - ft6x06_read_y();
				
				map_coordinates(x, y);
			}
			ALERT_TIMER0A_UPDATE = false;
		}
		if (ALERT_TIMER0B_UPDATE) {
			if (sw2_pressed()) {
				draw_digit();
				find_boundaries(&upper_bound, &lower_bound, &left_bound, &right_bound);
				draw_boundaries(upper_bound, lower_bound, left_bound, right_bound);
				nn_input = create_nn_input(upper_bound, lower_bound, left_bound, right_bound);
				if (NULL == nn_input) while (true) {
				}
				test_nn_input(nn_input);
				free(nn_input);
			}
			ALERT_TIMER0B_UPDATE = false;
		}
	}
	
	free_weights(weights);	// TODO: restructure code
}
