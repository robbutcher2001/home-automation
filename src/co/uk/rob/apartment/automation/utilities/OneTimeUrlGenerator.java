package co.uk.rob.apartment.automation.utilities;

import java.util.UUID;

/**
 * @author Rob
 *
 */
public class OneTimeUrlGenerator {

	public static String getOneTimeString() {
		return UUID.randomUUID().toString();
	}
}
