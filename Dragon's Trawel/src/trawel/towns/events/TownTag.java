package trawel.towns.events;

public enum TownTag {
	MERCHANT("Merchant Location","merchants"),
	ADVENTURE("Adventure Location","adventurers"),
	LAW("Lawful","strict laws"),
	LAWLESS("Lawless","nigh-absence of laws"),
	ARCANE("Arcane","arcane lore"),
	DRUDIC("Drudic","primal vistas"),
	ALCHEMY("Alchemy","brewed potions"),
	MYSTIC("Mystic","rumored leylines"),
	SMALL_TOWN("Small Town","rural community"),
	CITY("City","bustling urban developments"),
	HELLISH("Hellish","hellish energies"),
	MINERALS("Mineral Abundance","vast mineral abundance"),
	RICH("Wealthy","wealth"),
	HIDDEN("Hidden","obscure location"),
	TRAVEL("Travelers","many travelers"),
	HISTORY("History","rich history"),
	BARREN("Barren Fields","barren fields");
	
	
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
