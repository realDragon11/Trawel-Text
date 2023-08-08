package trawel.personal.item;

import trawel.Effect;
import trawel.extra;
import trawel.personal.Person;

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
			if (p.isPlayer() && effect.equals(Effect.CURSE)) {
				extra.println("You feel sick to your stomach...");
			}
			return true;
		}
		return false;
	}
}
