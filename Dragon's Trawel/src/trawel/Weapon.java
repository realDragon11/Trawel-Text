package trawel;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import trawel.TargetFactory.TargetType;

/**
 * 
 * @author Brian Malone
 * before 2/11/2018
 * A weapon is an item.
 * It contains a Stance, of attacks, and possibly an enchantment.
 * It is also made up of a certain material, and of a certain type.
 * Different materials have different properites, differnt types have different attacks, which
 * are stored in the weapon's stance.
 */

public class Weapon extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//instance variables
	private int weight = 1;//how much does it weigh?
	private float baseEnchant;//how good is the material at receiving enchantments?
	private EnchantConstant enchant;
	private boolean IsEnchantedConstant = false;
	private EnchantHit enchantHit;
	//private boolean IsEnchantedHit = false;//likely unused
	//private Stance martialStance = new Stance(); //what attacks the weapon can use
	
	
	private String weapName;
	private String material;
	private int cost;
	//private int level;
	private int effectiveCost;
	private int kills;
	
	private Material mat;
	private DamTuple dam;
	
	public List<WeaponQual> qualList = new ArrayList<WeaponQual>();
	
	public enum WeaponQual{
		DESTRUCTIVE("Destructive","Destroys some armor on hit."),
		PENETRATIVE("Penetrative","Ignores some local armor."),
		PINPOINT("Pinpoint","Ignores some global armor."),
		RELIABLE("Reliable","Deals damage even when blocked by armor, equal to the weapon level."), 
		DUELING("Dueling","In large fights, attack the same opponent repeatedly."),
		WEIGHTED("Weighted","Less accurate attacks deal more damage."),
		REFINED("Refined","Upon dealing damage, deals bonus damage equal to weapon level."),
		ACCURATE("Accurate","Flat accuracy bonus to all attacks."),
		CARRYTHROUGH("Carrythrough","Missing an attack makes your next attack on another target quicker."),
		;
		public String name, desc;
		WeaponQual(String name,String desc) {
			this.name = name;
			this.desc = desc;
		}
	}
	
	//constructors
	/**
	 * Standard weapon constructor. Makes a weapon of level level
	 * @param newLevel (int)
	 */
	public Weapon(int newLevel, Material materia, String weaponName) {
		
		//material = (String)bpmFunctions.choose("iron",bpmFunctions.choose("steel","silver",bpmFunctions.choose("gold","platinum",bpmFunctions.choose("adamantine","mithril"))));
		mat = materia;
		material = mat.name;
		level = newLevel;
		baseEnchant = mat.baseEnchant;
		weight *= mat.weight;
		cost = (int) mat.cost;
		//choosing the type of weapon
		weapName = weaponName;
		kills = 0;
		//cost*=level;
		
		
		switch (weapName) {
		case "longsword":
		cost *= 1;
		weight *=2;
		cost *= 1+ 0.1 * addQuals(WeaponQual.RELIABLE,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
		;break;
		case "broadsword":
		cost *= 2;
		weight *=3;
		cost *= 1+ 0.1 * addQuals(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
		;break;
		case "mace":
		cost *= 2;
		weight *=3;
		cost *= 1+ 0.1 * addQuals(WeaponQual.DESTRUCTIVE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED);
		;break;
		case "spear":
		cost *= 1;
		weight *=2;
		cost *= 1+ 0.1 * addQuals(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
		;break;
		case "axe":
		cost *= 1;
		weight *=2;
		cost *= 1+ 0.1 * addQuals(WeaponQual.RELIABLE,WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
		;break;
		case "rapier":
		cost *= 2;
		weight *=3;//I think rapiers were heavy? The blunt damage doesn't really reflect this though.
		cost *= 1+ 0.1 * addQuals(WeaponQual.PINPOINT,WeaponQual.DUELING,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
		;break;
		case "dagger":
		cost *= .7;
		weight *=1;
		cost *= 1+ 0.1 * addQuals(WeaponQual.PINPOINT,WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
		;break;
		case "claymore":
		cost *= 3;
		weight *=5;
		cost *= 1+ 0.1 * addQuals(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED);
		;break;
		case "lance":
		cost *= 2;
		weight *=3;
		cost *= 1+ 0.1 * addQuals(WeaponQual.PENETRATIVE,Weapon.WeaponQual.REFINED,Weapon.WeaponQual.ACCURATE);
		;break;
		case "shovel":
		cost *= .8;
		weight *=2;
		cost *= 1+ 0.1 * addQuals(WeaponQual.WEIGHTED,Weapon.WeaponQual.REFINED);
		;break;
		case "generic teeth":
			cost *= 1;
			weight *=3;	
			;break;
		case "standing reaver":
			cost *= 1;
			weight *=3;	
			;break;
		case "generic teeth and claws":
			cost *= 1;
			weight *=3;	
			;break;
		case "branches":
			cost *= 1;
			weight *=3;	
			;break;
		case "generic fists":
			cost *= 1;
			weight *=1;
			;break;
		case "unicorn horn":
			cost *= 3;
			weight *=3;	
			;break;
		case "generic talons":
			cost *= 1;
			weight *=3;	
			;break;
		case "fishing spear":
			cost *= 1;
			weight *=1;
			;break;
		}
		//random chance, partially based on enchantment power, to enchant the weapon
		effectiveCost = cost;
		if (baseEnchant*2 > Math.random() && extra.chanceIn(8,10)) {
		if (extra.chanceIn(2, 3)) {
		enchant = new EnchantConstant(level*baseEnchant);
		effectiveCost=(int)extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
		IsEnchantedConstant = true;}else {
			enchantHit = new EnchantHit(baseEnchant);//no level
			effectiveCost=(int)extra.zeroOut(cost * enchantHit.getGoldMult());
		}
		}
		
		//new first phase 'no bad enchants' code
		if (enchant != null) {//if there's an enchant, check to see if the weapon causes any zero's or is overwhelmingly negative
			if (effectiveCost < 2 || (enchant.getAimMod()*enchant.getDamMod()*enchant.getHealthMod()*enchant.getSpeedMod()) < .6) {
				//in this case we merely remove the enchantment entirely to avoid having to do recursive failure
				enchant = null;
				effectiveCost = cost;
				IsEnchantedConstant = false;
			}
		}
	}
	
	public Weapon(int newLevel) {
		this(newLevel,MaterialFactory.randWeapMat(),(String)extra.choose("longsword","broadsword","mace","spear","axe","rapier","dagger",extra.choose("claymore","lance","shovel")));
	}
	public Weapon(int newLevel, String weaponName) {
		this(newLevel,MaterialFactory.randWeapMat(),weaponName);
	}
	
	/***
	 * used for testing
	 * 
	 */
	public Weapon(boolean useSquid) {
		this(1,useSquid ? MaterialFactory.randWeapMat() : MaterialFactory.randMat(false, true),(String)extra.choose("longsword","broadsword","mace","spear","axe","rapier","dagger",extra.choose("claymore","lance","shovel")));
	}

	//instance methods
	
	/**
	 * Returns true if the weapon is enchanted
	 * @return isEnchantedConstant (boolean)
	 */
	public boolean IsEnchantedConstant() {
		return IsEnchantedConstant;
	}

	/**
	 * Returns the stance of the weapon.
	 * @return the martialStance (Stance)
	 */
	public Stance getMartialStance() {
		return WeaponAttackFactory.getStance(this.getBaseName());
	}
	
	/**
	 * Returns the full name of the weapon.
	 * @return String
	 */
	public String getName() {
		if (IsEnchantedConstant){
		return (getModiferName() + " " +enchant.getBeforeName() +MaterialFactory.getMat(material).color+ material + "[c_white] " +  weapName + enchant.getAfterName());}
		if (this.isEnchantedHit()){
			if (isKeen()) {
				return (getModiferName() + " " + enchantHit.getName() + MaterialFactory.getMat(material).color+material  + "[c_white] " + weapName);
			}else {
			return (getModiferName() +" "+MaterialFactory.getMat(material).color+ material + "[c_white] " +  weapName + enchantHit.getName());}
			
		}
			return (getModiferName() + " " +MaterialFactory.getMat(material).color+ material  + "[c_white] " + weapName);
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
	 * get the reference to the enchantment on the weapon
	 * @return enchant (EnchantConstant)
	 */
	public EnchantConstant getEnchant() {
		return enchant;
	}
	
	/**
	 * Swap out the current enchantment for a new one, if a better one is generated.
	 * Returns if a better one was generated or not.
	 * @param level (int)
	 * @return changed enchantment? (boolean)
	 */
	public boolean improveEnchantChance(int level) {
		if (IsEnchantedConstant) {
			EnchantConstant pastEnchant = enchant;
			enchant = Services.improveEnchantChance(enchant, level, baseEnchant);
			effectiveCost=(int) extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
			return pastEnchant != enchant;
		}else {
			IsEnchantedConstant = true;
			enchant = new EnchantConstant(level*baseEnchant);
			effectiveCost=(int) extra.zeroOut(cost * enchant.getGoldMult()+enchant.getGoldMod());
			return true;
		}
	}
	
	/**
	 * @return the baseName (String)
	 */
	public String getBaseName() {
		return weapName;
	}

	/**
	 * Returns the average damage of all the weapon's attacks. Factors in speed and damage, but not aiming.
	 * @return - average (int)
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
	
	/**
	 * Returns the damage/time (ie dps) of the most powerfull attack the weapon has
	 * @return highest damage (int)
	 */
	public DamTuple highestDamage() {
		if (dam != null) {
			return dam;
		}
		double high = 0;
		double damage = 0;
		double average = 0;
		double bs = 0;
		int size = this.getMartialStance().getAttackCount();
		int i = 0;
		Attack holdAttack;
		//does not account for aiming, since that is *very* opponent dependent
		while (i < size) {
			holdAttack = this.getMartialStance().getAttack(i);
			damage = (holdAttack.getHitmod()*100*holdAttack.getTotalDam(this))/holdAttack.getSpeed()*level;
			average +=damage/size;
			if (damage > high) {
				high = damage;
			}
			for (int j = 0; j < battleTests;j++) {
			bs+=Combat.handleTestAttack(holdAttack.impair(10,TargetType.HUMANOID,this),extra.randList(WorldGen.getDummyInvs()),Armor.armorEffectiveness).damage/holdAttack.getSpeed() ;
			}
			i++;
		}
		bs/=battleTests;
		dam = new DamTuple(high,average,(bs*level)/(size));
		return dam;
	}
	
	public static int battleTests = 50;
	
	public class DamTuple implements java.io.Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public double highest;
		public double average;
		public double battleScore;
		public DamTuple(double h, double a, double b) {
			highest = h;
			average = a;
			battleScore = b;
		}
	}

	@Override
	public void display(int style,float markup) {
		switch (style) {
		case 0: extra.println(material +" "+weapName+": "+extra.format(this.highestDamage().battleScore));
		break;
		case 1:
			extra.println(this.getName()
			+ " hd/ad/bs: " + extra.format(this.highestDamage().highest) + "/" + extra.format(this.highestDamage().average)+"/"+extra.format(this.highestDamage().battleScore)+" value: " + (int)(this.getCost()*markup));
			if (this.IsEnchantedConstant()) {
				this.getEnchant().display(1);
			}
			if (this.isEnchantedHit()) {
				this.getEnchantHit().display(1);
			}
			for (WeaponQual wq: qualList) {
				extra.println(wq.name + ": "+wq.desc);
			}
			;break;
		case 2:
			extra.println(this.getName()
			+ " hd/ad/bs: " + extra.format(this.highestDamage().highest) + "/" + extra.format(this.highestDamage().average)+ "/"+extra.format(this.highestDamage().battleScore)+" value: " + (int)(this.getCost()*markup) + " kills: " +this.getKills());
			if (this.IsEnchantedConstant()) {
				this.getEnchant().display(2);
			}
			if (this.isEnchantedHit()) {
				this.getEnchantHit().display(2);
			}
			for (WeaponQual wq: qualList) {
				extra.println(wq.name + ": "+wq.desc);
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
		return "weapon";
	}

	public String getMaterial() {
		return material;
	}

	public boolean isEnchantedHit() {
		return enchantHit != null;
	}

	public EnchantHit getEnchantHit() {
		return enchantHit;
	}

	public int getKills() {
		return kills;
	}

	public void addKill() {
		this.kills++;
	}

	public Material getMat() {
		return mat;
	}
	
	public void levelUp() {
		level++;
		dam = null;
	}
	
	public boolean isKeen() {
		if (this.isEnchantedHit()) {
			return this.enchantHit.isKeen();
		}
		return false;
	}
	
	public void deEnchant() {
		enchant = null;
		IsEnchantedConstant = false;
		enchantHit = null;
	}

	public Enchant getAnyEnchant() {
	if (isEnchantedHit()) {
		return this.getEnchantHit();
	}
	return this.getEnchant();
	}

	public void forceEnchantHit(int i) {
		this.enchant = null;
		this.enchantHit = new EnchantHit(true,baseEnchant);
		
	}

	public static double getRarity(String str) {
		switch (str) {
		case "claymore": case " lance": case "shovel":
			return (1/8.0)/3.0;
		default:
			return 1.0/8.0;
		}
	}
	
	public static Weapon genTestWeapon(int useSquid) {
		Weapon[] arr = new Weapon[3];
		boolean useS = useSquid == 1;
		arr[2] = new Weapon(useS);
		arr[1] = new Weapon(useS);
		arr[0] = new Weapon(useS);
		double highest = 0;
		double lowest = 99999;
		for (Weapon w: arr) {
			if (w.highestDamage().battleScore > highest) {
				highest = w.highestDamage().battleScore;
			}
			if (w.highestDamage().battleScore < lowest) {
				lowest = w.highestDamage().battleScore;
			}
		}
		for (Weapon w: arr) {
			if (w.highestDamage().battleScore != highest && w.highestDamage().battleScore  != lowest) {
				return w;
			}
		}
		return arr[0];
	}
	
	public static Weapon genMidWeapon(int newLevel) {
		Weapon[] arr = new Weapon[3];
		arr[2] = new Weapon(newLevel);
		arr[1] = new Weapon(newLevel);
		arr[0] = new Weapon(newLevel);
		double highest = 0;
		double lowest = 99999;
		for (Weapon w: arr) {
			if (w.highestDamage().battleScore > highest) {
				highest = w.highestDamage().battleScore;
			}
			if (w.highestDamage().battleScore < lowest) {
				lowest = w.highestDamage().battleScore;
			}
		}
		for (Weapon w: arr) {
			if (w.highestDamage().battleScore != highest && w.highestDamage().battleScore  != lowest) {
				return w;
			}
		}
		return arr[0];
	}
	public static Weapon genMidWeapon(int newLevel,String name) {
		Weapon[] arr = new Weapon[3];
		arr[2] = new Weapon(newLevel,name);
		arr[1] = new Weapon(newLevel,name);
		arr[0] = new Weapon(newLevel,name);
		double highest = 0;
		double lowest = 99999;
		for (Weapon w: arr) {
			if (w.highestDamage().battleScore > highest) {
				highest = w.highestDamage().battleScore;
			}
			if (w.highestDamage().battleScore < lowest) {
				lowest = w.highestDamage().battleScore;
			}
		}
		for (Weapon w: arr) {
			if (w.highestDamage().battleScore != highest && w.highestDamage().battleScore  != lowest) {
				return w;
			}
		}
		return arr[0];
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
	
	public static final String[] weaponTypes = new String[]{"longsword","broadsword","mace","spear","axe","rapier","dagger","claymore","lance","shovel"};
	
	public static void rarityMetrics() throws FileNotFoundException {
		final int attempts = 1_000_000;
		PrintWriter writer = new PrintWriter("rmetrics.csv");
		//List<Weapon> weaponList = new ArrayList<Weapon>();
		HashMap<String,Integer> weaponCount = new HashMap<String,Integer>();
		HashMap<String,Integer> materialCount = new HashMap<String,Integer>();
		HashMap<String,Integer> combCount = new HashMap<String,Integer>();
		double battleTotal = 0;
		for (int i = 0; i < attempts;i++) {
			//weaponList.add(Weapon.genMidWeapon(1));
			Weapon weap = genMidWeapon(1);
			weaponCount.put(weap.getBaseName(), weaponCount.getOrDefault(weap.getBaseName(),0)+1);
			materialCount.put(weap.getMaterial(), materialCount.getOrDefault(weap.getMaterial(),0)+1);
			String temp = weap.getMaterial() +weap.getBaseName();
			combCount.put(temp, combCount.getOrDefault(temp,0)+1);
			battleTotal+=weap.highestDamage().battleScore;
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
			extra.println(m.name+": "+materialCount.getOrDefault(m.name,0));
			for (String str: Weapon.weaponTypes) {
				writer.write(combCount.getOrDefault(m.name+str,0)+",");
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
		extra.println("---");
		
	}
	/***
	 * behold, a horrible performance test
	 * @throws FileNotFoundException
	 */
	public static void duoRarityMetrics() throws FileNotFoundException {
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
					maps.get((2*mult)-1).put(weap.getMaterial(), maps.get((2*mult)-1).getOrDefault(weap.getMaterial(),0)+1);
					String temp = weap.getMaterial() +weap.getBaseName();
					maps.get((3*mult)-1).put(temp, maps.get((3*mult)-1).getOrDefault(temp,0)+1);
					battleTotal[s]+=weap.highestDamage().battleScore;
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
	}
	
}
