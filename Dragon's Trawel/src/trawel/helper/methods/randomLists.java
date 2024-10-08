package trawel.helper.methods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import derg.strings.fluffer.StringNum;
import derg.strings.fluffer.StringResult;
import derg.strings.random.SRGapShuffle;
import derg.strings.random.SRInOrder;
import derg.strings.random.SRPlainRandom;
import trawel.battle.Combat.ATK_ResultCode;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.core.mainGame;
import trawel.helper.constants.TrawelColor;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.towns.features.services.Oracle;

/**
 * @author dragon
 * 2/8/2018
 * Some random lists that are used elsewhere, put here for cleanliness.
 */
public class randomLists {
	//holding lists, internal
	//initers
	private static StringResult commonElements, rareElements, randMats = null, colorList, normalFirstNames, theTitles,
			doerTitles, animals,plants,
			wolfNames,wolfPrefixes,wolfSuffixes,wolfAdjPrefixes,wolfAdjSuffixes,
			bearAdjs, bearPrefixes, bearSuffixes,
			batAdjs, batNames,
			entTrees,entThings,
			drudgerStarters,drudgerMiddles,drudgerEnds,
			fighterTypes,drifterTypes,thiefTypes,thugTypes,pirateTypes,collectTypes,theAlphaTitles,theLargeTitles, attackMisses, attackNegates, attackDodges
			,hunterTitles,thingsToSlay,slayerTitleToSlay,dGuardTitles,oldTitles, colorListPrintable,
			violateForestQuote, chestAdjectives
			;
	/**
	 * following have fallbacks if not loaded, and you can avoid filling them in for test purposes
	 */
	private static StringNum powerAdj = new SRInOrder(""), powerAdjFront = new SRInOrder(""),
			enchantHitIgnite = new SRInOrder(""),enchantHitFrost = new SRInOrder(""),enchantHitElec = new SRInOrder(""),enchantHitKeen = new SRInOrder("")
			;
	//NOTE: any =null means it should use a lazyloaded getter instead
	public static void init() {
		commonElements = new SRPlainRandom(Arrays.asList("earth","wind","fire","air","water","ice","storm","thunder","flame","lightning","frost","stone"));
		rareElements = new SRPlainRandom(Arrays.asList("grass","metal","mountain","ocean","sky","flesh","life","death","balance","rust","pie","math","soul","heart","word"));
		colorList = new SRPlainRandom("green","blue","yellow","purple","red","orange","white","black","grey","cyan","silver","maroon","rose","chestnut","vermilion","russet","scarlet","rust","auburn","mahogany","pumpkin","chocolate","copper","bronze","sepia","ochre","brown","cinnamon","tan","orange","peach","goldenrod","amber","saffron","ecru","gold","pearl","buff","flax","brass","lemon","cream","beige","olive","ivory","lime","chartreuse","emerald","jade","aquamarine","turquoise","teal","aqua","cerulean","charcoal","azure","cobalt","lavender","periwinkle","amethyst","violet","indigo","heliotrope","liliac","plum","fuchsia","mauve","puce","crimson","pink","cardinal","carmine");
		//TODO:
		colorListPrintable = new SRPlainRandom(
			TrawelColor.PRE_GREEN+"Green",TrawelColor.PRE_BLUE+"Blue",TrawelColor.PRE_YELLOW+"Yellow",TrawelColor.PRE_MAGENTA+"Purple"
			,TrawelColor.PRE_RED+"Red",TrawelColor.PRE_ORANGE+"Orange",TrawelColor.PRE_WHITE+"White",TrawelColor.TIMID_GREY+"Black"
			,TrawelColor.TIMID_GREY+"Grey",TrawelColor.PRE_BLUE+"Cyan",TrawelColor.PRE_BLUE+"Silver",TrawelColor.PRE_ORANGE+"Maroon"
			,TrawelColor.PRE_RED+"Rose",TrawelColor.PRE_ORANGE+"Chestnut",TrawelColor.TIMID_RED+"Vermilion",TrawelColor.TIMID_RED+"Russet"
			,TrawelColor.PRE_RED+"Scarlet",TrawelColor.PRE_ORANGE+"Rust",TrawelColor.PRE_ORANGE+"Auburn"
			,TrawelColor.TIMID_RED+"Mahogany",TrawelColor.PRE_ORANGE+"Pumpkin",TrawelColor.PRE_ORANGE+"Chocolate"
			,TrawelColor.PRE_ORANGE+"Copper",TrawelColor.PRE_ORANGE+"Bronze",TrawelColor.TIMID_GREY+"Sepia",TrawelColor.TIMID_RED+"ochre"
			,TrawelColor.PRE_ORANGE+"Brown",TrawelColor.PRE_ORANGE+"Cinnamon",TrawelColor.PRE_ORANGE+"Tan",TrawelColor.PRE_ORANGE+"orange"
			,TrawelColor.PRE_YELLOW+"Peach",TrawelColor.PRE_YELLOW+"Goldenrod",TrawelColor.PRE_YELLOW+"Amber","Saffron"
			,"Ecru",TrawelColor.PRE_YELLOW+"Gold",TrawelColor.PRE_WHITE+"Pearl","Buff","Flax",TrawelColor.PRE_ORANGE+"Brass"
			,TrawelColor.PRE_YELLOW+"Lemon",TrawelColor.PRE_YELLOW+"Cream",TrawelColor.PRE_ORANGE+"Beige",TrawelColor.PRE_ORANGE+"Olive"
			,TrawelColor.PRE_WHITE+"Ivory",TrawelColor.PRE_GREEN+"Lime","Chartreuse",TrawelColor.PRE_GREEN+"Emerald",TrawelColor.PRE_GREEN+"Jade"
			,TrawelColor.TIMID_GREEN+"Aquamarine",TrawelColor.TIMID_BLUE+"Turquoise",TrawelColor.PRE_BLUE+"Teal",TrawelColor.PRE_BLUE+"Aqua"
			,TrawelColor.PRE_BLUE+"Cerulean",TrawelColor.TIMID_GREY+"Charcoal",TrawelColor.PRE_BLUE+"Azure",TrawelColor.PRE_BLUE+"Cobalt"
			,TrawelColor.TIMID_MAGENTA+"Lavender","Periwinkle",TrawelColor.PRE_MAGENTA+"Amethyst",TrawelColor.PRE_MAGENTA+"Violet"
			,TrawelColor.TIMID_BLUE+"Indigo",TrawelColor.PRE_MAGENTA+"Heliotrope",TrawelColor.TIMID_MAGENTA+"Liliac",TrawelColor.TIMID_MAGENTA+"Plum"
			,TrawelColor.PRE_MAGENTA+"Fuchsia",TrawelColor.PRE_MAGENTA+"Mauve",TrawelColor.PRE_MAGENTA+"Puce"
			,TrawelColor.PRE_RED+"Crimson",TrawelColor.TIMID_MAGENTA+"Pink",TrawelColor.TIMID_RED+"Cardinal",TrawelColor.TIMID_RED+"Carmine");
		//https://www.ssa.gov/oact/babynames/decades/century.html - manually screened and inputed for 'normal' sounding names	
		normalFirstNames = new SRPlainRandom("Fred","Dave","Brian","Thomas","Alex","Bob","Susy","Cindy","Jessica","Jamie","James","Mary","John","Jennifier","Robert","Linda","Barbara","Susan","Margaret","Sarah","Karen","Nancy","Betty","Lisa","Michael","William","David","Richard","Joeseph","Charles","Christopher","Daniel","Matthew","Anthony","Donald","Mark","Paul","Steven","Andrew","George","Kevin","Edward","Timothy","Jason","Jeffrey","Ryan","Gary","Jacob","Nicholas","Eric","Stephen","Jonathan","Larry","Justin","Scott","Frank","Brandon","Sandra","Ashley","Kimberly","Donna","Carol","Michelle","Emily","Amanda","Helen","Melissa","Stephanie","Laura","Rebecca","Sharon","Cynthisa","Kathleen","Amy","Shirly","Anna","Angela","Ruth","Brenda","Nicole","Katherine","Benjamin","Samuel","Patrick","Jack","Dennis","Jerry","Tyler","Aaron","Henry","Jose","Peter","Adam","Zachary","Nathan","Walter","Harold","Kyle","Carl","Arthur","Roger","Keith","Jeremy","Catherine","Christine","Samantha","Debra","Janet","Rachel","Carolyn","Emma","Maria","Heather","Diane","Julie","Joyce","Evelyn","Joan","Christina","Kelly","Victoria","Lauren","Martha","Judith","Cheryl","Megan","Andrea","Ann","Terry","Lawrence","Sean","Christian","Albert","Joe","Ethan","Austin","Jesse","Willie","Billy","Bryan","Bruce","Jordan","Ralph","Roy","Noah","Dylan","Eugene","Wayne","Alan","Jaun","Louis","Russell","Gabriel","Randy","Philip","Alice","Jean","Doris","Kathryn","Hannah","Oliva","Gloria","Marie","Teresa","Sara","Janice","Julia","Grace","Judy","Theresa","Rose","Denise","Marilyn","Amber","Madison","Danielle","Brittany","Diana","Abigail","Jane","Harry","Vincent","Bobby","Johnny","Logan","Natalie","Lori","Tiffany","Alexis","Kayla");
		
		theTitles = new SRPlainRandom("daring","terrible","great","strong","mighty","amazing","fantastic","awesome","beheader","wise", "gullible","deceitful", "trickster", "hero", "protector", "devout", "crusader", "loyal", "disloyal", "epic", "legend", "myth", "cunning", "kind","rich", "poor", "nomad", "rebel", "creator", "destroyer","Hungry","Obnoxious","Grumpy","Mediocre","dreaded","feared","loathed","odious","vile","adaptable","adventurous","ambitious","amiable","courageous","diligent","persistent","witty","determined","humble");
		doerTitles = new SRPlainRandom("dancer","crusher","smasher","climber","eater","tamer","whisperer","planter","smith");
		thingsToSlay = new SRPlainRandom("Vampire","Vampire","Vampire","Wolf","Harpy","Drudger","Mimic","Reaver");
		slayerTitleToSlay = new SRPlainRandom("Slayer","Hunter","Killer","Exterminator","Butcher","Decimator","Eradicator","Expunger");
		theAlphaTitles = new SRPlainRandom("leader","alpha","packleader","packmaster","dominant","primal","controlling","matriarch");
		theLargeTitles = new SRPlainRandom("towering","strong","tall","fearsome","mighty","looming");
		oldTitles = new SRPlainRandom(", Just Old",", the Elder",", Senior"," the Aging",", the Dubiously Mortal",", not Dead Yet",", Veteran"," the Whitehaired","the Elder");
		
		fighterTypes = new SRPlainRandom("fighter","duelist","warrior","gladiator","scrapper","mercenary","brusier");
		drifterTypes = new SRPlainRandom("miscreant","drifter","grifter","delinquent","rascal","wretch","hooligan","hoodlum");
		thiefTypes = new SRPlainRandom("thief","rogue","cutthroat","looter","burglar","scoundrel","swindler","crook");
		thugTypes = new SRPlainRandom("mugger","robber","thug","bandit","marauder","outlaw","desperado","raider","brigand","ruffian");
		collectTypes = new SRPlainRandom("the Collector","the Keeper","the Finder");
		pirateTypes = new SRPlainRandom("pirate","buccaneer","corsair","viking");
		dGuardTitles = new SRPlainRandom(", Gatekeeper",", Guarder of the Gates",", Doorkeeper",", Lockmaster",", Dungeon Guard","the Guard","the Mook",", Henchman",", Big Bad Aspirant");
		hunterTitles = new SRPlainRandom("the Hunter","the Slayer","the Tracker","the Trapper","the Exterminator",", Monster Hunter",", Seeker",", Exterminator for Hire");
		
		//https://en.wikipedia.org/wiki/List_of_domesticated_animals
		animals = new SRPlainRandom("fox","ox","bird","cat","dog","horse","wolf","bear","monkey","lizard","snake","goat","pig","sheep","cow","chicken","donkey","zebra","hog","duck","buffalo","camel","pigeon","goose","yak","llama","alpaca","ferret","dove","turkey","goldfish","shark","rabbit","canary","finch","mouse","mink","hedgehog","guppy","reindeer","ostrich","oryx","gazelle","ibex","hyena","serval","bobcat","caracal","cheetah","elephant","mongoose","genet","deer","parakeet","snail","bee","wasp","hornet","cockatoo","swan","cricket","quail","squid","carp","sparrow","swallow","swallow","robin","rat","squirrel","pheasant","eland","alligator","moose","elk","stoat","coypu","skunk","hamster","lovebird","rainbowfish","frog","axolotl");
		plants = new SRPlainRandom("apple","tomato","potato","squash","bean","olive","pumpkin","coconut","eggcorn");
		
		powerAdj = new SRPlainRandom("undoubtable","impossible","endless","incredible","soaring","infinite","amazing","implausible","miraculous","astonishing","astounding","awesome","extraordinary","wondrous","bewildering","supernatural","great","considerable","extreme","abundant","prodigious");
		powerAdjFront = new SRPlainRandom("undoubtedly","impossibly","endlessly","incredibly","infinitely","implausibly","miraculously","astonishingly","astoundingly","awesomely","wondrous","bewilderingly","supernaturaly","greatly","considerably","extremely","abundantly","prodigiously");
		
		enchantHitIgnite = new SRPlainRandom("fire","flame","burning","blazing","heat","charring","the inferno","combustion","conflagration","embers","pyres","scorching","searing","ignition","kindling","flames");
		enchantHitFrost = new SRPlainRandom("freeze","frost","chilling","rime","freezing","hoarfrost","ice");
		enchantHitElec = new SRPlainRandom("shock","lightning","shocking","sparks","thundering","zapping");
		enchantHitKeen = new SRPlainRandom("keen","honed","whetted");
		
		wolfNames = new SRPlainRandom(loadNames("wolf"));
		wolfPrefixes = new SRPlainRandom(loadNames("wolfPrefix"));
		wolfSuffixes = new SRPlainRandom(loadNames("wolfSuffix"));
		wolfAdjPrefixes = new SRPlainRandom(loadNames("wolfAdjPrefix"));
		wolfAdjSuffixes = new SRPlainRandom(loadNames("wolfAdjSuffix"));
		
		bearAdjs = new SRPlainRandom(loadNames("bearAdj"));
		bearPrefixes = new SRPlainRandom(loadNames("bearPrefix"));
		bearSuffixes = new SRPlainRandom(loadNames("bearSuffix"));
		
		batAdjs = new SRPlainRandom(loadNames("batAdj"));
		batNames = new SRPlainRandom(loadNames("bat"));
		
		//https://en.wikipedia.org/wiki/List_of_tree_genera
		//banana trees aren't trees apparently!
		entTrees = new SRPlainRandom(loadNames("entTree"));
		entThings = new SRPlainRandom(loadNames("entThing"));
		
		drudgerStarters = new SRPlainRandom(loadNames("drudgerStart"));
		drudgerMiddles = new SRPlainRandom(loadNames("drudgerMiddle"));
		drudgerEnds = new SRPlainRandom(loadNames("drudgerEnd"));
		
		attackMisses = new SRPlainRandom("They miss!","It's a miss!","It's not even close!");
		attackDodges = new SRPlainRandom("The attack is dodged!","It's sidestepped!","A narrow miss!");
		attackNegates = new SRPlainRandom("It deflects off the armor!","The armor deflects the blow!","The attack is deflected!");
		
		violateForestQuote = new SRPlainRandom("You dare violate the forest?!","That was holy to the primal forces!","You have committed and unspeakable transgression!","The natural order has been perverted!");
		chestAdjectives = new SRPlainRandom("Vibrant ","Old ","Dull ","Simple ","Carved ","");
	}
	
