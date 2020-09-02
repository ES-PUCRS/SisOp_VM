package sisop.oliveiracley.io

import sisop.oliveiracley.ui.ANSI
import sisop.oliveiracley.VM

class HardDrive {

	def static readFile (String _file) {
		def file
		try{
			file = new Object() { }.getClass().getResource("/${_file}")
		}catch(Exception e) {
			println "${ANSI.RED_BOLD} Error reading file: ${ANSI.RED_UNDERLINE} ${_file} ${ANSI.RESET}"
			println "${ANSI.WHITE}" 			+
					"${ANSI.RED_BACKGROUND} " 	+ 
						"${e.getMessage()}"		+
					"${ANSI.RESET}"
		}

		return file //?.text
	}


}