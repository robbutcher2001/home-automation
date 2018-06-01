package co.uk.rob.apartment.automation.utilities.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LogParser {

	public static void main(String[] args) throws IOException, ParseException {
		File log = new File(args[0]);
		BufferedReader in = new BufferedReader(new FileReader(log));
		List<LogEntry> entries = new ArrayList<LogEntry>();
		
		try {
			String line;
			while ((line = in.readLine()) != null) {
				if (line.contains("PatioEnvironmentMonitor")) {
					LogEntry entry = new LogEntry();
					String[] portions = line.split(", ");
					
					DateFormat df = new SimpleDateFormat("dd/MM HH:mm");
					entry.setEntryDate(df.parse(portions[0].split(" - ")[1]));
					entry.setTemperature(portions[1]);
					entry.setLux(portions[2]);
					entry.setHumidity(portions[3]);
					
					entries.add(entry);
				}
			}
		}
		finally {
			in.close();
		}
		
		MakeChart.draw(entries);
	}

}
