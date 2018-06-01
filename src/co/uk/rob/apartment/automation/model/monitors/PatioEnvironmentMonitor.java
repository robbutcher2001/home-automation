package co.uk.rob.apartment.automation.model.monitors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;

public class PatioEnvironmentMonitor extends Thread {
	
	private Logger log = Logger.getLogger(PatioEnvironmentMonitor.class);
	
	private ReportingDevice reportingDevice;
	
	public PatioEnvironmentMonitor() {
		reportingDevice = DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(0);
		
		log.info("Patio environment monitor started");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			Calendar now = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat("dd/MM HH:mm");
			
			Float temperature = reportingDevice.getTemperature()[2];
			Float luminiscence = reportingDevice.getLuminiscence()[2];
			Float humidity = reportingDevice.getHumidity()[2];
			
			if (temperature != null && luminiscence != null && humidity != null) {
				log.info(df.format(now.getTime()) + ", " + temperature + ", " + luminiscence + ", " + humidity);
			}
			
			try {
				int fourMinutes = 60000 * 4;
				Thread.sleep(fourMinutes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
