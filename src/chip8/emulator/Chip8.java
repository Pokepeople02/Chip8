package chip8.emulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import chip8.gui.DisplayPanel;

/**Emulator/interpreter for the CHIP-8 virtual machine.
 * @author Douglas T. | GitHub: Pokepeople02
 */
public class Chip8 {
	/* CHIP-8 internal system components */
		/**4096-byte CHIP-8 main memory */
		private byte[] memory = new byte[MAIN_MEMORY_SIZE];
		
		/**(64 * 32)-bit CHIP-8 display buffer memory */
		private boolean[][] displayMemory = new boolean[DISPLAY_WIDTH][DISPLAY_HEIGHT];
		
		/**16 8-bit general purpose registers */
		private byte[] registers = new byte[NUM_REGISTERS];
		
		/**16-bit index register */
		private short index;
		
		/**16-bit program counter */
		private short pc;
		
		/**16-level stack for storing subroutine call return addresses */
		private short[] callStack = new short[CALL_STACK_SIZE];
		
		/**8-bit stack pointer */
		private byte sp;
		
		/**8-bit delay timer register */
		private byte delayTimer;
		
		/**8-bit sound timer register */
		private byte soundTimer;
	
	/* CHIP-8 external system components*/
		/** Emulated CHIP-8 16-key system keypad */
		private Keypad keypad;
		
		/** Emulated CHIP-8 64 * 32 pixel display screen */
		private Display display = new DisplayPanel(this);
	
	/* Emulator components for driving CHIP-8 system processes and operations */
		/** Timer to schedule delay timer decrement every 17 ms (roughly 60 Hz) */
		private Timer delayTimerDecrementer = new Timer("DelayTimer", true);
		
		/** Timer to schedule sound timer decrement every 17 ms (roughly 60 Hz) */
		private Timer soundTimerDecrementer = new Timer("SoundTimer", true);
		
		/** Random number generator for use in rnd operation */
		private Random rand = new Random();
		
		/** Decodes opcodes from ROM loaded into memory into usable emulator instructions */
		private InstructionMapper decoder = new InstructionMapper(this);
		
		/** The current opcode being executed */
		private short opcode;

	/** Timer to schedule automatic cycling of emulation */
	private Timer cycleTimer = new Timer("CycleTimer", false);
	
	private TimerTask cycleEmulatorTask = new TimerTask() {
		@Override
		public void run() {
			cycle();
		}//end method run
	};
	
	private long cycleCount;

	/** The number of bytes available in main memory */
	public static final short MAIN_MEMORY_SIZE = 4096;
	
	/** The vertical length of the emulated display */
	public static final byte DISPLAY_HEIGHT = 32;
	
	/** The horizontal length of the emulated display */
	public static final byte DISPLAY_WIDTH = 64;
	
	/** The maximum depth of the call stack */
	public static final byte CALL_STACK_SIZE = 16;

	/** The number of general-purpose registers present */
	public static final byte NUM_REGISTERS = 16;
	
	/** The starting address in main memory where the font is loaded */
	public static final short FONT_START_ADDRESS = 0x050;
	
	/** The starting address in main memory where a ROM is to be loaded */
	public static final short ROM_START_ADDRESS = 0x200;
	
	/** The width of a CHIP-8 sprite */
	private static final byte SPRITE_WIDTH = 8;
	
	/** The width of a CHIP-8 font sprite */
	private static final byte FONT_WIDTH = 5;
	
