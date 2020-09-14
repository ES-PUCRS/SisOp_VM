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
		private Map				pager

		private final int		frames
		private final int		pages
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

	    // Initialize memory as empty
		memory = new Word[properties."memory.size" as int]
		memory.eachWithIndex{ word, i ->
			memory[i] = new Word(Core.OPCODE.___, 0, 0, 0)
		}

		workbook = pager = [:]
		
		// Grab pages configuration
		pages = ((properties."memory.size" as int) / (properties."memory.frames" as int)) -1
		frames = (properties."memory.frames" as int)
		
		// Initialize memory map as empty
		def sheet
		(0..pages).each{ page ->
			sheet = [:]
			(0..(frames - 1)).each{ frame ->
				sheet[(page * frames) + frame] = false
			}
			pager[page] = sheet
		}
	}

	// Memory access ------------
	def get(int address){
		return memory[address]
	}

	// Memory output -----------------------------------------------------------
	def dump(String _program){
		return dump(([workbook[_program][0]..workbook[_program][1]] as Range[]))
	}
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

	def dumpPages(Range[] ranges){
		String output =
		"\n\n\t${ANSI.CYAN_BACKGROUND} ALLOCATED MEMORY ${ANSI.RESET}"
		ranges.eachWithIndex { range, i ->
			range.each{
				output += "\nPage: ${it}"
				pager[it].each { frame ->
					output += "\n\t Frame: ${String.format( "%04d", frame.key )} -> ${frame.value}"
				}
			}
		}
		return output
	}

	// Memory Manager -------------------------------------
	// Subscribe memory with the program and allocate memory on pager
	def loadProgram(String program, Word[] context){
		def bounds = malloc(context.size())
		workbook[program] = [ bounds[0], bounds[1] ]
		context.eachWithIndex{ word, i ->
			memory[i] = word
		}
	}

	// Write where the memory is been used on pager
	def malloc(int size){
		// Range range
		// def sheet
		// def begin
		// pager.each{ page -> 
		// 	sheet = page.findAll { key, value -> value == false }
		// 	if (sheet != [:]){
		// 		begin = sheet.take(1).key
		// 		range = (begin..begin)
		// 		(begin..(begin + size)).each{
		// 			if(pager[((it/frames) as int)][it])
		// 		}
		// 	}
		// }

		// Starts to be shiet code.
		// Stop 5 A.m.

		return [0, size-1] as int []
	}

	// Unsubscrime memory and note memory pager
	def free(Range range){
		range.each{
			memory[it] = new Word(Core.OPCODE.___, 0, 0, 0)
			pager[((it/frames) as int)][it] = false
		}
	}

	// Return the program position [0]=begin [1]=end
	def grep(String program){
		return workbook[program]
	}
}