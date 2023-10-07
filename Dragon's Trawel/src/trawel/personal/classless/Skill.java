package trawel.personal.classless;

import derg.menus.MenuItem;
import derg.menus.MenuLine;
import trawel.extra;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.personal.Person;

public enum Skill{
	
		UNIMPLEMENTED("Unimplemented","Skills after this point and before the next heading are old skills that haven't been updated."
				,true,Type.INTERNAL_USE_ONLY,1,""),
	    BEER_BELLY("Beer Belly bROken","Use two beers at once in battle!",true,Type.FIGHTER,1,"Gives you the beer health bonus twice- if you have two beers to drink."),
	    
	    
		BEER_LOVER("Beer Lover","Endless beer. Never have to go to a tavern again.",true,Type.FIGHTER,2,""),
		COUNTER("Counter","Attack slightly sooner after you are attacked.",true,Type.FIGHTER,3,""),
		
		TOWNSENSE("Connection Sense","Tell how many connections a town has.",false,Type.EXPLORER,1,""),
		TIERSENSE("Tier Sense","Tell what tier a town belongs to.",false,Type.EXPLORER,1,""),
		
		SHIPSENSE("Shipyard Sense","Tell if towns have a shipyard.",false,Type.EXPLORER,2,""),
		TELESENSE("Teleport Sense","Tell if towns have a teleport shop.",false,Type.EXPLORER,2,""),
		
		SHOPSENSE("Shop Sense","Tell if towns have at least one shop.",false,Type.EXPLORER,3,""),
		ARENASENSE("Arena Sense","Tell if towns have at least one arena.",false,Type.EXPLORER,3,""),
		
		MAGE_TRAINING("Mage Training","Unlock your inner magic potential.",true,Type.MAGE,1,""),
		LIFE_MAGE("Life Mage","+5% clarity as MHP.",true,Type.MAGE,1,""),//good for out of combat
		MONEY_MAGE("Money Mage","Generate money slowly.",false,Type.MAGE,1,""),//good for out of combat
		
		ELEMENTAL_MAGE("Elemental Mage","Unlock elemental magic spells.",false,Type.MAGE,2,""),
		DEATH_MAGE("Death Mage","Unlock necromantic magic spells.",true,Type.MAGE,2,""),
		ARMOR_MAGE("Armor Mage","Unlock armor repairing magic spells.",false,Type.MAGE,2,""),
		ILLUSION_MAGE("Illusion Mage","Unlocks the befuddle spell.",false,Type.MAGE,2,""),
		
		MAGE_POWER("Power Within","Replace one of your attacks with a spell and unlock your magic power.",false,Type.MAGE,3,""),//works with ai, bad choice tho
		
		IMAG_TRAINING("Mage Training","Hone your magic skills.",true,Type.MAGE,4,""),
		
		
		PARRY("Parry","Gives you a parrying dagger.",true,Type.DEFENDER,1,""),
		SHIELD("Shield","Gives you a shield.",true,Type.DEFENDER,1,""),

		ENDSKILL("New System Heading","Skills after this point are in the current game.",false,Type.INTERNAL_USE_ONLY,0,""),
		//anything above should be removed and filtered out over time
		
