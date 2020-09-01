package sisop.oliveiracley.processor

class Core {

	enum OPCODE {
			 DATA, 	// Has data 
			 ___,  	// Is null
	/*==============================================================================================================*/	
	/* No. |  OPCODE ||	    Syntax	   |  Micro-operation 		  | R1	|   R2	| P | Description					*/
	/*=====[ J - TYPE  INSTRUCTIONS ]===============================================================================*/
	/* 01 */ JMP,	 //     JMP k 	   | PC ← k					  |		|		| K	| Direct jump					//
	/* 02 */ JMPI,	 //    JMPI Rs     | PC ← Rs				  |	Rs	|		|   | Register jump					//
	/* 03 */ JMPIG,	 //  JMPIG Rs, Rc  | Rc > 0 ? PC ← Rs : PC++  |	Rs	|	Rc	|   | Jump if greater				//
	/* 04 */ JMPIL,	 //  JMPIL Rs, Rc  | Rc < 0 ? PC ← Rs : PC++  |	Rs	|	Rc	|   | Jump if less					//
	/* 05 */ JMPIE,	 //  JMPIE Rs, Rc  | Rc = 0 ? PC ← Rs : PC++  |	Rs	|	Rc	|   | Jump if equal	 				//
	/* 06 */ JMPIM,	 // JMPIM  [A], Rc | PC ← [A]				  |		|		| A | Memory jump					//
	/* 07 */ JMPIGM, // JMPIGM [A], Rc | Rc > 0 ? PC ← [A] : PC++ |		|	Rc	| A | Memory jump if greater		//
	/* 08 */ JMPILM, // JMPILM [A], Rc | Rc < 0 ? PC ← [A] : PC++ |		|	Rc	| A | Memory jump if less			//
	/* 09 */ JMPIEM, // JMPIEM [A], Rc | Rc = 0 ? PC ← [A] : PC++ |		|	Rc	| A | Memory jump if equal			//
	/*=====[ I - TYPE  INSTRUCTIONS ]===============================================================================*/
	/* 10 */ ADDI,	 //	 ADDI Rd, k	   | Rd   ← Rd + k			  |	Rd	|		| K | Immediate addition			//
	/* 11 */ SUBI,	 //	 SUBI Rd, k    | Rd   ← Rd - k			  |	Rd	|		| K | Immediate subtraction			//
	/* 12 */ LDI,	 //	  LDI Rd, k	   | Rd   ← k				  |	Rd	|		| K | Load immediate 				//
	/* 13 */ LDD,	 //	 LDD  Rd,[A]   | Rd   ← [A]				  |	Rd	|		| A | Load direct from data memory	//
	/* 14 */ STD,	 //	 STD [A],Rs	   | [A]  ← Rs				  |	Rs	|		| A | Store direct to data memory	//
	/*=====[ R2 - TYPE INSTRUCTIONS ]===============================================================================*/
	/* 15 */ ADD,	 //	  ADD Rd, Rs   | Rd   ← Rd + Rs			  |	Rd	|	Rs	|	| Addition						//
	/* 16 */ SUB,	 //	  SUB Rd, Rs   | Rd   ← Rd - Rs			  |	Rd	|	Rs	|	| Subtraction					//
	/* 17 */ MULT,	 //	 MULT Rd,[Rs]  | Rd   ← Rd * Rs			  |	Rd	|	Rs	|	| Multiplication 				//
	/* 18 */ LDX,	 //	 LDX  Rd,[Rs]  | Rd   ← [Rs]			  |	Rd	|	Rs	|	| Indirect load from memory		//
	/* 19 */ STX,	 //	 STX [Rd],Rs   | [Rd] ← Rs				  |	Rd	|	Rs	|	| Indirect storage to memory	//
	/*=====[ R1 - TYPE INSTRUCTIONS ]===============================================================================*/
	/* 20 */ SWAP,	 //	SWAP Ra, Rb    | T ← Ra; Ra ← Rb; Rb ← T  |		|		|	| SWAP regs						//
	/* 21 */ STOP	 // 			   |			 			  |		|		|	| HALT							//
	/*==============================================================================================================*/
	}

	private static Memory 	memory
	private static CPU 		cpu

	public Core (CPU _cpu){
		memory 	= Memory.getInstance()
		cpu 	= _cpu
	}

	// @EXECUTE
	/*=====[ J - TYPE  INSTRUCTIONS ]===============================================================================*/
	
	// P := k
	// PC ← k
	def JMP (def word){
		cpu.setPC(word.p)
	}
	
	// R1 := Rs
	// PC ← Rs
	def JMPI (def word){
		cpu.setPC(getRegister(word.r1))
	}
	
	// R1 := Rs
	// R2 := Rc
	// Rc > 0 ? PC ← Rs : PC++
	def JMPIG (def word){
		if(word.r2 > 0)
			cpu.setPC(word.r1)
		else
			cpu.increment()
	}
	
