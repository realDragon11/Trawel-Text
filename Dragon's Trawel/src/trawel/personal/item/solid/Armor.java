package trawel.personal.item.solid;

import java.util.ArrayList;
import java.util.List;

import org.nustaq.serialization.annotations.OneOf;

import trawel.Services;
import trawel.extra;
import trawel.personal.item.Item;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.solid.variants.ArmorStyle;

/***
 * An extension of Item, an armor has varying stats that can effect a person, and possibly and enchantment.
 * Different materials and different slots effect the attributes of items, as well as the level of the item.
 * 
 * @author dragon
 * 2/5/2018
 *
 */
public class Armor extends Item {

	private static final long serialVersionUID = 1L;
	
	public static final double armorEffectiveness = .1;//was .05
	
	
	//instance variables
	private byte slot;//The slot which the armor goes into
	private int material;//what material the object is made from
	private short style;
	private int fluff;//stores the fluff of the style
	private Enchant enchantment;
	
	private transient double burned;
	private transient float sharpActive, bluntActive, pierceActive;
	
	private List<ArmorQuality> quals = new ArrayList<ArmorQuality>();
	
	
	//removable after armor style rework
	private byte baseMap;//switched to offset, unsigned, but positives are normal
	private static final String[] BASE_MAPS = new String[] {"iron","cloth","crystal"};
	@OneOf({"heavy","light","chainmail","crystal","drudger"})
	private String matType;//ie heavy, light, chainmail
	private float dexMod = 1;
	
	//end removable after
	
	//enums
	
	public enum ArmorQuality {
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
		this(newLevel,(byte) extra.getRand().nextInt(5),MaterialFactory.randArmorMat(),null);
	}
	
	public Armor(int newLevel, int slot,Material mati) {
		this(newLevel,(byte) slot,mati,null);
	}
	/**
	 * This should be used in most cases if you want armor for one slot.
	 * TODO: replace with a similar thing to genMidWeapon maybe?
	 * @param level
	 * @param slot (head, arms, body, legs, feet)
	 */
	public Armor(int level, int slot) {
		this(level,(byte) slot,MaterialFactory.randArmorMat(),null);
	}
	
	/**
	 * Make a new armor that is of newLevel level, will go in slot slot, and has a certain material.
	 * This function should mostly be only used interally, but if you want a certain material and type to be generated, it will work.
	 * @param newLevel (int)
	 * @param slot (int)
	 * @param mati (Material)
	 * @param amatType (String)
	 */
	public Armor(int newLevel, byte slot,Material mati,ArmorStyle styleType) {	
		this.slot = slot;//type is equal to the array armor slot
		material = mati.curNum;
		level = newLevel;
		
		baseMap = 0;//"iron";
		
		if (styleType == null) {
			style = (short) extra.randList(mati.typeList).ordinal();
		}else {
			style = (short) styleType.ordinal();
		}

		fluff = ArmorStyle.fetch(style).genner[slot].generate();

		/*
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
							//prefixName = extra.PRE_YELLOW+"fragile[c_white] ";//DOLATER
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

		}*/

		dexMod *= mati.dexMod;

		float baseEnchant = getEnchantMult();
		if (baseEnchant > extra.randFloat()*3f) {
			enchantment = EnchantConstant.makeEnchant(baseEnchant,getAetherValue());//used to include level in the mult, need a new rarity system
		}

	}

	//instance methods
	
	//getters
	/**
	 * Gives the slot of the armor
	 * @return the armortype [aka slot] (byte)
	 */
	public byte getArmorType() {
		return getSlot();
	}
	
	public ArmorStyle getStyle() {
		return ArmorStyle.fetch(style);
	}
	
	/**
	 * Returns the name of the armor, with any enchantments tacked on.
	 * @return name - String
	 */
	@Override
	public String getName() {
		Material mat = getMat();
		if (enchantment != null){
			EnchantConstant enchant = (EnchantConstant)enchantment; 
		return (getModiferName() + " " +enchant.getBeforeName() +mat.color + mat.name+"[c_white]" + " " + getBaseName() + enchant.getAfterName());}
			return (getModiferName() + " " +mat.color + mat.name+"[c_white]"  + " " +  getBaseName());
	}
	

	/**
	 * gets the 'normal' armor level OOB
	 * @return the sharpResist (float)
	 */
	public float getSharpResist() {
		return MaterialFactory.getMat(material).sharpResist*baseResist();
	}

	/**
	 * gets the 'normal' armor level OOB
	 * @return the bluntResist (float)
	 */
	public float getBluntResist() {
		return MaterialFactory.getMat(material).bluntResist*baseResist();
	}

	/**
	 * gets the 'normal' armor level OOB
	 * @return the pierceResist (float)
	 */
	public float getPierceResist() {
		return MaterialFactory.getMat(material).pierceResist*baseResist();
	}
	
