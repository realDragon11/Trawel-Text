package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.List;

import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.Quest.TriggerType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.services.Oracle;

public class MineNode implements NodeType{
	
	private static final int EVENT_NUMBER = 9;
	
	private static final MineNode handler = new MineNode();
	
	private NodeConnector node;
	
	public static MineNode getSingleton() {
		return handler;
	}
	
	@Override
	public NodeConnector getNode(NodeFeature owner, int tier) {
		int idNum = extra.randRange(1,EVENT_NUMBER);
		NodeConnector make = new NodeConnector();
		if (extra.chanceIn(1, 5)) {
			idNum = 3;//gold7
		}
		if (extra.chanceIn(1, 30)) {
			idNum = extra.choose(-1,-2,-3);//emeralds
		}
		
		make.eventNum =(byte) idNum;
		make.typeNum = 0;
		make.level = tier;
		return make;
	}
	

	@Override
	public NodeConnector generate(NodeFeature owner, int size, int tier) {
		if (size < 3) {
			return genShaft(owner,size,tier,10);
			//shafts finalize themselves
			//they will also auto terminate if they run out
		}
		if (extra.chanceIn(1,3)) {//mineshaft splitting
			return genShaft(owner,size,tier,extra.randRange(2,5));
		}
		NodeConnector made = getNode(owner,tier).finalize(owner);
		size--;
		int sizeLeft;
		int[] split;
		if (size < 5) {
			split = new int[size];
			sizeLeft = 0;
			size = 0;
		}else {
			split = new int[extra.randRange(3,Math.min(size,5))];
			size-=split.length;
			sizeLeft = (size)/(split.length+2);
		}
		for (int i = 0; i < split.length;i++) {
			split[i] = sizeLeft;
		}
		sizeLeft = size-(sizeLeft*split.length);
		while (sizeLeft > 0) {
			split[extra.randRange(0,split.length-1)]+=1;
			sizeLeft--;
		}
		for (int j = 0; j < split.length;j++) {
			int tempLevel = tier;
			if (extra.chanceIn(1,20)) {//less likely to tier up without mineshafts
				tempLevel++;
			}
			NodeConnector n = generate(owner,split[j],tempLevel);
			made.connects.add(n);
			n.getConnects().add(made);
			n.finalize(owner);
		}
		return made;
	}
	
