package trawel.towns.nodes;
import trawel.Networking;
import trawel.extra;
import trawel.towns.Town;

public class Graveyard extends NodeFeature {

	private static final long serialVersionUID = 1L;
	public Graveyard(String name,Town t) {
		this.name = name;
		town = t;
		tutorialText = "Graveyards are teeming with undead.";
		generate(40);
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		Networking.setArea("dungeon");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|dungeon|Dungeon|");
		start.start();
	}

	@Override
	protected void generate(int size) {
		start = NodeType.NodeTypeNum.GRAVEYARD.singleton.getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}

	@Override
	protected byte bossType() {
		return -1;
	}

}
