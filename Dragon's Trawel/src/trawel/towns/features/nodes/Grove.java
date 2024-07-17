package trawel.towns.features.nodes;
import trawel.core.Networking.Area;
import trawel.helper.constants.TrawelColor;
import trawel.towns.contexts.Town;

public class Grove extends NodeFeature {

	private static final long serialVersionUID = 1L;

	public Grove(String name,Town t,int capacity, int _tier) {
		this.name = name;
		town = t;
		tier = _tier;
		generate(capacity);
		background_variant = 1;
	}
	
	public Grove(String name,Town t) {
		this(name,t,50,t.getTier());
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_NODE;
	}
	
	@Override
	public String nameOfType() {
		return "Grove";
	}
	
	@Override
	public String nameOfFeature() {
		return "Grove";
	}
	
	@Override
	public Area getArea() {
		return Area.FOREST;
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
