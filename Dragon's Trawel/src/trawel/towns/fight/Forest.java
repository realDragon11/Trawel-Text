package trawel.towns.fight;
import java.awt.Color;
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
import trawel.factions.Faction;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.QRMenuItem;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.services.Oracle;

public class Forest extends Feature{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tier;
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
		tutorialText = "Explore forests to progress in level.";
		color = Color.RED;
		background_area = "forest";
		background_variant = 1;
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
		if (Player.player.animalQuest == 0 && Math.random() > .8) {
			fallenTree();return;
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
			fallenTree();}else {hangedMan();}break;
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
		extra.println("You spot a bag of gold floating down a stream! Chase it?");
		Boolean result = extra.yesNo();
		if (result) {
			if (Math.random() > .5) {
				extra.println("A fighter runs up and calls you a thief before launching into battle!");
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),  RaceFactory.getMugger(tier));
				if (winner == Player.player.getPerson()) {
					int gold = (int) (tier*(30*Math.random()));
					extra.println("You pick up " + gold + " gold!");
					Player.bag.addGold(gold);
				}else {
					extra.println("They take the gold sack and leave you floating down the stream...");
				}
			}else {
				int gold = (int) (tier*(30*Math.random()));
				extra.println("You pick up " + gold + " gold!");
				Player.bag.addGold(gold);
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
			extra.println("When you wake up, you find that some of your gold is missing!");
			Player.bag.addGold(-53*tier);break;
			}
			if (Math.random() > .8) {
			extra.println("As you eat the mushroom, you hear a voice cry out:");
			extra.print(extra.PRE_RED);
			switch(extra.randRange(1,3)) {
			case 1: extra.println("\"You dare violate the forest?!\"");break;
			case 2: extra.println("\"Hey, I wanted that!\"");break;
			case 3: extra.println("\"You dirty plant-thief!\"");break;}
			mainGame.CombatTwo(Player.player.getPerson(), RaceFactory.getDryad(tier));
			}
			
			;break;
		case 3:
			extra.println("You pick up the mushroom to sell it.");
			if (Math.random() > .8) {
			extra.println("You hear someone cry out from behind you!");
			extra.print(extra.PRE_RED);
			switch(extra.randRange(1,3)) {
			case 1: extra.println("\"You dare violate the forest?!\"");break;
			case 2: extra.println("\"Hey, I wanted that!\"");break;
			case 3: extra.println("\"You dirty plant-thief!\"");break;
			}
			Person winner = mainGame.CombatTwo(Player.player.getPerson(), RaceFactory.getDryad(tier));
			if (winner == Player.player.getPerson()) {
				int gold = (int) (Math.random()*150*tier);
				extra.println("You sell the mushroom for " + gold + " gold.");
				Player.bag.addGold(gold);
			}
			}else {
				int gold = (int) (Math.random()*100*tier);
				extra.println("You sell the mushroom for " + gold + " gold.");
				Player.bag.addGold(gold);
			};break;
		case 4:
			extra.println("You crush the mushroom under your heel.");
			extra.println("You hear someone cry out from behind you!");
			extra.print(extra.PRE_RED);
			switch(extra.randRange(1,3)) {
			case 1: extra.println("\"You dare violate the forest?!\"");break;
			case 2: extra.println("\"Hey, I wanted that!\"");break;
			case 3: extra.println("\"You dirty plant-crusher!\"");break;
			}
			mainGame.CombatTwo(Player.player.getPerson(),  RaceFactory.getDryad(tier));break;
		}
		
	}
	
	private void mugger1() {
		extra.println("You see someone being robbed! Help?");
		Boolean help = extra.yesNo();
		Person robber = RaceFactory.getMugger(tier);
		robber.getBag().graphicalDisplay(1, robber);
		if (help) {
		Person winner = mainGame.CombatTwo(Player.player.getPerson(), robber);
	
		if (winner == Player.player.getPerson()) {
			int gold = (int) (Math.random()*130*tier);
			extra.println("They give you a reward of " + gold + " gold in thanks for saving them.");
			Player.bag.addGold(gold);
		}else {
			extra.println("They mugged you too!");
			Player.bag.addGold(-tier*134);
		}
		}else {
			extra.println("You walk away.");
		}
	}
	
	private void fallenTree() {
		
		extra.println("You come across a fallen tree. It is very pretty.");
		if (Player.player.animalQuest == 0) {
		extra.println("A "+Player.player.animalName()+" is sitting on it." );
		extra.println("It runs off.");
		extra.println();
		extra.println("You feel a sense of loss.");
		Player.player.animalQuest = 1;
		}
	}
	
	private void mugger2() {
		extra.print(extra.PRE_RED);
		extra.println("You see a mugger charge at you! Prepare for battle!");
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),  RaceFactory.getMugger(tier));
		if (winner == Player.player.getPerson()) {
		}else {
			extra.println("They take some of your gold!");
			Player.bag.addGold(-(int) (tier*300*Math.random()));
		}
	}
	
	private void hangedMan() {
		extra.println("You come across a man hanging from a tree.");
		extra.println("You sigh and move on.");
	}
	
	private void dryad() {
		Person robber = RaceFactory.getDryad(tier);
		robber.getBag().graphicalDisplay(1, robber);
		while (true) {
		extra.println("You come across a dryad tending to a tree.");
		extra.println("1 Leave");
		extra.print(extra.PRE_RED);
		extra.println("2 Attack them.");
		extra.println("3 Chat with them");
		switch (extra.inInt(3)) {
		default: case 1: extra.println("You leave the dryad alone");return;
		case 2: extra.println("You attack the dryad!");mainGame.CombatTwo(Player.player.getPerson(), robber);return;
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
		extra.println("You stumble upon a person stuck under a fallen tree. Help them?");
		if (extra.yesNo()) {
			extra.println("You move the tree off of them.");
			if (Math.random() > .9) {
				extra.print(extra.PRE_RED);
				extra.println("Suddenly, they attack you!");
				mainGame.CombatTwo(Player.player.getPerson(), RaceFactory.getMugger(tier));
			}else {
				if (Math.random() < .3) {
					extra.println("They scamper off...");
				}else {
					int gold = (int) (Math.random()*50*tier);
					extra.println("They give you a reward of " + gold + " gold in thanks for saving them.");
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,1,0);
					Player.bag.addGold(gold);
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
		extra.println("1 Leave");
		extra.print(extra.PRE_RED);
		extra.println("2 Attack them.");
		extra.println("3 Chat with them");
		switch (extra.inInt(3)) {
		default: case 1: extra.println("You leave the fighter alone");return;
		case 2: extra.println("You attack the fighter!");mainGame.CombatTwo(Player.player.getPerson(), robber);return;
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
	
	/*
	private boolean fairyCircle2() {
		extra.println("You find a fairy circle of mushrooms. Step in it?");
		if (extra.yesNo()) {
			extra.println("You step in it. You find yourseslf jerked nowhere. Your surroundings change...");
			Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()));
			return true;
		}else {
			extra.println("You stay away from the circle.");
			return false;
		}
	}*/
	
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
				extra.println("You feel the forest reward you! A sack of gold appears at your feet!");
				int gold = (int) (tier*(extra.randRange(30,50)));
				extra.println("You pick up " + gold + " gold!");
				Player.bag.addGold(gold);
				dryadQuest = 5;
				}
			}
		}else {
			extra.println("You stay away from the circle.");
		}
	}
	
	private void lumerbjackDryad() {
		//if (extra.chanceIn(1,2) ) {
			LocalDateTime t = LocalDateTime.now();
			if (t.getMonth() == Month.DECEMBER && t.getDayOfMonth() > 14 && t.getDayOfMonth() < 25 ) {
				extra.print(extra.PRE_RED);
				extra.println("A person is chopping down a christmas tree! Attack them?");
			}else {
				extra.print(extra.PRE_RED);
				extra.println("A lumberjack is chopping down a tree! Attack them?");
			}
		/*}else {
			
		}*/
			Person robber = RaceFactory.getLumberjack(tier);
			robber.getBag().graphicalDisplay(1, robber);
			if (extra.chanceIn(1, 3)) {
			robber.getBag().getDrawBanes().add(DrawBane.ENT_CORE);}
		
			extra.print(extra.PRE_RED);
		if (extra.yesNo()) {
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),robber);
		if (winner == Player.player.getPerson()) {
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
		AIClass.loot(new Person(tier).getBag(),Player.bag,Player.player.getPerson().getIntellect(),true);
	}
	
	private void abandonedHut() {
		extra.println("You find an abandoned hut. Enter?");
		if (extra.yesNo()) {
			switch (extra.randRange(1,2)) {
			case 1: extra.println("You step in it. You find yourseslf jerked nowhere. Your surroundings change...");
			Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()));break;
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
