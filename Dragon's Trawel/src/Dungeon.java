import java.awt.Color;

public class Dungeon extends Feature {

	private Town town;
	private int size;
	private DungeonNode start;
	public Dungeon(String name,Town t) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Explore dungeons to find treasure.";
		generate();
		color = Color.RED;
	}
	@Override
	public void go() {
		Networking.setArea("dungeon");
		Networking.sendStrong("Discord|imagesmall|icon|Dungeon|");
		start.go();
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub
	}
	
	public void generate() {
		start = new DungeonNode(size,town.getTier(),town);
	}

}
