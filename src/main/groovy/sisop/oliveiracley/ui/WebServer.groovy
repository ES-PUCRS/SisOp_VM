package sisop.oliveiracley.ui

import com.sun.net.httpserver.HttpServer
import groovy.lang.Lazy

import sisop.oliveiracley.VM

class WebServer {

	@Lazy
	private static Properties properties

	def static riseServer(){
		importProperties()
		int PORT = (properties."server.port" as int)
		HttpServer.create(new InetSocketAddress(PORT), /*max backlog*/ 0).with {
		    println "Server is listening on ${PORT}, hit Ctrl+C to exit."    
		    createContext("/") { http ->
		        http.responseHeaders.add("Content-type", "text/plain")
		        http.sendResponseHeaders(200, 0)
		        http.responseBody.withWriter { out ->
		            out << "Hello ${http.remoteAddress.hostName}!\n"
		        }
		        println "Hit from Host: ${http.remoteAddress.hostName} on port: ${http.remoteAddress.holder.port}"
		    }
		    start()
		}
	}

	def static importProperties(){
		new Object() {}
	    	.getClass()
	    	.getResource( VM.properties )
	    	.withInputStream {
	        	properties.load(it)
	    	}
	}
}