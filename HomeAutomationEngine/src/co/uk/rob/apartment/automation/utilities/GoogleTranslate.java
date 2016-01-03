package co.uk.rob.apartment.automation.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

public class GoogleTranslate {

	private static Logger log = Logger.getLogger(GoogleTranslate.class);
	private static String file;
	
	public static boolean convertTextToWaveFile(String toSay, String fileLocation) {
		boolean responseOK = false;
		if (fileLocation == null) {
			fileLocation = "output.wav";
		}
		file = fileLocation;
		
		try {
			int responseCode = sendHTTPRequest("http://translate.google.com/translate_tts?tl=en&q=" + URLEncoder.encode(toSay, "UTF-8"));
			if (responseCode == 200) {
				responseOK = true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return responseOK;
	}
	
	private static int sendHTTPRequest(String url) {
		//fake response as Google have blocked us :(
		int responseCode = 200;
		
		if (responseCode != 200) {
			try {
				URL constructedUrl = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) constructedUrl.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(20000);
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36");
				
				responseCode = connection.getResponseCode();
				
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				File dstFile = new File("/home/pi/HomeAutomationAudio/" + file);
				FileOutputStream out = new FileOutputStream(dstFile);
	
				try {
					byte[] buf = new byte[1024];
					int len;
					while ((len = connection.getInputStream().read(buf)) > 0) {
					    out.write(buf, 0, len);
					}
				}
				finally {
					in.close();
					out.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		
		return responseCode;
	}

}
