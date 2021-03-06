package trawel;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nustaq.serialization.annotations.OneOf;

/***
 * An extension of Item, an armor has varying stats that can effect a person, and possibly and enchantment.
 * Different materials and different slots effect the attributes of items, as well as the level of the item.
 * 
 * @author Brian Malone
 * 2/5/2018
 *
 */
public class Armor extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final double armorEffectiveness = .1;//was .05
	//instance variables
	private int armorType;//The slot which the armor goes into
	private String baseName;//what we call it, ie helm, helmet, hat, cap
	@OneOf({"cloth","iron","tin","copper","bronze","steel","silver","gold"})
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
	@OneOf({"iron","cloth","crystal"})
	private String baseMap;
	private String prefixName;
	private List<ArmorQuality> quals = new ArrayList<ArmorQuality>();
	
	//enums
	
	public enum ArmorQuality implements Serializable {
		FRAGILE("Fragile","Loses power when hit."),
		HAUNTED("Haunted","Chance to turn any hit on you into a miss.");
		
		public String name, desc;
		ArmorQuality(String nam, String des){
			name = nam;
			desc = des;
			
		}
	}
	
	//constructors
	
	/**
	 * Make a new armor.
	 * @param newLevel (int)
	 */
	public Armor(int newLevel) {
		this(newLevel,(int)(Math.random()*5),MaterialFactory.randMat(true,false),null);
	}
	
	/**
	 * Make a new armor, that will go in a certain slot.
	 * @param newLevel (int)
	 * @param slot (int)
	 */
	/*public Armor(int newLevel,int slot,String matType) {
		this(newLevel,slot,MaterialFactory.randMatByType(matType),job.amatType);		
	}*/
	
	public Armor(int newLevel, int slot,Material mati) {
		this(newLevel,slot,mati,null);
	}
	public Armor(int newLevel, int slot) {
		this(newLevel,slot,MaterialFactory.randMat(true,false),null);
	}
	
	/**
	 * Make a new armor that is of newLevel level, will go in slot slot, and has a certain material.
	 * This function should mostly be only used interally, but if you want a certain material to be generated, it will work.
	 * @param newLevel (int)
	 * @param slot (int)
	 * @param mat (String)
	 */
	public Armor(int newLevel, int slot,Material mati,String amatType) {
	//initialize	
	armorType = slot;//type is equal to the array armor slot
	mat = mati;
	material = mat.name;
	level = newLevel;
	prefixName = "";
	
	sharpResist = 1;
	bluntResist = 1;
	pierceResist = 1;
	
	baseMap = "iron";
	
	//what names make sense for the given material?
	if (amatType == null) {
		this.matType = extra.randList(mat.typeList);
	}else {
		this.matType = amatType;
		/*
	    HashSet<String> set = new HashSet<>();
	    set.addAll(Arrays.asList(amatType));
	    set.retainAll(mat.typeList);
	    String[] strs = (String[])set.toArray();
	    this.matType = strs[extra.randRange(0,strs.length-1)];
		*/
	}
	
	if (matType.equals("light")){
		baseMap = "cloth";
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
				case 3: baseName = (String)extra.choose("mail pants"); weight = 6; baseResist = 3; cost = 3;break;
				case 4: baseName = (String)extra.choose("mail boots"); weight = 4; baseResist = 2; cost = 2;break;
				}
				weight*=2;
				sharpResist*=1.5;
				pierceResist*=.5;
				
			}else {
				if (matType.equals("crystal")) {
					baseMap = "crystal";
					switch (armorType) {
					case 0: baseName = (String)extra.choose("tevaran helmet"); weight = 2; baseResist = 1; cost = 1;break;
					case 1: baseName = (String)extra.choose("tevaran bracers"); weight = 2; baseResist = 1; cost = 1;break;
					case 2: baseName = (String)extra.choose("tevaran breastplate"); weight = 10; baseResist = 4; cost = 3;break;
					case 3: baseName = (String)extra.choose("tevaran pants"); weight = 6; baseResist = 3; cost = 3;break;
					case 4: baseName = (String)extra.choose("tevaran boots"); weight = 4; baseResist = 2; cost = 2;break;
				}
					cost*=1.5;
					if (extra.chanceIn(2,3)) {
						quals.add(ArmorQuality.FRAGILE);
						prefixName = extra.inlineColor(Color.YELLOW)+"fragile[c_white] ";
						sharpResist*=1.25;
						bluntResist*=1.25;
						pierceResist*=1.25;
					}
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
		}else {
			if (extra.chanceIn(1,5)) {//non-enchanted qualities
				baseEnchant = 0;
				ArmorQuality aaq = extra.choose(ArmorQuality.HAUNTED);
				this.quals.add(aaq);
				switch (aaq) {
				case HAUNTED:
					cost*=1.2;
					break;
				}
			}
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
		return (getModiferName() + " " +enchantment.getBeforeName() +prefixName+MaterialFactory.getMat(material).color + material+"[c_white]" + " " + baseName + enchantment.getAfterName());}
			return (getModiferName() + " "+prefixName +MaterialFactory.getMat(material).color + material+"[c_white]"  + " " +  baseName);
	}
	

	/**
	 * @return the sharpResist (double)
	 */
	public double getSharpResist() {
		return sharpResist*level;
	}

	/**
	 * @return the bluntResist (double)
	 */
	public double getBluntResist() {
		return bluntResist*level;
	}

	/**
	 * @return the pierceResist (double)
	 */
	public double getPierceResist() {
		return pierceResist*level;
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
			
			enchantment = new EnchantConstant(level*baseEnchant);
			if (cost * enchantment.getGoldMult()+enchantment.getGoldMod() > 0) {
				isEnchanted = true;
				effectiveCost=(int) extra.zeroOut(cost * enchantment.getGoldMult()+enchantment.getGoldMod());
				return true;
			}else {
				return false;
			}
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
	public void display(int style,float markup) {
		switch (style) {
		case 1:
			extra.println(this.getName() + " sbp:" + extra.format(this.baseResist*this.getSharpResist()) + " " + extra.format(this.baseResist*this.getBluntResist()) + " " + extra.format(this.baseResist*this.getPierceResist())
			 + " value: " + (int)(this.getCost()*markup));
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			;break;
			
		case 2:
			extra.println(this.getName() + " sbp:" + extra.format(this.baseResist*this.getSharpResist()) + " " + extra.format(this.baseResist*this.getBluntResist()) + " " + extra.format(this.baseResist*this.getPierceResist())
			+ " dex: "+ this.getDexMod() + " flame: "+ this.getFireMod() + " shock: "+ this.getShockMod() + " frost: "+ this.getFreezeMod() + " value: " + (int)(this.getCost()*markup));
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			for (ArmorQuality aq: quals) {
				extra.println(aq.name + ": " + aq.desc);
			}
			;break;
		}
		
	}
	@Override
	public void display(int style) {
		this.display(style, 1);
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

	public String getSoundType() {
		return mat.soundType;
	}
	public String getMatType() {
		return this.matType;
	}

	public void deEnchant() {
		enchantment = null;
		isEnchanted = false;
	}

	public List<ArmorQuality> getQuals() {
		return quals;
	}
	
	public void armorQualDam(int dam) {
		if (quals.contains(ArmorQuality.FRAGILE)) {
			burned = extra.clamp(burned-extra.lerp(0.05f,.5f, extra.clamp(dam,1,20)/20f),0,2);
		}
	}
	
}
