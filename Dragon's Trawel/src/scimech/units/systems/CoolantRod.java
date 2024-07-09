package scimech.units.systems;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;
import trawel.helper.methods.extra;

public class CoolantRod extends Systems {

	public CoolantRod() {
		passive = false;
		powered = true;
	}
	
	@Override
	public int getBaseComplexity() {
		return 1;
	}

	@Override
	public ResistMap resistMap() {
		return null;
	}

	@Override
	public String getTitleAdditions() {
		return (uses == 0 ? " waiting" : " used" );
	}

	@Override
	public String getName() {
		return "Coolant Rod";
	}

	@Override
	public String getDescription() {
		return "Clears up to 8 heat when used. Single use.";
	}

	@Override
	public int getEnergyDraw() {
		return 1;
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		if (uses > 0) {
			return;
		}
		currentMech.clearHeat((int)(rating()*8));
		
		uses++;
	}

	@Override
	public int getWeight() {
		return 3;
	}
	
	@Override
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
						return getName() + ": " + (damString == null ? "" : " " + damString) +getTitleAdditions();
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
				if (uses == 0 && currentMech.energy >= getEnergyDraw()) {
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "use";
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
	
	public static Savable deserialize(String s) {
		return Systems.internalDeserial(s,new CoolantRod());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
