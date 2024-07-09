package trawel.arc.story;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import trawel.core.Networking;
import trawel.core.mainGame;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.Person;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.Feat;
import trawel.personal.classless.Perk;
import trawel.personal.people.Player;
import trawel.towns.data.WorldGen;
import trawel.towns.features.Feature;
import trawel.towns.features.fight.Arena;
import trawel.towns.features.fight.Forest;
import trawel.towns.features.fight.Mountain;
import trawel.towns.features.fight.Slum;
import trawel.towns.features.misc.Altar;
import trawel.towns.features.misc.Docks;
import trawel.towns.features.misc.Garden;
import trawel.towns.features.misc.Lot;
import trawel.towns.features.misc.TravelingFeature;
import trawel.towns.features.nodes.Dungeon;
import trawel.towns.features.nodes.Graveyard;
import trawel.towns.features.nodes.Grove;
import trawel.towns.features.nodes.Mine;
import trawel.towns.features.nodes.NodeFeature;
import trawel.towns.features.services.Blacksmith;
import trawel.towns.features.services.Doctor;
import trawel.towns.features.services.Enchanter;
import trawel.towns.features.services.Inn;
import trawel.towns.features.services.Library;
import trawel.towns.features.services.Oracle;
import trawel.towns.features.services.Store;
import trawel.towns.features.services.WitchHut;
import trawel.towns.features.services.guilds.HeroGuild;
import trawel.towns.features.services.guilds.HunterGuild;
import trawel.towns.features.services.guilds.MerchantGuild;
import trawel.towns.features.services.guilds.RogueGuild;

public class StoryTutorial extends Story{

	private Person killed;
	private int deaths = 0;
	private int wins = 0;
	private int combats = 0;
	private int levelReminders = 0;
	private String step;
	private boolean explainedNodes = false;
	
	private int battleFam;//0 none, 1 fought but didn't win, 2 won a fight
	
	private EnumSet<Perk> bossPerkTriggers = EnumSet.of(Perk.HELL_BARONESS_1,Perk.FATED,Perk.STORYTELLER,Perk.QUEENSLAYER);
	private EnumSet<Perk> worldPerkTriggers = EnumSet.of(Perk.MINE_ALL_VEINS,Perk.CULT_CHOSEN_BLOOD,Perk.CULT_CHOSEN_SKY);
	private EnumSet<Perk> blessPerkTriggers = EnumSet.of(Perk.SKY_BLESS_1,Perk.SKY_BLESS_2,Perk.FOREST_BLESS_1,Perk.FOREST_BLESS_2);
	
	private List<Class<? extends Feature>> explained = new ArrayList<>();
	
	@Override
	public void setPerson(Person p, int index) {
		killed = p;
	}
	
	@Override
	public void storyStart() {
		battleFam = 0;
		extra.println("You are now " + Player.player.getPerson().getName() +".");
		for (Archetype a: Player.player.getPerson().getArchSet()) {
			extra.println("Starting Archetype: " +a.getBriefText());
		}
		for (Feat a: Player.player.getPerson().getFeatSet()) {
			extra.println("Starting Feat: " +a.getBriefText());
		}
		extra.println();
		extra.println("You come to your senses. Your student, " + killed.getName() + " is dead.");
		extra.println("A wizard cast a curse on them, sending anyone who saw them into a blinding rage. But you are not where you were when you were afflicted...");
		extra.println("You resolve to find out where you are, to start your life anew- it's not like anyone would believe you back home in Oblask, anyway.");
		extra.println("1 start Trawel");
		extra.inInt(1);
		extra.println("You should head to the local arena. Your immortality will come in handy there.");
		step = "gotoarena1";
	}
	
