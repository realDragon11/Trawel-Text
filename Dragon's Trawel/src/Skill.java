
public enum Skill implements java.io.Serializable{
	
	    BEER_BELLY("Beer Belly","Use two beers at once in battle!",Type.FIGHTER,1),
	    BLOODTHIRSTY("Bloodthirsty","Gain some hp each time you attack!",Type.FIGHTER,1),
	    
		BEER_LOVER("Beer Lover","Endless beer. Never have to go to a tavern again.",Type.FIGHTER,2),
		HPSENSE("Health Sense","Discern how much hp the defender has after each attack.",Type.FIGHTER,2),
		
		SPEEDDODGE("Speed Dodge","Attack sooner after you dodge.",Type.FIGHTER,3),
		ARMORHEART("Armor Heart","Gain some hp every time your armor blocks an attack.",Type.FIGHTER,3),
		
		COUNTER("Counter","Attack slightly sooner after you are attacked.",Type.FIGHTER,4),
		BLITZ("Blitz","Attack slightly quicker.",Type.FIGHTER,4),
		
		DODGEREF("Refreshing Dodge","Gain some hp every time you dodge.",Type.FIGHTER,5),
		ARMORSPEED("Glancing Blow","Attack sooner after your armor blocks an attack.",Type.FIGHTER,5),
		
		BERSERKER("Berserker","Remove examine from possible attacks you can make.",Type.FIGHTER,6),
		DSTRIKE("Decisive Strike","Instantly kill anyone you damage by more than 80% in one blow.",Type.FIGHTER,6),
		//LAST_STAND("Last Stand","When reduced to 0 hp or lower, stay alive at 1 hp.",Type.FIGHTER,3),
		
		
		
		
		RESTOCK("Restocker","Can pay for stores to restock.",Type.TRADER,1),
		EXPANDER("Expander","All stores gain an additional item.",Type.TRADER,1),
		
		SKILLPLUS("SP Master","Gain 2 skill points.",Type.TRADER,2),
		INHERIT("Inheriter","Gain 500 gold.",Type.TRADER,2),
		
		LOOTER("Expert Looter","Gain some gold each time you loot a corpse.",Type.TRADER,3),
		
		
		
		TOWNSENSE("Connection Sense","Tell how many connections a town has.",Type.EXPLORER,1),
		TIERSENSE("Tier Sense","Tell what tier a town belongs to.",Type.EXPLORER,1),
		
		SHIPSENSE("Shipyard Sense","Tell if towns have a shipyard.",Type.EXPLORER,2),
		TELESENSE("Teleport Sense","Tell if towns have a teleport shop.",Type.EXPLORER,2),
		
		SHOPSENSE("Shop Sense","Tell if towns have at least one shop.",Type.EXPLORER,3),
		ARENASENSE("Arena Sense","Tell if towns have at least one arena.",Type.EXPLORER,3),
		
		
		
		MAGE_TRAINING("Mage Training","Unlock your inner magic potential.",Type.MAGE,1),
		LIFE_MAGE("Life Mage","Unlock healing magic spells.",Type.MAGE,1),//good for out of combat
		
		ELEMENTAL_MAGE("Elemental Mage","Unlock elemental magic spells.",Type.MAGE,2),
		DEATH_MAGE("Death Mage","Unlock necromantic magic spells.",Type.MAGE,2),
		
		
		ENDSKILL("","",Type.FIGHTER,0);
	    private String name,desc;
	    private Type type;
	    private int level;
	    public enum Type{
	    	TRADER, EXPLORER, FIGHTER, MAGE;
	    }
		Skill(String name,String desc,Type t, int lvl){
			this.name = name;
			this.desc = desc;
			type = t;
			level = lvl;
		}   
		
		public String getName() {return name;}
		public String getDesc() {return desc;}
		public Type getType() {return type;}
		public int getLevel() {return level;}
}
