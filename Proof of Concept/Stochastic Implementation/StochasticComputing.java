import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Implements operations related to stochastic computing. Primary purpose of the class is to determine 
 * how many bits are needed in the bitstream in order to make the neural net sufficiently accurate. 
 * Specifically, it's intended to figure out how many bits are needed to attain a certain level of 
 * precision, i.e., number of significant figures. The last bit of a stochastic number is the sign 
 * bit; all previous bits constitute the magnitude. Note that bitstreams are implemented as Boolean 
 * lists.
 * 
 * @author Zachary Zhou
 */
public class StochasticComputing {
	private static final int BITSTREAM_LENGTH = 1024;
	private static Random randGen = new Random();
	
	/* The below methods implement operations related to stochastic computing. */
	
	/**
	 * Converts a real number to a stochastic number.
	 * 
	 * @param x The real number
	 * @return A stochastic number representing x
	 */
	private static ArrayList<Boolean> numberToBitstream(double x) {
		ArrayList<Boolean> bitstream = new ArrayList<Boolean>();
		
		// Add the correct number of 1's and 0's
		int numTrue = Math.abs((int) (x * (BITSTREAM_LENGTH - 1)));
		for (int i = 0; i < BITSTREAM_LENGTH - 1; i++) bitstream.add(i < numTrue);
		
		Collections.shuffle(bitstream);
		bitstream.add(x < 0);	// Add the sign bit
		return bitstream;
	}
	
	/**
	 * Converts a stochastic number to a real number.
	 * 
	 * @param x A stochastic number
	 * @return The real number that x represents
	 */
	private static double bitstreamToNumber(ArrayList<Boolean> x) {
		boolean negative = x.remove(x.size() - 1);	// Extract the sign bit
		int numTrue = 0;
		for (Boolean b : x) if (b) numTrue++;
		double number = (double) numTrue / x.size();
		if (negative) number = -number;
		return number;
	}
	
	/**
	 * Multiplies two stochastic numbers 'x' and 'y' using an AND gate.
	 * 
	 * @param x
	 * @param y
	 * @return The product of 'x' and 'y' represented by a stochastic number
	 */
	private static ArrayList<Boolean> multiplyBitstreams(ArrayList<Boolean> x, ArrayList<Boolean> y) {
		ArrayList<Boolean> product = new ArrayList<Boolean>();
		for (int i = 0; i < BITSTREAM_LENGTH - 1; i++) product.add(x.get(i) && y.get(i));
		product.add(x.get(BITSTREAM_LENGTH - 1) ^ y.get(BITSTREAM_LENGTH - 1));
		return product;
	}
	
	/**
	 * Multiplies two numbers 'x' and 'y' using stochastic computing methods.
	 * 
	 * @param x
	 * @param y
	 * @return The product of 'x' and 'y' as a real number
	 */
	private static double multiply(double x, double y) {
		// Logically shift 'x' and 'y' left (multiply by 2) until they become significant enough 
		// to produce a significant product
		int aShifts = 0, bShifts = 0;
		while (2 * Math.abs(x) < 1) {
			x *= 2;
			aShifts++;
		}
		while (2 * Math.abs(y) < 1) {
			y *= 2;
			bShifts++;
		}
		double product = bitstreamToNumber(multiplyBitstreams(numberToBitstream(x), numberToBitstream(y)));
		
		// Arithmetically shift 'product' right (divide by 2) enough times to compensate for the LSLs
		for (int i = 0; i < aShifts + bShifts; i++) product /= 2;
		return product;
	}
	
	/**
	 * Adds the numbers in an array using an algorithm similar to the one used to compute the mean 
	 * of two stochastic numbers: convert numbers to bitstreams and feed the bitstreams into a 
	 * mux, then randomly choose one of the inputs to be the mux output and feed that output to an 
	 * output bitstream, which represents the average of all the bitstreams; multiply the average by 
	 * the number of elements to get the sum.
	 * 
	 * @param x The array of numbers
	 * @return The sum of all numbers in 'x'
	 */
	private static double add(double x[]) {
		// Create a list of stochastic numbers
		ArrayList<ArrayList<Boolean>> stochasticNumbers = new ArrayList<ArrayList<Boolean>>();
		for (double d : x) stochasticNumbers.add(numberToBitstream(d));
		
		// Compute the average of the stochastic numbers
		ArrayList<Boolean> average = new ArrayList<Boolean>();
		for (int i = 0; i < BITSTREAM_LENGTH; i++) {
			average.add(stochasticNumbers.get(randGen.nextInt(x.length)).get(i));
		}
		
		// Convert the average to a real number and multiply it by the number of stochastic numbers
		return bitstreamToNumber(average) * x.length;
	}
	
	/**
	 * Does the same thing as the other add() method, except instead of using only one of the 
	 * input bitstreams, samples multiple bitstreams and returns the majority value in order to  
	 * get a more accurate result; if 'numSamples' is even and an equal number of samples are 
	 * 1 and 0, then output 1.
	 * 
	 * @param x The array of numbers
	 * @param numSamples The number of input bitstreams to sample
	 * @return The sum of all numbers in 'x'
	 */
	private static double add(double x[], int numSamples) {
		// Create a list of stochastic numbers
		ArrayList<ArrayList<Boolean>> stochasticNumbers = new ArrayList<ArrayList<Boolean>>();
		for (double d : x) stochasticNumbers.add(numberToBitstream(d));
		
		// Compute the average of the stochastic numbers
		ArrayList<Boolean> average = new ArrayList<Boolean>();
		for (int i = 0; i < BITSTREAM_LENGTH; i++) {
			int sum = 0;
			for (int j = 0; j < numSamples; j++) {
				if (stochasticNumbers.get(randGen.nextInt(x.length)).get(i)) sum++;
			}
			average.add(sum >= numSamples/2.0);
		}
		
		// Convert the average to a real number and multiply it by the number of stochastic numbers
		return bitstreamToNumber(average) * x.length;
	}
	
