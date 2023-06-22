package trawel.fort;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.Player;
import trawel.extra;

public class Hunter extends FortFeature {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int tier;
	
	public SubSkill downTimeSkill;
	public SubSkill battleSkill;
	private int size;
	
	public List<SubSkill> pickList = new ArrayList<SubSkill>();
	
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
		tutorialText = "";
		color = Color.PINK;
		
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
	public void passTime(double time) {
		// TODO Auto-generated method stub

	}
	

}
