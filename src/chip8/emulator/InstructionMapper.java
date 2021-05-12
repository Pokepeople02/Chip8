package chip8.emulator;

import java.util.HashMap;

/**Provides a map of 16-bit CHIP-8 opcodes to their equivalent emulated CHIP-8 instructions based on unique bits in the opcode.
 * @author Douglas T. | GitHub: Pokepeople02
 */
public class InstructionMapper {

	/** The emulated CHIP-8 system associated with this Instruction Table */
	private final Chip8 system;
	
	/** The current opcode being decoded */
	private short opcode;
	
	/** The main mapping of instructions to the most-significant nibble of their opcodes*/
	private final HashMap<Byte, Instruction> mainMap = new HashMap<Byte, Instruction>(0x10);
	
	/** The sub-mapping of $0xxx instructions to the unique least-significant nibble of their opcodes*/
	private final HashMap<Byte, Instruction> map0 = new HashMap<Byte, Instruction>(0x2);
	
	/** The sub-mapping of $8xxx Instructions to the unique least-significant nibble of their opcodes*/
	private final HashMap<Byte, Instruction> map8 = new HashMap<Byte, Instruction>(0x9);
	
	/** The sub-mapping of $Exxx Instructions to the unique least-significant byte of their opcodes*/
	private final HashMap<Byte, Instruction> mapE = new HashMap<Byte, Instruction>(0x2);
	
	/** The sub-mapping of $Fxxx Instructions to the unique least-significant byte of their opcodes*/
	private final HashMap<Byte, Instruction> mapF = new HashMap<Byte, Instruction>();
	
	/** The dummy NOP instruction for handling invalid opcodes */
	private final Instruction dummy;
	
	/**Creates a new instruction mapper for decoding instructions to be run by the provided emulated CHIP-8 system.
	 * @param system The emulated CHIP-8 which will carry out all executed decoded instructions
	 */
	public InstructionMapper(Chip8 system) {
		this.system = system;
		
		constructMainMap();
		constructMap0();
		constructMap8();
		constructMapE();
		constructMapF();
		
		this.dummy = system::nop_dummy;
	}//end constructor method

	/**Constructs the main mapping of instructions to their opcode's most-significant nibble.*/
	private void constructMainMap() {
		this.mainMap.put( (byte) 0x0, () -> this.map0.getOrDefault((byte) (this.opcode & 0x00FF), this.dummy).execute() );	//Map $0xxx instructions
		this.mainMap.put( (byte) 0x1, this.system::jp_1nnn);												//Map $1xxx instructions
		this.mainMap.put( (byte) 0x2, this.system::call_2nnn );												//Map $2xxx instructions
		this.mainMap.put( (byte) 0x3, this.system::se_3xkk );												//Map $3xxx instructions
		this.mainMap.put( (byte) 0x4, this.system::sne_4xkk );												//Map $4xxx instructions
		this.mainMap.put( (byte) 0x5, this.system::se_5xy0 );												//Map $5xx0 instructions
		this.mainMap.put( (byte) 0x6, this.system::ld_6xkk );												//Map $6xxx instructions
		this.mainMap.put( (byte) 0x7, this.system::add_7xkk );												//Map $7xxx instructions
		this.mainMap.put( (byte) 0x8, () -> this.map8.getOrDefault((byte) (this.opcode & 0x000F), this.dummy).execute() );	//Map $8xxx instructions
		this.mainMap.put( (byte) 0x9, this.system::sne_9xy0 );												//Map $9xx0 instructions
		this.mainMap.put( (byte) 0xA, this.system::ld_Annn );												//Map $Axxx instructions
		this.mainMap.put( (byte) 0xB, this.system::jp_Bnnn );												//Map $Bxxx instructions
		this.mainMap.put( (byte) 0xC, this.system::rnd_Cxkk );												//Map $Cxxx instructions
		this.mainMap.put( (byte) 0xD, this.system::drw_Dxyn );												//Map $Dxxx instructions
		this.mainMap.put( (byte) 0xE, () -> this.mapE.getOrDefault((byte) (this.opcode & 0x00FF), this.dummy).execute() );	//Map $Exxx instructions
		this.mainMap.put( (byte) 0xF, () -> this.mapF.getOrDefault((byte) (this.opcode & 0x00FF), this.dummy).execute() );	//Map $Fxxx instructions
	}//end method constructMainMap
	