		//new values, sorting by type because saves break without order changing anyway
		RACIAL_SHIFTS("Flexible","Prone to changing its defense patterns."
				,"Changes stances in combat, changing its target types, and possibly its attacks."
				,Type.FEATURE),
		TA_NAILS("Tough as Nails","Grants a 20% chance to negate incoming wounds."
				,"Can apply to any wound inflicted on them, preventing it from processing entirely.",
				Type.DEFENSE),
		RAW_GUTS("Raw Guts","Resist damage up to 3% LHP, by Torso condition."
				,"Can only resist up to half the base damage of an attack. Only applies to hit attacks. Amount is random."
				,Type.DEFENSE),
		KUNG_FU("Unarmed Attacks","Gain martial arts attacks."
				,"Adds one attack option per turn, which is based on strength and dexterity, TODO."//TODO
				,Type.ATTACK_TYPE),
		BLITZ("Blitz","You actions take 3 less instants to complete than expected."
				,"Does not allow you to act sooner in order- but does change how much time passes when you do get to act."
				,Type.SPEED),
		SPEEDDODGE("Speed Dodge","Attack 10 instants sooner after you dodge."
				,"Applies to each dodge. Does not apply to misses."
				,Type.SPEED),
		DODGEREF("Refreshing Dodge","Gain 1% of attacker's LHP every time you dodge."
				,"Uncapped total HP gain, can exceed MHP. Attacker's LHP caps at 2 levels higher than your own. Does not apply to misses."
				,Type.DEFENSE),
		P_BREWER("Brewer","Your created potions can be sipped two more times before running out."
				,"Stacks with normal filler ingredients."
				,Type.CRAFT),
		TOXIC_BREWS("Toxic Brews","When drinking a cursed potion, gain up to 3 random minor positive potion effects. Curse reduces MHP by 20% instead of 50%. Your created potions can be sipped one more time."
				,"Stacks with normal filler ingredients."
				,Type.CRAFT),
		CURSE_MAGE("Curse Whisperer","Those who kill them are cursed, and start battles with half base MHP until cured."
				,"Cure at a Shaman or Doctor. Lasts between battles."
				, Type.OTHER),
		KILLHEAL("Vampiric Spirit","Gain 5% dead LHP on a kill."
				,"Dead LHP caps at 2 levels higher than your own. 'Kill' means being the last person to attack the target before they die."
				,Type.OFFENSE),
		SPUNCH("Sucker Punch","Your impactful attacks slow down your target's next action by 2% of their total time."
				,extra.IMPACT_TIP
				,Type.OFFENSE),
		DSTRIKE("Decisive Strike","Instantly kill anyone you damage by more than 70% of their MHP in one blow."
				,"Max HP is closely aligned to how much HP they start with, but can go higher. Must be part of an 'Impactful' action."
				,Type.OFFENSE),
		BLOODTHIRSTY("Bloodthirsty","Heal HP equal to the lower of 1% your LHP and their LHP every time you make an impactful attack."
				,"Cannot bring you above your MHP."
				,Type.OFFENSE),
		ARMOR_TUNING("Armor Tuning","Your armor is 20% stronger at the start of every battle."
				,"Armor above 100% degrades half of how much it's above 100% whenever you complete an action."
				,Type.DEFENSE),
		ARMORSPEED("Glancing Blow","Attack 10 instants sooner after your armor blocks an attack."
				,"Applies once per attack."
				,Type.DEFENSE),//defense not speed, speed is more 'fast in mobility'
		ARMORHEART("Armor Heart","Gain 2% of attacker's LHP every time your armor blocks an attack."
				,"Caps at your MHP. Attacker's LHP caps at 4 levels higher than your own. Applies once per attack."
				,Type.DEFENSE),
		MESMER_ARMOR("Mesmer Armor","When attacked for no Impact, Roll a contested Clarity vs their highest attribute to confuse them."
				,"Attacks with no impact typically are dodged, miss, or are blocked by armor. Confuse makes the target's next attack capable of friendly fire."
				,Type.DEFENSE),
		ARCANIST("Arcanist","Unlocks a magical swappable attack option."
				,"Use the skill attack config creator in the skills menu to select a source to stance your attacks from."
				,Type.ATTACK_TYPE),
		MAGE_FRUGAL("Frugal Mage","Grants you a better deal of Aether to Currency conversions in shops, starting at +10%.",
				"For every 1 clarity past 100, get +1% more. No penalty for sub 100 clarity."
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
		BLOODDRINKER("Blood Drinker","Those who attack you while bleeding give you HP equal to half their bleed damage."
				,"Stacks with other sources of bleed healing. Not capped by your MHP."
				,Type.DEFENSE),
		NIGHTVISION("Nightvision","Lets you see objects in the dark."
				,"Helps determine things in Graveyards."
				, Type.OTHER),
		//can be depowered
		NPC_BURN_ARMOR("Flaming Strikes","Burns armor with every Impactful attack."
				,"Attacks damage armor twice as much as percent HP damage."
				,Type.FEATURE),
		PLOT_ARMOR("Plot Armor","Confused targets have a reduced chance to attack this Person."
				,"Does not apply Confusion on its own."
				,Type.DEFENSE),
		ELEMENTALIST("Elementalist","Skill attacks deal +10% Ignite, Frost, and Elec damage."
				,"Does not apply to normal weapon attacks."
				,Type.OFFENSE),
		/**
		 * NOTE: do not grant without ELEMENTALIST
		 */
		M_PYRO("Pyromantic","25% chance to convert failed wounds into a random Ignite wound on any attack."
				,"Applies before choosing attack. Stacks with other Elementalist subskills, up to 75%."
				,Type.OFFENSE),
		/**
		 * NOTE: do not grant without ELEMENTALIST
		 */
		M_CRYO("Cryomantic","25% chance to convert failed wounds into a random Frost wound on any attack."
				,"Applies before choosing attack. Stacks with other Elementalist subskills, up to 75%."
				,Type.OFFENSE),
		/**
		 * NOTE: do not grant without ELEMENTALIST
		 */
		M_AERO("Aeromantic","25% chance to convert failed wounds into a random Elec wound on any attack."
				,"Applies before choosing attack. Stacks with other Elementalist subskills, up to 75%."
				,Type.OFFENSE),
		STERN_STUFF("Sterner Stuff","The first time each battle you would die, roll a contested Strength check vs their highest attribute to survive at 1 HP."
				,"Instant kill attacks that wouldn't deal enough damage to kill you otherwise do not roll, but still leave you at 1 HP.",
				Type.DEFENSE),
		REACTIVE_DODGE("Reactive Dodge","Grants one stack of Advantage after each dodge."
				,"Does not apply to misses. Advantage applies a +20% bonus to the first hit/dodge roll this Person makes, one stack per attack."
				,Type.SPEED),
		ARCANIST_2("Multi-Magical","Grants another Arcanist skill attack configuration."
				,""
				,Type.ATTACK_TYPE, Skill.ARCANIST),
		CONDEMN_SOUL("Condemner","Curses those they kill, forcing them start battles with half base MHP until cured."
				,"Cure at a Shaman or Doctor. Lasts between battles."
				, Type.OTHER),
		NO_HOSTILE_CURSE("Indomitable Spirit","Immune to the CURSE status by hostile Persons."
				,"Does not apply to self-inflicted curses or outside of battle."
				,Type.SOCIAL),
		TACTIC_TEST("examine test","test"
				,"test"
				,Type.TACTIC_TYPE),
		TACTIC_SINGLE_OUT("Tactic: Single Out","Grants access to the Single Out tactic at all times."
				,WeaponAttackFactory.AttackBonus.SINGLE_OUT.desc
				,Type.TACTIC_TYPE),
		TACTIC_DUCK_ROLL("Tactic: Duck 'n Roll","Grants access to the Roll tactic at all times."
				,WeaponAttackFactory.AttackBonus.ROLL.desc
				,Type.TACTIC_TYPE),
		OPPORTUNIST("Opportunist","Grants a skill configuration to put active tactics in.",
				""
				,Type.ATTACK_TYPE),
		TACTIC_CHALLENGE("Tactic: Challenge","Grants access to the Challenge tactic at all times."
				,WeaponAttackFactory.AttackBonus.CHALLENGE.desc
				,Type.TACTIC_TYPE),
		
		
		;
	    private String name,desc, longDesc;
	    private Type type;
	    private Skill aliasFor;
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
	    	//grants a person a 'generic tactic' attack that they can get, with no secondary effects
	    	//these tend to be granted from a source that also grants one ATTACK_TYPE skill, for generating random tactics
	    	//these just represent the 'safe reliable' tactic variant
	    	//only the player uses the 'reliable' variants, the ai might choose to do the unreliable secondary effect one, though 
	    	TACTIC_TYPE,
	    	//ATTACK_ALIAS,
	    	//need to make a type that just aliases for another ATTACK_TYPE so you can have more than one
	    	FEATURE,//skills that are 'part' of a person or thing. Should be only granted by perks
	    	INTERNAL_USE_ONLY;//skills that should not be displayed
	    }

