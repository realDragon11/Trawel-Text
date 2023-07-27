package trawel.towns.nodes;
import java.awt.Color;
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
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.personal.people.Skill;
import trawel.time.CanPassTime;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class NodeConnector implements Serializable {

	//used for connecting event nodes with bosses
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<NodeConnector> connects;
	protected String name;
	protected int level;
	protected String interactString = "ERROR";
	protected boolean forceGo = false;
	public boolean isSummit;//DOLATER: make just detect deepest floor
	public int floor = 0;//DOLATER: make floor apply to all types as 'depth'
	public boolean isStair = false;//idk what to do
	//public String parentName;//removed, convert to parent
	public transient boolean passing;//FIXME: might have to init at false every time
	public byte visited = 0;
	
	protected byte typeNum;
	protected byte eventNum;
	protected int state;
	protected int idNum;
	protected Object storage1, storage2;
	
	
	protected transient NodeFeature parent;
	//protected Class<Feature> parentType;
	
	public static NodeConnector lastNode = null;
	protected static NodeConnector currentNode = null;
	
	protected NodeConnector() {
		connects = new ArrayList<NodeConnector>();
		state = 0;
		floor = -1;
		typeNum = -1;
		eventNum = -1;
		forceGo = false;
	}
	
	public ArrayList<NodeConnector> getConnects() {
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
				if (isSummit) {
					switch(NodeConnector.currentNode.parent.getShape()) {
					case TOWER:
					Networking.sendStrong("Achievement|tower1|");
					break;
					case ELEVATOR:
						if (parent instanceof Mine) {
							Networking.sendStrong("Achievement|mine2|");
						}
						break;
					}
				}
				if (forceGo) {
					visited = 3;
					if (parent.numType(typeNum).interact(NodeConnector.this)) {
						NodeConnector.lastNode = null;
						return null;
					}
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
						NodeConnector.lastNode = null;
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
			switch (owner.visited) {
			case 0: visitColor = extra.COLOR_NEW; owner.visited = 2;break;
			case 1: visitColor = extra.COLOR_SEEN; owner.visited = 2;break;
			case 2: visitColor = extra.COLOR_BEEN;break;
			case 3: visitColor = extra.COLOR_OWN;break;
			}
			return visitColor + owner.name;
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
			if (owner.visited != 3) {
				visitColor = extra.TIMID_GREY;
			}
			return visitColor + owner.name;
		}

		@Override
		public boolean go() {
			owner.visited = 3;
			if (parent.numType(typeNum).interact(currentNode)) {
				currentNode = null;
			}
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
			switch (owner.visited) {
			case 0: visitColor = extra.COLOR_NEW; owner.visited = 1;break;
			case 1: visitColor = extra.COLOR_SEEN;break;
			case 2: visitColor = extra.COLOR_BEEN;break;
			case 3: visitColor = extra.COLOR_OWN;break;
			}
			String postText = "";
			if (owner.isStair) {
				if (owner.floor > NodeConnector.currentNode.floor) {
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
			currentNode = owner;
			owner.visited = 2;
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
		NodeFeature keeper = (NodeFeature)parent;
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
		passing = true;
		for (NodeConnector n: connects) {
			if (!n.passing) {
				n.parentChain(p);
			}
		}
	}
	
	protected NodeConnector finalize(NodeFeature owner) {
		owner.numType(typeNum).apply(this);
		return this;
	} 
}
