package ru.isp.svace.graphordersimulator;

import java.util.HashMap;
import java.util.Map;

public class ModuleLoader {
	
	private Map<Module, ModStat> loadedModules = new HashMap<Module, ModStat>();
	//TODO: write the simulation of cache ant get information logs for cache
	
	private class ModStat {
		public ModStat(long l) {
			timeForFinishLoading = l;
		}

		private long timeForFinishLoading;
	}
	
	/**
	 * 
	 * @param mod
	 * @return time of finish module loading
	 */
	public long getModuleFromCache(Module mod, long curTime){
		if (!loadedModules.containsKey(mod)){
			mod.setLoaded(curTime + mod.getLoadingTime());
			loadedModules.put(mod, new ModStat(curTime + mod.getLoadingTime()));
		}
		return loadedModules.get(mod).timeForFinishLoading;
	}
}
