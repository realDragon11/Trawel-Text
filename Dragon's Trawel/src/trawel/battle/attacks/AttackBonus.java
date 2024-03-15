package trawel.battle.attacks;

public enum AttackBonus{
	ROLL("Roll","Gain a temporary dodge bonus then an accuracy/speed bonus on the attack after, before having to recover."
			,"On use: Enhances dodge roll by a flat +0.2 and makes the next attack half the duration and more accurate, at the cost of half dodge during the attack following the attack that benefits."
			,"Quicker attacks gain 1x-2x accuracy based on un-reduced duration below 100 instants, but slower attacks get more benefit from the delay reduction. Dodge bonus applies after normal dodge is rolled."
			)
	,CHALLENGE("Challenge","Force opponent to attack you, then attack them back"
			,"On use: Forces your target to attack you: their current action is interrupted and they take an attack on you immediately, but they suffer an extended cooldown, you can negate an impactful wound if they hit, and you will attack them again with +20% damage if you not use the wound negation after the attack."
			,"The extended cooldown is the normal cooldown plus twice the warmup of their attack, and reduced by the time spent on their canceled attack, down to a minimum of the normal cooldown. Fails if the target is using a tactic.")
	,SINGLE_OUT("Single Out","Make attacks on this target likely to repeat"
			,"On use: Makes the target more likely to be chosen again by any Person that attacks it, with a 2/3rds chance to force them to attack again."
			,"This does not make it more likely that it is chosen in the first place. Does not apply if was attacked by an ally."),
	TAKEDOWN("Takedown","Your next impactful attack will inflict the Knockout Wound as a bonus."
			,"On use: Grants next impactful attack a bonus Knockout Wound infliction."
			,"Stacks in duration.")
	//FIXME: new status riders for some magic attacks
	,CHAR("Char","Burn enemy armor by 5%"
			,"On impact: Burn enemy armor by 5%"
			,"Applies after hit is resolved. Not localized."
			);
	public final String label, desc, mech, deep;
	AttackBonus(String _label, String _desc, String _mech, String _deep) {
		label = _label;
		desc = _desc;
		mech = _mech;
		deep = _deep;
	}
}