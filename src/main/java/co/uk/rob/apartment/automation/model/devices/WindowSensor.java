package co.uk.rob.apartment.automation.model.devices;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractDoorWindowSensor;
import co.uk.rob.apartment.automation.model.interfaces.ActivityHandler;

/**
 * @author Rob
 *
 */
public class WindowSensor extends AbstractDoorWindowSensor {

	public WindowSensor(String batteryUpdateEndpoint, String dataEndpoint,
			ActivityHandler handler, Zone zone) {
		super(batteryUpdateEndpoint, dataEndpoint, handler, zone);
		// stub
	}

}
