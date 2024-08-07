package trawel.personal.classless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import derg.menus.MenuItem;
import derg.menus.ScrollMenuGenerator;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.core.Input;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Person;
import trawel.personal.Person.FeatArchMenuPick;
import trawel.personal.Person.PersonFlag;
import trawel.personal.classless.Feat.FeatType;
import trawel.personal.classless.Skill.Type;

public enum Archetype implements IHasSkills{
	//TODO: making archetypes have more skills and generally be more impactful across the board, might need to reduce how often you get them offered
	//after the first 2 levels
	
	/**
	 * Archetypes should typically grant 2 real skills and then possibly a stance/tactics or a third skill
	 * they should also grant 15 stats
	 */
	
	/**
	 * try for 3 unlocks per archetype
	 */
	
	
	
	/**
	 * somewhat inspired by dd1 enemy
	 */
	VIRAGO("Venomous Virago"
			,"An expert on toxic incantations, salves, potions, and the like."
			,null
			,AType.AFTER_PERSONABLE_ONLY//after because toxic brews
			,EnumSet.of(AGroup.MAGIC,AGroup.CRAFT)
			,EnumSet.of(FeatType.MYSTIC,FeatType.CURSES,FeatType.POTIONS)
			//npcs can get potions
			,EnumSet.of(Skill.TOXIC_BREWS,Skill.P_BREWER,Skill.FEVER_STRIKE)
			,0,3,12
			)
	,GLADIATOR("Glitzy Gladiator"
			,"Real glory hogs know that fighting dirty is part of the show."
			,null
			,AType.ENTRY
			,EnumSet.of(AGroup.DIRTY, AGroup.CHARISMA)
			,EnumSet.of(FeatType.TRICKS,FeatType.SPIRIT,FeatType.BATTLE)
			//TODO: Skill.OPPORTUNIST maybe?
			,EnumSet.of(Skill.SPUNCH,Skill.DSTRIKE,Skill.TACTIC_DUCK_ROLL,Skill.TACTIC_SINGLE_OUT,Skill.TACTIC_CHALLENGE)
			,7,7,1
			)
	,ARMORMASTER("Armor Artisan"
			,"A walking fortress, advancing steadily into the fray."
			,null
			,AType.ENTRY
			,EnumSet.of(AGroup.DIRECT_BATTLE,AGroup.CRAFT)
			,EnumSet.of(FeatType.BATTLE,FeatType.CRAFT,FeatType.BRAWN)
			,EnumSet.of(Skill.ARMOR_TUNING,Skill.ARMORSPEED,Skill.ARMORHEART)
			,15,0,0
			)
	/**
	 * inspired by a lot of ttrpgs I used to talk about with some friends
	 */
	,HEDGE_MAGE("Hedge Mage"
			,"A perpetual novice, hedge mages aren't content to restrict themselves to one school."
			,"Grants basic arcane magic based on clarity."
			,AType.ENTRY_PERSONABLE_ONLY
			,EnumSet.of(AGroup.MAGIC,AGroup.GRANTED_ARCANIST)
			,EnumSet.of(FeatType.MYSTIC,FeatType.ARCANE,FeatType.SOCIAL)
			,EnumSet.of(Skill.LIFE_MAGE,Skill.ARCANIST)
			,0,0,20//less skills, 5 bonus stats
			)
	,FISH_MONSOON("Monsoon Maker"
			,"Calls up malicious magic that assails the land-people's domain."
			,"Grants oceanic occultisms that use clarity."
			,AType.RACIAL
			,EnumSet.of(AGroup.MAGIC,AGroup.GRANTED_ARCANIST)
			,EnumSet.of(FeatType.MYSTIC,FeatType.ARCANE,FeatType.CURSES)
			,EnumSet.of(Skill.ARCANIST,Skill.ELEMENTALIST,Skill.M_CRYO,Skill.M_AERO,Skill.PLOT_ARMOR)
			,0,0,15
			)
	,SEA_SAGE("Sea Sage"
			,"Tempered by a still calm, they can call forth the Sea's wrath if provoked."
			,"Grants oceanic occultisms that use clarity."
			,AType.ENTRY
			,EnumSet.of(AGroup.MAGIC,AGroup.GRANTED_ARCANIST)
			,EnumSet.of(FeatType.MYSTIC,FeatType.ARCANE,FeatType.SPIRIT)
			,EnumSet.of(Skill.DODGEREF,Skill.PRESS_ADV,Skill.ARCANIST)//press adv now simulates flow
			,2,2,11
			)
	,FISH_TALL("Torrental Titan"
			,"A raging force of the deep-sea bent on proving their conquest of land is inevitable."
			,"Grants disrespectful attacks that scale on strength."
			,AType.RACIAL
			,EnumSet.of(AGroup.DIRECT_BATTLE)
			,EnumSet.of(FeatType.BATTLE,FeatType.BRAWN,FeatType.SPIRIT)
			,EnumSet.of(Skill.STERN_STUFF,Skill.RAW_GUTS,Skill.DSTRIKE,Skill.OPPORTUNIST)
			,15,0,0
			)
	/**
	 * reference to Litheness in dragon quest 9
	 */
	,ACRO_DAREDEVIL("Dauntless Daredevil"
			,"Acrobatical prowess untempered by fright and funneled forth into fighting is a fearsome force."
			,"Grants opportunistic attacks that use the higher of strength or dexterity."
			,AType.ENTRY
			,EnumSet.of(AGroup.DEXTERITY)
			,EnumSet.of(FeatType.BATTLE,FeatType.AGILITY,FeatType.SPIRIT)//not tricks
			//doesn't have tactics, just random opportunities, to fit the theme
			,EnumSet.of(Skill.BLITZ,Skill.DODGEREF,Skill.OPPORTUNIST)
			,3,12,0
			)
	,ANIMAL_MIMIC("Mimic"
			,"TODO"
			,null
			,AType.RACIAL
			,EnumSet.of(AGroup.CRAFT)
			,EnumSet.of(FeatType.TRICKS,FeatType.BATTLE)
			,EnumSet.of(Skill.RACIAL_SHIFTS)
			)
	,FELL_REAVER("Fell Reaver"
			,"TODO"
			,null
			,AType.RACIAL
			,EnumSet.of(AGroup.MAGIC)
			,EnumSet.of(FeatType.CURSES)
			,EnumSet.of(Skill.RACIAL_SHIFTS)
			)
	,ARCHMAGE("Archmage"//now an AFTER archetype
			,"Weaving spells has become as easy as breathing."
			,"Grants no spells, but gives another arcany config slot."
			,AType.AFTER//not personable in the case that they get arcany through less-study methods
			,EnumSet.of(AGroup.GRANTED_ARCANIST)//do not change or add to unless adding skill requirements to archetypes
			,EnumSet.of(FeatType.ARCANE)
			,EnumSet.of(Skill.ARCANIST_2,Skill.ELEMENTALIST),0,0,25//lot of clarity
			)
	,HIRED_HATCHET("Hired Hatchet",
			"Trading lives is a glamorous business for some, but Hired Hatchets just need to get the job done."
			,null
			,AType.ENTRY
			,EnumSet.of(AGroup.DIRTY,AGroup.DEXTERITY)
			,EnumSet.of(FeatType.TRICKS,FeatType.FINESSE,FeatType.BATTLE)
			,EnumSet.of(Skill.NO_QUARTER,Skill.DSTRIKE,Skill.DEADLY_AIM)
			,2,10,3
			)
	,FIGHTING_FURY("Fighting Fury"
			,"Coherent rage and savagery set Furies apart from both civilized folk and enchanted monsters."
			,null,
			AType.ENTRY
			,EnumSet.of(AGroup.DIRECT_BATTLE,AGroup.STRENGTH)
			,EnumSet.of(FeatType.BATTLE,FeatType.BRAWN,FeatType.SPIRIT)
			,EnumSet.of(Skill.BLOODTHIRSTY,Skill.BULK,Skill.COUNTER)
			,12,3,0
			)
	,RUNEBLADE("Rune Blade"
			,"Enchanting is a fickle art, but a Rune Blade hammers the elements into their weapons nonetheless."
			,null,
			AType.ENTRY_PERSONABLE_ONLY//special only so npcs don't get rune abilities which would only work if they were forced to have enchanthits
			,EnumSet.of(AGroup.CRAFT)
			//less feattypes and skills due to being a special added
			,EnumSet.of(FeatType.CRAFT,FeatType.MYSTIC)
			,EnumSet.of(Skill.RUNESMITH,Skill.RUNIC_BLAST)//only two but former gives OOC benefit as well
			,7,0,13//stats slightly better, 20 instead of 15
			)
	,CUT_THROAT("Cut Throat",
			"Why get blood on your hands when you can get blood on your knife instead?"
			,null
			,AType.ENTRY
			,EnumSet.of(AGroup.DIRECT_BATTLE,AGroup.DEXTERITY)
			,EnumSet.of(FeatType.AGILITY,FeatType.BATTLE,FeatType.FINESSE)
			,EnumSet.of(Skill.OPEN_VEIN,Skill.DEADLY_AIM,Skill.QUICK_START)
			,3,12,0
			)
	,CHEF_ARCH("Comestible Critic",
			"Always bring snacks to battle."
			,null
			,AType.ENTRY_PERSONABLE_ONLY
			,EnumSet.of(AGroup.CRAFT)
			,EnumSet.of(FeatType.CRAFT,FeatType.POTIONS,FeatType.SOCIAL)
			//gets 4 skills because all are generally lower impact
			,EnumSet.of(Skill.CHEF,Skill.BIG_BAG,Skill.BULK,Skill.P_BREWER)
			,10,1,4
			)
	//racial archetypes to allow thematic FeatType selection, don't count as feat point consumers
	,ANIMAL_BEAR_STRONG("Strong Beast"
			,"A strong animal."
			,null
			,AType.RACIAL
			,EnumSet.of(AGroup.DIRECT_BATTLE)
			,EnumSet.of(FeatType.BRAWN,FeatType.BATTLE,FeatType.SPIRIT)
			,EnumSet.of(Skill.RAW_GUTS,Skill.BULK)
			,30,0,0
			)
	,ANIMAL_WOLF_PACK("Pack Hunter"
			,"A social hunter."
			,null
			,AType.RACIAL
			,EnumSet.of(AGroup.DIRECT_BATTLE)
			,EnumSet.of(FeatType.AGILITY,FeatType.TRICKS,FeatType.BATTLE)
			,EnumSet.of(Skill.SPUNCH)
			,5,20,5
			)
	,ANIMAL_BAT("Night Hunter"
			,"A night hunter."
			,null
			,AType.RACIAL
			,EnumSet.of(AGroup.DIRECT_BATTLE)
			,EnumSet.of(FeatType.AGILITY,FeatType.FINESSE)
			,EnumSet.of(Skill.SPEEDDODGE)
			,0,20,0
			)
	,ANIMAL_UNICORN("Magic Horse"
			,"A magic horse."
			,null
			,AType.RACIAL
			,EnumSet.of(AGroup.DIRECT_BATTLE)
			,EnumSet.of(FeatType.MYSTIC,FeatType.ARCANE,FeatType.SPIRIT)
			,EnumSet.of(Skill.PLOT_ARMOR,Skill.BULK,Skill.LIFE_MAGE)
			,10,0,30
			)
	;
	
