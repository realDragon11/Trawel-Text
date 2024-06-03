package trawel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import derg.SRGapShuffle;
import derg.SRInOrder;
import derg.SRPlainRandom;
import derg.StringNum;
import derg.StringResult;
import trawel.battle.Combat.ATK_ResultCode;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;

/**
 * @author dragon
 * 2/8/2018
 * Some random lists that are used elsewhere, put here for cleanliness.
 */
public class randomLists {
	//holding lists, internal
	//initers
	private static StringResult commonElements, rareElements, randMats = null, colorList, normalFirstNames, theTitles,
			doerTitles, animals,plants, wolfNames,bearNames,batNames, entNames, waterNames,
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
			extra.PRE_GREEN+"green",extra.PRE_BLUE+"blue",extra.PRE_YELLOW+"yellow",extra.PRE_MAGENTA+"purple"
			,extra.PRE_RED+"red",extra.PRE_ORANGE+"orange",extra.PRE_WHITE+"white",extra.TIMID_GREY+"black"
			,extra.TIMID_GREY+"grey",extra.PRE_BLUE+"cyan",extra.PRE_BLUE+"silver",extra.PRE_ORANGE+"maroon"
			,extra.PRE_RED+"rose",extra.PRE_ORANGE+"chestnut",extra.TIMID_RED+"vermilion",extra.TIMID_RED+"russet"
			,extra.PRE_RED+"scarlet",extra.PRE_ORANGE+"rust",extra.PRE_ORANGE+"auburn"
			,extra.TIMID_RED+"mahogany",extra.PRE_ORANGE+"pumpkin",extra.PRE_ORANGE+"chocolate"
			,extra.PRE_ORANGE+"copper",extra.PRE_ORANGE+"bronze",extra.TIMID_GREY+"sepia",extra.TIMID_RED+"ochre"
			,extra.PRE_ORANGE+"brown",extra.PRE_ORANGE+"cinnamon",extra.PRE_ORANGE+"tan",extra.PRE_ORANGE+"orange"
			,extra.PRE_YELLOW+"peach",extra.PRE_YELLOW+"goldenrod",extra.PRE_YELLOW+"amber","saffron"
			,"ecru",extra.PRE_YELLOW+"gold",extra.PRE_WHITE+"pearl","buff","flax",extra.PRE_ORANGE+"brass"
			,extra.PRE_YELLOW+"lemon",extra.PRE_YELLOW+"cream",extra.PRE_ORANGE+"beige",extra.PRE_ORANGE+"olive"
			,extra.PRE_WHITE+"ivory",extra.PRE_GREEN+"lime","chartreuse",extra.PRE_GREEN+"emerald",extra.PRE_GREEN+"jade"
			,extra.TIMID_GREEN+"aquamarine",extra.TIMID_BLUE+"turquoise",extra.PRE_BLUE+"teal",extra.PRE_BLUE+"aqua"
			,extra.PRE_BLUE+"cerulean",extra.TIMID_GREY+"charcoal",extra.PRE_BLUE+"azure",extra.PRE_BLUE+"cobalt"
			,extra.TIMID_MAGENTA+"lavender","periwinkle",extra.PRE_MAGENTA+"amethyst",extra.PRE_MAGENTA+"violet"
			,extra.TIMID_BLUE+"indigo",extra.PRE_MAGENTA+"heliotrope",extra.TIMID_MAGENTA+"liliac",extra.TIMID_MAGENTA+"plum"
			,extra.PRE_MAGENTA+"fuchsia",extra.PRE_MAGENTA+"mauve",extra.PRE_MAGENTA+"puce"
			,extra.PRE_RED+"crimson",extra.TIMID_MAGENTA+"pink",extra.TIMID_RED+"cardinal",extra.TIMID_RED+"carmine");;
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
		//https://www.fantasynamegenerators.com/scripts/wolfNames.js
		wolfNames = new SRPlainRandom("Ace","Akira","Alistair","Alpha","Apache","Apollo","Archer","Artemis","Astro","Athene","Atlas","Avalanche","Axis","Bandit","Bane","Baron","Beacon","Bear","Blaze","Blitz","Bolt","Bones","Boomer","Boon","Booth","Boulder","Brawn","Brick","Brock","Browne","Bruno","Brutus","Buck","Bud","Buddy","Bullet","Buster","Butch","Buzz","Caesar","Camelot","Chase","Chewy","Chronos","Cloud","Colt","Comet","Conan","Courage","Dagger","Dane","Danger","Dash","Delta","Dexter","Diablo","Digger","Drake","Duke","Dust","Dutch","Echo","Edge","Excalibur","Fang","Farkas","Flash","Frost","Fury","Ghost","Goliath","Gray","Grunt","Hannibal","Havoc","Hawke","Hawkeye","Hector","Hercules","Hooch","Hulk","Hunter","Hyde","Ice","Jaws","Jax","Jeckyll","Jethro","Judge","Kaine","Kane","Khan","Killer","King","Lad","Laika","Lecter","Lightning","Logan","Loki","Lupin","Lupus","Magnus","Mako","Mason","Maverick","Max","Maximus","Mayhem","Menace","Midnight","Miles","Murdoch","Myst","Nanook","Nero","Nightmare","Nova","Oak","Obsidian","Odin","Omega","Omen","Onyx","Orbit","Outlaw","Patriot","Phantom","Prince","Pyro","Quicksilver","Rage","Ralph","Ranger","Razor","Rebel","Rex","Rider","Riggs","Ripley","Riptide","Rogue","Rover","Scar","Scout","Shade","Shadow","Shepherd","Shredder","Silver","Skye","Slate","Sly","Smoke","Splinter","Steele","Storm","Striker","Summit","Tank","Thor","Thunder","Timber","Titan","Tooth","Trace","Trapper","Trouble","Tundra","Vapor","Whisper","Wolf","Acadia","Aiyana","Akita","Alaska","Alexia","Alexis","Alize","Alpine","Amber","Amethyst","Angel","Ares","Ari","Aspen","Astral","Athena","Atilla","Aura","Aurora","Avril","Babe","Banshee","Beauty","Blaze","Blitz","Blitzen","Blossom","Bo","Boone","Breeze","Charm","Chronis","Clarity","Cleo","Codex","Coral","Crystal","Dakota","Dash","Dawn","Delphi","Destiny","Dharma","Diva","Dodger","Dot","Duchess","Ebony","Echo","Eclipse","Enigma","Faith","Fern","Gemini","Gia","Girl","Grace","Hailey","Heather","Heaven","Helen","Hope","Ice","Indigo","Iris","Ivory","Ivy","Jade","Jasmine","Jewel","Jinx","June","Juno","Justice","Jynx","Karma","Kenya","Lady","Laika","Levi","Lexis","Liberty","Lore","Lotus","Luna","Maple","Maxima","Meadow","Mello","Melody","Mercy","Midnight","Mona","Moone","Myst","Mysti","Mystique","Myth","Nanook","Nova","Nymph","Nyx","Omen","Onyxia","Opal","Oracle","Pandora","Paws","Pearl","Pepper","Phantom","Phoenix","Precious","Princess","Pyro","Queen","Rags","Raine","Raven","Rogue","Sable","Saffron","Sapphire","Satin","Scarlet","Shade","Shadow","Silver","Snow","Snowball","Snowflake","Solstice","Star","Twilight","Vapor","Velvet","Violet","Vixen","Whisper","Willow","Winter","Xena","Zelda");
		bearNames = new SRPlainRandom("Adalbero","Aloysius","Andy","Anuk","Arcadius","Arcot","Arkadios","Arktos","Armel","Arnbjorn","Arshag","Art","Artair","Artan","Arther","Arthfael","Arthmael","Arthog","Arthur","Artie","Artis","Arto","Artorius","Arttu","Artturi","Artur","Arturas","Arturo","Arty","Asbjorn","Attie","Atty","Auberon","Avery","Avonaco","Bam-Bam","Bamard","Bamey","Banjo","Barbell","Barend","Barley","Barnard","Barnea","Barney","Barnie","Barny","Barret","Barrett","Barry","Basil","Bastian","Beamard","Bear","Bearnabus","Bearnard","Behrend","Beirne","Bemelle","Bemot","Benard","Benat","Benno","Benton","Beorn","Beornheard","Beowulf","Ber","Beraco","Beranger","Beregiso","Berend","Berengar","Berengarius","Berenger","Berenguer","Berernger","Beringar","Beringarius","Berinhard","Bern","Bernaldino","Bernard","Bernardino","Bernardo","Bernardus","Bernardyn","Bernat","Bernd","Berndt","Berne","Berney","Bernfried","Bernhard","Bernhardt","Bernie","Bernon","Bernt","Bernward","Berny","Bero","Billie","Biorna","Bitsy","Bjarne","Bjarni","Bjoern","Bjorn","Bjornar","Bjorne","Blubber","Bobby","Bobo","Boo Boo","Boots","Burnard","Burney","Buttercup","Buttons","Calico","Capps","Caramel","Casey","Cedar","Chancie","Charlie","Chip","Coco","Cornelius","Dopey","Dov","Drogo","Dubi","Dusty","Eden","Edun","Einstein","Enyeto","Esben","Espen","Finley","Flubber","Fluffy","Frankie","Freddy","Garcia","Geoff","George","Georgie","Georgy","Gerben","Grumpy","Gunnbjorn","Hallbjorn","Hamilton","Happy","Hartz","Hausu","Henri","Henry","Hohots","Honaw","Honon","Horace","Howell","Humbert","Humphrey","Huslu","Jack","James","Jammy","Jasper","Joachim","Johnny","Jonsey","Jupiter","Justin","Kolbjorn","Kuma","Kuruk","Lannie","Lennie","Liwanu","Louis","Machk","Mahon","Marley","Marshmellow","Mathe","Mathuin","Matoskah","Mecho","Mitch","Molimo","Myr","Nanook","Nanuk","Nibbs","Niels","Norman","Notaku","O'Berry","Oberon","Oliver","Omar","Orion","Ors","Orsen","Orsin","Orsino","Orso","Orson","Osbeorn","Osborn","Osborne","Osbourne","Oscar","Otso","Ottille","Otto","Ourson","Panda","Pandy","Pat","Patches","Pebbles","Ponty","Popey","Preben","Pridbjorn","Quadro","Rio","Robbie","Rocky","Rolly","Rum","Sabby","Sammy","Scoot","Scottie","Sebastian","Sewati","Shorty","Sigbjorn","Skittle","Sleepy","Smokey","Snowball","Sonny","Sooty","Spencer","Spike","Spiky","Stormy","Sugar","Sunshine","Svenbjorn","Tabby","Talbert","Tarben","Tatty","Teddy","Telutci","Theo","Theodore","Thorben","Thorbern","Thorbjorn","Thorburn","Thorton","Tickles","Tims","Toby","Toffy","Tony","Torben","Torbern","Torbernus","Torbjorn","Tottles","Trevor","Trump","Tubby","Tuketu","Turi","Twinky","Ucumari","Uffo","Uigbiorn","Urs","Ursino","Ursinus","Ursus","Uther","Uzumati","Vemados","Vermundo","Vernados","Victor","Waffle","Walter","Willie","Winston","Woodsy","Wyborn","Yana","Zed","Abby","Angel","Apple-pie","Arcadia","Ariane","Armelle","Arthuretta","Arthurine","Arti","Averi","Averie","Avery","Ayla","Banjo","Barrett","Barretta","Beatrice","Bella","Bemadette","Bera","Beratrice","Berdine","Berengari","Berengaria","Berenice","Bern","Bernadea","Bernadete","Bernadett","Bernadette","Bernadina","Bernadine","Bernarda","Bernarde","Bernardete","Bernardetta","Bernardette","Bernardina","Bernardine","Bernardita","Berne","Berneen","Bernelle","Bitsy","Blubber","Bobo","Boo Boo","Boots","Bubbles","Buttercup","Buttons","Calico","Calista","Callista","Callisto","Caramel","Clymene","Coco","Dandelion","Dopey","Doris","Dusty","Eden","Eferhild","Eferhilda","Elizabeth","Emily","Fatima","Flubber","Fluffy","Georgy","Grumpy","Hagar","Happy","Hausu","Heltu","Honey","Irene","Isobel","Izzy","Jammy","Jane","Jerica","Jewel","Jupiter","Justine","Kuma","Louis","Louise","Lusela","Maggie","Mahtowa","Margaret","Marshmellow","Mecislava","Melanie","Miffy","Myr","Nadetta","Nadette","Nibbs","Nita","Orsa","Orsaline","Orse","Orsel","Orselina","Orseline","Orsina","Orsola","Orsolya","Osha","Ottilie","Pam","Panda","Pandy","Pat","Patches","Patricia","Peaches","Pebbles","Penny","Persephone","Poe","Polly","Puddles","Queenie","Rio","Rolly","Rosie","Roxie","Sabby","Samantha","Sammie","Sandra","Sapata","Sargie","Sienna","Skittle","Sleepy","Smokey","Snowball","Sugar","Sunshine","Susanna","Susie","Suzie","Tabby","Taffy","Tatty","Thorborg","Tickles","Toffy","Torborg","Tottles","Tubby","Twinky","Ursa","Ursala","Ursel","Ursella","Ursicina","Ursina","Ursine","Urska","Ursula","Ursule","Ursulina","Urszula","Uschi","Valerie","Venus","Veronica","Versula","Viola","Violet","Violette","Waffle","Wilhelmina","Winifred","Winnie","Winona");
		batNames = new SRPlainRandom("Ace","Acrobat","Ajax","Angel","Apollo","Archangel","Artemis","Ash","Azar","Azral","Baltazar","Bandit","Bane","Basil","BatPitt","Batista","Batley","Baxter","Beaker","Bigglesworth","Bitz","Blackjack","Blade","Blaze","Blitz","Bloodwing","Blues","Booboo","Bruce","Brutus","Bubba","Bubbles","Bullet","Buster","Butch","Buttons","Chaos","Char","Chocula","Cole","Comet","Cookie","Count","Cupcake","Darkess","Darth","Dexter","Diablo","Dimitri","Ding","Dodge","Dodger","Doom","Drac","Dracula","Draculon","Drake","Echo","Equinox","Fangs","Flapper","Flappy","Flaps","Flash","Flicker","Fuzz","Gambat","Gargle","Gargles","Gargoyle","Gavalon","Ghost","Gizmo","Glider","Gloom","Glyde","Golbat","Gouge","Grey","Guano","Hannibal","Hawke","Hunter","Hyperion","Impaler","Jet","Kane","Khan","Kindle","Lecter","Lockjaw","Lucifer","Marble","Matrix","Merlin","Midas","Midnight","Mirage","Monty","Moon","Mothra","Muse","Nerf","Nibbles","Nightmare","Nightwing","Nugget","Nukem","Nyx","Onyx","Orion","Ozzy","Patch","Patches","Pebbles","Phantom","Pickle","Psych","Quickfang","Quilla","Rabies","Rainbow","Rascal","Remus","Render","Rhonin","Ripmaw","Rocky","Rufus","Sabath","Sawyer","Screech","Screechy","Shade","Shadow","Shreek","Shrike","Slate","Slithe","Snuffle","Sonar","Sonny","Spectre","Spitfire","Spudnik","Spuds","Swoops","Thunder","Tiberius","Titan","Twinkle","Umber","Vamp","Vlad","Vladimir","Vulkan","Wayne","Wiggles","Wingnut","Xanadu","Zion","Abby","Aerial","Aine","Alexia","Alexis","Amber","Angel","Angie","Apple","Ash","Atilla","Aura","Aurora","Azraelle","Azure","Azurys","Babes","Bandetta","Batsy","Batty","Beauty","Biscuit","Blaze","Breeze","Bubble","Bubbles","Buttercup","Buttons","Calypso","Cerulean","Chuckles","Cinderella","Cinnamon","Clementine","Cleo","Cookie","Cosmo","Cuddles","Cupcake","Dakota","Daphne","Dawn","Dawne","Dawnstar","Dot","Draculette","Ebony","Echo","Eclipse","Ember","Enigma","Equina","Equinox","Fang","Fangie","Faune","Fierra","Fizzle","Flappy","Fluffy","Flutters","Gadget","Gargles","Giggles","Grace","Guani","Harmony","Haze","Hazel","Honey","Huntress","Iggy","Illumina","Indigo","Iris","Ivory","Ivy","Jinx","Jynx","Lady","Liberty","Lucy","Lullaby","Luna","Mable","Marbles","Maya","Melody","Mirage","Mittens","Moonbeam","Moone","Moonlight","Morning","Morticia","Myst","Mystique","Nibbles","Nighte","Noodles","Nova","Nugget","Oracle","Peaches","Pebbles","Pepper","Phoenix","Pickle","Pickles","Plume","Precious","Princess","Psyche","Raine","Raven","Rebel","Rhyme","Rogue","Ruth","Sade","Sage","Scarlett","Shade","Shadow","Shay","Shine","Siren","Skye","Skylar","Snuffles","Sona","Sora","Star","Stardust","Starlight","Sugar","Tinkerbell","Trixie","Trixy","Twilight","Twinkle","Twinkles","Vanity","Velvet","Violet","Vixen","Wiggles","Xena");
		entNames = new SRPlainRandom("Abies","Acacia","Acca","Acer","Adansonia","Aesculus","Agathis","Agonis","Albizia","Aleurites","Alianthus","Alnus","Amalanchier","Amborella","Amentotaxus","Anacardium","Annona","Anogeissus","Antiaris","Aralia","Araucaria","Arbutus","Ardisia","Areca","Arenga","Argania","Artocarpus","Asimina","Athrotaxis","Azadirachta","Baccharis","Bactris","Bauhinia","Betula","Bombax","Borassus","Bourreria","Brachylaena","Brahea","Brosimum","Broussonetia","Bucida","Bursera","Busus","Butia","Byrsonima","Caesalpinia","Callistemon","Callitris","Calocedrus","Calophyllum","Calyptranthes","Canella","Capparis","Caragana","Carica","Carpinus","Carya","Caryota","Cassia","Castanea","Castanopsis","Castilla","Casuarina","Catalpa","Cecropia","Cedrela","Cedrus","Ceiba","Celtis","Ceratonia","Cercis","Chamaecyparis","Chilopsis","Cinnamomum","Citrus","Cladrastis","Clethra","Clusia","Cocos","Coffea","Combretum","Copernicia","Cordia","Cordyline","Cornus","Corylus","Corymbia","Corypha","Crataehus","Cupressus","Cussonia","Cycas","Cyrilla","Dacrycarpus","Dacrydium","Delonix","Diospyros","Dracaena","Drypetes","Durio","Elaeagnus","Elaeis","Elliottia","Erica","Eriobotrya","Erythrina","Eucommia","Eugenia","Euonymus","Euphorbia","Fagus","Ficu","Firmiana","Fraxinus","Garcinia","Ginkgo","Gleditsia","Gonystylus","Gordonia","Grevillea","Guibourtia","Gymnanthes","Halesia","Hamamelis","Harpullia","Hevea","Hibiscus","Hippomane","Howea","Hymenaea","Hyophorbe","Ilex","Illicium","Inga","Jacaranda","Jubaea","Juglans","Juniperus","Kalopanax","Khaya","Kigelia","Kokia","Laburnum","Lagunaria","Laurus","Lecythis","Leucaena","Licaria","Liquidambar","Liriodendron","Litchi","Lithocarpus","Livistona","Lodoicea","Lysiloma","Machaerium","Maclura","Magnolia","Malpighia","Malus","Mangifera","Maranthes","Maytenus","Medusagyne","Melia","Meryta","Metopium","Michelia","Millettia","Mimosa","Moringa","Morus","Musa","Myoporum","Myrica","Myristica","Myrsine","Myrtus","Nectandra","Nerium","Nyssa","Olea","Ostrya","Palaquium","Parrotia","Paulownia","Peltogyne","Pentaclethra","Persea","Phellodendron","Phytelephas","Picea","Pinus","Piscidia","Pistacia","Platanus","Plumeria","Populus","Prosopis","Prunus","Psidium","Pyrus","Quercus","Radermachera","Raphia","Rhapis","Rhizophora","Rhododendron","Rhus","Robinia","Sabal","Salix","Salvadora","Sambucus","Sapium","Sassafras","Schaefferia","Schefflera","Senegalia","Sequioa","Serenoa","Shorea","Sideroxylon","Sondias","Sophora","Sorbus","Stewartia","Syagrus","Syringa","Tabebuia","Taiwania","Talipariti","Tamarix","Taxandria","Taxus","Tectona","Tetradium","Theobroma","Thevetia","Thuja","Tilia","Tipuana","Toona","Torreya","Trema","Triadica","Tristaniopsis","Ulmus","Vachellia","Vernicia","Vitex","Wodyetia","Wollemia","Xylosma","Yucca","Zelkova");
		waterNames = new SRPlainRandom("Aba","Abalise","Abeia","Acalephia","Acatea","Achaeia","Achilia","Acotali","Actaea","Actaia","Actalphia","Actina","Admete","Adomeite","Adrasteia","Aegenia","Aegina","Aeleora","Aethelsa","Aethisia","Aethria","Aethusa","Aia","Aigale","Aigle","Aiglise","Akirea","Akiria","Akitai","Akraia","Aktaie","Aktarise","Alacine","Alcinne","Alcinoe","Alcinohre","Alciphei","Alcippe","Aleiki","Aleina","Alethrise","Alexidoe","Alexiroe","Alexise","Alke","Alokea","Alsira","Amalithea","Amalthea","Amalthelia","Amatheia","Ambirose","Ambirosi","Ambrosia","Amete","Amethelia","Amiphite","Amithelia","Amphilio","Amphinome","Amphinomis","Amphiomise","Amphiris","Amphiro","Amphise","Amphite","Amphithoe","Amphitrite","Amymone","Amymonei","Anithea","Anithraci","Anphia","Anteopi","Anthe","Anthracia","Anthrecea","Antiope","Antiropheu","Anuthei","Anymine","Aora","Aphithea","Arasine","Arastea","Arastellia","Arethisia","Arethiusei","Arethusa","Aria","Aroara","Arsinoe","Arsinolphi","Asea","Asia","Asitrise","Asteli","Astelle","Astellodia","Asteodia","Asteria","Asterodia","Asterope","Astirise","Astrina","Astris","Astropea","Atehrea","Atiliana","Atlantia","Atlentea","Axichis","Axioche","Axiphiche","Bateia","Bateilla","Batellia","Bolina","Bolionei","Bollinea","Bormea","Brentisa","Bretiax","Brettia","Bromia","Brumisia","Caigenia","Caliadne","Calidanea","Caligeina","Calipsyse","Calirianne","Caliroesis","Calligenia","Calliroe","Calonia","Calypise","Calypso","Canea","Canossila","Caronise","Castalia","Casteilise","Castelis","Celaeno","Celano","Celareino","Cerionis","Chaicilo","Chamine","Chania","Chanilla","Charellis","Chariclo","Chilane","Chione","Chryseis","Chrysesis","Chrysise","Chrysopeleia","Chrysoplei","Chyseleia","Cirilha","Cirohsa","Cirrha","Claironei","Clanea","Clonia","Clymene","Clymenti","Clymoni","Cnassea","Cnossia","Cordelia","Coridella","Coronis","Corstelis","Creamis","Creasi","Crephusa","Cretheis","Creusa","Criamisa","Crimisa","Crothesis","Crotheise","Daeira","Daleira","Dalleira","Danais","Danalise","Danallis","Daphine","Daphines","Daphne","Dercetis","Dexamene","Dexane","Deximenis","Diapatri","Digonia","Diogenia","Diolenia","Diomis","Dione","Dionele","Diopatra","Dopiara","Dorceti","Dorcetise","Doris","Dorissis","Doryse","Echellia","Echemeia","Echemellia","Echise","Echo","Ecirane","Ecole","Ecrintise","Edothei","Edothise","Egeria","Egoria","Eidothea","Eidyia","Electria","Elektra","Eletea","Elidia","Elidy","Elimenise","Elinoire","Elophis","Eludora","Eludore","Eluno","Elunore","Endeis","Enideise","Enodeis","Ephemei","Eralato","Erato","Eriato","Eriophai","Eriphia","Eriphise","Eryone","Ethemea","Ethimei","Ethimelle","Eucrante","Eudora","Eulimene","Eulimenei","Eunoe","Eunoite","Eunosise","Eunoste","Eupheme","Euphymes","Eurynome","Eurynomile","Euthami","Euthemia","Euthemilia","Evadne","Evidone","Evodine","Galaphaura","Galatea","Galaxaura","Galeine","Galene","Galine","Galixera","Gatalea","Glatealle","Hairiko","Halia","Halira","Halisa","Hariklo","Harilora","Harimoni","Harmeni","Harmonia","Harphinia","Harpina","Harponi","Hegetoria","Heila","Helgoria","Helia","Helike","Helikei","Heliria","Helleori","Holikei","Hylliphis","Hyllis","Hyllisei","Iaira","Ianassa","Ianeira","Ianisse","Ianithise","Ianthe","Iaosise","Iasis","Ida","Idahria","Idaia","Idaise","Idaphise","Ideia","Idophia","Ihrone","Ilaira","Ilanaera","Ilanara","Ilara","Ileneira","Ilo","Ione","Iosise","Iphanthei","Iphise","Isameine","Ismanise","Ismene","Isonei","Kaephei","Kahliste","Kaliphaia","Kalise","Kalleira","Kallianassa","Kallianeira","Kallianisse","Kallinei","Kalliphae","Kalliphaeia","Kalliste","Kalyphise","Kalypise","Kalypso","Kapheira","Kaphelirea","Karilya","Kariye","Karya","Kelaria","Kelleia","Kerkeis","Kerleise","Kerokeis","Kianise","Kimopola","Kiseise","Kisseis","Kissise","Klaia","Kleadorise","Kleeia","Kleia","Kleodora","Kleokhareia","Kleora","Kleorei","Klepheia","Klokharei","Klymeina","Klymene","Klymentise","Klyphotise","Klytea","Klytie","Kylomopeilia","Kymopoleia","Kyreanes","Kyrene","Kyrenise","Lamedesa","Lametisa","Lamiphelia","Lampetia","Laomedeia","Laripha","Larishae","Larissa","Laromedia","Lephice","Leuce","Leuciphi","Leuciphia","Leucippe","Leucise","Libeias","Liberi","Libethrias","Lilaia","Lilias","Liliope","Limnoreia","Limnorelia","Limorei","Liriope","Liropei","Lollaia","Lysianassa","Lysiasse","Lysiniassi","Maia","Maira","Mairia","Malia","Malilia","Malyra","Marilia","Marina","Mariphine","Marynae","Medeia","Meilira","Meilitae","Melaenia","Melaina","Melaniphi","Melanippe","Melia","Melinai","Meliphia","Melita","Melite","Mellaniphe","Melliata","Melorope","Melphite","Meltise","Mendeis","Menideis","Menidise","Meniphei","Meniphis","Menippe","Menodiace","Menodice","Menolorice","Mephite","Merily","Merope","Merophise","Meryl","Merylle","Messeis","Messelise","Messise","Metioche","Metiophei","Metiphoche","Metis","Metisis","Mideia","Midelia","Minithe","Minthe","Monithei","Morea","Morelia","Morella","Moria","Morilia","Myressei","Myritoesa","Myrtoessa","Mysteise","Mystis","Mystise","Nacoile","Nacole","Naicolei","Naise","Nasille","Nasiphe","Neaira","Neamertise","Neda","Nedali","Nekaia","Nelaira","Nelairi","Nelida","Nemea","Nemelphia","Nementia","Nemertes","Nemertise","Neomerilis","Neomeris","Neomorise","Nepheilise","Nephele","Nephilis","Nerin","Nerince","Nerinphe","Nerisei","Nerisha","Nerissa","Nesaea","Nesallea","Nesilia","Nikaia","Nikalia","Nixie","Nixilei","Nomia","Nomilia","Nomira","Nonakris","Nonakryse","Noniarise","Nylisa","Nysa","Nysali","Nyxie","Oenoe","Oinoie","Oinone","Oiolyka","Oiolyphei","Okyrhoe","Olenoe","Olenore","Olinophe","Olione","Oluania","Olurainise","Olyrei","Ophinoie","Ophiolyse","Ophyroe","Opiris","Opis","Opysis","Oreilhya","Oreithyia","Orethylia","Orinia","Oriphine","Oriphone","Ornia","Oronia","Orphne","Orseis","Orselise","Orsenise","Ortogise","Ortygia","Ortylia","Othreis","Othrephis","Othresise","Ourania","Palacia","Paleine","Pallene","Panelophi","Paphia","Paphila","Paphilia","Pareia","Pareila","Parila","Pasithea","Pasithelle","Pasithoe","Pasthera","Pasthilei","Penelope","Penolopei","Pereisis","Peria","Periboia","Periola","Periphoia","Perseis","Perseisise","Petiare","Petira","Petoria","Petra","Petraea","Petrallea","Phaethosia","Phaethusa","Phaino","Phaio","Phaisyle","Phalinio","Phalino","Phalisyle","Phanethusi","Phanio","Phaphino","Phebei","Phelousa","Pherolusei","Pherousa","Phiale","Phialyra","Philia","Philiale","Philiasei","Philise","Philyra","Philyrea","Phiolle","Phiriaxi","Phisylei","Phoebe","Pholebis","Pholora","Phonia","Phosithonia","Phosthonia","Phrixa","Phrixia","Physadeia","Physadelia","Physali","Pialleni","Piareili","Piasithole","Pilyphei","Pireini","Pirene","Pirenei","Plataea","Platalea","Plateila","Pleidone","Pleione","Plephione","Polhyno","Polixio","Polphymino","Polydiora","Polydora","Polyhymno","Polynome","Polynomise","Polyphe","Polyphise","Polyphoe","Polyxio","Polyxo","Pomedosa","Pontomedise","Pontomedousa","Pontoporeia","Pontoreisa","Pontropeira","Poreile","Praxithea","Praxithelia","Praxithise","Promidea","Prosymeina","Prosymina","Prosymna","Protomedea","Protomedelia","Psalacantha","Psalicanthise","Psamaphine","Psamathe","Psamiaphe","Pteili","Ptelea","Ptelera","Rhaenise","Rhalanise","Rhanis","Rhene","Rhenei","Rhenelis","Rhephila","Rhetia","Rhetila","Rhodia","Rhodiophe","Rhodisa","Rhodope","Rhodophine","Rhodys","Sabeana","Sabrina","Sabrinei","Sagariphis","Sagaritis","Sagitise","Salamis","Salaphise","Salomise","Samia","Samilea","Samisia","Sapharnia","Savarinea","Savarna","Selestia","Semestra","Semistrea","Silopei","Siniophe","Sinope","Solise","Sose","Soseilis","Speiliro","Speio","Spelino","Steiropi","Steliope","Sterope","Stirophia","Stophila","Strophia","Sylilis","Syllis","Sylphise","Symaithis","Symithia","Symithise","Synallasis","Synallia","Synallis","Syrianix","Syrinix","Syrinx","Talila","Talula","Tanagra","Taniara","Tanigrei","Taphiula","Teledice","Teledike","Teleidice","Telidaki","Telodice","Telphedice","Tereine","Terenei","Terephine","Thaleia","Thalenia","Thalice","Theamise","Theanole","Theanore","Thebe","Thebesi","Theisi","Theliphe","Thelphise","Themis","Themise","Themistae","Themisto","Themistoli","Theonoe","Thero","Theroli","Therolis","Thesipha","Thespia","Thespilia","Thetis","Thiosa","Thisbe","Thisei","Thisobei","Tholosa","Thoniphe","Thoosa","Thosei","Thousa","Thousia","Thrasise","Thrassa","Thronie","Thronise","Throssia","Thyia","Thyliase","Thyxia","Trilleia","Triteia","Tritelipha","Tykhe","Tykiphe","Tyriphe","Xaniphe","Xanthe","Zelipea","Zephixo","Zeuxipia","Zeuxippe","Zeuxise","Zeuxoli");
		
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
	
	
	//get random methods
	
	/**
	 * Returns a random 'element'
	 * @return an element (String)
	 */
	public static String randomElement(){
		if (extra.chanceIn(2,3)) {
			return commonElements.next();
		}
		if (extra.chanceIn(2,3)) {
			return rareElements.next();
		}
		if (extra.chanceIn(2,3)) {
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
		return "the " + extra.capFirst(theTitles.next());
	}
	
	public static String randomDoer() {
		return doerTitles.next();
	}
	
	public static String randomAnimal() {
		return animals.next();
		
	}
	
	public static String randomLastName() {
		switch (extra.randRange(0,1)) {
		default: case 0:
			return randomLists.randomTheTitle();
		case 1:
			switch (extra.randRange(0,2)) {
			default: case 0:
				return extra.capFirst(randomLists.randomElement()) + extra.capFirst(randomLists.randomDoer());
			case 1:
				return extra.capFirst(randomLists.randomElement()) + extra.capFirst(randomLists.randomLivingThing());
			case 2:
				return extra.capFirst(randomLists.randomLivingThing()) + extra.capFirst(randomLists.randomDoer());
			}
		}
	}
	
	public static String randomPlant() {
		return plants.next();
	}
	
	public static String randomLivingThing() {
		if (extra.chanceIn(3, 4)) {
			return randomAnimal();
		}
		return randomPlant();
	}
	
	public static void deadPerson() {
		extra.println(extra.choose("They are dead. You killed them.","This person is dead... by your hand.","Oh look, a corpse... that you made.","Yep. You killed them good."));
	}
	
	
	public static String randomWolfName() {
		return wolfNames.next();
	}
	
	public static String randomBearName() {
		return bearNames.next();
	}
	
	public static String randomBatName() {
		return batNames.next();
	}
	
	public static String randomEntName() {
	return entNames.next();
	}
	
	public static String randomWaterName() {
		return waterNames.next();
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
	
	public static String randomCollectorName() {
		return collectTypes.next();
	}
	
	public static String randomPirateName() {
		return pirateTypes.next();
	}

	public static String randomAlphaName() {
		return "the " +extra.capFirst(theAlphaTitles.next());
	}

	public static String randomLargeName() {
		return extra.capFirst(theLargeTitles.next());
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
		if (extra.chanceIn(1,5)) {
			return (extra.randFloat() > 0.5f ? ", " : " the ")+ thingsToSlay.next() + " " +slayerTitleToSlay.next();
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
