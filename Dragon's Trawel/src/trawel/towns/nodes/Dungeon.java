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
		tutorialText = "Dungeon.";
		shape = s;
		generate(50);
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
		start.start();
	}
	
	@Override
	protected void generate(int size) {
		start = NodeType.NodeTypeNum.DUNGEON.singleton.getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}

	
	@Override
	protected byte bossType() {
		return 1;
	}


}
