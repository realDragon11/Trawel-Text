package trawel.features.nodes;
import java.util.ArrayList;
import java.util.List;

import trawel.AIClass;
import trawel.DrawBane;
import trawel.Effect;
import trawel.Item;
import trawel.Networking;
import trawel.Oracle;
import trawel.Person;
import trawel.PlantSpot;
import trawel.Player;
import trawel.Race;
import trawel.RaceFactory;
import trawel.Seed;
import trawel.Services;
import trawel.Weapon;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.factions.Faction;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class GroveNode extends NodeConnector{
	//potentail problem: all this code is in a highly duplicated node

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private String name;
	private static final int EVENT_NUMBER = 21;
	protected int state;
	//private String interactString;
	protected int idNum;
	//private int level;
	protected Object storage1, storage2;
	//private ArrayList<GroveNode> connects;
	//private boolean forceGo;
	public Grove parent;
	
	/**
	 * used for CaveNode
	 */
	protected GroveNode() {
		
	}
	
	public GroveNode(int size, int tier,Grove p ) {
		state = 0;
		parent = p;
		idNum = extra.randRange(1,EVENT_NUMBER);
		if (idNum == 18 && tier <= 3) {//wolves
			idNum = extra.randRange(1,EVENT_NUMBER);
		}
		//DEBUG
		//idNum = (int)extra.choose(14,15);
		
		
		if (extra.chanceIn(1,10)) {
			idNum = 11;//mushroom
			if (extra.chanceIn(1,3)) {
				idNum = 12; //moss
			}
			
		}
		if (idNum == 13) {
			idNum = 12; //moss
		}
		level = tier;
		if (extra.chanceIn(1,10)) {
			level++;
		}
		setConnects(new ArrayList<NodeConnector>());
		forceGo = false;
		generate(size);
		parentName = p.getName();
		
	}
	
	
	private void generate(int size) {
		switch (idNum) {
		case 0: name = ""; interactString = "";break;
		case 1: name = randomLists.randomWarrior(); interactString = "challenge " + name;
		storage1 = RaceFactory.getDueler(level);
		break;
		case 2: name = extra.choose("river","pond","lake","stream","brook"); interactString = "wash yourself";break;
		case 3: name = randomLists.randomMuggerName(); interactString = "ERROR"; forceGo = true;
		storage1 = RaceFactory.getMugger(level);break;
		case 4: name = extra.choose("rotting","decaying") + " " +RaceFactory.randRace(Race.RaceType.HUMANOID).name +" " + extra.choose("corpse","body"); interactString = "loot corpse";break;
		case 5: name = "fairy circle"; interactString = "examine circle";break;
		case 6: name = "fairy circle"; interactString = "examine circle";break;
		case 7: name = "old " + randomLists.randomWarrior(); interactString = "approach " + name;
		storage1 = RaceFactory.makeOld(level + 2);
		if (extra.chanceIn(1, 4)) {
		((Person)storage1).getBag().getDrawBanes().add(DrawBane.REPEL);}
		break;
		case 8: name = "fallen tree"; interactString = "examine fallen tree";break;
		case 9: name = "dryad"; interactString = "approach the " + name;
		storage1 = RaceFactory.getDryad(level);
		storage2 = 0;
		break;
		case 10: name = "fallen tree";interactString = "examine fallen tree";break;
		case 11: name = randomLists.randomColor() + " mushroom";interactString = "approach mushroom";break;
		case 12: name = "moss"; interactString = "approach moss"; state = extra.randRange(0,1);break;
		case 13: name = "grey hole";interactString = "approach hole";break;
		case 14:storage2 = RaceFactory.getRacist(level); ((Person)storage2).setRacism(true); storage1 = ((Person)storage2).getBag().getRace(); name = ((Race)storage1).name; interactString = "approach " + name; ;break;
		case 15: storage1 = RaceFactory.getRich(level); storage2 = RaceFactory.getRich(level+1);
		name = ((Person)storage1).getBag().getRace().name; interactString = "approach " + name; 
		((Person)storage1).getBag().addGold(level*300);break;
		case 16: storage1 = new Weapon(level); name = ((Weapon)storage1).getBaseName() + " in a rock"; interactString = "pull on " +((Weapon)storage1).getBaseName(); break;
		case 17: storage1 = RaceFactory.getPeace(level); ((Person)storage1).setRacism(false);
		name = ((Person)storage1).getBag().getRace().name; interactString = "approach " + name;break;
		case 18: ArrayList<Person> list = new ArrayList<Person>();
		for (int i = 0;i < extra.randRange(2,3);i++) {
		list.add(RaceFactory.makeWolf(extra.zeroOut(level-3)+1));}
		name = "pack of wolves";
		interactString = "ERROR";
		storage1 = list;
		forceGo = true;
		state = 0;
		;break;
		case 19:
		name = extra.choose("shaman"); interactString = "approach the shaman"; forceGo = false;
		storage1 = RaceFactory.getShaman(level);
		storage2 = storage1;break;
		case 20:
			name = extra.choose("collector");
			interactString = "approach the " + name;
			forceGo = false;
			storage1 = RaceFactory.makeCollector(level);
			break;
		case 21:
			name = "bee hive";
			interactString = "destroy hive";
			forceGo = false;
			break;
		}
		//TODO: add lumberjacks and tending to tree
		if (size < 2) {
			return;
		}
		int split = extra.randRange(1,Math.min(size,3));
		int i = 1;
		int sizeLeft = size;
		while (i < split) {
			int sizeRemove = extra.randRange(0,sizeLeft-1);
			sizeLeft-=sizeRemove;
			GroveNode n = new GroveNode(sizeRemove,level,parent);
			connects.add(n);
			n.getConnects().add(this);
			i++;
		}
		if (extra.chanceIn(1, 20) && sizeLeft < 4) {
			CaveNode n = new CaveNode(sizeLeft,level,parent,true);
			connects.add(n);
			n.getConnects().add(this);
		}else {
		GroveNode n = new GroveNode(sizeLeft,level,parent);
		connects.add(n);
		n.getConnects().add(this);}
	}
	
	protected boolean interact() {
		switch(idNum) {
		case -1: plantSpot();break;
		case 1: duelist();break;
		case 2: extra.println("You wash yourself in the "+name+".");
		Player.player.getPerson().washAll();
		;break;
		case 3: mugger1(); if (state == 0) {return true;};break;
		case 4: findEquip();break;
		case 5: fairyCircle1();break;
		case 6: return fairyCircle2();
		case 7: oldFighter();break;
		case 8: treeOnPerson();break;
		case 9: dryad();break;
		case 10: fallenTree();break;
		case 11: funkyMushroom();break;
		case 12: funkyMoss();break;
		case 13: return greyHole();
		case 14: racist1();break;
		case 15: rich1();break;
		case 16: weapStone();break;
		case 17: equal1();break;
		case 18: return packOfWolves();
		case 19: shaman();break;
		case 20: collector();break;
		case 21: beeHive();break;
		}
		Networking.clearSide(1);
		return false;
	}
	



	public void plantSpot() {
		((PlantSpot)storage1).go();
	}


/*
	public void go() {
		Player.addTime(.1);
		int i = 1;
		if (forceGo) {
			if (interact()) {
				return;
			}

		}
		extra.println(name);
		extra.println(i+ " " + interactString);i++;
		for (GroveNode n: connects) {
			extra.println(i + " " + n.getName());
			if (Player.hasSkill(Skill.TIERSENSE)) {
				extra.println("Tier: " + n.level);
			}
			if (Player.hasSkill(Skill.TOWNSENSE)) {
				extra.println("Connections: " + n.connects.size());
			}
			i++;
			}
		extra.println(i + " exit grove");i++;
		int j = 1;
		int in = extra.inInt(i-1);
		if (in == j) {
			if (interact()) {
				return;
			}
		}j++;
		for (GroveNode n: connects) {
			if (in == j) {
				n.go();
				return;
			}
			j++;
			}
		if (in == j) {
			return;
		}
		go();
	}

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<GroveNode> getConnects() {
		return connects;
	}


	private void setConnects(ArrayList<GroveNode> connects) {
		this.connects = connects;
	}*/
	
	private void duelist() {
		if (state == 0) {
			Person p = (Person)storage1;
			p.getBag().graphicalDisplay(1, p);
			extra.print(extra.PRE_RED);
			extra.println("Challenge "+ p.getName() + "?");
			if (extra.yesNo()){
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "dead "+name;
					interactString = "examine body";
				}
			}
		}else {randomLists.deadPerson();findBehind("body");}
		
	}

	
	private void mugger1() {
		if (state == 0) {
			Person p = (Person)storage1;
			extra.print(extra.PRE_RED);
			extra.println("You are attacked by a " + name);
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "dead "+name;
					interactString = "examine body";
					forceGo = false;
				}
		}else {randomLists.deadPerson();findBehind("body");}
		
	}
	
	
	private void findEquip() {
		if (state == 0) {
		extra.println("You find a rotting body... With their equipment intact!");
		AIClass.loot(new Person(level).getBag(),Player.bag,Player.player.getPerson().getIntellect(),true);
		state = 1;}else {
			extra.println("You've already looted this corpse.");
			findBehind("body");
		}
	}
	
	private void fairyCircle1() {
		extra.println("You find a fairy circle of mushrooms. Step in it?");
		if (extra.yesNo()) {
			extra.println("You step in it. Nothing happens.");
		}else {
			extra.println("You stay away from the circle.");
		}
	}
	
	private boolean fairyCircle2() {
		extra.println("You find a fairy circle of mushrooms. Step in it?");
		if (extra.yesNo()) {
			extra.println("Some fairies appear, and ask you what fate you desire.");
			extra.println("1 a power fantasy");
			extra.println("2 a challenge");
			extra.println("3 your normal fate");
			switch (extra.inInt(3)) {
			case 1:
				extra.println("You find yourself jerked nowhere. Your surroundings change...");
				Networking.sendStrong("PlayDelay|sound_teleport|1|");
				Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()-2));
				break;
			case 2:
				extra.println("You find yourself jerked nowhere. Your surroundings change...");
				Networking.sendStrong("PlayDelay|sound_teleport|1|");
				Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()));
				break;
			case 3:
				extra.println("The fairies frown and disappear.");
				return false;
			}
			return true;
		}else {
			extra.println("You stay away from the circle.");
			return false;
		}
	}
	
	private void oldFighter() {
		if (state == 0) {
		while (true) {
			Person p = (Person)storage1;
			p.getBag().graphicalDisplay(1, p);
		extra.println("You come across an " + name + ", resting on a log.");
		extra.println("1 Leave");
		extra.print(extra.PRE_RED);
		extra.println("2 Attack them");
		extra.println("3 Chat with them");
		switch (extra.inInt(3)) {
		default: case 1: extra.println("You leave the " + name + " alone");return;
		case 2: extra.println("You attack the "+name+"!");
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
		if (winner != p) {
			state = 1;
			storage1 = null;
			name = "dead "+name;
			interactString = "examine body";
		}
		;return;
		case 3: extra.println("The " + name + " turns and answers your greeting.");
		while (true) {
		extra.println("What would you like to ask about?");
		extra.println("1 tell them goodbye");
		extra.println("2 ask for a tip");
		extra.println("3 this grove");
		int in = extra.inInt(3);
		switch (in) {
			case 1: extra.println("They wish you well.") ;break;
			case 2: Oracle.tip("old");break;
			case 3: extra.println("\"We are in " + parent.getName() + ". Beware, danger lurks under these trees.\"");break;
		}
		if (in == 1) {
			break;
		}
		}
		}
	}}else {randomLists.deadPerson();}}
	
	private void treeOnPerson() {
		if (state == 1) {
			extra.println("You examine the fallen tree. It is very pretty.");
			return;
		}
		extra.println("There's a person stuck under the fallen tree! Help them?");
		if (extra.yesNo()) {
			extra.println("You move the tree off of them.");
			state = 1;
			if (Math.random() > .9) {
				extra.print(extra.PRE_RED);
				extra.println("Suddenly, they attack you!");
				mainGame.CombatTwo(Player.player.getPerson(), RaceFactory.getMugger(level));
			}else {
				if (Math.random() < .3) {
					extra.println("They scamper off...");
					findBehind("tree");
				}else {
					int gold = (int) (extra.hrandom()*50*level);
					extra.println("They give you a reward of " + gold + " gold in thanks for saving them.");
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,1,0);
					Player.bag.addGold(gold);
				}
			}
		}else {
			extra.println("You leave them alone to rot...");
		}
	}
	
	private void dryad() {
		if (state == 0 || state == -1) {
		while (true) {
			Person p = (Person)storage1;
			p.getBag().graphicalDisplay(1, p);
		extra.println("You come across a dryad tending to a tree.");
		extra.println("1 Leave");
		extra.print(extra.PRE_RED);
		extra.println("2 Attack them.");
		extra.println("3 Chat with them");
		switch (extra.inInt(3)) {
		default: case 1: extra.println("You leave the "+name+" alone");return;
		case 2: extra.println("You attack the dryad!");
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
		if (winner != p) {
			state = 1;
			storage1 = null;
			name = "dead "+name;
			interactString = "examine body";
		}
		;return;
		case 3: extra.println("The dryad turns and answers your greeting.");
		while (true) {
		extra.println("What would you like to ask about?");
		extra.println("1 tell them goodbye");
		extra.println("2 their tree");
		extra.println("3 this forest");
		int in = extra.inInt(3);
		switch (in) {
			case 1: extra.println("They wish you well.") ;break;
			case 2: extra.println("They start describing their tree in intricate detail before finishing.");
			if (storage2 instanceof Integer) {
			if ((int)storage2 > 3) {
				if (extra.chanceIn(1, 3)) {
				extra.println("They ask if you would like your own tree.");
				if (extra.yesNo()) {
					extra.println("They say to find a spot where a lumberjack has chopped down a tree and plant one there.");
					storage2 = null;
					Player.bag.addSeed(Seed.ENT);
				}
				}else {
					storage2 = null;
				}
			}else {
				storage2 = (int)storage2+1;
			}
			}
			extra.println("They seem very passionate about it.");break;
			case 3: extra.println("\"We are in " + parent.getName() + ". I don't venture away from my tree.\"");
			if (state == 0) {extra.println("\"Would you like a seed to help grow the forest?\"");
			if (extra.yesNo()) {
				state = -1;
				Player.bag.addSeed(Seed.randSeed());
			}
			};break;
		}
		if (in == 1) {
			break;
		}
		}
		}
		}}else {randomLists.deadPerson();findBehind("body");}}
	
	private void fallenTree() {
		extra.println("You examine the fallen tree. It is very pretty.");
		if (Player.player.animalQuest == 0) {
		extra.println("A "+Player.player.animalName()+" is sitting on it." );
		extra.println("It vanishes.");
		extra.println();
		extra.println("You feel a sense of loss.");
		Player.player.animalQuest = 1;
		}
		findBehind("tree");
	}
	
	private void funkyMushroom() {
		if (state == 0) {
		extra.println("You spot a glowing mushroom on the forest floor.");
		extra.println("1 leave it");
		extra.println("2 eat it");
		extra.println("3 sell it");
		extra.println("4 crush it");
		int in =  extra.inInt(4);
		switch (in) {
		default: case 1: extra.println("You decide to leave it alone.");findBehind("mushroom");break;
		case 2:
			name = "plant spot";
			interactString = "approach plant spot";
			idNum = -1;
			extra.println("You eat the mushroom...");
			storage1 = new PlantSpot(level);
			state = 1;
			switch(extra.randRange(1,3)) {
			case 1: extra.println("The mushroom is delicous!");break;
			case 2: extra.println("Eating the mushroom is very difficult... but you manage.");
			Player.player.getPerson().addXp(level*2);break;
			case 3: extra.println("You feel lightheaded.... you pass out!");
			extra.println("When you wake up, you find that some of your gold is missing!");
			Player.bag.addGold(-53*level);break;
			}
			if (Math.random() > .8) {
				extra.print(extra.PRE_RED);
			extra.println("As you eat the mushroom, you hear a voice cry out:");
			switch((int)extra.randRange(1,2)) {
			case 1: mushHelpDryad();break;
			case 2: mushHelpRobber();break;}
			//mainGame.CombatTwo(Player.player.getPerson(), new Person(level));
			}
			
			;break;
		case 3:
			state = 1;
			name = "plant spot";
			interactString = "approach plant spot";
			idNum = -1;
			storage1 = new PlantSpot(level);
			extra.println("You pick up the mushroom to sell it.");
			if (Math.random() > .8) {
				extra.print(extra.PRE_RED);
			extra.println("You hear someone cry out from behind you!");
			Person winner = null;
			switch((int)extra.randRange(1,2)) {
			case 1: winner = mushHelpDryad() ;break;
			case 2: winner = mushHelpRobber();break;
			}
			if (winner == Player.player.getPerson()) {
				int gold = (int) (Math.random()*150*level);
				extra.println("You sell the mushroom for " + gold + " gold.");
				Player.bag.addGold(gold);
			}
			}else {
				int gold = (int) (Math.random()*100*level);
				extra.println("You sell the mushroom for " + gold + " gold.");
				Player.bag.addGold(gold);
			};break;
		case 4:
			name = "plant spot";
			state = 1;
			extra.println("You crush the mushroom under your heel.");
			extra.print(extra.PRE_RED);
			extra.println("You hear someone cry out from behind you!");
			switch(extra.randRange(1,2)) {
			case 1: mushHelpDryad();break;
			case 2: mushHelpRobber();break;
			}
			;break;
		}
		}else {
			extra.println("There was a mushroom here.");
		}
	}
	
	private Person mushHelpRobber() {
		name = extra.choose("mugger","robber","thug","bandit","marauder","outlaw","desperado","cutthroat"); interactString = "ERROR";
		storage1 = RaceFactory.getMugger(level);
		switch (extra.randRange(0, 1)) {
		case 0: extra.println("\"Hey, I wanted that!\"");break;
		case 1: extra.println("\"You dirty plant-thief!\"");break;
		}
		idNum = 3;
		state = 0;
		Person p = (Person)storage1;
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
		if (winner != p) {
			state = 1;
			storage1 = null;
			name = "dead "+name;
			interactString = "examine body";
		}else {
			 forceGo = true;
		}
		return winner;
	}
	private Person mushHelpDryad() {
		extra.println("\"You dare violate the forest?!\"");
		storage1 = RaceFactory.getDryad(level);
		Person p = (Person)storage1;
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
		idNum = 9;
		name = "dryad";
		if (winner != p) {
			state = 1;
			storage1 = null;
			name = "dead "+name;
			interactString = "examine body";
		}else {
			state = 0;
			 interactString = "approach the " + name;
		}
		return winner;
	}

	
	private void mossHelpRobber() {
		name = extra.choose("mugger","robber","thug","bandit","marauder","outlaw","desperado","cutthroat"); interactString = "ERROR";
		storage1 = RaceFactory.getMugger(level);
		switch (extra.randRange(0, 1)) {
		case 0: extra.println("\"Hey, I wanted that!\"");break;
		case 1: extra.println("\"You dirty plant-thief!\"");break;
		}
		idNum = 3;
		state = 0;
			 forceGo = true;
		
	}
	/*for  Albin Grahn */
	private void funkyMoss() {
		if (state == 0) {
		extra.println("You see something shining under the moss!");
		extra.println("1 take it");
		extra.println("2 leave it alone");
		switch (extra.inInt(2)) {
		case 1: 
			DrawBane db = this.attemptCollectAll(.7f, 3);
			if (db != null) {
				extra.println("Wow, there were a ton of "+ db.getName() +" pieces under the moss!");
				state = 1;
			}else {
				int g = extra.randRange(20,30)*level;
				extra.println("You find " +g + " gold!");
				Player.bag.addGold(g);
				if (extra.randRange(1,3) == 1) {
					mossHelpRobber();
				}else {
					state = 1;
				}
			}
			//no break because moss remains
		case 2: extra.println("You leave the moss alone.");;break;
		}
		}else {
			if (state == 1) { 
			extra.println("There is some moss here.");
			extra.println("1 eat it");
			extra.println("2 sell it");
			extra.println("3 leave it alone");
			switch (extra.inInt(3)) {
			default: case 3: extra.println("You decide to leave it alone.");break;
			case 1:
				state = 2;
				name = "plant spot";
				interactString = "approach plant spot";
				idNum = -1;
				storage1 = new PlantSpot(level);
				extra.println("You eat the moss...");
				switch(extra.randRange(1,4)) {
				case 1: extra.println("The moss is delicous!");break;
				case 2: case 4: extra.println("Eating the moss is very difficult... but you manage.");
				Player.player.getPerson().addXp(level*1);break;
				case 3: extra.println("You feel lightheaded.... you pass out!");
				extra.println("When you wake up, you find that some of your gold is missing!");
				Player.bag.addGold(-20*level);break;
				}
				if (Math.random() > .8) {
					extra.print(extra.PRE_RED);
				extra.println("As you eat the moss, you hear a voice cry out:");
				switch((int)extra.randRange(1,2)) {
				case 1: mushHelpDryad();break;
				case 2: mushHelpRobber();break;}
				}
				
				;break;
			case 2:
				state = 2;
				name = "plant spot";
				interactString = "approach plant spot";
				idNum = -1;
				storage1 = new PlantSpot(level);
				extra.println("You pick up the moss to sell it.");
				if (Math.random() > .8) {
					extra.print(extra.PRE_RED);
				extra.println("You hear someone cry out from behind you!");
				Person winner = null;
				switch((int)extra.randRange(1,2)) {
				case 1: winner = mushHelpDryad() ;break;
				case 2: winner = mushHelpRobber();break;
				}
				if (winner == Player.player.getPerson()) {
					int gold = (int) (Math.random()*30*level);
					extra.println("You sell the moss for " + gold + " gold.");
					Player.bag.addGold(gold);
				}
				}else {
					int gold = (int) (Math.random()*20*level);
					extra.println("You sell the moss for " + gold + " gold.");
					Player.bag.addGold(gold);
				};break;
			}}else {
				extra.println("There was some moss here.");
			}
		}
	}
	/*for wuzzelf of the greyhole game*/ //soft taken out, sorry :(
	private boolean greyHole() {
		if (state == 0) {
		extra.println("You find a mysterious grey shimmering hole right there on the ground in front of you. It's not stationary as you would expect from a hole in the floor. It moves around! And it sucks in detritus and gravel in it's vicinity.");
		extra.println("1 jump in");
		extra.println("2 throw rocks in it");
		extra.println("3 stick your " +Player.bag.getHand().getName() + " in it");
		extra.println("4 stay away from it");
		switch (extra.inInt(4)) {
		case 1:
			extra.println("You jump in it. You find yourself jerked nowhere. Your surroundings change...");
			state = 1; name = "black hole"; interactString = "approach black hole";
			Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()));
			return true;
		case 2:
			extra.println("You throw rocks at it...");
			extra.println("It grows into a black hole and you are sucked in! You find yourself jerked nowhere. Your surroundings change...");
			state = 1; name = "black hole"; interactString = "approach black hole";
			Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()));
			return true;
		case 3: 
			extra.println("You stick your "+Player.bag.getHand().getName() +" in it...");
			extra.println("It grows into a black hole and you are sucked in! You find yourself jerked nowhere. Your surroundings change...");
			state = 1; name = "black hole"; interactString = "approach black hole";
			Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()));
			return true;
		case 4:
			extra.println("You stay away from the hole.");
			return false;
		}
		return false;
		}else {
			Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()));
			extra.println("You are sucked in by the black hole! You find yourself jerked nowhere. Your surroundings change...");
			return true;
			
		}
	}
	
	private void racist1() {
		boolean bool = true;
		while (bool) {
			extra.print(extra.PRE_RED);
			((Person)storage2).getBag().graphicalDisplay(1, (Person)storage2);
		extra.println("1 attack");
		extra.println("2 chat");
		extra.println("3 leave");
		switch (extra.inInt(3)) {
		case 1: name = "angry " +name ; interactString = "ERROR";
		storage1 = storage2;
		forceGo = true;
		idNum = 3;
		bool = false;break;
		case 2:
			if (((Race)storage1) == Player.bag.getRace()) {
				String str = Oracle.tipString("racistPraise");
				str = str.replaceAll("oracles",((Race)storage1).namePlural);
				str = str.replaceAll("oracle",((Race)storage1).name);
				extra.println("\"" +extra.capFirst(str)+"\"");
			}else {
				if (extra.chanceIn(4,5)) {
				String str = Oracle.tipString(extra.choose("racistShun","racistPraise"));
				str = str.replaceAll("not-oracle",Player.bag.getRace().randomSwear());
				str = str.replaceAll("oracles",((Race)storage1).namePlural);
				str = str.replaceAll("oracle",((Race)storage1).name);
				extra.println("\"" +extra.capFirst(str)+"\"");	
				}else {
					extra.println("\"" + Player.bag.getRace().randomInsult() +"\"");
				}
			};break;
			
		case 3:bool = false;break;
		}
		}
	}
	
	private void rich1() {
		Person rich = (Person)storage1;
		Person bodyguard = (Person)storage2;
		rich.getBag().graphicalDisplay(1,rich);
		if (state == 0) {
		boolean bool = true;
		while (bool) {
			extra.print(extra.PRE_RED);
		extra.println("1 attack");
		extra.println("2 chat");
		extra.println("3 leave");
		switch (extra.inInt(3)) {
		case 1: name = "angry " +name ; interactString = "approach " +name;
		state = 2;
		//forceGo = true;//can't do this without kicking them out
		this.richHelper(bodyguard, rich);
		bool = false;break;
		case 2:
			if (Player.bag.getGold() > rich.getBag().getGold()*10) {
				String str = Oracle.tipString("racistPraise");
				str = str.replaceAll(" an "," a ");
				str = str.replaceAll("oracles","rich person");
				str = str.replaceAll("oracle","rich people");
				extra.println("\"" +extra.capFirst(str)+"\"");
			}else {
				String str = Oracle.tipString(extra.choose("racistShun","racistPraise"));
				str = str.replaceAll(" an "," a ");
				str = str.replaceAll("not-oracle","poor person");
				str = str.replaceAll("oracles","rich people");
				str = str.replaceAll("oracle","rich person");
				extra.println("\"" +extra.capFirst(str)+"\"");	
			};break;
			
		case 3:bool = false;break;
		}}}else {
			if (state == 2) {
				this.richHelper(bodyguard, rich);
			}else {
				randomLists.deadPerson();
				findBehind("bodies");
			}
		}
	}
	private boolean richHelper(Person bodyguard, Person rich) {
		Person winner;
		if (bodyguard.isAlive()) {
			extra.print(extra.PRE_RED);
			extra.println("Their bodyguard attacks you!");
			winner = mainGame.CombatTwo(Player.player.getPerson(),bodyguard);
			if (!winner.isPlayer()) {
				return true;
			}
		}
		if (rich.isAlive()) {
			winner = mainGame.CombatTwo(Player.player.getPerson(),rich);
			if (!winner.isPlayer()) {
				return true;
			}
		}
		state = 3;
		name = "dead "+ name;
		interactString = "examine body";
		return false;
	}
	
	private void weapStone() {
		if (state ==0) {
			extra.println("There is a " + ((Weapon)storage1).getBaseName() + " embeded in the stone here. Try to take it?");
			if (extra.yesNo()) {
				extra.println("As you pull on it, the stone crumbles to pieces!");
				state = 1;
				name = "rock pieces";
				interactString = "examine rock pieces";
				if (AIClass.compareItem(Player.bag.getHand(),(Item)storage1,-1,false)) {
					;
					Services.sellItem(Player.bag.swapWeapon((Weapon)storage1),Player.bag,false);
					Networking.charUpdate();
				}else {
					Services.sellItem((Weapon)storage1,Player.bag,false);
				}
			}else {
				extra.println("You leave it alone.");
			}
		}else {
			extra.println("Crumbled rock lies on the forest floor.");
		}
	}
	
	private void equal1() {
		if (state == 1) {
			randomLists.deadPerson();
			findBehind("body");
			return;
		}
		Person equal = (Person)storage1;
		equal.getBag().graphicalDisplay(1, equal);
		boolean bool = true;
		while (bool) {
			extra.print(extra.PRE_RED);
		extra.println("1 attack");
		extra.println("2 chat");
		extra.println("3 leave");
		switch (extra.inInt(3)) {
		case 1: bool = false;
			mainGame.CombatTwo(Player.player.getPerson(),equal);
			if (equal.isAlive()) {break;}
			state = 1;
			name = "dead "+ name;
			interactString = "examine body";
			
		;break;
		case 2:
			Oracle.tip("equality");break;
			
		case 3:bool = false;break;
		}
		}
	}
	
	private void shaman() {
		if (state == 1) {
			randomLists.deadPerson();
			findBehind("body");
			return;
		}
		Person equal = (Person)storage1;
		equal.getBag().graphicalDisplay(1,equal);
		boolean bool = true;
		while (bool) {
			extra.print(extra.PRE_RED);
		extra.println("1 attack");
		extra.println("2 chat");
		extra.println("3 leave");
		switch (extra.inInt(3)) {
		case 1: name = "angry " +name ; interactString = "ERROR";
			storage1 = storage2;
			forceGo = true;
			idNum = 3;
			bool = false;
		;break;
		case 2:
			boolean bool2 = true;
			while (bool2) {
			extra.println("They say that they are a shaman.");
			int cost = level*50;
			extra.println("1 Buy cleansing ("+cost + " gold)");
			extra.println("2 chat");
			extra.println("3 return");
			
			switch (extra.inInt(3)) {
			case 1: if (Player.bag.getGold() < cost) {
					extra.println("Not enough gold!");
					break;
				}
			Player.bag.addGold(-cost);
			Player.player.getPerson().cureEffects();
			extra.println("You feel better.");
			
			break;
			case 2: Oracle.tip("shaman");break;
			case 3: bool2 = false;break;
			}
			};break;
		case 3:bool = false;break;
		}
		}
	}
	
	private boolean packOfWolves() {
		if (state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("The pack descends upon you!");
			ArrayList<Person> list = (ArrayList<Person>)storage1;
			ArrayList<Person> survivors = mainGame.HugeBattle(list,Player.list());
			
			if (survivors.contains(Player.player.getPerson())) {
			forceGo = false;
			interactString = "approach wolf corpses";
			storage1 = null;
			state = 1;
			name = "dead "+name;
			return false;}else {
				storage1 = survivors;
				return true;
			}
		}else {
			extra.println("There are a few wolf corpses here.");
			findBehind("corpses");
			return false;
		}
	}
	
	private void collector() {
		if (state == 0) {
			Person p = (Person)storage1;
			p.getBag().graphicalDisplay(1, p);
			extra.print(extra.PRE_RED);
			extra.println("Challenge "+ p.getName() + "?");
			if (extra.yesNo()){
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "dead "+name;
					interactString = "examine body";
				}
			}
		}else {randomLists.deadPerson();findBehind("body");}
		
	}
	
	private void beeHive() {
		name = "plant spot";
		interactString = "approach plant spot";
		idNum = -1;
		storage1 = new PlantSpot(level);
		extra.println("You destroy the hive, angering the bees!");
		Player.bag.addNewDrawBane(DrawBane.HONEY);
		Player.bag.addNewDrawBane(DrawBane.WAX);
		Player.bag.addSeed(Seed.BEE);
		Player.player.getPerson().addEffect(Effect.BEES);
		
	}


	@Override
	protected String shapeName() {
		return "STANDARD";
	}
	
	@Override
	protected DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.MEAT,DrawBane.WOOD,DrawBane.TRUFFLE};
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		this.spreadTime(time, calling);
		return timeEvent(time,calling);
	}

	@Override
	public List<TimeEvent> timeEvent(double d, TimeContext calling) {
		if (idNum == -1) {
			calling.localEvents(((PlantSpot)storage1).passTime(d,calling));
		}
		return null;
	}


}
