package co.uk.rob.apartment.automation.utilities.livefeeds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import co.uk.rob.apartment.automation.model.M25TrafficResults;

public class M25TrafficReportCaller {

	private static List<M25TrafficResults> m25Incidents;
	
	public static String getM25TrafficReport() {
		m25Incidents = new ArrayList<M25TrafficResults>();
		
		String textToSpeak = "If you are driving to Maidenhead, the M25 is looking okay.";
		URL url;
		try {
			url = new URL("http://hatrafficinfo.dft.gov.uk/feeds/rss/UnplannedEvents/M25.xml");
			URLConnection conn = url.openConnection();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(conn.getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("item");
			if (nList != null && nList.getLength() > 0) {
				for (int index=0; index<nList.getLength(); index++) {
					Node node = nList.item(index);
					if (node != null) {
						NodeList childNodes = node.getChildNodes();
						for (int nodes=0; nodes<childNodes.getLength(); nodes++) {
							Node child = childNodes.item(nodes);
							if (child != null && "title".equals(child.getNodeName())) {
								M25TrafficResults incident = attemptMatchingJunctions(child.getTextContent());
								if (incident != null) {
									int catCount = 0;
									for (nodes=0; nodes<childNodes.getLength(); nodes++) {
										child = childNodes.item(nodes);
										if (child != null && "category".equals(child.getNodeName())) {
											if (catCount > 0) {
												incident.setCategory(child.getTextContent());
											}
											catCount++;
										}
									}
									m25Incidents.add(incident);
								}
							}
						}
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (m25Incidents.size() > 0) {
			textToSpeak = constructSpeechFromIncidents();
		}
		
		return textToSpeak;
	}
	
	private static M25TrafficResults attemptMatchingJunctions(String title) {
		M25TrafficResults incident = null;
		
		if (title != null && !"".equals(title)) {
			Pattern patternMatch = Pattern.compile("J(6|7|8|9|10|11|12|13|14|15)");
			Matcher match = patternMatch.matcher(title);
			if (match.find() && !title.contains("anti-clockwise")) {
				if (incident == null) {
					incident = new M25TrafficResults();
				}
				incident.setStartJunction(match.group(0).replace("J", "junction "));
			}
			
			if (match.find() && !title.contains("anti-clockwise")) {
				if (incident == null) {
					incident = new M25TrafficResults();
				}
				incident.setEndJunction(match.group(0).replace("J", "junction "));
			}
		}
		
		return incident;
	}
	
	private static String constructSpeechFromIncidents() {
		String textToSpeak = "";
		
		for (M25TrafficResults incident : m25Incidents) {
			if (!"".equals(incident.getEndJunction())) {
				textToSpeak += "There is a " + incident.getCategory().replace(" - ", " ").toLowerCase() + " on the M25 between " + 
						incident.getStartJunction() + " and " + incident.getEndJunction() + ". ";
			}
			else {
				textToSpeak += "There is a " + incident.getCategory().replace(" - ", " ").toLowerCase() + " on the M25 starting at " + 
						incident.getStartJunction() + ". ";
			}
		}
		
		if (textToSpeak.length() > 99) {
			textToSpeak = textToSpeak.substring(0, 99);
		}
		
		return textToSpeak;
	}

}
