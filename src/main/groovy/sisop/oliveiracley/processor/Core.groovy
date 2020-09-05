package sisop.oliveiracley.processor

class Core {

	enum OPCODE {
	//=========================================================================================================================// 	
	// No. |  OPCODE   | Params |	    Syntax	   |  Micro-operation 		  | R1	|   R2	| P | Description			 	   //
	//=====[ J - TYPE  INSTRUCTIONS ]==========================================================================================//
	/* 01 */ JMP 		(  3  ), //     JMP k 	   | PC ← k					  |		|		| K	| Direct jump				   //
	/* 02 */ JMPI 		(  1  ), //    JMPI Rs     | PC ← Rs				  |	Rs	|		|   | Register jump				   //
	/* 03 */ JMPIG 		( 1,2 ), //  JMPIG Rs, Rc  | Rc > 0 ? PC ← Rs : PC++  |	Rs	|	Rc	|   | Jump if greater			   //
	/* 04 */ JMPIL 		( 1,2 ), //  JMPIL Rs, Rc  | Rc < 0 ? PC ← Rs : PC++  |	Rs	|	Rc	|   | Jump if less				   //
	/* 05 */ JMPIE 		( 1,2 ), //  JMPIE Rs, Rc  | Rc = 0 ? PC ← Rs : PC++  |	Rs	|	Rc	|   | Jump if equal	 			   //
	/* 06 */ JMPIM 		(  3  ), // JMPIM  [A]	   | PC ← [A]				  |		|		| A | Memory jump				   //
	/* 07 */ JMPIGM 	( 3,2 ), // JMPIGM [A], Rc | Rc > 0 ? PC ← [A] : PC++ |		|	Rc	| A | Memory jump if greater	   //
	/* 08 */ JMPILM 	( 3,2 ), // JMPILM [A], Rc | Rc < 0 ? PC ← [A] : PC++ |		|	Rc	| A | Memory jump if less		   //
	/* 09 */ JMPIEM 	( 3,2 ), // JMPIEM [A], Rc | Rc = 0 ? PC ← [A] : PC++ |		|	Rc	| A | Memory jump if equal	       //
	//=====[ I - TYPE  INSTRUCTIONS ]==========================================================================================//
	/* 10 */ ADDI	 	( 1,3 ), //	 ADDI Rd, k	   | Rd   ← Rd + k			  |	Rd	|		| K | Immediate addition		   //
	/* 11 */ SUBI	 	( 1,3 ), //	 SUBI Rd, k    | Rd   ← Rd - k			  |	Rd	|		| K | Immediate subtraction		   //
	/* 12 */ LDI	 	( 1,3 ), //	 LDI  Rd, k	   | Rd   ← k				  |	Rd	|		| K | Load immediate 			   //
	/* 13 */ LDD	 	( 1,3 ), //	 LDD  Rd,[A]   | Rd   ← [A]				  |	Rd	|		| A | Load direct from data memory //
	/* 14 */ STD	 	( 3,1 ), //	 STD [A],Rs	   | [A]  ← Rs				  |	Rs	|		| A | Store direct to data memory  //
	//=====[ R2 - TYPE INSTRUCTIONS ]==========================================================================================//
	/* 15 */ ADD	 	( 1,2 ), //	  ADD Rd, Rs   | Rd   ← Rd + Rs			  |	Rd	|	Rs	|	| Addition					   //
	/* 16 */ SUB	 	( 1,2 ), //	  SUB Rd, Rs   | Rd   ← Rd - Rs			  |	Rd	|	Rs	|	| Subtraction				   //
	/* 17 */ MULT	 	( 1,2 ), //	 MULT Rd,[Rs]  | Rd   ← Rd * Rs			  |	Rd	|	Rs	|	| Multiplication 			   //
	/* 18 */ LDX	 	( 1,2 ), //	 LDX  Rd,[Rs]  | Rd   ← [Rs]			  |	Rd	|	Rs	|	| Indirect load from memory	   //
	/* 19 */ STX	 	( 1,2 ), //	 STX [Rd],Rs   | [Rd] ← Rs				  |	Rd	|	Rs	|	| Indirect storage to memory   //
	//=====[ R1 - TYPE INSTRUCTIONS ]==========================================================================================//
	/* 20 */ SWAP	 	( 1,2 ), //	SWAP Ra, Rb    | T ← Ra; Ra ← Rb; Rb ← T  |	Ra	|	Rb	|	| SWAP registers		       //
	/* 21 */ STOP	 	(  0  ), // 			   |			 			  |		|		|	| STOP (HALT)				   //
	//=====[ Control PARAMETERS ]==============================================================================================//
	/* -- */ CONF	 	(1,2,3), // 			   |						  |		|		|	| Configure CPU output 	       //
	/* -- */ DATA 	 	(  3  ), // 			   |						  |		|		|	| Memory addess has data 	   //
	/* -- */ ___		(  0  ); // 			   |						  |		|		|	| Memory addess is empty	   //
	//=========================================================================================================================//
		private final int[] value
		OPCODE(int[] value) {	this.value = value	}
	}

	private static Memory 	memory
	private static CPU 		cpu

	public Core (CPU _cpu, Memory _memory){
		memory 	= _memory
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
	    memory.get(word.p).OpCode = Core.OPCODE.DATA;
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
		cpu.setRegister(0, cpu.getRegister(word.r1))
		cpu.setRegister(word.r1, cpu.getRegister(word.r2))
		cpu.setRegister(word.r2, cpu.getRegister(0))
	}
	
	// HALT
	def STOP (def word){
		cpu.setInterruption(CPU.Interrupts.STOP)
	}


	// Mine option
	// Configure CPU output
	def CONF (def word){
		Range memoryOutput
		if(word.r1 < 0 || word.r2 < 0)
			memoryOutput = null
		else
			memoryOutput = word.r1..word.r2

		cpu.setOutputConfiguration(word.p as boolean, [memoryOutput] as Range[])
		cpu.increment()
	}
	/*==============================================================================================================*/

	// HALT
	def DATA (def word){
		cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
	}

	// HALT
	def ___ (def word){
		cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
	}
}