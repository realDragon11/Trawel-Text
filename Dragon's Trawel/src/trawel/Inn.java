package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.Feature.QRType;

//sells booze which increases temp hp for a few fights,
//has a resident which changes with time
public class Inn extends Feature implements java.io.Serializable{
	private int tier;
	private int resident;
	private double timePassed;
	private int wins = 0;
	private Town town;
	private String residentName;
	private int nextReset;
	private boolean playerwatch;
	
	private boolean canQuest = true;
	
	public ArrayList<Quest> sideQuests = new ArrayList<Quest>();

	
	private final static int RES_COUNT = 8;

	@Override
	public QRType getQRType() {
		return QRType.INN;
	}
	
	public Inn(String n, int t,Town twn, SuperPerson owner) {
		name = n;
		tier = t;
		town = twn;
		timePassed = extra.randRange(1,30);
		resident = extra.randRange(1,RES_COUNT);
		nextReset = extra.randRange(4,30);
		playerwatch = false;
		tutorialText = "Inns are a great place to buy beer and have various residents.";
		color = Color.YELLOW;
		this.owner = owner;
		
	}
	
	@Override
	public void init() {
		try {
			while (sideQuests.size() < 3) {
				generateSideQuest();
			}
			}catch (Exception e) {
				canQuest = false;
			}
	}
	
