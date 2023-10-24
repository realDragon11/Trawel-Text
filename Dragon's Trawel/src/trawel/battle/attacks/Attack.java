package trawel.battle.attacks;

import derg.StringResult;
import trawel.extra;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.TargetFactory.TypeBody.TargetReturn;
import trawel.battle.attacks.WeaponAttackFactory.AttackBonus;
import trawel.battle.attacks.WeaponAttackFactory.DamageTier;
import trawel.personal.Person;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Skill;
import trawel.personal.item.solid.Weapon;

/**
 * 
 * @author dragon
 * 2/8/2018
 * 
 * An attack has speed, accuracy, damage values, and a description.
 * It it used to attack other Persons in a certain way. It should be held in a Stance.
 *
 */
public class Attack implements IAttack{

	//private transient Skill skill;//should be dictated by the holding stance now	
	
	private double hitMult, warmup, cooldown;
	private int intValues[];
	private String name, desc;
	private StringResult fluffer;//TODO handle missing and hitting and damage types and etc etc
	private Stance holdingStance;//will be used for some shared data
	private int soundStrength;
	private String soundType;//shouldn't be a string
	/**
	 * most of the time, stored in stance instead, but tactics are different
	 */
	private Skill skill_for;
	/**
	 * if true, elemental damage occurs as if SBP damage wasn't there
	 * <br>
	 * if false, it's a rider
	 * <br>
	 * if true, it's the main course
	 * <br>
	 * <br>
	 * note that this current system wouldn't be able to handle a rock and a magic rock in the same attack
	 */
	private boolean bypass;
	
	private AttackBonus rider;
	
	//constructor
	/**
	 * @param name
	 * @param desc
	 * @param fluffer
	 * @param hitMult
	 * @param intValues
	 * @param warmup
	 * @param cooldown
	 */
	public Attack(String name, String desc, StringResult fluffer, double hitMult, int[] intValues,
			double warmup, double cooldown, boolean _bypass) {
		this.name = name;
		this.desc = desc;
		this.fluffer = fluffer;
		this.hitMult = hitMult;
		this.intValues = intValues;
		this.warmup = warmup;
		this.cooldown = cooldown;
		bypass = _bypass;
		
		if (!_bypass) {
			int pierce = getPierce();
			int sharp = getSharp();
			int blunt = getBlunt();
			//priority -> blunt > sharp > pierce
			if (pierce > sharp) {
				if (pierce > blunt) {
					soundType = "pierce";
				}else {
					soundType = "blunt";
				}
			}else {
				if (sharp > blunt) {
					soundType = "sharp";
				}else {
					soundType = "blunt";
				}
			}
		}else {
			int ignite = getIgnite();
			int frost = getFrost();
			int elec = getElec();
			//priority -> blunt > sharp > pierce
			if (frost > ignite) {
				if (frost > elec) {
					soundType = "freeze";
				}else {
					soundType = "shock";
				}
			}else {
				if (ignite > elec) {
					soundType = "fire";
				}else {
					soundType = "shock";
				}
			}
		}
		
		int dam = getTotalDam();
		if (dam < DamageTier.AVERAGE.getDam()) {
			soundStrength = 0;
		}else {
			if (dam >= DamageTier.HIGH.getDam()) {
				soundStrength = 2;
			}else {
				soundStrength = 1;
			}
		}
	}
	
