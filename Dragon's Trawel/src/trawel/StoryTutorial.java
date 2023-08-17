package trawel;

import java.util.EnumSet;

import trawel.personal.Person;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.Perk;
import trawel.personal.people.Player;
import trawel.towns.Feature;
import trawel.towns.fight.Arena;
import trawel.towns.nodes.NodeFeature;
import trawel.towns.services.Inn;

public class StoryTutorial extends Story{

	private Person killed;
	private int deaths = 0;
	private int wins = 0;
	private int combats = 0;
	private int levelReminders = 0;
	private String step;
	
	private int battleFam;//0 none, 1 fought but didn't win, 2 won a fight
	
	private EnumSet<Perk> bossPerkTriggers = EnumSet.of(Perk.HELL_BARONESS,Perk.FATED);
	private EnumSet<Perk> worldPerkTriggers = EnumSet.of(Perk.MINE_ALL_VEINS,Perk.CULT_LEADER_BLOOD);
	
	@Override
	public void setPerson(Person p, int index) {
		killed = p;
	}
	
	@Override
	public void storyStart() {
		battleFam = 0;
		extra.println("You are now " + Player.player.getPerson().getName() +".");
		extra.println("You come to your senses. Your student, " + killed.getName() + " is dead.");
		extra.println("A wizard cast a curse on them, sending anyone who saw them into a blinding rage. But you are not where you were when you were afflicted...");
		extra.println("You resolve to find out where you are, to start your life anew- it's not like anyone would believe you back home in Oblask, anyway.");
		extra.println("1 start Trawel");
		extra.inInt(1);
		for (Archetype a: Player.player.getPerson().getArchSet()) {
			extra.println("Starting Archetype: " +a.getOwnText());
		}
		extra.println();
		extra.println("You should head to the local arena. Your immortality will come in handy there.");
		step = "gotoarena1";
	}
	
	@Override
	public void startFight(boolean massFight) {
		if (battleFam < 2) {
			boolean disp = battleFam == 0;
			if (disp) {
				extra.println("Oh jeez, it's been a while since you've fought a stranger, you feel weird.");
				extra.println("A mysterious voice is telling you to \"Choose your attack below\"???");
				extra.println("...");
			}
			if (!disp && offerCombatTutorial()) {
				extra.println("Display the combat tutorial again?");
				disp = extra.yesNo();
			}
			if (disp) {
				extra.println("Higher numbers are better, except in the case of delay.");
				extra.println("Delay is how long an action takes- it determines turn order and skipping.");
				extra.println("For example, two 30 delay actions would go through before one 100 delay action, and then still have 40 instants left.");
				extra.println("Delay on abilities is combined from a warmup and a cooldown. The first number is the warmup- how long until the attack goes through. The second number is the cooldown- how long after that until you can choose another attack.");
				extra.println(
						massFight ?
								extra.CHAR_SHARP+extra.CHAR_BLUNT+extra.CHAR_PIERCE+" stands for sharp blunt pierce- the three main damage types. Your opponents also have sbp-based armor. Yeah, you got a mass fight for your first battle. Good luck."
								:
									extra.CHAR_SHARP+extra.CHAR_BLUNT+extra.CHAR_PIERCE+" stands for sharp blunt pierce- the three main damage types. Your opponent also has sbp-based armor."
						);
				extra.println("Hitpoints only matter in combat- you steel yourself fully before each battle, restoring to your current maximum. Very few things can reduce this maximum, the most notable being the CURSE status effect.");
				switch (mainGame.attackDisplayStyle) {
				case CLASSIC:
					extra.println("You have classic display mode on, and will show attacks as if they were plain tables.");
					extra.println("     name                hit    delay    sharp    blunt     pierce");
					extra.println("Classic mode doesn't show you cooldown and warmup- just the combined delay.");
					break;
				case TWO_LINE1: 
					extra.println("You have modern display on (at least, the current modern for this verison),"
							+" and will see attacks in a hybrid table/label format.");
					extra.println("Instead of only table headers, each cell is labeled. "
							+extra.CHAR_HITCHANCE+" is hitmult. "
							+extra.CHAR_INSTANTS+" is 'instants' (warmup and cooldown time.) "
							+ extra.CHAR_SHARP+" is sharp damage, "
							+extra.CHAR_BLUNT+" is blunt damage, and "
							+extra.CHAR_PIERCE+" is pierce damage.");
					extra.println("There are more damage types, such as elemental, but those are beyond the scope of this tutorial.");
					break;
				case TWO_LINE1_WITH_KEY:
					extra.println("You have modern display on with a key/legend (at least, the current modern for this verison),"
							+" and will see attacks in a hybrid table/label format.");
					extra.println("This display style will provide instructions on how to read the table at the top of each instance.");
					break;
				}
				extra.println("Displayed hit mult isn't a flat percent to hit- when the attack happens, it is a multiplier on your 'hit roll'- just like the enemies' dodge. Whoever rolls the higher number wins. Thus, if your total hit mult equals their total dodge, you have a 50% chance to hit. Most enemies will have a dodge multiplier of less than one.");
				extra.println("Attacks might also come with wounds, which have special effects. Good luck!");
				battleFam = 1;
			}
		}
		combats++;
	}
	
