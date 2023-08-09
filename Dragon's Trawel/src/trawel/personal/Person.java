package trawel.personal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.MenuSelectTitled;
import derg.menus.ScrollMenuGenerator;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.BarkManager;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.ImpairedAttack;
import trawel.battle.attacks.Stance;
import trawel.battle.attacks.TargetFactory;
import trawel.battle.attacks.TargetFactory.BloodType;
import trawel.battle.attacks.TargetFactory.TargetType;
import trawel.battle.attacks.TargetFactory.TypeBody;
import trawel.battle.attacks.TargetFactory.TypeBody.TargetReturn;
import trawel.battle.attacks.TargetHolder;
import trawel.earts.EAType;
import trawel.earts.EArt;
import trawel.earts.EArtSkillMenu;
import trawel.earts.PlayerSkillpointsLine;
import trawel.factions.FBox;
import trawel.factions.HostileTask;
import trawel.personal.Person.PersonFlag;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.Feat;
import trawel.personal.classless.HasSkillsClassless;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.classless.Skill.Type;
import trawel.personal.classless.SkillAttackConf;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race;
import trawel.personal.item.body.Race.RaceType;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;
import trawel.personal.item.solid.variants.ArmorStyle;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.towns.services.Store;

/**
 * 
 * @author dragon
 * 2/5/2018
 * A collection of stats, attributes, and an inventory.
 */
public class Person implements java.io.Serializable{

	private static final long serialVersionUID = 2L;

	//inst vars
	private Inventory bag;
	
	private transient ImpairedAttack attackNext;
	private int xp;
	private short level;
	private transient double speedFill;
	private transient boolean isWarmingUp;
	private transient int hp, tempMaxHp;
	
	
	private PersonType personType;
	private short beer;
	
	
	private byte flags = 0b00000000;//used with bitmasking
	
	public enum PersonFlag{
		RACIST, ANGRY,
		AUTOLOOT, AUTOLEVEL,
		ISPLAYER, SMART_COMPARE,
		PLAYER_SIDE//fighting alongside the player
		,IS_ADD//used to indicate less importance- in current cases, boss 'adds'
		//thats 8, can't hold any more rn, might need to make another set
	}
	//DOLATER: add a Set<Culture> that holds cultures. This can be used for advanced bigorty, cultural norms, etc etc
	//this might also be used to hold a place of origin
	//cultures won't have to be all of one type, so there might be a weaker 'world' culture
	//a 'town' culture, an 'island' culture, and then actually heavy cultures that aren't 'cultural groups'
	
	private String firstName,title;

	private short featPoints;
	
	private EnumMap<Effect,Integer> effects;//hash set not permitted
	private RaceFlag rFlag;

	private TargetFactory.TargetType targetOverride = null;//DOLATER swap over
	
	//need to make either this or raceflag not a thing
	//if removing this, raceflag needs to override
	//if removing raceflag, idk
	private TypeBody bodyType;
	//bodystatus should be entirely internal, use own hp values and stuff to display externally
	private transient TargetHolder bodystatus;
	
	//public Weapon backupWeapon = null;
	
	private String scar = "";
	
	private float pitch = 0;
	
	private int pKills = 0, deaths = 0;
	
	private AIJob job;
	
	public FBox facRep = new FBox();
	
	public HostileTask hTask;
	
	private SuperPerson superperson;
	
	public enum RaceFlag {
		NONE, CRACKS, UNDEAD;
	}
	
	
	public enum PersonType{
		COWARDLY,FEARLESS,GRIZZLED,DEATHCHEATED,LIFEKEEPER
	}public final static Set<PersonType> RAND_PERSON_TYPES = EnumSet.of(
			PersonType.COWARDLY,PersonType.FEARLESS
			);
	
	 //= EnumSet.noneOf(Skill.class);//new EnumSet<trawel.personal.classless.Skill>();
	private Set<Feat> featSet;//new EnumSet<trawel.personal.classless.Skill>();
	private Set<Perk> perkSet;
	private Set<Archetype> archSet;
	
	//rebuilt from the above 3, lazyloaded
	private transient AttributeBox atrBox;
	private transient Set<Skill> skillSet;
	
	//private boolean isPlayer;
	
	//Constructor
	protected Person(int level, boolean isAI, Race.RaceType raceType, Material matType,RaceFlag raceFlag,boolean giveScar,AIJob job,Race race) {
		featSet = EnumSet.noneOf(Feat.class);
		perkSet = EnumSet.noneOf(Perk.class);
		archSet = EnumSet.noneOf(Archetype.class);
		/*featSet = EnumSet.of(Feat.EMPTY);
		perkSet = EnumSet.of(Perk.EMPTY);
		archSet = EnumSet.of(Archetype.EMPTY);*/
		//FST has a problem saving enum sets if the enum has no element
		
		setFlag(PersonFlag.AUTOLOOT, true);
		setFlag(PersonFlag.SMART_COMPARE, true);
		
		xp = 0;
		
		this.job = job;
		rFlag = raceFlag;
		if (level < 1) {
			extra.println("non-fatal (until you run into the level zero person) exception: level is zero on someone");
		}
		
		
		bag = new Inventory(level,raceType,matType,job,race);
		bag.owner = this;
		if (raceType == RaceType.HUMANOID) {
			personType = extra.randCollection(RAND_PERSON_TYPES);
			firstName = randomLists.randomFirstName();
			title = randomLists.randomLastName();
			switch (raceFlag) {
			case CRACKS:
				bodyType = TypeBody.STATUE;
				break;
			case UNDEAD:
				bodyType = TypeBody.UNDEAD;
				break;
			default:
				bodyType = TypeBody.HUMAN_LIKE;
				break;
			
			}
		}else {
			switch (race.targetType) {
			case C_REAVER: case S_REAVER:
				bodyType = TypeBody.REAVER;
				break;
			case FLY:
				bodyType = TypeBody.BASIC_FLY;
				break;
			case MIMIC: case OPEN_MIMIC:
				bodyType = TypeBody.MIMIC;
				break;
			case QUAD:
				bodyType = TypeBody.BASIC_QUAD;
				break;
			case HUMANOID:
				bodyType = TypeBody.HUMAN_LIKE;
				break;
			default:
				throw new RuntimeException("invalid target type and flag");
			}
			title = "";
		}
		//placeOfBirth = extra.capFirst((String)extra.choose(randomLists.randomElement(),randomLists.randomColor()))+ " " +extra.choose("Kingdom","Kingdom","Colony","Domain","Realm");
		
		//brag = new Taunts(bag.getRace());
		
		if (giveScar) {
			this.scar = RaceFactory.scarFor(bag.getRace().raceID());
		}
		this.level = (short)level;
		featPoints = (short) (level-1);
		if (extra.chanceIn(1,5)) {
			this.setRacism(true);
			if (extra.chanceIn(4,5)) {
				this.setAngry(true);
			}
		}else {
			if (extra.chanceIn(3,5)) {
				this.setAngry(true);
			}
		}
		//this.magePow = bag.getRace().magicPower;
		//this.defPow = bag.getRace().defPower;
		if (isAI) {
			setFlag(PersonFlag.AUTOLEVEL, true);
			while (featPoints > 0) {
				if (!pickFeatRandom()) {
					break;
				}
				updateSkills();//TODO: makes higher level people slower
			}
		}
		//this.noAILevel = !isAI;
		effects = new EnumMap<Effect,Integer>(Effect.class);
		
		
		//atrBox = new AttributeBox(this);
	}
	
