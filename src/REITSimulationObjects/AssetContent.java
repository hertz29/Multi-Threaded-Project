package REITSimulationObjects;


/**
 * 
 * @author Omer&Idan 
 * This class holds all the information of a certain asset content 
 *
 */
class AssetContent {

	private String fItemName;
	private double fHealth;
	private double fRepairCostMultiplier;


	public AssetContent(){
		fHealth = 100;
		fItemName ="";
		fRepairCostMultiplier = 0;
	}

	public AssetContent(String newItemName, double itemRepairCostMultiplier){
		fItemName = newItemName;
		fRepairCostMultiplier = itemRepairCostMultiplier;
		fHealth=100;
	}

	public double calculateRepairCostMultiplier(){
		return (100-fHealth)*fRepairCostMultiplier;
	}

	public void repairedAsset(){
		fHealth = 100;
	}

	public boolean isHealthy(){
		return fHealth>=65;
	}

	public void updateHealth(double damage) {
		if(fHealth - damage < 0){
			fHealth = 0;
		}
		else{
			fHealth -= damage;
		}
	}

	public String getAssetContentName() {
		return fItemName;
	}

	
	public void printAssetContent() {
		System.out.println("----Content name: " + fItemName);
		System.out.println("----Content cost to repair: " + fRepairCostMultiplier);	
	}
	
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append(fItemName);
		s.append(", repair cost is: ");
		s.append(fRepairCostMultiplier);
		return s.toString();
	}
}

