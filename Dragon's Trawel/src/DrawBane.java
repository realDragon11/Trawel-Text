
public enum DrawBane {
	TEST("test","test",0,0),
	GARLIC("garlic","Used to repel vampires.",20,.3),
	SILVER("silver","A shiny metal.",100,2),
	MEAT("meat","A cut of meat.",20,.5),
	CEON_STONE("eon stone","A cracked stone with infinite choices.",100,3),
	NOTHING("nothing","Empty slot.",0,0),
	PROTECTIVE_WARD("protective ward","Used to repel monsters.",200,4),
	ENT_CORE("ent core","The core of an ent tree.",50,2),
	BLOOD("blood","A vial of blood.",40,.1),
	REPEL("beast repellant","Repels wolves and bears.",100,1),
	BAT_WING("bat wing","A witch's reagent.",10,.4),
	MIMIC_GUTS("mimic guts","A witch's reagent.",50,.75),
	CLEANER("cloth","Discard to clean blood off of your equipment.",5,0),
	APPLE("apple","Used to increase potion thickness.",20,.3),
	WOOD("wood","A good building resource.",30,.3),
	HONEY("honey","A witch's reagent.",40,.5),
	WAX("wax","A witch's reagent.",40,.5),
	PUMPKIN("pumpkin","A hearty meal.",30,.6), 
	BEATING_HEART("beating heart","Is it... still alive?!",100,1), 
	EGGCORN("eggcorn","What is this plant?",50,.5),
	TRUFFLE("truffle","A rare food.",200,2), 
	GOLD("gold chunk","A shiny metal.",300,4);
	private String name, flavorText;
	private int value;
	private double mVal;
	DrawBane(String name, String flavorText,int val, double mVal) {
		this.name = name;
		this.flavorText = flavorText;
		this.value = val;
		this.mVal = mVal;
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
	
	
	
	
}
