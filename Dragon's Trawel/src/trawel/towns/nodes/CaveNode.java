package trawel.towns.nodes;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.World;

public class CaveNode implements NodeType{
	
	private static final int EVENT_NUMBER =3;
	
	private static final CaveNode handler = new CaveNode();
	
	private NodeConnector node;
	
	public static CaveNode getSingleton() {
		return handler;
	}
	
	@Override
	public NodeConnector getNode(NodeFeature owner, int tier) {
		byte idNum = (byte) extra.randRange(1,EVENT_NUMBER);
		NodeConnector make = new NodeConnector();
		make.eventNum = idNum;
		make.typeNum = 1;
		make.level = tier;
		return make;
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
		return generate(owner,size,tier).finalize(owner);
	}
	
	@Override
	public NodeConnector generate(NodeFeature owner, int size, int tier) {
		size--;
		NodeConnector made = getNode(owner,tier);
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
			if (extra.chanceIn(1,5)) {//much higher chance to level up
				tempLevel++;
			}
			NodeConnector n = generate(owner,sizePer,tempLevel);
			made.connects.add(n);
			n.getConnects().add(made);
			n.finalize(owner);
		}
		return made;
	}

	@Override
	public void apply(NodeConnector made) {
		switch (made.eventNum) {
		case -2:
			made.name = "cave entrance";
			made.interactString = "traverse "+made.name;
			made.forceGo = true;
			break;
		case 1:
			made.name = "bear";
			made.interactString = "ERROR";
			made.forceGo = true;
			made.storage1 = RaceFactory.makeBear(made.level);
			break;
		case 2:
			made.storage1 = extra.choose("silver","gold","platinum","iron","copper");
			made.name = made.storage1+" vein";
			made.interactString = "mine "+made.storage1;
			break;
		case 3:
			made.name = "bat";
			made.interactString = "ERROR";
			made.forceGo = true;
			made.storage1 = RaceFactory.makeBat(made.level);
			break;
		}
	}
	
	@Override
	public boolean interact(NodeConnector node) {
		this.node = node;
		switch(node.eventNum) {
		case -2: Networking.unlockAchievement("cave1");break;
		case 1: bear1(); if (node.state == 0) {return true;};break;
		case 2: goldVein1();break;
		case 3: bat1(); if (node.state == 0) {return true;};break;
		}
		return false;
	}
	
	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.MEAT,DrawBane.BAT_WING,DrawBane.HONEY};
	}

	@Override
	public void passTime(NodeConnector node, double time, TimeContext calling){
		//none for now
	}
	
	
	private void bear1() {
		if (node.state == 0) {
			extra.println(extra.PRE_RED+"The bear attacks you!");
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
			extra.println("The bear's corpse lays here.");
			node.findBehind("dead bear");	
		}
		
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
			Player.addGold(gold);
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
					node.forceGo = false;
				}
		}else {
			extra.println("The bat corpse lies here.");
			//too small for now
		}
		
	}
	

}
