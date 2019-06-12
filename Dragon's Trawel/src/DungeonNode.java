import java.awt.Color;
import java.util.ArrayList;

public class DungeonNode implements java.io.Serializable{
	//potentail problem: all this code is in a highly duplicated node

	private String name;
	private static final int EVENT_NUMBER =6;
	private int state;
	private String interactString;
	private int idNum;
	private int level;
	private Object storage1, storage2;
	private ArrayList<DungeonNode> connects;
	private boolean forceGo;
	private Town town;
	
	public DungeonNode(int size, int tier, Town t) {
		state = 0;
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
		if (extra.chanceIn(1,10)) {
			level++;
		}
		setConnects(new ArrayList<DungeonNode>());
		forceGo = false;
		town = t;
		generate(size);
		
	}
	
	
	
	
	private void generate(int size) {
		switch (idNum) {
		case 1: storage1 = extra.choose("chest","chest","chest"); name = (String) storage1; interactString = "open "+name; storage2 = RaceFactory.makeMimic(1);break;
		case 2: name = extra.choose("dungeon guard","gatekeeper","dungeon guard"); interactString = "ERROR"; forceGo = true;
		storage1 = new Person(level);break;
		case 3: name = extra.choose("locked door","barricaded door","padlocked door"); interactString = "examine broken door";forceGo = true;break;
		case 4: name = "ladder"; interactString = "traverse ladder"; forceGo = true; break;
		case 5: ArrayList<Person> list = new ArrayList<Person>();
		list.add(new Person(extra.zeroOut(level-3)+1));
		list.add(new Person(extra.zeroOut(level-3)+1));
		name = "gate guards";
		interactString = "ERROR";
		storage1 = list;
		forceGo = true;
		state = 0;
		break;
		case 6: storage1 = extra.choose("chest"); name = (String) storage1; interactString = "open "+name;
		storage2 = RaceFactory.makeMimic(level);
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
			DungeonNode n = new DungeonNode(sizeRemove,level,town);
			connects.add(n);
			n.getConnects().add(this);
			i++;
		}
		DungeonNode n = new DungeonNode(sizeLeft,level,town);
		connects.add(n);
		n.getConnects().add(this);
	}
	
	private boolean interact() {
		switch(idNum) {
		case 1: chest();break;
		case 2: mugger1(); if (state == 0) {return true;};break;
		case 3: if (forceGo == true) {
			extra.println("You bash open the door.");
			 name = "broken door";forceGo = false;
		}else {
			extra.println("The door is broken.");
		};break;
		case 4: extra.println("You traverse the ladder.");break;
		case 5: return gateGuards();
		case 6: mimic(); if (state == 0) {return true;};break;
		}
		return false;
	}

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
		for (DungeonNode n: connects) {
			extra.println(i + " " + n.getName());
			if (Player.hasSkill(Skill.TIERSENSE)) {
				extra.println("Tier: " + n.level);
			}
			if (Player.hasSkill(Skill.TOWNSENSE)) {
				extra.println("Connections: " + n.connects.size());
			}
			i++;
			}
		extra.println(i + " exit dungeon");i++;
		int j = 1;
		int in = extra.inInt(i-1);
		if (in == j) {
			interact();
		}j++;
		for (DungeonNode n: connects) {
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
	
	public ArrayList<DungeonNode> getConnects() {
		return connects;
	}


	private void setConnects(ArrayList<DungeonNode> connects) {
		this.connects = connects;
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
	

}
