/**
 * 
 */
package ru.isp.svace.graphordersimulator;

/**
 * @author volta
 *
 */
public class ThreadWorker{
	private long curTimeIdle;
	private long waitTime = 0;
	private ModuleLoader modLoader;
	
	Procedure currentlyAnalysingProc = null;
	
	public ThreadWorker(ModuleLoader moduleLoader) {
		curTimeIdle = 0;
		modLoader = moduleLoader;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (! (obj instanceof ThreadWorker))
			return false;
		return curTimeIdle == ((ThreadWorker)obj).getIdleTime() ;
	}

	public long getIdleTime() {
		return curTimeIdle;
	}
	
	public void updateIdleTime(long newTime) {
		curTimeIdle = newTime;
	}
	
	public long getWaitTime() {
		return waitTime;
	}
	
	public void updateWaitTime(long newTime) {
		waitTime += newTime;
	}
	
	public void analyzeProcedure (Procedure proc){
		long timeWhenModuleFinishLoading = modLoader.getModuleFromCache(proc.getModuleId(), curTimeIdle);
		if (timeWhenModuleFinishLoading > curTimeIdle){
			updateWaitTime(timeWhenModuleFinishLoading - curTimeIdle);
			curTimeIdle = timeWhenModuleFinishLoading;
		}
		curTimeIdle += proc.getTimeForAnalyze();
		currentlyAnalysingProc = proc;
	}
	
	public void setProcAnalyzed(){
		if (currentlyAnalysingProc == null){
			System.out.println("Error! Trying to set procedure in thread analysed, but thread not contains any procedure");
			throw new IllegalStateException();
		}
		
		currentlyAnalysingProc.setAnalyzed();
		currentlyAnalysingProc = null;
	}
	
	public Procedure getCurrentlyAnalyzingProc(){
		return currentlyAnalysingProc;
	}
	
}
