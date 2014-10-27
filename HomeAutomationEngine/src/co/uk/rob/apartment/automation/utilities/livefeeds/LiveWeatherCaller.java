package co.uk.rob.apartment.automation.utilities.livefeeds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LiveWeatherCaller {

	public static String getCurrentWeatherInLondon() {
		String textToSpeak = "";
		URL url;
		try {
			url = new URL("http://weather.yahooapis.com/forecastrss?w=27899477&u=c");
			URLConnection conn = url.openConnection();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(conn.getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("yweather:forecast");
			if (nList != null && nList.getLength() > 1) {
				Node node = nList.item(0);
				if (node != null) {
					NamedNodeMap attrMap = node.getAttributes();
					for (int i = 0; i < attrMap.getLength(); i++) {
						Node attr = attrMap.item(i);
						if ("text".equals(attr.getNodeName()) && attr.getNodeValue() != null && !"".equals(attr.getNodeValue())) {
							textToSpeak += "Today is " + attr.getNodeValue().toLowerCase();
							break;
						}
					}
					
					for (int i = 0; i < attrMap.getLength(); i++) {
						Node attr = attrMap.item(i);
						if ("high".equals(attr.getNodeName()) && attr.getNodeValue() != null && !"".equals(attr.getNodeValue())) {
							textToSpeak += " with a high of " + attr.getNodeValue().toLowerCase();
							break;
						}
					}
					
					for (int i = 0; i < attrMap.getLength(); i++) {
						Node attr = attrMap.item(i);
						if ("low".equals(attr.getNodeName()) && attr.getNodeValue() != null && !"".equals(attr.getNodeValue())) {
							textToSpeak += " and a low of " + attr.getNodeValue().toLowerCase() + ".";
							break;
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
		
		return textToSpeak;
	}

}
