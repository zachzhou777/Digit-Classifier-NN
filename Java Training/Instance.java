import java.util.ArrayList;

/**
 * Maintains a tuple representing an instance. An instance consists of two elements: the input 
 * values and the desired output value for that instance.
 * 
 * @author Zachary Zhou
 */
public class Instance {
	// Because the sole purpose of this class is to maintain a tuple, I chose to make the fields 
	// public rather than encapsulate them as private variables
	public ArrayList<Double> input;
	public int desiredOutput;
	
	public Instance(ArrayList<Double> input, int desiredOutput) {
		this.input = input;
		this.desiredOutput = desiredOutput;
	}
}
