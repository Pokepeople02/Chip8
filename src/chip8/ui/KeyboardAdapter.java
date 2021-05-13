package chip8.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import chip8.emulator.Keypad;

public class KeyboardAdapter extends KeyAdapter {
	
	private Keypad emulatedKeypad = new Keypad();
	
	/** Mapping of keyboard key IDs to the byte value of their respective Keypad key */
	private final HashMap<Integer, Byte> controlMap = new HashMap<Integer, Byte>(Keypad.KEYS.length);
	
	/**Creates a new KeyboardAdapter with the default control mapping*/
	public KeyboardAdapter() {
		initDefaultControlMap();
	}//end constructor method

	/**Initializes the default control mapping. <br>
	 * <p>Default key mappings are as follows:<br>
	 * format: key byte : keyboard key<br>
	 * 0x1 : 1, 	
	 * 0x2 : 2,
	 * 0x3 : 3,
	 * 0xC : 4
	 * <br>
	 * 0x4 : Q,
	 * 0x5 : W,
	 * 0x6 : E,
	 * 0xD : R
	 * <br>
	 * 0x7 : A,
	 * 0x8 : S,
	 * 0x9 : D,
	 * 0xE : F
	 * <br>
	 * 0xA : Z,
	 * 0x0 : X,
	 * 0xB : C,
	 * 0xF : V
	 * </p>
	 */
	private void initDefaultControlMap() {
		controlMap.put(KeyEvent.VK_1, (byte) 0x1);
		controlMap.put(KeyEvent.VK_2, (byte) 0x2);
		controlMap.put(KeyEvent.VK_3, (byte) 0x3);
		controlMap.put(KeyEvent.VK_4, (byte) 0xC);
		
		controlMap.put(KeyEvent.VK_Q, (byte) 0x4);
		controlMap.put(KeyEvent.VK_W, (byte) 0x5);
		controlMap.put(KeyEvent.VK_E, (byte) 0x6);
		controlMap.put(KeyEvent.VK_R, (byte) 0xD);
		
		controlMap.put(KeyEvent.VK_A, (byte) 0x7);
		controlMap.put(KeyEvent.VK_S, (byte) 0x8);
		controlMap.put(KeyEvent.VK_D, (byte) 0x9);
		controlMap.put(KeyEvent.VK_F, (byte) 0xE);
		
		controlMap.put(KeyEvent.VK_Z, (byte) 0xA);
		controlMap.put(KeyEvent.VK_X, (byte) 0x0);
		controlMap.put(KeyEvent.VK_C, (byte) 0xB);
		controlMap.put(KeyEvent.VK_V, (byte) 0xF);
	}//end method initDefaultControlMap
	
	/**Gets the emulated keypad that this KeyboardAdapter is using.
	 * @return The emulated Keypad used by this Adapter
	 */
	public Keypad getKeypad() {
		return this.emulatedKeypad;
	}//end method getKeypad
	
	/** Sends key press request to the associated emulated Keypad */
	@Override
	public void keyPressed(KeyEvent e) {
		this.emulatedKeypad.pressKey(this.controlMap.get(e.getKeyCode()));
	}//end method keyPressed
	
	/** Sends key release request to the associated emulated Keypad */
	@Override
	public void keyReleased(KeyEvent e) {
		this.emulatedKeypad.releaseKey(this.controlMap.get(e.getKeyCode()));
	}//end method keyPressed
	
}//end class KeyboardAdapter
