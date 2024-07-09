package trawel.towns.features.elements;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.battle.Combat;
import trawel.core.Networking;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.Seed;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.time.CanPassTime;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class PlantSpot implements java.io.Serializable, CanPassTime{

	private static final long serialVersionUID = 1L;
	public Seed contains;
	public double timer = 0;
	public int level;
	public PlantSpot(int tier) {
		level = tier;
		contains = Seed.EMPTY;
	}
	public PlantSpot(int tier,Seed starting) {
		level = tier;
		contains = starting;
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
				if (contains == Seed.EMPTY) {
					mList.add(new MenuSelect() {
	
						@Override
						public String title() {
							return "Plant Seed.";
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
					case GROWN_APPLE:
						harvestFunction = new MenuSelect() {

							@Override
							public String title() {
								return "Harvest apple.";
							}

							@Override
							public boolean go() {
								if (timer > 110) {
									timer = 110;//cap on how many can get stored
								}
								timer-=20;
								Player.bag.addNewDrawBanePlayer(DrawBane.APPLE);
								if (extra.chanceIn(1,3)) {
									Player.bag.addSeed(Seed.SEED_APPLE);
								}
								if (timer <= 0) {
									contains = Seed.HARVESTED_APPLE;
								}
								return false;
							}
							
						};
						break;
					case GROWN_PUMPKIN:
						harvestFunction = new MenuSelect() {

							@Override
							public String title() {
								return "Harvest pumpkin.";
							}

							@Override
							public boolean go() {
								if (timer > 80) {
									timer = 80;//cap on how many can get stored
								}
								timer-=35;
								Player.bag.addNewDrawBanePlayer(DrawBane.PUMPKIN);
								if (extra.chanceIn(1,3)) {
									Player.bag.addSeed(Seed.SEED_PUMPKIN);
								}
								if (timer <= 0) {
									contains = Seed.HARVESTED_PUMPKIN;
								}
								return false;
							}
							
						};
						break;
					case GROWN_BEE:
						harvestFunction = new MenuSelect() {

							@Override
							public String title() {
								return "Attempt to harvest honey.";
							}

							@Override
							public boolean go() {
								timer= -20;//flat
								switch (extra.randRange(1,6)) {
								case 1:
									extra.println(TrawelColor.RESULT_BAD+"The bees sting!");
									Player.player.getPerson().addEffect(Effect.BEES);
									Networking.unlockAchievement("bees_hive");
									break;
								case 2:
									extra.println(TrawelColor.RESULT_FAIL+"You escape unscathed but with wounded pride.");
									break;
								case 3:
									Player.bag.addNewDrawBanePlayer(DrawBane.WAX);
									break;
								case 4:
									Player.bag.addNewDrawBanePlayer(DrawBane.HONEY);
									break;
								case 5:
									Player.bag.addNewDrawBanePlayer(DrawBane.HONEY);
									Player.bag.addNewDrawBanePlayer(DrawBane.WAX);
									break;
								case 6:
									Player.bag.addSeed(Seed.SEED_BEE);
									Player.bag.addNewDrawBanePlayer(DrawBane.HONEY);
									break;
								}
								contains = Seed.HARVESTED_BEE;
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
							return "Take" + (harvestFunction != null ? " All." : ".");
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
				mList.add(new MenuBack());
				return mList;
			}
		});
	}
	
	private void take() {
		switch (contains) {
		case GROWN_GARLIC: 
			Player.bag.addNewDrawBanePlayer(DrawBane.GARLIC);
			Player.bag.addSeed(Seed.SEED_GARLIC);
			break;
		case GROWN_APPLE: 
			Player.bag.addNewDrawBanePlayer(DrawBane.APPLE);
			Player.bag.addNewDrawBanePlayer(DrawBane.APPLE);
			Player.bag.addNewDrawBanePlayer(DrawBane.WOOD);
			Player.bag.addSeed(Seed.SEED_APPLE);
		break;
		case HARVESTED_APPLE:
			Player.bag.addNewDrawBanePlayer(DrawBane.WOOD);
			break;
		case SEED_GARLIC: Player.bag.addSeed(Seed.SEED_GARLIC);break;
		case SEED_APPLE: Player.bag.addSeed(Seed.SEED_APPLE);break;
		case GROWN_BEE: 
			Player.bag.addNewDrawBanePlayer(DrawBane.HONEY);
			Player.bag.addNewDrawBanePlayer(DrawBane.WAX);
			Player.bag.addSeed(Seed.SEED_BEE);
			
			extra.println("The bees sting!");
			Player.player.getPerson().addEffect(Effect.BEES);
			Networking.unlockAchievement("bees_hive");
		break;
		case SEED_BEE: Player.bag.addSeed(Seed.SEED_BEE);break;
		case HARVESTED_BEE:Player.bag.addSeed(Seed.SEED_BEE); break;
		case GROWN_ENT: 
			Networking.send("PlayDelay|sound_entmake|1|");
			Combat c = Player.player.fightWith(RaceFactory.makeEnt(level));
			if (c.playerWon() > 0) {
				Player.bag.addNewDrawBanePlayer(DrawBane.WOOD);//in addition to normal loot
			}else {
				List<Person> list = c.getNonSummonSurvivors();//don't add ent if they died
				if (list.size() > 0) {
					for (Person p: list) {//should only be one, but
						Player.getPlayerWorld().addReoccuring(p.setOrMakeAgentGoal(AgentGoal.SPOOKY));
					}
				}
			}
		break;
		case SEED_ENT: Player.bag.addSeed(Seed.SEED_ENT);break;
		case GROWN_PUMPKIN: 
			Player.bag.addNewDrawBanePlayer(DrawBane.PUMPKIN);
			Player.bag.addSeed(Seed.SEED_PUMPKIN);
			Player.bag.addSeed(Seed.SEED_PUMPKIN);
		break;
		case HARVESTED_PUMPKIN: 
			Player.bag.addSeed(Seed.SEED_PUMPKIN);
		break;
		case SEED_PUMPKIN: Player.bag.addSeed(Seed.SEED_PUMPKIN);break;
		case GROWN_EGGCORN: 
			Player.bag.addNewDrawBanePlayer(DrawBane.EGGCORN);
			Player.bag.addSeed(Seed.SEED_EGGCORN);
		break;
		case SEED_EGGCORN: Player.bag.addSeed(Seed.SEED_EGGCORN);break;
		case GROWN_TRUFFLE: 
			Player.bag.addNewDrawBanePlayer(DrawBane.TRUFFLE);
		break;
		case SEED_TRUFFLE:
			if (extra.chanceIn(1,4)) {
				Player.bag.addSeed(Seed.SEED_TRUFFLE);
			}else {
				extra.println(TrawelColor.RESULT_FAIL+"You are unable to gather the spores.");
			}
			;break;
		case SEED_FAE:
			Player.bag.addNewDrawBanePlayer(DrawBane.GRAVE_DUST);//undead fairies?????
			break;
		case GROWN_FAE:
			Player.bag.addNewDrawBanePlayer(DrawBane.UNICORN_HORN);
			break;
		case SEED_FUNGUS:
			if (extra.chanceIn(1,3)) {
				Player.bag.addSeed(Seed.SEED_FUNGUS);
			}else {
				extra.println(TrawelColor.RESULT_FAIL+"You are unable to gather the spores.");
			}
			break;
		case GROWN_FUNGUS:
			if (extra.chanceIn(1,6)) {
				Player.bag.addNewDrawBanePlayer(DrawBane.GRAVE_DUST);
			}else {
				Player.bag.addNewDrawBanePlayer(DrawBane.GRAVE_DIRT);
			}
			break;
		case EMPTY:
			extra.println("EMPTY ERROR");
			break;
		}
		contains = Seed.EMPTY;
		
	}

	private void plant() {
		timer = 0;
		Seed s = Player.bag.getSeed();
		if (s != null) {
			contains = s;
		}else {
			contains = Seed.EMPTY;
		}
		if (contains == null) {
			contains = Seed.EMPTY;
		}
	}

	@Override
	public List<TimeEvent> passTime(double t, TimeContext tc) {
		timer +=t;
		switch (contains) {
		case SEED_GARLIC: if (timer > 57) { contains = Seed.GROWN_GARLIC;timer = 0;}break;
		case SEED_APPLE: if (timer > 323) { contains = Seed.GROWN_APPLE;timer = 0;}break;
		case SEED_BEE: if (timer > 98) { contains = Seed.GROWN_BEE;timer = 0;}break;
		case SEED_ENT: if (timer > 630) { contains = Seed.GROWN_ENT;timer = 0;}break;
		case SEED_PUMPKIN: if (timer > 60) { contains = Seed.GROWN_PUMPKIN;timer = 0;}break;
		case SEED_EGGCORN: if (timer > 33) { contains = Seed.GROWN_EGGCORN;timer = 0;}break;
		case SEED_TRUFFLE: if (timer > 60) { contains = Seed.GROWN_TRUFFLE;timer = 0;}break;
		case SEED_FAE: if (timer > 60) { contains = Seed.GROWN_FAE;timer = 0;}break;
		case SEED_FUNGUS: if (timer > 50) { contains = Seed.GROWN_FUNGUS;timer = 0;}break;
		
		case GROWN_FUNGUS:
			if (timer > 200) {
				//fungus lets other plant grow if not taken for grave dirt
				contains = extra.choose(Seed.SEED_GARLIC,Seed.SEED_TRUFFLE,Seed.SEED_PUMPKIN,Seed.SEED_APPLE);
				timer = 0;
			}
		break;
		
		case HARVESTED_APPLE: if (timer >= 0) { contains = Seed.GROWN_APPLE;}break;
		case HARVESTED_PUMPKIN: if (timer >= 0) { contains = Seed.GROWN_PUMPKIN;}break;
		case HARVESTED_BEE: if (timer >= 0) { contains = Seed.GROWN_BEE;}break;
		}
		return null;
	}
	
	public MenuItem getMenuForGarden() {
		return new MenuSelect() {

			@Override
			public String title() {
				return contains == Seed.EMPTY ? "Empty Plant Spot" : "Section- " + contains;
			}

			@Override
			public boolean go() {
				PlantSpot.this.go();
				return false;
			}
		};
	}
}
