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
	
	int getNode(NodeConnector owner, int guessDepth, int tier);
	
	/**
	 * might create the node connector that holds everything???
	 */
	NodeConnector generate(NodeFeature owner, int sizeLeft, int tier);
	
	/**
	 * creates the node connector that holds everything
	 */
	NodeConnector getStart(NodeFeature owner, int size, int tier);
	
	void apply(NodeConnector holder,int madeNode);
	
	String interactString(NodeConnector holder,int node);
	
	String nodeName(NodeConnector holder,int node);
	
	//FIXME: provide some 'generic handlers' which can handle basic nodes (like combat with a person who won't see reason anymore)

	public static int GENERIC_CUTOFF = Byte.SIZE-50;
	public enum ReservedType{
		GENERIC_TYPE(Byte.SIZE-2),
		BOSS_TYPE(Byte.SIZE-3)
		;
		public final int num;
		
		ReservedType(int _num){
			num = _num;
			//GENERIC_CUTOFF = Math.max(GENERIC_CUTOFF,_num);
		}
	}
	
	public static ReservedType getReservedTypeEnum(int num) {
		ReservedType[] vals = ReservedType.values();
		for (int i = 0; i < vals.length;i++) {
			if (vals[i].num == num) {
				return vals[i];
			}
		}
		throw new RuntimeException("invalid reserved node type");
	}
}
