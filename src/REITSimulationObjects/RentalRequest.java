package REITSimulationObjects;

import java.util.logging.Level;

import utils.ReitLogger;


/**
 * 
 * @author Omer&Idan
 * 
 *This class holds all the information of a certain rental request
 */
class RentalRequest {

	private String fRequestId;
	private String fAssetType;
	private int fAssetSize;
	private int fDurationOfStay;
	private Asset fAsset;
	private String fRequestStatus;
	private int fNumOfCustomers;

	public RentalRequest(){
		fRequestId = "";
		fAssetType = "";
		fAssetSize = 0;
		fDurationOfStay = 0;
		fAsset = null;
		fRequestStatus = "";	
		fNumOfCustomers = 0;
	}

	public RentalRequest(String requestId, String assetType, int assetSize, int duration, int numOfCustomers){
		fRequestId = requestId;
		fAssetType = assetType;
		fAssetSize = assetSize;
		fDurationOfStay = duration;
		fRequestStatus = "INCOMPLETE";
		fNumOfCustomers = numOfCustomers;
	}
	public int getDuration(){
		return fDurationOfStay;
	}
	/** 
	 * At updateAsset, the asset status is changing to "FULFILLED" and notify all clerks 
	 * @param asset
	 */
	public void updateAsset(Asset asset){
		synchronized (this) {
			fAsset = asset;
			fRequestStatus = "FULFILLED";
			ReitLogger.fLogger.log(Level.INFO, "Request " + fRequestId + " has been fulfilled");
			this.notify();
		}
	}
	
	
	/**
	 * At updateStatus, the rental request status is changing according to the @param status
	 * 
	 */
	public void updateStatus(String status) {
		fRequestStatus = status;
	}

	/** 
	 * At fulfilled, the runnableCustomerGroupManager is waiting till the rental request will fulfilled
	 */
	public void fulfilled(){
		synchronized (this){
			while(!fRequestStatus.equals("FULFILLED")){
				try 
				{
					this.wait();
				} 
				catch (InterruptedException e1){}
			}
		}
	}
	
	public DamageReport createDamageReport(double damage){
		DamageReport tempDamageReport = new DamageReport(fAsset, damage);
		return tempDamageReport;
	}

	public int getSize() {
		return fAssetSize;
	}

	public String getType() {
		return fAssetType;
	}

	public String getStatus() {
		return fRequestStatus;
	}

	public String getRequestName() {

		return fRequestId;
	}

	/**
	 * update the health of the asset contents, when the rental duration time is over
	 */
	public void freeAsset() {
		fAsset.freeAsset();

	}
	/**
	 * 
	 * @return return the total rental cost
	 */
	public double calculateCost() {
		return fAsset.getCostPerNight()*fDurationOfStay*fNumOfCustomers;
	}
	public String getAssetName() {
		return fAsset.getAssetName();
	}
		

	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("Request ID: ");
		s.append(fRequestId);
		s.append("\n");
		s.append("Requested Asset Size:");
		s.append(fAssetSize);
		s.append("\n");
		s.append("Request Asset Type ");
		s.append(fAssetType);
		s.append("\n");
		s.append("Rental Request duration");
		s.append(fDurationOfStay);
		s.append("\n");
		s.append(fAsset);
		s.append("\n \n");
		return s.toString();
	}

	public void occupyAsset() {
		fAsset.occupyAsset();
	}
	
	public void printRequest(){
		System.out.println("Request name: " + fRequestId);
		System.out.println("Requesting asset size: " + fAssetSize);
		System.out.println("Requesting asset type: " + fAssetType);
		System.out.println("Request Duration of stay: " + fDurationOfStay);
	}
}
