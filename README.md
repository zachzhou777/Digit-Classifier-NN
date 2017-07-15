# Neural Network for Digit Classification
This is an implementation of a neural network for the Tiva LaunchPad MCU. Purpose of the neural net is to classify handwritten digits. 
The MCU is connected to an LCD touchscreen, and the MCU classifies digits traced on the screen. Training data is the Semeion 
handwritten digit data. Training is done in Java, and the resulting edge weights are programmed onto the MCU.

At one time, I thought about implementing classifications on an FPGA using stochastic computing methods. The Survey of Stochastic 
Computing paper by Armin Alaghi and John P. Hayes from the University of Michigan will do a better job than I can of explaining 
what stochastic computing is, but basically, real numbers in the range [0,1] can be represented by bitstreams where the proportion 
of 1's represents the number. Multiplication can then be implemented using a single AND gate by passing two bitstreams through the 
gate. As nice as that sounds, it turns out the MCU I'm using is fast enough at classifying digits where implementing stochastic 
computing on an FPGA would only overcomplicate things. I've still included all the stochastic computing files in this repo, which 
includes a couple Verilog files I was working on, and a Java proof of concept I made when testing the effectiveness of stochastic 
computing.

I didn't realize this at first, but the '1' digit, which I usually represent with a vertical line, was represented as a caret-like 
symbol in the dataset. The '7' digit has a little dash through the middle, which is normal, but something to keep in mind when 
tracing on the screen. The system hasn't correctly classified a '4' so far, which is odd. Aside from that, I've gotten the system to 
correctly classify all the other digits.

I might try to throw in the FPGA at some point, but right now, I think I'll take a break from this project.