	/**Constructs the sub-mapping of $00Ex instructions to their opcode's unique least-significant byte.*/
	private void constructMap0() {
		this.map0.put( (byte) 0xE0, this.system::cls_00E0 );	//Map $00E0 instructions
		this.map0.put( (byte) 0xEE, this.system::ret_00EE );	//Map $00EE instructions
	}//end method constructTable0
	
	/**Constructs the sub-mapping of $8xxx instructions to their opcode's unique least-significant nibble.*/
	private void constructMap8() {
		this.map8.put( (byte) 0x0, this.system::ld_8xy0 );	//Map $8xx0 instructions
		this.map8.put( (byte) 0x1, this.system::or_8xy1 );	//Map $8xx1 instructions
		this.map8.put( (byte) 0x2, this.system::and_8xy2 );	//Map $8xx2 instructions
		this.map8.put( (byte) 0x3, this.system::xor_8xy3 );	//Map $8xx3 instructions
		this.map8.put( (byte) 0x4, this.system::add_8xy4 );	//Map $8xx4 instructions
		this.map8.put( (byte) 0x5, this.system::sub_8xy5 );	//Map $8xx5 instructions
		this.map8.put( (byte) 0x6, this.system::shr_8xy6 );	//Map $8xx6 instructions
		this.map8.put( (byte) 0x7, this.system::subn_8xy7 );//Map $8xx7 instructions
		this.map8.put( (byte) 0xE, this.system::shl_8xyE );	//Map $8xxE instructions
	}//end method constructTable8

	/**Constructs the sub-mapping of $Exxx instructions to their opcode's unique least-significant byte.*/
	private void constructMapE() {
		this.mapE.put( (byte) 0xA1, this.system::sknp_ExA1 );	//Map $ExA1 instructions
		this.mapE.put( (byte) 0x9E, this.system::skp_Ex9E );	//Map $Ex9E instructions
	}//end method constructTableE
	
	/**Constructs the sub-mapping of $Fxxx instructions to their opcode's unique least-significant byte.*/
	private void constructMapF() {
		this.mapF.put( (byte) 0x07, this.system::ld_Fx07 );	//Map $Fx07 instructions
		this.mapF.put( (byte) 0x0A, this.system::ld_Fx0A );	//Map $Fx0A instructions
		this.mapF.put( (byte) 0x15, this.system::ld_Fx15 );	//Map $Fx15 instructions
		this.mapF.put( (byte) 0x18, this.system::ld_Fx18 );	//Map $Fx18 instructions
		this.mapF.put( (byte) 0x1E, this.system::add_Fx1E );//Map $Fx1E instructions
		this.mapF.put( (byte) 0x29, this.system::ld_Fx29 );	//Map $Fx29 instructions
		this.mapF.put( (byte) 0x33, this.system::ld_Fx33 );	//Map $Fx33 instructions
		this.mapF.put( (byte) 0x55, this.system::ld_Fx55 );	//Map $Fx55 instructions
		this.mapF.put( (byte) 0x65, this.system::ld_Fx65 );	//Map $Fx65 instructions
	}//end method constructMapF

	/**Gets the appropriate instruction based on the provided opcode.
	 * @param opcode The provided opcode
	 * @return The Instruction for the provided short opcode. 
	 */
	public Instruction getInstruction(short opcode) {
		this.opcode = opcode;
		
		return this.mainMap.getOrDefault( (byte) ((this.opcode & 0xF000) >>> 12), this.dummy );
	}//end method getInstruction
	
}//end class InstructionMapper
