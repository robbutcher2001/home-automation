package co.uk.rob.apartment.automation.utilities.livefeeds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BBCNewsCaller {
	
	public static String getCurrentUKNewsHeadlines() {
		int newsItemCount = 3;
		String textToSpeak = "";
		URL url;
		try {
			url = new URL("http://feeds.bbci.co.uk/news/uk/rss.xml?edition=uk");
			URLConnection conn = url.openConnection();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(conn.getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("channel");
			if (nList != null && nList.getLength() > 0) {
				Node node = nList.item(0);
				if (node != null) {
					NodeList childNodes = node.getChildNodes();
					if (childNodes != null && childNodes.getLength() > 0) {
						for (int i = 0; i < childNodes.getLength() && newsItemCount > 0; i++) {
							if ("item".equals(childNodes.item(i).getNodeName())) {
								newsItemCount--;
								NodeList newsItemNodes = childNodes.item(i).getChildNodes();
								for (int j = 0; j < newsItemNodes.getLength(); j++) {
									if ("title".equals(newsItemNodes.item(j).getNodeName())) {
										textToSpeak += "<p><prosody pitch=\"+5%\">" + newsItemNodes.item(j).getTextContent() + "</prosody></p>";
									}
								}
								for (int j = 0; j < newsItemNodes.getLength(); j++) {
									if ("description".equals(newsItemNodes.item(j).getNodeName())) {
										textToSpeak += "<p><prosody pitch=\"-15%\">" + newsItemNodes.item(j).getTextContent() + "</prosody></p>";
									}
								}
								
								if (newsItemCount > 0) {
									textToSpeak += "<break time=\"1s\"/><p>Next article. </p>";
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
			textToSpeak = "<p><prosody pitch=\"+40%\">Shit.</prosody> I couldn't find any news headlines <prosody pitch=\"-10%\">for you. Sorry. </prosody></p>";
		}
		
		return textToSpeak;
	}
}
