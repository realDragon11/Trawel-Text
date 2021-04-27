package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import scimech.combat.ResistMap;
import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.extra;

public abstract class Systems implements TurnSubscriber{

	public abstract int getComplexity();
	
	protected boolean powered = false;
	
	public int activated = 0;
	
	protected int damage = 0;//max 100
	public Mech currentMech;
	
	public abstract int heatCap();
	
	public void takeDamage(int toTake) {
		damage = extra.clamp(damage+toTake, 0, 100);
	}
	
	public float rating() {
		float total = ((100f-damage)/100f)*currentMech.rating();
		return total;
	}
	
	public abstract ResistMap resistMap();
	
	@Override
	public void roundStart() {
		activated = 0;
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
			return sys.getName() + ": " + (sys.powered ? "on" : "off") + (damString == null ? "" : " " + damString) + sys.getTitleAdditions();
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
				return mList;
			}});
	}

	public abstract String getTitleAdditions();

	public abstract String getName();
	public abstract String getDescription();
	public abstract int getEnergyDraw();

}