	//our lazyloaded lists
	/**
	 * lazyloaded random material
	 * note that this is string only and shouldn't be used for anything but fluff
	 * it also doesn't care about weight
	 */
	public static String getRandMat() {
		if (randMats == null) {
			//uses a gap shuffler because otherwise plain random would be useless- if you want plain random you can take it from the source
			List<String> list = new ArrayList<String>();
			for (Material m: MaterialFactory.matList) {
				list.add(m.name);
			}
			randMats = new SRGapShuffle(list);
		}
		
		return randMats.next();
	}
	
	//names in files
	public static final String[] namesManifest = new String[] {};
	public static final List<String> namesManifestList = Arrays.asList(namesManifest);
	
	public static List<String> loadNames(String mask) {
		try {
			try (Scanner fileInput = new Scanner (Oracle.class.getResourceAsStream(Oracle.rescLocation()+"names/"+mask+".names"))){
				List<String> list = new ArrayList<String>();
				while (fileInput.hasNextLine()) {
					String line = fileInput.nextLine();
					list.add(line);
				}
				return list;
			}
		}catch(Exception e){
			return Collections.singletonList("Nameless");
		}
		/*
		if (mainGame.inEclipse) {
			File f = Oracle.rescPath().toFile();
			System.out.println(Oracle.rescPath().toAbsolutePath()+"/names/"+mask+".names");
			f = new File(Oracle.rescPath().toAbsolutePath()+"/names/"+mask+".names");
			try (Scanner fileInput = new Scanner (f)){
				List<String> list = new ArrayList<String>();
				while (fileInput.hasNextLine()) {
					String line = fileInput.nextLine();
					list.add(line);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			try (Scanner fileInput = new Scanner (Oracle.class.getResourceAsStream(Oracle.rescLocation()+"/names/"+mask+".names"))){
				List<String> list = new ArrayList<String>();
				while (fileInput.hasNextLine()) {
					String line = fileInput.nextLine();
					list.add(line);
				}
				return list;
			}
		}
		return null;
		*/
		
	}
	
	
	//get random methods
	
