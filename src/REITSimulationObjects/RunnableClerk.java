package REITSimulationObjects;
import utils.ReitLogger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a clerk, enrolled in the REIT office.
 * The clerk is handling a rental request and finds an Asset that fits the customer group request.
 * The RunnableClerk implements Runnable interface.
 * @author Omer&Idan
 *
 */

class RunnableClerk implements Runnable {

	private ClerkDetails fClerkDetails;
	private BlockingQueue<RentalRequest>  fRentalRequest;   //implement me mother fucker
	private AtomicInteger fNumOfRentalRequests;                      //why is this important?!@$?!@
	private Assets fAssets;
	private CyclicBarrier fShiftClock;
	private AtomicInteger fNumOfRequestsPerShift;

	public RunnableClerk(){
		fClerkDetails = null;
		fRentalRequest = null;
		fNumOfRentalRequests = null;
		fShiftClock = null;
		fNumOfRequestsPerShift = null;
	}

	public RunnableClerk(ClerkDetails clerkDetails, BlockingQueue<RentalRequest> rentalRequests, Assets assets,
			AtomicInteger numOfRentalRequests, AtomicInteger numOfRequestsPerShift){
		fClerkDetails = clerkDetails;
		fRentalRequest = rentalRequests;
		fAssets = assets;
		fNumOfRentalRequests = numOfRentalRequests;
		fNumOfRequestsPerShift = numOfRequestsPerShift;
	}

	private RentalRequest getRentalRequest(){
		try {
			if(fNumOfRentalRequests.get() != 0){
				fNumOfRentalRequests.decrementAndGet();
				fNumOfRequestsPerShift.incrementAndGet();
				return fRentalRequest.take();
			}
			else{
				return fRentalRequest.poll();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();			
		}
		return null;
	}

	/**
	 * Simulate a shift cycle in a clerks life -
	 * handle requests until he ends his shift. 
	 * while he is still handling requests, he finds the closest asset to the require asset 
	 */
	public void run(){
		ReitLogger.logThis(fClerkDetails.getName() + " is starting a new shift", "RunnableClerk");
		long sleepTime = 0;
		while(sleepTime <= 8){
			if(fNumOfRentalRequests.get() == 0){
				try {
					fShiftClock.await();
					break;
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}	
			RentalRequest currentRentalRequest = getRentalRequest();
			ReitLogger.logThis(fClerkDetails.getName() + " is handling request " + currentRentalRequest.getRequestName(), "RunnableClerk");
			int requiredSize = currentRentalRequest.getSize();
			String requireType = currentRentalRequest.getType();
			Asset requiredAsset = fAssets.getRequiredAsset(requiredSize, requireType);
			
			while(requiredAsset == null) {
				requiredAsset = fAssets.getRequiredAsset(requiredSize, requireType);
			}
			sleepTime += 2*(fClerkDetails.getLocation().calculateDistance(requiredAsset.getLocation()));
			ReitLogger.logThis(fClerkDetails.getName() + " found the suitable asset for request: " +currentRentalRequest.getRequestName() +  " the suitable asset is: " + requiredAsset.getAssetName() +" the asset type is: " + requireType , "RunnableClerk");
			ReitLogger.logThis(fClerkDetails.getName() + " is going to the asset, It will take " + sleepTime + " seconds", "RunnableClerk");
			try {
				Thread.sleep(sleepTime*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();}
			currentRentalRequest.updateAsset(requiredAsset);
		}
		try {
			ReitLogger.logThis("The clerk " + fClerkDetails.getName() + " has finished his/hers shift. Waiting for a new shift.", "RunnableClerk");
			fShiftClock.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	public void initializeShiftClock(CyclicBarrier shiftClock) {
		fShiftClock = shiftClock;

	}

}
