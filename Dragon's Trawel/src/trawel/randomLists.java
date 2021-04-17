package trawel;


/**
 * @author Brian Malone
 * 2/8/2018
 * Some random lists that are used elsewhere, put here for cleanliness.
 */
public class randomLists {
	//static methods
	/**
	 * Returns a random 'element'
	 * @return an element (String)
	 */
	public static String randomElement(){
		if (extra.chanceIn(2,3)) {
			return (String) extra.choose("earth","wind","fire","air","water","ice","storm","thunder","flame","lightning","frost","stone");
		}
		if (extra.chanceIn(2,3)) {
			return (String)extra.choose("grass","metal","mountain","ocean","sky","flesh","life","death","balance","rust","pie","math","soul","heart","word");	
		}
		return MaterialFactory.randMat(true,true).name;
	}
	
	/**
	 * Returns a random color name.
	 * @return color (String)
	 */
	public static String randomColor() {
		return (String)extra.choose("green","blue","yellow","purple","red","orange","white","black","grey","cyan","silver","maroon","rose","chestnut","vermilion","russet","scarlet","rust","auburn","mahogany","pumpkin","chocolate","copper","bronze","sepia","ochre","brown","cinnamon","tan","orange","peach","goldenrod","amber","saffron","ecru","gold","pearl","buff","flax","brass","lemon","cream","beige","olive","ivory","lime","chartreuse","emerald","jade","aquamarine","turquoise","teal","aqua","cerulean","charcoal","azure","cobalt","lavender","periwinkle","amethyst","violet","indigo","heliotrope","liliac","plum","fuchsia","mauve","puce","crimson","pink","cardinal","carmine");
	}
	
	
	/**
	 * Returns a random first name
	 * @return name (string)
	 */
	public static String randomFirstName() {
	return(String)extra.choose("Fred","Dave","Brian","Thomas","Alex","Bob","Susy","Cindy","Jessica","Jamie","James","Mary","John","Jennifier","Robert","Linda","Barbara","Susan","Margaret","Sarah","Karen","Nancy","Betty","Lisa","Michael","William","David","Richard","Joeseph","Charles","Christopher","Daniel","Matthew","Anthony","Donald","Mark","Paul","Steven","Andrew","George","Kevin","Edward","Timothy","Jason","Jeffrey","Ryan","Gary","Jacob","Nicholas","Eric","Stephen","Jonathan","Larry","Justin","Scott","Frank","Brandon","Sandra","Ashley","Kimberly","Donna","Carol","Michelle","Emily","Amanda","Helen","Melissa","Stephanie","Laura","Rebecca","Sharon","Cynthisa","Kathleen","Amy","Shirly","Anna","Angela","Ruth","Brenda","Nicole","Katherine","Benjamin","Samuel","Patrick","Jack","Dennis","Jerry","Tyler","Aaron","Henry","Jose","Peter","Adam","Zachary","Nathan","Walter","Harold","Kyle","Carl","Arthur","Roger","Keith","Jeremy","Catherine","Christine","Samantha","Debra","Janet","Rachel","Carolyn","Emma","Maria","Heather","Diane","Julie","Joyce","Evelyn","Joan","Christina","Kelly","Victoria","Lauren","Martha","Judith","Cheryl","Megan","Andrea","Ann","Terry","Lawrence","Sean","Christian","Albert","Joe","Ethan","Austin","Jesse","Willie","Billy","Bryan","Bruce","Jordan","Ralph","Roy","Noah","Dylan","Eugene","Wayne","Alan","Jaun","Louis","Russell","Gabriel","Randy","Philip","Alice","Jean","Doris","Kathryn","Hannah","Oliva","Gloria","Marie","Teresa","Sara","Janice","Julia","Grace","Judy","Theresa","Rose","Denise","Marilyn","Amber","Madison","Danielle","Brittany","Diana","Abigail","Jane","Harry","Vincent","Bobby","Johnny","Logan","Natalie","Lori","Tiffany","Alexis","Kayla");
	//https://www.ssa.gov/oact/babynames/decades/century.html - manually screened and inputed for 'normal' sounding names	
	}
	
