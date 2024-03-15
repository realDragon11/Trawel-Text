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
	HAMSTRUNG("Hamstrung","Delays the defender's next attack by %1$d instants.","Their leg is hamstrung!"), 
	BLINDED("Blind","Inflicts %1$d%% inaccuracy on the current attack, or half that to the next set of attack choices as compounding inaccuracy.","They can't see!"),
	CONFUSED("Confuse","Forces the defender to retarget.","They look confused!"), 
	DIZZY("Dizzy","Inflicts %1$d%% inaccuracy to the defender's current action, or compounding inaccuracy to the next set of attacks.","They look dizzy!"),
	SLICE("Slice","Attacker's next action will happen %1$d%% quicker and be %2$d%% more accurate.","They are sliced!"),
	DICE("Dice","Attacker's next action will happen %1$d%% and %2$d instants sooner.","They are diced!"),
	WINDED("Wind","Defender's action will take %1$d instants longer.","The wind is knocked out of them!"),
	BLEED("Cut","Applies %1$d stacks of bleed, around %2$d damage per tick.","They bleed..."),
	MAJOR_BLEED("Lacerate","Applies %1$d stacks of bleed, around %2$d damage per tick, and prevents bleed from healing.","An artery is cut!"),
	I_BLEED("Fracture","Applies a stacking %2$d bleed, expected %1$d for this stack against this attacker.","Their insides get crushed!"),
	I_BLEED_WEAK("Trauma","Applies a stacking %2$d bleed, expected %1$d for this stack against this attacker.","Their insides get smashed."),
	DISARMED("Disarm","Defender loses one attack choice on next action.","Their attack is put off-kilter!"),		
	TRIPPED("Trip","Defender's action will take %1$d instants longer.","They are tripped!"),
	KO("Knockout","Deals %1$d direct damage, but defender heals after their next attack.","It's a knockout!"),
	HACK("Hack","Deals up to %1$d direct damage based on unblocked damage to defender.","It's a wicked hack!"),
	TAT("Puncture","Deals up to %1$d direct damage, based on final pierce damage to defender and to-hit.","The blow goes right through them!"),
	CRUSHED("Crush","Deals %1$d direct damage.","They are crushed!"),
	
	//elemental
	SCALDED("Scald","Deals %1$d direct damage.","They are scalded by the flames!"),//TODO: more elemental wounds
	BLACKENED("Blacken","Burns defender's armor by %1$d%%.","Their armor burns!"),
	SCREAMING("Scream","Defender loses one attack choice on next action.","They scream!"),
	FROSTED("Frost","Defender's action takes %1$d%% longer on the current time, up to %2$d instants increase.","They are frozen over..."),
	FROSTBITE("Frostbite","Deals %1$d direct damage.","Their flesh is frozen!"),
	JOLTED("Jolt","Defender's action takes %1$d instants longer.","They are jolted!"),
	SHIVERING("Shiver","Applies %1$d%% compounding inaccuracy to the defender's next set of attacks.","They shiver at the intense cold..."),
	
	//exotic
	TEAR("Tear","Decreases defender's dodge by %1$%d%%, stacking.","Their wing is torn!"), //see if need to add a '%'
	MANGLED("Mangle","Halves the condition of the targeted body part.","Their body is mangled!"),
	BLOODY("Bloody","The current attack will be %1$d%% less accurate, or the next set half that compounding. Applies %2$d stacks of bleed, around %3$d damage per tick.","Blood wells around their eyes!"),
	
	//perma 'condition loss' wounds
	DEPOWER("Depower","Injury: Removes special abilities.","Depowered!"),
	MAIMED("Maim","Injury: Removes one attack choice each attack. Doesn't stack with Disarmed.","Maimed!"),
	CRIPPLED("Cripple","Injury: Set to 80% of dodge mult. Stacks.","Crippled!"),
	HIT_VITALS("Shatter","Injury: Takes double condition damage, and parts with low condition cause another wound to be inflicted.","Shattered!"),//the unholy matrimony of dd1 and weaverdice wounds
	BRAINED("Brain","Injury: KO wounds no longer heal. Also inflicts KO at %1$d.","Split skull!")
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