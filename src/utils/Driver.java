package utils;

import REITSimulationObjects.Management;
import REITSimulationObjects.ReadXmlFile;

public class Driver {
	
	public static void main(String[] arg) throws InterruptedException{
		Management management = new Management();
		ReadXmlFile.initializeManagment(management, arg);
		management.startSimulation();
	}
}


