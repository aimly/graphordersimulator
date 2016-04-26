package ru.isp.svace.graphordersimulator;

import java.util.List;

public class MemoryStatistics {
	
	private static long currentNonFreeMem = 0;
	private static long maxNonFreeMem = 0;
	private static long currentTime = 0;
	private static double integral = 0.0;
//	private static String outlog = "";
	
	public static void update (Procedure proc, long curTime){
		integral += (curTime - currentTime)*currentNonFreeMem/(1000.0*1024.0);
		currentTime = curTime;
		if (!proc.getCallers().isEmpty())
			currentNonFreeMem += proc.getAnnotationSize();
		List<Procedure> called = proc.getCalled();
		for (Procedure calledProc: called){
			if (calledProc.isCallerAnalyzed()){
				currentNonFreeMem -= calledProc.getAnnotationSize();
			}
		}
		if (currentNonFreeMem > maxNonFreeMem)
			maxNonFreeMem = currentNonFreeMem;
//		System.out.println("current non-free memory: " + currentNonFreeMem);
	}
	
	public static void printMemInfo(){
		System.out.println("Memory integral (kb*sec): " + integral);
		System.out.println("Max memory (kb): " + (double)integral/1024.0);
	}
}
