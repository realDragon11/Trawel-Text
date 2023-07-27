package trawel.towns.nodes;
import java.awt.Color;
import java.util.List;

import trawel.Networking;
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
		color = Color.RED;
		this.owner = owner;
		shape = s;
		generate();
		background_area = "mine";
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
	
	@Override
	protected void generate() {
		switch (shape) {
		case NONE:
			start = new MineNode(size,town.getTier(),this);
			break;
		case ELEVATOR:
			start = new MineNode(size,town.getTier(),this);
			NodeConnector lastNode = start;
			NodeConnector newNode;
			for (int i = 0;i < 50;i++) {
				newNode = new MineNode(size,town.getTier()+(i/10),this);
				lastNode.getConnects().add(newNode);
				newNode.getConnects().add(lastNode);
				lastNode.reverseConnections();
				lastNode = newNode;
			}
			lastNode.isSummit = true;
			BossNode b = new BossNode(town.getTier()+5,1);
			lastNode.getConnects().add(b);
			b.getConnects().add(lastNode);
			b.parentName = "hell";
			lastNode.reverseConnections();
			break;
		}
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
	


}
