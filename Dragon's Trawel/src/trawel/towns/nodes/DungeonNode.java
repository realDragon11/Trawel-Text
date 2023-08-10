package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.AIClass;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.World;

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
		byte idNum = (byte) extra.randRange(1,EVENT_NUMBER);
		//might be overwritten by shape but we do it as a backup/full rng
		NodeConnector make = new NodeConnector();
		/*if (extra.chanceIn(1,2)) {
			idNum = GUARD_NUMBERS[dungeonGuardRoller.random(extra.getRand())];
		}
		if (extra.chanceIn(1,10)) {
			idNum = 1;//chest	
		}*/
		int ret = holder.newNode(NodeType.NodeTypeNum.CAVE.ordinal(),idNum,tier);
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
		NodeConnector start_node = new NodeConnector();
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
			int lastNode2;
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
						lastNode2 = getNode(start_node,0,0,stair_level);
						start_node.setFloor(lastNode2, floor);
						start_node.setMutualConnect(lastNode, lastNode2);
						//lastNode.reverseConnections();
						lastNode = lastNode2;
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
					//sadly list conversion is probably easier than writing mergesort myself
					//although with these sizes just use selection/insertion sort
					List<Integer> connects = start_node.getConnects(f);
					connects.sort(new Comparator<Integer>() {

						@Override
						public int compare(Integer a0, Integer a1) {
							return (int) Math.signum(
									start_node.getFloor(a0)
									-
									start_node.getFloor(a1)
									);
						}});
					int[] pass = new int[connects.size()];
					Integer[] agh = connects.toArray(new Integer[0]);//at this point I regret not writing my own sort right away
					for (int i = 0; i < connects.size();i++) {
						pass[i] = agh[i];
					}
					start_node.setConnects(f,pass);
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
			int guardLevel = holder.getLevel(madeNode);
			int guardAmount = 2;
			while (guardAmount < 4) {
				if (guardLevel > 3 && extra.chanceIn(3,4)) {
					guardAmount+=1;
					guardLevel--;
				}else {
					break;
				}
			}
			for (int i = 0; i < guardAmount;i++) {
				list.add(RaceFactory.makeDGuard(guardLevel));
			}
			holder.setForceGo(madeNode, true);
			holder.setStorage(madeNode, new Object[] {
					extra.choose("Large ","Well Lit ","High Security ") +extra.choose("Checkpoint","Barricade","Guardpost")
					,RaceFactory.makeDGuard(holder.getLevel(madeNode))});
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
			Combat bgc = mainGame.HugeBattle(holder.getWorld(),Player.wrapForMassFight(guards));
			if (bgc.playerWon() > 0) {
				holder.setForceGo(node,false);
				String wasname = holder.getStorageFirstClass(node,String.class);
				GenericNode.setTotalDeadString(holder, node,"Wrecked " +wasname,"Examine Bodies","They are slowly rotting.", "pile of corpses");
				return false;
			}else {
				holder.setStorage(node,bgc.survivors);//they don't revive
				return true;
			}
		case 5: return chest(holder, node);
		case 6: return mimic(holder, node);
		case 8: return statue(holder, node);
		case 7: return statueLoot(holder, node);
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
			AIClass.loot(p.getBag(),Player.bag,true,Player.player.getPerson());
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
		}
		return null;
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1://ladder etc
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
		}
		return null;
	}
	

}
