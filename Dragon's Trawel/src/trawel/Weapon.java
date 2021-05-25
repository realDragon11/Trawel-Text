package trawel;

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
	private double baseEnchant;//how good is the material at receiving enchantments?
	private EnchantConstant enchant;
	private boolean IsEnchantedConstant = false;
	private EnchantHit enchantHit;
	//private boolean IsEnchantedHit = false;//likely unused
	//private Stance martialStance = new Stance(); //what attacks the weapon can use
	
	//the multipiers of what material is good at dealing what types of damage
	private double sharpMult;
	private double bluntMult;
	private double pierceMult;
	
	
	private String weapName;
	private String material;
	private int cost;
	//private int level;
	private int effectiveCost;
	private int kills;
	
	private Material mat;
	private DamTuple dam;
	
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
		sharpMult = mat.sharpMult;
		bluntMult = mat.bluntMult;
		pierceMult = mat.pierceMult;
		weight *= mat.weight;
		cost = (int) mat.cost;
		//choosing the type of weapon
		weapName = weaponName;
		kills = 0;
		//level scaling
		/*
		sharpMult*=level;
		bluntMult*=level;//TODO: reincoperate
		pierceMult*=level;
		*/
		//cost*=level;
		
		
		switch (weapName) {
		case "longsword":
		cost *= 1;
		weight *=2;
		;break;
		case "broadsword":
		cost *= 2;
		weight *=3;
		;break;
		case "mace":
		cost *= 2;
		weight *=3;
		;break;
		case "spear":
		cost *= 1;
		weight *=2;
		;break;
		case "axe":
		cost *= 1;
		weight *=2;
		;break;
		case "rapier":
		cost *= 2;
		weight *=3;//I think rapiers were heavy? The blunt damage doesn't really reflect this though.
		;break;
		case "dagger":
		cost *= .7;
		weight *=1;
		;break;
		case "claymore":
		cost *= 3;
		weight *=5;
		;break;
		case "lance":
		cost *= 2;
		weight *=3;
		;break;
		case "shovel":
		cost *= .8;
		weight *=2;	
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
	}
	
	public Weapon(int newLevel) {
		this(newLevel,MaterialFactory.randMat(false,true),(String)extra.choose("longsword","broadsword","mace","spear","axe","rapier","dagger",extra.choose("claymore","lance","shovel")));
	}
	public Weapon(int newLevel, String weaponName) {
		this(newLevel,MaterialFactory.randMat(false,true),weaponName);
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
			bs+=Combat.handleTestAttack(holdAttack.impair(10,TargetType.HUMANOID,this),extra.randList(WorldGen.getDummyInvs()),Armor.armorEffectiveness).damage;
			}
			i++;
		}
		bs/=battleTests;
		dam = new DamTuple(high,average,bs/size);
		return dam;
	}
	
	public static final int battleTests = 20;
	
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
		case 1:
			extra.println(this.getName()
			+ " hd/ad/bs: " + extra.format(this.highestDamage().highest) + "/" + extra.format(this.highestDamage().average)+"/"+extra.format2(this.highestDamage().battleScore)+" value: " + (int)(this.getCost()*markup));
			if (this.IsEnchantedConstant()) {
				this.getEnchant().display(1);
			}
			if (this.isEnchantedHit()) {
				this.getEnchantHit().display(1);
			}
			;break;
		case 2:
			extra.println(this.getName()
			+ " hd/ad/bs: " + extra.format(this.highestDamage().highest) + "/" + extra.format(this.highestDamage().average)+ "/"+extra.format2(this.highestDamage().battleScore)+" value: " + (int)(this.getCost()*markup) + " kills: " +this.getKills());
			if (this.IsEnchantedConstant()) {
				this.getEnchant().display(2);
			}
			if (this.isEnchantedHit()) {
				this.getEnchantHit().display(2);
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
	
}
