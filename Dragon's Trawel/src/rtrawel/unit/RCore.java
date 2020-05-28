package rtrawel.unit;

import java.util.List;

import rtrawel.items.WeaponFactory;

public class RCore {

	
	public static double calcDamageMod(double attackStat,double defendStat) {
		return (100+attackStat)/(100+defendStat);
	}
	
	public static boolean doesHit(RUnit attacker, RUnit defender, double baseHitMult) {
		return (1.1*(attacker.getAgility()+10))/(1.35*(attacker.getAgility()+10)) > Math.random();
	}
	
	public static int dealDamage(RUnit defender, int attackStat, int damage, List<DamageType> types ) {
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
	 * @param damage
	 * @param list
	 * @return
	 */
	public static int doAttack(RUnit attacker, RUnit defender, int attackStat, double baseHitMult, int damage, List<DamageType> list ) {
		if (doesHit(attacker,defender,baseHitMult)) {
			return dealDamage(defender,attackStat,damage,list);
		}else {
			return -1;
		}
	}
	
	
	public static void init() {
		ActionFactory.init();
		WeaponFactory.init();
		MonsterFactory.init();
	}
	
	
	
}