	/**
	 * used for dummy person, empty
	 */
	protected Person() {
		
	}
	
	protected Person(int level) {
		this(level,true,Race.RaceType.HUMANOID,null,Person.RaceFlag.NONE,true);
	}
	
	protected Person(int level, boolean aiLevel, Race.RaceType raceType, Material matType,RaceFlag raceFlag,boolean giveScar) {
		this(level,aiLevel,raceType,matType,raceFlag,giveScar,null,null);
	}
	
	protected Person(int level,AIJob job) {
		this(level,true,Race.RaceType.HUMANOID,null,Person.RaceFlag.NONE,true,job,null);
	}
	
	protected static Person animal(int level,RaceFactory.RaceID race,Material matType,boolean giveScar){
		return new Person(level,true,Race.RaceType.BEAST,matType,RaceFlag.NONE,giveScar,null,RaceFactory.getRace(race));
	}
	

	public enum AIJob{
		KNIGHT(new ArmorStyle[] {ArmorStyle.PLATE,ArmorStyle.PLATE,ArmorStyle.MAIL},
				new String[] {"longsword","mace","axe","lance"}),
		ROGUE(new ArmorStyle[] {ArmorStyle.FABRIC,ArmorStyle.SEWN},new String[] {"rapier","dagger"}), 
		LUMBERJACK(new ArmorStyle[] {ArmorStyle.FABRIC},new String[] {"axe"}), 
		GRAVER(new ArmorStyle[] {ArmorStyle.FABRIC},new String[] {"shovel"});
		
		public ArmorStyle[] amatType;
		public String[] weapType;
		AIJob(ArmorStyle[] amatType, String[] weapType) {
			this.amatType = amatType;
			this.weapType = weapType;
		}
	}
	
	//instance methods
	
	/**
	 * do not use, use updateSkills instead
	 * FIXME: see if can just put updateskills code here... might have some stream issues
	 */
	public Stream<Skill> collectSkills(){
		//return Stream.concat(featSet.parallelStream(), perkSet.parallelStream(),archSet.parallelStream());
		//return Stream.of(featSet.parallelStream(),perkSet.parallelStream(),archSet.parallelStream());
		//return HasSkills.combine(featSet.stream().flatMap(s -> collectSkills()),
		//		perkSet.stream().flatMap(s -> collectSkills()),
		//		archSet.stream().flatMap(s -> collectSkills()));
		
		//ugh need to process them for attributes anyway
		atrBox.reset();
		atrBox.setCapacity(bag.getCapacity());
		Stream<Skill> s = Stream.empty();
		for (Archetype a: archSet) {
			atrBox.process(a);
			s = Stream.concat(s,a.collectSkills());
		}
		for (Perk p: perkSet) {
			atrBox.process(p);
			s = Stream.concat(s,p.collectSkills());
		}
		for (Feat f: featSet) {
			atrBox.process(f);
			s = Stream.concat(s,f.collectSkills());
		}
		
		return s;
	}
	
	/**
	 * this will be called automatically if the Person does not have one of the base things yet.
	 * <br>
	 * Should be called if updating any skill haver directly outside of the wrapper functions setX()
	 */
	public Set<Skill> updateSkills() {
		atrBox = new AttributeBox(this);
		skillSet = EnumSet.noneOf(Skill.class);
		collectSkills().forEach(skillSet::add);
		return skillSet;
	}
	public Set<Skill> fetchSkills() {
		if (skillSet == null) {
			updateSkills();
		}
		return skillSet;
	}
	
	public AttributeBox fetchAttributes() {
		if (atrBox == null) {
			updateSkills();
		}
		return atrBox;
	}
	
	public void setPerk(Perk p) {
		perkSet.add(p);
		updateSkills();//just update instantly now
	}
	public boolean hasPerk(Perk p) {
		return perkSet.contains(p);
	}
	public void setFeat(Feat f) {
		featSet.add(f);
		updateSkills();//just update instantly now
	}
	public void setArch(Archetype a) {//did I actually misspell that
		archSet.add(a);
		updateSkills();//just update instantly now
	}
	
	public Set<Perk> getPerkSet(){
		return perkSet;
	}
	
	public Set<Feat> getFeatSet(){
		return featSet;
	}
	
	public Set<Archetype> getArchSet(){
		return archSet;
	}
	
