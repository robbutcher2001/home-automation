package co.uk.rob.apartment.automation.utilities;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.utilities.livefeeds.LiveWeatherCaller;
import co.uk.rob.apartment.automation.utilities.livefeeds.M25TrafficReportCaller;
import co.uk.rob.apartment.automation.utilities.livefeeds.TrainTimeCaller;

public class SpeechOrchestrationManager extends Thread {
	
	private Logger log = Logger.getLogger(SpeechOrchestrationManager.class);
	private String initialWords = null;
	private boolean weather = false;
	private boolean trainTimes = false;
	private boolean traffic = false;
	private String requestedTrainTime;
	
	public SpeechOrchestrationManager(String initialWords, boolean weather, boolean trainTimes, boolean traffic, String requestedTrainTime) {
		this.initialWords = initialWords;
		this.weather = weather;
		this.trainTimes = trainTimes;
		this.traffic = traffic;
		this.requestedTrainTime = requestedTrainTime;
	}
	
	@Override
	public void run() {
		GoogleTranslate.convertTextToWaveFile(initialWords, HomeAutomationAudioFiles.getAudioFileLocation("firstAudioFile"));
		boolean played = AudioHelper.playAudio(HomeAutomationAudioFiles.getAudioFileLocation("firstAudioFile"));
		if (played) {
			log.info("Played generic text through speaker");
		}
		else {
			log.error("Could not speak following text through speaker: \"" + initialWords + "\"");
		}
		
		if (!weather && !trainTimes && !traffic) {
			return;
		}
		else {
			played = false;
		}
		
		if (weather) {
			log.info("Attemping to retrieve latest weather information for today");
		}
		
		if (trainTimes) {
			log.info("Attemping to retrieve latest train time information for today at " + this.requestedTrainTime);
		}
		
		if (traffic) {
			log.info("Attemping to retrieve latest traffic information for the M25 today");
		}
		
		String[] filesToPlay = new String[3];
		
		if (weather) {
			String textToSpeak = LiveWeatherCaller.getCurrentWeatherInLondon();
			GoogleTranslate.convertTextToWaveFile(textToSpeak, HomeAutomationAudioFiles.getAudioFileLocation("firstAudioFile"));
			filesToPlay[0] = HomeAutomationAudioFiles.getAudioFileLocation("firstAudioFile");
		}
		
		if (trainTimes) {
			String textToSpeak = TrainTimeCaller.getCurrentTrainStatus(this.requestedTrainTime);
			GoogleTranslate.convertTextToWaveFile(textToSpeak, HomeAutomationAudioFiles.getAudioFileLocation("secondAudioFile"));
			filesToPlay[1] = HomeAutomationAudioFiles.getAudioFileLocation("secondAudioFile");
		}
		
		if (traffic) {
			String textToSpeak = M25TrafficReportCaller.getM25TrafficReport();
			GoogleTranslate.convertTextToWaveFile(textToSpeak, HomeAutomationAudioFiles.getAudioFileLocation("thirdAudioFile"));
			filesToPlay[2] = HomeAutomationAudioFiles.getAudioFileLocation("thirdAudioFile");
		}
		
		played = AudioHelper.playAudio(filesToPlay);
		if (played) {
			if (weather) {
				log.info("Played latest weather information through speaker");
			}
			
			if (trainTimes) {
				log.info("Played train time information through speaker");
			}
			
			if (traffic) {
				log.info("Played M25 information through speaker");
			}
		}
		else {
			if (weather) {
				log.error("Could not speak latest weather information through speaker");
			}
			
			if (trainTimes) {
				log.error("Could not speak train time information through speaker");
			}
			
			if (traffic) {
				log.error("Could not speak M25 information through speaker");
			}
		}
	}
}
