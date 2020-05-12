import java.awt.Color;

public class Mine extends Feature {

	private Town town;
	private int size;
	private MineNode start;
	private int veinsLeft = 0;
	public enum Shape{
		STANDARD, HELL;
	}
	private Shape shape;
	public Mine(String name,Town t, SuperPerson owner,Shape s) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Mines have minerals for you to make profit off of.";
		generate();
		color = Color.RED;
		this.owner = owner;
		shape = s;
	}
	@Override
	public void go() {
		Networking.setArea("mine");
		Networking.sendStrong("Discord|imagesmall|icon|Mine|");
		start.go();
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub
	}
	
	public void generate() {
		switch (shape) {
		case STANDARD:
			start = new MineNode(size,town.getTier(),this);
			break;
		case HELL:
			start = new MineNode(size,town.getTier(),this);
			MineNode lastNode = start;
			MineNode newNode;
			for (int i = 0;i < 50;i++) {
				newNode = new MineNode(size,town.getTier()+(i/10),this);
				lastNode.getConnects().add(newNode);
				newNode.getConnects().add(lastNode);
				lastNode.reverseConnections();
			}
			BossNode b = new BossNode(town.getTier()+5,1);
			lastNode.getConnects().add(b);
			b.getConnects().add(lastNode);
			lastNode.reverseConnections();
			break;
		}
	}
	
	public void addVein() {
		veinsLeft++;
	}
	public void removeVein() {
		veinsLeft--;
		if (veinsLeft == 0 && shape.equals(Shape.STANDARD)) {
			Networking.sendStrong("Achievement|miner1|");
		}
	}
	public Shape getShape() {
		return shape;
	}
	


}
