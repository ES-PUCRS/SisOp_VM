package sisop.oliveiracley.processor

import sisop.oliveiracley.io.HardDrive
import sisop.oliveiracley.ui.ANSI

class CPU {

	enum Interrupts {
		NoInterrupt, InvalidAddress, InvalidInstruction, STOP;
	}
	//Instance variables ------------------------------------------------
		private static CPU instance 	// Singleton instance

		private def Word ir 			// Instruction register
		private Interrupts interrupt 	// Processor state
		private int[] registers 		// Processor registers
		private Memory memory 			// RAM Memory
		private int base 				// Memory base access	(not use yet)
		private int limit 				// Memory limit access	(not use yet)
		private int pc 					// Program Counter
		private Core core_1				// CPU Core 1
	//-------------------------------------------------------------------

	// Singleton access 
	def static getInstance(){
		if(!instance)
			instance = new CPU()
		return instance
	}

	// Singleton constructor
	private CPU(){
		interrupt = Interrupts.NoInterrupt
		
		memory = Memory.getInstance()
		limit = Memory.memorySize - 1
		base = 0;

		registers = new int[8]
		core_1 = new Core(this)
		pc = 0
	}

	def increment(){ pc++ }

	def getRegister(int rs){
		if((rs < 0) || (rs > 7))
			interrupt = Interrupts.InvalidAddress
		else
			return registers[rs];
	}

	def getPC(){
		return pc
	}

	def setRegister(int rs, p){
		// println "RS:: " + rs
		// println "PC:: " + pc
		if((rs < 0) || (rs > 7))
			interrupt = Interrupts.InvalidAddress
		else
			registers[rs] = p;
	}

	def setInterruption(Interrupts state){
		interrupt = state;
	}

	def setPC(int _pc){
		if((_pc < 0) || (_pc > (Memory.memorySize - 1)))
			interrupt = Interrupts.InvalidAddress
		else
			pc = _pc
	}


	private boolean legal(int e) {
		if ((e < base) || (e > limit)) {
			interrupt = Interrupts.InvalidAddress
			return false
		}
		return true
	}

	
	def run(){

		hardLoad()
		
		while(interrupt == Interrupts.NoInterrupt) {
			
			// SHIELD
			if(legal(pc)){
				
				// @FETCH	
				ir = memory.get(pc)

				// @DECORE -> @EXECUTE
				core_1."${ir.OpCode}"(ir)
	
			}
			// @REPEAT
		}

		// if(interrupt != Interrupts.STOP)
		// 	println("${ANSI.RED_BOLD} Program interrupted with: ${ANSI.RED_UNDERLINE} ${interrupt} ${ANSI.RESET}")		
		// else
			// registerDump()
			// memory.dump(0..5)

	}

	def registerDump(){
		println "\n\t\t${ANSI.CYAN_BACKGROUND} MEMORY DUMP ${ANSI.RESET}"
		registers.eachWithIndex{ reg, i ->
			print "[R${i}] = ${reg}\t"
			if(i == 2 || i == 5)
				println ""
		}
	}



	// HARD LOAD PROGRAM
	def hardLoad(){
		memory.get(0).OpCode	= Core.OPCODE.STX
		memory.get(0).r1		= 0
		memory.get(0).r2		= 5
		memory.get(0).p 		= 0
	
		memory.get(1).OpCode	= Core.OPCODE.STOP
		memory.get(1).r1		= 0
		memory.get(1).r2		= 0
		memory.get(1).p 		= 0
	
	}



	def readFile (String _file) {
		try {
			def file = this.getClass().getResource("/${_file}").text
			println file

		}catch(Exception e) {
			println "${ANSI.RED_BOLD} Error reading file: ${ANSI.RED_UNDERLINE} ${_file} ${ANSI.RESET}"
			println "${ANSI.WHITE}" 			+
					"${ANSI.RED_BACKGROUND} " 	+ 
						"${e.getMessage()}"		+
					"${ANSI.RESET}"
		}

		// return file
	}

}