	@Override
	public void winFight(boolean massFight) {
		wins++;
		if (step == "gotoinn1") {
			return;
		}
		if (step == "anyfight1") {
			extra.print("Congratulations! You killed something.");
			step = "gotoinn1";
		}
		if (step == "gotoarena1") {
			extra.print("Well, looks like you managed to kill something without having gone to an arena. Arenas are fairly easy, but as you've learned, there's a lot of Combat to be had in Trawel!");
		}
		if (step == "gotoinn1") {
			extra.println(" Next you should ingest questionable substances at an inn. Compass (which is basically mapquest), in the 'You' menu, can take you to 'Unun', a town with an inn.");
		}
	}


	@Override
	public void onDeath() {
		deaths++;
		switch (deaths) {
		case 1:
			extra.println("Welcome to your first death! You continue as normal. If you're in an exploration area, you got kicked out of it, otherwise not much changed... except maybe the thing that killed you leveled up.");
			Networking.unlockAchievement("die1");
		;break;
		default:
			boolean doReminder = false;
			if (levelReminders <= 2) {
				if (deaths == 5 && levelReminders == 0) {
					doReminder = true;
				}else {
					if (extra.chanceIn(deaths,wins+deaths+combats)) {//you can die outside of combat
						doReminder = true;
					}
				}
			}
			if (!doReminder) {
				extra.println("Rest in pieces.");
				break;
			}

			extra.println("If you're having trouble, it's often best to come back with better gear and more levels than to challenge-spam someone. At least, for your own time investment.");
			levelReminders++;
			break;
		}
		
	}
	
	@Override
	public void onDeathPart2() {
		if (step == "anyfight1") {
			extra.println("You should keep trying to win a fight.");
		}
		switch (deaths) {
		default:
			;break;
		}
		
	}
	
