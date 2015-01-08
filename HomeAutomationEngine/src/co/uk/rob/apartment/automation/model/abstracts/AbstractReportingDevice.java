package co.uk.rob.apartment.automation.model.abstracts;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.ZwayResultSet;
import co.uk.rob.apartment.automation.model.interfaces.ActivityHandler;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CallZwaveModule;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public abstract class AbstractReportingDevice extends AbstractDevice implements ReportingDevice {
	
	private Logger log = Logger.getLogger(AbstractReportingDevice.class);
	
	// Endpoints
	protected String host = HomeAutomationProperties.getProperty("host");
	protected String requestNewReportEndpoint;
	protected String batteryUpdateEndpoint;
	protected String motionEndpoint = "48.data.1";
	
	// Environment variables
	private boolean motion = true;
	protected ActivityHandler handler;
	protected long lastUpdated = System.currentTimeMillis();
	protected boolean resultProcessed = false;
	
	// Zone
	protected Zone zone;
	
	public AbstractReportingDevice(String batteryUpdateEndpoint, String dataEndpoint, ActivityHandler handler, Zone zone) {
		this.batteryUpdateEndpoint = batteryUpdateEndpoint;
		this.dataEndpoint = dataEndpoint;
		this.handler = handler;
		this.zone = zone;
	}

	@Override
	public synchronized void requestNewReport() {
		if (requestNewReportEndpoint != null) {
			callParseResult(host + requestNewReportEndpoint);
		}
	}
	
	@Override
	public synchronized void requestNewBatteryReport() {
		if (batteryUpdateEndpoint != null) {
			callParseResult(host + batteryUpdateEndpoint);
		}
	}

	protected boolean parseMotionValue(String resultSet) {
		// To handle any possible motion detected
		boolean applied = false;
		Object result = parseReportedValue(this.dataEndpoint + this.motionEndpoint, resultSet, "level");
		
		if (result != null) {
			try {
				boolean newMotion = Boolean.parseBoolean(result.toString());
				setTriggered(newMotion);
				applied = true;
			}
			catch (Exception e) {
				//no op
				log.error("Could not parse motion response as Boolean");
			}
		}
		
		return applied;
	}
	
	@Override
	public synchronized boolean isTriggered() {
		return this.motion;
	}
	
	
	@Override
	public void setTriggered(boolean motion) {
		this.motion = motion;
		if (this.motion == true) {
			this.lastUpdated = System.currentTimeMillis();
		}
	}

	@Override
	public synchronized Float[] getTemperature() {
		return null;
	}

	@Override
	public synchronized Float[] getLuminiscence() {
		return null;
	}

	@Override
	public synchronized Float[] getHumidity() {
		return null;
	}
	
	@Override
	public Integer getBatteryLevel() {
		return this.battery;
	}

	@Override
	public long getLastUpdated() {
		return this.lastUpdated;
	}

	@Override
	public Zone getZone() {
		return this.zone;
	}

	protected boolean callParseResult(String path) {
		ZwayResultSet result = CallZwaveModule.speakToModule(path);
		
		if (result.getResponseCode() == 200) {
			return true;
		}
		
		return false;
	}
}
