package sisop.oliveiracley.processor.process

import sisop.oliveiracley.processor.process.Interrupts
import sisop.oliveiracley.processor.Memory
import sisop.oliveiracley.processor.Core
import sisop.oliveiracley.processor.CPU

import java.util.PriorityQueue
import java.util.ArrayList
import java.util.Queue

class ProcessManager {

	private static final CPU cpu 	= 	CPU.getInstance()
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
		if(e.getProcessStatus() == STATUS.DONE){
			println "FINISH PROCESS:: ${e.getProcessName()}"
			processedList.add(e)
			cpu.output()
		} else {
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
			memory.get(block.getProcessName(), block.getIoRegisters()[1]).OpCode = Core.OPCODE.DATA;
			memory.get(block.getProcessName(), block.getIoRegisters()[1]).p = value
		}

		block.setProcessInterruption(Interrupts.NoInterrupt)
		block.setProcessStatus(STATUS.READY)
		block.setIoRequest(IOREQUEST.NONE)
		processList.add(block)
	}

	def kill() {
		processList.removeAll()
		blockedList.removeAll()
	}
	def kill(int pid) {
		kill(processList.find { block -> block.getProcessId() == pid })
		kill(blockedList.find { block -> block.getProcessId() == pid })
	}
	def killProcess(String program) {
		kill(processList.find { block -> block.getProcessName() == program })
		kill(blockedList.find { block -> block.getProcessName() == program })
	}
	def void kill(ProcessControlBlock block) {
		processList.remove(block)
		blockedList.remove(block)
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
	def restoreProcess()					{ processList.poll()	 }
	def haveProcessBlocked()				{ !blockedList.isEmpty() }
	def haveProcess() { 
		!processList.isEmpty() || !blockedList.isEmpty()
	}
	def processedList() { 
		def list = processedList.clone()
		processedList = new LinkedList()
		list
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