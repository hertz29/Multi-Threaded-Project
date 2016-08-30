package REITSimulationObjects;

/**
 * 
 * @author Omer&Idan
 * This class has 2 uses. It saves the quantity of the tools according to a tool name.
 * use 1) In Warehouse this class is being used as a collection of a certain tool.
 * use 2) Also can be used as a tool to be used by a maintenance person to fix assets.
 *  
 */
class RepairTool {

	private String fToolName;     
	private int fToolQuantity;

	public RepairTool(){
		fToolName = "";
		fToolQuantity = 0;
	}

	public RepairTool(String name, int quantity){
		fToolName = name;
		fToolQuantity = quantity;
	}

	/**
	 * This function retrieves the asked quantity (param) of the tool from the warehouse.
	 * **This method is thread safe!
	 * @param quantity
	 */
	public synchronized void acquireTool(int quantity){
		synchronized(this){
			while(fToolQuantity == 0){
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			fToolQuantity -= quantity;
		}
	}

	/**
	 * returns the tool to the collection of the warehouse
	 * @param quantity
	 */
	public synchronized void returnTool(int quantity){
		fToolQuantity += quantity;
		this.notify();
	}

	public String getToolName(){
		return fToolName;
	}


	public int getQuantity() {
		return fToolQuantity;
	}
	
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append(fToolName);
		s.append(" quantity is: ");
		s.append(fToolQuantity);
		return s.toString();
	}
}