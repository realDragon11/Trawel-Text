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
		vals = new int[attack.valueSize()];
		
		double speedMult = _style.speed;
		double speedModUp = extra.randRange(0,10);
		double speedModDown = extra.randRange(-5,5);
		float hitMult = (float) (_style.hit*t.hit);
		
		switch (getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
			double sMult = _style.damage*t.sharp;
			double bMult = _style.damage*t.blunt;
			double pMult = _style.damage*t.pierce;
			
			if (_weapon != null) {
				sMult *= _weapon.getMat().sharpMult;
				bMult *= _weapon.getMat().bluntMult;
				pMult *= _weapon.getMat().pierceMult;
				hitMult *=_weapon.qualList.contains(Weapon.WeaponQual.ACCURATE) ? 1.1 : 1;
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
	
	private Attack getAttack() {//should probably do direct functions
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
		return null;
	}
	@Override
	public int getSharp() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
			return IAttack.getSharpFromWeap(vals);
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
			return IAttack.getBluntFromWeap(vals);
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
			return IAttack.getPierceFromWeap(vals);
		case SKILL:
			break;
		}
		return 0;
	}
	@Override
	public double getHitMult() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public AttackType getType() {
		return attack.getType();
	}

	@Override
	public int valueSize() {
		return vals.length;
	}
}
