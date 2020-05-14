import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public abstract class NodeConnector implements Serializable {

	//used for connecting event nodes with bosses
	
	protected ArrayList<NodeConnector> connects;
	protected String name;
	protected int level;
	protected String interactString = "ERROR";
	protected boolean forceGo = false;
	public boolean isSummit;
	public int floor = 0;
	public boolean isStair = false;
	public String parentName;
	
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
		
		int i = 1;
		if (forceGo) {
			if (interact()) {
				return;
			}

		}
		extra.println(name);
		extra.println(i+ " " + interactString);i++;
		for (NodeConnector n: connects) {
			extra.print(i + " " + n.getName());
			if (n.isStair) {
				if (this.floor > n.floor) {
					extra.print(" down");
				}else {
					extra.print(" up");
				}
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
			interact();
		}j++;
		for (NodeConnector n: connects) {
			if (in == j) {
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
}
