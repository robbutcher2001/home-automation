package co.uk.rob.apartment.automation.utilities;

import java.util.Properties;

public class HomeAutomationAudioFiles {
	
	private static Properties audioFiles;
	
	public static void setAudioFileLocations(Properties props) {
		audioFiles = props;
	}
	
	public static String getAudioFileLocation(String key) {
		return audioFiles.getProperty(key);
	}
}
