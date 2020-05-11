import java.io.Serializable;
import java.util.ArrayList;

public abstract class NodeConnector implements Serializable {

	//used for connecting event nodes with bosses
	
	protected ArrayList<NodeConnector> connects;
	protected String name;
	protected int level;
	protected String interactString = "ERROR";
	protected boolean forceGo = false;
	public boolean isSummit;
	
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


	public void go() {
		if (isSummit) {
			Networking.sendStrong("Achievement|tower1|");
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
			extra.println(i + " " + n.getName());
			if (Player.hasSkill(Skill.TIERSENSE)) {
				extra.println("Tier: " + n.getLevel());
			}
			if (Player.hasSkill(Skill.TOWNSENSE)) {
				extra.println("Connections: " + n.connects.size());
			}
			i++;
			}
		extra.println(i + " exit dungeon");i++;
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
}
