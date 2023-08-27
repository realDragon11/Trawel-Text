package trawel.battle.attacks;

import java.util.function.Supplier;

import trawel.Effect;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.Attack.Wound;
import trawel.battle.attacks.TargetFactory.TypeBody.TargetReturn;
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
	
	private int[] vals;
	
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
		
		potencyMult = 1;
		//compution
		Target t = target.tar;
		vals = new int[attack.valueSize()];
		
		double speedMult = _style.speed;
		double speedModUp = extra.randRange(0,10);
		double speedModDown = extra.randRange(-5,5);
		float hitMult = (float) (_style.hit*t.hit*_attack.getHitMult());
		
		double elementalDamMult = 1;
		double physicalDamMult = 1;
		
		double effectiveLevel;
		
		switch (getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			assert !(_weapon == null && _attacker == null);//only one can be null
			double sMult = attack.getSharp()*t.sharp;
			double bMult = attack.getBlunt()*t.blunt;
			double pMult = attack.getPierce()*t.pierce;
			int w_lvl;
			boolean alwaysWound = (_weapon != null && _weapon.isKeen());
			//boolean isAttackTest = defender == null;
			
			if (_weapon != null) {
				w_lvl = _weapon.getLevel();//wow I was going crazy due to setting it to 10 here
				effectiveLevel = IEffectiveLevel.unEffective(IEffectiveLevel.effective(w_lvl));
				sMult *= _weapon.getMat().sharpMult;
				bMult *= _weapon.getMat().bluntMult;
				pMult *= _weapon.getMat().pierceMult;
				if (_attacker != null) {
					physicalDamMult *= _attacker.attMultStr();
				}
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
			
			double damMult = effectiveLevel*_style.damage;
			
			if (_attacker != null) {
				if (attacker.hasEffect(Effect.CHALLENGE_BACK)) {
					damMult*=1.2;
				}
			}

			vals[0] = damageRoll(DamageType.SHARP,sMult*damMult*physicalDamMult);
			vals[1] = damageRoll(DamageType.BLUNT,bMult*damMult*physicalDamMult);
			vals[2] = damageRoll(DamageType.PIERCE,pMult*damMult*physicalDamMult);
			if (attack.isBypass() || !(_weapon != null && _weapon.isEnchantedHit())) {
				vals[3] = damageRoll(DamageType.IGNITE,attack.getIgnite()*damMult*elementalDamMult);
				vals[4] = damageRoll(DamageType.FROST,attack.getFrost()*damMult*elementalDamMult);
				vals[5] = damageRoll(DamageType.ELEC,attack.getElec()*damMult*elementalDamMult);
			}else {//enchant hit weapon with no bypass
				int totalDam = attack.getTotalDam();
				Enchant enc = _weapon.getEnchant();
				vals[3] = damageRoll(DamageType.IGNITE,enc.getFireMod()*totalDam*damMult*elementalDamMult);
				vals[4] = damageRoll(DamageType.FROST,enc.getFreezeMod()*totalDam*damMult*elementalDamMult);
				vals[5] = damageRoll(DamageType.ELEC,enc.getShockMod()*totalDam*damMult*elementalDamMult);
			}
			
			double counter = extra.getRand().nextDouble() * (vals[0] + vals[1] + vals[2] + vals[3] + vals[4] + vals[5]);
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
			if (!alwaysWound && wound != Wound.GRAZE && extra.randRange(1,10) == 1) {
				this.wound = null;//null is not different from grazing, grazing is a dedicated 'ineffective'
			}
			
			level = w_lvl;
			break;
		}
		if (_attacker != null) {
			if (wound == null && _attacker.hasSkill(Skill.ELEMENTALIST)){
				switch (extra.randRange(0, 3)) {//0 is a hard fail, 1-3 depends on if they have the subskills
				case 1:
					if(_attacker.hasSkill(Skill.M_PYRO)) {
						wound = extra.randList(TargetFactory.fireWounds);
					} 
					break;
				case 2:
					if(_attacker.hasSkill(Skill.M_CRYO)) {
						wound = extra.randList(TargetFactory.freezeWounds);
					} 
					break;
				case 3:
					if(_attacker.hasSkill(Skill.M_AERO)) {
						wound = extra.randList(TargetFactory.shockWounds);
					} 
					break;
				}
			}
			
			speedMult *= _attacker.getSpeed();
			if (_attacker.hasEffect(Effect.SLICE)) {
				hitMult*=1.1;//WET
				speedMult*=.9;
			}
			if (_attacker.hasEffect(Effect.DICE)) {
				speedMult*=.9;//WET
			}
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
		hitroll = extra.lerp(hitMult*.8f,hitMult*1.2f, extra.hrandomFloat());
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
		vals = new int[attack.valueSize()];
		hitroll = 10;
		potencyMult = 0;
		wound = null;
		level = _attacker.getLevel();
		cooldown = attack.getCooldown();
		warmup = attack.getWarmup();
	}
	
	public enum DamageType{
		SHARP("S","sharp","slashing, slicing, and cutting",
				()-> (extra.CHAR_SHARP)
				),
		BLUNT("B","blunt","bashing, slamming, and crushing",
				()-> (extra.CHAR_BLUNT)
				),
		PIERCE("P","pierce","puncturing, piercing, and skewering",
				()-> (extra.CHAR_PIERCE)
				),
		IGNITE("I","ignite","set aflame, fire, burning",
				()-> (extra.CHAR_IGNITE)),
		FROST("F","frost","chilled, frozen, frostbite",
				()-> (extra.CHAR_FROST)),
		ELEC("E","elec","zapped, shocked, electrocuted",
				()-> (extra.CHAR_ELEC)),
		DECAY("D","decay","withered, aged, decayed",
				()-> (extra.CHAR_DECAY));
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
			return name + "("+getDisp()+"):" + desc;
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
			return getDisp() + " " + getAmountFor(ia);
		}
		
		public int getAmountFor(ImpairedAttack ia) {
			switch (this) {
			case BLUNT:
				return ia.getBlunt();
			case DECAY:
				break;
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
			str += t.getDisp() + " " + t.getName()+ ", ";
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
		amount = DamageType.DECAY.getAmountFor(att);
		if (amount > 0) {
			return DamageType.DECAY.getDispFor(att);
		}
		return null;
	}
	
	private int damageRoll(DamageType dt, double max) {
		switch (dt) {
		case SHARP: case BLUNT: case PIERCE:
			if (attacker != null) {
				max*= attacker.getBag().getDam();//DOLATER make physical damage mult seperate;
				max*= attacker.fetchAttributes().multStrength();//DOLATER see if working
			}else {
				if (weapon != null) {
					if (weapon.getEnchant() != null) {
						max*=weapon.getEnchant().getDamMod();//DOLATER make physical damage mult seperate
					}	
				}
			}
			return extra.randRange((int)(max*.7),(int)max);
		case IGNITE: case FROST: case ELEC:
			if (attacker != null) {
				max*= attacker.getBag().getDam();//DOLATER make physical damage mult seperate;
			}else {
				if (weapon != null) {
					if (weapon.getEnchant() != null) {
						max*=weapon.getEnchant().getDamMod();//DOLATER make physical damage mult seperate
					}	
				}
			}
			return extra.randRange((int)(max*.7),(int)max);
		default: 
			return 0;
		}
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
		return attack.fluff(attret) + (mainGame.advancedCombatDisplay ? attret.getNotes() : "");
	}
	@Override
	public int getSharp() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getSharpFromWeap(vals));
		}
		return 0;
	}
	@Override
	public int getBlunt() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getBluntFromWeap(vals));
		}
		return 0;
	}
	@Override
	public int getPierce() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getPierceFromWeap(vals));
		}
		return 0;
	}
	
	@Override
	public int getIgnite() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getIgniteFromWeap(vals));
		}
		return 0;
	}
	
	@Override
	public int getFrost() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getFrostFromWeap(vals));
		}
		return 0;
	}
	
	@Override
	public int getElec() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getElecFromWeap(vals));
		}
		return 0;
	}
	
	@Override
	public double getHitMult() {
		return hitroll;
	}
	
	@Override
	public AttackType getType() {
		return attack.getType();
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
		return getSharp()+getBlunt()+getPierce()+getIgnite()+getFrost()+getElec();
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
			in = new int[6];
			in[0] = 8+12;
			in[1] = 7;
			in[2] = 9;
			in[3] = 9;
			in[4] = 10;
			in[5] = 6+4;
			if (attack.isBypass()) {
				extra.println("ELEMENTAL");
				extra.specialPrint(in,getName() ,extra.format(getHitMult()) , extra.format(getWarmup()+getCooldown())  ,""+ (getIgnite())  ,""+(getFrost())  ,  ""+(getElec()));
				break;
			}
			extra.specialPrint(in,getName() ,extra.format(getHitMult()) , extra.format(getWarmup()+getCooldown())  ,""+ (getSharp())  ,""+(getBlunt())  ,  ""+(getPierce()));
			break;
		case 2://two line 1
			extra.println(getName());
				in = new int[9];
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
				in[8] = 0;//bonus
				String dam1, dam2, dam3, dam4;
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
				
				extra.specialPrint(in,"  "+extra.CHAR_HITCHANCE + extra.format(getHitMult()),
						extra.CHAR_INSTANTS +extra.formatInt(getWarmup()),"-",extra.CHAR_INSTANTS+extra.formatInt(getCooldown()),
						"=",
						dam1,dam2,dam3,dam4//unsure if spacing messes up narrator
						);
			}
			if (wound != null) {
				extra.println("  "+this.wound.name + " - " + String.format(this.wound.desc,(Object[])Combat.woundNums(this,attacker,defender,null,wound)));
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
		return  attack.getSkill_for() != null && attack.getSkill_for() != attack.getStance().getSkill();
	}
}