	private void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		sideQuests.add(BasicSideQuest.getRandomSideQuest(this.getTown(),this));
	}

	@Override
	public void go() {
		Networking.setArea("inn");
		if (owner == Player.player && moneyEarned > 0) {
			extra.println("You take the " + moneyEarned + " in profits.");
			Player.bag.addGold(moneyEarned);
			moneyEarned = 0;
		}
		mainGame.story.inn();
		Networking.sendStrong("Discord|imagesmall|inn|Inn|");
		while (true) {
		getResidentName();
		extra.println("1 leave");
		extra.println("2 beer ("+tier+"gp)");
		extra.println("3 "+residentName);
		extra.println("4 bard");
		extra.println("5 backroom");
		int size = 5;
		if (town.getOccupants().size() >=2){
		extra.println("6 watch duel (" + extra.format(nextReset-timePassed+1) + " hours)");
		size++;}
		
		switch(extra.inInt(size)) {
		case 1: return;
		case 2: buyBeer();break;
		case 3: goResident();break;
		case 4: bard();break;
		case 5: backroom();break;
		case 6: this.playerwatch = true; occupantDuel(); Player.addTime((nextReset-timePassed+1));
		;return;
		}
		}
	}

	

	private void backroom() {
		Inn inn = this;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuSelect> gen() {
				List<MenuSelect> mList = new ArrayList<MenuSelect>();
				
				for (Quest q: sideQuests) {
					mList.add(new QBMenuItem(q,inn));
				}
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}});
		
	}

	private void bard() {
		town.getIsland().getWorld().getRandSong().printSong();
	}

	@Override
	public void passTime(double time) {
		timePassed += time;
		moneyEarned +=tier*5*time;
		if (timePassed > nextReset) {
			timePassed = 0;
			occupantDuel();
			resident = extra.randRange(1,RES_COUNT);
			nextReset = extra.randRange(4,30);
			if (canQuest) {this.generateSideQuest();}
		}
		
	}
	
	private void occupantDuel() {
		if (town.getOccupants().size() >=2){
			Agent agent1= ((Agent)town.getOccupants().get(0));
			Agent agent2= ((Agent)town.getOccupants().get(1));
			if (playerwatch == false) {extra.changePrint(true); }playerwatch =false;
			if (mainGame.CombatTwo(agent1.getPerson(),agent2.getPerson(),town.getIsland().getWorld()) == agent2.getPerson()) {
				town.getOccupants().remove(0);
			}else {
				town.getOccupants().remove(1);
			}
			extra.changePrint(false);
			
		  }
	}
	
	private void goResident() {
		switch (resident) {
		case 1: goOldFighter();break;
		case 2: goDancers();break;
		case 3: goOracle();break;
		default:
			if (town.getOccupants().size() == 0){
			barFight();
			}else {
			goAgent((Agent)town.getOccupants().get(0));}
			;break;
		}
	}
	
	private void getResidentName() {
		residentName = "resident: ";
		switch(resident) {
		case 1: residentName += "A group of old fighters";break;
		case 2: residentName += "A group of dancers";break;
		case 3: residentName += "An oracle.";break;
		default:
			if (town.getOccupants().size() == 0){residentName += "Open Bar";}else {
				Person p = ((Agent)town.getOccupants().get(0)).getPerson();
				residentName += p.getName()+ " (" +p.getLevel() +")";
			}
			
			;break;
		}
		
	}
	
	private void goAgent(Agent agent) {
		Networking.sendColor(Color.RED);
		extra.println("1 fight");
		extra.println("2 chat");
		extra.println("3 leave");
		switch(extra.inInt(3)) {
		case 3: return;
		case 1: if (mainGame.CombatTwo(Player.player.getPerson(),agent.getPerson()) == Player.player.getPerson()) {
			town.getOccupants().remove(0);
			return;}else {break;}
		case 2:
			if (extra.chanceIn(1,2)) {
				BarkManager.getBoast(Player.player.getPerson(),true);
				//extra.println("You "+extra.choose("boast")+ " \"" + Player.player.getPerson().getTaunts().getBoast()+"\"");		
		}else {
			BarkManager.getTaunt(Player.player.getPerson());
				//extra.println("You "+extra.choose("taunt")+ " \"" + Player.player.getPerson().getTaunts().getTaunt()+"\"");				
		}
			if (extra.chanceIn(1,2)) {
				BarkManager.getBoast(agent.getPerson(), true);//extra.println(agent.getPerson().getName() + " "+extra.choose("boasts")+ " \"" + agent.getPerson().getTaunts().getBoast()+"\"");		
		}else {
			BarkManager.getTaunt(agent.getPerson());
				//extra.println(agent.getPerson().getName() + " "+extra.choose("taunts")+ " \"" + agent.getPerson().getTaunts().getTaunt()+"\"");				
		} 
			
			;break;
		}
			
			goAgent(agent);
		
	}

	private void buyBeer() {
		resident = 4;
		if (Player.bag.getGold() >= tier) {
			extra.println("Pay "+tier+" gold for a beer?");
			if (extra.yesNo()) {
				Player.player.getPerson().addBeer();
				moneyEarned +=tier;
			}
			}else {
				extra.println("You can't afford that!");
			}
	}
	
	private void goOldFighter() {
		while (true) {
			extra.println("There's an old fighter here, at the inn.");
			extra.println("1 Leave");
			extra.println("2 Chat with them");
			switch (extra.inInt(2)) {
			default: case 1: extra.println("You leave the fighter");return;
			case 2: extra.println("The old fighter turns and answers your greeting.");
			while (true) {
			extra.println("What would you like to ask about?");
			extra.println("1 tell them goodbye");
			extra.println("2 ask for a tip");
			extra.println("3 this inn");
			Networking.sendColor(Color.RED);
			extra.println("4 a duel");
			int in = extra.inInt(4);
			switch (in) {
				case 1: extra.println("They wish you well.") ;break;
				case 2: Oracle.tip("old");;break;
				case 3: extra.println("\"We are in " + this.getName() + ". It is pleasant here.\"");break;
				case 4: extra.println("You challenge the fighter!");mainGame.CombatTwo(Player.player.getPerson(), new Person(tier+2));return;
			}
			if (in == 1) {
				break;
			}
			}
			}
		}
	}
	
	private void goDancers() {
		extra.println("There are some dancers dancing excellently.");
		extra.println("They put on a good show.");
	}
	
	private void goOracle() {
		extra.println("There's an oracle staying at the inn.");
		new Oracle("inn",tier).go();
	}
	
	private void barFight() {
		extra.println("There is no resident, but there is room for a barfight... start one?");
		Networking.sendColor(Color.RED);
		if (extra.yesNo()) {
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),new Person(tier));
			if (winner.isPlayer()) {
				wins++;
				if (Player.player.animalQuest == 3) {
					extra.println("Hi, I'm Micheal SnowDancer. I really like the cut of your gib.");
					extra.println("Tell you what. Win at epino arena and I'll give you a reward.");
					Player.player.animalQuest++;
				}
			if (wins == 3) {
				Player.player.addTitle(this.getName() + " barfighter");
			}
			if (wins == 5) {
				Player.player.addTitle(this.getName() + " barbrewer");
			}
			if (wins == 10) {
				Player.player.addTitle(this.getName() + " barmaster");
			}
			
		}}
	}

	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}

}
