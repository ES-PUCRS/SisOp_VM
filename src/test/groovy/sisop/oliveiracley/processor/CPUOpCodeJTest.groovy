package sisop.oliveiracley.processor

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sisop.oliveiracley.processor.CPU

class CPUOpCodeJTest {

	CPU cpu = CPU.getInstance()

    //========================================================================================================================//   
    // No. |  OPCODE   |  Param  |     Syntax     |  Micro-operation         | R1  |   R2  | P | Description                  //
    //========================================================================================================================//
    // 01  | JMP       | (  3  ) |     JMP k      | PC ← k                   |     |       | K | Direct jump                  //
    // 02  | JMPI      | (  1  ) |    JMPI Rs     | PC ← Rs                  | Rs  |       |   | Register jump                //
    // 03  | JMPIG     | ( 1,2 ) |  JMPIG Rs, Rc  | Rc > 0 ? PC ← Rs : PC++  | Rs  |   Rc  |   | Jump if greater              //
    // 04  | JMPIL     | ( 1,2 ) |  JMPIL Rs, Rc  | Rc < 0 ? PC ← Rs : PC++  | Rs  |   Rc  |   | Jump if less                 //
    // 05  | JMPIE     | ( 1,2 ) |  JMPIE Rs, Rc  | Rc = 0 ? PC ← Rs : PC++  | Rs  |   Rc  |   | Jump if equal                //
    // 06  | JMPIM     | (  3  ) | JMPIM  [A]     | PC ← [A]                 |     |       | A | Memory jump                  //
    // 07  | JMPIGM    | ( 3,2 ) | JMPIGM [A], Rc | Rc > 0 ? PC ← [A] : PC++ |     |   Rc  | A | Memory jump if greater       //
    // 08  | JMPILM    | ( 3,2 ) | JMPILM [A], Rc | Rc < 0 ? PC ← [A] : PC++ |     |   Rc  | A | Memory jump if less          //
    // 09  | JMPIEM    | ( 3,2 ) | JMPIEM [A], Rc | Rc = 0 ? PC ← [A] : PC++ |     |   Rc  | A | Memory jump if equal         //
    //=====[ J - TYPE  INSTRUCTIONS ]=========================================================================================//


	@Test
	@DisplayName("JMP (PC <- k)")
    void JMP() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMP")
 		cpu.execute("J_Type/Assembly_JMP")
        assertEquals(6,cpu.getPC());
    }

	@Test
	@DisplayName("JMPI (PC <- Rs) *Uses LDI")
    void JMPI() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPI")
 		cpu.execute("J_Type/Assembly_JMPI")
        assertEquals(7,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIG (Rc > 0 ? PC <- Rs : PC++) *IF Fail")
    void JMPIGF() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIG-FAIL")
 		cpu.execute("J_Type/Assembly_JMPIG-FAIL")
        assertEquals(3,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIG (Rc > 0 ? PC <- Rs : PC++) *IF Pass")
    void JMPIGP() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIG-PASS")
 		cpu.execute("J_Type/Assembly_JMPIG-PASS")
        assertEquals(5,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIL (Rc < 0 ? PC <- Rs : PC++) *IF Fail")
    void JMPILF() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIL-FAIL")
 		cpu.execute("J_Type/Assembly_JMPIL-FAIL")
        assertEquals(3,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIL (Rc < 0 ? PC <- Rs : PC++) *IF Pass")
    void JMPILP() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIL-PASS")
 		cpu.execute("J_Type/Assembly_JMPIL-PASS")
        assertEquals(6,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIE (Rc = 0 ? PC <- Rs : PC++) *IF Fail")
    void JMPIEF() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIE-FAIL")
 		cpu.execute("J_Type/Assembly_JMPIE-FAIL")
        assertEquals(5,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIE (Rc = 0 ? PC <- Rs : PC++) *IF Pass")
    void JMPIEP() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIE-PASS")
 		cpu.execute("J_Type/Assembly_JMPIE-PASS")
        assertEquals(8,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIM (PC <- [A])")
    void JMPIM() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIM")
 		cpu.execute("J_Type/Assembly_JMPIM")
        assertEquals(11,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIGM (Rc > 0 ? PC <- [A] : PC++) *IF Fail")
    void JMPIGMF() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIGM-FAIL")
 		cpu.execute("J_Type/Assembly_JMPIGM-FAIL")
        assertEquals(4,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIGM (Rc > 0 ? PC <- [A] : PC++) *IF Pass")
    void JMPIGMP() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIGM-PASS")
 		cpu.execute("J_Type/Assembly_JMPIGM-PASS")
        assertEquals(12,cpu.getPC());
    }

	@Test
	@DisplayName("JMPILM (Rc < 0 ? PC <- [A] : PC++) *IF Fail")
    void JMPILMF() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPILM-FAIL")
 		cpu.execute("J_Type/Assembly_JMPILM-FAIL")
        assertEquals(4,cpu.getPC());
    }

	@Test
	@DisplayName("JMPILM (Rc < 0 ? PC <- [A] : PC++) *IF Pass")
    void JMPILMP() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPILM-PASS")
 		cpu.execute("J_Type/Assembly_JMPILM-PASS")
        assertEquals(13,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIEM (Rc = 0 ? PC <- [A] : PC++) *IF Fail")
    void JMPIEMF() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIEM-FAIL")
 		cpu.execute("J_Type/Assembly_JMPIEM-FAIL")
        assertEquals(4,cpu.getPC());
    }

	@Test
	@DisplayName("JMPIEM (Rc = 0 ? PC <- [A] : PC++) *IF Pass")
    void JMPIEMP() {
        cpu.loadProgramToMemory("J_Type/Assembly_JMPIEM-PASS")
 		cpu.execute("J_Type/Assembly_JMPIEM-PASS")
        assertEquals(14,cpu.getPC());
    }

}