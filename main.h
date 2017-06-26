//*****************************************************************************
// Digit Classifier Neural Network
// Zachary Zhou
//*****************************************************************************

#ifndef __MAIN_H__
#define __MAIN_H__

#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <stdbool.h>
#include <math.h>
#include "TM4C123.h"
#include "gpio_port.h"
#include "timers.h"
#include "i2c.h"
#include "pc_buffer.h"
#include "uart.h"
#include "launchpad_io.h"
#include "lcd.h"
#include "ft6x06.h"
#include "serial_debug.h"
#include "fonts.h"

#define FIVE_MS_INTERVALS		2500		// Loaded into TAILR register
#define TEN_MS_INTERVALS		5000
#define PRESCALER						100			// Prescaler for timers

#define SCREEN_WIDTH				240			// Dimensions of LCD screen in pixels
#define SCREEN_HEIGHT				320

#define NUM_INPUT_UNITS			256			// Constants related to the neural net
#define NUM_HIDDEN_UNITS		10
#define NUM_OUTPUT_UNITS		10


#endif
