package co.uk.rob.apartment.automation.model;

public enum Zone {
	LOUNGE("Lounge"), KITCHEN("Kitchen"), BATHROOM("Bathroom"), HALLWAY("Hallway"), ROB_ROOM("Rob room"), SCARLETT_ROOM("Scarlett room"), PATIO("Patio"), APARTMENT("Apartment");
	
	private String value;
	
	private Zone(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
