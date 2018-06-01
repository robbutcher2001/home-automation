package co.uk.rob.apartment.automation.model;

/**
 * @author Rob
 *
 */
public class M25TrafficResults {

	private String startJunction;
	private String endJunction;
	private String category;
	
	public M25TrafficResults() {
		startJunction = "";
		endJunction = "";
		category = "";
	}
	
	public String getStartJunction() {
		return startJunction;
	}
	
	public void setStartJunction(String startJunction) {
		this.startJunction = startJunction;
	}
	
	public String getEndJunction() {
		return endJunction;
	}
	
	public void setEndJunction(String endJunction) {
		this.endJunction = endJunction;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
}