	public static String randomMighty() {
		return "the " + extra.capFirst(extra.choose("Daring","Terrible","Great","Strong","Mighty","Amazing","Fantastic","Awesome","Beheader","Wise", "Gullible","Deceitful", "Trickster", "Hero", "protector", "devout", "crusader", "loyal", "disloyal", "epic", "legend", "myth", "cunning", "kind","rich", "poor", "nomad", "rebel", "creator", "destroyer","Hungry","Obnoxious","Grumpy","Mediocre","dreaded","feared","loathed","odious","vile","adaptable","adventurous","ambitious","amiable","courageous","diligent","persistent","witty","determined","humble"));
	}
	
	public static String randomDancer() {
		return extra.choose("dancer","crusher","smasher","climber","eater","tamer","whisperer","planter","smith");
	}
	
	public static String randomAnimal() {
		return extra.choose("fox","ox","bird","cat","dog","horse","wolf","bear","monkey","lizard","snake","goat","pig","sheep","cow","chicken","donkey","zebra","hog","duck","buffalo","camel","pigeon","goose","yak","llama","alpaca","ferret","dove","turkey","goldfish","shark","rabbit","canary","finch","mouse","mink","hedgehog","guppy","reindeer","ostrich","oryx","gazelle","ibex","hyena","serval","bobcat","caracal","cheetah","elephant","mongoose","genet","deer","parakeet","snail","bee","wasp","hornet","cockatoo","swan","cricket","quail","squid","carp","sparrow","swallow","swallow","robin","rat","squirrel","pheasant","eland","alligator","moose","elk","stoat","coypu","skunk","hamster","lovebird","rainbowfish","frog","axolotl");
		//https://en.wikipedia.org/wiki/List_of_domesticated_animals
	}
	
	public static String randomLastName() {
		return extra.choose(randomLists.randomMighty(),extra.choose(extra.capFirst(extra.choose(randomLists.randomElement())) + extra.capFirst(extra.choose(randomLists.randomDancer(),randomLists.randomDancer(),randomLists.randomLivingThing())),extra.capFirst(extra.choose(randomLists.randomElement(),randomLists.randomElement(),randomLists.randomLivingThing())) + extra.capFirst(extra.choose(randomLists.randomDancer()))));
	}
	
	public static String randomPlant() {
		return extra.choose("apple","tomato","potato","squash","bean","olive","pumpkin","coconut","eggcorn");
	}
	
	public static String randomLivingThing() {
		return extra.choose(randomAnimal(),randomAnimal(),randomAnimal(),randomPlant());
	}
	
