/* Testbench for weighted_binary_generator. */
module weighted_binary_generator_tb();

// Connections to the LFSR
wire [31:0] lfsr_value;
reg clk, rst_n;

// Connections to the DUT
reg [9:0] binary_number;
wire [9:0] random_number;
wire stochastic_number;

integer low_count, high_count;

// Instantiate an LFSR to generate random numbers, and the DUT
lfsr iLFSR(.register(lfsr_value), .clk(clk), .rst_n(rst_n));
weighted_binary_generator iDUT(.binary_number(binary_number), .random_number(random_number), 
			.stochastic_number(stochastic_number));

// Set 'random_number' to a 10-bit slice from the LFSR's value
assign random_number = lfsr_value[24:15];

initial begin
	// Initialize clock and reset. Deassert reset before first positive clock edge
	clk = 0;
	rst_n = 0;
	#1 rst_n = 1;

	// "Warm up" the LFSR
	repeat(10000) @(posedge clk);

	// Set different values for 'binary_number' and observe the bitstream
	/* Test 1 */
	@(negedge clk) begin
		binary_number = 10'h1FF;	// = 511; roughly half the bits should be high
		low_count = 0;
		high_count = 0;
	end
	repeat(1024) @(negedge clk) begin
		if (stochastic_number) high_count = high_count + 1;
		else low_count = low_count + 1;
	end
	$display("TEST 1\nHigh count: %d\nLow count: %d", high_count, low_count);

	/* Test 2 */
	@(negedge clk) begin
		binary_number = 10'h0FF;	// = 255; roughly a quarter of the bits should be high
		low_count = 0;
		high_count = 0;
	end
	repeat(1024) @(negedge clk) begin
		if (stochastic_number) high_count = high_count + 1;
		else low_count = low_count + 1;
	end
	$display("TEST 2\nHigh count: %d\nLow count: %d", high_count, low_count);

	/* Test 3 */
	@(negedge clk) begin
		binary_number = 10'h155;	// = 255; roughly a third of the bits should be high
		low_count = 0;
		high_count = 0;
	end
	repeat(1024) @(negedge clk) begin
		if (stochastic_number) high_count = high_count + 1;
		else low_count = low_count + 1;
	end
	$display("TEST 3\nHigh count: %d\nLow count: %d", high_count, low_count);

	$stop;
end

// Clock period is 10 time units
always #5 clk = ~clk;

endmodule
