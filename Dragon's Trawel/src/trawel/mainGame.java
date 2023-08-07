package trawel;
import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuGeneratorPaged;
import derg.menus.MenuItem;
import derg.menus.MenuLast;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import scimech.combat.MechCombat;
import scimech.handlers.SaveHandler;
import scimech.mech.Mech;
import scimech.people.Pilot;
import scimech.units.mechs.DebugMech;
import scimech.units.mechs.Dynamo;
import scimech.units.mechs.Hazmat;
import scimech.units.mechs.Musketeer;
import scimech.units.mechs.Packrat;
import scimech.units.mechs.Pirate;
import scimech.units.mechs.Pyro;
import scimech.units.mechs.Swashbuckler;
import trawel.Networking.ConnectType;
import trawel.battle.Combat;
import trawel.battle.TauntsFactory;
import trawel.battle.attacks.StyleFactory;
import trawel.battle.attacks.TargetFactory;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.factions.HostileTask;
import trawel.personal.DummyPerson;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Skill;
import trawel.personal.item.Item;
import trawel.personal.item.body.Race;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;
import trawel.personal.item.solid.variants.ArmorStyle;
import trawel.personal.people.Player;
import trawel.quests.QuestReactionFactory;
import trawel.towns.Calender;
import trawel.towns.World;
import trawel.towns.events.TownFlavorFactory;
import trawel.towns.services.BookFactory;
import trawel.towns.services.Oracle;
/**
 * 
 * @author dragon
 * 2/5/2018
 * Entry point for compiled game.
 */
public class mainGame {

	public static final String VERSION_STRING = "v0.8.b__2";//__X is in development, _X is the actual release of that version
	public static final String[] changelog = new String[] {
			//add to front, changeviewer cycles to older ones when used
			"b_1: base attack code reworked in basically every way. currency divided. threading added (nothreads is an arg), time passing redone. Node exploration mostly same but had entire backend update. Locational damage exists but does little at the moment."
			,"End of current beta ingame changelog. Check the github for more."
	};
	public static int changelogViewer = 0;

	//static vars
	public static Scanner scanner = new Scanner(System.in);
	
	public static boolean debug = false;
	public static boolean inEclipse = true;//for other run configs
	public static boolean autoConnect = false;
	public static boolean legacyConnect = true;
	public static boolean noDisconnect = false;
	public static boolean noThreads = true;
	public static boolean permaNoThreads = false;
	public static boolean noTerminal = false;
	public static boolean headless = false;

	public static boolean GUIInput = true;
	
	public static Story story = null;
	
	public static Date lastAutoSave = new Date();
	
	public static boolean logStreamIsErr = false;
	
	public static boolean delayWaits = false;//DOLATER maybe turn back on TODO: seems to apply anyway???
	
	private static boolean finalSetup1 = false;
	private static boolean basicSetup1 = false;
	
	public static boolean multiCanRun = false;
	
	//FIXME: need saved prefs
	public static DispAttack attackDisplayStyle = DispAttack.TWO_LINE1_WITH_KEY;
	public static boolean advancedCombatDisplay = false;
	public static boolean doAutoSave = true;
	public static PrintStream logStream;
	
	public enum DispAttack{
		CLASSIC("Classic simple table, delay instead of cooldown and warmup"),
		TWO_LINE1_WITH_KEY("Current version, hybrid table with per-cell labels instead of a header. Includes a key/legend."),
		TWO_LINE1("Current version, hybrid table with per-cell labels instead of a header. Does not include key/legend");
		
		private String desc;
		DispAttack(String _desc){
			desc = _desc;
		}
		
		public String desc() {
			return desc;
		}
	}
	
	public static void mainMenu() {
		//log("gen main menu 0");
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				//log("gen main menu 1");
				extra.changePrint(false);
				//log("gen main menu 2");
				Networking.sendStrong("Discord|desc|Main Menu|");
				Networking.sendStrong("Visual|MainMenu|");
				
				//log("gen main menu 3");
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Play Game";
					}

					@Override
					public boolean go() {
						Networking.clearSides();
						gameTypes();
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Load Trawel Save";
					}

					@Override
					public boolean go() {
						forceSetup();
						Networking.clearSides();
						for (int i = 1; i < 9; i++) {
							extra.println(i+" slot: "+WorldGen.checkNameInFile(""+i));
						}
						extra.println("9 autosave: "+WorldGen.checkNameInFile("auto"));
						int in = extra.inInt(9);
						WorldGen.load(in == 9 ? "auto" : in+"");
						boolean runit;
						try {
							Player.player.getPerson();
							runit = true;
						}catch (Exception e) {
							runit = false;
						}
						 if (runit) {
							 adventureBody();
						 }
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Changelog, press multiple times to cycle\n";
					}

