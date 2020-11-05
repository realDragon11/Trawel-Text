package trawel.fort;

import java.util.ArrayList;

import trawel.Combat;
import trawel.Feature;
import trawel.Person;
import trawel.Skill;
import trawel.Town;
import trawel.mainGame;

/**
 * 
 * @author dragon
 *
 * The most important fort feature. Holds extra data and the build menu.
 */
public class FortHall extends FortFeature {

	public int level;
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
		
		return i;
	}
	
	public void Fight(ArrayList<Person>... people) {
		Combat c = new Combat(this.town.getIsland().getWorld(),this,people);
		if (c.survivors.get(0).hasSkill(Skill.PLAYERSIDE)) {
			
		}else {
			
		}
	}

	private ArrayList<Person>[] getAllies() {
		// TODO Auto-generated method stub
		return null;
	}

}
