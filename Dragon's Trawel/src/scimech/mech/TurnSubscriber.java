package scimech.mech;

import scimech.combat.Target;

public interface TurnSubscriber {

	public void activate(Target t, TurnSubscriber ts);
	
	public void roundStart();
}
