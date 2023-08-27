package trawel.towns.fight;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.AIClass;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.factions.Faction;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.quests.QRMenuItem;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;
import trawel.towns.services.Oracle;

public class Forest extends Feature{
	
	private static final long serialVersionUID = 1L;
	private int explores;
	private int dryadQuest;
	private int exhaust;
	
	@Override
	public QRType getQRType() {
		return QRType.FOREST;
	}
	
	public Forest(String name, int tier) {
		this.tier = tier;
		this.name = name;
		explores = 0;
		exhaust = 0;
		tutorialText = "Forest.";
		background_area = "forest";
		background_variant = 1;
	}
	
	@Override
	public String getColor() {
		return extra.F_COMBAT;//unsure
	}
	
	@Override
	public void go() {
		Networking.setArea("forest");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|forest|Forest|");
		
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "explore";
					}

					@Override
					public boolean go() {
						explore();
						return false;
					}
				});
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "exit";
					}

					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}
			
		});
	}
	
	public void explore() {
		exhaust++;
		explores++;
		if (explores == 10) {
			Player.player.addTitle(this.getName() + " wanderer");
		}
		if (explores == 50) {
			Player.player.addTitle(this.getName() + " explorer");
		}
		if (explores == 100) {
			Player.player.addTitle(this.getName() + " guide");
		}
		if (dryadQuest > 0 && dryadQuest < 5 && Math.random() > .5) {
			lumerbjackDryad();return;
		}
		if (exhaust > 10) {
			if (!extra.chanceIn(1,(int)(exhaust/3))) {
				dryForest();
				return;
			}
		}
		switch (extra.randRange(1,21)) {
		case 1: goldStream();break;
		case 2: case 3: case 4: funkyMushroom();break;
		case 5: case 6:  mugger1();break;
		case 7: if (Math.random() > .5){
			this.treeOnPerson();
			}else {hangedMan();
			}break;
		case 8: case 9: mugger2();break;
		case 10: case 11: dryad();break;
		case 12: case 13: this.treeOnPerson();break;
		case 14: case 15: this.oldFighter();break;
		case 16: this.fairyCircle1();break;
		case 17: case 21: //if (fairyCircle2()) {return;};break;
		case 18: this.fairyCircle3();break;
		case 19: findEquip();break;
		case 20: abandonedHut();break;
		}
		Player.addTime(.5);
		mainGame.globalPassTime();
		Networking.clearSide(1);
	}



	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (exhaust > 0 && time > .1) {
			exhaust--;
		}
		return null;
	}
	
	private void goldStream() {
		extra.println("You spot a bag of "+World.currentMoneyString()+" floating down a stream! Chase it?");
		Boolean result = extra.yesNo();
		if (result) {
			if (Math.random() > .5) {
				extra.println("A fighter runs up and calls you a thief before launching into battle!");
				Combat c = Player.player.fightWith(RaceFactory.getMugger(tier));
				if (c.playerWon() > 0) {
					int gold = (2*tier)+extra.randRange(0,3);
					extra.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
					Player.player.addGold(gold);
				}else {
					extra.println("They take the gold sack and leave you floating down the stream...");
				}
			}else {
				int gold = (tier)+extra.randRange(0,2);
				extra.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
				Player.player.addGold(gold);
			}
		}else {
			extra.println("You let the bag drift out of sight...");
		}
	}
	
	private void funkyMushroom() {
		extra.println("You spot a glowing mushroom on the forest floor.");
		extra.println("1 leave it");
		extra.println("2 eat it");
		extra.println("3 sell it");
		extra.println("4 crush it");
		int in =  extra.inInt(4);
		switch (in) {
		default: case 1: extra.println("You decide to leave it alone.");break;
		case 2:
			extra.println("You eat the mushroom...");
			switch(extra.randRange(1,3)) {
			case 1: extra.println("The mushroom is delicous!");break;
			case 2: extra.println("Eating the mushroom is very difficult... but you manage.");
			Player.player.getPerson().addXp(tier*2);break;
			case 3: extra.println("You feel lightheaded.... you pass out!");
			extra.println("When you wake up, you notice someone went through your bags!");
			extra.println(Player.loseGold(tier*extra.randRange(5,10),true));
			break;
			}
			if (Math.random() > .8) {
				extra.println("As you eat the mushroom, you hear a voice cry out:");
				extra.print(extra.PRE_BATTLE);
				switch(extra.randRange(1,3)) {
				case 1: 
					extra.println("\"You dare violate the forest?!\"");
					Player.player.fightWith(RaceFactory.getDryad(tier));
					break;
				case 2:
					extra.println("\"Hey, I wanted that!\"");
					Player.player.fightWith(RaceFactory.getMugger(tier));
					break;
				case 3:
					extra.println("\"You dirty plant-thief!\"");
					Player.player.fightWith(RaceFactory.getMugger(tier));
					break;
				}
			
			}
			
			;break;
		case 3:
			extra.println("You pick up the mushroom to sell it.");
			if (Math.random() > .8) {
			extra.println("You hear someone cry out from behind you!");
			extra.print(extra.PRE_BATTLE);
			Combat c;
			switch(extra.randRange(1,3)) {
			default:
				extra.println("\"You dare violate the forest?!\"");
				c = Player.player.fightWith(RaceFactory.getDryad(tier));
				break;
			case 2:
				extra.println("\"Hey, I wanted that!\"");
				c = Player.player.fightWith(RaceFactory.getMugger(tier));
				break;
			case 3:
				extra.println("\"You dirty plant-thief!\"");
				c = Player.player.fightWith(RaceFactory.getMugger(tier));
				break;
			}
			if (c.playerWon() > 0) {
				int gold = 2*extra.randRange(1,tier+1);
				extra.println("You sell the mushroom for " +World.currentMoneyDisplay(gold) + ".");
				Player.player.addGold(gold);
			}
			}else {
				int gold = extra.randRange(1,tier+1);
				extra.println("You sell the mushroom for " +World.currentMoneyDisplay(gold) + ".");
				Player.player.addGold(gold);
			};break;
		case 4:
			extra.println("You crush the mushroom under your heel.");
			extra.println("You hear someone cry out from behind you!");
			extra.print(extra.PRE_BATTLE);
			switch(extra.randRange(1,3)) {
			case 1: 
				extra.println("\"You dare violate the forest?!\"");
				Player.player.fightWith(RaceFactory.getDryad(tier));
				break;
			case 2:
				extra.println("\"Hey, I wanted that!\"");
				Player.player.fightWith(RaceFactory.getMugger(tier));
				break;
			case 3:
				extra.println("\"You dirty plant-crusher!\"");
				Player.player.fightWith(RaceFactory.getMugger(tier));
				break;
			}
		}
		
	}
	
	private void mugger1() {
		extra.println("You see someone being robbed! Help?");
		Boolean help = extra.yesNo();
		Person robber = RaceFactory.getMugger(tier);
		robber.getBag().graphicalDisplay(1, robber);
		if (help) {
			Combat c = Player.player.fightWith(robber);
			if (c.playerWon() > 0) {
				int gold = extra.randRange(2,10)*tier;
				extra.println("They give you a reward of " + World.currentMoneyDisplay(gold) + " in thanks for saving them.");
				Player.player.addGold(gold);
			}else {
				extra.println("They mugged you too!");
				extra.println(Player.loseGold((20*tier)+extra.randRange(0,10),true));
			}
		}else {
			extra.println("You walk away.");
		}
	}
	
	private void mugger2() {
		extra.println(extra.PRE_BATTLE+"You see a mugger charge at you!");
		Combat c = Player.player.fightWith(RaceFactory.getMugger(tier));
		if (c.playerWon() > 0) {
		}else {
			extra.println("They rifle through your bags!");
			extra.println(Player.loseGold((30*tier)+extra.randRange(0,20),true));
		}
	}
	
	private void hangedMan() {
		extra.println("You come across a man hanging from a tree.");
		switch (extra.randRange(0,2)) {
		case 0:extra.println("You sigh and move on.");
			break;
		case 1: extra.println("There's something off about the corpse... You feel like you need to leave, so you do.");
			break;
		case 2:
			extra.println(extra.PRE_BATTLE+ "Something fell and horrible steps out of the hanged man's shadow!");
			Person reaver = RaceFactory.makeFellReaver(tier);
			Combat c = Player.player.fightWith(reaver);
			if (c.playerWon() > 0) {
				extra.println("They say a predator is often blind to its own peril- at least there won't be any more men hanged here soon.");
				//bonus heroism
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC, (tier/5f),0);
			}else {
				extra.println("You wake up elsewhere, striken with nightmares of claw, teeth, sinew, and bone. You feel an evil presence watching you...");
				town.getIsland().getWorld().addReoccuring(new Agent(reaver,AgentGoal.SPOOKY));
			}
			break;
		}
		
	}
	
	private void dryad() {
		Person robber = RaceFactory.getDryad(tier);
		robber.getBag().graphicalDisplay(1, robber);
		while (true) {
		extra.println("You come across a dryad tending to a tree.");
		extra.println("1 Leave");//DOLATER: fix menu
		extra.println(extra.PRE_BATTLE+"2 Attack them.");
		extra.println("3 Chat with them");
		switch (extra.inInt(3)) {
		default: case 1: extra.println("You leave the dryad alone");return;
		case 2: 
			extra.println("You attack the dryad!");
			Player.player.fightWith(robber);
		return;
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
			extra.println("They seem very passionate about it.");break;
			case 3: extra.println("\"We are in " + this.getName() + ". I don't venture away from my tree.\"");break;
		}
		if (in == 1) {
			break;
		}
		}
		}
	}}
	
	private void treeOnPerson() {
		Person p = RaceFactory.getMugger(tier);
		p.getBag().graphicalDisplay(1, p);
		extra.println("You stumble upon a person stuck under a fallen tree. Help them?");
		if (extra.yesNo()) {
			extra.println("You move the tree off of them.");
			if (Math.random() > .9) {
				extra.println(extra.PRE_BATTLE+"Suddenly, they attack you!");
				Player.player.fightWith(p);
			}else {
				if (Math.random() < .3) {
					extra.println("They scamper off...");
				}else {
					int gold = extra.randRange(0,5)+ (tier*extra.randRange(1,4));
					extra.println("They give you a reward of " + World.currentMoneyDisplay(gold) + " in thanks for saving them.");
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,1,0);
					Player.player.addGold(gold);
				}
			}
		}else {
			extra.println("You leave them alone to rot...");
		}
	}

	public int getExplores() {
		return explores;
	}

	public void setExplores(int explores) {
		this.explores = explores;
	}
	
	private void oldFighter() {
		Person robber = RaceFactory.makeOld(tier+2);
		robber.getBag().graphicalDisplay(1, robber);
		while (true) {
		extra.println("You come across an old fighter, resting on a log.");
		extra.println("1 Leave");//DOLATER: fix menu
		extra.println(extra.PRE_BATTLE+"2 Attack them.");
		extra.println("3 Chat with them");
		switch (extra.inInt(3)) {
		default: case 1: extra.println("You leave the fighter alone");return;
		case 2: 
			extra.println("You attack the fighter!");
			Player.player.fightWith(robber);
			;return;
		case 3: extra.println("The old fighter turns and answers your greeting.");
		while (true) {
		extra.println("What would you like to ask about?");
		extra.println("1 tell them goodbye");
		extra.println("2 ask for a tip");
		extra.println("3 this forest");
		int in = extra.inInt(3);
		switch (in) {
			case 1: extra.println("They wish you well.") ;break;
			case 2: Oracle.tip("old");break;
			case 3: extra.println("\"We are in " + this.getName() + ". Beware, danger lurks under these trees.\"");break;
		}
		if (in == 1) {
			break;
		}
		}
		}
	}}
	
	private void fairyCircle1() {
		extra.println("You find a fairy circle of mushrooms. Step in it?");
		if (extra.yesNo()) {
			extra.println("You step in it. Nothing happens.");
		}else {
			extra.println("You stay away from the circle.");
		}
	}
	
	private void fairyCircle3() {
		extra.println("You find a fairy circle of mushrooms. Step in it?");
		if (extra.yesNo()) {
			extra.println("You step in it...");
			if (dryadQuest == 0) {
			extra.println("A squirrel asks if you want to be a dryad.");
			if (extra.yesNo()) {
				extra.println("You are told to kill lumberjacks damaging the forest.");
				dryadQuest = 1;
			}
			}else {
				if (dryadQuest == 4) {
				extra.println("You feel the forest reward you! Whirlwinds of aether appear at your feet!");
				int a_reward = (tier*(extra.randRange(400,500)));
				extra.println("You gain " + a_reward + " aether!");
				Player.bag.addAether(a_reward);
				dryadQuest = 5;
				}
			}
		}else {
			extra.println("You stay away from the circle.");
		}
	}
	
	private void lumerbjackDryad() {
		Person robber = RaceFactory.getLumberjack(tier);
		if (extra.chanceIn(1,3)) {//TODO: make this much rarer and use ingame holidays/months instead
			LocalDateTime t = LocalDateTime.now();
			switch (t.getMonth()) {//lmao alphabetically ordered months
			//enums are ordinal ordered, why does eclipse do this
			case APRIL:
				//idk how to describe medieval april fools day
				extra.println(extra.PRE_BATTLE+"A person is chopping down tree covered in mud! Attack them?");
				break;
			case AUGUST:
				//trawel v0.8 haha
				extra.println(extra.PRE_BATTLE+"A person is chopping down a tree with the symbols 'v.8' carved into it! Attack them?");
				break;
			case DECEMBER:
				//t.getDayOfMonth() > 14 && t.getDayOfMonth() < 25
				extra.println(extra.PRE_BATTLE+"A person is chopping down a christmas tree! Attack them?");
				break;
			case FEBRUARY:
				//valentines day
				extra.println(extra.PRE_BATTLE+"A lumberjack is chopping down a tree with many hearts and initials carved into it! Attack them?");
				break;
			case JANUARY:
				extra.println(extra.PRE_BATTLE+"A wannabe lumberjack has resolved to cut down more trees this year! Attack them?");
				break;
			case JULY:
				//canada's independence day isn't pog enough to celebrate, so we celebrate
				//https://www.holidayinsights.com/moreholidays/july/iforgotday.htm
				extra.println("What were you doing again? There's a lumberjack here, should you attack them?");
				break;
			case JUNE:
				//Emancipation Day, D day
				robber.setRacism(true);//assigned racist at tree
				extra.println(extra.PRE_BATTLE+"A racist is chopping down a tree. Attack them?");
				break;
			case MARCH:
				//daylight savings, trawel doesn't have that lmao
				//trawel has perfect years, but ironically it DOES have different rise and set times
				extra.println(extra.PRE_BATTLE+"A lumberjack is chopping down a tree, apparently they need more paper for calender... clocks? You feel kinda bad, should you attack them?");
				break;
			case MAY:
				//lot of war days, cinco de mayo included
				extra.println(extra.PRE_BATTLE+"A lumberjack is chopping down a tree planted over a grave! Attack them?");
				//lumberjacks truly have no chill in Trawel
				break;
			case NOVEMBER:
				//truly sad business day has is rough being around cyber monday, black friday, and american thanksgiving
				//https://www.holidayinsights.com/moreholidays/november/small-business-saturday.htm
				extra.println(extra.PRE_BATTLE+"A lumberjack is fueling society's green! Attack them?");
				break;
			case OCTOBER:
				//movie
				extra.println(extra.PRE_BATTLE+"A person is chopping down a christmas tree?! Attack them?");
				break;
			case SEPTEMBER:
				//american enough to choose labor day in september, not american enough to choose 9/11 as my sad holiday over june and may's
				extra.println(extra.PRE_BATTLE+"A strikebreaker lumberjack is chopping down a tree without a permit! Attack them?");
				break;	
			}
		}else {
			extra.println(extra.PRE_BATTLE+"A lumberjack is chopping down a tree! Attack them?");
		}
		
		robber.getBag().graphicalDisplay(1, robber);
		if (extra.chanceIn(1, 3)) {
			robber.getBag().addDrawBaneSilently(DrawBane.ENT_CORE);
		}else {
			robber.getBag().addDrawBaneSilently(DrawBane.WOOD);
		}
		
		extra.print(extra.PRE_BATTLE);
		if (extra.yesNo()) {
		Combat c = Player.player.fightWith(robber);
		if (c.playerWon() > 0) {
			dryadQuest++;
			if (dryadQuest == 4) {
				extra.println("You feel a connection to the forest.");
				Player.addXp(tier);
				Player.player.addTitle("dryad of " + this.name);
			} 
		}}
	}
	
	private void findEquip() {
		extra.println("You find a rotting body... With their equipment intact!");
		AIClass.playerLoot(RaceFactory.makeLootBody(extra.clamp(tier-1, 1,Player.player.getPerson().getLevel())).getBag(),true);
	}
	
	private void abandonedHut() {
		extra.println("You find an abandoned hut. Enter?");
		if (extra.yesNo()) {
			switch (extra.randRange(2,3)) {
			case 1:
				extra.println("You feel yourself being jerked nowhere, some strange force is attempting to teleport you. Let it?");
				if (extra.yesNo()) {
					Player.player.setLocation(Player.player.getWorld().getRandom(tier,Player.player.getPerson().getLevel()+1));
					if (Player.player.getLocation() != town) {
						extra.println("The skyline outside of the forest changes... Checking your map, it looks like you've found a temporary liminal connection to "+Player.player.getLocation().getName()+".");
					}else {
						extra.println("You find yourself in a different part of the forest.");
					}
					
				}else {
					extra.println("You resist the unknown force.");
				}
				break;
			case 2: extra.println("There is a log in the hut");oldFighter();break;
			case 3: extra.println("There is a tree inside the hut."); lumerbjackDryad();break;
			}
		}else {
			extra.println("You move away from the hut.");
		}
	}
	
	private void dryForest() {
		extra.println("You don't find anything. You think you may have exhausted this forest, for now. Maybe come back later?");
	}
	

}
