package co.uk.rob.apartment.automation.utilities;

import java.util.Properties;

public class HomeAutomationProperties {
	
	private static Properties properties;
	
	public static synchronized void setProperties(Properties props) {
		properties = props;
	}
	
	public static synchronized void setOrUpdateProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public static synchronized String getProperty(String key) {
		return properties.getProperty(key);
	}
}
