package trawel.personal.item.solid;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nustaq.serialization.annotations.OneOf;

import trawel.Services;
import trawel.extra;
import trawel.personal.item.Item;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;

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
	private byte armorType;//The slot which the armor goes into
	private String baseName;//what we call it, ie helm, helmet, hat, cap
	@OneOf({"cloth","iron","tin","copper","bronze","steel","silver","gold"})
	private String material;//what material the object is made from
	private int cost;//how much it costs in gold pieces
	//private int baseResist;//the base damage resistance of the item//now built into the following
	private float sharpResist;
	private float bluntResist;
	private float pierceResist;
	private transient double sharpActive, bluntActive, pierceActive;
	private float baseEnchant;//the multiplier of how powerful enchantments on the item are
	private byte weight;
	private Enchant enchantment;
	private float dexMod = 1;
	//private double burnMod, freezeMod, shockMod;
	private transient double burned;
	@OneOf({"heavy","light","chainmail","crystal","drudger"})
	private String matType;//ie heavy, light, chainmail
	//@OneOf({"iron","cloth","crystal"})
	//private String baseMap;
	private byte baseMap;//switched to offset, unsigned, but positives are normal
	private static final String[] BASE_MAPS = new String[] {"iron","cloth","crystal"};
	
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
	 * Make a new random armor.
	 * @param newLevel (int)
	 */
	public Armor(int newLevel) {
		this(newLevel,extra.getRand().nextInt(5),MaterialFactory.randArmorMat(),null);
	}
	
	public Armor(int newLevel, int slot,Material mati) {
		this(newLevel,slot,mati,null);
	}
	/**
	 * This should be used in most cases if you want armor for one slot.
	 * TODO: replace with a similar thing to genMidWeapon maybe?
	 * @param level
	 * @param slot (head, arms, body, legs, feet)
	 */
	public Armor(int level, int slot) {
		this(level,slot,MaterialFactory.randArmorMat(),null);
	}
	
	/**
	 * Make a new armor that is of newLevel level, will go in slot slot, and has a certain material.
	 * This function should mostly be only used interally, but if you want a certain material and type to be generated, it will work.
	 * @param newLevel (int)
	 * @param slot (int)
	 * @param mat (Material)
	 * @param amatType (String)
	 */
	public Armor(int newLevel, int slot,Material mati,String amatType) {
	//initialize	
	armorType = (byte)slot;//type is equal to the array armor slot
	//mat = mati;
	material = mati.name;
	level = newLevel;
	prefixName = "";
	
	sharpResist = 1;
	bluntResist = 1;
	pierceResist = 1;
	
	//burnMod = 1;
	//shockMod = 1;
	//freezeMod = 1;
	
	baseMap = 0;//"iron";
	
	//what names make sense for the given material?
	if (amatType == null) {
		this.matType = extra.randList(mati.typeList);
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
	
	float baseResist = 1;
	
	if (matType.equals("light")){
		baseMap = 1;//"cloth";
		switch (armorType) {//adamantine can be either
			case 0: baseName = "hat"; weight = 2; baseResist = 1; cost = 1;break;//"hood"
			case 1: baseName = "gloves"; weight = 2; baseResist = 1; cost = 1;break;//,"gloves","gloves","fingerless gloves"
			case 2: baseName = "tunic"; weight = 10; baseResist = 4; cost = 3;break;//"shirt","toga"
			case 3: baseName = "pants"; weight = 6; baseResist = 3; cost = 3;break;//"leggings",
			case 4: baseName = "boots"; weight = 4; baseResist = 2; cost = 2;break;//,"slippers","shoes"
		}
	}else {
		if (matType.equals("heavy")) {
			baseMap = 0;//"iron";
		switch (armorType) {
			case 0: baseName = "helm"; weight = 2; baseResist = 1; cost = 1;break;//"helmet",,"cap","hat","mask"
			case 1: baseName = "gauntlets"; weight = 2; baseResist = 1; cost = 1;break;//"bracers",
			case 2: baseName = "chestplate"; weight = 10; baseResist = 4; cost = 3;break;//,"breastplate","cuirass"
			case 3: baseName = "greaves"; weight = 6; baseResist = 3; cost = 3;break;
			case 4: baseName = "boots"; weight = 4; baseResist = 2; cost = 2;break;//,"shoes","high boots","low boots"
		}
		}else {
			if (matType.equals("chainmail")) {
				baseMap = 0;//"iron";
				switch (armorType) {
				case 0: baseName = "mail hood"; weight = 2; baseResist = 1; cost = 1;break;
				case 1: baseName = "mail gloves"; weight = 2; baseResist = 1; cost = 1;break;
				case 2: baseName = "mail shirt"; weight = 10; baseResist = 4; cost = 3;break;
				case 3: baseName = "mail pants"; weight = 6; baseResist = 3; cost = 3;break;
				case 4: baseName = "mail boots"; weight = 4; baseResist = 2; cost = 2;break;
				}
				weight*=2;
				sharpResist*=1.5;
				pierceResist*=.5;
				
			}else {
				if (matType.equals("crystal")) {
					baseMap = 2;//"crystal";
					switch (armorType) {
					case 0: baseName = "helmet"; weight = 2; baseResist = 1; cost = 1;break;
					case 1: baseName = "bracers"; weight = 2; baseResist = 1; cost = 1;break;
					case 2: baseName = "breastplate"; weight = 10; baseResist = 4; cost = 3;break;
					case 3: baseName = "pants"; weight = 6; baseResist = 3; cost = 3;break;
					case 4: baseName = "boots"; weight = 4; baseResist = 2; cost = 2;break;
				}
					cost*=1.5;
					if (extra.chanceIn(2,3)) {
						quals.add(ArmorQuality.FRAGILE);
						prefixName = extra.PRE_YELLOW+"fragile[c_white] ";
						sharpResist*=1.25;
						bluntResist*=1.25;
						pierceResist*=1.25;
					}
				}else {
					if (matType.equals("is")) {//flesh and bone mostly
						baseMap = 0;//"iron";
						switch (armorType) {
						case 0: baseName = "head"; weight = 2; baseResist = 1; cost = 1;break;
						case 1: baseName = "arms"; weight = 2; baseResist = 1; cost = 1;break;
						case 2: baseName = "body"; weight = 10; baseResist = 4; cost = 3;break;
						case 3: baseName = "legs"; weight = 6; baseResist = 3; cost = 3;break;
						case 4: baseName = "feet"; weight = 4; baseResist = 2; cost = 2;break;
						}
						weight*=2;
						sharpResist*=1.5;
						pierceResist*=.5;
						
					}else {
					if (matType.equals("drudger")) {
						baseMap = 0;//"iron";
					switch (armorType) {
						case 0: baseName = "mask"; weight = 2; baseResist = 1; cost = 1;break;//"helmet",,"cap","hat","mask"
						case 1: baseName = "bracers"; weight = 2; baseResist = 1; cost = 1;break;//"bracers",
						case 2: baseName = "chestplate"; weight = 10; baseResist = 4; cost = 3;break;//,"breastplate","cuirass"
						case 3: baseName = "greaves"; weight = 6; baseResist = 3; cost = 3;break;
						case 4: baseName = "boots"; weight = 4; baseResist = 2; cost = 2;break;//,"shoes","high boots","low boots"
					}
					}
					}
				}
			}
		}
		
	}
	
		baseEnchant = mati.baseEnchant;
		baseResist *= mati.baseResist;
		sharpResist *= mati.sharpResist;
		bluntResist *= mati.bluntResist;
		pierceResist *= mati.pierceResist;
		//burnMod *= mati.fireVul;
		//shockMod *= mati.shockVul;
		//freezeMod *= mati.freezeVul;
		weight *= mati.weight;
		cost *= mati.cost;
		dexMod *= mati.dexMod;
		
		
		sharpResist *= baseResist;
		bluntResist *= baseResist;
		pierceResist *= baseResist;
		
		//effectiveCost = cost;
		//add an enchantment and mark it down if the enchantment is greater than a random value form 0 to <1.0
		if (baseEnchant > extra.randFloat()*3f) {
			enchantment = EnchantConstant.makeEnchant(baseEnchant,cost);//used to include level in the mult, need a new rarity system
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
	 * @return the armortype [aka slot] (byte)
	 */
	public byte getArmorType() {
		return armorType;
	}
	
	/**
	 * Returns the name of the armor, with any enchantments tacked on.
	 * @return name - String
	 */
	public String getName() {
		if (enchantment != null){
			EnchantConstant enchant = (EnchantConstant)enchantment; 
		return (getModiferName() + " " +enchant.getBeforeName() +prefixName+this.getMat().color + material+"[c_white]" + " " + baseName + enchant.getAfterName());}
			return (getModiferName() + " "+prefixName +MaterialFactory.getMat(material).color + material+"[c_white]"  + " " +  baseName);
	}
	

	/**
	 * gets the 'normal' armor level OOB
	 * @return the sharpResist (float)
	 */
	public float getSharpResist() {
		return sharpResist*level;
	}

	/**
	 * gets the 'normal' armor level OOB
	 * @return the bluntResist (float)
	 */
	public float getBluntResist() {
		return bluntResist*level;
	}

	/**
	 * gets the 'normal' armor level OOB
	 * @return the pierceResist (float)
	 */
	public float getPierceResist() {
		return pierceResist*level;
	}
	
	public void resetArmor(int s, int b, int p) {
		sharpActive = sharpResist*level+s;
		pierceActive = pierceResist*level+b;
		bluntActive = bluntResist*level+p;
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
		return this.getMat().fireVul;
	}
	
	public double getShockMod() {
		return this.getMat().shockVul;
	}
	
	public double getFreezeMod() {
		return this.getMat().freezeVul;
	}
	
	/**
	 * reduces armor by the provided double, scaled against fireMod and with diminishing returns as burned approaches 0
	 * bigger burns at once will ignore part of the diminishing returns
	 * @param d
	 */
	public void burn(double d) {
		assert d >= 0;
		burned = Math.min(1, Math.max(0, burned - Math.min(burned,1)*(1- (d*getFireMod())))) ;
	}
	
	
	
	
	/**
	 * Get the weight of the item
	 * @return weight (byte)
	 */
	public byte getWeight() {
		return weight;
	}
	
	/**
	 * get the cost of the item
	 * @return cost (int)
	 */
	public int getCost() {
		return (int) (cost*level*(isEnchanted() ? enchantment.getGoldMult()+enchantment.getGoldMod() : 1));
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
		return enchantment != null;
	}
	
	/**
	 * Swap out the current enchantment for a new one, if a better one is generated.
	 * Returns if a better one was generated or not.
	 * @param level (int)
	 * @return changed enchantment? (boolean)
	 */
	public boolean improveEnchantChance(int level) {
		if (isEnchanted()) {
			Enchant pastEnchant = enchantment;
			enchantment = Services.improveEnchantChance(enchantment, level, baseEnchant);
			//effectiveCost=(int) extra.zeroOut(cost * enchantment.getGoldMult()+enchantment.getGoldMod());
			return pastEnchant != enchantment;
		}else {
			
			enchantment = new EnchantConstant(level*baseEnchant);
			if (cost * enchantment.getGoldMult()+enchantment.getGoldMod() > 0) {
				//isEnchanted = true;
				//effectiveCost=(int) extra.zeroOut(cost * enchantment.getGoldMult()+enchantment.getGoldMod());
				return true;
			}else {
				enchantment = null;
				return false;
			}
		}
	}

	/**
	 * @return the dexMod (float)
	 */
	public float getDexMod() {
		return dexMod;
	}
	
	/**
	 * @return the enchantment (EnchantConstant)
	 */
	public Enchant getEnchant() {
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
			extra.println(this.getName() + " sbp:" + extra.format(this.getSharpResist()) + " " + extra.format(this.getBluntResist()) + " " + extra.format(this.getPierceResist())
			 + " value: " + (int)(this.getCost()*markup));
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			;break;
			
		case 2:
			extra.println(this.getName() + " sbp:" + extra.format(this.getSharpResist()) + " " + extra.format(this.getBluntResist()) + " " + extra.format(this.getPierceResist())
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
		return BASE_MAPS[Byte.toUnsignedInt(baseMap)];
	}

	public Material getMat() {
		return MaterialFactory.getMat(material);
	}

	public void restoreArmor(double d) {
		burned+=d;
		burned = extra.clamp(burned,0,1);
	}
	
	public void levelUp() {
		level++;
	}

	public String getSoundType() {
		return getMat().soundType;
	}
	public String getMatType() {
		return this.matType;
	}

	public void deEnchant() {
		enchantment = null;
		//isEnchanted = false;
	}

	public List<ArmorQuality> getQuals() {
		return quals;
	}
	
	public void armorQualDam(int dam) {
		if (quals.contains(ArmorQuality.FRAGILE)) {
			burned = extra.clamp(burned-extra.lerp(0.05f,.5f, extra.clamp(dam,1,20)/20f),0,2);
		}
	}

	@Override
	public boolean coinLoot() {
		switch (material) {
		case "flesh": return false;
		case "bone": return false;
			default: return true;
		}
	}
	
}
