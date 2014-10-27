package co.uk.rob.apartment.automation.model.jsonapi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import co.uk.rob.apartment.automation.model.Zone;

public class ZoneStatusResultSet {
	private Zone zone;
	private Map<String, Map<String, String>> statuses;
	
	public ZoneStatusResultSet(Zone zone) {
		this.zone = zone;
		this.statuses = new ConcurrentHashMap<String, Map<String,String>>();
	}
	
	public Zone getZone() {
		return zone;
	}
	
	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	public Set<String> getSensors() {
		return this.statuses.keySet();
	}
	
	public Map<String, String> getStatuses(String sensor) {
		return statuses.get(sensor);
	}
	
	public void setStatuses(String sensor, Map<String, String> statuses) {
		this.statuses.put(sensor, statuses);
	}
}
