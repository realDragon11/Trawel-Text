package trawel.personal;

import trawel.helper.constants.TrawelColor;

public enum Effect{
	//FIXME: could switch to functional interfaces for storing description, which would allow recoloring the effects that reference features
	//would also be better to switch those effects to their own Punishment type, since they behave differently
	//this would also have technical gains since that would mean effects could always be cleared and reset since only punishments Last
	
	//negative lasting status effects the player has to remove somewhere
	//used as punishments/costs for events, or as skill tie ins
	/**
	 * used on failed OOB rolls to make the player less likely to spam them while the consequence can't stack
	 */
	BURNOUT("Burnout","Halves attributes in out of battle contested rolls. Cure at Doctor or District; rest off in Tavern or Library; or overcome at Oracle or Altar.",true,false,-2),
	CURSE("Curse","Base HP is decreased by half. Lift at Enchanter, Oracle, or Altar.",true,false,-4),
	DAMAGED("Damaged","Armor starts at 50% condition each battle. Repair at Blacksmith or Appraiser.",true,false,-4),
	BEES("BEEEES","Bees sting them occasionally, dealing random damage between 1 flat and 4% LHP. Wash off in water or bathe in Tavern.",true,false,-2),
	TIRED("Tired","Halves dodge multiplier. Rest off at Tavern or Library.",true,false,-3),
	WOUNDED("Wounded","Body starts at 50% condition each battle. Cure at Doctor or District.",true,false,-4),
	//other
	CONFUSED_TARGET("Confused","Their next attack will have a random defender from any side.",false,false,-2),
	FLUMMOXED("Flummoxed","Next queued attack will be around 1% less accurate per stack.",false,true,-2),
	//bleeding
	BLEED("Bleed","Each stack causes them to take around .5% LHP damage after each action, but the amount of stacks are halved each time it applies.",false,true,-2),
	MAJOR_BLEED("Hemorrhage","Causes Bleed stacks to not decay when they tick.",false,false,-3),
	I_BLEED("Internal Bleeding","Takes 2% LHP damage after each of their actions, or less if their target is more than 2 levels lower than them, per action. Stacks.",false,true,-3),
	BLEEDOUT("Bleedout","Takes 10% MHP damage after each of their actions.",false,false,-4),
	//normal wounds
	DISARMED("Disarmed","Their next turn will get one less weapon attack choice. Doesn't stack.",false,false,-2),
	TORN("Torn","Decreases dodge by 10% compounding per stack.",false,true,-3), 
	SLICE("Slicing and Dicing","Their next attack is 10% faster and accurate.",false,false,2),
	//DICE("Dicing","Their next attack is 10% faster and also 10 time units quicker.",false,false,2),
	SHAKY("Shaky","Decreases dodge by 10% compounding per stack. Halves every time the suffering Person completes a swing.",false,true,-2), 
	/** wound, also used for knockout and some skills */
	BREATHING("Breathing","Will regain 5% LHP as healing next turn. Stacks, but only one applies per turn.",false,true,1),
	RECOVERING("Recovering","Will regain 5% LHP as healing at the end of this turn.",false,false,1),
	//potions
	HASTE("Haste","+5% speed stat.",false,false,2), 
	HEARTY("Hearty","+5% LHP at battle start. Doesn't stack with Forged.",false,false,2), 
	R_AIM("Reactive Aim","Upon taking attack damage, if currently attacking, their attack gains a percent bonus to hit roll equal to the percent of MHP they lost.",false,false,2),
	BEE_SHROUD("Bee Shroud","1.1x dodge. When they dodge an attack or are missed, applies bees to their attacker.",false,false,2),
	B_MARY("Bloody Mary","Whenever attacked, add a stack of internal bleeding to themselves and their attacker. Attacker's bleeding heals them 2x the amount they bleed for.",false,false,0),
	FORGED("Forged","+5% LHP at battle start. Every defense, restore a flat 10% of their armor before the attack, up to 100% quality.",false,false,2),
	TELESCOPIC("Telescopic","Attacks longer than 100 instants gain +1% additive hit mult for every instant longer than 100. Applied when choosing attack.",false,false,2),
	CLOTTER("Clotting","Immune to bleed effects from wounds. Magic and dedicated effects still apply.",false,false,2),
	SIP_GRAVE_ARMOR("Grave Armor","Adds 2 Padded Armor and Sterner Stuff.",false,false,4),
	//other
	