		Skill(String name,String desc, String mechanicDesc,Type type){
			this.name = name;
			this.desc = desc;
			this.longDesc = mechanicDesc;
			this.type = type;
		}
		
		Skill(String name,String desc, String mechanicDesc,Type type, Skill alias){
			this.name = name;
			this.desc = desc;
			this.longDesc = mechanicDesc;
			this.type = type;
			this.aliasFor = alias;
		}
		
		Skill(String name,String desc,boolean a, Type type, int num, String mechanicDesc){
			this.name = name;
			this.desc = desc;
			this.longDesc = mechanicDesc;
			this.type = type;
		}
		
		
		public String getName() {return name;}
		public String getDesc() {return desc;}
		public Type getType() {return type;}

		public void display() {
			extra.println(name + ": " + desc);
		}
		
		public String disp() {
			return (name + ": " + desc);
		}

		public String getLongDesc() {
			return longDesc;
		}
		
		public Skill getAliasFor() {
			return aliasFor;
		}
		
		public Skill getAliasOrSelf() {
			if (aliasFor == null) {
				return this;
			}
			return aliasFor;
		}

		public String explain() {
			return name + ": " + desc+ (longDesc != "" ? " ("+longDesc+")" : "");
		}
		
		public MenuItem getMenuView() {
			return new MenuLine() {

				@Override
				public String title() {
					return " " + name + ": " + desc+ (longDesc != "" ? " ("+longDesc+")" : "");
				}};
			
		}
		
		public MenuItem getMenuViewForPerson(Person p) {
			if (p == null) {
				return getMenuView();
			}
			return new MenuLine() {

				@Override
				public String title() {
					return " " + (p.hasSkill(Skill.this) ?extra.TIMID_RED+"HAVE: " : "") + extra.ITEM_VALUE+ name+extra.PRE_WHITE + ": " + desc+ (longDesc != "" ? " ("+longDesc+")" : "");
				}};
			
		}
}
