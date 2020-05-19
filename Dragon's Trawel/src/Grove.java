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
		Networking.setArea("forest");
		Networking.sendStrong("Discord|imagesmall|grove|Grove|");
		start.go();
	}

	@Override
	public void passTime(double time) {
		start.passTime(time);
		start.timeFinish();
	}
	
	public void generate() {
		start = new GroveNode(size,town.getTier(),this);
	}
	public Shape getShape() {
		return Shape.STANDARD;
	}
	
	public enum Shape{
		STANDARD;
	}

}
