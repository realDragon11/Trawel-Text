package trawel.towns.features.nodes;
import trawel.core.Networking;
import trawel.core.Networking.Area;
import trawel.helper.constants.TrawelColor;
import trawel.personal.classless.Perk;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.towns.contexts.Town;
import trawel.towns.features.nodes.BossNode.BossType;

public class Beach extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private int size;
	private BossType bossType;
	
	public Beach(String _name,Town t,int _size, int _tier, Shape s, BossType _bossType) {
		name = _name;
		town = t;
		tier = _tier;
		size = _size;
		shape = s;
		bossType = _bossType;
		generate(size);
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_NODE;
	}
	
	@Override
	public String nameOfType() {
		switch (shape) {
		case TREASURE_BEACH:
			return "Treasure Beach";
		case NONE:
			return "Beach";
		}
		return "Beach?";
	}
	
	@Override
	public String nameOfFeature() {
		return "Beach";
	}
	
	@Override
	public Area getArea() {
		return Area.BEACH;
	}
	
	@Override
	public void go() {
		start.start();
	}
	@Override
	protected void generate(int size) {
		start = NodeType.NodeTypeNum.BEACH.singleton.getStart(this, size, tier);
	}
	
	@Override
	protected BossType bossType() {
		return bossType;
	}
}
