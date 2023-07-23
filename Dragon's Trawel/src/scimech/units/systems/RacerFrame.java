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
import trawel.extra;

public class RacerFrame extends Systems {
	
	public RacerFrame() {
		this.passive = true;
		this.powered = true;
	}

	@Override
	public int getBaseComplexity() {
		return 5;
	}

	@Override
	public ResistMap resistMap() {
		return null;
	}

	@Override
	public String getTitleAdditions() {
		return "";
	}

	@Override
	public String getName() {
		return "Racer Frame";
	}

	@Override
	public String getDescription() {
		return "Increases speed.";
	}

	@Override
	public int getEnergyDraw() {
		return 0;
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		currentMech.addSpeed(3);
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public void examine() {//cannot turn off
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
				return mList;
			}});
	}
	
	public static Savable deserialize(String s) {
		return Systems.internalDeserial(s,new RacerFrame());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.SARATOGA_SYSTEMS;
	}
	
	@Override
	public int installLimit() {
		return 1;
	}
	
}
