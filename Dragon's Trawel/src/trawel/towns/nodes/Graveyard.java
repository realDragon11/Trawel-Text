package trawel.towns.nodes;
import trawel.Networking;
import trawel.extra;
import trawel.Networking.Area;
import trawel.towns.Town;
import trawel.towns.nodes.NodeFeature.Shape;

public class Graveyard extends NodeFeature {

	private static final long serialVersionUID = 1L;
	public Graveyard(String name,Town t) {
		this.name = name;
		town = t;
		tutorialText = "Graveyard.";
		generate(40);
		area_type = Area.GRAVEYARD;
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|dungeon|Dungeon|");
		start.start();
	}

	@Override
	protected void generate(int size) {
		shape = Shape.NONE;
		start = NodeType.NodeTypeNum.GRAVEYARD.singleton.getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}

	@Override
	protected byte bossType() {
		return -1;
	}

}
