package trawel;

public enum Effect{
	
	CURSE("Cursed","Start battles with less hp.",true,false),
	BURNOUT("Burnout","Decreased skill.",false,true),
	
	BLEED("Bleeding","Take damage over time.",false,false),
	MAJOR_BLEED("Bleeding even more","Take damage over time.",false,false),
	DISARMED("Disamred","Have less attack options.",false,false),
	RECOVERING("Recovering","Will regain HP soon.",false,false),
	I_BLEED("Bleeding Inside","Take damage over time.",false,false),
	TORN("Torn","Decreased dodging.",false,true), 
	HASTE("Haste","Slightly increased speed.",false,false), 
	HEARTY("Hearty","Slightly increased health.",false,false), 
	R_AIM("Reactive Aim","Taking damage increases aim slightly.",false,false),
	BEES("BEEEES","Bees sting you occasionally.",true,false),
	BEE_SHROUD("Bee Shroud","Higher evasion. When you dodge an attack, sting the opponent.",false,false),
	B_MARY("Bloody Mary","Start the battle bleeding- whenever someone attacks you, their bleeding heals you.",false,false),
	FORGED("Forged","Increased health and armor regen.",false,false),
	TELESCOPIC("Telescopic","Slow attacks gain an accuracy boost.",false,false), ;
	
	private String name,desc;
	private boolean lasts, stacks;
	Effect(String name, String desc, boolean lasts, boolean stacks) {
		this.name = name;
		this.desc = desc;
		this.lasts = lasts;
		this.stacks = stacks;
	}
	public String getName() {return name;}
	public String getDesc() {return desc;}
	public boolean lasts() {
		return lasts;
	}
	public boolean stacks() {
		return stacks;
	}
	
	public static Effect[] estimEffects = new Effect[] {HASTE,HEARTY};//random drug effects
}