	//DOLATER entirely nonfunctional at this point, just remake the whole system
	/*
	@Deprecated
	public Attack(Skill skill, int mageLevel, TargetFactory.TargetType targetType) {
		//name
		//magicDesc;
		hitMod = 1;
		isMagic = true;
		sharp = 0;//fire
		blunt = 0;//shock
		pierce = 0;//ice
		this.skill = skill;
		desc = "ERROR";
		soundStrength = -1;
		double pow = extra.hrandom()*Math.log(mageLevel);
		if (skill == Skill.ELEMENTAL_MAGE) {
			speed = 100+extra.randRange(0,20)-10;
			target = null;//TargetFactory.randTarget(targetType);
			switch (extra.randRange(1,3)) {
			case 1: 
				name = extra.choose("sear","flame blast","searing shot","fireball");
				sharp = (int)((pow*.2)*100);
				magicDesc = sharp +"% fire";
				soundType = "fire";
				this.wound = extra.randList(TargetFactory.fireWounds)
				;break;
			case 2:
				name = extra.choose("shock","zap");
				blunt = (int)(extra.hrandom()*mageLevel*5);
				magicDesc = blunt + " shock";
				soundType = "shock";
				this.wound = extra.randList(TargetFactory.shockWounds);
				;break;
			case 3: 
				name = extra.choose("cone of cold","freeze","permafrost","ray of frost");
				pierce = (int)((pow)*100);
				magicDesc = pierce +"% freeze";
				soundType = "freeze";
				this.wound = extra.randList(TargetFactory.freezeWounds);
				;break;
			}
			desc = "X` casts "+name+" at Y`!";
			
		}
		if (skill == Skill.DEATH_MAGE) {
			speed = 60+extra.randRange(0,20)-10;
			target = null;//TargetFactory.randTarget(targetType);
			soundType = "wither";
			switch (extra.randRange(1,3)) {
			case 1:
			name = extra.choose("wither","halt","impair");
			sharp = (int)((pow*.5)*100);
			magicDesc = sharp + "% impairment";
			
			break;
			case 2:
				name = extra.choose("harm","damage");
				blunt = (int)(extra.hrandom()*mageLevel*3);
				magicDesc = blunt + " damage";
				;break;
			case 3:
				name = extra.choose("harm","damage");
				pierce = (int)(extra.hrandom()*mageLevel);
				magicDesc = pierce + " skill damage";
				;break;
			}
			desc = "X` casts "+name+" at Y`!";
		}
		
		if (skill == Skill.ARMOR_MAGE) {
			speed = 50+extra.randRange(0,20)-10;
			target = null;//TargetFactory.randTarget(targetType);
			name = extra.choose("repair","armorweave");
			desc = "X` casts "+name+"!";
			sharp = (int)((pow*.4)*100);
			magicDesc = sharp + "% repair";
			soundType = "armor";
		}
		if (skill == Skill.ILLUSION_MAGE) {
			speed = 60+extra.randRange(0,20)-10;
			target = null;//TargetFactory.randTarget(targetType);
			name = extra.choose("befuddle","confuse");
			desc = "X` casts "+name+"!";
			magicDesc = "";
			soundType = "confuse";
		}
		if (skill == Skill.EXECUTE_ATTACK) {
			speed = 100+extra.randRange(0,20)-10;
			target = null;//TargetFactory.randTarget(targetType);
			name = "execute";
			desc = "X` attempts to "+name+" Y`!";
			magicDesc = "";
			soundType = "sharp";
			sharp = Math.min(12,4+mageLevel);//% threshold to kill
			blunt = (int)(extra.hrandom()*mageLevel*2);
			wound = Wound.EXE_WOUND;
		}
		if (skill == Skill.DRUNK_DRINK) {
			speed = 120+extra.randRange(0,40)-20;
			target = null;//TargetFactory.randTarget(targetType);
			name = "drink";
			desc = "X` drinks from a flagon!";
			magicDesc = "+" + mageLevel + " hp";
			wound = Wound.DRINK;
		}
		if (skill == Skill.MARK_ATTACK) {
			speed = 80+extra.randRange(0,40)-20;
			target = null;//TargetFactory.randTarget(targetType);
			name = "mark";
			desc = "X` marks Y`!";
			magicDesc = "";
		}
		
		if (skill == Skill.BLOOD_SURGE) {
			speed = 100+extra.randRange(0,40)-20;
			target = null;//TargetFactory.randTarget(targetType);
			name = "blood surge";
			desc = "X` regenerates!";
			magicDesc = "";
		}
		
		if (skill == Skill.BLOOD_HARVEST) {
			speed = 140+extra.randRange(0,40)-20;
			target = null;//TargetFactory.randTarget(targetType);
			name = "blood harvest";
			desc = "X` harvests Y`!";
			magicDesc = "";
		}
	}*/
	
	//instance methods
	
	/**
	 * @return the speed(double)
	 */
	public double getSpeed() {
		return warmup+cooldown;
	}
	
	/**
	 * @return the sharp damage (int)
	 */
	@Override
	public int getSharp() {
		return IAttack.getSharpFromWeap(intValues);
	}

	/**
	 * @return the blunt damage (int)
	 */
	@Override
	public int getBlunt() {
		return IAttack.getBluntFromWeap(intValues);
	}