	/**
	 * Returns a random 'element'
	 * @return an element (String)
	 */
	public static String randomElement(){
		if (Rand.chanceIn(2,3)) {
			return commonElements.next();
		}
		if (Rand.chanceIn(2,3)) {
			return rareElements.next();
		}
		if (Rand.chanceIn(2,3)) {
			return MaterialFactory.randWeapMat().name;
		}
		return MaterialFactory.randArmorMat().name;
	}
	
	/**
	 * Returns a random color name.
	 * @return color (String)
	 */
	public static String randomColor() {
		return colorList.next();
	}
	
	public static String randomPrintableColor() {
		return colorListPrintable.next();
	}
	
	
	/**
	 * Returns a random first name
	 * @return name (string)
	 */
	public static String randomFirstName() {
		return normalFirstNames.next();
	}
	
	public static String randomTheTitle() {
		return "the " + Print.capFirst(theTitles.next());
	}
	
	public static String randomDoer() {
		return doerTitles.next();
	}
	
	public static String randomAnimal() {
		return animals.next();
		
	}
	
	public static String randomLastName() {
		switch (Rand.randRange(0,1)) {
		default: case 0:
			return randomLists.randomTheTitle();
		case 1:
			switch (Rand.randRange(0,2)) {
			default: case 0:
				return Print.capFirst(randomLists.randomElement()) + Print.capFirst(randomLists.randomDoer());
			case 1:
				return Print.capFirst(randomLists.randomElement()) + Print.capFirst(randomLists.randomLivingThing());
			case 2:
				return Print.capFirst(randomLists.randomLivingThing()) + Print.capFirst(randomLists.randomDoer());
			}
		}
	}
	
