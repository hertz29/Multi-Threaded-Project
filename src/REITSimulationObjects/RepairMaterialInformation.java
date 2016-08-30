package REITSimulationObjects;


class RepairMaterialInformation {

	private String fMaterialName;
	private int fMaterialQuantity; 

	public RepairMaterialInformation(){
		fMaterialName = "";
		fMaterialQuantity = 0;
	}

	public RepairMaterialInformation(String materialName, int materialQuantity){
		fMaterialName = materialName;
		fMaterialQuantity = materialQuantity;
	}



	public String getMaterialName() {
		return fMaterialName;
	}
	
	public int getMaterialQuantity(){
		return fMaterialQuantity;
	}
}
