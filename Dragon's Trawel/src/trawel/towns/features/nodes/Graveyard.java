package trawel.towns.features.nodes;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.towns.contexts.Town;
import trawel.towns.data.FeatureData;

public class Graveyard extends NodeFeature {
	
	static {
		FeatureData.registerFeature(Graveyard.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" can be [act_explore]explored[revert]. Many of the events within will be [ibm]hidden[revert] by the unnatural darkness until [p_been]visited[revert].");
			}
			
			@Override
			public int priority() {
				return 90;
			}
			
			@Override
			public String name() {
				return "Graveyard";
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
	public Graveyard(String name,Town t, int goalSize) {
		this.name = name;
		town = t;
		tier = getTown().getTier();
		generate(goalSize);
	}
	
	public Graveyard(String name,Town t) {
		this(name,t,40);
	}
	
	
	@Override
	public String nameOfType() {
		return "Graveyard";
	}
	
	
	@Override
	public Area getArea() {
		return Area.GRAVEYARD;
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
