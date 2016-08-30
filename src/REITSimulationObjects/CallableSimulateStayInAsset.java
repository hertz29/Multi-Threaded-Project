package REITSimulationObjects;
import java.util.concurrent.Callable;

class CallableSimulateStayInAsset implements Callable<Double>{

	private Customer fCustomer;
	private int fRentalDuration;

	public CallableSimulateStayInAsset(){
		fCustomer = new Customer();
		fRentalDuration = 0;
	}

	public CallableSimulateStayInAsset(Customer customer, int rentalDuration){
		fCustomer = customer;
		fRentalDuration = rentalDuration;
	}

	public Double call() throws Exception{
		Thread.sleep(24000*fRentalDuration);
		Double returnValue = new Double(0);
		returnValue = fCustomer.calculateDamage();
		return returnValue;
	}

}
