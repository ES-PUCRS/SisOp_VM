package sisop.oliveiracley.processor

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sisop.oliveiracley.processor.Memory
import sisop.oliveiracley.processor.Core
import sisop.oliveiracley.processor.CPU

class CPUOpcodeITest {


	CPU cpu = CPU.getInstance()

	//=========================================================================================================================// 	
	// No. |  OPCODE   |  Param  |	    Syntax	   |  Micro-operation 		  | R1	|   R2	| P | Description			 	   //
	//=========================================================================================================================//
	// 10  | ADDI	   | ( 1,3 ) |	 ADDI Rd, k	   | Rd   ← Rd + k			  |	Rd	|		| K | Immediate addition		   //
	// 11  | SUBI	   | ( 1,3 ) |	 SUBI Rd, k    | Rd   ← Rd - k			  |	Rd	|		| K | Immediate subtraction		   //
	// 12  | LDI	   | ( 1,3 ) |	 LDI  Rd, k	   | Rd   ← k				  |	Rd	|		| K | Load immediate 			   //
	// 13  | LDD	   | ( 1,3 ) |	 LDD  Rd,[A]   | Rd   ← [A]				  |	Rd	|		| A | Load direct from data memory //
	// 14  | STD	   | ( 3,1 ) |	 STD [A],Rs	   | [A]  ← Rs				  |	Rs	|		| A | Store direct to data memory  //
	//=====[ I - TYPE  INSTRUCTIONS ]==========================================================================================//


	@Test
	@DisplayName("ADDI (Rd <- Rd + k)")
    void ADDI() {
 		cpu.execute("I_Type/Assembly_ADDI")
        assertEquals(25,cpu.getRegister(0));
    }

	@Test
	@DisplayName("SUBI (Rd <- Rd - k)")
    void SUBI() {
 		cpu.execute("I_Type/Assembly_SUBI")
        assertEquals(14,cpu.getRegister(1));
    }

	@Test
	@DisplayName("LDI (Rd <- k)")
    void LDI() {
 		cpu.execute("I_Type/Assembly_LDI")
        assertEquals(2,cpu.getRegister(4));
    }

	@Test
	@DisplayName("LDD (Rd <- [A])")
    void LDD() {
 		cpu.execute("I_Type/Assembly_LDD")
        assertEquals(8,cpu.getRegister(5));
    }

	@Test
	@DisplayName("STD ([A] <- Rs)")
    void STD() {
 		cpu.execute("I_Type/Assembly_STD")
        Memory memory = cpu.getMemory()

        assertEquals(8, memory.get(0).p);
        assertEquals(Core.OPCODE.DATA, memory.get(0).OpCode);
    }
}