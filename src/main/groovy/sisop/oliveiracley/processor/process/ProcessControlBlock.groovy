package sisop.oliveiracley.processor.process

import sisop.oliveiracley.processor.process.IOREQUEST
import sisop.oliveiracley.processor.process.PRIORITY
import sisop.oliveiracley.processor.process.STATUS

import groovy.transform.MapConstructor
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@MapConstructor
class ProcessControlBlock
	implements Comparable<ProcessControlBlock> {

	private static	 	serial = 0
	private final int	id = serial++
	Interrupts			processInterruption
	PRIORITY 			processPriority
	STATUS 				processStatus
	String				processName
	int 				memoryLimit
	int 				memoryBase
	IOREQUEST			ioRequest
	int[] 				registers
	int					cursor


	@Override
	public int compareTo(ProcessControlBlock n) {
        return ((this.processPriority.value - n.processPriority.value) * -1);
	}

	@Override
    public String toString(){
    	"[ id:${id}, prog:${processName}, priority:${processPriority}, status:${processStatus}, cursor:${cursor} ]"
    }
}