					@Override
					public boolean go() {
						extra.println(changelog[changelogViewer++]);
						changelogViewer%=changelog.length;
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Infodump Tutorial";
					}

					@Override
					public boolean go() {
						extra.println("Thanks for playing Trawel! Here's a few tips about learning how to play:");
						extra.println("All of Trawel proper, and most of the side games, only require inputing a number between 1 and 9.");
						extra.println();
						extra.println("There are a few games in Trawel, but the one simply called 'Trawel' has the following advice:");
						extra.println("Always be on the lookout for better gear than you currently have. Your power level is largely determined by how powerful your gear is- not just it's level.");
						extra.println("There are three primary attack and defense types, sharp, blunt, and pierce.");
						extra.println("Sharp is edged and cutting. Swords are good at it, and chainmail is good at defending from it. Some materials are softer, like Gold, and thus bad at it.");
						extra.println("Blunt is heavy and crushing. Maces are good at it, and gold is good at defending from it- and also dealing it.");
						extra.println("Pierce is pointy and puncturing. Spears are good at it, and metals are better at defending from it.");
						extra.println("If you're feeling tactical, you can read your opponent's equipment to try to determine which type they are weak to.");
						extra.println("As you play the game, you'll get a grasp of the strengths and weaknesses of varying materials and weapons. It's part of the fun of the game!");
						extra.println("Attacks have a delay amount (further broken down into warmup/cooldown) and a hitchance, along with damage types.");
						extra.println("Delay is how long it takes for the attack to happen- it can be thought of how 'slow' the attack is, so lower is better. Warmup is the period before you act, and Cooldown is the period after- but you can't choose another action until both elapse.");
						extra.println("Hitchance is the opposite- higher is more accurate. However, it is not a percent chance to hit, as it does not account for the opponent's dodge, which can change over time.");
						extra.println("Enchantments can be both good and bad, so keep an eye out for gear that has low stats but boosts overall stats a high amount- or gear that makes you much weaker!");
						extra.println("When looting equipment, you are shown the new item, then your current item, and then the stat changes between the two- plus for stat increases, minus for stat decreases. The difference will not show stats that remain the same.");
						extra.println("Value can be a good rough indicator of quality, but it does not account for the actual effectiveness of the item, just the rarity and tier.");
						extra.println("For example, gold (a soft metal) sharp/piercing weapons are expensive but ineffective.");
						extra.println("When in combat, you will be given 3 (by default, skills and circumstance may change this) random attacks ('opportunities') to use your weapon.");
						extra.println("Pay close attention to hit, warmup/cooldown, and sbp (sharp, blunt, pierce) damage.");
						extra.println("More simply: Higher is better, except in the case of delay (warmup and cooldown).");
						extra.println("Well, you made it through bootcamp. Have fun!");
						extra.println("-realDragon");
						return false;
					}});
				
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Credits";
					}

					@Override
					public boolean go() {
						credits();
						return false;
					}});
				
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "GitHub (opens in browser)";
					}

					@Override
					public boolean go() {
						openWebpage("https://github.com/realDragon11/Trawel-Text");
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Advanced Options";
					}

					@Override
					public boolean go() {
						//TODO: just reconnecting for now, need to have accessibility options later
						advancedOptions();
						return false;
					}});
				mList.add(new MenuLast() {

					@Override
					public String title() {
						return "Exit";
					}

					@Override
					public boolean go() {
						System.exit(0);
						return false;
					}});
				
				return mList;
			}
		});
	}
	
	private static void advancedOptions() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Here are some display options. They currently do not save per run, although they are planned to later after prefs get made bigger.";
					}}
				);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return advancedCombatDisplay +" Advanced Combat Display (HP per attack, attack result notes)";
					}

					@Override
					public boolean go() {
						advancedCombatDisplay = !advancedCombatDisplay;
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return attackDisplayStyle +" Attack Display Style (Cycles through options) currently: " + attackDisplayStyle.desc();
					}

					@Override
					public boolean go() {
						attackDisplayStyle = DispAttack.values()[(attackDisplayStyle.ordinal()+1)%DispAttack.values().length];
						return false;
					}});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "\nThese options deal with reconnecting to Trawel Graphical, and only apply to the Steam version. They only work on the command line, otherwise you may have to restart or use the command line to finish reconnecting.";
					}}
				);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Quick Reconnect";
					}

					@Override
					public boolean go() {
						Networking.connect(6510); 
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Full Reconnect\n";
					}

					@Override
					public boolean go() {
							extra.println("Port?"); Networking.connect(extra.inInt(65535)); 
						return true;
					}});
				mList.add(new MenuBack());
				return mList;
			}
		});
	}
	
	
	private static void credits() {
		extra.println("Made by 'dragon' of realDragon");
		extra.println("With thanks to many different discords across different programming languages and engines.");
		extra.println("Achievement icons can be found on game-icons.net");
		extra.println("Game-Icons.net list:\r\n"
				+ "    Lorc, http://lorcblog.blogspot.com\r\n"
				+ "    Delapouite, https://delapouite.com\r\n"
				+ "    John Colburn, http://ninmunanmu.com\r\n"
				+ "    Felbrigg, http://blackdogofdoom.blogspot.co.uk\r\n"
				+ "    John Redman, http://www.uniquedicetowers.com\r\n"
				+ "    Carl Olsen, https://twitter.com/unstoppableCarl\r\n"
				+ "    Sbed, http://opengameart.org/content/95-game-icons\r\n"
				+ "    PriorBlue\r\n"
				+ "    Willdabeast, http://wjbstories.blogspot.com\r\n"
				+ "    Viscious Speed, http://viscious-speed.deviantart.com\r\n"
				+ "    Lord Berandas, http://berandas.deviantart.com\r\n"
				+ "    Irongamer, http://ecesisllc.wix.com/home\r\n"
				+ "    HeavenlyDog, http://www.gnomosygoblins.blogspot.com\r\n"
				+ "    Lucas\r\n"
				+ "    Faithtoken, http://fungustoken.deviantart.com\r\n"
				+ "    Skoll\r\n"
				+ "    Andy Meneely, http://www.se.rit.edu/~andy/\r\n"
				+ "    Cathelineau\r\n"
				+ "    Kier Heyl\r\n"
				+ "    Aussiesim\r\n"
				+ "    Sparker, http://citizenparker.com\r\n"
				+ "    Zeromancer\r\n"
				+ "    Rihlsul\r\n"
				+ "    Quoting\r\n"
				+ "    Guard13007, https://guard13007.com\r\n"
				+ "    DarkZaitzev, http://darkzaitzev.deviantart.com\r\n"
				+ "    SpencerDub\r\n"
				+ "    GeneralAce135\r\n"
				+ "    Zajkonur\r\n"
				+ "    Catsu\r\n"
				+ "    Starseeker\r\n"
				+ "    Pepijn Poolman\r\n"
				+ "    Pierre Leducq\r\n"
				+ "    Caro Asercion\r\n"
				+ "");
		extra.println("Music by manicInsomniac");
		extra.println("Art:");
		extra.println("Character, armor, and weapon art by SmashCooper and Duster. Background art by Damrok and he-who-shall-not-be-named");
		extra.println("Sounds:");
		extra.println("Stock Media provided by Soundrangers / FxProSound / SoundIdeasCom / PrankAudio / hdaudio / agcnf_media / sounddogs / AbloomAudio / Yurikud / SoundMorph => through Pond5");
	}
	
	@SuppressWarnings("unused")
	private static void baseSetup1() {
		if (!basicSetup1) {
			EnchantConstant.init();
			
			new MaterialFactory();
			new RaceFactory();
			new TargetFactory();
			new StyleFactory();
			ArmorStyle.init();
			new Oracle().load();
			new TauntsFactory();
			new BookFactory();
			new BumperFactory();
			new WeaponAttackFactory();
			new TownFlavorFactory();
			new QuestReactionFactory();
			WorldGen.initDummyInvs();
			story = new StoryNone();
			DummyPerson.init();
			
			basicSetup1 = true;
		}
	}
	
	public static void forceSetup() {
		baseSetup1();
		if (!finalSetup1) {
			randomLists.init();
			finalSetup1 = true;
		}
	}
	
	public static void unitTestSetup() {
		forceSetup();
	}

	
	private static void gameTypes() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Choose a Game Mode:";
					}}
				);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Trawel Quickstart (base game, recommended)";
					}

					@Override
					public boolean go() {
						adventure1(false,false,false,false);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Trawel Slowstart (base game)";
					}

					@Override
					public boolean go() {
						adventure1(false,true,true,true);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "rTrawel (experimental jrpg, prototype, nongraphical)";
					}

					@Override
					public boolean go() {
						rtrawel.TestRunner.run();
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "SciMechs (experimental mech game, prototype, nongraphical)";
					}

					@Override
					public boolean go() {
						SaveHandler.clean();
						while (true) {
							extra.println("Choose your mechs");
							List<Mech> mechs = mechsForSide(true);
							extra.println("Save mechs?");
							if (extra.yesNo()) {
								SaveHandler.clean();
								SaveHandler.imprintMechs(mechs);
								SaveHandler.save();
							}
							extra.println("Choose their mechs");
							mechs.addAll(mechsForSide(false));
							
							MechCombat mc = new MechCombat(mechs);
							mc.go();
							
							extra.println(mc.activeMechs.get(0).playerControlled == true ? "You win!" : "You lose!"+ "\n Would you like the quit?");
							if (extra.yesNo()) {
								return true;
							}
						}
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "various tests";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator(){

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> mList = new ArrayList<MenuItem>();
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Cheat Trawel";
									}

									@Override
									public boolean go() {
										adventure1(true,false,false,false);
										return true;
									}});
								mList.add(new MenuLine() {

									@Override
									public String title() {
										return "Graphical Tests:";
									}}
								);
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Model Mode";
									}

									@Override
									public boolean go() {
										modelMode();
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Time Test";
									}

									@Override
									public boolean go() {
										Calender.timeTest();
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Scroll Menu Test";
									}

									@Override
									public boolean go() {
										scrollTest();
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Weapon Stat Query";
									}

									@Override
									public boolean go() {
										weaponStatQuery();
										return true;
									}});
								/*
								mList.add(new MenuLine() {

									@Override
									public String title() {
										return "Backend Tests:";
									}}
								);
								
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Weapon Power Metrics (creates csv)";
									}

									@Override
									public boolean go() {
										baseSetup1();
										try {
											WeaponAttackFactory.weaponMetrics();
										} catch (FileNotFoundException e) {
											e.printStackTrace();
										}
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Weapon Rarity Metrics (creates csv)";
									}

									@Override
									public boolean go() {
										baseSetup1();
										try {
											Weapon.duoRarityMetrics();
										} catch (FileNotFoundException e) {
											e.printStackTrace();
										}
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Save Test";
									}

									@Override
									public boolean go() {
										saveTest();
										return true;
									}});*/
								mList.add(new MenuBack());
								return mList;
							}
						});
						return false;
					}});
				mList.add(new MenuBack());
				return mList;
			}
			});
	}
	
	private static void saveTest() {
		baseSetup1();
		for (int j = 0;j < 10;j++) {
			extra.println("try: "+j);
			for (int i = 1;i<9;i++ ) {
				extra.println("slot: "+i);
				WorldGen.eoano();
				Person p = RaceFactory.makeGeneric(1);
				p.setPlayer();
				WorldGen.plane.setPlayer(new Player(p));
				WorldGen.save(i+"");
				WorldGen.load(1+"");
			}
			//WorldGen.conf.getClassRegistry().dragonDump();
		}
	}

	private static List<Mech> curMechs;

	
	private static List<Mech> mechsForSide(boolean side){
		extra.menuGoPaged(new MenuGeneratorPaged(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "load mechs (??? complexity)";
					}

					@Override
					public boolean go() {
						curMechs = SaveHandler.exportMechs();
						for (Mech m: curMechs) {
							m.playerControlled = side;
							m.swapPilot(new Pilot());
						}
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "debug mechs (70x2 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = debugMechs(side);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "three musketeers (80x3 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = threeMusketeers(side);
						return true;
					}});
				
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "pirate squad (80x3 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = pirateSquad(side);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "science squad (120x3 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = scienceSquad(side);
						return true;
					}});
				return mList;
			}});
		
		return curMechs;
	}
	
	private static List<Mech> debugMechs(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new DebugMech(side));
		mechs.add(new DebugMech(side));
		return mechs;
	}
	
	private static List<Mech> threeMusketeers(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new Musketeer(side));
		mechs.add(new Musketeer(side));
		mechs.add(new Musketeer(side));
		return mechs;
	}
	
	private static List<Mech> pirateSquad(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new Swashbuckler(side));
		mechs.add(new Packrat(side));
		mechs.add(new Pirate(side));
		return mechs;
	}
	
	private static List<Mech> scienceSquad(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new Dynamo(side));
		mechs.add(new Pyro(side));
		mechs.add(new Hazmat(side));
		return mechs;
	}
	
	private static void modelMode() {
		baseSetup1();
		Person manOne;
		manOne = RaceFactory.makeOld(2);//new Person(starting_level,false,Race.RaceType.HUMANOID,null);
			 new Player(manOne);
			 Player.bag.swapWeapon(new Weapon(1,"shovel"));
			 //Player.bag.getHand().forceEnchantHit(0);
			 /*
			 player.bag.swapArmorSlot(new Armor(1,0,MaterialFactory.getMat("emerald")),0);
			 player.bag.swapArmorSlot(new Armor(1,1,MaterialFactory.getMat("emerald")),1);
			 player.bag.swapArmorSlot(new Armor(1,2,MaterialFactory.getMat("emerald")),2);
			 player.bag.swapArmorSlot(new Armor(1,3,MaterialFactory.getMat("emerald")),3);
			 player.bag.swapArmorSlot(new Armor(1,4,MaterialFactory.getMat("emerald")),4);*/
			 manOne.setPlayer();
			 Networking.sendStrong("Discord|desc|Character Select|");
			 Networking.charUpdate();
		
	}
	
	public static String headerText() {
		return ("Dragon's Trawel "+VERSION_STRING)+"\r\n"+
		(
				" ___________  ___  _    _ _____ _     \r\n" + 
				"|_   _| ___ \\/ _ \\| |  | |  ___| |    \r\n" + 
				"  | | | |_/ / /_\\ \\ |  | | |__ | |    \r\n" + 
				"  | | |    /|  _  | |/\\| |  __|| |    \r\n" + 
				"  | | | |\\ \\| | | \\  /\\  / |___| |____\r\n" + 
				"  \\_/ \\_| \\_\\_| |_/\\/  \\/\\____/\\_____/");
	}
	
	public static void log(String str) {
		if (extra.isMainThread()) {
			logStream.println(str);
			logStream.flush();
		}
	}
	
	/**
	 * Main method. Calls the main game.
	 * @param args (Strings)
	 */
	public static void main(String[] args) {
		inEclipse = false;
		try {
		System.out.println(headerText());
		extra.setMainThread();
		new WorldGen();
		logStream = new PrintStream("log.txt");
		for (String a: args) {
			if (a.toLowerCase().equals("autoconnect")){
				autoConnect = true;
				//extra.println("Please wait for the graphical to load...");
				//Networking.autoConnect();
			}
			if (a.toLowerCase().equals("noguiinput")){
				GUIInput = false;
			}
			if (a.toLowerCase().equals("nowarnings")) {
				logStreamIsErr = true;
				System.setErr(logStream);
			}
			if (a.toLowerCase().equals("ineclipse")) {
				inEclipse = true;
			}
			if (a.toLowerCase().equals("nodisconnect")) {
				noDisconnect = true;
			}
			if (a.toLowerCase().equals("nothreads")) {
				permaNoThreads = true;
			}
			if (a.toLowerCase().equals("gdx")) {
				legacyConnect = false;
			}
			if (a.toLowerCase().equals("quiet")) {
				noTerminal = true;
			}
			if (a.toLowerCase().equals("loud")) {
				headless = true;
			}
			
		}
		new Networking();
		if (autoConnect) {
			System.out.println("Please wait for the graphical to load...");
			Networking.handleAnyConnection(legacyConnect ? ConnectType.LEGACY : ConnectType.GDX);
		}else {
			Networking.handleAnyConnection(ConnectType.NONE);
		}
		}catch(Exception e) {
			System.out.println("There was an error when setting up Trawel.");
			e.printStackTrace();
			
			extra.println("[jitter]Trawel has encountered an exception. Please report to realDragon. More details can be found on the command prompt.");
			extra.println("PREVIEW, SEE ABOVE IN TEXT FOR FULL MESSAGE: "+ (e.getMessage() != null ? (e.getMessage()) :"null" + e.getStackTrace()));
			System.out.println("Press enter to quit.");
			extra.inString();
		}
		boolean breakErr = false;
		while (!breakErr) {
			try {
				//mainGame.log("starting main menu");
				mainMenu();
			}catch (Exception e) {
				try {
					mainGame.log(e.getMessage());
					errorHandle(e);
				}catch(Exception fatal) {
					System.out.println("Error was fatal. Press enter to close.");
					breakErr = true;
				}
				if (!breakErr) {
					System.out.println("Press enter to attempt to recover from the error.");
				}
				extra.inString();
			}
		}
		//mainGame.log("exiting game");
		
		//
		
		logStream.close();
	}
	
	private static void errorHandle(Exception e) {
		System.out.println("Error Stacktrace:");
		e.printStackTrace();
		extra.println("[jitter]Trawel has encountered an exception. Please report to realDragon. More details can be found on the command prompt.");
		extra.println((e.getMessage() != null ? (e.getMessage()) :"null" + e.getStackTrace()));
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
		extra.println("Our first fighter is " + first_man.getName()  + "."); //+extra.choose("They hail from the","They come from the","They are from the","The place they call home is the") + " " + first_man.whereFrom() + ".");
		extra.println("Our second fighter is " + second_man.getName()  + "."); //+extra.choose("They hail from the","They come from the","They are from the","The place they call home is the") + " " + second_man.whereFrom() + ".");
		extra.println();
		if (first_man.isPlayer()) {
			first_man.getBag().graphicalDisplay(-1,first_man);
			second_man.getBag().graphicalDisplay(1,second_man);
			Networking.setBattle(Networking.BattleType.NORMAL);
			story.startFight(false);
		}
		new Combat(first_man,second_man, w);//////
		if (second_man.getHp() > 0) {
			holdPerson = second_man;
			second_man = first_man;
			first_man = holdPerson;
		}
		boolean hasPlayer = false;
		if (first_man.isPlayer() || second_man.isPlayer()) {
			Networking.setBattle(Networking.BattleType.NONE);
			hasPlayer = true;

		}

		first_man.addXp(second_man.getLevel());

		if (!second_man.isPlayer() && first_man.getBag().getRace().racialType == Race.RaceType.HUMANOID) {
			extra.println(first_man.getName() +" goes to loot " + second_man.getName() +".");
			AIClass.loot(second_man.getBag(),first_man.getBag(),first_man.getIntellect(),true,first_man);
		}

		if (second_man.isPlayer()) {
			first_man.addPlayerKill();
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
			second_man.addDeath();
			if (second_man.getBag().getRace().racialType == Race.RaceType.HUMANOID) {
				int deaths = second_man.getDeaths();
				int pKills = second_man.getPlayerKills();
				//max revive chance decreases the more they die, even if they have infinite kills on you, to reduce annoyance
				if (
						(deaths == 1 && extra.randFloat() > .97)//~3% chance if this is our only death
						|| (pKills > 0 && deaths < 4 && extra.chanceIn(Math.min((4*deaths)+pKills,(pKills*2)+1), pKills+(5*deaths)))
						//killing the player is effective
						//we can at most get a (X-1)/x chance
						//can cheat death a max of 3 times
						) {
					w.addDeathCheater(second_man);//dupes don't happen since in this case the dupe is instantly removed in the wander code
					second_man.hTask = HostileTask.REVENGE;
				}
			}
			story.winFight(false);
		}
		if (hasPlayer) {
			Networking.setBattle(Networking.BattleType.NONE);
			Networking.clearSide(1);
		}
		first_man.clearBattleEffects();
		second_man.clearBattleEffects();

		return first_man;
	}
		
		/**
		 * if the fight doesn't involve the player, use the full method, with the World parameter
		 * @param first_man
		 * @param second_man
		 * @return
		 */
		public static Person CombatTwo(Person first_man,Person second_man) {
			return CombatTwo( first_man, second_man,Player.getWorld());
		}
		
		public static List<Person> HugeBattle(List<Person>...people){
			return HugeBattle(Player.getWorld(),Arrays.asList(people));
		}
		public static List<Person> HugeBattle(World w,List<Person>...people){
			return HugeBattle(w,Arrays.asList(people));
		}
		
		public static List<Person> HugeBattle(World w, List<List<Person>> people){
			Combat battle = new Combat(w, people);
			Comparator<Person> levelSorter = new Comparator<Person>(){//sort in descending order
				@Override
				public int compare(Person arg0, Person arg1) {
					if (arg0.getLevel() == arg1.getLevel()) {
						//there can only be one player, multiplayer would need a bigger re-write
						//player wins ties
						if (arg0.isPlayer()) {
							return 1;
						}
						if (arg1.isPlayer()) {
							return -1;
						}
						return 0;
					}
					if (arg0.getLevel() < arg1.getLevel()) {
						return -1;
					}
					return 1;
				}
				
			};
			battle.killed.sort(levelSorter);
			battle.survivors.sort(levelSorter);
			
			
			int killLevelTotal = 0;
			int winSide = -1;
			int[] xpTotalList = new int[people.size()];
			int[] xpDeadList = new int[people.size()];
			int[] xpAverage = new int[people.size()];
			int xpHighestKill = battle.killed.get(0).getLevel();
			int xpHighestAverage = 0;//excludes winning side
			int xpSideHigh = -1;
			int xpLowestAverage = Integer.MAX_VALUE;//excludes winning side
			int xpSideLow = -1;
			
			for (int i = people.size()-1;i >=0;i--) {
				xpTotalList[i] = 0;
				xpDeadList[i] = 0;
				if (people.get(i).contains(battle.survivors.get(0))) {
					winSide = i;
				}
				for (Person p: people.get(i)) {
					int lvl = p.getLevel();
					if (battle.killed.contains(p)) {
						xpDeadList[i]+=lvl;
					}
					xpTotalList[i]=lvl;
				}
				xpAverage[i] = xpTotalList[i]/people.get(i).size();
				if (i != winSide) {
					if (xpAverage[i] <= xpLowestAverage) {
						xpLowestAverage = xpAverage[i];
						xpSideLow = i;
					}
					if (xpAverage[i] >= xpHighestAverage) {
						xpHighestAverage = xpAverage[i];
						xpSideHigh = i;
					}
					//we use >= and <= so that if they're all the same it will pick the last one for both, so we can compare xpSideHigh == xpSideLow for that
				}
			}
			
			assert winSide >= 0;
			assert xpSideHigh >= 0;
			assert xpSideLow >= 0;
			
			
			int xpReward = xpHighestKill;//by default it's just the highest level of the killed list
			boolean bypassLevelCap = false;
			if (people.size() == 2) {//if a XvX battle
				assert xpSideHigh == xpSideLow;
				if (people.get(winSide).size() == 1) {// if it was a 1vX
					xpReward = xpDeadList[xpSideHigh];
					if (xpAverage[winSide]-1 < xpHighestAverage) {//if it was a 1vX where average level of X side was at least winside level minus one
						bypassLevelCap = true;
						//the solo winner gets xp equal to total dead level instead of just the best one
						//because the 1vX was roughly fair
						//otherwise, still capped at their level to avoid kicking a bunch of level 1 wolves being good
					}
				}
			}
			
			//NOTE: player does not get first pick unless they were highest level, they do win ties
			
			for (Person surv: battle.survivors){
				surv.clearBattleEffects();
				int subReward = xpReward;
				if (!bypassLevelCap && subReward > surv.getLevel()) {
					subReward = surv.getLevel();
				}
				boolean isPlayer = surv.isPlayer();
				if (isPlayer) {
					Networking.setBattle(Networking.BattleType.NONE);
				}
				surv.addXp(subReward);
				for (Person kill: battle.killed) {
					if (isPlayer) {
						kill.getBag().graphicalDisplay(1,kill);
					}
					AIClass.loot(kill.getBag(),surv.getBag(),surv.getIntellect(),false,surv);
				}
				if (isPlayer) {
					Networking.clearSide(1);
				}
			}
			
			int gold = 0;
			int aether = 0;
			Item isell = null;
			for (Person kill: battle.killed) {
				kill.clearBattleEffects();
				if (kill.isPlayer()) {
					Networking.setBattle(Networking.BattleType.NONE);
					Networking.clearSide(1);
					die();
					continue;
				}
				gold += kill.getBag().getGold();
				aether += kill.getBag().getAether();
				for (int i = 0;i<5;i++) {
					isell = kill.getBag().getArmorSlot(i);
					if (isell.canAetherLoot()) {
						aether +=isell.getAetherValue();
					}
				}
				isell = kill.getBag().getHand();
				if (isell.canAetherLoot()) {
					aether +=isell.getAetherValue();
				}
				//DOLATER: none of these people have their aether, money or items taken because it is assumed they won't be used again
			}
			
			
			if (battle.survivors.size() > 1) {
				int giveGold = gold/battle.survivors.size();
				int giveAether = aether/battle.survivors.size();
				if (giveGold > 0) {
					extra.println("The remaining " +World.currentMoneyDisplay(gold) +" is divvied up, "+giveGold +" each.");
				}
				if (giveAether > 0) {
					extra.println("The remaining " + aether +" aether is divvied up, "+giveAether +" each.");
				}
				for (Person surv: battle.survivors){
					if (giveGold > 0) {
						surv.getBag().addGold(giveGold);
					}
					if (giveAether > 0) {
						surv.getBag().addAether(giveAether);
					}
					if (surv.isPlayer()) {
						Networking.setBattle(Networking.BattleType.NONE);
						Networking.clearSide(1);
						mainGame.story.winFight(true);
					}
				}
			}else {
				Person looter = battle.survivors.get(0);
				if (gold > 0) {
					extra.println(looter.getName() + " claims the remaining " +World.currentMoneyDisplay(gold) +".");
					looter.getBag().addGold(gold);
				}
				if (aether > 0) {
					extra.println(looter.getName() + " claims the remaining " +aether +" aether.");
					looter.getBag().addAether(aether);
				}
				
				if (looter.isPlayer()) {
					Networking.setBattle(Networking.BattleType.NONE);
					Networking.clearSide(1);
					mainGame.story.winFight(true);
				}
			}
			
			
			return battle.survivors;
		}
		
		public static void adventureBody() {
			lastAutoSave = new Date();
			while(Player.player.isAlive()) {
				if (doAutoSave && (new Date().getTime()-lastAutoSave.getTime() > 1000*60*2)) {
					extra.println("Autosaving...");
					WorldGen.plane.prepareSave();;
					WorldGen.save("auto");
					lastAutoSave = new Date();
				}
				Player.player.getLocation().atTown();
				globalPassTime();
			}
			multiCanRun = false;
			extra.println("You do not wake up.");
		}
		
		/**
		 * note that some events, like the player generating gold, ignore normal restrictions
		 */
		public static void globalPassTime() {
			double time = Player.popTime();
			if (time > 0) {
				WorldGen.plane.advanceTime(time);
			}
		}
		
		
		public static void adventure1(boolean cheaty, boolean displayFight, boolean rerolls, boolean advancedDisplay){
			baseSetup1();
			if (!finalSetup1) {
				randomLists.init();
				finalSetup1 = true;
			}
			Networking.sendStrong("Discord|desc|Character Select|");
			World world = null;//WorldGen.eoano();
			if (rerolls) {
				story = new StoryNone();
			}else {
				story = new StoryTutorial();
			}
			Person manOne = null, manTwo;
			Player player;
			//
			while (manOne == null) {
				if (world == null) {
					extra.println("Generating world...");
					world = WorldGen.eoano();
				}
				manOne = RaceFactory.makePlayerValid();
				Person manThree = manOne;
				manTwo = RaceFactory.makePlayerValid();;
				if (!displayFight) {
					extra.changePrint(true);
				}
				manOne = CombatTwo(manOne,manTwo,null);
				if (manOne == manThree) {
					story.setPerson(manTwo, 0);
				}else {
					story.setPerson(manThree, 0);
				}
				if (!displayFight) {
					extra.changePrint(false);
				}
				if (rerolls) {
					manOne.getBag().graphicalDisplay(1, manOne);
					if (advancedDisplay) {
						manOne.displayStats(false);
					}
					extra.println("Play as " + manOne.getName() +"?");
					if (extra.yesNo()) {
						break;
					}
					manOne = null;
				}
				//manOne.displayStats();
				Networking.clearSides();
			}
			Networking.clearSides();
			player = new Player(manOne);
			manOne.setPlayer();
			//Networking.send("Visual|Race|" + manOne.getBag().getRace().name+  "|");
			Networking.charUpdate();
			player.getPerson().setSkillPoints(0);
			Player.addSkill(Skill.BLOODTHIRSTY);
			Player.player.getPerson().addFighterLevel();
			if (cheaty) {
				Player.toggleTutorial();
				player.getPerson().addXp(9999);
				story = new StoryNone();
				Player.addGold(1000);
				Player.bag.addAether(100000);
			}
			story.storyStart();
			player.storyHold = story;
			player.setLocation(world.getStartTown());
			WorldGen.plane.setPlayer(player);
			
			multiCanRun = true;
			
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
			Networking.sendStrong("StatUp|deaths|1|");
			story.onDeath();
			extra.println(deathMessage);
			story.onDeathPart2();
		}
		
		public static void die() {
			die(extra.choose("You rise from death...","You return to life.","You walk again!","You rise from the grave!","Death releases its hold on you."));
		}

		public static void scrollTest() {
			extra.menuGo(new ScrollMenuGenerator(30, "back <> more","forward (<> left)") {
				
				@Override
				public List<MenuItem> header() {
					return null;
				}
				
				@Override
				public List<MenuItem> forSlot(int i) {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return " hi " + i;
						}

						@Override
						public boolean go() {
							return true;
						}});
					return list;
				}
				
				@Override
				public List<MenuItem> footer() {
					return null;
				}
			});
			extra.menuGo(new ScrollMenuGenerator(16,"a <>", "b<>") {
				
				@Override
				public List<MenuItem> header() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "dead header";
						}});
					return list;
				}
				
				@Override
				public List<MenuItem> forSlot(int i) {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return " bye " + i;
						}

						@Override
						public boolean go() {
							return false;
						}});
					return list;
				}
				
				@Override
				public List<MenuItem> footer() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return " live footer";
						}

						@Override
						public boolean go() {
							return true;
						}});
					return list;
				}
			});
		}
		
		private static List<Material> weaponMatTesterList;
		
		public static void weaponStatQuery() {
			forceSetup();
			
			List<Material> standardList = new ArrayList<Material>();
			standardList.add(MaterialFactory.getMat("iron"));
			standardList.add(MaterialFactory.getMat("steel"));
			standardList.add(MaterialFactory.getMat("gold"));
			standardList.add(MaterialFactory.getMat("adamantine"));
			
			MenuGenerator weapMenu = new ScrollMenuGenerator(WeaponType.values().length,"previous <> weapons", "next <> weapons") {

				@Override
				public List<MenuItem> forSlot(int i) {
					List<MenuItem> list= new ArrayList<MenuItem>();
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return WeaponType.values()[i].toString();
						}

						@Override
						public boolean go() {
							WeaponAttackFactory.dispTestWeapon(WeaponType.values()[i],weaponMatTesterList);
							return false;
						}});
					return list;
				}

				@Override
				public List<MenuItem> header() {
					return null;
				}

				@Override
				public List<MenuItem> footer() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuBack("back"));
					return list;
				}};
				List<Material> allWeapMats = new ArrayList<Material>();
				MaterialFactory.matList.stream().filter(M->M.weapon).forEach(allWeapMats::add);
				
				MenuGenerator matMenu = new ScrollMenuGenerator(allWeapMats.size(),"previous <> materials", "next <> materials") {

					@Override
					public List<MenuItem> forSlot(int i) {
						List<MenuItem> list= new ArrayList<MenuItem>();
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return allWeapMats.get(i).name;
							}

							@Override
							public boolean go() {
								weaponMatTesterList = Collections.singletonList(allWeapMats.get(i));
								extra.menuGo(weapMenu);
								return false;
							}});
						return list;
					}

					@Override
					public List<MenuItem> header() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "standard";
							}

							@Override
							public boolean go() {
								weaponMatTesterList = standardList;
								extra.menuGo(weapMenu);
								return false;
							}});
						return list;
					}

					@Override
					public List<MenuItem> footer() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuBack("exit"));
						return list;
					}};
			
			extra.menuGo(matMenu);
		}


}
