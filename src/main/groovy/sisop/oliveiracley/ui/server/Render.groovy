package sisop.oliveiracley.ui.server

import groovy.text.* 
import java.io.* 

class Render{

	private static final String root = "./src/main/groovy/sisop/oliveiracley/ui/server/views/"

	def static engine() {
		
		def file = new File(root, "test.html") 
		def binding = [
			'name'			: 'Project name:',
			'value'			: 0,
			'buttonValue'	: 'Subbimita'
		]
		
		def engine = new SimpleTemplateEngine() 
		def template = engine.createTemplate(file) 
		def writable = template.make(binding) 
		// println writable
		return writable
	}

	def static index() {		
		def file = new File(root, "index.html") 
		def binding = [:]
		
		
		new SimpleTemplateEngine()
			.createTemplate(file)
			.make(binding)
	}

	def static cmd() {
		// Runs directly the html file
		return null
	}
}
