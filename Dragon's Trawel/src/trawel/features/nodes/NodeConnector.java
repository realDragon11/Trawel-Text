package trawel.features.nodes;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trawel.DrawBane;
import trawel.Feature;
import trawel.Networking;
import trawel.Player;
import trawel.Skill;
import trawel.extra;
import trawel.mainGame;
import trawel.time.CanPassTime;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public abstract class NodeConnector implements Serializable, CanPassTime {

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
	public boolean isSummit;
	public int floor = 0;
	public boolean isStair = false;
	public String parentName;
	public boolean passing = false;
	public int visited = 0;
	
	protected transient Feature parent;
	//protected Class<Feature> parentType;
	
	public static transient NodeConnector lastNode = null;
	
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
	
	protected abstract String shapeName();


	public void go() {
		Feature.atFeatureForHeader.goHeader();
		if (isSummit) {
			switch(this.shapeName()) {
			case "TOWER":
			Networking.sendStrong("Achievement|tower1|");
			break;
			case "HELL":
				Networking.sendStrong("Achievement|mine2|");
				break;
			}
		}
		Player.addTime(.1);
		mainGame.globalPassTime();
		int i = 1;
		if (forceGo) {
			visited = 3;
			if (interact()) {
				return;
			}

		}
		switch (visited) {
		case 0: Networking.sendColor(Color.ORANGE);break;
		case 1: Networking.sendColor(Color.YELLOW);break;
		case 2: Networking.sendColor(Color.BLUE);break;
		case 3: Networking.sendColor(Color.GREEN);break;
		}
		if (visited < 2) {
			visited = 2;
		}
		extra.println(name);
		extra.println(i+ " " + interactString);i++;
		for (NodeConnector n: connects) {
			switch (n.visited) {
			case 0: Networking.sendColor(Color.ORANGE); n.visited = 1;break;
			case 1: Networking.sendColor(Color.YELLOW);break;
			case 2: Networking.sendColor(Color.BLUE);break;
			case 3: Networking.sendColor(Color.GREEN);break;
			}
			extra.print(i + " " + n.getName());
			if (n.isStair) {
				if (this.floor > n.floor) {
					extra.print(" down");
				}else {
					extra.print(" up");
				}
			}
			if (n.equals(lastNode)) {
				extra.print(" (back) ");
			}
			extra.println();
			if (Player.hasSkill(Skill.TIERSENSE)) {
				extra.println("Tier: " + n.getLevel());
			}
			if (Player.hasSkill(Skill.TOWNSENSE)) {
				extra.println("Connections: " + n.connects.size());
			}
			i++;
			}
		extra.println(i + " exit " + parentName);i++;
		int j = 1;
		int in = extra.inInt(i-1);
		if (in == j) {
			visited = 3;
			if (interact()) {
				return;
			}
		}j++;
		for (NodeConnector n: connects) {
			if (in == j) {
				lastNode = this;
				n.go();
				return;
			}
			j++;
			}
		if (in == j) {
			return;
		}
		go();
	}


	protected abstract boolean interact();
	
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
	//used for nodes without a passtime component
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		return null;
	}
	
	//called in some passTime implementations to propagate it
	public void spreadTime(double time, TimeContext calling) {
		passing = true;
		for (NodeConnector n: connects) {
			if (!n.passing) {
				n.passTime(time,calling);
			}
		}
	}
	
	public List<TimeEvent> timeEvent(double time, TimeContext calling){
		return null;
	}
	
	abstract protected DrawBane[] dbFinds();
	
	protected DrawBane attemptCollectAll(float odds,int amount) {
		NodeFeature keeper = (NodeFeature)parent;
		if (keeper.getFindTime() > 1 && Player.player.sideQuests.size() > 0) {
			if (extra.randFloat() < odds) {
				List<String> list = Player.player.allQTriggers();
				for (DrawBane str: dbFinds()) {
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


	protected void parentChain(Feature p) {
		parent = p;
		passing = true;
		for (NodeConnector n: connects) {
			if (!n.passing) {
				n.parentChain(p);
			}
		}
	}
}
