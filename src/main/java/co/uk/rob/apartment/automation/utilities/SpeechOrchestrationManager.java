package co.uk.rob.apartment.automation.utilities;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.utilities.livefeeds.BBCNewsCaller;
import co.uk.rob.apartment.automation.utilities.livefeeds.LiveWeatherCaller;
import co.uk.rob.apartment.automation.utilities.livefeeds.TrainTimeCaller;

public class SpeechOrchestrationManager extends Thread {

	private Logger log = Logger.getLogger(SpeechOrchestrationManager.class);
	private String initialWords = null;
	private boolean weather = false;
	private boolean trainTimes = false;
	private boolean news = false;
	private String requestedTrainTime;
	private String stationsString;
	private String verboseTargetStation;

	public SpeechOrchestrationManager(String initialWords, boolean weather, boolean trainTimes,
			boolean news, String requestedTrainTime, String stationsString, String verboseTargetStation) {
		this.initialWords = initialWords;
		this.weather = weather;
		this.trainTimes = trainTimes;
		this.news = news;
		this.requestedTrainTime = requestedTrainTime;
		this.stationsString = stationsString;
		this.verboseTargetStation = verboseTargetStation;
	}

	@Override
	public void run() {
		String textToSpeak = this.initialWords;

		if (weather) {
			log.info("Attemping to retrieve latest weather information for today");
			textToSpeak += LiveWeatherCaller.getCurrentWeatherInLondon();
		}

		if (trainTimes) {
			log.info("Attemping to retrieve latest train time information for today at " + this.requestedTrainTime);
			textToSpeak += TrainTimeCaller.getCurrentTrainStatus(this.requestedTrainTime, this.stationsString, this.verboseTargetStation);
		}

		if (news) {
			log.info("Attemping to retrieve latest news headlines for today");
			textToSpeak += "<p>I'll now read you today's top news articles. </p>";
			textToSpeak += BBCNewsCaller.getCurrentUKNewsHeadlines();
		}

		if (weather || trainTimes || news) {
			textToSpeak += "<p>That's all from me this morning. Have a nice day.</p>";
		}

		String fileToPlay = HomeAutomationAudioFiles.getAudioFileLocation("firstAudioFile");
		Polly.convertTextToWaveFile(textToSpeak, fileToPlay);

		boolean played = AudioHelper.playAudio(fileToPlay);
		if (played) {
			log.info("Played generic text through speaker: \"" + this.initialWords + "\"");

			if (weather) {
				log.info("Played latest weather information through speaker");
			}

			if (trainTimes) {
				log.info("Played train time information through speaker");
			}

			if (news) {
				log.info("Played BBC News information through speaker");
			}
		}
		else {
			log.error("Could not speak following text through speaker: \"" + this.initialWords + "\"");

			if (weather) {
				log.error("Could not speak latest weather information through speaker");
			}

			if (trainTimes) {
				log.error("Could not speak train time information through speaker");
			}

			if (news) {
				log.error("Could not speak BBC News information through speaker");
			}
		}
	}
}
