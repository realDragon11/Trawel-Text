package trawel.factions;

public enum Faction{
	HEROIC("Hero's Guild"),
	ROGUE("Rogue's Guild"),
	DUEL("Dueling Reputation"),
	MERCHANT("Merchant's Guild"),
	HUNTER("Hunter's Guild"),
	FOREST("Dryads"),
	LAW_GOOD("The People's Watch"),
	LAW_EVIL("The Lawman's Guild");
	
	public String name;
	Faction(String name){
		this.name = name;
	}
}