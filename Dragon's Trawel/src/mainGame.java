import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
/**
 * 
 * @author Brian Malone
 * 2/5/2018
 * The main method.
 * calls all the other classes, and holds the defining gameplay types and inputs.
 */
public class mainGame {
	//instance variables
	public static Scanner scanner = new Scanner(System.in);
	
	public static int starting_level = 1;
	public static boolean debug = false;

	public static boolean GUIInput = true;
	
	public static Story story = null;
	//constructors
	/**
	 * The main game. Includes the selector for gamemode and output type.
	 */
	mainGame(){
	//script variables
	
	
	Person p = new Person(1);
	Inventory bag = p.getBag();
	/*
	extra.disablePrintSubtle();
	bag.swapArmorSlot(new Armor(1, 0, MaterialFactory.getMat("iron")),0);
	bag.swapArmorSlot(new Armor(1, 1, MaterialFactory.getMat("iron")),1);
	bag.swapArmorSlot(new Armor(1, 2, MaterialFactory.getMat("iron")),2);
	bag.swapArmorSlot(new Armor(1, 3, MaterialFactory.getMat("iron")),3);
	bag.swapArmorSlot(new Armor(1, 4, MaterialFactory.getMat("iron")),4);
	*/
	bag.graphicalDisplay(-1,p);
	bag.graphicalDisplay(1,p);
	//extra.enablePrintSubtle();
	extra.changePrint(false);
	Networking.sendStrong("Discord|desc|Main Menu|");
	extra.println("1 Adventures (single player).");
	extra.println("2 Help");
	extra.println("3 Load (version-specific)");
	extra.println("4 Links");
	extra.println("5 Credits");
	extra.println("6 Connect");
	extra.println("7 Exit");
	extra.linebreak();
	switch(extra.inInt(8)){
	case 1:
		Networking.clearSides();
		gameTypes();
		return;     
	case 2:
		extra.println("Thanks for playing Trawel! Here's a few tips about learning how to play:");
		extra.println("Stylized like the old days, you must enter all input through a command line.");
		extra.println("As you start your adventure, be on the lookout for better gear than you currently have.");
		extra.println("There are three primary attack and defense types, sharp, blunt, and pierce.");
		extra.println("Sharp is edged. Swords are good at it, and chainmail is good at defending from it.");
		extra.println("Blunt is heavy. Maces are good at it, and gold is good at defending from it.");
		extra.println("Pierce is pointy. Spears are good at it, and metals are better at defending from it.");
		extra.println("Check your opponent's equipment to try to determine which type they are weak to!");
		extra.println("As you play the game, you'll get a grasp of the strengths and weaknesses of varying");
		extra.println("materials and weapons. It's part of the fun of the game!");
		extra.println("Attacks have a speed and a hitchance, along with damage types.");
		extra.println("Lower is faster for delay, higher is more accurate for the hitchance.");
		extra.println("Enchantments can be both good and bad, so keep an eye out!");
		extra.println("Value can be a good indicator of quality, but watch out for misses like");
		extra.println("Gold (a soft metal) sharp/piercing weapons.");
		extra.println("When in combat, type the number of the attack you wish to use.");
		extra.println("Pay close attention to hit, speed, sharp, blunt, and pierce.");
		extra.println("Higher is better, except in the case of delay.");
		extra.println("Well, you made it through bootcamp. Have fun!");
		extra.println("-realDragon");
		return;
	
	case 7: System.exit(0);break;
	case 3: Networking.clearSides(); WorldGen.load(); adventureBody(); break;
	case 6: extra.println("Port?"); Networking.connect(extra.inInt(65535)); Networking.send("Visual|MainMenu|");return;
	case 4: links();break;
	case 5: credits();break;
	}
	}
	
	
	private void credits() {
		extra.println("Made by Brian Malone");
		extra.println("Book writer: Tibo Smolders");
		extra.println("With thanks to the /r/GameMaker discord");
		
	}

	
	private void gameTypes() {
		extra.println("Choose an adventure:");
		extra.println("1 The DeathWalker");
		extra.println("2 Eoanan Sandbox");
		extra.println("3 Eoanan Wizard Mode");
		switch(extra.inInt(3)) {
		case 1: adventure1();break;
		case 2: adventure2();break;
		case 3: adventure3();break;
			}
	}
	
	private void links() {
		while (true) {
			//extra.println("1 Patreon");
			extra.println("1 Discord");
			//extra.println("3 Kickstarter");
			extra.println("2 Github");
			extra.println("3 back");
			switch(extra.inInt(2)) {
			//case 3: openWebpage("https://www.patreon.com/realDragon");break;
			case 1: openWebpage("https://discord.gg/jsyqu7X");break;
			//case 4: openWebpage("https://www.kickstarter.com/projects/738083082/trawel?ref=ea8wbq");break;
			case 2: openWebpage("https://github.com/realDragon11/Trawel-Text");
			case 3: return;
			}
		}
	}
	
