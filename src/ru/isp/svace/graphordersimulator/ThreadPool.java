/**
 * 
 */
package ru.isp.svace.graphordersimulator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author volta
 *
 */
public class ThreadPool {
	
	private final int threadNum;
	
	private class ThreadComparator implements Comparator<ThreadWorker>{
		public int compare(ThreadWorker a, ThreadWorker b) {
			return a.getIdleTime() - b.getIdleTime() > 0 ? 1 : -1;
		}
	}
	
	private TreeSet<ThreadWorker> threadSet = new TreeSet<ThreadWorker>(new ThreadComparator());
	
	public ThreadPool(int threadNum, ModuleLoader modLoader) {
		if (threadNum <= 0){
			System.out.println("incorrect input: threadNum must be greater than 0");
			System.exit(1);
		}
		
		this.threadNum = threadNum;
		
		for (int i = 0; i < threadNum; i++)
			threadSet.add(new ThreadWorker(modLoader));
		
	}
	
	public Set<ThreadWorker> getNextAvailableThreads(){
		checkThreadNum();
		
		Set<ThreadWorker> threads = new HashSet<ThreadWorker>();
		ThreadWorker thread = threadSet.pollFirst();
		threads.add(thread);
		while (!threadSet.isEmpty() && threadSet.first().equals(thread)){
			ThreadWorker nextThr = threadSet.pollFirst();
			threads.add(nextThr);
		}
		
		return threads;
	}
	
	public void updatePool(Set<ThreadWorker> threads){
		threadSet.addAll(threads);
	
		checkThreadNum();
	}

	public void waitForNextAvailableProcedure(Set<ThreadWorker> threads) {
		ThreadWorker thread = threadSet.pollFirst();
		long time = thread.getIdleTime();
		Iterator<ThreadWorker> it = threads.iterator();
		while (it.hasNext()){
			ThreadWorker nextThread = it.next();
			nextThread.updateWaitTime(thread.getIdleTime() - nextThread.getIdleTime());
			nextThread.updateIdleTime(time);
		}
		threadSet.addAll(threads);
		threadSet.add(thread);
		
		checkThreadNum();
	}
	
	private void checkThreadNum(){
		if (threadNum != threadSet.size()){
			System.out.println("We lost some threads after iteration! threadNum = " + threadNum + "; threadSet.size() = " + threadSet.size());
			throw new IllegalStateException();
		}
	}
}
