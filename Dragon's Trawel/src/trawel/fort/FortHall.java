package trawel.fort;

import java.util.ArrayList;
import java.util.List;

import trawel.Combat;
import trawel.Feature;
import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.MenuSelectNumber;
import trawel.Person;
import trawel.Player;
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
	public FortHall(int tier, Town town) {
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
			int cost = this.level*5000;
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
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "buy a soldier ("+getSoldierCost()+")";
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
					return mList;
				}
			});
		}
	}

	@Override
	public void passTime(double time) {
		
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
