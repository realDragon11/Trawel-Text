package trawel.personal.classless;

import derg.menus.MenuItem;
import derg.menus.MenuLine;
import trawel.battle.attacks.AttackBonus;
import trawel.core.Print;
import trawel.helper.constants.StringTip;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.item.solid.DrawBane;

public enum Skill{
	
		UNIMPLEMENTED("Unimplemented","Skills after this point and before the next heading are old skills that haven't been updated."
				,true,Type.INTERNAL_USE_ONLY,1,""),
	    
	    
	    
		BEER_LOVER("Beer Lover","Endless beer. Never have to go to a tavern again.",true,Type.FIGHTER,2,""),
		
		
		TOWNSENSE("Connection Sense","Tell how many connections a town has.",false,Type.EXPLORER,1,""),
		TIERSENSE("Tier Sense","Tell what tier a town belongs to.",false,Type.EXPLORER,1,""),
		
		SHIPSENSE("Shipyard Sense","Tell if towns have a shipyard.",false,Type.EXPLORER,2,""),
		TELESENSE("Teleport Sense","Tell if towns have a teleport shop.",false,Type.EXPLORER,2,""),
		
		SHOPSENSE("Shop Sense","Tell if towns have at least one shop.",false,Type.EXPLORER,3,""),
		ARENASENSE("Arena Sense","Tell if towns have at least one arena.",false,Type.EXPLORER,3,""),
		
		MAGE_TRAINING("Mage Training","Unlock your inner magic potential.",true,Type.MAGE,1,""),
		
		MONEY_MAGE("Money Mage","Generate money slowly.",false,Type.MAGE,1,""),//good for out of combat
		