	public static String randomPlant() {
		return plants.next();
	}
	
	public static String randomLivingThing() {
		if (Rand.chanceIn(3, 4)) {
			return randomAnimal();
		}
		return randomPlant();
	}
	
	public static void deadPerson() {
		Print.println(Rand.choose("They are dead. You killed them.","This person is dead... by your hand.","Oh look, a corpse... that you made.","Yep. You killed them good."));
	}
	
	
	public static String randomWolfName() {
		switch (Rand.randRange(2,8)) {
		default:
		case 0:
		case 1://one
			return wolfNames.next();
		case 2://one-two
			return wolfPrefixes.next()+"-"+wolfSuffixes.next();
		case 3://onetwo
			return wolfPrefixes.next()+wolfSuffixes.next().toLowerCase();
		case 4://"one two"
			return wolfPrefixes.next()+" "+wolfSuffixes.next();
		//front adjectives
		case 5://adj-two
			return wolfAdjPrefixes.next()+"-"+wolfSuffixes.next();
		case 6://adjtwo
			return wolfAdjPrefixes.next()+wolfSuffixes.next().toLowerCase();
		//rear adjectives
		case 7://one-adj
			return wolfPrefixes.next()+"-"+wolfAdjSuffixes.next();
		case 8://oneadj
			return wolfPrefixes.next()+wolfAdjSuffixes.next().toLowerCase();
		}
	}
	
