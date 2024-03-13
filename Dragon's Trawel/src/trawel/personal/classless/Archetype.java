package trawel.personal.classless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import derg.menus.MenuItem;
import derg.menus.ScrollMenuGenerator;
import trawel.extra;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.personal.Person;
import trawel.personal.Person.FeatArchMenuPick;
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
	 * somewhat inspired by dd1 enemy
	 */
	VIRAGO("Venomous Virago"
			,"An expert on toxic incantations, salves, potions, and the like."
			,null
			,AType.ENTRY
			,EnumSet.of(AGroup.MAGIC,AGroup.CRAFT)
			,EnumSet.of(FeatType.MYSTIC,FeatType.CURSES,FeatType.POTIONS)
			,EnumSet.of(Skill.TOXIC_BREWS,Skill.P_BREWER,Skill.FEVER_STRIKE)
			,0,3,12
			)
	,GLADIATOR("Glitzy Gladiator"
			,"Attention seeking but no-nonsense physical fighter. Fighting dirty is part of the show."
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
			,EnumSet.of(FeatType.BATTLE,FeatType.SMITHS)
			,EnumSet.of(Skill.ARMOR_TUNING,Skill.ARMORSPEED,Skill.ARMORHEART)
			,15,0,0
			)
	/**
	 * inspired by a lot of ttrpgs I used to talk about with some friends
	 */
	,HEDGE_MAGE("Hedge Mage"
			,"A perpetual novice, hedge mages aren't content to restrict themselves to one school."
			,"Grants basic arcane magic based on clarity."
			,AType.ENTRY
			,EnumSet.of(AGroup.MAGIC,AGroup.CRAFT,AGroup.GRANTED_ARCANIST)
			,EnumSet.of(FeatType.MYSTIC,FeatType.ARCANE)
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
			,EnumSet.of(Skill.DODGEREF,Skill.PRESS_ADV,Skill.ARCANIST)//TODO: PRESS_ADV is temp
			,2,2,11
			)
	,FISH_TALL("Torrental Titan"
			,"A raging force of the deep-sea bent on proving their conquest of land is inevitable."
			,"Grants disrespectful attacks that scale on strength."
			,AType.RACIAL
			,EnumSet.of(AGroup.DIRECT_BATTLE)
			,EnumSet.of(FeatType.BATTLE)
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
	,PROMOTED("Promoted","They made it big.",null
			,AType.ADDED
			,EnumSet.of(AGroup.DIRECT_BATTLE)
			,EnumSet.of(FeatType.BATTLE,FeatType.SPIRIT)
			,EnumSet.of(Skill.TA_NAILS,Skill.STERN_STUFF, Skill.PLOT_ARMOR,Skill.NO_HOSTILE_CURSE)
			)//should probably make a non-archetype owner version, idk what to call it, but the 'no hostile curse' is kinda important
	,MIMIC("Mimic"
			,"TODO"
			,null
			,AType.RACIAL
			,EnumSet.of(AGroup.CRAFT)
			,EnumSet.of(FeatType.TRICKS)
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
			,AType.AFTER
			,EnumSet.of(AGroup.GRANTED_ARCANIST)//do not change or add to unless adding skill requirements to archetypes
			,EnumSet.of(FeatType.ARCANE)
			,EnumSet.of(Skill.ARCANIST_2,Skill.ELEMENTALIST),0,0,25//lot of clarity
			)
	,HIRED_HATCHET("Hired Hatchet",
			"Trading lives is a glamorous business for some, but Hired Hatchets just need to get the job done."
			,null
			,AType.ENTRY
			,EnumSet.of(AGroup.DIRTY,AGroup.DEXTERITY)
			,EnumSet.of(FeatType.TRICKS,FeatType.AGILITY)
			,EnumSet.of(Skill.TWINNED_TAKEDOWN,Skill.DSTRIKE,Skill.DEADLY_AIM)//temp?
			,2,11,2
			)
	,FIGHTING_FURY("Fighting Fury"
			,"Coherent rage and savagery set Furies apart from both civilized folk and enchanted monsters."
			,null,
			AType.ENTRY
			,EnumSet.of(AGroup.DIRECT_BATTLE,AGroup.STRENGTH)
			,EnumSet.of(FeatType.BATTLE,FeatType.SPIRIT)
			,EnumSet.of(Skill.BLOODTHIRSTY,Skill.BLITZ,Skill.COUNTER)
			,12,3,0
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
		ADDED//similar to perk, but mostly for npcs
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
	static {
		for (Archetype a: Archetype.values()) {
			if (a.type == AType.ENTRY) {
				ENTRY_LIST.add(a);
				AFTER_LIST.add(a);
			}else {
				if (a.type == AType.AFTER) {
					AFTER_LIST.add(a);
				}
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
		return extra.ITEM_VALUE+ name +extra.PRE_WHITE+ ": "+desc;
	}
	
	@Override
	public String getBriefText() {
		return extra.ITEM_VALUE+ name +extra.PRE_WHITE + ": "+desc;
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
	public static List<Archetype> getFirst(int desiredAmount, Set<Archetype> has) {
		List<Archetype> list = new ArrayList<Archetype>();
		List<Archetype> options = new ArrayList<Archetype>();
		for (Archetype a: ENTRY_LIST) {
			if (!has.contains(a)) {
				options.add(a);
			}
		}
		
		for (int i = 0; i < 30 && list.size() < desiredAmount;i++) {
			if (options.size() == 0) {
				break;
			}
			Archetype choice = extra.randList(options);
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
	public static List<Archetype> getAfter(int desiredAmount, Archetype has, Set<Archetype> extendedHas) {
		List<Archetype> list = new ArrayList<Archetype>();
		List<Archetype> options = new ArrayList<Archetype>();
		for (Archetype a: AFTER_LIST) {
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
			Archetype choice = extra.randList(options);
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
		case AFTER:
		case ENTRY:
			return friendlyName();
		case ADDED:
			return friendlyName() + " (Added Only)";
		case RACIAL:
			return friendlyName() + " (Species Only)";
		}
	}

	@Override
	public boolean goMenuItem() {
		extra.println("n/a");
		return false;
	}

	public static List<IHasSkills> getFeatChoices(Person person) {
		List<IHasSkills> list = new ArrayList<IHasSkills>();
		Set<Archetype> pAs = person.getArchSet();
		int nonRacialAs = (int) pAs.stream().filter(a -> a.type != AType.RACIAL).count();
		if (nonRacialAs == 0) {
			list.addAll(getFirst(6,pAs));//current game never actually has this matter because the player gets it randomly chosen, or they get to pick it from the entire list
			return list;
		}
		
		//now tries to get you to have 2 + floor(level/5) archetypes
		if (nonRacialAs < 2+Math.floor(person.getLevel()/5)) {
			Set<Archetype> restrictSet = EnumSet.copyOf(pAs);
			List<Archetype> newList = getFirst(2,restrictSet);//first moved up so that it doesn't get blocked by friends of friends basically
			list.addAll(newList);
			restrictSet.addAll(newList);
			list.addAll(getAfter(2,pAs.iterator().next(),restrictSet));
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
			list.addAll(Feat.randFeat(8-list.size(),allowSet,fset,person.fetchSkills())); 
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
		extra.println("Please choose a starting Archetype.");
		List<Archetype> alist = new ArrayList<Archetype>();
		alist.addAll(ENTRY_LIST);
		//int start_points = person.getFeatPoints();
		extra.menuGo(new ScrollMenuGenerator(alist.size(),"previous <> Archetypes","next <> Archetypes") {
			
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
		Archetype starting = person.getArchSet().stream().findAny().get();
		extra.println("Please choose a second Archetype to go with "+starting.friendlyName()+".");
		List<Archetype> alist = new ArrayList<Archetype>();
		
		AFTER_LIST.stream().filter(a -> a.doesAfterWith(starting)).forEach(alist::add);
		ENTRY_LIST.stream().filter(a -> !alist.contains(a)).forEach(alist::add);
		alist.remove(starting);
		//int start_points = person.getFeatPoints();
		extra.menuGo(new ScrollMenuGenerator(alist.size(),"previous <> Archetypes","next <> Archetypes") {
			
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

	public AType getType() {
		return type;
	}
}
