package trawel;

public class StoryDeathWalker extends Story{

	public static Person killed;
	public static int deaths = 0;
	public static String step;
	
	
	public void storyStart() {
		extra.println("You did what you had to.");
		extra.println("At least, that's what you told yourself, when you struck them down.");
		extra.println(killed.getName() + " didn't have it coming.");
		extra.println("They were a nice kid.");
		extra.println("1 sigh");
		extra.inInt(1);
		extra.println("They were a young " + killed.getBag().getRace().name +", always eager to please.");
		extra.println("Damn that wizard!");
		
		extra.println("...");
		extra.println("But there's no sense dwelling in the past. You've managed to flee to Homa, to start your life anew.");
		extra.println("You don't have much... you should probably explore the forest or fight in the arena to gain some money before moving on to a town with an inn.");
		extra.println("1 look around");
		extra.inInt(1);
		step = "gotoinn1";
	}


	public void onDeath() {
		deaths++;
		switch (deaths) {
		case 1:
		extra.println("No! You can't die here!");
		extra.println("You didn't escape from Oblask to just die in some random village!");
		extra.println("You can see " +killed.getName() +"'s face clearly now..." );
		extra.println("1 wake up");
		extra.inInt(1);
		Networking.sendStrong("Achievement|die1|");
		;break;
		case 2: extra.println("Once again, your body hits the floor...");
		extra.println("You've wasted your second chance...");
		extra.println("You can see " +killed.getName() +"'s face again..." );
		extra.println("1 wake up");
		extra.inInt(1);
		;break;
		default: extra.println("You see glimpses of " +killed.getName()+ "'s face.");break;
		}
		
	}
	
	public void onDeathPart2() {
		switch (deaths) {
		case 1:
			extra.println("What?!");
			extra.println("You're alive?!");
			extra.println("But how? Why?!");
			extra.println("You guess you should be thankful...");
			extra.println("Dusting off your "+Player.bag.getArmorSlot(extra.randRange(0, 4)).getName() + " you stand up.");
			extra.println("Well, best get moving, at least. Figure out what happened later.");
			extra.println("1 look around");
			extra.inInt(1);
		;break;
		case 2: extra.println("It happened again!");
		extra.println("You saw their face again too!");
		extra.println("You must be immortal now... but... you don't understand!");
		extra.println("1 look around");
		extra.inInt(1);
		;break;
		default:;break;
		}
		
	}
	
	public void inn() {
		switch(step) {
		case "gotoinn1":
			extra.println("As you enter the inn you can hear the clashing of mugs.");
			extra.println("You walk up to a table and listen in, trying to see if there's any word from Oblask.");
			extra.println("After some time, you hear the name of the town drop.");
			extra.println("1 listen in");
			extra.inInt(1);
			extra.println("\"Hear some kid... " + killed.getName() + " I think their name was? Was killed a while ago in Oblask.\"");
			extra.println("\"Oh really, sad to hear. Not uncommon though. Lots of death these days.\"");
			extra.println("\"Well, apparently, they were killed by their own teacher!\"");
			extra.println("\"That's a shame. I guess you can't trust anybody these days.");
			extra.println("What was the teacher's name?\"");
			extra.println("\""+Player.player.getPerson().getName()+".\"");
			extra.println("Maybe you should come back later...");
			extra.println("1 leave");
			extra.inInt(1);
			step = "gotoinn2";
			break;
		case "gotoinn2":step = "gotoinn3";break;
		case "gotoinn3": 
			extra.println("As you enter the main room, there is a commotion outside.");
			extra.println("Some people carry a person, frothing at the mouth, into the inn.");
			extra.println("\"Bring me to the touched one! Let me know the one who cannot die! Tell him to seek the oracles, to complete the travel to revan!\"");
			extra.println("They suddenly grip their head, and scream, before blood pours out of their eyes.");
			extra.println("1 watch them die");
			step = "gotorevan1";
			extra.inInt(1);
			;break;
		default: break;
		}
		
	}
	
	public void altar() {
		switch(step) {
		case "gotorevan1": 
			extra.println("You hear a voice within your head.");
			extra.println("\"Greetings " +Player.player.getPerson().getName()+", and welcome... to our sanctuary.\"");
			extra.println("\"You have been chosen- not for your skill, but for your loss.\"");
			step = "potato";
		;break;
		}
		
	}
	
}
