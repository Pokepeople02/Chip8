package chip8.emulator;

import java.util.HashMap;

import chip8.Chip8Emulator;

/**Virtual keypad for emulated CHIP-8 system. Handles key press/release queries. 
 * @author Douglas T. | GitHub: Pokepeople02
 */
public class Keypad {

	/** Mapping of keys and their associated byte value */
	private HashMap<Byte, Key> keys = new HashMap<Byte, Key>(Keypad.KEYS.length);
	
	/** Nonexistent default dummy key to handle invalid key requests */
	private static final Key dummyKey = new Key();
	
	/** Table of all keys' associated byte values */
	public static final byte[] KEYS = {
			0x0, 0x1, 0x2, 0x3,
			0x4, 0x5, 0x6, 0x7,
			0x8, 0x9, 0xA, 0xB,
			0xC, 0xD, 0xE, 0xF
	};
	
	/** Creates a new CHIP-8 keypad */
	public Keypad() {
		for(byte keyByte : Keypad.KEYS)
			this.keys.put(keyByte, new Key());
	}//end constructor method
	
	/**Presses the key associated with the provided byte value.
	 * If no such key exists, or if the requested key is already being pressed, does nothing.
	 * @param key The byte value of the key to be pressed.
	 */
	public void pressKey(byte key) {
		Chip8Emulator.debugLog("Pressing key " + Byte.toUnsignedInt(key));
		
		this.keys.getOrDefault(key, dummyKey).press();
	}//end method pressKey
	
	/**Releases the key associated with the provided byte value.
	 * If no such key exists, or if the requested key is not being pressed, does nothing.
	 * @param key The byte value of the key to be released.
	 */
	public void releaseKey(byte key) {
		Chip8Emulator.debugLog("Releasing key " + Byte.toUnsignedInt(key));
		
		this.keys.getOrDefault(key, dummyKey).release();
	}//end method releaseKey
	
	/**Gets whether the key associated with the provided byte value is currently pressed.
	 * @param requestedKey The byte value of the key to be queried
	 * @return True, if the requested key is being pressed. If not, or if no such key exists, returns false.
	 */
	public boolean isKeyPressed(byte requestedKey) {	
		Key result = this.keys.getOrDefault(requestedKey, Keypad.dummyKey);
		return (result != Keypad.dummyKey && result.isPressed());
	}//end method isKeyPressed
	

	/**Gets all of the keys currently being pressed.
	 * @return A byte array containing all of the byte values for all currently pressed keys. If no keys are currently pressed, returns an empty array.
	 * */
	public byte[] getKeysPressed() {
		byte[] pressedKeysBuffer = new byte[Keypad.KEYS.length];
		int numKeysPressed = 0;
		
		for(byte keyByte : Keypad.KEYS) {
			if(isKeyPressed(keyByte)) {
				Chip8Emulator.debugLog("Key " + keyByte + " is pressed");
				
				pressedKeysBuffer[numKeysPressed++] = keyByte;
			}//end if
			else
				Chip8Emulator.debugLog("Key " + keyByte + " is not pressed");
		}//end for
				
		
		byte[] pressedKeys = new byte[numKeysPressed];
		System.arraycopy(pressedKeysBuffer, 0, pressedKeys, 0, numKeysPressed);
		
		return pressedKeys;
	}//end method queryKeyboard

}//end class Keypad