	/**The default CHIP-8 font sprite set of 16 characters (0-9, A-F).<br>
	 * Stored as a short array for convenience */
	private static final short[] FONT_SET = {	
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
	
	/**Creates a new CHIP-8 emulator */
	public Chip8() {
		System.out.println("Creating new CHIP-8 emulator object");
		loadFont();
		initTimers();
	}//end constructor method
	
	/**Loads ROM data into memory from the provided file location.
	 * @param filename String containing the name and location of the ROM file
	 * @return True, if the load was successful. If not, false.
	 */
	public boolean loadROM(String filename) {
		byte[] fileContents;
		
		try {
			//Attempt reading supplied file contents
			System.out.println("Attempting to load ROM data from file " + filename);
			
			FileInputStream fileReader = new FileInputStream(filename);
			fileContents = fileReader.readAllBytes();
			
			fileReader.close();
			
			//Check if size of file contents exceeds maximum available space for a ROM
			if(fileContents.length > Chip8.MAIN_MEMORY_SIZE - Chip8.ROM_START_ADDRESS)
				throw new OutOfMemoryError("Supplied file " + filename + " too large to fit into CHIP-8 memory");
		} catch(FileNotFoundException fnf) {
			System.err.println("ROM load failed: Requested file " + filename + " could not be found.");
			fnf.printStackTrace();
			
			return false;
		} catch(IOException io) {
			System.err.println("ROM load failed: IO Exception occurred during read.");
			io.printStackTrace();
			
			return false;
		} catch(OutOfMemoryError oom) {
			System.err.println("ROM load failed: Invalid file size for a CHIP-8 ROM.");
			oom.printStackTrace();
			
			return false;
		}//end try-catch
		
		//Copy read content to main memory and return success
		System.out.println("ROM loaded successful, attempting copy to memory");
		System.arraycopy(fileContents, 0, this.memory, Chip8.ROM_START_ADDRESS, fileContents.length);
		this.pc = Chip8.ROM_START_ADDRESS;
		
		System.out.println("ROM data successfully copied to memory");
		return true;
	}//end method LoadROM
	
	/**Gets the emulated display of the emulator.
	 * @return A reference to the Display visualizing this emulator's display memory.
	 */
	public Display getDisplay() {
		System.out.println("Getting emulator display");
		return this.display;
	}//end method getVisual
	
	/** Attaches an emulated keypad to the virtual CHIP-8 system.
	 * @param keypad The keypad to be attached to the CHIP-8.
	 */
	public void attachKeypad(Keypad keypad) {
		System.out.println("Attaching new emulated keypad");
		this.keypad = keypad;
	}//end method attachKeypad
	
	/**Grabs the current state of the display memory buffer for the emulated CHIP-8 system.
	 * @return An array containing boolean representations of each pixel, where true indicates the given screen pixel is on and false indicates it is off.
	 */
	public boolean[][] getCurrentDisplayBuffer() {
		System.out.println("Grabbing the current display buffer");
		return this.displayMemory;
	}//end method getCurrentDisplayBuffer
	
	/** Completes one cycle of the emulator. Loads the next instruction from memory, decodes it, and executes it, then updates the display. */
	public void cycle() {
		System.out.println("\nEmulator cycle " + this.cycleCount);
		
		//Fetch
		this.opcode = fetch();
		incrementPC();
		System.out.println("Fetched opcode " + String.format("0x%04X", this.opcode));
		
		//Decode and execute
		execute(decode(this.opcode));
		
		//Update display
		display.update();
		
		//Iterate cycle count
		this.cycleCount++;
	}//end method cycle
	
	/** Begins auto-cycling the emulation.
	 * @param cycleDelay The delay between cycle executions, in milliseconds.
	 */
	public void startEmulation(int cycleDelay) {
		System.out.println("Starting emulation with cycle delay " + cycleDelay);
		
		this.cycleTimer.scheduleAtFixedRate(this.cycleEmulatorTask, 0, cycleDelay);
	}//end method startEmulation
	
	/**Stops emulator auto-cycling.*/
	public void stopEmulation() {
		System.out.println("Stopping emulation");
		
		this.cycleTimer.cancel();
		this.cycleTimer.purge();
	}//end method stopEmulation
	
	/**Loads the built-in font set into memory.*/
	private void loadFont() {
		System.out.println("Attempting to copy font data into memory");
		
		for(int i = 0; i < Chip8.FONT_SET.length; ++i)
			this.memory[Chip8.FONT_START_ADDRESS + i] = (byte) Chip8.FONT_SET[i];
		
		System.out.println("Font data successfully copied to memory");
	}//end method LoadFont
	
	/** Initializes the sound and delay timers */
	private void initTimers() {
		//Initiate delay timer decrementer
		System.out.println("Initializing delay timer");
		TimerTask delayDecrement = new TimerTask() {
			@Override
			public void run() {
				if(delayTimer != 0) {
					delayTimer--;
					System.out.println("Delay timer: " + delayTimer);
				}//end if
			}//end method run
		};
		this.delayTimerDecrementer.scheduleAtFixedRate(delayDecrement, 0, 17);
		
		//Initiate sound timer decrementer
		System.out.println("Initializing sound timer");
		TimerTask soundDecrement = new TimerTask() {
			@Override
			public void run() {
				if(soundTimer != 0) {
					//TODO implement simple tone when decrement takes place
					soundTimer--;
					System.out.println("Sound timer: " + delayTimer);
				}//end if
			}//end method run
		};
		this.soundTimerDecrementer.scheduleAtFixedRate(soundDecrement, 0, 17);
	}//end method initTimers
	
	/**Fetches the next instruction from memory.
	 * Stops emulation if end of memory is reached.
	 * @return The next two bytes of instructions as a short
	 */
	private short fetch() {
		try {
			return (short) ((this.memory[this.pc] << 8) | this.memory[this.pc + 1]);
		} catch(ArrayIndexOutOfBoundsException oob) {
			System.out.println("Handled OutOfBoundsException in main memory.");
			
			this.stopEmulation();
			return -1;
		}//end try-catch
	}//end method fetch
	
	/**Decodes the given opcode.
	 * @param opcode The encoded instruction to be decoded
	 * @return The equivalent Instruction to be executed
	 */
	private Instruction decode(short opcode) {
		return decoder.getInstruction(opcode);
	}//end method decode

	/**Executes the provided instruction
	 * @param instruction The instruction to be executed.
	 */
	private void execute(Instruction instruction) {
		instruction.execute();
	}//end method execute
	
	/**Increments the PC by two bytes */
	private void incrementPC() {
		this.pc += 2;
	}//end method incrementPC
	
	/**Decrements the PC by two bytes */
	private void decrementPC() {
		this.pc -= 2;
	}//end method decrementPC
	
	/* CHIP-8 instruction methods */
	
	/**CLS:<br>
	 * Clears the display memory
	 */
	protected void cls_00E0() {
		System.out.println("Executing CLS");
		
		this.displayMemory = new boolean[Chip8.DISPLAY_WIDTH][Chip8.DISPLAY_HEIGHT];
	}//end method cls_00E0
	
	/**RET:<br>
	 * Pops a return address off of the stack and returns to it.
	 */
	protected void ret_00EE() {
		System.out.println("Executing RET");
		
		this.pc = this.callStack[sp--];
	}//end method ret_00EE
	
	/**JP addr:<br>
	 * Jumps to the address indicated by the lower 3 nibbles of the opcode.
	 */
	protected void jp_1nnn() {
		short addr = (short) (this.opcode & 0x0FFF);
		
		System.out.println("Executing JP " + Short.toUnsignedInt(addr));
		
		this.pc = addr;
	}//end method jp_1nnn
	
	/**CALL addr:<br>
	 * Calls the subroutine at the address indicated by the lower 3 nibbles of the opcode.
	 */
	protected void call_2nnn() {
		short addr = (short) (this.opcode & 0x0FFF);
		
		System.out.println("Executing CALL " + Short.toUnsignedInt(addr));
		
		this.callStack[this.sp++] = this.pc;
		this.pc = addr;
	}//end method call_2nnn
	
	/**SE Vx, kk:<br>
	 * Skips the next instruction if the value in register Vx is equal to byte kk.
	 */
	protected void se_3xkk() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte kk = (byte) (this.opcode & 0x00FF);
		
		System.out.println("Executing SE V" + Byte.toUnsignedInt(x) + ", " + Byte.toUnsignedInt(kk));
		
		if(this.registers[x] == kk)
			incrementPC();
	}//end method se_3xkk
	