	private final String name, desc, stanceDesc;
	private final Set<Skill> skills;
	private final int strength, dexterity, clarity;
	private final AType type;
	private final Set<AGroup> groups;
	private final Set<FeatType> fTypes;
	Archetype(String _name, String description,String _stanceDesc, AType _type, Set<AGroup> _groups,Set<FeatType> _fTypes, Set<Skill> skillset){
		name = _name;
		desc = description;
		skills = skillset;
		strength = 0;
		dexterity = 0;
		clarity = 0;
		type = _type;
		groups = _groups;
		fTypes = _fTypes;
		stanceDesc = _stanceDesc;
	}
	Archetype(String _name, String description,String _stanceDesc, AType _type, Set<AGroup> _groups,Set<FeatType> _fTypes, Set<Skill> skillset
			,int _strength, int _dexterity, int _clarity){
		name = _name;
		desc = description;
		skills = skillset;
		type = _type;
		groups = _groups;
		fTypes = _fTypes;
		stanceDesc = _stanceDesc;
		strength = _strength;
		dexterity = _dexterity;
		clarity = _clarity;
		//ugh final fields make chained constructors annoying
	}
	
	public enum AType{
		RACIAL,//race archetypes
		ENTRY,//can appear as first archetype choice, also later on
		AFTER,//can't appear as first choice, but can appear after first choice
		ADDED,//similar to perk, but mostly for npcs
		//these two can only appear on personable NPCs, ie those who could get potions and equipment from towns
		ENTRY_PERSONABLE_ONLY,
		AFTER_PERSONABLE_ONLY
	}
	