		ELEMENTAL_MAGE("Elemental Mage","Unlock elemental magic spells.",false,Type.MAGE,2,""),
		DEATH_MAGE("Death Mage","Unlock necromantic magic spells.",true,Type.MAGE,2,""),
		
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
		TA_NAILS("Tough as Nails","20% chance to negate incoming wounds."
				,"Can apply to any wound inflicted on them, preventing it from processing entirely.",
				Type.DEFENSE),
		RAW_GUTS("Raw Guts","Resist damage up to 3% LHP, by Torso condition."
				,"Can only resist up to half the base damage of an attack. Only applies to hit attacks."
				,Type.DEFENSE),
		KUNG_FU("Unarmed Attacks","Gain martial arts attacks."
				,"Adds one attack option per turn, which is based on strength and dexterity, TODO."//TODO
				,Type.ATTACK_TYPE),
		BLITZ("Blitz","Actions take 3 less instants to complete than expected."
				,"Does not allow you to act sooner in order- but does change how much time passes when you do get to act."
				,Type.SPEED),
		COUNTER("Counter","When Attacked: Advance 2 instants."
				,"Attacks are when to-hit is rolled on you."
				,Type.SPEED),
		SPEEDDODGE("Speed Dodge","On Dodge: Attack 10 instants sooner."
				,"Applies to each dodge. Does not apply to misses."
				,Type.SPEED),
		DODGEREF("Refreshing Dodge","On Dodge: Gain 1% of attacker's LHP."
				,"Uncapped total HP gain, can exceed MHP. Attacker's LHP caps at 2 levels higher than your own. Does not apply to misses."
				,Type.SPEED),
		P_BREWER("Brewer","Created potions can be sipped twice as many times before running out."
				,""
				,Type.CRAFT),
		TOXIC_BREWS("Toxic Brews","When drinking a "+Effect.CURSE.getName()+" potion, gain up to 3 random minor positive potion effects. After drinking "+Effect.CURSE.getName()+" in battle, only lose 25% MHP. All created potions can be sipped one extra time per Botch reagent."
				,"Botch reagants can create "+Effect.CURSE.getName()+" potions, like "+DrawBane.WOOD.getName()+"."
				,Type.CRAFT),
		CURSE_MAGE("Curse Whisperer","On Death: Curse Killer."
				,"Curse halves HP. Cure at a Shaman or Doctor. Lasts between battles."
				, Type.SOCIAL),
		KILLHEAL("Vampiric Spirit","On Kill: Gain 5% of dead's LHP."
				,"Can overheal. Dead LHP caps at 2 levels higher than your own. 'Kill' means being the last person to attack the target before they die."
				,Type.OFFENSE),
		SPUNCH("Sucker Punch","On Impact: Slow down target's next action by 2% of their total time."
				,StringTip.IMPACT_TIP
				,Type.OFFENSE),
		DSTRIKE("Decisive Strike","On Impact: Instantly kill target you damage by more than 70% of their MHP in one blow."
				,"Max HP is closely aligned to how much HP they start with, but can go higher. Must be part of an 'Impactful' action."
				,Type.OFFENSE),
		BLOODTHIRSTY("Bloodthirsty","On Impact: Heal HP equal to the lower of 1% attacker's LHP and defender's LHP."
				,"Cannot overheal."
				,Type.OFFENSE),
		ARMOR_TUNING("Armor Tuning","Armor is 20% stronger at the start of every battle."
				,StringTip.ARMOR_TIP
				,Type.DEFENSE),
		ARMORSPEED("Glancing Blow","On Armor Block: Attack 10 instants sooner."
				,"Applies once per attack."
				,Type.DEFENSE),//defense not speed, speed is more 'fast in mobility'
		ARMORHEART("Armor Heart","On Armor Block: Gain 2% of attacker's LHP."
				,"Cannot overheal. Attacker's LHP caps at 4 levels higher than your own. Applies once per attack."
				,Type.DEFENSE),
		MESMER_ARMOR("Mesmer Armor","When attacked for no Impact, Roll a contested Clarity vs highest attribute to confuse the attacker."
				,"Attacks with no impact typically are dodged, miss, or are blocked by armor. Confuse makes the target's next attack capable of friendly fire."
				,Type.DEFENSE),
		ARCANIST("Arcanist","Unlocks a magical swappable attack option."
				,"Use the skill attack config creator in the skills menu to select a source to stance your attacks from."
				,Type.ATTACK_TYPE),
		MAGE_FRUGAL("Frugal Mage OLD","Grants you a better deal when trading with Aether in shops, starting at +10%.",
				"For every 1 clarity past 100, get +1% more. No penalty for sub 100 clarity."
				,Type.SOCIAL),
		OPENING_MOVE("Opening Move","Grants two bonus weapon attack choices at the start of every battle.",
				"You can have a max of 5 weapon attack choices at a time, stacks will only be consumed if they add attacks."
				,Type.OFFENSE),
		QUICK_START("Quick Start","Grants a stack of Advantage at the start of every battle."
				,StringTip.ADV_TIP
				,Type.SPEED),
		PRESS_ADV("Press the Advantage","On Any Crit: Gain one stack of advantage."
				,StringTip.CRIT_TIP+" "+StringTip.ADV_TIP
				,Type.SPEED),
		BLOODDRINKER("Blood Drinker","When Attacked: Gain HP equal to half attacker's bleed damage tick that attack."
				,"Can overheal. Stacks with other sources of bleed healing.."
				,Type.SOCIAL),
		NIGHTVISION("Nightvision","Can see in the dark."
				,"Helps determine things in Graveyards."
				, Type.OTHER),
		//can be depowered
		NPC_BURN_ARMOR("Flaming Strikes","On Impact: Burn armor by 10%."
				,""
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
		M_PYRO("Pyromantic","30% chance to convert 'Grazed' into a random Ignite wound on any attack."
				,"Applies before choosing attack. Stacks with other Elementalist subskills, up to 90%."
				,Type.OFFENSE),
		/**
		 * NOTE: do not grant without ELEMENTALIST
		 */
		M_CRYO("Cryomantic","30% chance to convert 'Grazed' into a random Frost wound on any attack."
				,"Applies before choosing attack. Stacks with other Elementalist subskills, up to 90%."
				,Type.OFFENSE),
		/**
		 * NOTE: do not grant without ELEMENTALIST
		 */
		M_AERO("Aeromantic","30% chance to convert 'Grazed' into a random Elec wound on any attack."
				,"Applies before choosing attack. Stacks with other Elementalist subskills, up to 90%."
				,Type.OFFENSE),
		/**
		 * NOTE: do not grant without ELEMENTALIST
		 */
		M_PYRO_BOOST("Ignite Boost","On Elemental Wound: Gain Parry."
				,StringTip.ELEMBOOST_TIP + " " + StringTip.PARRY_TIP
				,Type.DEFENSE),
		/**
		 * NOTE: do not grant without ELEMENTALIST
		 */
		M_CYRO_BOOST("Frost Boost","On Elemental Wound: Boost armor by +10% flat."
				,StringTip.ELEMBOOST_TIP + " " +StringTip.ARMOR_TIP
				,Type.DEFENSE),
		/**
		 * NOTE: do not grant without ELEMENTALIST
		 */
		M_AERO_BOOST("Elec Boost","On Elemental Wound: Add 4 instants to target's action."
				,StringTip.ELEMBOOST_TIP
				,Type.OFFENSE),
		STERN_STUFF("Sterner Stuff","On Death: Once per battle, roll a contested Strength check vs attacker's highest attribute to survive at 1 HP."
				,"Instant kill attacks that wouldn't deal enough damage to kill otherwise skip the roll, but still leave them at 1 HP.",
				Type.DEFENSE),
		REACTIVE_DODGE("Reactive Dodge","On Dodge: Gain one stack of Advantage."
				,"Does not apply to misses. "+StringTip.ADV_TIP
				,Type.SPEED),
		ARCANIST_2("Multi-Magical","Grants another Arcanist skill attack configuration."
				,""
				,Type.ATTACK_TYPE, Skill.ARCANIST),
		CONDEMN_SOUL("Condemner","On Kill: Apply Curse."
				,"Curse halves HP. Cure at a Shaman or Doctor. Lasts between battles."
				, Type.SOCIAL),
		NO_HOSTILE_CURSE("Indomitable Spirit","Immune to the CURSE status by hostile Persons."
				,"Does not apply to self-inflicted curses or outside of battle."
				,Type.SOCIAL),
		TACTIC_TEST("examine test","test"
				,"test"
				,Type.TACTIC_TYPE),
		TACTIC_SINGLE_OUT("Tactic: Single Out","Grants access to the Single Out tactic at all times."
				,AttackBonus.SINGLE_OUT.desc
				,Type.TACTIC_TYPE),
		TACTIC_DUCK_ROLL("Tactic: Duck 'n Roll","Grants access to the Roll tactic at all times."
				,AttackBonus.ROLL.desc
				,Type.TACTIC_TYPE),
		OPPORTUNIST("Opportunist","Grants a skill configuration to put active tactics in.",
				""
				,Type.ATTACK_TYPE),
		TACTIC_CHALLENGE("Tactic: Challenge","Grants access to the Challenge tactic at all times."
				,AttackBonus.CHALLENGE.desc
				,Type.TACTIC_TYPE),
		TACTIC_TAKEDOWN("Tactic: Planned Takedown","Grants access to the Takedown tactic at all times."
				,AttackBonus.TAKEDOWN.desc
				,Type.TACTIC_TYPE),
		NO_QUARTER("No Quarter","On Kill: Gain Takedown effect and two stacks of Advantage."
				,"Takedown applies Knockout on your next impactful attack. It stacks in duration. "+StringTip.ADV_TIP
				,Type.OFFENSE),
		ARMOR_MAGE("Armor Mage","Increases armor defenses based on Clarity."
				,"Adds 1/60th of Clarity to each piece of armor's SBP."
				,Type.DEFENSE),
		BEER_BELLY("Beer Belly","Doubles LHP bonus for drinking beer."
				,"From 5% to 10% increase, does not count as max HP."
				,Type.DEFENSE),
		DEADLY_AIM("Deadly Aim","On Any Crit: Deal 20% bonus damage."
				,StringTip.CRIT_TIP
				,Type.OFFENSE),
		LIFE_MAGE("Life Mage","+5% Clarity as MHP."
				,""
				,Type.DEFENSE),
		POTION_CHUGGER("Potion Chugger","After Potion Drink: Regenerates 10% LHP at the end of second turn."
				,"Applies through the Breathing Effect."
				,Type.OTHER),
		FETID_FUMES("Fetid Fumes","When Attacked: Force a contested Clarity check at half Clarity to apply Miasma. Attackers already inflicted with Miasma suffer a -10% hit roll."
				,StringTip.MIASMA_TIP
				,Type.DEFENSE),
		FEVER_STRIKE("Fever Strike","On Attack: Force a contested Clarity check to apply two stacks of Miasma. When targeting opponents already suffering from Miasma with physical attacks, deal +10% damage as Decay damage."
				,StringTip.MIASMA_TIP+" Damage bonus applies on targeting, not on swing."
				,Type.OFFENSE),
		BIG_BAG("Big Bag","Gain 3 Drawbane slots, for a total of 8."
				,""
				,Type.CRAFT),
		BULK("Bulk","+5% Strength as MHP."
				,""
				,Type.DEFENSE),
		RUNESMITH("Runesmith","Allows applying Runes at Enchanters. On-Hit enchantments are 1.3x as effective."
				,""
				,Type.CRAFT),
		RUNIC_BLAST("Runic Blast","On Impact Crit: Apply a wound of the elemental on-hit enchantment."
				,"Does not apply if used weapon doesn't have an element on-hit enchantment. "+StringTip.CRIT_TIP
				,Type.CRAFT),
		OPEN_VEIN("Open Vein","On Impact Crit: Apply "+Effect.MAJOR_BLEED.getName()+", preventing "+Effect.BLEED.getName() +" stacks from healing. Apply "+Effect.BLEED.getName()+" if already "+Effect.MAJOR_BLEED.getName()
				,StringTip.CRIT_TIP
				,Type.OFFENSE),
		AGGRESS_PARRY("Aggress Parry","On Any Crit: Grants Parry."
				,StringTip.CRIT_TIP+" "+StringTip.PARRY_TIP
				,Type.DEFENSE),
		LIVING_ARMOR("Living Armor","On Armor Block: Grants Parry."
				,StringTip.PARRY_TIP
				,Type.DEFENSE),
		SALVAGE("Salvage","On Impactful Crit: Boost your armor by +12% flat."
				,StringTip.CRIT_TIP+" "+StringTip.ARMOR_TIP
				,Type.DEFENSE),
		CHEF("Chef","Crafted "+Effect.HEARTY.getName()+" Potions have double sips. Sipping "+Effect.HEARTY.getName()+" applies one "+Effect.PADDED.getName()+" stack."
				,StringTip.PADDED_TIP
				,Type.CRAFT)
		
		/**
		 * move tactics here to keep them in order
		 */
		
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
	    	SPEED,//skills that tend to make you faster or take advantage of agility
	    	DEFENSE,//skills that provide direct defensive benefits
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
			Print.println(name + ": " + desc);
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
			return TrawelColor.ITEM_VALUE+ name+TrawelColor.COLOR_RESET + ": " + desc+ (longDesc != "" ? " ("+longDesc+")" : "");
		}
		
		public MenuItem getMenuView() {
			return new MenuLine() {

				@Override
				public String title() {
					return " " + TrawelColor.ITEM_VALUE+name+TrawelColor.COLOR_RESET + ": " + desc+ (longDesc != "" ? " ("+longDesc+")" : "");
				}};
			
		}
		
		public MenuItem getMenuViewForPerson(Person p) {
			if (p == null) {
				return getMenuView();
			}
			return new MenuLine() {

				@Override
				public String title() {
					return " " + (p.hasSkill(Skill.this) ?TrawelColor.TIMID_RED+"HAVE: " : "") + TrawelColor.ITEM_VALUE+ name+TrawelColor.PRE_WHITE + ": " + desc+ (longDesc != "" ? " ("+longDesc+")" : "");
				}};
			
		}
}
