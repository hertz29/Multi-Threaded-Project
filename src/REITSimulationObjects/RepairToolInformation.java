package REITSimulationObjects;

class RepairToolInformation {

	private String fToolName;
	private int fToolQuantity;
	
	public RepairToolInformation(){
		fToolName = "";
		fToolQuantity = 0;
	}
	
	public RepairToolInformation(String toolName, int toolQuantity){
		fToolName = toolName;
		fToolQuantity = toolQuantity;
	}


	public String getToolName() {
		return fToolName;
	}

	public int getToolQuantity() {
		return fToolQuantity;
	}
}
