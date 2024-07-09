package trawel.towns.features.fort.features;

import java.util.List;

import trawel.helper.methods.extra;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.features.fort.FortFeature;
import trawel.towns.features.fort.elements.LSkill;
import trawel.towns.features.fort.elements.LaborType;
import trawel.towns.features.fort.elements.Laborer;
import trawel.towns.features.fort.elements.SubSkill;

public class Hunter extends FortFeature {

	private static final long serialVersionUID = 1L;
	private int size;
	
	public Hunter(int tier,int size) {
		this.tier = tier;
		this.size = size;
		laborer = new Laborer(LaborType.WATCH);
		switch (size) {
		case 1: this.name = "Small Hunter's Den";
		break;
		case 3: this.name = "Large Hunter's Den";
		laborer.lSkills.add(new LSkill(SubSkill.WATCH,3));break;
		case 2:
			laborer.lSkills.add(new LSkill(SubSkill.WATCH,1));
		default: this.name = "Hunter's Den";break;
		
		}
		tutorialText = "Hunter";
	}
	
	@Override
	public String getColor() {
		return extra.F_FORT;
	}
	
	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getDefenceRating() {
		return (size/2);
	}

	@Override
	public int getOffenseRating() {
		return 4*size;
	}

	@Override
	public void go() {
		if (this.getOwner() != Player.player) {
			extra.println("You do not own this fort.");
			return;
		}
		extra.println("The hunters are out hunting.");
		
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
