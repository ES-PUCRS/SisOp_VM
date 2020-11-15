package sisop.oliveiracley.processor.process

import sisop.oliveiracley.processor.process.Interrupts

import java.util.PriorityQueue
import java.util.Queue

class ProcessManager {

	private Queue<ProcessControlBlock> processedList
	private Queue<ProcessControlBlock> processList
	private static ProcessManager instance

	static ProcessManager getInstance() { 
		if (!instance) instance = new ProcessManager()
		return instance
	}

	private ProcessManager(){
		processedList = new LinkedList()
		processList = new PriorityQueue()
	}


	def saveProcess(ProcessControlBlock e){
		if(e.getProcessStatus() != STATUS.READY)
			processedList.add(e)
		else{
			if 		(e.getProcessPriority().value == 3) e.setProcessPriority(PRIORITY.MEDIUM)
			else if (e.getProcessPriority().value == 2)	e.setProcessPriority(PRIORITY.LOW)
			processList.add(e)
		}
	}


	def newProcess(ProcessControlBlock e){ processList.add(e) }
	def processedList(){ processedList }
	def restoreProcess(){ processList.poll() }
	def haveProcess(){ !processList.isEmpty() }
	def peek() { processList.peek() }

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