	public void setSkillHas(IHasSkills has) {
		if (has instanceof Feat) {
			setFeat((Feat) has);
			Feat fBase = (Feat)has;
			switch (fBase) {
			case NOT_PICKY:
				if (isPlayer()) {//only applies to player
					//not picky grants 2 extra picks on take (does not check to see if you already have)
					getSuper().addFeatPick(2);
				}
				break;
			}
		}else {
			if (has instanceof Archetype) {
				setArch((Archetype) has);
			}else {
				if (has instanceof Perk) {
					setPerk((Perk) has);
				}
			}
		}
	}
	
	public boolean hasSkillHas(IHasSkills has) {
		if (has instanceof Feat) {
			return featSet.contains(has);
		}else {
			if (has instanceof Archetype) {
				return archSet.contains(has);
			}else {
				if (has instanceof Perk) {
					return perkSet.contains(has);
				}
			}
		}
		return false;
	}
	
	public RaceFlag getRaceFlag() {
		return rFlag;
	}
	
	public TargetReturn randTarget() {
		return bodystatus.randTarget();
	}
	
	public BloodType getBlood() {
		BloodType temp = bodyType.getBlood();
		if (temp == BloodType.VARIES) {
			switch (getBag().getRaceID()) {
			case B_REAVER_SHORT: case B_REAVER_TALL:
				break;
			case B_MIMIC_CLOSED:
				return BloodType.NONE;
			case B_MIMIC_OPEN:
				return BloodType.NORMAL;
			}
		}
		return temp;
	}
	
	/**
	 * Returns the references to the inventory associated with this person.
	 * @return the bag (Inventory)
	 */
	public Inventory getBag() {
		return bag;
	}
	
	/**
	 * Queue an attack for usage, which will be completed when the speed fills up
	 * @param newAttack (Attack)
	 */
	public void setAttack(ImpairedAttack newAttack){
		attackNext = newAttack;
		speedFill = Math.max(speedFill+attackNext.getWarmup(),10);
		isWarmingUp = true;
	}
	
	/**
	 * if currently dealing with an attack time
	 * @return has an attack already (boolean)
	 */
	public boolean isAttacking() {
		return attackNext != null;
	}
	
	public boolean isOnCooldown() {
		return isWarmingUp == false;
	}
	
	public void finishWarmup() {
		isWarmingUp = false;
	}
	
	public void finishTurn() {
		attackNext = null;
	}
	
	/**
	 * Returns what attack the person wants to use next
	 * @return the next attack (Attack)
	 */
	public ImpairedAttack getNextAttack() {
		return attackNext;
	}
	
	/**
	 * Get the time to the next attack
	 * @return speedFill (double)
	 */
	public double getTime(){
		return speedFill;
	}
	
	/**
	 * Advances the attack timer by t
	 * @param t (double)
	 */
	public void advanceTime(double t){
		speedFill-=t;
	}
	
	public static final int ENDURANCE_HP_BONUS = 6;
	public static final int SKILLPOINT_HP_BONUS = 3;
	
	public int getOOB_HP() {
		int total = getBase_HP();
		if (this.hasSkill(Skill.LIFE_MAGE)) {
			//hp+=this.getMageLevel();
			hp+=this.getClarity();
		}
		total*=bag.getHealth();
		return total;
	}
	
	public int getBase_HP() {
		int total = 20+(50*level);
		//total+=(edrLevel*ENDURANCE_HP_BONUS)*( hasEnduranceTraining ? 2 :1);
		total+=featPoints*SKILLPOINT_HP_BONUS;
		return total;
	}
	
	/**
	 * Clear the person for a new battle.
	 */
	public void battleSetup() {
		
		boolean isPlay = false;
		Effect sipped = null;
		if (this.isPlayer()) {
			sipped = Player.player.doSip();
			isPlay = true;
		}
		if (sipped == Effect.CURSE && hasSkill(Skill.TOXIC_BREWS)) {
			for (int i = 0; i < 3;i++) {
				addEffect(extra.randList(Effect.minorBuffEffects));
			}
		}
		if (hasSkill(Skill.QUICK_START)) {
			addEffect(Effect.ADVANTAGE_STACK);
		}
		
		bodystatus = new TargetHolder(bodyType);
		tempMaxHp = getOOB_HP();
		hp = tempMaxHp;
		if (takeBeer()) {
			if (isPlay) {
				Networking.unlockAchievement("drink_beer");
			}
			hp+=level*5;
		}
		
		if (hasSkill(Skill.BEER_BELLY)) {
			if (takeBeer()) {
				hp+=level*5;
			}
		}
		if (this.hasEffect(Effect.CURSE)) {
			hp-=10*level;
		}
		if (this.hasEffect(Effect.HEARTY) || this.hasEffect(Effect.FORGED)) {
			hp+=3*level;
		}
		if (hasEffect(Effect.SUDDEN_START)) {
			addEffect(Effect.BONUS_WEAP_ATTACK);
			addEffect(Effect.ADVANTAGE_STACK);
		}
		
		if (hasSkill(Skill.OPENING_MOVE)) {
			addEffect(Effect.BONUS_WEAP_ATTACK);
			addEffect(Effect.BONUS_WEAP_ATTACK);
		}
		
		speedFill = -1;
		isWarmingUp = false;
		int s = this.hasSkill(Skill.ARMOR_MAGE) ? this.getClarity()/60: 0;
		int b = this.hasSkill(Skill.ARMOR_MAGE) ? this.getClarity()/60: 0;
		int p = this.hasSkill(Skill.ARMOR_MAGE) ?this.getClarity()/60: 0;
		int defLvl = this.getStrength()/60;
		if (this.hasSkill(Skill.SHIELD)) {
			s+=1*defLvl;
			b+=1*defLvl;
			p+=1*defLvl;
		}else {
			if (this.hasSkill(Skill.PARRY)) {
				s+=2*(defLvl);
				b+=1*(defLvl);
			}
		}
		bag.resetArmor(s,b,p);
		if (hasSkill(Skill.ARMOR_TUNING)) {
			bag.buffArmor(1.2f);
		}
		/*
		if (this.hasEffect(Effect.B_MARY)) {
			this.addEffect(Effect.BLEED);
		}*/
	}
	


