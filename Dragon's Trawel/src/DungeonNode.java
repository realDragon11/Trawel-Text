import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

public class DungeonNode extends NodeConnector implements java.io.Serializable{
	//potentail problem: all this code is in a highly duplicated node


	private static final int EVENT_NUMBER =7;
	private int state;
	private int idNum;
	
	private Object storage1, storage2;
	
	private Town town;
	private Dungeon parent;
	
	
	
	public DungeonNode(int size, int tier, Town t,Dungeon p,boolean stair) {
		state = 0;
		parent = p;
		
		idNum = extra.randRange(1,EVENT_NUMBER);
		if (extra.chanceIn(1,2)) {
			idNum = 2;//dungeon guard
			if (extra.chanceIn(1,3)) {
			idNum = 5;	
			}
			
		}
		if (extra.chanceIn(1,10)) {
			idNum = 1;//chest
			
		}
		level = tier;
		if (p.getShape() != Dungeon.Shape.TOWER) {
		if (extra.chanceIn(1,10)) {
			level++;
		}}
		if (stair) {
			idNum = -1;
			isStair = true;
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
		case 1: storage1 = extra.choose("chest","chest","chest"); name = (String) storage1; interactString = "open "+name; storage2 = RaceFactory.makeMimic(1);break;
		case 2: name = extra.choose("dungeon guard","gatekeeper","dungeon guard"); interactString = "ERROR"; forceGo = true;
		storage1 = new Person(level);break;
		case 3: name = extra.choose("locked door","barricaded door","padlocked door"); interactString = "examine broken door";forceGo = true;break;
		//case 4: name = "ladder"; interactString = "traverse ladder"; forceGo = true; break;
		case 4: ArrayList<Person> list = new ArrayList<Person>();
		list.add(new Person(extra.zeroOut(level-3)+1));
		list.add(new Person(extra.zeroOut(level-3)+1));
		name = "gate guards";
		interactString = "ERROR";
		storage1 = list;
		forceGo = true;
		state = 0;
		break;
		case 5: storage1 = extra.choose("chest"); name = (String) storage1; interactString = "open "+name;
		storage2 = RaceFactory.makeMimic(level);
		break;
		case 6:
			if (extra.chanceIn(1, 3)) {
			forceGo = true;}
		case 7:
			storage1 =  RaceFactory.makeStatue(level); name = ((Person)storage1).getBag().getRace().name + " statue";
			interactString = "loot statue";
			break;
		}
		if (size < 2 || parent.getShape() != Dungeon.Shape.STANDARD) {
			return;
		}
		int split = extra.randRange(1,Math.min(size,3));
		int i = 1;
		int sizeLeft = size;
		while (i < split) {
			int sizeRemove = extra.randRange(0,sizeLeft-1);
			sizeLeft-=sizeRemove;
			DungeonNode n = new DungeonNode(sizeRemove,level,town,parent,false);
			connects.add(n);
			n.getConnects().add(this);
			i++;
		}
		DungeonNode n = new DungeonNode(sizeLeft,level,town,parent,false);
		connects.add(n);
		n.getConnects().add(this);
	}
	
	@Override
	protected boolean interact() {
		switch(idNum) {
		case 1: chest();break;
		case 2: mugger1(); if (state == 0) {return true;};break;
		case 3: if (forceGo == true) {
			extra.println("You bash open the door.");
			 name = "broken door";forceGo = false;
		}else {
			extra.println("The door is broken.");
		};break;
		//case 4: extra.println("You traverse the ladder.");break;
		case 4: return gateGuards();
		case 5: mimic(); if (state == 0) {return true;};break;
		case 6: statue(); if (state == 0) {return true;};break;
		case 7: statueLoot();break;
		}
		return false;
	}

	
	private void chest() {
		if (state == 0) {
			Person p = (Person)storage2;
			p.getBag().graphicalDisplay(1,p);
			extra.println("Really open the " + name + "?");
			if (extra.yesNo()) {
			int gold = extra.randRange(100,200)*level;
			Player.bag.addGold(gold);
			extra.println("You open the " +storage1 + " and find " + gold + " gold.");
					state = 1;
					name = "empty " + storage1;
					interactString = "examine empty " + storage1;
			}else {
				extra.println("You decide not to open it.");
			}
			}else {extra.println("The "+storage1+" has already been opened.");}
		
	}
	
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
	
	private boolean gateGuards() {
		if (state == 0) {
			Networking.sendColor(Color.RED);
			extra.println("They attack you!");
			ArrayList<Person> list = (ArrayList<Person>)storage1;
			ArrayList<Person> survivors = mainGame.HugeBattle(list,Player.list());
			
			
			if (survivors.contains(Player.player.getPerson())) {
			forceGo = false;
			interactString = "approach bodies";
			storage1 = null;
			state = 1;
			name = "dead "+name;
			return false;}else {
				storage1 = survivors;
				return true;
			}
		}else {
			randomLists.deadPerson();
			randomLists.deadPerson();
			return false;
		}
		
		
	}
	
	private void mimic() {
		if (state == 0) {
			Person p = (Person)storage2;
			p.getBag().graphicalDisplay(1,p);
			extra.println("Really open the " + name + "?");
		if (extra.yesNo()) {
			Networking.sendColor(Color.RED);
			extra.println("The mimic attacks you!");
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "dead mimic";
					interactString = "examine body";
					forceGo = false;
				}
		}else {extra.println("You decide not to open it.");}
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
					name = "dead "+name + "statue";
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
				
		}else {randomLists.deadPerson();}
		
	}



	/*
	public void addBacks() {
		backed++;
		if (connects.size() > 0) {
			for (DungeonNode d: connects) {
				if ((this.isStair)
				if (backed == 0) {
				d.connects.add(this);
				d.addBacks();}
			}
		}
		
	}*/
	

}