	public void resetArmor(int s, int b, int p) {
		sharpActive = getSharpResist()+s;
		bluntActive = getBluntResist()+b;
		pierceActive = getPierceResist()+p;
		burned = 1;
	}
	
	
	//DOLATER: for now, all are the same
	//either fix, or make certain armor bits count more for global armor
	private float baseResist() {
		return 1*level*ArmorStyle.fetch(style).totalMult*MaterialFactory.getMat(material).baseResist;
		/*
		switch (slot) {
		case 0://helm
			return 1*level*ArmorStyle.fetch(style).totalMult;
		case 1://gloves
			return 1*level*ArmorStyle.fetch(style).totalMult;
		case 2://chest
			return 1*level*ArmorStyle.fetch(style).totalMult;
		case 3://greaves
			return 1*level*ArmorStyle.fetch(style).totalMult;
		case 4://boots
			return 1*level*ArmorStyle.fetch(style).totalMult;
		}
		throw new RuntimeException("invalid armor slot for base resist");
		*/
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
	 * @return weight (int)
	 */
	public int getWeight() {
		return (int) (slotImpact()*MaterialFactory.getMat(material).weight*ArmorStyle.fetch(style).weightMult);
	}
	
	private int slotImpact() {
		if (getSlot() > 1) {
			if (getSlot() == 2) {
				return 10;//chest
			}else {
				if (getSlot() == 3) {
					return 7;//pants
				}else {
					return 4;//boots
				}
			}
		}
		return 3;//head and gloves
	}
	
	/**
	 * get the cost of the item
	 * @return cost (int)
	 */
	@Override
	public int getAetherValue() {
		return (int) (getBaseCost()*(isEnchanted() ? enchantment.getGoldMult() : 1)+(isEnchanted() ? enchantment.getGoldMod() : 0));
	}
	
	/**
	 * get the base cost of the item
	 * @return base cost (int)
	 */
	public int getBaseCost() {
		return (int) (MaterialFactory.getMat(material).cost*ArmorStyle.fetch(style).costMult*slotImpact()*level);
	}
	
	public float getEnchantMult() {
		return ArmorStyle.fetch(style).enchantMult*MaterialFactory.getMat(material).baseEnchant;
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
			enchantment = Services.improveEnchantChance(enchantment, level, getEnchantMult());
			//effectiveCost=(int) extra.zeroOut(cost * enchantment.getGoldMult()+enchantment.getGoldMod());
			return pastEnchant != enchantment;
		}else {
			enchantment = EnchantConstant.makeEnchant(getEnchantMult(), getAetherValue());
			return enchantment != null;
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
	@Override
	public Enchant getEnchant() {
		return enchantment;
	}
	
	/**
	 * @return the baseName (String)
	 */
	public String getBaseName() {
		return ArmorStyle.fetch(style).genner[getSlot()].decode(fluff)[0];
	}

	@Override
	public void display(int style,float markup) {
		switch (style) {
		case 1:
			extra.println(this.getName() + " sbp:" + extra.format(this.getSharpResist()) + " " + extra.format(this.getBluntResist()) + " " + extra.format(this.getPierceResist())
			 + " aether: " + (int)(this.getAetherValue()*markup));
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			;break;	
		case 2:
			extra.println(this.getName() + " sbp:" + extra.format(this.getSharpResist()) + " " + extra.format(this.getBluntResist()) + " " + extra.format(this.getPierceResist())
			+ " dex: "+ this.getDexMod() + " flame: "+ this.getFireMod() + " shock: "+ this.getShockMod() + " frost: "+ this.getFreezeMod() + " aether: " + (int)(this.getAetherValue()*markup));
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			for (ArmorQuality aq: quals) {
				extra.println(aq.name + ": " + aq.desc);
			}
			;break;
		case 3://for stores
			extra.println(this.getName() + " sbp:" + extra.format(this.getSharpResist()) + " " + extra.format(this.getBluntResist()) + " " + extra.format(this.getPierceResist())
			 + " value: " + extra.F_WHOLE.format(Math.ceil(this.getMoneyValue()*markup)));
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			;break;
		}
		
	}
	@Override
	public void display(int style) {
		this.display(style, 1);
	}
	
	@Override
	public String storeString(float markup, boolean canShow) {
		if (canShow) {
			return this.getName() 
				+ " S " + extra.F_WHOLE.format(this.getSharpResist())
				+ " B " + extra.F_WHOLE.format(this.getBluntResist())
				+ " P " + extra.F_WHOLE.format(this.getPierceResist())
				+ " cost: " +  extra.F_WHOLE.format(Math.ceil(getMoneyValue()*markup))
				;
		}
		String base = getBaseName();
		return "  They refuse to show you something you think " + extra.pluralIsA(base) + " "+base+".";
	}
	
	@Override
	public ItemType getType() {
		return Item.ItemType.ARMOR;
	}

	public String getMaterialName() {
		return MaterialFactory.getMat(material).name;
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
	
	@Override
	public void levelUp() {
		level++;
	}

	public String getSoundType() {
		if (getStyle().equals(ArmorStyle.MAIL)) {
			return "mail";
		}else {
			return getMat().soundType;
		}
		
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
	public boolean canAetherLoot() {
		if (ArmorStyle.fetch(style).equals(ArmorStyle.BODY)) {
			return false;
		}
		return true;
	}

	public byte getSlot() {
		return slot;
	}
	
}
