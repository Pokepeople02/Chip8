package chip8;

import javax.swing.SwingUtilities;

import chip8.gui.MainWindow;

public class Chip8Emulator {

	/**Creates a new CHIP-8 emulator running the ROM located at the supplied filename, with a visual scaled to the requested factor.
	 * @param args Command line arguments, ideally of the format "Chip8Emulator &lt;ROM filename&gt; &lt;Cycle Delay&gt; &lt;Scale factor&gt;"
	 */
	public static void main(String[] args) {
		if(args.length != 3) {
			printUsageError();
			System.exit(0);
		}//end if
		
		try {
			//Initialize GUI and parse parameters
			SwingUtilities.invokeLater( () -> new MainWindow(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2])) );
		} catch(NumberFormatException nfe) {
			//Unable to parse parameters
			printUsageError();
			System.exit(0);
		}//end try-catch
		
	}//end method main
	
	/** Prints proper command line parameter usage message */
	private static void printUsageError() {
		System.out.println("Usage: java " + Chip8Emulator.class.getSimpleName() + " <ROM filename> <Cycle Delay> <Scale factor>");
	}//end method printUsageError
	
}//end class Driver
