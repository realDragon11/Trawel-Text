package trawel.arc.misc;

import derg.menus.MenuSelect;
import trawel.core.Input;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;

public class Changelog {
	//b__X is in development, b_X is the actual release of that version
		public static final String VERSION_STRING = "v0.8.b__13";
		public static final String VERSION_DATE = " updated July 23rd 2024";
		public static final String[][] changelog = new String[][] {
			//add to front, changeviewer cycles to older ones when used
			{"b_13",
				//test color changes
				TrawelColor.ADVISE_3+"Be advised, this is a color test "+TrawelColor.ADVISE_5+"that just tested."+TrawelColor.COLOR_RESET+" And then this should be back."
			},
			{"b_12",
				//discuss death punishments
				"Dying adds a Punishment Effect to your character, as with attribute contests in earlier versions. These can be cured as normal. They make it harder to win battles.",
				//discuss cheat mode
				"Cheat Trawel can now be enabled on existing saves, and finer tuned Cheats are available for those who want less of a boost.",
				//discuss new save system
				"Trawel has been updated to Java 21 and the Fury saving system. The first save or load each boot will take longer, but subsequent ones should be faster or the same speed. As with the last save system update, this might get reverted if it causes issues on other computers.",
				//discuss examine and combat summary
				"The combat summary that displays to introduce enemies has been reduced to only the important defensive info. The Examine action in battle is broken up into different sub-segments in a menu to allow more frequent usage without truly massive dumps.",
				//discuss beach and regrowing
				"Beach added as a new Node Feature. The Beach contains a wide area broken into branches which contain Caves, gated Dungeons, and deeper Beaches. Clarity can be used to open Doors and Chests in Node Features, rounding out the attributes. Node regrowth has been patched and adjusted in Node Exploration features.",
				//discuss drawbane changes
				"The draw and bane effects of DrawBanes are capped at 5 total. Cloth hides wealth, blood requires more blood to trigger, and you can view blood, wealth, and daylight meters from the Status subsection of the Player Menu.",
				//discuss people creation changes
				"More types of NPCs can spawn with free bonus Feats or Archetypes, but more loot. AI Jobs (classes) now have preferred starting Archetypes, which makes them tend towards certain builds. Muggers now have two primary classes- Thieves and Thugs. Creatures can no longer learn new Archetypes after generation, restricting their Feat gain.",
				//discuss graphical changes
				"Added a second art set to the Graphical, along with some new sounds for unicorns. This new art set is planned to fully replace the current one in time, and has been changed to the default."
			},
			{"b_11",
				//discuss magic overall
				"Magic skill stances have all been buffed to around iron tier damage. Some of the attacks have also had bonus effects added. In the future, more bonus effects might be added, or their damage further buffed, depending on the direction magic stances go in, but this should make them playable for now.",
				//discuss wound changes
				"Most wounds now inflict more damage and penalties, getting a large overhaul. Sharp wounds focus on damage or attacking quickly, Pierce wounds focus on impairing your target, and Blunt/Ignite/Frost/Shock wounds have a mix. The chances of rolling each type of wound have also been adjusted to lean into this.",
				//discuss material changes
				"High end materials had their stats lowered and were given tradeoffs. Mythril has lower defense stats but remains all around solid and enchantable. Adamantine lose some enchant chance and resist, especially blunt resist. Sunsteel was brought up to adamantine levels, but remains unenchantable. Solar Gold gained some more blunt resist.",
				//discuss curse changes
				"Curse inflicting skills have been replaced. Normal NPCs can no longer inflict curse, which should lessen doctor visits. Doctors no longer cure Tired and Bees, but the Wounded punishment effect was added in some places where the Damaged effect was used prior.",
				//discuss new skillstuff
				"Slowstart now allows selecting Feats, not just Archetypes. Misc New Skills: Fetid Fumes, Fever Strike, No Quarter, Bulk, Big Bag, Press Advantage, Runesmith, Runic Blast, Open Vein, Living Armor, Aggress Parry, Salvage, Chef, Ignite Boost, Frost Boost, Elec Boost; Misc New Feats: Heavyweight, Swift, Cocooned; Misc New Archetypes: Rune Blade, Cut Throat, Comestible Critic.",
				//discuss drawbane bag changes
				"Increased drawbane inventory size to 5 base and the Big Bag skill increases it to 8. Feat Fragments no longer take up space.",
				//discuss attribute changes
				"Strength no longer doubles up on physical damage bonus. Clarity now applies as an elemental damage multiplier.",
				//discuss new exploration content
				"Sky Cult added to Mountains. Locked Doors now require an attribute contest to open, and on failure you have Burnout applied.",
				"Some bumpers now steal currency from you on loss against them. Gems have been tweaked in rarity, Rubies are also granted as a gift for killing bosses in nodes.",
			},
			{"b_10 hotfixes",
				"pouch swap rejection crash, dryad refight crash, dungeon regrow wrong checkpoint type, more perks have attributes, buffed magic attacks and some related archetypes and feats.",
				"fixed fort menu crashes, improved timeevent error reporting.",
				"increased world currency amount for most humanoid NPCs, attempted to fix dungeon party crash, fixed Inn rent waiting and let you rent more if you have less than 1 month left.",
			},
			{"b_10",
				"You can travel through multiple towns at the same time by using the Compass from your Map, in your Inventory. This can result in multiple random encounters.",
				"Curse, Tired, Damaged, and Bees are now punishment longer lasting effects. Tired can be cured when resting, Bees can be cured by entering water. Curse is still cured at Doctors, which heals everything but Damaged. Damaged requires Blacksmith repairs. These effects will be used to give a downside to failing Attribute checks, so they're not just free rewards with no consequences.",
				"This punishment system can be seen in the 'Trapped Treasure Chambers' in Mines and Dungeons, which have their own small set of traps and mechanics for those that would seek to loot them. Failing checks applies Burnout which prevents further ones until you rest or cure. Rolls that aren't prevented (such as mountain rocks) have their attributes halved.",
				"Aether is now used entirely for buying/selling equipment. Drawbanes still use World Currency. Enchanters no longer let you sell aether. This changes are meant to make each currency serve a purpose and let them be more common without dealing with exchanging balancing problems.",
				"Added Drudger, Fell, Monster, and Animal Cleanse Quests. Almost all targets will award progress if encountered in Node Explorations or other features as well, with some exceptions for mooks. Some fightable hunters now reward Amber when looted. Witch Huts have more Collect targets.",
				"Random encounter frequency was bugged, is now a lot more common. Vampires and some other Bumpers will appear less during the daytime. Bandits will prefer money, not just valuable metals. Removed most bumper level requirements, and updated wolf pack with player allies. There are more Primal Deathcheater types.",
				"The first Node in Features will not have forced combat, and the first two layers will have less free loot and less obstacle fights.",
				"log.txt has full stack trace. Updated tutorial's feature explanations and list of world perks. Mines now properly weight their contents. Graveyard's Nightvison behavior has been fixed. Connection failure message will only play once, after 10 seconds.",
				"Some less prominent bugfixes can be found in the Steam patch notes and github commit history.",
			},
			{"b_9",
				"Expanded Bag items can now be melted down into aether from player menu or if Bag is full when looting. 'Really Attack' prompts give the option to examine the Person's inventory.",
				"Plant Spots, Drawbanes, and Alchemy have recieved interconnected improvements, most notably a few extra potion combos. Altars now can have Forest blessings. Traveling features have new special behavior, and you can rent rooms at Inns.",
				"Added the Enchanter Town Feature, which can enchant or improve enchantments on Weapons and Armor. It can also allow you to sell Aether for World Currency. The Blacksmith can now temper armor to remove negative traits at the cost of Amber Gems.",
				"Changed internal Sidequest code to save properly, tweaked quest generation behavior, added quests to the Rogue, Hero, and new Hunter's guild. Added more Quest Reactions.",
				"Increased positive reputation gain from combat, decreased negative gain. Added Amber as the Hunter's gem, and updated world currency/gem costs and rewards.",
				"Adjusted Wounds, importantly adding more elemental wounds.",
				"Added more Classless Skills and Feats: Beer Belly, Deadly Aim, Glutton, Life Mage, Shaman, Potion Chugger.",
				"Expanded Greap worldgen map, filling out some more towns and adding many town locations. Route types have been expanded to include caravans and lesser traveled paths, which have different travel speeds and NPC interests.",
				"Added Simplified attack display style, moved attack notes from Combat Debug to its own option.",
			},
			{"b_8",
				"Added a wait option to Arenas and prevented giant time chunks by accident, made Arenas fight without the player and have the winners move on after enough training. Players always get a new fighter in their first round (so entirely for 1 round titles), but more rounds are done entirely naturally now, potentially drawing from survivors.",
				"Updated Lot menu, standardized collecting earned money from built Features. Features can now have intro/outro text, which can be turned off as a display option. Node Features now have autosave enabled while exploring them, and saves once again use a different date format.",
				"Created unique stances for Fish Anchor, Fish Spear, and Lance which all used other weapon stances as placeholders since the weapon updates. Two stances still need migration. Grazes have been renamed Negates and Graze is now used to indicate no rolled wound. Armor Quality value and fitness updated, which mostly matters for AI, as well as Weapon Quality price.",
				"Cleaned up Wound/Effect/Tactic text and code, added a few new Skills. Archetypes now grant more skills, as well as stats. Some new archetypes were added.",
				"More insults added to species, materials with extreme outlier elemental stats reigned in.",
			},
			{"b_7",
				"Nodes display inward/outward to indicate going deeper or closer to the entrance (in most cases, ultimately its merely a 'floor' counter).",
				"Updated to Kryo serialization, Java 8 to Java 17 migration failed. ",
			},
			{"b_6",
				"Updated stat displays and item 'higher/lower is better' displays. Tutorial text separated from 'feature description' text.",
				"Added many new towns to the Eoano WorldGen, and fluffed up the lore and flavor of existing towns as well. Species store re-added to Arona. Most bosses are now non-unique in that you don't have only one option if you want their Perk. Ancient Queen boss added to new island.",
				"Cleaned up Greap WorldGen, although it is still not a complete world.",
				"Some Features got larger improvements as part of the WorldGen updates: Districts now can house sub-stores. Gardens now start filled, and refill over time, and their changes in growth speed are more distinct for worldgen gardens. Several node features had changes to what nodes are likely to generate in them, and also their levels, which now display as a range when viewing from the town. Added Forest Altar, making altars now be dedicated to different primal forces.",
				"Some titles were changed, others were added. Steam version gained several new leaderboards and achievements.",
			},
			{"b_5",
				"You can now recruit adventurers in certain dungeons. All other dungeons had their mass battles removed. In 'dangerous' dungeons, these adventurers will assist you in the 'high security' checkpoints, and against the boss. Currently this is just the Tower of Fate and the Fatespinner. They will get a share of the loot, and if higher level than you, might get first pick. They will not be interested in joining you if they are 2 or more levels higher. They will not revive on death, and will leave if you do not visit the dungeon for a while. Fighting with them will make them take longer to leave than just visiting will.",
				"Passive population migration has been fixed and tweaked, now if you play close attention you might be able to see persons moving from town to town. They have a slight preference to move towards towns with Inns, but also to not overcrowd a town past what its features can support.",
				"All Features and Nodes now have updated behavior to update the background in the legacy graphical, and often fall back to different art now, since not all of it was made. (Same as the treatment armor, people, and weapons got). The 'examine closely' for weapons has been improved, and armor qualities also appear in world again, with slightly different functionalities.",
				"Necks and Heads best damage mult is 2.5x damage instead of 3x, reducing the 'neck meta' of AI's absurd alpha strikes.",
			},
			{"b_4",
				"Added 'back out' option to terminal (graphical will get it when it gets other updates). Added a few new display options, check the reduced indicator menu again, and also various 'realtime waiting' options. Added 'tactics' which are persistent action options in combat that don't deal damage (requires skills), and also some skill stances which apply a tactic effect and also a normal attack.",
				"Changed up condition wounds a fair bit, turning them into an injury system to constrast the typically short-lived nature of normal wounds in combat. Getting brained or shattered can hurt quite a bit. A boss, 'Yore' was also added to the Dungeon of Fate, with a new battle condition-based dungeon mechanic. Baron of Hell also got a demon bodytype, and cloth was turned into patchwork, linen, and cotton.",
				"Added a 'pouch/bag' feature to store equipment, currently works quite well outside of making room for things you're not currently comparing. You menu reforged into 'Player' menu, which can save in Nodes now. Includes character-exclusive settings like autolevel, autoloot, and autobattle.",
				"Enchanced compass into a full map, with directions to any visited town (and always Unun), and also town flavor in that menu, in addition to population and feature counts, timezone, etc. Display current time in hours and minutes in town menu. Society Titles have been expanded.",
			},
			{"b_3",
				"Tutorial mostly moved from 'feature text' to 'story'. Added saveable display options. Known issues: combat town Features need another cleanup with the new currency, mass battles have some balancing issues if they have an un-even number of fighters.",
				"Subtle changes to battle conditions. Effective level added, so level 2 is only a flat +10% better, not 2x better, which each further level being another +10% (level 3 is 1.3x mult, because level 1 is 1.1x). Stat tweaks on most species, some non-'personable' creatures now have barks and can loot money.",
				"Many town Feature mechanics had small tweaks, Brewing being the most visible in regards to how DrawBanes are talked about, and once again Merchant Guild prices, Fort Hall prices, and the more one-off Doctor prices. 'Highest Contribution' battlescore stat got split into the more sensible 'highest and lowest % deviation from average', which can show the extremes in attack balance a weapon has, for example a gold weapon being good at slapping but nothing else.",
			},
			{"b_2",
				"Attack backend changes became frontend changes for weapons, which now have different stat displays. This also made armor and dodging actual things again, before their average stats were a bit too low, now the forumlas have been re-tested and remade. 'classless' system (essentially multiclassing but with many multis) has born fruit, you can now use the replacement system, although it still has a long ways to go.",
				"If you want to pick your own Archetype to start with, use slowstart, otherwise it will pick a random one. Note that some require additional setup of their magic attacks. Nodes areas and ports also received complete overhauls, and much of the update development time was spent making Trawel run again after breaking nodes to improve them. Various small features, including witch huts, slums, and world generation also got less major updates and fixes.",
				"Some changes were made but not enough to have anything to show, for example summons should work, (which is a far cry from Trawel in 2019, where the concept of a 3 person fight was unthinkable) but there are no skills that summon any creatures yet.",
			},
			{"b_1",
				"base attack code reworked in basically every way. currency divided. threading added (nothreads is an arg), time passing redone. Node exploration mostly same but had entire backend update. Locational damage exists but does little at the moment."
			},
			{"Notes",
				"End of current beta ingame changelog. Check the Github and Steam for prior updates and more detailed notes."
			}
		};
		public static int changelogViewerIndex = 1;
		public static int changelogViewerVersion = 0;
		
