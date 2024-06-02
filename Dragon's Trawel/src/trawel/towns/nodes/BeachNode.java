package trawel.towns.nodes;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.extra;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.behaviors.GateNodeBehavior;
import trawel.time.TimeContext;

public class BeachNode implements NodeType {
	
	/**
	 * EntryRoller is used in the first 2 layers of beaches to avoid blocking off the entrance with a fight and low hanging fruit rewards
	 */
	private WeightedTable beachBasicRoller, beachEntryRoller;
	
	public BeachNode() {
		beachBasicRoller = new WeightedTable(new float[] {
				//skeleton pirate blocker
				0f
				,1f//placeholder
		});
		beachEntryRoller = new WeightedTable(new float[] {
				//skeleton pirate blocker
				0f
				,1f//placeholder
		});
	}
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		int id = 1;//since first content node is 1 id, and we add from there, including adding 0
		switch (guessDepth) {
		case 0://start
		case 1: case 2://entry
			id+=beachEntryRoller.random(extra.getRand());
			break;
		default:
			id+=beachBasicRoller.random(extra.getRand());
			break;
		}
		int ret = holder.newNode(NodeType.NodeTypeNum.BEACH.ordinal(),id,tier);
		holder.setFloor(ret, guessDepth);
		return ret;
	}
	
	@Override
	public void apply(NodeConnector holder, int node) {
		switch (holder.getEventNum(node)) {
		case 1://beach dungeon blocker
			Person skelePerson; 
			int gateType = extra.randRange(1,3);
			switch (gateType) {
				default: case 0:
					//skeleton pirate
					skelePerson = RaceFactory.makeSkeletonPirate(holder.getLevel(node));
					break;
				case 1://hidden map
					skelePerson = RaceFactory.makeCollector(holder.getLevel(node));
					break;
			}
			Agent skeleAgent = skelePerson.getMakeAgent(AgentGoal.WORLD_ENCOUNTER);
			holder.parent.getTown().getIsland().getWorld().addReoccuring(skeleAgent);
			GateNodeBehavior behavior = new GateNodeBehavior(skeleAgent,holder.parent);
			skeleAgent.enqueueBehavior(behavior);
			holder.setForceGo(node,true);
			holder.setStorage(node,behavior);
			break;
		}
	}
	
	@Override
	public int generate(NodeConnector holder, int from, int sizeLeft, int tier) {
		int floor = from == 0 ? 0 : holder.getFloor(from)+1;
		int node = getNode(holder,from,floor,tier);
		sizeLeft--;
		if (sizeLeft < 2) {//if we don't have much left to do anything, return
			return node;
		}
		int routeA = generate(holder,from,(sizeLeft/2) + (extra.chanceIn(1,3) ? 1 : 0),tier + (extra.chanceIn(1,10) ? 1 : 0));
		int routeB = generate(holder,from,(sizeLeft/2) + (extra.chanceIn(1,3) ? 1 : 0),tier + (extra.chanceIn(1,10) ? 1 : 0));
		holder.setMutualConnect(node, routeA);
		holder.setMutualConnect(node, routeB);
		return node;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		NodeConnector start = new NodeConnector(owner);
		start.parent = owner;
		switch (owner.getShape()) {
			case TREASURE_BEACH://minimum rec'd size of 32, is probably more volatile than usual nodefeatures
				//split into 4-5 main quadrants, min 3 each
				int branchAmount = extra.randRange(4,5);
				int branchSize = Math.max(8,size/branchAmount);
				int dungeonsLeft = 2;//minimum two dungeons
				int cavesLeft = 3;//minimum three caves
				int entry = getNode(start,0,0,tier);
				for (int branch = 0; branch < branchAmount;branch++) {
					int headNode = getNode(start,entry,1,tier);
					int subSizeLeft = branchSize;
					//used to help with trying to force the quota, can only force once per branch
					boolean dungeonYet = false;
					boolean caveYet = false;
					for (int subNum = 0; subNum < 5; subNum++) {
						//max subnum is 5 but will terminate early if running out of space
						int subSize = extra.randRange(3,branchSize/2);
						//subbranch types: 0 = none, goto beach normal generate; 1 = cave, make linear cave as with grove; 2 = dungeon, make a small dungeon
						int subType = 0;
						//first sub-branch will always be a beach branch
						if (subNum > 0) {
							//if we roll a dungeon, or if we won't meet quota without one forced per branch remaining
							//first branch is immune to minimum if 3 or less, since 3-0 = 3, but can still roll naturally
							if (extra.chanceIn(1,20) || (dungeonYet == false && dungeonsLeft > 3-branch)) {
								dungeonYet = true;
								dungeonsLeft--;
								subType = 2;
							}else {
								//dungeon takes priority over cave
								//if we roll a cave, or haven't met quota yet
								if (extra.chanceIn(1,10) || (caveYet == false && cavesLeft > 3-branch)) {
									caveYet = true;
									cavesLeft--;
									subType = 1;
								}
							}
						}
						int subHead;
						switch (subType) {
						case 0://normal none beach
							//uses base tier instead of going up by one
							subHead = generate(start,headNode, subSize, tier);
							start.setMutualConnect(headNode,subHead);
							break;
						case 1://linear cave
							int tempTier = tier+1;
							int caveFloor = start.getFloor(headNode)+1;
							subHead = NodeType.NodeTypeNum.CAVE.singleton.getNode(start, headNode, caveFloor, tempTier);
							//set to cave entrance
							start.setEventNum(subHead,1);
							start.setMutualConnect(headNode,subHead);
							int caveLast = subHead;
							for (int i = 1; i < subSize;i++) {
								int caveNext = NodeType.NodeTypeNum.CAVE.singleton.getNode(start,caveLast,++caveFloor, tempTier);
								start.setMutualConnect(caveLast, caveNext);
								caveLast = caveNext;
								//tier levels up much more often in caves
								if (extra.chanceIn(1,3)) {
									tempTier++;
								}
							}
							break;
						case 2://small dungeon
							//TODO: dungeon should have a large reward somewhere in it?
							subHead = getNode(start, headNode, start.getFloor(headNode)+1, tier+1);
							//set to skeleton pirate blocker on dungeon
							start.setEventNum(subHead,1);
							int dungeonNodes = NodeType.NodeTypeNum.DUNGEON.singleton.generate(start,subHead,subSize,tier+1);
							start.setMutualConnect(subHead,dungeonNodes);
							break;
						}
						//we decided what branch we want to use fully, subtract out budget
						//will not be exact due to using two generates and not recording how much they make
						subSizeLeft-=subSize;
						//if we're almost out of budget, cancel. Can go slightly over and under budget
						//this can lead to quotas not being met if budget rolls poorly but is unlikely
						if (subSizeLeft <= 2) {
							break;
						}
					}
					
					//shuffle subbranches to avoid consistently lopsided chances
					start.shuffleConnects(headNode);
					start.setMutualConnect(entry,headNode);
				}
				//shuffle branches after since each of them will get different chances of stuff
				start.shuffleConnects(entry);
				
				return start.complete(owner);
		}
		throw new RuntimeException("Invalid Beach: "+owner.getName());
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch (holder.getEventNum(node)) {
		case 0://skeleton pirate blocker
			return holder.getStorageFirstClass(node,GateNodeBehavior.class).getNodeName();		
		}
		return null;
	}
	
	@Override
	public String interactString(NodeConnector holder, int node) {
		switch (holder.getEventNum(node)) {
		case 0://skeleton pirate blocker
			return holder.getStorageFirstClass(node,GateNodeBehavior.class).getInteractText();		
		}
		return null;
	}
	
	@Override
	public boolean interact(NodeConnector holder, int node) {
		switch (holder.getEventNum(node)) {
		case 0:
			GateNodeBehavior gnb = holder.getStorageFirstClass(node,GateNodeBehavior.class);
			if (gnb.opened) {//already been opened
				extra.println(gnb.getOpenedText());
				return false;
			}else {
				if (gnb.checkOpened()) {
					//opening!
					extra.println(gnb.getOpeningText());
					return false;
				}else {
					//still locked
					extra.println(gnb.getLockedInteract());
					NodeConnector.setKickGate();//gate kick back
					return false;
				}
			}
			//all paths return by this point so return would be unreachable
		}
		return false;
	}

	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.WOOD,DrawBane.TELESCOPE,DrawBane.KNOW_FRAG};
	}

	@Override
	public void passTime(NodeConnector holder, int node, double time, TimeContext calling) {
		// TODO Auto-generated method stub

	}

}
