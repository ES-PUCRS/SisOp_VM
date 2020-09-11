package sisop.oliveiracley.processor

import groovy.lang.Lazy

import sisop.oliveiracley.ui.ANSI
import sisop.oliveiracley.VM

class Memory {

    @Lazy
	Properties properties

	// Instance variables ------------------------
		private static Memory 	instance
		private Word[] 			memory
		private Map				workbook
	//---------------------------------------------
	

	// Singleton access
	def static getInstance(){
		if(!instance)
			instance = new Memory()
		return instance
	}
	
	// Singleton constructor
	private Memory(){
	    this.getClass()
	    	.getResource( VM.properties )
	    	.withInputStream {
	        properties.load(it)
	    }
		memory = new Word[properties."memory.size" as int]
		memory.eachWithIndex{ word, i ->
			memory[i] = new Word(Core.OPCODE.___, 0, 0, 0)
		}
		workbook = [:]
	}

	// Memory access ------------
	def get(int address){
		return memory[address]
	}

	// Memory output -----------------------------------------------------------
	def dump(Range[] ranges){
		String output =
		"\n\n\t\t${ANSI.CYAN_BACKGROUND} MEMORY DUMP ${ANSI.RESET}"

		ranges.eachWithIndex { range, i ->
		  	memory.getAt(range)
			      .eachWithIndex{ word, l ->
					output += "\n[${String.format( "%04d", l + range[0] )}] => " + word.toString()
				}
			if(i != ranges.size()-1)
				output += "\n\t\t     ..."
		}
		return output
	}


	// Memory Manager -------------------------------------
	def loadProgram(String program, Word[] context){
		workbook[program] = [ 0, context.size() ]
		context.eachWithIndex{ word, i ->
			memory[i] = word
		}
	}

	def grep(String program){
		return workbook[program]
	}
}