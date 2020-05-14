
public enum DrawBane {
	TEST("test","test"),
	GARLIC("garlic","used to repel vampires.");
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
