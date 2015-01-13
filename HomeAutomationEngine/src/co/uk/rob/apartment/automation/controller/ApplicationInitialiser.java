package co.uk.rob.apartment.automation.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.interfaces.BatteryOperable;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.model.monitors.ApartmentActivityManager;
import co.uk.rob.apartment.automation.model.monitors.BatteryStatusMonitor;
import co.uk.rob.apartment.automation.model.monitors.BedroomOneEnvironmentMonitor;
import co.uk.rob.apartment.automation.model.monitors.LoungeEnvironmentMonitor;
import co.uk.rob.apartment.automation.model.monitors.NewUserMonitor;
import co.uk.rob.apartment.automation.model.monitors.ReportingDeviceProber;
import co.uk.rob.apartment.automation.utilities.BaselineDevices;
import co.uk.rob.apartment.automation.utilities.DailyFlagManager;
import co.uk.rob.apartment.automation.utilities.HomeAutomationAudioFiles;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.SpeechOrchestrationManager;
import co.uk.rob.apartment.automation.utilities.SystemVerifier;

/**
 * Servlet implementation class ApplicationInitialiser
 */
@WebServlet("/ApplicationInitialiser")
public class ApplicationInitialiser implements ServletContextListener {

	private Logger log = Logger.getLogger(ApplicationInitialiser.class);
	
	@Override
	public void contextInitialized(ServletContextEvent servlet) {
		log.info("SYSTEM: Automation system coming online..");
		InputStream is = servlet.getServletContext().getResourceAsStream("/WEB-INF/properties");
		Properties properties = new Properties();
		
		try {
			try {
				properties.load(is);
				// Save properties to application-wide accessible location
				HomeAutomationProperties.setProperties(properties);
				HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedRob", "false");
				HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedScarlett", "false");
				HomeAutomationProperties.setOrUpdateProperty("ApartmentWelcomeHome", "false");
				HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "false");
				HomeAutomationProperties.setOrUpdateProperty("LoungeBedroomMode", "false");
				HomeAutomationProperties.setOrUpdateProperty("RobRoomBedroomMode", "false");
				HomeAutomationProperties.setOrUpdateProperty("ContinuousAlarmMode", "false");
				HomeAutomationProperties.setOrUpdateProperty("RouterIP", "0.0.0.0");
				
				is = servlet.getServletContext().getResourceAsStream("/WEB-INF/audiofiles");
				properties = new Properties();
				properties.load(is);
				// Save audio files to application-wide accessible location
				HomeAutomationAudioFiles.setAudioFileLocations(properties);
			} catch (IOException e) {
				e.printStackTrace();
			}
        } finally {
            try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
		boolean online = SystemVerifier.doCheck();
		
		if (online) {
			baselineDevices();
			monitorApartmentActivity();
			monitorApartmentEnvironment();
			probeReportingDevices();
			implementDailyTimer();
			implementDailyIPChecker();
			monitorForNewUsers();
			monitorBatteryStatuses();
			playOnlineAudio();
		}
		else {
			log.info("Automation Engine not reachable - continuing without monitors and probes");
		}
		
		beginManagingDailyFlags();
	}
	
	private void baselineDevices() {
		BaselineDevices.trigger();
	}
	
	private void monitorApartmentActivity() {
		new ApartmentActivityManager().start();
	}
	
	private void monitorApartmentEnvironment() {
		new LoungeEnvironmentMonitor().start();
		new BedroomOneEnvironmentMonitor().start();
		//new PatioEnvironmentMonitor().start();
	}
	
	private void probeReportingDevices() {
		new ReportingDeviceProber().start();
	}
	
	private void implementDailyTimer() {
		Timer timer = new Timer("Daily battery monitor");
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				List<ReportingDevice> reportingDevices = DeviceListManager.getReportingDevices();
				for (ReportingDevice device : reportingDevices) {
					device.requestNewBatteryReport();
				}
				
				List<ControllableDevice> controllableDevices = DeviceListManager.getControllableDevices();
				for (ControllableDevice device : controllableDevices) {
					if (device instanceof BatteryOperable) {
						((BatteryOperable) device).requestNewBatteryReport();
					}
				}
				
				log.info("All battery operated devices probed for latest battery status, sleeping for one day");
			}
		};
		
		timer.schedule(task, 0, 86400000);
	}
	
	private void implementDailyIPChecker() {
		Timer timer = new Timer("Hourly IP monitor");
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				String newIP;
				BufferedReader bufferedReader = null;
				try {
					bufferedReader = new BufferedReader(new FileReader("/home/pi/apache-tomcat-7.0.40/webapps/ROOT/WEB-INF/ip"));
					newIP = bufferedReader.readLine();
					
					if (newIP != null) {
						String currentIP = HomeAutomationProperties.getProperty("RouterIP");
						
						if (!newIP.equals(currentIP)) {
							HomeAutomationProperties.setOrUpdateProperty("RouterIP", newIP);
							log.info("IP address of remote host has changed [" + newIP + "], sleeping for one hour");
						}
					}
					else {
						log.error("Could not read IP address from file");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		
		//sleep for one hour, 10 minute delay
		timer.schedule(task, 600000, 3600000);
	}
	
	private void monitorForNewUsers() {
		new NewUserMonitor().start();
	}
	
	private void monitorBatteryStatuses() {
		new BatteryStatusMonitor().start();
	}
	
	private void beginManagingDailyFlags() {
		new DailyFlagManager().start();
	}
	
	private void playOnlineAudio() {
		//Hello. The automation engine is now online. I will begin managing the apartment for you.
		new SpeechOrchestrationManager("The automation engine is now online.", false, false, false, null).start();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		log.info("SYSTEM: Automation system shutting down..");
	}

}
