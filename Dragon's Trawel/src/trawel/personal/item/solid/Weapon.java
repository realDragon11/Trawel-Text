package trawel.personal.item.solid;

import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.Set;

import derg.ds.Chomp;
import trawel.battle.Combat;
import trawel.battle.Combat.ATK_ResultType;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.Stance;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.Services;
import trawel.helper.methods.extra;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.magic.EnchantHit;
import trawel.personal.item.solid.Armor.ArmorQuality;
import trawel.personal.people.Player;
import trawel.threads.ThreadData;

/**
 * 
 * @author dragon
 * before 2/11/2018
 * A Weapon is an Item.
 * It is made up of a certain material, and of a certain type. It my also have an Enchantment of some sort.
 * Different materials have different properties, different types have different attacks, which
 * are stored in the weapon's stance elsewhere.
 */

public class Weapon extends Item implements IEffectiveLevel {

	private static final long serialVersionUID = 1L;
	
	private Enchant enchant;

	private WeaponType weap;
	private int material;
	private int kills;
	/**
	 * packed int
	 * <br>
	 * 1st byte = impact chance 0-100
	 * 2nd + 3rd bytes = short for best damage
	 * 4th + 5th bytes = short for weighted average damage
	 * 6th byte = highest
	 * 7th byte = lowest
	 */
	private long bsCon = 0;
	//is lazy inited again
	
	private Set<WeaponQual> qualList = EnumSet.noneOf(WeaponQual.class);
	
	public enum WeaponQual{
		DESTRUCTIVE("Destructive","On Damage and Wound: Damages local armor by 1/3rd of total percent HP dealt.",2),
		PENETRATIVE("Penetrative","Attack: The locally attacked armor counts for 3/5ths as much.",2),
		PINPOINT("Pinpoint","Attack: Armor not in slots you are attacking counts for 2/3rds as much.",2),
		RELIABLE("Reliable","On Armor Reduction/Block: Minimum damage equal to 1/2th WELVL. If blocked, does not become Impactful.",2), 
		DUELING("Dueling","Attack: In large fights, attack the same opponent repeatedly.",1),
		WEIGHTED("Weighted","On Damage: Less accurate attacks deal more damage.",1),
		REFINED("Refined","On Damage: Deals bonus damage equal to 1/2th WELVL.",3),
		ACCURATE("Accurate","Attack: Flat +%.10 accuracy bonus to attacks after all modifiers.",2),
		CARRYTHROUGH("Carrythrough","On Miss/Dodge: Your next attack on a different opponent is 20% quicker.",2),
		;
		public final String name, desc;
		public final int goodNegNeut;
		WeaponQual(String name,String desc,int _goodNegNeut) {
			this.name = name;
			this.desc = desc;
			goodNegNeut = _goodNegNeut;
		}
	}
	
	public int numQual() {
		return qualList.size();
	}
	
	public boolean hasQual(WeaponQual qual) {
		return qualList.contains(qual);
	}
	
	public boolean equalQuals(Weapon w) {
		return qualList.equals(w.qualList);
	}
	
