package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import scimech.combat.Target;
import scimech.mech.Fixture.MenuFixture;
import trawel.MenuGeneratorPaged;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.extra;

public abstract class Mount implements TurnSubscriber{

	protected int slots, heat = 0;
	protected List<Fixture> fixtures = new ArrayList<Fixture>();
	public Mech currentMech;
	
	@Override
	public void activate(Target t, TurnSubscriber ts) {
		for (Fixture f: fixtures) {
			if (f.powered) {
				f.activate(t,this);
			}
		}
	}
	
	public void takeHeat(int amount) {
		heat+=amount;
		currentMech.takeHeat(amount);
	}
	
	public void roundStart() {
		for (Fixture f: fixtures) {
			if (f.overclocked) {
				f.heatCheck(heat*2);
			}else {
				f.heatCheck(heat);
			}
			f.roundStart();
		}
		heat /=2;
	}
	
	public void togglePowerAll(boolean state) {
		for (Fixture f: fixtures) {
			f.powered = state;
		}
	}
	
	public int getEnergyDraw() {
		int total = 0;
		for (Fixture f: fixtures) {
			total += f.getEnergyDraw();
		}
		return total;
	}
	
	public void examine() {
		extra.menuGoPaged(new MenuGeneratorPaged(){

			@Override
			public List<MenuItem> gen() {
				this.header = new MenuLine() {

					@Override
					public String title() {
						return "heat: "  + heat + " draw: " + getEnergyDraw();
					}};
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}});
				for (Fixture f: fixtures) {
					MenuFixture mf = f.new MenuFixture();
					mf.fix = f;
					mList.add(mf);
				}
				return mList;
			}});
		
	}
}