	public static String randomBearName() {
		//'old 'iron'fang
		return bearAdjs.next() + " "+bearPrefixes.next()+bearSuffixes.next();
	}
	
	public static String randomBatName() {
		return batAdjs.next()+" "+batNames.next();
	}
	
	public static String randomEntName() {
	return entTrees.next()+entThings.next();
	}
	
	public static String randomDrudgerStockName() {
		return drudgerStarters.next()+drudgerMiddles.next();
	}
	
	public static String randomDrudgerHonorName() {
		return drudgerStarters.next()+drudgerMiddles.next()+drudgerEnds.next();
	}
	
	public static String extractDrudgerHouse(String name) {
		return name.substring(0,4);
	}
	
	public static String extractDrudgerPersonal(String name) {
		return name.substring(5,8);
	}
	
	public static String randomDrudgerHouse() {
		return drudgerStarters.next();
	}
	public static String randomDrudgerPersonal() {
		return drudgerMiddles.next();
	}
	public static String randomDrudgerHonor() {
		return drudgerEnds.next();
	}
	public static String honorDrudgerName(String name) {
		return name+drudgerEnds.next();
	}
	
	/**
	 * Returns a random adjective 50% of the time. Used to make 'weapon of X Y" statements fancier
	 * @return String
	 */
	public static String powerAdjective() {
		return powerAdj.next();
	}
	
