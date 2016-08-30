package REITSimulationObjects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import utils.ReitLogger;



/**
 * 
 * @author us
 * a passive class that hold all of the simulation collections that are used by the active classes
 *
 */
public class Management {

	private Warehouse fWarehouse;
	private Assets fAssets; 
	private HashMap<String, ArrayList<RepairToolInformation>> fRepairToolInfoMap; 
	private HashMap<String, ArrayList<RepairMaterialInformation>> fRepairMaterialInfoMap;
	private ArrayList<CustomerGroupDetails> fCustomerGroupsList; 
	private ArrayList<RunnableClerk> fRunnableClerkList;
	private ArrayList<RunnableCustomerGroupManager> fRunnableCustomerGroupManagerList;
	private ArrayList<RunnableMaintenanceRequest> fRunnableMaintenanceRequest;
	private ArrayBlockingQueue<RentalRequest> fRentalRequestQueue;
	private Vector<RentalRequest> fFinishedRentals; 
	private Vector<DamageReport> fDamageReportList; 
	private boolean fClerksAreDone;
	private AtomicInteger fTotalNumOfRentalRequests;
	private AtomicInteger fNumOfRentalRequestsPerShift;
	private Semaphore fNumOfMaintenanceWorkers;
	private CyclicBarrier fShiftClock;
	private CountDownLatch fMaintenanceWorkersShiftClock;
	private Statistics fStatistics;

