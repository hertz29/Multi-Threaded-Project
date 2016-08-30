package REITSimulationObjects;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import utils.ReitLogger;


/**
 * Represents a customer group manager. 
 * The customer group manager role is to communicate with the REIT office (management).
 * Submits rental request for his groups, and let them occupy the asset when the request is fulfilled.
 * The customer group manager submit a damage report when the asset is vacated.
 * @author Omer&Idan
 *
 */
class RunnableCustomerGroupManager implements Runnable{

	private CustomerGroupDetails fCustomerGroupDetails;
	private RentalRequest fCurrentRentalRequest;
	private BlockingQueue<RentalRequest>  fRentalRequestQueue;
	private Vector<RentalRequest> fFinishedRentals;
	private Vector<DamageReport>  fDamageReportList;
	private AtomicInteger fNumOfRentalRequestsPerShift;


	public RunnableCustomerGroupManager(){
		fCustomerGroupDetails = new CustomerGroupDetails();
		fRentalRequestQueue = null;
		fCurrentRentalRequest = null;
		fDamageReportList = null;
		fNumOfRentalRequestsPerShift = null;
	}

	public RunnableCustomerGroupManager(CustomerGroupDetails customerGroup,
			ArrayBlockingQueue<RentalRequest> rentalRequestQueue,
			Vector<DamageReport> damageReportList, AtomicInteger numOfRentalRequestsPerShift){
		fCustomerGroupDetails = customerGroup;
		fRentalRequestQueue = rentalRequestQueue;  
		fDamageReportList = damageReportList;
		fNumOfRentalRequestsPerShift = numOfRentalRequestsPerShift;
	}

	private double calculateDamage(Set<Future<Double>> futureSet) throws InterruptedException, ExecutionException{
		StringBuilder damageString = new StringBuilder();
		damageString.append("=====>[");
		damageString.append(fCustomerGroupDetails.getManagerName());
		damageString.append("]");
		double damage = 0;
		for (Future<Double> future : futureSet) {
			double tempDamage = future.get();
			damage += tempDamage;
			damageString.append("[damage = ");
			damageString.append(tempDamage);
			damageString.append("]");
		}
		damageString.append("=====> total damage is: [");
		damageString.append(damage);
		damageString.append("]");
		ReitLogger.logThis(damageString.toString(), "RunnableCustomerGroupManager");
		return damage;
	}

	private void submitDamageReport(double damage){
		DamageReport damageReport = fCurrentRentalRequest.createDamageReport(damage);
			fDamageReportList.add(damageReport); // add the damage report to the end of the list
	}



	private double activateCustomerInAsset(Set<Future<Double>> futureSet) throws InterruptedException, ExecutionException{
		int rentalDuration = fCurrentRentalRequest.getDuration();
		ExecutorService executor = Executors.newFixedThreadPool(fCustomerGroupDetails.getNumOfCustomers());
		ArrayList<Customer> customersList = fCustomerGroupDetails.getCustomersList();
		Iterator<Customer> itr = customersList.iterator();
		while(itr.hasNext()){
			Callable<Double> callable = new CallableSimulateStayInAsset(itr.next(), rentalDuration);
			Future<Double> future = executor.submit(callable);
			futureSet.add(future);
		}
		double damage = calculateDamage(futureSet);
		executor.shutdown();
		return damage;
	}

	//  we finished when i need to figure out if i want to use future or container of future


	public void run(){
		fCurrentRentalRequest = fCustomerGroupDetails.getRentalRequest();
		while(fCurrentRentalRequest != null){
			synchronized(fCurrentRentalRequest){
				try {
					fRentalRequestQueue.put(fCurrentRentalRequest);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				fCurrentRentalRequest.fulfilled();
			}
			fCurrentRentalRequest.updateStatus("INPROGRESS");
			fCurrentRentalRequest.occupyAsset();
			ReitLogger.logThis(fCustomerGroupDetails.getManagerName()  + " has entered the asset", "RunnableCustomerGroupManager");
			Set<Future<Double>> set = new HashSet<Future<Double>>();
			double damage = 0;
			try {
				damage = activateCustomerInAsset(set);
				submitDamageReport(damage);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			fCurrentRentalRequest.updateStatus("COMPLETE");
			printOutput(damage); //maybe  here
			fNumOfRentalRequestsPerShift.decrementAndGet();
			fCurrentRentalRequest.freeAsset();
			fFinishedRentals.add(fCurrentRentalRequest);
			fCurrentRentalRequest = fCustomerGroupDetails.getRentalRequest();
		}
		ReitLogger.logThis("CustomerGroupManager: "+fCustomerGroupDetails.getManagerName()+ " IS DONE! now I can die peacefully.", "RunnableCustomerGroupManager");
	}
	private void printOutput(double damage){
		StringBuilder output = new StringBuilder();
		output.append(fCustomerGroupDetails.getManagerName());
		output.append("[assetName= ");
		output.append(fCurrentRentalRequest.getAssetName());
		output.append("][assetType= ");
		output.append(fCurrentRentalRequest.getType());
		output.append("][assetsize= ");
		output.append(fCurrentRentalRequest.getSize());
		output.append("][totalDamage= ");
		output.append(damage);
		output.append("] vacating asset");
		ReitLogger.logThis(output.toString(), "RunnableCustomerGroupManager");
	}
	
	public void addFinishedRentalsList(
			Vector<RentalRequest> finishedRentals) {
		fFinishedRentals = finishedRentals;
		
	}
}
