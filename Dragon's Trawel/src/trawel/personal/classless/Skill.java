package trawel.personal.classless;

import derg.menus.MenuItem;
import derg.menus.MenuLine;
import trawel.extra;

public enum Skill{
	
	    BEER_BELLY("Beer Belly","Use two beers at once in battle!",true,Type.FIGHTER,1,"Gives you the beer health bonus twice- if you have two beers to drink."),
	    
	    
		BEER_LOVER("Beer Lover","Endless beer. Never have to go to a tavern again.",true,Type.FIGHTER,2,""),
		HPSENSE("Health Sense","Discern how much hp the defender has after each attack. Improves examine as well.",false,Type.FIGHTER,2,""),
		
		COUNTER("Counter","Attack slightly sooner after you are attacked.",true,Type.FIGHTER,3,""),
		
		BERSERKER("Berserker","Remove examine from possible attacks you can make.",true,Type.FIGHTER,4,""),
		
		
		WAIT("Wait","Allows you to wait for a better opportunity.",false,Type.FIGHTER,5,""),
		//LAST_STAND("Last Stand","When reduced to 0 hp or lower, stay alive at 1 hp.",Type.FIGHTER,3),
		
		IOFF_TRAINING("Offensive Training","Hone your combat skills.",true,Type.FIGHTER,6,""),
		
		
		
		
		RESTOCK("Restocker","Can pay for stores to restock.",false,Type.TRADER,1,""),
		EXPANDER("Expander","All stores gain an additional item.",false,Type.TRADER,1,""),
		
		SKILLPLUS("SP Master","Gain 2 skill points.",false,Type.TRADER,2,""),
		INHERIT("Inheriter","Gain 500 gold.",false,Type.TRADER,2,""),
		
		LOOTER("Expert Looter","Gain some gold each time you loot a corpse.",false,Type.TRADER,3,""),
		
		
		
		TOWNSENSE("Connection Sense","Tell how many connections a town has.",false,Type.EXPLORER,1,""),
		TIERSENSE("Tier Sense","Tell what tier a town belongs to.",false,Type.EXPLORER,1,""),
		
		SHIPSENSE("Shipyard Sense","Tell if towns have a shipyard.",false,Type.EXPLORER,2,""),
		TELESENSE("Teleport Sense","Tell if towns have a teleport shop.",false,Type.EXPLORER,2,""),
		
		SHOPSENSE("Shop Sense","Tell if towns have at least one shop.",false,Type.EXPLORER,3,""),
		ARENASENSE("Arena Sense","Tell if towns have at least one arena.",false,Type.EXPLORER,3,""),
		
		
		//move to esoteric art- Arcanist
		MAGE_TRAINING("Mage Training","Unlock your inner magic potential.",true,Type.MAGE,1,""),
		LIFE_MAGE("Life Mage","Unlock healing magic spells.",true,Type.MAGE,1,""),//good for out of combat
		MONEY_MAGE("Money Mage","Generate money slowly.",false,Type.MAGE,1,""),//good for out of combat
		
		ELEMENTAL_MAGE("Elemental Mage","Unlock elemental magic spells.",false,Type.MAGE,2,""),
		DEATH_MAGE("Death Mage","Unlock necromantic magic spells.",true,Type.MAGE,2,""),
		ARMOR_MAGE("Armor Mage","Unlock armor repairing magic spells.",false,Type.MAGE,2,""),
		ILLUSION_MAGE("Illusion Mage","Unlocks the befuddle spell.",false,Type.MAGE,2,""),
		
		MAGE_POWER("Power Within","Replace one of your attacks with a spell and unlock your magic power.",false,Type.MAGE,3,""),//works with ai, bad choice tho
		
		IMAG_TRAINING("Mage Training","Hone your magic skills.",true,Type.MAGE,4,""),
		
		
		PARRY("Parry","Gives you a parrying dagger.",true,Type.DEFENDER,1,""),
		SHIELD("Shield","Gives you a shield.",true,Type.DEFENDER,1,""),
		
		GOOFFENSIVE("Go on the offensive","Allows you to attack with your defensive item.",true,Type.DEFENDER,4,""),
		DEFENSIVE_TRAINING("Stay on the defensive","Unlocks your defensive potential.",true,Type.DEFENDER,4,""),
		
		
		
