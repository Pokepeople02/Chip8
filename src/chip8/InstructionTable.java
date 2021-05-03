package chip8;

import java.util.Hashtable;

public class InstructionTable {

	private short opcode;
	private Hashtable<Integer, InstructionGetter> mainTable;
	private Hashtable<Integer, InstructionGetter> table0;
	private Hashtable<Integer, InstructionGetter> table8;
	private Hashtable<Integer, InstructionGetter> tableE;
	private Hashtable<Integer, InstructionGetter> tableF;
	
	/**Creates a new instruction table for decoding instructions to be run by the given processor.
	 * Initializes the main instruction table and all sub-tables.
	 * @param cpu The processor which will carry out all executed decoded instructions
	 */
	public InstructionTable(Processor cpu) {
		constructMainTable(cpu);
		constructTable0(cpu);
		constructTable8(cpu);
		constructTableE(cpu);
		constructTableF(cpu);
	}//end constructor method

	/**Creates the main instruction table of instruction getters
	 * @param cpu The processor which will carry out all executed decoded instructions.
	 */
	private void constructMainTable(Processor cpu) {
		this.mainTable = new Hashtable<Integer, InstructionGetter>();
		
		//Add $0xxx instructions
		InstructionGetter getter0 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return getTable0Instruction();
			}//end method getInstruction
		};
		mainTable.put(0x0, getter0);
		
		//Add $1xxx instructions
		InstructionGetter getter1 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						cpu.jp_1nnn((short) (opcode & 0x0FFF));
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x1, getter1);
		
		//Add $2xxx instructions
		InstructionGetter getter2 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						cpu.call_2nnn((short) (opcode & 0x0FFF));
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x2, getter2);
		
		//Add $3xxx instructions
		InstructionGetter getter3 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						byte kk = (byte) (opcode & 0x00FF);
						cpu.se_3xkk(x, kk);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x3, getter3);
		
		//Add $4xxx instructions
		InstructionGetter getter4 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						byte kk = (byte) (opcode & 0x00FF);
						cpu.sne_4xkk(x, kk);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x4, getter4);
		
		//Add $5xxx instructions
		InstructionGetter getter5 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						cpu.se_5xy0(x, y);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x5, getter5);
		
		//Add $6xxx instructions
		InstructionGetter getter6 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						byte kk = (byte) (opcode & 0x00FF);
						cpu.ld_6xkk(x, kk);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x6, getter6);
		
		//Add $7xxx instructions
		InstructionGetter getter7 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						byte kk = (byte) (opcode & 0x00FF);
						cpu.add_7xkk(x, kk);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x7, getter7);
		
		//Add $8xxx instructions
		InstructionGetter getter8 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return getTable8Instruction();
			}//end method getInstruction
		};
		mainTable.put(0x8, getter8);
		
		//Add $9xxx instructions
		InstructionGetter getter9 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						cpu.sne_9xy0(x, y);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x9, getter9);
		
		//Add $Axxx instructions
		InstructionGetter getterA = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						cpu.ld_Annn((short) (opcode & 0x0FFF));
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0xA, getterA);
		
		//Add $Bxxx instructions
		InstructionGetter getterB = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						cpu.jp_Bnnn((short) (opcode & 0x0FFF));
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0xB, getterB);
		
		//Add $Cxxx instructions
		InstructionGetter getterC = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						byte kk = (byte) (opcode & 0x00FF);
						cpu.rnd_Cxkk(x, kk);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0xC, getterC);
		
		//Add $Dxxx instructions
		InstructionGetter getterD = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						byte n = (byte) (opcode & 0x000F);
						cpu.drw_Dxyn(x, y, n);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0xD, getterD);
		
		//Add $Exxx instructions
		InstructionGetter getterE = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return getTableEInstruction();
			}//end method getInstruction
		};
		mainTable.put(0xE, getterE);
		
		//Add $Fxxx instructions
		InstructionGetter getterF = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return getTableFInstruction();
			}//end method getInstruction
		};
		mainTable.put(0xF, getterF);
		
	}//end method constructMainTable
	
	/**Constructs the 0xxx instructions sub-table.
	 * @param cpu The processor which will carry out all executed decoded instructions
	 */
	private void constructTable0(Processor cpu) {
		this.table0 = new Hashtable<Integer, InstructionGetter>();
		
		//Add $0xx0 instructions
		InstructionGetter getter0_0 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						cpu.cls_00E0();
					}//end method execute
				};
			}//end method getInstruction
		};
		table0.put(0x0, getter0_0);
		
		//Add $0xxE instructions
		InstructionGetter getter0_E = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						cpu.ret_00EE();
					}//end method execute
				};
			}//end method getInstruction
		};
		table0.put(0xE, getter0_E);
		
	}//end method constructTable0
	
	/**Constructs the $8xxx instructions sub-table.
	 * @param cpu The processor which will carry out all executed decoded instructions
	 */
	private void constructTable8(Processor cpu) {
		this.table8 = new Hashtable<Integer, InstructionGetter>();
		
		//Add $8xx0 instructions
		InstructionGetter getter8_0 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						cpu.ld_8xy0(x, y);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x0, getter8_0);
		
		//Add $8xx1 instructions
		InstructionGetter getter8_1 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						cpu.or_8xy1(x, y);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x1, getter8_1);
		
		//Add $8xx2 instructions
		InstructionGetter getter8_2 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						cpu.and_8xy2(x, y);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x2, getter8_2);
		
		//Add $8xx3 instructions
		InstructionGetter getter8_3 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						cpu.xor_8xy3(x, y);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x3, getter8_3);
		
		//Add $8xx4 instructions
		InstructionGetter getter8_4 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						cpu.add_8xy4(x, y);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x4, getter8_4);
		
		//Add $8xx5 instructions
		InstructionGetter getter8_5 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						cpu.sub_8xy5(x, y);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x5, getter8_5);
		
		//Add $8xx6 instructions
		InstructionGetter getter8_6 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.shr_8xy6(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x6, getter8_6);
		
		//Add $8xx7 instructions
		InstructionGetter getter8_7 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						int y = (opcode & 0x00F0) >>> 4;
						cpu.subn_8xy7(x, y);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x7, getter8_7);
		
		//Add $8xxE instructions
		InstructionGetter getter8_E = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.shl_8xyE(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0xE, getter8_E);
		
	}//end method constructTable8

	/**Constructs the $Exxx instructions sub-table.
	 * @param cpu The processor which will carry out all executed decoded instructions
	 */
	private void constructTableE(Processor cpu) {
		this.tableE = new Hashtable<Integer, InstructionGetter>();
		
		//Add $ExA1 instructions
		InstructionGetter getterE_A1 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.sknp_ExA1(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0xA1, getterE_A1);
		
		//Add $Ex9E instructions
		InstructionGetter getterE_9E = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.skp_Ex9E(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x9E, getterE_9E);
		
	}//end method constructTableE
	
	private void constructTableF(Processor cpu) {
		this.tableF = new Hashtable<Integer, InstructionGetter>();
		
		//Add $Fx07 instructions
		InstructionGetter getterF_07 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.ld_Fx07(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x07, getterF_07);
		
		//Add $Fx0A instructions
		InstructionGetter getterF_0A = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.ld_Fx0A(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x0A, getterF_0A);
		
		//Add $Fx15 instructions
		InstructionGetter getterF_15 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.ld_Fx15(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x15, getterF_15);
		
		//Add $Fx18 instructions
		InstructionGetter getterF_18 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.ld_Fx18(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x18, getterF_18);
		
		//Add $Fx1E instructions
		InstructionGetter getterF_1E = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.add_Fx1E(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x1E, getterF_1E);
		
		//Add $Fx29 instructions
		InstructionGetter getterF_29 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.ld_Fx29(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x29, getterF_29);
		
		//Add $Fx33 instructions
		InstructionGetter getterF_33 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.ld_Fx33(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x33, getterF_33);
		
		//Add $Fx55 instructions
		InstructionGetter getterF_55 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.ld_Fx55(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x55, getterF_55);
		
		//Add $Fx65 instructions
		InstructionGetter getterF_65 = new InstructionGetter() {
			@Override
			public Instruction getInstruction() {
				return new Instruction() {
					@Override
					public void execute() {
						int x = (opcode & 0x0F00) >>> 8;
						cpu.ld_Fx65(x);
					}//end method execute
				};
			}//end method getInstruction
		};
		mainTable.put(0x65, getterF_65);
		
	}//end method constructTableF

	/**Gets the appropriate instruction based on the first digit of the provided opcode
	 * @param opcode The provided opcode
	 * @return The Instruction for the provided short opcode. 
	 */
	public Instruction getInstruction(short opcode) {
		this.opcode = opcode;
		InstructionGetter instructionGetter = this.mainTable.get((this.opcode & 0xF000) >>> 16);
		
		//If instruction gotten from table is not null, return its instruction
		if(instructionGetter != null && instructionGetter.getInstruction() != null)
			return instructionGetter.getInstruction();
		
		//If null, unknown opcode. Return dummy instruction.
		return new Instruction() {
			@Override
			public void execute() {
				System.out.println("Unknown opcode: " + String.format("0x%X", opcode));
			}//end method execute
		};
	}//end class getInstruction

	/**Gets the appropriate $0xxx instruction based on the last digit of the previously provided opcode
	 * @return The Instruction for the provided opcode, or null if no appropriate instruction exists. 
	 */
	private Instruction getTable0Instruction() {
		try {
			return this.table0.get(this.opcode & 0x000F).getInstruction();
		}//end try
		catch(NullPointerException e) {
			return null;
		}//end catch
	}//end method getTable0Instruction
	
	/**Gets the appropriate $8xxx instruction based on the last digit of the previously provided opcode
	 * @return The Instruction for the provided opcode, or null if no appropriate instruction exists. 
	 */
	private Instruction getTable8Instruction() {
		try {
			return this.table8.get(this.opcode & 0x000F).getInstruction();
		}//end try
		catch(NullPointerException e) {
			return null;
		}//end catch
	}//end method getTable8Instruction
	
	/**Gets the appropriate $Exxx instruction based on the last two digits of the previously provided opcode
	 * @return The Instruction for the provided opcode, or null if no appropriate instruction exists. 
	 */
	private Instruction getTableEInstruction() {
		try {
			return this.tableE.get(this.opcode & 0x00FF).getInstruction();
		}//end try
		catch(NullPointerException e) {
			return null;
		}//end catch
	}//end method getTableEInstruction
	
	/**Gets the appropriate $Fxxx instruction based on the last two digits of the previously provided opcode
	 * @return The Instruction for the provided opcode, or null if no appropriate instruction exists. 
	 */
	private Instruction getTableFInstruction() {
		try {
			return this.tableF.get(this.opcode & 0x00FF).getInstruction();
		}//end try
		catch(NullPointerException e) {
			return null;
		}//end catch
	}//end method getTableFInstruction

}//end class InstructionTable
