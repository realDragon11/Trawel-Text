package trawel.towns.features.fight;
import java.time.LocalDateTime;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.battle.Combat;
import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.factions.Faction;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.quests.types.Quest.TriggerType;
import trawel.towns.contexts.World;
import trawel.personal.people.Player;

public class Forest extends ExploreFeature{
	
	private static final long serialVersionUID = 1L;
	private int dryadQuest = 0;
	private static WeightedTable roller;
	
	@Override
	public QRType getQRType() {
		return QRType.FOREST;
	}
	
	public Forest(String name, int tier) {
		this.tier = tier;
		this.name = name;
		background_area = "forest";
		background_variant = 1;
		area_type = Area.FOREST;
	}
	@Override
	public String getTutorialText() {
		return "Forest";
	}
	@Override
	public String nameOfType() {
		return "forest";
	}
	
	@Override
	public void onExhaust() {
		Print.println("You don't find anything. You think you may have exhausted this forest, for now. Maybe come back later?");
	}
	
	@Override
	public void onNoGo() {
		Print.println("The forest is barren.");
	}
	
	@Override
	public void subExplore(int id) {
		//dryad quest overrides normal title chance
		if (explores == 10 && dryadQuest < 4) {
			Player.player.addAchieve(this, this.getName() + " wanderer");
		}
		if (explores == 50 && dryadQuest < 4) {
			Player.player.addAchieve(this, this.getName() + " explorer");
		}
		if (explores == 100 && dryadQuest < 4) {
			Player.player.addAchieve(this, this.getName() + " guide");
		}
		if (dryadQuest > 0 && dryadQuest < 4 && Rand.chanceIn(1,3)) {
			lumerbjackDryad();
			return;
		}
		if ((dryadQuest == 0 || dryadQuest == 4) && Rand.chanceIn(1,6)) {
			id = 10;
		}
		switch (id) {
		case 1:
		risky_gold(
				"You spot a bag of "+World.currentMoneyString()+" floating down a stream! Chase it?",
				"They take the "+World.currentMoneyString()+" sack and leave you floating down the stream...",
				"You let the sack float away..."
				);
		break;
		case 2: funkyMushroom();break;
		case 3: mugger_other_person();break;
		case 4: treeOnPerson();break;
		case 5: hangedMan();break;
		case 6: mugger_ambush();break;
		case 7: dryad();break;
		case 8: oldFighter("a log"," Beware, danger lurks under these trees.",this);break;
		case 9: fairyCircle1();break;
		case 10: fairyCircle3();break;
		case 11: findEquip("");break;
		case 12: abandonedHut();break;
		}
	}
	
	@Override
	protected WeightedTable roller() {
		if (roller == null) {
			roller = new WeightedTable(new float[] {
					//gold in stream
					1f,
					//funky mushroom
					1f,
					//mugging interrupt
					1f,
					//fallen tree
					1f,
					//fell reaver hanged tree
					1f,
					//mugger attack
					1f,
					//dryad
					1f,
					//old fighter
					1f,
					//inert fairy circle
					.5f,
					//quest fairy circle, has a flat chance if quest not started every time
					.5f,
					//wolf body
					1f,
					//hut
					1f
			});
		}
		return roller;
	}
	
