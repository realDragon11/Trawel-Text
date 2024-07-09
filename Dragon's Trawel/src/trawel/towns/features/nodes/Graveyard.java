package trawel.towns.features.nodes;
import trawel.Networking.Area;
import trawel.helper.methods.extra;
import trawel.towns.Town;

public class Graveyard extends NodeFeature {

	private static final long serialVersionUID = 1L;
	public Graveyard(String name,Town t, int goalSize) {
		this.name = name;
		town = t;
		tutorialText = "Graveyard";
		area_type = Area.GRAVEYARD;
		tier = getTown().getTier();
		generate(goalSize);
	}
	
	public Graveyard(String name,Town t) {
		this(name,t,40);
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		start.start();
	}

	@Override
	protected void generate(int size) {
		shape = Shape.NONE;
		start = NodeType.NodeTypeNum.GRAVEYARD.singleton.getStart(this, size,tier);
	}

}
