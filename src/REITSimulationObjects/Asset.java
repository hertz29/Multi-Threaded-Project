package REITSimulationObjects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utils.ReitLogger;

/**
 * 
 * @author
 * This class holds all the information of a certain asset
 *
 */

class Asset implements Comparable<Asset>{

	private String fAssetName;
	private String fType; 
	private Location fLocation;
	private ArrayList<AssetContent> fAssetContentList;
	private String fStatus;
	private int fCostPerNight;
	private int fSize;
	private int fAssetHealth;
	private ArrayList<RepairToolInformation> fRepairToolInfo;
	private ArrayList<RepairMaterialInformation> fRepairMaterialInfo;
	private int fTimesRepaired;

	public Asset(){
		fAssetName = "";
		fType = "";
		fLocation = null;
		fAssetContentList = new ArrayList<AssetContent>();
		fStatus = "";
		fCostPerNight = 0;
		fSize = 0;
		fTimesRepaired = 0;
		fAssetHealth = 100;
		fRepairMaterialInfo = null;
		fRepairToolInfo = null;
	}

	public Asset(String assetName , String type, int size, int x, int y, int costPerNight){
		fAssetName = assetName;
		fType = type;
		fSize = size;
		fLocation = new Location(x,y);
		fCostPerNight = costPerNight;
		fAssetContentList = new ArrayList<AssetContent>();
		fStatus = "AVAILABLE";
		fAssetHealth = 100;
		fRepairMaterialInfo = new ArrayList<RepairMaterialInformation>();
		fRepairToolInfo = new ArrayList<RepairToolInformation>();
	}
	
/**
 * This function checks if the asset is within the restrains of type and size of the requested asset.
 * @param size
 * @param type
 * @return asset that is suitable with the restrains of the parameters. If it is not suitable, returns null.
 */
	public Asset checkAsset(int size, String type){
		synchronized(this){
			if(fType.equals(type)){
				if(size <= fSize){
					if(fStatus == "AVAILABLE"){
						fStatus = "BOOKED";
						ReitLogger.logThis("Asset named: " +fAssetName + " has been booked!", "Asset");
						return this;
					}
				}
			}
			return null;
		}
	}
	
	/**
	 * Adds an asset content object to the list of Asset contents of the Asset
	 * @param assetContentToAdd
	 */
	public void addAssetContent(AssetContent assetContentToAdd){
		fAssetContentList.add(assetContentToAdd);
	}

	public synchronized boolean isAvailable(){
		return fStatus == "AVAILABLE"; 
	}

	/**
	 * At changeAvailability, the status of the asset will change by the param status 
	 * @param status
	 */
	public synchronized void changeAvailability(String status){
		fStatus = status;
	}

	/**
	 * Overrides Comparable compareTo method, to compare and sort assets by size.
	 */
	public int compareTo(Asset assetToCompare){
		return this.fSize - assetToCompare.fSize;
	}

	/**
	 * at updateAsset, the Asset health and the Asset contents are increase by the value of damage 
	 * @param damage
	 */
	public synchronized void updateHealth(double damage) {
		if(fAssetHealth - damage < 0){
			fAssetHealth = 0;
		}
		else{
			fAssetHealth -= damage; 
		}
		freeAsset();
		Iterator<AssetContent> it = fAssetContentList.iterator();
		while(it.hasNext()){
			it.next().updateHealth(damage);
		}
		if(fAssetHealth <= 65) fStatus = "UNAVAILABLE";
	}

	/**
	 * At freeAsset, the Asset status is changed to AVAILIABLE.
	 */
	public void freeAsset() {
			fStatus = "AVAILABLE";
			Assets.notifyVacateAsset();
				ReitLogger.logThis(fAssetName + " has been vacated, Wake up all clerks!", "Asset" );

	}

	/**
	 * 
	 * @return if the Asset is UNAVAILABLE
	 */
	public boolean isUnavailable(){
		return fStatus.equals("UNAVAILABLE");
	}

	/**
	 * At occupyAsset the Asset status is changing to OCCUPIED
	 */
	public void occupyAsset() {
		fStatus = "OCCUPIED";
	}
	
	/**
	 * @return a ArrayList of string that contains all asset contents name that stored at the asset
	 */
	private ArrayList<String> getAssetContentNames(){
		Iterator<AssetContent> itr = fAssetContentList.iterator();
		ArrayList<String> assetContentNamesList = new ArrayList<String>(); 
		while(itr.hasNext()){
			assetContentNamesList.add(itr.next().getAssetContentName());
		}
		return assetContentNamesList;
	}
	/**
	 * At initializeRepairAssetInformation, the RepairToolInformation ArrayList and the 
	 * RepairMaterialInformation ArrayList is initialized, and insert the Repair information 
	 * that needs to repair the Asset  
	 * @param repairMaterialInfoMap
	 * @param repairToolInfoMap
	 */
	public void initializeRepairAssetInformation(
			HashMap<String, ArrayList<RepairMaterialInformation>> repairMaterialInfoMap,
			HashMap<String, ArrayList<RepairToolInformation>> repairToolInfoMap) {
		initializeRepairToolInfo(repairToolInfoMap);
		initializeRepairMaterialInfo(repairMaterialInfoMap);

	}

