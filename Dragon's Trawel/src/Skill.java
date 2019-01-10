
public enum Skill implements java.io.Serializable{
	
	    BEER_BELLY("Beer Belly","Use two beers at once in battle!",true,Type.FIGHTER,1),
	    BLOODTHIRSTY("Bloodthirsty","Gain some hp each time you attack!",true,Type.FIGHTER,1),
	    
		BEER_LOVER("Beer Lover","Endless beer. Never have to go to a tavern again.",true,Type.FIGHTER,2),
		HPSENSE("Health Sense","Discern how much hp the defender has after each attack.",false,Type.FIGHTER,2),
		
		SPEEDDODGE("Speed Dodge","Attack sooner after you dodge.",true,Type.FIGHTER,3),
		ARMORHEART("Armor Heart","Gain some hp every time your armor blocks an attack.",true,Type.FIGHTER,3),
		
		COUNTER("Counter","Attack slightly sooner after you are attacked.",true,Type.FIGHTER,4),
		BLITZ("Blitz","Attack slightly quicker.",true,Type.FIGHTER,4),
		
		DODGEREF("Refreshing Dodge","Gain some hp every time you dodge.",true,Type.FIGHTER,5),
		ARMORSPEED("Glancing Blow","Attack sooner after your armor blocks an attack.",true,Type.FIGHTER,5),
		
		BERSERKER("Berserker","Remove examine from possible attacks you can make.",true,Type.FIGHTER,6),
		DSTRIKE("Decisive Strike","Instantly kill anyone you damage by more than 80% in one blow.",true,Type.FIGHTER,6),
		
		KUNG_FU("Hand to Hand","Gain martial arts attacks.",true,Type.FIGHTER,7),
		//LAST_STAND("Last Stand","When reduced to 0 hp or lower, stay alive at 1 hp.",Type.FIGHTER,3),
		
		
		
		
		RESTOCK("Restocker","Can pay for stores to restock.",false,Type.TRADER,1),
		EXPANDER("Expander","All stores gain an additional item.",false,Type.TRADER,1),
		
		SKILLPLUS("SP Master","Gain 2 skill points.",false,Type.TRADER,2),
		INHERIT("Inheriter","Gain 500 gold.",false,Type.TRADER,2),
		
		LOOTER("Expert Looter","Gain some gold each time you loot a corpse.",false,Type.TRADER,3),
		
		
		
		TOWNSENSE("Connection Sense","Tell how many connections a town has.",false,Type.EXPLORER,1),
		TIERSENSE("Tier Sense","Tell what tier a town belongs to.",false,Type.EXPLORER,1),
		
		SHIPSENSE("Shipyard Sense","Tell if towns have a shipyard.",false,Type.EXPLORER,2),
		TELESENSE("Teleport Sense","Tell if towns have a teleport shop.",false,Type.EXPLORER,2),
		
		SHOPSENSE("Shop Sense","Tell if towns have at least one shop.",false,Type.EXPLORER,3),
		ARENASENSE("Arena Sense","Tell if towns have at least one arena.",false,Type.EXPLORER,3),
		
		
		
		MAGE_TRAINING("Mage Training","Unlock your inner magic potential.",true,Type.MAGE,1),
		LIFE_MAGE("Life Mage","Unlock healing magic spells.",true,Type.MAGE,1),//good for out of combat
		
		ELEMENTAL_MAGE("Elemental Mage","Unlock elemental magic spells.",false,Type.MAGE,2),
		DEATH_MAGE("Death Mage","Unlock necromantic magic spells.",true,Type.MAGE,2),
		ARMOR_MAGE("Armor Mage","Unlock armor repairing magic spells.",false,Type.MAGE,2),
		ILLUSION_MAGE("Illusion Mage","Unlocks the befuddle spell.",false,Type.MAGE,2),
		
		
		PARRY("Parry","Gives you a parrying dagger.",true,Type.DEFENDER,1),
		SHIELD("Shield","Gives you a shield.",true,Type.DEFENDER,1),
		
		GOOFFENSIVE("Go on the offensive","Allows you to attack with your defensive item.",true,Type.DEFENDER,2),//false for now
		
		
		ENDSKILL("","",false,Type.FIGHTER,0);
	    private String name,desc;
	    private Type type;
	    private int level;
	    private boolean AITake;
	    public enum Type{
	    	TRADER, EXPLORER, FIGHTER, MAGE, DEFENDER;
	    }
		Skill(String name,String desc,boolean AITake, Type t, int lvl){
			this.name = name;
			this.desc = desc;
			type = t;
			level = lvl;
			this.AITake = AITake;
		}   
		
		public String getName() {return name;}
		public String getDesc() {return desc;}
		public Type getType() {return type;}
		public int getLevel() {return level;}
		public boolean getAITake() {return AITake;}
}