	/**
	 * Take damage. Return true if this caused a death.
	 * @param dam (int)
	 * @return if this person is now dead. (boolean)
	 */
	public boolean takeDamage(int dam) {
		hp-=dam;
		return (hp <= 0);
	}
	/**
	 * Get the current hp of the person.
	 * Note that this is not the base hp, it is specific to the current battle instead.
	 * @return hp (int)
	 */
	public int getHp() {
		return hp;
	}
	
	/**
	 * Adds xp. Returns true if this causes a level up.
	 * @param x (int)
	 * @return has leveled up (boolean)
	 */
	public boolean addXp(int x) {
		xp += x;
		int pluslevel = level;
		while (xp >= pluslevel*pluslevel) {
			xp-=pluslevel*pluslevel;
			pluslevel++;
		}
		if (pluslevel > level) {
			extra.println(this.getName() + " leveled up " + (pluslevel-level) + " times and is now level " + (pluslevel) + ".");
			computeLevels(pluslevel-level);
		}
		extra.println(this.getName() + " has " + xp + "/" + level*level + " xp toward level " + (level+1) + ". +" + x + "xp.");
		return pluslevel > level;
	}
	
	/**
	 * call after leveling up
	 * @param levels
	 * @return
	 */
	public void computeLevels(int levels) {
			//maxHp+=50*levels;
			if (this.getBag().getRace().racialType == Race.RaceType.HUMANOID) {
				BarkManager.getBoast(this, false);
			}
			addFeatPoint(levels);
			if (getFlag(PersonFlag.AUTOLEVEL)) {
				while (featPoints > 0) {
					if (!pickFeatRandom()) {pickFeatRandom();//autoleveling doesn't consume picks
						break;
					}
				}
			}
			if (this.isPlayer()) {
				Networking.send("PlayDelay|sound_magelevel|1|");
				Networking.leaderboard("Highest Level",level);
				
				mainGame.story.levelUp(level+levels);
				if (level < 5 && level+levels >= 5) {
					Networking.unlockAchievement("level5");
				}
				if (level < 10 && level+levels >= 10) {
					Networking.unlockAchievement("level10");
				}
				Player.player.addFeatPick(levels);//ai cannot delay leveling up, player can
				playerSkillMenu();
			}
			level+=levels;
	}
	
