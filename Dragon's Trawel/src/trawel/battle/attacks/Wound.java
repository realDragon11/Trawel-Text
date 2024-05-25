package trawel.battle.attacks;

import trawel.extra;

//.0f = float/double with no decimal places. Weird that it can't auto convert, but oh well
//UPDATE welp can't make them look nicer so just made them all ints anyways
//MAYBELATER: display the actual wound name every time, but also have a stringfluffer describe it
public enum Wound{//TODO: make sure the reworked wounds are fully in
	//special
	ERROR("ERROR","ERROR","ERROR"),
	NEGATED("Negated","No effect, resists.","Negated..."),
	EMPTY("Grazed","No bonus.","The blow's a graze..."),
	//normal wound start
	//sharp primary, bleed and bonus damage- hp race damage type
	BLEED("Cut","Applies %1$d stacks of bleed, around %2$d damage per tick.","They bleed..."),
	MAJOR_BLEED("Lacerate","Applies %1$d stacks of bleed, around %2$d damage per tick, and prevents bleed from healing.","An artery is cut!"),
	SLICE("Slice","Attacker's next action will happen %1$d%% quicker and be %1$d%% more accurate. Also grants one stack of Advantage.","They are sliced!"),
	DICE("Dice","Attacker's next action will happen %1$d%% quicker and be %1$d%% more accurate, as well as %2$d instants sooner.","They are diced!"),
	HACK("Hack","Deals up to %1$d direct damage based on unblocked damage to defender.","It's a wicked hack!"),
	//pierce primary, impair and destroy defensive resources
	HAMSTRUNG("Hamstrung","Delays the defender's next attack by %1$d instants, and applies %2$d -10%% dodge Shaky stacks.","Their leg is hamstrung!"), 
	DISARMED("Disarm","Defender loses one attack choice and suffers %1$d%% compounding inaccuracy on next attack.","Their attack is put off-kilter!"),
	BLINDED("Blind","Inflicts %1$d%% inaccuracy on the current attack, or half that to the next set of attack choices as compounding inaccuracy.","They can't see!"),
	PUNCTURED("Puncture","Damages armor up to %1$d%%, based on double MHP percent of dealt damage. Ignores Padded and removes one stack.","The blow punctures through the armor!"),
	RUPTURED("Rupture","Damages armor by %1$d%%.","The blow ruptures the armor!"),
	//blunt, mix of wounds but damage type tends to be resisted less
	WINDED("Wind","Defender's action will take %1$d instants longer.","The wind is knocked out of them!"),
	CONFUSED("Confuse","Forces the defender to retarget, and applies %1$d -10%% dodge Shaky stacks.","They look confused!"), 
	DIZZY("Dizzy","Inflicts %1$d%% inaccuracy to the defender's current action, or compounding inaccuracy to the next set of attacks.","They look dizzy!"),
	TRIPPED("Trip","Defender's action will take %1$d instants longer, and applies %2$d -10%% dodge Shaky stacks.","They are tripped!"),
	CRUSHED("Crush","Deals %1$d direct damage.","They are crushed!"),
	KO("Knockout","Deals %1$d direct damage, but defender heals after their next attack.","It's a knockout!"),
	BLEED_BLUNT("Trauma","Applies %1$d stacks of bleed, around %2$d damage per tick.","Their insides get smashed."),
	MAJOR_BLEED_BLUNT("Fracture","Applies %1$d stacks of bleed, around %2$d damage per tick, and prevents bleed from healing.","Their insides get crushed!"),
	//soft decap'd wounds, used mostly as weird condwounds
	I_BLEED("Fracture","Applies a stacking %2$d bleed, expected %1$d for this stack against this attacker.","Their insides get crushed!"),
	
	//elemental
	SCALDED("Scald","Deals %1$d direct damage and burns defender's armor by %2$d%%.","They are scalded by the flames!"),//TODO: more elemental wounds
	BLACKENED("Blacken","Burns defender's armor by %1$d%%.","Their armor burns!"),
	SCREAMING("Scream","Defender loses one attack choice and applies %1$d -10%% dodge Shaky stacks.","They scream!"),
	FROSTED("Frost","Defender's action takes %1$d%% longer on the current time, up to %2$d instants increase.","They are frozen over..."),
	FROSTBITE("Frostbite","Deals %1$d direct damage and applies %2$d -10%% dodge Shaky stacks.","Their flesh is frozen!"),
	JOLTED("Jolt","Defender's action takes %1$d instants longer.","They are jolted!"),
	SHIVERING("Shiver","Applies %1$d%% compounding inaccuracy to the defender's next set of attacks, and applies %2$d -10%% dodge Shaky stacks.","They shiver at the intense cold..."),
	STATIC("Static","Removes all stacks of Advantage and Bonus Weapon Attacks, and applies %1$d -10%% dodge Shaky stacks.","The static dazes them..."),
	//exotic
	TEAR("Tear","Decreases defender's dodge by %1$%d%%, stacking.","Their wing is torn!"), //see if need to add a '%'
	MANGLED("Mangle","Halves the condition of the targeted body part.","Their body is mangled!"),
	BLOODY("Bloody","The current attack will be %1$d%% less accurate, or the next set half that compounding. Applies %2$d stacks of bleed, around %3$d damage per tick.","Blood wells around their eyes!"),
	
	//perma 'condition loss' wounds
	DEPOWER("Depower","Injury: Removes special abilities.","Depowered!"),
	MAIMED("Maim","Injury: Removes one attack choice each attack. Doesn't stack with Disarmed.","Maimed!"),
	CRIPPLED("Cripple","Injury: Set to 80% of dodge mult. Stacks.","Crippled!"),
	HIT_VITALS("Shatter","Injury: Takes double condition damage, and parts with low condition cause another wound to be inflicted.","Shattered!"),//the unholy matrimony of dd1 and weaverdice wounds
	BRAINED("Brain","Injury: KO wounds no longer heal. Also inflicts KO at %1$d.","Split skull!"),
	//undead condwounds which are less perma
	SHINE("Shine","Deals %1$d direct damage and burn defender's armor by %2$d%%.","Their body shines with holy flame!"),
	GLOW("Glow","Applies %1$d%% compounding inaccuracy to the defender's next set of attacks and burn armor by %2$d%%.","Their body glows with holy light!")
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