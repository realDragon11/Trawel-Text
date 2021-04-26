package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import scimech.combat.Target;

public abstract class Mount implements TurnSubscriber{

	protected int slots, heat = 0;
	protected List<Fixture> fixtures = new ArrayList<Fixture>();
	
	@Override
	public void activate(Target t, TurnSubscriber ts) {
		for (Fixture f: fixtures) {
			if (f.powered) {
				f.activate(t,this);
			}
		}
	}
	
	public void takeHeat(int amount) {
		
	}
	
	public void roundStart() {
		for (Fixture f: fixtures) {
			if (f.overclocked) {
				f.heatCheck(heat*2);
			}else {
				f.heatCheck(heat);
			}
		}
	}
}
