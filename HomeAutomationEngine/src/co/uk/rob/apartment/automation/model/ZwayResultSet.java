package co.uk.rob.apartment.automation.model;

public class ZwayResultSet {
	private int responseCode;
	private String jsonResponse;
	
	public int getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getJsonResponse() {
		return jsonResponse;
	}
	
	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}
}
