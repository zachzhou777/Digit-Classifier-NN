/* Implementation of a 32-bit LFSR. */
module lfsr(register, clk, rst_n);

// The LFSR itself
output reg [31:0] register;

// Clock and asynchronous low reset signals
input clk, rst_n;

// Implement LFSR behavior
always @(posedge clk, negedge rst_n) begin
	// Upon reset, initialize all flip-flops with 1, not 0 because 0 is a dead-end state
	if (~rst_n) register <= 32'hFFFF_FFFF;
	// Infer connections (as well as occasional XOR gates) between flip-flops
	else begin
		register[31:28]	<= register[30:27];
		register[27]	<= register[26] ^ register[31];
		register[26:24]	<= register[25:23];
		register[23]	<= register[22] ^ register[31];
		register[22:17]	<= register[21:16];
		register[16]	<= register[15] ^ register[31];
		register[15:9]	<= register[14:8];
		register[8]	<= register[7] ^ register[31];
		register[7:6]	<= register[6:5];
		register[5]	<= register[4] ^ register[31];
		register[4]	<= register[3] ^ register[31];
		register[3]	<= register[2] ^ register[31];
		register[2:0]	<= {register[1:0], register[31]};
	end
end

endmodule
