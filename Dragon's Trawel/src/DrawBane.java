
public enum DrawBane {
	TEST("test","test"),
	GARLIC("garlic","Used to repel vampires."),
	SILVER("silver","A shiny metal."),
	MEAT("meat","A cut of meat.");
	private String name, flavorText;
	DrawBane(String name, String flavorText) {
		this.name = name;
		this.flavorText = flavorText;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFlavor() {
		return flavorText;
	}
	
	
	
	
}
