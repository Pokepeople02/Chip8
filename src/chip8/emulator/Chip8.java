package chip8.emulator;

/**Emulator for the CHIP-8 system.
 * @author Douglas T.
 * Last modified: 4/30/2021
 */
public class Chip8 {
	protected byte[] memory;
	protected boolean[][] displayMemory;
	private Processor cpu;
	private Keypad keypad;
	
	private InstructionTable decoder;
	
	public static final int MAIN_MEMORY_SIZE = 4096;
	public static final int DISPLAY_HEIGHT = 32;
	public static final int DISPLAY_WIDTH = 64;
	public static final int FONT_START_ADDRESS = 0x050;
	public static final short ROM_START_ADDRESS = 0x200;
	public static final int SPRITE_WIDTH = 8;
	public static final int FONT_WIDTH = 5;
	
	/**Initializes the emulator and prepares for ROM reading. */
	public Chip8() {
		this.memory = new byte[MAIN_MEMORY_SIZE];
		this.displayMemory = new boolean[DISPLAY_WIDTH][DISPLAY_HEIGHT];
		
		this.cpu = new Processor(this);
		this.decoder = new InstructionTable(cpu);
		
		loadFont();
	}//end constructor method
	
	/**Loads ROM into memory from the provided byte data.
	 * @param rom Byte array containing the ROM data
	 */
	public void loadROM(byte[] rom) {
		for(int i = 0; i < rom.length; ++i)
			this.memory[ROM_START_ADDRESS + i] = rom[i];
	}//end method LoadROM
	
	/**Loads the built-in font set into memory.*/
	private void loadFont() {
		/* Default font sprite set of 16 characters (0-9, A-F)
		 * Stored as shorts for convenience */
		final short[] FONT_SET = {	
				0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
				0x20, 0x60, 0x20, 0x20, 0x70, // 1
				0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
				0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
				0x90, 0x90, 0xF0, 0x10, 0x10, // 4
				0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
				0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
				0xF0, 0x10, 0x20, 0x40, 0x40, // 7
				0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
				0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
				0xF0, 0x90, 0xF0, 0x90, 0x90, // A
				0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
				0xF0, 0x80, 0x80, 0x80, 0xF0, // C
				0xE0, 0x90, 0x90, 0x90, 0xE0, // D
				0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
				0xF0, 0x80, 0xF0, 0x80, 0x80  // F	
			};
		
		for(int i = 0; i < FONT_SET.length; ++i)
			memory[FONT_START_ADDRESS + i] = (byte)FONT_SET[i];
	}//end method LoadFont
	
	/** Attaches a virtual keypad to the emulated CHIP-8 system.
	 * @param keypad The keypad to be attached to the CHIP-8.
	 */
	public void attachKeypad(Keypad keypad) {
		this.keypad = keypad;
	}//end method attachKeypad
	
	/**Queries the attached keypad, if one exists, about whether the key with the supplied value is currently being pressed.
	 * @param requestedKey The byte value of the key to be queried.
	 * @return True, if the requested key is being pressed. If not, or if no keypad is currently attached, false.
	 */
	public boolean queryKeyboard(byte requestedKey) {
		return (this.keypad != null && this.keypad.isKeyPressed(requestedKey));
	}//end method queryKeyboard
	
	public byte[] queryKeyboard() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Completes one cycle of the emulator. Loads the next instruction from memory, decodes it, and executes it. */
	public void cycle() {
		short opcode = fetch();
		cpu.iteratePC();
		execute(decode(opcode));
	}//end method cycle
	
	/**Fetches the next instruction from memory
	 * @return The next two bytes of instructions as a short
	 */
	public short fetch() {
		return (short) ((this.memory[cpu.getPC()] << 8) | this.memory[cpu.getPC() + 1]);
	}//end method fetch
	
	/**Decodes the given opcode.
	 * @param opcode The encoded instruction to be decoded
	 * @return The equivalent Instruction to be executed
	 */
	public Instruction decode(short opcode) {
		return decoder.getInstruction(opcode);
	}//end method decode

	/**Executes the provided instruction
	 * @param instruction The instruction to be executed.
	 */
	public void execute(Instruction instruction) {
		instruction.execute();
	}//end method execute
	
}//end class Chip8