	@Override
	public void enterFeature(Feature f) {
		switch(step) {
		case "gotoarena1":
			if (!(f instanceof Arena)) {
				return;
			}
			extra.println("It looks like there's a fight about to take place here. You could wait to participate in it. Winner gets the loser's stuff, apparently.");
			step = "anyfight1";
			break;
		case "gotoinn1":
			if (!(f instanceof Inn)) {
				return;
			}
			extra.println("The inn has 'beer' (you hope its actually beer) which can raise your HP for as many fights as you buy beer for... but somewhat more importantly, random side quests.");
			extra.println("Browse the backrooms, and see if any quests suit your fancy. In general, its much more fun to explore, but sidequests can help you if you're having trouble justifying going into the scary wider world.");
			extra.println("Merchant Guilds, Witch Huts, and a few other locations can also provide similar sidequests- and even more can be quest locations, like mountains and forests.");
			extra.println("There are also areas meant for sub-exploration, such as Groves, Mines, Dungeons, and Graveyards. The Tower of Fate in Unun and the Staircase to Hell in another world entirely also have bosses. Try to enter one such feature next.");
			step = "gotonode1";
			break;
		case "gotonode1":
			if (!(f instanceof NodeFeature)) {
				return;
			}
			extra.println("These areas have a variable number of nodes, seen below. Each node has a link to other nodes, and the ability to 'interact' with it. Nodes can be {"
			+extra.VISIT_NEW+"} unseen, {"+extra.VISIT_SEEN+"}seen, {"+extra.VISIT_BEEN + "}been, {" +extra.VISIT_DONE + "}done, "
					+extra.VISIT_OWN + "owned (usually used to indicate you've done an action that will change with time), "
					+extra.PRE_WHITE+"and {"+extra.VISIT_REGROWN+ "}regrown,"+extra.PRE_WHITE+" which means that they got replaced since you last visited them.");
			extra.println("The order of nodes presented is often erratic, but the last node you were in will be marked by '(back)'. Some areas will also place nodes that are 'deeper' or 'higher' on the top. One such instance is the Tower of Fate in Unun, which loops back in on itself, but picking the highest choice will always take to up the tower until you reach the top floor.");
			extra.println("While interacting, you might find yourself in a sub-menu, otherwise you can always leave the area by selecting the last option.");
			extra.println("You have completed the tutorial section of this story. If you make it to >10 level, you've essentially beaten the game. Those bosses mentioned earlier and some other world events will be tracked by this tutorial- but there's no main quest in this version of Trawel, so good luck.");
			step = "end";
			break;
		default: break;
		}
		
	}
	@Override
	public void levelUp(int level) {
		while (lastKnownLevel < level) {
			lastKnownLevel++;
			switch(lastKnownLevel) {
			case 1:
				extra.println("You have leveled up! You can spend skillpoints in this menu. You likely will want to select an 'Exotic Art' and then back out of that selection screen, to spend the skillpoint in the art you just unlocked.");
				break;
			case 10:
				extra.println("You're very high level! You can probably beat 80% of current Trawel content. If you're really ambitious, try to get to level 15.");
				break;
			case 15:
				extra.println("You're so high level you could slay a dragon! ...there are no dragons :(");
				break;
			case 20:
				extra.println("A winner is you?");
				break;
			case 25:
				extra.println("A master is you?");
				break;
			}
		}
	}
	
	@Override
	public void perkTrigger(Perk perk) {
		if (bossPerkTriggers.remove(perk)) {
			switch (perk) {
			case FATED:
				if (bossPerkTriggers.contains(Perk.HELL_BARONESS)) {
					extra.println("You've slain the Fatespinner and gotten their Fated perk... but can you Beat the Baron? Travel to the world of Greap through the teleporter in Repa, then seek out the Staircase to Hell.");
				}else {
					extra.println("You've slain the Fatespinner and gained the Fated perk!");
				}
				break;
			case HELL_BARONESS:
				extra.println("You've slain the Hell Baron and gained their throne perk!");
				break;
			}
			if (bossPerkTriggers.isEmpty()) {
				extra.println("You've obtained all tracked boss perks in this version of Trawel!");
			}
		}
		if (worldPerkTriggers.remove(perk)) {
			switch (perk) {
			default: extra.println("You gained the " +perk.friendlyName() + " world perk!");
			break;
			}
			if (worldPerkTriggers.isEmpty()) {
				extra.println("You've obtained all tracked world perks in this version of Trawel!");
			}
		}
	}
	
	private boolean offerCombatTutorial() {
		return combats < 3 || battleFam < 2;
	}
	
}
