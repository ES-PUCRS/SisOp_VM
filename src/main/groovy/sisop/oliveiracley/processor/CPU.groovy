package sisop.oliveiracley.processor

import groovy.transform.ThreadInterrupt
import groovy.lang.Lazy

import sisop.oliveiracley.io.HardDrive
import sisop.oliveiracley.ui.WebServer
import sisop.oliveiracley.ui.ANSI
import sisop.oliveiracley.VM

@ThreadInterrupt
class CPU {

    @Lazy
	Properties properties

	enum Interrupts {
		NoInterrupt, InvalidAddress, InvalidInstruction, InvalidProgram, STOP;
	}

		public static final boolean debug = false
		// public static final boolean debug = true
	// Instance variables -----------------------------------------------------------
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
	def static run(String[] args){
		def _instance = getInstance()
		args.each{ arg -> 
			_instance.loadProgramToMemory(arg as String)
			_instance.execute(arg as String)
		}
	}

	// Singleton access
	def static getInstance() {
		if(!instance)
			instance = new CPU()
		return instance
	}

	// Singleton constructor

	private CPU(){
	    this
	    .getClass()
    	.getResource( VM.properties )
    	.withInputStream {
        	properties.load(it)
    	}

		interrupt = Interrupts.NoInterrupt
		
		registers = new int[ properties."cpu.registers" as int ]
		cores = new Core[properties."cpu.registers" as int]
		memory = Memory.getInstance()
		
		cores.eachWithIndex { core, i -> 
			cores[i] = new Core(this, memory)
		}
		
		pc = base = limit = -1

		// Start ui web server
		WebServer.riseServer()
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
		if((_pc < 0) || (_pc > ((properties."memory.size" as int) - 1)))
			interrupt = Interrupts.InvalidAddress
		else
			pc = _pc
	}

	// Outside imput--------------------------------------------------------

	def loadProgramToMemory(String _program) {
		// Read the assembly program
		memory.loadProgram(
			_program,
			HardDrive.readFile(this, _program)
		)
		if(debug)
			println "\n\n\t       ${ANSI.CYAN_BACKGROUND} Program loaded ${ANSI.RESET}" + memory.dump(_program).replaceFirst("\n","")
	}

	def loadProgram(String _program){
		def programBounds = memory.grep(_program)
		
		if(programBounds){
			base 	= programBounds[0]
			limit 	= programBounds[1]
		} else {
			interrupt = Interrupts.InvalidProgram
		}

		if(debug)
			println "Program loaded on base: ${base} -> limit: ${limit}"
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
			output += "[R${i}] = ${reg}\t"
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

	private reset(String program){
		interrupt = Interrupts.NoInterrupt
		
		if(base >= 0 && limit <= (properties."memory.size" as int) - 1)
			memory.free(program)

		limit = (properties."memory.size" as int) - 1
		base = 0;

		registers = new int[8]
		pc = base
	}

	private setCores(String _program){
		cores.each{
			it.set(base: base, limit: limit, program: _program)
		}
	}

	private prepare(String program){
		reset(program)
		loadProgram(program)
		setCores(program)
	}
	// ---------------------------------------------

	def execute(String _program){
		prepare(_program)
		String output

		while(interrupt == Interrupts.NoInterrupt) {
			// SHIELD
			if(legal(pc)){
				
				// @FETCH	
				ir = memory.get(_program, pc)
				// @DECORE -> @EXECUTE
				cores[0]."${ir.OpCode}"(ir)
	
			}
			// @REPEAT
		}
		// ERR/OUT
		if(interrupt != Interrupts.STOP){
			output = ("${ANSI.RED_BOLD} Program interrupted with: ${ANSI.RED_UNDERLINE} ${interrupt} ${ANSI.RESET}")
			if(debug){
				println registerDump()
				println memory.dump([base..limit] as Range[])
			}
		} else {
			if(registersOutput)
				output  = registerDump()
			if(memoryOutput)
				output += memory.dump(memoryOutput)
		}
	
		if(output)
			println output
		return output
	}
}