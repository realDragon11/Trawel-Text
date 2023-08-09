package trawel.towns.nodes;
import trawel.Networking;
import trawel.extra;
import trawel.towns.Town;

public class Grove extends NodeFeature {

	private static final long serialVersionUID = 1L;

	public Grove(String name,Town t,int capacity) {
		this.name = name;
		town = t;
		tutorialText = "Explore groves to progress in level.";
		generate(capacity);
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
		start.start();
	}
	
	@Override
	protected void generate(int size) {
		start = NodeType.NodeTypeNum.GROVE.singleton.getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}
	@Override
	protected byte bossType() {
		return 0;
	}

}
