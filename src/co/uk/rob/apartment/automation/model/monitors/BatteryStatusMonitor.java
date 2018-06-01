package co.uk.rob.apartment.automation.model.monitors;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.AlarmUnit;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

public class BatteryStatusMonitor extends Thread {
	
	private Logger log = Logger.getLogger(BatteryStatusMonitor.class);
	
	public BatteryStatusMonitor() {
		log.info("Weekly battery monitor started");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			try {
				int oneDay = (60000 * 60) * 24;
				Thread.sleep(oneDay * 7);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			ReportingDevice multisensorRobRoom = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(0);
			ReportingDevice doorSensorRobRoom = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(1);
			ReportingDevice windowSensorRobRoom = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(2);
			ReportingDevice windowSensorScarlettsRoom = DeviceListManager.getReportingDeviceByLocation(Zone.SCARLETT_ROOM).get(0);
			ReportingDevice multisensorLounge = DeviceListManager.getReportingDeviceByLocation(Zone.LOUNGE).get(0);
			ReportingDevice frontDoorSensor = DeviceListManager.getReportingDeviceByLocation(Zone.HALLWAY).get(0);
			ReportingDevice multisensorPatioSensor = DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(0);
			ReportingDevice patioDoorSensor = DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(1);
			AlarmUnit outdoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.PATIO).get(0);
			AlarmUnit indoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.HALLWAY).get(0);
			ReportingDevice multisensorBathroomSensor = DeviceListManager.getReportingDeviceByLocation(Zone.BATHROOM).get(0);
			
			boolean batteriesLow = false;
			String textToSend = "";
			
			if (multisensorRobRoom.getBatteryLevel() >= 0 && multisensorRobRoom.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "Rob room multisensor";
				batteriesLow = true;
			}
			
			if (doorSensorRobRoom.getBatteryLevel() >= 0 && doorSensorRobRoom.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "Rob room door sensor";
				batteriesLow = true;
			}
			
			if (windowSensorRobRoom.getBatteryLevel() >= 0 && windowSensorRobRoom.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "Rob room window sensor";
				batteriesLow = true;
			}
			
			if (windowSensorScarlettsRoom.getBatteryLevel() >= 0 && windowSensorScarlettsRoom.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "Scarlett's room window sensor";
				batteriesLow = true;
			}
			
			if (multisensorLounge.getBatteryLevel() >= 0 && multisensorLounge.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "lounge multisensor";
				batteriesLow = true;
			}
			
			if (frontDoorSensor.getBatteryLevel() >= 0 && frontDoorSensor.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "front door sensor";
				batteriesLow = true;
			}
			
			if (multisensorPatioSensor.getBatteryLevel() >= 0 && multisensorPatioSensor.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "patio multisensor";
				batteriesLow = true;
			}
			
			if (patioDoorSensor.getBatteryLevel() >= 0 && patioDoorSensor.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "patio door sensor";
				batteriesLow = true;
			}
			
			if (outdoorAlarmUnit.getBatteryLevel() >= 0 && outdoorAlarmUnit.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "outdoor alarm unit";
				batteriesLow = true;
			}
			
			if (indoorAlarmUnit.getBatteryLevel() >= 0 && indoorAlarmUnit.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "indoor alarm unit";
				batteriesLow = true;
			}
			
			if (multisensorBathroomSensor.getBatteryLevel() >= 0 && multisensorBathroomSensor.getBatteryLevel() <= 10) {
				textToSend = preLoadTextString(textToSend) + "bathroom multisensor";
				batteriesLow = true;
			}
			
			if (batteriesLow) {
				if (SMSHelper.sendSMS("07965502960", textToSend)) {
					log.info(textToSend + " - SMS reminder sent");
				}
				else {
					log.info(textToSend + " - error sending SMS reminder");
				}
			}
		}
	}
	
	private String preLoadTextString(String textToSend) {
		if ("".equals(textToSend)) {
			textToSend += "Batteries lower than 10% in: ";
		}
		else {
			textToSend += ", ";
		}
		
		return textToSend;
	}
}
