package sisop.oliveiracley.processor

class Memory {

	public static final int memorySize = 1024

	class Word {
		public Core.OPCODE 	OpCode;	// OpCode or Mnemonic 
		public int 			r1; 	// Index to the first register of the operation (Rs or Rd cfe opcode on Core table)
		public int 			r2; 	// Index to the second register of the operation (Rs or Rd cfe opcode on Core table)
		public int 			p; 		// Parameter for the instruction (k or A on Core table), or data, if OpCode = DATA

		Word(Core.OPCODE _opc, int _r1, int _r2, int _p) {
			OpCode = _opc;
			r1 = _r1;
			r2 = _r2;
			p = _p;
		}

		@Override
		public String toString(){
			return "[OPCODE: ${OpCode}, R1: ${r1}, R2: ${r2}, P: ${p}]"
		}
	}

	private static Memory instance
	private Word[] memory

	def static getInstance(){
		if(!instance)
			instance = new Memory()
		return instance
	}

	private Memory(){
		memory = new Word[memorySize]
		memory.eachWithIndex{ word, i ->
			memory[i] = new Word(Core.OPCODE.___, 0, 0, i)
		}
	}

	def get(int address){
		return memory[address]
	}

	def dump(){
		memory.each{ word ->
			println word
		}
	}

	def dump(Range range){
	  memory.getAt(range)
		  	.eachWithIndex{ word, i ->
				println "[${String.format( "%04d", i + range[0] )}] => " + word.toString()
			}
	}
}