	public <E extends IHasSkills> ScrollMenuGenerator skillDispMenu(Set<E> set, String typeName,String typeNamePlural) {
		List<IHasSkills> skillList = new ArrayList<IHasSkills>();
		skillList.addAll(set);
		return new ScrollMenuGenerator(skillList.size(), "previous <> "+typeNamePlural, "next <> "+typeNamePlural) {

			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.addAll(IHasSkills.dispMenuItem(skillList.get(i)));
				return list;
			}

			@Override
			public List<MenuItem> header() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return getName() + "'s "+typeNamePlural;
					}});
				return list;
			}

			@Override
			public List<MenuItem> footer() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuBack("back"));
				return list;
			}
			
		};
	}
	
	public void playerSkillMenu() {
		updateSkills();
		if (Player.getTutorial()) {
			extra.println("This is the skill overview menu.");
		}
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "Classless Menu for " + getName();
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Configure skill attacks";
					}

					@Override
					public boolean go() {
						return getSuper().configAttacks();
					}});
				if (getSuper().getFeatPicks() > 0) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "You have " + getSuper().getFeatPicks() + " picks to unlock " + getFeatPoints() + " feats.";
						}

						@Override
						public boolean go() {
							pickFeats(true);
							return false;
						}});
				}else {
					if (getFeatPoints() > 0) {
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "You have " + getFeatPoints() + " feat points waiting for picking, but no picks left.";
							}});
					}else {
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "No feat points or picks.";
							}});
					}
				}
				if (Player.getTutorial()) {
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "You may choose to not unlock a feat, which will not consume the unlock. Every time you level up, you get another chance to unlock all the feats you haven't unlocked yet, but if you choose to delay, you stop picking any remaining unlocks. They will still be there the next picking opportunity.";
						}});
				}
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "View All Skills";
					}

					@Override
					public boolean go() {
						extra.println("not in yet");
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "View Archetypes";
					}

					@Override
					public boolean go() {
						if (archSet.size() == 0) {
							extra.println("You have no archetypes.");
							return false;
						}
						extra.menuGo(skillDispMenu(archSet,"archetype","archetypes"));
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "View Feats";
					}

					@Override
					public boolean go() {
						if (featSet.size() == 0) {
							extra.println("You have no feats.");
							return false;
						}
						extra.menuGo(skillDispMenu(featSet,"feat","feats"));
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "View Perks";
					}

					@Override
					public boolean go() {
						if (perkSet.size() == 0) {
							extra.println("You have no perks.");
							return false;
						}
						extra.menuGo(skillDispMenu(perkSet,"perk","perks"));
						return false;
					}});
				
				list.add(new MenuBack("back"));
				return list;
			}});
		//extra.println("Classless backend is implemented, but both choosing new feats/archetypes and the actual feats/archetypes/perks did not make it into this beta release. Instead get 'The Tough'.");
		//setFeat(Feat.TOUGH_COMMON);
	}
	/**
	 * will return false if can't get a feat
	 */
	public boolean pickFeatRandom() {//does not consume pick
		List<IHasSkills> hases = Archetype.getFeatChoices(this);
		if (hases.isEmpty()) {
			return false;
		}
		useFeatPoint();
		setSkillHas(extra.randList(hases));
		return true;
	}
	
	public void pickFeats(boolean consumePick) {
		if (consumePick) {
			getSuper().addFeatPick(-1);
		}
		Person p = this;
		List<IHasSkills> iList = Archetype.getFeatChoices(p);
		if (isPlayer()) {
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					if (featPoints == 0) {
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "You have no more feats to unlock.";
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "exit menu";
							}

							@Override
							public boolean go() {
								return true;
							}});
						return list;
					}
					if (Player.getTutorial()) {
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "Pick a feat/archetype to gain. If you skip, you will be able to choose later, from an updated list. You may delay any number of times, and each level up grants one chance to pick your feats, which itself can also be delayed.";
							}});
					}
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "You have " + featPoints + " remaining feats to choose.";
						}});
					
					for (IHasSkills ihas: iList) {
						list.add(new FeatArchMenuPick(ihas,p));
					}
					list.add(new MenuBack("delay remaining " + featPoints));
					return list;
				}});
		}
	}
	
	public class FeatArchMenuPick extends MenuSelect {

		private final IHasSkills base;
		private boolean picked;
		private final Person pickFor;
		public FeatArchMenuPick(IHasSkills _base, Person p) {
			base = _base;
			pickFor = p;
		}
		
		@Override
		public String title() {
			return base.getOwnText();
		}

		@Override
		public boolean go() {
			picked = false;
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.addAll(IHasSkills.viewMenuItems(base));
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "select " + base.friendlyName();
						}

						@Override
						public boolean go() {
							picked = true;
							pickFor.useFeatPoint();
							pickFor.setSkillHas(base);
							
							//updateSkills();
							return true;
						}});
					list.add(new MenuBack("back (do not pick)"));
					return list;
				}});
			return picked;
		}
		
	}

	/*
	private void eaSubMenu(EAType eat) {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list2 = new ArrayList<MenuItem>();
				if (Player.player.eArts.size() >= 2) {
					list2.add(new MenuBack());
					return list2;
				}
				for (EArt ear: EArt.values()) {
					if (!Player.player.eArts.contains(ear) && ear.type.equals(eat)) {
						list2.add(new MenuSelectTitled(ear.name) {

							@Override
							public boolean go() {
								extra.menuGo(new MenuGenerator() {

									@Override
									public List<MenuItem> gen() {
										List<MenuItem> list3 = new ArrayList<MenuItem>();
										list3.add(new MenuLine(){

											@Override
											public String title() {
												EArt earta = EArt.valueOf(nameT.toUpperCase());
												return earta.name + ": " + earta.desc;
											}});
										list3.add(new MenuSelect() {

											@Override
											public String title() {
												return "accept";
											}

											@Override
											public boolean go() {
												EArt earta = EArt.valueOf(nameT.toUpperCase());
												Player.player.addEArt(earta);
												return true;
											}
											
										});
										
										list3.add(new MenuBack());
										
										return list3;
									}});
								return false;
							}});
					}
				}
				list2.add(new MenuBack());
				return list2;
			}});
	}
	private void csSubMenu() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Armor Skills";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list2 = new ArrayList<MenuItem>();
								list2.add(new PlayerSkillpointsLine());
								list2.add(new MenuSelect() {

									@Override
									public String title() {
										return "Light Armor: " + Player.player.getPerson().lightArmorLevel;
									}

									@Override
									public boolean go() {
										extra.println("Light Armor skill will help you with armor pieces that already grant a benefit to dodge. It costs 1 skillpoint per level. Buy?");
										if (extra.yesNo()) {
										if (Player.player.getPerson().getSkillPoints() > 0) {
											Player.player.getPerson().useSkillPoint();
											Player.player.getPerson().lightArmorLevel++;
										}}
										return false;
									}});
								list2.add(new MenuSelect() {

									@Override
									public String title() {
										return "Heavy Armor: " + Player.player.getPerson().heavyArmorLevel;
									}

									@Override
									public boolean go() {
										extra.println("Heavy Armor skill will help negate the dodge penalty of armors. It costs 1 skillpoint per level. Buy?");
										if (extra.yesNo()) {
										if (Player.player.getPerson().getSkillPoints() > 0) {
											Player.player.getPerson().useSkillPoint();
											Player.player.getPerson().heavyArmorLevel++;
										}}
										return false;
									}});
								list2.add(new MenuBack());
								return list2;
							}});
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Defense Skills";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list2 = new ArrayList<MenuItem>();
								list2.add(new PlayerSkillpointsLine());
								list2.add(new MenuSelect() {

									@Override
									public String title() {
										return "Endurance: " + Player.player.getPerson().edrLevel;
									}

									@Override
									public boolean go() {
										extra.println("Endurance grants you "+ENDURANCE_HP_BONUS+" base hp per stack. It costs 1 skillpoint per level. Buy?");
										if (extra.yesNo()) {
										if (Player.player.getPerson().getSkillPoints() > 0) {
											Player.player.getPerson().useSkillPoint();
											Player.player.getPerson().edrLevel++;
										}}
										return false;
									}});
								list2.add(new MenuBack());
								return list2;
							}});
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Utility Skills";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list2 = new ArrayList<MenuItem>();
								list2.add(new PlayerSkillpointsLine());
								if (!Player.player.getPerson().hasSkill(Skill.EXPANDER)) {
								list2.add(new MenuSelect() {

									@Override
									public String title() {
										return "Shopping lvl 1";
									}

									@Override
									public boolean go() {
										extra.println("Adds +1 item in each store maximum. Buy?");
										if (extra.yesNo()) {
										if (Player.player.getPerson().getSkillPoints() > 0) {
											Player.player.getPerson().useSkillPoint();
											Player.player.getPerson().addSkill(Skill.EXPANDER);
										}}
										return false;
									}});
								}
								list2.add(new MenuBack());
								return list2;
							}});
						return false;
					}});
				list.add(new MenuBack());
				return list;
			}});
	}
		
	*/

	/**
	 * Returns the stance that this person is currently using.
	 * @return Stance (Stance)
	 */
	public Stance getStance() {
		return bag.getStance();
	}
	
	/**
	 * Get the level of the person
	 * @return level (int)
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Get the full name of this person
	 * @return String - full name
	 */
	public String getName() {
		if (title == "" || title == null) {
			return firstName;
		}
		if (title.startsWith(",")) {
			return firstName+title;
		}
		return firstName + " " + title;
	}
	
	public void setFirstName(String str) {
		firstName = str;
	}
	
	/**
	 * Display this person's stats.
	 */
	public void displayStats() {
		displayStats(true);
	}
	
	public void displayStats(boolean inCombat) {
		extra.println("This is " + this.getName() +". They are a level " + this.getLevel() +" " + this.getBag().getRace().renderName(false)+".");
		if (inCombat) {
			extra.println("They have " + this.getHp() +"/"+ tempMaxHp + " hp. Their health modifier is " + extra.format(bag.getHealth()) + "x.");
		}else {
			extra.println("They have " + getBase_HP() + " base hp. Their health modifier is " + extra.format(bag.getHealth()) + "x. Their expected hp is "+getOOB_HP() +".");
		}
		extra.println("They have " + extra.format(bag.getAim()) + "x aiming, " +  extra.format(bag.getDam()) + "x damage, and "+extra.format(bag.getSpeed()) + "x speed.");
		extra.println("They have " + extra.format(bag.getDodge()) + "x dodging, " + extra.format(bag.getBluntResist()) + " blunt resistance, " +
		extra.format(bag.getSharpResist()) + " sharp resistance, and "+ extra.format(bag.getPierceResist()) + " pierce resistance.");
		extra.println("They have " + xp + "/" + level*level + " xp toward level " + (level+1) + ".");
		
		if (this.getBag().getRace().racialType == Race.RaceType.HUMANOID) {
		extra.println("Their inventory includes " + bag.nameInventory());
		if (beer > 0 || hasSkill(Skill.BEER_LOVER)) {extra.println("They look drunk.");}
		if (hasSkill(Skill.PARRY)) {extra.println("They have a parrying dagger.");}
		if (hasSkill(Skill.SHIELD)) {extra.println("They have a shield.");}}
	}
	
	/**
	 * Returns the reference to the Taunts instance this person uses.
	 * @return (Taunts)
	 */
	//public Taunts getTaunts() {
		//return brag;
	//}
	
	public PersonType getPersonType() {
		return personType;
	}
	
	public void setPersonType(PersonType pt) {
		personType = pt;
	}
	
	/**
	 * Get's the string of where this person is 'from'
	 * @return (String) - place of birth
	 */
	/*
	public String whereFrom() {
		return placeOfBirth;
	}*/

	public boolean isPlayer() {
		return getFlag(PersonFlag.ISPLAYER);
	}

	public void setPlayer() {
			//intellect = -2;
			setFlag(PersonFlag.ISPLAYER,true);
	}
	
	public void autoLootPlayer() {
		setFlag(PersonFlag.AUTOLOOT,true);
	}

	public void displayStatsShort() {
		extra.println("This is " + this.getName() +". They are a level " + this.getLevel() +" " + this.getBag().getRace().renderName(false)+".");
		if (this.getBag().getRace().racialType == Race.RaceType.HUMANOID) {
		extra.println("Their inventory includes: \n " + bag.nameInventory()); 
		if (beer > 0 || hasSkill(Skill.BEER_LOVER)) {extra.println("They look drunk.");}
		if (hasSkill(Skill.PARRY)) {extra.println("They have a parrying dagger.");}
		if (hasSkill(Skill.SHIELD)) {extra.println("They have a shield.");}}
	}
	
	public void addBeer() {
		beer++;
	}
	public void addBeer(int i) {
		beer+=i;
	}
	
	public boolean takeBeer() {
		if (beer > 0 || hasSkill(Skill.BEER_LOVER)) {
		beer--;
		return true;
		}else {
			return false;
		}
	}

	public void displayXp() {
		extra.println(this.getName() + " has " + xp + "/" + level*level + " xp toward level " + (level+1) + ".");
	}

	public int getMaxHp() {
		return tempMaxHp;
	}

	public void displayHp() {
		extra.println("Hp: " + hp + "/" + tempMaxHp);
		
	}

	public int getFeatPoints() {
		return featPoints;
	}
	
	public void useFeatPoint() {
		featPoints--;
	}
	public void addFeatPoint() {
		featPoints++;
	}
	public void addFeatPoint(int number) {
		featPoints+=number;
	}
	
	public boolean hasSkill(Skill o) {
		return fetchSkills().contains(o);
	}

	public boolean isAlive() {
		return this.getHp() > 0;
	}

	public void addHp(int i) {
		hp+=i;
	}
	
	//yayyyy standardizedish flags
	//will work on ints and longs as well if need more flags
	//got the idea from the concept of enumset
	//this just avoids the object overhead plus byte is 1/8th of a long
	
	public boolean getFlag(PersonFlag flag) {
		return Byte.toUnsignedInt((byte) (flags & (1 << flag.ordinal()))) > 0;
	}
	
	public void setFlag(PersonFlag flag, boolean bool) {
		if (bool) {
			flags |= (1 << flag.ordinal());
			return;
		}
		flags &= ~(1 << flag.ordinal());
	}

	public boolean isRacist() {
		//unsure if best way, but I care more about memory storage than speed, so doesn't need to be fast rn
		//return Byte.toUnsignedInt((byte) (flags & (1 << 0))) > 0;
		return getFlag(PersonFlag.RACIST);
	}
	
	public void setRacism(boolean bool) {
		//at 0 index
		/*
		if (bool) {
			flags |= (1 << 0);
			return;
		}
		flags &= ~(1 << 0);*/
		setFlag(PersonFlag.RACIST,bool);
	}
	
	public boolean isAngry() {
		return getFlag(PersonFlag.ANGRY);
		//unsure if best way, but I care more about memory storage than speed, so doesn't need to be fast rn
		//return Byte.toUnsignedInt((byte) (flags & (1 << 1))) > 0;
	}
	
	public void setAngry(boolean bool) {
		setFlag(PersonFlag.ANGRY,bool);
		//at 1 index
		/*
		if (bool) {
			flags |= (1 << 1);
			return;
		}
		flags &= ~(1 << 1);*/
	}

	public void displayArmor() {
		int sharp=0, blunt=0, pierce=0, sharpm =0, bluntm = 0, piercem = 0;
		for (Armor a: bag.getArmor()) {
			sharp += a.getSharp();
			blunt += a.getBlunt();
			pierce += a.getPierce();
			
			sharpm += a.getSharpResist();
			bluntm += a.getBluntResist();
			piercem += a.getPierceResist();
		}
		extra.println("Sharp: " + sharp + "/" +sharpm);
		extra.println("Blunt: " + blunt + "/" +bluntm);
		extra.println("Pierce: " + pierce + "/" +piercem);
	}

	/*
	public int getMageLevel() {
		int base = this.isPlayer() ? Player.player.eaBox.getStatMAG() : mageLevel;
		return extra.zeroOut(base+bag.getRace().magicPower+magePow-burnouts());
	}

	public int getDefenderLevel() {
		int base = this.isPlayer() ? Player.player.eaBox.getStatDEF() : defenderLevel;
		return extra.zeroOut(base+bag.getRace().defPower+defPow-burnouts());
	}

	public int getFighterLevel() {
		int base = this.isPlayer() ? Player.player.eaBox.getStatATK() : fighterLevel;
		return extra.zeroOut(base+fightPow-burnouts());
	}*/
	
	public void removeEffectAll(Effect e) {
		effects.put(e, 0);
	}
	
	public void cureEffects() {
		effects.clear();//will need to be more complex if there ever are positive longterm effects
	}
	
	public int effectsSize() {
		return effects.size(); 
	}
	
	public void displayEffects() {
		
		boolean found = false;
		for (Effect e: effects.keySet()) {//listing is slower now
			int num = effects.getOrDefault(e, 0);
			if (num > 0) {
				found = true;
				if (num > 1) {
					extra.println(e.name() + " x"+num+": "+ e.getDesc());
				}else {
					extra.println(e.name() + ": "+ e.getDesc());
				}
			}
			
		}
		if (!found) {
			extra.println("They're perfectly healthy.");
		}
	}
	
	
	public void addEffect(Effect e) {
		if (e.stacks()) {
			effects.put(e,1+effects.getOrDefault(e, 0));
		}else {
			effects.put(e,1);
		}
	}
	
	public boolean hasEffect(Effect e) {
		return effects.getOrDefault(e, 0) > 0;
	}
	
	/** REMOVED because hasEffect with a map is fast and iteration is slow now
	 * should only be used to iterate over the effects, instead of calling
	 * hasEffect repeatedly.
	 *
	public List<Effect> getEffects() {
		return effects;
	}*/
	
	public void clearBattleEffects() {
		//only goes over keyset because those are the ones it bothered to add
		for (Effect e: effects.keySet()) {
			if (!e.lasts()) {
				effects.put(e,0);
			}
		}
	}
	
	private int burnouts() {
		return effects.getOrDefault(Effect.BURNOUT, 0);
	}

	public void displaySkills() {
		for (Skill s: fetchSkills()) {
			if (s.getType() == Type.INTERNAL_USE_ONLY) {
				continue;
			}
			s.display();
		}
		
	}
	
	/**
	 * due to code changes there is a max of one skill count anyway
	 * <br>
	 * in the future will need to use perks or feats mostly
	 * @param skill
	 * @return false if already has and no change
	 */
	@Deprecated
	public boolean setHasSkill(Skill skill) {
		if (hasSkill(skill)) {
			return false;
		}
		skillSet.add(skill);
		return true;
	}

	public void setTitle(String s) {
		title = s;
	}
	
	/**
	 * how many special attacks they have and are currently in use
	 * <br>
	 * 3 is the most that can be obtained without starting to eat into normal attack potential
	 * <br>
	 * but 5 is fine if they don't have any sources of bonus weapon attacks
	 */
	public int specialAttackNum() {
		SuperPerson sp = getSuper();
		if (sp == null) {
			return 0;
		}
		SkillAttackConf[] list = sp.getSpecialAttacks();
		return list != null ? list.length : 0;
	}

	public int attacksThisAttack() {
		int i = 3;
		int cap = Math.min(5, 8-specialAttackNum());
		if (this.hasEffect(Effect.DISARMED)) {
			this.removeEffectAll(Effect.DISARMED);
			i--;
		}
		for (;i < cap;) {//max 5 attacks if no special attacks
			if (hasEffect(Effect.BONUS_WEAP_ATTACK)) {
				i++;
				removeEffect(Effect.BONUS_WEAP_ATTACK);
				continue;
			}
			break;
		}
		return Math.max(1,Math.min(5,i));
	}

	/**
	 * removes a single stack
	 */
	public void removeEffect(Effect e) {
		effects.remove(e);
		effects.put(e,Math.max(1,effects.getOrDefault(e, 0)-1));
	}
	
	public int effectCount(Effect e) {
		return effects.getOrDefault(e,0);
	}

	public double getTornCalc() {
		//double starter = 1;
		//int count = ;
		return Math.pow(.9,effects.getOrDefault(Effect.TORN,0));
		/*for (int i = 0; i < count;i++) {
			starter*=.9;
		}*/
		//return starter;
	}
	
	public int bloodSeed = extra.randRange(0,2000);
	private float bloodCount = 0;
	
	public void wash() {
		bloodSeed = extra.randRange(0,2000);
		bloodCount = 0;
	}
	
	public void washAll() {
		int bTotal = (int)bloodCount;
		this.wash();
		for (Armor a: bag.getArmor()) {
			bTotal+=a.getBloodCount();
			a.wash();
		}
		bTotal += bag.getHand().getBloodCount();
		bag.getHand().wash();
		if (this.isPlayer()) {
			if (bTotal > 6) {
				extra.println("You were REALLY bloody!");
			}
			this.bag.graphicalDisplay(-1,this);
		}
	}
	
	public int getBloodCount() {
		return (int)bloodCount;
	}
	
	public void addBlood(float i) {
		bloodCount+=i;
		if (bloodCount > 16) {
			bloodCount = 16;
		}
	}

	public String getScar() {
		return scar;
	}

	public void setScar(String b) {
		scar = b;
	}
	
	public float getPitch() {//TODO: examine for what it does besides just set it base, probably missing rng
		if (pitch < this.getBag().getRace().minPitch || pitch > this.getBag().getRace().maxPitch) {
			//pitch = extra.curveLerp(this.getBag().getRace().minPitch, this.getBag().getRace().maxPitch,.7f);
			pitch = this.getBag().getRace().minPitch+(extra.hrandomFloat()*(this.getBag().getRace().maxPitch-this.getBag().getRace().minPitch));
		}//extra.randRange((int)this.getBag().getRace().minPitch*1000,(int)this.getBag().getRace().maxPitch*1000)/1000.0f;}
		return pitch;
	}

	public void addPlayerKill() {
		pKills++;
	}
	public int getPlayerKills() {
		return pKills;
	}

	public int getDeaths() {
		return deaths;
	}
	
	public void addDeath() {
		deaths++;
	}

	public void addKillStuff() {
		//if (this.isPlayer()) {
			//Player.player.eaBox.exeKillLevel += .3;
		//}
		
	}

	public boolean hasBeer() {
		return beer > 0;
	}

	public void consumeBeer() {
		beer--;
	}

	public void forceLevelUp(int endLevel) {
		while (getLevel() < endLevel) {
			addXp(getLevel()*getLevel());
		}
	}

	public double getSpeed() {
		return getBag().getSpeed() + (hasEffect(Effect.HASTE) ? 0.1 : 0 );
	}

	public TypeBody getBodyType() {
		return bodyType;
	}

	public void updateRaceWeapon() {
		switch (bag.getRaceID()) {
		case B_REAVER_SHORT:
			bag.getHand().transmuteWeapMat(MaterialFactory.getMat("bone"));
			bag.getHand().transmuteWeapType(WeaponType.CLAWS_TEETH_GENERIC);
			break;
		case B_REAVER_TALL:
			bag.getHand().transmuteWeapMat(MaterialFactory.getMat("flesh"));
			bag.getHand().transmuteWeapType(WeaponType.REAVER_STANDING);
			
			break;
		}
		
	}

	public void debug_print_status(int damageDone) {
		if (extra.getPrint()) {
			return;
		}
		if (damageDone != 0) {
			if (!mainGame.advancedCombatDisplay) {
				return;
			}
			extra.println(getHp() +" ("+damageDone+")->"+ (getHp()-damageDone) + "/" + getMaxHp());
		}else {
			bodystatus.debug_print(true);//testing, will have to redo both this and submethods if used for real
			extra.println(attributeDesc());
		}
	}
	
	public void multBodyStatus(int spot, double mult) {
		bodystatus.multStatus(spot, mult);
	}
	
	public void addBodyStatus(int spot, double add) {
		bodystatus.addStatus(spot, add);
	}
	
	public int guessBodyHP(int spot) {
		return (int) (bodystatus.getRootStatus(spot)*getMaxHp());
	}

	public boolean isSameTargets(Person other) {
		return bodyType == other.internalBType();
	}
	
	protected TypeBody internalBType() {
		return bodyType;
	}
	
	//TODO: should probably declare this to the player
	public void applyDiscount(double time) {
		speedFill-=time;
	}

	public double getConditionForPart(int mapping) {
		return bodystatus.getStatusOnMapping(mapping);
	}

	public int getStrength() {
		return fetchAttributes().getStrength();
	}

	public int getDexterity() {
		return (int) (getRawDexterity()* bag.getAgiPen());
	}
	
	public int getClarity() {
		return fetchAttributes().getClarity();
	}
	
	public int getHighestAttribute() {
		return Math.max(getStrength(),Math.max(getDexterity(),getClarity()));
	}
	
	public int getRawDexterity() {
		return fetchAttributes().getDexterity();
	}
	
	public float getAttributeAgiPen() {
		//make sure to update capacity
		if (atrBox == null) {
			updateSkills();
		}
		return atrBox.getTotalAgiPen(bag.getAgiPen());
	}
	
	public float getTotalAgiPen() {
		//make sure to update capacity
		return getAttributeAgiPen() * bag.getAgiPen();
	}
	
	public String attributeDesc() {
		return "(raw) dex (" + getRawDexterity() +") " + getDexterity() + " cap/str " + bag.getCapacity() + "/"+getStrength()
		+ " clarity: " + getClarity()
		+ " (total) AMP (" + getTotalAgiPen() + ") " + getAttributeAgiPen();
	}

	public SuperPerson getSuper() {
		return superperson;
	}

	public void setSuper(SuperPerson p) {
		superperson = p;
	}

	public boolean isHumanoid() {
		return bag.getRace().racialType == RaceType.HUMANOID;
	}

	public void forceKill() {
		hp = 0;
	}

	//mostly just a method in case I want to plug 'any contested roll' skills in later
	/**
	 * make a contested roll against another person
	 * <br>
	 * typically, should use >= since engager wins ties
	 */
	public int contestedRoll(Person defender, int mynum, int theirnum) {
		int myroll = extra.randRange(0,mynum);
		int theirroll = extra.randRange(0,theirnum);
		return myroll-theirroll;
	}

	public String getTitle() {
		return title;
	}


}
