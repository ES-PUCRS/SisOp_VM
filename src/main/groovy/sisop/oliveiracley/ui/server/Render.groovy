package sisop.oliveiracley.ui.server


import sisop.oliveiracley.processor.CPU
import sisop.oliveiracley.VM

import groovy.text.SimpleTemplateEngine 
import groovy.lang.Lazy

class Render{

	@Lazy
	private static Properties properties

	private static final String root = "./src/main/groovy/sisop/oliveiracley/ui/server/views/"
	private static final CPU cpu = CPU.getInstance()

	def static index(def map) {
		if(!properties) { importProperties() }
		def file = new File(root, "index.html") 
		def binding = [
			'port' 	: 	properties."server.port" as int,
			'url' 	: 	properties."server.url"
		]
		
		return
			new SimpleTemplateEngine()
				.createTemplate(file)
				.make(binding)
	}








	def static cpu_execute(def map) {
		def file = new File(root, "cpu_execute.html") 
		def resp = ""

		map.each{
			if((it.value as String) == "[undefined]"){
				resp = cpu.execute()
			} else {
				it.value.eachWithIndex{ parm, i ->
					resp += cpu.execute(parm as String)
					if(i < (it.value.size() - 1)){ resp += "\n" }
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
				if(i != true)
					resp += i
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


	def static importProperties(){
		new Object() {}
	    	.getClass()
	    	.getResource( VM.propertiesPath )
	    	.withInputStream {
	        	properties.load(it)
	    	}
	}
}
