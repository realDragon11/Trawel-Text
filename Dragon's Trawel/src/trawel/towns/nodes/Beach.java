package trawel.towns.nodes;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.personal.classless.Perk;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.towns.Town;
import trawel.towns.nodes.BossNode.BossType;

public class Beach extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private int veinsLeft = 0;
	private int size;
	private BossType bossType;
	private int totalMined = 0;
	
	public Beach(String name,Town t, SuperPerson _owner,Shape s) {
		this(name,t,50,t.getTier(),s,BossType.NONE);
		owner = _owner;
	}
	public Beach(String _name,Town t,int _size, int _tier, Shape s, BossType _bossType) {
		background_area = "beach";
		tutorialText = "Beach";
		area_type = Area.MOUNTAIN;
		name = _name;
		town = t;
		tier = _tier;
		size = _size;
		shape = s;
		bossType = _bossType;
		generate(size);
	}
	
	@Override
	public String getTutorialText() {
		switch (shape) {
		case TREASURE_BEACH:
			return "Treasure Beach";
		case NONE:
			return "Beach";
		}
		return "Beach?";
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
	public void sendBackVariant() {
		Networking.sendStrong("Backvariant|"+background_area+background_variant+"|1|0|");
	}
	
	@Override
	protected void generate(int size) {
		start = NodeType.NodeTypeNum.BEACH.singleton.getStart(this, size, tier);
	}
	
	@Override
	protected BossType bossType() {
		return bossType;
	}
	@Override
	public String sizeDesc() {
		return super.sizeDesc() + " V: " +veinsLeft;
	}

}