	public enum AGroup{
		DEXTERITY, STRENGTH,
		MAGIC, CRAFT,
		DIRTY, CHARISMA,
		DIRECT_BATTLE,
		/**
		 * implicit way to gate archmage, by using the disjoint.
		 */
		GRANTED_ARCANIST
	}
	private static final Set<Archetype> ENTRY_LIST = EnumSet.noneOf(Archetype.class);
	private static final Set<Archetype> AFTER_LIST = EnumSet.noneOf(Archetype.class);
	private static final Set<Archetype> ENTRY_LIST_PERSONABLE = EnumSet.noneOf(Archetype.class);
	private static final Set<Archetype> AFTER_LIST_PERSONABLE = EnumSet.noneOf(Archetype.class);
	static {
		for (Archetype a: Archetype.values()) {
			switch (a.type) {
			case ENTRY:
				ENTRY_LIST.add(a);
			case ENTRY_PERSONABLE_ONLY:
				ENTRY_LIST_PERSONABLE.add(a);
			case AFTER:
				AFTER_LIST.add(a);
			case AFTER_PERSONABLE_ONLY:
				//used only for player
				AFTER_LIST_PERSONABLE.add(a);
				break;//end fallthrough
				
			//not put in base lists
			case ADDED:
			case RACIAL:
				break;
			}
		}
	}
	 
	
	@Override
	public Stream<Skill> collectSkills() {
		return skills.stream();
	}
	
