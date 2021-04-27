package chip8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**Emulator for the CHIP-8 system.
 * @author Douglas T.
 * Last modified: 4/26/2021
 */
public class Chip8 {
	/** Sixteen general-purpose 8-bit registers */
	private short[] registers;
	
	/**4K bytes of main memory */
	private short[] memory;
	
	/** 16-bit Program Counter */
	private int pc;
	
	/** Sixteen layer Call Stack */
	private int[] callStack;
	
	/** 8-bit Stack Pointer */
	private short sp;
	
	/** 8-bit simple delay timer */
	private short delayTimer;
	
	/** 8-bit simple sound timer */
	private short soundTimer;
	
	/** Memory for 64 pixel wide * 32 pixel tall 1-bit display */
	private boolean[][] displayMemory;
	
	/**Total number general-purpose registers present */
	public static final int NUM_REGISTERS = 16;
	
	/**Total number bytes of memory present */
	public static final int MEMORY_SIZE = 4096;
	
	/**Total depth of the call stack */
	public static final int STACK_DEPTH = 16;
	
	/**Height of display buffer */
	public static final int DISPLAY_HEIGHT = 32;
	
	/** Width of display buffer */
	public static final int DISPLAY_WIDTH = 64;
	
	/** Default font sprite set of 16 characters (0-9, A-F) */
	private static final short[] FONT_SET =
		{
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
	
	/** The starting address of memory reserved for ROM instructions */
	public static final int ROM_START_ADDRESS = 0x200;
	
	/** The starting address of memory reserved for the built-in font set */
	public static final int FONT_START_ADDRESS = 0x050;
	
	/**Initializes the emulator and prepares for ROM reading. */
	public Chip8() {
		this.registers = new short[NUM_REGISTERS];
		this.memory = new short[MEMORY_SIZE];
		this.callStack = new int[STACK_DEPTH];
		this.sp = 0;
		this.displayMemory = new boolean[DISPLAY_WIDTH][DISPLAY_HEIGHT];
		
		LoadFont();
	}//end constructor method
	
	/**Loads ROM data from the provided input file.
	 * @param file The input file
	 * @throws FileNotFoundException If the supplied file cannot be read.
	 * @throws IOException If an IO exception occurs during file read.
	 */
	public void LoadROM(File file) throws FileNotFoundException, IOException {
		try {
			FileInputStream fs = new FileInputStream(file);
			byte[] readBuffer = fs.readAllBytes();
			
			for(int i = 0; i < readBuffer.length; ++i)
				memory[ROM_START_ADDRESS + i] = readBuffer[i];
			
			fs.close();
		}//end try
		catch (FileNotFoundException fnf) {
			throw new FileNotFoundException("Unable to load ROM file " + file.getName());
		}//end catch
		
		this.pc = ROM_START_ADDRESS;
	}//end method LoadROM
	
	/**Loads the built-in font set into memory.*/
	private void LoadFont() {
		for(int i = 0; i < FONT_SET.length; ++i)
			memory[FONT_START_ADDRESS + i] = FONT_SET[i];
	}//end method LoadFont
	
}//end class Chip8