	/**
	 * @return the pierce damage (int)
	 */
	@Override
	public int getPierce() {
		return IAttack.getPierceFromWeap(intValues);
	}
	@Override
	public int getIgnite() {
		return IAttack.getIgniteFromWeap(intValues);
	}
	@Override
	public int getFrost() {
		return IAttack.getFrostFromWeap(intValues);
	}
	@Override
	public int getElec() {
		return IAttack.getElecFromWeap(intValues);
	}
	/**
	 * @return the desc (String)
	 */
	@Override
	public String getDesc() {
		return desc;
	}
/**
 * Returns the string of an attack, taking the format
 * "X <does something to> Y with their Z"
 * @param X - (String) Attacker name
 * @param Y - (String) Defender name
 * @param Z - (String) Weapon name
 
	public String attackStringer(String X, String Y, String Z) {
		String tempStr = desc;
		tempStr = tempStr.replace("X`",X);
		tempStr = tempStr.replace("Y`",Y + "'s " + (target != null ? target.getName() : "!!"));
		tempStr = tempStr.replace("Z`",Z+"[*]");//technically if you were to look upwards you could find the weapon, but I'm gonna put it in this way
		return tempStr;
	}
	*/
	/**
	 * @return the name (String) of the attack
	 */
	@Override
	public String getName() {
		return name;
	}
	
	@Deprecated
	public void display(int style) {
		switch (style) {
		case 0://naive style
			extra.println(
				name 
				+" rarity: " + extra.formatPerSubOne(holdingStance.getRarity(this))
				+" hit mult: " + extra.format(hitMult)
				+" warmup: " + extra.format(warmup)
				+" cooldown: " + extra.format(cooldown)
				+" Sharp: " + extra.format(getSharp())
				+" Blunt: " + extra.format(getBlunt())
				+" Pierce: " + extra.format(getPierce())
				);
			break;	
		default:
			extra.println(this.toString());
			break;
		}
		
	}
	/**
	 * note this is of the attack in a vacuum
	 */
	public double getDPI() {
		return getTotalDam()/getSpeed();
	}
	
	public void display(Weapon w) {
		float damMult = w.getUnEffectiveLevel();
		//rounding errors can occur with the dam mults, but the raw values aren't achieved anyway so it should be insignificant
		float sharp = getSharp()*damMult*w.getMat().sharpMult;
		float blunt = getBlunt()*damMult*w.getMat().bluntMult;
		float pierce = getPierce()*damMult*w.getMat().pierceMult;
		float baseDam = (sharp+blunt+pierce);
		float ignite = getIgnite()*damMult;
		float frost = getFrost()*damMult;
		float elec = getElec()*damMult;
		if (w.isEnchantedHit()) {
			//damage multiplier factored in baseDam
			ignite += w.getEnchant().getFireMod()*baseDam;
			frost += w.getEnchant().getFreezeMod()*baseDam;
			elec += w.getEnchant().getShockMod()*baseDam;
		}
		
		
		float totalDam = sharp+blunt+pierce+ignite+frost+elec;
		
		String p = extra.ITEM_DESC_PROP;
		String g = extra.ITEM_WANT_HIGHER;
		String b = extra.ITEM_WANT_LOWER;
		extra.println(
			" "+extra.STAT_HEADER+extra.cutPadLenFront(8,name)+extra.PRE_WHITE+"="
			+p+" Raw DPI:" +g+extra.cutPadLenError(5, extra.F_TWO_TRAILING.format((totalDam/getSpeed()))) 
			//+" Against Equity DPI: " + extra.F_WHOLE.format(dpi/expectedAverage)
			+p+" Rarity:" +extra.ITEM_VALUE+ extra.cutPadLenError(5, extra.formatPerSubOne(holdingStance.getRarity(this)))
			+p+" Base Accuracy:"+g+extra.cutPadLenError(5, extra.format(hitMult))
			+p+" Warmup:"+b +extra.cutPadLenError(3, extra.F_WHOLE.format(warmup))
			+p+" Cooldown:"+b + extra.cutPadLenError(3, extra.F_WHOLE.format(cooldown))
			+p+" Total Delay:"+b + extra.cutPadLenError(4, extra.F_WHOLE.format(warmup+cooldown))
			);
		extra.println(
				extra.STAT_HEADER+"  Base Damage"+extra.PRE_WHITE+"="
				+(sharp > 0 ? p+" Sharp: " +g+ extra.F_WHOLE.format(sharp) : "")
				+(blunt > 0 ? p+" Blunt: " +g+ extra.F_WHOLE.format(blunt) : "")
				+(pierce > 0 ? p+" Pierce: " +g+ extra.F_WHOLE.format(pierce) : "")
				
				+(ignite > 0 ? p+" Ignite: " +g+ extra.F_WHOLE.format(ignite) : "")
				+(frost > 0 ? p+" Frost: " +g+ extra.F_WHOLE.format(frost) : "")
				+(elec > 0 ? p+" Elec: " +g+ extra.F_WHOLE.format(elec) : "")
				);
		
	}
	
