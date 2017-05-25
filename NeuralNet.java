import java.util.ArrayList;
import java.util.List;

public class NeuralNet {
	private List<List<Unit>> layers;
	
	/**
	 * Constructs the neural network, i.e., the graph. Initializes all edge weights randomly. 
	 * Also adds a bias node to each layer excluding the output layer.
	 * 
	 * @param unitsPerLayer Indicates how many layers there are and how many units should be in 
	 * each layer
	 */
	public NeuralNet(List<Integer> unitsPerLayer) {
		layers = new ArrayList<List<Unit>>();
		
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
	 * Trains the network using backpropagation. Uses sigmoid function as the activation function.
	 * 
	 * @param inputs A list of inputs for each training instance
	 * @param desiredOutputs A list of desired output labels for each training instance
	 * @param learningRate The learning rate for updating weights
	 * @param numEpochs The number of epochs, i.e., passes through the training set
	 */
	public void train(List<List<Double>> inputs, List<Integer> desiredOutputs, 
			double learningRate, int numEpochs) {
		for (int i = 0; i < numEpochs; i++) {
			for (int j = 0; j < inputs.size(); j++) {
				// Set outputs of all input nodes
				for (int k = 0; k < layers.get(0).size() - 1; k++)
					layers.get(0).get(k).setOutput(inputs.get(j).get(k));
				
				// TODO: Perform backpropagation
			}
		}
	}
}