	protected NodeConnector genShaft(NodeFeature owner, int size, int tier,int shaftLeft) {
		if (size <= 1) {//ran out of resources
			return getNode(owner,tier).finalize(owner);
		}
		if (shaftLeft > 1 || size < 3) {
			//shaft always is same level
			NodeConnector n = genShaft(owner, size-1, tier,shaftLeft-1);
			NodeConnector me = getNode(owner,tier);
			me.connects.add(n);
			n.getConnects().add(me);
			return me.finalize(owner);//shafts finalize themselves
		}else {
			//always a tier up when ending shaft
			return generate(owner,size-1,tier+1);
		}
	}

	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		switch (owner.shape) {
		case NONE: 
			return generate(owner,size, tier).finalize(owner);
		case ELEVATOR:
			NodeConnector start = getNode(owner,tier).finalize(owner);
			NodeConnector lastNode = start;
			NodeConnector newNode;
			for (int i = 0;i < size;i++) {
				newNode = getNode(owner, tier+(i/10)).finalize(owner);
				newNode.setFloor(i+1);
				lastNode.getConnects().add(newNode);
				newNode.getConnects().add(lastNode);
				lastNode.reverseConnections();
				lastNode = newNode;
			}
			NodeConnector b = BossNode.getSingleton().getNode(owner,1+tier+(int)Math.ceil(size/10));
			lastNode.getConnects().add(b);
			b.getConnects().add(lastNode);
			b.setFloor(size+10);
			lastNode.reverseConnections();
			return start;
		}
		throw new RuntimeException("Invalid mine");
	}
	
	@Override
	public void apply(NodeConnector made) {
		switch (made.eventNum) {
		case -3: made.name = "sapphire cluster"; made.interactString = "mine sapphires";break;
		case -2: made.name = "ruby cluster"; made.interactString = "mine rubies";break;
		case -1: made.name = "emerald cluster"; made.interactString = "mine emeralds";break;
		case 0: made.name = ""; made.interactString = "";break;
		case 1: 
			made.name = randomLists.randomWarrior();
			made.interactString = "challenge " + made.name;
			made.storage1 = RaceFactory.getDueler(made.level);
		break;
		case 2: 
			made.name = extra.choose("river","pond","lake","stream");
			made.interactString = "wash yourself";break;
		case 3: 
			made.storage1 = extra.choose("silver","gold","platinum","iron","copper");
			made.name = made.storage1+" vein";
			made.interactString = "mine "+made.storage1;break;
		case 4:
			made.name = randomLists.randomMuggerName();
			made.interactString = "ERROR";
			made.forceGo = true;
			made.storage1 = RaceFactory.getMugger(made.level);break;
		case 5: 
			made.name = extra.choose("locked door","barricaded door","padlocked door");
			made.interactString = "unlock door";
			made.forceGo = true;break;
		case 6:
			made.interactString = "examine crystals";
			made.storage1 = randomLists.randomColor();
			made.name = "weird " + (String)made.storage1 + " crystals";break;
		case 7:
			made.name = "minecart";
			made.interactString = "examine minecart";break;
		case 8: made.name = "ladder"; made.interactString = "traverse ladder"; made.forceGo = true; break;
		case 9: made.name = "cultists"; made.interactString = "approach cultists";
			made.storage1 = RaceFactory.getCultist(made.level);
		break;
		}
	}

	@Override
	public boolean interact(NodeConnector node) {
		this.node = node;
		switch(node.eventNum) {
		case -3: saph1();break;
		case -2: rubies1();break;
		case -1: emeralds1();break;
		case 1: duelist();break;
		case 2: extra.println("You wash yourself in the "+node.name+".");Player.player.getPerson().washAll();break;
		case 3: goldVein1();break;
		case 4: mugger1(); if (node.state == 0) {return true;};break;
		case 5: 
			if (node.forceGo == true) {
				if (node.parent.getOwner() == Player.player) {
					extra.println("You unlock and then relock the door.");
					node.forceGo = false;
				}else {
					extra.println("You bash open the door.");
					node.interactString = "examine broken door";
					node.name = "broken door";node.forceGo = false;
				}
			}else {
				extra.println("The door is broken.");
				node.findBehind("broken door");
			};break;
		case 6: 
			extra.println("You examine the " + ((String)node.storage1)+ " crystals. They are very pretty.");
			node.findBehind(((String)node.storage1)+ " crystals");break;
		case 7: 
			extra.println("You examine the iron minecart. It is on the tracks that travel throughout the mine.");
			node.findBehind("minecart");break;
		case 8: 
			extra.println("You traverse the ladder.");
			Networking.sendStrong("PlayDelay|sound_footsteps|1|");
			node.findBehind("ladder");
			break;
		case 9: cultists1();break;
		}
		Networking.clearSide(1);
		return false;
	}
	
	private void duelist() {
		if (node.state == 0) {
			Person p = (Person)node.storage1;
			p.getBag().graphicalDisplay(1, p);
			extra.println(extra.PRE_RED+"Challenge "+ p.getName() + "?");
			if (extra.yesNo()){
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					node.state = 1;
					node.storage1 = null;
					node.name = "dead "+node.name;
					node.interactString = "examine body";
				}
			}
		}else {
			randomLists.deadPerson();
			node.findBehind("body");
		}

	}

	private void emeralds1() {
		if (node.state == 0) {
			Networking.sendStrong("Achievement|ore1|");
			Player.player.emeralds++;
			extra.println("You mine the vein and claim an emerald!");
			node.state = 1;
			node.name = "empty vein";
			node.interactString = "examine empty vein";
			((Mine)node.parent).removeVein();
			//to indicate you can find stuff
			node.findBehind("empty vein");
		}else {
			extra.println("The emeralds have already been mined.");
			node.findBehind("vein");
		}
	}

	private void rubies1() {
		if (node.state == 0) {
			Networking.sendStrong("Achievement|ore1|");
			Player.player.rubies++;
			extra.println("You mine the vein and claim a ruby!");
			node.state = 1;
			node.name = "empty vein";
			node.interactString = "examine empty vein";
			((Mine)node.parent).removeVein();
			//to indicate you can find stuff
			node.findBehind("empty vein");
		}else {
			extra.println("The rubies have already been mined.");
			node.findBehind("vein");
		}
	}

	private void saph1() {
		if (node.state == 0) {
			Networking.sendStrong("Achievement|ore1|");
			Player.player.sapphires++;
			extra.println("You mine the vein and claim a sapphire!");
			node.state = 1;
			node.name = "empty vein";
			node.interactString = "examine empty vein";
			((Mine)node.parent).removeVein();
			//to indicate you can find stuff
			node.findBehind("empty vein");
		}else {
			extra.println("The sapphires have already been mined.");
			node.findBehind("vein");
		}
	}

	private void goldVein1() {
		if (node.state == 0) {
			Networking.sendStrong("Achievement|ore1|");
			int mult1 = 0, mult2 = 0;
			switch (node.storage1.toString()) {
			case "gold": mult1 = 100; mult2 = 200;break;
			case "silver": mult1 = 75; mult2 = 150;break;
			case "platinum": mult1 = 150; mult2 = 300;break;
			case "iron": mult1 = 50; mult2 = 100;break;
			case "copper": mult1 = 25; mult2 = 50;break;
			}
			int gold = extra.randRange(mult1,mult2)*node.level;
			Player.bag.addGold(gold);
			extra.println("You mine the vein for "+node.storage1+" worth "+ gold + " gold.");
			node.state = 1;
			node.name = "empty vein";
			node.interactString = "examine empty vein";
			((Mine)node.parent).removeVein();
			//to indicate you can find stuff
			node.findBehind("empty vein");
		}else {
			extra.println("The "+node.storage1+" has already been mined.");
			node.findBehind("empty vein");
		}
	}
	
	private void mugger1() {
		if (node.state == 0) {
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
	

	/* for Ryou Misaki (nico)*/
	private void cultists1() {
		Person p = (Person)node.storage1;
		p.getBag().graphicalDisplay(1, p);
		int inInt = 2;
		do {
			boolean hasSkills = Player.player.hasCult;
			if (hasSkills) {
				extra.println("The cultists welcome you and ask you how you are doing.");
				extra.println("1 chat");
				hasSkills = true;
			}else {
				extra.println("The cultists offer to enhance you... if you sacrifice some blood.");
				extra.println("1 accept offer");
			}
			extra.println("2 "+extra.PRE_RED+"attack");	
			extra.println("3 leave");
			inInt = extra.inInt(3);
			switch (inInt) {
			case 1: 
				if (hasSkills) {
					Oracle.tip("cult");
				}else {
					extra.println("You die!");
					mainGame.die("You rise from the altar!");
					extra.println("The cultists praise you as the second coming of flagjaij!");
					Player.player.getPerson().addEffect(Effect.CURSE);
					Player.player.getPerson().setSkillPoints(Player.player.getPerson().getSkillPoints()+1);
					Player.player.hasCult = true;
					hasSkills = true;
					Networking.sendStrong("Achievement|cult1|");
				};break;

			case 2:
				node.name = "angry cultist leader";
				node.interactString = "ERROR";

				node.forceGo = true;
				node.eventNum = 4;
				break;

			case 3: extra.println("You leave them alone.");break;

			}
		}while(inInt == 1);

	}

	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.LIVING_FLAME,DrawBane.SILVER,DrawBane.GOLD};
	}

	@Override
	public void passTime(NodeConnector node, double time, TimeContext calling) {
		// empty
		
	}


}
