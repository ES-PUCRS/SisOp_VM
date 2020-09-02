package sisop.oliveiracley.processor

import sisop.oliveiracley.ui.ANSI

class Memory {

	public static final int memorySize = 1024
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
			memory[i] = new Word(Core.OPCODE.___, 0, 0, 0)
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

	def dump(Range[] ranges){
		println "\n\n\t\t${ANSI.CYAN_BACKGROUND} MEMORY DUMP ${ANSI.RESET}"

		ranges.eachWithIndex { range, i ->
		  	memory.getAt(range)
			      .eachWithIndex{ word, l ->
					println "[${String.format( "%04d", l + range[0] )}] => " + word.toString()
				}
			if(i != ranges.size()-1)
				println "\t\t     ..."
		}
	}
}