	@Override
	public String getText() {
		String str = name + ": "+desc;
		for (Skill s: skills) {
			if (s.getType() == Type.INTERNAL_USE_ONLY) {
				continue;
			}
			str += "\n "+IHasSkills.padNewlines(s.disp());
		}
		return str;
	}
	
	@Override
	public String getOwnText() {
		return TrawelColor.ITEM_VALUE+ name +TrawelColor.PRE_WHITE+ ": "+desc;
	}
	
	@Override
	public String getBriefText() {
		return TrawelColor.ITEM_VALUE+ name +TrawelColor.PRE_WHITE + ": "+desc;
	}
	
	@Override
	public String getStanceText() {
		if (WeaponAttackFactory.hasStance(this)) {
			return stanceDesc;
		}
		return null;
	}
	
	/**
	 * used for the first set of archetypes, but also the 'not like the ones you have' in the set after
	 */
	public static List<Archetype> getFirst(Person p,int desiredAmount, Set<Archetype> has) {
		List<Archetype> list = new ArrayList<Archetype>();
		List<Archetype> options = new ArrayList<Archetype>();
		for (Archetype a: (p.isPersonable() ? ENTRY_LIST_PERSONABLE : ENTRY_LIST)) {
			if (!has.contains(a)) {
				options.add(a);
			}
		}
		
		for (int i = 0; i < 30 && list.size() < desiredAmount;i++) {
			if (options.size() == 0) {
				break;
			}
			Archetype choice = Rand.randList(options);
			options.remove(choice);
			for (Archetype blocker: list) {
				if (!choice.canFirstWith(blocker)) {
					continue;
				}
			}
			list.add(choice);
		}
		return list;
	}
	
	/**
	 * used for the 'more like this' in the second set of archetypes
	 * <br>
	 * only makes sense when 'more like this' is one archetype, if you have more pick a random one
	 */
	public static List<Archetype> getAfter(Person p,int desiredAmount, Archetype has, Set<Archetype> extendedHas) {
		List<Archetype> list = new ArrayList<Archetype>();
		List<Archetype> options = new ArrayList<Archetype>();
		
		for (Archetype a: (p.isPersonable() ? AFTER_LIST_PERSONABLE : AFTER_LIST)) {
			if (!extendedHas.contains(a)) {
				if (!Collections.disjoint(has.groups,a.groups)) {//if we have at least one thing in common
					options.add(a);
				}
			}
		}
		
		for (int i = 0; i < 30 && list.size() < desiredAmount;i++) {
			if (options.size() == 0) {
				break;
			}
			Archetype choice = Rand.randList(options);
			options.remove(choice);
			for (Archetype blocker: list) {
				if (!choice.doesAfterWith(blocker)) {
					continue;
				}
			}
			list.add(choice);
		}
		return list;
	}
	
	public boolean canFirstWith(Archetype t) {
		if (t.equals(this) || t.groups.equals(groups)) {//if we have the same exact groups or are the same
			return false;
		}
		//DOLATER: try to prevent overlap as well
		return true;
	}
	
