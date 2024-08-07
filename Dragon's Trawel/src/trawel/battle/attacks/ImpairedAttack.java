package trawel.battle.attacks;

import java.util.function.Supplier;

import trawel.battle.Combat;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.targets.Target;
import trawel.battle.targets.TargetFactory;
import trawel.battle.targets.TargetFactory.TypeBody.TargetReturn;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelChar;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Skill;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponQual;

public class ImpairedAttack implements IAttack{
	private Attack attack;
	
	private TargetReturn target;
	private Style style;
	private Wound wound;
	private Weapon weapon;
	private Person attacker, defender;//only used for mass battles
	
	private double warmup, cooldown;
	
	private double[] vals;
	
	private float hitroll;
	
	private double potencyMult;
	private int level;
	
	public ImpairedAttack(Attack _attack,
			TargetReturn _target, Style _style,
			Weapon _weapon, Person _attacker, Person _defender) {
		attack = _attack;
		
		target = _target;
		style = _style;
		weapon = _weapon;
		attacker = _attacker;
		defender = _defender;
		
		//compution
		Target t = target.tar;
		vals = new double[attack.valueSize()];
		
		double speedMult = _style.speed;
		double speedModUp = Rand.randRange(0,10);
		double speedModDown = Rand.randRange(-5,5);
		float hitMult = (float) (_style.hit*t.hit*_attack.getHitMult());
		
		double elementalDamMult = 1;
		double physicalDamMult = 1;
		
		//attrib bonuses now always apply to each type
		if (_attacker != null) {
			physicalDamMult = _attacker.attMultStr();
			elementalDamMult = _attacker.attMultCla();
		}
		
		double effectiveLevel;
		assert !(_weapon == null && _attacker == null);//only one can be null
		double sMult = attack.getSharp()*t.sharp;
		double bMult = attack.getBlunt()*t.blunt;
		double pMult = attack.getPierce()*t.pierce;
		int w_lvl;
		boolean alwaysWound = (_weapon != null && _weapon.isKeen());
		//boolean isAttackTest = defender == null;
		
		if (_weapon != null) {
			w_lvl = _weapon.getLevel();
			effectiveLevel = IEffectiveLevel.unEffective(IEffectiveLevel.effective(w_lvl));
			sMult *= _weapon.getMat().sharpMult;
			bMult *= _weapon.getMat().bluntMult;
			pMult *= _weapon.getMat().pierceMult;
		}else {
			IHasSkills attSource = attack.getSkillSource();
			if (_attacker != null && attSource != null) {
				w_lvl = attack.getStance().getEffectiveLevelFor(_attacker);
				effectiveLevel = IEffectiveLevel.unEffective(w_lvl);
				w_lvl-=10;
				if (_attacker.hasSkill(Skill.ELEMENTALIST)) {
					elementalDamMult*=1.1;//10% more elemental damage
				}
			}else {
				throw new RuntimeException("invalid weapon resolved level for impaired attack");
			}

		}
		//now uses potency mult for base power
		potencyMult = effectiveLevel*_style.damage;
		
		if (_attacker != null) {
			if (attacker.hasEffect(Effect.CHALLENGE_BACK)) {
				potencyMult*=1.2;
			}
		}

		vals[0] = damageRoll(DamageType.SHARP,sMult);
		vals[1] = damageRoll(DamageType.BLUNT,bMult);
		vals[2] = damageRoll(DamageType.PIERCE,pMult);
		
		double baseDam =vals[0]+vals[1]+vals[2];//set base damage (might still be zero)
		
		if (attack.isBypass()) {// || !(_weapon != null && _weapon.isEnchantedHit())
			vals[3] = damageRoll(DamageType.IGNITE,attack.getIgnite());
			vals[4] = damageRoll(DamageType.FROST,attack.getFrost());
			vals[5] = damageRoll(DamageType.ELEC,attack.getElec());
			//bypass attacks have their elemental damage count as base damage
			baseDam+=vals[3]+vals[4]+vals[5];
			
		}else {
			//enchant hit weapon with no bypass
			if (_weapon != null && _weapon.isEnchantedHit()) {
				Enchant enc = _weapon.getEnchant();
				//dam mult included in totalDam already
				double onhitMult = (_attacker != null && _attacker.hasSkill(Skill.RUNESMITH) ? 1.3d : 1d);
				vals[3] = damageRoll(DamageType.IGNITE,enc.getFireMod()*baseDam*onhitMult);
				vals[4] = damageRoll(DamageType.FROST,enc.getFreezeMod()*baseDam*onhitMult);
				vals[5] = damageRoll(DamageType.ELEC,enc.getShockMod()*baseDam*onhitMult);
			}
		}
		if (attacker != null && defender != null && attacker.hasSkill(Skill.FEVER_STRIKE) && defender.hasEffect(Effect.MIASMA)) {
			//decay damage bonus if defender has miasma
			//goes off of the rolled values because it itself does not roll
			vals[6] += baseDam*.1f;
		}
		
		//strength/clarity and other damage mults applied step
		vals[0]*=physicalDamMult;
		vals[1]*=physicalDamMult;
		vals[2]*=physicalDamMult;
		vals[3]*=elementalDamMult;
		vals[4]*=elementalDamMult;
		vals[5]*=elementalDamMult;
		vals[6]*=elementalDamMult;
		
		//no decay wounds
		double counter = Rand.getRand().nextDouble() * (vals[0] + vals[1] + vals[2] + vals[3] + vals[4] + vals[5]);
		counter-=vals[0];
		if (counter<=0) {
			this.wound = target.tar.rollWound(DamageType.SHARP);
		}else {
			counter-=vals[2];
			if (counter<=0) {
				this.wound = target.tar.rollWound(DamageType.PIERCE);
			}else {
				counter-=vals[3];
				if (counter<=0) {
					this.wound = target.tar.rollWound(DamageType.IGNITE);
				}else {
					counter-=vals[4];
					if (counter<=0) {
						this.wound = target.tar.rollWound(DamageType.FROST);
					}else {
						counter-=vals[5];
						if (counter<=0) {
							this.wound = target.tar.rollWound(DamageType.ELEC);
						}else {
							//blunt is last to be a default for rounding errors
							this.wound = target.tar.rollWound(DamageType.BLUNT);
						}
					}
				}
				
			}
		}
		if (!alwaysWound && wound != Wound.NEGATED && Rand.randRange(1,10) == 1) {
			this.wound = null;//null is not different from grazing, negating is a dedicated 'ineffective'
		}
		
		level = w_lvl;
		if (_attacker != null) {
			if (wound == null && _attacker.hasSkill(Skill.ELEMENTALIST)){
				switch (Rand.randRange(1, 10)) {//10 is a hard fail, 1-9 depends on if they have the subskills
				case 1: case 2: case 3:
					if(_attacker.hasSkill(Skill.M_PYRO)) {
						wound = Rand.randList(TargetFactory.fireWounds);
					} 
					break;
				case 4: case 5: case 6:
					if(_attacker.hasSkill(Skill.M_CRYO)) {
						wound = Rand.randList(TargetFactory.freezeWounds);
					} 
					break;
				case 7: case 8: case 9:
					if(_attacker.hasSkill(Skill.M_AERO)) {
						wound = Rand.randList(TargetFactory.shockWounds);
					} 
					break;
				}
			}
			
			speedMult *= _attacker.getSpeed();
			if (_attacker.hasEffect(Effect.SLICE)) {
				hitMult*=1.1;//WET
				speedMult*=.9;
			}
			/*if (_attacker.hasEffect(Effect.DICE)) {
				speedMult*=.9;//WET
			}*/
			hitMult *= _attacker.attMultDex()*_attacker.getBag().getAim();
		}
		cooldown = (attack.getCooldown()*speedMult)+speedModDown;
		warmup = (attack.getWarmup()*speedMult)+speedModUp;
		
		warmup = extra.clamp(warmup,10,400);
		cooldown = extra.clamp(cooldown,10,400);
		
		if (_attacker != null) {
			if (attacker.hasEffect(Effect.TELESCOPIC)) {
				hitMult +=((warmup+cooldown)/100);
			}
			if (attacker.hasEffect(Effect.BRISK)) {
				if (warmup+cooldown < 100) {
					hitMult*=extra.lerp(2,1,(warmup+cooldown)/100);
				}
				warmup/=2;
				cooldown/=2;
			}
		}
		hitroll = extra.lerp(hitMult*.8f,hitMult*1.2f, Rand.hrandomFloat());
		if (_weapon != null) {
			hitroll +=_weapon.hasQual(Weapon.WeaponQual.ACCURATE) ? .1 : 0;
		}
	}
	/**
	 * variant used for generic tactics with no secondary effects or deviation
	 */
	public ImpairedAttack(Attack _attack, Person _attacker, Person _defender) {
		attack = _attack;
		
		target = null;
		style = null;
		weapon = null;
		attacker = _attacker;
		defender = _defender;
		
		//none
		vals = new double[attack.valueSize()];
		hitroll = 10;
		potencyMult = 0;
		wound = null;
		level = _attacker.getLevel();
		cooldown = attack.getCooldown();
		warmup = attack.getWarmup();
	}
	
