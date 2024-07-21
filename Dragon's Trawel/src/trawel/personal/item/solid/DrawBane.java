package trawel.personal.item.solid;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;

public enum DrawBane {
	//values:
	//1 = basic
	//3 = decent value (repel)
	//4 = decent value, often magical and uncommon
	//5 = decent value, silver
	//6 = rare magical (unicorn horn)
	//8 = expensive (gold/virgin)
	GARLIC("Garlic",null,TrawelColor.NAME_ITEM_FOOD,"Used to repel vampires. Edible.",1,.3,true),
	SILVER("Silver Chunk",null,TrawelColor.NAME_ITEM_VALUE,"A shiny metal of decent value. Alchemists have given up trying to transmute other metals into gold.",5,2,true),
	MEAT("Meat",null,TrawelColor.NAME_ITEM_FOOD,"A cut of meat. Slightly edible.",1,.5,true),
	CEON_STONE("Eon Stone",null,TrawelColor.NAME_ITEM_MAGIC,"A cracked stone with infinite choices- all are terrible. Useful for transmutation.",4,1,true),
	PROTECTIVE_WARD("Ward",null,TrawelColor.NAME_ITEM_MAGIC,"Used to ward off monsters and other maladies when out and about.",6,8,false),
	ENT_CORE("Heartwood",null,TrawelColor.NAME_ITEM_MAGIC,"The central core of a living tree. Brew it!",4,2,true),
	BLOOD("Blood",null,TrawelColor.NAME_ITEM_MATERIAL,"A vial of blood. Brew it!",1,.1,true),
	REPEL("Repellant",null,TrawelColor.NAME_ITEM_VALUE,"Repels wolves, bears, and other mostly mundane creatures.",3,1,false),
	//bat wing not edible
	BAT_WING("Bat Wing",null,TrawelColor.NAME_ITEM_MATERIAL,"A flappy appendage torn from a bat. Brew it!",1,.4,true),
	//mimic guts not edible
	MIMIC_GUTS("Mimic Guts",null,TrawelColor.NAME_ITEM_MATERIAL,"An alternative reward from a treasure chest. Brew it!",1,.75,true),
	CLOTH("Cloth",null,TrawelColor.NAME_ITEM_MISC,"Hides Wealth. Discard to clean blood off of your equipment.",1,0,false),
	APPLE("Apple",null,TrawelColor.NAME_ITEM_FOOD,"Used to increase potion thickness, but might turn it into a stew instead. Edible.",1,.3,true),
	WOOD("Wood","Wood",TrawelColor.NAME_ITEM_MATERIAL,"A good building resource. Can be used to boost potion thickness, but risks a botch.",1,.3,true),
	HONEY("Honey",null,TrawelColor.NAME_ITEM_FOOD,"Edible and quite tasty.",2,.5,true),
	WAX("Wax","Waxes",TrawelColor.NAME_ITEM_MATERIAL,"Used to increase potion thickness with little side effects... unless you add honey.",1,.5,true),
	PUMPKIN("Pumpkin",null,TrawelColor.NAME_ITEM_FOOD,"A hearty meal. Edible.",1,.6,true), 
	BEATING_HEART("Beating Heart",null,TrawelColor.NAME_ITEM_MAGIC,"Is it... still alive?!",4,1,false), 
	EGGCORN("Eggcorn",null,TrawelColor.NAME_ITEM_FOOD,"What is this plant? Edible?",1,.5,true),
	TRUFFLE("Truffle",null,TrawelColor.NAME_ITEM_FOOD,"A prized mushroom. Edible.",5,2,true), 
	GOLD("Gold Chunk",null,TrawelColor.NAME_ITEM_VALUE,"An enchantable metal of great value. Bring it to a Merchant's Guild!",8,10,false),
	UNICORN_HORN("Unicorn Horn",null,TrawelColor.NAME_ITEM_MAGIC,"An impressive magic horn hacked from a horse that holds harmful intent at bay. Brew it!",6,3,true),
	VIRGIN("Virgin",null,TrawelColor.NAME_ITEM_VALUE,"Bound and gagged, tied and dejected. Their soul has been claimed by evil powers. There is no happy ending here.",8,.5,true),
	KNOW_FRAG("Feat Fragment",null,TrawelColor.NAME_ITEM_VALUE,"Aetheric wisdom partially bound to a scrap of paper. Bring it to a library to study and absorb for feat point progress!",100,1,false),
	LIVING_FLAME("Living Flame",null,TrawelColor.NAME_ITEM_MAGIC,"A fierce, fun-size fire that burns brightly without fuel. The perfect centerpiece to any forge!",4,3,true), 
	GRAVE_DIRT("Grave Dirt",null,TrawelColor.NAME_ITEM_MATERIAL,"Dirt that's somewhat worse for wear after much mixing with mortality. Brew it!",1,.2,true),
	TELESCOPE("Telescope",null,TrawelColor.NAME_ITEM_MATERIAL,"A large telescope, fit for gazing into the sky over overlooking an area.",4,2,true),
	SINEW("Mystic Sinew",null,TrawelColor.NAME_ITEM_MATERIAL,"Slightly possessed flesh. Brew it!",1,.1,true),
	GRAVE_DUST("Grave Dust",null,TrawelColor.NAME_ITEM_MAGIC,"The ashen remains of heavily necromantic bones. Not to be confused with grave dirt. Brew it!",3,.9,true),
	//for draws and banes, not items
	EV_NOTHING("nothing",null,TrawelColor.RESULT_ERROR,"Empty slot.",0,0,false),
	EV_DAYLIGHT("daylight",null,TrawelColor.RESULT_ERROR,"daytime",0,0,false),
	EV_WEALTH("money",null,TrawelColor.RESULT_ERROR,"money",0,0,false),
	EV_BLOOD("bloody",null,TrawelColor.RESULT_ERROR,"blood",0,0,false)
	;
	private final String name, namePlural, flavorText, color;
	private final int value;
	private final double mVal;
	private final boolean anyBrew;
	DrawBane(String name, String namePlural, String color, String flavorText,int val, double mVal, boolean _anyBrew) {
		this.name = name;
		this.namePlural = namePlural;
		this.color = color;
		this.flavorText = flavorText;
		this.value = val;
		this.mVal = mVal;
		anyBrew = _anyBrew;
	}
	
