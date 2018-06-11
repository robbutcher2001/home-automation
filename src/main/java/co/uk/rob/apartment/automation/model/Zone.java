package co.uk.rob.apartment.automation.model;

public enum Zone {
	LOUNGE("lounge"), KITCHEN("kitchen"), BATHROOM("bathroom"), HALLWAY("hallway"), ROB_ROOM("rob_room"), SCARLETT_ROOM("scarlett_room"), PATIO("patio"), APARTMENT("apartment");
	
	private String value;
	
	private Zone(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
