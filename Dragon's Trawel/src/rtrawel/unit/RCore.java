package rtrawel.unit;

public class RCore {

	
	public static double calcDamageMod(double attackStat,double defendStat) {
		return (100+attackStat)/(100+defendStat);
	}
	
	public static boolean doesHit(RUnit attacker, RUnit defender, double baseHitMult) {
		
		
		return true;
	}
	
	public static int dealDamage(RUnit defender, int attackStat, int damage, DamageType... types ) {
		double mult = calcDamageMod(attackStat, defender.getResilence());
		for (DamageType t: types) {
			mult*= defender.getDamageMultFor(t);
		}
		int ret = (int) (damage*mult);
		defender.takeDamage(ret);
		return ret;
	}
	
	
}
