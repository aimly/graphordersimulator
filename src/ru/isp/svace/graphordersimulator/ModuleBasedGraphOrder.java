package ru.isp.svace.graphordersimulator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ModuleBasedGraphOrder implements GraphOrder {
		
	@Override
	public void init(Data data) {
		setOfAllProcedures.addAll(data.getProcedures());
		this.registerSimpleProcedures(setOfAllProcedures);

		HashMap<Procedure, Set<Procedure>> callMap = data.getCallMap();
		
		for(Procedure callerId : callMap.keySet()) {
			for (Procedure calleeId : callMap.get(callerId)) {
				this.addSimpleCall(callerId, calleeId);
			}
		}
		
//		checkTimeForAnalyze();
		checkAnnotSize();

		this.finishBuilding();
		
	}
	
	private void checkTimeForAnalyze(){
		System.out.println("Chech time");
		for (Procedure proc: setOfAllProcedures){
			System.out.println("procedure name: " + proc.getName());
			long time = proc.getTimeForAnalyze();
			if (time == -1)
				System.out.println("lol");
		}
	}
	
	private void checkAnnotSize(){
		
	}
	
	/** The map from procedure properties to their module clusters */
	private final Map<Procedure, ModuleOrder> mapOfProcedureAndModule = new HashMap<>();
	
	/** The map from procedures identifiers to their properties */
	protected final Set<Procedure> setOfAllProcedures = new HashSet<Procedure>();
	
	/** Accessible for analyze procedures pool */
	protected final Set<Procedure> candidates = new HashSet<Procedure>();
	
	private final Map<Module, ModuleOrder> mapOfModNamesAndModOrders = new HashMap<Module, ModuleOrder>();
	
	private final Set<ModuleOrder> moduleOrders = new HashSet<ModuleOrder>();
	
	/** Ordered list of ModuleOrder */
	private final List<ModuleOrder> order = new LinkedList<ModuleOrder>();
	
	/** Currently analyzing procedures */
	protected final Set<Procedure> analyzing = new HashSet<Procedure>();
	
	private int sortStepsNum = 0;
	
	public class ModuleOrder {
		private final Module moduleName;

		//TODO: remove from this map when procedure won't be needed.
		private final Set<Procedure> procedures = new HashSet<Procedure>();

		private final Set<ModuleOrder> dependencies = new HashSet<ModuleOrder>();

		private final List<Procedure> candidates = new LinkedList<>();

		//private final List<CGOrderProcedureId> analyzing = CollectionUtils
		//		.createLinkedList();

		private boolean isAnalyzed = false;

		private int analyzedCount = 0;

		private ModuleOrder(Module modName) {
			this.moduleName = modName;
		}

		/**
		 * @return the moduleName
		 */
		public Module getModuleName() {
			return moduleName;
		}

		/**
		 * @param cgoproc
		 */
		public void addProcedure(Procedure proc) {
			procedures.add(proc);
		}

		/**
		 * @param moduleOrder 
		 * 
		 */
		
		public void addDependency (ModuleOrder moduleOrder){
			dependencies.add(moduleOrder);
		}
		
		public void buildDeps() {
			for(Procedure proc : procedures) {
				for (Procedure calledProc : proc.getCalled()) {
					dependencies.add(mapOfProcedureAndModule.get(calledProc));
				}
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((moduleName == null) ? 0 : moduleName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ModuleOrder other = (ModuleOrder) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (moduleName == null) {
				if (other.moduleName != null)
					return false;
			} else if (!moduleName.equals(other.moduleName))
				return false;
			return true;
		}

		private ModuleBasedGraphOrder getOuterType() {
			return ModuleBasedGraphOrder.this;
		}

		/**
		 * @return the isAnalyzed
		 */
		public boolean isAnalyzed() {
			return isAnalyzed;
		}

		/**
		 * @param isAnalyzed
		 *            the isAnalyzed to set
		 */
		public void setAnalyzed(boolean isAnalyzed) {
			this.isAnalyzed = isAnalyzed;
		}

		

	}

	private void addSimpleCall(Procedure callerId, Procedure calleeId) {
		addCall(callerId, calleeId);
	}

	public void addCall(Procedure caller, Procedure called) {
		caller.addCall(called);
	}

	private void finishBuilding() {
		buildModulesOrder();
		checkSynchronization();
	}

	private void checkSynchronization() {
		int sizeOfCandidates = 0;
		int numOfProc = 0;
		for (ModuleOrder modOrder : order){
			sizeOfCandidates += modOrder.candidates.size();
			numOfProc += modOrder.procedures.size();
		}
		
		if (sizeOfCandidates != candidates.size())
			System.out.println("An error occured! Candidate lists not synchronized");
		
		if (numOfProc != setOfAllProcedures.size())
			System.out.println("An error occured! Procedure lists not synchronized");

		
	}

	private void buildModulesOrder() {
		sortModuleOrder();
		buildCandidatesLists();
		
	}
	
	private void buildCandidatesLists() {
		for (ModuleOrder modOrder : order) {
			for(Procedure en : modOrder.procedures) {
				Procedure proc = en;
			//for (Procedure proc : modOrder.procedures) {
				if (proc.getCalled().isEmpty()) {
					modOrder.candidates.add(proc);
					this.candidates.add(proc);
				}
			}
		}
	}

	
	private void sortModuleOrder() {
		System.out.println("Starting modules sorting");
		while (!moduleOrders.isEmpty()) {
			addNextLevelOfModulesToOrder();
			sortStepsNum++;
			if (sortStepsNum%500 == 0){
				System.out.println("Step " + sortStepsNum);
//				System.out.println(order.size() + " modules sorted");
				System.out.println(moduleOrders.size() + " modules not sorted");
			}
		}

	}
	
	private void addNextLevelOfModulesToOrder() {
		List<ModuleOrder> differenseAfterStep = new LinkedList<ModuleOrder>();
		for (ModuleOrder modOrder : moduleOrders) {

			boolean fail = false;
			for (ModuleOrder calledModOrder : modOrder.dependencies) {
				if (moduleOrders.contains(calledModOrder)
						&& !calledModOrder.equals(modOrder)) { // TODO: need to
																// debug equals
					fail = true;
					break;
				}
			}
			if (!fail) {
				differenseAfterStep.add(modOrder);
				order.add(modOrder);
			}

		}

		// if differenseAfterStep is empty, we'll have endless cycle, cause
		// nothing is added to order
		// here we added one ModuleOrder with shortest list of ModuleOrder, that
		// is not in list now

		if (differenseAfterStep.isEmpty()) {
			List<ModuleOrder> bestOrder = chooseBestOrder();
			differenseAfterStep.addAll(bestOrder);
			order.addAll(bestOrder);
		}

		moduleOrders.removeAll(differenseAfterStep);
	}
	
	private List<ModuleOrder> chooseBestOrder() {

		assert (!moduleOrders.isEmpty());
		ModuleOrder curBestModOrder = moduleOrders.iterator().next();
		List<ModuleOrder> bestOrders = new LinkedList<ModuleOrder>();
		int bestDepsNum = moduleOrders.size();

		for (ModuleOrder modOrder : moduleOrders) {
			int curDepsNum = 0;
			for (ModuleOrder dependency : modOrder.dependencies) {
				if (moduleOrders.contains(dependency)) {
					curDepsNum++;
				}
			}
			if (curDepsNum < bestDepsNum) {
				bestDepsNum = curDepsNum;
				curBestModOrder = modOrder;
				bestOrders.clear();
				bestOrders.add(curBestModOrder);
			}
			else if (curDepsNum == bestDepsNum){
				bestOrders.add(modOrder);
			}

		}
		
//		Collections.sort(bestOrders, modComp); //According to comments from 236, there's no need to sort this list for determinism :)

		return bestOrders;
	}

	private void registerSimpleProcedures(Set<Procedure> procSet) {
		for (Procedure proc : procSet) {
			//TODO: ;lsjdfkl;dsk
			addProcToModulesOrder(proc);
		}
	}
	
	private void addProcToModulesOrder(Procedure cgoproc) {

		Module modName = cgoproc.getModuleId();
		if (mapOfModNamesAndModOrders.containsKey(modName)){
			addToModule(mapOfModNamesAndModOrders.get(modName), cgoproc);
			return;
		}

		ModuleOrder newOrder = new ModuleOrder(modName);
		mapOfModNamesAndModOrders.put(modName, newOrder);
		addToModule(newOrder, cgoproc);
		addModuleOrder(newOrder);
	}
	
	private void addToModule(ModuleOrder order, Procedure cgoproc) {
		order.addProcedure(cgoproc);
		mapOfProcedureAndModule.put(cgoproc, order);
	}
	
	private void addModuleOrder(ModuleOrder newOrder) {
		moduleOrders.add(newOrder);
	}

	public Procedure getAnyNext() {

		Iterator<ModuleOrder> it = order.iterator();
		ModuleOrder modOrder = it.next();
		assert (!isFinished());
		// TODO: I have some doubts...
		while (modOrder.candidates.isEmpty() && it.hasNext()){
			modOrder = it.next();
		}

		Iterator<Procedure> itProc = modOrder.candidates.iterator();
		if (modOrder.candidates.isEmpty() || candidates.isEmpty()){
//			System.out.println("An error!");
			return null;
//			Procedure pr = this.candidates.iterator().next();
//			Module modname = pr.getModuleId();
//			ModuleOrder o = mapOfModNamesAndModOrders.get(modname);
//			mapOfModNamesAndModOrders.get(modname).candidates.remove(pr);
//			this.candidates.remove(pr);
//			analyzing.add(pr);
//			return pr;
		}
		
		Procedure procId = itProc.next();
		
		itProc.remove();
		this.candidates.remove(procId);
		//modOrder.analyzing.add(procId);
		this.analyzing.add(procId);
		
		return procId;
	}
	
	
	public synchronized void setAnalyzed(Procedure analyzed, long curTime) {
		// Removing procedure from global set "analyzing"
		boolean wasPrev = analyzing.remove(analyzed);
		assert (wasPrev);

		// Removing procedure from local set "analyzing" in cluster
		ModuleOrder modOrder = mapOfProcedureAndModule.get(analyzed);
		//boolean wasPrevLocal = modOrder.analyzing.remove(analyzed);
		modOrder.analyzedCount++;
		//assert (wasPrevLocal);
		if (modOrder.candidates.isEmpty() && modOrder.analyzedCount == modOrder.procedures.size()){
			order.remove(modOrder);
		}

		analyzed.setAnalyzed();

		for (Procedure callerInfo : analyzed.getCallers()) {
			if (callerInfo.isCalledAnalyzed()) {
				// it's next candidate
				assert (!callerInfo.isAnalyzed());
				if (callerInfo.isAnalyzed())
					System.out.println("Error: procedure " + callerInfo.getName() + " is analyzed");

				// Adding to global set of candidates
				int size2 = candidates.size();
				candidates.add(callerInfo);
				int size1 =  mapOfProcedureAndModule.get(callerInfo).candidates.size();
				// Adding to local set of candidates
				mapOfProcedureAndModule.get(callerInfo).candidates
						.add(callerInfo);
				if (size1 == mapOfProcedureAndModule.get(callerInfo).candidates.size())
					System.out.println("Error: not added to global candidates");
				if (size2 == candidates.size())
					System.out.println("Error: not added to local candidates");
			}
		}
		MemoryStatistics.update(analyzed, curTime);
	}
	
	@Override
	public synchronized boolean isFinished() {
		return candidates.isEmpty() && analyzing.isEmpty();
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Procedure> getNextOfModule(Module moduleId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
