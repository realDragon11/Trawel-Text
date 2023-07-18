package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import trawel.FeatureNodes.NodeFeature;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class Dungeon extends NodeFeature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public enum Shape{
		STANDARD, TOWER;
	}
	private Shape shape;
	private int boss;
	public Dungeon(String name,Town t,Shape s,int bossType) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Explore dungeons to find treasure.";
		shape = s;
		generate();
		color = Color.RED;
		boss = bossType;
		
	}
	@Override
	public void go() {
		Networking.setArea("dungeon");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|dungeon|Dungeon|");
		start.go();
	}
	
	@Override
	protected void generate() {
		switch (shape) {
		case STANDARD: start = new DungeonNode(size,town.getTier(),town,this,false);break;
		case TOWER:
			int curSize = 1;
			List<List<NodeConnector>> floors = new ArrayList<List<NodeConnector>>();
			List<NodeConnector> curFloor;
			NodeConnector stair;
			NodeConnector curStair;
			start = new DungeonNode(size,town.getTier(),town,this,true);
			start.floor = 0;
			stair = start;
			int levelUp = 0;
			int floor = 0;
			NodeConnector lastNode;
			NodeConnector lastNode2;
			//List<DungeonNode> lastFloorOnboarding;
			//List<DungeonNode> thisFloorOnboarding;//TODO: fix order of nodes
			while (curSize < size) {
				floor++;
				lastNode = stair;
				
				//thisFloorOnboarding = new ArrayList<DungeonNode>();
				levelUp++;
				curStair = new DungeonNode(size,stair.getLevel()+(levelUp == 3 ? 1 : 0),town,this,true);
				
				curFloor = new ArrayList<NodeConnector>();
				for (int i = 0;i <2; i++) {
					floor++;
					lastNode2 = new DungeonNode(size,stair.getLevel()+(levelUp == 3 ? 1 : 0),town,this,false);
					lastNode2.floor = floor;
					lastNode.getConnects().add(lastNode2);
					lastNode2.getConnects().add(lastNode);
					lastNode.reverseConnections();
					lastNode = lastNode2;
					curFloor.add(lastNode);
					/*if (i == 0) {
						stair.getConnects().add(lastNode);
					}*/
					if (i == 1) {
						lastNode.getConnects().add(curStair);
					}
				}
				floor-=2;
				//thisFloorOnboarding.add(lastNode);
				curStair.getConnects().add(lastNode);
				
				lastNode = stair;
				for (int i = 0;i <2; i++) {
					floor++;
					lastNode2 = new DungeonNode(size,stair.getLevel()+(levelUp == 3 ? 1 : 0),town,this,false);
					lastNode2.floor = floor;
					lastNode.getConnects().add(lastNode2);
					lastNode2.getConnects().add(lastNode);
					lastNode.reverseConnections();
					lastNode = lastNode2;
					curFloor.add(lastNode);
					/*if (i == 0) {
						stair.getConnects().add(lastNode);
					}*/
					if (i == 1) {
						lastNode.getConnects().add(curStair);
					}
					
				}
				floor-=2;
				floor +=10;
				curFloor.add(curStair);
				//thisFloorOnboarding.add(lastNode);
				curStair.getConnects().add(lastNode);
				floors.add(curFloor);
				curSize +=curFloor.size();
				//lastFloorOnboarding = thisFloorOnboarding;
				//reverse order of stair connects
				stair.reverseConnections();
				floor++;
				curStair.floor = floor;
				//move onto next floor
				stair = curStair;
				if (levelUp == 3) {
					levelUp = 0;
				}
			}
			stair.isSummit = true;
			BossNode b =new BossNode(stair.getLevel(),boss);
			b.getConnects().add(stair);
			b.floor = floor +=10;
			b.parentName = "dungeon";
			stair.reverseConnections();
			stair.getConnects().add(b);
			stair.reverseConnections();
			//
			for (List<NodeConnector> fl: floors) {
				for (NodeConnector f: fl) {
					f.getConnects().sort(new Comparator<NodeConnector>() {

						@Override
						public int compare(NodeConnector a0, NodeConnector a1) {
							return (int) Math.signum(a0.floor-a1.floor);//TODO
						}});
					f.reverseConnections();
				}
				
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
