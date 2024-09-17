package trawel.towns.features.nodes;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.towns.contexts.Town;
import trawel.towns.data.FeatureData;
import trawel.towns.features.nodes.BossNode.BossType;

public class Beach extends NodeFeature implements IUnlockableNodeFeature{
	
	static {
		FeatureData.registerFeature(Beach.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" can be [act_explore]explored[revert]. "+fancyNamePlural()+" host a branching area that splits into "
						//TODO: have cave type be printed with special text here as well
						+"[f_combat]Caves[revert] and "+FeatureData.getData(Dungeon.class).fancyNamePlural()+"."
						+ " Over time, new events [p_regrown]wash up[revert] on the shores.");
			}
			
			@Override
			public int priority() {
				return 80;
			}
			
			@Override
			public String name() {
				return "Beach";
			}
			
			@Override
			public String namePlural() {
				return "Beaches";
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.NODE_EXPLORATION;
			}
			
			@Override
			public String color() {
				return TrawelColor.F_NODE;
			}
		});
	}

	private static final long serialVersionUID = 1L;
	private int size;
	private BossType bossType;
	
	/**
	 * number of sub-areas that have been unlocked/completed
	 */
	private int unlocksGot = 0, unlocksDone = 0;
	
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
	
	@Override
	public String sizeDesc() {
		if (shape != Shape.TREASURE_BEACH) {
			return super.sizeDesc();
		}
		return super.sizeDesc() + " A: " +unlocksDone+"/"+unlocksGot;
	}
	
	@Override
	public void addNewUnlock() {
		unlocksGot++;
	}
	@Override
	public void addDoneUnlock() {
		unlocksDone++;
	}
}
