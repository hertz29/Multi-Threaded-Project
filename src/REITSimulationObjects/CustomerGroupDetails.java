package REITSimulationObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * @author us
 * This class hold a collection of customers, and their requests for rentals
 *
 */
class CustomerGroupDetails {

	private String fGroupManagerName;
	private ArrayList<Customer> fCustomerList;
	private Queue<RentalRequest> fRentalRequestQueue;

	public CustomerGroupDetails(){
		fGroupManagerName = "";
		fCustomerList = null;
		fRentalRequestQueue = null;
	}

	public CustomerGroupDetails(String groupName){
		fGroupManagerName = groupName;
		fCustomerList = new ArrayList<Customer>();
		fRentalRequestQueue = new LinkedList<RentalRequest>();
	}
	
	public void addCustomer(Customer newCustomer){
		fCustomerList.add(newCustomer);
	}

	public void addRentalRequest(RentalRequest newRentalRequest){
		fRentalRequestQueue.add(newRentalRequest);
	}

	public RentalRequest getRentalRequest() {
		if(fRentalRequestQueue.peek() != null){
			return fRentalRequestQueue.poll();
		}
		else{
			return null;
			//no more requests
		}
	}

	public int getNumOfCustomers() {
		return fCustomerList.size();
	}

	public ArrayList<Customer> getCustomersList(){
		return fCustomerList;
	}

	public String getManagerName() {
		return fGroupManagerName;
	}
	
	public void printCustomerGroups(){
		System.out.println("Group Manager Name: " + fGroupManagerName);
		Iterator<Customer> it = fCustomerList.iterator();
		while(it.hasNext()){
			it.next().printCustomer();
		System.out.println();}
	
		System.out.println("This group's requests: ");
		Iterator<RentalRequest> itr = fRentalRequestQueue.iterator();
		while(itr.hasNext()){
			itr.next().printRequest();
			System.out.println();
		}
	}

}