	// R1 := Rs
	// R2 := Rc
	// Rc < 0 ? PC ← Rs : PC++
	def JMPIL (def word){
		if(word.r2 < 0)
			cpu.setPC(word.r1)
		else
			cpu.increment()

	}
	
	// R1 := Rs
	// R2 := Rc
	// Rc = 0 ? PC ← Rs : PC++
	def JMPIE (def word){
		if(word.r2 == 0)
			cpu.setPC(word.r1)
		else
			cpu.increment()
	}
	
	// P := A
	// PC ← [A]
	def JMPIM (def word){
		def mem = memory.get(word.p)
		if(mem.OpCode == OPCODE.___)
			cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
		else
			cpu.setPC(mem.p)
	}
	

	// P  := A
	// R2 := Rc
	// Rc  > 0 ? PC ← [A] : PC++
	def JMPIGM (def word){
		if(word.r2 > 0){
			def mem = memory.get(word.p)
			if(mem.OpCode == OPCODE.___)
				cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
			else
				cpu.setPC(mem.p)
		} else {
	    	cpu.increment()
		}
	}
	
	// P  := A
	// R2 := Rc
	// Rc  < 0 ? PC ← [A] : PC++
	def JMPILM (def word){
		if(word.r2 < 0){
			def mem = memory.get(word.p)
			if(mem.OpCode == OPCODE.___)
				cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
			else
				cpu.setPC(mem.p)
		} else {
	    	cpu.increment()
		}
	}
	
	// P  := A
	// R2 := Rc
	// Rc  = 0 ? PC ← [A] : PC++
	def JMPIEM (def word){
		if(word.r2 == 0){
			def mem = memory.get(word.p)
			if(mem.OpCode == OPCODE.___)
				cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
			else
				cpu.setPC(mem.p)
		} else {
	    	cpu.increment()
		}
	}


	/*=====[ I - TYPE  INSTRUCTIONS ]===============================================================================*/
	
	// P  := K
	// R1 := rd
	// Rd ← Rd + k
	def ADDI (def word){
		cpu.setRegister(word.r1, (word.r1 - word.p))
	    cpu.increment()
	}
	
	// P  := K
	// R1 := rd
	// Rd ← Rd - k
	def SUBI (def word){
		cpu.setRegister(word.r1, (word.r1 - word.p))
	    cpu.increment()
	}
	
	// P  := K
	// R1 := rd
	// Rd ← k
	def LDI (def word){
		cpu.setRegister(word.r1, word.p)
	    cpu.increment()
	}
	
	// P  := A
	// R1 := Rd
	// Rd ← [A]
	def LDD (def word){
		def mem = memory.get(word.p)
		if(mem.OpCode == OPCODE.___)
			cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
		else
			cpu.setRegister(word.r1, mem.p)		
	    cpu.increment()
	}
	
	// P  := A
	// R1 := Rs
	// [A] ← Rs
	def STD (def word){
	    memory.get(word.p).OpCode = OpCode.DATA;
	    memory.get(word.p).p = cpu.getRegister(word.r1)
	    cpu.increment()
	}


	/*=====[ R2 - TYPE INSTRUCTIONS ]===============================================================================*/
	
	// R1 := Rd
	// R2 := Rs
	// Rd ← Rd + Rs
	def ADD (def word){
		cpu.setRegister(word.r1, (word.r1 + word.r2))
	    cpu.increment()
	}
	
	// R1 := Rd
	// R2 := Rs
	// Rd ← Rd - Rs
	def SUB (def word){
		cpu.setRegister(word.r1, (word.r1 - word.r2))
	    cpu.increment()
	}
	
	// R1 := Rd
	// R2 := Rs
	// Rd ← Rd * Rs
	def MULT (def word){
		cpu.setRegister(word.r1, (word.r1 * word.r2))
	    cpu.increment()
	}
	
	// R1 := Rd
	// R2 := Rs
	// Rd ← [Rs]
	def LDX (def word){
		def mem = memory.get(word.r2)
		if(mem.OpCode == OPCODE.___)
			cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
		else
			cpu.setRegister(word.r1, mem.p)
	    cpu.increment()
	}
	
	// R1 := Rd
	// R2 := Rs
	// [Rd] ← Rs
	def STX (def word){
		def mem = memory.get(word.r1)
		if(mem.OpCode == OPCODE.___)
			cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
		else
			cpu.setRegister(mem.p, word.r2)
	    cpu.increment()
	}


	/*=====[ R1 - TYPE INSTRUCTIONS ]===============================================================================*/
	
	// T  ← Ra
	// Ra ← Rb
	// Rb ← T
	def SWAP (def word){
		cpu.setRegister(0, cpu.getRegister(1))
		cpu.setRegister(1, cpu.getRegister(2))
		cpu.setRegister(2, cpu.getRegister(0))
	}
	
	// HALT
	def STOP (def word){
		cpu.setInterruption(CPU.Interrupts.STOP)
	}
}