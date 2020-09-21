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
		private Map				virtual_memory
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
	    	.getResource( VM.propertiesPath )
	    	.withInputStream {
	        properties.load(it)
	    } 

	    // Initialize memory as empty
		memory = new Word[properties."memory.size" as int]
		memory.eachWithIndex{ word, i ->
			memory[i] = new Word(Core.OPCODE.___, 0, 0, 0)
		}

		virtual_memory = [:]
		pager = [:]
		
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
	def get(String program, int address){
		return memory[virtual_memory[program][address]]
	}

	// Memory output -----------------------------------------------------------
	def dump(String[] programs){
		String output
		if(!CPU.web)	output = "\n\n\t\t${ANSI.CYAN_BACKGROUND} MEMORY DUMP ${ANSI.RESET}"
		else			output = "\n\t\t       MEMORY DUMP "
				
		programs.eachWithIndex{ program, i ->
			if(virtual_memory.containsKey(program)){
				output += "\n\t\tProgram: ${program}"
				output += "\n[fake][real]"
				virtual_memory[program].each{ virtual, address ->
					output += "\n[${String.format( "%04d", (virtual as int) )}]"+
							  "[${String.format( "%04d", (address as int) )}]"+
							  " => ${memory[address].toString()}"
				}
			} else {
				output += "Program not loaded in memory"
				if(i < programs.size()) output += "\n"
			}
		}
		return output
	}
	def dump(Range[] ranges){
		String output
		if(!CPU.web)	output = "\n\n\t\t${ANSI.CYAN_BACKGROUND} MEMORY DUMP ${ANSI.RESET}"
		else			output = "\n\t\t       MEMORY DUMP "

		ranges.eachWithIndex { range, i ->
		  	memory.getAt(range)
			      .eachWithIndex{ word, l ->
					output += "\n[${String.format( "%04d", ((range[0] as int) + (l as int)) )}] => ${word.toString()}"
				}
			if(i != ranges.size()-1)
				output += "\n\t\t     ..."
		}
		return output
	}

	def dumpPages(Range[] ranges){
		String output
		if(!CPU.web)	output = "\n\n\t${ANSI.CYAN_BACKGROUND} ALLOCATED MEMORY ${ANSI.RESET}"
		else			output = "\n\t    ALLOCATED MEMORY "
		ranges.eachWithIndex { range, i ->
			range.each{
				output += "\nPage: ${it}"
				pager[it as int].each { frame ->
					output += "\n\t Frame: ${String.format( "%04d", frame.key )} -> ${frame.value}"
				}
			}
		}
		return output
	}

	// Memory Manager -------------------------------------
	// Subscribe memory with the program and allocate memory on pager
	def loadProgram(String program, Word[] context){
		if(!virtual_memory.containsKey(program)){
			if(context){
				def addresses = malloc(program, context.size())
				if(addresses){
					context.eachWithIndex{ word, i ->
						memory[addresses[i]] = word
					}
					return true
				}
			}
			return "Error on loading file \"${program}\""
		}
		return "Program already loaded in memory"
	}

	// Write where the memory is been used on pager
	private malloc(String program, int size){
		def frame
		def sheet = [:]
		int i = 0

		pager.each{ pageIndex, frameMap ->
			frame = frameMap.findAll { key, value -> value == false }
			if(i >= size) return true
			if (frame != [:]){
				frame.each{
					if(i < size){
						pager[(it.key/frames) as int][it.key] = true
						sheet[i] = it.key
						i++
					}
				}
			}
		}

		return virtual_memory[program] = sheet
	}

	// Unsubscrime memory and note memory pager
	def free(){
		String[] progs = new String[virtual_memory.size()]
		virtual_memory.each{
			progs += it.key
		}
		free(progs)
		return ""
	}
	def free(String program){ free([program] as String[]) }
	def free(String[] programs){
		String output = ""
		programs.eachWithIndex{ program, i ->
			if(virtual_memory.containsKey(program)){
				virtual_memory[program].each {virtual, address ->
					memory[address] = new Word(Core.OPCODE.___, 0, 0, 0)
					pager[((address/frames) as int)][address] = false
				}
				virtual_memory.remove(program)
			} else {
				output += "Program not loaded in memory"
				if(i < programs.size() -1) output += "\n"
			}
		}
		return output
	}

	// Return the program position [0]=begin [1]=end
	def grep(String program){
		if(virtual_memory.containsKey(program)){		
			def begin 	= 0
			def end 	= (virtual_memory[program].size() - 1)

			return [begin, end] as int[]
		}
		return null
	}
}