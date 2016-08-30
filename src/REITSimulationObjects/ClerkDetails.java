package REITSimulationObjects;


/**
 * 
 * @author Omer&Idan
 * This class holds all the information of a certain Clerk
 *
 */
class ClerkDetails {

	private String fClerkName;
	private Location fClerkLocation;

	public ClerkDetails(){
		fClerkLocation = new Location(0,0);
		fClerkName = "";
	}

	public ClerkDetails(int x, int y, String name){
		fClerkLocation = new Location(x,y);
		fClerkName = name;
	}

	public Location getLocation(){
		return fClerkLocation;
	}

	public String getName(){
		return fClerkName;
	}
}
