package trawel.personal.item;

import java.util.regex.Pattern;

import derg.strings.fluffer.StringResult;
import derg.strings.random.SRPlainRandom;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.classless.Skill;

public class Potion implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	public byte sips;
	public final Effect effect;
	
	public Potion(Effect e, int s) {
		effect = e;
		sips = (byte)s;
	}
	
	public boolean sip(Person p) {
		if (sips > 0) {
			p.addEffect(effect);
			sips--;
			if (!Print.getPrint()) {
				boolean personal = p.isPlayer();
				boolean overwrite = false;
				switch (effect) {
				case SIP_GRAVE_ARMOR:
					uncork(p,personal);
					if (personal) {
						Print.println("Your body turns dark and foggy as you are embraced by the cold dead earth!");
					}else {
						Print.println(p.getName() + " form shifts in a haze, covered in foggy dark soil!");
					}
					overwrite = true;
					Print.println(" " +effect.getDisp());
					Print.println("  " +Effect.PADDED.getDisp());
					Print.println("  " +Effect.STERN_STUFF.getDisp());
					
					p.removeEffectAll(Effect.SIP_GRAVE_ARMOR);
					p.addEffectCount(Effect.PADDED,2);
					p.addEffect(Effect.STERN_STUFF);
					break;
				case STERN_STUFF:
					uncork(p,personal);
					if (personal) {
						Print.println("You feel resolved to survive!");
					}else {
						Print.println(p.getName() + " steels themselves!");
					}
					break;
				case CURSE:
					uncork(p,personal);
					Print.println(personal ? "You feel sick to your stomach..." : p.getName() + " looks like they're about to throw up!");
					break;
				case BEE_SHROUD:
					Print.println("Bees swarm, defending " + (personal ? "you" : p.getName())+"!");
					break;
				case BEES:
					if (personal) {
						Print.println("You uncork your potion, but it was filled with BEEEEEEEEEEES!!!");
						if (p.isPlayer()) {
							Networking.unlockAchievement("bees_hive");
						}
					}else {
						Print.println(p.getName() + " uncorks their potion, but only angry bees pour out!");
					}
					break;
				case HASTE:
					uncork(p,personal);
					if (personal) {
						Print.println("You feel a lot peppier!");
					}else {
						Print.println(p.getName() + " gains some extra pep in their step!");
					}
					break;
				case HEARTY:
					uncork(p,personal);
					if (personal) {
						Print.println("That was tasty, you feel full and hearty!");
					}else {
						Print.println(p.getName() + " gains some extra pep in their step!");
					}
					if (p.hasSkill(Skill.CHEF)) {
						overwrite = true;
						Print.println(" " +effect.getDisp());
						Print.println(" " +Effect.PADDED.getDisp());
						p.addEffect(Effect.PADDED);
					}
					break;
				case SUDDEN_START:
					uncork(p,personal);
					if (personal) {
						Print.println("You feel like you can take on the world!");
					}else {
						Print.println(p.getName() + " surges forward, filled with purpose!");
					}
					overwrite = true;
					Print.println(" " +effect.getDisp());
					Print.println("  " +Effect.ADVANTAGE_STACK.getDisp());
					Print.println("  " +Effect.BONUS_WEAP_ATTACK.getDisp());
					break;
				case BLEED:
					uncork(p,personal);
					if (personal) {
						Print.println("Your skin starts to bleed!");
					}else {
						Print.println(p.getName() + "'s skin starts bleeding...");
					}
					p.addEffectCount(Effect.BLEED,p.getMaxHp()/10);//10% of MHP (plus one)
					break;
				case MAJOR_BLEED:
					uncork(p,personal);
					if (personal) {
						Print.println("Lacerations appear on your body that weren't there before!");
					}else {
						Print.println(p.getName() + " is suddenly covered with intense lacerations!");
					}
					p.addEffectCount(Effect.BLEED,p.getMaxHp()/5);//20% of MHP
					break;
				default:
					uncork(p,personal);
					Print.println(personal ? "Your potion applied the " + effect.getName() +TrawelColor.PRE_WHITE+ " effect to you!"
							: "The potion applied the " +effect.getName() +TrawelColor.PRE_WHITE+ " effect to "+p.getName()+"!");
					break;
				}
				if (!overwrite) {
					Print.println(" " +effect.getDisp());
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
		Print.println(str);
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
