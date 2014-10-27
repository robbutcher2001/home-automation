package co.uk.rob.apartment.automation.utilities.livefeeds;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TrainTimeCaller {
	
	public static String getCurrentTrainStatus(String requestedTrainTime) {
		boolean timesFound = false;
		Map<String, String> trainTimes = new HashMap<String, String>();
		String textToSpeak = "";
		Document doc = null;
		try {
			if (requestedTrainTime != null && !"".equals(requestedTrainTime)) {
				doc = Jsoup.connect("http://ojp.nationalrail.co.uk/service/timesandfares/NWD/LBG/today/" + requestedTrainTime + "/dep").get();
			}
			else {
				doc = Jsoup.connect("http://ojp.nationalrail.co.uk/service/timesandfares/NWD/LBG/today/0815/dep").get();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Elements trainTimeTable = doc.select("#oft");
		if (trainTimeTable.hasText()) {
			Elements returnedJourneys = trainTimeTable.select(".mtx");
			if (returnedJourneys.hasText()) {
				for (Iterator<Element> it = returnedJourneys.iterator(); it.hasNext();) {
					Element train = it.next();
					if (train.childNodeSize() > 8 && train.child(0) != null && train.child(7) != null) {
						Element trainTime = train.child(0);
						Element trainDisruption = train.child(7);
						Elements trainDisruptionDetail = trainDisruption.select(".journey-status");
						if (trainDisruptionDetail.hasText() && trainDisruptionDetail.get(0) != null && !"".equals(trainDisruptionDetail.get(0).html())) {
							if (trainTime.html() != null && !"".equals(trainTime.html())) {
								Elements trainDisruptionStatus = trainDisruptionDetail.select(".status");
								if (trainDisruptionStatus.hasText()) {
									timesFound = true;
									trainTimes.put(trainTime.html(), trainDisruptionStatus.text().replaceAll("mins", "minutes"));
								}
								else {
									trainDisruptionStatus = trainDisruptionDetail.select("p");
									if (trainDisruptionStatus.hasText()) {
										timesFound = true;
										trainTimes.put(trainTime.html(), trainDisruptionStatus.text());
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (timesFound) {
			for (Map.Entry<String, String> entry : trainTimes.entrySet()) {
				if ("07:49".equals(entry.getKey()) || "08:21".equals(entry.getKey())) {
					if (entry.getValue().startsWith("bus")) {
						textToSpeak = "Your usual " + entry.getKey() + " train is unfortunately a " + entry.getValue() + " today.";
					}
					else {
						textToSpeak = "Your usual " + entry.getKey() + " train is " + entry.getValue() + " today.";
					}
					break;
				}
			}
			
			if ("".equals(textToSpeak)) {
				textToSpeak = "I'm afraid I cannot find information on your usual train.";
			}
		}
		else {
			textToSpeak = "I couldn't get any details on your normal train times today. Sorry.";
		}
		
		return textToSpeak;
	}
}
