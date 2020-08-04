package trawel.earts;

public enum ASpell {

	ELEMENTAL_BURST("Elemental Burst","A blast of a fluctuating elemental type."), 
	DEATH_BURST("Death Burst","A blast of a fluctuating death spell type."),
	;
	
	public String name, desc;
	ASpell(String name, String desc){
		this.name = name;
		this.desc = desc;
	}
}
