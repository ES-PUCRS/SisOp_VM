package sisop.oliveiracley.ui.server

import sisop.oliveiracley.processor.CPU
import groovy.text.* 
import java.io.* 

class Render{

	private static final String root = "./src/main/groovy/sisop/oliveiracley/ui/server/views/"
	private static final CPU cpu = CPU.getInstance()


	def static index(def map) {
		def file = new File(root, "index.html") 
		def binding = [:]
		
		return
			new SimpleTemplateEngine()
				.createTemplate(file)
				.make(binding)
	}




	def static cpu_execute(def map) {
		def file = new File(root, "cpu_execute.html") 
		def resp

		map.each{
			if((it.value as String) == "[undefined]"){
				resp = cpu.execute()
			} else {
				it.value.eachWithIndex{ parm, i ->
					def i =cpu_load(param as String)
					if(!i){
						resp = i
					} else {
						resp = cpu.execute(param as String)
					}
					if(i < it.value.size()){
						resp += "\n"
					}
				}
			}
		}

		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)	
	}



	def static cpu_load(def map){
		def file = new File(root, "cpu_load.html") 
		def resp = ""

		map.each{
			it.value.each{
				def i = cpu.loadProgram(it as String)
				if(i == false)
					resp += "Error on loading file \"${it}\""
			}
		}

		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)
	}


	def static cpu_load_memory(def map){
		def file = new File(root, "cpu_load_memory.html") 
		def resp = ""

		map.each{
			it.value.each{
				def i = cpu.loadProgramToMemory(it as String)
				if(i != true)
					if(i == false)
						resp += "Error on loading file \"${it}\""
					else
						resp += i	
			}
		}

		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)
	}

	def static favicon(def map) { return null }
	def static cmd(def map) { return null }
}
