package trawel.towns.features.nodes;
import trawel.core.Print;
import trawel.core.Networking.Area;
import trawel.helper.constants.TrawelColor;
import trawel.towns.contexts.Town;
import trawel.towns.data.FeatureData;

public class Grove extends NodeFeature {
	
	static {
		FeatureData.registerFeature(Grove.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" can be [act_explore]explored[revert]. Over time, the "+fancyName()+" will [p_regrown]regrow[revert].");
			}
			
			@Override
			public int priority() {
				return 30;
			}
			
			@Override
			public String name() {
				return "Grove";
			}
			
			@Override
			public String color() {
				return TrawelColor.F_NODE;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.NODE_EXPLORATION;
			}
		});
	}

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
	public String nameOfType() {
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
