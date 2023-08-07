package trawel.personal.classless;

import derg.menus.MenuItem;
import derg.menus.MenuLine;
import trawel.extra;

public enum Skill{
	
	    BEER_BELLY("Beer Belly","Use two beers at once in battle!",true,Type.FIGHTER,1,"Gives you the beer health bonus twice- if you have two beers to drink."),
	    BLOODTHIRSTY("Bloodthirsty","Gain some hp each time you attack!",true,Type.FIGHTER,1,""),
	    
		BEER_LOVER("Beer Lover","Endless beer. Never have to go to a tavern again.",true,Type.FIGHTER,2,""),
		HPSENSE("Health Sense","Discern how much hp the defender has after each attack. Improves examine as well.",false,Type.FIGHTER,2,""),
		KILLHEAL("Vampire","Gain a moderate amount of hp on a kill.",true,Type.FIGHTER,2,""),
		
		COUNTER("Counter","Attack slightly sooner after you are attacked.",true,Type.FIGHTER,3,""),
		BLITZ("Blitz","Attack slightly quicker.",true,Type.FIGHTER,3,""),
		
		BERSERKER("Berserker","Remove examine from possible attacks you can make.",true,Type.FIGHTER,4,""),
		DSTRIKE("Decisive Strike","Instantly kill anyone you damage by more than 80% in one blow.",true,Type.FIGHTER,4,""),
		SPUNCH("Sucker Punch","Your attacks slow down your foe.",true,Type.FIGHTER,4,""),
		
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
		CURSE_MAGE("Witch","Your attacks curse the opponent.",true,Type.MAGE,1,""),
		
		ELEMENTAL_MAGE("Elemental Mage","Unlock elemental magic spells.",false,Type.MAGE,2,""),
		DEATH_MAGE("Death Mage","Unlock necromantic magic spells.",true,Type.MAGE,2,""),
		ARMOR_MAGE("Armor Mage","Unlock armor repairing magic spells.",false,Type.MAGE,2,""),
		ILLUSION_MAGE("Illusion Mage","Unlocks the befuddle spell.",false,Type.MAGE,2,""),
		
		MAGE_POWER("Power Within","Replace one of your attacks with a spell and unlock your magic power.",false,Type.MAGE,3,""),//works with ai, bad choice tho
		MAGE_FRUGAL("Power Without","Decrease your magic power but increase your offensive and defensive skills.",true,Type.MAGE,3,""),
		
		IMAG_TRAINING("Mage Training","Hone your magic skills.",true,Type.MAGE,4,""),
		
		
		PARRY("Parry","Gives you a parrying dagger.",true,Type.DEFENDER,1,""),
		SHIELD("Shield","Gives you a shield.",true,Type.DEFENDER,1,""),
		
		SPEEDDODGE("Speed Dodge","Attack sooner after you dodge.",true,Type.DEFENDER,2,""),
		ARMORHEART("Armor Heart","Gain some hp every time your armor blocks an attack.",true,Type.DEFENDER,2,""),
		
		DODGEREF("Refreshing Dodge","Gain some hp every time you dodge.",true,Type.DEFENDER,3,""),
		ARMORSPEED("Glancing Blow","Attack sooner after your armor blocks an attack.",true,Type.DEFENDER,3,""),
		
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
		
		
		//INTERNAL MARKER VALUES, KEEP
		PLAYERSIDE(null,null,null,Type.INTERNAL_USE_ONLY),
		
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
	    	DEFENSE,//skills that provide defensive benefits
	    	ATTACK_TYPE,//skills that grant a new attack type, generally there should only be 5 of these total
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