	public enum DamageType{
		SHARP("S","sharp","slashing, slicing, and cutting",
				()-> (TrawelChar.CHAR_SHARP)
				),
		BLUNT("B","blunt","bashing, slamming, and crushing",
				()-> (TrawelChar.CHAR_BLUNT)
				),
		PIERCE("P","pierce","puncturing, piercing, and skewering",
				()-> (TrawelChar.CHAR_PIERCE)
				),
		IGNITE("I","ignite","set aflame, fire, burning",
				()-> (TrawelChar.CHAR_IGNITE)),
		FROST("F","frost","chilled, frozen, frostbite",
				()-> (TrawelChar.CHAR_FROST)),
		ELEC("E","elec","zapped, shocked, electrocuted",
				()-> (TrawelChar.CHAR_ELEC)),
		DECAY("D","decay","withered, aged, decayed",
				()-> (TrawelChar.CHAR_DECAY));
		private final String disp;
		private final String name, desc;
		private final Supplier<String> getCode;
		DamageType(String _disp, String _name, String _desc, Supplier<String> _code){
			disp = _disp;
			name = _name;
			desc = _desc;
			getCode = _code;
		}
		public String getDisp() {
			return getCode.get();
		}
		public String getStaticDisp() {
			return disp;
		}
		public String getName() {
			return name;
		}
		public String getStaticExplain() {
			return name + "("+disp+"):" + desc;
		}
		
