
public enum DrawBane {
	TEST("test","test"),
	GARLIC("garlic","Used to repel vampires."),
	SILVER("silver","A shiny metal."),
	MEAT("meat","A cut of meat."),
	CEON_STONE("eon stone","A cracked stone with infinite choices."),
	NOTHING("nothing","A void."),
	PROTECTIVE_WARD("protective ward","Used to repel monsters."),
	ENT_CORE("ent core","The core of an ent tree.");
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
