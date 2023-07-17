package trawel;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import trawel.time.CanPassTime;

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
	public abstract void timeFinish();
}
