package ru.isp.svace.graphordersimulator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Data {

	private HashMap<Procedure, Set<Procedure>> callMap = new HashMap<Procedure, Set<Procedure>>();
	private Set<Procedure> procedures;
	private Set<Module> modules = new HashSet<Module>();


	/**
	 * @return the callMap
	 */
	public HashMap<Procedure, Set<Procedure>> getCallMap() {
		return callMap;
	}
	/**
	 * @param callMap the callMap to set
	 */
	public void setCallMap(HashMap<Procedure, Set<Procedure>> callMap) {
		this.callMap = callMap;
	}
	/**
	 * @return the procedures
	 */
	public Set<Procedure> getProcedures() {
		return procedures;
	}
	/**
	 * @param procedures2 the procedures to set
	 */
	public void setProcedures(Set<Procedure> procedures2) {
		this.procedures = procedures2;
	}
	/**
	 * @return the modules
	 */
	public Set<Module> getModules() {
		return modules;
	}
	/**
	 * @param set the modules to set
	 */
	public void setModules(Set<Module> set) {
		this.modules = set;
	}
	
	
}
