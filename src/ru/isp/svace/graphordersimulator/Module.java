/**
 * 
 */
package ru.isp.svace.graphordersimulator;

/**
 * @author volta
 *
 */
public class Module {
	
	private long loadingTime = -1;
	private int hashCode;
	private final String origName;
	protected final int fileId;
	private boolean isLoaded = false;
	private long timeOfFinishLoading = -1;
	
	public Module(String origName, int fileId, int hashCode) {
		this.fileId = fileId;
		this.origName = origName;
		this.hashCode = hashCode;
	}


	public String getName() {
		return origName;
	}
		
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	public void setLoadingTime(long time){
		if (loadingTime != -1){
			System.out.println("Error! time for loading of module is initialized yet");
			throw new IllegalStateException();
		}
		loadingTime = time;
//		loadingTime = 0;
	}
	
	public long getLoadingTime (){
		if (loadingTime == -1){
			System.out.println("Error! time for loading of module is not initialized");
			throw new IllegalStateException();
		}
		return loadingTime;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Module))
			return false;

		Module that = (Module)obj;
		return fileId==that.fileId
			&& Utils.equalsOrNull(origName, that.origName);
	}
	
	public boolean isLoaded(){
		return isLoaded;
	}
	
	public void setLoaded(long timeOfFinishLoading){
		if (isLoaded){
			System.out.println("Error: module" + this.getName() + " loaded!");
			throw new IllegalStateException();
		}
		isLoaded = true;
		this.timeOfFinishLoading = timeOfFinishLoading;
	}


	/**
	 * @return the timeOfFinishLoading
	 */
	public long getTimeOfFinishLoading() {
		if (!isLoaded){
			System.out.println("Error: module" + this.getName() + " is not loaded!");
			throw new IllegalStateException();
		}
		return timeOfFinishLoading;
	}
	
}
