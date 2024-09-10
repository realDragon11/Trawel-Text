package trawel.battle.attacks;

import trawel.helper.constants.TrawelColor;

//.0f = float/double with no decimal places. Weird that it can't auto convert, but oh well
//UPDATE welp can't make them look nicer so just made them all ints anyways
//MAYBELATER: display the actual wound name every time, but also have a stringfluffer describe it
public enum Wound{//TODO: make sure the reworked wounds are fully in
	//special
	ERROR("ERROR","ERROR",false,true,"ERROR"),
	NEGATED("Negated","No effect, resists.",true,false,"Negated..."),
	EMPTY("Grazed","No bonus.",true,false,"The blow's a graze..."),
	//normal wound start
	//sharp primary, bleed and bonus damage- hp race damage type
	BLEED("Cut","Applies %1$d stacks of bleed."
			,false,false,"They bleed..."),
	MAJOR_BLEED("Lacerate","Applies %1$d stacks of bleed and prevents bleed from healing."
			,false,false,"An artery is cut!"),
	SLICE("Slice","Attacker's next action will happen %1$d%% quicker and be %1$d%% more accurate. Also grants one stack of Advantage."
			,false,false,"They are sliced!"),
	DICE("Dice","Attacker's next action will happen %1$d%% quicker and be %1$d%% more accurate, as well as %2$d instants sooner."
			,false,false,"They are diced!"),
	HACK("Hack","Deals up to %1$d direct damage based on unblocked damage to defender."
			,false,false,"It's a wicked hack!"),
	FLAYED("Flay","Applies %1$d stacks of bleed, then deal direct damage equal to bleed stacks, expected %1$d."
			,false,false,"They are flayed!"),
	//pierce primary, impair and destroy defensive resources
	HAMSTRUNG("Hamstrung","Delays the defender's next attack by %1$d instants, and applies %2$d -10%% dodge Shaky stacks."
			,false,false,"Their leg is hamstrung!"), 
	DISARMED("Disarm","Defender loses one attack choice and suffers %1$d%% compounding inaccuracy on next attack."
			,false,false,"Their attack is put off-kilter!"),
	BLINDED("Blind","Inflicts %1$d%% inaccuracy on the current attack, or half that to the next set of attack choices as compounding inaccuracy."
			,false,false,"They can't see!"),
	PUNCTURED("Puncture","Damages armor up to %1$d%%, based on double MHP percent of dealt damage. Ignores Negation and removes one Padded stack."
			,true,false,"The blow punctures through the armor!"),
	RUPTURED("Rupture","Damages armor by %1$d%%."
			,false,false,"The blow ruptures the armor!"),
	//blunt, mix of wounds but damage type tends to be resisted less
	WINDED("Wind","Defender's action will take %1$d instants longer."
			,false,false,"The wind is knocked out of them!"),
	CONFUSED("Confuse","Forces the defender to retarget, and applies %1$d -10%% dodge Shaky stacks."
			,false,false,"They look confused!"), 
	DIZZY("Dizzy","Inflicts %1$d%% inaccuracy to the defender's current action, or compounding inaccuracy to the next set of attacks."
			,false,false,"They look dizzy!"),
	TRIPPED("Trip","Defender's action will take %1$d instants longer, and applies %2$d -10%% dodge Shaky stacks."
			,false,false,"They are tripped!"),
	CRUSHED("Crush","Deals %1$d direct damage."
			,false,false,"They are crushed!"),
	KO("Knockout","Deals %1$d direct damage, but defender heals after their next attack."
			,false,false,"It's a knockout!"),
	BLEED_BLUNT("Trauma","Applies %1$d stacks of bleed."
			,false,false,"Their insides get smashed."),
	MAJOR_BLEED_BLUNT("Fracture","Applies %1$d stacks of bleed and prevents bleed from healing."
			,false,false,"Their insides get crushed!"),
	//soft decap'd wounds, used mostly as weird condwounds
	I_BLEED("Fracture","Applies a stacking %2$d bleed, expected %1$d for this stack against this attacker."
			,true,true,"Their insides get crushed!"),
	
