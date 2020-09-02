package sisop.oliveiracley.io

import sisop.oliveiracley.processor.Memory

class Decoder {

	Memory memory = memory.getInstance()

	def static decodeAssembly(def file){
		String assembly = file?.text as String
		String[] code = assembly.split('\n')

			//memory.loadProgram(code)

		//println code[0]
	}

}