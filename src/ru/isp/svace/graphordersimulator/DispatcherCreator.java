package ru.isp.svace.graphordersimulator;

public class DispatcherCreator {

	public static ProcedureSelector getDispatcher(GraphOrder order) {
		// TODO Auto-generated method stub
		return new StandardProcedeureSelecor(order);
	}

}
