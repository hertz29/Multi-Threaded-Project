package REITSimulationObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import utils.ReitLogger;

/**
 * 
 * @author us
 * This class holds a collection of assets and functions on them
 *
 */
class Assets {

	private ArrayList<Asset> fAssetsList;
	private static Object waitToAsset = new Object();

	public Assets(){
		fAssetsList = new  ArrayList<Asset>();
	}
	/** Return a list of asset. An asset will contained at the list only if the asset health
	 * is below 65% 
	 * @param none
	 * @return list of damaged assets
	 */
	public ArrayList<Asset> retrieveDamagedAssets(){
		StringBuilder output = new StringBuilder();
		output.append("Damage Assets is");
		ArrayList<Asset> queueToReturn = new ArrayList<Asset>(fAssetsList.size());
		Iterator<Asset> it = fAssetsList.iterator();
		while(it.hasNext()){
			Asset tempAsset = it.next();
			if(tempAsset.isUnavailable()){
				queueToReturn.add(tempAsset);
				output.append("[");
				output.append(tempAsset.getAssetName());
				output.append("]");
			}
		}
		ReitLogger.logThis(output.toString(), "Assets");
		return queueToReturn;
	}

	/**
	 * Return an Asset with Asset Type that equals to assetType, and the closest size (only 
	 * if Asset size >= assetSize).
	 * if not fount - wait.
	 * @param assetSize
	 * @param assetType
	 * @return Asset that fits the parameters
	 */
	
	public Asset getRequiredAsset(int assetSize, String assetType){
		Asset requiredAsset = null; 
		boolean found = false;
		Iterator<Asset> it = fAssetsList.iterator();
		while(it.hasNext() && !found){
			requiredAsset = it.next().checkAsset(assetSize, assetType);
			if(requiredAsset != null){
				found = true;
			}
		}
		if(found == false){
			synchronized(waitToAsset){
				try {
					waitToAsset.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return requiredAsset;
	}

	public void addAsset(String assetName, int assetSize, String assetType, Asset assetToAdd) {
		fAssetsList.add(assetToAdd);
	}

	public void sortAssets() {
		Collections.sort(fAssetsList);
	}

	public void initializeRepairAssetInformation(
			HashMap<String, ArrayList<RepairMaterialInformation>> repairMaterialInfoMap,
			HashMap<String, ArrayList<RepairToolInformation>> repairToolInfoMap) {

		Iterator<Asset> it = fAssetsList.iterator();
		while(it.hasNext()){
			it.next().initializeRepairAssetInformation(repairMaterialInfoMap, repairToolInfoMap);
		}
	}
	
	/**
	 * Notifies all RunnableClerk threads that an asset has been vacated.
	 */
	public static void notifyVacateAsset() {
		synchronized (waitToAsset) {
			waitToAsset.notify();
		}
	}
	
	public void printAssets(){
		Iterator<Asset> it = fAssetsList.iterator();
		while(it.hasNext()){
			it.next().printAsset();
			System.out.println();
			System.out.println();
		}
	}
}
