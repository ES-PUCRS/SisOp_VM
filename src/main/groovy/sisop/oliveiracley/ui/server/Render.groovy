package sisop.oliveiracley.ui.server

import sisop.oliveiracley.processor.process.ProcessControlBlock
import sisop.oliveiracley.processor.process.ProcessManager
import sisop.oliveiracley.processor.process.IOREQUEST
import sisop.oliveiracley.processor.Memory
import sisop.oliveiracley.processor.CPU
import sisop.oliveiracley.VM

import groovy.text.SimpleTemplateEngine 
import groovy.lang.Lazy

class Render{

	@Lazy
	private static Properties properties
	private static final ProcessManager pm 	= ProcessManager.getInstance()
	private static final CPU cpu 			= CPU.getInstance()
	private static final Memory memory 		= cpu.getMemory()
	private static final String root 		= importProperties()

	def static shell(def map) {	null }
	def static console(def map) {
		def file = new File(root, "console.html")
		def writeList 		= ""
		def readList 		= ""
		def response		= ""
		
		if(!map.isEmpty())
			map.each{ key, value -> 
				response += key
			}

		def disabled
		pm.filterByIORequest(IOREQUEST.READ).each { block ->
			if(block.getProcessName().equals(response))
				 disabled = "disabled"
			else disabled = ""
			readList +=
			"""
				<form id="readForm" action="/unblock">
					<label for="block.getProcessName()">${block.getProcessName()}</label>
					<br>
					<input type="number" id="${block.getProcessName()}" name="${block.getProcessName()}" ${disabled}>
					<br><br>
				</form>
			"""
		}
		if(readList.equals(""))
			readList = "<p>There is no blocked process waiting to be read</p>"

		def disabledField
		pm.filterByIORequest(IOREQUEST.WRITE).each { block ->
			if(block.getProcessName().equals(response)){
				disabledField = "disabled"
				disabled = "disabled"
			} else {
				disabled = "readonly"
				disabledField = ""
			}
			writeList +=
			"""
				<form id="writeForm" action="/unblock">
					<label for="block.getProcessName()">${block.getProcessName()}</label>
					<br>
					<input class="textField" type="text" id="${block.getProcessName()}" name="${block.getProcessName()}" value="${block.getIoRegisters()[1]}" ${disabled}>
					<input class="textFieldButton" type="submit" value="Free" ${disabledField}>
					<br><br>
				</form>
			"""
		}
		if(writeList.equals(""))
			writeList = "<p>There is no blocked process waiting to be written</p>"

		
		def binding = [
			'writeList' : writeList,
			'readList': readList
		]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)
	}

	def static unblock(def map) {

		try {
			new Thread() {
			    public void run() {
			        map.each{ key, value ->
						pm.unblock(key, (value[0] as int))
					}
			    }
			}.start();
		} catch (Exception np) {
			println "Thrown on: ${np.getLineNumber}"
			println "Error: ${np.getMessage}"
		}				
		
		return console(map)
	}



	def static output(def map) {
		def file = new File(root, "cpu.html") 
		def resp

				resp = cpu.output(map)
		resp = resp.replaceFirst("\n","")
		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)
	}

	def static free(def map) {		
		def file = new File(root, "template.html") 
		def resp
			if(map["file"] != ["undefined"])
				resp = cpu.free(map["file"])
			else
				resp = cpu.free()

		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)	
	}


	def static dump_memory(def map) {
		def file = new File(root, "memory.html")
		def resp = ""

		if(map["params"][0] == "true"){
			def pages = (properties."memory.size" as int) -1
			resp = cpu.getMemory().dump([0..pages] as Range[])
		} else {
			Range[] rng = [] as Range[]
			String[] str = [] as String[]
			boolean success = true
			String err = " is not a valid range or program name"
			map["params"].eachWithIndex{ it, i ->
				def attempt
				def tmp
				try{
					if(!it.contains("..")){
						attempt = [(it as int)..(it as int)] as Range[]
						rng += attempt
					} else {
						tmp = it.replaceAll("\\[","")
        						.replaceAll("\\]","")
						tmp = tmp.split("\\.\\.")
						attempt = [(tmp[0] as int)..(tmp[1] as int)] as Range[]
        				rng += attempt
					}
				} catch(Exception e) {
					// println e.getMessage()
					try {
						attempt = it as String
						tmp = memory.grep(attempt)
						if(tmp){ str += attempt }
						else {success = false}
					}catch(Exception ex)
					{ ex.printStackTrace() }
				}

				if(!success){
					resp += attempt + err
					if(i < (it.size() - 1)){ resp += "\n" }
				}
			}

			if(rng)
				resp += memory.dump(rng)
			if(str)
				resp += memory.dump(str)
		}

		resp = resp.replaceFirst("\n","")
		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)	
	}



	def static dump_pages(def map) {
		def file = new File(root, "memory.html")
		def resp = ""

		if(map["params"][0] == "true"){
			def pages = ((properties."memory.size" as int) / (properties."memory.frames" as int)) -1
			resp = cpu.getMemory().dumpPages([0..pages] as Range[])
		} else {
			Range[] rng = [] as Range[]
			boolean success = true
			String err = " is not a valid range"
			map["params"].eachWithIndex{ it, i ->
				if(it.size() < 4){
					try {
						rng += [it..it] as Range[]	
					}catch(Exception e) { resp += rng + err; success = false }						
				} else {
					def tmp
					try {
						tmp = it.replaceAll("\\[","")
        						.replaceAll("\\]","")
						tmp = tmp.split("\\.\\.")
        				rng += [tmp[0]..tmp[1]] as Range[]
					}catch(Exception ex) { resp += rng + err; success = false }					
				}
				if(i < (it.size() - 1)){ resp += "\n" }
			}
			if(success){
				resp += memory.dumpPages(rng)
			}
		}

		resp = resp.replaceFirst("\n","")
		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)	
	}



	def static dump_registers(def map) {
		def file = new File(root, "cpu.html")
		
			def resp = cpu.registersDump()

		resp = resp.replaceFirst("\n","")
		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)	
	}

	def static cpu_execute(def map) {
		def file = new File(root, "cpu.html") 
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

		if(resp)
			resp = resp.replaceFirst("\n","")
		else
			resp = ""

		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)	
	}

	def static cpu_load(def map){
		def file = new File(root, "template.html") 
		def resp = ""

		map.each{
			it.value.each{ //it, i ->
				def i = cpu.loadProcess(it as String)
				if(i != true) { if(!resp) resp = i else resp += "\n${i}"}
			}
		}

		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)
	}

	def static cpu_load_memory(def map){
		def file = new File(root, "template.html") 
		def resp = ""

		map.each{
			it.value.each{ //it, i ->
				def i = cpu.loadProgramToMemory(it as String)
				if(i != true) { if(!resp) resp = i else resp += "\n${i}"}
			}
		}

		def binding = ['response' : resp]
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)
	}



	// DEVTOOLS
	def static restart(def map) {
		Runtime.
		   getRuntime().
		   exec("cmd /c start \"\" DevTools.bat 0");
	}

	def static commit(def map) {
		Runtime.
		   getRuntime().
		   exec("cmd /c start \"\" DevTools.bat 1 \"${map["comment"]}\"");
	}



	def static favicon(def map) { 
		def file = new File((root+"assets"), "template.html") 

	}

	def static importProperties(){
		new Object() {}
	    	.getClass()
	    	.getResource( VM.propertiesPath )
	    	.withInputStream {
	        	properties.load(it)
	    	}
	    properties."ui.views.path"
	}
}
