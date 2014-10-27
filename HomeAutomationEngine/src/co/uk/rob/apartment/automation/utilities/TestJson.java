package co.uk.rob.apartment.automation.utilities;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import co.uk.rob.apartment.automation.model.ZwayResultSet;

public class TestJson {
	
	private static Logger log = Logger.getLogger(TestJson.class);
	private static long lastUpdate = 0l;
	
	public static void main(String[] args) {
		while (true) {
			String path = "http://192.168.1.40:8083/ZWaveAPI/Data/" + lastUpdate;
			ZwayResultSet result = CallZwaveModule.speakToModule(path);
			lastUpdate = System.currentTimeMillis()/1000;
			log.info(path);
			log.info(result.getJsonResponse());
			
			JSONParser parser = new JSONParser();
			
			JSONObject parsedResults = null;
			try {
				parsedResults = (JSONObject) parser.parse(result.getJsonResponse());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			log.info(parsedResults.get("devices.10.instances.0.commandClasses.48.data.1"));
			try {
				Object newResult = parsedResults.get("devices.10.instances.0.commandClasses.48.data.1");
				if (newResult != null) {
					JSONObject device = (JSONObject) parser.parse(parsedResults.get("devices.10.instances.0.commandClasses.48.data.1").toString());
					device = (JSONObject) parser.parse(device.get("level").toString());
					log.info(device.get("value"));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
