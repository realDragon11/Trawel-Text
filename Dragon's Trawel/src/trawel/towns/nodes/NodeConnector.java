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
	
	public transient boolean passing;
	protected transient NodeFeature parent;
	
	public static NodeConnector lastNode = null;
	protected static NodeConnector currentNode = null;
	protected static boolean forceGoProtection = false;
	
	protected NodeConnector() {
		connects = new ArrayList<NodeConnector>();
		state = 0;
		floor = -1;
		typeNum = Byte.MIN_VALUE;
		eventNum = Byte.MIN_VALUE;
		setForceGo(false);
	}
	
	public boolean getFlag(NodeFlag f) {
		return extra.getEnumByteFlag(f.ordinal(), flags);
	}
	
	public void setFlag(NodeFlag f, boolean bool) {
		flags = extra.setEnumByteFlag(f.ordinal(), flags, bool);
	}
	
	public List<NodeConnector> getConnects() {
		return connects;
	}


	protected void setConnects(ArrayList<NodeConnector> connects) {
		this.connects = connects;
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

	public static void enter(NodeConnector start) {
		Feature.atFeatureForHeader.goHeader();
		lastNode = null;
		currentNode = start;
		while (currentNode != null) {
			Player.addTime(.1);
			mainGame.globalPassTime();
			extra.menuGo(currentNode.menuGen());
		}
	}
	
	public MenuGenerator menuGen() {
		return new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				if (NodeConnector.currentNode.parent.isDeepest(NodeConnector.this)) {
					switch(NodeConnector.currentNode.parent.getShape()) {
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
				if (isForceGo() && !forceGoProtection) {
					setVisited(3);
					forceGoProtection = true;
					interactCode();
					return null;//redo operation
				}
				mList.add(new NodeMenuTitle(NodeConnector.this));
				mList.add(new NodeMenuInteract(NodeConnector.this));
				for (NodeConnector n: connects) {
					mList.add(new NodeMenuItem(n));
				}
				mList.add(new MenuLast() {

					@Override
					public String title() {
						return extra.TIMID_RED + "exit " + parent.getName();//TODO: fix parent name
					}

					@Override
					public boolean go() {
						NodeConnector.currentNode = null;
						return true;
					}
					
				});
				return mList;
			}
		};
	}
	
	protected class NodeMenuTitle extends MenuLine{

		private NodeConnector owner;
		
		public NodeMenuTitle(NodeConnector own) {
			owner = own;
		}
		
		@Override
		public String title() {
			String visitColor = extra.PRE_WHITE;
			switch (owner.getVisited()) {
			case 0: visitColor = extra.VISIT_NEW; owner.setVisited(2);break;
			case 1: visitColor = extra.VISIT_SEEN; owner.setVisited(2);break;
			case 2: visitColor = extra.VISIT_BEEN;break;
			case 3: visitColor = extra.VISIT_DONE;break;
			}
			return visitColor + owner.name;
		}
		
	}
	
	protected void interactCode() {
		if (typeNum < 0) {
			switch (typeNum) {
			case -1:
				if (BossNode.getSingleton().interact(currentNode)) {
					currentNode = null;
				}
				break;
			}
			return;
		}
		if (parent.numType(typeNum).interact(currentNode)) {
			currentNode = null;
		}
	}
	protected class NodeMenuInteract extends MenuSelect{

		private NodeConnector owner;
		
		public NodeMenuInteract(NodeConnector own) {
			owner = own;
		}
		
		@Override
		public String title() {
			String visitColor = extra.PRE_WHITE;
			if (owner.getVisited() != 3) {
				visitColor = extra.TIMID_GREY;
			}
			return visitColor + owner.interactString;
		}

		@Override
		public boolean go() {
			owner.setVisited(3);
			interactCode();
			return true;
		}
		
	}
	
	protected class NodeMenuItem extends MenuSelect{

		private NodeConnector owner;
		
		public NodeMenuItem(NodeConnector own) {
			owner = own;
		}
		
		@Override
		public String title() {
			String visitColor = extra.PRE_WHITE;
			switch (owner.getVisited()) {
			case 0: visitColor = extra.VISIT_NEW; owner.setVisited(1);break;
			case 1: visitColor = extra.VISIT_SEEN;break;
			case 2: visitColor = extra.VISIT_BEEN;break;
			case 3: visitColor = extra.VISIT_DONE;break;
			}
			String postText = "";
			if (owner.isStair()) {
				if (owner.floor < NodeConnector.currentNode.floor) {
					postText = " down";
				}else {
					postText = " up";
				}
			}
			if (Player.hasSkill(Skill.TIERSENSE)) {
				postText +=" T: " + owner.getLevel();
			}
			if (Player.hasSkill(Skill.TOWNSENSE)) {
				postText +=" C: " + owner.connects.size();
			}
			return visitColor + owner.name +postText+ (owner == NodeConnector.lastNode ? extra.PRE_WHITE+" (back)" : "");
		}

		@Override
		public boolean go() {
			forceGoProtection = false;
			NodeConnector.lastNode = NodeConnector.currentNode;
			currentNode = owner;
			owner.setVisited(2);
			return true;//always return true to prevent recursive nesting issues
		}
		
	}
	
	public void reverseConnections() {
		Collections.reverse(connects);
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
		parent.numType(typeNum).passTime(this, time, calling);
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


	protected void parentChain(NodeFeature p) {
		parent = p;
		p.size++;
		passing = true;
		for (NodeConnector n: connects) {
			if (!n.passing) {
				n.parentChain(p);
			}
		}
		p.deepest = (short) Math.max(p.deepest,getFloor());
	}
	
	protected NodeConnector finalize(NodeFeature owner) {
		if (typeNum < 0) {
			switch (typeNum) {
			case -1:
				BossNode.getSingleton().apply(this);
				break;
			}
			return this;
		}
		owner.numType(typeNum).apply(this);
		if (eventNum == 3) {
			if (owner instanceof Mine) {
				((Mine) owner).addVein();
			}
		}
		return this;
	}
	
	protected void setStair() {
		setFlag(NodeFlag.STAIR, true);
	}

	protected void setFloor(int b) {
		floor = (short) b;
	}

	public boolean isStair() {
		return getFlag(NodeFlag.STAIR);
	}

	public int getFloor() {
		return floor;
	}

	public boolean isForceGo() {
		return getFlag(NodeFlag.FORCEGO);
	}

	public void setForceGo(boolean forceGo) {
		setFlag(NodeFlag.FORCEGO,forceGo);
	}

	public byte getVisited() {
		//we don't actually need to know the order since we look per bit
		//but left is more significant in effect below
		if (getFlag(NodeFlag.VISIT_BIT1)) {//1X
			if (getFlag(NodeFlag.VISIT_BIT2)) {//11
				return 3;
			}else {//10
				return 2;
			}
		}else {//0X
			if (getFlag(NodeFlag.VISIT_BIT2)) {//01
				return 1;
			}else {//00
				return 0;
			}
		}
	}

	public void setVisited(int visited) {
		switch (visited) {
		case 0:
			setFlag(NodeFlag.VISIT_BIT1,false);
			setFlag(NodeFlag.VISIT_BIT2,false);
			break;
		case 1:
			setFlag(NodeFlag.VISIT_BIT1,false);
			setFlag(NodeFlag.VISIT_BIT2,true);
			break;
		case 2:
			setFlag(NodeFlag.VISIT_BIT1,true);
			setFlag(NodeFlag.VISIT_BIT2,false);
			break;
		case 3:
			setFlag(NodeFlag.VISIT_BIT1,true);
			setFlag(NodeFlag.VISIT_BIT2,true);
			break;
		}
	}
	
}
