package trawel.towns.nodes;
import trawel.Networking;
import trawel.extra;
import trawel.towns.Town;

public class Grove extends NodeFeature {

	private static final long serialVersionUID = 1L;

	public Grove(String name,Town t,int capacity) {
		this.name = name;
		town = t;
		size = capacity;
		tutorialText = "Explore groves to progress in level.";
		generate();
		background_area = "forest";
		background_variant = 1;
	}
	
	public Grove(String name,Town t) {
		this(name,t,50);
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		Networking.setArea("forest");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|grove|Grove|");
		NodeConnector.enter(start);
	}
	
	@Override
	protected void generate() {
		start = GroveNode.getSingleton().getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}
	@Override
	public NodeType numType(int i) {
		switch (i) {
		case 0: return GroveNode.getSingleton();
		case 1: return CaveNode.getSingleton();
		}
		throw new RuntimeException("invalid numtype");
	}
	@Override
	protected byte bossType() {
		return 0;
	}

}
