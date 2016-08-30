package REITSimulationObjects;
import java.util.Random;

/**
 * 
 * @author us
 * This class hold customer info of a certain customer in the groups
 *
 */
class Customer {

	private String fCustomerName;
	private String fVandalismType;
	private int fMinDamage;
	private int fMaxDamage;

	public Customer(){
		fCustomerName = "";
		fVandalismType = "";
		fMinDamage = 0;
		fMaxDamage = 0;
	}

	public Customer(String customerName, String vandalismType, int minDamage, int maxDamage){
		fCustomerName = customerName;
		fVandalismType = vandalismType;
		fMinDamage = minDamage;
		fMaxDamage = maxDamage;
	}

	public String getVandalismType() {
		return fVandalismType;
	}

	public int getMaxDamage() {
		return fMaxDamage;
	}

	public int getMinDamage() {
		return fMinDamage;
	}

	public String getName() {
		return fCustomerName;	
	}

	/**
	 * 
	 * @return the damage caused by the customer at the asset according the vandalism type
	 */
	public Double calculateDamage() {
		Double returnValue = new Double(0);
		if(fVandalismType.equals("Arbitrary")){
			Random random = new Random();
			returnValue = (random.nextDouble()*(fMaxDamage - fMinDamage) + fMinDamage);
		}
		
		if(fVandalismType.equals("Fixed")){
			returnValue = ((double)fMaxDamage + (double)fMinDamage)/2;
		}

		if(fVandalismType.equals("None")){
			returnValue = 0.5;
		}
		return returnValue;
	}
	
	public void printCustomer() {
		System.out.println("Customer name: " + fCustomerName);
		System.out.println("Customer vandalism type: " + fVandalismType);
		
	}

}