	public static void deadPerson() {
		extra.println(extra.choose("They are dead. You killed them.","This person is dead... by your hand.","Oh look, a corpse... that you made.","Yep. You killed them good."));
	}
	
	
	//https://www.fantasynamegenerators.com/scripts/wolfNames.js
	public static String randomWolfName() {
		return extra.choose("Ace","Akira","Alistair","Alpha","Apache","Apollo","Archer","Artemis","Astro","Athene","Atlas","Avalanche","Axis","Bandit","Bane","Baron","Beacon","Bear","Blaze","Blitz","Bolt","Bones","Boomer","Boon","Booth","Boulder","Brawn","Brick","Brock","Browne","Bruno","Brutus","Buck","Bud","Buddy","Bullet","Buster","Butch","Buzz","Caesar","Camelot","Chase","Chewy","Chronos","Cloud","Colt","Comet","Conan","Courage","Dagger","Dane","Danger","Dash","Delta","Dexter","Diablo","Digger","Drake","Duke","Dust","Dutch","Echo","Edge","Excalibur","Fang","Farkas","Flash","Frost","Fury","Ghost","Goliath","Gray","Grunt","Hannibal","Havoc","Hawke","Hawkeye","Hector","Hercules","Hooch","Hulk","Hunter","Hyde","Ice","Jaws","Jax","Jeckyll","Jethro","Judge","Kaine","Kane","Khan","Killer","King","Lad","Laika","Lecter","Lightning","Logan","Loki","Lupin","Lupus","Magnus","Mako","Mason","Maverick","Max","Maximus","Mayhem","Menace","Midnight","Miles","Murdoch","Myst","Nanook","Nero","Nightmare","Nova","Oak","Obsidian","Odin","Omega","Omen","Onyx","Orbit","Outlaw","Patriot","Phantom","Prince","Pyro","Quicksilver","Rage","Ralph","Ranger","Razor","Rebel","Rex","Rider","Riggs","Ripley","Riptide","Rogue","Rover","Scar","Scout","Shade","Shadow","Shepherd","Shredder","Silver","Skye","Slate","Sly","Smoke","Splinter","Steele","Storm","Striker","Summit","Tank","Thor","Thunder","Timber","Titan","Tooth","Trace","Trapper","Trouble","Tundra","Vapor","Whisper","Wolf","Acadia","Aiyana","Akita","Alaska","Alexia","Alexis","Alize","Alpine","Amber","Amethyst","Angel","Ares","Ari","Aspen","Astral","Athena","Atilla","Aura","Aurora","Avril","Babe","Banshee","Beauty","Blaze","Blitz","Blitzen","Blossom","Bo","Boone","Breeze","Charm","Chronis","Clarity","Cleo","Codex","Coral","Crystal","Dakota","Dash","Dawn","Delphi","Destiny","Dharma","Diva","Dodger","Dot","Duchess","Ebony","Echo","Eclipse","Enigma","Faith","Fern","Gemini","Gia","Girl","Grace","Hailey","Heather","Heaven","Helen","Hope","Ice","Indigo","Iris","Ivory","Ivy","Jade","Jasmine","Jewel","Jinx","June","Juno","Justice","Jynx","Karma","Kenya","Lady","Laika","Levi","Lexis","Liberty","Lore","Lotus","Luna","Maple","Maxima","Meadow","Mello","Melody","Mercy","Midnight","Mona","Moone","Myst","Mysti","Mystique","Myth","Nanook","Nova","Nymph","Nyx","Omen","Onyxia","Opal","Oracle","Pandora","Paws","Pearl","Pepper","Phantom","Phoenix","Precious","Princess","Pyro","Queen","Rags","Raine","Raven","Rogue","Sable","Saffron","Sapphire","Satin","Scarlet","Shade","Shadow","Silver","Snow","Snowball","Snowflake","Solstice","Star","Twilight","Vapor","Velvet","Violet","Vixen","Whisper","Willow","Winter","Xena","Zelda").toLowerCase();	
	}
	
