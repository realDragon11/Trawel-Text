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
		shape = s;
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
		switch (shape) {
		case STANDARD: start = new DungeonNode(size,town.getTier(),town,this,false);break;
		case TOWER:
			int curSize = 1;
			List<List<DungeonNode>> floors = new ArrayList<List<DungeonNode>>();
			List<DungeonNode> curFloor;
			DungeonNode stair, stair2;
			start = new DungeonNode(size,town.getTier(),town,this,true);
			stair = start;
			DungeonNode lastNode,lastNode2;
			//List<DungeonNode> lastFloorOnboarding;
			//List<DungeonNode> thisFloorOnboarding;
			while (curSize < size) {
				lastNode = stair;
				//thisFloorOnboarding = new ArrayList<DungeonNode>();
				curFloor = new ArrayList<DungeonNode>();
				for (int i = 0;i <2; i++) {
					lastNode2 = new DungeonNode(size,stair.getLevel()+1,town,this,true);
					lastNode.getConnects().add(lastNode2);
					lastNode = lastNode2;
					curFloor.add(lastNode);
					if (i == 0) {
						stair.getConnects().add(lastNode);
					}
				}
				//thisFloorOnboarding.add(lastNode);
				stair2 = new DungeonNode(size,stair.getLevel()+1,town,this,true);
				stair2.getConnects().add(lastNode);
				lastNode.getConnects().add(stair2);
				lastNode = stair;
				for (int i = 0;i <2; i++) {
					lastNode2 = new DungeonNode(size,stair.getLevel()+1,town,this,true);
					lastNode.getConnects().add(lastNode2);
					lastNode = lastNode2;
					curFloor.add(lastNode);
					if (i == 0) {
						stair.getConnects().add(lastNode);
					}
				}
				//thisFloorOnboarding.add(lastNode);
				stair2.getConnects().add(lastNode);
				lastNode.getConnects().add(stair2);
				floors.add(curFloor);
				curSize +=curFloor.size();
				//lastFloorOnboarding = thisFloorOnboarding;
				//reverse order of stair connects
				stair.reverseConnections();
				//move onto next floor
				stair = stair2;
			}
			
			//add back connections
			//start.addBacks();
			break;
		}
	}
	public Shape getShape() {
		return shape;
	}

}
