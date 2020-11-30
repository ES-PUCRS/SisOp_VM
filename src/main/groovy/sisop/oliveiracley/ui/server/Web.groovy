package sisop.oliveiracley.ui.server

import com.sun.net.httpserver.*
import groovy.lang.Lazy

import sisop.oliveiracley.ui.server.Render
import sisop.oliveiracley.ui.ANSI
import sisop.oliveiracley.VM

class Web
	extends Thread {

	@Lazy
	private static Properties properties

	def static importProperties(){
		new Object() {}
	    	.getClass()
	    	.getResource( VM.propertiesPath )
	    	.withInputStream {
	        	properties.load(it)
	    	}
	}

	private Web(){ importProperties() }

	public void run(){
		final TYPES = [
			"css"	: "text/css",
			"html"	: "text/html",
			"jpg"	: "image/jpeg",
			"js"	: "application/javascript",
			"png"	: "image/png",
			"ico"	: "image/x-icon"
		]

		def render
		def port = (properties."server.port" as int) ?: 2345
		def root = new File(properties."ui.views.path")
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
				def exchangeRequestURI = (exchange.requestURI as String)
				if ((exchangeRequestURI.charAt(exchangeRequestURI.length()-1)) != "?")
					if (exchangeRequestURI.contains("?")){
						params = exchangeRequestURI
							.replaceAll(".*\\?","")
							.split('&')
							.inject([:]) { map, token -> 
	    	    				token.split('=').with {
	        						map[it[0]?.trim()] = (it[1]?.trim()?.replace("%20","")?.split(','))
	    						}
	    						map
							}
					}

				if(!path.contains("console") && !path.contains("favicon"))
					println "${ANSI.GREEN}GET $path ${ANSI.RESET}-> Params: $params"

				def file
				render = null
				if (path.contains(".ico")){
					file = new File((root.getPath() + "/assets"), path.substring(1))
				} else if (path.contains(".css")){
					file = new File((root.getPath() + "/styles"), path.substring(1))
				} else {
					file = new File(root, path.substring(1))
					if (file.isDirectory()) {
						file = new File(file, "shell.html")
						render = Render.shell(params)
					} else {
						file = new File(root, path.substring(1) + ".html")
				   		render = Render."${file.name.split(/\./)[0]}"(params)
					}
				}

				


				if (file.exists() || render) {
					exchange.responseHeaders.set(
						"Content-Type",
						TYPES[file.name.split(/\./)[-1]] ?: "text/plain"
					)
			        exchange.sendResponseHeaders(200, 0)

			        if(render){
						exchange.responseBody << render
			        } else {
				        file.withInputStream {
							exchange.responseBody << it
				        }
				    }

					exchange.responseBody.close()
				} else {	
			        exchange.sendResponseHeaders(404, 0)
					exchange.responseBody.close()
				}

			} catch(e) {
				println e.getLineNumber()
				println e.getMessage()
			}

		} as HttpHandler)

		server.start()
		println "Web Server started on port ${port}"
	}
}