	/**SNE Vx, kk:<br>
	 * Skips the next instruction if the value in register Vx is not equal to byte kk.
	 */
	protected void sne_4xkk() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte kk = (byte) (this.opcode & 0x00FF);
		
		System.out.println("Executing SNE V" + Byte.toUnsignedInt(x) + ", " + Byte.toUnsignedInt(kk));
		
		if(this.registers[x] != kk)
			incrementPC();
	}//end method sne_4xkk
	
	/**SE Vx, Vy:<br>
	 * Skips the next instruction if the value in register Vx is equal to that in register Vy.
	 */
	protected void se_5xy0() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		
		System.out.println("Executing SE V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y));
		
		if(this.registers[x] == this.registers[y])
			incrementPC();
	}//end method se_5xy0
	
	/**LD Vx, kk:<br>
	 * Loads the value of byte kk into register Vx.
	 */
	protected void ld_6xkk() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte kk = (byte) (this.opcode & 0x00FF);
		
		System.out.println("Executing LD V" + Byte.toUnsignedInt(x) + ", " + Byte.toUnsignedInt(kk));
		
		this.registers[x] = kk;
	}//end method ld_6xkk
	
	/**ADD Vx, kk:<br>
	 * Adds the byte value kk to the value stored in register Vx.
	 */
	protected void add_7xkk() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte kk = (byte) (this.opcode & 0x00FF);
		
		System.out.println("Executing ADD V" + Byte.toUnsignedInt(x) + ", " + Byte.toUnsignedInt(kk));
		
		this.registers[x] += kk;
	}//end method add_7xkk
	
	/**LD Vx, Vy:<br>
	 * Loads the value in register Vy into register Vx.
	 */
	protected void ld_8xy0() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		
		System.out.println("Executing LD V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y));
		
		this.registers[x] = this.registers[y];
	}//end method ld_8xy0
	
	/**OR Vx, Vy:<br>
	 * Sets register Vx to the value of register Vx OR the value of register Vy.
	 */
	protected void or_8xy1() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		
		System.out.println("Executing OR V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y));
		
		this.registers[x] |= this.registers[y];
	}//end method or_8xy1
	
	/**AND Vx, Vy: <br>
	 * Sets register Vx to the value of register Vx AND the value of register Vy.
	 */
	protected void and_8xy2() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		
		System.out.println("Executing AND V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y));
		
		this.registers[x] &= this.registers[y];
	}//end method and_8xy2
	
	/**XOR Vx, Vy: <br>
	 * Sets register Vx to the value of register Vx XOR the value of register Vy.
	 */
	protected void xor_8xy3() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		
		System.out.println("Executing XOR V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y));
		
		this.registers[x] ^= this.registers[y];
	}//end method xor_8xy3
	
	/**ADD Vx, Vy:<br>
	 * Sets register Vx to the sum of the values in registers Vx and Vy.
	 * Sets register VF to 1 if overflow occurred during the addition. Else, 0.
	 */
	protected void add_8xy4() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		
		System.out.println("Executing ADD V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y));
		
		short sum = (short) (this.registers[x] + this.registers[y]);
		this.registers[0xF] = (byte) (sum > 0xFF ? 0x1 : 0x0);
		this.registers[x] = (byte) sum;
	}//end method add_8xy4
	
	/**SUB Vx, Vy:<br>
	 * Sets register Vx to the value in register Vy subtracted from the value in register Vx.
	 * Sets register VF to 1 if the value in register Vx is greater than that in Vy. Else, 0.
	 */
	protected void sub_8xy5() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		
		System.out.println("Executing SUB V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y));
		
		this.registers[0xF] = (byte) (registers[x] > registers[y] ? 0x1 : 0x0);
		this.registers[x] -= this.registers[y];
	}//end method sub_8xy5
	
	/**SHR Vx: <br>
	 * Shifts the value stored in register Vx right by 1.
	 * Stores the least significant bit of Vx in register VF.
	 */
	protected void shr_8xy6() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing SHR V" + Byte.toUnsignedInt(x));
		
		this.registers[0xF] = (byte) (registers[x] & 0x1) ;
		this.registers[x] >>>= 1;
	}//end method shr_8xy6
	
	/**SUBN Vx, Vy: <br>
	 * Sets register Vx to the value of register Vx subtracted from the value of register Vy.
	 * Sets register VF to 1 if the value in register Vy is greater than that in Vx. Else, 0.
	 */
	protected void subn_8xy7() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		
		System.out.println("Executing SUBN V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y));
		
		this.registers[0xF] = (byte) (registers[y] > registers[x] ? 0x1 : 0x0);
		this.registers[x] = (byte) (registers[y] - registers[x]);
	}//end method subn_8xy7
	
	/**SHL Vx: <br>
	 * Shifts the value stored in register Vx left by 1.
	 * Stores the most significant bit of register Vx in register VF.
	 */
	protected void shl_8xyE() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing SHL V" + Byte.toUnsignedInt(x));
		
		this.registers[0xF] = (byte) ((registers[x] & 0x80) >>> 7);
		this.registers[x] <<= 1;
	}//end method shl_8xyE
	
	/**SNE Vx, Vy: <br>
	 * Skips the next instruction if the value in register Vx is not equal to that in register Vy.
	 */
	protected void sne_9xy0() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		
		System.out.println("Executing SNE V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y));
		
		if(this.registers[x] != this.registers[y])
			incrementPC();
	}//end method sne_9xy0
	
	/**LD I, addr: <br>
	 * Stores the address supplied into the index register.
	 */
	protected void ld_Annn() {
		short addr = (short) (this.opcode & 0x0FFF);
		
		System.out.println("Executing LD I, " + Short.toUnsignedInt(addr));
		
		this.index = addr;
	}//end method ld_Annn
	
	/**JP V0, addr: <br>
	 * Sets the program counter to the sum of the address supplied and the value stored in register V0.
	 */
	protected void jp_Bnnn() {
		short addr = (short) (this.opcode & 0x0FFF);
		
		System.out.println("Executing JP V0, " + Short.toUnsignedInt(addr));
		
		this.pc = (short) (this.registers[0x0] + addr);
	}//end method jp_Bnnn
	
	/**RND Vx, kk: <br>
	 * Stores a random byte AND the supplied byte kk into register Vx.
	 */
	protected void rnd_Cxkk() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte kk = (byte) (this.opcode & 0x00FF);
		
		System.out.println("Executing RND V" + Byte.toUnsignedInt(x) + ", " + Byte.toUnsignedInt(kk));
		
		byte[] randByte = new byte[1];
		this.rand.nextBytes(randByte);
		this.registers[x] = (byte) (randByte[0] & kk);
	}//end method rnd_Cxkk
	
	/**DRW Vx, Vy, n:<br>
	 * Draws an n-bytes-tall sprite starting at the address stored in the index register to location (Vx, Vy) in the CHIP-8's display memory.
	 * If the sprite drawn goes beyond the boundaries of the screen, it wraps.
	 * Set the value in register VF to 1 if a sprite collision occurred while drawing. Else, 0.
	 */
	protected void drw_Dxyn() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		byte y = (byte) (this.opcode & 0x00F0 >>> 4);
		byte n = (byte) (this.opcode & 0x000F);
		
		System.out.println("Executing DRW V" + Byte.toUnsignedInt(x) + ", V" + Byte.toUnsignedInt(y) + ", " + Byte.toUnsignedInt(n));
		
		//Get sprite x and y positions within the display boundaries, wrapping if beyond display bounds
		byte xStartPos = (byte) (this.registers[x] % Chip8.DISPLAY_WIDTH);
		byte yStartPos =  (byte) (this.registers[y] % Chip8.DISPLAY_HEIGHT);
		
		this.registers[0xF] = 0x0;
		//Iterate over 8 columns and n rows of sprite
		for(int row = 0; row < n; ++row) {
			byte spriteNextByte = this.memory[this.index + row];
			
			for(int column = 0; column < Chip8.SPRITE_WIDTH; ++column) {
				//Isolate the next bit in the next byte of the sprite
				byte spritePixel = (byte) (spriteNextByte & (0x80 >>> column) >>> Chip8.SPRITE_WIDTH - column); 
				
				//Get whether the isolated sprite pixel bit is on, and whether the screen pixel is already on.
				boolean isSpritePixelOn = spritePixel == 0x1;
				boolean isScreenPixelOn = this.displayMemory[xStartPos + column][yStartPos + row];
				
				//Set screen pixel to be on if sprite pixel is on XOR screen pixel was already on
				this.displayMemory[xStartPos + column][yStartPos + row] = isSpritePixelOn ^ isScreenPixelOn;
				
				//Set whether a sprite collision occurred
				if(isSpritePixelOn && isScreenPixelOn)
					registers[0xF] = 0x1;
			}//end for
			
		}//end for
		
	}//end method drw_Dxyn
	
	/**SKP Vx:<br>
	 * Skips the next instruction if the keypad key with the value in register Vx is being pressed.
	 */
	protected void skp_Ex9E() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing SKP V" + Byte.toUnsignedInt(x));
		
		if(this.keypad != null && this.keypad.isKeyPressed(this.registers[x]))
			incrementPC();
	}//end method skp_Ex9E
	
	/**SKNP Vx:<br>
	 * Skips the next instruction if the keypad key with the value in register Vx is not being pressed.
	 */
	protected void sknp_ExA1() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing SKNP V" + Byte.toUnsignedInt(x));
		
		if(this.keypad == null || !this.keypad.isKeyPressed(this.registers[x]))
			incrementPC();
	}//end method sknp_ExA1
	
	/**LD Vx, DT:<br>
	 * Loads the current value of the delay timer into register Vx.
	 */
	protected void ld_Fx07() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing LD V" + Byte.toUnsignedInt(x) + ", DT");
		
		this.registers[x] = this.delayTimer;
	}//end method ld_Fx07
	
	/**LD Vx, K:<br>
	 * Waits for a keypad key to be pressed. Once a key is pressed, loads the byte value of the lowest value pressed key into register Vx.
	 */
	protected void ld_Fx0A() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing LD V" + Byte.toUnsignedInt(x) + ", K");
		
		byte[] keysPressed = this.keypad.getKeysPressed();
		//If no keys are pressed, re-run this instruction
		if(keysPressed.length == 0)
			decrementPC();
		else
			this.registers[x] = keysPressed[0];
	}//end method ld_Fx0A
	
	/**LD DT, Vx:<br>
	 * Loads the current value of register Vx into the delay timer.
	 */
	protected void ld_Fx15() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing LD DT, V" + Byte.toUnsignedInt(x));
		
		this.delayTimer = this.registers[x];
	}//end method ld_Fx15
	
	/**LD ST, Vx:<br>
	 * Loads the current value of register Vx into the sound timer.
	 */
	protected void ld_Fx18() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing LD ST, V" + Byte.toUnsignedInt(x));
		
		this.soundTimer = this.registers[x];
	}//end method ld_Fx18
	
	/**ADD I, Vx:<br>
	 * Adds the value in register Vx to that in the index register.
	 */
	protected void add_Fx1E() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing LD I, V" + Byte.toUnsignedInt(x));
		
		this.index += this.registers[x];
	}//end method add_Fx1E
	
	/**LD F, Vx:<br>
	 * Sets the index register to the address of the sprite for the digit held in register Vx.
	 */
	protected void ld_Fx29() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing LD F, V" + Byte.toUnsignedInt(x));
		
		this.index = (short) (Chip8.FONT_START_ADDRESS + (Chip8.FONT_WIDTH * this.registers[x]));
	}//end method ld_Fx29
	
	/**LD B, Vx:<br>
	 * Stores the BCD representation of the value in register Vx into memory, beginning at the address stored in the index register.
	 */
	protected void ld_Fx33() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing LD B, V" + Byte.toUnsignedInt(x));
		
		byte decimalValue = this.registers[x];
		for(int i = 2; i >= 0; ++i) {
			this.memory[this.index + i] = (byte) (decimalValue % 10);
			decimalValue /= 10;
		}//end for
	}//end method ld_Fx33
	
	/**LD [I], Vx:<br>
	 * Loads the values in registers V0 through Vx into memory starting at the address stored in the index register.
	 */
	protected void ld_Fx55() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing LD [I], V" + Byte.toUnsignedInt(x));
		
		for(int i = 0; i <= x; ++i)
			this.memory[this.index + i] = this.registers[i];
	}//end method ld_Fx55
	
	/**LD Vx, [I]:<br>
	 * Loads the values in memory starting at the address stored in the index register into registers V0 through Vx.
	 */
	protected void ld_Fx65() {
		byte x = (byte) (this.opcode & 0x0F00 >>> 8);
		
		System.out.println("Executing LD V" + Byte.toUnsignedInt(x) + ", [I]");
		
		for(int i = 0; i <= x; ++i)
			this.registers[i] = this.memory[this.index + i];
	}//end method ld_Fx65
	
	/**Dummy instruction for handling invalid opcode requests*/
	protected void nop_dummy() {
		System.out.println("Invalid opcode " + String.format("0x%04X", this.opcode));
		System.out.println("Executing NOP as failsafe");
	}//end method nop_dummy
	
}//end class Chip8