	public static String randomBearName() {
		return extra.choose("Adalbero","Aloysius","Andy","Anuk","Arcadius","Arcot","Arkadios","Arktos","Armel","Arnbjorn","Arshag","Art","Artair","Artan","Arther","Arthfael","Arthmael","Arthog","Arthur","Artie","Artis","Arto","Artorius","Arttu","Artturi","Artur","Arturas","Arturo","Arty","Asbjorn","Attie","Atty","Auberon","Avery","Avonaco","Bam-Bam","Bamard","Bamey","Banjo","Barbell","Barend","Barley","Barnard","Barnea","Barney","Barnie","Barny","Barret","Barrett","Barry","Basil","Bastian","Beamard","Bear","Bearnabus","Bearnard","Behrend","Beirne","Bemelle","Bemot","Benard","Benat","Benno","Benton","Beorn","Beornheard","Beowulf","Ber","Beraco","Beranger","Beregiso","Berend","Berengar","Berengarius","Berenger","Berenguer","Berernger","Beringar","Beringarius","Berinhard","Bern","Bernaldino","Bernard","Bernardino","Bernardo","Bernardus","Bernardyn","Bernat","Bernd","Berndt","Berne","Berney","Bernfried","Bernhard","Bernhardt","Bernie","Bernon","Bernt","Bernward","Berny","Bero","Billie","Biorna","Bitsy","Bjarne","Bjarni","Bjoern","Bjorn","Bjornar","Bjorne","Blubber","Bobby","Bobo","Boo Boo","Boots","Burnard","Burney","Buttercup","Buttons","Calico","Capps","Caramel","Casey","Cedar","Chancie","Charlie","Chip","Coco","Cornelius","Dopey","Dov","Drogo","Dubi","Dusty","Eden","Edun","Einstein","Enyeto","Esben","Espen","Finley","Flubber","Fluffy","Frankie","Freddy","Garcia","Geoff","George","Georgie","Georgy","Gerben","Grumpy","Gunnbjorn","Hallbjorn","Hamilton","Happy","Hartz","Hausu","Henri","Henry","Hohots","Honaw","Honon","Horace","Howell","Humbert","Humphrey","Huslu","Jack","James","Jammy","Jasper","Joachim","Johnny","Jonsey","Jupiter","Justin","Kolbjorn","Kuma","Kuruk","Lannie","Lennie","Liwanu","Louis","Machk","Mahon","Marley","Marshmellow","Mathe","Mathuin","Matoskah","Mecho","Mitch","Molimo","Myr","Nanook","Nanuk","Nibbs","Niels","Norman","Notaku","O'Berry","Oberon","Oliver","Omar","Orion","Ors","Orsen","Orsin","Orsino","Orso","Orson","Osbeorn","Osborn","Osborne","Osbourne","Oscar","Otso","Ottille","Otto","Ourson","Panda","Pandy","Pat","Patches","Pebbles","Ponty","Popey","Preben","Pridbjorn","Quadro","Rio","Robbie","Rocky","Rolly","Rum","Sabby","Sammy","Scoot","Scottie","Sebastian","Sewati","Shorty","Sigbjorn","Skittle","Sleepy","Smokey","Snowball","Sonny","Sooty","Spencer","Spike","Spiky","Stormy","Sugar","Sunshine","Svenbjorn","Tabby","Talbert","Tarben","Tatty","Teddy","Telutci","Theo","Theodore","Thorben","Thorbern","Thorbjorn","Thorburn","Thorton","Tickles","Tims","Toby","Toffy","Tony","Torben","Torbern","Torbernus","Torbjorn","Tottles","Trevor","Trump","Tubby","Tuketu","Turi","Twinky","Ucumari","Uffo","Uigbiorn","Urs","Ursino","Ursinus","Ursus","Uther","Uzumati","Vemados","Vermundo","Vernados","Victor","Waffle","Walter","Willie","Winston","Woodsy","Wyborn","Yana","Zed","Abby","Angel","Apple-pie","Arcadia","Ariane","Armelle","Arthuretta","Arthurine","Arti","Averi","Averie","Avery","Ayla","Banjo","Barrett","Barretta","Beatrice","Bella","Bemadette","Bera","Beratrice","Berdine","Berengari","Berengaria","Berenice","Bern","Bernadea","Bernadete","Bernadett","Bernadette","Bernadina","Bernadine","Bernarda","Bernarde","Bernardete","Bernardetta","Bernardette","Bernardina","Bernardine","Bernardita","Berne","Berneen","Bernelle","Bitsy","Blubber","Bobo","Boo Boo","Boots","Bubbles","Buttercup","Buttons","Calico","Calista","Callista","Callisto","Caramel","Clymene","Coco","Dandelion","Dopey","Doris","Dusty","Eden","Eferhild","Eferhilda","Elizabeth","Emily","Fatima","Flubber","Fluffy","Georgy","Grumpy","Hagar","Happy","Hausu","Heltu","Honey","Irene","Isobel","Izzy","Jammy","Jane","Jerica","Jewel","Jupiter","Justine","Kuma","Louis","Louise","Lusela","Maggie","Mahtowa","Margaret","Marshmellow","Mecislava","Melanie","Miffy","Myr","Nadetta","Nadette","Nibbs","Nita","Orsa","Orsaline","Orse","Orsel","Orselina","Orseline","Orsina","Orsola","Orsolya","Osha","Ottilie","Pam","Panda","Pandy","Pat","Patches","Patricia","Peaches","Pebbles","Penny","Persephone","Poe","Polly","Puddles","Queenie","Rio","Rolly","Rosie","Roxie","Sabby","Samantha","Sammie","Sandra","Sapata","Sargie","Sienna","Skittle","Sleepy","Smokey","Snowball","Sugar","Sunshine","Susanna","Susie","Suzie","Tabby","Taffy","Tatty","Thorborg","Tickles","Toffy","Torborg","Tottles","Tubby","Twinky","Ursa","Ursala","Ursel","Ursella","Ursicina","Ursina","Ursine","Urska","Ursula","Ursule","Ursulina","Urszula","Uschi","Valerie","Venus","Veronica","Versula","Viola","Violet","Violette","Waffle","Wilhelmina","Winifred","Winnie","Winona").toLowerCase();
	}
	
