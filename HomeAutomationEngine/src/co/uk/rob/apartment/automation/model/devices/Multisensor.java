package co.uk.rob.apartment.automation.model.devices;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractReportingDevice;
import co.uk.rob.apartment.automation.model.interfaces.ActivityHandler;

public class Multisensor extends AbstractReportingDevice {

	private Logger log = Logger.getLogger(Multisensor.class);
	
	// Endpoints
	protected String temperatureEndpoint = "49.data.1";
	protected String luminiscenceEndpoint = "49.data.3";
	protected String humidityEndpoint = "49.data.5";
	
	// Environment variables
	protected Float[] temperature = {0f, 0f, 0f};
	protected Float[] luminiscence = {0f, 0f, 0f};
	protected Float[] humidity = {0f, 0f, 0f};
	
	public Multisensor(String batteryUpdateEndpoint, String requestNewReportEndpoint, String dataEndpoint, ActivityHandler handler, Zone zone) {
		super(batteryUpdateEndpoint, dataEndpoint, handler, zone);
		super.requestNewReportEndpoint = requestNewReportEndpoint;
	}
	
	/**
	 * Apply motion update plus any multisensor updates
	 */
	@Override
	public synchronized boolean applyNewReport(String resultSet) {
		boolean applied = super.parseBatteryValue(resultSet);
		applied = super.parseMotionValue(resultSet);
		
		Object result = super.parseReportedValue(this.dataEndpoint + this.temperatureEndpoint, resultSet, "val");
		//Float newTemperature = parseSensorValue(this.dataEndpoint + this.temperatureEndpoint, resultSet, "val");
		if (result != null) {
			try {
				Float newTemperature = Float.parseFloat(result.toString());
				if (applied) {
					shiftTemperature(newTemperature);
				}
				else {
					applied = shiftTemperature(newTemperature);
				}
			}
			catch (Exception e) {
				//no op
				log.error("Could not parse temperature response as Float");
			}
		}
		
		result = super.parseReportedValue(this.dataEndpoint + this.luminiscenceEndpoint, resultSet, "val");
		//Float newLuminiscence = parseSensorValue(this.dataEndpoint + this.luminiscenceEndpoint, resultSet, "val");
		if (result != null) {
			try {
				Float newLuminiscence = Float.parseFloat(result.toString());
				if (applied) {
					shiftLuminiscence(newLuminiscence);
				}
				else {
					applied = shiftLuminiscence(newLuminiscence);
				}
			}
			catch (Exception e) {
				//no op
				log.error("Could not parse luminiscence response as Float");
			}
		}
		
		result = super.parseReportedValue(this.dataEndpoint + this.humidityEndpoint, resultSet, "val");
		//Float newHumidity = parseSensorValue(this.dataEndpoint + this.humidityEndpoint, resultSet, "val");
		if (result != null) {
			try {
				Float newHumidity = Float.parseFloat(result.toString());
				if (applied) {
					shiftHumidity(newHumidity);
				}
				else {
					applied = shiftHumidity(newHumidity);
				}
			}
			catch (Exception e) {
				//no op
				log.error("Could not parse humidity response as Float");
			}
		}
		
		if (applied) {
			this.handler.handleActivity(this);
		}
		
		return applied;
	}
	
	@Override
	public synchronized Float[] getTemperature() {
		return this.temperature;
	}

	@Override
	public synchronized Float[] getLuminiscence() {
		return this.luminiscence;
	}

	@Override
	public synchronized Float[] getHumidity() {
		return this.humidity;
	}
	
//	private Float parseSensorValue(String endpoint, String resultSet, String key) {
//		try {
//			JSONObject parsedResults = (JSONObject) this.zWayResultParser.parse(resultSet);
//			
//			if (parsedResults.containsKey(endpoint)) {
//				JSONObject result = (JSONObject) this.zWayResultParser.parse(parsedResults.get(endpoint).toString());
//				result = (JSONObject) this.zWayResultParser.parse(result.get(key).toString());
//				
//				return Float.parseFloat(result.get("value").toString());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
	private boolean shiftTemperature(Float newTemperature) {
		if (!newTemperature.equals(this.temperature[2])) {
			this.temperature[0] = this.temperature[1];
			this.temperature[1] = this.temperature[2];
			this.temperature[2] = newTemperature;
			
			//log.info("Temp readings for " + this.dataEndpoint + ": " + Arrays.toString(this.temperature));
			
			return true;
		}
		
		return false;
	}
	
	private boolean shiftLuminiscence(Float newLuminiscence) {
		if (!newLuminiscence.equals(this.luminiscence[2]) || newLuminiscence > 999 || newLuminiscence < 1) {
			this.luminiscence[0] = this.luminiscence[1];
			this.luminiscence[1] = this.luminiscence[2];
			this.luminiscence[2] = newLuminiscence;
			
			//log.info("Lux readings for " + this.dataEndpoint + ": " + Arrays.toString(this.luminiscence));
			
			return true;
		}
		
		return false;
	}
	
	private boolean shiftHumidity(Float newHumidity) {
		if (!newHumidity.equals(this.humidity[2])) {
			this.humidity[0] = this.humidity[1];
			this.humidity[1] = this.humidity[2];
			this.humidity[2] = newHumidity;
			
			//log.info("Humidity readings for " + this.dataEndpoint + ": " + Arrays.toString(this.humidity));
			
			return true;
		}
		
		return false;
	}
}
