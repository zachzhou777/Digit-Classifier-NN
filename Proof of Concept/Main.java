import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author Zachary Zhou
 */
public class Main {
	private static final int NUM_INSTANCES = 1593;
	private static final int NUM_INPUTS = 256;
	private static final int NUM_HIDDEN[] = {10};
	private static final int NUM_OUTPUTS = 10;
	private static final int NUM_EPOCHS = 125;
	private static final double LEARNING_RATE = 0.1;
	
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
		
		// Use a 5:1 ratio of training instances to testing instances
		int cutoff = NUM_INSTANCES / 5;
		List<Instance> trainingInstances = instances.subList(cutoff, NUM_INSTANCES);
		List<Instance> testingInstances = instances.subList(0, cutoff);
		
		// Create lists for training inputs and desired outputs
		ArrayList<ArrayList<Double>> trainingInputs = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> trainingDesiredOutputs = new ArrayList<Integer>();
		for (Instance i : trainingInstances) {
			trainingInputs.add(i.input);
			trainingDesiredOutputs.add(i.desiredOutput);
		}
		
		// Create lists for testing inputs and desired outputs
		ArrayList<ArrayList<Double>> testingInputs = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> testingDesiredOutputs = new ArrayList<Integer>();
		for (Instance i : testingInstances) {
			testingInputs.add(i.input);
			testingDesiredOutputs.add(i.desiredOutput);
		}
		
		// Construct and train the network
		ArrayList<Integer> unitsPerLayer = new ArrayList<Integer>();
		unitsPerLayer.add(NUM_INPUTS);
		for (int i : NUM_HIDDEN) unitsPerLayer.add(i);
		unitsPerLayer.add(NUM_OUTPUTS);
		NeuralNet nn = new NeuralNet(unitsPerLayer, NeuralNet.SIGMOID);
		nn.train(trainingInputs, trainingDesiredOutputs, NUM_EPOCHS, LEARNING_RATE);
		
		// Evaluate the effectiveness of the network
		int correctCount = 0;
		for (int i = 0; i < testingInputs.size(); i++) {
			int classification = nn.classify(testingInputs.get(i));
			int desiredOutput = testingDesiredOutputs.get(i);
			if (classification == desiredOutput) {
				correctCount++;
				System.out.println("Correct classification; expected and got " + classification);
			}
			else System.out.println("Misclassification; expected " + desiredOutput + 
					", instead got " + classification);
		}
		System.out.println("Correctly classified: " + (double) correctCount / testingInputs.size());
	}
}
