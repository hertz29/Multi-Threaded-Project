package REITSimulationObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import utils.ReitLogger;


/**
 * Represents a maintenance request. 
 * The maintenance worker role is to fix an asset according the REIT office (management) commend.
 * @author Omer&Idan
 */
class RunnableMaintenanceRequest implements Runnable {

	private Asset fAssetToRepair;
	private Warehouse fWarehouse;	
	private Semaphore fSemaphore;
	private Statistics fStatistics;
	private CountDownLatch fMaintenanceWorkersShiftClock;
	private ArrayList<RepairTool> fRepairTools;
	private ArrayList<RepairMaterial> fRepairMaterials;
	private ArrayList<RepairToolInformation> fRepairToolsInformation;
	private ArrayList<RepairMaterialInformation> fRepairMaterialsInformation;

	public RunnableMaintenanceRequest(){
		fAssetToRepair = null;
		fWarehouse = null;
		fRepairMaterials = null;
		fRepairTools = null;
		fRepairMaterialsInformation = null;
		fRepairToolsInformation = null;
	}

	public RunnableMaintenanceRequest(Warehouse warehouse, Asset damagedAsset, Semaphore semaphore, 
			CountDownLatch maintenanceWorkersShiftClock, Statistics statistics){
		fWarehouse = warehouse;
		fAssetToRepair = damagedAsset;
		fSemaphore = semaphore;
		fStatistics = statistics;
		fMaintenanceWorkersShiftClock = maintenanceWorkersShiftClock;
		fRepairMaterials = new ArrayList<RepairMaterial>();
		fRepairTools = new ArrayList<RepairTool>();
		fRepairMaterialsInformation = new ArrayList<RepairMaterialInformation>();
		fRepairToolsInformation = new ArrayList<RepairToolInformation>();
	}

	private void initializeRepairInformationLists(){
		fRepairToolsInformation = fAssetToRepair.getAssetContentToolsInfo();
		fRepairMaterialsInformation = fAssetToRepair.getAssetContentMaterialsInfo();
	}
	
	
	public void run(){
		ReitLogger.logThis("Maintenance person is going to repair: " + fAssetToRepair.getAssetName(), "RunnableMaintenanceRequest");
		try {
			fSemaphore.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		initializeRepairInformationLists();
		aqcuireToolsFromWarehouse();
		acquireMaterialsFromWarehouse();
		
		long toSleep = fAssetToRepair.calculateRepairCost();
		fStatistics.increaseRepairCost(toSleep);
		try {
			ReitLogger.logThis("It's going to take " + toSleep + "time to fix the asset named " + fAssetToRepair.getAssetName(), "RunnableMaintenanceRequest");
			Thread.sleep(toSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		returnToolsToWarehouse();	
		fAssetToRepair.assetRepaired();
		fSemaphore.release();
		fMaintenanceWorkersShiftClock.countDown();
		ReitLogger.logThis("-=>REPAIR REQUEST IS DONE<=-", "RunnableMaintenanceRequest");
	}
		
	private void aqcuireToolsFromWarehouse(){
		Iterator<RepairToolInformation> toolInfoIt = fRepairToolsInformation.iterator();
		while(toolInfoIt.hasNext()){
			RepairToolInformation tempToolInfo = toolInfoIt.next();
			RepairTool toolToAdd = fWarehouse.acquireTool(tempToolInfo.getToolName(), tempToolInfo.getToolQuantity());
			fStatistics.addToolsUsed(toolToAdd);
			fRepairTools.add(toolToAdd);
		}
	}
	
	private void acquireMaterialsFromWarehouse(){
		Iterator<RepairMaterialInformation> materialInfoIt = fRepairMaterialsInformation.iterator();
		while(materialInfoIt.hasNext()){
			RepairMaterialInformation tempMaterialInfo = materialInfoIt.next();
			RepairMaterial tempMaterial = fWarehouse.acquireMaterial(tempMaterialInfo.getMaterialName(), tempMaterialInfo.getMaterialQuantity());
			fStatistics.addMaterialsUsed(tempMaterial);
			fRepairMaterials.add(tempMaterial);
			
		}
	}
	
	private void returnToolsToWarehouse(){
		Iterator<RepairTool> toolIt = fRepairTools.iterator();
		while(toolIt.hasNext()){
			RepairTool toolToReturn = toolIt.next();
			fWarehouse.releaseTool(toolToReturn.getToolName(), toolToReturn.getQuantity());
		}	
	}
	

}
