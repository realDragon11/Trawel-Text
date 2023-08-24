package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.AIClass;
import trawel.Networking;
import trawel.extra;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.World;
import trawel.towns.fort.SubSkill;

public class DungeonNode implements NodeType{

	private static final int EVENT_NUMBER =8;
	
	
	private WeightedTable dungeonGuardRoller, dungeonLootRoller;
	
	/*
	 * 2: single guard
	 * 3: multi guard
	 */
	private static final byte[] GUARD_NUMBERS = new byte[] {2,3};
	
	/*
	 * 5/6: chest/mimic
	 * 7/8: statue/living statue
	 */
	private static final byte[] LOOT_NUMBERS = new byte[] {5,6,7,8};
	
	public DungeonNode() {
		dungeonGuardRoller = new WeightedTable(new float[] {2f,1f});
		dungeonLootRoller = new WeightedTable(new float[] {3f,1f,1.5f,.5f});
	}
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		byte idNum = (byte) extra.randRange(2,EVENT_NUMBER);//1 is ladder
		//might be overwritten by shape but we do it as a backup/full rng
		/*if (extra.chanceIn(1,2)) {
			idNum = GUARD_NUMBERS[dungeonGuardRoller.random(extra.getRand())];
		}
		if (extra.chanceIn(1,10)) {
			idNum = 1;//chest	
		}*/
		int ret = holder.newNode(NodeType.NodeTypeNum.DUNGEON.ordinal(),idNum,tier);
		return ret;
	}

	@Override
	public int generate(NodeConnector holder,int from, int size, int tier) {
		int made = getNode(holder,from,0,tier);
		if (size < 2) {
			return made;
		}
		int split = extra.randRange(1,Math.min(size,3));
		int i = 0;
		int sizeLeft = size-1;
		//now more even, as with new groves, but less likely to fill it entirely
		int baseSize = sizeLeft/split;
		sizeLeft-=baseSize*split;
		while (i < split) {
			int sizeRemove = sizeLeft > 2 ? extra.randRange(1,sizeLeft-1) : 0;
			sizeLeft-=sizeRemove;
			int tempLevel = tier;
			if (extra.chanceIn(1,10)) {
				tempLevel++;
			}
			int n = generate(holder,made,sizeRemove+baseSize,tempLevel);
			holder.setMutualConnect(made, n);
			i++;
		}
		return made;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		NodeConnector start_node = new NodeConnector(owner);
		switch (owner.shape) {
		case NONE: generate(start_node,0,size, tier);
			return start_node.complete(owner);
		case TOWER:
			int curSize = 1;
			List<List<Integer>> floors = new ArrayList<List<Integer>>();
			List<Integer> curFloor;
			int stair;
			int curStair;
			int start = getNode(start_node,0,0,tier);
			start_node.setFloor(start,0);
			stair = start;
			int levelUp = 0;
			int floor = 0;
			int lastNode;
			int curNode;
			//DOLATER: fix order of nodes if that is still an issue
			while (curSize < size) {
				floor++;
				lastNode = stair;
				int stair_level = start_node.getLevel(stair);
				levelUp++;
				curStair = getNode(start_node,stair,0,stair_level+levelUp%3);//stair as from is tentative
				start_node.setStair(curStair);
				start_node.setEventNum(curStair, 1);
				
				stair_level = start_node.getLevel(curStair);
				
				curFloor = new ArrayList<Integer>();
				for (int j = 0; j < 2;j++) {
					for (int i = 0;i <2; i++) {
						floor++;
						curNode = getNode(start_node,0,0,stair_level);
						start_node.setFloor(curNode, floor);
						start_node.setMutualConnect(lastNode, curNode);
						//lastNode.reverseConnections();
						lastNode = curNode;
						curFloor.add(lastNode);
						if (i == 1) {
							start_node.setMutualConnect(lastNode, curStair);
						}
						if (floors.size()%2==0) {//every other floor
							//guard floor
							if (extra.chanceIn(3,4)) {//linking nodes have a 3/4ths chance to become a guard of some sort
								start_node.setEventNum(lastNode,GUARD_NUMBERS[dungeonGuardRoller.random(extra.getRand())]);
							}
						}else {
							if (extra.chanceIn(1,2)) {//linking nodes have a 50% chance to become a loot of some sort
								//might not be a safe loot
								start_node.setEventNum(lastNode,LOOT_NUMBERS[dungeonLootRoller.random(extra.getRand())]);
							}
						}
						
					}
					floor-=2;
					//curStair.getConnects().add(lastNode);
					
					lastNode = stair;
				}
				floor +=10;
				curFloor.add(curStair);
				floors.add(curFloor);
				curSize +=curFloor.size();
				//reverse order of stair connects
				//stair.reverseConnections();
				//curStair.setFloor(floor);
				start_node.setFloor(curStair, floor);
				
				//move onto next floor
				stair = curStair;
			}
			floor+=10;
			int b = NodeType.NodeTypeNum.BOSS.singleton.getNode(start_node, 0, floor, (levelUp%3)+1);
			start_node.setMutualConnect(b, stair);
			start_node.setFloor(b, floor);
			/*
			stair.reverseConnections();
			stair.getConnects().add(b);
			stair.reverseConnections();
			*/
			for (List<Integer> fl: floors) {
				for (Integer f: fl) {
					List<Integer> connects = start_node.getConnects(f);
					int isize = connects.size()-1;
					int[] pass = new int[isize+1];
					for (int i = isize; i >=0 ;i--) {
						int lowest = 256;
						int erasespot = -1;
						int low_loc = -1;
						for (int j = isize; j >=0;j--) {
							int cur = connects.get(j);
							if (cur == 256) {
								continue;
							}
							int cur_num = start_node.getFloor(cur);
							if (lowest > cur_num) {
								low_loc = cur;
								lowest = cur_num;
								erasespot = j;
							}
						}
						assert low_loc > 0;
						pass[i] = low_loc;
						connects.set(erasespot, 256);
					}
					start_node.setConnects(f,pass);
				}
				
			}
			return start_node.complete(owner);
		case RIGGED_DUNGEON:
			int max_level = tier*2;
			int start_level = tier;
			int path_length_weak = 10;
			int weak_end_level = (int) (tier*1.5f);
			int path_length_tough = 4;
			int tough_end_level = tier*2;
			int fight_room = NodeType.NodeTypeNum.BOSS.singleton.getNode(start_node, 0, 0, max_level);
			Dungeon keeper = (Dungeon) owner;
			keeper.setupBattleCons();
			List<SubSkill> skillcon_list = new ArrayList<SubSkill>();
			
			skillcon_list.add(SubSkill.SCRYING);
			skillcon_list.add(SubSkill.DEATH);
			skillcon_list.add(SubSkill.ELEMENTAL);
			Collections.shuffle(skillcon_list);//random order
			while (!skillcon_list.isEmpty()) {
				int last_node = fight_room;
				boolean tough = extra.randRange(0, 1) == 0;
				int this_length = tough ? path_length_tough : path_length_weak;
				int this_end_level = tough ? tough_end_level : weak_end_level;
				for (int i = 0; i < this_length;i++) {
					int cur_node = getNode(start_node,fight_room,i,(int)extra.lerp(start_level, this_end_level, ((float)(i))/this_length));
					start_node.setMutualConnect(last_node, cur_node);
					if (i == this_length-1) {
						start_node.setEventNum(cur_node, 100);
						keeper.registerBattleConWithNode(skillcon_list.remove(0), cur_node);
					}else {
						if (i == this_length-2) {
							start_node.setEventNum(cur_node, 3);//improved guard post
						}
					}
					last_node = cur_node;
				}
			}
			return start_node.complete(owner);
		}
		throw new RuntimeException("Invalid dungeon");
	}
	
	@Override
	public void apply(NodeConnector holder,int madeNode) {
		switch (holder.getEventNum(madeNode)) {
		case 1:
			holder.setForceGo(madeNode,true);
			holder.setStorage(madeNode, extra.choose("stairs","ladder"));
			break;
		case 5: case 6:
			Person mimic = RaceFactory.makeMimic(holder.getLevel(madeNode));
			holder.setStorage(madeNode, new Object[] {extra.choose("Old ","Vibrant ","Simple ","") + "Chest",mimic});
			break;
		case 2:
			holder.setForceGo(madeNode, true);
			holder.setStorage(madeNode, new Object[] {extra.choose("Checkpoint","Barricade","Guardpost")
					,RaceFactory.makeDGuard(holder.getLevel(madeNode))});
		break;
		case 3:
			List<Person> list = new ArrayList<Person>();
			int baseLevel = holder.getLevel(madeNode)+3;
			holder.setLevel(madeNode, baseLevel);//now increases node level
			int guardLevel = baseLevel;
			int testLevel = RaceFactory.addAdjustLevel(guardLevel, 1);
			int guardAmount = 2;
			if (testLevel <= 0) {
				guardAmount = 1;
				guardLevel = baseLevel;
				//we can't fit two guards, so add one stronger guard
			}else {
				guardLevel = testLevel;
				while (guardAmount < 4) {
					if (testLevel > 1 && extra.chanceIn(3,4)) {
						testLevel = RaceFactory.addAdjustLevel(baseLevel,guardAmount);
						if (testLevel <= 0) {
							break;
						}
						guardAmount+=1;
						guardLevel = testLevel;
					}else {
						break;
					}
				}
			}
			
			for (int i = 0; i < guardAmount;i++) {
				list.add(RaceFactory.makeDGuard(guardLevel));
			}
			holder.setForceGo(madeNode, true);
			holder.setStorage(madeNode, new Object[] {
					extra.choose("Large ","Well Lit ","High Security ") +extra.choose("Checkpoint","Barricade","Guardpost")
					,list});
		break;
		case 4:
			GenericNode.applyLockDoor(holder, madeNode);
			break;
		case 8:
			if (extra.chanceIn(1, 3)) {
				holder.setForceGo(madeNode, true);
			}
		case 7:
			holder.setStorage(madeNode, RaceFactory.makeStatue(holder.getLevel(madeNode)));
			break;
		case 100://skillcon holder
			break;
		}
	}
	
	@Override
	public boolean interact(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1: Networking.sendStrong("PlayDelay|sound_footsteps|1|"); break;
		case 2://single guard
			Person guard = holder.getStorageFirstPerson(node);
			Combat gc = Player.player.fightWith(guard);
			if (gc.playerWon() > 0) {
				holder.setForceGo(node,false);
				GenericNode.setSimpleDeadRaceID(holder, node, guard.getBag().getRaceID());
				return false;
			}else {
				return true;
			}
		case 3://multiguards
			List<Person> guards = holder.getStorageFirstClass(node,List.class);
			Combat bgc = Player.player.massFightWith(guards);
			String wasname = holder.getStorageFirstClass(node,String.class);
			if (bgc.playerWon() > 0) {
				holder.setForceGo(node,false);
				
				GenericNode.setTotalDeadString(holder, node,"Wrecked " +wasname,"Examine Bodies","They are slowly rotting.", "pile of corpses");
				return false;
			}else {
				holder.setStorage(node,new Object[] {wasname,bgc.getNonSummonSurvivors()});//they don't revive
				return true;
			}
		case 5: return chest(holder, node);
		case 6: return mimic(holder, node);
		case 8: return statue(holder, node);
		case 7: return statueLoot(holder, node);
		
		case 100://skillcon holder
			if (holder.getStateNum(node) == 0) {
				holder.setStateNum(node,1);
				switch (((Dungeon)holder.parent).requestRemoveBattleCon(node)) {
				default:
					extra.println("You smash the orb of power.");
					break;
				case DEATH:
					extra.println("You smash the orb of power, and the screams of the dead thank you.");
					break;
				case ELEMENTAL:
					extra.println("You smash the orb of power, and the room is briefly brought to a boil.");
					break;
				case SCRYING:
					extra.println("You smash the orb of power, and a vision of an arena flashes in your mind.");
					break;
				}
				
			}else {
				extra.println("The orb is broken into jagged fragments.");
				holder.findBehind(node,"broken orb");
			}
			return false;
		}
		return false;
	}


	private boolean chest(NodeConnector holder, int node) {
		if (holder.getStateNum(node) != 0) {
			extra.println("The "+holder.getStorageFirstClass(node,String.class)+" has already been opened.");
			holder.findBehind(node,"chest");
			return false;
		}
		Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1,p);
		extra.println("Really open the " + holder.getStorageFirstClass(node,String.class) + "?");
		if (extra.yesNo()) {
			holder.setStateNum(node,1);
			if (extra.chanceIn(5,6)) {
				int gold = extra.randRange(0,2)+(extra.randRange(1,3)*holder.getLevel(node));
				Player.player.addGold(gold);
				extra.println("You open the " +holder.getStorageFirstClass(node,String.class) + " and find " + World.currentMoneyDisplay(gold) + ".");
			}else {
				switch (extra.randRange(1,3)) {
				case 1:
					Player.player.emeralds++;
					extra.println("You open the " + holder.getStorageFirstClass(node,String.class) + " and find an emerald!");
					break;
				case 2:
					Player.player.rubies++;
					extra.println("You open the " + holder.getStorageFirstClass(node,String.class) + " and find a ruby!");
					break;
				case 3:
					Player.player.sapphires++;
					extra.println("You open the " + holder.getStorageFirstClass(node,String.class) + " and find a sapphire!");
					break;
				}
			}
			Networking.clearSide(1);
			return false;
		}else {
			Networking.clearSide(1);
			extra.println("You decide not to open it.");
			return false;
		}
	}
	
	private boolean mimic(NodeConnector holder, int node) {

		Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1,p);
		extra.println("Really open the " + holder.getStorageFirstClass(node,String.class) + "?");
		if (extra.yesNo()) {
			extra.println(extra.PRE_RED+"The mimic attacks you!");
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0) {
				holder.setForceGo(node, false);
				GenericNode.setSimpleDeadRaceID(holder, node,p.getBag().getRaceID());
				return false;
			}else {
				return true;
			}
		}else {
			extra.println("You decide not to open it.");
			return false;
		}
	}


	private boolean statue(NodeConnector holder, int node) {
		int state = holder.getStateNum(node);
		Person p = holder.getStorageFirstPerson(node);
		if (state == 0 && !holder.isForced()) {
			extra.println("Really loot the statue?");
			p.getBag().graphicalDisplay(1, p);
			if (!extra.yesNo() && extra.chanceIn(1,2)) {//half chance to attack you anyway
				extra.println("You decide not to loot it.");
				Networking.clearSide(1);
				return false;
			}
			extra.println(extra.PRE_RED+"The statue springs to life and attacks you!");
		}
		if (state == 0) {
			holder.setStateNum(node,1);
			holder.setForceGo(node,true);
		}else {//already attacked
			extra.println("The statue attacks you!");
		}
		
		Combat c = Player.player.fightWith(p);
		if (c.playerWon() > 0) {
			holder.setForceGo(node,false);
			GenericNode.setSimpleDeadString(holder, node, "Living Statue");
			return false;
		}else {
			return true;
		}
	}

	private boolean statueLoot(NodeConnector holder, int node) {
		int state = holder.getStateNum(node);
		Person p = holder.getStorageFirstPerson(node);
		if (state == 0) {
			extra.println("Really loot the statue?");
			p.getBag().graphicalDisplay(1, p);
			if (!extra.yesNo()) {//half chance to attack you anyway
				extra.println("You decide not to loot it.");
				Networking.clearSide(1);
				return false;
			}
			extra.println("You loot the statue...");
			AIClass.playerLoot(p.getBag(),true);
			return false;
		}else {
			extra.println("The " + holder.getStorageFirstPerson(node).getBag().getRace().renderName(false) + " statue has already been looted.");
			holder.findBehind(node,"statue");
			return false;
		}
	}
	
	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.VIRGIN,DrawBane.MIMIC_GUTS,DrawBane.CEON_STONE,DrawBane.TELESCOPE,DrawBane.KNOW_FRAG};
	}

	@Override
	public void passTime(NodeConnector holder, int node, double time, TimeContext calling) {
		// empty
		
	}

	@Override
	public String interactString(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1:
			return "Traverse " +holder.getStorageFirstClass(node,String.class)+".";
		case 5: 
			if (holder.getStateNum(node) != 0) {
				return "Examine Opened " + holder.getStorageFirstClass(node,String.class);
			}
		case 6:
			return "Open the " + holder.getStorageFirstClass(node,String.class);
		case 7: 
			if (holder.getStateNum(node) != 0) {
				return "Looted " + extra.capFirst(holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)) + " Statue";
			}
		case 8:
			return extra.capFirst(holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)) + " Statue";
		case 100:
			if (holder.getStateNum(node) != 0) {
				return "Examine broken orb.";
			}else {
				return "Break orb of power.";
			}
		}
		return null;
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1://ladder etc
			/*if (NodeConnector.currentNode != node) {
				return holder.getStorageFirstClass(node,String.class) + ( 
						holder.getFloor(node) > holder.getFloor(NodeConnector.currentNode)
						? " {up}" : " down");
			}*/
			return holder.getStorageFirstClass(node,String.class);
		case 2://guard
		case 3://guards
			return holder.getStorageFirstClass(node,String.class);
			//door is handled by genericnode
		case 5:
			if (holder.getStateNum(node) != 0) {
				return "Opened " + holder.getStorageFirstClass(node,String.class);
			}
		 case 6:
			return holder.getStorageFirstClass(node,String.class);
		case 7: 
			if (holder.getStateNum(node) != 0) {
				return "Looted " + extra.capFirst(holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)) + " Statue";
			}
		case 8:
			return extra.capFirst(holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)) + " Statue";
		case 100:
			if (holder.getStateNum(node) != 0) {
				return "Destroyed Orb of Power";
			}else {
				return "Orb of Power";
			}
		}
		return null;
	}
	

}
