import java.util.ArrayList;

/**
 * Multilayer feedforward neural network implementation.
 * 
 * @author Zachary Zhou
 */
public class NeuralNet {
	private ArrayList<ArrayList<Unit>> layers;
	private final int activationFunction;
	public static final int SIGMOID = 1;
	public static final int ReLU = 2;
	
	/**
	 * Constructs the neural network, i.e., the graph. Initializes all edge weights randomly. 
	 * Also adds a bias node to each layer excluding the output layer.
	 * 
	 * @param unitsPerLayer Indicates how many layers there are and how many units should be in 
	 * @param activationFunction The number corresponding to the desired activation function
	 * each layer
	 */
	public NeuralNet(ArrayList<Integer> unitsPerLayer, int activationFunction) {
		layers = new ArrayList<ArrayList<Unit>>();
		this.activationFunction = activationFunction;
		
		// For each layer excluding the output layer, create a layer of nodes of the specified 
		// size plus one for the bias node
		for (int i = 0; i < unitsPerLayer.size() - 1; i++) {
			ArrayList<Unit> layer = new ArrayList<Unit>();
			for (int j = 0; j < unitsPerLayer.get(i); j++) layer.add(new Unit());
			
			// Add a bias node whose output is +1
			Unit biasNode = new Unit();
			biasNode.setOutput(1.0);
			layer.add(biasNode);
			
			layers.add(layer);
		}
		
		// For the output layer, create a layer of nodes of the specified size. No bias node 
		// is needed
		ArrayList<Unit> layer = new ArrayList<Unit>();
		for (int i = 0; i < unitsPerLayer.get(unitsPerLayer.size() - 1); i++) layer.add(new Unit());
		layers.add(layer);
		
		// Create weights initialized to a random number from -0.005 to +0.005
		for (int i = 0; i < layers.size() - 1; i++)
			for (int j = 0; j < unitsPerLayer.get(i) + 1; j++) {
				for (int k = 0; k < unitsPerLayer.get(i + 1); k++)
					layers.get(i).get(j).addRandWeight();
				if (i == layers.size() - 2) layers.get(i).get(j).addRandWeight();
			}
	}
	
	/**
	 * @param x The input to the activation function
	 * @param function The number corresponding to the desired activation function
	 * @return The activation function on input x
	 */
	private double activationFunction(double x, int function) {
		switch (function) {
			case SIGMOID:	return 1.0 / (1.0 + Math.exp(-x));
			case ReLU:		return Math.max(0, x);
			default:		System.err.println("Invalid activation function argument");
							System.exit(0);
							return -1;
		}
	}
	
	/**
	 * @param x The input to the activation function
	 * @param function The number corresponding to the desired activation function
	 * @return The derivative of the activation function on input x
	 */
	private double activationDerivative(double x, int function) {
		switch (function) {
			case SIGMOID:	return activationFunction(x, SIGMOID) * (1 - activationFunction(x, SIGMOID));
			case ReLU:		return (x > 0) ? 1 : 0;
			default:		System.err.println("Invalid activation function argument");
							System.exit(0);
							return -1;
		}
	}
	
	/**
	 * Propagate inputs forward to compute the 'output' field for each unit. Also computes the 
	 * 'weightedSum' field for each unit.
	 * 
	 * @param input The input for a given instance
	 */
	private void propagateFwd(ArrayList<Double> input) {
		// Set outputs of all input nodes
		ArrayList<Unit> inputLayer = layers.get(0);
		for (int i = 0; i < input.size(); i++) inputLayer.get(i).setOutput(input.get(i));
		
		// For each subsequent layer, compute the outputs of all nodes
		for (int i = 1; i < layers.size(); i++) {
			ArrayList<Unit> prevLayer = layers.get(i - 1);
			for (int j = 0; j < layers.get(i).size() - 1; j++) {	// Exclude the bias node
				double weightedSum = 0;
				for (int k = 0; k < prevLayer.size(); k++)
					weightedSum += prevLayer.get(k).getOutput() * prevLayer.get(k).getWeight(j);
				Unit u = layers.get(i).get(j);
				u.setWeightedSum(weightedSum);
				u.setOutput(activationFunction(weightedSum, activationFunction));
			}
			if (i == layers.size() - 1) {
				double weightedSum = 0;
				for (int k = 0; k < prevLayer.size(); k++)
					weightedSum += prevLayer.get(k).getOutput() * 
						prevLayer.get(k).getWeight(layers.get(i).size() - 1);
				Unit u = layers.get(i).get(layers.get(i).size() - 1);
				u.setWeightedSum(weightedSum);
				u.setOutput(activationFunction(weightedSum, activationFunction));
			}
		}
	}
	
