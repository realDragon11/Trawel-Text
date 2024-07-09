package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import scimech.handlers.Savable;
import trawel.helper.methods.extra;

public abstract class Fixture extends MechPart implements TurnSubscriber, Savable{
	
	//TODO: don't save this, save a text representation of the name, power state, and damage state

	public boolean powered = true;
	public boolean overclocked = false;
	protected int damage = 0, empDamage = 0;//max 100, 200
	public Mount currentMount;
	public void heatCheck(int heat) {
		if (heat > heatCap()) {
			int over = (heat-heatCap())-1;
			takeDamage((int)Math.pow(2,over));
		}
	}
	
	public abstract int heatCap();
	
	public void takeDamage(int toTake) {
		damage = extra.clamp(damage+toTake, 0, 100);
	}
	
	public void takeEMPDamage(int toTake) {
		empDamage = extra.clamp(empDamage+toTake, 0, 200);
	}
	public void empDecay() {
		empDamage/=2;
	}
	
	public float rating() {
		float total = ((100f-Math.min(100,damage+empDamage))/100f);
		if (damage < 80 && overclocked) {
			total += 0.2f;
		}
		return total;
	}
	
	public class MenuFixture extends MenuSelect {//can be extended further

		public Fixture fix;
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
			return fix.getName() + ": " + (fix.powered ? "on" : "off") + (fix.overclocked ? "OVERCLOCKED" : "") + (damString == null ? "" : " " + damString)+(fix.empDamage > 10 ? " EMP: " +(fix.empDamage/10)*10 : "") + fix.getTitleAdditions();
		}

		@Override
		public boolean go() {
			fix.examine();
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
						return getName() + ": " + (powered ? "on" : "off") + (overclocked ? "OVERCLOCKED" : "") + (damString == null ? "" : " " + damString) +getTitleAdditions();
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
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "toggle overclock";
					}

					@Override
					public boolean go() {
						overclocked = !overclocked;
						return false;
					}});
				return mList;
			}});
		
	}
	public String getTitleAdditions() {
		return "";
	}

	public abstract String getName();
	public abstract int getEnergyDraw();
	public abstract String getDescription();
	public abstract int getBaseComplexity();
	public abstract int getWeight();
	public abstract int getSlots();
	
	public int getComplexity() {
		return (locked ? 0 : getBaseComplexity());
	}
	//activate is abstract, remember to check to see if it's a dummy before applying heat to yourself

	public void repair(int rep) {
		damage = extra.clamp(damage-rep, 0, 100);
	}

	public void repairLimit(int rep, int limit) {
		while (damage < limit && rep > 0) {
		damage = extra.clamp(damage-1, 0, 100);
		rep--;
		}
	}
	

	@Override
	public String saveString() {
		return this.getClass().getName()+"&" + (locked ? "L" : "");
	}
	
	public static Fixture internalDeserial(String s,Fixture add){
		String edit;
		if (s.charAt(s.length()-1) == 'L') {
			add.locked = true;
		}
		
		return add;
	}
}