		public String getExplain() {
			return name + "("+getDisp()+TrawelColor.COLOR_RESET+"):" + desc;
		}
		
		public String getDispFor(ImpairedAttack ia) {
			/*switch (this) {
			case BLUNT:
				return disp + " " +ia.getBlunt();
			case DECAY:
				break;
			case ELEC:
				return disp + " " +ia.getElec();
			case FROST:
				return disp + " " +ia.getFrost();
			case IGNITE:
				return disp + " " +ia.getIgnite();
			case PIERCE:
				return disp + " " +ia.getPierce();
			case SHARP:
				return disp + " " +ia.getSharp();
			}
			return null;*/
			return getDisp() + " " + getAmountFor(ia)+TrawelColor.COLOR_RESET;
		}
		
		public int getAmountFor(ImpairedAttack ia) {
			switch (this) {
			case BLUNT:
				return ia.getBlunt();
			case DECAY:
				return ia.getDecay();
			case ELEC:
				return ia.getElec();
			case FROST:
				return ia.getFrost();
			case IGNITE:
				return ia.getIgnite();
			case PIERCE:
				return ia.getPierce();
			case SHARP:
				return ia.getSharp();
			}
			return 0;
		}
	}

	public static String EXPLAIN_DAMAGE_TYPES() {
		String str = "";
		for (DamageType t: DamageType.values()) {
			str += t.getDisp() + " " + t.getName()+TrawelColor.PRE_WHITE+ ", ";
		}
		return str;
	}
	
