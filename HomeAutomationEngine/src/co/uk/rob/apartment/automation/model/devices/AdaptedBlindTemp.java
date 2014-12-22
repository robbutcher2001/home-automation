package co.uk.rob.apartment.automation.model.devices;

import co.uk.rob.apartment.automation.model.Zone;

/**
 * @author Rob
 *
 */
public class AdaptedBlindTemp extends Blind {

	/**
	 * @param endpoint
	 * @param location
	 * @param switchBinaryEndpoint
	 */
	public AdaptedBlindTemp(String endpoint, Zone location,
			String switchBinaryEndpoint) {
		super(endpoint, location, switchBinaryEndpoint);
	}

	@Override
	public Boolean turnDeviceOn(boolean manuallyOverride, String level) {
		String newLevel = level;
		
		//if target is 80% open
		if ("80".equals(level)) {
			//if blinds are going down (current level is above 80)
			if ("100".equals(this.getDeviceLevel())) {
				newLevel = "77";
			}
			//blinds are going up
			else {
				newLevel = "78";
			}
		}
		//same logic as above
		else if ("55".equals(level)) {
			if ("100".equals(this.getDeviceLevel()) || "80".equals(this.getDeviceLevel())) {
				newLevel = "58";
			}
			else {
				newLevel = "51";
			}
		}
		else if ("40".equals(level)) {
			//opposite logic to above
			if ("0".equals(this.getDeviceLevel())) {
				newLevel = "35";
			}
			else {
				newLevel = "41";
			}
		}
		
		boolean run = super.turnDeviceOn(manuallyOverride, newLevel);
		
		//fake retain old level - bit of a hack
		setDeviceLevel(level);
		
		return run;
	}
	
	//rework the below?
	@Override
	public Boolean turnDeviceOnAutoOverride(String level) {
		super.turnDeviceOnAutoOverride(level);
		
		return turnDeviceOn(false, level);
	}
}
