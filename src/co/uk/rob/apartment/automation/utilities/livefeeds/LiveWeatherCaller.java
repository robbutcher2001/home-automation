package co.uk.rob.apartment.automation.utilities.livefeeds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LiveWeatherCaller {

	private static Map<String, String> weatherType;
	
	static {
		weatherType = new HashMap<String, String>();
		weatherType.put("NA", "not available, sorry about that. ");
		weatherType.put("0", "going to be a clear night ");
		weatherType.put("1", "going to be a nice sunny day! ");
		weatherType.put("2", "going to be partly cloudy ");
		weatherType.put("3", "going to be partly cloudy ");
		weatherType.put("4", "not available, sorry about that. ");
		weatherType.put("5", "going to be a little misty ");
		weatherType.put("6", "going to be a foggy ");
		weatherType.put("7", "going to be a cloudy ");
		weatherType.put("8", "going to be a little overcast ");
		weatherType.put("9", "going to consist of some light rain ");
		weatherType.put("10", "going to consist of some light rain ");
		weatherType.put("11", "going to be a bit drizzly ");
		weatherType.put("12", "going to consist of some light rain ");
		weatherType.put("13", "going to be heavy rain ");
		weatherType.put("14", "going to be heavy rain ");
		weatherType.put("15", "going to be heavy rain ");
		weatherType.put("16", "going to be a lovely and horrible sleet shower! ");
		weatherType.put("17", "going to be a lovely and horrible sleet shower! ");
		weatherType.put("18", "going to consist of some sleet ");
		weatherType.put("19", "going to be a hail stone shower! ");
		weatherType.put("20", "going to be a hail stone shower! ");
		weatherType.put("21", "going to consist of some hail ");
		weatherType.put("22", "going to be a light snow shower ");
		weatherType.put("23", "going to be a light snow shower ");
		weatherType.put("24", "going to snow! Only lightly though ");
		weatherType.put("25", "going to be a heavy snow shower ");
		weatherType.put("26", "going to be a heavy snow shower ");
		weatherType.put("27", "going to snow! And it's going to be heavy! ");
		weatherType.put("28", "going to be a gloomy thunder shower ");
		weatherType.put("28", "going to be a gloomy thunder shower ");
		weatherType.put("28", "going to be horrid thunder and lightening ");
	}
	
	public static String getCurrentWeatherInLondon() {
		final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		final String date = sdf.format(Calendar.getInstance().getTime());
		
		String textToSpeak = "";
		URL url;
		try {
			url = new URL("http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/353605?res=daily&key=a4d1ac06-3f2e-490a-88b6-70acfe9e5913");
			URLConnection conn = url.openConnection();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(conn.getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Period");
			if (nList != null && nList.getLength() > 1) {
				Node node = nList.item(0);
				if (node != null) {
					NamedNodeMap attrMap = node.getAttributes();
					for (int i = 0; i < attrMap.getLength(); i++) {
						Node attr = attrMap.item(i);
						if ("value".equals(attr.getNodeName()) && attr.getNodeValue() != null && attr.getNodeValue().equalsIgnoreCase(date)) {
							NodeList childNodes = node.getChildNodes();
							if (childNodes != null && childNodes.getLength() > 0) {
								Node morningNode = childNodes.item(0);
								if (morningNode != null) {
									NamedNodeMap morningNodeAttrMap = morningNode.getAttributes();
									Node w = morningNodeAttrMap.getNamedItem("W");
									Node Dm = morningNodeAttrMap.getNamedItem("Dm");
									Node FDm = morningNodeAttrMap.getNamedItem("FDm");
									Node PPd = morningNodeAttrMap.getNamedItem("PPd");
									
									if (w != null && w.getNodeValue() != null) {
										textToSpeak += "<p>Today's weather is " + weatherType.get(w.getNodeValue());
										if ("NA".equals(w.getNodeValue()) || "4".equals(w.getNodeValue())) {
											break;
										}
									}
									
									if (Dm != null && Dm.getNodeValue() != null) {
										textToSpeak += "with a high of " + Dm.getNodeValue() + " degrees Celsius ";
									}
									
									if (FDm != null && FDm.getNodeValue() != null) {
										textToSpeak += "and a feels-like temperature of " + FDm.getNodeValue() + ". ";
									}

									if (PPd != null && PPd.getNodeValue() != null) {
										textToSpeak += "The chance of rain today is about " + PPd.getNodeValue() + " percent.</p> ";
									}
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
		
		if ("".equals(textToSpeak)) {
			textToSpeak = "<p><prosody pitch=\"+40%\">Shit.</prosody> I couldn't find any weather info <prosody pitch=\"-10%\">for you. Sorry. </prosody></p>";
		}
		
		return textToSpeak;
	}
}
