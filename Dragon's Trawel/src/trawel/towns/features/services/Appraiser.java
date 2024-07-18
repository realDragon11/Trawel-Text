package trawel.towns.features.services;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.item.Item;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.contexts.World;
import trawel.towns.data.FeatureData;
import trawel.towns.features.Feature;

public class Appraiser extends Feature {
	
	static {
		FeatureData.registerFeature(Appraiser.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" examine gear to fix "+Effect.DAMAGED.getName()+".");
			}
			
			@Override
			public int priority() {
				return 30;
			}
			
			@Override
			public String name() {
				return "Appraiser";
			}
			
			@Override
			public String color() {
				return TrawelColor.F_SERVICE;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.VITAL_SERVICES;
			}
		});
	}

	private static final long serialVersionUID = 1L;

	public Appraiser(String name) {
		this.name = name;
	}
	
	@Override
	public String nameOfType() {
		return "Appraiser";
	}
	
	@Override
	public Area getArea() {
		return Area.MISC_SERVICE;
	}
	
	@Override
	public void go() {
		Input.menuGo(new MenuGenerator() {
			
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_FREE+"Examine gear.";
					}

					@Override
					public boolean go() {
						Input.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								final List<MenuItem> list = new ArrayList<MenuItem>();
								Consumer<Item> listadd = new Consumer<Item>() {
									
									@Override
									public void accept(Item t) {
										list.add(new MenuSelect() {

											@Override
											public String title() {
												return t.getName();
											}

											@Override
											public boolean go() {
												t.display(2);
												return false;
											}});
									}
								};
								Player.player.getPerson().getBag().getSolids().forEachOrdered(listadd);
								list.add(new MenuBack());
								return list;
							}
						});
						return false;
					}});
				if (Player.player.getPerson().hasEffect(Effect.DAMAGED)) {
					int repairCost = (int) Math.ceil(getUnEffectiveLevel());
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_CURRENCY+"Repair Damaged Effect. ("+World.currentMoneyDisplay(repairCost)+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= repairCost) {
								Player.player.loseGold(repairCost);
								Print.println(TrawelColor.RESULT_PASS+"They tinker with your equipment.");
								//next call displays the effect results
								Player.player.getPerson().repairEffects();
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You can't afford that!");
							}
							return false;
						}});
				}else {
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "Equipment not damaged.";
						}});
				}
				list.add(new MenuBack());
				return list;
			}
		});
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		return null;
	}

}