	private void initializeRepairToolInfo(
			HashMap<String, ArrayList<RepairToolInformation>> repairToolInfoMap) {
		HashMap<String, RepairToolInformation> toolInfoList = new HashMap<String, RepairToolInformation>();
		ArrayList<String> assetContentNamesList = getAssetContentNames();
		Iterator<String> it = assetContentNamesList.iterator(); 
		while(it.hasNext()){
			String tempName = it.next();
			ArrayList<RepairToolInformation> tempList = repairToolInfoMap.get(tempName);
			Iterator<RepairToolInformation> tempListItr = tempList.iterator();
			while(tempListItr.hasNext()){
				RepairToolInformation tempToolInfo = tempListItr.next();
				if(!toolInfoList.containsKey(tempToolInfo.getToolName())){
					toolInfoList.put(tempToolInfo.getToolName(), tempToolInfo);
				}
				else{
					if(tempToolInfo.getToolQuantity() > toolInfoList.get(tempToolInfo.getToolName()).getToolQuantity()){
						toolInfoList.put(tempToolInfo.getToolName(), tempToolInfo);
					}
				}
			}
		}
		convertToolMapToList(toolInfoList);

	}

	private void convertToolMapToList(
			HashMap<String, RepairToolInformation> toolInfoList) {
		for (Map.Entry<String, RepairToolInformation> entry : toolInfoList.entrySet()) {
			fRepairToolInfo.add(entry.getValue());
		}

	}

	private void initializeRepairMaterialInfo(
			HashMap<String, ArrayList<RepairMaterialInformation>> repairMaterialInfoMap) {
		HashMap<String, RepairMaterialInformation> materialInfoList = new HashMap<String, RepairMaterialInformation>();
		ArrayList<String> assetContentNamesList = getAssetContentNames();
		Iterator<String> it = assetContentNamesList.iterator(); 
		while(it.hasNext()){
			String tempName = it.next();
			ArrayList<RepairMaterialInformation> tempList = repairMaterialInfoMap.get(tempName);
			Iterator<RepairMaterialInformation> tempListItr = tempList.iterator();
			while(tempListItr.hasNext()){
				RepairMaterialInformation tempMaterialInfo = tempListItr.next();
				if(!materialInfoList.containsKey(tempMaterialInfo.getMaterialName())){
					materialInfoList.put(tempMaterialInfo.getMaterialName(), tempMaterialInfo);
				}
				else{
					int materialQuentity = materialInfoList.get(tempMaterialInfo.getMaterialName()).getMaterialQuantity()
							+ tempMaterialInfo.getMaterialQuantity();
					RepairMaterialInformation newMaterialToAdd = new RepairMaterialInformation(tempMaterialInfo.getMaterialName(),materialQuentity);
					materialInfoList.put(newMaterialToAdd.getMaterialName(), newMaterialToAdd);
				}
			}
		}
		convertMaterialMapToList(materialInfoList);

	}

	private void convertMaterialMapToList(
			HashMap<String, RepairMaterialInformation> materialInfoList) {
		for (Map.Entry<String, RepairMaterialInformation> entry : materialInfoList.entrySet()) {
			fRepairMaterialInfo.add(entry.getValue());
		}

	}

	public ArrayList<RepairToolInformation> getAssetContentToolsInfo(){
		return fRepairToolInfo;
	}

	public ArrayList<RepairMaterialInformation> getAssetContentMaterialsInfo(){
		return fRepairMaterialInfo;
	}
	/**
	 * @return the total repair cost of the Asset 
	 */
	public long calculateRepairCost() {
		long repairCost = 0;
		Iterator<AssetContent> it = fAssetContentList.iterator();
		while(it.hasNext()){
			repairCost += (long) it.next().calculateRepairCostMultiplier();
		}
		StringBuilder output = new StringBuilder();
		output.append("COST IN milliseconds =");
		output.append(repairCost);
		ReitLogger.logThis(output.toString(), "Asset");
		return repairCost;
	}
	/**
	 * update Asset health and all Asset contents health to 100% 
	 */
	public void assetRepaired() {
		fAssetHealth = 100;
		fStatus = "AVAILABLE";
		fTimesRepaired++;
		Iterator<AssetContent> itr = fAssetContentList.iterator();
		while(itr.hasNext()){
			itr.next().repairedAsset();
		}
		
		ReitLogger.logThis("Asset named " + fAssetName + "is fixed", "Asset");
	}

	public int getHealth() {
		return fAssetHealth;
	}

	public Location getLocation(){
		return fLocation;
	}

	public int getCostPerNight() {
		return fCostPerNight;
	}

	public int getTimesRepaired(){
		return fTimesRepaired;
	}

	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("Asset name is: ");
		s.append(fAssetName);
		s.append(", asset Type is: ");
		s.append(fType);
		s.append(" with ");
		s.append(fSize);
		s.append(" rooms ");
		s.append(" located at: ");
		s.append(fLocation);
		s.append(". The asset has been repaired ");
		s.append(fTimesRepaired);
		s.append("times.");
		return s.toString();
	}

	public String getAssetName() {
		return fAssetName;
	}
	
	public void printAsset(){
		System.out.println(fAssetName + " Details: ");
		System.out.println("Asset size: " + fSize);
		System.out.println("Asset type: " + fType);
		System.out.println("Asset cost per night: " + fCostPerNight);
		System.out.println("----Contents: ");
		Iterator<AssetContent> it = fAssetContentList.iterator();
		while(it.hasNext()) it.next().printAssetContent();
	}
}