	//Constructor
	public Management(){
		fRepairMaterialInfoMap = new HashMap<String, ArrayList<RepairMaterialInformation>>();
		fRepairToolInfoMap = new HashMap<String, ArrayList<RepairToolInformation>>();
		fAssets = new Assets();
		fCustomerGroupsList = new ArrayList<CustomerGroupDetails>();
		fWarehouse = new Warehouse();
		fTotalNumOfRentalRequests = new AtomicInteger();
		fRunnableCustomerGroupManagerList = new ArrayList<RunnableCustomerGroupManager>();
		fRunnableClerkList = new ArrayList<RunnableClerk>();
		fDamageReportList = new Vector<DamageReport>();
		fRunnableMaintenanceRequest = new ArrayList<RunnableMaintenanceRequest>();
		fFinishedRentals = new Vector<RentalRequest>();
		fClerksAreDone = false;
		fStatistics = new Statistics();
		fNumOfRentalRequestsPerShift = new AtomicInteger(0);
		try {
			ReitLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Initialization of Management methods.
	public AtomicInteger getNumOfRentalRequests(){
		return fTotalNumOfRentalRequests;
	}

	/**
	 * At addClerk, a clerk is added to clerks list at management
	 * @param clerkDetails
	 */
	public void addClerk(ClerkDetails clerkDetails){
		RunnableClerk runClerk = new RunnableClerk(clerkDetails, fRentalRequestQueue, fAssets, fTotalNumOfRentalRequests, fNumOfRentalRequestsPerShift);
		fRunnableClerkList.add(runClerk);
	}

	/**	
	 * initialized the management shift clock.
	 * @param clerkCounter
	 */
	public void addNumberOfClerks(int clerkCounter) {
		initializeShiftClock(clerkCounter);

	}

	private void initializeShiftClock(int clerkCounter){
		fShiftClock = new CyclicBarrier(clerkCounter, new Runnable(){
			public void run(){
				changeClerksAreDone();
			}
		});
		Iterator<RunnableClerk> itr = fRunnableClerkList.iterator();
		while(itr.hasNext()){
			itr.next().initializeShiftClock(fShiftClock);
		}
	}
	
	private void changeClerksAreDone(){
		synchronized (this) {
			fClerksAreDone = true;
			this.notify();
		}
	}
	
	/**
	 * * At CustomerGroup, a customer group is added to customers list at management
	 * @param customerGroup
	 */
	public void addCustomerGroup(CustomerGroupDetails customerGroup){
		fCustomerGroupsList.add(customerGroup);
		RunnableCustomerGroupManager newManager = new RunnableCustomerGroupManager(customerGroup, fRentalRequestQueue,fDamageReportList
			,fNumOfRentalRequestsPerShift);
		fRunnableCustomerGroupManagerList.add(newManager);
	}

	/**
	 *  At CustomerGroup, a customer group is added to customer list at management
	 * @param numberOfMaintenancePersons
	 */
	public void initializeMaintenanceGuys(int numberOfMaintenancePersons) {
		fNumOfMaintenanceWorkers = new Semaphore(numberOfMaintenancePersons);
	}

	/**
	 * A RepairToolInformation is added to RepairToolInformation list at management
	 * @param itemName
	 * @param repairTools
	 */
	public void addItemRepairTool(String itemName, ArrayList<RepairToolInformation> repairTools){
		fRepairToolInfoMap.put(itemName, repairTools);
	}
	
	/**
	 *A RepairMaterialInformation is added to RepairMaterialInformaton at management
	 * @param itemName
	 * @param repairMaterials
	 */
	public void addItemRepairMaterial(String itemName, ArrayList<RepairMaterialInformation> repairMaterials){
		fRepairMaterialInfoMap.put(itemName, repairMaterials);
	}
	
	/**
	 * A RepairToolInformation is added to warehouse
	 * @param toolName
	 * @param repairTool
	 */
	public void addToolToWarehouse(String toolName, RepairTool repairTool){
		fWarehouse.addTool(toolName, repairTool);
	}

	
	/**
	 * A RepairMaterialInformation is added to warehouse
	 * @param materialName
	 * @param repairMaterial
	 */
	public void addMaterialToWarehouse(String materialName, RepairMaterial repairMaterial){
		fWarehouse.addMaterial(materialName, repairMaterial);
	}
	
	/** 
	 * add asset to assets at management
	 * @param assetName
	 * @param assetSize
	 * @param assetType
	 * @param assetToAdd
	 */
	public void addAsset(String assetName, int assetSize,  String assetType, Asset assetToAdd){
		fAssets.addAsset(assetName, assetSize, assetType, assetToAdd);
	}
	
	/**
	 * initialize a new Blocking Queue and an atomic integer of rental requests
	 * @param totalNumberOfRentalRequests
	 */
	public void initializeNumRentalRequests(int totalNumberOfRentalRequests) {
		fRentalRequestQueue = new ArrayBlockingQueue<RentalRequest>(totalNumberOfRentalRequests);
		fTotalNumOfRentalRequests = new AtomicInteger(totalNumberOfRentalRequests);
	}
	
	public void initializeRepairAssetInformation(){
		fAssets.initializeRepairAssetInformation(fRepairMaterialInfoMap, fRepairToolInfoMap);
	}

	public void sortAssets() {
		fAssets.sortAssets();

	}

	
	//Start simulation and its helping methods
	/**
	 * This function simulates the work of REIT, including its customers.
	 * THIS STARTS THE SIMULATION.
	 */
	public void startSimulation() {
		printSimulationDetails();
		ReitLogger.logThis("Launching Simulation! launching all Runnables.", "Management");
		startManagers();
		int count = 0;
		while(fTotalNumOfRentalRequests.get() != 0){
			count++;
			ReitLogger.logThis("--------------------Shift " +count + " Has Begun --------------------", "Management");
			ReitLogger.logThis("New Clerk Shift Has Begun", "Management");
			fClerksAreDone = false;
			startClerks();
			isClerksShiftDone();
			ReitLogger.logThis("All clerks are done!! waiting for customer groups to vacate the assets!", "Management");
			isRentalRequestsPerShiftDone();
			ReitLogger.logThis("ALL ASSETS HAVE BEEN VACATED!", "Management");
			updateAssetHealth();
			ArrayList<Asset> tempAsset = fAssets.retrieveDamagedAssets();
			initializeWorkersShiftClock(tempAsset.size());
			createNewMaintenanceRequests(tempAsset);
			startMaintenanceWorkers();
			while(!areWorkersDone());
			ReitLogger.logThis("The Maintenance Workers finished fixing the damaged assets", "Management");
			ReitLogger.logThis("------------------ Day "+count + " Has ended with "+ count +" requests left to handle!", "Management");
		}
		ReitLogger.logThis("Prepearing Simulation statistics to print!", "Management");
		fStatistics.applyFinishedRentalsList(fFinishedRentals);
		System.out.println(fStatistics);
		ReitLogger.logThis("Simulation has ended. Goodbye.", "Management");	
	}
	
	private void printSimulationDetails() {
		fAssets.printAssets();
		Iterator<CustomerGroupDetails> it = fCustomerGroupsList.iterator();
		while(it.hasNext()){
			it.next().printCustomerGroups();
		}
		
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	private void startClerks(){
		Iterator<RunnableClerk> it = fRunnableClerkList.iterator();
		while(it.hasNext()){
			Thread toRun = new Thread(it.next());
			toRun.start();
		}
	}

	private void startManagers(){
		Iterator<RunnableCustomerGroupManager> it = fRunnableCustomerGroupManagerList.iterator();
		while(it.hasNext()){
			RunnableCustomerGroupManager tempManager = it.next();
			tempManager.addFinishedRentalsList(fFinishedRentals);
			Thread toRun = new Thread(tempManager);
			toRun.start();
		}

	}

	private void isRentalRequestsPerShiftDone() {
		while(fNumOfRentalRequestsPerShift.get()!= 0){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void isClerksShiftDone() {
		while(fClerksAreDone == false){
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e){ 
					e.printStackTrace();
				}
			}
		}
	}

	private boolean areWorkersDone() {
		return fMaintenanceWorkersShiftClock.getCount() == 0;
	}

	private void startMaintenanceWorkers() {
		Iterator<RunnableMaintenanceRequest> it = fRunnableMaintenanceRequest.iterator();
		while(it.hasNext()){
			Thread toRun = new Thread(it.next());
			toRun.start();
			it.remove();
		}
	}

	private void initializeWorkersShiftClock(int count){
		fMaintenanceWorkersShiftClock = new CountDownLatch(count);
	}
	
	private void createNewMaintenanceRequests(
			ArrayList<Asset> damagedAssets) {
		ReitLogger.logThis("New Maintenance Workers shift has begun with " + damagedAssets.size() + " to handle!", "Management");
		Iterator<Asset> it = damagedAssets.iterator();
		while(it.hasNext()){
			RunnableMaintenanceRequest tempMaintenanceRequest = new RunnableMaintenanceRequest(fWarehouse,it.next(),fNumOfMaintenanceWorkers, fMaintenanceWorkersShiftClock, fStatistics);
			fRunnableMaintenanceRequest.add(tempMaintenanceRequest);
		}
	}
	
	private void updateAssetHealth(){
		while(!fDamageReportList.isEmpty()){
			DamageReport damageReportToHandle = fDamageReportList.remove(0);
			damageReportToHandle.updateHealth(damageReportToHandle.getDamage());
		}
	}

}
