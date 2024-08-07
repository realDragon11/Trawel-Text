package trawel.towns.features.nodes;
import com.github.yellowstonegames.core.WeightedTable;

import trawel.core.Print;
import trawel.core.Rand;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.time.TimeContext;

public class CaveNode implements NodeType{
	
	private WeightedTable caveBasicRoller, caveEntryRoller, caveRegrowRoller;
	
	public CaveNode() {
		caveBasicRoller = new WeightedTable(new float[] {
				//1: entryway
				0f,
				//2: rage bear
				1f,
				//3: vein
				1f,
				//4: rage bat
				1f
		});
		caveEntryRoller = new WeightedTable(new float[] {
				//1: entryway
				0f,
				//2: rage bear
				0f,
				//3: vein
				1f,
				//4: rage bat
				0f
		});
		caveRegrowRoller = new WeightedTable(new float[] {
				//1: entryway
				0f,
				//2: rage bear
				1f,
				//3: vein
				1f,
				//4: rage bat
				1f
		});
	}
	
	@Override
	public int rollRegrow() {
		return 1+caveRegrowRoller.random(Rand.getRand());
	}
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		int idNum = 1;//start at 1
		switch (guessDepth) {
		case 0://start
		case 1://entry
			idNum+=caveEntryRoller.random(Rand.getRand());
			break;
		default:
			idNum+=caveBasicRoller.random(Rand.getRand());
			break;
		}
		
		int ret = holder.newNode(NodeType.NodeTypeNum.CAVE.ordinal(),idNum,tier);
		holder.setFloor(ret, guessDepth);
		return ret;
	}
	
	//NOTE: idNum = -1 is reserved by GroveNode
	/*
	  if (stair) {
			idNum = -2;
			isStair = true;
		}
	 */
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		NodeConnector start = new NodeConnector(owner);
		generate(start,0,size,tier);
		return start.complete(owner);
	}
	
	@Override
	public int generate(NodeConnector holder,int from, int size, int tier) {
		size--;
		int made = getNode(holder,from,from == 0 ? 0 : holder.getFloor(from)+1,tier);
		int split;
		int sizePer;
		if (size < 5) {
			split = size;
			sizePer = 1;
		}else {
			split = 2;
			sizePer = size/2;
		}
		for (int i = 0; i < split;i++) {
			int tempLevel = tier;
			if (Rand.chanceIn(1,5)) {//much higher chance to level up
				tempLevel++;
			}
			int n = generate(holder,made,sizePer,tempLevel);
			holder.setMutualConnect(n,made);
		}
		return made;
	}

	@Override
	public void apply(NodeConnector holder,int madeNode) {
		switch (holder.getEventNum(madeNode)) {
		case 1:
			//made.name = "cave entrance";
			//made.interactString = "traverse "+made.name;
			//made.setForceGo(true);
			holder.setForceGo(madeNode,true);
			break;
		case 2:
			GenericNode.setBasicRagePerson(holder,madeNode,RaceFactory.makeBear(holder.getLevel(madeNode)),"Sleeping Bear","The bear here mauls you!");
			break;
		case 3:
			GenericNode.applyGenericVein(holder, madeNode, 1);
			break;
		case 4:
			GenericNode.setBasicRagePerson(holder,madeNode,RaceFactory.makeBat(holder.getLevel(madeNode)),"Ceiling Bat","The bat swoops down to attack you!");
			break;
		}
	}
	
	@Override
	public boolean interact(NodeConnector holder,int node) {
		switch(holder.getEventNum(node)) {
		case 1:
			Print.println("The cave entrance is damp.");
			break;
		}
		return false;
	}
	
	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.MEAT,DrawBane.BAT_WING,DrawBane.HONEY};
	}

	@Override
	public void passTime(NodeConnector holder,int node, double time, TimeContext calling){
		//none for now
	}

	@Override
	public String interactString(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1:
			return "Examine entryway.";
		}
		return null;
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1:
			return "Cave Entrance";
		}
		return null;
	}
	
	/*
	private boolean bear1(NodeConnector holder,int node) {
		if (holder.getStateNum(node) == 0) {
			extra.println(extra.PRE_RED+"The bear attacks you!");
			Person p = holder.getStorageFirstPerson(node);
				Combat c = Player.player.fightWith(p);
				if (c.playerWon() > 0) {
					GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
					holder.setForceGo(node,false);
				}else {
					return true;
				}
		}
		return false;
	}


	private void goldVein1() {
		if (node.state == 0) {
			Networking.unlockAchievement("ore1");
			int mult1 = 0, mult2 = 0;
			switch (node.storage1.toString()) {
			case "gold": mult1 = 5; mult2 = 10;break;
			case "silver": mult1 = 3; mult2 = 7;break;
			case "platinum": mult1 = 6; mult2 = 12;break;
			case "iron": mult1 = 2; mult2 = 5;break;
			case "copper": mult1 = 1; mult2 = 3;break;
			}
			int gold = extra.randRange(0,2)+extra.randRange(mult1,mult2)*node.level;
			Player.player.addGold(gold);
			extra.println("You mine the vein for "+node.storage1+" worth "+ World.currentMoneyDisplay(gold) + ".");
			node.state = 1;
			node.name = "empty vein";
			node.interactString = "examine empty vein";
		}else {
			extra.println("The "+node.storage1+" has already been mined.");
			node.findBehind("vein");
		}
	}
	
	private void bat1() {
		if (node.state == 0) {
			extra.println(extra.PRE_RED+"The bat attacks you!");
			Person p = (Person)node.storage1;
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					node.state = 1;
					node.storage1 = null;
					node.name = "dead "+node.name;
					node.interactString = "examine body";
					node.setForceGo(false);
				}
		}else {
			extra.println("The bat corpse lies here.");
			//too small for now
		}
		
	}*/
	

}