	public static String getOtherDam(ImpairedAttack att) {
		int amount = DamageType.IGNITE.getAmountFor(att);
		if (amount > 0) {
			return DamageType.IGNITE.getDispFor(att);
		}
		amount = DamageType.FROST.getAmountFor(att);
		if (amount > 0) {
			return DamageType.FROST.getDispFor(att);
		}
		amount = DamageType.ELEC.getAmountFor(att);
		if (amount > 0) {
			return DamageType.ELEC.getDispFor(att);
		}
		/* decay cannot be a normal added dam
		amount = DamageType.DECAY.getAmountFor(att);
		if (amount > 0) {
			return DamageType.DECAY.getDispFor(att);
		}*/
		return null;
	}
	
	private double damageRoll(DamageType dt, double max) {
		if (attacker != null) {
			max*= attacker.getBag().getDam();//DOLATER make physical damage mult seperate;
		}else {
			if (weapon != null) {//if attacker is null, but there is still a weapon
				if (weapon.getEnchant() != null) {
					max*=weapon.getEnchant().getDamMod();//DOLATER make physical damage mult seperate
				}	
			}
		}
		return Rand.getRand().nextDouble(max*.7,max);
	}
	
	/**
	 * can be null
	 */
	public Person getDefender() {
		return defender;
	}
	public void setDefender(Person defender) {
		this.defender = defender;
	}

	public Person getAttacker() {
		return attacker;
	}

	public Wound getWound() {
		return wound;
	}

	@Override
	public double getWarmup() {
		return warmup;
	}

	@Override
	public double getCooldown() {
		return cooldown;
	}

	/**
	 * can be null
	 */
	public Weapon getWeapon() {
		return weapon;
	}
	public Style getStyle() {
		return style;
	}
	public TargetReturn getTarget() {
		return target;
	}
	
	/**
	 * discourged use
	 */
	public Attack getAttack() {//should probably do direct functions
		return attack;
	}
	@Override
	public boolean physicalDamage() {
		return attack.physicalDamage();
	}
	@Override
	public String getName() {
		if (isTacticOnly()) {
			return attack.getName();
		}
		return style.name + attack.getName() + " " + target.getName();
	}
	@Override
	public String getDesc() {
		return attack.getDesc();
	}
	@Override
	public String fluff(AttackReturn attret) {
		return attack.fluff(attret);
	}
	@Override
	public int getSharp() {
		return (int) (getPotencyMult()*IAttack.getSharpFromWeap(vals));
	}
	@Override
	public int getBlunt() {
		return (int) (getPotencyMult()*IAttack.getBluntFromWeap(vals));
	}
	@Override
	public int getPierce() {
		return (int) (getPotencyMult()*IAttack.getPierceFromWeap(vals));
	}
	
	@Override
	public int getIgnite() {
		return (int) (getPotencyMult()*IAttack.getIgniteFromWeap(vals));
	}
	
	@Override
	public int getFrost() {
		return (int) (getPotencyMult()*IAttack.getFrostFromWeap(vals));
	}
	
	@Override
	public int getElec() {
		return (int) (getPotencyMult()*IAttack.getElecFromWeap(vals));
	}
	
	@Override
	public int getDecay() {
		return (int) (getPotencyMult()*IAttack.getDecayFromWeap(vals));
	}
	
	@Override
	public double getHitMult() {
		return hitroll;
	}

	@Override
	public int valueSize() {
		return vals.length;
	}
	
	public int getSlot() {
		return target.tar.slot;
	}
	
	public int getTargetSpot() {
		return target.spot;
	}

	public double getTime() {
		return warmup+cooldown;
	}

	@Override
	public int getTotalDam() {
		// TODO add other damage types when they get added
		return getSharp()+getBlunt()+getPierce()+getIgnite()+getFrost()+getElec()+getDecay();
	}
	
	public float multiplyHit(float mult) {
		return hitroll*=mult;
	}

	@Override
	public double getPotencyMult() {
		return potencyMult;
	}

	@Override
	public void multPotencyMult(double multMult) {
		potencyMult*= multMult;
	}

