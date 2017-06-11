# Neural Network for Digit Classification
This is an implementation of a neural network on an embedded system controlled by the Tiva LaunchPad MCU. Purpose of the neural 
net is to classify handwritten digits. The LaunchPad is connected to an LCD touchscreen, and I plan to have the system classify 
digits traced on the screen. Training data is the Semeion handwritten digit data, which can be found online. Training will be done 
in Java so that edge weights can be calculated. These weights will then be loaded onto the DE0-Nano FPGA board, which will be used 
to perform future classifications.

Rather than performing conventional floating-point arithmetic, I'm using stochastic computing methods. The Survey of Stochastic 
Computing paper by Armin Alaghi and John P. Hayes from the University of Michigan will do a better job than I can of explaining 
what stochastic computing is, but basically, real numbers in the range [0,1] can be represented by bitstreams where the proportion 
of 1's represents the number. Multiplication can then be implemented using a single AND gate by passing two bitstreams through the 
gate. For example, the square of 0.5 is 0.25 and can be modeled as the bitwise AND of the vectors \<0, 1, 0, 1> and \<0, 0, 1, 1> 
to get \<0, 0, 0, 1>. Note that in this example, the product happens to be exactly correct; most of the time this will not 
happen. If the two bitstreams for 0.5 were exactly the same, the product would be 0.5 as well. Therefore, large bitstreams 
are needed to (on average) achieve a certain level of precision. In short, the advantage to using stochastic computing to perform 
arithmetic is simplicity in hardware; you can implement multiplication with a single AND gate, whereas a multiplier requires 
significantly greater resources. The major disadvantage would be time and precision; to multiply two numbers, you consume many 
clock cycles trying to push two bitstreams through an AND gate, and the result may not even be that accurate. For most applications, 
stochastic computing isn't necessary, but due to the massive amount of arithmetic needed in implementing a neural net, combined 
with the fact that many of the calculations can be performed in parallel and do not have to be terribly accurate to yield useful 
results, stochastic computing is preferred over conventional arithmetic.

Proof of Concept - Java implementations of the neural network, one using standard floating-point arithmetic, the other using 
stochastic computing methods. For the stochastic implementation, the StochasticComputing.java file is a standalone file that 
is used to determine how long a bitstream needs to be to attain a given level of precision in representing the real number. 
NeuralNet.java performs arithmetic the same way as the standard implementation, but performs the extra step of rounding 
floating-point numbers so that only a given number of significant figures are present. Despite rounding errors, this neural net 
performs just as well as the standard neural net.

FPGA - Verilog/SystemVerilog code to be loaded on the FPGA
