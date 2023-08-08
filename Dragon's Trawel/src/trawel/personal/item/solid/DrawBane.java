package trawel.personal.item.solid;

import trawel.extra;

public enum DrawBane {
	//values:
	//1 = basic
	//3 = decent value (repel)
	//4 = decent value, often magical and uncommon
	//5 = decent value, silver
	//6 = rare magical (unicorn horn)
	//8 = expensive (gold/virgin)
	
	TEST("test","test",0,0,false),
	GARLIC("garlic","Used to repel vampires. Edible.",1,.3,true),
	SILVER("silver","A shiny metal of decent value.",5,2,true),
	MEAT("meat","A cut of meat. Slightly edible.",1,.5,true),
	CEON_STONE("eon stone","A cracked stone with infinite choices- all are terrible.",4,3,true),
	NOTHING("nothing","Empty slot.",0,0,false),
	PROTECTIVE_WARD("protective ward","Used to ward off monsters and other maladies when on the roads.",5,4,false),
	ENT_CORE("ent heartwood","The core of an ent tree.",4,2,true),
	BLOOD("blood","A vial of blood.",1,.1,true),
	REPEL("beast repellant","Repels wolves, bears, and other mostly mundane creates.",3,1,false),
	BAT_WING("bat wing","A witch's reagent, useful in some potions.",1,.4,true),
	MIMIC_GUTS("mimic guts","A witch's reagent. Has slight intrinsic value.",1,.75,true),
	CLEANER("cloth","Discard to clean blood off of your equipment.",1,0,false),
	APPLE("apple","Used to increase potion thickness. Edible.",1,.3,true),
	WOOD("wood","A good building resource. Can be used to increase potion thickness, but risks ruining it.",1,.3,true),
	HONEY("honey","Edible and quite tasty.",2,.5,true),
	WAX("wax","Used to increase potion thickness.",1,.5,true),
	PUMPKIN("pumpkin","A hearty meal. Edible.",1,.6,true), 
	BEATING_HEART("beating heart","Is it... still alive?!",4,1,false), 
	EGGCORN("eggcorn","What is this plant? Edible?",1,.5,true),
	TRUFFLE("truffle","A prized mushroom. Edible",5,2,true), 
	GOLD("gold chunk","A shiny metal of great value.",8,4,false),
	UNICORN_HORN("unicorn horn","An impressive magic horn.",6,3,false),
	VIRGIN("virgin","You have them tied up.",8,.5,true),
	UNDERLEVELED("nothing","Empty slot.",0,0,false),
	KNOW_FRAG("knowledge fragment","Bring this to a library to study, and gain feat points.",2,1,false),
	LIVING_FLAME("living flame","A living flame, for a forge.",4,3,true), 
	GRAVE_DIRT("grave dirt","A witch's reagent.",2,.25,true),
	TELESCOPE("telescope","A large telescope.",4,2,true),
	SINEW("mystic sinew","Slightly possessed flesh.",1,.1,false);
	
	private String name, flavorText;
	private int value;
	private double mVal;
	private boolean anyBrew;
	DrawBane(String name, String flavorText,int val, double mVal, boolean _anyBrew) {
		this.name = name;
		this.flavorText = flavorText;
		this.value = val;
		this.mVal = mVal;
		anyBrew = _anyBrew;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFlavor() {
		return flavorText;
	}
	
	public int getValue() {
		return value;
	}
	public double getMValue() {
		return mVal;
	}
	
	public boolean getCanBrew() {
		return anyBrew;
	}
	
	public static DrawBane forCollector() {
		return extra.choose(REPEL,CEON_STONE,LIVING_FLAME,TELESCOPE,PROTECTIVE_WARD,SILVER,extra.choose(UNICORN_HORN,GOLD,VIRGIN,KNOW_FRAG));
	}
	
	
}
