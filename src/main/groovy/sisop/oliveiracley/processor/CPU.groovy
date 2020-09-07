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
		private Core[] 		cores			// CPU Core 1

		private boolean 	registersOutput	// Enable registers output
		private Range[]		memoryOutput	// Configuration to output memory dump
	//--------------------------------------------------------------------------------


	//-Singleton Class Configuration------------

	// Start the VM CPU
	def static run(){
		getInstance()
	}

	// Singleton access
	def static getInstance() {
		if(!instance)
			instance = new CPU()
		return instance
	}

	// Singleton constructor
	private CPU(){
		interrupt = Interrupts.NoInterrupt
		
		memory = Memory.getInstance()
		limit = Memory.memorySize - 1
		base = 0

		cores = new Core(this, memory) as Core[]
		registers = new int[8]
		pc = base

		// Start ui web server
		// WebServer.riseServer()
	}

	//-CPU Instance Variables Access---------------------

	def increment(){ pc++ }

	def getCores(int core){
		if((core < 0) || (core > (core.size() - 1)))
			interrupt = Interrupts.InvalidAddress
		else
			return cores[core];
	}

	def getMemory(){ memory	}

	def getPC(){ pc }

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

	// Outside imput--------------------------------------------------------

	def loadProgram(String file) {
		// Read the assembly program
		HardDrive.readFile(this, file)
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
		String output = 
		"\n\t       ${ANSI.CYAN_BACKGROUND} REGISTERS DUMP ${ANSI.RESET}\n"
		registers.eachWithIndex{ reg, i ->
			output += "[R${i+1}] = ${reg}\t"
			if((i + 1) % 3 == 0)
				output += "\n"
		}
		return output
	}

	// Runtime Auxiliar Methods --------------------
	private boolean legal(int e) {
		if ((e < base) || (e > limit)) {
			interrupt = Interrupts.InvalidAddress
			return false
		}
		return true
	}

	private reset(){
		interrupt = Interrupts.NoInterrupt
		
		limit = Memory.memorySize - 1
		base = 0;

		registers = new int[8]
		pc = base
	}
	// ---------------------------------------------

	def execute(String _program){
		reset()
		loadProgram(_program)
		String output

		while(interrupt == Interrupts.NoInterrupt) {
			// SHIELD
			if(legal(pc)){
				
				// @FETCH	
				ir = memory.get(pc)
				// println ir
				// @DECORE -> @EXECUTE
				cores[0]."${ir.OpCode}"(ir)
	
			}
			// @REPEAT
		}

		// ERR/OUT
		if(interrupt != Interrupts.STOP){
			output = ("${ANSI.RED_BOLD} Program interrupted with: ${ANSI.RED_UNDERLINE} ${interrupt} ${ANSI.RESET}")
			println registerDump()
			println memory.dump([0..18, 50..60] as Range[])		
		} else {
			if(registersOutput)
				output  = registerDump()
			if(memoryOutput)
				output += memory.dump(memoryOutput)
		}

		println output
		return output
	}
}