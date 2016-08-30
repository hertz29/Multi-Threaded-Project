package REITSimulationObjects;

import java.lang.Math;

/**
 * 
 * @author us
 * Basic location class, to store location.
 *
 */

class Location {

	private int fX;
	private int fY;

	public Location(){
		fX = 0;
		fY = 0;
	}

	public Location(int x,int y){
		fX = x;
		fY = y;
	}

	/**
	 * 
	 * @param other
	 * @return the distance between to locations
	 */
	public double calculateDistance(Location other){
		return Math.sqrt(Math.pow(Math.abs(fX-other.fX), 2)+Math.pow(Math.abs(fY-other.fY), 2));

	}

	public String toString(){
		String ret = "(" + fX +"," + fY + ")";
		return ret;
	}
}
