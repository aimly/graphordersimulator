package ru.isp.svace.graphordersimulator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;


public class BaseProcedureSelector implements ProcedureSelector {

protected final GraphOrder order;
	
	@SuppressWarnings("unused")
	private final int threadsNumber;
	
	protected final int max_candidates_size;

	private static final int FACTOR = 1500;
	
	private static final int CACHE_SIZE = 50;
	private static final int RECENTLY_USED_DISTANCE = CACHE_SIZE/2 + 1;
	private static final int NOT_USED_DISTANCE = 20 * CACHE_SIZE;
	
	public BaseProcedureSelector(GraphOrder order, 
			int threadsNumber) {
		this.threadsNumber = threadsNumber;
		this.max_candidates_size = threadsNumber * FACTOR;
		this.order = order;

	}
	
	public static ProcedureSelector create(GraphOrder order, 
//			ProcedureBuilderHelper procedureBuilderHelper, 
			int threadsNumber) {
		return new BaseProcedureSelector(order, threadsNumber);
	}

	
	private static volatile int curTimeStamp = 0;
	//private static volatile int curModuleNum = 0;
	
	private class ModuleStat {
		//module wasn't in cache and it is currently analyzed
		boolean waitLoading = false;
		int lastUseTimeStamp = 0;
		//private final int priority = curModuleNum++;
		
		public void updateTimeStamp(boolean theSameModule) {
			/*if(lastUseTimeStamp==0) {
				waitLoading = true;
			} else if(isNotUseForALongTime()) {
				//it probably is not in cache
				waitLoading = true;
			}*/
			
			if(!theSameModule)
				this.lastUseTimeStamp = curTimeStamp++;
		}
		
		//at current time module may be being added to the cache
		//it's better to choose another module
		public boolean waitLoading() {
			return waitLoading;
		}
		
		/*public int getPriority() {
			//for now simply prefer modules that were added 1st.
			return priority;
			
			//TODO: try other priorities:
			//1. prefer small modules
			//2. prefer big modules
			//3. take into accounted number of analyzed procedures.
			//4. prefer with most number of available procedures.
		}*/
		
		public boolean isRecentlyUsed() {
			return lastUseTimeStamp>0 && lastUseDistance() < RECENTLY_USED_DISTANCE;
		}
		
		/*public boolean isNotUseForALongTime() {
			return lastUseDistance() > NOT_USED_DISTANCE;
		}*/
		
		//less is better
		public int lastUseDistance() {
			return curTimeStamp - lastUseTimeStamp;
		}
	}
	
	//private final Set<IRModuleId> visitedModules = CollectionUtils.createHashSet();
	
	private final Map<Module, ModuleStat> modules = new HashMap<Module, ModuleStat>();
	
	private ModuleStat getModuleStat(Module module) {
		ModuleStat prev = modules.get(module);
		if(prev!=null)
			return prev;
		
		ModuleStat stat = new ModuleStat();
		modules.put(module, stat);
		return stat;
	}
		
	//private final Set<CGOrderProcedureId> candidates = CollectionUtils.createLinkedHashSet();
	protected final HashMultimap<Module, Procedure> candidates = HashMultimap.create();
	protected int candidatesSize = 0;
	
	//a subset of candidates that should be analyzed before any other candidates
	//private final Set<CGOrderProcedureId> firstOrderCandidates = CollectionUtils.createHashSet();
	protected final List<Procedure> firstOrderCandidates = new LinkedList<Procedure>();

	//private static final boolean DEBUG_PROCEDURE_CACHE = StaticScope.getSettings().DEBUG_PROCEDURE_CACHE.isOn();
	
	
	protected Procedure selectProcedureAndRemove(/*Set<CGOrderProcedureId> candidates*/) {
		/*if(USE_CACHE) {
			assert(!candidates.isEmpty());
			
			MaybeVal<CGOrderProcedureId> res = this.procedureBuilderHelper.selectProcedure(candidates);
			if(!res.isNothing())
				return res.getVal();
		}*/
		
		Iterator<Procedure> it = firstOrderCandidates.iterator();
		Procedure proc = it.next();
		it.remove();
		boolean wasRemoved = candidates.remove(proc.getModuleId(), proc);
		if(wasRemoved)
			--candidatesSize;
		return proc;
	}
	
