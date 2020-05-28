package rtrawel.unit;

import java.util.List;

import rtrawel.items.WeaponFactory;
import rtrawel.unit.RUnit.FightingStance;
import trawel.extra;

public class RCore {

	
	public static double calcDamageMod(double attackStat,double defendStat) {
		return (100+attackStat)/(100+defendStat);
	}
	
	public static boolean doesHit(RUnit attacker, RUnit defender, double baseHitMult,boolean ranged) {
		if (Math.random() < defender.shieldBlockChance()) {
			return false;
		}
		double evadeMult = 1.35;
		switch (defender.getStance()) {
		case OFFENSIVE: evadeMult -= .1;break;
		case DEFENSIVE: evadeMult += .1;break;
		}
		double hitMult = 1.1 + (attacker.getStance().equals(FightingStance.DEFENSIVE) && !ranged ? -.1 : 0);
		return (hitMult*(attacker.getAgility()+10))/(evadeMult*(attacker.getAgility()+10)) > Math.random();
	}
	
	public static int dealDamage(RUnit defender, int attackStat, double damage, List<DamageType> types ) {
		double mult = calcDamageMod(attackStat, defender.getResilence());
		for (DamageType t: types) {
			mult*= defender.getDamageMultFor(t);
		}
		int ret = (int) (damage*mult);
		defender.takeDamage(ret);
		return ret;
	}
	
	/**
	 * Proper usage: detect bonus damage in advance to load into 'damage', then apply on-hit bonuses if you detect >-1 damage dealt.
	 * 
	 * @param attacker
	 * @param defender
	 * @param attackStat
	 * @param baseHitMult
	 * @param d
	 * @param list
	 * @return
	 */
	public static int doAttack(RUnit attacker, RUnit defender, int attackStat, double baseHitMult, double d, boolean ranged, List<DamageType> list ) {
		if (doesHit(attacker,defender,baseHitMult, ranged)) {
			int dam = dealDamage(defender,attackStat,d * (!ranged && attacker.getStance().equals(RUnit.FightingStance.OFFENSIVE) && !ranged ? 1.1 : 1),list);
			extra.println("It deals " + dam +" to "+ defender.getName() +"!");
			return dam;
		}else {
			extra.println("It's a miss!");
			return -1;
		}
	}
	
	
	public static void init() {
		ActionFactory.init();
		WeaponFactory.init();
		MonsterFactory.init();
	}
	
	
	
}
