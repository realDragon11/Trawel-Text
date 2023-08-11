package trawel;

import trawel.personal.Person;
import trawel.personal.classless.Archetype;
import trawel.personal.people.Player;
import trawel.towns.Feature;
import trawel.towns.fight.Arena;
import trawel.towns.services.Inn;

public class StoryTutorial extends Story{

	public Person killed;
	public int deaths = 0;
	public String step;
	
	@Override
	public void setPerson(Person p, int index) {
		killed = p;
	}
	
	@Override
	public void storyStart() {
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
		if (step == "anyfight1") {
			extra.println("Oh jeez, it's been a while since you've fought a stranger, you feel weird.");
			extra.println("A mysterious voice is telling you to \"Choose your attack below\"???");
			extra.println("...");
			extra.println("Higher numbers are better, except in the case of delay!");
			extra.println("Delay is how long an action takes- it determines turn order and skipping.");
			extra.println("For example, two 30 delay actions would go through before one 100 delay action, and then still have 40 instants left.");
			extra.println("Delay on abilities is shown as both a warmup and a cooldown. The first number is the warmup- how long until the attack goes through. The second number is the cooldown- how long after that until you can choose another attack.");
			extra.println(
					massFight ?
					"sbp stands for sharp blunt pierce- the three main damage types. Your opponents also have sbp-based armor. Yeah, you got a mass fight for your first battle. Good luck."
					:
					"sbp stands for sharp blunt pierce- the three main damage types. Your opponent also has sbp-based armor."
					);
			extra.println("Hitpoints only matter in combat- you steel yourself fully before each battle, restoring to your current maximum.");
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
						+extra.CHAR_HITCHANCE+" is hitchance. "
						+extra.CHAR_INSTANTS+" is 'instants' (warmup and cooldown time.) "
						+ "S is sharp damage, B is blunt damage, and P is pierce damage.");
				//will have to describe esoteric attacks when those get added
				break;
			}
			extra.println("Displayed hit isn't a flat percent to hit- when the attack happens, it is a multiplier on your 'hit roll'- just like the enemies' dodge. Whoever rolls the higher number wins. Thus, if your total aim equals their total dodge, you have a 50% chance to hit. Most enemies will have a dodge multiplier of less than one.");
			extra.println("Attacks might also come with wounds, which have special effects. Good luck!");
		}
	}
	
	@Override
	public void winFight(boolean massFight) {
		if (step == "anyfight1") {
			extra.println("Congratulations! You killed something. Now you should ingest questionable substances at an inn. Compass (which is basically mapquest), in the 'You' menu, will take you to 'Unun', a town with an inn.");
			step = "gotoinn1";
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
		case 5:
			extra.println("If you're having trouble, it's often best to come back with better gear and more levels than to challenge-spam someone. At least, for your own time investment.");
		;break;
		default: extra.println("Rest in pieces.");break;
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
			if (! (f instanceof Arena)) {
				return;
			}
			extra.println("It looks like there's a fight about to take place here. You could wait to participate in it. Winner gets the loser's stuff, apparently.");
			step = "anyfight1";
			break;
		case "gotoinn1":
			if (! (f instanceof Inn)) {
				return;
			}
			extra.println("The inn has 'beer' (you hope its actually beer) which can raise your HP for as many fights as you buy beer for... but somewhat more importantly, random side quests.");
			extra.println("Browse the backrooms, and see if any quests suit your fancy. In general, its much more fun to explore, but sidequests can help you if you're having trouble justifying going into the scary wider world.");
			extra.println("There's no main quest in this version of Trawel, so good luck. If you make it to >10 level, you've essentially beaten the game.");
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
			}
		}
	}
	
}
