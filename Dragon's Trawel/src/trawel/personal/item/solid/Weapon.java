package trawel.personal.item.solid;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import trawel.Services;
import trawel.WorldGen;
import trawel.extra;
import trawel.battle.Combat;
import trawel.battle.Combat.ATK_ResultCode;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.Stance;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.battle.attacks.TargetFactory.TargetType;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.Item.ItemType;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.magic.EnchantHit;

/**
 * 
 * @author dragon
 * before 2/11/2018
 * A Weapon is an Item.
 * It is made up of a certain material, and of a certain type. It my also have an Enchantment of some sort.
 * Different materials have different properties, different types have different attacks, which
 * are stored in the weapon's stance elsewhere.
 */

public class Weapon extends Item {

	private static final long serialVersionUID = 1L;
	
	private Enchant enchant;

	private WeaponType weap;//TODO: make this an enum or byte or something
	private int material;
	private int cost;//probably also remove cost and weight like I did for armor
	//FIXME: needs to be changed to account for the aether/money divide
	private int weight;

	private int kills;
	private float bsCon, bsIpt, bsAvg, bsWgt;//these don't need to update for internal weapons
	
	public List<WeaponQual> qualList = new ArrayList<WeaponQual>();
	
	public enum WeaponQual{
		DESTRUCTIVE("Destructive","On Damage: Destroys 33% * percent of health damage dealt local armor."),
		PENETRATIVE("Penetrative","Attack: The locally attacked armor counts for 3/5ths as much."),
		PINPOINT("Pinpoint","Attack: Armor not in slots you are attacking counts for 2/3rds as much."),
		RELIABLE("Reliable","On Armor Block: Deals damage equal to the weapon level instead. Counts as being blocked by armor."), 
		DUELING("Dueling","Attack: In large fights, attack the same opponent repeatedly."),
		WEIGHTED("Weighted","On Damage: Less accurate attacks deal more damage."),
		REFINED("Refined","On Damage: Deals bonus damage equal to weapon level."),
		ACCURATE("Accurate","Attack: Flat +10% accuracy bonus to all attacks."),
		CARRYTHROUGH("Carrythrough","On Miss/Dodge: Your next attack on another target is 10% quicker."),
		;
		public String name, desc;
		WeaponQual(String name,String desc) {
			this.name = name;
			this.desc = desc;
		}
	}
	
	public enum WeaponType{
		LONGSWORD("longsword","longsword"),BROADSWORD("broadsword","broadsword"),MACE("mace","mace"),SPEAR("spear","spear"),AXE("axe","small_axe"),RAPIER("rapier","rapier")
		,DAGGER("dagger","dagger"),CLAYMORE("claymore","claymore"),LANCE("spear","spear"),
		SHOVEL("shovel","shovel"),TEETH_GENERIC("teeth",null),
		REAVER_STANDING("clawed feet",null),
		CLAWS_TEETH_GENERIC("teeth and claws",null), BRANCHES("branches",null), 
		GENERIC_FISTS("fists",null), UNICORN_HORN("horn",null), TALONS_GENERIC("talons",null),
		FISH_SPEAR("fishing spear","spear"), FISH_ANCHOR("anchor","claymore");
		
