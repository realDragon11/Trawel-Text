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
