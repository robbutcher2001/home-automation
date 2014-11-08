package co.uk.rob.apartment.automation.model.interfaces;

/**
 * Used to represent an object that needs attention.
 * 
 * Objects that implement this interface will be 
 * used to generate an MSO message at top of webapp.
 * 
 * @author Rob
 *
 */
public interface MajorServiceOutageTriggerable {

	public boolean hasCausedOutage();
}
