package co.uk.rob.apartment.automation.utilities;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;

public class Polly {
	
	private static Logger log = Logger.getLogger(Polly.class);
	private static AmazonPolly pollyClient;
	
	public static boolean convertTextToWaveFile(String toSay, String fileLocation) {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTimeout(20000);
        
		pollyClient = AmazonPollyClientBuilder.standard()
				.withCredentials(new EnvironmentVariableCredentialsProvider())
				.withClientConfiguration(clientConfiguration)
				.withRegion(Regions.EU_WEST_1)
				.build();
		
		return sendHTTPRequest(toSay, fileLocation);
	}
	
	private static boolean sendHTTPRequest(String toSay, String fileLocation) {
		SynthesizeSpeechRequest synthRequset = new SynthesizeSpeechRequest()
			.withText(toSay)
			.withVoiceId("Amy")
			.withOutputFormat("mp3");
		SynthesizeSpeechResult synthResponse = pollyClient.synthesizeSpeech(synthRequset);
				
		boolean response = false;
		
		if (synthResponse.getAudioStream() != null) {
			response = true;
		}
		
		try {
			File dstFile = new File("/home/pi/HomeAutomationAudio/" + fileLocation);
			FileOutputStream out = new FileOutputStream(dstFile);

			try {
				byte[] buf = new byte[1024];
				int len;
				while ((len = synthResponse.getAudioStream().read(buf)) > 0) {
				    out.write(buf, 0, len);
				}
			}
			finally {
				out.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return response;
	}
}