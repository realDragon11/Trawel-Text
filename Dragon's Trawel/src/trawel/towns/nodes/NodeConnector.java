package trawel.towns.nodes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLast;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.Person;
import trawel.personal.classless.Skill;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.World;
import trawel.towns.misc.PlantSpot;

public class NodeConnector implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum NodeFlag{//flag with up to 8 values
		FORCEGO, STAIR,
		VISIT_BIT1, VISIT_BIT2
		,GENERIC_OVERRIDE//allows nodes to use generic behavior without overriding their typenum
		,SILENT_FORCEGO_POSSIBLE
		,REGROWN
		//7th flag
	}
	
	/**
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
	
	protected int highestLevel = 0, lowestLevel = 0;
	
	/**
	 * used for 'reset' actions that replace corpses
	 */
	protected double globalTimer = 0d;
	
	protected NodeFeature parent;
	
	private static boolean isForceGoIng = false;
	private static boolean gateKickBack = false;
	
	protected NodeConnector(NodeFeature haver) {
		parent = haver;
		//NOTE: the first node (index 0) can be used to store data for the entire nodeconnector, it is never referenced otherwise
		connections = new long[256];
		dataContainer = new long[256];
		storage = new Object[256];//NOTE THAT THESE WILL ALMOST ALWAYS BE ARRAYS OF MORE OBJECTS, it's just Object[][] is unneeded
	}
	
	
	public int newNode() {
		size++;
		connections[size] = 0b0;//can't set yet
		dataContainer[size] =0b0;
		storage[size] = null;//new Object[2];//for now we just assume we need to, can trim later compiletime
		return size;
	}
	public int newNode(int typeNum, int eventNum,int level) {
		size++;
		connections[size] = 0b0;//can't set yet
		dataContainer[size] =  extra.setShortInLong
				(extra.setNthByteInLong(
				extra.setNthByteInLong(0b0, typeNum, 3)
				,eventNum,1)
				,level,32);
		assert extra.intGetNthByteFromLong(dataContainer[size], 1) == eventNum;
		assert extra.intGetNthByteFromLong(dataContainer[size], 3) == typeNum;
		assert extra.intGetNthShortFromLong(dataContainer[size], 2) == level;
		assert getFlag(size,NodeFlag.GENERIC_OVERRIDE) == false;
		storage[size] = null;//new Object[2];//for now we just assume we need to, can trim later compiletime
		return size;
	}
	//MAYBELATER: might need more constructors
	
	public void trim() {
		//makes all arrays only as long as they need to be, because we make them max size in generator step
		//to avoid the 'add one vs double' problem, which we would rather not add and then reduce
		connections = Arrays.copyOf(connections, size+1);
		dataContainer = Arrays.copyOf(dataContainer, size+1);
		storage = Arrays.copyOf(storage, size+1);
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
		assert node != 0;
		long sub = 0b0;
		for (int i = 0; i < places.length;i++) {
			//I think I have to do this so it doesn't delete info
			assert places[i] <= size;
			sub = extra.setNthByteInLong(sub,places[i],i);
		}
		connections[node] = sub;
	}
	
	protected void addConnect(int node, int tonode) {
		assert node != 0;
		assert tonode != 0;
		long sub = connections[node];
		for (int i = 0; i < 8;i++) {
			int ret = extra.intGetNthByteFromLong(sub,i);
			assert ret >= 0;
			assert ret <= 255;
			if (ret == 0) {
				connections[node] = extra.setNthByteInLong(sub,tonode,i);
				return;
			}
		}
		throw new RuntimeException("not enough room for node connection!");
	}
	
	/**
	 * does not check to see if node already has connection, that's caller's job
	 */
	protected void setMutualConnect(int node1, int node2) {
		assert node1 != 0;
		assert node2 != 0;
		addConnect(node1,node2);
		addConnect(node2,node1);
	}
	
	public String getName(int node) {
		//return "index: " +node + " num: " + getEventNum(node);//maybe have it be able to check the first object in storage is a string if the eventnum tells it to
		if (getFlag(node,NodeFlag.GENERIC_OVERRIDE)) {
			return NodeType.NodeTypeNum.GENERIC.singleton.nodeName(this, node);
		}
		return NodeType.getTypeEnum(getTypeNum(node)).singleton.nodeName(this, node);
	}
	
	public String getInteractString(int node) {
		//return "type: " + getTypeNum(node) + " num: " + getEventNum(node);
		if (getFlag(node,NodeFlag.GENERIC_OVERRIDE)) {
			return NodeType.NodeTypeNum.GENERIC.singleton.interactString(this, node);
		}
		return NodeType.getTypeEnum(getTypeNum(node)).singleton.interactString(this, node);
	}

	public void start() {
		//Feature.atFeatureForHeader.goHeader();
		if (getCurrentNode() == 0) {//since player can save inside of them now
			setLastNode(1);
			setCurrentNode(1);
		}
		isForceGoIng = false;
		setForceGoProtection(false);
		while (getCurrentNode() != 0 && Player.isPlaying) {
			boolean wasKicked = false;
			if (gateKickBack) {
				wasKicked = true;
				gateKickBack = false;
				setForceGoProtection(false);//can be forced to go to the node they got kicked back into
				if (getCurrentNode() == getLastNode()) {
					break;//exit area since we got gated at start
				}
				setCurrentNode(getLastNode());//set to last node
			}else {
				//will only autosave if not having kickback stuff to avoid any edge cases that might occur
				mainGame.checkAutosave();
			}
			enter(getCurrentNode());
			if (gateKickBack == true && wasKicked) {
				break;//kick out since we're stuck in a gateKickBack loop
			}
		}
		setLastNode(0);
		setCurrentNode(0);
	}
	public void enter(int node) {
		//generic nodes still have their base typenum, they just have an override flag
		switch (NodeType.getTypeEnum(getTypeNum(node))) {
		case BOSS:
			//Networking.setArea(Area.CHAMPION);
			//do not change area
			//Networking.updateTime();//time gets updated in a tiny bit
			//and unlike the others we don't care to update the background if needed
			break;
		case CAVE:
			Networking.setArea(Area.CAVE);
			if (parent instanceof Grove) {
				Networking.unlockAchievement("cave1");
			}
			break;
		case DUNGEON:
			Networking.setArea(Area.DUNGEON);
			break;
		case GENERIC:
			throw new RuntimeException("invalid true area: should not be generic");
		case GRAVEYARD:
			Networking.setArea(Area.GRAVEYARD);
			break;
		case GROVE:
			Networking.setArea(Area.FOREST);
			break;
		case MINE:
			Networking.setArea(Area.MINE);
			break;
		}
		Player.addTime(.1);
		mainGame.globalPassTime();
		if (isForceGo(node) && !isForceGoProtection()) {
			isForceGoIng = true;
			if (!getFlag(node,NodeFlag.SILENT_FORCEGO_POSSIBLE)) {
				setVisited(node,3);
			}
			setForceGoProtection(true);
			interactCode(node);
			return;//redo operation
		}
		extra.menuGo(menuGen(getCurrentNode()));
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
				isForceGoIng = false;
				mList.add(new NodeMenuTitle(node));
				mList.add(new NodeMenuInteract(node));
				for (int n: getConnects(node)) {
					if (n > size) {//not >= because we have a 0 node at 0
						System.err.println("node too high: " +n +"/"+size);
					}else {
						mList.add(new NodeMenuItem(n));
					}
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.TIMID_RED + "exit " + parent.getName();
					}

					@Override
					public boolean go() {
						NodeConnector.setCurrentNode(0);//kick out
						return true;
					}
					
				});
				mList.add(new MenuLast() {

					@Override
					public String title() {
						return "Player Menu";
					}

					@Override
					public boolean go() {
						Player.player.youMenu();
						return true;
					}});
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
	
	protected void interactCode(int node) {
		if (getFlag(node,NodeFlag.GENERIC_OVERRIDE)) {
			if (NodeType.NodeTypeNum.GENERIC.singleton.interact(this, node)) {
				setCurrentNode(0);//kicked out
			}
			return;
		}
		if (NodeType.getTypeEnum(getTypeNum(node)).singleton.interact(this,node)) {
			setCurrentNode(0);//kicked out
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
				visitColor = extra.TIMID_GREY;//may be overridden by the node itself by just changing the color right back
			}
			return visitColor + getInteractString(node);
		}

		@Override
		public boolean go() {
			setVisited(node,3);
			interactCode(node);
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
				case 0: visitColor = extra.VISIT_NEW;
				setVisited(node,1);
				if (NodeConnector.this.getFlag(node,NodeFlag.REGROWN)) {
					visitColor = extra.VISIT_REGROWN;
				}
				;break;
				case 1: visitColor = extra.VISIT_SEEN; break;
				case 2: visitColor = extra.VISIT_BEEN; break;
				case 3:
					visitColor = extra.VISIT_DONE;
					if (containsOwnable(node)) {
						visitColor = extra.VISIT_OWN;
					}
				break;
			}
			String postText = "";
			if (isStair(node)) {
				if (getFloor(node) < getFloor(NodeConnector.getCurrentNode())) {
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
			if (mainGame.displayNodeDeeper) {
				int fromFloor = getFloor(getCurrentNode());
				int toFloor = getFloor(node);
				if (toFloor < fromFloor) {
					postText += extra.COLOR_OPTION_B+" (Outward)";
				}else {
					if (toFloor > fromFloor) {
						postText += extra.COLOR_OPTION_A+" (Inward)";
					}
				}
			}
			return (node == NodeConnector.getLastNode() ? extra.PRE_WHITE+"Back: " : "")+visitColor + getName(node)+extra.PRE_WHITE+postText;
		}

		@Override
		public boolean go() {
			setForceGoProtection(false);
			setLastNode(getCurrentNode());
			setCurrentNode(node);
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
	
	//TODO: an 'order connections' thing that just orders them by number so it can handle 'go back'
	//or maybe just move the lowest number node to the bottom with a single swap?
	
	public void spreadTime(double time, TimeContext calling) {
		globalTimer += time;
		for (int i = 1; i < size+1;i++) {
			if (getFlag(i,NodeFlag.GENERIC_OVERRIDE)) {
				NodeType.NodeTypeNum.GENERIC.singleton.passTime(this, i, time, calling);
			}else {
				NodeType.getTypeEnum(getTypeNum(i)).singleton.passTime(this, i, time, calling);
			}
		}
	}
	
	public List<TimeEvent> timeEvent(double time, TimeContext calling){
		return null;
	}
	
	protected DrawBane attemptCollectAll(int node,float odds,int amount) {
		NodeFeature keeper = parent;
		if (keeper.getFindTime() > 1 && Player.player.sideQuests.size() > 0) {
			if (extra.randFloat() < odds) {
				List<String> list = Player.player.allQTriggers();
				for (DrawBane str: NodeType.getTypeEnum(getTypeNum(node)).singleton.dbFinds()) {
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
	
	protected boolean findBehind(int node,String behind) {
		DrawBane db = this.attemptCollectAll(node,.2f,1);
		if (db == null) {return false;}
		extra.println("Hey, there's some "+ db.getName() +" pieces behind that "+ behind+"!");
		return true;
	}


	protected void reset(NodeFeature owner) {
		parent = owner;
		deepest = 0;
		highestLevel = 0;
		lowestLevel = Integer.MAX_VALUE;
		for (int i = 1; i < size+1;i++) {//first node is for us and not a node
			deepest= Math.max(deepest,getFloor(i));
			int level = getLevel(i);
			highestLevel = Math.max(highestLevel, level);
			lowestLevel = Math.min(lowestLevel, level);
		}
	}
	
	protected NodeConnector complete(NodeFeature owner) {
		trim();
		assert size == storage.length-1;
		for (int i = 1; i < size+1;i++) {
			NodeType.getTypeEnum(getTypeNum(i)).singleton.apply(this, i);
		}
		reset(owner);
		return this;
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

	public int getSize() {
		return size;
	}

	public void setStorage(int node, Object object) {
		storage[node] = object;
	}
	
	public Object getStorage(int node) {
		return storage[node];
	}
	
	public Object[] getStorageAsArray(int node) {
		return (Object[])storage[node];
	}
	
	public Person getStorageFirstPerson(int node) {
		Object o = storage[node];
		if (o instanceof Person) {
			return (Person)o;
		}
		Object[] arr = (Object[])(o);
		for (int i = 0; i < arr.length;i++) {
			if (arr[i] instanceof Person) {
				return (Person)arr[i];
			}
		}
		return null;
	}
	
	public <T> T getStorageFirstClass(int node, Class<T> clazz){
		Object o = storage[node];
		if (clazz.isInstance(o)) {
			return clazz.cast(o);
		}
		Object[] arr = (Object[]) o;
		for (int i = 0; i < arr.length;i++) {
			if (clazz.isInstance(arr[i])) {
				return clazz.cast(arr[i]);
			}
		}
		return null;
	}
	
	protected boolean containsOwnable(int node) {
		Object o = storage[node];
		if (o instanceof Object[]) {
			Object[] os = (Object[]) o;
			for (int i = 0; i < os.length;i++) {
				if (isOwnable(os[i])) {
					return true;
				}
			}
			return false;
		}else {
			return isOwnable(o);
		}
	}
	private boolean isOwnable(Object o) {
		if (o instanceof PlantSpot) {
			return true;
		}
		return false;
	}

	public World getWorld() {
		return parent.getTown().getIsland().getWorld();
	}


	public void addVein() {
		assert parent != null;
		if (parent instanceof Mine) {
			((Mine)parent).addVein();
		}
	}
	
	public boolean isForced() {
		return isForceGoIng;
	}


	public static int getLastNode() {
		return Player.player.lastNode;
	}


	public static void setLastNode(int lastNode) {
		Player.player.lastNode = lastNode;
	}


	protected static int getCurrentNode() {
		return Player.player.currentNode;
	}


	protected static void setCurrentNode(int currentNode) {
		Player.player.currentNode = currentNode;
	}


	protected static boolean isForceGoProtection() {
		return Player.player.forceGoProtection;
	}


	protected static void setForceGoProtection(boolean forceGoProtection) {
		Player.player.forceGoProtection = forceGoProtection;
	}


	public static void setKickGate() {
		gateKickBack = true;
	}
	
}