	public boolean doesAfterWith(Archetype t) {
		//if we're not equal and we share one element
		return (!t.equals(this) && !Collections.disjoint(t.groups,groups));
	}

	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}
	
	@Override
	public int getClarity() {
		return clarity;
	}
	
	@Override
	public String friendlyName() {
		return name;
	}
	
	@Override
	public String menuName() {
		switch (type) {
		default:
		case AFTER: case AFTER_PERSONABLE_ONLY:
		case ENTRY: case ENTRY_PERSONABLE_ONLY:
			return friendlyName();
		case ADDED:
			return friendlyName() + " (Added Only)";
		case RACIAL:
			return friendlyName() + " (Species Only)";
		}
	}

	@Override
	public boolean goMenuItem() {
		Print.println("n/a");
		return false;
	}

	public static List<IHasSkills> getFeatChoices(Person person) {
		List<IHasSkills> list = new ArrayList<IHasSkills>();
		Set<Archetype> pAs = person.getArchSet();
		int nonRacialAs = (int) pAs.stream().filter(a -> a.type != AType.RACIAL).count();
		if (person.getFlag(PersonFlag.CAN_LEARN) && nonRacialAs == 0) {
			list.addAll(getFirst(person,6,pAs));//current game never actually has this matter because the player gets it randomly chosen, or they get to pick it from the entire list
			return list;
		}
		
		//now tries to get you to have 2 + floor(level/5) archetypes
		//can't learn any if canlearn flag is not set
		if (person.getFlag(PersonFlag.CAN_LEARN) && nonRacialAs < 2+Math.floor(person.getLevel()/5)) {
			Set<Archetype> restrictSet = EnumSet.copyOf(pAs);
			List<Archetype> newList = getFirst(person,2,restrictSet);//first moved up so that it doesn't get blocked by friends of friends basically
			list.addAll(newList);
			restrictSet.addAll(newList);
			list.addAll(getAfter(person,2,pAs.iterator().next(),restrictSet));
			//fall through and fill the rest with normal feats
		}
		/*int baseArch = list.size();
		
		if (baseArch == 0 && nonRacialAs >= 2) {
			if (extra.chanceIn(1,3)) {//1 in 3 chance, but dupes just cause it to not go through
				//this simulates decreasing as you get more
				Archetype addA = extra.randCollection(AFTER_LIST);
				if (!pAs.contains(addA)) {
					list.add(addA);
				}
			}
			if (extra.chanceIn(1,pAs.size())) {//get a similar archetype to a random one we have
				Archetype addA = getAfter(1,extra.randCollection(pAs),pAs).stream().findFirst().orElse(null);
				if (addA != null) {
					list.add(addA);
				}
			}
		}*/
		Set<Feat> fset = EnumSet.copyOf(person.getFeatSet());
		Set<FeatType> allowSet = EnumSet.of(FeatType.COMMON);
		for (Archetype a: pAs) {
			allowSet.addAll(a.getFeatTypes());
		}
		if (list.size() < 8) {
			list.addAll(Feat.randFeat(person,8-list.size(),allowSet,fset,person.fetchSkills())); 
		}
		
		return list;
	}

	public Set<FeatType> getFeatTypes() {
		return fTypes;
	}

	@Override
	public Set<Skill> giveSet() {
		return skills;
	}
	
	public static void menuChooseFirstArchetype(Person person) {
		Print.println("Please choose a starting Archetype.");
		List<Archetype> alist = new ArrayList<Archetype>();
		alist.addAll(ENTRY_LIST_PERSONABLE);
		//int start_points = person.getFeatPoints();
		Input.menuGo(new ScrollMenuGenerator(alist.size(),"previous <> Archetypes","next <> Archetypes") {
			
			@Override
			public List<MenuItem> header() {
				return null;
			}
			
			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new FeatArchMenuPick(alist.get(i),person));
				//should return if picked, so we don't even need to check anything
				return list;
			}
			
			@Override
			public List<MenuItem> footer() {
				return null;
			}
		});
	}
	
	public static void menuChooseSecondArchetype(Person person) {
		person.updateSkills();
		Archetype starting = person.getArchSet().stream().findAny().get();
		Print.println("Please choose a second Unlock to go with "+starting.friendlyName()+".");
		List<IHasSkills> alist = new ArrayList<IHasSkills>();
		
		//use player list
		AFTER_LIST_PERSONABLE.stream().filter(a -> a.doesAfterWith(starting)).forEach(alist::add);
		ENTRY_LIST_PERSONABLE.stream().filter(a -> !alist.contains(a)).forEach(alist::add);
		
		Set<FeatType> allowSet = EnumSet.of(FeatType.COMMON);
		allowSet.addAll(starting.getFeatTypes());
		Feat.FEAT_LIST_PERSONABLE.stream().filter(f -> f.featValid(allowSet,EnumSet.noneOf(Feat.class),person.fetchSkills())).forEach(alist::add);
		
		alist.remove(starting);
		//int start_points = person.getFeatPoints();
		Input.menuGo(new ScrollMenuGenerator(alist.size(),"previous <> Unlocks","next <> Unlocks") {
			
			@Override
			public List<MenuItem> header() {
				return null;
			}
			
			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new FeatArchMenuPick(alist.get(i),person));
				//should return if picked, so we don't even need to check anything
				return list;
			}
			
			@Override
			public List<MenuItem> footer() {
				return null;
			}
		});
		person.updateSkills();
	}

	public AType getType() {
		return type;
	}
}
