package trawel.helper.methods;

import trawel.core.Print;
import trawel.core.mainGame;
import trawel.helper.constants.TrawelChar;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.people.Player;

public class PrintTutorial {

	public static void battleTutorial(boolean massFight) {
		String timeString = "ERROR";
		boolean delayOnly = false;
		switch (mainGame.attackDisplayStyle) {
		case CLASSIC:
		case SIMPLIFIED:
			delayOnly = true;
			timeString = TrawelChar.CHAR_INSTANTS+"delay";
			break;
		case TWO_LINE1:
		case TWO_LINE1_WITH_KEY:
			delayOnly = false;
			timeString = TrawelChar.CHAR_INSTANTS+"warmup and "+TrawelChar.CHAR_INSTANTS+" cooldown";
			break;
		}
		Print.println("Higher numbers are better, except in the case of "+timeString+".");
		if (!delayOnly) {
			Print.println("Delay on abilities is combined from a warmup and a cooldown. The first number is the warmup- how long until the attack goes through. The second number is the cooldown- how long after that until you can choose another attack.");
		}
		Print.println("Delay is how long an action takes- it determines turn order and skipping. It is measured in Instants. (Symbol: "+TrawelChar.CHAR_INSTANTS+")");
		Print.println("For example, two 30 delay actions would go through before one 100 delay action, and then still have 40 instants left for the next action.");
		Print.println(
				massFight ?
						TrawelChar.CHAR_SHARP+TrawelChar.CHAR_BLUNT+TrawelChar.CHAR_PIERCE+TrawelColor.PRE_WHITE+" stands for sharp blunt pierce- the three main damage types. Your opponents also have sbp-based armor. Yeah, you got a mass fight for your first battle. Good luck."
						:
							TrawelChar.CHAR_SHARP+TrawelChar.CHAR_BLUNT+TrawelChar.CHAR_PIERCE+TrawelColor.PRE_WHITE+" stands for sharp blunt pierce- the three main damage types. Your opponent also has sbp-based armor."
				);
		Print.println("Hitpoints only matter in combat- you steel yourself fully before each battle, restoring to your current maximum. Very few things can reduce this maximum, the most notable being the "+Effect.CURSE.getName()+" punishment effect.");
		switch (mainGame.attackDisplayStyle) {
		case CLASSIC:
			Print.println("You are using "+mainGame.attackDisplayStyle.name+"- and will show attacks as if they were plain tables.");
			Print.println("     name                hit    delay    sharp    blunt     pierce");
			Print.println("While in Classic, Warmup and Cooldown will be hidden, showing only the combined Delay, and it will only print 3 damage types at once.");
			break;
		case TWO_LINE1: 
			Print.println("You are using "+mainGame.attackDisplayStyle.name+"- and will see attacks in a hybrid table/label format.");
			Print.println("Instead of only table headers, each cell is labeled. "
					+TrawelChar.CHAR_HITMULT+" is hitmult. "
					+TrawelChar.CHAR_INSTANTS+" is 'instants' (warmup and cooldown time.) "
					+TrawelChar.CHAR_SHARP+TrawelColor.PRE_WHITE+" is sharp damage, "
					+TrawelChar.CHAR_BLUNT+TrawelColor.PRE_WHITE+" is blunt damage, and "
					+TrawelChar.CHAR_PIERCE+TrawelColor.PRE_WHITE+" is pierce damage.");
			Print.println("There are more damage types, such as elemental, but those are beyond the scope of this tutorial.");
			break;
		case TWO_LINE1_WITH_KEY:
			Print.println("You are using "+mainGame.attackDisplayStyle.name+"- it includes a key/legend, and will display attacks in a hybrid table/label format.");
			Print.println("This display style will provide instructions on how to read the table at the top of each instance.");
			break;
		case SIMPLIFIED:
			Print.println("You are using "+mainGame.attackDisplayStyle.name+"- it will show only the "+TrawelChar.CHAR_HITMULT+" hitmult, combined "+TrawelChar.CHAR_INSTANTS+" Delay, and combined "+TrawelChar.CHAR_DAMAGE + " damage.");
			break;
		}
		Print.println("Each attack usually comes with an added wound, which will be applied if the attack hits and doesn't get deflected by armor.");
		Print.println("Note that Hitmult isn't a flat percent to hit- when the attack happens, it is a multiplier on your 'hit roll'- just like the enemies' dodge. Whoever rolls the higher number wins. Thus, if your total hit mult equals their total dodge, you have a 50% chance to hit. Most enemies will have a dodge multiplier of less than one.");
	}
	
	public static void deathTutorial() {
		Print.println("When you die, your gear is safe from looting. However, you do grant XP on kill, so enemies will level up given enough experience, making them harder to beat.");
		if (!Player.isGameMode_NoPunishments()) {
			Print.println("After each death, you will gain an additional Punishment Effect, until you have them all. Each Punishment effect impairs your combat potential, and must be resolved at certain features. You can view your active Punishments in the Status screen, which will tell you how to resolve them.");
		}
		Print.println("If you were exploring a Node Feature, you will usually be kicked out of it on death, and your killer will remain hostile.");
		Print.println(TrawelColor.RESULT_WARN+"It is often (but not always, if they got a lucky shot in) better to go somewhere else and improve your combat capabilities than repeatedly challenge the same opponent.");
	}
	
	public static void featPickPointTutorial() {
		Print.println("Each time you level up, you get both a Feat Pick and a Feat Point.");
		Print.println("If you have both a Feat Pick and a Feat Point, you can select a new Feat or Archetype from the Character sub-menu.");
		Print.println("If you have multiple Feat Points, you can keep choosing options until you run out, for no additional Feat Picks.");
		Print.println("If you choose not to select any of the options presented, you save that Feat Point, but require a new Feat Pick to choose again.");
	}
}
