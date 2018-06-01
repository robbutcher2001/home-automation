package co.uk.rob.apartment.automation.utilities.logs;

import java.util.Date;

/**
 * @author Rob
 *
 */
public class LogEntry {
	private Date date;
	private String temperature;
	private String lux;
	private String humidity;
	
	public Date getEntryDate() {
		return date;
	}
	
	public void setEntryDate(Date date) {
		this.date = date;
	}
	
	public String getTemperature() {
		return temperature;
	}
	
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	
	public String getLux() {
		return lux;
	}
	
	public void setLux(String lux) {
		this.lux = lux;
	}
	
	public String getHumidity() {
		return humidity;
	}
	
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
}
