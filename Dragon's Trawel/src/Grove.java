import java.awt.Color;

public class Grove extends Feature {

	private Town town;
	private int size;
	private GroveNode start;
	public Grove(String name,Town t) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Explore groves to progress in level.";
		generate();
		color = Color.RED;
	}
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|icon|Grove|");
		start.go();
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub
	}
	
	public void generate() {
		start = new GroveNode(size,town.getTier(),this);
	}

}