	/**
	 * may return null if there no next procedure
	 * @return
	 */
	public synchronized Procedure getNext() {
		if (!this.hasNext())
			return null;

		if(!firstOrderCandidates.isEmpty()) {
			Procedure procId = selectProcedureAndRemove(/*firstOrderCandidates*/);

			ModuleStat stat = getModuleStat(procId.getModuleId());
			applyCandidate(procId, stat, true);
			return procId;
		}
		
		//if(order.hasNext() && candidates.size()<max_candidates_size) {
		//	candidates.addAll(order.getNexts(max_candidates_size-candidates.size()));
		//}
			
		while(order.hasNext() && candidatesSize<max_candidates_size) {
			Procedure next = order.getAnyNext();
			addCandidate(next);
		}
		
		Module bestModule = getBestModuleId(modules.keySet());
		//IRModuleId bestModule = getBestModuleId(candidates.keySet());
		
		//if(bestModule==null) {
		//	bestModule = candidates.keySet().iterator().next();
		//}
		
		if(bestModule!=null) {
			Iterator<Procedure> it = order.getNextOfModule(bestModule);
			while(it.hasNext()) {
				Procedure next = it.next();
				addCandidate(next);
			}	
		}
		
		Set<Procedure> procedures = bestModule==null?
				Collections.<Procedure>emptySet() : candidates.get(bestModule);
		
		if(procedures.isEmpty()){
			bestModule = getBestModuleId(candidates.keySet());
			
			if(bestModule==null) {
				bestModule = candidates.keySet().iterator().next();
			}
			
			{
				Iterator<Procedure> it = order.getNextOfModule(bestModule);
				while(it.hasNext()) {
					Procedure next = it.next();
					addCandidate(next);
				}	
			}
			
			procedures = candidates.get(bestModule);
		}
		
		Iterator<Procedure> it = procedures.iterator();
		assert(it.hasNext());
		
		Procedure nextProc = it.next();
		
		ModuleStat stat = getModuleStat(bestModule);
		
		applyCandidate(nextProc, stat, false);
		
		if(!stat.waitLoading()) {
			while(it.hasNext()) {
				Procedure procId = it.next();
				addCandidate(procId);
			}
		}
		
		return nextProc;
	}
	
	private Module getBestModuleId(Collection<Module> candidates) {
		//best module by distance of cache using
		int minDistance = NOT_USED_DISTANCE;

		Module bestModule = null;
		for(Module module : candidates) {
			ModuleStat stat = getModuleStat(module);
			if(!stat.waitLoading()) {
				if(stat.isRecentlyUsed()) {
					bestModule = module;
					break;
				}
				
				int curDistance = stat.lastUseDistance();
				
				if(bestModule==null || curDistance<minDistance) {
					bestModule = module;
					minDistance = curDistance;
				}
			}
		}
		
		return bestModule;
	}
	
	private void addCandidate(Procedure cand) {
		boolean isNew = candidates.put(cand.getModuleId(), cand);
		if(isNew)
			++candidatesSize;
		
		getModuleStat(cand.getModuleId());
	}
	
	private synchronized void applyCandidate(Procedure candidate, ModuleStat stat,
			boolean theSameModule) {
		stat.updateTimeStamp(theSameModule);
		boolean wasRemoved = candidates.remove(candidate.getModuleId(), candidate);
		//visitedModules.add(candidate.getModuleId());
		if(wasRemoved)
			--candidatesSize;

		firstOrderCandidates.remove(candidate);
	}

	
	public synchronized void workIsFinished(Procedure procId) {
		ModuleStat stat = getModuleStat(procId.getModuleId());
		stat.waitLoading = false;
		//FIXME
		order.setAnalyzed(procId, candidatesSize);
		
		if(candidatesSize<max_candidates_size) {
			//get next modules for analysis
			Iterator<Procedure> it = order.getNextOfModule(procId.getModuleId());
			while(it.hasNext()) {
				Procedure next = it.next();
				addCandidate(next);
			}
		}
		//notifyAll();
	}
	
	public GraphOrder getOrder() {
		return order;
	}
	
	/**
	 * Call before loading the module.
	 * Needed for better cache hit rate for C/C++ analysis.
	 * @param module
	 */
	public synchronized void informModuleStartLoading(Module module) {
		ModuleStat stat = getModuleStat(module);
		stat.waitLoading = true;
	}
	
	/**
	 * Call after loading the module.
	 * Needed for better cache hit rate.
	 * @param module
	 */
	public synchronized void informModuleEndLoading(Module module) {
		ModuleStat stat = getModuleStat(module);
		stat.waitLoading = false;
	}

	public synchronized void waitNext() {
		if(hasNext())
			return;

		while(!hasNext() && !order.isFinished()) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
	}
	
	public boolean hasNext() {
		//if(USE_CACHE)
		//	return order.hasNext() || !firstOrderCandidates.isEmpty();
		
		return order.hasNext() || !candidates.isEmpty()
				|| !firstOrderCandidates.isEmpty();
	}

}
