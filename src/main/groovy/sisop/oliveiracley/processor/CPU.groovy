package sisop.oliveiracley.processor

import groovy.transform.ThreadInterrupt
import groovy.lang.Lazy


import sisop.oliveiracley.processor.process.ProcessControlBlock
import sisop.oliveiracley.processor.process.ProcessManager
import sisop.oliveiracley.processor.process.Interrupts
import sisop.oliveiracley.processor.process.IOREQUEST
import sisop.oliveiracley.processor.process.PRIORITY
import sisop.oliveiracley.processor.process.STATUS

import sisop.oliveiracley.ui.server.Web
import sisop.oliveiracley.ui.ANSI

import sisop.oliveiracley.io.HardDrive
import sisop.oliveiracley.VM


@ThreadInterrupt
class CPU 
	extends Thread {

    @Lazy
	Properties properties

		public static final boolean debug = false
		public static boolean web
	// Instance variables -------------------------------------------------------------
		private static CPU instance 		// Singleton instance

		private def Word 	ir 				// Instruction register
		private Interrupts 	interrupt 		// Processor state
		private int[] 		registers 		// Processor registers
		private Memory 		memory 			// RAM Memory
		private String		program 		// Program ready for execution
		private int 		base 			// Memory base access
		private int 		limit 			// Memory limit access
		private int 		pc 				// Program Counter
		private Core[] 		cores			// CPU Core 1

		private boolean 	registersOutput	// Enable registers output
		private Range[]		memoryOutput	// Configuration to output memory dump

		private ProcessManager pm 			// ProcessManager class to control process
		private int 		quantum			// Number of words until change process
		private int 		steps

		private String 		output 			// Request response interface
	//----------------------------------------------------------------------------------

	//-Singleton Class Configuration------------

	// Start the VM CPU
	def static run(String[] args){
		if(args.size() > 0) {
			def _instance = getInstance()
			args.each{ arg -> 
				_instance.loadProgramToMemory(arg as String)
				_instance.loadProgram(arg as String)
				_instance.execute(arg as String)
			}
		} else {
			// Start ui web server
			CPU.web = true
			Web.riseServer()
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
    	.getResource( VM.propertiesPath )
    	.withInputStream {
        	properties.load(it)
    	}

		interrupt = Interrupts.NoInterrupt
		
		registers 	= new int [ properties."cpu.registers" as int ]
		cores 		= new Core[ properties."cpu.registers" as int ]
		pm 			= ProcessManager.getInstance()
		memory 		= Memory.getInstance()
		
		cores.eachWithIndex { core, i -> 
			cores[i] = new Core(this, memory)
		}
		
		quantum = properties."cpu.quantum" as int
		pc = base = limit = -1
		program = ""
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
		return memory.loadProgram(
			_program,
			HardDrive.readFile(this, _program)
		)
		println memory.dump([0..30] as Range[])
	}

	def loadProcess(String _program){
		// reset()
		
		def programBounds = memory.grep(_program)
		if(programBounds){
			pm.newProcess(
				new ProcessControlBlock(
					ioRequest: 		 IOREQUEST.NONE,
					processPriority: PRIORITY.HIGH,
					processStatus:   STATUS.READY,
					processName: 	 _program,
					memoryLimit: 	 programBounds[1],
					memoryBase: 	 programBounds[0],
					registers: 		 new int[properties."cpu.registers" as int],
					cursor: 		 programBounds[0]
				)
			)
			println "Loaded process ${_program}"
			return true
		} else {
			interrupt = Interrupts.InvalidProgram
			return "Error on loading program \"${_program}\""
		}
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

	def registersDump(){
		String output
		if(!CPU.web)	output = "\n\t       ${ANSI.CYAN_BACKGROUND} REGISTERS DUMP ${ANSI.RESET}\n"
		else			output = "\n\t        REGISTERS DUMP \n"
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

	private reset(){
		program = ""
		base = limit = pc = -1
		memoryOutput = null
		registersOutput = false
		registers = new int[properties."cpu.registers" as int]
		interrupt = Interrupts.NoInterrupt
	}

	def free(String _program){
		memory.free(_program)
	}

	private setCores(String _program){
		cores.each{
			it.set(base: base, limit: limit, program: _program)
		}
	}

	// ---------------------------------------------
	def execute(String _program){ loadProgram(_program); execute(); }
	def execute(){
		this.start()
		this.join()
		output
	}

	public void run(){
		if(!pm.haveProcess())
			output = "There is no process ready to run"

		steps = 0
		def block
		program = pm.peek().getProcessName()
		if(memory.grep(program)){

			while(pm.haveProcess() || interrupt == Interrupts.NoInterrupt) {
				block = syncProcess(block)
				if(!block){	steps = 0; continue	}

				if(debug)
					println "Process:${steps}:${block}"
				
				// SHIELD
				if(legal(pc)){
					
					// @FETCH
					ir = memory.get(program, pc)
					// @DECORE -> @EXECUTE
					cores[0]."${ir.OpCode}"(ir)
		
				}

				// @REPEAT
				steps++
				if(!pm.haveProcess() &&
					interrupt == Interrupts.STOP)
					syncProcess(block)
			}

			def list = pm.processedList()
			list.each{ e ->			
			if (e.getProcessInterruption() != Interrupts.STOP){
				if(!CPU.web)	output = ("${ANSI.RED_BOLD} Program ${e.getProcessName()} interrupted with: ${ANSI.RED_UNDERLINE} ${e.getProcessInterruption()} ${ANSI.RESET}")
				else			output = (" Program ${e.getProcessName()} interrupted with:  ${e.getProcessInterruption()} \n")
			} else if (memoryOutput)
				output += "\n ${memory.dump(e.getProcessName())}"
			}

			if(output)
				if(!web)
					println output

		} else {
			// output = pm.processed()
			output = "The program has been removed from memory between load and execution\n"
			interrupt == Interrupts.InvalidProgram
		}
	}


	//CPU should have a ProcessControlBlock besides those separeted variables
	def syncProcess(ProcessControlBlock block){
		if (steps == quantum || interrupt != Interrupts.NoInterrupt){
			block.setProcessInterruption(interrupt)
			block.setProcessName(program)
			block.setRegisters(registers)
			block.setMemoryLimit(limit)
			block.setMemoryBase(base)
			block.setCursor(pc)
			
			if (interrupt == Interrupts.IOInterrupt){
				block.setProcessStatus(STATUS.BLOCKED)
			} else if (interrupt != Interrupts.NoInterrupt)
				block.setProcessStatus(STATUS.DONE)

			pm.saveProcess(block)
		
			if(pm.haveProcess())
				interrupt = Interrupts.NoInterrupt
		
			block = null
		} else if(steps == 0){
			block = pm.restoreProcess()
			if(block){
				program 	= block.getProcessName()
				limit		= block.getMemoryLimit()
				base 		= block.getMemoryBase()
				registers 	= block.getRegisters()
				pc 			= block.getCursor()
				setCores(program)

				if(!debug)
					println "Running:: ${block}"
			}
		}
		block
	}





	def test(def map){
		loadProgramToMemory("Assembly_sample")
		loadProgramToMemory("Assembly_test")
		loadProcess("Assembly_sample")
		loadProcess("Assembly_test")
	}
	def testx(def map){
		if(steps == 0){
			loadProgramToMemory("Assembly_sample")
			loadProcess("Assembly_sample")

			def block = syncProcess(null)
			block.setProcessStatus(STATUS.BLOCKED)
			block.setIoRequest(IOREQUEST.WRITE)
			pm.saveProcess(block)
			steps++
		} else {
			pm.pollBlocked()
		}
	}
}