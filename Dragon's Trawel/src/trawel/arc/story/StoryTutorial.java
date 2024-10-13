package trawel.arc.story;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.core.mainGame;
import trawel.helper.constants.TrawelChar;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.PrintTutorial;
import trawel.personal.Person;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.Feat;
import trawel.personal.classless.Perk;
import trawel.personal.people.Player;
import trawel.towns.data.FeatureData;
import trawel.towns.data.WorldGen;
import trawel.towns.features.Feature;
import trawel.towns.features.fight.Arena;
import trawel.towns.features.fight.Forest;
import trawel.towns.features.fight.Mountain;
import trawel.towns.features.misc.Altar;
import trawel.towns.features.misc.Docks;
import trawel.towns.features.misc.Garden;
import trawel.towns.features.misc.Lot;
import trawel.towns.features.misc.TravelingFeature;
import trawel.towns.features.multi.Inn;
import trawel.towns.features.multi.Slum;
import trawel.towns.features.nodes.Dungeon;
import trawel.towns.features.nodes.Graveyard;
import trawel.towns.features.nodes.Grove;
import trawel.towns.features.nodes.Mine;
import trawel.towns.features.nodes.NodeFeature;
import trawel.towns.features.services.Blacksmith;
import trawel.towns.features.services.Doctor;
import trawel.towns.features.services.Enchanter;
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
		/*
		Print.println();
		Print.println("You come to your senses. Your student, " + killed.getName() + " is dead.");
		Print.println("A wizard cast a curse on them, sending anyone who saw them into a blinding rage. But you are not where you were when you were afflicted...");
		Print.println("You resolve to find out where you are, to start your life anew- it's not like anyone would believe you back home in Oblask, anyway.");*/
		Print.println("1 start Trawel");
		Input.inInt(1);
		Print.println("You should head to the local "+FeatureData.getName(Arena.class,true,false)+". Your immortality will come in handy there.");
		step = "gotoarena1";
	}
	
	@Override
	public void startFight(boolean massFight) {
		boolean disp = battleFam == 0;
		if (disp) {
			/*
			Print.println("Oh jeez, it's been a while since you've fought a stranger, you feel weird.");
			Print.println("A mysterious voice is telling you to \"Choose your attack below\"???");
			Print.println("...");*/
			
			Print.println("Show the Combat Tutorial?");
			disp = Input.yesNo();
			if (disp) {
				PrintTutorial.battleTutorial(massFight);
				if (battleFam == 0) {
					Print.println(TrawelColor.PRE_BATTLE+"Good luck!");
				}
				battleFam = 1;
			}
		}
		if (!disp && offerCombatTutorial()) {
			Print.println("You can review the Combat Tutorial in the Manual.");
		}
		
		combats++;
	}
	
	@Override
	public void winFight(boolean massFight) {
		battleFam =2;
		wins++;
		if (step == "gotoarena2") {
			Print.println("Congratulations on the "+FeatureData.getName(Arena.class,true,false)+" victory! Next you should visit a "+FeatureData.getName(Store.class,true,false)+" to find better equipment.");
			step = "gotostore1";
		}
		if (step == "anyfight1") {
			Print.print("Congratulations on the victory. There's a lot of [act_combat]Combat[revert] to be had in Trawel, and it can be found it many places, both inside and outside of Towns.");
			Print.println(" Next you head to a "+FeatureData.getName(Inn.class,true,false)+". Compass, in the Player Menu under 'Player->Inventory->Map', can take you to 'Homa', the town you started in. 'Unun' is just a few hours walk from there, and it has [f_multi]Trailblazer's Tavern[revert].");
			step = "gotoinn1";
		}
	}


	@Override
	public void onDeath() {
		deaths++;
		switch (deaths) {
		case 1:
			Print.println("Welcome to your first death! In Trawel, dying is a minor setback.");
			PrintTutorial.deathTutorial();
		break;
		default:
			boolean doReminder = false;
			if (levelReminders <= 2) {
				if (deaths == 5 && levelReminders == 0) {
					doReminder = true;
				}else {
					if (Rand.chanceIn(deaths,wins+deaths+combats)) {//you can die outside of combat
						doReminder = true;
					}
				}
			}else {
				if (Rand.chanceIn(1,levelReminders+wins+deaths+combats)) {
					doReminder = true;
				}
			}
			if (!doReminder) {
				Print.println("Rest in pieces.");
				break;
			}

			Print.println("If you're having trouble, it's often best to come back with better gear and more levels than to challenge-spam someone. At least, for your own time investment and sanity.");
			levelReminders++;
			break;
		}
		
	}
	
	@Override
	public void onDeathPart2() {
		if (step == "gotoarena2") {
			Print.println("While you may have lost that [act_combat]fight[revert], you can come back for revenge! Visit a "+FeatureData.getName(Store.class,true,false)+" to find better equipment, and we'll continue.");
			step = "gotostore1";
		}
		if (step == "anyfight1") {
			Print.println("You should keep trying to win a [act_combat]fight[revert].");
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
			Print.println("It looks like there's a [act_combat]fight[revert] about to take place in this "+FeatureData.getName(Arena.class,true,false)+". You should participate in it, to try to get XP and loot.");
			step = "gotoarena2";
			return;//we will explain arenas again if they re-enter
		case "gotostore1":
			if (!(f instanceof Store)) {
				break;
			}
			FeatureData store = FeatureData.getData(Store.class);
			Print.println("Some "+store.fancyNamePlural()+" sell equipment for [pay_aether]Aether[revert], which you mostly find by melting down other equipment you loot in the wild. Others sell DrawBanes for [pay_money]Currency[revert]. "+store.fancyNamePlural()+" are just one way to get equipment, but they can be more reliable than using only on what you loot.");
			Print.println("You should gear up in an equipment "+store.fancyName()+" and then win a [act_combat]fight[revert] to continue!");
			step = "anyfight1";
			return;//we will explain stores directly if they re-enter
		case "gotoinn1":
			if (!(f instanceof Inn)) {
				break;
			}
			Print.println(FeatureData.getData(Inn.class).fancyNamePlural()+" primarily provide room and board, as well random [act_quest]side quests[revert].");
			Print.println("Browse the [act_quest]backrooms[revert], and see if any sidequests suit your fancy. You can complete them as you [act_explore]explore[revert] the wider world, then come back here for the reward.");
			Print.println("[f_guild]Guilds[revert], "+FeatureData.getData(WitchHut.class).fancyNamePlural()+", "+FeatureData.getData(Slum.class).fancyNamePlural()+", and a few other locations can also provide similar [act_quest]sidequests[revert]- and even more can be quest locations, like "+FeatureData.getData(Mountain.class).fancyNamePlural()+" and "+FeatureData.getData(Forest.class).fancyNamePlural()+".");
			Print.println("There are also areas meant for [act_explore]sub-exploration[revert], such as "+FeatureData.getData(Grove.class).fancyNamePlural()+", "+FeatureData.getData(Mine.class).fancyNamePlural()+", "+FeatureData.getData(Dungeon.class).fancyNamePlural()+", amongst other [f_node]Node Exploration[revert] types. The [f_node]Tower of Fate[revert] in Unun and the [f_node]Staircase to Hell[revert] in another world entirely also have bosses. Try to enter one such feature next.");
			step = "gotonode1";
			return;//we will explain inns again if they re-enter
		case "gotonode1":
			if (!(f instanceof NodeFeature)) {
				break;
			}
			Print.println("These areas have a variable number of nodes. Each node has a link to other nodes, and the ability to 'interact' with it. Nodes can be "
					
			+TrawelColor.COLOR_NEW+"{"+TrawelColor.VISIT_NEW+"} unseen[revert],"
			+TrawelColor.COLOR_SEEN+" {"+TrawelColor.VISIT_SEEN+"} seen[revert],"
			+TrawelColor.COLOR_BEEN+" {"+TrawelColor.VISIT_BEEN + "} been[revert],"
			+TrawelColor.COLOR_OWN+" {" +TrawelColor.VISIT_DONE + "} done[revert], "
			+TrawelColor.COLOR_OWN+" {"+TrawelColor.VISIT_OWN + "} owned[revert], "+ "(usually used to indicate you've done an action that will change with time), "
			+TrawelColor.PRE_WHITE+" and "+TrawelColor.COLOR_REGROWN+"{"+TrawelColor.VISIT_REGROWN+ "} regrown[revert],"+" which means that they got replaced since you last visited them.");
			Print.println("The order of nodes presented is often erratic, but the last node you were in will be marked by '([clear]back[revert])'. Some areas will also place nodes that are '[opt_a]deeper[revert]' or '[opt_b]higher[revert]' on the top. One such instance is the [f_node]Tower of Fate[revert] in Unun, which loops back in on itself, but picking the highest choice will always take to up the tower until you reach the top floor.");
			Print.println("While interacting, you might find yourself in a sub-menu, otherwise you can always leave the area by selecting '[opt_exit]exit[revert]'.");
			Print.println("The [f_node]Tower of Fate[revert] and some other features allow you to recruit fellow adventurers. They will only assist in [act_combat]mass battles[revert], and take a share of the loot.");
			Print.println(" ");
			Print.println("You have completed the tutorial section of this story. The game continues on as an open world, up to around level "+WorldGen.highestLevel+".");
			Print.println("There is no more direct guidance to be had, but "+bossPerkTriggers.size() + " boss perks, "+worldPerkTriggers.size() + " world perks, and " + blessPerkTriggers.size() + " blessing perks are tracked by this story.");
			Print.println("You can read about advanced topics and refresh on mechanics in the Manual, found under Player->Manual.");
			Print.println("Happy Traweling!");
			step = "end";
			return;//we explain the subtypes differently if they re-enter
		default: break;
		}
		if (!explained.contains(f.getClass())) {
			explained.add(f.getClass());
			
			FeatureData data = FeatureData.getData(f.getClass());
			if (data != null) {
				data.tutorial();
			}
			/*
			
			if (f instanceof Lot) {
				Print.println("A 'Lot' is a piece of owned, undeveloped land. You can pay both Currency and Aether to build something on that land.");
				return;
			}
			
			if (f instanceof TravelingFeature) {//must be above store, which it extends
				Print.println("Some Towns have stalls for outsiders to set up. They might make a faux-arena, a store, an 'inn' that is just a couple of people with beer, or something else.");
				return;
			}
			
			if (f instanceof WitchHut) {//must be above store, which it extends
				Print.println("Witch Huts let you brew potions with 'DrawBanes'. They also sell them, and host collection quests where you can find DrawBanes for yourself.");
				return;
			}
			if (f instanceof Slum) {//must be above store, which it extends
				Print.println("Districts hold sidequests, backalley vendors, and are often controlled by a Crime Lord. If the Crime Lord is removed, you might be able to pay for reform programs to enfranchise the people there. The cost of such programs will increase with the amount of crime still present.");
				return;
			}
			if (f instanceof Store) {
				Print.println("Stores will sell equipment or other items. For equipment, they'll want Aether. For other items, they'll want Currency. You can increase what items they're willing to sell you by doing favors for Merchant Guilds, if not you might see stock hidden in the back.");
				return;
			}
			if (f instanceof Enchanter) {
				Print.println("Enchanters let you attempt to enchant equipment.");
				return;
			}
			if (f instanceof Arena) {
				Print.println("Arenas let you wait to fight- which could take a while in-game waiting wise, and also match against former victors.");
				return;
			}
			if (f instanceof Docks) {
				Print.println("Docks are Ports- they have water connections to other Towns. They are also assaulted by Drudger armies, which might take temporary control from the Townsfolk. If the Town owns the Docks, they can be used to travel to distant shores much quicker.");
				return;
			}
			if (f instanceof MerchantGuild) {
				Print.println("Merchant Guilds have sidequests, a donation area to get Stores to give you better deals, and some 'buy in bulk' options. Their guild gem of choice is the Emerald.");
				return;
			}
			if (f instanceof HunterGuild) {
				Print.println("Hunter Guilds offer quests to further their goals of cleaning up bandits and monsters from the world. Their guild gem of choice is Amber.");
				return;
			}
			if (f instanceof HeroGuild) {
				Print.println("Hero Guilds will let you buy Feat Fragments with Heroic Reputation. Use these at libraries. They also offer heroic quests. Their guild gem of choice is the Ruby.");
				return;
			}
			if (f instanceof RogueGuild) {
				Print.println("Rogue Guilds will let you launder gems into different gems, for a fee. They also offer mostly criminal quests. Their guild gem of choice is the Sapphire.");
				return;
			}
			if (f instanceof Library) {
				Print.println("At a Library, you can turn in any Feat Fragments you hold to progress on a free Feat Point. If you have 2 or less Feat Picks, you can gain another for free, once per Library.");
				return;
			}
			if (f instanceof Oracle) {
				Print.println("Oracles are in tune with the world, and will mumble on about what someone else is trying to say. This frequently has them repeat utter nonsense, since the Oracle has no context for the message. If you pay them well enough, they will attempt to be helpful by breaking the 4th wall.");
				return;
			}
			if (f instanceof Doctor) {
				Print.println("Doctors can diagnose and cure long-term ailments, most notably the CURSE effect.");
				return;
			}
			if (f instanceof Altar) {
				Print.println("Altars worship a divine force. Sacrificing DrawBanes might please this force- but different forces will want different things.");
				return;
			}
			if (f instanceof Garden) {
				Print.println("Gardens hold multiple plant spots to plant in. Plants grow over time.");
				return;
			}
			if (f instanceof Blacksmith) {
				Print.println("Blacksmith's can create new items for stores, and improve items that you carry.");
				return;
			}
			if (f instanceof Mountain) {
				Print.println("Mountains can be explored, but do not have persistence. They have a fixed number of explores that restores over time.");
				return;
			}
			if (f instanceof Forest) {
				Print.println("Forests can be explored, but do not have persistence. They have a fixed number of explores that restores over time.");
				return;
			}
			
			//TODO: appraiser
			
			if (f instanceof Grove) {
				if (!explainedNodes) {
					Print.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				Print.println("Groves have explorable sub-areas. They are the most living type with subareas, and after you explore one, you might find things regrown when you next visit.");
				return;
			}
			if (f instanceof Mine) {
				if (!explainedNodes) {
					Print.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				Print.println("Mines have explorable sub-areas. The veins present often provide an ample source of Currency.");
				return;
			}
			if (f instanceof Dungeon) {
				if (!explainedNodes) {
					Print.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				Print.println("Dungeons have explorable sub-areas. They often have treasure and many guards.");
				return;
			}
			if (f instanceof Graveyard) {
				if (!explainedNodes) {
					Print.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				Print.println("Graveyards have explorable sub-areas. The darkness will make it hard to see if you don't have the Nightvison Skill.");
				return;
			}
			
			
			//for not covered by a subclass
			if (f instanceof NodeFeature) {
				if (explainedNodes) {
					Print.println("This Node Exploration Feature has no tutorial beyond the basic one.");
				}else {
					Print.println("This is a Node Exploration Feature, continue the main tutorial to learn more.");
				}
				return;
			}
			*/
		}
		
	}
	@Override
	public void levelUp(int level) {
		while (lastKnownLevel < level) {
			lastKnownLevel++;
			switch(lastKnownLevel) {
			case 1:
				Print.println("You have leveled up! You can spend Feat Points in the Character menu through Feat Picks.");
				break;
			case 5:
				Print.println("Every five levels, you have the opportunity to start selecting a new Archetype. Selecting a Feat will delay this choice to the next pick.");
				break;
			case 10:
				Print.println("You are starting to become a force to be reckoned with!");
				break;
			case 15:
				Print.println("You're so high level you could slay a dragon! ...there are no dragons :(");
				break;
			case 20:
				Print.println("A winner is you?");
				break;
			case 25:
				Print.println("A master is you?");
				break;
			case 100:
				Print.println("I'd ask why, but you'd probably say \"because it was there\".");
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
					Print.println("You've slain a Fatespinner and gotten the Fated perk... but can you Beat the Baron? Travel to the world of Greap through the teleporter in Repa, then seek out the [f_node]Staircase to Hell[revert].");
				}else {
					Print.println("You've slain a Fatespinner and gained the Fated perk!");
				}
				break;
			case HELL_BARONESS_1:
				Print.println("You've slain a Hell Baron and gained their throne perk!");
				break;
			case STORYTELLER:
				Print.println("You've outlived a legend and gained the Storyteller perk!");
				break;
			case QUEENSLAYER:
				Print.println("You've slain an ancient evil empress and gained the Queenslayer perk!");
				break;
			}
			if (bossPerkTriggers.isEmpty()) {
				Print.println("You've obtained all tracked boss perks in this version of Trawel!");
			}else {
				Print.println("There are " +bossPerkTriggers.size() + " boss perks left!");
			}
		}
		if (worldPerkTriggers.remove(perk)) {
			switch (perk) {
			case MINE_ALL_VEINS:
				Print.println("You gained the " +perk.friendlyName() + " world perk for cleaning out "
						+ (Player.player.atFeature != null ? Player.player.atFeature.getName() : "a mine")+"!");
				break;
			default:
				Print.println("You gained the " +perk.friendlyName() + " world perk!");
				break;
			}
			if (worldPerkTriggers.isEmpty()) {
				Print.println("You've obtained all tracked world perks in this version of Trawel!");
			}else {
				Print.println("There are " +worldPerkTriggers.size() + " world perks left!");
			}
		}
		if (blessPerkTriggers.remove(perk)) {
			switch (perk) {
			default:
				Print.println("You gained the " +perk.friendlyName() + " blessing perk!");
				break;
			}
			if (blessPerkTriggers.isEmpty()) {
				Print.println("You've obtained all tracked blessing perks in this version of Trawel!");
			}else {
				Print.println("There are " +blessPerkTriggers.size() + " blessing perks left!");
			}
		}
	}
	
	private boolean offerCombatTutorial() {
		return combats < 3 || battleFam < 2;
	}
	
}
