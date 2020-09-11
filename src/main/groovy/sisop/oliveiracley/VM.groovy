package sisop.oliveiracley

import sisop.oliveiracley.processor.CPU;

class VM {

	public static final String properties = "/application.properties"
    public static void main(String[] args) {
         CPU.run(args)
    }

}