package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import scimech.combat.ResistMap;
import scimech.combat.Target;
import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.extra;

public abstract class Systems implements TurnSubscriber{

	public abstract int getComplexity();
	
	protected boolean powered = true;
	protected boolean passive = false;
	protected boolean oneUse = false;
	
	public int uses = 0;
	public int activated = 0;
	
	protected int damage = 0, empDamage = 0;//max 100
	public Mech currentMech;
	
	public void takeDamage(int toTake) {
		damage = extra.clamp(damage+toTake, 0, 100);
	}
	
	public void takeEMPDamage(int toTake) {
		empDamage = extra.clamp(empDamage+toTake, 0, 100);
	}
	
	public void empDecay() {
		empDamage/=2;
	}
	
	
	public float rating() {
		float total = ((100f-Math.min(damage+empDamage,100))/100f)*currentMech.rating();
		return total;
	}
	
	public Systems me() {
		return this;
	}
	
	public boolean isPassive() {
		return passive;
	}
	public abstract ResistMap resistMap();
	
	@Override
	public void roundStart() {
		activated = 0;
		if (passive && powered) {
			if (this.getEnergyDraw() <= 0 || currentMech.energy >= this.getEnergyDraw()) {
				this.activate(currentMech,this);
			}
		}
		this.empDecay();
	}
	
	public class MenuSystem extends MenuSelect {//can be extended further

		public Systems sys;
		@Override
		public String title() {
			String damString = null;
			if (damage > 30) {
				if (damage > 60) {
					if (damage > 90) {
						damString = "destroyed";
					}else {
						damString = "damaged";
					}
				}else {
					damString = "scratched";
				}
			}
			return sys.getName() + ": " + (sys.powered ? "on" : "off") + (damString == null ? "" : " " + damString) +(sys.empDamage > 10 ? " EMP: " +(sys.empDamage/10)*10 : "") +  sys.getTitleAdditions();
		}

		@Override
		public boolean go() {
			sys.examine();
			return false;
		}}
	
	
	public void examine() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				if (empDamage > 10) {
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return "EMP: " + (empDamage/10)*10;
						}});
				}
				mList.add(new MenuLine() {

					@Override
					public String title() {
						String damString = "";
						if (damage > 30) {
							if (damage > 60) {
								if (damage > 90) {
									damString = "destroyed";
								}else {
									damString = "damaged";
								}
							}else {
								damString = "scratched";
							}
						}
						return getName() + ": " + (powered ? "on" : "off") + (damString == null ? "" : " " + damString) +getTitleAdditions();
					}
				});
				
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return getDescription();
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
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "toggle power: " + getEnergyDraw();
					}

					@Override
					public boolean go() {
						powered = !powered;
						return false;
					}});
				if (activated == 0 && (getEnergyDraw() <= 0 || currentMech.energy >= getEnergyDraw())) {
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "burst power: " + getEnergyDraw();
					}

					@Override
					public boolean go() {
						activate(currentMech,me());
						return false;
					}});
				}
				return mList;
			}});
	}

	public abstract String getTitleAdditions();

	public abstract String getName();
	public abstract String getDescription();
	public abstract int getEnergyDraw();
	
	@Override
	public void activate(Target t, TurnSubscriber ts) {
		currentMech.energy -= this.getEnergyDraw();
		activated++;
		this.activateInternal(t, ts);
	}

	protected abstract void activateInternal(Target t, TurnSubscriber ts);

	public abstract int getWeight();
	
	public void repair(int rep) {
		damage = extra.clamp(damage-rep, 0, 100);
	}
	
	public void repairLimit(int rep, int limit) {
		while (damage < limit && rep > 0) {
		damage = extra.clamp(damage-1, 0, 100);
		rep--;
		}
	}

}
