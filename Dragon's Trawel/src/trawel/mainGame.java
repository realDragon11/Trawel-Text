package trawel;
import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
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
import trawel.battle.Combat.SkillCon;
import trawel.battle.attacks.ImpairedAttack.DamageType;
import trawel.battle.attacks.StyleFactory;
import trawel.battle.attacks.TargetFactory;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.factions.HostileTask;
import trawel.personal.DummyPerson;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Archetype;
import trawel.personal.item.Item;
import trawel.personal.item.body.Race;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.solid.DrawBane;
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
import trawel.towns.fort.FortHall;
import trawel.towns.services.BookFactory;
import trawel.towns.services.Oracle;
/**
 * 
 * @author dragon
 * 2/5/2018
 * Entry point for compiled game.
 */
public class mainGame {

	//b__X is in development, b_X is the actual release of that version
	public static final String VERSION_STRING = "v0.8.b__3 updated Aug 15th 2023";
	public static final String[] changelog = new String[] {
			//add to front, changeviewer cycles to older ones when used
			"b__3: Multiple display option improvements, subtle changes to battle conditions.",
			"b_2: {part 1/3} Attack backend changes became frontend changes for weapons, which now have different stat displays. This also made armor and dodging actual things again, before their average stats were a bit too low, now the forumlas have been re-tested and remade. 'classless' system (essentially multiclassing but with many multis) has born fruit, you can now use the replacement system, although it still has a long ways to go.",
			"b_2: {part 2/3} If you want to pick your own Archetype to start with, use slowstart, otherwise it will pick a random one. Note that some require additional setup of their magic attacks. Nodes areas and ports also received complete overhauls, and much of the update development time was spent making Trawel run again after breaking nodes to improve them. Various small features, including witch huts, slums, and world generation also got less major updates and fixes.",
			"b_2: {part 3/3} Some changes were made but not enough to have anything to show, for example summons should work, (which is a far cry from Trawel in 2019, where the concept of a 3 person fight was unthinkable) but there are no skills that summon any creatures yet.",
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
	public static boolean doTutorial;
	public static boolean displayTravelText;
	public static boolean displayFlavorText;
	public static boolean displayLocationalText;
	public static boolean displayOwnName;
	public static boolean displayOtherCombat;
	
	public static boolean doAutoSave = true;
	public static PrintStream logStream;
	
	public static Properties prefs;
	public static File prefFile;
	
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
	
	public static DispAttack dispAttackLookup(String str) {
		for (DispAttack da: DispAttack.values()) {
			if (da.name().equals(str)) {
				return da;
			}
		}
		return null;
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
						return "Changelog, press multiple times to cycle";
					}

					@Override
					public boolean go() {
						extra.println(changelog[changelogViewer++]);
						changelogViewer%=changelog.length;
						return false;
					}});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return " ";
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Infodump Tutorial (partly outdated)";
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
						extra.println("Leveling Terms: WELVL is weapon effective level this starts at 10 and goes up from the Crude tier. LHP is Leveled HP. This is 100 at level 0, and goes up by 10 every level.");
						extra.println("Well, you made it through bootcamp. Have fun!");
						extra.println("-realDragon");
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Display Options";
					}

					@Override
					public boolean go() {
						advancedDisplayOptions();
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
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return " ";
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
						advancedOptions();
						return false;
					}});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return " ";
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
	
	//MAYBELATER: for now, options are only global and there is no 'per save' override
	public static void advancedDisplayOptions() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Changing these display options will change them for all saves.";
					}}
				);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return doTutorial +" Tutorial. (Does not impact story tutorial)";
					}

					@Override
					public boolean go() {
						doTutorial = !doTutorial;
						prefs.setProperty("tutorial",doTutorial+"");
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
						prefs.setProperty("attack_display",attackDisplayStyle.name());
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Change display Chars ("+extra.current_display_style+")";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list = new ArrayList<MenuItem>();
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "Accuracy Hit: " +extra.CHAR_HITCHANCE
												+ " Time Instants: " + extra.CHAR_INSTANTS;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										String dams = "";
										for (DamageType dt: DamageType.values()) {
											dams += dt.getExplain() +"; "; 
										}
										return dams;
										//return "Sharp: " +extra.CHAR_SHARP + " Blunt: " + extra.CHAR_BLUNT + " Pierce: "+extra.CHAR_PIERCE;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "Weight: " +extra.DISP_WEIGHT
												+ " Aether: " + extra.DISP_AETHER
												+ " Agility Multiplier Penalty: " +extra.DISP_AMP
												+ " Qualities: " + extra.DISP_QUALS;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "more than 75% HP: " +extra.HP_I_FULL
												+ "; more than 50% HP: " + extra.HP_I_MOSTLY
												+ "; more than 25% HP: " +extra.HP_I_HALF
												+ "; more than 0% HP: " + extra.HP_I_SOME
												+ "; dead: " + extra.HP_I_DEAD
												;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return ">= 75% damage: " +extra.DAM_I_KILL
												+ "; >= 50% damage: " + extra.DAM_I_HEAVY
												+ "; >= 25% damage: " +extra.DAM_I_SOME
												+ "; <25% damage: " + extra.DAM_I_NONE;
												
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Visual (Full ASCII)";
									}

									@Override
									public boolean go() {
										extra.charSwitchVisual();
										prefs.setProperty("char_style","visual");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Narrator (Narrator friendly ASCII only)";
									}

									@Override
									public boolean go() {
										extra.charSwitchNarrator();
										prefs.setProperty("char_style","narrator");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Emote (Unicode)";
									}

									@Override
									public boolean go() {
										extra.charSwitchEmote();
										prefs.setProperty("char_style","emote");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "None (Visual without HP/Damage Indicators)";
									}

									@Override
									public boolean go() {
										extra.charSwitchNone();
										prefs.setProperty("char_style","none");
										return false;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "In the future there will also be an option that reads from a file.";
									}});
								list.add(new MenuBack());
								return list;
							}});
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Reduce Flavor and Indicator Text";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list = new ArrayList<MenuItem>();
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Flavor Text " + displayFlavorText + " (Covers random town flavor, 'first arrival' flavor, and random taunting/boasting)";
									}

									@Override
									public boolean go() {
										displayFlavorText = !displayFlavorText;
										prefs.setProperty("flavor_text", displayFlavorText+"");
										return false;
									}});
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "Travel Text " + displayTravelText+ " (covers 'You start to travel to X' and the arrival message.)";
										}

										@Override
										public boolean go() {
											displayTravelText = !displayTravelText;
											prefs.setProperty("travel_text", displayTravelText+"");
											return false;
										}});
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "Locational Text " + displayLocationalText + " (covers 'You are in X' and date.)";
										}

										@Override
										public boolean go() {
											displayLocationalText = !displayLocationalText;
											prefs.setProperty("locational_text", displayLocationalText+"");
											return false;
										}});
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "Own Name " + displayOwnName + " (If disabled, 'you' will be used whenever your character's name, title, or fullname would be used. This will not make sense, but might help clarity.)";
										}

										@Override
										public boolean go() {
											displayOwnName = !displayOwnName;
											prefs.setProperty("ownname_text", displayOwnName+"");
											return false;
										}});
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "Other Combat Text " + displayOtherCombat + " (If disabled, no text will be displayed on Person turns that do not include you as an attacker or defender. Does not disable Combat Conditions text, and does disable death messages.)";
										}

										@Override
										public boolean go() {
											displayOtherCombat = !displayOtherCombat;
											prefs.setProperty("othercombat_text", displayOtherCombat+"");
											return false;
										}});
									
								list.add(new MenuBack());
								return list;
							}});
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return advancedCombatDisplay +" Debug Combat Display (HP per attack, attack result notes)";
					}

					@Override
					public boolean go() {
						advancedCombatDisplay = !advancedCombatDisplay;
						prefs.setProperty("debug_attacks",advancedCombatDisplay+"");
						return false;
					}});

				mList.add(new MenuBack());
				return mList;
			}
		});
		try (FileWriter fw = new FileWriter(prefFile)){
			prefs.store(fw, null);
		} catch (Exception e) {
			extra.println("Was not able to save options.");
		}
		
	}
	
	private static void advancedOptions() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "These options deal with reconnecting to Trawel Graphical, and only apply to the Steam version. They only work on the command line, otherwise you may have to restart or use the command line to finish reconnecting.";
					}}
				);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Quick Reconnect";
					}

					@Override
					public boolean go() {
						if (autoConnect) {
							extra.println(extra.PRE_ORANGE+"This option will break your connection! Really continue?");
							if (!extra.yesNo()) {
								return false;
							}
						}
						Networking.connect(6510); 
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Full Reconnect";
					}

					@Override
					public boolean go() {
						if (autoConnect) {
							extra.println(extra.PRE_ORANGE+"This option will break your connection! Really continue?");
							if (!extra.yesNo()) {
								return false;
							}
						}
						extra.println("Port?");
						Networking.connect(extra.inInt(65535)); 
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
			DrawBane.setup();
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
			//WorldGen.initDummyInvs();
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
						return "Trawel Slowstart (base game, rerolling character creator)";
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
			 Player.bag.swapWeapon(new Weapon(1,WeaponType.AXE));
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
		
		prefs = new Properties();
		prefFile = new File("trawel_prefs.properties");//FIXME: properties
		prefFile.createNewFile();
		try (FileReader prefReader = new FileReader(prefFile)){
			prefs.load(prefReader);
			prefReader.close();
		}catch (Exception e) {
			throw e;
		}
		
		
		String charStyle = prefs.getProperty("char_style");
		if (charStyle == null) {
			if (autoConnect) {
				charStyle = "none";
			}else {
				charStyle = "narrator";
			}
		}
		
		switch (charStyle) {
		case "visual":
			extra.charSwitchVisual();
			break;
		case "narrator":
			extra.charSwitchNarrator();
			break;
		case "emote":
			extra.charSwitchEmote();
			break;
		case "none":
			extra.charSwitchNone();
			break;
		}
		advancedCombatDisplay = Boolean.parseBoolean(prefs.getProperty("debug_attacks","FALSE"));
		doTutorial = Boolean.parseBoolean(prefs.getProperty("tutorial","TRUE"));
		attackDisplayStyle = dispAttackLookup(prefs.getProperty("attack_display",DispAttack.TWO_LINE1_WITH_KEY.name()));
		displayTravelText = Boolean.parseBoolean(prefs.getProperty("travel_text","TRUE"));
		displayFlavorText = Boolean.parseBoolean(prefs.getProperty("flavor_text","TRUE"));
		displayLocationalText = Boolean.parseBoolean(prefs.getProperty("locational_text","TRUE"));
		displayOwnName = Boolean.parseBoolean(prefs.getProperty("ownname_text","TRUE"));
		displayOtherCombat = Boolean.parseBoolean(prefs.getProperty("othercombat_text","TRUE"));
		
		if (autoConnect) {
			System.out.println("Please wait for the graphical to load...");
			Networking.handleAnyConnection(legacyConnect ? ConnectType.LEGACY : ConnectType.GDX);
		}else {
			Networking.handleAnyConnection(ConnectType.NONE);
		}
		}catch(Exception e) {
			System.out.println("There was an error when setting up Trawel.");
			e.printStackTrace();
			
			errorHandle(e);
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
		mainGame.log(e.getMessage());
		System.out.println("Error Stacktrace:");
		e.printStackTrace();
		extra.println(extra.PRE_RED+"Trawel has encountered an exception. Please report to realDragon. More details can be found in log.txt.");
		extra.println("PREVIEW, SEE ABOVE IN TEXT FOR FULL MESSAGE: "+ (e.getMessage() != null ? (e.getMessage()) :"null" + e.getStackTrace()));
		extra.println((e.getMessage() != null ? (e.getMessage()) :"null" + e.getStackTrace()));
	}


	//instance methods


	/**
	 * should only call in the wild if you want a duel with no adds or summons
	 * @return combat
	 */
	public static Combat CombatTwo(Person first_man,Person second_man, World w) {
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
		Combat c = new Combat(first_man,second_man, w);//////
		if (c.getNonSummonSurvivors().contains(second_man)) {
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
			AIClass.loot(second_man.getBag(),first_man.getBag(),true,first_man);
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

		return c;
	}
		
		/**
		 * if the fight doesn't involve the player, use the full method, with the World parameter
		 * <br>
		 * LEGACY
		 * @param first_man
		 * @param second_man
		 * @return
		 */
		@Deprecated
		public static Person CombatTwo(Person first_man,Person second_man) {
			return CombatTwo( first_man, second_man,Player.player.getWorld()).getNonSummonSurvivors().get(0);
		}
		public static Combat HugeBattle(World w, List<List<Person>> people){
			return HugeBattle(w,null,people,true);
		}
		
		public static Combat HugeBattle(World w,List<SkillCon> cons, List<List<Person>> people, boolean canLoot){
			Combat battle = new Combat(w,cons,people);
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
			List<Person> lives = battle.getAllSurvivors();
			battle.killed.sort(levelSorter);
			lives.sort(levelSorter);
			
			
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
				if (people.get(i).contains(lives.get(0))) {
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
			lives = battle.getNonSummonSurvivors();//summons count for xp but not loot
			for (Person surv: lives){
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
				if (!surv.isHumanoid()) {
					continue;//skip
				}
				if (canLoot) {
					for (Person kill: battle.killed) {
						if (kill.isPlayer()) {
							continue;//skip
						}
						if (isPlayer) {
							kill.getBag().graphicalDisplay(1,kill);
						}
						AIClass.loot(kill.getBag(),surv.getBag(),false,surv);
					}
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
			
			if (canLoot){
				if (lives.size() > 1) {
					int giveGold = gold/lives.size();
					int giveAether = aether/lives.size();
					if (giveGold > 0) {
						extra.println("The remaining " +World.currentMoneyDisplay(gold) +" is divvied up, "+giveGold +" each.");
					}
					if (giveAether > 0) {
						extra.println("The remaining " + aether +" aether is divvied up, "+giveAether +" each.");
					}
					for (Person surv: lives){
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
					//FIXME: will crash if summon is only one left alive
					Person looter = lives.get(0);
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
			}
			
			battle.endaether = aether + (100 *gold);
			return battle;
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
			if (cheaty || rerolls) {
				if (cheaty) {
					story = new StoryNone();
				}else {
					extra.println("Skip tutorial?");
					if (extra.yesNo()) {
						story = new StoryNone();
					}else {
						story = new StoryTutorial();
					}
				}
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
					extra.getThreadData().world = world;//init
				}
				manOne = RaceFactory.makePlayerValid();
				manTwo = RaceFactory.makePlayerValid();
				if (!displayFight) {
					extra.changePrint(true);
				}
				Combat c = CombatTwo(manOne,manTwo,null);
				manOne = c.getNonSummonSurvivors().get(0);
				story.setPerson(c.killed.get(0), 0);
				//story.setPerson(manTwo, 0);
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
				Networking.clearSides();
			}
			Networking.clearSides();
			player = new Player(manOne);
			manOne.setPlayer();
			//Networking.send("Visual|Race|" + manOne.getBag().getRace().name+  "|");
			Networking.charUpdate();
			assert player.getPerson().getFeatPoints() > 0;
			if (!rerolls) {//autopick first archetype for quickstart?
				player.getPerson().pickFeatRandom();
			}else {
				Archetype.menuChooseFirstArchetype(manOne);
			}
			player.setLocation(world.getStartTown());//also sets the player world
			if (cheaty) {
				Player.player.setCheating();
				story = new StoryNone();
				//player.getPerson().addXp(9999);
				Player.player.addGold(1000);
				Player.bag.addAether(100000);
			}
			story.storyStart();
			player.storyHold = story;
			
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
							WeaponAttackFactory.dispTestWeapon(1,WeaponType.values()[i],weaponMatTesterList);
							WeaponAttackFactory.dispTestWeapon(10,WeaponType.values()[i],weaponMatTesterList);
							WeaponAttackFactory.dispTestWeapon(100,WeaponType.values()[i],weaponMatTesterList);
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
