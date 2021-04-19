package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

public class GraveyardNode extends NodeConnector implements java.io.Serializable{
	//potentail problem: all this code is in a highly duplicated node


	private static final int EVENT_NUMBER =7;
	private int state;
	private int idNum;
	
	private Object storage1, storage2;
	
	private Town town;
	private Graveyard parent;
	
	
	
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
		case 1: name = "Gravedigger"; storage1 =  RaceFactory.getGravedigger(level); interactString = "Mug Gravedigger";break;
		case 2: name = "Graverobber"; interactString = "ERROR"; forceGo = true;
		storage1 = RaceFactory.getGraverobber(level);break;
		case 6:
			if (extra.chanceIn(1, 3)) {
			forceGo = true;}
		case 7:
			storage1 =  RaceFactory.makeStatue(level); name = ((Person)storage1).getBag().getRace().name + " statue";
			interactString = "loot statue";
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
			GraveyardNode n = new GraveyardNode(sizeRemove,level,town,parent);
			connects.add(n);
			n.getConnects().add(this);
			i++;
		}
		GraveyardNode n = new GraveyardNode(sizeLeft,level,town,parent);
		connects.add(n);
		n.getConnects().add(this);
	}
	
	@Override
	protected boolean interact() {
		switch(idNum) {
		case -1: Networking.sendStrong("PlayDelay|sound_footsteps|1|"); break;
		case 1:graveDigger(); break;
		case 2: mugger1(); if (state == 0) {return true;};break;
		case 6: statue(); if (state == 0) {return true;};break;
		case 7: statueLoot();break;
		}
		return false;
	}

	
	private void graveDigger() {
		if (state == 0) {
		while (true) {
			Person p = (Person)storage1;
			p.getBag().graphicalDisplay(1, p);
		extra.println("You come across a weary " + name + ", warding against undead during a break.");
		extra.println("1 Leave");
		Networking.sendColor(Color.RED);
		extra.println("2 Mug them");
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
		extra.println("3 this graveyard");
		int in = extra.inInt(3);
		switch (in) {
			case 1: extra.println("They wish you well.") ;break;
			case 2: Oracle.tip("gravedigger");break;
			case 3: extra.println("\"We are in " + parent.name + ". Beware, danger lurks everywhere.\"");break;
		}
		if (in == 1) {
			break;
		}
		}
		}
	}}else {randomLists.deadPerson();}}
	
	private void mugger1() {
		if (state == 0) {
			Networking.sendColor(Color.RED);
			extra.println("They attack you!");
			Person p = (Person)storage1;
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "dead "+name;
					interactString = "examine body";
					forceGo = false;
				}
		}else {randomLists.deadPerson();}
		
	}

	@Override
	protected String shapeName() {
		return parent.getShape().name();
	}
	
	private void statue() {
		if (state == 0) {
			Networking.sendColor(Color.RED);
			extra.println("The state springs to life and attacks you!");
			Person p = (Person)storage1;
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "destroyed "+name;
					interactString = "examine statue corpse";
					forceGo = false;
				}
		}else {randomLists.deadPerson();}
		
	}
	
	private void statueLoot() {
		if (state == 0) {
			Networking.sendColor(Color.RED);
			extra.println("You loot the statue...");
			Person p = (Person)storage1;
			p.getBag().graphicalDisplay(1,p);
			AIClass.loot(p.getBag(),Player.bag,Player.player.getPerson().getIntellect(),true);
					state = 1;
					storage1 = null;
					name = "looted statue";
					interactString = "loot statue";
					forceGo = false;
				
		}else {extra.println("You already looted this statue!");}
		Networking.clearSide(1);
	}

	@Override
	public void passTime(double d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeFinish() {
		// TODO Auto-generated method stub
		
	}
	

}