	public ImpairedAttack impair(Person attacker, Weapon weap, Person defender) {
		TargetReturn rtar;
		if (defender == null) {
			rtar = TargetFactory.TypeBody.HUMAN_LIKE.randTarget(null);//empty config
		}else {
			rtar = defender.randTarget();
		}
		
		Style s = StyleFactory.randStyle();
		
		return new ImpairedAttack(this,rtar,s,weap,attacker,defender);
	}
	
	public ImpairedAttack impairTactic(Person attacker, Person defender) {
		return new ImpairedAttack(this,attacker,defender);
	}

	public int getSoundIntensity() {
		return soundStrength;
	}

	public String getSoundType() {
		return soundType;
	}
	
	//.0f = float/double with no decimal places. Weird that it can't auto convert, but oh well
	//UPDATE welp can't make them look nicer so just made them all ints anyways
	//MAYBELATER: display the actual wound name every time, but also have a stringfluffer describe it
	public enum Wound{//TODO: make sure the reworked wounds are fully in
		//special
		ERROR("ERROR","ERROR","ERROR"),
		NEGATED("Negated","No effect, resists.","Negated..."),
		EMPTY("Grazed","No bonus.","The blow's a graze..."),
		//normal wound start
		HAMSTRUNG("Hamstrung","Delays the defender's next attack by %1$d instants.","Their leg is hamstrung!"), 
		BLINDED("Blinded","Inflicts %1$d%% inaccuracy on the current attack, or half that to the next set of attack choices as compounding inaccuracy.","They can't see!"),
		CONFUSED("Confused","Forces the defender to retarget.","They look confused!"), 
		DIZZY("Dizzy","Inflicts %1$d%% inaccuracy to the defender's current action, or compounding inaccuracy to the next set of attacks.","They look dizzy!"),
		SLICE("Slice","Attacker's next action will happen %1$d%% quicker and be %2$d%% more accurate.","They are sliced!"),
		DICE("Dice","Attacker's next action will happen %1$d%% and %2$d instants sooner.","They are diced!"),
		WINDED("Winded","Defender's action will take %1$d instants longer.","The wind is knocked out of them!"),
		BLEED("Cut","Applies %1$d stacks of bleed, around %2$d damage per tick.","They bleed..."),
		MAJOR_BLEED("Lacerated","Applies %1$d stacks of bleed, around %2$d damage per tick, and prevents bleed from healing.","An artery is cut!"),
		I_BLEED("Fractured","Applies a stacking %2$d bleed, expected %1$d for this stack against this attacker.","Their insides get crushed!"),
		I_BLEED_WEAK("Trauma","Applies a stacking %2$d bleed, expected %1$d for this stack against this attacker.","Their insides get smashed."),
		DISARMED("Disarm","Defender loses one attack choice on next action.","Their attack is put off-kilter!"),		
		TRIPPED("Tripped","Defender's action will take %1$d instants longer.","They are tripped!"),
		KO("Knockout","Deals %1$d direct damage, but defender heals after their next attack.","It's a knockout!"),
		HACK("Hack","Deals up to %1$d direct damage based on unblocked damage to defender.","It's a wicked hack!"),
		TAT("Punctured","Deals up to %1$d direct damage, based on final pierce damage to defender and to-hit.","The blow goes right through them!"),
		CRUSHED("Crushed","Deals %1$d direct damage.","They are crushed!"),
		
		//elemental
		SCALDED("Scalded","Deals %1$d direct damage.","They are scalded by the flames!"),//TODO: more elemental wounds
		BLACKENED("Blackened","Burns defender's armor by %1$d%%.","Their armor burns!"),
		SCREAMING("Screaming","Defender loses one attack choice on next action.","They scream!"),
		FROSTED("Frosted","Defender's action takes %1$d%% longer on the current time, up to %2$d instants increase.","They are frozen over..."),
		FROSTBITE("Frostbite","Deals %1$d direct damage.","Their flesh is frozen!"),
		JOLTED("Jolted","Defender's action takes %2$d instants longer.","They are jolted!"),
		SHIVERING("Shivering","Applies %1$d%% compounding inaccuracy to the defender's next set of attacks.","They shiver at the intense cold..."),
		
		//exotic
		TEAR("Tear","Decreases defender's dodge by %1$%d%%, stacking.","Their wing is torn!"), //see if need to add a '%'
		MANGLED("Mangled","Halves the condition of the targeted body part.","Their body is mangled!"),
		BLOODY("Bloody","The current attack will be %1$d%% less accurate, or the next set half that compounding. Applies %2$d stacks of bleed, around %3$d damage per tick.","Blood wells around their eyes!"),
		
