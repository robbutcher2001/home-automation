package co.uk.rob.apartment.automation.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.ZwayResultSet;
import co.uk.rob.apartment.automation.model.exceptions.ZwaveModuleResponseException;

public class CallZwaveModule {
	
	private static Logger log = Logger.getLogger(CallZwaveModule.class);
	
	/**
	 * Call to Zwave Module, if response is null then the module could not process the request
	 * @param url
	 * @return
	 */
	public static ZwayResultSet speakToModule(String url) {
		ZwayResultSet results = null;
		try {
			URL constructedUrl = new URL(url);
			HttpURLConnection moduleConnection = (HttpURLConnection) constructedUrl.openConnection();
			moduleConnection.setRequestMethod("POST");
			moduleConnection.setConnectTimeout(20000);
			
			results = new ZwayResultSet();
			results.setResponseCode(moduleConnection.getResponseCode());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(moduleConnection.getInputStream()));
			StringBuffer responseString = new StringBuffer();
			
			try {
				String line;
				while ((line = in.readLine()) != null) {
					responseString.append(line);
				}
			}
			finally {
				in.close();
			}
			
			results.setJsonResponse(responseString.toString());
			
			if (results.getResponseCode() != 200) {
				log.error("Response code was not 200 [" + responseString.toString() + "]");
				throw new ZwaveModuleResponseException("Response code was not 200 [" + responseString.toString() + "]");
			}
			
			if (results.getJsonResponse().isEmpty()) {
				log.error("No results received from module");
				throw new ZwaveModuleResponseException("No results received from module");
			}
		} catch (ZwaveModuleResponseException zmue) {
			zmue.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return results;
	}
}