	//mostly skills
	BONUS_WEAP_ATTACK("Bonus Attack","Their next turn will have an additional weapon attack to choose from. Stacks.",false,true,2),
	ADVANTAGE_STACK("Advantage","Their next to hit or dodge roll will gain a +20% bonus. Stacks.",false,true,2),
	SUDDEN_START("Sudden Start","Applies Advantage and grants 2 Bonus Weapon Attacks at the start of battle.",false,false,2),
	STERN_STUFF("Sterner Stuff","Chance to resist death once per battle.",false,false,3),
	MIASMA("Miasma","Grants skill interactions to enemy Persons. Halves every time the suffering Person completes a swing.",false,true,-1),
	PARRY("Parry","Grants a compounding 1.2x dodge against one attack. Stacks in effect.",false,true,2),
	//condwounds
	DEPOWERED("Depowered","Unable to use some special abilities.",false,false,-2),
	MAIMED("Maimed","Loses one weapon attack choice per attack. Doesn't stack with Disarmed.",false,false,-3),
	CRIPPLED("Crippled","Dodge reduced to 80%. Stacks.",false,true,-3),
	HIT_VITALS("Shattered","Takes double condition damage. When attacking a part with no condition, roll another wound.",false,false,-4),
	BRAINED("Brained","Does not recover from further KO wounds.",false,false,-4),
	//tactics
	SINGLED_OUT("Singled Out","Those who attack this Person have a 2/3rds chance to attack them again afterwards.",false,false,-1),
	DUCKING("Ducking","Gains +0.2 flat dodge roll (not mult), but will be exhausted after the next attack after this attack completes.",false,false,2),
	ROLLING("Rolling","Gains +0.2 flat dodge roll (not mult), but will be exhausted after their attack completes.",false,false,1),
	/**
	 * used in tactics but also if tired or ambushed
	 */
	EXHAUSTED("Exhausted","Halves dodge mult until they next complete an attack cooldown.",false,false,-1),
	BRISK("Brisk","Halves attack time, and increases hit mult based on amount of time before reduction, with attacks under 100 total instants getting a 1x-2x multiplier the closer they are to 0 instants. Only applies to one set of attacks.",false,false,2),
	CHALLENGE_BACK("Temerity","Negates the next wound from a suffered Impactful attack, or adds +20% damage to the next attack choice, whichever comes first.",false,false,2),
	PLANNED_TAKEDOWN("Takedown","Causes next impactful attack to inflict the Knockout Wound.",false,false,1),
	//armor and maybe skills/potions
	PADDED("Padded","1/5th chance of negating a wound per stack, once per stack.",false,true,2)
	;
	
	private String name,desc;
	private boolean lasts, stacks;
	private int goodNegNeut;
	Effect(String name, String desc, boolean lasts, boolean stacks,int _goodNegNeut) {
		this.name = name;
		this.desc = desc;
		this.lasts = lasts;
		this.stacks = stacks;
		goodNegNeut = _goodNegNeut;
	}
	public String getName() {
		if (goodNegNeut == 1) {
			return TrawelColor.TIMID_GREEN+name+TrawelColor.COLOR_RESET;
		}
		if (goodNegNeut == -1) {
			return TrawelColor.TIMID_RED+name+TrawelColor.COLOR_RESET;
		}
		if (goodNegNeut < 0) {
			return TrawelColor.PRE_RED+name+TrawelColor.COLOR_RESET;
		}
		if (goodNegNeut > 0) {
			return TrawelColor.PRE_GREEN+name+TrawelColor.COLOR_RESET;
		}
		return TrawelColor.TIMID_MAGENTA+name+TrawelColor.COLOR_RESET;
	}
	public String getDesc() {return desc;}
	public boolean lasts() {
		return lasts;
	}
	public boolean stacks() {
		return stacks;
	}
	
	public String getDisp() {
		return getName() + TrawelColor.PRE_WHITE+": " +getDesc();
	}
	
	public boolean isNegative() {
		return goodNegNeut < 0;
	}
	
	public static Effect[] minorBuffEffects = new Effect[] {HASTE,HEARTY,FORGED,TELESCOPIC,R_AIM};//used for TOXIC_BREWS as minor positive potions
	
	public static final Effect[] randomPotion = new Effect[] {
			Effect.HEARTY,Effect.BEES,Effect.BEE_SHROUD,Effect.CURSE,Effect.FORGED,
			Effect.HASTE,Effect.CLOTTER,Effect.R_AIM,Effect.SUDDEN_START,SIP_GRAVE_ARMOR
		};
	
	public static final Effect[] randomQuestionablePotion = new Effect[] {
			Effect.CURSE,Effect.CURSE,Effect.BEES,Effect.BLEED,Effect.MAJOR_BLEED,
			Effect.HEARTY,Effect.BEE_SHROUD,Effect.FORGED,Effect.HASTE,
			Effect.CLOTTER,Effect.R_AIM,Effect.SUDDEN_START,SIP_GRAVE_ARMOR
		};
	public static final Effect[] randomNegativePotion = new Effect[] {
			Effect.CURSE,Effect.CURSE,Effect.BEES,Effect.BLEED,Effect.MAJOR_BLEED
	};
	public static final Effect[] randomPositivePotion = new Effect[] {
			Effect.HEARTY,Effect.BEE_SHROUD,Effect.FORGED,Effect.HASTE,
			Effect.CLOTTER,Effect.R_AIM,Effect.SUDDEN_START,SIP_GRAVE_ARMOR
	};
}
