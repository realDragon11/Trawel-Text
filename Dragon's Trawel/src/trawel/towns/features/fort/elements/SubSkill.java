package trawel.towns.features.fort.elements;

public enum SubSkill{
	SCRYING("Scrying","Look into the future and at distant lands.",Active.DOWNTIME,Type.WIZARD,1.25f),
	ENCHANTING("Enchanting","Enchant armor and weapons.",Active.DOWNTIME,Type.WIZARD,1.1f),
	DEATH("Death Magic","Slay attackers with spells of death and decay.",Active.BATTLE,Type.WIZARD,1.25f),
	ELEMENTAL("Elementalism","Impair and expose attackers with elemental spells.",Active.BATTLE,Type.WIZARD,1.1f),
	SMITHING("Smithing","Increase weapon and armor production speed.",Active.DOWNTIME,Type.BLACKSMITH,1.1f),
	WATCH("Watching","CAN'T LEVEL NORMALLY",Active.DOWNTIME,Type.WATCH,1.0f),
	DEFENSE("Defensive Rating","CAN'T LEVEL NORMALLY",Active.BATTLE,Type.WATCH,1.0f),
	FATE("Tower of Fate Bonus","CAN'T LEVEL NORMALLY",Active.BATTLE,Type.WATCH,1.0f),
	;
	
	public String name, desc;
	public Active act;
	public float costMult;
	public Type type;
	SubSkill(String name, String desc, Active act, Type type, float costMult){
		this.name = name;
		this.desc = desc;
		this.act = act;
		this.costMult = costMult;
		this.type = type;
	}
	
	public enum Active{
		DOWNTIME, BATTLE;
	}
	
	public enum Type{
		WIZARD,BLACKSMITH,WATCH;
	}
}
