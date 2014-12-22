package co.uk.rob.apartment.automation.model;

import java.util.ArrayList;
import java.util.List;

import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.devices.ColouredLedRod;
import co.uk.rob.apartment.automation.model.devices.Dehumidifier;
import co.uk.rob.apartment.automation.model.devices.DoorSensor;
import co.uk.rob.apartment.automation.model.devices.Lamp;
import co.uk.rob.apartment.automation.model.devices.MainCeilingLight;
import co.uk.rob.apartment.automation.model.devices.Multisensor;
import co.uk.rob.apartment.automation.model.handlers.BedroomOneActivityHandler;
import co.uk.rob.apartment.automation.model.handlers.BedroomOneDoorActivityHandler;
import co.uk.rob.apartment.automation.model.handlers.FrontDoorActivityHandler;
import co.uk.rob.apartment.automation.model.handlers.LoungeActivityHandler;
import co.uk.rob.apartment.automation.model.handlers.PatioActivityHandler;
import co.uk.rob.apartment.automation.model.handlers.PatioDoorActivityHandler;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class DeviceListManager {
	
	private static List<ControllableDevice> controllableDevices;
	private static List<ReportingDevice> reportingDevices;

	public static List<ControllableDevice> getControllableDevices() {
		if (controllableDevices == null) {
			setupControllableApartmentDevices();
		}
		
		return controllableDevices;
	}
	
	public static List<ControllableDevice> getControllableDeviceByLocation(Zone location) {
		if (controllableDevices == null) {
			setupControllableApartmentDevices();
		}
		
		List<ControllableDevice> controllableDevicesByLocation = new ArrayList<ControllableDevice>();
		for (ControllableDevice device : controllableDevices) {
			if (device.getZone().equals(location)) {
				controllableDevicesByLocation.add(device);
			}
		}
		
		return controllableDevicesByLocation;
	}
	
	public static List<ReportingDevice> getReportingDevices() {
		if (reportingDevices == null) {
			setupReportingApartmentDevices();
		}
		
		return reportingDevices;
	}
	
	public static List<ReportingDevice> getReportingDeviceByLocation(Zone location) {
		if (reportingDevices == null) {
			setupReportingApartmentDevices();
		}
		
		List<ReportingDevice> reportingDevicesByLocation = new ArrayList<ReportingDevice>();
		for (ReportingDevice device : reportingDevices) {
			if (device.getZone().equals(location)) {
				reportingDevicesByLocation.add(device);
			}
		}
		
		return reportingDevicesByLocation;
	}
	
	private static void setupControllableApartmentDevices() {
		controllableDevices = new ArrayList<ControllableDevice>();
		
		// Lounge
		ControllableDevice lampOneLoungeEndpoint = new Lamp(HomeAutomationProperties.getProperty("lampOneLoungeEndpoint"),
				Zone.LOUNGE);
		controllableDevices.add(lampOneLoungeEndpoint);
		
		ControllableDevice ledRodLoungeEndpoint = new ColouredLedRod(HomeAutomationProperties.getProperty("ledRodLoungeEndpoint"),
				Zone.LOUNGE);
		controllableDevices.add(ledRodLoungeEndpoint);
		
		ControllableDevice lampTwoLoungeEndpoint = new Lamp(HomeAutomationProperties.getProperty("lampTwoLoungeEndpoint"),
				Zone.LOUNGE);
		controllableDevices.add(lampTwoLoungeEndpoint);
		
		ControllableDevice loungeWindowBlindEndpoint = new Blind(HomeAutomationProperties.getProperty("loungeWindowBlindEndpoint"),
				Zone.LOUNGE, HomeAutomationProperties.getProperty("loungeWindowBlindSwitchBinaryEndpoint"));
		controllableDevices.add(loungeWindowBlindEndpoint);
		
		ControllableDevice loungePatioBlindEndpoint = new Blind(HomeAutomationProperties.getProperty("loungePatioBlindEndpoint"),
				Zone.LOUNGE, HomeAutomationProperties.getProperty("loungePatioBlindSwitchBinaryEndpoint"));
		controllableDevices.add(loungePatioBlindEndpoint);
		
		// Kitchen
		ControllableDevice ledRodKitchenEndpoint = new ColouredLedRod(HomeAutomationProperties.getProperty("ledRodKitchenEndpoint"),
				Zone.KITCHEN);
		controllableDevices.add(ledRodKitchenEndpoint);
		
		// Rob's room
		ControllableDevice lampRobEndpoint = new Lamp(HomeAutomationProperties.getProperty("lampRobEndpoint"),
				Zone.ROB_ROOM);
		controllableDevices.add(lampRobEndpoint);
		
		ControllableDevice ceilingLightRobEndpoint = new MainCeilingLight(HomeAutomationProperties.getProperty("ceilingLightRobEndpoint"),
				Zone.ROB_ROOM);
		controllableDevices.add(ceilingLightRobEndpoint);
		
		ControllableDevice dehumidifier = new Dehumidifier(HomeAutomationProperties.getProperty("dehumidifierRobEndpoint"),
				Zone.ROB_ROOM);
		controllableDevices.add(dehumidifier);
		
		ControllableDevice ledRodRobEndpoint = new ColouredLedRod(HomeAutomationProperties.getProperty("ledRodRobEndpoint"),
				Zone.ROB_ROOM);
		controllableDevices.add(ledRodRobEndpoint);
	}
	
	private static void setupReportingApartmentDevices() {
		reportingDevices = new ArrayList<ReportingDevice>();
		
		// Lounge
		ReportingDevice multisensorLoungeEndpoint = new Multisensor(HomeAutomationProperties.getProperty("multisensorLoungeBatteryUpdateEndpoint"),
				HomeAutomationProperties.getProperty("multisensorLoungeUpdateEndpoint"),
				HomeAutomationProperties.getProperty("multisensorLoungeEndpoint"),
				new LoungeActivityHandler(),
				Zone.LOUNGE);
		reportingDevices.add(multisensorLoungeEndpoint);
		
		// Rob's room
		ReportingDevice multisensorRobEndpoint = new Multisensor(HomeAutomationProperties.getProperty("multisensorRobBatteryUpdateEndpoint"),
				HomeAutomationProperties.getProperty("multisensorRobUpdateEndpoint"),
				HomeAutomationProperties.getProperty("multisensorRobEndpoint"),
				new BedroomOneActivityHandler(),
				Zone.ROB_ROOM);
		reportingDevices.add(multisensorRobEndpoint);
		
		ReportingDevice robDoorSensorEndpoint = new DoorSensor(HomeAutomationProperties.getProperty("robDoorSensorBatteryUpdateEndpoint"),
				HomeAutomationProperties.getProperty("robDoorSensorEndpoint"),
				new BedroomOneDoorActivityHandler(),
				Zone.ROB_ROOM);
		robDoorSensorEndpoint.setTriggered(true);
		reportingDevices.add(robDoorSensorEndpoint);
		
		// Hallway
		ReportingDevice frontDoorSensorEndpoint = new DoorSensor(HomeAutomationProperties.getProperty("frontDoorSensorBatteryUpdateEndpoint"),
				HomeAutomationProperties.getProperty("frontDoorSensorEndpoint"),
				new FrontDoorActivityHandler(),
				Zone.HALLWAY);
		reportingDevices.add(frontDoorSensorEndpoint);
		
		// Patio
		ReportingDevice patioSensorEndpoint = new Multisensor(HomeAutomationProperties.getProperty("multisensorPatioBatteryUpdateEndpoint"),
				HomeAutomationProperties.getProperty("multisensorPatioUpdateEndpoint"),
				HomeAutomationProperties.getProperty("multisensorPatioEndpoint"),
				new PatioActivityHandler(),
				Zone.PATIO);
		patioSensorEndpoint.setTriggered(false);
		reportingDevices.add(patioSensorEndpoint);
		
		ReportingDevice patioDoorEndpoint = new DoorSensor(HomeAutomationProperties.getProperty("patioSensorBatteryUpdateEndpoint"),
				HomeAutomationProperties.getProperty("patioSensorEndpoint"),
				new PatioDoorActivityHandler(),
				Zone.PATIO);
		reportingDevices.add(patioDoorEndpoint);
	}
}
