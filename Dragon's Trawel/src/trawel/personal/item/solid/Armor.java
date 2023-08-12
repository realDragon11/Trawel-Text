package trawel.personal.item.solid;

import java.util.ArrayList;
import java.util.List;

import org.nustaq.serialization.annotations.OneOf;

import trawel.Services;
import trawel.extra;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Inventory;
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
public class Armor extends Item implements IEffectiveLevel{

	private static final long serialVersionUID = 1L;
	
	public static final double armorEffectiveness = 1;//5 armor slots
	
	
	//instance variables
	private byte slot;//The slot which the armor goes into
	private int material;//what material the object is made from
	private short style;
	private int fluff;//stores the fluff of the style
	private Enchant enchantment;
	
	private transient double condition;
	private transient float sharpActive, bluntActive, pierceActive;
	
	private List<ArmorQuality> quals = new ArrayList<ArmorQuality>();
	
	
	//removable after armor style rework
	private byte baseMap;//switched to offset, unsigned, but positives are normal
	private static final String[] BASE_MAPS = new String[] {"iron","cloth","crystal"};
	@OneOf({"heavy","light","chainmail","crystal","drudger"})
	private String matType;//ie heavy, light, chainmail
	
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
	public String getNameNoTier() {
		Material mat = getMat();
		if (enchantment != null){
			EnchantConstant enchant = (EnchantConstant)enchantment; 
		return (enchant.getBeforeName() +mat.color + mat.name+"[c_white]" + " " + getBaseName() + enchant.getAfterName());}
			return (mat.color + mat.name+"[c_white]"  + " " +  getBaseName());
	}
	
	@Override
	public String getName() {
		return getModiferName() + " " + getNameNoTier();
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
		condition = 1;
	}
	
	
	//MAYBELATER: for now, all are the same
	//either fix, or make certain armor bits count more for global armor
	private float baseResist() {
		return IEffectiveLevel.unEffective(
				1.5f*getEffectiveLevel()*ArmorStyle.fetch(style).totalMult
				*MaterialFactory.getMat(material).baseResist
				);
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
		return extra.zeroOut((pierceActive)*condition);
	}
	
	public double getBlunt() {
		return extra.zeroOut((bluntActive)*condition);
	}
	
	public double getSharp() {
		return extra.zeroOut((sharpActive)*condition);
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
	 * damage, but times by firemod first
	 */
	public void burn(double d) {
		damage(d*getFireMod());
	}
	
	/**
	 * as with burn but without applying fire mod
	 * <br>
	 * pass the % decrease in armor.
	 * <br>
	 * the decrease is diminishing, but not continuously dimminishing, so large damages at once will have more effect 
	 * <br>
	 * will attempt to not let condition reach 0 entirely, minimum of 5% overall and
	 * max 70% reduction in one hit
	 */
	public void damage(double d) {
		assert d >= 0;
		condition = Math.max(.05, condition-(condition*extra.clamp(d,0,.7)));
	}
	
	/**
	 * multiplier on effectiveness
	 */
	public void buff(double d) {
		condition *=d;
	}
	
	/**
	 * half multiplier above 100%
	 */
	public void buffDecay() {
		if (condition > 1) {
			condition = extra.lerp(1,condition,.5);
		}
	}
	
	
	/**
	 * Get the weight of the item
	 * @return weight (int)
	 */
	public int getWeight() {
		return (int) (Inventory.TEMP_WEIGHT_MULT*slotImpact()*MaterialFactory.getMat(material).weight*ArmorStyle.fetch(style).weightMult);
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
		if (getEnchantMult() == 0) {
			return false;
		}
		if (isEnchanted()) {
			Enchant pastEnchant = enchantment;
			enchantment = Services.improveEnchantChance(enchantment, level, getEnchantMult());
			//effectiveCost=(int) extra.zeroOut(cost * enchantment.getGoldMult()+enchantment.getGoldMod());
			updateStats();
			return pastEnchant != enchantment;
		}else {
			enchantment = EnchantConstant.makeEnchant(getEnchantMult(), getAetherValue());
			updateStats();
			return enchantment != null;
		}
	}

	/**
	 * @return the dexMod (float)
	 */
	public float getAgiPenMult() {
		//FIXME: I don't know what the ultimate impact of this agipen formula is,
		//but the goal is that if the style has no penalty, there is less impact from the material,
		//but if it has a penalty, the material matters more
		return extra.lerp(getMat().dexMod*getStyle().dexMultBase,1f,getStyle().dexMultBase);
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
			+ " agi: "+ getAgiPenMult() + " flame: "+ this.getFireMod() + " shock: "+ this.getShockMod() + " frost: "+ this.getFreezeMod() + " aether: " + (int)(this.getAetherValue()*markup));
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

	@Override
	public Material getMat() {
		return MaterialFactory.getMat(material);
	}

	public void restoreArmor(double d) {
		if (condition > 1) {
			return;//if above 100% it can stay that way
		}
		condition+=d;
		condition = extra.clamp(condition,0,1);
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
		updateStats();
	}

	public List<ArmorQuality> getQuals() {
		return quals;
	}
	
	public void armorQualDam(int dam) {
		if (quals.contains(ArmorQuality.FRAGILE)) {
			//TODO fix and update
			condition = extra.clamp(condition-extra.lerp(0.05f,.5f, extra.clamp(dam,1,20)/20f),0,2);
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
