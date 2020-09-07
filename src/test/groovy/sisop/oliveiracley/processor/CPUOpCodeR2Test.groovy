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
 		cpu.execute("R2_Type/Assembly_ADD")
        assertEquals(11,cpu.getRegister(2));
    }

	@Test
	@DisplayName("SUB (Rd <- Rd - Rs)")
    void SUB() {
 		cpu.execute("R2_Type/Assembly_SUB")
        assertEquals(-1,cpu.getRegister(2));
    }

	@Test
	@DisplayName("MULT (Rd <- Rd * Rs)")
    void MULT() {
 		cpu.execute("R2_Type/Assembly_MULT")
        assertEquals(12,cpu.getRegister(2));
    }

	@Test
	@DisplayName("LDX (Rd <- [Rs])")
    void LDX() {
 		cpu.execute("R2_Type/Assembly_LDX")
        assertEquals(9,cpu.getRegister(4));
    }

	@Test
	@DisplayName("STX ([Rd] <- Rs)")
    void STX() {
 		cpu.execute("R2_Type/Assembly_STX")
        Memory memory = cpu.getMemory()

        assertEquals(10, memory.get(5).p);
        assertEquals(Core.OPCODE.DATA, memory.get(5).OpCode);
    }
}