	private void funkyMushroom() {
		Print.println("You spot a glowing mushroom on the forest floor.");
		Print.println("1 eat it");
		Print.println("2 sell it");
		Print.println("3 crush it");
		Print.println("9 leave it");
		int in =  Input.inInt(4,true,true);
		switch (in) {
		default: Print.println("You decide to leave it alone.");break;
		case 1:
			Print.println("You eat the mushroom...");
			switch(Rand.randRange(1,3)) {
			case 1: Print.println("The mushroom is delicious!");break;
			case 2: Print.println("Eating the mushroom is very difficult... but you manage.");
			Player.player.getPerson().addXp(getTempLevel()*2);break;
			case 3: Print.println("You feel lightheaded.... you pass out!");
			Print.println("When you wake up, you notice someone went through your bags!");
			Print.println(Player.loseGold(IEffectiveLevel.cleanRangeReward(getTempLevel(),10f,.2f),true));
			break;
			}
			if (Math.random() > .8) {
				Print.println("As you eat the mushroom, you hear a voice cry out:");
				Print.print(TrawelColor.PRE_BATTLE);
				switch(Rand.randRange(1,3)) {
				case 1: 
					Print.println("\"You dare violate the forest?!\"");
					Player.player.fightWith(RaceFactory.makeDryad(getTempLevel()));
					break;
				case 2:
					Print.println("\"Hey, I wanted that!\"");
					Player.player.fightWith(RaceFactory.makeCollector(getTempLevel()));
					break;
				case 3:
					Print.println("\"You dirty plant-thief!\"");
					Player.player.fightWith(RaceFactory.makeLawman(getTempLevel()));
					break;
				}
			
			}
			
			;break;
		case 2:
			Print.println("You pick up the mushroom to sell it.");
			if (Math.random() > .3) {
			Print.println("You hear someone cry out from behind you!");
			Print.print(TrawelColor.PRE_BATTLE);
			Combat c;
			switch(Rand.randRange(1,3)) {
			default:
				Print.println("\"You dare violate the forest?!\"");
				c = Player.player.fightWith(RaceFactory.makeDryad(getTempLevel()));
				break;
			case 2:
				Print.println("\"Hey, I wanted that!\"");
				c = Player.player.fightWith(RaceFactory.makeCollector(getTempLevel()));
				break;
			case 3:
				Print.println("\"You dirty plant-thief!\"");
				c = Player.player.fightWith(RaceFactory.makeLawman(getTempLevel()));
				break;
			}
			if (c.playerWon() > 0) {
				int gold = IEffectiveLevel.cleanRangeReward(getTempLevel(),3f,.7f);
				Print.println("You sell the mushroom for " +World.currentMoneyDisplay(gold) + ".");
				Player.player.addGold(gold);
			}
			}else {
				int gold = IEffectiveLevel.cleanRangeReward(getTempLevel(),2f,.2f);
				Print.println("You sell the mushroom for " +World.currentMoneyDisplay(gold) + ".");
				Player.player.addGold(gold);
			};break;
		case 3:
			Print.println("You crush the mushroom under your heel.");
			Print.println("You hear someone cry out from behind you!");
			Print.print(TrawelColor.PRE_BATTLE);
			switch(Rand.randRange(1,3)) {
			case 1: 
				Print.println("\"You dare violate the forest?!\"");
				Player.player.fightWith(RaceFactory.makeDryad(getTempLevel()+1));
				break;
			case 2:
				Print.println("\"Hey, I wanted that!\"");
				Player.player.fightWith(RaceFactory.makeCollector(getTempLevel()+1));
				break;
			case 3:
				Print.println("\"You dirty plant-crusher!\"");
				Player.player.fightWith(RaceFactory.makeLawman(getTempLevel()+1));
				break;
			}
		}
		
	}
	