	@Override
	public void startFight(boolean massFight) {
		boolean disp = battleFam == 0;
		if (disp) {
			extra.println("Oh jeez, it's been a while since you've fought a stranger, you feel weird.");
			extra.println("A mysterious voice is telling you to \"Choose your attack below\"???");
			extra.println("...");
		}
		if (disp) {
			extra.println("Show the combat tutorial?");
			disp = extra.yesNo();
		}else {
			if (offerCombatTutorial()) {
				extra.println("Display the combat tutorial again?");
				disp = extra.yesNo();
			}
		}
		
		if (disp) {
			extra.println("Higher numbers are better, except in the case of delay.");
			extra.println("Delay is how long an action takes- it determines turn order and skipping.");
			extra.println("For example, two 30 delay actions would go through before one 100 delay action, and then still have 40 instants left.");
			extra.println("Delay on abilities is combined from a warmup and a cooldown. The first number is the warmup- how long until the attack goes through. The second number is the cooldown- how long after that until you can choose another attack.");
			extra.println(
					massFight ?
							extra.CHAR_SHARP+extra.CHAR_BLUNT+extra.CHAR_PIERCE+TrawelColor.PRE_WHITE+" stands for sharp blunt pierce- the three main damage types. Your opponents also have sbp-based armor. Yeah, you got a mass fight for your first battle. Good luck."
							:
								extra.CHAR_SHARP+extra.CHAR_BLUNT+extra.CHAR_PIERCE+TrawelColor.PRE_WHITE+" stands for sharp blunt pierce- the three main damage types. Your opponent also has sbp-based armor."
					);
			extra.println("Hitpoints only matter in combat- you steel yourself fully before each battle, restoring to your current maximum. Very few things can reduce this maximum, the most notable being the CURSE status effect.");
			switch (mainGame.attackDisplayStyle) {
			case CLASSIC:
				extra.println("You have classic display mode on, and will show attacks as if they were plain tables.");
				extra.println("     name                hit    delay    sharp    blunt     pierce");
				extra.println("Classic mode doesn't show you cooldown and warmup- just the combined delay.");
				break;
			case TWO_LINE1: 
				extra.println("You have modern display on, and will see attacks in a hybrid table/label format.");
				extra.println("Instead of only table headers, each cell is labeled. "
						+extra.CHAR_HITCHANCE+" is hitmult. "
						+extra.CHAR_INSTANTS+" is 'instants' (warmup and cooldown time.) "
						+extra.CHAR_SHARP+TrawelColor.PRE_WHITE+" is sharp damage, "
						+extra.CHAR_BLUNT+TrawelColor.PRE_WHITE+" is blunt damage, and "
						+extra.CHAR_PIERCE+TrawelColor.PRE_WHITE+" is pierce damage.");
				extra.println("There are more damage types, such as elemental, but those are beyond the scope of this tutorial.");
				break;
			case TWO_LINE1_WITH_KEY:
				extra.println("You have modern display on with a key/legend, and will see attacks in a hybrid table/label format.");
				extra.println("This display style will provide instructions on how to read the table at the top of each instance.");
				break;
			}
			extra.println("Displayed hit mult isn't a flat percent to hit- when the attack happens, it is a multiplier on your 'hit roll'- just like the enemies' dodge. Whoever rolls the higher number wins. Thus, if your total hit mult equals their total dodge, you have a 50% chance to hit. Most enemies will have a dodge multiplier of less than one.");
			extra.println("Attacks often come with wounds, which have special effects. Good luck!");
			battleFam = 1;
		}
		combats++;
	}
	
	@Override
	public void winFight(boolean massFight) {
		battleFam =2;
		wins++;
		if (step == "gotoarena2") {
			extra.println("Congratulations on the arena victory! Next you should visit a Store to find better equipment.");
			step = "gotostore1";
		}
		if (step == "anyfight1") {
			extra.print("Congratulations on the victory. There's a lot of Combat to be had in Trawel, and it can be found it many places, both inside and outside of Towns.");
			extra.println(" Next you should ingest questionable substances at an Inn. Compass, in the Player Menu under 'Player->Inventory->Map', can take you to 'Homa', the town you started in. 'Unun' is just a few hours walk from there, and it has an Inn.");
			step = "gotoinn1";
		}
	}