	public static String randomBatName() {
		return extra.choose("Ace","Acrobat","Ajax","Angel","Apollo","Archangel","Artemis","Ash","Azar","Azral","Baltazar","Bandit","Bane","Basil","BatPitt","Batista","Batley","Baxter","Beaker","Bigglesworth","Bitz","Blackjack","Blade","Blaze","Blitz","Bloodwing","Blues","Booboo","Bruce","Brutus","Bubba","Bubbles","Bullet","Buster","Butch","Buttons","Chaos","Char","Chocula","Cole","Comet","Cookie","Count","Cupcake","Darkess","Darth","Dexter","Diablo","Dimitri","Ding","Dodge","Dodger","Doom","Drac","Dracula","Draculon","Drake","Echo","Equinox","Fangs","Flapper","Flappy","Flaps","Flash","Flicker","Fuzz","Gambat","Gargle","Gargles","Gargoyle","Gavalon","Ghost","Gizmo","Glider","Gloom","Glyde","Golbat","Gouge","Grey","Guano","Hannibal","Hawke","Hunter","Hyperion","Impaler","Jet","Kane","Khan","Kindle","Lecter","Lockjaw","Lucifer","Marble","Matrix","Merlin","Midas","Midnight","Mirage","Monty","Moon","Mothra","Muse","Nerf","Nibbles","Nightmare","Nightwing","Nugget","Nukem","Nyx","Onyx","Orion","Ozzy","Patch","Patches","Pebbles","Phantom","Pickle","Psych","Quickfang","Quilla","Rabies","Rainbow","Rascal","Remus","Render","Rhonin","Ripmaw","Rocky","Rufus","Sabath","Sawyer","Screech","Screechy","Shade","Shadow","Shreek","Shrike","Slate","Slithe","Snuffle","Sonar","Sonny","Spectre","Spitfire","Spudnik","Spuds","Swoops","Thunder","Tiberius","Titan","Twinkle","Umber","Vamp","Vlad","Vladimir","Vulkan","Wayne","Wiggles","Wingnut","Xanadu","Zion","Abby","Aerial","Aine","Alexia","Alexis","Amber","Angel","Angie","Apple","Ash","Atilla","Aura","Aurora","Azraelle","Azure","Azurys","Babes","Bandetta","Batsy","Batty","Beauty","Biscuit","Blaze","Breeze","Bubble","Bubbles","Buttercup","Buttons","Calypso","Cerulean","Chuckles","Cinderella","Cinnamon","Clementine","Cleo","Cookie","Cosmo","Cuddles","Cupcake","Dakota","Daphne","Dawn","Dawne","Dawnstar","Dot","Draculette","Ebony","Echo","Eclipse","Ember","Enigma","Equina","Equinox","Fang","Fangie","Faune","Fierra","Fizzle","Flappy","Fluffy","Flutters","Gadget","Gargles","Giggles","Grace","Guani","Harmony","Haze","Hazel","Honey","Huntress","Iggy","Illumina","Indigo","Iris","Ivory","Ivy","Jinx","Jynx","Lady","Liberty","Lucy","Lullaby","Luna","Mable","Marbles","Maya","Melody","Mirage","Mittens","Moonbeam","Moone","Moonlight","Morning","Morticia","Myst","Mystique","Nibbles","Nighte","Noodles","Nova","Nugget","Oracle","Peaches","Pebbles","Pepper","Phoenix","Pickle","Pickles","Plume","Precious","Princess","Psyche","Raine","Raven","Rebel","Rhyme","Rogue","Ruth","Sade","Sage","Scarlett","Shade","Shadow","Shay","Shine","Siren","Skye","Skylar","Snuffles","Sona","Sora","Star","Stardust","Starlight","Sugar","Tinkerbell","Trixie","Trixy","Twilight","Twinkle","Twinkles","Vanity","Velvet","Violet","Vixen","Wiggles","Xena").toLowerCase();
	}
	
