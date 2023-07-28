package trawel.towns.nodes;
import trawel.Networking;
import trawel.extra;
import trawel.towns.Town;

public class Dungeon extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private int boss;
	public Dungeon(String name,Town t,Shape s,int bossType) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Explore dungeons to find treasure.";
		shape = s;
		generate();
		boss = bossType;
		
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
		NodeConnector.enter(start);
	}
	
	@Override
	protected void generate() {
		start = DungeonNode.getSingleton().getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}
	@Override
	public NodeType numType(int i) {
		switch (i) {
		case 0: return DungeonNode.getSingleton();
		}
		return null;
	}
	
	@Override
	protected byte bossType() {
		return 1;
	}


}
