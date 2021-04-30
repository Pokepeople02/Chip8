package chip8;

import java.util.Random;

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
	
	/** 8-bit sound timer register */
	private byte soundTimer;
	
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
		this.system = system;
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
	
	public void cls_00E0() {
		
	}

}//end class Processor
