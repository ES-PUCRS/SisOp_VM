package sisop.oliveiracley

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sisop.oliveiracley.processor.Memory
import sisop.oliveiracley.processor.Core
import sisop.oliveiracley.processor.CPU

class CPUOpcodeR2Test {


	CPU cpu = CPU.getInstance()

	//=========================================================================================================================// 	
	// No. |  OPCODE   |  Param  |	    Syntax	   |  Micro-operation 		  | R1	|   R2	| P | Description			 	   //
	//=========================================================================================================================//
	// 15  | ADD	   | ( 1,2 ) |	  ADD Rd, Rs   | Rd   ← Rd + Rs			  |	Rd	|	Rs	|	| Addition					   //
	// 16  | SUB	   | ( 1,2 ) |	  SUB Rd, Rs   | Rd   ← Rd - Rs			  |	Rd	|	Rs	|	| Subtraction				   //
	// 17  | MULT	   | ( 1,2 ) |	 MULT Rd,[Rs]  | Rd   ← Rd * Rs			  |	Rd	|	Rs	|	| Multiplication 			   //
	// 18  | LDX	   | ( 1,2 ) |	 LDX  Rd,[Rs]  | Rd   ← [Rs]			  |	Rd	|	Rs	|	| Indirect load from memory	   //
	// 19  | STX	   | ( 1,2 ) |	 STX [Rd],Rs   | [Rd] ← Rs				  |	Rd	|	Rs	|	| Indirect storage to memory   //
	//=====[ R2 - TYPE INSTRUCTIONS ]==========================================================================================//
	

	@Test
	@DisplayName("ADD (Rd <- Rd + Rs)")
    void ADD() {
 		cpu.execute("R2_Type/Assembly_ADDI")
        assertEquals(25,cpu.getRegister(0));
    }

	@Test
	@DisplayName("SUBI (Rd <- Rd - k)")
    void SUBI() {
 		cpu.execute("R2_Type/Assembly_SUBI")
        assertEquals(14,cpu.getRegister(1));
    }

	@Test
	@DisplayName("LDI (Rd <- k)")
    void LDI() {
 		cpu.execute("R2_Type/Assembly_LDI")
        assertEquals(2,cpu.getRegister(4));
    }

	@Test
	@DisplayName("LDD (Rd <- [A])")
    void LDD() {
 		cpu.execute("R2_Type/Assembly_LDD")
        assertEquals(8,cpu.getRegister(5));
    }

	@Test
	@DisplayName("STD ([A] <- Rs)")
    void STD() {
 		cpu.execute("R2_Type/Assembly_STD")
        Memory memory = cpu.getMemory()

        assertEquals(8, memory.get(0).p);
        assertEquals(Core.OPCODE.DATA, memory.get(0).OpCode);
    }
}