package trawel.arc.story;

import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.personal.Person;
import trawel.personal.people.Player;
import trawel.towns.features.Feature;
import trawel.towns.features.Feature.QRType;

public class StoryDeathWalker extends Story{

	public Person killed;
	public int deaths = 0;
	public String step;
	
	
	@Override
	public void setPerson(Person p, int index) {
		killed = p;
	}
	
	@Override
	public void storyStart() {
		Print.println("You did what you had to.");
		Print.println("At least, that's what you told yourself, when you struck them down.");
		Print.println(killed.getName() + " didn't have it coming.");
		Print.println("They were a nice kid.");
		Print.println("1 sigh");
		Input.inInt(1);
		Print.println("They were a young " + killed.getBag().getRace().renderName(false) +", always eager to please.");
		Print.println("Damn that wizard!");
		
		Print.println("...");
		Print.println("But there's no sense dwelling in the past. You've managed to flee to Homa, to start your life anew.");
		Print.println("You don't have much... you should probably explore the forest or fight in the arena to gain some money before moving on to a town with an inn.");
		Print.println("1 look around");
		Input.inInt(1);
		step = "gotoinn1";
	}


	@Override
	public void onDeath() {
		deaths++;
		switch (deaths) {
		case 1:
		Print.println("No! You can't die here!");
		Print.println("You didn't escape from Oblask to just die in some random village!");
		Print.println("You can see " +killed.getName() +"'s face clearly now..." );
		Print.println("1 wake up");
		Input.inInt(1);
		Networking.unlockAchievement("die1");
		;break;
		case 2: Print.println("Once again, your body hits the floor...");
		Print.println("You've wasted your second chance...");
		Print.println("You can see " +killed.getName() +"'s face again..." );
		Print.println("1 wake up");
		Input.inInt(1);
		;break;
		default: Print.println("You see glimpses of " +killed.getName()+ "'s face.");break;
		}
		
	}
	
	@Override
	public void onDeathPart2() {
		switch (deaths) {
		case 1:
			Print.println("What?!");
			Print.println("You're alive?!");
			Print.println("But how? Why?!");
			Print.println("You guess you should be thankful...");
			Print.println("Dusting off your "+Player.bag.getArmorSlot(Rand.randRange(0, 4)).getName() + " you stand up.");
			Print.println("Well, best get moving, at least. Figure out what happened later.");
			Print.println("1 look around");
			Input.inInt(1);
		;break;
		case 2: Print.println("It happened again!");
		Print.println("You saw their face again too!");
		Print.println("You must be immortal now... but... you don't understand!");
		Print.println("1 look around");
		Input.inInt(1);
		;break;
		default:
			;break;
		}
		
	}
	
	@Override
	public void enterFeature(Feature f) {
		if (f.getQRType() != QRType.INN) {
			return;
		}
		switch(step) {
		case "gotoinn1":
			Print.println("As you enter the inn you can hear the clashing of mugs.");
			Print.println("You walk up to a table and listen in, trying to see if there's any word from Oblask.");
			Print.println("After some time, you hear the name of the town drop.");
			Print.println("1 listen in");
			Input.inInt(1);
			Print.println("\"Hear some kid... " + killed.getName() + " I think their name was? Was killed a while ago in Oblask.\"");
			Print.println("\"Oh really, sad to hear. Not uncommon though. Lots of death these days.\"");
			Print.println("\"Well, apparently, they were killed by their own teacher!\"");
			Print.println("\"That's a shame. I guess you can't trust anybody these days.");
			Print.println("What was the teacher's name?\"");
			Print.println("\""+Player.player.getPerson().getName()+".\"");
			Print.println("Maybe you should come back later...");
			Print.println("1 lurk");
			Input.inInt(1);
			step = "gotoinn2";
			break;
		case "gotoinn2":step = "gotoinn3";break;
		case "gotoinn3": 
			Print.println("As you enter the main room, there is a commotion outside.");
			Print.println("Some people carry a person, frothing at the mouth, into the inn.");
			Print.println("\"Bring me to the touched one! Let me know the one who cannot die! Tell him to seek the oracles, to complete the journey to revan!\"");
			Print.println("They suddenly grip their head, and scream, before blood pours out of their eyes.");
			Print.println("1 watch them die");
			step = "gotorevan1";
			Input.inInt(1);
			;break;
		case "gotorevan1": 
			Print.println("You hear a voice within your head.");
			Print.println("\"Greetings " +Player.player.getPerson().getName()+", and welcome... to our sanctuary.\"");
			Print.println("\"You have been chosen- not for your skill, but for your loss.\"");
			step = "potato";
		;break;
		default: break;
		}
		
	}
	
	@Override
	public void levelUp(int level) {
		while (lastKnownLevel < level) {
			lastKnownLevel++;
			switch(lastKnownLevel) {
			case 3:
				Print.println("You think back to your student. You taught them everything you could, and it still wasn't enough. You've learned much more in the past few days... maybe enough to survive.");
				break;
			case 4:
				Print.println("You weren't always a teacher at heart- for a time, you were something else. But you were drawn in by the local Lord's offer.");
				break;
			}
		}
	}
	
}
