package sisop.oliveiracley.processor

import sisop.oliveiracley.io.HardDrive
import sisop.oliveiracley.ui.WebServer
import sisop.oliveiracley.ui.ANSI

class CPU {

	enum Interrupts {
		NoInterrupt, InvalidAddress, InvalidInstruction, STOP;
	}
	//Instance variables ------------------------------------------------------------
		private static CPU instance 		// Singleton instance

		private def Word 	ir 				// Instruction register
		private Interrupts 	interrupt 		// Processor state
		private int[] 		registers 		// Processor registers
		private Memory 		memory 			// RAM Memory
		private int 		base 			// Memory base access	(not use yet)
		private int 		limit 			// Memory limit access	(not use yet)
		private int 		pc 				// Program Counter
		private Core 		core_1			// CPU Core 1

		private boolean 	registersOutput	// Enable registers output
		private Range[]		memoryOutput	// Configuration to output memory dump
	//--------------------------------------------------------------------------------


	//-Singleton Class Configuration------------

	// Start the VM CPU
	def static run(){
		getInstance()
	}

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
		core_1 = new Core(this, memory)
		pc = 0

		// Read the assembly program
		HardDrive.readFile("Assembly_01")

		// Start ui web server
		WebServer.riseServer()

		// Run the program
		execute()
	}

	//-CPU Instance Variables Access---------------------

	def increment(){ pc++ }

	def getRegister(int rs){
		if((rs < 0) || (rs > 7))
			interrupt = Interrupts.InvalidAddress
		else
			return registers[rs];
	}

	def setRegister(int rs, p){
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

	// Output --------------------------------------------------------------

	def setOutputConfiguration(
			boolean _registersOutput,
			Range[] _memoryOutput
		){

		registersOutput  	= _registersOutput
		
		if(!memoryOutput)
			memoryOutput 	= _memoryOutput
		else
			memoryOutput 	+= _memoryOutput
	}

	def registerDump(){
		println "\n\t       ${ANSI.CYAN_BACKGROUND} REGISTERS DUMP ${ANSI.RESET}"
		registers.eachWithIndex{ reg, i ->
			print "[R${i}] = ${reg}\t"
			if(i == 2 || i == 5)
				println ""
		}
	}

	// Runtime Auxiliar Methods --------------------
	private boolean legal(int e) {
		if ((e < base) || (e > limit)) {
			interrupt = Interrupts.InvalidAddress
			return false
		}
		return true
	}
	// ---------------------------------------------

	def execute(){
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

		if(interrupt != Interrupts.STOP)
			println("${ANSI.RED_BOLD} Program interrupted with: ${ANSI.RED_UNDERLINE} ${interrupt} ${ANSI.RESET}")		
		else {
			if(registersOutput)
				registerDump()
			if(memoryOutput)
				memory.dump(memoryOutput)
		}
	}


	// HARD LOAD PROGRAM
	def hardLoad(){
		def index = 0;
		memory.get(index).OpCode	= Core.OPCODE.CONF
		memory.get(index).r1		= 0
		memory.get(index).r2		= 5
		memory.get(index).p 		= 0
		index++

		memory.get(index).OpCode	= Core.OPCODE.CONF
		memory.get(index).r1		= 10
		memory.get(index).r2		= 13
		memory.get(index).p 		= 1
		index++

		memory.get(index).OpCode	= Core.OPCODE.STX
		memory.get(index).r1		= 0
		memory.get(index).r2		= 5
		memory.get(index).p 		= 0
		index++

		memory.get(index).OpCode	= Core.OPCODE.CONF
		memory.get(index).r1		= 17
		memory.get(index).r2		= 20
		memory.get(index).p 		= 1
		index++

		memory.get(index).OpCode	= Core.OPCODE.STOP
		memory.get(index).r1		= 0
		memory.get(index).r2		= 0
		memory.get(index).p 		= 0
		index++
	
	}
}