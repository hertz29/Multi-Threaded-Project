package REITSimulationObjects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
/**
 * 
 * @author Omer&Idan
 * 
 *This class holds all the information of the REIT financial statistics 
 */
class Statistics {
	private double fTotalIncome;
	private double fMoneyGained;
	private long fRepairCost;
	private Vector<RentalRequest> fFinishedRentals;
	private HashMap<String, RepairMaterial> fUsedMaterialMap;
	private HashMap<String,RepairTool> fUsedToolMap;
	
	public Statistics(){
		fMoneyGained = 0;
		fUsedMaterialMap = new HashMap<String, RepairMaterial>();
		fUsedToolMap = new HashMap<String,RepairTool>();
	}
	
	private void calculateMoneyGained(){
		Iterator<RentalRequest> it = fFinishedRentals.iterator();
		while(it.hasNext()){
			fMoneyGained += it.next().calculateCost();
		}
	}
	
	public void getTotalIncome(){
		calculateMoneyGained();
		fTotalIncome = fMoneyGained - fRepairCost;
	}
	
	public void applyFinishedRentalsList(Vector<RentalRequest> finishedRentals){
		fFinishedRentals = finishedRentals;
	}
	
	public void increaseIncome(double money){
		fMoneyGained += money;
	}

	public synchronized void addToolsUsed(RepairTool toolUsed) {
		if(fUsedToolMap.containsKey(toolUsed.getToolName())){
			RepairTool tempTool = fUsedToolMap.get(toolUsed.getToolName());
			fUsedToolMap.put(tempTool.getToolName(), new RepairTool(toolUsed.getToolName(), toolUsed.getQuantity() + tempTool.getQuantity()));
		}
		else{
			fUsedToolMap.put(toolUsed.getToolName(), toolUsed);
		}
	}


	public synchronized void addMaterialsUsed(RepairMaterial materialUsed) {
		if(fUsedMaterialMap.containsKey(materialUsed.getMaterialName())){
			RepairMaterial tempMaterial = fUsedMaterialMap.get(materialUsed.getMaterialName());
			fUsedMaterialMap.put(tempMaterial.getMaterialName(), new RepairMaterial(materialUsed.getMaterialName(), materialUsed.getQuantity() + tempMaterial.getQuantity()));
		}
		else{
			fUsedMaterialMap.put(materialUsed.getMaterialName(), materialUsed);
		}
	}
	
	public String toString(){
		calculateMoneyGained();
		getTotalIncome();
		StringBuilder s = new StringBuilder();
		s.append("-------------------Simulation Statistics!----------------------");
		s.append("\n");
		s.append(getStatisticsdetails());
		s.append("\n");
		s.append(getFinishedRentalRequest());
		s.append("\n");
		s.append(getToolsUsed());
		s.append("\n");
		s.append(getMaterialsUsed());
		return s.toString();
	}

	private String getFinishedRentalRequest(){
		StringBuilder s = new StringBuilder();
		s.append("Finished Rental Request Information:");
		s.append("\n");
		Iterator<RentalRequest> it = fFinishedRentals.iterator();
		while(it.hasNext()){
			s.append(it.next());
		}
		return s.toString();
	}	
	
	private String getStatisticsdetails(){
		StringBuilder s = new StringBuilder();
		s.append("Simulation Total Income:");
		s.append(fTotalIncome);
		s.append("\n");
		s.append("Simulation Money Gained:");
		s.append(fMoneyGained);
		s.append("\n");
		s.append("Simulation Repair Cost:");
		s.append(fRepairCost);
		s.append("\n \n");
		return s.toString();
	}
	
	private String getToolsUsed(){
		StringBuilder s = new StringBuilder();
		s.append("Tools Used:");
		s.append("\n");
		for(Map.Entry<String, RepairTool> entry: fUsedToolMap.entrySet()){
			s.append(entry.getValue());
			s.append("\n");
		}
		return s.toString();
	}
	
	private String getMaterialsUsed(){
		StringBuilder s = new StringBuilder();
		s.append("Materials Used:");
		s.append("\n");
		for(Map.Entry<String, RepairMaterial> entry: fUsedMaterialMap.entrySet()){
			s.append(entry.getValue());
			s.append("\n");
		}
		return s.toString();
	}
	
	public void increaseRepairCost(long repairCost) {
		fRepairCost += repairCost;
		
	}

	
	
}