	/**
	 * Main method. Calls the main game.
	 * @param args (Strings)
	 */
	public static void main(String[] args) {
		try {
		new MaterialFactory();
		new RaceFactory();
		new TargetFactory();
		new StyleFactory();
		new Oracle().load();
		new TauntsFactory();
		new BookFactory();
		story = new StoryNone();
		extra.println("Dragon's Trawel v0.4.1");
		extra.println(
				" ___________  ___  _    _ _____ _     \r\n" + 
				"|_   _| ___ \\/ _ \\| |  | |  ___| |    \r\n" + 
				"  | | | |_/ / /_\\ \\ |  | | |__ | |    \r\n" + 
				"  | | |    /|  _  | |/\\| |  __|| |    \r\n" + 
				"  | | | |\\ \\| | | \\  /\\  / |___| |____\r\n" + 
				"  \\_/ \\_| \\_\\_| |_/\\/  \\/\\____/\\_____/");
		/*extra.println("   ▄▄▄▄▀ █▄▄▄▄ ██     ▄ ▄   ▄███▄   █     \r\n" + 
				"▀▀▀ █    █  ▄▀ █ █   █   █  █▀   ▀  █     \r\n" + 
				"    █    █▀▀▌  █▄▄█ █ ▄   █ ██▄▄    █     \r\n" + 
				"   █     █  █  █  █ █  █  █ █▄   ▄▀ ███▄  \r\n" + 
				"  ▀        █      █  █ █ █  ▀███▀       ▀ \r\n" + 
				"          ▀      █    ▀ ▀                 \r\n" + 
				"                ▀                         ");*/
		new Networking();
		for (String a: args) {
			if (a.toLowerCase().equals("autoconnect")){
				Networking.autoConnect();
			}
			if (a.toLowerCase().equals("noguiinput")){
				GUIInput = false;
			}
			
		}
		/*
		int i = 1;
		while (i < 30) {
			extra.println(new Person(i).getName());
			i++;
		}*/
		
		while (true) { new mainGame();}
		}catch(Exception e) {
			extra.println("Trawel has encountered an exception. Please report to realDragon.");
			e.printStackTrace();
		}
		main(args);
		
	}
	
	
	//instance methods
	
	
		/**
		 * The advanced combat, incorporating weapons and armors.
		 * @param first_man (Person)
		 * @param second_man (Person)
		 * @return winner (Person)
		 */
		public static Person CombatTwo(Person first_man,Person second_man, World w) {
				Person holdPerson;
				extra.println("Our first fighter is " + first_man.getName()  + ". " +extra.choose("They hail from the","They come from the","They are from the","The place they call home is the") + " " + first_man.whereFrom() + ".");
				extra.println("Our second fighter is " + second_man.getName()  + ". " +extra.choose("They hail from the","They come from the","They are from the","The place they call home is the") + " " + second_man.whereFrom() + ".");
				extra.println();
				if (first_man.isPlayer()) {
					first_man.getBag().graphicalDisplay(-1,first_man);
					second_man.getBag().graphicalDisplay(1,second_man);
				}
				new Combat(first_man,second_man, w);//////
				if (first_man.getHp() <= 0) {
					holdPerson = second_man;
					second_man = first_man;
					first_man = holdPerson;
				}
				//if (first_man.isPlayer()) {//now this combat only works with the player
				//extra.println("");
				
				if (!second_man.isPlayer()) {
					extra.println(first_man.getName() +" goes to loot " + second_man.getName() +".");
				AIClass.loot(second_man.getBag(),first_man.getBag(),first_man.getIntellect(),true);}
				
				first_man.addXp(second_man.getLevel());
				
				if (second_man.isPlayer()) {
					Player.addXp(extra.zeroOut((int) (first_man.getLevel()*Math.floor((first_man.getMaxHp()-first_man.getHp())/(first_man.getMaxHp())))));
					die();
				}
				

					if (first_man.isPlayer()) {
					Player.player.wins++;
					if (Player.player.wins == 10) {
						Player.player.addTitle("duelist");
					}
					if (Player.player.wins == 50) {
						Player.player.addTitle("fighter");
					}
					if (Player.player.wins == 100) {
						Player.player.addTitle("warrior");
					}
					if (Player.player.wins == 1000) {
						Player.player.addTitle("master duelist");
					}
					}
					if (first_man.isPlayer() || second_man.isPlayer()) {
						Networking.clearSide(1);
					}
				return first_man;
		}
		
		/**
		 * if the fight doesn't involve the player, use the full method, with the World parameter
		 * @param first_man
		 * @param second_man
		 * @return
		 */
		public static Person CombatTwo(Person first_man,Person second_man) {
			return CombatTwo( first_man, second_man,Player.world);
		}
		
		public static ArrayList<Person> HugeBattle(ArrayList<Person>...people){
			return HugeBattle(Player.world,people);
		}
		
