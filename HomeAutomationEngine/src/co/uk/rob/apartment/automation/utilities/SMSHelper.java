package co.uk.rob.apartment.automation.utilities;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

public class SMSHelper {
	
	private static Logger log = Logger.getLogger(SMSHelper.class);

	public static boolean sendSMS(String to, String message) {
		boolean result = false;
		
		String endpoint = HomeAutomationProperties.getProperty("smsEndpoint");
		String key = HomeAutomationProperties.getProperty("smsKey");
		
		if (to != null && message != null && endpoint != null && key != null) {
			
			if (to.startsWith("0")) {
				to = to.replaceFirst("0", "44");
			}
			
			StringBuilder fullUrl = new StringBuilder();
			
			fullUrl.append(endpoint);
			try {
				fullUrl.append("?key=").append(URLEncoder.encode(key, "UTF-8"));
				fullUrl.append("&to=").append(URLEncoder.encode(to, "UTF-8"));
				fullUrl.append("&content=").append(URLEncoder.encode(message, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			int responseCode = sendHTTPRequest(fullUrl.toString());
			
			if (responseCode == 200) {
				result = true;
			}
			else {
				log.info("Could not send SMS to " + to + ", response code was " + responseCode);
			}
		}
		else {
			log.info("Could not send SMS, some params were null");
		}
		
		return result;
	}
	
	private static int sendHTTPRequest(String url) {
		int responseCode = 0;
		log.info("Sending SMS: " + url);
		
		try {
			URL constructedUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) constructedUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(20000);
			
			responseCode = connection.getResponseCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return responseCode;
	}
}
