
public enum DrawBane {
	TEST("test","test",0),
	GARLIC("garlic","Used to repel vampires.",20),
	SILVER("silver","A shiny metal.",100),
	MEAT("meat","A cut of meat.",20),
	CEON_STONE("eon stone","A cracked stone with infinite choices.",100),
	NOTHING("nothing","A void.",0),
	PROTECTIVE_WARD("protective ward","Used to repel monsters.",200),
	ENT_CORE("ent core","The core of an ent tree.",50);
	private String name, flavorText;
	private int value;
	DrawBane(String name, String flavorText,int val) {
		this.name = name;
		this.flavorText = flavorText;
		this.value = val;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFlavor() {
		return flavorText;
	}
	
	public int getValue() {
		return value;
	}
	
	
	
	
}
