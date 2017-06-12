import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * @author Zachary Zhou
 */
public class Main {
	private static final int NUM_INPUTS = 256;
	private static final int NUM_HIDDEN[] = {10};
	private static final int NUM_OUTPUTS = 10;
	private static final int NUM_EPOCHS = 100;
	private static final double LEARNING_RATE = 0.02;
	
	/**
	 * Creates a neural network using instances from a training set. Uses the trained network to 
	 * classify instances from a testing set.
	 */
	public static void main(String[] args) {
		// Read the 'semeion.data' file
		ArrayList<Instance> instances = new ArrayList<Instance>();
		Scanner stdin;
		try {
			stdin = new Scanner(new File("semeion.data"));
			while (stdin.hasNextLine()) {
				String[] line = stdin.nextLine().split(" ");
				
				// Create input for an instance
				ArrayList<Double> input = new ArrayList<Double>();
				for (int i = 0; i < NUM_INPUTS; i++) input.add(Double.valueOf(line[i]));
				
				// Create desired output for an instance
				int desiredOutput = 0;	// Default 'desiredOutput' to 0
				if (line[NUM_INPUTS + 1].equals("1")) desiredOutput = 1;
				else if (line[NUM_INPUTS + 2].equals("1")) desiredOutput = 2;
				else if (line[NUM_INPUTS + 3].equals("1")) desiredOutput = 3;
				else if (line[NUM_INPUTS + 4].equals("1")) desiredOutput = 4;
				else if (line[NUM_INPUTS + 5].equals("1")) desiredOutput = 5;
				else if (line[NUM_INPUTS + 6].equals("1")) desiredOutput = 6;
				else if (line[NUM_INPUTS + 7].equals("1")) desiredOutput = 7;
				else if (line[NUM_INPUTS + 8].equals("1")) desiredOutput = 8;
				else if (line[NUM_INPUTS + 9].equals("1")) desiredOutput = 9;
				
				// Add the instance to 'instances'
				instances.add(new Instance(input, desiredOutput));
			}
		}
		catch (FileNotFoundException e) {
			System.err.println("Cannot find semeion.data");
			System.exit(0);
		}
		
		// Shuffle instances so that instances with the same class labels aren't grouped together
		Collections.shuffle(instances);
		
		// Create lists for training inputs and desired outputs
		ArrayList<ArrayList<Double>> inputs = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> desiredOutputs = new ArrayList<Integer>();
		for (Instance i : instances) {
			inputs.add(i.input);
			desiredOutputs.add(i.desiredOutput);
		}
		
		// Construct and train the network
		ArrayList<Integer> unitsPerLayer = new ArrayList<Integer>();
		unitsPerLayer.add(NUM_INPUTS);
		for (int i : NUM_HIDDEN) unitsPerLayer.add(i);
		unitsPerLayer.add(NUM_OUTPUTS);
		NeuralNet nn = new NeuralNet(unitsPerLayer, NeuralNet.SIGMOID);
		nn.train(inputs, desiredOutputs, NUM_EPOCHS, LEARNING_RATE);
		
		// Write the edge weights to the files
		try {
			nn.writeWeightsToFile(NeuralNet.BOTH);
		} catch (FileNotFoundException e) {
			System.err.println("Cannot create the file");
			System.exit(0);
		}
	}
}