	public static String powerAdjectiveFront() {
		return powerAdjFront.next();
	}

	public static String randomWarrior() {
		return fighterTypes.next();
	}
	
	public static String randomThiefName() {
		return thiefTypes.next();
	}
	public static String randomThugName() {
		return thugTypes.next();
	}
	public static String randomDrifterName() {
		return drifterTypes.next();
	}
	
	public static String randomTitleFormat(String title) {
		switch (Rand.randRange(0,2)) {
		default: case 0:
			return "the " + Print.capFirst(title);
		case 1:
			return ", the " + Print.capFirst(title);
		case 2:
			return Print.capFirst(title) + " ";
		}
	}
	
	public static String extractTitleFormat(String title) {
		if (title.endsWith(" ")) {
			return title.substring(0,title.length()-1);
		}
		return title.substring(title.indexOf("the ")+4);
	}
	
	public static String randomCollectorName() {
		return collectTypes.next();
	}
	
	public static String randomPirateName() {
		return pirateTypes.next();
	}

	public static String randomAlphaName() {
		return "the " +Print.capFirst(theAlphaTitles.next());
	}

	public static String randomLargeName() {
		return Print.capFirst(theLargeTitles.next());
	}
	
	public static String attackMissFluff(ATK_ResultCode code) {
		return code == ATK_ResultCode.MISS ? attackMisses.next() : attackDodges.next();
	}
	
	public static String attackNegateFluff() {
		return attackNegates.next();
	}
	
	public static StringNum powerMightyAdj(boolean before) {
		return before ? powerAdjFront : powerAdj;
	}

	public static String randomHunterTitle() {
		if (Rand.chanceIn(1,5)) {
			return (Rand.randFloat() > 0.5f ? ", " : " the ")+ thingsToSlay.next() + " " +slayerTitleToSlay.next();
		}
		return hunterTitles.next();
	}

	public static String randomDGuardTitle() {
		return dGuardTitles.next();
	}

	public static String randomOldTitle() {
		return oldTitles.next();
	}
	
	public static String randomChestAdjective() {
		return chestAdjectives.next();
	}

	public static String randomViolateForestQuote() {
		return "\""+violateForestQuote.next()+"\"";
	}
	
	public static String enchantHitIgnite() {
		return enchantHitIgnite.next();
	}
	public static String enchantHitFrost() {
		return enchantHitFrost.next();
	}
	public static String enchantHitElec() {
		return enchantHitElec.next();
	}
	public static String enchantHitKeen() {
		return enchantHitKeen.next();
	}
}
