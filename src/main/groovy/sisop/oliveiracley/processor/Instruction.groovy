package sisop.oliveiracley.processor

class Word {
	public Core.OPCODE 	OpCode;	// OpCode or Mnemonic 
	public int 			r1; 	// Index to the first register of the operation (Rs or Rd cfe opcode on Core table)
	public int 			r2; 	// Index to the second register of the operation (Rs or Rd cfe opcode on Core table)
	public int 			p; 		// Parameter for the instruction (k or A on Core table), or data, if OpCode = DATA

	Word(Core.OPCODE _opc, int _r1, int _r2, int _p) {
		OpCode = _opc;
		r1 = _r1;
		r2 = _r2;
		p = _p;
	}

	@Override
	public String toString(){
		return "[OPCODE: ${OpCode}, R1: ${r1}, R2: ${r2}, P: ${p}]"
	}
}