package co.uk.rob.apartment.automation.model.abstracts;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Used to hold common code between both ControllableDevices and ReportingDevices
 * 
 * @author Rob
 *
 */
public abstract class AbstractDevice {

	private Logger log = Logger.getLogger(AbstractDevice.class);
	
	// Endpoints
	protected String dataEndpoint = "";
	protected String batteryEndpoint = "128.data";
	
	// Environment variables
	protected int battery = 0;
	
	//JSON parser
	protected JSONParser zWayResultParser;
	
	protected boolean parseBatteryValue(String resultSet) {
		// To handle battery report
		boolean applied = false;
		Object result = parseReportedValue(this.dataEndpoint + this.batteryEndpoint, resultSet, "last");
		
		if (result != null) {
			try {
				int newBatteryLevel = Integer.parseInt(result.toString());
				this.battery = newBatteryLevel;
				applied = true;
			}
			catch (Exception e) {
				//no op
				log.error("Could not parse battery level response as Integer");
			}
		}
		
		return applied;
	}
	
	/**
	 * Apply update for a reported value
	 */
	protected synchronized Object parseReportedValue(String endpoint, String resultSet, String key) {
		try {
			JSONObject parsedResults = (JSONObject) this.zWayResultParser.parse(resultSet);
			
			if (parsedResults.containsKey(endpoint)) {
				JSONObject result = (JSONObject) this.zWayResultParser.parse(parsedResults.get(endpoint).toString());
				result = (JSONObject) this.zWayResultParser.parse(result.get(key).toString());
				
				return result.get("value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
