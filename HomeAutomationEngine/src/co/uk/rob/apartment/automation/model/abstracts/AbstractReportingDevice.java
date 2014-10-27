package co.uk.rob.apartment.automation.model.abstracts;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.ZwayResultSet;
import co.uk.rob.apartment.automation.model.interfaces.ActivityHandler;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CallZwaveModule;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public abstract class AbstractReportingDevice implements ReportingDevice {
	
	private Logger log = Logger.getLogger(AbstractReportingDevice.class);
	
	// Endpoints
	protected String host = HomeAutomationProperties.getProperty("host");
	protected String requestNewReportEndpoint;
	protected String batteryUpdateEndpoint;
	protected String dataEndpoint;
	protected String motionEndpoint = "48.data.1";
	protected String batteryEndpoint = "128.data";
	
	// Environment variables
	private boolean motion = true;
	private int battery = 0;
	protected ActivityHandler handler;
	protected long lastUpdated = System.currentTimeMillis();
	protected boolean resultProcessed = false;
	
	// Zone
	protected Zone zone;
	
	//JSON parser
	protected JSONParser zWayResultParser;
	
	public AbstractReportingDevice(String batteryUpdateEndpoint, String dataEndpoint, ActivityHandler handler, Zone zone) {
		this.batteryUpdateEndpoint = batteryUpdateEndpoint;
		this.dataEndpoint = dataEndpoint;
		this.handler = handler;
		this.zone = zone;
		this.zWayResultParser = new JSONParser();
	}

	@Override
	public synchronized void requestNewReport() {
		if (requestNewReportEndpoint != null) {
			callParseResult(host + requestNewReportEndpoint);
		}
	}
	
	@Override
	public synchronized void requestNewBatteryReport() {
		if (batteryUpdateEndpoint != null) {
			callParseResult(host + batteryUpdateEndpoint);
		}
	}

	protected boolean parseMotionValue(String resultSet) {
		// To handle any possible motion detected
		boolean applied = false;
		Object result = parseReportedValue(this.dataEndpoint + this.motionEndpoint, resultSet, "level");
		
		if (result != null) {
			try {
				boolean newMotion = Boolean.parseBoolean(result.toString());
				setTriggered(newMotion);
				//log.info("Motion state of sensor " + this.dataEndpoint + ": " + this.isTriggered());
				applied = true;
			}
			catch (Exception e) {
				//no op
				log.error("Could not parse motion response as Boolean");
			}
		}
		
		return applied;
	}
	
	protected boolean parseBatteryValue(String resultSet) {
		// To handle battery report
		boolean applied = false;
		Object result = parseReportedValue(this.dataEndpoint + this.batteryEndpoint, resultSet, "last");
		
		if (result != null) {
			try {
				int newBatteryLevel = Integer.parseInt(result.toString());
				this.battery = newBatteryLevel;
				//log.info("Battery level of sensor " + this.dataEndpoint + ": " + this.battery);
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
//		try {
//			JSONObject parsedResults = (JSONObject) this.zWayResultParser.parse(resultSet);
//			if (parsedResults.containsKey(this.dataEndpoint + this.motionEndpoint)) {
//				JSONObject motionResult = (JSONObject) this.zWayResultParser.parse(parsedResults.get(this.dataEndpoint + this.motionEndpoint).toString());
//				motionResult = (JSONObject) this.zWayResultParser.parse(motionResult.get("level").toString());
//				
//				setTriggered(Boolean.parseBoolean(motionResult.get("value").toString()));
//				
//				return true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		try {
			JSONObject parsedResults = (JSONObject) this.zWayResultParser.parse(resultSet);
			
			if (parsedResults.containsKey(endpoint)) {
				JSONObject result = (JSONObject) this.zWayResultParser.parse(parsedResults.get(endpoint).toString());
				result = (JSONObject) this.zWayResultParser.parse(result.get(key).toString());
				
				return result.get("value");
				//setTriggered(Boolean.parseBoolean(result.get("value").toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public synchronized boolean isTriggered() {
		return this.motion;
	}
	
	
	@Override
	public void setTriggered(boolean motion) {
		this.motion = motion;
		if (this.motion == true) {
			this.lastUpdated = System.currentTimeMillis();
		}
	}

	@Override
	public synchronized Float[] getTemperature() {
		return null;
	}

	@Override
	public synchronized Float[] getLuminiscence() {
		return null;
	}

	@Override
	public synchronized Float[] getHumidity() {
		return null;
	}
	
	@Override
	public Integer getBatteryLevel() {
		return this.battery;
	}

	@Override
	public long getLastUpdated() {
		return this.lastUpdated;
	}

	@Override
	public Zone getZone() {
		return this.zone;
	}

	protected boolean callParseResult(String path) {
		ZwayResultSet result = CallZwaveModule.speakToModule(path);
		
		if (result.getResponseCode() == 200) {
			return true;
		}
		
		return false;
	}
}