	public static String randomEntName() {
	return extra.choose("Abies","Acacia","Acca","Acer","Adansonia","Aesculus","Agathis","Agonis","Albizia","Aleurites","Alianthus","Alnus","Amalanchier","Amborella","Amentotaxus","Anacardium","Annona","Anogeissus","Antiaris","Aralia","Araucaria","Arbutus","Ardisia","Areca","Arenga","Argania","Artocarpus","Asimina","Athrotaxis","Azadirachta","Baccharis","Bactris","Bauhinia","Betula","Bombax","Borassus","Bourreria","Brachylaena","Brahea","Brosimum","Broussonetia","Bucida","Bursera","Busus","Butia","Byrsonima","Caesalpinia","Callistemon","Callitris","Calocedrus","Calophyllum","Calyptranthes","Canella","Capparis","Caragana","Carica","Carpinus","Carya","Caryota","Cassia","Castanea","Castanopsis","Castilla","Casuarina","Catalpa","Cecropia","Cedrela","Cedrus","Ceiba","Celtis","Ceratonia","Cercis","Chamaecyparis","Chilopsis","Cinnamomum","Citrus","Cladrastis","Clethra","Clusia","Cocos","Coffea","Combretum","Copernicia","Cordia","Cordyline","Cornus","Corylus","Corymbia","Corypha","Crataehus","Cupressus","Cussonia","Cycas","Cyrilla","Dacrycarpus","Dacrydium","Delonix","Diospyros","Dracaena","Drypetes","Durio","Elaeagnus","Elaeis","Elliottia","Erica","Eriobotrya","Erythrina","Eucommia","Eugenia","Euonymus","Euphorbia","Fagus","Ficu","Firmiana","Fraxinus","Garcinia","Ginkgo","Gleditsia","Gonystylus","Gordonia","Grevillea","Guibourtia","Gymnanthes","Halesia","Hamamelis","Harpullia","Hevea","Hibiscus","Hippomane","Howea","Hymenaea","Hyophorbe","Ilex","Illicium","Inga","Jacaranda","Jubaea","Juglans","Juniperus","Kalopanax","Khaya","Kigelia","Kokia","Laburnum","Lagunaria","Laurus","Lecythis","Leucaena","Licaria","Liquidambar","Liriodendron","Litchi","Lithocarpus","Livistona","Lodoicea","Lysiloma","Machaerium","Maclura","Magnolia","Malpighia","Malus","Mangifera","Maranthes","Maytenus","Medusagyne","Melia","Meryta","Metopium","Michelia","Millettia","Mimosa","Moringa","Morus","Musa","Myoporum","Myrica","Myristica","Myrsine","Myrtus","Nectandra","Nerium","Nyssa","Olea","Ostrya","Palaquium","Parrotia","Paulownia","Peltogyne","Pentaclethra","Persea","Phellodendron","Phytelephas","Picea","Pinus","Piscidia","Pistacia","Platanus","Plumeria","Populus","Prosopis","Prunus","Psidium","Pyrus","Quercus","Radermachera","Raphia","Rhapis","Rhizophora","Rhododendron","Rhus","Robinia","Sabal","Salix","Salvadora","Sambucus","Sapium","Sassafras","Schaefferia","Schefflera","Senegalia","Sequioa","Serenoa","Shorea","Sideroxylon","Sondias","Sophora","Sorbus","Stewartia","Syagrus","Syringa","Tabebuia","Taiwania","Talipariti","Tamarix","Taxandria","Taxus","Tectona","Tetradium","Theobroma","Thevetia","Thuja","Tilia","Tipuana","Toona","Torreya","Trema","Triadica","Tristaniopsis","Ulmus","Vachellia","Vernicia","Vitex","Wodyetia","Wollemia","Xylosma","Yucca","Zelkova").toLowerCase();
	}
	/**
	 * Returns a random adjective 50% of the time. Used to make 'weapon of X Y" statements fancier
	 * @return String
	 */
	public static String powerAdjective() {
		return (String)extra.choose("",extra.choose("undoubtable","impossible","endless","incredible","soaring","infinite","amazing","implausible","miraculous","astonishing","astounding","awesome","extraordinary","wondrous","bewildering","supernatural","great","considerable","extreme","abundant","prodigious") + " ");
	}

	public static String randomWarrior() {
		return extra.choose("fighter","duelist","warrior","gladiator","scrapper","mercenary","brusier");
	}

	public static String randomMuggerName() {
		return extra.choose("mugger","robber","thug","bandit","marauder","outlaw","desperado","cutthroat");
	}
}
