package trawel.towns.misc;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.RaceFactory;
import trawel.personal.item.Seed;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.CanPassTime;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class PlantSpot implements java.io.Serializable, CanPassTime{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String contains = "";
	public double timer = 0;
	public int level;
	public PlantSpot(int tier) {
		level = tier;
	}
	public void go() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Contains: "+contains;
					}
				});
				if (contains.equals("")) {
					mList.add(new MenuSelect() {
	
						@Override
						public String title() {
							return "plant";
						}
	
						@Override
						public boolean go() {
							plant();
							return false;
						}
					});
					
				}else {
					final MenuItem harvestFunction;
					switch (contains) {
					case "apple tree":
						harvestFunction = new MenuSelect() {

							@Override
							public String title() {
								return "harvest apples";
							}

							@Override
							public boolean go() {
								timer-=20;
								Player.bag.addNewDrawBane(DrawBane.APPLE);
								if (extra.chanceIn(1,3)) {
									Player.bag.addSeed(Seed.APPLE);
								}
								if (timer <= 0) {
									contains = "exhausted apple tree";
								}
								return false;
							}
							
						};
						break;
					case "pumpkin patch":
						harvestFunction = new MenuSelect() {

							@Override
							public String title() {
								return "harvest pumpkin";
							}

							@Override
							public boolean go() {
								timer-=35;
								Player.bag.addNewDrawBane(DrawBane.PUMPKIN);
								if (extra.chanceIn(1,3)) {
									Player.bag.addSeed(Seed.PUMPKIN);
								}
								if (timer <= 0) {
									contains = "empty pumpkin patch";
								}
								return false;
							}
							
						};
						break;
					case "bee hive":
						harvestFunction = new MenuSelect() {

							@Override
							public String title() {
								return "attempt to harvest honey";
							}

							@Override
							public boolean go() {
								timer=-20;
								switch (extra.randRange(1,6)) {
								case 1:
									extra.println("The bees sting!");
									Player.player.getPerson().addEffect(Effect.BEES);
									break;
								case 2:
									extra.println("You escape unscathed but with wounded pride.");
									break;
								case 3:
									Player.bag.addNewDrawBane(DrawBane.WAX);
									break;
								case 4:
									Player.bag.addNewDrawBane(DrawBane.HONEY);
									break;
								case 5:
									Player.bag.addNewDrawBane(DrawBane.HONEY);
									Player.bag.addNewDrawBane(DrawBane.WAX);
									break;
								case 6:
									Player.bag.addSeed(Seed.BEE);
									Player.bag.addNewDrawBane(DrawBane.HONEY);
									break;
								}
								contains = "angry bee hive";
								return false;
							}
							
						};
						break;
					default:
						harvestFunction = null;
						break;
					}
					mList.add(new MenuSelect() {
						
						@Override
						public String title() {
							return "take" + (harvestFunction != null ? " all" : "");
						}
	
						@Override
						public boolean go() {
							take();
							return false;
						}
					});
					if (harvestFunction != null) {
						mList.add(harvestFunction);
					}
				}
				mList.add(new MenuSelect() {
					
					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}
		});
		boolean breakit = false;
		while (!breakit) {
			extra.println("Contains: " + contains);
			extra.println(contains.equals("") ? "1 plant" : "1 take");
			extra.println("2 leave");
			switch (extra.inInt(2)) {
			case 1:
				if (contains.equals("")) {
					plant();
				}else {
					take();
				}
			case 2:
				breakit = true;
				break;
			}
		}
	}
	
	private void take() {
		switch (contains) {
		case "garlic": 
			Player.bag.addNewDrawBane(DrawBane.GARLIC);
			Player.bag.addSeed(Seed.GARLIC);
			break;
		case "apple tree": 
			Player.bag.addNewDrawBane(DrawBane.APPLE);
			Player.bag.addNewDrawBane(DrawBane.APPLE);
			Player.bag.addNewDrawBane(DrawBane.WOOD);
			Player.bag.addSeed(Seed.APPLE);
		break;
		case "exhausted apple tree":
			Player.bag.addNewDrawBane(DrawBane.WOOD);
			break;
		case "garlic seed": Player.bag.addSeed(Seed.GARLIC);break;
		case "apple seed": Player.bag.addSeed(Seed.APPLE);break;
		case "bee hive": 
			Player.bag.addNewDrawBane(DrawBane.HONEY);
			Player.bag.addNewDrawBane(DrawBane.WAX);
			Player.bag.addSeed(Seed.BEE);
		break;
		case "bee larva": Player.bag.addSeed(Seed.BEE);break;
		case "angry bee hive":Player.bag.addSeed(Seed.BEE); break;
		case "ent": 
			Networking.send("PlayDelay|sound_entmake|1|");
			mainGame.CombatTwo(Player.player.getPerson(),RaceFactory.makeEnt(level));
		break;
		case "ent sapling": Player.bag.addSeed(Seed.ENT);break;
		case "pumpkin patch": 
			Player.bag.addNewDrawBane(DrawBane.PUMPKIN);
			Player.bag.addSeed(Seed.PUMPKIN);
			Player.bag.addSeed(Seed.PUMPKIN);
		break;
		case "empty pumpkin patch": 
			Player.bag.addSeed(Seed.PUMPKIN);
		break;
		case "pumpkin seed": Player.bag.addSeed(Seed.PUMPKIN);break;
		case "eggcorn": 
			Player.bag.addNewDrawBane(DrawBane.EGGCORN);
			Player.bag.addSeed(Seed.EGGCORN);
		break;
		case "eggcorn seed": Player.bag.addSeed(Seed.EGGCORN);break;
		case "truffle": 
			Player.bag.addNewDrawBane(DrawBane.TRUFFLE);
		break;
		case "truffle spores":;break;
		default: case "":extra.println("ERROR");break;
		}
		contains = "";
		
	}

	private void plant() {
		timer = 0;
		Seed s = Player.bag.getSeed();
		if (s != null) {
		contains = s.toString().toLowerCase();
		}else {
			contains = "";
		}
		if (contains == null) {
			contains = "";
		}
	}

	@Override
	public List<TimeEvent> passTime(double t, TimeContext tc) {
		timer +=t;
		switch (contains) {
		case "garlic seed": if (timer > 57) { contains = "garlic";timer = 0;}break;
		case "apple seed": if (timer > 323) { contains = "apple tree";timer = 0;}break;
		case "bee larva": if (timer > 98) { contains = "bee hive";timer = 0;}break;
		case "ent sapling": if (timer > 630) { contains = "ent";timer = 0;}break;
		case "pumpkin seed": if (timer > 60) { contains = "pumpkin patch";timer = 0;}break;
		case "eggcorn seed": if (timer > 33) { contains = "eggcorn";timer = 0;}break;
		case "truffle spores": if (timer > 60) { contains = "truffle";timer = 0;}break;
		
		case "exhausted apple tree": if (timer >= 0) { contains = "apple tree";}break;
		case "empty pumpkin patch": if (timer >= 0) { contains = "pumpkin patch";}break;
		case "angry bee hive": if (timer >= 0) { contains = "bee hive";}break;
		}
		return null;
	}
}