		IDEF_TRAINING("Defensive Training","Hone your defensive skills.",true,Type.DEFENDER,6,""),
		
		EXECUTE_ATTACK("EXEATTACK","Internal",false,Type.FEATURE,0,""),
		DRUNK_DRINK("DRUNKDRINK","Internal",false,Type.FEATURE,0,""),
		BONUSATTACK_BERSERKER("BABER","Internal",false,Type.FEATURE,0,""),
		MARK_ATTACK("Mark","Internal",false,Type.FEATURE,0,""),
		BLOOD_SURGE("BS","Internal",false,Type.FEATURE,0,""),
		BLOOD_HARVEST("BH","Internal",false,Type.FEATURE,0,""),
		SKY_BLESSING_1("Sky Blessing I","Internal",false,Type.FEATURE,0,""),
		ENDSKILL("","",false,Type.FIGHTER,0,""),
		//anything above should be removed and filtered out over time
		
		//new values, sorting by type because saves break without order changing anyway
		RACIAL_SHIFTS("Flexible","Prone to changing its defense patterns."
				,"Changes stances in combat, changing its target types, and possibly its attacks."
				,Type.FEATURE),
		TA_NAILS("Tough as Nails","Grants a 20% chance to ignore incoming wounds."
				,"Can apply to any wound inflicted on them, completely negating it.",
				Type.DEFENSE),
		RAW_GUTS("Raw Guts","Resist damage up to level, by Torso condition."
				,"Can only resist up to half the base damage of an attack. Only applies to hit attacks. Amount is random."
				,Type.DEFENSE),
		KUNG_FU("Unarmed Attacks","Gain martial arts attacks."
				,"Adds one attack option per turn, which is based on strength and dexterity, but capped to weapon level."
				,Type.ATTACK_TYPE),
		BLITZ("Blitz","You actions take 3 less instants to complete than expected."
				,"Does not allow you to act sooner in order- but does impact how much time passes when you do get to act."
				,Type.SPEED),
		SPEEDDODGE("Speed Dodge","Attack 10 instants sooner after you dodge."
				,"Applies to each dodge. Does not apply to misses."
				,Type.SPEED),
		DODGEREF("Refreshing Dodge","Gain hp equal to your attacker's level every time you dodge."
				,"Uncapped total HP gain. Caps at twice your level per activation. Does not apply to misses."
				,Type.DEFENSE),
		P_BREWER("Brewer","Your created potions can be sipped two more times before running out."
				,"Stacks with normal filler ingredients."
				,Type.CRAFT),
		TOXIC_BREWS("Toxic Brews","When drinking a cursed potion, gain up to 3 random minor positive potion effects. Your created potions can be sipped one more time."
				,"Stacks with normal filler ingredients."
				,Type.CRAFT),
		CURSE_MAGE("Curse Whisperer","Those who kill them are cursed, and start battles with less hp until cured."
				,"Cure at a Shaman or Doctor. Lasts between battles."
				, Type.OTHER),
		KILLHEAL("Vampiric Spirit","Gain 5 x dead person level HP on a kill."
				,"Caps at 10 x your own level. 'Kill' means being the last person to attack the target before they die."
				,Type.OFFENSE),
		SPUNCH("Sucker Punch","Your impactful attacks slow down your target's next action by 2% of their total time."
				,extra.IMPACT_TIP
				,Type.OFFENSE),
		DSTRIKE("Decisive Strike","Instantly kill anyone you damage by more than 70% of their max HP in one blow."
				,"Max HP is closely aligned to how much HP they start with. Must be part of an 'Impactful' action. (HP gained after battle start can go above this limit."
				,Type.OFFENSE),
		BLOODTHIRSTY("Bloodthirsty","Gain HP equal to the lower of your level and your target's level every time you make an impactful attack."
				,extra.IMPACT_TIP
				,Type.OFFENSE),
		ARMOR_TUNING("Armor Tuning","Your armor is 20% stronger at the start of every battle."
				,"Armor above 100% degrades half of how much it's above 100% whenever you complete an action."
				,Type.DEFENSE),
		ARMORSPEED("Glancing Blow","Attack 10 instants sooner after your armor blocks an attack."
				,"Applies once per attack."
				,Type.DEFENSE),//defense not speed, speed is more 'fast in mobility'
		ARMORHEART("Armor Heart","Gain hp equal to your attacker's level every time your armor blocks an attack."
				,"Uncapped total HP gain. Caps at twice your level per activation. Applies once per attack."
				,Type.DEFENSE),
		MESMER_ARMOR("Mesmer Armor","When attacked for no Impact, Roll a contested Clarity vs their highest attribute to confuse them."
				,"Attacks with no impact typically are dodged, miss, or are blocked by armor. Confuse makes the target's next attack capable of friendly fire."
				,Type.DEFENSE),
		ARCANIST("Arcanist","Unlocks a magical swappable attack option."
				,"Use the skill attack config creator in the skills menu to select a source to stance your attacks from."
				,Type.ATTACK_TYPE),
		MAGE_FRUGAL("Frugal Mage","Grants you a better deal of Aether to Currency conversions in shops, starting at +10%.",
				"For every 10 clarity past 100, get +10% more. No penalty for sub 100 clarity."
				,Type.SOCIAL),
		OPENING_MOVE("Opening Move","Grants two bonus weapon attack choices at the start of every battle.",
				"You can have a max of 5 weapon attack choices at a time, stacks will only be consumed if they add attacks."
				,Type.OFFENSE),
		QUICK_START("Quick Start","Grants a stack of Advantage at the start of every battle, confering a 20% to one hit or dodge roll."
				,"Applies to only one attack, and is consumed on use, but stacks with other sources of Advantage."
				,Type.SPEED),
		PRESS_ADV("Press the Advantage","Grants two stacks of Advantage after each kill."
				,"Advantage applies a +20% bonus to the first hit/dodge roll this Person makes, one stack per attack."
				,Type.SPEED),
		BLOODDRINKER("Blood Drinker","Those who attack you while bleeding heal you equal to their bleed damage."
				,"Stacks with other sources of bleed healing"
				,Type.DEFENSE),
		NIGHTVISION("Nightvision","Lets you see objects in the dark."
				,"Helps determine things in Graveyards."
				, Type.OTHER),
		NPC_BURN_ARMOR("Flaming Strikes","Burns armor with every Impactful attack."
				,"Attacks damage armor twice as much as percent HP damage."
				,Type.FEATURE),
		PLOT_ARMOR("Fated","Confused targets have a reduced chance to attack this Person."
				,"Does not apply Confusion on its own."
				,Type.DEFENSE),
		
		
		;
	    private String name,desc, longDesc;
	    private Type type;
	    private int level;
	    private boolean AITake;
	    public enum Type{
	    	TRADER,
	    	EXPLORER,
	    	FIGHTER,
	    	MAGE,
	    	DEFENDER,
	    	//new set
	    	OFFENSE,
	    	CRAFT,
	    	SOCIAL,
	    	OTHER,
	    	SPEED,//skills that tend to make you faster
	    	DEFENSE,//skills that provide defensive benefits
	    	ATTACK_TYPE,//skills that grant a new attack type, generally there should only be 5 of these total
	    	//TODO: need to make a type that just aliases for another ATTACK_TYPE so you can have more than one
	    	FEATURE,//skills that are 'part' of a person or thing. Should be only granted by perks
	    	INTERNAL_USE_ONLY;//skills that should not be displayed
	    }
		Skill(String name,String desc,boolean AITake, Type t, int lvl, String longDesc){
			this.name = name;
			this.desc = desc;
			type = t;
			level = lvl;
			this.AITake = AITake;
			this.longDesc = longDesc;
		}
		//new skills
		Skill(String name,String desc, String mechanicDesc,Type type){
			this.name = name;
			this.desc = desc;
			this.longDesc = mechanicDesc;
			this.type = type;
			
			//REMOVE LATER:
			level = 0;
			AITake = false;
		}
		
		
		public String getName() {return name;}
		public String getDesc() {return desc;}
		public Type getType() {return type;}
		public int getLevel() {return level;}
		public boolean getAITake() {return AITake;}

		public void display() {
			extra.println(name + ": " + desc);
		}
		
		public String disp() {
			return (name + ": " + desc);
		}

		public String getLongDesc() {
			return longDesc;
		}
		
		public String explain() {
			return (name + ": " + desc + "\n " + " " + longDesc);
		}
		
		public MenuItem getMenuView() {
			return new MenuLine() {

				@Override
				public String title() {
					return " " + name + ": " + desc + " ("+longDesc+")";
				}};
			
		}
}
