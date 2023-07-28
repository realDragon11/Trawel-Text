package trawel.earts;

public enum EArt{
	//NOTE: name must be the lowercase/multicase equilavent of the enum NAME
	ARCANIST("Arcanist","A combat magic class focused on swapping out spell slots. Study arcane lore at libraries.", EAType.MAGIC),
	EXECUTIONER("Executioner","A martial class based around dealing the final blow. Complete kill quests to increase in power.", EAType.MARTIAL),
	BERSERKER("Berserker","A martial class that cannot examine, and instead strikes lightning-quick. Also adept at hand-to-hand.", EAType.MARTIAL),
	HUNTER("Hunter","A utility class that can carry more drawbanes.", EAType.OTHER),
	DRUNK("Drunk","A martial class that has more health and can use kung-fu.", EAType.MARTIAL),
	WITCH("Witch","A utility class that is adept with curses, alchemy, and healing magic.", EAType.MAGIC),
	BLOODMAGE("Bloodmage","A magic class that uses blood.", EAType.MAGIC),
	DEFENDER("Defender","A martial class with a shield or dagger.", EAType.MARTIAL);
	
	public String name, desc;
	public EAType type;
	EArt(String nam, String des, EAType typ) {
		this.name = nam;
		this.desc = des;
		this.type = typ;
	}
}
