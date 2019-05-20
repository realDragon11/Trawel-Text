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

	//instance variables
	private int weight = 1;//how much does it weigh?
	private double baseEnchant;//how good is the material at receiving enchantments?
	private EnchantConstant enchant;
	private boolean IsEnchantedConstant = false;
	private EnchantHit enchantHit;
	//private boolean IsEnchantedHit = false;//likely unused
	private Stance martialStance = new Stance(); //what attacks the weapon can use
	
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
		martialStance.addAttack(new Attack("slash",1.5,100.0,40*sharpMult,5*bluntMult,0*pierceMult,"X` slashes at Y` with their Z`!",1,"sharp"));
		martialStance.addAttack(new Attack("stab",1.0,90.0,1*sharpMult,2*bluntMult,20*pierceMult,"X` stabs at Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.4,60.0,1*sharpMult,4*bluntMult,20*pierceMult,"X` thrusts at Y` with their Z`!",2,"pierce"));
		martialStance.addAttack(new Attack("pommel",1,110.0,0*sharpMult,12*bluntMult,0*pierceMult,"X` hits Y` with the pommel of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.1,100.0,0*sharpMult,10*bluntMult,0*pierceMult,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		;break;
		case "broadsword":
		cost *= 2;
		weight *=3;
		martialStance.addAttack(new Attack("slash",1.3,110.0,35*sharpMult,8*bluntMult,0*pierceMult,"X` slashes at Y` with their Z`!",1,"sharp"));
		martialStance.addAttack(new Attack("stab",1.0,110.0,2*sharpMult,3*bluntMult,10*pierceMult,"X` stabs at Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("pommel",1,110.0,0*sharpMult,12*bluntMult,0*pierceMult,"X` hits Y` with the pommel of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.2,120.0,0*sharpMult,15*bluntMult,0*pierceMult,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.5,180.0,50*sharpMult,20*bluntMult,0*pierceMult,"X` lifts their Z` over their head, and then brings it down on Y`!",2,"sharp"));
		;break;
		case "mace":
		cost *= 2;
		weight *=3;
		martialStance.addAttack(new Attack("bash",1.5,150.0,0*sharpMult,35*bluntMult,1*pierceMult,"X` bashes Y` with their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("smash",1,100.0,0*sharpMult,30*bluntMult,1*pierceMult,"X` smashes Y` with their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.5,180.0,1*sharpMult,80*bluntMult,5*pierceMult,"X` lifts their Z` over their head, and then brings it down on Y`!",2,"blunt"));
		;break;
		case "spear":
		cost *= 1;
		weight *=2;
		martialStance.addAttack(new Attack("skewer",1.1,120.0,1*sharpMult,2*bluntMult,50*pierceMult,"X` skewers Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.4,60.0,1*sharpMult,2*bluntMult,30*pierceMult,"X` thrusts at Y` with their Z`!",2,"pierce"));
		martialStance.addAttack(new Attack("pole",.6,120.0,0*sharpMult,10*bluntMult,0*pierceMult,"X` hits Y` with the pole of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("smack",1,100.0,1*sharpMult,8*bluntMult,0*pierceMult,"X` smacks Y` with the side of their Z`!",0,"blunt"));
		;break;
		case "axe":
		cost *= 1;
		weight *=2;
		martialStance.addAttack(new Attack("hack",.9,90.0,20*sharpMult,20*bluntMult,0*pierceMult,"X` hacks at Y` with their Z`!",1,"sharp"));	
		martialStance.addAttack(new Attack("slap",.8,100.0,0*sharpMult,30*bluntMult,0*pierceMult,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("heft",1,120.0,0*sharpMult,10*bluntMult,0*pierceMult,"X` hits Y` with the heft of their Z`!",0,"blunt"));
		;break;
		case "rapier":
		cost *= 2;
		weight *=3;//I think rapiers were heavy? The blunt damage doesn't really reflect this though.
		martialStance.addAttack(new Attack("slash",1.3,80.0,30*sharpMult,1*bluntMult,0*pierceMult,"X` slashes at Y` with their Z`!",1,"sharp"));
		martialStance.addAttack(new Attack("slice",.9,50.0,20*sharpMult,0*bluntMult,0*pierceMult,"X` slices up Y` with their Z`!",0,"sharp"));
		martialStance.addAttack(new Attack("stab",1.2,80.0,5*sharpMult,0*bluntMult,35*pierceMult,"X` stabs at Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.6,50.0,10*sharpMult,0*bluntMult,30*pierceMult,"X` thrusts at Y` with their Z`!",2,"pierce"));
		martialStance.addAttack(new Attack("pommel",1,110.0,0*sharpMult,10*bluntMult,0*pierceMult,"X` hits Y` with the pommel of their Z`!",1,"blunt"));
		;break;
		case "dagger":
		cost *= .7;
		weight *=1;
		martialStance.addAttack(new Attack("slash",1,60.0,20*sharpMult,2*bluntMult,0*pierceMult,"X` slashes at Y` with their Z`!",0,"sharp"));
		martialStance.addAttack(new Attack("stab",1.1,60.0,1*sharpMult,1*bluntMult,10*pierceMult,"X` stabs at Y` with their Z`!",0,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.4,60.0,1*sharpMult,2*bluntMult,20*pierceMult,"X` thrusts at Y` with their Z`!",1,"pierce"));
		martialStance.addAttack(new Attack("slap",.8,80.0,0*sharpMult,4*bluntMult,0*pierceMult,"X` slaps Y` with the side of their Z`!",0,"blunt"));
		;break;
		case "claymore":
		cost *= 3;
		weight *=5;
		martialStance.addAttack(new Attack("slash",1.4,200.0,60*sharpMult,20*bluntMult,0*pierceMult,"X` slashes at Y` with their Z`!",1,"sharp"));
		martialStance.addAttack(new Attack("stab",0.4,300.0,30*sharpMult,10*bluntMult,3*pierceMult,"X` stabs at Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("pommel",.1,300.0,0*sharpMult,15*bluntMult,0*pierceMult,"X` hits Y` with the pommel of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.3,200.0,0*sharpMult,30*bluntMult,0*pierceMult,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.4,400.0,100*sharpMult,50*bluntMult,0*pierceMult,"X` lifts their Z` over their head, and then brings it down on Y`!",2,"sharp"));
		;break;
		case "lance":
		cost *= 2;
		weight *=3;
		martialStance.addAttack(new Attack("skewer",0.5,150.0,1*sharpMult,5*bluntMult,35*pierceMult,"X` skewers Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.3,100.0,1*sharpMult,5*bluntMult,20*pierceMult,"X` thrusts at Y` with their Z`!",1,"pierce"));
		martialStance.addAttack(new Attack("smack",1,100.0,0*sharpMult,15*bluntMult,0*pierceMult,"X` smacks Y` with the side of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("charge",1.4,300.0,1*sharpMult,20*bluntMult,80*pierceMult,"X` charges forward with their Z`!",2,"pierce"));
		;break;
		case "shovel":
		cost *= .8;
		weight *=2;	
		martialStance.addAttack(new Attack("thrust",.4,60.0,2*sharpMult,30*bluntMult,3*pierceMult,"X` thrusts at Y` with their Z`!",2,"blunt"));
		martialStance.addAttack(new Attack("slap",1.2,100.0,0*sharpMult,20*bluntMult,0*pierceMult,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("pole",.6,120.0,0*sharpMult,10*bluntMult,0*pierceMult,"X` hits Y` with the pole of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("smack",1,100.0,2*sharpMult,25*bluntMult,0*pierceMult,"X` smacks Y` with the side of their Z`!",1,"blunt"));
		;break;
		case "generic teeth":
			cost *= 1;
			weight *=3;	
			martialStance.addAttack(new Attack("bite",1,100.0,5*sharpMult,0*bluntMult,35*pierceMult,"X` bites at Y` with their teeth!",2,"pierce"));
			martialStance.addAttack(new Attack("tear",1.4,140.0,20*sharpMult,0*bluntMult,20*pierceMult,"X` tears into Y`'s flesh with their teeth!",1,"pierce"));
			martialStance.addAttack(new Attack("rip",.6,120.0,30*sharpMult,0*bluntMult,30*pierceMult,"X` rips up Y`'s flesh with their teeth!",0,"pierce"));
			;break;
		/*
		case "wand":
		cost *= 2;
		weight *=1;
		martialStance.addAttack(new Attack("magic missle",2,120.0,10*sharpMult,20*bluntMult,0*pierceMult,"X` points at Y` with their Z`! They cast magic missle!"));
		martialStance.addAttack(new Attack("icicles",1.0,100.0,5*sharpMult,0*bluntMult,20*pierceMult,"X` points at Y` with their Z`! They cast icicles!"));	
		martialStance.addAttack(new Attack("fireball",1.5,150.0,0*sharpMult,40*bluntMult,0*pierceMult,"X` points at Y` with their Z`! They cast fireball!"));
		martialStance.addAttack(new Attack("curse",3,150.0,10*sharpMult,10*bluntMult,10*pierceMult,"X` points at Y` with their Z`! They cast curse!"))
		;break;
		*/
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
		return martialStance;
	}
	
	/**
	 * Returns the full name of the weapon.
	 * @return String
	 */
	public String getName() {
		if (IsEnchantedConstant){
		return (getModiferName() + " " +enchant.getBeforeName() + material + " " +  weapName + enchant.getAfterName());}
		if (this.isEnchantedHit()){
			return (getModiferName() +" "+ material + " " +  weapName + enchantHit.getName());}
			return (getModiferName() + " " + material  + " " + weapName);
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
	public double averageDamage() {
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
	}
	
	/**
	 * Returns the damage/time (ie dps) of the most powerfull attack the weapon has
	 * @return highest damage (int)
	 */
	public double highestDamage() {
		double high = 0;
		double damage = 0;
		int size = this.getMartialStance().getAttackCount();
		int i = 0;
		Attack holdAttack;
		//does not account for aiming, since that is *very* opponent dependent
		while (i < size) {
			holdAttack = this.getMartialStance().getAttack(i);
			damage = (holdAttack.getHitmod()*100*(holdAttack.getBlunt()+holdAttack.getPierce()+holdAttack.getSharp()))/holdAttack.getSpeed()*level;
			if (damage > high) {
				high = damage;
			}
			i++;
		}
		return (damage);
	}

	@Override
	public void display(int style) {
		switch (style) {
		case 1:
			extra.println(this.getName()
			+ " hd: " + extra.format(this.highestDamage()) + " value: " + this.getCost());
			if (this.IsEnchantedConstant()) {
				this.getEnchant().display(1);
			}
			if (this.isEnchantedHit()) {
				this.getEnchantHit().display(1);
			}
			;break;
		case 2:
			extra.println(this.getName()
			+ " hd: " + extra.format(this.highestDamage()) + " value: " + this.getCost() + " kills: " +this.getKills());
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
	}

	
}
