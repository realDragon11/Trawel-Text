package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import trawel.AIClass;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;

public class DungeonNode implements NodeType{

	private static final int EVENT_NUMBER =7;
	
	private static final DungeonNode handler = new DungeonNode();
	
	private NodeConnector node;
	
	public static DungeonNode getSingleton() {
		return handler;
	}
	
	@Override
	public NodeConnector getNode(NodeFeature owner, int tier) {
		byte idNum = (byte) extra.randRange(1,EVENT_NUMBER);
		NodeConnector make = new NodeConnector();
		if (extra.chanceIn(1,2)) {
			idNum = 2;//dungeon guard
			if (extra.chanceIn(1,3)) {
			idNum = 5;	
			}
		}
		if (extra.chanceIn(1,10)) {
			idNum = 1;//chest	
		}
		make.eventNum = idNum;
		make.typeNum = 0;
		make.level = tier;
		return make;
	}

	@Override
	public NodeConnector generate(NodeFeature owner, int size, int tier) {
		NodeConnector made = getNode(owner,tier);
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
			NodeConnector n = generate(owner,sizeRemove+baseSize,tempLevel);
			made.connects.add(n);
			n.getConnects().add(made);
			n.finalize(owner);
			i++;
		}
		return made;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		switch (owner.shape) {
		case NONE: 
			return generate(owner,size, tier).finalize(owner);
		case TOWER:
			int curSize = 1;
			List<List<NodeConnector>> floors = new ArrayList<List<NodeConnector>>();
			List<NodeConnector> curFloor;
			NodeConnector stair;
			NodeConnector curStair;
			NodeConnector start = getNode(owner,tier).finalize(owner);
			start.setFloor(0);
			stair = start;
			int levelUp = 0;
			int floor = 0;
			NodeConnector lastNode;
			NodeConnector lastNode2;
			//DOLATER: fix order of nodes if that is still an issue
			while (curSize < size) {
				floor++;
				lastNode = stair;

				levelUp++;
				curStair = getNode(owner,stair.getLevel()+levelUp%3);
				curStair.setStair();
				curStair.finalize(owner);
				
				curFloor = new ArrayList<NodeConnector>();
				for (int j = 0; j < 2;j++) {
					for (int i = 0;i <2; i++) {
						floor++;
						lastNode2 = getNode(owner,curStair.getLevel());
						lastNode2.finalize(owner);
						lastNode2.setFloor(floor);
						lastNode.getConnects().add(lastNode2);
						lastNode2.getConnects().add(lastNode);
						lastNode.reverseConnections();
						lastNode = lastNode2;
						curFloor.add(lastNode);
						if (i == 1) {
							lastNode.getConnects().add(curStair);
						}
					}
					floor-=2;
					curStair.getConnects().add(lastNode);
					
					lastNode = stair;
				}
				floor +=10;
				curFloor.add(curStair);
				floors.add(curFloor);
				curSize +=curFloor.size();
				//reverse order of stair connects
				stair.reverseConnections();
				curStair.setFloor(floor);
				//move onto next floor
				stair = curStair;
			}
			NodeConnector b = BossNode.getSingleton().getNode(owner,(levelUp%3)+1);
			b.getConnects().add(stair);
			b.setFloor(floor+10);
			b.finalize(owner);
			stair.reverseConnections();
			stair.getConnects().add(b);
			stair.reverseConnections();
			//
			for (List<NodeConnector> fl: floors) {
				for (NodeConnector f: fl) {
					f.getConnects().sort(new Comparator<NodeConnector>() {

						@Override
						public int compare(NodeConnector a0, NodeConnector a1) {
							return (int) Math.signum(a0.getFloor()-a1.getFloor());
						}});
					f.reverseConnections();
				}
				
			}
			return start;
			
		}
		throw new RuntimeException("Invalid dungeon");
	}
	
	@Override
	public void apply(NodeConnector made) {
		if (made.isStair()) {
			made.eventNum = -1;
		}
		switch (made.eventNum) {
		case -1:
			made.name = extra.choose("stairs","ladder");
			made.interactString = "traverse "+made.name;
			made.forceGo = true;
			break;
		case 1:
			made.storage1 = extra.choose("old ","vibrant ","simple ","") + "chest";
			made.name = (String) made.storage1;
			made.interactString = "open "+made.name;
			made.storage2 = RaceFactory.makeMimic(1);
			break;
		case 2: made.name = extra.choose("dungeon guard","gatekeeper","dungeon guard");
		made.interactString = "ERROR";
		made.forceGo = true;
		made.storage1 = RaceFactory.getDGuard(made.level);
		break;
		case 3:
			made.name = extra.choose("locked door","barricaded door","padlocked door");
			made.interactString = "examine broken door";
			made.forceGo = true;break;
		case 4:
			ArrayList<Person> list = new ArrayList<Person>();
			list.add(RaceFactory.getDGuard(extra.zeroOut(made.level-3)+1));
			list.add(RaceFactory.getDGuard(extra.zeroOut(made.level-3)+1));
			made.name = "gate guards";
			made.interactString = "ERROR";
			made.storage1 = list;
			made.forceGo = true;
			made.state = 0;
		break;
		case 5:
			made.storage1 = extra.choose("old ","vibrant ","simple ","") + "chest";
			made.name = (String) made.storage1;
			made.interactString = "open "+made.name;
			made.storage2 = RaceFactory.makeMimic(made.level);
		break;
		case 6:
			if (extra.chanceIn(1, 3)) {
				made.forceGo = true;
			}
		case 7:
			made.storage1 =  RaceFactory.makeStatue(made.level);
			made.name = ((Person)made.storage1).getBag().getRace().renderName(false) + " statue";
			made.interactString = "loot statue";
			break;
		}
	}
	
	@Override
	public boolean interact(NodeConnector node) {
		this.node = node;
		switch(node.eventNum) {
		case -1: Networking.sendStrong("PlayDelay|sound_footsteps|1|"); break;
		case 1: chest();break;
		case 2: mugger1(); if (node.state == 0) {return true;};break;
		case 3: if (node.forceGo == true) {
			extra.println("You bash open the door.");
			node.name = "broken door";node.forceGo = false;
		}else {
			extra.println("The door is broken.");
		};break;
		//case 4: extra.println("You traverse the ladder.");break;
		case 4: return gateGuards();
		case 5: mimic(); if (node.state == 0) {return true;};break;
		case 6: statue(); if (node.state == 0) {return true;};break;
		case 7: statueLoot();break;
		}
		return false;
	}


	private void chest() {
		if (node.state == 0) {
			Person p = (Person)node.storage2;
			p.getBag().graphicalDisplay(1,p);
			extra.println("Really open the " + node.name + "?");
			if (extra.yesNo()) {
				if (extra.chanceIn(1, 10)) {
					Player.player.emeralds++;
					extra.println("You open the " +node.storage1 + " and find an emerald!");
				}else {
					if (extra.chanceIn(1, 10)) {
						Player.player.rubies++;
						extra.println("You open the " +node.storage1 + " and find a ruby!");
					}else {
						if (extra.chanceIn(1, 10)) {
							Player.player.sapphires++;
							extra.println("You open the " +node.storage1 + " and find a sapphire!");
						}else {
							int gold = extra.randRange(100,200)*node.level;
							Player.bag.addGold(gold);
							extra.println("You open the " +node.storage1 + " and find " + gold + " gold.");
						}
					}
				}
				node.state = 1;
				node.name = "empty " + node.storage1;
				node.interactString = "examine empty " + node.storage1;
			}else {
				extra.println("You decide not to open it.");
			}
		}else {
			extra.println("The "+node.storage1+" has already been opened.");
			node.findBehind("chest");
		}
		Networking.clearSide(1);

	}

	private void mugger1() {
		if (node.state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("They attack you!");
			Person p = (Person)node.storage1;
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					node.state = 1;
					node.storage1 = null;
					node.name = "dead "+node.name;
					node.interactString = "examine body";
					node.forceGo = false;
				}
		}else {
			randomLists.deadPerson();
			node.findBehind("body");
		}
		
	}
	
	private boolean gateGuards() {
		if (node.state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("They attack you!");
			List<Person> list = (List<Person>)node.storage1;
			List<Person> survivors = mainGame.HugeBattle(list,Player.list());
			
			
			if (survivors.contains(Player.player.getPerson())) {
				node.forceGo = false;
				node.interactString = "approach bodies";
				node.storage1 = null;
				node.state = 1;
				node.name = "dead "+node.name;
				return false;
			}else {
				node.storage1 = survivors;
				return true;
			}
		}else {
			randomLists.deadPerson();
			randomLists.deadPerson();
			node.findBehind("bodies");
			return false;
		}
		
		
	}
	
	private void mimic() {
		if (node.state == 0) {
			Person p = (Person)node.storage2;
			p.getBag().graphicalDisplay(1,p);
			extra.println("Really open the " + node.name + "?");
			if (extra.yesNo()) {
				extra.print(extra.PRE_RED);
				extra.println("The mimic attacks you!");
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					node.state = 1;
					node.storage1 = null;
					node.name = "dead mimic";
					node.interactString = "examine body";
					node.forceGo = false;
				}
			}else {
				extra.println("You decide not to open it.");
			}
		}else {
			randomLists.deadPerson();
			node.findBehind("mimic");
		}

	}


	private void statue() {
		if (node.state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("The statue springs to life and attacks you!");
			Person p = (Person)node.storage1;
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
			if (winner != p) {
				node.state = 1;
				node.storage1 = null;
				node.name = "destroyed "+node.name;
				node.interactString = "examine destroyed statue";
				node.forceGo = false;
			}
		}else {
			randomLists.deadPerson();
			node.findBehind("destroyed statue");
		}

	}

	private void statueLoot() {
		if (node.state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("You loot the statue...");
			Person p = (Person)node.storage1;
			p.getBag().graphicalDisplay(1,p);
			AIClass.loot(p.getBag(),Player.bag,Player.player.getPerson().getIntellect(),true,Player.player.getPerson());
			node.state = 1;
			node.storage1 = null;
			node.name = "looted statue";
			node.interactString = "loot statue";
			node.forceGo = false;

		}else {
			extra.println("You already looted this statue!");
			node.findBehind("statue");
		}
		Networking.clearSide(1);
	}
	
	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.VIRGIN,DrawBane.MIMIC_GUTS,DrawBane.CEON_STONE,DrawBane.TELESCOPE,DrawBane.KNOW_FRAG};
	}

	@Override
	public void passTime(NodeConnector node, double time, TimeContext calling) {
		// empty
		
	}
	

}
