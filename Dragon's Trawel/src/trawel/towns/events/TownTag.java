package trawel.towns.events;

public enum TownTag {
	MERCHANT, ADVENTURE,
	LAW, LAWLESS,
	ARCANE, DRUDIC, ALCHEMY, MYSTIC,
	SMALL_TOWN, CITY,
	RICH("Wealthy","Has more wealth to spend, but more prone to attacks."),
	HIDDEN("Hidden","Much less prone to attack.");
	
	
	public final String name, desc;
	public final boolean display;
	TownTag(String name, String desc) {
		this.name = name;
		this.desc = desc;
		this.display = true;
	}
	TownTag(){
		this.name = null;
		this.desc = null;
		this.display = false;
	}
}
