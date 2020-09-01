package sisop.oliveiracley.processor

class CPU {

	enum Interrupts {
		NoInterrupt, InvalidAddress, InvalidInstruction, STOP;
	}

	//Instance variables ------------------------------------------------
		private static CPU instance 	// Singleton instance

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

		// hardLoad()
		
		while(interrupt == Interrupts.NoInterrupt) {
			
			// SHIELD
			if(legal(pc)){
				
				// @FETCH	
				def word = memory.getAt(0)

				// @DECORE -> @EXECUTE
				core_1."${word.OpCode}"(word)
				break;
	
			}
			// @REPEAT
		}

		if(interrupt != Interrupts.NoInterrupt)
			println interrupt
		else
			memory.dump(0..20)

	}



	//HARD LOAD PROGRAM
	// def hardLoad(){
	// 	memory.get(0).OpCode	= Core.OPCODE.
	// 	memory.get(0).r1		=
	// 	memory.get(0).r2		=
	// 	memory.get(0).p 		=
	// }

}