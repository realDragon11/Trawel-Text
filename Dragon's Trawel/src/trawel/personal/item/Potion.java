package trawel.personal.item;

import trawel.Effect;
import trawel.extra;
import trawel.personal.Person;

import java.util.regex.Pattern;

import derg.SRPlainRandom;
import derg.StringResult;

public class Potion implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	public byte sips;
	public final Effect effect;
	
	public Potion(Effect e, int s) {
		effect = e;
		sips = (byte)s;
	}
	
	public boolean sip(Person p) {//FIXME: give ai superpeople potions
		if (sips > 0) {
			p.addEffect(effect);
			sips--;
			if (!extra.getPrint()) {
				boolean personal = p.isPlayer();
				boolean overwrite = false;
				switch (effect) {
				case CURSE:
					uncork(p,personal);
					extra.println(personal ? "You feel sick to your stomach..." : p.getName() + " looks like they're about to throw up!");
					break;
				case BEE_SHROUD:
					extra.println("Bees swarm, defending " + (personal ? "you" : p.getName())+"!");
					break;
				case BEES:
					if (personal) {
						extra.println("You uncork your potion, but it was filled with BEEEEEEEEEEES!!!");
					}else {
						extra.println(p.getName() + " uncorks their potion, but only angry bees pour out!");
					}
					break;
				case HASTE:
					uncork(p,personal);
					if (personal) {
						extra.println("You feel a lot peppier!");
					}else {
						extra.println(p.getName() + " gains some extra pep in their step!");
					}
					break;
				case HEARTY:
					uncork(p,personal);
					if (personal) {
						extra.println("That was tasty, you feel full and hearty!");
					}else {
						extra.println(p.getName() + " gains some extra pep in their step!");
					}
					break;
				case SUDDEN_START:
					uncork(p,personal);
					if (personal) {
						extra.println("You feel like you can take on the world!");
					}else {
						extra.println(p.getName() + " surges forward, filled with purpose!");
					}
					overwrite = true;
					extra.println(" " +effect.getName() + ": " +effect.getDesc());
					extra.println("  " +Effect.ADVANTAGE_STACK.getName() + ": " +Effect.ADVANTAGE_STACK.getDesc());
					extra.println("  " +Effect.BONUS_WEAP_ATTACK.getName() + ": " +Effect.BONUS_WEAP_ATTACK.getDesc());
					break;
				case BLEED:
					uncork(p,personal);
					if (personal) {
						extra.println("Your skin starts to bleed!");
					}else {
						extra.println(p.getName() + "'s skin starts bleeding...");
					}
					break;
				case MAJOR_BLEED:
					uncork(p,personal);
					if (personal) {
						extra.println("Lacerations appear on your body that weren't there before!");
					}else {
						extra.println(p.getName() + " is suddenly covered with intense lacerations!");
					}
					break;
				default:
					uncork(p,personal);
					extra.println(personal ? "Your potion applied the " + effect.getName() + " effect to you!" : "The potion applied the " +effect.getName() + " effect to "+p.getName()+"!");
					break;
				}
				if (!overwrite) {
					extra.println(" " +effect.getName() + ": " +effect.getDesc());
				}
				
				
			}
			return true;
		}
		return false;
	}
	
	public static void uncork(Person p,boolean personal) {
		String str = uncorkNormalFluff.next();
		if (!personal) {
			str = str.replaceAll("You",p.getName());
			str = str.replaceAll(Pattern.quote("(s)"),"s");
		}else {
			str = str.replaceAll(Pattern.quote("(s)"),"");
		}
		extra.println(str);
	}
	
	private static final StringResult uncorkNormalFluff = new SRPlainRandom(
			"You uncork(s) the potion and take a sip..."
			,"You take(s) a deep draft of potion."
			,"You gulp(s) greedily from the flask"
			,"You chug(s) a lot of fluid straight from the bottle."
			,"You quaff(s) the unlabeled bottle."//nethack joke
			,"You take(s) a swig from the flask."
			,"You lean(s) back and drink deep, as if taking a shot."
			,"You get(s) a mouthful of potion."
			,"You guzzle(s) down a lot of liquid."
			);
}