		//perma 'condition loss' wounds
		DEPOWER("Depower","Injury: Removes special abilities.","Depowered!"),
		MAIMED("Maimed","Injury: Removes one attack choice each attack. Doesn't stack with Disarmed.","Maimed!"),
		CRIPPLED("Crippled","Injury: Set to 80% of dodge mult. Stacks.","Crippled!"),
		HIT_VITALS("Damaged Vitals","Injury: Takes double condition damage, and parts with low condition cause another wound to be inflicted.","Shattered!"),//the unholy matrimony of dd1 and weaverdice wounds
		BRAINED("Brained","Injury: KO wounds no longer heal. Also inflicts KO at %1$d.","Split skull!")
		;
		//done line
		public String name, desc, active;
		Wound(String iName,String iDesc,String activeDesc){
			name = iName;
			desc = iDesc;
			active = activeDesc;
		}
		
		public String getColor() {
			switch (this) {
			case NEGATED:
				return extra.ATK_WOUND_NEGATE;
			case EMPTY:
				return extra.ATK_WOUND_GRAZE;
			default://MAYBELATER cond wounds aren't displayed to the player like this, otherwise they'd need their own color
				return extra.ATK_WOUND_NORMAL;
			}
		}
	}

	/**
	 * should not be used on impaired/final attacks
	 */
	public double getTotalDam(Weapon weap) {
		double sMult = 1;
		double bMult = 1;
		double pMult = 1;
		if (weap != null) {
			sMult = weap.getMat().sharpMult;
			bMult = weap.getMat().bluntMult;
			pMult = weap.getMat().pierceMult;
		}
		return (this.getSharp()*sMult)+(this.getBlunt()*bMult)+(this.getPierce()*pMult);
	}
	
	@Override
	public int getTotalDam() {
		// TODO add other damage types when they get added
		return getSharp()+
				getBlunt()+
				getPierce()+
				getIgnite()+
				getFrost()+
				getElec();
	}
	
	public double getTotalDam(double sMult, double bMult, double pMult) {
		return (this.getSharp()*sMult)+(this.getBlunt()*bMult)+(this.getPierce()*pMult);
	}

	@Override
	public boolean physicalDamage() {
		return !bypass;//DOLATER
	}

	@Override
	public String fluff(AttackReturn attret) {
		String tempStr = fluffer.next();
		tempStr = tempStr.replace("X`","[HA]"+attret.attack.getAttacker().getName()+"[C]");
		tempStr = tempStr.replace("Y`","[HD]"+attret.attack.getDefender().getName() + "'s[C] " +
		(attret.attack.getTarget() != null ? attret.attack.getTarget().getName() : ""));
		tempStr = tempStr.replace("V`","[HD]"+attret.attack.getDefender().getName() + "[C]");
		tempStr = tempStr.replace("Z`",(attret.attack.getWeapon() != null ? attret.attack.getWeapon().getBaseName() : "fists" )+"[C]");//technically if you were to look upwards you could find the weapon, but I'm gonna put it in this way
		return tempStr;
	}

	@Override
	public double getWarmup() {
		return warmup;
	}

	@Override
	public double getCooldown() {
		return cooldown;
	}

	@Override
	public double getHitMult() {
		return hitMult;
	}

	@Override
	public int valueSize() {
		return intValues.length;
	}

	@Override
	public double getPotencyMult() {
		return 1;
	}

	@Override
	public void multPotencyMult(double multMult) {
		
	}

	public Stance getStance() {
		return holdingStance;
	}

	public void setStance(Stance holdingStance) {
		this.holdingStance = holdingStance;
	}

	//TODO: this will break in some circumstances
	public Attack copy() {
		Attack a = new Attack(name, desc, fluffer, hitMult, intValues, warmup,cooldown,bypass);
		a.soundStrength = soundStrength;
		a.soundType = soundType;
		a.skill_for = skill_for;
		a.rider = rider;
		return a;
	}

	public boolean isBypass() {
		return bypass;
	}

	public IHasSkills getSkillSource() {
		return holdingStance.getSkillSource();
	}

	public Skill getSkill_for() {
		/*USED TO
		//secondary tactics in stances overwrite their skill_fors
		if (skill_for != null) {
			return skill_for;
		}*/
		return holdingStance.getSkill();
	}
	
	public AttackBonus getRider() {
		return rider;
	}
	
	//fluent
	public Attack setSkill_for(Skill skill_for) {
		this.skill_for = skill_for;
		return this;
	}
	
	public Attack setRider(AttackBonus _rider) {
		rider = _rider;
		return this;
	}
	
	

}