		public static MenuSelect getMenu() {
			return new MenuSelect() {

				@Override
				public String title() {
					return "Changelog";
				}

				@Override
				public boolean go() {
					changelog: do {
						Print.println(changelog[changelogViewerVersion][0]
								+ " part "+changelogViewerIndex+"/"+(changelog[changelogViewerVersion].length-1) +": "
								+ changelog[changelogViewerVersion][changelogViewerIndex]);
						//log = log.replace("@", "{part "+(1+changelogViewerIndex)+"/"+changelog[changelogViewerVersion].length+"}");
						
						Print.println("1 "+(changelogViewerIndex == changelog[changelogViewerVersion].length-1 ? "Next." : "More."));
						Print.println("2 Next update.");
						Print.println("9 Back.");
						switch (Input.inInt(2,true, true)) {
						case 1:
							changelogViewerIndex++;
							if (changelogViewerIndex >= changelog[changelogViewerVersion].length) {
								changelogViewerIndex = 1;
								changelogViewerVersion++;
								changelogViewerVersion%=changelog.length;
							}
							break;
						case 2:
							changelogViewerIndex = 1;
							changelogViewerVersion++;
							changelogViewerVersion%=changelog.length;
							break;
						default: case 9:
							break changelog;
								
						}
					}while(true);
					
					return false;
				}};
		}
}
