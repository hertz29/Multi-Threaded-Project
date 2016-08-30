package REITSimulationObjects;

import java.util.HashMap;

class Warehouse {

	private HashMap<String, RepairMaterial> fMaterialMap;
	private HashMap<String,RepairTool> fToolMap;
	
	public Warehouse(){
		fMaterialMap = new HashMap<String, RepairMaterial>();
		fToolMap = new HashMap<String, RepairTool>();
	}
	/**
	 * add RepairMaterial to the warehouse
	 * @param MaterialName
	 * @param repairMaterial
	 */
	public void addMaterial(String MaterialName, RepairMaterial repairMaterial) {
		fMaterialMap.put(MaterialName, repairMaterial);
	}
	/**
	 * add RepairTool to the warehouse
	 * @param toolName
	 * @param repairTool
	 */
	public void addTool(String toolName, RepairTool repairTool) {
		fToolMap.put(toolName, repairTool);
	}
	/**
	 * Acquire RepairTool from the warehouse to the maintenance worker
	 * @param toolName
	 * @param toolQuantity
	 * @return
	 */
	public RepairTool acquireTool(String toolName, int toolQuantity){
		fToolMap.get(toolName).acquireTool(toolQuantity);
		return new RepairTool(toolName, toolQuantity);
		
	}
	/**
	 * Acquire RepairMaterial from the warehouse to the maintenance worker
	 * @param materialName
	 * @param materialQuantity
	 * @return
	 */
	public RepairMaterial acquireMaterial(String materialName,
			int materialQuantity) {
		return new RepairMaterial(materialName, materialQuantity);
	}
	/**
	 * a maintenance worker is return the acquired RepairTool to the warehouse
	 * @param toolName
	 * @param quantity
	 */
	public void releaseTool(String toolName, int quantity) {
		fToolMap.get(toolName).returnTool(quantity);
	}

}
