import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	/**
	 * Creates a neural network using instances from a training set. Uses the trained network to 
	 * classify instances from a testing set.
	 */
	public static void main(String[] args) {
		// Read the 'semeion.data' file into 'inputs' and 'desiredOutputs'
		Scanner stdin;
		try {
			stdin = new Scanner(new File("semeion.data"));
			ArrayList<ArrayList<Double>> inputs = new ArrayList<ArrayList<Double>>();
			ArrayList<Integer> desiredOutputs = new ArrayList<Integer>();
			while (stdin.hasNextLine()) {
				String[] line = stdin.nextLine().split(" ");
				// Add input for an instance to 'inputs'
				ArrayList<Double> input = new ArrayList<Double>();
				for (int i = 0; i < 256; i++) input.add(Double.valueOf(line[i]));
				inputs.add(input);
				// Add desired output for an instance to 'desiredOutputs'
				if (line[256 + 0].equals("1")) desiredOutputs.add(0);
				else if (line[256 + 1].equals("1")) desiredOutputs.add(1);
				else if (line[256 + 4].equals("1")) desiredOutputs.add(2);
				else if (line[256 + 3].equals("1")) desiredOutputs.add(3);
				else if (line[256 + 4].equals("1")) desiredOutputs.add(4);
				else if (line[256 + 5].equals("1")) desiredOutputs.add(5);
				else if (line[256 + 6].equals("1")) desiredOutputs.add(6);
				else if (line[256 + 7].equals("1")) desiredOutputs.add(7);
				else if (line[256 + 8].equals("1")) desiredOutputs.add(8);
				else desiredOutputs.add(9);
			}
			
			// Use a 5:1 ratio of training instances to testing instances
			int cutoff = 1593 / 5;
			ArrayList<ArrayList<Double>> trainingInputs = new ArrayList<ArrayList<Double>>(inputs.subList(cutoff, 1593));
			ArrayList<ArrayList<Double>> testingInputs = new ArrayList<ArrayList<Double>>(inputs.subList(0, cutoff));
			ArrayList<Integer> trainingDesiredOutputs = new ArrayList<Integer>(desiredOutputs.subList(cutoff, 1593));
			ArrayList<Integer> testingDesiredOutputs = new ArrayList<Integer>(desiredOutputs.subList(0, cutoff));
			
			// Construct and train the network
			ArrayList<Integer> unitsPerLayer = new ArrayList<Integer>();
			unitsPerLayer.add(256);
			unitsPerLayer.add(100);
			unitsPerLayer.add(10);
			NeuralNet nn = new NeuralNet(unitsPerLayer);
			nn.train(trainingInputs, trainingDesiredOutputs, 0.2, 125);
			
			// Evaluate the effectiveness of the network
		}
		catch (FileNotFoundException e) {
			System.err.println("Cannot find semeion.data");
		}
	}
}
