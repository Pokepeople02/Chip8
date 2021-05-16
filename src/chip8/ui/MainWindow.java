package chip8.ui;

import javax.swing.JFrame;

import chip8.emulator.Chip8;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private final Chip8 emulator = new Chip8();
	
	private final KeyboardAdapter emulatorController = new KeyboardAdapter();
	
	private DisplayPanel emulatorDisplay;
	
	private int displayScale;
	
	/**Creates the main window to drive the emulator.
	 * @param rom The location of the ROM to be loaded
	 * @param initialScale The initial scale factor for the emulator's display
	 * @param cycleDelay The delay between emulator cycle executions, in milliseconds
	 */
	public MainWindow(String rom, int cycleDelay, int initialScale) {
		super("CHIP-8 Emulator | ROM: " + rom);
		this.displayScale = initialScale;
		
		if( emulator.loadROM(rom) ) {
			emulator.attachKeypad(this.emulatorController.getKeypad());
			
			this.emulatorDisplay = (DisplayPanel) emulator.getDisplay();
			this.emulatorDisplay.scale(displayScale);
			
			initWindow();
			
			emulator.startEmulation(cycleDelay);
		}//end if
		else System.exit(0);
	}//end constructor method
	
	private void initWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		isFocused();
		
		setContentPane(this.emulatorDisplay);
		addKeyListener(this.emulatorController);
		
		this.pack();
		this.setVisible(true);
	}//end method frameInit
	
}//end class MainWindow