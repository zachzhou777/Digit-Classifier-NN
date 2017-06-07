import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Convert 'semeion.data' to a file 'training_instances.dat' that is readable by the 
 * $readmemb() task in Verilog/SystemVerilog.
 * 
 * @author Zachary Zhou
 */
public class FileConverter {
	public static void main(String[] args) {
		PrintWriter printWriter = null;
		Scanner stdin;
		int instance = 0;
		try {
			printWriter = new PrintWriter("training_instances.dat");
		}
		catch (FileNotFoundException e) {
			System.err.println("Cannot create the file");
			System.exit(0);
		}
		try {
			stdin = new Scanner(new File("semeion.data"));
			while (stdin.hasNextLine()) {
				printWriter.print("@" + instance + " ");
				String line[] = stdin.nextLine().split(" ");
				for (int i = 0; i < line.length; i++) {
					printWriter.print(Double.valueOf(line[i]).intValue());
				}
				printWriter.println();
				instance++;
			}
			stdin.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("Cannot find semeion.data");
			System.exit(0);
		}
		finally {
			printWriter.close();
		}
	}
}
