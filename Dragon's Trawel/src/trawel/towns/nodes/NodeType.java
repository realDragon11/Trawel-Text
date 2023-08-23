package trawel.towns.nodes;

import trawel.personal.item.solid.DrawBane;
import trawel.time.TimeContext;

public interface NodeType{

	boolean interact(NodeConnector holder,int node);
	
	DrawBane[] dbFinds();
	
	/**
	 * should make time events happen in localevents
	 * @param node
	 * @param time
	 * @param calling
	 */
	void passTime(NodeConnector holder,int node, double time, TimeContext calling);
	
	/**
	 * owner can be 0 for head
	 */
	int getNode(NodeConnector holder, int owner, int guessDepth, int tier);
	
	/**
	 * returns the spot
	 */
	int generate(NodeConnector holder, int from, int sizeLeft, int tier);
	
	/**
	 * creates the node connector that holds everything
	 */
	NodeConnector getStart(NodeFeature owner, int size, int tier);
	
	void apply(NodeConnector holder,int madeNode);
	
	String interactString(NodeConnector holder,int node);
	
	String nodeName(NodeConnector holder,int node);
	
	public static int GENERIC_CUTOFF = Byte.SIZE-50;
	public enum NodeTypeNum{
		GENERIC(new GenericNode()),
		BOSS(new BossNode()),
		GROVE(new GroveNode()),
		CAVE(new CaveNode()),
		MINE(new MineNode()),
		DUNGEON(new DungeonNode()),
		GRAVEYARD(new GraveyardNode());
		
		public final NodeType singleton;
		NodeTypeNum(NodeType create){
			singleton = create;
		}
	}
	
	public static NodeTypeNum getTypeEnum(int num) {
		return NodeTypeNum.values()[num];
		/*
		NodeTypeNum[] vals = NodeTypeNum.values();
		for (int i = 0; i < vals.length;i++) {
			if (vals[i].ordinal() == num) {
				return vals[i];
			}
		}
		throw new RuntimeException("invalid num node type");*/
	}
}
