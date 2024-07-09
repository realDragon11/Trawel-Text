package trawel.towns.features.fort.features;

import java.util.ArrayList;
import java.util.List;

import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.features.fort.FortFeature;
import trawel.towns.features.fort.elements.LSkill;
import trawel.towns.features.fort.elements.LaborType;
import trawel.towns.features.fort.elements.Laborer;
import trawel.towns.features.fort.elements.SubSkill;

public class Watchtower extends FortFeature {
	private static final long serialVersionUID = 1L;
	
	private int size;
	
	public List<SubSkill> pickList = new ArrayList<SubSkill>();
	
	public Watchtower(int tier,int size) {
		this.tier = tier;
		this.size = size;
		laborer = new Laborer(LaborType.WATCH);
		switch (size) {
		case 1: this.name = "Small Watchtower";
		laborer.lSkills.add(new LSkill(SubSkill.WATCH,2));
		break;
		case 3: this.name = "Large Watchtower";
		laborer.lSkills.add(new LSkill(SubSkill.WATCH,12));break;
		case 2:
			laborer.lSkills.add(new LSkill(SubSkill.WATCH,5));
		default: this.name = "Watchtower";break;
		
		}
		tutorialText = "Watchtower";
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_FORT;
	}
	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getDefenceRating() {
		return 5*size;
	}

	@Override
	public int getOffenseRating() {
		return size;
	}

	@Override
	public void go() {
		if (this.getOwner() != Player.player) {
			Print.println("You do not own this fort.");
			return;
		}
		Print.println("The watch is on vigil.");
		
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
