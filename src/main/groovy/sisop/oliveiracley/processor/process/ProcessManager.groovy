package sisop.oliveiracley.processor.process

import sisop.oliveiracley.processor.process.Interrupts

import java.util.PriorityQueue
import java.util.Queue

class ProcessManager {

	private Queue<ProcessControlBlock> processedList
	private Queue<ProcessControlBlock> processList
	private Queue<ProcessControlBlock> blockedList
	private static ProcessManager instance

	static ProcessManager getInstance() { 
		if (!instance) instance = new ProcessManager()
		return instance
	}

	private ProcessManager(){
		processedList = new LinkedList()
		processList = new PriorityQueue()
		blockedList = new PriorityQueue()
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


	def haveProcessBlockedRead()  { filter(IOREQUEST.READ)  }
	def haveProcessBlockedWrite() {	filter(IOREQUEST.WRITE) }
	private boolean filter(IOREQUEST ioRequest) {
		def array = blockedList.toArray().findAll { block ->
			block.getIoRequest() == ioRequest
		}
		!array.isEmpty()
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
		return resp.replaceFirst("\n","")
	}
}