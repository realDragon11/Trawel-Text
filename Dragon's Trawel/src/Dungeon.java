import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Dungeon extends Feature {

	private Town town;
	private int size;
	private DungeonNode start;
	public enum Shape{
		STANDARD, TOWER;
	}
	private Shape shape;
	public Dungeon(String name,Town t,Shape s) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Explore dungeons to find treasure.";
		generate();
		color = Color.RED;
		shape = s;
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
		switch (shape) {
		case STANDARD: start = new DungeonNode(size,town.getTier(),town,this,false);break;
		case TOWER:
			int curSize = 1;
			List<List<DungeonNode>> floors = new ArrayList<List<DungeonNode>>();
			List<DungeonNode> curFloor;
			start = new DungeonNode(size,town.getTier(),town,this,true);
			while (curSize < size) {
				curFloor = new ArrayList<DungeonNode>();
				
			}
			
			//add back connections
			start.addBacks();
			break;
		}
	}
	public Shape getShape() {
		return shape;
	}

}
