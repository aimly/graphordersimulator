package ru.isp.svace.graphordersimulator;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StandardAnalyzeSimulator implements Simulator {
	
	private GraphOrder graphOrder;
	private ThreadPool threadPool;
	private ProcedureSelector procedureSelector;
	private Data data;
	
	@Override
	public void init(ThreadPool pool, GraphOrder order, ProcedureSelector procSelector, Data data) {
		graphOrder = order;
		threadPool = pool;
		procedureSelector = procSelector;
		this.data = data;
	}

	@Override
	public void simulateAnalysis(String outLog) {
		List<Procedure> procList = new LinkedList<Procedure>();
		int globalNum = 0;
		while (!graphOrder.isFinished()){
			Set<ThreadWorker> threads = threadPool.getNextAvailableThreads();
			for (ThreadWorker thread : threads){
				Procedure procId = thread.getCurrentlyAnalyzingProc();
				if (procId != null){
					graphOrder.setAnalyzed(procId, thread.getIdleTime());
					thread.setProcAnalyzed();
				}
			}
			if (graphOrder.isFinished()){

				threadPool.updatePool(threads);
				break;
			}
			
			Procedure procId = null;
			if (procList.isEmpty()){
				procId = procedureSelector.getNext();
				if (procId != null){
					procList.add(procId);
//					System.out.println("Get procedure: \"" + procId.getName() + "\" " + ++globalNum);
				}
				
			} else
				procId = procList.get(0);
			
			if (procId == null){
				threadPool.waitForNextAvailableProcedure(threads);
				continue;
			} else {
				ThreadWorker workingThread = threads.iterator().next();
//				System.out.println("Analyzing procedure: \"" + procId.getName() + "\" " + ++globalNum);
				workingThread.analyzeProcedure(procId);
				procList.clear();
				threadPool.updatePool(threads);
			}
		}
		
		Set<ThreadWorker> threads = threadPool.getNextAvailableThreads();
		long fullWorkTime = 0;
		long fullTime = 0;
		int num = 0;
		for (ThreadWorker thr: threads){
			System.out.println("Thread number " + num);
//			System.out.println("Thread wait time " + thr.getWaitTime());
//			System.out.println("Thread work finish time " + thr.getIdleTime());
			System.out.println("Wait time part: " + ((float)thr.getWaitTime()/(float)thr.getIdleTime()));
			fullWorkTime += thr.getIdleTime() - thr.getWaitTime();
			fullTime += thr.getIdleTime();
			num++;
		}
		System.out.println("Full useful work time: " + fullWorkTime);
		System.out.println("Full work time part: " + (float)fullWorkTime/(float)fullTime);
		System.out.println("Full work time: " + threads.iterator().next().getIdleTime());
		MemoryStatistics.printMemInfo();
	}
//
//	Memory integral (kb*sec): 1330723.386734375
//	Max memory (kb): 1299.5345573577881


}

//16 - 533408