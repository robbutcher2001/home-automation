package co.uk.rob.apartment.automation.model.monitors;

import java.util.List;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.users.User;
import co.uk.rob.apartment.automation.model.users.UserManager;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

public class NewUserMonitor extends Thread {
	
	private Logger log = Logger.getLogger(NewUserMonitor.class);
	
	public NewUserMonitor() {
		log.info("New user monitor started with 15 minute sleep");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			if (UserManager.readUsers()) {
				List<User> users = UserManager.getUsers();
				
				Integer toUpdate = null;
				for (int index = 0; index < users.size(); index++) {
					if (!users.get(index).isActivationTextSent()) {
						log.info("Sending activation text to " + users.get(index).getFirstName() + " " + users.get(index).getLastName());
						SMSHelper.sendSMS(users.get(index).getMobileNumber(), users.get(index).getFirstName() + ", your user has now been activated. You can control Rob's flat here: http://www.robsflat.co.uk/");
						toUpdate = index;
					}
				}
				
				if (toUpdate != null) {
					User updateUser = users.get(toUpdate);
					updateUser.setActivationTextSent(true);
					if (UserManager.updateUser(updateUser)) {
						log.info("Activation flag updated for " + users.get(toUpdate).getFirstName() + " " + users.get(toUpdate).getLastName());
					}
					else {
						log.info("There has been an issue saving updated activation flag for " + users.get(toUpdate).getFirstName() + " " + users.get(toUpdate).getLastName());
					}
				}
			}
			
			int fifteenMinutes = 60000 * 15;
			try {
				Thread.sleep(fifteenMinutes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