	//elemental
	SCALDED("Scald","Deals %1$d direct damage and burns defender's armor by %2$d%%."
			,false,false,"They are scalded by the flames!"),
	BLACKENED("Blacken","Burns defender's armor by %1$d%%."
			,false,false,"Their armor burns!"),
	SCREAMING("Scream","Defender loses one attack choice and applies %1$d -10%% dodge Shaky stacks."
			,false,false,"They scream!"),
	FROSTED("Frost","Defender's action takes %1$d%% longer on the current time, up to %2$d instants increase."
			,false,false,"They are frozen over..."),
	FROSTBITE("Frostbite","Deals %1$d direct damage and applies %2$d -10%% dodge Shaky stacks."
			,false,false,"Their flesh is frozen!"),
	JOLTED("Jolt","Defender's action takes %1$d instants longer."
			,false,false,"They are jolted!"),
	SHIVERING("Shiver","Applies %1$d%% compounding inaccuracy to the defender's next set of attacks, and applies %2$d -10%% dodge Shaky stacks."
			,false,false,"They shiver at the intense cold..."),
	STATIC("Static","Removes all stacks of Advantage and Bonus Weapon Attacks, and applies %1$d -10%% dodge Shaky stacks."
			,false,false,"The static dazes them..."),
	//exotic
	TEAR("Tear","Decreases defender's dodge by %1$%d%%, stacking."
			,false,false,"Their wing is torn!"), //see if need to add a '%'
	MANGLED("Mangle","Halves the condition of the targeted body part."
			,false,false,"Their body is mangled!"),
	BLOODY("Bloody","The current attack will be %1$d%% less accurate, or the next set half that compounding. Applies %2$d stacks of bleed."
			,false,false,"Blood wells around their eyes!"),
	
	//perma 'condition loss' wounds
	DEPOWER("Depower","Injury: Removes special abilities."
			,true,true,"Depowered!"),
	MAIMED("Maim","Injury: Removes one attack choice each attack. Doesn't stack with Disarmed."
			,true,true,"Maimed!"),
	CRIPPLED("Cripple","Injury: Set to 80% of dodge mult. Stacks."
			,true,true,"Crippled!"),
	HIT_VITALS("Shatter","Injury: Takes double condition damage, and parts with low condition cause another wound to be inflicted."
			,true,true,"Shattered!"),//the unholy matrimony of dd1 and weaverdice wounds
	BRAINED("Brain","Injury: KO wounds no longer heal. Also inflicts KO at %1$d."
			,true,true,"Split skull!"),
	//undead condwounds which are less perma
	SHINE("Shine","Deals %1$d direct damage and burn defender's armor by %2$d%%."
			,true,true,"Their body shines with holy flame!"),
	GLOW("Glow","Applies %1$d%% compounding inaccuracy to the defender's next set of attacks and burn armor by %2$d%%."
			,true,true,"Their body glows with holy light!"),
	//statue condwound
	CRUMBLE("Crumble","Deals %1$d direct damage."
			,true,true,"Their stone skin crumbles!")
	;
	//done line
	public final String name, desc, active;
	/**
	 * bypass = allows ignoring wound mitigation, should mostly be used for condwounds
	 * <br>
	 * injury = is condwound only
	 */
	public final boolean bypass, injury;
	Wound(String iName,String iDesc,boolean _bypass,boolean _injury, String activeDesc){
		name = iName;
		desc = iDesc;
		active = activeDesc;
		bypass = _bypass;
		injury = _injury;
	}
	
	public String getColor() {
		switch (this) {
		case NEGATED:
			return TrawelColor.ATK_WOUND_NEGATE;
		case EMPTY:
			return TrawelColor.ATK_WOUND_GRAZE;
		default:
			if (injury) {
				return TrawelColor.ATK_WOUND_INJURY;
			}
			return TrawelColor.ATK_WOUND_NORMAL;
		}
	}

}