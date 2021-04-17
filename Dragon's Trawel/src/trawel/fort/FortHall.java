package trawel.fort;

import java.util.ArrayList;
import java.util.List;

import trawel.Combat;
import trawel.Feature;
import trawel.Person;
import trawel.Player;
import trawel.Skill;
import trawel.Town;
import trawel.extra;
import trawel.mainGame;

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
		// TODO Auto-generated method stub

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
				}
			}
		}
		if (this.getOwner() == Player.player) {
			
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

}
