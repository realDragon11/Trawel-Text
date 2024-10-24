package trawel.personal.item.solid;

import java.util.EnumSet;
import java.util.Set;

import trawel.core.Print;
import trawel.core.Rand;
import trawel.core.mainGame;
import trawel.core.mainGame.GraphicStyle;
import trawel.helper.constants.TrawelChar;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.Services;
import trawel.helper.methods.extra;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.solid.Weapon.WeaponQual;
import trawel.personal.item.solid.variants.ArmorStyle;
import trawel.personal.people.Player;

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
	
	public static final double armorEffectiveness = 8;//5 armor slots
	
	//instance variables
	private byte slot;//The slot which the armor goes into
	private int material;//what material the object is made from
	private short style;
	private int fluff;//stores the fluff of the style
	private Enchant enchantment;
	
	private transient double condition;
	private transient float sharpActive, bluntActive, pierceActive;
	
	private EnumSet<ArmorQuality> quals = EnumSet.noneOf(ArmorQuality.class);
	
	public enum ArmorQuality {
		FRAGILE("Fragile",.9f,.9f,"Takes double condition damage from all sources."
				,null,-2),
		DEFLECTING("Deflecting",1.1f,1.15f,"Provides immunity to the Penetrative and Pinpoint Weapon Quals on this slot."
				,null,1),
		DISPLACING("Displacing",1.1f,1.15f,"Increases hostile miss threshold by +.01."
				,"Base miss threshold is .05.",2),
		STURDY("Sturdy",1.1f,1.25f,"Takes half condition damage from all sources."
				,null,2),
		HEAVY("Heavy",.9f,1f,"Weighs +20% as much."
				,"Weight increase is relative to a normal armor of that type.",-1),
		LIGHT("Light",1.05f,1.2f,"Weighs -10% as much."
				,"Weight increase is relative to a normal armor of that type.",1),
		PADDED("Padded",1.2f,1.3f,"Has a 1/5th chance to resist each wound, one wound resisted per fight."
				,"Stacks additively in chance, decreasing as wounds are resisted.",3),
		REFINED("Refined",1.2f,1.3f,"+10% condition at the start of each fight."
				,"Condition above 100% degrades each action.",3),
		BLOCKING("Blocking",1.1f,1.2f,"Causes attacks to be fully blocked at +2% more percentage of armor mitigation."
				,"Applies globally, stacking on the base value of 40%.",2),
		RELIABLE("Reliable",1.2f,1.3f,"+5% minimum armor roll."
				,"Applies globally, stacking on base value of 5%.",2)
		
		;
		
		public final String name, desc, mechDesc;
		/**
		 * can be any negative or positive number, or even zero
		 */
		private final int goodNegNeut;
		/**
		 * mFit is the mechanical value for the effectiveness
		 * <br>
		 * mVal is used to represent trade value and is often higher for positive qualities but near the same for negative ones
		 * <br>
		 * some qualities are considered 'convenient' in lore but that is not reflected in game, so the value is even higher,
		 * such as sturdy and reliable
		 */
		private final float mFit, mVal;
		ArmorQuality(String nam, float _mFit, float _mVal, String des, String _mechDesc, int _goodNegNeut){
			name = nam;
			desc = des;
			mechDesc = _mechDesc;
			goodNegNeut = _goodNegNeut;
			mFit = _mFit;
			mVal = _mVal;
		}
		
		public String removeColor() {
			if (goodNegNeut == 0) {
				return TrawelColor.TIMID_GREY+"Trait: ";
			}
			if (goodNegNeut < 0) {
				return TrawelColor.TIMID_GREEN+"Flaw: ";
			}
			//if (goodNegNeut > 0) {
				return TrawelColor.TIMID_RED+"Qual: ";
			//}
		}
		public String addColor() {
			if (goodNegNeut == 0) {
				return TrawelColor.TIMID_GREY+"Trait: ";
			}
			if (goodNegNeut < 0) {
				return TrawelColor.TIMID_RED+"Flaw: ";
			}
			//if (goodNegNeut > 0) {
				return TrawelColor.TIMID_GREEN+"Qual: ";
			//}
		}
		
		public String addText() {
			return (addColor()+name + ": " +desc);
		}
		public String removeText() {
			return (removeColor()+name + ": " +desc);
		}
	}
	
	//constructors
	
	/**
	 * Make a new random armor.
	 * @param newLevel (int)
	 */
	public Armor(int newLevel) {
		this(newLevel,(byte) Rand.getRand().nextInt(5),MaterialFactory.randArmorMat(),null);
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
	 * This function should mostly be only used internally, but if you want a certain material and type to be generated, it will work.
	 * @param newLevel (int)
	 * @param slot (int)
	 * @param mati (Material)
	 * @param styleType (ArmorStyle)
	 */
	public Armor(int newLevel, byte slot,Material mati,ArmorStyle styleType) {	
		this.slot = slot;//type is equal to the array armor slot
		
		level = newLevel;
		
		if (styleType == null) {
			assert mati != null;
			style = (short) Rand.randList(mati.typeList).ordinal();
		}else {
			style = (short) styleType.ordinal();
			if (mati == null) {
				mati = styleType.getMatFor();
			}
		}
		
		material = mati.curNum;

		fluff = ArmorStyle.fetch(style).genner[slot].generate();

		float baseEnchant = getEnchantMult();
		if (baseEnchant > Rand.randFloat()*3f) {
			enchantment = EnchantConstant.makeEnchant(baseEnchant,getAetherValue());//used to include level in the mult, need a new rarity system
		}
		//TODO: better quality generation code
		if (Rand.randFloat() < mati.sturdy) {
			quals.add(ArmorQuality.STURDY);
		}
		if (Rand.randFloat() < mati.shimmer) {
			quals.add(ArmorQuality.DISPLACING);
		}
		
		switch (ArmorStyle.fetch(style)) {
		case PLATE:
			if (Rand.chanceIn(2,5)) {//weight is base, but craftsmanship can make it worse
				quals.add(ArmorQuality.HEAVY);
			}else {
				if (Rand.chanceIn(1,5)) {
					quals.add(ArmorQuality.LIGHT);
				}
			}
			if (Rand.chanceIn(5,6)) {
				int size = quals.size();
				while (size < 5 && Rand.chanceIn(2,size+2)) {
					switch (Rand.randRange(1,5)) {
					case 1:
						quals.add(ArmorQuality.BLOCKING);
						break;
					case 2:
						quals.add(ArmorQuality.RELIABLE);
						break;
					case 3:
						quals.add(ArmorQuality.REFINED);
						break;
					case 4:
						quals.add(ArmorQuality.PADDED);
						break;
					case 5:
						quals.add(ArmorQuality.DEFLECTING);
						break;
					}
					if (quals.size() == size) {
						break;//did not add
					}
					size = quals.size();
				}
			}
			break;
		case MAIL:
			if (Rand.chanceIn(1,3)) {
				quals.add(ArmorQuality.DEFLECTING);
			}
			//not much room to make the chainmail heavier or lighter
			switch (Rand.randRange(1,3)) {
			case 1://nothing
				break;
			case 2:
				quals.add(ArmorQuality.RELIABLE);
				break;
			case 3:
				quals.add(ArmorQuality.REFINED);
				break;
			}
			break;
		case GEM:
			if (!quals.contains(ArmorQuality.STURDY) && Rand.chanceIn(2,3)) {
				quals.add(ArmorQuality.FRAGILE);
				quals.add(ArmorQuality.REFINED);
			}
			if (Rand.chanceIn(1,3)) {
				quals.add(ArmorQuality.DISPLACING);//can't stack, but fails without issue
			}
			if (Rand.chanceIn(1,3)) {
				quals.add(ArmorQuality.LIGHT);
			}else {
				if (Rand.chanceIn(1,3)) {
					quals.add(ArmorQuality.HEAVY);
				}
			}
			switch (Rand.randRange(1,3)) {
			case 1://nothing
				break;
			case 2:
				quals.add(ArmorQuality.BLOCKING);
				break;
			case 3:
				quals.add(ArmorQuality.REFINED);
				break;
			}
			break;
		case BODY:
			//n/a
			break;
		case FABRIC:
			if (Rand.chanceIn(1,3)) {
				quals.add(ArmorQuality.DISPLACING);
			}
			if (Rand.chanceIn(1,3)) {
				quals.add(ArmorQuality.LIGHT);
			}else {
				if (Rand.chanceIn(1,3)) {
					quals.add(ArmorQuality.HEAVY);
				}
			}
			break;
		case SEWN:
			if (Rand.chanceIn(1,3)) {
				quals.add(ArmorQuality.REFINED);
			}
			if (Rand.chanceIn(2,3)) {
				quals.add(ArmorQuality.PADDED);
			}
			if (Rand.chanceIn(1,3)) {
				quals.add(ArmorQuality.LIGHT);
			}else {
				if (Rand.chanceIn(1,3)) {
					quals.add(ArmorQuality.HEAVY);
				}
			}
			break;
		case GROWN:
			ArmorStyle armorStyle = ArmorStyle.fetch(style);
			if (!armorStyle.addBonusQuals.isEmpty()) {
				quals.add(Rand.randCollection(armorStyle.addBonusQuals));
				quals.add(Rand.randCollection(armorStyle.addBonusQuals));
			}
			break;
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
			return (enchant.getBeforeName() +mat.color + mat.name+TrawelColor.COLOR_RESET + " " + getBaseName() + enchant.getAfterName());
		}
		return (mat.color + mat.name+TrawelColor.COLOR_RESET  + " " +  getBaseName());
	}
	
	@Override
	public String getName() {
		//traits are now positive and negative for armors
		//this doesn't impact how the ai sees them, but a player won't quite understand
		//that the same statline without fragile is better without indicators like this
		return getModiferNameColored(5+qualTraitSum())+getLevelName() + " " + getNameNoTier();
	}
	
	public int qualTraitSum() {
		int sum = 0;
		for (ArmorQuality q: quals) {
			sum += q.goodNegNeut;
		}
		return sum;
	}
	
	@Override
	public int getQualityTier() {
		return qualTraitSum();
	}
	
	@Override
	public int temperNegQuality(int amount) {
		int removed = 0;
		for (int i = 0; i < amount; i++) {
			int worstAmount = 0;
			ArmorQuality worstQual = null;
			for (ArmorQuality q: quals) {
				if (q.goodNegNeut < worstAmount) {
					worstAmount = q.goodNegNeut;
					worstQual = q;
				}
			}
			if (worstAmount < 0) {
				quals.remove(worstQual);
				removed++;
			}else {
				return removed;
			}
		}
		return removed;
	}
	
	@Override
	public boolean hasNegQuality() {
		for (ArmorQuality q: quals) {
			if (q.goodNegNeut < 0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int improvePosQuality(int amount) {
		ArmorStyle style = getStyle();
		if (style.addBonusQuals.isEmpty()) {
			return 0;
		}
		int added = 0;
		//will only try a max of 5 times
		for (int i = 0; i < 5;i++) {
			if (added >= amount) {
				return added;
			}
			ArmorQuality q = Rand.randCollection(style.addBonusQuals);
			if (!quals.contains(q)) {
				quals.add(q);
				added++;
			}
		}
		return added;
	}
	

	/**
	 * gets the 'normal' armor level OOB
	 * @return the sharpResist (float)
	 */
	public float getSharpResist() {
		return ArmorStyle.fetch(style).sharpMult*MaterialFactory.getMat(material).sharpResist*baseResist();
	}

	/**
	 * gets the 'normal' armor level OOB
	 * @return the bluntResist (float)
	 */
	public float getBluntResist() {
		return ArmorStyle.fetch(style).bluntMult*MaterialFactory.getMat(material).bluntResist*baseResist();
	}

	/**
	 * gets the 'normal' armor level OOB
	 * @return the pierceResist (float)
	 */
	public float getPierceResist() {
		return ArmorStyle.fetch(style).pierceMult*MaterialFactory.getMat(material).pierceResist*baseResist();
	}
	
	public void resetArmor(int s, int b, int p) {
		sharpActive = getSharpResist()+s;
		bluntActive = getBluntResist()+b;
		pierceActive = getPierceResist()+p;
		condition = 1 + (hasArmorQual(ArmorQuality.REFINED) ? .1 : 0);
	}
	
	
	//MAYBELATER: for now, all are the same
	//either fix, or make certain armor bits count more for global armor
	private float baseResist() {
		return IEffectiveLevel.unEffective(getEffectiveLevel())*
				1.5f*ArmorStyle.fetch(style).totalMult
				*MaterialFactory.getMat(material).baseResist
				;
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
		damage(d);//*getFireMod()
	}
	
	/**
	 * as with burn but without applying fire mod
	 * <br>
	 * pass the % decrease in armor.
	 * <br>
	 * the decrease is diminishing, but not continuously diminishing, so large damages at once will have more effect 
	 * <br>
	 * will attempt to not let condition reach 0 entirely, minimum of 5% overall and
	 * max 70% reduction in one hit
	 */
	public void damage(double d) {
		assert d >= 0;
		if (hasArmorQual(ArmorQuality.STURDY)) {
			d*=.5;
		}
		if (hasArmorQual(ArmorQuality.FRAGILE)) {
			d*=2;
		}
		condition = Math.max(.05, condition-(condition*extra.clamp(d,0,.7)));
	}
	
	/**
	 * multiplier on effectiveness
	 */
	public void buff(double d) {
		condition *=d;
	}
	
	public void buffAdd(double d) {
		condition +=d;
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
		return (int) (
				Inventory.TEMP_WEIGHT_MULT
				*slotImpact()
				*MaterialFactory.getMat(material).weight
				*ArmorStyle.fetch(style).weightMult
				* (hasArmorQual(ArmorQuality.HEAVY) ? 1.2f : 1f)
				* (hasArmorQual(ArmorQuality.LIGHT) ? .9f : 1f)
				);
	}
	
	private int slotImpact() {
		switch (slot) {
		case 0://head
		case 1://gloves
			return 3;
		case 2://chest
			return 10;
		case 3://pants
			return 7;
		case 4:
			return 4;
		}
		throw new RuntimeException("Invalid slot for impact" + slot + " "+this.toString());
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
		float mult = MaterialFactory.getMat(material).cost*ArmorStyle.fetch(style).costMult*slotImpact()*getUnEffectiveLevel();
		for (ArmorQuality q: quals) {
			mult *= q.mVal;
		}
		return (int) (mult);
	}
	
	@Override
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
	@Override
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
		case 0:
			//for quick store overviews use storestring instead
			break;
		case 1://comparing
		case 3:
			Print.println(this.getName() + ":");
			Print.println(
			  TrawelChar.DISP_INDENT +TrawelColor.ITEM_DESC_PROP+ TrawelChar.CHAR_SHARP+TrawelColor.ITEM_WANT_HIGHER+Print.format(this.getSharpResist())
			+ TrawelChar.DISP_INDENT+TrawelColor.ITEM_DESC_PROP + TrawelChar.CHAR_BLUNT+TrawelColor.ITEM_WANT_HIGHER+Print.format(this.getBluntResist())
			+ TrawelChar.DISP_INDENT+TrawelColor.ITEM_DESC_PROP + TrawelChar.CHAR_PIERCE+TrawelColor.ITEM_WANT_HIGHER+Print.format(this.getPierceResist())
			+ (Player.player.caresAboutCapacity() ? TrawelChar.DISP_INDENT+TrawelColor.ITEM_DESC_PROP+TrawelChar.DISP_WEIGHT+": "+TrawelColor.ITEM_WANT_LOWER+getWeight() : "")
			+ (Player.player.caresAboutAMP() ? TrawelChar.DISP_INDENT+TrawelColor.ITEM_DESC_PROP+TrawelChar.DISP_AMP+": "+TrawelColor.ITEM_WANT_LOWER+ Print.F_TWO_TRAILING.format(getAgiPenMult())+"x" : "")
			+TrawelChar.DISP_INDENT+TrawelColor.ITEM_DESC_PROP+TrawelChar.DISP_AETHER+": " +TrawelColor.ITEM_VALUE+ Print.F_WHOLE.format(Math.ceil(getAetherValue()*markup)));
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			printQuals();
			;break;	
		case 4:
		case 5:
			//extra examine
		case 2://full examine
			Print.println(
			this.getName()
			+ TrawelChar.DISP_INDENT + TrawelColor.ITEM_DESC_PROP+TrawelChar.CHAR_SHARP+TrawelColor.ITEM_WANT_HIGHER+Print.format(this.getSharpResist())
			+ TrawelChar.DISP_INDENT + TrawelColor.ITEM_DESC_PROP+TrawelChar.CHAR_BLUNT+TrawelColor.ITEM_WANT_HIGHER+Print.format(this.getBluntResist())
			+ TrawelChar.DISP_INDENT + TrawelColor.ITEM_DESC_PROP+TrawelChar.CHAR_PIERCE+TrawelColor.ITEM_WANT_HIGHER+Print.format(this.getPierceResist())
			+ TrawelChar.DISP_INDENT+TrawelColor.ITEM_DESC_PROP+TrawelChar.DISP_WEIGHT+": "+TrawelColor.ITEM_WANT_LOWER+getWeight()
			+ TrawelChar.DISP_INDENT+TrawelChar.DISP_AMP+": "+TrawelColor.ITEM_WANT_LOWER+ Print.F_TWO_TRAILING.format(getAgiPenMult())+"x"
			/*no longer used
			+TrawelColor.ITEM_DESC_PROP+ " ignite mult: "+TrawelColor.ITEM_WANT_LOWER+ Print.F_TWO_TRAILING.format(getFireMod())+"x"
			+TrawelColor.ITEM_DESC_PROP+ " frost mult: "+TrawelColor.ITEM_WANT_LOWER+ Print.F_TWO_TRAILING.format(getFreezeMod())+"x"
			+TrawelColor.ITEM_DESC_PROP+ " elec mult: "+TrawelColor.ITEM_WANT_LOWER+ Print.F_TWO_TRAILING.format(getShockMod())+"x"
			*/
			+TrawelColor.ITEM_DESC_PROP+ TrawelChar.DISP_INDENT+"aether: " +TrawelColor.ITEM_VALUE+ (int)(this.getAetherValue()*markup));
			if (this.getEnchant() != null) {
				this.getEnchant().display(1);
			}
			printQuals();
			;break;
		case 20://for store overviews
			Print.println(this.getName() + TrawelColor.ITEM_DESC_PROP+" sbp:" +TrawelColor.ITEM_WANT_HIGHER
			+Print.format(this.getSharpResist()) + " " + Print.format(this.getBluntResist()) + " " + Print.format(this.getPierceResist())
			+" "+TrawelColor.ITEM_DESC_PROP+TrawelChar.DISP_AETHER+": " +TrawelColor.ITEM_VALUE+ Print.F_WHOLE.format(Math.ceil(getAetherValue()*markup)));
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
	
	public void printQuals() {
		for (ArmorQuality aq: quals) {
			Print.println(TrawelChar.DISP_INDENT+aq.addText());
			/*
			if (aq.goodNegNeut < 0) {
				extra.println(" Flaw: "+aq.addText());
			}else {
				if (aq.goodNegNeut > 0) {
					extra.println(" Qual: "+aq.addText());
				}else {
					extra.println(" Trait: "+aq.addText());
				}
			}*/
			
		}
	}
	
	@Override
	public String storeString(double markup, int canShow) {
		if (canShow > 0) {
			return this.getName() 
				+ TrawelColor.ITEM_DESC_PROP+TrawelChar.DISP_INDENT+TrawelChar.CHAR_SHARP + Print.F_WHOLE.format(this.getSharpResist())
				+ TrawelColor.ITEM_DESC_PROP+TrawelChar.DISP_INDENT+TrawelChar.CHAR_BLUNT + Print.F_WHOLE.format(this.getBluntResist())
				+ TrawelColor.ITEM_DESC_PROP+TrawelChar.DISP_INDENT+TrawelChar.CHAR_PIERCE + Print.F_WHOLE.format(this.getPierceResist())
				+ TrawelColor.ITEM_DESC_PROP+TrawelChar.DISP_INDENT+"cost"+TrawelColor.PRE_WHITE+": " +TrawelColor.ITEM_VALUE+Print.F_WHOLE.format(Math.ceil(getAetherValue()*markup))
				+ (canShow == 1 ?TrawelColor.TIMID_RED+" (raw deal)" : "");
		}
		String base = getBaseName();
		return TrawelColor.TIMID_GREY+"  They refuse to show you something you think " + Print.pluralIsA(base) + " "+base+".";
	}
	
	@Override
	public ItemType getType() {
		return Item.ItemType.ARMOR;
	}

	public String getMaterialName() {
		return MaterialFactory.getMat(material).name;
	}

	public String getBaseMap(GraphicStyle style) {
		switch (style) {
		case LEGACY:
			switch (getStyle()) {
			case BODY:
				return "";
			case FABRIC:
			case SEWN:
				return "cloth";
			case GEM:
				return "crystal";
			case MAIL:
			case PLATE:
				return "iron";
			}
			return null;
		case WASDD:
			return "wasdd";
		}
		return null;
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

	public EnumSet<ArmorQuality> getQuals() {
		return quals;
	}
	
	/*
	public void armorQualDam(float hpPercent) {
		if (quals.contains(ArmorQuality.FRAGILE)) {
			damage(hpPercent*.5f);
		}
	}*/

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
	
	public float fitness() {
		float mult = 1;
		for (ArmorQuality q: quals) {
			mult *= q.mFit;
		}
		if (isEnchanted()) {
			mult *= enchantment.fitness();
		}
		return mult*(getBluntResist()+getPierceResist()+getSharpResist());
	}

	public boolean hasArmorQual(ArmorQuality qual) {
		return quals.contains(qual);
	}
	
}
