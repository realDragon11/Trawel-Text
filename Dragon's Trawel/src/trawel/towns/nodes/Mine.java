package trawel.towns.nodes;
import java.awt.Color;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Town;

public class Mine extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private int veinsLeft = 0;
	
	public Mine(String name,Town t, SuperPerson owner,Shape s) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Mines have minerals for you to make profit off of.";
		this.owner = owner;
		shape = s;
		generate();
		background_area = "mine";
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		Networking.setArea("mine");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|mine|Mine|");
		Networking.addMultiLight(80,471);
		Networking.addMultiLight(486,360);
		Networking.addMultiLight(1012,353);
		NodeConnector.enter(start);
		Networking.clearLights();
	}
	@Override
	public void sendBackVariant() {
		Networking.sendStrong("Backvariant|"+background_area+background_variant+"|1|0|");
	}
	
	public void addVein() {
		veinsLeft++;
	}
	public void removeVein() {
		veinsLeft--;
		if (veinsLeft == 0 && shape.equals(Shape.NONE)) {
			Networking.sendStrong("Achievement|mine1|");
		}
	}
	
	@Override
	protected void generate() {
		start = MineNode.getSingleton().getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}
	@Override
	public NodeType numType(int i) {
		switch (i) {
		case 0: return MineNode.getSingleton();
		}
		return null;
	}
	@Override
	protected byte bossType() {
		return 2;
	}
	


}
