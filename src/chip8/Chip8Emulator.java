package chip8;

import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import chip8.emulator.Chip8;
import chip8.emulator.Keypad;
import chip8.gui.DisplayVisual;

@SuppressWarnings("serial")
public class Chip8Emulator extends JFrame {
	
	private Timer cycleTimer;
	private Chip8 emulator;
	private KeyListener emulatedKeypad;
	private DisplayVisual emulatorDisplay;

	/**Creates a new CHIP-8 emulator running the ROM located at the supplied filename, with a visual scaled to the requested factor.
	 * @param args Command line arguments, ideally of the format "Chip8Emulator &lt;ROM filename&gt; &lt;Scale factor&gt;"
	 */
	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.println("Usage: java " + Chip8Emulator.class.getSimpleName() + " <ROM filename> <Scale factor>");
			System.exit(0);
		}//end if
		
		//Try parsing emulator parameters
		try {
			int scaleFactor = Integer.parseInt(args[1]);
			byte[] rom = readROM(args[0]);
			new Chip8Emulator(rom, scaleFactor);
		} catch(NumberFormatException nfe) {
			System.out.println("Could not start emulator: Invalid scale factor " + nfe);
			System.exit(0);
		} catch (FileNotFoundException fnfe) {
			System.out.println("Could not start emulator: Unable to locate ROM file " + fnfe);
			System.exit(0);
		} catch (OutOfMemoryError oom) {
			System.out.println("Could not start emulator: Invalid size of ROM file " + oom);
			System.exit(0);
		} catch (IOException io) {
			System.out.println("Could not start emulator: Unknown IO exception occurred during reading " + io);
			System.exit(0);
		}//end try-catch
		
	}//end method main
	
	/**Creates new Chip8 emulator.
	 * @param rom The contents of the ROM to be run by the emulator
	 * @param scaleFactor The factor by which to scale the emulated display
	 */
	public Chip8Emulator(byte[] rom, int scaleFactor) {
		this.emulator = new Chip8();
		this.emulator.loadROM(rom);
		this.emulatorDisplay = emulator.getVisual();
		this.emulatorDisplay.scale(scaleFactor);
		
		initKeyboardController();
		initWindow();
		initCycleTimer();
	}//end constructor method

	private void initCycleTimer() {
		this.cycleTimer = new Timer("CycleTimer", true);
		TimerTask mainCycle = new TimerTask() {
			@Override
			public void run() {
				System.out.println("Running main cycle");
				emulator.cycle();
				emulatorDisplay.update();
			}//end method run
		};
		
		this.cycleTimer.scheduleAtFixedRate(mainCycle, 0, 17);
	}//end initCycleTimer

	private void initKeyboardController() {
		Keypad keypad = new Keypad();
		emulator.attachKeypad(keypad);
		
	}

	private void initWindow() {
		this.setTitle("CHIP-8 Emulator");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(emulatorDisplay);
		
		this.pack();
		this.setVisible(true);
	}//end method initWindow

	/**Reads the CHIP-8 ROM file at the given file location.
	 * @param filename The file location of the requested ROM file.
	 * @return a byte array containing the contents of the requested file.
	 * @throws FileNotFoundException If the requested file does not exist.
	 * @throws IOException If an IO exception occurs during ROM reading.
	 * @throws OutOfMemoryError If the size of the ROM file read exceeds the available memory of the CHIP-8 system.
	 */
	private static byte[] readROM(String filename) throws FileNotFoundException, IOException, OutOfMemoryError {
		File romFile = new File(filename);
		FileInputStream romReader = new FileInputStream(romFile);
		
		byte[] romContents = romReader.readAllBytes();
		romReader.close();
		
		if(romContents.length > (Chip8.MAIN_MEMORY_SIZE - Chip8.ROM_START_ADDRESS))
			throw new OutOfMemoryError("ROM file size exceeds maximum allocatable CHIP-8 system memory");
		
		return romContents;
	}//end method readROM

}//end class Driver
