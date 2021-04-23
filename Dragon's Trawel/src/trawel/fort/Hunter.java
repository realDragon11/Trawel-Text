package trawel.fort;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuSelect;
import trawel.MenuSelectNumber;
import trawel.Player;
import trawel.QRMenuItem;
import trawel.QuestR;
import trawel.extra;
import trawel.fort.SubSkill.Active;
import trawel.fort.SubSkill.Type;

public class Hunter extends FortFeature {

	
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
		return size;
	}

	@Override
	public int getOffenseRating() {
		return 6*size;
	}

	@Override
	public void go() {
		if (this.getOwner() != Player.player.player) {
			extra.println("You do not own this fort.");
			return;
		}
		extra.println("The hunter's are out hunting.");
		
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub

	}
	

}
