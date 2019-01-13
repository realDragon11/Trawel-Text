/***
 * An extension of Item, an armor has varying stats that can effect a person, and possibly and enchantment.
 * Different materials and different slots effect the attributes of items, as well as the level of the item.
 * 
 * @author Brian Malone
 * 2/5/2018
 *
 */
public class Armor extends Item {

	//instance variables
	private int armorType;//The slot which the armor goes into
	private String baseName;//what we call it, ie helm, helmet, hat, cap
	private String material;//what material the object is made from
	private int cost;//how much it costs in gold pieces
	//private int level;//the level of the item
	private int baseResist;//the base damage resistance of the item
	private double sharpResist, sharpActive;
	private double bluntResist, bluntActive;
	private double pierceResist, pierceActive;
	private double baseEnchant;//the multiplier of how powerful enchantments on the item are
	private int weight;
	private EnchantConstant enchantment;
	private boolean isEnchanted = false;
	private int effectiveCost;
	private double dexMod = 1;
	private Material mat;
	private double burnMod, freezeMod, shockMod;
	private double burned;
	private String matType;//ie heavy, light, chainmail
	private String baseMap;
	
	//constructors
	
	/**
	 * Make a new armor.
	 * @param newLevel (int)
	 */
	public Armor(int newLevel) {
		this(newLevel,(int)(Math.random()*5),MaterialFactory.randMat(true,false));
	}
	
	/**
	 * Make a new armor, that will go in a certain slot.
	 * @param newLevel (int)
	 * @param slot (int)
	 */
	public Armor(int newLevel,int slot) {
		this(newLevel,slot,MaterialFactory.randMat(true,false));		
	}
	
	/**
	 * Make a new armor that is of newLevel level, will go in slot slot, and has a certain material.
	 * This function should mostly be only used interally, but if you want a certain material to be generated, it will work.
	 * @param newLevel (int)
	 * @param slot (int)
	 * @param mat (String)
	 */
	public Armor(int newLevel, int slot,Material mati) {
	//initialize	
	armorType = slot;//type is equal to the array armor slot
	mat = mati;
	material = mat.name;
	level = newLevel;
	
	sharpResist = 1;
	bluntResist = 1;
	pierceResist = 1;
	
	baseMap = "iron";
	
	//what names make sense for the given material?
	this.matType = extra.randList(mat.typeList);
	if (matType.equals("light")){
		switch (armorType) {//adamantine can be either
			case 0: baseName = (String)extra.choose("homan hat"); weight = 2; baseResist = 1; cost = 1;break;//"hood"
			case 1: baseName = (String)extra.choose("homan gloves"); weight = 2; baseResist = 1; cost = 1;break;//,"gloves","gloves","fingerless gloves"
			case 2: baseName = (String)extra.choose("homan tunic"); weight = 10; baseResist = 4; cost = 3;break;//"shirt","toga"
			case 3: baseName = (String)extra.choose("homan pants"); weight = 6; baseResist = 3; cost = 3;break;//"leggings",
			case 4: baseName = (String)extra.choose("homan boots"); weight = 4; baseResist = 2; cost = 2;break;//,"slippers","shoes"
		}
	}else {
		if (matType.equals("heavy")) {
			baseMap = "iron";
		switch (armorType) {
			case 0: baseName = (String)extra.choose("plackan helm"); weight = 2; baseResist = 1; cost = 1;break;//"helmet",,"cap","hat","mask"
			case 1: baseName = (String)extra.choose("plackan gauntlets"); weight = 2; baseResist = 1; cost = 1;break;//"bracers",
			case 2: baseName = (String)extra.choose("plackan chestplate"); weight = 10; baseResist = 4; cost = 3;break;//,"breastplate","cuirass"
			case 3: baseName = (String)extra.choose("plackan greaves"); weight = 6; baseResist = 3; cost = 3;break;
			case 4: baseName = (String)extra.choose("plackan boots"); weight = 4; baseResist = 2; cost = 2;break;//,"shoes","high boots","low boots"
		}
		}else {
			if (matType.equals("chainmail")) {
				baseMap = "iron";
				switch (armorType) {
				case 0: baseName = (String)extra.choose("mail hood"); weight = 2; baseResist = 1; cost = 1;break;
				case 1: baseName = (String)extra.choose("mail gloves"); weight = 2; baseResist = 1; cost = 1;break;
				case 2: baseName = (String)extra.choose("mail shirt"); weight = 10; baseResist = 4; cost = 3;break;
				case 3: baseName = (String)extra.choose("mail greaves"); weight = 6; baseResist = 3; cost = 3;break;
				case 4: baseName = (String)extra.choose("mail boots"); weight = 4; baseResist = 2; cost = 2;break;
			}
				weight*=2;
				sharpResist*=1.5;
				pierceResist*=.5;
				
			}else {
				if (matType.equals("crystal")) {
					
					switch (armorType) {
					case 0: baseName = (String)extra.choose("tevaran helmet"); weight = 2; baseResist = 1; cost = 1;break;
					case 1: baseName = (String)extra.choose("tevaran bracers"); weight = 2; baseResist = 1; cost = 1;break;
					case 2: baseName = (String)extra.choose("tevaran breastplate"); weight = 10; baseResist = 4; cost = 3;break;
					case 3: baseName = (String)extra.choose("tevaran pants"); weight = 6; baseResist = 3; cost = 3;break;
					case 4: baseName = (String)extra.choose("tevaran boots"); weight = 4; baseResist = 2; cost = 2;break;
				}
					cost*=1.5;
				}
			}
		}
	}
	
		baseEnchant = mat.baseEnchant;
		baseResist *= mat.baseResist;
		sharpResist *= mat.sharpResist;
		bluntResist *= mat.bluntResist;
		pierceResist *= mat.pierceResist;
		burnMod = mat.fireVul;
		shockMod = mat.shockVul;
		freezeMod = mat.freezeVul;
		weight *= mat.weight;
		cost *= mat.cost;
		dexMod *= mat.dexMod;
		//level scaling
		//cost *= level;
		//baseResist *= level;
		
		effectiveCost = cost;
		//add an enchantment and mark it down if the enchantment is greater than a random value form 0 to <1.0
		if (baseEnchant*2 > Math.random() && extra.chanceIn(8,10)) {
		enchantment = new EnchantConstant(level*baseEnchant);
		effectiveCost=(int) extra.zeroOut(cost * enchantment.getGoldMult()+enchantment.getGoldMod());
		isEnchanted = true;
		}
		

	}