	@Override
	public void onDeath() {
		deaths++;
		switch (deaths) {
		case 1:
			extra.println("Welcome to your first death! You continue as normal. If you're in an exploration area, you likely got kicked out of it, otherwise not much changed... except maybe the thing that killed you leveled up.");
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
			}else {
				if (extra.chanceIn(1,levelReminders+wins+deaths+combats)) {
					doReminder = true;
				}
			}
			if (!doReminder) {
				extra.println("Rest in pieces.");
				break;
			}

			extra.println("If you're having trouble, it's often best to come back with better gear and more levels than to challenge-spam someone. At least, for your own time investment and sanity.");
			levelReminders++;
			break;
		}
		
	}
	
	@Override
	public void onDeathPart2() {
		if (step == "gotoarena2") {
			extra.println("While you may have lost that fight, you can come back for revenge! Visit a Store to find better equipment, and we'll continue.");
			step = "gotostore1";
		}
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
				break;
			}
			extra.println("It looks like there's a fight about to take place here. You could wait to participate in it. Winner gets the loser's stuff, apparently.");
			step = "gotoarena2";
			return;//we will explain arenas again if they re-enter
		case "gotostore1":
			if (!(f instanceof Store)) {
				break;
			}
			extra.println("Some stores sell equipment for Aether, which you mostly find by melting down other equipment you loot in the wild. Others sell DrawBanes for Currency. Stores are just one way to get equipment, but they can be more reliable than relying only on what you loot.");
			extra.println("You should gear up in an equipment shop and then win a fight to continue!");
			step = "anyfight1";
			return;//we will explain stores directly if they re-enter
		case "gotoinn1":
			if (!(f instanceof Inn)) {
				break;
			}
			extra.println("The Inn has 'beer' (you hope it's actually beer) which can raise your HP for as many fights as you buy beer for... but somewhat more importantly, random side quests.");
			extra.println("Browse the backrooms, and see if any quests suit your fancy. In general, its much more fun to explore, but sidequests can help you if you're having trouble justifying going into the scary wider world.");
			extra.println("Guilds, Witch Huts, Districts, and a few other locations can also provide similar sidequests- and even more can be quest locations, like Mountains and Forests.");
			extra.println("There are also areas meant for sub-exploration, such as Groves, Mines, Dungeons, and Graveyards. The Tower of Fate in Unun and the Staircase to Hell in another world entirely also have bosses. Try to enter one such feature next.");
			step = "gotonode1";
			return;//we will explain inns again if they re-enter
		case "gotonode1":
			if (!(f instanceof NodeFeature)) {
				break;
			}
			extra.println("These areas have a variable number of nodes, seen below. Each node has a link to other nodes, and the ability to 'interact' with it. Nodes can be "
					
			+TrawelColor.COLOR_NEW+"{"+TrawelColor.VISIT_NEW+"} unseen,"+TrawelColor.PRE_WHITE
			+TrawelColor.COLOR_SEEN+" {"+TrawelColor.VISIT_SEEN+"} seen,"+TrawelColor.PRE_WHITE
			+TrawelColor.COLOR_BEEN+" {"+TrawelColor.VISIT_BEEN + "} been,"+TrawelColor.PRE_WHITE
			+TrawelColor.COLOR_OWN+" {" +TrawelColor.VISIT_DONE + "} done, "+TrawelColor.PRE_WHITE
			+TrawelColor.COLOR_OWN+" {"+TrawelColor.VISIT_OWN + "} owned, "+TrawelColor.PRE_WHITE+ "(usually used to indicate you've done an action that will change with time), "
			+TrawelColor.PRE_WHITE+" and "+TrawelColor.COLOR_REGROWN+"{"+TrawelColor.VISIT_REGROWN+ "} regrown,"+TrawelColor.PRE_WHITE+" which means that they got replaced since you last visited them.");
			extra.println("The order of nodes presented is often erratic, but the last node you were in will be marked by '(back)'. Some areas will also place nodes that are 'deeper' or 'higher' on the top. One such instance is the Tower of Fate in Unun, which loops back in on itself, but picking the highest choice will always take to up the tower until you reach the top floor.");
			extra.println("While interacting, you might find yourself in a sub-menu, otherwise you can always leave the area by selecting the last option.");
			extra.println(" ");
			extra.println("You have completed the tutorial section of this story. The game continues on as an open world, up to around level "+WorldGen.highestLevel+".");
			extra.println("There is no more direct guidance to be had, but "+bossPerkTriggers.size() + " boss perks, "+worldPerkTriggers.size() + " world perks, and " + blessPerkTriggers.size() + " blessing perks are tracked by this story.");
			extra.println("Happy Traweling!");
			step = "end";
			return;//we explain the subtypes differently if they re-enter
		default: break;
		}
		if (!explained.contains(f.getClass())) {
			explained.add(f.getClass());
			
			if (f instanceof Lot) {
				extra.println("A 'Lot' is a piece of owned, undeveloped land. You can pay both Currency and Aether to build something on that land.");
				return;
			}
			
			if (f instanceof TravelingFeature) {//must be above store, which it extends
				extra.println("Some Towns have stalls for outsiders to set up. They might make a faux-arena, a store, an 'inn' that is just a couple of people with beer, or something else.");
				return;
			}
			
			if (f instanceof WitchHut) {//must be above store, which it extends
				extra.println("Witch Huts let you brew potions with 'DrawBanes'. They also sell them, and host collection quests where you can find DrawBanes for yourself.");
				return;
			}
			if (f instanceof Slum) {//must be above store, which it extends
				extra.println("Districts hold sidequests, backalley vendors, and are often controlled by a Crime Lord. If the Crime Lord is removed, you might be able to pay for reform programs to enfranchise the people there. The cost of such programs will increase with the amount of crime still present.");
				return;
			}
			if (f instanceof Store) {
				extra.println("Stores will sell equipment or other items. For equipment, they'll want Aether. For other items, they'll want Currency. You can increase what items they're willing to sell you by doing favors for Merchant Guilds, if not you might see stock hidden in the back.");
				return;
			}
			if (f instanceof Enchanter) {
				extra.println("Enchanters let you attempt to enchant equipment.");
				return;
			}
			if (f instanceof Arena) {
				extra.println("Arenas let you wait to fight- which could take a while in-game waiting wise, and also match against former victors.");
				return;
			}
			if (f instanceof Docks) {
				extra.println("Docks are Ports- they have water connections to other Towns. They are also assaulted by Drudger armies, which might take temporary control from the Townsfolk. If the Town owns the Docks, they can be used to travel to distant shores much quicker.");
				return;
			}
			if (f instanceof MerchantGuild) {
				extra.println("Merchant Guilds have sidequests, a donation area to get Stores to give you better deals, and some 'buy in bulk' options. Their guild gem of choice is the Emerald.");
				return;
			}
			if (f instanceof HunterGuild) {
				extra.println("Hunter Guilds offer quests to further their goals of cleaning up bandits and monsters from the world. Their guild gem of choice is Amber.");
				return;
			}
			if (f instanceof HeroGuild) {
				extra.println("Hero Guilds will let you buy Feat Fragments with Heroic Reputation. Use these at libraries. They also offer heroic quests. Their guild gem of choice is the Ruby.");
				return;
			}
			if (f instanceof RogueGuild) {
				extra.println("Rogue Guilds will let you launder gems into different gems, for a fee. They also offer mostly criminal quests. Their guild gem of choice is the Sapphire.");
				return;
			}
			if (f instanceof Library) {
				extra.println("At a Library, you can turn in any Feat Fragments you hold to progress on a free Feat Point. If you have 2 or less Feat Picks, you can gain another for free, once per Library.");
				return;
			}
			if (f instanceof Oracle) {
				extra.println("Oracles are in tune with the world, and will mumble on about what someone else is trying to say. This frequently has them repeat utter nonsense, since the Oracle has no context for the message. If you pay them well enough, they will attempt to be helpful by breaking the 4th wall.");
				return;
			}
			if (f instanceof Doctor) {
				extra.println("Doctors can diagnose and cure long-term ailments, most notably the CURSE effect.");
				return;
			}
			if (f instanceof Altar) {
				extra.println("Altars worship a divine force. Sacrificing DrawBanes might please this force- but different forces will want different things.");
				return;
			}
			if (f instanceof Garden) {
				extra.println("Gardens hold multiple plant spots to plant in. Plants grow over time.");
				return;
			}
			if (f instanceof Blacksmith) {
				extra.println("Blacksmith's can create new items for stores, and improve items that you carry.");
				return;
			}
			if (f instanceof Mountain) {
				extra.println("Mountains can be explored, but do not have persistence. They have a fixed number of explores that restores over time.");
				return;
			}
			if (f instanceof Forest) {
				extra.println("Forests can be explored, but do not have persistence. They have a fixed number of explores that restores over time.");
				return;
			}
			
			//TODO: appraiser
			
			if (f instanceof Grove) {
				if (!explainedNodes) {
					extra.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				extra.println("Groves have explorable sub-areas. They are the most living type with subareas, and after you explore one, you might find things regrown when you next visit.");
				return;
			}
			if (f instanceof Mine) {
				if (!explainedNodes) {
					extra.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				extra.println("Mines have explorable sub-areas. The veins present often provide an ample source of Currency.");
				return;
			}
			if (f instanceof Dungeon) {
				if (!explainedNodes) {
					extra.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				extra.println("Dungeons have explorable sub-areas. They often have treasure and many guards.");
				return;
			}
			if (f instanceof Graveyard) {
				if (!explainedNodes) {
					extra.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				extra.println("Graveyards have explorable sub-areas. The darkness will make it hard to see if you don't have the Nightvison Skill.");
				return;
			}
			
			
			//for not covered by a subclass
			if (f instanceof NodeFeature) {
				if (explainedNodes) {
					extra.println("This Node Exploration Feature has no tutorial beyond the basic one.");
				}else {
					extra.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				return;
			}
		}
		
	}
	@Override
	public void levelUp(int level) {
		while (lastKnownLevel < level) {
			lastKnownLevel++;
			switch(lastKnownLevel) {
			case 1:
				extra.println("You have leveled up! You can spend Feat Points in the Character menu through Feat Picks.");
				break;
			case 5:
				extra.println("Every five levels, you have the opportunity to start selecting a new Archetype. Selecting a Feat will delay this choice to the next pick.");
				break;
			case 10:
				extra.println("You are starting to become a force to be reckoned with!");
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
			case 100:
				extra.println("I'd ask why, but you'd probably say \"because it was there\".");
				break;
			}
		}
	}
	
	@Override
	public void perkTrigger(Perk perk) {
		if (bossPerkTriggers.remove(perk)) {
			switch (perk) {
			case FATED:
				if (bossPerkTriggers.contains(Perk.HELL_BARONESS_1)) {
					extra.println("You've slain a Fatespinner and gotten the Fated perk... but can you Beat the Baron? Travel to the world of Greap through the teleporter in Repa, then seek out the Staircase to Hell.");
				}else {
					extra.println("You've slain a Fatespinner and gained the Fated perk!");
				}
				break;
			case HELL_BARONESS_1:
				extra.println("You've slain a Hell Baron and gained their throne perk!");
				break;
			case STORYTELLER:
				extra.println("You've outlived out a legend and gained the Storyteller perk!");
				break;
			case QUEENSLAYER:
				extra.println("You've slain an ancient evil empress and gained the Queenslayer perk!");
				break;
			}
			if (bossPerkTriggers.isEmpty()) {
				extra.println("You've obtained all tracked boss perks in this version of Trawel!");
			}else {
				extra.println("There are " +bossPerkTriggers.size() + " boss perks left!");
			}
		}
		if (worldPerkTriggers.remove(perk)) {
			switch (perk) {
			case MINE_ALL_VEINS:
				extra.println("You gained the " +perk.friendlyName() + " world perk for cleaning out "
						+ (Player.player.atFeature != null ? Player.player.atFeature.getName() : "a mine")+"!");
				break;
			default:
				extra.println("You gained the " +perk.friendlyName() + " world perk!");
				break;
			}
			if (worldPerkTriggers.isEmpty()) {
				extra.println("You've obtained all tracked world perks in this version of Trawel!");
			}else {
				extra.println("There are " +worldPerkTriggers.size() + " world perks left!");
			}
		}
		if (blessPerkTriggers.remove(perk)) {
			switch (perk) {
			default:
				extra.println("You gained the " +perk.friendlyName() + " blessing perk!");
				break;
			}
			if (blessPerkTriggers.isEmpty()) {
				extra.println("You've obtained all tracked blessing perks in this version of Trawel!");
			}else {
				extra.println("There are " +blessPerkTriggers.size() + " blessing perks left!");
			}
		}
	}
	
	private boolean offerCombatTutorial() {
		return combats < 3 || battleFam < 2;
	}
	
}
