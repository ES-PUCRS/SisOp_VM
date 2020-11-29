package sisop.oliveiracley.processor.process

import sisop.oliveiracley.processor.process.Interrupts
import sisop.oliveiracley.processor.Memory
import sisop.oliveiracley.processor.Core

import java.util.PriorityQueue
import java.util.ArrayList
import java.util.Queue

class ProcessManager {

	private Queue<ProcessControlBlock> 	processedList
	private Queue<ProcessControlBlock> 	processList
	private Queue<ProcessControlBlock> 	blockedList
	private static ProcessManager 		instance
	private Memory 						memory 

	static ProcessManager getInstance() {
		if (!instance) instance = new ProcessManager()
		return instance
	}

	private ProcessManager(){
		processedList = new LinkedList()
		processList = new PriorityQueue()
		blockedList = new LinkedList()
		memory = Memory.getInstance()
	}


	def saveProcess(ProcessControlBlock e){
		if(e.getProcessStatus() == STATUS.DONE)
			processedList.add(e)
		else{
			if 		(e.getProcessPriority().value == 3) e.setProcessPriority(PRIORITY.MEDIUM)
			else if (e.getProcessPriority().value == 2)	e.setProcessPriority(PRIORITY.LOW)
			
			if(e.getProcessStatus() == STATUS.BLOCKED)
				 blockedList.add(e)
			else processList.add(e)
		}
	}

	def unblock( String program, int value ){
		def block = blockedList.find { blk ->  
			blk.getProcessName() == program
		}

		blockedList.remove(block)

		if(block.getIoRequest() == IOREQUEST.READ){
			memory.get(block.getProcessName(), block.getIoRegisters()[0]).OpCode = Core.OPCODE.DATA;
			memory.get(block.getProcessName(), block.getIoRegisters()[0]).p = value
		}

		block.setProcessInterruption(Interrupts.NoInterrupt)
		block.setProcessStatus(STATUS.READY)
		block.setIoRequest(IOREQUEST.NONE)
		block.setCursor(block.getCursor() + 1)
		processList.add(block)
	}

	def haveProcessBlockedRead()  { !filterByIORequest(IOREQUEST.READ).isEmpty()  }
	def haveProcessBlockedWrite() {	!filterByIORequest(IOREQUEST.WRITE).isEmpty() }
	def filterByIORequest(IOREQUEST ioRequest) {
		blockedList.toArray().findAll { block ->
			block.getIoRequest() == ioRequest
		}
	}

	def peek() 								{ processList.peek()	 }
	def pollBlocked() 						{ blockedList.poll()	 }
	def newProcess(ProcessControlBlock e) 	{ processList.add(e)	 }
	def processedList()						{ processedList 		 }
	def restoreProcess()					{ processList.poll()	 }
	def haveProcessBlocked()				{ !blockedList.isEmpty() }
	def haveProcess(){ 
		!processList.isEmpty() || !blockedList.isEmpty()
	}

	public String processed(){
		def resp = ""
		processedList.each { i -> resp+="\n\t${i}"}
		return resp.replaceFirst("\n","")
	}

	@Override
	public String toString(){
		def resp = ""
		processList.each { i -> resp+="\n\t${i}"}
		blockedList.each { i -> resp+="\n\t${i}"}
		return resp.replaceFirst("\n","")
	}
}