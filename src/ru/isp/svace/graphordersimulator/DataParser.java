package ru.isp.svace.graphordersimulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataParser {
	
	private static final int fileOfAllProcedures_limit = 6;
	private static final int fileOfCalls_limit = 12;
	private static final int fileOfAnalysisTimeProcedures_limit = 7;
	private static final int fileOfAnnotationsSize_limit = 7;
	private static final int fileOfModuleLoadingTime_limit = 5;
	
	
	public static Data parse(String fileOfAllProcedures, String fileOfCalls, String fileOfAnalysisTimeProcedures,
			String fileOfAnnotationsSize, String fileOfModuleLoadingTime){
		
		HashMap<Procedure, Set<Procedure>> callMap = new HashMap<Procedure, Set<Procedure>>();
		Map<Procedure, Procedure> procedures = new HashMap<Procedure, Procedure>();
		Map<Module, Long> modules = new HashMap<Module, Long>();
		
		
		try {
			long summaryTime = 0;
			BufferedReader br = new BufferedReader(new FileReader(fileOfModuleLoadingTime));
		    String line = br.readLine();

		    while (line != null) {
		        String[] splitted = line.split(":");
		        
		    	if (splitted.length != fileOfModuleLoadingTime_limit){
		    		System.out.println("lalal");
		    		throw new IllegalStateException();
		    	}

		    	Module mod = new Module(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
		    	modules.put(mod, Long.parseLong(splitted[3]));
		        summaryTime += Long.parseLong(splitted[3]);
		    	line = br.readLine();
		    }
		    System.out.println("All module loading time: " + summaryTime);
		    br.close();
		} catch (Throwable e){
			System.out.println("Incorrect format of file " + fileOfAllProcedures + " first stage");
			System.exit(1);
		}
		
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileOfAllProcedures));
		    String line = br.readLine();

		    while (line != null) {
		        String[] splitted = line.split(":");
		    	if (splitted.length != fileOfAllProcedures_limit){
		    		throw new IllegalStateException();
		    	}
		    	Procedure proc = new Procedure(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
		    	Module mod = new Module(splitted[3], Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]));
		    	mod.setLoadingTime(modules.get(mod));
		    	proc.setModuleId(mod);
		    	procedures.put(proc, proc);
		        line = br.readLine();
		    }

		    br.close();
		} catch (Throwable e){
			System.out.println("Incorrect format of file " + fileOfAllProcedures);
			System.exit(1);
		}

		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileOfAnalysisTimeProcedures));
		    String line = br.readLine();
		    long summaryTime = 0;

		    while (line != null) {
		        String[] splitted = line.split(":");
		    	if (splitted.length != fileOfAnalysisTimeProcedures_limit){
		    		throw new IllegalStateException();
		    	}

		    	Procedure proc = new Procedure(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
		    	Module mod = new Module(splitted[3], Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]));
		    	proc.setModuleId(mod);
		    	
		    	Procedure fullProc = procedures.get(proc);
		    	if (fullProc == null){
//		    		System.out.println("File " + fileOfAllProcedures + " is not full");
//					System.exit(1);

			    	line = br.readLine();
		    		continue;
		    	}
		    	fullProc.setTimeForAnalyze(Long.parseLong(splitted[6]));
		    	fullProc.getTimeForAnalyze();
		    	procedures.put(proc, fullProc);
		    	summaryTime += Long.parseLong(splitted[6]);
		    	line = br.readLine();
		    }
		    System.out.println("All procedure analyzing time: " + summaryTime);
		    br.close();
		} catch (Throwable e){
			System.out.println("Incorrect format of file " + fileOfAllProcedures);
			System.exit(1);
		}
		
		
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileOfAnnotationsSize));
		    String line = br.readLine();

		    while (line != null) {
		        String[] splitted = line.split(":");
		    	if (splitted.length != fileOfAnnotationsSize_limit){
		    		throw new IllegalStateException();
		    	}
		    	Procedure proc = new Procedure(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
		    	Module mod = new Module(splitted[3], Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]));
		    	proc.setModuleId(mod);
		    	
		    	Procedure fullProc = procedures.get(proc);
		    	if (fullProc == null){
		    		System.out.println("File " + fileOfAllProcedures + " is not full");
			    	line = br.readLine();
		    		continue;
//					System.exit(1);
		    	}
		    	fullProc.setAnnotSize(Long.parseLong(splitted[6]));
		    	procedures.put(proc, fullProc);
		    	line = br.readLine();
		    }

		    br.close();
		} catch (Throwable e){
			System.out.println("Incorrect format of file " + fileOfAllProcedures);
			System.exit(1);
		}
		
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileOfCalls));
		    String line = br.readLine();

		    while (line != null) {
		        String[] splitted = line.split(":");
		    	if (splitted.length != fileOfCalls_limit){
		    		throw new IllegalStateException();
		    	}
		    	
		    	Procedure callerProc = new Procedure(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
		    	Module callerMod = new Module(splitted[3], Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]));
		    	callerProc.setModuleId(callerMod);
		    	Procedure fullCallerProc = procedures.get(callerProc);
		    	
		    	Procedure calledProc = new Procedure(splitted[6], Integer.parseInt(splitted[7]), Integer.parseInt(splitted[8]));
		    	Module calledProcMod = new Module(splitted[9], Integer.parseInt(splitted[10]), Integer.parseInt(splitted[11]));
		    	calledProc.setModuleId(calledProcMod);
		    	Procedure fullCalledProc = procedures.get(calledProc);
		    	
		    	if (fullCallerProc == null || fullCalledProc == null){
			    	line = br.readLine();
		    		System.out.println("File " + fileOfAllProcedures + " is not full");
		    		continue;
//					System.exit(1);
		    	}
		    	Set<Procedure> called = callMap.get(fullCallerProc);
		    	if (called == null){
		    		called = new HashSet<Procedure>();
		    		
		    	} 
		    	called.add(fullCalledProc);
	    		callMap.put(fullCallerProc, called);
	    		
		    	line = br.readLine();
		    }

		    br.close();
		} catch (Throwable e){
			System.out.println("Incorrect format of file " + fileOfAllProcedures);
			System.exit(1);
		}
		
		
		Data data = new Data();
		
		data.setCallMap(callMap);
		data.setModules(modules.keySet());
		data.setProcedures(new HashSet<Procedure>(procedures.values()));
		return data;
	}
}
