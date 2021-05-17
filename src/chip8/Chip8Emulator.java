package chip8;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.SwingUtilities;

import chip8.emulator.Chip8;
import chip8.ui.KeyboardAdapter;
import chip8.ui.MainWindow;

public class Chip8Emulator {
	
	/* Command line arguments */
	private static String romFilename = "";
	private static String outputFilename = "";
	private static PrintStream outputStream;
	private static int displayScale = -1;
	private static int cycleSpeed = -1;
	private static boolean verboseFlag = true; //TODO: Change verbose default to false for release

	/**Creates a new CHIP-8 emulator using the supplied command line arguments to supply the location of the ROM to be loaded.
	 * @param args Command line arguments to be parsed
	 */
	public static void main(String[] args) {
		parseOptions(args);
		
		//Check validity of required options
		if(romFilename.equals("")) {
			System.err.println("Missing argument: file");
			printUsage();
			System.exit(1);
		}//end if
		else if(displayScale == -1) {
			System.err.println("Missing argument: display-scale");
			printUsage();
			System.exit(1);
		}//end else-if
		else if(cycleSpeed == -1) {
			System.err.println("Missing argument: cycle-speed");
			printUsage();
			System.exit(1);
		}//end else-if
		else {
			prepOutputStream();
			Chip8 emulator = new Chip8();
			if( emulator.loadROM(romFilename) ) {
				KeyboardAdapter controller = new KeyboardAdapter();
				emulator.attachKeypad(controller.getKeypad());
				emulator.getDisplay().scale(displayScale);
			
				SwingUtilities.invokeLater( () -> new MainWindow(emulator, controller) );
				
				emulator.startEmulation(cycleSpeed);
			}//end if
			else
				System.err.println("Unable to load ROM " + romFilename);
		}//end else
	}//end method main
	
	/**Prints supplied debug log statement to the output stream if verbose mode is enabled
	 * @param statement The statement to be printed to the output stream
	 */
	public static void debugLog(String statement) {
		if(verboseFlag)
			outputStream.println(statement);
	}//end method debugLog
	
	/**Prepares the requested output stream for emulator console output
	 * If one was not requested, or if an error occurs during the opening process, defaults to stdout.*/
	private static void prepOutputStream() {
		//If no output file provided, set output stream to stdout
		if(outputFilename.equals("")) {
			outputStream = System.out;
			return;
		}//end if
		
		try {
			outputStream = new PrintStream(outputFilename);
		} catch(FileNotFoundException e) {
			System.err.println("Unable to create or open file " + outputFilename);
			System.err.println("Defaulting to stdout");
			outputStream = System.out;
		}//end try-catch
	}//end method prepOutputStream

	/**Parses command line options.
	 * @param args An array of input command line options
	 */
	private static void parseOptions(String[] args) {
		for(int i = 0; i < args.length && args[i].startsWith("-"); ++i) {
			switch(args[i]) {
			/* Parsing options with required arguments */
				case "-f" :
					try {
						romFilename = args[++i];
					} catch(ArrayIndexOutOfBoundsException e) {
						System.err.println(args[i - 1] + " requires a filename argument");
						System.exit(1);
					}//end try-catch
					break;
					
				case "-d" :
					try {
						displayScale = Integer.parseInt(args[++i]);
					} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
						System.err.println(args[i - 1] + " requires an integer argument");
						System.exit(1);
					}//end try-catch
					break;
					
				case "-c" :
					try {
						cycleSpeed = Integer.parseInt(args[++i]);
					} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
						System.err.println(args[i - 1] + " requires an integer argument");
						System.exit(1);
					}//end try-catch
					break;
					
				case "-output" :
				case "-o" :
					try {
						outputFilename = args[++i];
					} catch(ArrayIndexOutOfBoundsException e) {
						System.err.println(args[i - 1] + " requires a filename argument");
						System.exit(1);
					}//end try-catch
					break;
				
				/* Parsing wordy flags */
				case "--help" :
					printUsage();
					System.exit(0);
					
				case "--no-verbose" :
					verboseFlag = false;
					break;
					
				/* Parsing series of non-wordy flags */
				default :
					for(int j = 1; j < args[i].length(); ++j) {
						switch(args[i].charAt(j)) {
							case 'v' :
								verboseFlag = true;
								break;
								
							case 'f' :
							case 'o' :
							case 'd' :
							case 'c' :
								System.out.println("Option " + args[i].charAt(j) + " requires an argument");
								System.exit(1);
								
							default :
								System.err.println("Illegal option: " + args[i].charAt(j));
								System.exit(1);
						}//end switch
					}//end for
			}//end switch
		}//end for
	}//end method parseOptions

	/** Prints proper command line parameter usage message */
	private static void printUsage() {
		System.out.println("Usage: Chip8Emulator -f \"file path\" -d display-scale -c cycle-speed [-v | --not-verbose] [-o output-file | --output output-file] [--help]");
		
		System.out.println("\t-f file : Specifies filename of ROM as \"file\"");
		System.out.println("\t-d display-scale : Specifies integer initial scale factor for the CHIP-8's display");
		System.out.println("\t-c cycle-speed : Specifies integer delay between emulation cycles in milliseconds");
		System.out.println("\t[-v | --not-verbose] : Optionally specifies whether verbose debugging mode should be enabled");
		System.out.println("\t[-o output-file | --output output-file] : Optionally specifies an output text file for logging debug statements");
		System.out.println("\t[--help] : Prints this message");
	}//end method printUsageError
	
}//end class Chip8Emulator
