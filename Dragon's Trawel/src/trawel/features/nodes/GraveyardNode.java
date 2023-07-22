package trawel.features.nodes;
import java.util.ArrayList;
import java.util.List;

import trawel.AIClass;
import trawel.DrawBane;
import trawel.Networking;
import trawel.Oracle;
import trawel.Person;
import trawel.Player;
import trawel.RaceFactory;
import trawel.Town;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class GraveyardNode extends NodeConnector implements java.io.Serializable{
	//potentail problem: all this code is in a highly duplicated node


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int EVENT_NUMBER =7;
	private int state;
	private int idNum;
	
	private Object storage1, storage2;
	
	private Town town;
	
	
	
	public GraveyardNode(int size, int tier, Town t,Graveyard p) {
		state = 0;
		parent = p;
		parentName = "graveyard";
		
		idNum = extra.randRange(1,EVENT_NUMBER);
		level = tier;
		if (extra.chanceIn(1,10)) {
			level++;
		}
		
		setConnects(new ArrayList<NodeConnector>());
		forceGo = false;
		town = t;
		generate(size);
		
	}
	
	public int getLevel() {
		return level;
	}
	
	
	
	private void generate(int size) {
		switch (idNum) {
		case -1:name = extra.choose("stairs","ladder"); interactString = "traverse "+name;forceGo = true;break;
		case 1: name = "Shadowy Figure"; storage1 =  RaceFactory.getGravedigger(level); interactString = "Approach Shadowy Figure";break;
		case 2: name = "Shadowy Figure"; interactString = "Approach Shadowy Figure";storage1 = RaceFactory.getGraverobber(level);break;
		
		case 3: ArrayList<Person> list = new ArrayList<Person>();
			for (int i = 0;i < extra.randRange(3,4);i++) {
			list.add(RaceFactory.makeBat(extra.zeroOut(level-4)+1));}
			name = "bats";
			interactString = "ERROR";
			storage1 = list;
			forceGo = true;
			state = 0;
		;break;
		case 4: name = "Shadowy Figure"; interactString = "Approach Shadowy Figure";storage1 = RaceFactory.makeVampire(level);break;
		case 5:
			name = "Shadowy Figure"; interactString = "Approach Shadowy Figure";
			storage1 = RaceFactory.makeCollector(level);
			break;
		case 6: case 7:
			storage1 =  RaceFactory.makeStatue(level); name = "Shadowy Figure";
			interactString = "Approach Shadowy Figure";
			break;
		}
		if (size < 2) {
			return;
		}
		int split = extra.randRange(1,Math.min(size,3));
		int i = 1;
		int sizeLeft = size;
		while (i < split) {
			int sizeRemove = extra.randRange(0,sizeLeft-1);
			sizeLeft-=sizeRemove;
			GraveyardNode n = new GraveyardNode(sizeRemove,level,town,(Graveyard)parent);
			connects.add(n);
			n.getConnects().add(this);
			i++;
		}
		GraveyardNode n = new GraveyardNode(sizeLeft,level,town,(Graveyard)parent);
		connects.add(n);
		n.getConnects().add(this);
	}
	
	@Override
	protected boolean interact() {
		switch(idNum) {
		case -1: Networking.sendStrong("PlayDelay|sound_footsteps|1|"); break;
		case 1:graveDigger(); break;
		case 2: mugger1(); if (state == 0) {return true;};break;
		case 3: return packOfBats();
		case 4: vampire1(); if (state == 0) {return true;};break;
		case 5:	collector();break;
		case 6: statue(); if (state == 0) {return true;};break;
		case 7: statueLoot();break;
		
		}
		return false;
	}
	
	private boolean packOfBats() {
		if (state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("The bats descend upon you!");
			List<Person> list = (List<Person>)storage1;
			List<Person> survivors = mainGame.HugeBattle(list,Player.list());
			
			if (survivors.contains(Player.player.getPerson())) {
			forceGo = false;
			interactString = "approach bat corpses";
			storage1 = null;
			state = 1;
			name = "dead "+name;
			return false;}else {
				storage1 = survivors;
				return true;
			}
		}else {
			extra.println("There are a few bat corpses here.");
			findBehind("corpses");
			return false;
		}
	}

	
	private void graveDigger() {
		extra.println("Approach the "+ name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (state == 0) {
		while (true) {
			Person p = (Person)storage1;
			p.getBag().graphicalDisplay(1, p);
		extra.println("You come across a weary gravedigger, warding against undead during a break.");
		name = "Gravedigger";interactString = "Approach the "+name;
		extra.println("1 Leave");
		extra.print(extra.PRE_RED);
		extra.println("2 Mug them");
		extra.println("3 Chat with them");
		switch (extra.inInt(3)) {
		default: case 1: extra.println("You leave the gravedigger alone.");return;
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
		extra.println("3 this graveyard");
		int in = extra.inInt(3);
		switch (in) {
			case 1: extra.println("They wish you well.") ;break;
			case 2: Oracle.tip("gravedigger");break;
			case 3: extra.println("\"We are in " + parent.getName() + ". Beware, danger lurks everywhere.\"");break;
		}
		if (in == 1) {
			break;
		}
		}
		}
	}}else {randomLists.deadPerson();}findBehind("body");}
	
	private void mugger1() {
		extra.println("Approach the "+ name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("The graverobber attacks you!");
			name = "Graverobber";
			interactString = "Approach the "+name;
			Person p = (Person)storage1;
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
	
	private void vampire1() {
		extra.println("Approach the "+ name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("The vampire attacks you!");
			name = "Vampire";
			interactString = "Approach the "+name;
			Person p = (Person)storage1;
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "dead "+name;
					interactString = "examine body";
					forceGo = false;
				}
		}else {randomLists.deadPerson();findBehind("dust");}
		
	}

	@Override
	protected String shapeName() {
		return ((Graveyard)parent).getShape().name();
	}
	
	private void statue() {
		extra.println("Approach the "+ name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (state == 0) {
			name = ((Person)storage1).getBag().getRace().renderName(false) + " statue";
			extra.print(extra.PRE_RED);
			extra.println("The statue springs to life and attacks you!");
			name = "Living Statue";
			interactString = "Approach the "+name;
			Person p = (Person)storage1;
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "destroyed "+name;
					interactString = "examine destroyed statue";
					forceGo = false;
				}
		}else {randomLists.deadPerson();findBehind("destroyed statue");}
		
	}
	
	private void statueLoot() {
		extra.println("Approach the "+ name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("You loot the statue...");
			Person p = (Person)storage1;
			p.getBag().graphicalDisplay(1,p);
			AIClass.loot(p.getBag(),Player.bag,Player.player.getPerson().getIntellect(),true);
					state = 1;
					storage1 = null;
					name = "looted statue";
					interactString = "examine statue";
					forceGo = false;
				
		}else {extra.println("You already looted this statue!");findBehind("statue");}
		Networking.clearSide(1);
	}
	
	private void collector() {
		extra.println("Approach the "+ name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (state == 0) {
			Person p = (Person)storage1;
			name = p.getName();
			interactString = "Approach "+ name;
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

	@Override
	public void endPass() {
		// Auto-generated method stub
		
	}
	
	@Override
	protected DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.GRAVE_DIRT,DrawBane.GARLIC,DrawBane.TELESCOPE};
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
