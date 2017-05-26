import java.util.ArrayList;

public class NeuralNet {
	private ArrayList<ArrayList<Unit>> layers;
	
	/**
	 * Constructs the neural network, i.e., the graph. Initializes all edge weights randomly. 
	 * Also adds a bias node to each layer excluding the output layer.
	 * 
	 * @param unitsPerLayer Indicates how many layers there are and how many units should be in 
	 * each layer
	 */
	public NeuralNet(ArrayList<Integer> unitsPerLayer) {
		layers = new ArrayList<ArrayList<Unit>>();
		
		// For each layer excluding the output layer, create a layer of nodes of the specified 
		// size plus one for the bias node
		for (int i = 0; i < unitsPerLayer.size() - 1; i++) {
			ArrayList<Unit> layer = new ArrayList<Unit>();
			layers.add(layer);
			for (int j = 0; j < unitsPerLayer.get(i); j++) layer.add(new Unit());
			
			// Add a bias node whose output is +1
			Unit biasNode = new Unit();
			biasNode.setOutput(1.0);
			layer.add(biasNode);
		}
		
		// For the output layer, create a layer of nodes of the specified size. No bias node 
		// is needed
		ArrayList<Unit> layer = new ArrayList<Unit>();
		layers.add(layer);
		for (int i = 0; i < unitsPerLayer.get(unitsPerLayer.size() - 1); i++) layer.add(new Unit());
	}
	
	/**
	 * Use the sigmoid function as the activation function.
	 * 
	 * @param x The input to the sigmoid function S(x)
	 * @return S(x)
	 */
	private double activationFunction(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}
	
	/**
	 * Calculates the derivative S'(x) of the sigmoid function S(x).
	 * 
	 * @param x The input to the activation function
	 * @return S'(x)
	 */
	private double activationDerivative(double x) {
		return activationFunction(x) * (1 - activationFunction(x));
	}
	
	/**
	 * Propagate inputs forward to compute outputs for each unit.
	 * 
	 * @param input The input for a given instance
	 */
	private void propagateFwd(ArrayList<Double> input) {
		// Set outputs of all input nodes
		for (int i = 0; i < input.size(); i++) layers.get(0).get(i).setOutput(input.get(i));
		
		// For each subsequent layer, compute the outputs of all nodes
		for (int i = 1; i < layers.size(); i++)
			for (int j = 0; j < layers.get(i).size() - 1; j++) {	// Exclude the bias node
				double sum = 0;
				ArrayList<Unit> prevLayer = layers.get(i - 1);
				for (int k = 0; k < prevLayer.size(); k++)
					sum += prevLayer.get(k).getOutput() * prevLayer.get(k).getWeight(j);
				layers.get(i).get(j).setOutput(activationFunction(sum));
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
		return index;	// Luckily enough, 'index' directly maps to the classification
	}
	
	/**
	 * Trains the network using backpropagation.
	 * 
	 * @param inputs A list of inputs for each training instance
	 * @param desiredOutputs A list of desired output labels for each training instance
	 * @param learningRate The learning rate for updating weights
	 * @param numEpochs The number of epochs, i.e., passes through the training set
	 */
	public void train(ArrayList<ArrayList<Double>> inputs, ArrayList<Integer> desiredOutputs, 
			double learningRate, int numEpochs) {
		for (int i = 0; i < numEpochs; i++) {
			for (int j = 0; j < inputs.size(); j++) {
				// Feed forward
				propagateFwd(inputs.get(j));
				
				// Calculate error at all output nodes
				for (int k = 0; k < layers.get(layers.size() - 1).size(); k++) {
					Unit u = layers.get(layers.size() - 1).get(k);
					u.setDelta(activationDerivative(u.getOutput()) * 
							(u.getOutput() - desiredOutputs.get(k)));
				}
				
				// Back-propagate deltas
				for (int k = layers.size() - 1; k > 0; k--) {
					
				}
				
				// Update weights using deltas
			}
		}
	}
}