	public void display(int style) {
		int[] in;
		switch (style) {
		case 0://not impaired style
		case 1://classic
			//MAYBELATER: this doesn't display bonus damage properly
			in = new int[6];
			in[0] = 8+12;
			in[1] = 7;
			in[2] = 9;
			in[3] = 9;
			in[4] = 10;
			in[5] = 6+4;
			if (attack.isBypass()) {
				Print.specialPrint(in,"MAGIC: "+getName(),Print.format(getHitMult()),Print.format(getWarmup()+getCooldown()),TrawelColor.COLOR_IGNITE+(getIgnite()),TrawelColor.COLOR_FROST+(getFrost()),TrawelColor.COLOR_ELEC+(getElec()));
				break;
			}
			Print.specialPrint(in,getName(),Print.format(getHitMult()),Print.format(getWarmup()+getCooldown()),TrawelColor.COLOR_SHARP+(getSharp()),TrawelColor.COLOR_BLUNT+(getBlunt()),TrawelColor.COLOR_PIERCE+(getPierce()));
			break;
		case 2://two line 1
			Print.println(getName());
			in = new int[10];
			in[0] = 9;//hitchance, should be %9.99 >= x > 0.00 | 9 gives a 2 spot gap to the times
			in[1] = 4;//instants, should be _999 >= x > 0 | 4 because we're okay with pressing up against the next one
			in[2] = 1;//warmup/cooldown seperator
			in[3] = 5;//instants, should be _999 >= x > 0 | 5 because we want to give the next a one-two spot gap
			//
			in[4] = 2;//details seperator
			//sbp 6 should be fine for 3 digits, 7 for 4
			//these can vary based on the attack
			in[5] = 6;
			in[6] = 6;
			in[7] = 6;
			in[8] = 0;//bonus1
			in[9] = 0;//bonus2
			String dam1, dam2, dam3, dam4, dam5;
			if (attack.isBypass()) {
				dam1 = DamageType.IGNITE.getDispFor(this);
				dam2 = DamageType.FROST.getDispFor(this);
				dam3 = DamageType.ELEC.getDispFor(this);
				dam4 = "";
			}else {
				dam1 = DamageType.SHARP.getDispFor(this);
				dam2 = DamageType.BLUNT.getDispFor(this);
				dam3 = DamageType.PIERCE.getDispFor(this);
				dam4 = getOtherDam(this);
				if (dam4 != null) {
					in[8] = 6;
				}else {
					dam4 = "";
				}
			}
			if (getDecay() > 0) {
				dam5 = DamageType.DECAY.getDispFor(this);
				in[9] = 6;
			}else {
				dam5 = "";
			}

			Print.specialPrint(in,"  "+TrawelChar.CHAR_HITMULT + Print.format(getHitMult()),
					TrawelChar.CHAR_INSTANTS +Print.formatInt(getWarmup())," ",TrawelChar.CHAR_INSTANTS+Print.formatInt(getCooldown()),
					"=",
					dam1,dam2,dam3,dam4,dam5//unsure if spacing messes up narrator
					);
			break;
		case 3://simplified
			Print.println(getName());
			in = new int[3];
			in[0] = 9;//hitchance, should be %9.99 >= x > 0.00 |
			in[1] = 6;//instants, should be _999 >= x > 0
			in[2] = 7;//5 damage digits
			Print.specialPrint(in,
					"  "+TrawelChar.CHAR_HITMULT + Print.format(getHitMult()),
					TrawelChar.CHAR_INSTANTS + Print.formatInt(getWarmup()+getCooldown()),
					TrawelChar.CHAR_DAMAGE + getTotalDam()
					);
			break;
		}
		Wound aWound = wound;
		if (aWound == null) {
			aWound = Wound.EMPTY;
		}
		Print.println("  "+aWound.getColor()+aWound.name + TrawelColor.PRE_WHITE+ " - " + String.format(aWound.desc,(Object[])Combat.woundNums(this,attacker,defender,null,aWound)));
		if (hasBonusEffect()) {
			AttackBonus ab = getAttack().getRider();
			Print.println("  "+TrawelColor.ATK_BONUS+ab.label +TrawelColor.PRE_WHITE+": " + ab.desc);
		}
	}

	public int getLevel() {
		return level;
	}
	
	public boolean hasWeaponQual(WeaponQual qual) {
		return weapon != null && weapon.hasQual(qual);
	}
	
	public boolean isTacticOnly() {
		return target == null;
	}
	
	public boolean hasBonusEffect() {
		return  attack.getRider() != null;
	}
}