	/* The below methods test the above methods. */
	
	/**
	 * Test bitstreamToNumber() on a small input.
	 */
	private static void bitstreamToNumber_test() {
		// 'twoThirds' contains a bitstream that should be interpreted as 2/3
		ArrayList<Boolean> twoThirds = new ArrayList<Boolean>();
		twoThirds.add(true);
		twoThirds.add(false);
		twoThirds.add(true);
		twoThirds.add(false);
		System.out.println(bitstreamToNumber(twoThirds) == 2.0/3 ? 
				"bitstreamToNumber() passed test" : "bitstreamToNumber() failed test");
	}
	
	/**
	 * Test numberToBitstream() by converting numbers to stochastic numbers and converting them 
	 * back to numbers with bitstreamToNumber().
	 */
	private static void numberToBitstream_test() {
		final double TOLERATED_ERROR = 0.01;
		
		double number = 0.6;
		double result = bitstreamToNumber(numberToBitstream(number));
		if (Math.abs(result - number) / number > TOLERATED_ERROR) {
			System.out.println("numberToBitstream() failed test");
			return;
		}
		
		number = 0.52;
		result = bitstreamToNumber(numberToBitstream(number));
		if (Math.abs(result - number) / number > TOLERATED_ERROR) {
			System.out.println("numberToBitstream() failed test");
			return;
		}
		
		number = 0.864;
		result = bitstreamToNumber(numberToBitstream(number));
		if (Math.abs(result - number) / number > TOLERATED_ERROR) {
			System.out.println("numberToBitstream() failed test");
			return;
		}
		
		System.out.println("numberToBitstream() passed test");
	}
	
	/**
	 * Test multiply() by comparing the results attained using stochastic computing with the 
	 * ones returned by direct calculation.
	 */
	private static void multiply_test() {
		final double TOLERATED_ERROR = 0.05;
		
		double a = 0.5531, b = 0.6249;
		double stochasticResult = multiply(a, b);
		double directResult = a * b;
		if (Math.abs(stochasticResult - directResult) / directResult > TOLERATED_ERROR) {
			System.out.println("multiply() failed test");
			return;
		}
		
		a = 0.864;	b = -0.543;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		if (Math.abs(stochasticResult - directResult) / directResult > TOLERATED_ERROR) {
			System.out.println("multiply() failed test");
			return;
		}
		
		a = -0.85978;	b = 0.56807;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		if (Math.abs(stochasticResult - directResult) / directResult > TOLERATED_ERROR) {
			System.out.println("multiply() failed test");
			return;
		}
		
		a = -0.76974;	b = -0.68908;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		if (Math.abs(stochasticResult - directResult) / directResult > TOLERATED_ERROR) {
			System.out.println("multiply() failed test");
			return;
		}
		
		// Test on small inputs
		a = 0.0000125;	b = 0.0000356;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		if (Math.abs(stochasticResult - directResult) / directResult > TOLERATED_ERROR) {
			System.out.println("multiply() failed test");
			return;
		}
		
		a = 0.00000035342;	b = -0.00000035596;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		if (Math.abs(stochasticResult - directResult) / directResult > TOLERATED_ERROR) {
			System.out.println("multiply() failed test");
			return;
		}
		
		a = -0.0025021;	b = 0.035905;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		if (Math.abs(stochasticResult - directResult) / directResult > TOLERATED_ERROR) {
			System.out.println("multiply() failed test");
			return;
		}
		
		a = -0.10944;	b = -0.096046;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		if (Math.abs(stochasticResult - directResult) / directResult > TOLERATED_ERROR) {
			System.out.println("multiply() failed test");
			return;
		}
		
		System.out.println("multiply() passed test");
	}
	
	/**
	 * Test add(double x[]) and add(double x[], int numSamples) against each other. The expectation 
	 * is that add(double x[], int numSamples) will generally be better than add(double x[]).
	 */
	private static void add_test1() {
		double numbers[] = {0.55, 0.684, 0.3552, 0.395, 0.215};
		double sum = 0.0;
		for (double number : numbers) sum += number;
		System.out.println(add(numbers));
		System.out.println(add(numbers, 1));
		
		// Test add(double x[], int numSamples)
		for (int i = 2; i <= 2; i++) {
			System.out.println(i + " samples: " + add(numbers, i) + " vs " + sum);
		}
	}
	
	/**
	 * Test add(double x[], int numSamples) more rigourously.
	 */
	private static void add_test2() {
		double numbers[] = new double[256];
		double sum = 0.0;
		for (int i = 0; i < numbers.length; i++) numbers[i] = randGen.nextDouble() * 0.01;
		for (double number : numbers) sum += number;
		System.out.println(add(numbers) + " vs " + sum);
	}
	
	/**
	 * Tests stochastic computing methods by calling the tester methods.
	 */
	public static void main(String[] args) {
		bitstreamToNumber_test();
		numberToBitstream_test();
		multiply_test();
		add_test1();
		add_test2();
	}
}
