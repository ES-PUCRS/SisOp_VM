package sisop.oliveiracley.io

import sisop.oliveiracley.processor.Memory
import sisop.oliveiracley.processor.Word
import sisop.oliveiracley.processor.Core
import sisop.oliveiracley.processor.CPU
import sisop.oliveiracley.ui.ANSI
import sisop.oliveiracley.VM

// import java.util.regex.*

class HardDrive {


	def static readFile (CPU cpu, String _file) {
		def file
		try{
			file = new Object() { }.getClass().getResource("/HardDisk/${_file}")
		}catch(Exception e) {
			println "${ANSI.RED_BOLD} Error reading file: ${ANSI.RED_UNDERLINE} ${_file} ${ANSI.RESET}"
			println "${ANSI.WHITE}" 			+
					"${ANSI.RED_BACKGROUND} " 	+ 
						"${e.getMessage()}"		+
					"${ANSI.RESET}"
		}

		return decodeAssembly(cpu, file) //?.text
	}


	def static decodeAssembly(CPU cpu, def file){
		String assembly = file?.text as String
		String[] code = assembly.split('\n')
		Word[] temp = new Word[code.size()]

		code.eachWithIndex{ word, i ->
		// -Formating String-------------------------
			word =  word.replaceAll("^\\d+\\s+|^\\d+\\t+","")
						.replaceAll("//.*","")
			if(word.contains("\t"))
				word = 	word.replaceFirst("\t",",")
							.replaceAll("\t","")
			else
				word = 	word.replaceFirst(" ",",")
							.replaceAll(" ","")

			word = word.split(",")
		// ------------------------------------------
			try{
				temp[i] = translator(word)
			} catch(Exception e){
				cpu.setInterruption(CPU.Interrupts.InvalidInstruction)
			}
		}
		//memory.loadProgram(temp)
	}


	translator(def word){
		def opcode, r1, r2, p

		try{
			opcode = Core.OPCODE."${word[0]}"
			
		}catch(Exception e){

		}

		return new Word(opcode, r1, r2, p)
	}
}