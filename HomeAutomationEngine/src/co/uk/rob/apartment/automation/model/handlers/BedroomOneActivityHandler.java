package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.devices.Dehumidifier;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class BedroomOneActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(BedroomOneActivityHandler.class);
	private List<ControllableDevice> devicesToControl;
	private ControllableDevice lamp;
	private ControllableDevice ceilingLight;
	private ReportingDevice reportingDeviceMotionSensor;
	private ReportingDevice reportingDeviceDoorSensor;
	
	@Override
	public void run() {
		devicesToControl = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM);
		lamp = devicesToControl.get(0);
		ceilingLight = devicesToControl.get(1);
		reportingDeviceMotionSensor = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(0);
		reportingDeviceDoorSensor = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(1);
		
		Calendar tenPM = Calendar.getInstance();
		Calendar halfSevenAM = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		
		tenPM.set(Calendar.HOUR_OF_DAY, 22);
		tenPM.set(Calendar.MINUTE, 00);
		
		halfSevenAM.set(Calendar.HOUR_OF_DAY, 7);
		halfSevenAM.set(Calendar.MINUTE, 35);
		
		String robRoomBedroomMode = HomeAutomationProperties.getProperty("RobRoomBedroomMode");
		
		if (reportingDeviceMotionSensor.isTriggered()) {
			//door is open
			if (reportingDeviceDoorSensor.isTriggered()) {
				if (!CommonQueries.isBrightnessGreaterThan800()) {
					log.info("Rob room occupied, dark enough outside and not bedtime mode, lamp off and ceiling light on");
					
					ceilingLightOnLampOff();
				}
			}
			//door is closed
			else {
				//bedroom mode not enabled
				if (robRoomBedroomMode == null || (robRoomBedroomMode != null && "false".equals(robRoomBedroomMode))) {
					//occupancy between 10pm and 7:30am next day on a weekday
					if ((now.after(tenPM) || now.before(halfSevenAM) || CommonQueries.isItTheWeekendOrBankHoliday())
							&& !CommonQueries.isBrightnessGreaterThan800()) {
						if (!lamp.isDeviceOn()) {
							log.info("Rob room occupied during bed time mode, lamp on 20%");
							
							lamp.turnDeviceOn(false, "20");
						}
					}
					else {
						if (!ceilingLight.isDeviceOn()) {
							log.info("Rob room occupied and not bedtime mode but door still closed, lamp off and ceiling light on");
							
							ceilingLightOnLampOff();
						}
					}
				}
				else {
					log.info("Rob room occupied and door closed but full bedroom mode is enabled so not reacting");
				}
			}
		}
		else {
			int index = 1;
			for (ControllableDevice device : devicesToControl) {
				if (device.isDeviceOn() && !device.isManuallyOverridden() && !device.isAutoOverridden() && !(device instanceof Dehumidifier)) {
					device.turnDeviceOff(false);
					log.info("Rob room not occupied, device not manually or auto overridden, switching off light " + index);
				}
				index++;
			}
			
			if (!lamp.isDeviceOn() && lamp.isAutoOverridden() &&
					(robRoomBedroomMode == null || (robRoomBedroomMode != null && "false".equals(robRoomBedroomMode)))) {
				//hack to fade lamp back on
				lamp.resetManuallyOverridden();
				lamp.turnDeviceOnAutoOverride("300");
			}
		}
	}
	
	private void ceilingLightOnLampOff() {
		if (!ceilingLight.isDeviceOn()) {
			ceilingLight.turnDeviceOn(false);
		}
		
		if (lamp.isDeviceOn()) {
			lamp.turnDeviceOff(true);
		}
	}
}
