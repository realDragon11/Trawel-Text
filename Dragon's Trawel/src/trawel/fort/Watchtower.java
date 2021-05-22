package trawel.fort;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.Player;
import trawel.extra;

public class Watchtower extends FortFeature {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int tier;
	
	public SubSkill downTimeSkill;
	public SubSkill battleSkill;
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
		tutorialText = "";
		color = Color.PINK;
		
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
		if (this.getOwner() != Player.player.player) {
			extra.println("You do not own this fort.");
			return;
		}
		extra.println("The watch is on vigil.");
		
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub

	}
	

}
