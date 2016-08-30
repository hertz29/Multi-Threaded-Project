package REITSimulationObjects;


/**
 * 
 * @author us
 * This class represent a damage report that submit to the management
 *
 */
class DamageReport {


	private Asset fAsset;
	private double fDamagePercentage;

	public DamageReport(){
		fAsset=null;
		fDamagePercentage = 0;
	}

	public DamageReport(Asset asset, double damagePercentage){
		fAsset = asset;
		fDamagePercentage = damagePercentage;
	}

	public Asset getAsset() {
		return fAsset;
	}
	
	
	public void updateHealth(double damage) {
		fAsset.updateHealth(damage);
	}

	public double getDamage() {
		return fDamagePercentage;
	}
}
