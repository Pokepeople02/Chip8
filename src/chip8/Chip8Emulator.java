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
	private static int displayScale = -1;
	private static int cycleSpeed = -1;
	private static Boolean verboseFlag;
	
	private static PrintStream traceStream;
	
	/* Default options */
	private static final int DEFAULT_DISPLAY_SCALE = 10;
	private static final int DEFAULT_CYCLE_SPEED = 10;
	private static final PrintStream DEFAULT_TRACE_STREAM = System.out;
	private static final boolean DEFAULT_VERBOSE_FLAG = false;

	/**Creates a new CHIP-8 emulator using the supplied command line arguments to supply the location of the ROM to be loaded.
	 * @param args Command line arguments to be parsed
	 */
	public static void main(String[] args) {
		parseFile(args);
		parseOptions(args);
		
		validateArguments();
		
		openTraceStream();
		Chip8 emulator = new Chip8();
		if( emulator.loadROM(romFilename) ) {
			KeyboardAdapter controller = new KeyboardAdapter();
			emulator.attachKeypad(controller.getKeypad());
			
			emulator.getDisplay().scale(displayScale);
		
			SwingUtilities.invokeLater( () -> new MainWindow(emulator, controller) );
			
			emulator.startEmulation(cycleSpeed);
		}//end if
		else {
			System.err.println("Error: Unable to load ROM " + romFilename);
			System.exit(1);
		}//end else
	}//end method main
	
	/**Ensures provided arguments are valid, and sets any unprovided arguments to their default values*/
	private static void validateArguments() {
		if(romFilename.equals(""))
			missingROMFilename();
		
		if(displayScale == -1) {
//			System.out.println("Defaulting to display-scale " + DEFAULT_DISPLAY_SCALE);
			displayScale = DEFAULT_DISPLAY_SCALE;
		}//end if
		
		if(cycleSpeed == -1) {
//			System.out.println("Defaulting to cycle-speed " + DEFAULT_CYCLE_SPEED);
			cycleSpeed = DEFAULT_CYCLE_SPEED;
		}//end if
		
		if(verboseFlag == null) {
//			System.out.println("Defaulting to verbose " + DEFAULT_VERBOSE_FLAG);
			verboseFlag = DEFAULT_VERBOSE_FLAG;
		}//end if
		
		if(outputFilename.equals("")) {
//			System.out.println("Defaulting to output-stream stdout");
			traceStream = DEFAULT_TRACE_STREAM;
		}//end if
	}//end method validateArguments
	
	/** Alerts the user that they are missing the required ROM filename argument, and exits.*/
	private static void missingROMFilename() {
		System.err.println("Missing argument: FILE");
		printUsage();
		System.exit(1);
	}//end method missingROMFilename

	/**Parses the first argument supplied as the ROM filename
	 * @param args The array of input command line options
	 */
	private static void parseFile(String[] args) {
		try {
			if(!args[0].startsWith("-"))
				romFilename = args[0];
		} catch(ArrayIndexOutOfBoundsException e) {
			missingROMFilename();
		}//end try-catch
	}//end method parseFile

	/**If verbose debugging mode is enabled, prints supplied debug statement to the trace output stream.
	 * @param statement The statement to be printed to the output stream
	 */
	public static void debugLog(String statement) {
		if(verboseFlag)
			traceStream.println(statement);
	}//end method debugLog
	
	/**Prepares the requested output stream for trace output
	 * If one was not requested, or if an error occurs during the opening process, defaults to stdout.*/
	private static void openTraceStream() {
		//If no output file provided, keep output stream as stdout
		if(outputFilename.equals(""))
			return;
		
		try {
			traceStream = new PrintStream(outputFilename);
		} catch(FileNotFoundException e) {
			System.err.println("Unable to create or open file \"" + outputFilename + "\", defaulting to stdout.");
			traceStream = DEFAULT_TRACE_STREAM;
		}//end try-catch
	}//end method prepOutputStream

	/**Parses command line options.
	 * @param args The array of input command line options
	 */
	private static void parseOptions(String[] args) {
		for(int i = 0; i < args.length; ++i) {
			//Only standalone argument that shouldn't start with "-" should be argument 0: FILE
			if(!args[i].startsWith("-") && i == 0)
				continue;
			
			switch(args[i]) {
				/* Wordy options */
				case "--help" :
					printUsage();
					System.exit(0);
					
				case "--no-verbose" :
					verboseFlag = false;
					break;
					
				/* (Series of) non-wordy options */
				default :
					String simpleOptionSeries = args[i];
					for(int j = 1; j < simpleOptionSeries.length(); ++j) {
						switch(simpleOptionSeries.charAt(j)) {
							case 'v' :
								verboseFlag = true;
								break;
								
							case 'o' :
								try {
									if(args[i + 1].startsWith("-"))
										throw new IllegalArgumentException("-o requires an argument");
									outputFilename = args[++i];
								} catch(ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
									System.err.println("-o requires a filename argument");
									System.exit(1);
								}//end try-catch
								break;
								
							case 'd' :
								try {
									if(args[i + 1].startsWith("-"))
										throw new IllegalArgumentException("-d requires an argument");
									displayScale = Integer.parseInt(args[++i]);
								} catch(ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
									System.err.println("-d requires an integer argument");
									System.exit(1);
								}//end try-catch
								break;
								
							case 'c' :
								try {
									if(args[i + 1].startsWith("-"))
										throw new IllegalArgumentException("-c requires an argument");
									cycleSpeed = Integer.parseInt(args[++i]);
								} catch(ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
									System.err.println("-c requires an integer argument");
									System.exit(1);
								}//end try-catch
								break;
								
							default :
								System.err.println("Unexpected option: " + args[i].charAt(j));
								System.exit(1);
						}//end switch
					}//end for		
			}//end switch	
		}//end for
	}//end method parseOptions

	/** Prints proper command line parameter usage message */
	private static void printUsage() {
		System.out.println("Usage: Chip8Emulator \"FILE\" [-d display-scale] [-c cycle-speed] [-v | --not-verbose] [-o output-file | --output output-file] [--help]");
		
		System.out.println("\tFILE : The filename of the ROM to be loaded");
		System.out.println("\t[-d display-scale] : Specifies integer initial scale factor for the CHIP-8's display. Defaults to " + DEFAULT_DISPLAY_SCALE + " if not provided.");
		System.out.println("\t[-c cycle-speed] : Specifies integer delay between emulation cycles in milliseconds. Defaults to " + DEFAULT_CYCLE_SPEED + " if not provided.");
		System.out.println("\t[-v | --not-verbose] : Optionally specifies whether verbose debugging mode should be enabled");
		System.out.println("\t[-o output-file] : Optionally specifies an output text file for logging debug statements. Defaults to stdout if not provided.");
		System.out.println("\t[--help] : Prints this message");
	}//end method printUsageError
	
}//end class Chip8Emulator
