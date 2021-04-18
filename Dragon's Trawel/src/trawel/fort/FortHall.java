package trawel.fort;

import java.util.ArrayList;
import java.util.List;

import trawel.AIClass;
import trawel.Combat;
import trawel.Feature;
import trawel.Inventory;
import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.MenuSelectNumber;
import trawel.Person;
import trawel.Player;
import trawel.Race;
import trawel.Skill;
import trawel.Town;
import trawel.extra;
import trawel.mainGame;
import trawel.fort.SubSkill.Active;
import trawel.fort.SubSkill.Type;

/**
 * 
 * @author dragon
 *
 * The most important fort feature. Holds extra data and the build menu.
 */
public class FortHall extends FortFeature {

	public int level;
	public ArrayList<Person> allies = new ArrayList<Person>();
	
	public double forgeTimer = 24.0*7;
	public FortHall(int tier, Town town) {
		this.name = "Fort Hall";
		this.town = town;
		this.level = tier;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public int getDefenceRating() {
		return 0;
	}

	@Override
	public int getOffenseRating() {
		return 0;
	}

	@Override
	public void go() {
		if (this.getOwner() != Player.player) {
			int cost = this.level*2500;
			extra.println("Buy this for fort for "+cost+" gold? (You have " + Player.bag.getGold()+")");
			if (extra.yesNo()) {
				if (Player.bag.getGold() < cost) {
					extra.println("You can't afford to buy this fort.");
				}else {
					Player.bag.addGold(-cost);
					for (Feature f: this.town.getFeatures()) {
						f.setOwner(Player.player);
					}
					this.town.visited=3;
					while (allies.size() < 5) {
						allies.add(new Person(level));
					}
				}
			}
		}
		if (this.getOwner() == Player.player) {
			
			extra.menuGo(new MenuGenerator() {
				@Override
				public List<MenuItem> gen() {
					List<MenuItem> mList = new ArrayList<MenuItem>();
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return "You have " + allies.size() + " soldiers here.";
						}});
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "leave";
						}

						@Override
						public boolean go() {
							return true;
						}
					});
					if (allies.size() < 10) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Buy a soldier ("+getSoldierCost()+")";
						}

						@Override
						public boolean go() {
							if (Player.bag.getGold() >= getSoldierCost()) {
								Player.bag.addGold(-getSoldierCost());
								allies.add(new Person(level));
							}else {
								extra.println("You can't afford another soldier.");
							}
							return false;
						}
					});
					}
					if (town.fortSizeLeft() > 0) {
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return "Construction Menu";
							}

							@Override
							public boolean go() {
								constructionFoundations();
								return false;
							}
						});
						}
					return mList;
				}
			});
		}
	}
	
	public void constructionFoundations() {
		extra.menuGo(new MenuGenerator() {
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "You have " + town.fortSizeLeft() + " more space.";
					}});
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
				if (town.fortSizeLeft() > 2) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Build a Large Foundation ("+(level*1000)+")";
						}

						@Override
						public boolean go() {
							if (Player.bag.getGold() >= (level*1000)) {
								Player.bag.addGold(-(level*1000));
								town.enqueneAdd(new FortFoundation(3));
							}else {
								extra.println("You can't afford a new large foundation.");
							}
							return false;
						}
					});
					}
				if (town.fortSizeLeft() > 1) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Build a Medium Foundation ("+(level*500)+")";
						}

						@Override
						public boolean go() {
							if (Player.bag.getGold() >= (level*500)) {
								Player.bag.addGold(-(level*500));
								town.enqueneAdd(new FortFoundation(2));
							}else {
								extra.println("You can't afford a new medium foundation.");
							}
							return false;
						}
					});
					}
				if (town.fortSizeLeft() > 2) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Build a Small Foundation ("+(level*250)+")";
						}

						@Override
						public boolean go() {
							if (Player.bag.getGold() >= (level*250)) {
								Player.bag.addGold(-(level*250));
								town.enqueneAdd(new FortFoundation(1));
							}else {
								extra.println("You can't afford a new small foundation.");
							}
							return false;
						}
					});
					}
				return mList;
			}
		});
	}

	@Override
	public void passTime(double time) {
		forgeTimer -= (time* (double)getSkillCount(SubSkill.SMITHING))/10.0;
		if (forgeTimer <=0) {
			forgeTimer = 24.0*7;
			Inventory inv = new Inventory(level, Race.RaceType.HUMANOID, null);
			for (Person p: allies) {
				AIClass.loot(p.getBag(), inv,level, false);
			}
		}
	}
	
	public int getSkillCount(SubSkill s) {
		int i = 0;
		for (Feature f: town.getFeatures()) {
			FortFeature ff = (FortFeature)f;
			i+=ff.laborer.getSkillCount(s);
		}
		return i;
	}
	
	public int getWatchScore() {
		int i = 0;
		i+=getSkillCount(SubSkill.SCRYING)*3;
		return i;
	}
	
	public void Fight(ArrayList<Person>... people) {
		Combat c = new Combat(this.town.getIsland().getWorld(),this,people);
		allies.clear();
		if (c.survivors.get(0).hasSkill(Skill.PLAYERSIDE)) {
			c.survivors.remove(Player.player.getPerson());
			allies.addAll(c.survivors);
		}else {
			
		}
	}

	private ArrayList<Person> getAllies() {
		while (allies.size() < 10) {
			allies.add(new Person(level));
		}
		for (Person a: allies) {
			if (!a.hasSkill(Skill.PLAYERSIDE)) {
				a.addSkill(Skill.PLAYERSIDE);
			}
		}
		return allies;
	}
	
	public int getSoldierCost() {
		return 50*level*Math.max(allies.size(),5);
	}

}