	//instance methods
	
	//getters
	/**
	 * Gives the slot of the armor
	 * @return the armortype [aka slot] (int)
	 */
	public int getArmorType() {
		return armorType;
	}
	
	/**
	 * Returns the name of the armor, with any enchantments tacked on.
	 * @return name - String
	 */
	public String getName() {
		if (isEnchanted){
		return (getModiferName() + " " +enchantment.getBeforeName() + material + " " + baseName + enchantment.getAfterName());}
			return (getModiferName() + " " +material + " " +  baseName);
	}
	

	/**
	 * @return the sharpResist (double)
	 */
	public double getSharpResist() {
		return sharpResist;
	}

	/**
	 * @return the bluntResist (double)
	 */
	public double getBluntResist() {
		return bluntResist;
	}

	/**
	 * @return the pierceResist (double)
	 */
	public double getPierceResist() {
		return pierceResist;
	}
	
	public void resetArmor(int s, int b, int p) {
		sharpActive = sharpResist*baseResist*level+s;
		pierceActive = pierceResist*baseResist*level+b;
		bluntActive = bluntResist*baseResist*level+p;
		burned = 1;
	}
	
	public double getPierce() {
		return extra.zeroOut((pierceActive)*burned);
	}
	
	public double getBlunt() {
		return extra.zeroOut((bluntActive)*burned);
	}
	
	public double getSharp() {
		return extra.zeroOut((sharpActive)*burned);
	}
	
	public double getFireMod() {
		return burnMod;
	}
	
	public double getShockMod() {
		return shockMod;
	}
	
	public double getFreezeMod() {
		return freezeMod;
	}
	
	public void burn(double d) {
		burned-=d*burnMod;
	}
	
	
	
	
	/**
	 * Get the weight of the item
	 * @return weight (int)
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * get the cost of the item
	 * @return cost (int)
	 */
	public int getCost() {
		return effectiveCost*level;
	}
	
	/**
	 * get the base cost of the item
	 * @return base cost (int)
	 */
	public int getBaseCost() {
		return cost*level;
	}
	
	/**
	 * Returns if the armor is enchanted or not.
	 * @return if enchanted (boolean)
	 */
	public boolean isEnchanted() {
		return isEnchanted;
	}
	
	/**
	 * Swap out the current enchantment for a new one, if a better one is generated.
	 * Returns if a better one was generated or not.
	 * @param level (int)
	 * @return changed enchantment? (boolean)
	 */
	public boolean improveEnchantChance(int level) {
		if (isEnchanted) {
			EnchantConstant pastEnchant = enchantment;
			enchantment = Services.improveEnchantChance(enchantment, level, baseEnchant);
			effectiveCost=(int) extra.zeroOut(cost * enchantment.getGoldMult()+enchantment.getGoldMod());
			return pastEnchant != enchantment;
		}else {
			isEnchanted = true;
			enchantment = new EnchantConstant(level*baseEnchant);
			effectiveCost=(int) extra.zeroOut(cost * enchantment.getGoldMult()+enchantment.getGoldMod());
			return true;
		}
	}

	/**
	 * @return the dexMod (double)
	 */
	public double getDexMod() {
		return dexMod;
	}
	
	/**
	 * @return the baseResist (double)
	 */
	public double getResist() {
		return baseResist;
	}
	
	/**
	 * @return the enchantment (EnchantConstant)
	 */
	public EnchantConstant getEnchant() {
		return enchantment;
	}
	
	/**
	 * @return the baseName (String)
	 */
	public String getBaseName() {
		return baseName;
	}

	@Override
	public void display(int style) {
		switch (style) {
		case 1:
			extra.println(this.getName() + " sbp:" + extra.format(this.baseResist*this.getSharpResist()*level) + " " + extra.format(this.baseResist*this.getBluntResist()*level) + " " + extra.format(this.baseResist*this.getPierceResist()*level)
			 + " value: " + this.getCost());
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			;break;
			
		case 2:
			extra.println(this.getName() + " sbp:" + extra.format(this.baseResist*this.getSharpResist()*level) + " " + extra.format(this.baseResist*this.getBluntResist()*level) + " " + extra.format(this.baseResist*this.getPierceResist()*level)
			+ " dex: "+ this.getDexMod() + " flame: "+ this.getFireMod() + " shock: "+ this.getShockMod() + " frost: "+ this.getFreezeMod() + " value: " + this.getCost());
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			;break;
		}
		
	}
	
	@Override
	public String getType() {
		return "armor" + armorType;
	}

	public String getMaterial() {
		return material;
	}

	public String getBaseMap() {
		return baseMap;
	}

	public Material getMat() {
		return mat;
	}

	public void restoreArmor(double d) {
		burned+=d;
		burned = extra.clamp(burned,0,1);
	}
	
	public void levelUp() {
		level++;
	}
	
}
