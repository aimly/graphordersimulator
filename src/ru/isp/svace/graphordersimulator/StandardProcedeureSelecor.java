package ru.isp.svace.graphordersimulator;

public class StandardProcedeureSelecor implements ProcedureSelector {
	
	private final GraphOrder graphOrder;

	public StandardProcedeureSelecor(GraphOrder order){
		this.graphOrder = order;
	}
	
	@Override
	public Procedure getNext() {
		return graphOrder.getAnyNext();
	}

}
