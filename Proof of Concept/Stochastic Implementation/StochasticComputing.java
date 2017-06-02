import java.util.ArrayList;
import java.util.Collections;

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
	 * Multiplies two stochastic numbers a and b.
	 * 
	 * @param a First bitstream
	 * @param b Second bitstream
	 * @return The product of a and b represented by a stochastic number
	 */
	private static ArrayList<Boolean> multiplyBitstreams(ArrayList<Boolean> a, ArrayList<Boolean> b) {
		ArrayList<Boolean> product = new ArrayList<Boolean>();
		for (int i = 0; i < BITSTREAM_LENGTH - 1; i++) product.add(a.get(i) && b.get(i));
		product.add(a.get(BITSTREAM_LENGTH - 1) ^ b.get(BITSTREAM_LENGTH - 1));
		return product;
	}
	
	/**
	 * Multiplies two numbers a and b using stochastic computing methods.
	 * 
	 * @param a
	 * @param b
	 * @return The product of a and b as a real number
	 */
	private static double multiply(double a, double b) {
		// Logically shift 'a' and 'b' left (multiply by 2) until they become significant enough 
		// to produce a significant product
		int aShifts = 0, bShifts = 0;
		while (2 * Math.abs(a) < 1) {
			a *= 2;
			aShifts++;
		}
		while (2 * Math.abs(b) < 1) {
			b *= 2;
			bShifts++;
		}
		double product = bitstreamToNumber(multiplyBitstreams(numberToBitstream(a), numberToBitstream(b)));
		
		// Arithmetically shift 'product' right (divide by 2) enough times to compensate for the LSLs
		for (int i = 0; i < aShifts + bShifts; i++) product /= 2;
		return product;
	}
	
	/**
	 * Tests methods in this class.
	 */
	public static void main(String[] args) {
		// Test bitstreamToNumber() on a small input
//		ArrayList<Boolean> twoThirds = new ArrayList<Boolean>();
//		twoThirds.add(true);
//		twoThirds.add(false);
//		twoThirds.add(true);
//		twoThirds.add(false);
//		System.out.println(bitstreamToNumber(twoThirds));
		
		// Test numberToBitstream() by converting numbers to bitstreams and converting them back to 
		// numbers with bitstreamToNumber()
//		System.out.println(bitstreamToNumber(numberToBitstream(0.6)) + " vs " + 0.6);
//		System.out.println(bitstreamToNumber(numberToBitstream(0.52)) + " vs " + 0.52);
//		System.out.println(bitstreamToNumber(numberToBitstream(0.864)) + " vs " + 0.864);
		
		// Test multiply() by comparing the result attained using stochastic computing with the 
		// one returned by direct calculation
		double a = 0.5531, b = 0.6249;
		double stochasticResult = multiply(a, b);
		double directResult = a * b;
		System.out.println(stochasticResult + " vs " + directResult);
		
		a = 0.864;	b = -0.543;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		System.out.println(stochasticResult + " vs " + directResult);
		
		a = -0.85978;	b = 0.56807;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		System.out.println(stochasticResult + " vs " + directResult);
		
		a = -0.76974;	b = -0.68908;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		System.out.println(stochasticResult + " vs " + directResult);
		
		// Test multiply() on small inputs
		a = 0.0000125;	b = 0.0000356;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		System.out.println(stochasticResult + " vs " + directResult);
		
		a = 0.00000035342;	b = -0.00000035596;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		System.out.println(stochasticResult + " vs " + directResult);
		
		a = -0.0025021;	b = 0.035905;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		System.out.println(stochasticResult + " vs " + directResult);
		
		a = -0.10944;	b = -0.096046;
		stochasticResult = multiply(a, b);
		directResult = a * b;
		System.out.println(stochasticResult + " vs " + directResult);
	}
}
