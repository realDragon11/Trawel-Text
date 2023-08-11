package trawel;

public enum Effect{
	
	CURSE("Curse","Start battles with 10x level less HP.",true,false),
	BURNOUT("Burnout","Decreased skill.",false,true),
	
	BLEED("Bleeding","Take level damage after each of their attacks.",false,false),
	MAJOR_BLEED("Major Bleeding","Take 2*level damage after each of their attacks.",false,false),
	DISARMED("Disarmed","Their next turn will get one less weapon attack choice. Doesn't stack.",false,false),
	RECOVERING("Recovering","Will regain 5x level HP soon.",false,false),
	I_BLEED("Bleeding Inside","Take level damage after each of their attacks. Stacks.",false,true),
	TORN("Torn","Decreases dodging by 10% compounding per stack.",false,true), 
	HASTE("Haste","+10% speed stat.",false,false), 
	HEARTY("Hearty","+3x level hp at battle start. Doesn't stack with Forged.",false,false), 
	R_AIM("Reactive Aim","Upon taking attack damage, if currently attacking, their attack gains a percent bonus to hit roll equal to the percent of HP they lost.",false,false),
	BEES("BEEEES","Bees sting them occasionally, dealing random damage between 1 and twice their level.",true,false),
	BEE_SHROUD("Bee Shroud","1.1x dodge. When they dodge an attack or are missed, bees sting their attacker for 1 damage.",false,false),
	B_MARY("Bloody Mary","Whenever attacked, add a stack of internal bleeding to themselves and their attacker. Attacker's bleeding heals them 2x the amount they bleed for.",false,false),
	FORGED("Forged","+3x level hp at battle start. Every defense, restore a flat 10% of their armor before the attack, up to 100% quality.",false,false),
	TELESCOPIC("Telescopic","Attacks longer than 100 instants gain +1% additive hit mult for every instant longer than 100. Applied when choosing attack.",false,false),
	SLICE("Slicing","Their next attack is 10% faster and accurate.",false,false),
	DICE("Dicing","Their next attack is 10% faster and also 10 time units quicker.",false,false),
	CONFUSED_TARGET("Confused","Their next attack will have a random defender from any side.",false,false),
	BONUS_WEAP_ATTACK("Bonus Attack","Their next turn will have an additional weapon attack to choose from. Stacks.",false,true),
	ADVANTAGE_STACK("Advantage","The next hit or dodge roll involving them will gain a +20% bonus on their side. Stacks.",false,true),
	CLOTTER("Clotting","They are immune to bleed effects from wounds. Magic and dedicated effects still apply.",false,false),
	SUDDEN_START("Sudden Start","Applies Advantage and grants 2 Bonus Weapon Attacks at the start of battle.",false,false),
	STERN_STUFF("Sterner Stuff","Chance to resist death once per battle.",false,false)
	;
	
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
	public static Effect[] minorBuffEffects = new Effect[] {HASTE,HEARTY,FORGED,TELESCOPIC,R_AIM};//used for TOXIC_BREWS as minor positive potions
}
