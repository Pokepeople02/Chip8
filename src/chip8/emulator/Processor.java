package chip8.emulator;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Processor {

	/** The CHIP-8 system of which this processor is a part of */
	private Chip8 system;
	
	/** 8-bit general purpose registers */
	private byte[] registers;
	
	/** 16-bit index register */
	private short index;
	
	/** 16-bit program counter */
	private short pc;
	
	/** Stack for storing subroutine call return addresses */
	private short[] callStack;
	
	/** 8-bit stack pointer */
	private byte sp;
	
	/** 8-bit delay timer register */
	private byte delayTimer;
	
	/** Timer to schedule delay timer decrementation every 17 ms (roughly 60 Hz) */
	private Timer delayTimerScheduler;
	
	/** 8-bit sound timer register */
	private byte soundTimer;
	
	/** Timer to schedule sound timer decrementation every 17 ms (roughly 60 Hz) */
	private Timer soundTimerScheduler;
	
	/** Random number generator for use in certain instructions */
	private Random rand;
	
	/** The maximum size of the call stack */
	public static final int CALL_STACK_SIZE = 16;
	
	/** The number of registers present on the processor */
	public static final int NUM_REGISTERS = 16;
	
	/** Creates a new processor.
	 * @param system The CHIP-8 system that this processor will be associated with.
	 */
	public Processor(Chip8 system) {
		//Associate with the given CHIP-8 system
		this.system = system;
		
		//Initiate register and call stack arrays
		this.registers = new byte[NUM_REGISTERS];
		this.callStack = new short[CALL_STACK_SIZE];
		
		//Initiate delay timer
		this.delayTimerScheduler = new Timer("DelayTimer");
		TimerTask delayDecrement = new TimerTask() {
			@Override
			public void run() {
				if(delayTimer != 0)
					delayTimer--;
			}//end method run
		};
		this.delayTimerScheduler.scheduleAtFixedRate(delayDecrement, 0, 17);
		
		//Initiate sound timer
		this.soundTimerScheduler = new Timer("SoundTimer");
		TimerTask soundDecrement = new TimerTask() {
			@Override
			public void run() {
				if(soundTimer != 0) {
					//TODO implement simple tone when decrement takes place
					soundTimer--;
				}//end if
			}//end method run
		};
		this.soundTimerScheduler.scheduleAtFixedRate(soundDecrement, 0, 17);
		
		//Initiate random number generator
		this.rand = new Random();
		
		//Set program counter to starting position for ROM reading
		this.pc = Chip8.ROM_START_ADDRESS;
	}//end constructor method

	/** Iterates the program counter by two bytes */
	public void iteratePC() {
		this.pc += 0x2;
	}//end method iteratePC

	/** Gets the current address of the program counter */
	public short getPC() {
		return this.pc;
	}//end method getPC
	
	/** Decrements the program counter, such that the current instruction executes a second time */
	private void repeatPC() {
		this.pc -= 0x2;
	}//end method repeatPC
	
	/**CLS:<br>
	 * Clears the display memory
	 */
	public void cls_00E0() {
		system.displayMemory = new boolean[Chip8.DISPLAY_WIDTH][Chip8.DISPLAY_HEIGHT];
	}//end method cls_00E0
	
	/**RET:<br>
	 * Pops a return address off of the stack and returns to it.
	 */
	public void ret_00EE() {
		this.pc = this.callStack[sp--];
	}//end method ret_00EE
	
	/**JP addr:<br>
	 * Jumps to the supplied address.
	 * @param addr The address to be jumped to.
	 */
	public void jp_1nnn(short addr) {
		this.pc = addr;
	}//end method jp_1nnn
	
	/**CALL addr:<br>
	 * Calls the subroutine at the supplied address.
	 * @param addr The address of the subroutine to be jumped to.
	 */
	public void call_2nnn(short addr) {
		this.callStack[sp++] = this.pc;
		this.pc = addr;
	}//end method call_2nnn
	
	/**SE Vx, kk:<br>
	 * Skips the next instruction if the value in register Vx is equal to byte kk.
	 * @param regX The index of the register to check
	 * @param kk The byte value to be checked against
	 */
	public void se_3xkk(int regX, byte kk) {
		if(this.registers[regX] == kk)
			iteratePC();
	}//end method se_3xkk
	
	/**SNE Vx, kk:<br>
	 * Skips the next instruction if the value in register Vx is not equal to byte kk.
	 * @param regX The index of the register Vx
	 * @param kk The byte value to be checked against
	 */
	public void sne_4xkk(int regX, byte kk) {
		if(this.registers[regX] != kk)
			iteratePC();
	}//end method sne_4xkk
	
	/**SE Vx, Vy:<br>
	 * Skips the next instruction if the value in register Vx is equal to that in register Vy.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 */
	public void se_5xy0(int regX, int regY) {
		if(this.registers[regX] == this.registers[regY])
			iteratePC();
	}//end method se_5xy0
	
	/**LD Vx, kk:<br>
	 * Loads the value of byte kk into register Vx.
	 * @param regX The index of register Vx
	 * @param kk The byte value to be loaded.
	 */
	public void ld_6xkk(int regX, byte kk) {
		this.registers[regX] = kk;
	}//end method ld_6xkk
	
	/**ADD Vx, kk:<br>
	 * Adds the byte value kk to the value stored in register Vx.
	 * @param regX The index of register Vx
	 * @param kk The byte value to be added to Vx.
	 */
	public void add_7xkk(int regX, byte kk) {
		this.registers[regX] += kk;
	}//end method add_7xkk
	
	/**LD Vx, Vy:<br>
	 * Loads the value in register Vy into register Vx.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 */
	public void ld_8xy0(int regX, int regY) {
		this.registers[regX] = this.registers[regY];
	}//end method ld_8xy0
	
	/**OR Vx, Vy:<br>
	 * Sets register Vx to the value of register Vx OR the value of register Vy.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 */
	public void or_8xy1(int regX, int regY) {
		this.registers[regX] |= this.registers[regY];
	}//end method or_8xy
	
	/**AND Vx, Vy: <br>
	 * Sets register Vx to the value of register Vx AND the value of register Vy.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 */
	public void and_8xy2(int regX, int regY) {
		this.registers[regX] &= this.registers[regY];
	}//end method and_8xy2
	
	/**XOR Vx, Vy: <br>
	 * Sets register Vx to the value of register Vx XOR the value of register Vy.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 */
	public void xor_8xy3(int regX, int regY) {
		this.registers[regX] ^= this.registers[regY];
	}//end method xor_8xy3
	
	/**ADD Vx, Vy:<br>
	 * Sets register Vx to the sum of the values in registers Vx and Vy.
	 * Sets register VF to 1 if overflow occurred during the addition. Else, 0.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 */
	public void add_8xy4(int regX, int regY) {
		short sum = (short) (this.registers[regX] + this.registers[regY]);
		this.registers[0xF] = (byte) (sum > 0xFF ? 0x1 : 0x0);
		this.registers[regX] = (byte) sum;
	}//end method add_8xy4
	
	/**SUB Vx, Vy:<br>
	 * Sets register Vx to the value in register Vy subtracted from the value in register Vx.
	 * Sets register VF to 1 if the value in register Vx is greater than that in Vy. Else, 0.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 */
	public void sub_8xy5(int regX, int regY) {
		this.registers[0xF] = (byte) (registers[regX] > registers[regY] ? 0x1 : 0x0);
		this.registers[regX] -= this.registers[regY];
	}//end method sub_8xy5
	
	/**SHR Vx: <br>
	 * Shifts the value stored in register Vx right by 1.
	 * Stores the least significant bit of Vx in register VF.
	 * @param regX The index of register Vx.
	 */
	public void shr_8xy6(int regX) {
		this.registers[0xF] = (byte) (registers[regX] & 0x1) ;
		this.registers[regX] >>>= 1;
	}//end method shr_8xy6
	
	/**SUBN Vx, Vy: <br>
	 * Sets register Vx to the value of register Vx subtracted from the value of register Vy.
	 * Sets register VF to 1 if the value in register Vy is greater than that in Vx. Else, 0.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 */
	public void subn_8xy7(int regX, int regY) {
		this.registers[0xF] = (byte) (registers[regY] > registers[regX] ? 0x1 : 0x0);
		this.registers[regX] = (byte) (registers[regY] - registers[regX]);
	}//end method subn_8xy7
	
	/**SHL Vx: <br>
	 * Shifts the value stored in resgister Vx left by 1.
	 * Stores the most significant bit of register Vx in register VF.
	 * @param regX The index of register Vx.
	 */
	public void shl_8xyE(int regX) {
		this.registers[0xF] = (byte) ((registers[regX] & 0x80) >>> 7);
		this.registers[regX] <<= 1;
	}//end method shl_8xyE
	
	/**SNE Vx, Vy: <br>
	 * Skips the next instruction if the value in register Vx is not equal to that in register Vy.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 */
	public void sne_9xy0(int regX, int regY) {
		if(this.registers[regX] != this.registers[regY])
			iteratePC();
	}//end method sne_9xy0
	
	/**LD I, addr: <br>
	 * Stores the address supplied into the index register.
	 * @param addr The address to be stored
	 */
	public void ld_Annn(short addr) {
		this.index = addr;
	}//end method ld_Annn
	
	/**JP V0, addr: <br>
	 * Sets the program counter to the sum of the address supplied and the value stored in register V0.
	 * @param addr The address to be used to calculate the jump
	 */
	public void jp_Bnnn(short addr) {
		this.pc = (short) (this.registers[0x0] + addr);
	}//end method jp_Bnnn
	
	/**RND Vx, kk: <br>
	 * Stores a random byte AND the supplied byte kk into register Vx.
	 * @param regX The index of register Vx
	 * @param kk The supplied byte
	 */
	public void rnd_Cxkk(int regX, byte kk) {
		byte[] randByte = new byte[1];
		this.rand.nextBytes(randByte);
		this.registers[regX] = (byte) (randByte[0] & kk);
	}//end method 
	
	/**DRW Vx, Vy, n:<br>
	 * Draws an n-bytes-tall sprite starting at the address stored in the index register to location (Vx, Vy) in the CHIP-8's display memory.
	 * If the sprite drawn goes beyond the boundaries of the screen, it wraps.
	 * Set the value in register VF to 1 if a sprite collision occurred while drawing. Else, 0.
	 * @param regX The index of register Vx
	 * @param regY The index of register Vy
	 * @param n The 4-bit height of the sprite to be drawn
	 */
	public void drw_Dxyn(int regX, int regY, byte n) {
		//Trim height byte n down to a nibble
		n &= 0xF;
		
		//Get sprite x and y positions within the display boundaries, wrapping if beyond display bounds
		byte xStartPos = (byte) (this.registers[regX] % Chip8.DISPLAY_WIDTH);
		byte yStartPos =  (byte) (this.registers[regY] % Chip8.DISPLAY_HEIGHT);
		
		this.registers[0xF] = 0x0;
		//Iterate over 8 columns and n rows of sprite
		for(int row = 0; row < n; ++row) {
			byte spriteNextByte = system.memory[this.index + row];
			
			for(int column = 0; column < Chip8.SPRITE_WIDTH; ++column) {
				//Isolate the next bit in the next byte of the sprite
				byte spritePixel = (byte) (spriteNextByte & (0x80 >>> column) >>> Chip8.SPRITE_WIDTH - column); 
				
				//Get whether the isolated sprite pixel bit is on, and whether the screen pixel is already on.
				boolean isSpritePixelOn = spritePixel == 0x1;
				boolean isScreenPixelOn = system.displayMemory[xStartPos + column][yStartPos + row];
				
				//Set screen pixel to be on if sprite pixel is on XOR screen pixel was already on
				system.displayMemory[xStartPos + column][yStartPos + row] = isSpritePixelOn ^ isScreenPixelOn;
				
				//Set whether a sprite collision occurred
				if(isSpritePixelOn && isScreenPixelOn)
					registers[0xF] = 0x1;
			}//end for
			
		}//end for
		
	}//end method drw_Dxyn
	
	/**SKP Vx:<br>
	 * Skips the next instruction if the keypad key with the value in register Vx is being pressed.
	 * @param regX The index of register Vx
	 */
	public void skp_Ex9E(int regX) {
		if(system.queryKeyboard(this.registers[regX]))
			iteratePC();
	}//end method skp_Ex9E
	
	/**SKNP Vx:<br>
	 * Skips the next instruction if the keypad key with the value in register Vx is not being pressed.
	 * @param regX The index of register Vx
	 */
	public void sknp_ExA1(int regX) {
		if(!system.queryKeyboard(this.registers[regX]))
			iteratePC();
	}//end method sknp_ExA1
	
	/**LD Vx, DT:<br>
	 * Loads the current value of the delay timer into register Vx.
	 * @param regX The index of register Vx.
	 */
	public void ld_Fx07(int regX) {
		this.registers[regX] = this.delayTimer;
	}//end method ld_Fx07
	
	/**LD Vx, K:<br>
	 * Waits for a keypad key to be pressed. Once a key is pressed, loads the byte value of the first pressed key into register Vx.
	 * @param regX The index of register Vx
	 */
	public void ld_Fx0A(int regX) {
		byte[] keysPressed = system.queryKeyboard();
		
		if(keysPressed.length == 0)
			repeatPC();
		else
			this.registers[regX] = keysPressed[0];
	}//end method ld_Fx0A
	
	/**LD DT, Vx:<br>
	 * Loads the current value of register Vx into the delay timer.
	 * @param regX The index of register Vx
	 */
	public void ld_Fx15(int regX) {
		this.delayTimer = this.registers[regX];
	}//end method ld_Fx15
	
	/**LD ST, Vx:<br>
	 * Loads the current value of register Vx into the sound timer.
	 * @param regX The index of register Vx
	 */
	public void ld_Fx18(int regX) {
		this.soundTimer = this.registers[regX];
	}//end method ld_Fx18
	
	/**ADD I, Vx:<br>
	 * Adds the value in register Vx to that in the index register.
	 * @param regX The index of register Vx
	 */
	public void add_Fx1E(int regX) {
		this.index += this.registers[regX];
	}//end method add_Fx1E
	
	/**LD F, Vx:<br>
	 * Sets the index register to the address of the sprite for the digit held in register Vx.
	 * @param regX The index of register Vx
	 */
	public void ld_Fx29(int regX) {
		this.index = (short) (Chip8.FONT_START_ADDRESS + (Chip8.FONT_WIDTH * this.registers[regX]));
	}//end method ld_Fx29
	
	/**LD B, Vx:<br>
	 * Stores the BCD representation of the value in register Vx into memory, beginning at the address stored in the index register.
	 * @param regX The index of register Vx
	 */
	public void ld_Fx33(int regX) {
		byte decimalValue = this.registers[regX];
		for(int i = 2; i >= 0; ++i) {
			system.memory[this.index + i] = (byte) (decimalValue % 10);
			decimalValue /= 10;
		}//end for
	}//end method ld_Fx33
	
	/**LD [I], Vx:<br>
	 * Loads the values in registers V0 through Vx into memory starting at the address stored in the index register.
	 * @param regX The index of register Vx
	 */
	public void ld_Fx55(int regX) {
		for(int i = 0; i <= regX; ++i)
			system.memory[this.index + i] = this.registers[i];
	}//end method ld_Fx55
	
	/**LD Vx, [I]:<br>
	 * Loads the values in memory starting at the address stored in the index register into registers V0 through Vx.
	 * @param regX The index of register Vx
	 */
	public void ld_Fx65(int regX) {
		for(int i = 0; i <= regX; ++i)
			this.registers[i] = system.memory[this.index + i];
	}//end method ld_Fx65
	
}//end class Processor