	private void hangedMan() {
		Print.println("You come across a man hanging from a tree.");
		switch (Rand.randRange(0,2)) {
		case 0:Print.println("You sigh and move on.");
			break;
		case 1: Print.println("There's something off about the corpse... You feel like you need to leave, so you do.");
			break;
		case 2:
			Print.println(TrawelColor.PRE_BATTLE+ "Something fell and horrible steps out of the hanged man's shadow!");
			Person reaver = RaceFactory.makeFellReaver(getTempLevel());
			Combat c = Player.player.fightWith(reaver);
			if (c.playerWon() > 0) {
				//not a collector, but this is a dd1 quote ref
				Print.println("They say a predator is often blind to its own peril- at least there won't be any more men hanged here soon.");
				//bonus heroism
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC, IEffectiveLevel.unclean(getTempLevel()),0);
			}else {
				//terraria ref, unsure if references like this are a bit much
				Print.println("You wake up elsewhere, striken with nightmares of claw, teeth, sinew, and bone. You feel an evil presence watching you...");
				town.getIsland().getWorld().addReoccuring(new Agent(reaver,AgentGoal.SPOOKY));
			}
			break;
		}
		
	}
	
	private void dryad() {
		Person robber = RaceFactory.makeDryad(getTempLevel());
		robber.getBag().graphicalDisplay(1, robber);
		while (true) {
		Print.println("You come across a dryad tending to a tree.");
		Print.println("1 Leave");//DOLATER: fix menu
		Print.println(TrawelColor.PRE_BATTLE+"2 Attack them.");
		Print.println("3 Chat with them");
		switch (Input.inInt(3)) {
		default: case 1: Print.println("You leave the dryad alone");return;
		case 2: 
			Print.println("You attack the dryad!");
			Player.player.fightWith(robber);
		return;
		case 3: Print.println("The dryad turns and answers your greeting.");
		while (true) {
		Print.println("What would you like to ask about?");
		Print.println("1 tell them goodbye");
		Print.println("2 their tree");
		Print.println("3 this forest");
		int in = Input.inInt(3);
		switch (in) {
			case 1: Print.println("They wish you well.") ;break;
			case 2: Print.println("They start describing their tree in intricate detail before finishing.");
			Print.println("They seem very passionate about it.");break;
			case 3: Print.println("\"We are in " + this.getName() + ". I don't venture away from my tree.\"");break;
		}
		if (in == 1) {
			break;
		}
		}
		}
	}}
	
	private void treeOnPerson() {
		Person p = RaceFactory.makeMugger(getTempLevel());
		p.getBag().graphicalDisplay(1, p);
		Print.println("You stumble upon a person stuck under a fallen tree. Help them?");
		if (Input.yesNo()) {
			Print.println("You move the tree off of them.");
			if (Math.random() > .9) {
				Print.println(TrawelColor.PRE_BATTLE+"Suddenly, they attack you!");
				Combat c = Player.player.fightWith(p);
				if (c.playerWon() > 0) {
				}
			}else {
				if (Math.random() < .3) {
					Print.println("They scamper off...");
				}else {
					int gold = IEffectiveLevel.cleanRangeReward(getTempLevel(),2f,.7f);
					Print.println("They give you a reward of " + World.currentMoneyDisplay(gold) + " in thanks for saving them.");
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,1*IEffectiveLevel.unclean(getTempLevel()),0);
					Player.player.addGold(gold);
				}
			}
		}else {
			Print.println("You leave them alone to rot...");
		}
	}

	public int getExplores() {
		return explores;
	}

	public void setExplores(int explores) {
		this.explores = explores;
	}
	
	private void fairyCircle1() {
		Print.println("You find a fairy circle of mushrooms. Step in it?");
		if (Input.yesNo()) {
			Print.println("You step in it. Nothing happens.");
		}else {
			Print.println("You stay away from the circle.");
		}
	}
	
	private void fairyCircle3() {
		Print.println("You find a fairy circle of mushrooms. Step in it?");
		if (Input.yesNo()) {
			Print.println("You step in it...");
			if (dryadQuest == 0) {
			Print.println("A squirrel asks if you want to be a dryad.");
			if (Input.yesNo()) {
				Print.println("You are told to kill lumberjacks damaging the forest.");
				dryadQuest = 1;
			}
			}else {
				if (dryadQuest == 4) {
				Print.println("You feel the forest reward you! Whirlwinds of aether appear at your feet!");
				int a_reward = Math.round(getUnEffectiveLevel()*Rand.randRange(400f,500f));
				Print.println("You gain " + a_reward + " aether!");
				Player.bag.addAether(a_reward);
				dryadQuest = 5;
				}
			}
		}else {
			Print.println("You stay away from the circle.");
		}
	}
	
	private void lumerbjackDryad() {
		Person robber = RaceFactory.makeLumberjack(getTempLevel());
		if (Rand.chanceIn(1,3)) {//TODO: make this much rarer and use ingame holidays/months instead
			LocalDateTime t = LocalDateTime.now();
			switch (t.getMonth()) {//lmao alphabetically ordered months
			//enums are ordinal ordered, why does eclipse do this
			case APRIL:
				//idk how to describe medieval april fools day
				Print.println(TrawelColor.PRE_BATTLE+"A person is chopping down tree covered in mud! Attack them?");
				break;
			case AUGUST:
				//trawel v0.8 haha
				Print.println(TrawelColor.PRE_BATTLE+"A person is chopping down a tree with the symbols 'v.8' carved into it! Attack them?");
				break;
			case DECEMBER:
				//t.getDayOfMonth() > 14 && t.getDayOfMonth() < 25
				Print.println(TrawelColor.PRE_BATTLE+"A person is chopping down a christmas tree! Attack them?");
				break;
			case FEBRUARY:
				//valentines day
				Print.println(TrawelColor.PRE_BATTLE+"A lumberjack is chopping down a tree with many hearts and initials carved into it! Attack them?");
				break;
			case JANUARY:
				Print.println(TrawelColor.PRE_BATTLE+"A wannabe lumberjack has resolved to cut down more trees this year! Attack them?");
				break;
			case JULY:
				//canada's independence day isn't pog enough to celebrate, so we celebrate
				//https://www.holidayinsights.com/moreholidays/july/iforgotday.htm
				Print.println("What were you doing again? There's a lumberjack here, should you attack them?");
				break;
			case JUNE:
				//Emancipation Day, D day
				robber.setRacism(true);//assigned racist at tree
				Print.println(TrawelColor.PRE_BATTLE+"A racist is chopping down a tree. Attack them?");
				break;
			case MARCH:
				//daylight savings, trawel doesn't have that lmao
				//trawel has perfect years, but ironically it DOES have different rise and set times
				Print.println(TrawelColor.PRE_BATTLE+"A lumberjack is chopping down a tree, apparently they need more paper for calender... clocks? You feel kinda bad, should you attack them?");
				break;
			case MAY:
				//lot of war days, cinco de mayo included
				Print.println(TrawelColor.PRE_BATTLE+"A lumberjack is chopping down a tree planted over a grave! Attack them?");
				//lumberjacks truly have no chill in Trawel
				break;
			case NOVEMBER:
				//truly sad business day has is rough being around cyber monday, black friday, and american thanksgiving
				//https://www.holidayinsights.com/moreholidays/november/small-business-saturday.htm
				//https://en.wikipedia.org/wiki/Lumber_Cartel
				//https://en.wikipedia.org/wiki/Timber_mafia
				Print.println(TrawelColor.PRE_BATTLE+"An agent of the timber mafia is trying to strongarm someone into the lumber cartel! Attack them?");
				break;
			case OCTOBER:
				//movie
				Print.println(TrawelColor.PRE_BATTLE+"A person is chopping down a christmas tree?! Attack them?");
				break;
			case SEPTEMBER:
				//american enough to choose labor day in september, not american enough to choose 9/11 as my sad holiday over june and may's
				Print.println(TrawelColor.PRE_BATTLE+"A strikebreaker lumberjack is chopping down a tree without a permit! Attack them?");
				break;	
			}
		}else {
			Print.println(TrawelColor.PRE_BATTLE+"A lumberjack is chopping down a tree! Attack them?");
		}
		
		robber.getBag().graphicalDisplay(1, robber);
		if (Rand.chanceIn(1, 3)) {
			robber.getBag().addDrawBaneSilently(DrawBane.ENT_CORE);
		}else {
			robber.getBag().addDrawBaneSilently(DrawBane.WOOD);
		}
		
		Print.print(TrawelColor.PRE_BATTLE);
		if (Input.yesNo()) {
			Combat c = Player.player.fightWith(robber);
			//quest need not be started normally
			//just not completed or in a negative state
			if (c.playerWon() > 0 && dryadQuest >= 0 && dryadQuest < 4) {
				dryadQuest++;
				if (dryadQuest == 4) {
					Print.println("You feel a connection to the forest.");
					Player.addXp(tier);
					Player.player.addAchieve(this, "Dryad of " + getName());
				}
			}
		}
	}
	
	private void abandonedHut() {
		Print.println("You find an abandoned hut. Enter?");
		if (Input.yesNo()) {
			switch (Rand.randRange(2,3)) {
			case 1:
				//'jerked nowhere' is doomRL
				Print.println("You feel yourself being jerked nowhere, some strange force is attempting to teleport you. Let it?");
				if (Input.yesNo()) {
					Player.player.setLocation(Player.player.getWorld().getRandom(tier,Player.player.getPerson().getLevel()+1));
					if (Player.player.getLocation() != town) {
						Print.println("The skyline outside of the forest changes... Checking your map, it looks like you've found a temporary liminal connection to "+Player.player.getLocation().getName()+".");
					}else {
						Print.println("You find yourself in a different part of the forest.");
					}
				}else {
					Print.println("You resist the unknown force.");
				}
				break;
			case 2:
				oldFighter("a bench","Beware, some of these cabins are cursed.",this);
				break;
			case 3:
				Print.println("There is a tree inside the hut.");
				lumerbjackDryad();
			break;
			}
		}else {
			Print.println("You move away from the hut.");
		}
	}
	

}
