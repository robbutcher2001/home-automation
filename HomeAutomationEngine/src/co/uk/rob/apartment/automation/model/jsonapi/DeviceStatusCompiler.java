package co.uk.rob.apartment.automation.model.jsonapi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.devices.Dehumidifier;
import co.uk.rob.apartment.automation.model.devices.DoorSensor;
import co.uk.rob.apartment.automation.model.devices.Multisensor;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class DeviceStatusCompiler {
	
	//results
	private List<ZoneStatusResultSet> allZoneStatuses = null;
	private List<ReportingDevice> sensors = null;
	private Map<String, String> genericStatusHolder = null;
	
	//utilities
	private DateFormat dateFormat = null;
	
	public DeviceStatusCompiler() {
		this.allZoneStatuses = new ArrayList<ZoneStatusResultSet>();
		this.dateFormat = new SimpleDateFormat("dd/MM HH:mm");
	}
	
	public ZoneStatusResultSet getZoneStatus(Zone zone) {
		ZoneStatusResultSet resultSet = null;
		//use Map with smaller scope here as this method can be called on its own
		Map<String, String> statuses = null;
		
		this.sensors = DeviceListManager.getReportingDeviceByLocation(zone);
		resultSet = new ZoneStatusResultSet(zone);
		
		for (ReportingDevice sensor : this.sensors) {
			statuses = new ConcurrentHashMap<String, String>();
			if (sensor != null) {
				Calendar lastOccupied = Calendar.getInstance();
				lastOccupied.setTime(new Date(sensor.getLastUpdated()));
				if (sensor instanceof Multisensor) {
					statuses.put("temperature", Float.toString(sensor.getTemperature()[2]));
					statuses.put("humidity", Float.toString(sensor.getHumidity()[2]));
					statuses.put("luminiscence", Float.toString(sensor.getLuminiscence()[2]));
					statuses.put("occupied", Boolean.toString(sensor.isTriggered()));
					statuses.put("last_occupied", dateFormat.format(lastOccupied.getTime()));
				}
				else {
					statuses.put("open", Boolean.toString(sensor.isTriggered()));
					statuses.put("last_triggered", dateFormat.format(lastOccupied.getTime()));
				}
				
				statuses.put("battery_level", Integer.toString(sensor.getBatteryLevel()));
				
				if (sensor instanceof Multisensor) {
					resultSet.setStatuses("multisensor", statuses);
				}
				else if (sensor instanceof DoorSensor) {
					resultSet.setStatuses("door_sensor", statuses);
				}
				else {
					resultSet.setStatuses("unknown_sensor", statuses);
				}
			}
		}
		
		return resultSet;
	}
	
	public List<ZoneStatusResultSet> getEntireApartmentStatus() {
		this.allZoneStatuses.clear();
		
		ZoneStatusResultSet apartmentStatuses = new ZoneStatusResultSet(Zone.APARTMENT);
		addBlindStatuses(apartmentStatuses);
		addApartmentOccupancy(apartmentStatuses);
		addModeStatuses(apartmentStatuses);
		this.allZoneStatuses.add(apartmentStatuses);
		
		ZoneStatusResultSet loungeStatuses = getZoneStatus(Zone.LOUNGE);
		addModeStatuses(loungeStatuses);
		this.allZoneStatuses.add(loungeStatuses);
		
		ZoneStatusResultSet robRoomStatuses = getZoneStatus(Zone.ROB_ROOM);
		addDehumidifierStatuses(robRoomStatuses);
		addModeStatuses(robRoomStatuses);
		this.allZoneStatuses.add(robRoomStatuses);
		
		this.allZoneStatuses.add(getZoneStatus(Zone.HALLWAY));
		this.allZoneStatuses.add(getZoneStatus(Zone.PATIO));
		
		return this.allZoneStatuses;
	}
	
	private void addBlindStatuses(ZoneStatusResultSet resultSet) {
		this.genericStatusHolder = new ConcurrentHashMap<String, String>();
		List<ControllableDevice> controllableDevices = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE);
		controllableDevices.addAll(DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM));
		int blindNumber = 1;
		for (ControllableDevice device : controllableDevices) {
			if (device instanceof Blind) {
				this.genericStatusHolder.put("percent_open", device.getDeviceLevel());
				this.genericStatusHolder.put("tilted", Boolean.toString(((Blind) device).isTilted()));
				resultSet.setStatuses("blinds" + blindNumber, this.genericStatusHolder);
				blindNumber++;
			}
		}
	}
	
	private void addDehumidifierStatuses(ZoneStatusResultSet resultSet) {
		this.genericStatusHolder = new ConcurrentHashMap<String, String>();
		List<ControllableDevice> controllableDevices = DeviceListManager.getControllableDeviceByLocation(resultSet.getZone());
		int dehumidifierNumber = 1;
		for (ControllableDevice device : controllableDevices) {
			if (device instanceof Dehumidifier) {
				this.genericStatusHolder.put("dehumidifying", Boolean.toString(device.isDeviceOn()));
				resultSet.setStatuses("dehumidifier" + dehumidifierNumber, this.genericStatusHolder);
				dehumidifierNumber++;
			}
		}
	}
	
	private void addApartmentOccupancy(ZoneStatusResultSet resultSet) {
		this.genericStatusHolder = new ConcurrentHashMap<String, String>();
		this.genericStatusHolder.put("occupied", Boolean.toString(CommonQueries.isApartmentOccupied()));
		Calendar lastOccupancy = CommonQueries.getLastApartmentOccupancyTime();
		if (lastOccupancy != null && !CommonQueries.isApartmentOccupied()) {
			this.genericStatusHolder.put("last_occupied", dateFormat.format(lastOccupancy.getTime()));
		}
		else {
			this.genericStatusHolder.put("last_occupied", null);
		}
		resultSet.setStatuses("occupancy", this.genericStatusHolder);
	}
	
	private void addModeStatuses(ZoneStatusResultSet resultSet) {
		this.genericStatusHolder = new ConcurrentHashMap<String, String>();
		Zone zone = resultSet.getZone();
		
		if (Zone.LOUNGE.equals(zone)) {
			String loungeBedroomMode = HomeAutomationProperties.getProperty("LoungeBedroomMode");
			if (loungeBedroomMode != null && "true".equals(loungeBedroomMode)) {
				this.genericStatusHolder.put("enabled", "true");
			}
			else {
				this.genericStatusHolder.put("enabled", "false");
			}
			resultSet.setStatuses("bedroom_mode", this.genericStatusHolder);
		}
		else if (Zone.ROB_ROOM.equals(zone)) {
			String robRoomBedroomMode = HomeAutomationProperties.getProperty("RobRoomBedroomMode");
			if (robRoomBedroomMode != null && "true".equals(robRoomBedroomMode)) {
				this.genericStatusHolder.put("enabled", "true");
			}
			else {
				this.genericStatusHolder.put("enabled", "false");
			}
			resultSet.setStatuses("full_bedroom_mode", this.genericStatusHolder);
		}
		else if (Zone.APARTMENT.equals(zone)) {
			String atHomeModeLounge = HomeAutomationProperties.getProperty("AtHomeTodayMode");
			if (atHomeModeLounge != null && "true".equals(atHomeModeLounge)) {
				this.genericStatusHolder.put("enabled", "true");
			}
			else {
				this.genericStatusHolder.put("enabled", "false");
			}
			resultSet.setStatuses("at_home_today_mode", this.genericStatusHolder);
		}
	}
}
