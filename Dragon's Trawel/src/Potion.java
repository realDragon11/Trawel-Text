
public class Potion {

	public int sips;
	public Effect effect;
	
	public Potion(Effect e, int s) {
		effect = e;
		sips = s;
	}
	
	public void sip(Person p) {
		if (sips > 0) {
		p.addEffect(effect);
		sips--;
		if (p.isPlayer() && effect.equals(Effect.CURSE)) {
			extra.println("You feel sick to your stomach...");
		}
		}
	}
}