	/**
	 * Classify a given instance based on the input vector.
	 * 
	 * @param input The input for a given instance
	 * @return The classification according to the neural net
	 */
	public int classify(ArrayList<Double> input) {
		propagateFwd(input);
		ArrayList<Unit> outputLayer = layers.get(layers.size() - 1);
		double highestValue = outputLayer.get(0).getOutput();
		int index = 0;
		for (int i = 1; i < outputLayer.size(); i++)
			if (outputLayer.get(i).getOutput() > highestValue) {
				highestValue = outputLayer.get(i).getOutput();
				index = i;
			}
		
		// For debugging
//		System.out.println("Values at each output unit:");
//		for (int i = 0; i < outputLayer.size(); i++) 
//			System.out.println(i + ") " + outputLayer.get(i).getOutput());
		
		return index;	// Luckily enough, 'index' directly maps to the classification
	}
	
	/**
	 * Trains the network using backpropagation.
	 * 
	 * @param inputs A list of inputs for each training instance
	 * @param desiredOutputs A list of desired output labels for each training instance
	 * @param numEpochs The number of epochs, i.e., passes through the training set
	 * @param learningRate The learning rate for updating weights
	 */
	public void train(ArrayList<ArrayList<Double>> inputs, ArrayList<Integer> desiredOutputs, 
			int numEpochs, double learningRate) {
		for (int i = 0; i < numEpochs; i++)
			for (int j = 0; j < inputs.size(); j++) {
				// Feed forward
				propagateFwd(inputs.get(j));
				
				// Calculate errors at all output nodes
				ArrayList<Unit> outputLayer = layers.get(layers.size() - 1);
				
				for (int k = 0; k < outputLayer.size(); k++) {
					Unit u = outputLayer.get(k);
					double label = 0.0;
					if (desiredOutputs.get(j) == k) label = 1.0;
					u.setError(activationDerivative(u.getWeightedSum(), activationFunction) * 
							(label - u.getOutput()));
				}
				
				// Back-propagate errors from output layer to the second layer, i.e., the layer right 
				// after the input layer; break this into two steps because the output layer doesn't 
				// contain a bias node, while all the others before it do
				
				// Step 1: Back-propagate errors from output layer to layer right before it
				for (int k = 0; k < layers.get(layers.size() - 2).size() - 1; k++) {
					Unit u = layers.get(layers.size() - 2).get(k);
					double error = 0;
					for (int l = 0; l < outputLayer.size(); l++)
						error += u.getWeight(l) * outputLayer.get(l).getError();
					error *= activationDerivative(u.getWeightedSum(), activationFunction);
					u.setError(error);
				}
				
				// Step 2: Continue back-propagating errors all the way to the second
				for (int k = layers.size() - 3; k > 0; k--)
					for (int l = 0; l < layers.get(k).size() - 1; l++) {
						Unit u = layers.get(k).get(l);
						double error = 0;
						for (int m = 0; m < layers.get(k + 1).size() - 1; m++)
							error += u.getWeight(m) * layers.get(k + 1).get(m).getError();
						error *= activationDerivative(u.getWeightedSum(), activationFunction);
						u.setError(error);
					}
				
				// Update weights using errors
				for (int k = 0; k < layers.size() - 1; k++)
					for (int l = 0; l < layers.get(k).size(); l++) {
						Unit u = layers.get(k).get(l);
						for (int m = 0; m < layers.get(k + 1).size() - 1; m++) {
							double weight = u.getWeight(m) + 
									learningRate * u.getOutput() * layers.get(k + 1).get(m).getError();
							u.setWeight(m, weight);
						}
					}
			}
	}
}
