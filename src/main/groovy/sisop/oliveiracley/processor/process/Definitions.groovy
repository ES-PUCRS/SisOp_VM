package sisop.oliveiracley.processor.process

enum PRIORITY {
	LOW(1), MEDIUM(2), HIGH(3);
	private final int value 
	PRIORITY(int value) { this.value = value }
}

enum STATUS {
	READY, BLOCKED, DONE;
}

enum IOREQUEST {
	NONE, WRITE, READ;
}

enum Interrupts {
	NoInterrupt,
	InvalidAddress,
	InvalidInstruction,
	InvalidProgram,
	IOInterrupt,
	STOP;
}