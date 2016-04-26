package ru.isp.svace.graphordersimulator;

import java.util.Iterator;

public interface GraphOrder {

	public void init(Data data);
	
	public Procedure getAnyNext();
	
	public void setAnalyzed(Procedure proc, long l);

	public boolean isFinished();

	public boolean hasNext();

	public Iterator<Procedure> getNextOfModule(Module moduleId);
	
}
