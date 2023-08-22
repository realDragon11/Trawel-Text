package trawel.personal.item.solid;

import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.Set;
import trawel.Services;
import trawel.WorldGen;
import trawel.extra;
import trawel.battle.Combat;
import trawel.battle.Combat.ATK_ResultType;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.Stance;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.magic.EnchantHit;
import trawel.personal.people.Player;

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
	private float bsIpt = -1, bsAvg, bsWgt;//these don't need to update for internal weapons
	/**
	 * packed int
	 * <br>
	 * 1st byte = average, unused
	 * 2nd byte = highest
	 * 3rd byte = lowest
	 */
	private int bsCon = 0;
	//is lazy inited again
	
	private Set<WeaponQual> qualList = EnumSet.noneOf(WeaponQual.class);
	
	public enum WeaponQual{
		DESTRUCTIVE("Destructive","On Damage: Damages local armor by 1/3rd of total percent HP dealt."),
		PENETRATIVE("Penetrative","Attack: The locally attacked armor counts for 3/5ths as much."),
		PINPOINT("Pinpoint","Attack: Armor not in slots you are attacking counts for 2/3rds as much."),
		RELIABLE("Reliable","On Armor Reduction/Block: Minimum damage equal to 1/2th WELVL. If blocked, does not become Impactful."), 
		DUELING("Dueling","Attack: In large fights, attack the same opponent repeatedly."),
		WEIGHTED("Weighted","On Damage: Less accurate attacks deal more damage."),
		REFINED("Refined","On Damage: Deals bonus damage equal to 1/2th WELVL."),
		ACCURATE("Accurate","Attack: Flat +%.10 accuracy bonus to all attacks after all modifiers."),
		CARRYTHROUGH("Carrythrough","On Miss/Dodge: Your next attack on another target is 20% quicker."),
		;
		public String name, desc;
		WeaponQual(String name,String desc) {
			this.name = name;
			this.desc = desc;
		}
	}
	
	public int numQual() {
		return qualList.size();
	}
	
	public boolean hasQual(WeaponQual qual) {
		return qualList.contains(qual);
	}
	
	public boolean equalQuals(Weapon w) {
		return qualList.equals(w.qualList);//private bypassing lol
	}
	
	public enum WeaponType{
		LONGSWORD("longsword","longsword",3f,2,EnumSet.of(WeaponQual.RELIABLE,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		BROADSWORD("broadsword","broadsword",3.5f,3,EnumSet.of(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		MACE("mace","mace",3f,2,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,WeaponQual.CARRYTHROUGH)),
		SPEAR("spear","spear",2.5f,2,EnumSet.of(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		AXE("axe","small_axe",2.5f,2,EnumSet.of(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		RAPIER("rapier","rapier",4f,3,EnumSet.of(WeaponQual.PINPOINT,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		DAGGER("dagger","dagger",1.8f,1,EnumSet.of(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		CLAYMORE("claymore","claymore",4f,5,EnumSet.of(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED)),
		LANCE("spear","spear",4f,3,EnumSet.of(WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		SHOVEL("shovel","shovel",2.2f,2,EnumSet.of(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,WeaponQual.CARRYTHROUGH)),
		TEETH_GENERIC("teeth",null,0,0,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.PENETRATIVE,WeaponQual.DUELING,Weapon.WeaponQual.ACCURATE)),
		REAVER_STANDING("clawed feet",null,0,0),
		CLAWS_TEETH_GENERIC("teeth and claws",null,0,0,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE)),
		BRANCHES("branches",null,0,0),
		GENERIC_FISTS("fists",null,0,0),
		UNICORN_HORN("horn",null,0,0,EnumSet.of(WeaponQual.PENETRATIVE,WeaponQual.PINPOINT)),
		TALONS_GENERIC("talons",null,0,0,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.PENETRATIVE,WeaponQual.PINPOINT,Weapon.WeaponQual.ACCURATE,WeaponQual.CARRYTHROUGH)),
		FISH_SPEAR("fishing spear","spear",.5f,1),
		FISH_ANCHOR("anchor","claymore",1f,5,EnumSet.of(WeaponQual.DESTRUCTIVE,WeaponQual.WEIGHTED,WeaponQual.CARRYTHROUGH)),
		NULL_WAND("WAND",null,0,0)
		;
		
		private final String name, legacysprite;
		private final float cost, weight;
		private final Set<WeaponQual> qList;
		WeaponType(String _name, String _legacysprite, float _cost, float _weight, Set<WeaponQual> _list) {
			name = _name;
			legacysprite = _legacysprite;
			cost = _cost;
			weight = _weight;
			qList = _list;
		}
		WeaponType(String _name, String _legacysprite, float _cost, float _weight) {
			name = _name;
			legacysprite = _legacysprite;
			cost = _cost;
			weight = _weight;
			qList = EnumSet.noneOf(WeaponQual.class);
		}
		
		public String getName() {
			return name;
		}
		public String getLegacy() {
			return legacysprite;
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
		
		addQuals(weap.qList);
		//random chance, partially based on enchantment power, to enchant the weapon
		if (getEnchantMult() > extra.randFloat()*3f) {
			if (extra.chanceIn(2, 3)) {
				enchant = EnchantConstant.makeEnchant(getEnchantMult(),getBaseCost());
			}else {
				enchant = new EnchantHit(getEnchantMult());
			}
		}
		/*
		if (materia != MaterialFactory.getMat("bone")) {
			refreshBattleScore();//TODO: make a better way to realize we're not gonna need battlescore
		}*/
		
	}
	
	public static WeaponType randWeapType() {
		//FIXME needs better generation anyway, should be full stack
		return extra.choose(WeaponType.LONGSWORD,
				WeaponType.BROADSWORD,
				WeaponType.MACE,
				WeaponType.SPEAR,
				WeaponType.AXE,
				WeaponType.RAPIER,
				WeaponType.DAGGER,
				extra.choose(WeaponType.CLAYMORE,WeaponType.LANCE,WeaponType.SHOVEL));
	}
	
	public Weapon(int newLevel) {
		this(newLevel,MaterialFactory.randWeapMat(),randWeapType());
	}
	public Weapon(int newLevel, WeaponType type) {
		this(newLevel,MaterialFactory.randWeapMat(),type);
	}
	
	/***
	 * used for testing
	 */
	public Weapon(boolean useSquid) {
		this(1,MaterialFactory.randWeapMat(),randWeapType());
	}

	//instance methods
	
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
		case UNICORN_HORN: return false;//maybe make a drawbane for this
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
		if (this.isEnchantedConstant()){
			EnchantConstant conste = ((EnchantConstant)enchant);
		return (conste.getBeforeName() +mat.color+ mat.name + "[c_white] " +  weapName + conste.getAfterName());}
		if (this.isEnchantedHit()){
			;
			if (isKeen()) {
				return (((EnchantHit)enchant).getName() + mat.color+mat.name  + "[c_white] " + weapName);
			}else {
			return (mat.color+ mat.name + "[c_white] " +  weapName + ((EnchantHit)enchant).getName());}
			
		}
			return (mat.color+ mat.name  + "[c_white] " + weapName);
	}
	
	@Override
	public String getName() {
		return getModiferName() + " " + getNameNoTier();
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
		return (int) (getMat().cost*weap.cost* getUnEffectiveLevel() * (1 + .1f *qualList.size()));
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
		int impactChance = 0;
		double total = 0;
		double weighted = 0;
		Stance stance = this.getMartialStance();
		int size = stance.getAttackCount();
		double[] contributions = new double[size];//used for determining highest contribution
		for (int i = size-1;i >=0;i--) {
			Attack holdAttack = stance.getAttack(i);
			double dam = 0;
			for (int j = WorldGen.getDummyInvs().size()-1; j >=0;j--) {
				for (int ta = 0; ta < battleTests;ta++) {
					AttackReturn ret = Combat.handleTestAttack(holdAttack.impair(null,this,null)
							,WorldGen.getDummyInvs().get(j).atLevel(level)
							,Armor.armorEffectiveness);
					dam+= ret.damage/ret.attack.getTime();
					if (ret.type == ATK_ResultType.IMPACT) {
						impactChance++;
					}
				}
			}
			contributions[i] += dam;
			total += dam;
			weighted += dam*stance.getWeight(i);
		}
		//double average = 0;
		double highest = 0;
		double lowest = 1;//100%
		for (int h = size-1; h >= 0 ;h--) {
			double normed = contributions[h]/total;
			//average += normed;
			if (highest < normed) {
				highest = normed;
			}
			if (lowest > normed) {
				lowest = normed;
			}
		}
		
		int subTests = battleTests*WorldGen.getDummyInvs().size();
		int totalTests = size*subTests;
		double levelAdjust = IEffectiveLevel.unEffective(IEffectiveLevel.effective(level));//DOLATER: test
		//the above battlescore assumes equal armor
		//so we put the effective level in now to make it higher
		//TODO: unsure if the natural allowed damage increase will be enough to signal a weapon as better to a player/AI
		//int conAssembler = extra.setNthByteInInt(0b0, (int)(average*(100)), 0);
		int conAssembler = extra.setNthByteInInt(0b0, (int)(highest*(100)), 1);
		conAssembler = extra.setNthByteInInt(conAssembler, (int)(lowest*(100)), 2);
		
		this.bsCon = conAssembler;//(float) (high*level);
		this.bsAvg = (float)((levelAdjust*total)/totalTests);//(float) (average*level);
		this.bsIpt = impactChance/(float)totalTests;
		this.bsWgt = (float) ((levelAdjust*weighted)/subTests);
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
		if (bsIpt == -1) {
			refreshBattleScore();
		}
		return extra.intGetNthByteFromInt(bsCon, 1);
	}
	/**
	 * 0-100
	 */
	public int scoreLowestContribution() {
		if (bsIpt == -1) {
			refreshBattleScore();
		}
		return extra.intGetNthByteFromInt(bsCon, 2);
	}
	
	public double scoreImpact() {
		if (bsIpt == -1) {
			refreshBattleScore();
		}
		return this.bsIpt;
	}
	
	public double scoreWeight() {
		if (bsIpt == -1) {
			refreshBattleScore();
		}
		return this.bsWgt;
	}
	public double scoreAverage() {
		if (bsIpt == -1) {
			refreshBattleScore();
		}
		return this.bsAvg;
	}

	@Override
	public void display(int style,float markup) {
		switch (style) {
		//0 is quick for store quickview, not used, use storestring
		case 0: extra.println(getMaterialName() +" "+getBaseName()+":"+extra.format(this.scoreWeight()));
		break;
		case 1://used for comparing and in stores
		case 3:
			extra.println(this.getName()
			+ " ic/ad/wa: " + extra.formatPerSubOne(this.scoreImpact())
			+ "/" + extra.format(this.scoreAverage())
			+"/"+extra.format(this.scoreWeight())
			+ (Player.player.caresAboutCapacity() ? " "+extra.DISP_WEIGHT+": "+getWeight() : "")
			+(style == 1 ?
					" "+extra.DISP_AETHER+": " + (int)(this.getAetherValue()*markup) :
						" value: "+extra.F_WHOLE.format(Math.ceil(this.getMoneyValue()*markup)))
			);
			
			if (this.isEnchantedConstant()) {
				this.getEnchant().display(1);
			}
			if (this.isEnchantedHit()) {
				this.getEnchant().display(1);
			}
			for (WeaponQual wq: qualList) {
				extra.println(" " +wq.name + ": "+wq.desc);
			}
			;break;
		case 2://Appraiser/full self on stat
			//by dividing it later we implicitly mult it by 100x to get it to display as a whole number
			float expectedAverage = (1f/getMartialStance().getAttackCount());
			extra.println(this.getName() +"="
			+ " highest contribution: " +extra.F_WHOLE.format(scoreHighestContribution()/expectedAverage) +"% of equity"
			+ " lowest contribution: " +extra.F_WHOLE.format(scoreLowestContribution()/expectedAverage) +"% of equity"
			+ " impact chance: " + extra.formatPerSubOne(this.scoreImpact())
			+ " average damage: " + extra.format(this.scoreAverage())
			+ " weighted average damage: " + extra.format(this.scoreWeight())
			+" aether: " + (int)(this.getAetherValue()*markup)
			+ " infused kills: " +this.getKills());
			
			if (this.isEnchantedConstant()) {
				this.getEnchant().display(2);
			}
			if (this.isEnchantedHit()) {
				this.getEnchant().display(2);
			}
			for (WeaponQual wq: qualList) {
				extra.println(" " +wq.name + ": "+wq.desc);
			}
			;break;
		case 20://for stores in depth
			extra.println(this.getName()
					+ " ic/ad/wa: " + extra.formatPerSubOne(this.scoreImpact())
					+ "/" + extra.format(this.scoreAverage())
					+"/"+extra.format(this.scoreWeight()));
					
					
					if (this.isEnchantedConstant()) {
						this.getEnchant().display(1);
					}
					if (this.isEnchantedHit()) {
						this.getEnchant().display(1);
					}
					for (WeaponQual wq: qualList) {
						extra.println(" " +wq.name + ": "+wq.desc);
					}
			break;
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
					+ " ic/wa: " + extra.formatPerSubOne(this.scoreImpact())
					+"/"+extra.format(this.scoreWeight())
				+ " cost: " +  extra.F_WHOLE.format(Math.ceil(getMoneyValue()*markup))
				+ (canShow == 1 ? " (raw deal)" : "");
		}
		String base = getBaseName();
		return "  They refuse to show you something you think " + extra.pluralIsA(base) + " "+base+".";
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
		if (this.isEnchantedHit()) {
			return ((EnchantHit)enchant).isKeen();
		}
		return false;
	}
	
	public void deEnchant() {
		enchant = null;
		updateStats();
	}

	public void forceEnchantHit(int i) {
		this.enchant = new EnchantHit(true,getEnchantMult());
		updateStats();
	}

	public static double getRarity(String str) {
		switch (str) {
		case "claymore": case " lance": case "shovel":
			return (1/8.0)/3.0;
		default:
			return 1.0/8.0;
		}
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
	
	private int addQuals(Set<WeaponQual> quals) {
		if (quals.isEmpty()) {
			return 0;
		}
		int added = 0;
		for (int i = 0; i < 5;i++) {
			if (added >= 3) {
				return added;
			}
			WeaponQual wq = extra.randCollection(quals);
			if (!qualList.contains(wq)) {
				qualList.add(wq);
				added++;
			}
		}
		return added;
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
	
	public static final String[] weaponTypes = new String[]{"longsword","broadsword","mace","spear","axe","rapier","dagger","claymore","lance","shovel"};
	
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
