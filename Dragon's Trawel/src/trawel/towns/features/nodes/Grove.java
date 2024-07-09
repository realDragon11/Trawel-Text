package trawel.towns.features.nodes;
import trawel.core.Networking.Area;
import trawel.helper.methods.extra;
import trawel.towns.contexts.Town;

public class Grove extends NodeFeature {

	private static final long serialVersionUID = 1L;

	public Grove(String name,Town t,int capacity, int _tier) {
		this.name = name;
		town = t;
		tier = _tier;
		tutorialText = "Grove";
		generate(capacity);
		background_area = "forest";
		background_variant = 1;
		area_type = Area.FOREST;
	}
	
	public Grove(String name,Town t) {
		this(name,t,50,t.getTier());
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
		start = NodeType.NodeTypeNum.GROVE.singleton.getStart(this, size,tier);
	}

}
