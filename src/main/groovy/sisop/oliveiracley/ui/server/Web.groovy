package sisop.oliveiracley.ui.server

import com.sun.net.httpserver.*
import groovy.lang.Lazy

import sisop.oliveiracley.ui.server.Render
import sisop.oliveiracley.VM

class Web {

	@Lazy
	private static Properties properties

	def static importProperties(){
		new Object() {}
	    	.getClass()
	    	.getResource( VM.properties )
	    	.withInputStream {
	        	properties.load(it)
	    	}
	}


	def static riseServer(){
		importProperties()

		final TYPES = [
			"css": "text/css",
			"html": "text/html",
			"jpg": "image/jpeg",
			"js": "application/javascript",
			"png": "image/png",
		]

		def render
		def port = (properties."server.port" as int) ?: 2345
		def root = new File("./src/main/groovy/sisop/oliveiracley/ui/server/views")
		def server = HttpServer.create(new InetSocketAddress(port), 0)

		server.createContext("/", { HttpExchange exchange ->	
			try {
				
				if (!"GET".equalsIgnoreCase(exchange.requestMethod)) {			
			        exchange.sendResponseHeaders(405, 0)
					exchange.responseBody.close()
					return
				}
				
				def params = [:]
				def path = exchange.requestURI.path
				if((exchange.requestURI as String).conteins("?")){
					params = (exchange.requestURI as String)
									  .replaceAll(".*\\?","")
				}

				println "GET $path -> Params: $params"

				def file = new File(root, path.substring(1) + ".html")
				if (file.isDirectory()) {
					file = new File(file, "index.html")
					render = Render."index"()
				} else {
			   		render = Render."${path.substring(1)}"()
				}

				if (file.exists()) {
					exchange.responseHeaders.set(
						"Content-Type",
						TYPES[file.name.split(/\./)[-1]] ?: "text/plain"
					)
			        exchange.sendResponseHeaders(200, 0)

			        if(render)
						exchange.responseBody << render
				    else
				        file.withInputStream {
							exchange.responseBody << it
				        }
					exchange.responseBody.close()
				} else {		
			        exchange.sendResponseHeaders(404, 0)
					exchange.responseBody.close()
				}

			} catch(e) {
				e.printStackTrace()
			}

		} as HttpHandler)

		server.start()
		println "Web Server started on port ${port}"
	}
}

			   //      file.withInputStream {
						// exchange.responseBody << it
			   //      }

				// if(path == "/index.html"){
				// 	def str = exchange.requestURI as String
				// 	str = str.replaceAll(".*\\?","")
				// 	println str
				// }