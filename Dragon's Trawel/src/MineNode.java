import java.awt.Color;
import java.util.ArrayList;

public class MineNode extends NodeConnector implements java.io.Serializable{
	//potentail problem: all this code is in a highly duplicated node

	//private String name;
	private static final int EVENT_NUMBER = 9;
	private int state;
	//private String interactString;
	private int idNum;
	//private int level;
	private Object storage1, storage2;
	private boolean forceGo;
	private Mine parent;
	
	public MineNode(int size, int tier,Mine p) {
		parent = p;
		state = 0;
		idNum = extra.randRange(1,EVENT_NUMBER);
		if (extra.chanceIn(1, 5)) {
			idNum = 3;//gold7
			
		}
		if (idNum == 3) {
			p.addVein();
		}
		level = tier;
		
		setConnects(new ArrayList<NodeConnector>());
		forceGo = false;
		generate(size);
		
	}
	
	
	
	
	private void generate(int size) {
		switch (idNum) {
		case 0: name = ""; interactString = "";break;
		case 1: name = extra.choose("fighter","duelist","warrior"); interactString = "challenge " + name;
		storage1 = new Person(level);
		break;
		case 2: name = extra.choose("river","pond","lake","stream"); interactString = "wash yourself";break;
		case 3: storage1 = extra.choose("silver","gold","platinum","iron","copper"); name = storage1+" vein"; interactString = "mine "+storage1;break;
		case 4: name = extra.choose("mugger","robber","thug","bandit","marauder","outlaw","desperado","cutthroat"); interactString = "ERROR"; forceGo = true;
		storage1 = new Person(level);break;
		case 5: name = extra.choose("locked door","barricaded door","padlocked door"); interactString = "unlock door";forceGo = true;break;
		case 6: interactString = "examine crystals"; storage1 = randomLists.randomColor(); name = "weird " + (String)storage1 + " crystals";break;
		case 7: name = "minecart"; interactString = "examine minecart";break;
		case 8: name = "ladder"; interactString = "traverse ladder"; forceGo = true; break;
		case 9: name = "cultists"; interactString = "approach cultists";
		storage1 = new Person(level);
		break;
		}
		if (size < 2 || parent.getShape().equals(Mine.Shape.HELL)) {
			return;
		}
		if (extra.chanceIn(1,10)) {
			level++;
		}
		int split = extra.randRange(1,Math.min(size,3));
		int i = 1;
		int sizeLeft = size;
		while (i < split) {
			int sizeRemove = extra.randRange(0,sizeLeft-1);
			sizeLeft-=sizeRemove;
			MineNode n = new MineNode(sizeRemove,level,parent);
			connects.add(n);
			n.getConnects().add(this);
			i++;
		}
		MineNode n = new MineNode(sizeLeft,level,parent);
		connects.add(n);
		n.getConnects().add(this);
	}
	@Override
	protected boolean interact() {
		switch(idNum) {
		case 1: duelist();break;
		case 2: extra.println("You wash yourself in the "+name+".");break;
		case 3: goldVein1();break;
		case 4: mugger1(); if (state == 0) {return true;};break;
		case 5: if (forceGo == true) {
			if (parent.getOwner() == Player.player) {
				extra.println("You unlock and then relock the door.");
			}else {
			extra.println("You bash open the door.");
			interactString = "examine broken door";
			 name = "broken door";forceGo = false;}
		}else {
			extra.println("The door is broken.");
		};break;
		case 6: extra.println("You examine the " + ((String)storage1)+ " crystals. They are very pretty.");break;
		case 7: extra.println("You examine the iron minecart. It is on the tracks that travel throughout the mine.");break;
		case 8: extra.println("You traverse the ladder.");break;
		case 9: cultists1();break;
		
		}
		Networking.clearSide(1);
		return false;
	}

	/*public void go() {
		Player.addTime(.1);
		int i = 1;
		if (forceGo) {
			if (interact()) {
				return;
			}

		}
		extra.println(name);
		extra.println(i+ " " + interactString);i++;
		for (MineNode n: connects) {
			extra.println(i + " " + n.getName());
			if (Player.hasSkill(Skill.TIERSENSE)) {
				extra.println("Tier: " + n.level);
			}
			if (Player.hasSkill(Skill.TOWNSENSE)) {
				extra.println("Connections: " + n.connects.size());
			}
			i++;
			}
		extra.println(i + " exit mine");i++;
		int j = 1;
		int in = extra.inInt(i-1);
		if (in == j) {
			interact();
		}j++;
		for (MineNode n: connects) {
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
	
	public ArrayList<MineNode> getConnects() {
		return connects;
	}


	private void setConnects(ArrayList<MineNode> connects) {
		this.connects = connects;
	}
	*/
	
	private void duelist() {
		if (state == 0) {
			Person p = (Person)storage1;
			p.getBag().graphicalDisplay(1, p);
			Networking.sendColor(Color.RED);
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
		}else {randomLists.deadPerson();}
		
	}

	
	private void goldVein1() {
		if (state == 0) {
			int mult1 = 0, mult2 = 0;
			switch (storage1.toString()) {
			case "gold": mult1 = 100; mult2 = 200;break;
			case "silver": mult1 = 75; mult2 = 150;break;
			case "platinum": mult1 = 150; mult2 = 300;break;
			case "iron": mult1 = 50; mult2 = 100;break;
			case "copper": mult1 = 25; mult2 = 50;break;
			}
			int gold = extra.randRange(mult1,mult2)*level;
			Player.bag.addGold(gold);
			extra.println("You mine the vein for "+storage1+" worth "+ gold + " gold.");
					state = 1;
					name = "empty vein";
					interactString = "examine empty vein";
			parent.removeVein();
			}else {extra.println("The "+storage1+" has already been mined.");}
		
	}
	
	private void mugger1() {
		if (state == 0) {
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
	
	
	/* for Ryou Misaki (nico)*/
	private void cultists1() {
		Person p = (Person)storage1;
		p.getBag().graphicalDisplay(1, p);
		int inInt = 2;
		do {
		inInt = 2;
		boolean hasSkills = false;
		if (Player.hasSkill(Skill.BLOODTHIRSTY) || Player.hasSkill(Skill.BEER_BELLY)){
		if (Player.hasSkill(Skill.BLOODTHIRSTY) && Player.hasSkill(Skill.BEER_BELLY)) {
			extra.println("The cultists welcome you and ask you how you are doing.");
			extra.println("1 chat");
			hasSkills = true;
		}else {
			extra.println("The cultists offer to enhance you... if you sacrifice some blood.");
			extra.println("1 accept offer");
		}
		Networking.sendColor(Color.RED);
		extra.println("2 attack");	
		extra.println("3 leave");
		inInt = extra.inInt(3);
		}
	
		switch (inInt) {
		case 1: 
			if (hasSkills) {
				Oracle.tip("cult");
			}else {
				extra.println("You die!");
				mainGame.die("You rise from the altar!");
				extra.println("The cultists praise you as the second coming of flagjaij!");
				Player.player.getPerson().addEffect(Effect.CURSE);
				Player.addSkill(Skill.BLOODTHIRSTY);
				Player.addSkill(Skill.BEER_BELLY);
				hasSkills = true;
				Networking.sendStrong("Achievement|cult1|");
			};break;
			
		case 2:name = "angry cultist"; interactString = "ERROR";
		
		forceGo = true;
		idNum = 4;break;
	
		case 3: extra.println("You leave them alone.");break;
		
		}
		}while(inInt == 1);
		
	}
	
	@Override
	protected String shapeName() {
		return parent.getShape().name();
	}
	
	

}
