import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a unit in the neural network.
 * 
 * @author Zachary Zhou
 */
public class Unit {
	private ArrayList<Double> outgoingWeights;
	private double weightedSum;
	private double output;
	private double error;	// Also known as the delta value for a unit
	private static Random randGen = new Random();
	
	/**
	 * Constructor for a unit.
	 */
	public Unit() {
		outgoingWeights = new ArrayList<Double>();
	}
	
	/**
	 * @param index The index in the layer immediately following that of the current unit
	 * @return The weight linking the current unit to the unit indicated by 'index'
	 */
	public double getWeight(int index) {
		return outgoingWeights.get(index);
	}
	
	/**
	 * @param index The index in the layer immediately following that of the current unit
	 * @param weight The edge's new weight value
	 */
	public void setWeight(int index, double weight) {
		outgoingWeights.set(index, weight);
	}
	
	/**
	 * Add a weight to the unit. Weight's value is a random number from -0.005 to +0.005.
	 */
	public void addRandWeight() {
		outgoingWeights.add((randGen.nextDouble()/* - 0.5*/) * 0.01);
	}
	
	/**
	 * @return The 'weightedSum' field
	 */
	public double getWeightedSum() {
		return weightedSum;
	}
	
	/**
	 * @param weightedSum The new 'weightedSum' value
	 */
	public void setWeightedSum(double weightedSum) {
		this.weightedSum = weightedSum;
	}
	
	/**
	 * @return The 'output' field
	 */
	public double getOutput() {
		return output;
	}
	
	/**
	 * @param output The new 'output' value
	 */
	public void setOutput(double output) {
		this.output = output;
	}
	
	/**
	 * @return The 'error' field
	 */
	public double getError() {
		return error;
	}
	
	/**
	 * @param error The new 'error' value
	 */
	public void setError(double error) {
		this.error = error;
	}
}
