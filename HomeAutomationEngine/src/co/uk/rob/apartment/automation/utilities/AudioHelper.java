package co.uk.rob.apartment.automation.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rob
 *
 */
public class AudioHelper {
	
	public static boolean playAudio(String fileLocation) {
		String[] fileLocations = {fileLocation};
		return playAudio(fileLocations);
	}

	public static boolean playAudio(String[] fileLocations) {
		boolean result = false;
		
		String root = HomeAutomationAudioFiles.getAudioFileLocation("root");
		
		if (root != null && !"".equals(root) && fileLocations != null && fileLocations.length > 0) {
			List<String> args = new ArrayList<String>();
			args.add("mplayer");
			args.add("-af");
			args.add("volume=12");
			
			for (String fileLocation : fileLocations) {
				if (fileLocation != null && !"".equals(fileLocation)) {
					args.add(root + fileLocation);
				}
			}
			
			ProcessBuilder pb = new ProcessBuilder(args);
			try {
				Process p = pb.start();
				int exitCode = p.waitFor();
				if (exitCode == 0) {
					result = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
