package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.devices.Dehumidifier;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class BedroomOneActivityHandler extends AbstractActivityHandler {

	private Logger log = Logger.getLogger(BedroomOneActivityHandler.class);
	private List<ControllableDevice> devicesToControl;
	private ControllableDevice lamp;
	private ControllableDevice ceilingLight;
	private Blind robWindowBlind;
	private ReportingDevice reportingDeviceMotionSensor;
	private ReportingDevice reportingDeviceDoorSensor;

	@Override
	public void run() {
		devicesToControl = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM);
		lamp = devicesToControl.get(0);
		ceilingLight = devicesToControl.get(1);
		robWindowBlind = (Blind) devicesToControl.get(5);

		reportingDeviceMotionSensor = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(0);
		reportingDeviceDoorSensor = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(1);

		Calendar tenPM = Calendar.getInstance();
		Calendar halfSevenAM = Calendar.getInstance();

		tenPM.set(Calendar.HOUR_OF_DAY, 22);
		tenPM.set(Calendar.MINUTE, 00);

		halfSevenAM.set(Calendar.HOUR_OF_DAY, 7);
		halfSevenAM.set(Calendar.MINUTE, 48);

		String robRoomBedroomMode = HomeAutomationProperties.getProperty("RobRoomBedroomMode");

		if (reportingDeviceMotionSensor.isTriggered()) {
			//door is open
			if (reportingDeviceDoorSensor.isTriggered()) {
				if (!"88".equals(robWindowBlind.getDeviceLevel())) {
					log.info("Rob room occupied, dark enough outside and not bedtime mode, lamp off and ceiling light on");

					ceilingLightOnLampOff();
				}
			}
		}
		else {
			int index = 1;
			for (ControllableDevice device : devicesToControl) {
				if (device.isDeviceOn() && !device.isManuallyOverridden() &&
						!device.isAutoOverridden() && !(device instanceof Dehumidifier) && !(device instanceof Blind)) {
					device.turnDeviceOff(false);
					log.info("Rob room not occupied, device not manually or auto overridden, switching off light " + index);
				}
				index++;
			}

			if (!lamp.isDeviceOn() && lamp.isAutoOverridden() &&
					(robRoomBedroomMode == null || (robRoomBedroomMode != null && "false".equals(robRoomBedroomMode)))) {
				lamp.turnDeviceOnAutoOverride("100");
			}
		}
	}

	private void ceilingLightOnLampOff() {
		if (!ceilingLight.isDeviceOn()) {
			ceilingLight.turnDeviceOn(false);
		}

		if (lamp.isDeviceOn()) {
			Timer timer = new Timer("Lamp delay timer off");
		
			TimerTask delayLampsOff = new TimerTask() {
				@Override
				public void run() {
					lamp.turnDeviceOff(true);
				}
			};
			
			timer.schedule(delayLampsOff, 15000);
		}
	}
}
