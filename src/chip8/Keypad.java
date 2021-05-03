package chip8;

import java.util.Hashtable;

public class Keypad {

	/** Table of keys and their associated byte value */
	private Hashtable<Byte, Key> keys;
	
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
		this.keys = new Hashtable<Byte, Key>();
		
		for(byte keyByte : Keypad.KEYS)
			this.keys.put(keyByte, new Key());
	}//end constructor method
	
	/**Presses the key associated with the provided byte value.
	 * If no such key exists, or if the requested key is already being pressed, does nothing.
	 * @param key The byte value of the key to be pressed.
	 */
	public void pressKey(byte key) {
		this.keys.getOrDefault(key, dummyKey).press();
	}//end method pressKey
	
	/**Releases the key associated with the provided byte value.
	 * If no such key exists, or if the requested key is not being pressed, does nothing.
	 * @param key The byte value of the key to be released.
	 */
	public void releaseKey(byte key) {
		this.keys.getOrDefault(keys, dummyKey).release();
	}//end method releaseKey
	
	/**Gets whether the key associated with the provided byte value is currently pressed.
	 * @param requestedKey The byte value of the key to be queried
	 * @return True, if the requested key is being pressed. If not, or if no such key exists, returns false.
	 */
	public boolean isKeyPressed(byte requestedKey) {
		Key result = this.keys.get(requestedKey);
		return (result != null && result.isPressed());
	}//end method isKeyPressed

}//end class Keypad
