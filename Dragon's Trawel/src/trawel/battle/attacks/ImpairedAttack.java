package trawel.battle.attacks;

import trawel.Effect;
import trawel.extra;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.Attack.Wound;
import trawel.battle.attacks.IAttack.AttackType;
import trawel.battle.attacks.TargetFactory.TypeBody.TargetReturn;
import trawel.personal.Person;
import trawel.personal.item.solid.Weapon;

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
		float hitMult = (float) (_style.hit*t.hit);
		
		switch (getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
			assert !(_weapon == null && _attacker == null);//only one can be null
			double sMult = attack.getSharp()*t.sharp;
			double bMult = attack.getBlunt()*t.blunt;
			double pMult = attack.getPierce()*t.pierce;
			int w_lvl;
			boolean alwaysWound = (_weapon != null && _weapon.isKeen());
			boolean isAttackTest = defender == null;
			
			if (_weapon != null) {
				w_lvl = isAttackTest ? 10 :_weapon.getLevel();
				sMult *= _weapon.getMat().sharpMult;
				bMult *= _weapon.getMat().bluntMult;
				pMult *= _weapon.getMat().pierceMult;
				hitMult *=_weapon.qualList.contains(Weapon.WeaponQual.ACCURATE) ? 1.1 : 1;
			}else {
				if (_attacker != null) {
					w_lvl = _attacker.getLevel();
				}else {
					throw new RuntimeException("invalid weapon resolved level for impaired attack");
				}

			}
			double damMult = w_lvl*_style.damage;

			vals[0] = damageRoll(DamageType.SHARP,sMult*damMult);
			vals[1] = damageRoll(DamageType.BLUNT,bMult*damMult);
			vals[2] = damageRoll(DamageType.PIERCE,pMult*damMult);

			if (!alwaysWound && extra.randRange(1,10) == 1) {
				this.wound = Attack.Wound.GRAZE;
			}else {
				double counter = extra.getRand().nextDouble() * (vals[0] + vals[1] + vals[2]);
				counter-=vals[0];
				if (counter<=0) {
					this.wound = extra.randList(target.tar.slashWounds);
				}else {
					counter-=vals[2];
					if (counter<=0) {
						this.wound = extra.randList(target.tar.pierceWounds);
					}else {//blunt is last now to be a default for rounding errors
						this.wound = extra.randList(target.tar.bluntWounds);
					}
				}
			}
			break;
		case SKILL:
			break;
		}
		if (_attacker != null) {
			speedMult *= _attacker.getSpeed();
			if (_attacker.hasEffect(Effect.SLICE)) {
				hitMult*=1.1;//WET
				speedMult*=.9;
			}
			if (_attacker.hasEffect(Effect.DICE)) {
				speedMult*=.9;//WET
			}
		}
		cooldown = (attack.getCooldown()*speedMult)+speedModDown;
		warmup = (attack.getWarmup()*speedMult)+speedModUp;

		//
		hitroll = extra.lerp(hitMult*.8f,hitMult*1.2f, extra.hrandomFloat());
	}
	
	public enum DamageType{
		SHARP, BLUNT, PIERCE, SHOCK, FIRE, FROST, DECAY
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
		return style.name + attack.getName() + " " + target.getName();
	}
	@Override
	public String getDesc() {
		return attack.getDesc();
	}
	@Override
	public String fluff(AttackReturn attret) {
		// TODO Auto-generated method stub
		return attack.fluff(attret);
	}
	@Override
	public int getSharp() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
			return (int) (getPotencyMult()*IAttack.getSharpFromWeap(vals));
		case SKILL:
			break;
		}
		return 0;
	}
	@Override
	public int getBlunt() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
			return (int) (getPotencyMult()*IAttack.getBluntFromWeap(vals));
		case SKILL:
			break;
		}
		return 0;
	}
	@Override
	public int getPierce() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
			return (int) (getPotencyMult()*IAttack.getPierceFromWeap(vals));
		case SKILL:
			break;
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
		return getSharp()+getBlunt()+getPierce();
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
}