	public String getName() {
		if (color != null) {
			return color+name+TrawelColor.COLOR_RESET;
		}
		return name;
	}
	
	public String getNamePlural() {
		if (namePlural != null) {
			if (color != null) {
				return color+namePlural+TrawelColor.COLOR_RESET;
			}
			return namePlural;
		}
		if (color != null) {
			return color+name+"s"+TrawelColor.COLOR_RESET;
		}
		return name+"s";
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
	
	public enum DrawList{
		COLLECTOR, GENERIC_STORE, WITCH_STORE, FOOD;
	}
	
	public static void setup() {
		DrawList[] vals = DrawList.values();
		int size = vals.length;
		weightList = new WeightedTable[size];
		subLists = new DrawBane[size][];
		for (int i = size-1; i >=0 ;i--) {
			float[] weight;
			DrawBane[] list;
			switch (vals[i]) {
			case COLLECTOR:
				weight = new float[10];
				list = new DrawBane[10];
				
				weight[0] = 2f;
				list[0] = REPEL;
				weight[1] = 1f;
				list[1] = CEON_STONE;
				weight[2] = 1f;
				list[2] = LIVING_FLAME;
				weight[3] = 4f;
				list[3] = TELESCOPE;
				weight[4] = 1.5f;
				list[4] = PROTECTIVE_WARD;
				weight[5] = 1f;
				list[5] = SILVER;
				weight[6] = .6f;
				list[6] = UNICORN_HORN;
				weight[7] = .3f;
				list[7] = GOLD;
				weight[8] = .1f;
				list[8] = PUMPKIN;
				weight[9] = .5f;
				list[9] = KNOW_FRAG;
				
				subLists[i] = list;
				weightList[i] = new WeightedTable(weight);
				break;
		case GENERIC_STORE:
			weight = new float[9];
			list = new DrawBane[9];
			
			weight[0] = 3f;
			list[0] = MEAT;
			weight[1] = 2f;
			list[1] = GARLIC;
			weight[2] = .5f;
			list[2] = BLOOD;
			weight[3] = 4f;
			list[3] = REPEL;
			weight[4] = 2f;
			list[4] = CLOTH;
			weight[5] = .7f;
			list[5] = SILVER;
			weight[6] = .5f;
			list[6] = PROTECTIVE_WARD;
			weight[7] = .1f;
			list[7] = GOLD;
			weight[8] = .1f;
			list[8] = TRUFFLE;
			
			subLists[i] = list;
			weightList[i] = new WeightedTable(weight);
			break;
		case WITCH_STORE:
			weight = new float[12];
			list = new DrawBane[12];
			
			weight[0] = 1f;
			list[0] = MEAT;
			weight[1] = 3f;
			list[1] = BAT_WING;
			weight[2] = 5f;
			list[2] = APPLE;
			weight[3] = 1f;
			list[3] = CEON_STONE;
			weight[4] = 2f;
			list[4] = MIMIC_GUTS;
			weight[5] = 1f;
			list[5] = BLOOD;
			weight[6] = 2f;
			list[6] = WAX;
			weight[7] = 2f;
			list[7] = WOOD;
			weight[8] = .3f;
			list[8] = VIRGIN;
			weight[9] = 1f;
			list[9] = GRAVE_DUST;
			weight[10] = .01f;
			list[10] = BEATING_HEART;
			weight[11] = .4f;
			list[11] = SINEW;
			
			subLists[i] = list;
			weightList[i] = new WeightedTable(weight);
			break;
		case FOOD:
			weight = new float[7];
			list = new DrawBane[7];
			
			weight[0] = 20f;
			list[0] = MEAT;
			weight[1] = 20f;
			list[1] = APPLE;
			weight[2] = 7f;
			list[2] = HONEY;
			weight[3] = 10f;
			list[3] = PUMPKIN;
			weight[4] = 5f;
			list[4] = EGGCORN;
			weight[5] = 3f;
			list[5] = TRUFFLE;
			weight[6] = .1f;
			list[6] = VIRGIN;
			
			subLists[i] = list;
			weightList[i] = new WeightedTable(weight);
			break;
		}
		}
	}
	
	public static DrawBane draw(DrawList list) {
		return subLists[list.ordinal()][weightList[list.ordinal()].random(Rand.getRand())];
	}
	
	
	private static WeightedTable[] weightList;
	private static DrawBane[][] subLists;
	public static DrawBane getByName(String targetName) {
		DrawBane[] dbs = DrawBane.values();
		for (int i = dbs.length-1; i >=0; i--) {
			if (dbs[i].getName() == targetName) {
				return dbs[i];
			}
		}
		return null;
	}
	
	
}
