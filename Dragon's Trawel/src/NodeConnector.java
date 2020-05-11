import java.io.Serializable;
import java.util.ArrayList;

public abstract class NodeConnector implements Serializable {

	//used for connecting event nodes with bosses
	
	protected ArrayList<NodeConnector> connects;
	protected String name;
	protected int level;
	
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


	public abstract void go();
}