		private final String name, legacysprite;
		WeaponType(String _name, String _legacysprite) {
			name = _name;
			legacysprite = _legacysprite;
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
		weight *= materia.weight;
		cost = (int) materia.cost;
		//choosing the type of weapon
		weap = weapon;
		kills = 0;
		
		//DOLATER: convert to enum or some other method
		switch (weap) {
		case LONGSWORD:
			cost *= 1;
			weight *=2;
			cost *= 1+ 0.1 * addQuals(WeaponQual.RELIABLE,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case BROADSWORD:
			cost *= 2;
			weight *=3;
			cost *= 1+ 0.1 * addQuals(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case MACE:
			cost *= 2;
			weight *=3;
			cost *= 1+ 0.1 * addQuals(WeaponQual.DESTRUCTIVE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED);
			;break;
		case SPEAR:
			cost *= 1;
			weight *=2;
			cost *= 1+ 0.1 * addQuals(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case AXE:
			cost *= 1;
			weight *=2;
			cost *= 1+ 0.1 * addQuals(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case RAPIER:
			cost *= 2;
			weight *=3;//I think rapiers were heavy? The blunt damage doesn't really reflect this though.
			cost *= 1+ 0.1 * addQuals(WeaponQual.PINPOINT,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case DAGGER:
			cost *= .7;
			weight *=1;
			cost *= 1+ 0.1 * addQuals(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case CLAYMORE:
			cost *= 3;
			weight *=5;
			cost *= 1+ 0.1 * addQuals(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED);
			;break;
		case LANCE:
			cost *= 2;
			weight *=3;
			cost *= 1+ 0.1 * addQuals(WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
			;break;
		case SHOVEL:
			cost *= .8;
			weight *=2;
			cost *= 1+ 0.1 * addQuals(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED);
			;break;


	//not normal weapons start
		case TEETH_GENERIC:
			cost *= 1;
			weight *=0;
			;break;
		case REAVER_STANDING:
			cost *= 1;
			weight *=0;	
			;break;
		case CLAWS_TEETH_GENERIC:
			cost *= 1;
			weight *=0;	
			;break;
		case BRANCHES:
			cost *= 1;
			weight *=0;	
			;break;
		case GENERIC_FISTS:
			cost *= 1;
			weight *=0;
			;break;
		case UNICORN_HORN:
			cost *= 3;
			weight *=0;	
			;break;
		case TALONS_GENERIC:
			cost *= 1;
			weight *=0;	
			;break;
		case FISH_SPEAR:
			cost *= .2f;
			weight *=1;
			;break;
		case FISH_ANCHOR:
			cost *= 1;
			weight *=5;
			;break;
		}
		//random chance, partially based on enchantment power, to enchant the weapon
		if (getEnchantMult() > extra.randFloat()*3f) {
			if (extra.chanceIn(2, 3)) {
				enchant = EnchantConstant.makeEnchant(getEnchantMult(),cost);
			}else {
				enchant = new EnchantHit(getEnchantMult());
			}
		}
		if (materia != MaterialFactory.getMat("bone")) {
			refreshBattleScore();//TODO: make a better way to realize we're not gonna need battlescore
		}
		
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
	public Weapon(int newLevel, String weaponName) {
		this(newLevel,MaterialFactory.randWeapMat(),randWeapType());
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
	public String getName() {
		String weapName = weap.getName();
		Material mat = MaterialFactory.getMat(material);
		if (this.isEnchantedConstant()){
			EnchantConstant conste = ((EnchantConstant)enchant);
		return (getModiferName() + " " +conste.getBeforeName() +mat.color+ mat.name + "[c_white] " +  weapName + conste.getAfterName());}
		if (this.isEnchantedHit()){
			;
			if (isKeen()) {
				return (getModiferName() + " " + ((EnchantHit)enchant).getName() + mat.color+mat.name  + "[c_white] " + weapName);
			}else {
			return (getModiferName() +" "+mat.color+ mat.name + "[c_white] " +  weapName + ((EnchantHit)enchant).getName());}
			
		}
			return (getModiferName() + " " +mat.color+ mat.name  + "[c_white] " + weapName);
	}
	
	
	/**
	 * Get the weight of the item
	 * @return weight (int)
	 */
	public int getWeight() {
		return (int) (weight*Inventory.TEMP_WEIGHT_MULT);
	}
	
	/**
	 * get the cost of the item
	 * @return cost (int)
	 */
	@Override
	public int getAetherValue() {
		if (this.isEnchantedConstant()) {
			return (int) (level*cost * enchant.getGoldMult()+enchant.getGoldMod());
		}
		if (this.isEnchantedHit()) {
			return (int) (level*cost * enchant.getGoldMult());
		}
		return cost*level;
	}
	
	/**
	 * get the base cost of the item
	 * @return base cost (int)
	 */
	public int getBaseCost() {
		return cost*level;
	}

	
	/**
	 * Swap out the current enchantment for a new one, if a better one is generated.
	 * Returns if a better one was generated or not.
	 * @param level (int)
	 * @return changed enchantment? (boolean)
	 */
	public boolean improveEnchantChance(int level) {
		if (this.isEnchantedConstant()) {
			Enchant pastEnchant = enchant;
			enchant = Services.improveEnchantChance(enchant, level, getEnchantMult());
			//effectiveCost=(int) extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
			return pastEnchant != enchant;
		}else {
			//IsEnchantedConstant = true;
			enchant = EnchantConstant.makeEnchant(getEnchantMult(),cost);//new EnchantConstant(level*baseEnchant);
			//effectiveCost=(int) extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
			return true;
		}
	}
	
	/**
	 * @return the baseName (String)
	 */
	public String getBaseName() {
		return weap.getName();
	}

	/*
	 * Returns the average damage of all the weapon's attacks. Factors in speed and damage, but not aiming.
	 *  - average (int)
	 */
	/*public double averageDamage() {
		double average = 0;
		int size = this.getMartialStance().getAttackCount();
		int i = 0;
		Attack holdAttack;
		//does not account for aiming, since that is *very* opponent dependent
		while (i < size) {
			holdAttack = this.getMartialStance().getAttack(i);
			average +=holdAttack.getHitmod()*(holdAttack.getBlunt()+holdAttack.getPierce()+holdAttack.getSharp())/holdAttack.getSpeed();
			i++;
		}
		
		
		return (int)(average/size);
	}*/
	
	private void refreshBattleScore() {
		int impactChance = 0;
		double total = 0;
		double weighted = 0;
		//double highestContrib = -10;
		Stance stance = this.getMartialStance();
		int size = stance.getAttackCount();
		double[] contributions = new double[size];//used for determining highest contribution
		//does not account for aiming, since that is *very* opponent dependent
		for (int i = size-1;i >=0;i--) {
			Attack holdAttack = stance.getAttack(i);
			int dam = 0;
			for (int j = WorldGen.getDummyInvs().size()-1; j >=0;j--) {
				//double thisaverage = 0;
				//double thishighest = -10;
				
				for (int ta = 0; ta < battleTests;ta++) {
					AttackReturn ret = Combat.handleTestAttack(holdAttack.impair(null,this,null)
							,WorldGen.getDummyInvs().get(j)
							,Armor.armorEffectiveness);
					//double thisret = ;
					dam+= ret.damage/holdAttack.getSpeed();//thisret;
					/*
					thisaverage += thisret;
					if (thisret > thishighest) {
						thishighest = thisret;
					}*/
					if (ret.code == ATK_ResultCode.DAMAGE) {
						impactChance++;
					}
				}
				/*
				if (thishighest/thisaverage > highestContrib) {
					highestContrib = thishighest/thisaverage;
				}
				weighted += thisaverage * stance.getWeight(i);
				average+=thisaverage;*/
			}
			contributions[i] = dam;
			total += dam;
			weighted += dam*stance.getWeight(i);
		}
		double highest = 0;
		for (int h = size-1; h >= 0 ;h--) {
			if (highest < contributions[h]) {
				highest = contributions[h];
			}
		}
		highest /= total;
		
		int subTests = battleTests*WorldGen.getDummyInvs().size();
		int totalTests = size*subTests;
		double levelAdjust = level/10.0;
		//the above battlescore assumes level 10 power weapon on level 10 armor
		//so we put the real level in now
		this.bsCon = (float)(highest);//(float) (high*level);
		this.bsAvg = (float)((levelAdjust*total)/totalTests);//(float) (average*level);
		this.bsIpt = impactChance/(float)totalTests;
		this.bsWgt = (float) ((levelAdjust*weighted)/subTests);
	}
	
	public static int battleTests = 10;//now how many times each attack gets tested on each armor set 3 to 5
	
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
	
	public double scoreHighestContribution() {
		return this.bsCon;
	}
	
	public double scoreImpact() {
		return this.bsIpt;
	}
	
	public double scoreWeight() {
		return this.bsWgt;
	}
	public double scoreAverage() {
		return this.bsAvg;
	}

	@Override
	public void display(int style,float markup) {
		switch (style) {
		case 0: extra.println(getMaterialName() +" "+getBaseName()+":"+extra.format(this.scoreWeight()));
		break;
		case 1:
			extra.println(this.getName()
			+ " ic/ad/wa: " + extra.formatPerSubOne(this.scoreImpact())
			+ "/" + extra.format(this.scoreAverage())
			+"/"+extra.format(this.scoreWeight())
			+" aether: " + (int)(this.getAetherValue()*markup));
			
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
		case 2:
			extra.println(this.getName()
			+ " highest contribution: " + extra.formatPerSubOne(this.scoreHighestContribution())
			+ " impact chance: " + extra.formatPerSubOne(this.scoreImpact())
			+ " average damage: " + extra.format(this.scoreAverage())
			+ " weighted average damage: " + extra.format(this.scoreWeight())
			+" aether: " + (int)(this.getAetherValue()*markup)
			+ " kills: " +this.getKills());
			
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
		case 3://for stores in depth
			extra.println(this.getName()
					+ " ic/ad/wa: " + extra.formatPerSubOne(this.scoreImpact())
					+ "/" + extra.format(this.scoreAverage())
					+"/"+extra.format(this.scoreWeight())
					+" value: " + extra.F_WHOLE.format(Math.ceil(this.getMoneyValue()*markup)));
					
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
	public String storeString(float markup, boolean canShow) {//for stores brief overview
		if (canShow) {
			return this.getName() 
					+ " ic/ad/rw: " + extra.formatPerSubOne(this.scoreImpact())
					+ "/" + extra.format(this.scoreAverage())
					+"/"+extra.format(this.scoreWeight())
				+ " cost: " +  extra.F_WHOLE.format(Math.ceil(getMoneyValue()*markup))
				;
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
	
	@Override
	public void levelUp() {
		level++;
		//dam = null;
		refreshBattleScore();
	}
	
	public boolean isKeen() {
		if (this.isEnchantedHit()) {
			return ((EnchantHit)enchant).isKeen();
		}
		return false;
	}
	
	public void deEnchant() {
		enchant = null;
	}

	public void forceEnchantHit(int i) {
		this.enchant = new EnchantHit(true,getEnchantMult());
		
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
	public static Weapon genMidWeapon(int newLevel,String name) {
		return new Weapon(newLevel,name);
		/*Weapon[] arr = new Weapon[3];
		arr[2] = new Weapon(newLevel,name);
		arr[1] = new Weapon(newLevel,name);
		arr[0] = new Weapon(newLevel,name);
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
	
	private int addQuals(WeaponQual ...quals) {
		List<WeaponQual> wqs = Arrays.asList(quals);
		int added = 0;
		for (int i = 0; i < 5;i++) {
			if (added >= 3) {
				return added;
			}
			WeaponQual wq = extra.randList(wqs);
			if (!this.qualList.contains(wq)) {
				qualList.add(wq);
				added++;
			}
		}
		return added;
	}
	

	public void transmuteWeapType(WeaponType newt) {
		weap = newt;//DOLATER?
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

	
}
