import java.util.ArrayList;

/**
 * Represents a unit in the neural network.
 * 
 * @author Zachary Zhou
 */
public class Unit {
	private ArrayList<Double> outgoingWeights;
	private double output;
	private double delta;
	
	/**
	 * Empty constructor for a unit.
	 */
	public Unit() {}
	
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
	 * @return The 'delta' field
	 */
	public double getDelta() {
		return delta;
	}
	
	/**
	 * @param delta The new 'delta' value
	 */
	public void setDelta(double delta) {
		this.delta = delta;
	}
}
