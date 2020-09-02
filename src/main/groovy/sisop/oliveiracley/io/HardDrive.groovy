package sisop.oliveiracley.io

import sisop.oliveiracley.processor.Memory
import sisop.oliveiracley.ui.ANSI
import sisop.oliveiracley.VM

class HardDrive {

	def static readFile (String _file) {
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

		return decodeAssembly(file) //?.text
	}


	Memory memory = Memory.getInstance()
	def static decodeAssembly(def file){
		String assembly = file?.text as String
		String[] code = assembly.split('\n')

			//memory.loadProgram(code)

		//println code[0]
	}

}