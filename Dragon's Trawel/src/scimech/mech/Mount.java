package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import scimech.combat.Target;
import scimech.mech.Fixture.MenuFixture;
import trawel.MenuGenerator;
import trawel.MenuGeneratorPaged;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.extra;

public abstract class Mount implements TurnSubscriber{

	protected int heat = 0;
	protected List<Fixture> fixtures = new ArrayList<Fixture>();
	public Mech currentMech;
	public boolean fired = false;
	
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
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
					mList.add( new MenuLine() {
					@Override
					public String title() {
						return "heat: "  + heat + " draw: " + getEnergyDraw();
					}});
				
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}});
				if (fired == false) {
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "activate";
					}

					@Override
					public boolean go() {
						return targeting();
					}});
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "manage fixtures";
					}

					@Override
					public boolean go() {
						manageFixtures();
						return true;
					}});
				
				return mList;
			}});
		
	}
	public abstract String getName();
	public boolean targeting() {
		fired = true;
		return fired;//TODO: Method stubby wubby
	}
	
	public void manageFixtures() {
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
	
	public class MenuMount extends MenuSelect {//can be extended further

		@Override
		public String title() {
			int damageSum = 0;
			for (Fixture f: fixtures) {
				damageSum += f.damage;
			}
			damageSum/=fixtures.size();
			String damString = null;
			if (damageSum > 30) {
				if (damageSum > 60) {
					if (damageSum > 90) {
						damString = "destroyed";
					}else {
						damString = "damaged";
					}
				}else {
					damString = "scratched";
				}
			}
			return "heat: "  + heat + " draw: " + getEnergyDraw() + (damString == null ? "" : " " + damString);
		}
		

		@Override
		public boolean go() {
			examine();
			return false;
		}}
	
	public abstract int baseWeight();
	
	public int getWeight() {
		int total = baseWeight();
		for (Fixture f: fixtures) {
			total += f.getWeight();
		}
		return total;
	}
	
	public abstract int baseComplexity();
	
	public int getComplexity() {
		int total = baseComplexity();
		for (Fixture f: fixtures) {
			total += f.getComplexity();
		}
		return total;
	}
	
	public abstract int baseSlots();
	
	public int usedSlots() {
		int total = 0;
		for (Fixture f: fixtures) {
			total += f.getSlots();
		}
		return total;
	}
}
