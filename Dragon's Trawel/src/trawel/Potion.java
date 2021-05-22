package trawel;

public class Potion implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
