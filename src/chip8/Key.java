package chip8;

public class Key {

	private boolean pressed;
	
	/**Presses the key */
	public void press() {
		this.pressed = true;
	}//end method press

	/** Gets whether the key is currently being pressed
	 * @return True, if the key is being pressed. If not, false.
	 */
	public boolean isPressed() {
		return pressed;
	}//end method isPressed

	/**Releases the key */
	public void release() {
		this.pressed = false;
	}//end method release

}//end class Key