		public static ArrayList<Person> HugeBattle(World w, ArrayList<Person>... people){
			Combat battle = new Combat(w, people);
			Comparator<Person> levelSorter = new Comparator<Person>(){//sort in descending order
				@Override
				public int compare(Person arg0, Person arg1) {
					if (arg0.getLevel() == arg1.getLevel()) {
					return 0;}
					if (arg0.getLevel() < arg1.getLevel()) {
						return -1;
					}
					return 1;
				}
				
			};
			battle.killed.sort(levelSorter);
			battle.survivors.sort(levelSorter);
			
			for (Person surv: battle.survivors){
				surv.addXp(Math.min(surv.getLevel(),battle.killed.get(0).getLevel()));
				for (Person kill: battle.killed) {
					if (kill.isPlayer()) {continue;}else {
					AIClass.loot(kill.getBag(),surv.getBag(),surv.getIntellect(),false);}
				}
			}
			
			int gold = 0;
			for (Person kill: battle.killed) {
				if (kill.isPlayer()) {continue;}
				gold += kill.getBag().getGold();
				for (int i = 0;i<5;i++) {
					gold +=kill.getBag().getArmorSlot(i).getCost();
				}
				gold+=kill.getBag().getHand().getCost();
				
			}
			
			for (Person surv: battle.survivors){
				surv.getBag().addGold(gold/battle.survivors.size());
			}
			
			return battle.survivors;
		}

		
		
		public void adventure2(){
			World world = new World(10,20,"eoano");
			WorldGen.eoano(world);
			Player player = randPerson(false,true);
			player.setLocation(world.getStartTown());
			WorldGen.plane.setPlayer(player);
			player.getPerson().playerLevelUp();
			Player.toggleTutorial();
			adventureBody();
		}
		
		public void adventure3(){
			World world = new World(10,20,"eoano");
			WorldGen.eoano(world);
			Player player = randPerson(false,true);
			player.setLocation(world.getStartTown());
			WorldGen.plane.setPlayer(player);
			//player.getPerson().playerLevelUp();
			player.getPerson().addXpSilent(9999);
			Player.toggleTutorial();
			adventureBody();
		}
		
		public void adventureBody() {
			while(Player.player.isAlive()) {
				
				Player.player.getLocation().atTown();
				double time = Player.popTime();
				WorldGen.plane.passTime(time);
			}
			extra.println("You do not wake up.");
		}
		
		private static Player randPerson(boolean printIt, boolean choice) {
			Person manOne, manTwo;
			Player player;
			while (true) {
				 manOne = new Person(starting_level);
				 manTwo = new Person(starting_level);
				 extra.changePrint(!printIt);
				 manOne = CombatTwo(manOne,manTwo);
				 extra.changePrint(false);
				 manOne.displayStats();
				 player = new Player(manOne);
				 manOne.setPlayer();
				 Networking.sendStrong("Discord|desc|Character Select|");
				 //Networking.send("Visual|Race|" + manOne.getBag().getRace().name+  "|");
				 Networking.charUpdate();
				 if (!choice) {
					 break;
				 }
				 extra.println("Play as " + manOne.getName() + "?");
				
				 if (extra.yesNo()) {
					 break;
				 }
			}
			return player;
		}
		
		
		public void adventure1(){
			World world = new World(10,20,"eoano");
			WorldGen.eoano(world);
			story = new StoryDeathWalker();
			Person manOne, manTwo;
			Player player;
				 manOne = new Person(starting_level);
				 Person manThree = manOne;
				 manTwo = new Person(starting_level);
				 extra.changePrint(true);
				 manOne = CombatTwo(manOne,manTwo);
				 if (manOne == manThree) {
					 StoryDeathWalker.killed = manTwo;
				 }else {
					 StoryDeathWalker.killed = manThree;
				 }
				 extra.changePrint(false);
				 //manOne.displayStats();
				 player = new Player(manOne);
				 manOne.setPlayer();
				 Networking.sendStrong("Discord|desc|Character Select|");
				 //Networking.send("Visual|Race|" + manOne.getBag().getRace().name+  "|");
				 Networking.charUpdate();
				 player.getPerson().setSkillPoints(0);
				 Player.addSkill(Skill.BLOODTHIRSTY);
				 Player.player.getPerson().addFighterLevel();
			story.storyStart();
			player.setLocation(world.getStartTown());
			WorldGen.plane.setPlayer(player);
			//player.getPerson().playerLevelUp();
			adventureBody();
		}
		
		

		public static void openWebpage(String urlString) {
			try {
				Desktop.getDesktop().browse(new URL(urlString).toURI());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public static void die(String deathMessage) {
			story.onDeath();
			extra.println(deathMessage);
			story.onDeathPart2();
		}
		
		public static void die() {
			die(extra.choose("You rise from death...","You return to life.","You walk again!","You rise from the grave!","Death releases it's hold on you."));
		}



}
