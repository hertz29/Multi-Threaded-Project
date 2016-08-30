package REITSimulationObjects;


/**
 * 
 * @author Omer&Idan
 * This class has 2 uses. It saves the quantity of the materials according to a material name.
 * use 1) In Warehouse this class is being used as a collection of a certain material.
 * use 2) Also can be used as a material to be used by a maintenance person to fix assets.
 *  
 */
class RepairMaterial {

	private String fMaterialName;
	private int fMaterialQuantity;

	public RepairMaterial(){
		fMaterialName = "";
		fMaterialQuantity = 0;
	}

	public RepairMaterial(String name, int quantity){
		fMaterialName = name;
		fMaterialQuantity = quantity;

	}
	/**
	 * @param quantity
	 * Retrieve the right quantity of a certain material stored in the Warehouse.
	 * **This function is thread safe!
	 */
	public synchronized void acquireMaterial(int quantity){
			fMaterialQuantity -= quantity; 
	}

	public String getMaterialName(){
		return fMaterialName;
	}

	public int getQuantity() {
		return fMaterialQuantity;
	}
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append(fMaterialName);
		s.append(" quantity is: ");
		s.append(fMaterialQuantity);
		return s.toString();
	}

}