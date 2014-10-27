package co.uk.rob.apartment.automation.utilities;


import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTML {
	public static void main(String[] a) {
		Document doc = null;
		try {
			doc = Jsoup.connect("http://ojp.nationalrail.co.uk/service/timesandfares/NWD/LBG/today/0815/dep").get();
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
									System.out.println(trainTime.html() + " is " + trainDisruptionStatus.text().replaceAll("mins", "minutes"));
								}
								else {
									trainDisruptionStatus = trainDisruptionDetail.select("p");
									if (trainDisruptionStatus.hasText()) {
										System.out.println(trainTime.html() + " is " + trainDisruptionStatus.text());
									}
								}
							}
						}
					}
				}
			}
		}
		else {
			System.out.println("No journeys found");
		}
	}
}
