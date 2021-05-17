package chip8.ui;

import javax.swing.JFrame;

import chip8.emulator.Chip8;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private final Chip8 emulator;
	private DisplayPanel emulatorDisplay;
	private final KeyboardAdapter emulatorController;
	
	/**Creates the main window to drive the emulator.
	 * @param rom The location of the ROM to be loaded
	 * @param initialScale The initial scale factor for the emulator's display
	 * @param cycleDelay The delay between emulator cycle executions, in milliseconds
	 */
	public MainWindow(Chip8 emulator, KeyboardAdapter controller) {
		super();
		
		this.emulator = emulator;
		this.emulatorController = controller;
		this.emulatorDisplay = (DisplayPanel) this.emulator.getDisplay();
		
		initWindow();
	}//end constructor method
	
	private void initWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("CHIP-8 Emulator");
		setResizable(false);
		isFocused();
		
		setContentPane(this.emulatorDisplay);
		addKeyListener(this.emulatorController);
		
		this.pack();
		this.setVisible(true);
	}//end method frameInit
	
}//end class MainWindow
