/**
 * 
 */
package ru.isp.svace.graphordersimulator;

import java.util.LinkedList;
import java.util.List;

/**
 * @author volta
 *
 */
public class Procedure {
	
	private final String name;
	
	private final int valId;
	
	private final int hashcode;
	
	private Module moduleId = null;
	
	private long timeForAnalyze = -1;
	
	private long annotationSize = -1;
	
	private LinkedList<Procedure> caller = new LinkedList<Procedure>();
	
	private LinkedList<Procedure> called = new LinkedList<Procedure>();
	
	boolean isAnalyzed = false;
	
	public Procedure (String procName, int valueId, int hashCode){
		name = procName;
		valId = valueId;
		hashcode = hashCode;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isAnalyzed(){
		return isAnalyzed;
	}
	
	public void setAnalyzed (){
		isAnalyzed = true;
	}
	
	public void setTimeForAnalyze(long time){
		if (timeForAnalyze != -1){
			System.out.println("Error! time for analyze of procedure " + this.getName() + " is initialized yet");
			throw new IllegalStateException();
		}
		timeForAnalyze = time;
	}
	
	public long getTimeForAnalyze (){
		if (timeForAnalyze == -1){
			System.out.println("Error! time for analyze of procedure " + this.getName() + " is not initialized");
			//FIXME
			timeForAnalyze = 1000;
//			throw new IllegalStateException();
		}
		return timeForAnalyze;
	}
	
	
	public void setAnnotSize(long time){
		if (annotationSize != -1){
			System.out.println("Error! Annotation size of procedure " + this.getName() + " is initialized yet");
			throw new IllegalStateException();
		}
		annotationSize = time;
	}
	
	public long getAnnotationSize (){
		if (annotationSize == -1){
			System.out.println("Error! Annotation size of procedure " + this.getName() + " is not initialized");
			//FIXME
			annotationSize = 1;
//			throw new IllegalStateException();
		}
		return annotationSize;
	}
	
	public void setModuleId(Module modId){
		if (moduleId != null){
			System.out.println("Error! Module of procedure " + this.getName() + " is initialized yet");
			throw new IllegalStateException();
		}
		moduleId = modId;
	}
	
	public Module getModuleId (){
		if (moduleId == null){
			System.out.println("Error! Module of procedure " + this.getName() + " is not initialized");
			throw new IllegalStateException();
		}
		return moduleId;
	}

	public List<Procedure> getCalled() {
		return called;
	}

	public void addCall(Procedure calledInfo) {
		this.called.add(calledInfo);
		calledInfo.addCaller(this);
	}

	private void addCaller(Procedure procedure) {
		this.caller.add(procedure);
	}

	public List<Procedure> getCallers() {
		return caller;
	}
	
	public boolean isCalledAnalyzed() {
		for(Procedure calledInfo : called) {
			if(!calledInfo.isAnalyzed())
				return false;
		}
		return true;
	}
	

	public boolean isCallerAnalyzed() {
		for(Procedure callerInfo : caller) {
			if(!callerInfo.isAnalyzed())
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		//do not use name for performance
		//moduleId (name.hashCode() & 0x00FF00FF)
		//return HashUtils.hash(moduleId, valId);
		//do not use module id because of nondeterministic behaviour on some build systems
		return this.hashcode^2;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if(!(obj instanceof Procedure))
			return false;

		Procedure that = (Procedure)obj;

		//do not use name for performance
		return (this.name.equals(that.name))
				&& (this.valId==that.valId)
				&& (this.getModuleId().equals(that.getModuleId()));
	}



}
