package scimech.units.systems;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;
import trawel.helper.methods.extra;

public class Plating extends Systems {
	
	public Plating() {
		this.passive = true;
		this.powered = true;
	}

	@Override
	public int getBaseComplexity() {
		return 1;
	}

	@Override
	public ResistMap resistMap() {
		//normally would check for powered
		ResistMap map = new ResistMap();
		map.isSub = true;
		float r = rating();
		//map.put(DamageMods.AP, 1f, 1f);
		map.put(DamageMods.NORMAL, extra.lerp(1f, 0.9f,r), extra.lerp(1f, 0.9f,r));
		map.put(DamageMods.HOLLOW, extra.lerp(1f, 0.7f,r), extra.lerp(1f, 0.7f,r));
		return map;
	}

	@Override
	public String getTitleAdditions() {
		return "";
	}

	@Override
	public String getName() {
		return "plating";
	}

	@Override
	public String getDescription() {
		return "Provides protection without an energy cost, but slightly impairs your speed.";
	}

	@Override
	public int getEnergyDraw() {
		return 0;
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		currentMech.addSpeed(-0.5f);
	}

	@Override
	public int getWeight() {
		return 3;
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
		return Systems.internalDeserial(s,new Plating());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}
	
}
