package trawel.earts;

public enum ASpell {

	ELEMENTAL_BURST("Elemental Burst","A blast of a fluctuating elemental type."), 
	DEATH_BURST("Death Burst","A blast of a fluctuating death spell type."),
	ARMOR_UP("Armor Up","Restore your armor."),
	BEFUDDLE("Befuddle","Force a creature to swap targets."),
	;
	
	public String name, desc;
	ASpell(String name, String desc){
		this.name = name;
		this.desc = desc;
	}
}
