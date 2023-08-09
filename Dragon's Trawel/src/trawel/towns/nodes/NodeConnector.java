package trawel.towns.nodes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLast;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.classless.Skill;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class NodeConnector implements Serializable {

	//used for connecting event nodes with bosses
	private static final long serialVersionUID = 1L;
	protected List<NodeConnector> connects;
	protected String name;//DOLATER: could probably generate these two Strings from the Objects
	protected String interactString = "ERROR";
	
	protected int level;
	
	private short floor = 0;//DOLATER: make floor apply to all types as 'depth'. Floor needs to stay an int/short for spacing reasons
	
	private byte flags = extra.emptyByte;
	
	public enum NodeFlag{
		FORCEGO, STAIR,
		VISIT_BIT1, VISIT_BIT2
	}
	
	protected byte typeNum;
	protected byte eventNum;
	protected byte state;
	protected Object storage1, storage2;
	/**
	 * "turn everything into arrays plan"
	 * 
	 * typeNum + eventNum + state + flags = 32 bits (4 bytes)
	 * level + floor = 32 bits (2 shorts)
	 * make interactstring and name generate?
	 * connections needs addresses, but I can cap it
	 * 
	 * storage1 and storage2 would still exist as Object[] storage1, storage2
	 * which would be 64 bits per slot likely due to refs
	 * 
	 * passing could be removed entirely and just iterate over it instead of recurse over it
	 * parent would be single per thing, which is nice
	 */
	/**
	 * (8) flags
	 * (8) eventNum
	 * (8) state
	 * (8) typeNum
	 * <br>
	 * -next 32-
	 * <br>
	 * (16) level
	 * (16) floor
	 */
	protected long dataContainer[];
	
	/**
	 * max of 255 nodes per area
	 * <br>
	 * 0 = no connection
	 * <br>
	 * stored as up to 8 unsigned bytes
	 */
	private long[] connections;//only allowing a max of 256 nodes now
	
	private Object[] storage;
	private int size = 0;
	/**
	 * store the highest floor in
	 */
	private int deepest = 1;
	
	public transient boolean passing;
	protected transient NodeFeature parent;
	
	public static int lastNode = 1;
	protected static int currentNode = 1;
	protected static boolean forceGoProtection = false;
	
	protected NodeConnector() {
		//NOTE: the first node (index 0) can be used to store data for the entire nodeconnector, it is never referenced otherwise
		connections = new long[256];
		dataContainer = new long[256];
		storage = new Object[256];//NOTE THAT THESE WILL ALMOST ALWAYS BE ARRAYS OF MORE OBJECTS, it's just Object[][] is unneeded
	}
	
	//FIXME: make a constructor
	public int newNode() {
		size++;
		connections[size] = 0b0;//can't set yet
		dataContainer[size] =0b0;//TODO
		storage[size] = new Object[2];//for now we just assume we need to, can trim later compiletime
		return size;
	}
	
	public void trim() {
		//FIXME: makes all arrays only as long as they need to be, because we make them max size in generator step
	}
	
	public boolean getFlag(int node,NodeFlag f) {
		return extra.getEnumByteFlag(f.ordinal(), extra.extractByteFromLong(dataContainer[node], 0));
	}
	
	public void setFlag(int node,NodeFlag f, boolean bool) {
		final long temp = dataContainer[node];
		final byte sub = extra.extractByteFromLong(temp,0);//note weird sign stuff
		dataContainer[node] = extra.setByteInLong(temp,extra.setEnumByteFlag(f.ordinal(), sub, bool), 0);
	}
	
	public int getEventNum(int node) {
		return extra.intGetNthByteFromLong(dataContainer[node],1);
	}
	public int getStateNum(int node) {
		return extra.intGetNthByteFromLong(dataContainer[node],2);
	}
	public int getTypeNum(int node) {
		return extra.intGetNthByteFromLong(dataContainer[node],3);
	}
	
	public void setEventNum(int node, int num) {
		dataContainer[node] = extra.setByteInLong(dataContainer[node],num, 8);
	}
	public void setStateNum(int node, int num) {
		dataContainer[node] = extra.setByteInLong(dataContainer[node],num, 16);
	}
	public void setTypeNum(int node, int num) {
		dataContainer[node] = extra.setByteInLong(dataContainer[node],num, 24);
	}
	
	public int getLevel(int node) {
		return extra.intGetNthShortFromLong(dataContainer[node], 2);//3rd, is zero indexed
	}
	
	public void setLevel(int node,int tier) {
		dataContainer[node] = extra.setShortInLong(dataContainer[node], tier, 32);
	}
	
	public List<Integer> getConnects(int node) {
		long sub = connections[node];
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 8;i++) {
			int ret = extra.intGetNthByteFromLong(sub,i);
			assert ret >= 0;
			if (ret > 0) {
				list.add(ret);
			}
		}
		return list;
	}

	protected void setConnects(int node,int...places) {
		long sub = 0b0;
		for (int i = 0; i < places.length-1;i++) {
			//I think I have to do this so it doesn't delete info
			sub = extra.setNthByteInLong(sub, (byte)(places[i]-Byte.MAX_VALUE),i);
		}
		connections[node] = sub;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	public int getLevel() {
		return level;
	}

	public void start() {
		Feature.atFeatureForHeader.goHeader();
		lastNode = 1;
		currentNode = 1;
		while (currentNode != 0) {
			enter(currentNode);
		}
		
	}
	public void enter(int node) {
		Player.addTime(.1);
		mainGame.globalPassTime();
		extra.menuGo(menuGen(currentNode));
	}
	
	public MenuGenerator menuGen(int node) {
		return new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				if (isDeepest(node)) {
					switch(parent.getShape()) {
					case TOWER:
					Networking.unlockAchievement("tower1");
					break;
					case ELEVATOR:
						if (parent instanceof Mine) {
							Networking.unlockAchievement("mine2");
						}
						break;
					}
				}
				if (isForceGo(node) && !forceGoProtection) {
					setVisited(node,3);
					forceGoProtection = true;
					interactCode();
					return null;//redo operation
				}
				mList.add(new NodeMenuTitle(node));
				mList.add(new NodeMenuInteract(node));
				for (int n: getConnects(node)) {
					mList.add(new NodeMenuItem(n));
				}
				mList.add(new MenuLast() {

					@Override
					public String title() {
						return extra.TIMID_RED + "exit " + parent.getName();//TODO: fix parent name
					}

					@Override
					public boolean go() {
						NodeConnector.currentNode = 0;//kick out
						return true;
					}
					
				});
				return mList;
			}
		};
	}
	
	protected class NodeMenuTitle extends MenuLine{

		private int node;
		
		public NodeMenuTitle(int _node) {
			node = _node;
		}
		
		@Override
		public String title() {
			String visitColor = extra.PRE_WHITE;
			switch (getVisited(node)) {
			case 0: visitColor = extra.VISIT_NEW; setVisited(node,2);break;
			case 1: visitColor = extra.VISIT_SEEN; setVisited(node,2);break;
			case 2: visitColor = extra.VISIT_BEEN;break;
			case 3: visitColor = extra.VISIT_DONE;break;
			}
			return visitColor + getName(node);
		}
		
	}
	
	protected void interactCode() {
		if (typeNum < 0) {
			switch (typeNum) {
			case -1:
				if (BossNode.getSingleton().interact(this)) {
					currentNode = 0;//kicked out
				}
				break;
			}
			return;
		}
		if (parent.numType(typeNum).interact(this)) {
			currentNode = 0;//kicked out
		}
	}
	protected class NodeMenuInteract extends MenuSelect{

		private int node;
		
		public NodeMenuInteract(int _node) {
			node = _node;
		}
		
		@Override
		public String title() {
			String visitColor = extra.PRE_WHITE;
			if (getVisited(node) != 3) {
				visitColor = extra.TIMID_GREY;
			}
			return visitColor + getInteractString(node);
		}

		@Override
		public boolean go() {
			setVisited(node,3);
			interactCode();
			return true;
		}
		
	}
	
	protected class NodeMenuItem extends MenuSelect{

		private int node;
		
		public NodeMenuItem(int _node) {
			node = _node;
		}
		
		@Override
		public String title() {
			String visitColor = extra.PRE_WHITE;
			switch (getVisited(node)) {
			case 0: visitColor = extra.VISIT_NEW; setVisited(node,1);break;
			case 1: visitColor = extra.VISIT_SEEN;break;
			case 2: visitColor = extra.VISIT_BEEN;break;
			case 3: visitColor = extra.VISIT_DONE;break;
			}
			String postText = "";
			if (isStair(node)) {
				if (getFloor(node) < getFloor(NodeConnector.currentNode)) {
					postText = " down";
				}else {
					postText = " up";
				}
			}
			if (Player.hasSkill(Skill.TIERSENSE)) {
				postText +=" T: " + getLevel(node);
			}
			if (Player.hasSkill(Skill.TOWNSENSE)) {
				postText +=" C: " + getConnects(node).size();
			}
			return visitColor + getName(node) +postText+ (node == NodeConnector.lastNode ? extra.PRE_WHITE+" (back)" : "");
		}

		@Override
		public boolean go() {
			forceGoProtection = false;
			NodeConnector.lastNode = NodeConnector.currentNode;
			currentNode = node;
			setVisited(node,2);
			return true;//always return true to prevent recursive nesting issues
		}
		
	}
	
	public void reverseConnections(int node) {
		List<Integer> connects = getConnects(node);
		int[] replace = new int[connects.size()];
		for (int i = replace.length-1;i >=0; i--) {
			replace[i] = connects.get(i);
		}
		setConnects(node,replace);
	}
	public void endPass() {
		passing = false;
		for (NodeConnector n: connects) {
			if (n.passing) {
				n.endPass();
			}
		}
	}
	
	//called in some passTime implementations to propagate it
	public void spreadTime(double time, TimeContext calling) {
		passing = true;
		for (int i = 0; i < size;i++)
		parent.numType(typeNum).passTime(this,i, time, calling);
		for (NodeConnector n: connects) {
			if (!n.passing) {
				n.spreadTime(time,calling);
			}
		}
	}
	
	public List<TimeEvent> timeEvent(double time, TimeContext calling){
		return null;
	}
	
	protected DrawBane attemptCollectAll(float odds,int amount) {
		NodeFeature keeper = parent;
		if (keeper.getFindTime() > 1 && Player.player.sideQuests.size() > 0) {
			if (extra.randFloat() < odds) {
				List<String> list = Player.player.allQTriggers();
				for (DrawBane str: parent.numType(typeNum).dbFinds()) {
					if (list.contains("db:"+str.name())) {
						keeper.findCollect("db:"+str.name(), amount);
						return str;
					}
				}
			}
		}
		keeper.delayFind();
		return null;
	}
	
	protected boolean findBehind(String behind) {
		DrawBane db = this.attemptCollectAll(.2f,1);
		if (db == null) {return false;}
		extra.println("Hey, there's some "+ db.getName() +" pieces behind that "+ behind+"!");
		return true;
	}


	protected void reset(NodeFeature p) {
		parent = p;
		p.size = size;
		deepest = 0;
		for (int i = 1; i < size+1;i++) {//first node is for us and not a node
			deepest= Math.max(deepest,getFloor(i));
		}
	}
	
	protected void finalize(int node,NodeFeature owner) {
		int typeNum = getTypeNum(node);
		int eventNum = getEventNum(node);
		if (typeNum < 0) {
			switch (typeNum) {
			case -1:
				BossNode.getSingleton().apply(this);
				break;
			}
			return;
		}
		owner.numType(typeNum).apply(this);
		if (eventNum == 3) {
			if (owner instanceof Mine) {
				((Mine) owner).addVein();
			}
		}
		return;
	}
	
	protected void setStair(int node) {
		setFlag(node,NodeFlag.STAIR, true);
	}

	protected void setFloor(int node, int floor) {
		dataContainer[node] = extra.setShortInLong(dataContainer[node], floor, 64-16);
	}

	public boolean isStair(int node) {
		return getFlag(node,NodeFlag.STAIR);
	}

	public int getFloor(int node) {
		return extra.intGetNthShortFromLong(dataContainer[node],3);//the last short
	}
	
	public boolean isDeepest(int node) {
		return getFloor(node) == deepest;
	}

	public boolean isForceGo(int node) {
		return getFlag(node,NodeFlag.FORCEGO);
	}

	public void setForceGo(int node,boolean forceGo) {
		setFlag(node,NodeFlag.FORCEGO,forceGo);
	}

	public byte getVisited(int node) {
		//we don't actually need to know the order since we look per bit
		//but left is more significant in effect below
		if (getFlag(node,NodeFlag.VISIT_BIT1)) {//1X
			if (getFlag(node,NodeFlag.VISIT_BIT2)) {//11
				return 3;
			}else {//10
				return 2;
			}
		}else {//0X
			if (getFlag(node,NodeFlag.VISIT_BIT2)) {//01
				return 1;
			}else {//00
				return 0;
			}
		}
	}

	public void setVisited(int node, int visited) {
		switch (visited) {
		case 0:
			setFlag(node,NodeFlag.VISIT_BIT1,false);
			setFlag(node,NodeFlag.VISIT_BIT2,false);
			break;
		case 1:
			setFlag(node,NodeFlag.VISIT_BIT1,false);
			setFlag(node,NodeFlag.VISIT_BIT2,true);
			break;
		case 2:
			setFlag(node,NodeFlag.VISIT_BIT1,true);
			setFlag(node,NodeFlag.VISIT_BIT2,false);
			break;
		case 3:
			setFlag(node,NodeFlag.VISIT_BIT1,true);
			setFlag(node,NodeFlag.VISIT_BIT2,true);
			break;
		}
	}
	
}