	public enum WeaponType{
		LONGSWORD("longsword","longsword","broadsword",3f,2,1f,EnumSet.of(WeaponQual.RELIABLE,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		BROADSWORD("broadsword","broadsword","broadsword",3.5f,3,1f,EnumSet.of(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		MACE("mace","mace","hatchet",3f,2,1f,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,WeaponQual.CARRYTHROUGH)),
		SPEAR("spear","spear","hatchet",2.5f,2,1f,EnumSet.of(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		AXE("axe","small_axe","hatchet",2.5f,2,1f,EnumSet.of(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		RAPIER("rapier","rapier","broadsword",4f,3,.8f,EnumSet.of(WeaponQual.PINPOINT,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		DAGGER("dagger","dagger","broadsword",1.8f,1,.8f,EnumSet.of(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		CLAYMORE("claymore","claymore","broadsword",4f,5,.3f,EnumSet.of(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED)),
		LANCE("lance","lance","hatchet",4f,3,.2f,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		SHOVEL("shovel","shovel","hatchet",2.2f,2,.1f,EnumSet.of(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,WeaponQual.CARRYTHROUGH)),
		TEETH_GENERIC("teeth",null,null,0,0,0f,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.PENETRATIVE,WeaponQual.DUELING,Weapon.WeaponQual.ACCURATE)),
		REAVER_STANDING("clawed feet",null,null,0,0,0f),
		CLAWS_TEETH_GENERIC("teeth and claws",null,null,0,0,0f,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		BRANCHES("branches",null,null,0,0,0f),
		GENERIC_FISTS("fists",null,null,0,0,0f),
		UNICORN_HORN("horn",null,null,0,0,0f,EnumSet.of(WeaponQual.PENETRATIVE,WeaponQual.PINPOINT)),
		TALONS_GENERIC("talons",null,null,0,0,0f,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.PENETRATIVE,WeaponQual.PINPOINT,Weapon.WeaponQual.ACCURATE,WeaponQual.CARRYTHROUGH)),
		FISH_SPEAR("fishing bill","spear","hatchet",.5f,1,0f,EnumSet.of(WeaponQual.ACCURATE,WeaponQual.PENETRATIVE,WeaponQual.PINPOINT,WeaponQual.DUELING)),//bill is a type of hook polearm and 'hook spear' doesn't quite sound right. I think bills are probably a bit too curved tho?
		FISH_ANCHOR("barnacled anchor","claymore","broadsword",1f,5,0f,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.WEIGHTED,WeaponQual.CARRYTHROUGH)),
		NULL_WAND("WAND",null,null,0,0,0f)
		;
		
		private final String name, legacysprite, wasddsprite;
		private final float cost, weight, rarity;
		private final Set<WeaponQual> qList;
		WeaponType(String _name, String _legacysprite, String _wasddsprite, float _cost, float _weight, float _rarity, Set<WeaponQual> _list) {
			name = _name;
			legacysprite = _legacysprite;
			wasddsprite = _wasddsprite;
			cost = _cost;
			weight = _weight;
			qList = _list;
			rarity = _rarity;
		}
		WeaponType(String _name, String _legacysprite, String _wasddsprite, float _cost, float _weight, float _rarity) {
			name = _name;
			legacysprite = _legacysprite;
			wasddsprite = _wasddsprite;
			cost = _cost;
			weight = _weight;
			qList = EnumSet.noneOf(WeaponQual.class);
			rarity = _rarity;
		}
		
		public String getName() {
			return name;
		}
		public String getLegacy() {
			return legacysprite;
		}
		public String getWasdd() {
			return wasddsprite;
		}
		public float getRarity() {
			return rarity;
		}
	}
	
	//constructors
	/**
	 * Standard weapon constructor. Makes a weapon of level newLevel
	 * @param newLevel (int)
	 */
	public Weapon(int newLevel, Material materia, WeaponType weapon) {
		material = materia.curNum;
		level = newLevel;
		//choosing the type of weapon
		weap = weapon;
		kills = 0;
		
		addQuals(weap.qList,3);
		//random chance, partially based on enchantment power, to enchant the weapon
		if (getEnchantMult() > Rand.randFloat()*3f) {
			if (Rand.chanceIn(2, 3)) {
				enchant = EnchantConstant.makeEnchant(getEnchantMult(),getBaseCost());
			}else {
				enchant = new EnchantHit(getEnchantMult(),false);
			}
		}
		
	}
	
	public Weapon(int newLevel) {
		this(newLevel,MaterialFactory.randWeapMat(),WeaponAttackFactory.randWeapType());
	}
	public Weapon(int newLevel, WeaponType type) {
		this(newLevel,MaterialFactory.randWeapMat(),type);
	}

	//instance methods
	
	public WeaponType getWeaponType() {
		return weap;
	}
	
	@Override
	public float getEnchantMult() {
		return MaterialFactory.getMat(material).baseEnchant;
	}
	
	@Override
	public boolean canAetherLoot() {
		switch (weap) {
		case TEETH_GENERIC: return false;
		case REAVER_STANDING: return false;
		case CLAWS_TEETH_GENERIC: return false;
		case BRANCHES: return false;
		case GENERIC_FISTS: return false;
		case UNICORN_HORN: return false;
		case TALONS_GENERIC: return false;
		case FISH_SPEAR: return true;
		case FISH_ANCHOR: return true;
		case NULL_WAND: return false;
		default: return true;//normal weapons
		}
	}
	
	/**
	 * Returns true if the weapon is enchanted
	 * @return isEnchantedConstant (boolean)
	 */
	public boolean isEnchantedConstant() {
		return enchant != null && enchant.getEnchantType() == Enchant.Type.CONSTANT;
	}
	
	public boolean isEnchantedHit() {
		return enchant != null && enchant.getEnchantType() == Enchant.Type.HIT;
	}
	
	/**
	 * get the reference to the enchantment on the weapon
	 * @return enchant (EnchantConstant)
	 */
	@Override
	public Enchant getEnchant() {
		return enchant;
	}

	/**
	 * Returns the stance of the weapon.
	 * @return the martialStance (Stance)
	 */
	public Stance getMartialStance() {
		return WeaponAttackFactory.getStance(weap);
	}
	
	/**
	 * Returns the full name of the weapon.
	 * @return String
	 */
	@Override
	public String getNameNoTier() {
		String weapName = weap.getName();
		Material mat = MaterialFactory.getMat(material);
		if (enchant != null) {
			return (enchant.getBeforeName() +mat.color+ mat.name + TrawelColor.COLOR_RESET+ " " +  weapName + enchant.getAfterName());
		}
		return (mat.color+ mat.name  +TrawelColor.COLOR_RESET+ " " + weapName);		
	}
	
	@Override
	public String getName() {
		return getModiferNameColored(extra.clamp(qualTraitSum(), 0,12))+getLevelName() + " " +getNameNoTier();
	}
	
	public int qualTraitSum() {
		//quals tend to be 1-3 average 2
		//so 2*3 = 6
		//this means we can just take it directly since it'll hover around 5, which is 'no quality'
		int sum = 0;
		for (WeaponQual q: qualList) {
			sum += q.goodNegNeut;
		}
		return sum;
	}
	
	@Override
	public int getQualityTier() {
		return qualTraitSum();
	}
	
	
	/**
	 * Get the weight of the item
	 * @return weight (int)
	 */
	public int getWeight() {
		return (int) (getMat().weight*weap.weight*Inventory.TEMP_WEIGHT_MULT);
	}
	
	/**
	 * get the cost of the item
	 * @return cost (int)
	 */
	@Override
	public int getAetherValue() {
		if (this.isEnchantedConstant()) {
			return (int) (getBaseCost() * enchant.getGoldMult()+enchant.getGoldMod());
		}
		if (this.isEnchantedHit()) {
			return (int) (getBaseCost() * enchant.getGoldMult());
		}
		return getBaseCost();
	}
	
	/**
	 * get the base cost of the item
	 * @return base cost (int)
	 */
	public int getBaseCost() {
		return (int) (getMat().cost*weap.cost*getUnEffectiveLevel() * (1 + .05f*qualTraitSum()));
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
		if (this.isEnchantedConstant()) {
			Enchant pastEnchant = enchant;
			enchant = Services.improveEnchantChance(enchant, level, getEnchantMult());
			//effectiveCost=(int) extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
			updateStats();
			return pastEnchant != enchant;
		}else {
			//IsEnchantedConstant = true;
			enchant = EnchantConstant.makeEnchant(getEnchantMult(),getBaseCost());//new EnchantConstant(level*baseEnchant);
			//effectiveCost=(int) extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
			updateStats();
			return true;
		}
	}
	
	/**
	 * @return the baseName (String)
	 */
	public String getBaseName() {
		return weap.getName();
	}
	
	private void refreshBattleScore() {
		double impactChance = 0;
		double total = 0;
		double weighted = 0;
		final Stance stance = this.getMartialStance();
		final int testSize = ThreadData.getDumInvs().size();
		double highest = 0;
		double lowest = Double.MAX_VALUE;
		for (int i = stance.getAttackCount()-1;i >=0;i--) {
			Attack holdAttack = stance.getAttack(i);
			float weight = stance.getWeight(i);
			double dam = 0;
			for (int j = testSize-1; j >=0;j--) {
				for (int ta = 0; ta < battleTests;ta++) {
					AttackReturn ret = Combat.handleTestAttack(holdAttack.impair(null,this,null)
							,ThreadData.getDumInvs().get(j).atLevel(level)
							);
					dam+= ret.damage/ret.attack.getTime();
					if (ret.type == ATK_ResultType.IMPACT) {
						impactChance+=weight;
					}
				}
			}
			total += dam;
			weighted += dam*weight;
			if (highest < dam) {
				highest = dam;
			}
			if (lowest > dam) {
				lowest = dam;
			}
		}
		
		final int subTests = battleTests*testSize;
		//create long and set impact chance
		long conAssembler = Chomp.setNthByteInInt(0b0,(int)((impactChance*100d)/subTests), 0);
		//below two will explode if contain more than a short can hold, but that will be a VERY large level
		//set best damage
		conAssembler = Chomp.setShortInLong(conAssembler, (int)((highest*100d)/(subTests)),8);
		//set weighted damage
		conAssembler = Chomp.setShortInLong(conAssembler, (int)((weighted*100d)/(subTests)),24);
		conAssembler = Chomp.setByteInLong(conAssembler, (int)(highest*(100d/total)), 40);
		conAssembler = Chomp.setByteInLong(conAssembler, (int)(lowest*(100d/total)), 48);
		
		this.bsCon = conAssembler;
	}
	
	public static int battleTests = 10;//now how many times each attack gets tested on each armor set 3 to 5 to 10
	
	public class DamTuple implements java.io.Serializable{
		
		private static final long serialVersionUID = 1L;
		public final double highest;
		public final double average;
		public final double battleScore;
		public DamTuple(double h, double a, double b) {
			highest = h;
			average = a;
			battleScore = b;
		}
	}
	
	/**
	 * 0-100
	 */
	public int scoreHighestContribution() {
		if (bsCon == 0) {
			refreshBattleScore();
		}
		return Chomp.extractByteFromLong(bsCon, 40);
	}
	/**
	 * 0-100
	 */
	public int scoreLowestContribution() {
		if (bsCon == 0) {
			refreshBattleScore();
		}
		return Chomp.extractByteFromLong(bsCon, 48);
	}
	
	public double scoreImpact() {
		if (bsCon == 0) {
			refreshBattleScore();
		}
		return Chomp.extractByteFromLong(bsCon, 0)/100d;
	}
	
	public double scoreWeight() {
		if (bsCon == 0) {
			refreshBattleScore();
		}
		return Chomp.extractShortFromLong(bsCon,24)/100d;
	}
	public double scoreBest() {
		if (bsCon == 0) {
			refreshBattleScore();
		}
		return Chomp.extractShortFromLong(bsCon,8)/100d;
	}

	@Override
	public void display(int style,float markup) {
		switch (style) {
		//0 is quick for store quickview, not used, use storestring
		case 0: Print.println(getMaterialName() +" "+getBaseName()+":"+Print.format(this.scoreWeight()));
		break;
		case 1://used for comparing and in stores
		case 3:
			Print.println(this.getName()
			+ TrawelColor.ITEM_DESC_PROP+" ic/bd/wa"+TrawelColor.PRE_WHITE+": "
			+TrawelColor.ITEM_WANT_HIGHER
			+ Print.formatPerSubOne(this.scoreImpact())
			+ "/" + Print.format(this.scoreBest())
			+"/"+Print.format(this.scoreWeight())
			+ (Player.player.caresAboutCapacity() ? " "+TrawelColor.ITEM_DESC_PROP+extra.DISP_WEIGHT+TrawelColor.PRE_WHITE+": "+TrawelColor.ITEM_WANT_LOWER+getWeight() : "")
			+" "+TrawelColor.ITEM_DESC_PROP+extra.DISP_AETHER+": " +TrawelColor.ITEM_VALUE+ Print.F_WHOLE.format(Math.ceil(getAetherValue()*markup))
			);
			
			if (this.isEnchantedConstant()) {
				this.getEnchant().display(1);
			}
			if (this.isEnchantedHit()) {
				this.getEnchant().display(1);
			}
			for (WeaponQual wq: qualList) {
				Print.println(" " +TrawelColor.TIMID_GREEN+wq.name +TrawelColor.PRE_WHITE+": "+wq.desc);
			}
			;break;
		case 4:
		case 5:
			//extra examine
		case 2://Appraiser/full self on stat
			//by dividing it later we implicitly mult it by 100x to get it to display as a whole number
			float expectedAverage = (1f/getMartialStance().getAttackCount());
			Print.println(getName() +":");
			Print.println(TrawelColor.STAT_HEADER+"Tested Stats:");
			Print.println(TrawelColor.ITEM_DESC_PROP+" Impact Chance (ic)"+TrawelColor.PRE_WHITE+": "+TrawelColor.ITEM_WANT_HIGHER+ Print.formatPerSubOne(scoreImpact()));
			Print.println(TrawelColor.ITEM_DESC_PROP+" Best DPI (bd)"+TrawelColor.PRE_WHITE+": "+TrawelColor.ITEM_WANT_HIGHER + Print.format(scoreBest()));
			Print.println(TrawelColor.ITEM_DESC_PROP+" Weighted DPI (wa)"+TrawelColor.PRE_WHITE+": "+TrawelColor.ITEM_WANT_HIGHER + Print.format(scoreWeight()));
			Print.println(TrawelColor.STAT_HEADER+"Value and Usage:");
			Print.println(TrawelColor.ITEM_DESC_PROP+" Aether"+TrawelColor.PRE_WHITE+": "+TrawelColor.ITEM_VALUE + (int)(getAetherValue()*markup));
			Print.println(TrawelColor.ITEM_DESC_PROP+" Infused kills"+TrawelColor.PRE_WHITE+": " +getKills());
			
			if (isEnchantedConstant()) {
				Print.println(TrawelColor.STAT_HEADER+"Constant Enchantment:");
				getEnchant().display(2);
			}
			if (isEnchantedHit()) {
				Print.println(TrawelColor.STAT_HEADER+"On-Hit Enchantment:");
				getEnchant().display(2);
			}
			if (qualList.size() > 0) {
				Print.println(TrawelColor.STAT_HEADER+"Qualities:");
				for (WeaponQual wq: qualList) {
					Print.println(" " +TrawelColor.TIMID_GREEN+wq.name +TrawelColor.PRE_WHITE+": "+wq.desc);
				}
			}
			Print.println(TrawelColor.STAT_HEADER+"Tested Equity DPI:");
			Print.println(TrawelColor.ITEM_DESC_PROP+" Highest"+TrawelColor.PRE_WHITE+": "+TrawelColor.ITEM_WANT_LOWER+Print.F_WHOLE.format(scoreHighestContribution()/expectedAverage)+TrawelColor.PRE_WHITE+"% of perfect equity");
			Print.println(TrawelColor.ITEM_DESC_PROP+" Lowest"+TrawelColor.PRE_WHITE+": "+TrawelColor.ITEM_WANT_HIGHER+Print.F_WHOLE.format(scoreLowestContribution()/expectedAverage)+TrawelColor.PRE_WHITE+"% of perfect equity");
			Print.println(TrawelColor.STAT_HEADER+"Raw Untested Attacks"+TrawelColor.PRE_WHITE+":");
			WeaponAttackFactory.getStance(this.weap).display(this);
			;break;
		}
	}
	@Override
	public void display(int style) {
		this.display(style, 1);
	}
	
	@Override
	public String storeString(double markup, int canShow) {//for stores brief overview
		if (canShow > 0) {
			return this.getName() 
					+ TrawelColor.ITEM_DESC_PROP+" ic/wa"+TrawelColor.PRE_WHITE+": " +TrawelColor.ITEM_WANT_HIGHER+Print.formatPerSubOne(this.scoreImpact())
					+"/"+Print.format(this.scoreWeight())
					+ TrawelColor.ITEM_DESC_PROP+ " cost"+TrawelColor.PRE_WHITE+": " + TrawelColor.ITEM_VALUE+Print.F_WHOLE.format(Math.ceil(getAetherValue()*markup))
						+ (canShow == 1 ? TrawelColor.TIMID_RED+" (raw deal)" : "");
		}
		String base = getBaseName();
		return TrawelColor.TIMID_GREY+"  They refuse to show you something you think " + Print.pluralIsA(base) + " "+base+".";
	}

	@Override
	public ItemType getType() {
		return Item.ItemType.WEAPON;
	}

	public String getMaterialName() {
		return MaterialFactory.getMat(material).name;
	}

	public int getKills() {
		return kills;
	}

	public void addKill() {
		this.kills++;
	}

	@Override
	public Material getMat() {
		return MaterialFactory.getMat(material);
	}
	
	public boolean isKeen() {
		if (enchant != null) {
			return enchant.isKeen();
		}
		return false;
	}
	
	public void deEnchant() {
		enchant = null;
		updateStats();
	}

	public void forceEnchantHit() {
		enchant = new EnchantHit(getEnchantMult(),false);
		updateStats();
	}
	
	public void forceEnchantHitElemental() {
		enchant = new EnchantHit(getEnchantMult(),true);
		updateStats();
	}
	
	public static Weapon genMidWeapon(int newLevel) {
		return new Weapon(newLevel);
		/*Weapon[] arr = new Weapon[3];
		arr[2] = new Weapon(newLevel);
		arr[1] = new Weapon(newLevel);
		arr[0] = new Weapon(newLevel);
		double highest = 0;
		double lowest = 99999;
		for (Weapon w: arr) {
			if (w.score() > highest) {
				highest = w.score();
			}
			if (w.score() < lowest) {
				lowest = w.score();
			}
		}
		for (Weapon w: arr) {
			if (w.score() != highest && w.score()  != lowest) {
				return w;
			}
		}
		return arr[0];*/
	}
	
	/**
	 * will only try a max of 5 times
	 * @param quals
	 * @param maxAdded
	 * @return
	 */
	private int addQuals(Set<WeaponQual> quals, int maxAdded) {
		if (quals.isEmpty()) {
			return 0;
		}
		int added = 0;
		//will only try a max of 5 times
		for (int i = 0; i < 5;i++) {
			if (added >= maxAdded) {
				return added;
			}
			WeaponQual wq = Rand.randCollection(quals);
			if (!qualList.contains(wq)) {
				qualList.add(wq);
				added++;
			}
		}
		return added;
	}
	
	@Override
	public int temperNegQuality(int amount) {
		int removed = 0;
		for (int i = 0; i < amount; i++) {
			int worstAmount = 0;
			WeaponQual worstQual = null;
			for (WeaponQual q: qualList) {
				if (q.goodNegNeut < worstAmount) {
					worstAmount = q.goodNegNeut;
					worstQual = q;
				}
			}
			if (worstAmount < 0) {
				qualList.remove(worstQual);
				removed++;
			}else {
				return removed;
			}
		}
		return removed;
	}
	
	@Override
	public int improvePosQuality(int amount) {
		return addQuals(weap.qList,amount);
	}
	

	public void transmuteWeapType(WeaponType newt) {
		weap = newt;//MAYBELATER?
		updateStats();
	}
	
	public void transmuteWeapMat(Material m) {
		material = m.curNum;
		updateStats();
	}
	
	public void transmuteWeapMat(int i) {
		material = i;
		updateStats();
	}
	
	@Override
	public void updateStats() {
		super.updateStats();
		refreshBattleScore();
	}
	
	public static void rarityMetrics() throws FileNotFoundException {
		/*
		final int attempts = 1_000_000;
		PrintWriter writer = new PrintWriter("rmetrics.csv");
		//List<Weapon> weaponList = new ArrayList<Weapon>();
		HashMap<String,Integer> weaponCount = new HashMap<String,Integer>();
		HashMap<Integer,Integer> materialCount = new HashMap<Integer,Integer>();
		HashMap<String,Integer> combCount = new HashMap<String,Integer>();
		double battleTotal = 0;
		for (int i = 0; i < attempts;i++) {
			//weaponList.add(Weapon.genMidWeapon(1));
			Weapon weap = genMidWeapon(1);
			weaponCount.put(weap.getBaseName(), weaponCount.getOrDefault(weap.getBaseName(),0)+1);
			materialCount.put(weap.material, materialCount.getOrDefault(weap.material,0)+1);
			String temp = weap.material +weap.getBaseName();
			combCount.put(temp, combCount.getOrDefault(temp,0)+1);
			battleTotal+=weap.score();
			//weaponList.add(weap);
		}
		battleTotal/=attempts;
		extra.println("total score: "+battleTotal);
		writer.write(",");
		for (String str: Weapon.weaponTypes) {
			writer.write(str+",");
			extra.println(str+": "+weaponCount.getOrDefault(str,0));
		}
		writer.write("\n");
		for (Material m: MaterialFactory.matList) {
			if (!m.weapon) {
				continue;
			}
			writer.write(m.name+",");
			extra.println(m.name+": "+materialCount.getOrDefault(m.curNum,0));
			for (String str: Weapon.weaponTypes) {
				writer.write(combCount.getOrDefault(m.curNum+str,0)+",");
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
		extra.println("---");
		*/
	}
	/***
	 * behold, a horrible performance test
	 * @throws FileNotFoundException
	 */
	public static void duoRarityMetrics() throws FileNotFoundException {
		/*
		final int trials = 100;
		final int attempts = 10_000;
		PrintWriter writer1 = new PrintWriter("rmetrics1.csv");
		PrintWriter writer2 = new PrintWriter("rmetrics2.csv");
		//List<Weapon> weaponList = new ArrayList<Weapon>();
		HashMap<String,Integer> weaponCount1 = new HashMap<String,Integer>();
		HashMap<String,Integer> materialCount1 = new HashMap<String,Integer>();
		HashMap<String,Integer> combCount1 = new HashMap<String,Integer>();
		HashMap<String,Integer> weaponCount2 = new HashMap<String,Integer>();
		HashMap<String,Integer> materialCount2 = new HashMap<String,Integer>();
		HashMap<String,Integer> combCount2 = new HashMap<String,Integer>();
		
		List<HashMap<String,Integer>> maps = Arrays.asList(weaponCount1,materialCount1,combCount1,weaponCount2,materialCount2,combCount2);
		
		for (int warmup = 0; warmup < 200;warmup++) {
			genTestWeapon(0);
			genTestWeapon(1);
		}
		extra.println("warmup complete");
		
		long[] time = {0,0};
		long[] temptime = {0,0};
		double[] battleTotal = {0,0};
		long starttime;
		int mult = 1;
		for (int j = 0; j <=trials;j++) {
			extra.println("trial " + j + " - "+ temptime[0] +" _ "+  temptime[1]);
			for (int s = 0; s <= 1; s++) {
				starttime = System.nanoTime();
				mult = s+1;
				for (int i = 0; i < attempts;i++) {
					Weapon weap = genTestWeapon(s);
					maps.get((1*mult)-1).put(weap.getBaseName(), maps.get((1*mult)-1).getOrDefault(weap.getBaseName(),0)+1);
					maps.get((2*mult)-1).put(weap.getMaterialName(), maps.get((2*mult)-1).getOrDefault(weap.getMaterialName(),0)+1);
					String temp = weap.getMaterialName() +weap.getBaseName();
					maps.get((3*mult)-1).put(temp, maps.get((3*mult)-1).getOrDefault(temp,0)+1);
					battleTotal[s]+=weap.score();
				}
				temptime[s]=System.nanoTime()-starttime;
				time[s] += temptime[s];
			}
		}
		battleTotal[0]/=attempts;
		battleTotal[1]/=attempts;
		extra.println("total score 1: "+battleTotal[0]);
		extra.println("total score 2: "+battleTotal[1]);
		extra.println("old way took: " + time[0]/1_000_000_000 + " total");
		extra.println("squid way took: " + time[1]/1_000_000_000 + " total");
		for (int s = 0; s <= 1; s++) {
			mult = s+1;
			PrintWriter writer = s == 0 ? writer1 : writer2;
			writer.write(",");
			extra.println("starting " + (s+1));
			for (String str: Weapon.weaponTypes) {
				writer.write(str+",");
				extra.println(str+": "+maps.get((1*mult)-1).getOrDefault(str,0));
			}
			writer.write("\n");
			for (Material m: MaterialFactory.matList) {
				if (!m.weapon) {
					continue;
				}
				writer.write(m.name+",");
				extra.println(m.name+": "+maps.get((2*mult)-1).getOrDefault(m.name,0));
				for (String str: Weapon.weaponTypes) {
					writer.write(maps.get((3*mult)-1).getOrDefault(m.name+str,0)+",");
				}
				writer.write("\n");
			}
			writer.flush();
			writer.close();
		}
		extra.println("---");
		MaterialFactory.materialWeapDiag();
		*/
	}

	public boolean canDisplay() {
		return weap != WeaponType.NULL_WAND;
	}

	
}
