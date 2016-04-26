package ru.isp.svace.graphordersimulator;

public interface Simulator {
	
	void simulateAnalysis(String outLog);

	void init(ThreadPool pool, GraphOrder order, ProcedureSelector procSelector, Data data);

}
