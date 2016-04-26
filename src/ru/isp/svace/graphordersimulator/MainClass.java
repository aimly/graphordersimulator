package ru.isp.svace.graphordersimulator;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			exceptionMain(args);
		} catch (Throwable e){
			e.printStackTrace();
		}
	}

	private static void exceptionMain(String[] args) {
		
		// TODO: parse arguments
		String fileOfAllProcedures = "/home/volta/work/statistic/cgo-data/build-i386-pc-linux-gnu-all-CGOProcedures.log";
		String fileOfCalls = "/home/volta/work/statistic/cgo-data/build-i386-pc-linux-gnu-all-CGOProcedure-calls.log";
		String fileOfAnalysisTimeProcedures = "/home/volta/work/statistic/cgo-data/build-i386-pc-linux-gnu-procedure-analyze-time.log";
		String fileOfAnnotationsSize = "/home/volta/work/statistic/cgo-data/build-i386-pc-linux-gnu-procedure-annot-size.log";
		String fileOfModuleLoadingTime = "/home/volta/work/statistic/cgo-data/build-i386-pc-linux-gnu-module-loading-time.log";
		// TODO: parse arguments
		String outLog = "/home/volta/work/myOutfile.txt";
		// TODO: parse arguments
		ModuleLoader modLoader = new ModuleLoader();
		ThreadPool pool = new ThreadPool(32, modLoader);
		// 4 thr - 224 s in program, 230 s real
		// 8 thr - 217 s in program, 
		// 2 thr - 266 s in program, 
		GraphOrder order = OrderCreator.getOrder();
		
		Data data = DataParser.parse(fileOfAllProcedures, fileOfCalls, fileOfAnalysisTimeProcedures,
				fileOfAnnotationsSize, fileOfModuleLoadingTime);
		
		order.init(data);
		
		ProcedureSelector procSelector = DispatcherCreator.getDispatcher(order);
		
		Simulator simulator = new StandardAnalyzeSimulator();
		
		simulator.init(pool, order, procSelector, data);
		
		simulator.simulateAnalysis(outLog);
		
	}

}
