package trawel.personal;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLast;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.BarkManager;
import trawel.battle.attacks.Attack.Wound;
import trawel.battle.attacks.ImpairedAttack;
import trawel.battle.attacks.ImpairedAttack.DamageType;
import trawel.battle.attacks.Stance;
import trawel.battle.attacks.Target;
import trawel.battle.attacks.TargetFactory.BloodType;
import trawel.battle.attacks.TargetFactory.TypeBody;
import trawel.battle.attacks.TargetFactory.TypeBody.TargetReturn;
import trawel.battle.attacks.TargetHolder;
import trawel.factions.FBox;
import trawel.factions.Faction;
import trawel.factions.HostileTask;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.Archetype.AType;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.Feat;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.classless.Skill.Type;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race;
import trawel.personal.item.body.Race.RaceType;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Armor.ArmorQuality;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon.WeaponType;
import trawel.personal.item.solid.variants.ArmorStyle;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.CleanseSideQuest;

/**
 * 
 * @author dragon
 * 2/5/2018
 * A collection of stats, attributes, and an inventory.
 */
public class Person implements java.io.Serializable, IEffectiveLevel{

	private static final long serialVersionUID = 2L;

	private SuperPerson superperson;
	protected Inventory bag;
	private int xp;
	private short level;
	private short featPoints;
	
	private short pKills = 0, deaths = 0;
	public FBox facRep = new FBox();
	
	private AIJob job;
	private PersonType personType;
	public HostileTask hTask;
	private String firstName,title;
	private int scar = -1;
	private float pitch = 0;
	/**
	 * used for cleanse quests, you can remove or change if you want to change if a person or creature counts,
	 * ie a bandit changing to not bandit or vice versa, or just set it to -1 to make a creature not count entirely
	 * (for mook reasons usually)
	 */
	public byte cleanseType = -1;

	private EnumMap<Effect,Integer> effects;//hash set not permitted
	
	private TypeBody bodyType;
	//bodystatus should be entirely internal, use own hp values and stuff to display externally
	private transient TargetHolder bodystatus;
	
	private Set<Feat> featSet;
	private Set<Perk> perkSet;
	private Set<Archetype> archSet;
	
	//rebuilt from the above 3, lazyloaded
	private transient AttributeBox atrBox;
	private transient Set<Skill> skillSet;
	
	private transient ImpairedAttack attackNext;
	private transient double speedFill;
	private transient boolean isWarmingUp;
	private transient int hp, tempMaxHp;
	
	private short flags = 0b0;//used with bitmasking, starts empty
	
	public enum PersonFlag{//current capacity= 16 (short)
		/**
		 * says slurs
		 */
		RACIST,
		/**
		 * effectively racist to things that can't talk
		 */
		ANGRY,
		AUTOLOOT, AUTOLEVEL,
		ISPLAYER,
		/*
		 * if off, they assess by value instead of by quality
		 */
		SMART_COMPARE,
		/**
		 * if this is set to true, allows non-personable races to accumulate wealth, not just aether,
		 * through looting
		 */
		HAS_WEALTH
		/*
		 * used to indicate less importance- in current cases, boss 'adds'. This is not a summon, however
		 * just a persistent non-leader
		 */
		,IS_MOOK
		/**
		 * not part of main list, added by the wrapper functions to add traveling friends
		 */
		,IS_SUMMON
		/**
		 *can learn archetypes when autoleveling, otherwise can only get feats they can already get
		 *this essentially prevents wolves from learning how to be good at armor unless they already know armor
		 *via it getting set in their makeWolf thing
		 *<br>
		 *note that 'common' feats will still be accessible
		 */
		,CAN_LEARN
		,AUTOBATTLE
		/**
		 * only the player is allowed to loot them in mass battles
		 * <br>
		 * may or may not be respected in 1v1's
		 */
		,PLAYER_LOOT_ONLY
	}
	
	public enum RaceFlag {
		NONE, CRACKS, UNDEAD;
	}
	
	public enum PersonType{
		NO_SPEAK,
		COWARDLY,FEARLESS,GRIZZLED,DEATHCHEATED,LIFEKEEPER,
		DRUDGER_GENERIC, FELL_MONSTER, HARPY_GENERIC
	}
	public final static Set<PersonType> RAND_PERSON_TYPES = EnumSet.of(
			PersonType.COWARDLY,PersonType.FEARLESS
			);
	
	
	
	
	//Constructors
	protected Person(int level, boolean autolevel, Race.RaceType raceType, Material matType,RaceFlag raceFlag,boolean giveScar,AIJob job,Race race) {
		featSet = EnumSet.noneOf(Feat.class);
		perkSet = EnumSet.noneOf(Perk.class);
		archSet = EnumSet.noneOf(Archetype.class);
		skillSet = EnumSet.noneOf(Skill.class);
		
		setFlag(PersonFlag.AUTOLOOT, false);//only applies to players anyway
		setFlag(PersonFlag.SMART_COMPARE, true);
		
		xp = 0;
		
		this.job = job;
		assert level >= 1;
		
		bag = new Inventory(level,raceType,matType,job,race);
		bag.owner = this;
		if (raceType == RaceType.PERSONABLE) {
			personType = extra.randCollection(RAND_PERSON_TYPES);
			firstName = randomLists.randomFirstName();
			title = randomLists.randomLastName();
			switch (bag.getRace().targetType) {
			case DEMON:
				bodyType = TypeBody.DEMON;
				break;
			default:
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
				break;
			}
		}else {
			personType = PersonType.NO_SPEAK;
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
			case STATUE:
				//directly used for ents
				bodyType = TypeBody.STATUE;
				break;
			default:
				throw new RuntimeException("invalid target type and flag");
			}
			title = "";
		}
		
		this.level = (short)level;
		featPoints = (short) (level);//now just straight up level
		
		
		Race tRace = bag.getRace();
		if (tRace.archetype != null && autolevel) {
			archSet.add(tRace.archetype);
			if (tRace.archetype.getType() != AType.RACIAL) {
				featPoints--;
				//if not racial, counts as their starting archetype.
				//SlowStart now effectively grants a choice of the second archetype
				//if this is set, and only the first if it isn't
				//but the player will level up immediately, and will get to choose their second archetype from that menu
				//when they please to do so
			}
		}
		if (giveScar) {
			this.scar = RaceFactory.scarFor(tRace.raceID());
		}
		
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
		if (autolevel) {
			setFlag(PersonFlag.AUTOLEVEL, true);
		}
		effects = new EnumMap<Effect,Integer>(Effect.class);
		setFlag(PersonFlag.CAN_LEARN,isPersonable());//can be overwritten by caller if need be
	}
	
	/**
	 * used for dummy person, empty
	 */
	protected Person() {
		
	}
	
	protected Person(int level) {
		this(level,true,Race.RaceType.PERSONABLE,null,Person.RaceFlag.NONE,true);
	}
	
	protected Person(int level, boolean autolevel, Race.RaceType raceType, Material matType,RaceFlag raceFlag,boolean giveScar) {
		this(level,autolevel,raceType,matType,raceFlag,giveScar,null,null);
	}
	
	protected Person(int level,AIJob job) {
		this(level,true,Race.RaceType.PERSONABLE,null,Person.RaceFlag.NONE,true,job,null);
	}
	
	protected static Person animal(int level,RaceFactory.RaceID race,Material matType,boolean giveScar){
		return new Person(level,true,Race.RaceType.BEAST,matType,RaceFlag.NONE,giveScar,null,RaceFactory.getRace(race));
	}
	

	public enum AIJob{
		KNIGHT(new ArmorStyle[] {ArmorStyle.PLATE,ArmorStyle.PLATE,ArmorStyle.MAIL},
				new WeaponType[] {
					WeaponType.LONGSWORD,WeaponType.BROADSWORD,WeaponType.MACE,WeaponType.AXE,WeaponType.LANCE
					,WeaponType.CLAYMORE
				}
		),
		ROGUE(new ArmorStyle[] {ArmorStyle.FABRIC,ArmorStyle.SEWN},
				new WeaponType[] {
						WeaponType.RAPIER,WeaponType.DAGGER,WeaponType.SPEAR,WeaponType.MACE,WeaponType.SHOVEL
					}
		), 
		LUMBERJACK(new ArmorStyle[] {ArmorStyle.FABRIC,ArmorStyle.SEWN},new WeaponType[] {WeaponType.AXE}), 
		GRAVER(new ArmorStyle[] {ArmorStyle.FABRIC,ArmorStyle.SEWN},new WeaponType[] {WeaponType.SHOVEL})
		, CULTIST_WORSHIPER(new ArmorStyle[] {ArmorStyle.FABRIC,ArmorStyle.SEWN}
				,new WeaponType[] {
						WeaponType.DAGGER,WeaponType.SHOVEL,WeaponType.SPEAR,WeaponType.MACE//tools of the trade
				})
		,COLLECTOR(new ArmorStyle[] {ArmorStyle.GEM,ArmorStyle.PLATE},
				new WeaponType[] {
						WeaponType.LONGSWORD,WeaponType.BROADSWORD,WeaponType.MACE,WeaponType.AXE,WeaponType.SPEAR
						,WeaponType.SHOVEL,WeaponType.RAPIER
					}
				)
		,DUELER(new ArmorStyle[] {ArmorStyle.PLATE,ArmorStyle.PLATE,ArmorStyle.PLATE,ArmorStyle.SEWN},
				new WeaponType[] {
						WeaponType.LONGSWORD,WeaponType.BROADSWORD,WeaponType.MACE,WeaponType.AXE,WeaponType.SPEAR
					})
		,DUELIST_ONLY(new ArmorStyle[] {ArmorStyle.SEWN,ArmorStyle.FABRIC},
				new WeaponType[] {
						WeaponType.RAPIER
					})
		;
		
		public ArmorStyle[] amatType;
		public WeaponType[] weapType;
		AIJob(ArmorStyle[] amatType, WeaponType[] weapType) {
			this.amatType = amatType;
			this.weapType = weapType;
		}
		public WeaponType randWeap() {
			return extra.randList(weapType);
			//for now no weighted table
		}
	}
	
	//instance methods
	
	/**
	 * do not use directly, use updateSkills instead
	 */
	private Stream<Skill> collectSkills(){

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
	 * <br>
	 * you can call it through 'finishgeneration' if you want to autolevel instead
	 */
	public Set<Skill> updateSkills() {
		atrBox = new AttributeBox(this);
		skillSet = EnumSet.noneOf(Skill.class);
		collectSkills().forEach(skillSet::add);
		if (isPlayer()) {
			Player.player.skillUpdate();
		}
		return skillSet;
	}
	
	/**
	 * as updateskills, except does not compute end result. Useful if you want to autolevel after
	 * <br> in most cases you will want to use 'lite' adding instead of clean adding, making this useless
	 */
	public void liteRefreshClassless() {
		skillSet = EnumSet.noneOf(Skill.class);
		
		for (Archetype a: archSet) {
			skillSet.addAll(a.giveSet());
		}
		for (Perk p: perkSet) {
			skillSet.addAll(p.giveSet());
		}
		for (Feat f: featSet) {
			skillSet.addAll(f.giveSet());
		}
	}
	
	/**
	 * autolevel if that is set, and update skills no matter what (ie, if autolevel didn't happen, still update)
	 */
	public void finishGeneration() {
		if (!autoLevelIf()) {
			updateSkills();
		}
	}
	/**
	 * attempts to decide all the character's remaining feat points, if they are set to autolevel
	 * <br>
	 * if returned false, didn't update list
	 */
	public boolean autoLevelIf() {
		boolean autoLeveled = false;
		if (featPoints > 0 && getFlag(PersonFlag.AUTOLEVEL)) {
			if (featPoints == 1) {//only one
				IHasSkills gain = pickFeatRandom();
				if (isPlayer()) {
					extra.println(gain.getOwnText());
				}
				updateSkills();
				autoLeveled = true;
				if (superperson != null) {
					superperson.fillSkillConfigs();
				}
			}else {
				while (featPoints > 0) {//autoleveling doesn't consume picks
					IHasSkills gain = pickFeatRandom();
					if (gain == null) 
					{
						break;
					}
					if (isPlayer()) {
						extra.println(gain.getOwnText());
					}
					skillSet.addAll(gain.giveSet());//doesn't compute them fully, just enough to understand requirements
				}
				updateSkills();
				autoLeveled = true;
				if (superperson != null) {
					superperson.fillSkillConfigs();
				}
			}
		}
		return autoLeveled;
	}
	/**
	 * wipes the attribute box and skillset to lazyload them later
	 * <Br>
	 * not quite sure why you'd want this since most of the time you need the skillset to determine levelups,
	 * but it could be useful if you set a ton and don't care
	 */
	@Deprecated
	private void setClasslessDirty() {
		skillSet = null;
		atrBox = null;
	}
	
	public Set<Skill> fetchSkills() {
		if (skillSet == null) {
			updateSkills();
		}
		return skillSet;
	}
	
	public List<IHasSkills> fetchSkillSources() {
		List<IHasSkills> list = new ArrayList<IHasSkills>();
		//could have been a set, but an enumset wouldn't work, and this is mostly used for iteration
		//so the better lookup of a hashset is actually negative
		list.addAll(archSet);
		list.addAll(featSet);
		list.addAll(perkSet);
		return list;
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
		switch (f) {
		case NOT_PICKY:
			if (isPlayer()) {//only applies to player
				//not picky grants 2 extra picks on take (does not check to see if you already have)
				getSuper().addFeatPick(2);
			}
			break;
		}
		updateSkills();//just update instantly now
	}
	public void setArch(Archetype a) {//did I actually misspell that
		archSet.add(a);
		switch (a) {
		case PROMOTED:
			this.clearEffects();//cures effects, notably curse, which they just gained immunity to
			break;
		}
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
	
	/**
	 * sets a skillhas as with lite, except it doesn't consume a feat point.
	 * <br>
	 * useful for adding things that aren't class choices, such as perks
	 */
	public void cleanSetSkillHas(IHasSkills has) {
		skillSet.addAll(has.giveSet());
		if (has instanceof Archetype) {
			archSet.add((Archetype)has);
		}else {
			if (has instanceof Perk) {
				perkSet.add((Perk)has);
			}else {
				if (has instanceof Feat) {
					featSet.add((Feat)has);
				}
			}
		}
	}
	
	/**
	 * sets a skillhas, naively adding it's own skills to the skillset and also deducting a feat point
	 * <br>
	 * use for adding skillhases in worldgen, because the character will just not level their feats until they are positive again
	 * <br>
	 * should call finishGeneration after if want to level up any remainders, or updateskills directly if not
	 */
	public void liteSetSkillHas(IHasSkills has) {
		featPoints--;//can go into negatives
		skillSet.addAll(has.giveSet());
		if (has instanceof Archetype) {
			archSet.add((Archetype)has);
		}else {
			if (has instanceof Perk) {
				perkSet.add((Perk)has);
			}else {
				if (has instanceof Feat) {
					featSet.add((Feat)has);
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
		switch (bodyType) {
		case STATUE:
			return RaceFlag.CRACKS;
		case UNDEAD:
			return RaceFlag.UNDEAD;
		}
		return RaceFlag.NONE;
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
		speedFill += attackNext.getCooldown();
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
	public static final int SKILLPOINT_HP_BONUS = 5;
	
	public int getOOB_HP() {
		int total = getBase_HP();
		if (this.hasSkill(Skill.LIFE_MAGE)) {
			total+=this.getClarity()/20;
		}
		total*=bag.getHealth();
		if (this.hasEffect(Effect.CURSE)) {
			if (hasSkill(Skill.TOXIC_BREWS)) {
				total*=.8f;
			}else {
				total/=2;
			}
			
		}
		return total;
	}
	
	public int getBase_HP() {
		int total = getEffectiveLevel()*10;
		//int total = 20+(50*level);
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
			if (hasEffect(Effect.CURSE)) {
				extra.println("The curse saps the life out of you...");
			}
		}else {
			if (superperson != null) {
				sipped = superperson.doSip();
			}
		}
		if (sipped != null) {
			if (hasSkill(Skill.POTION_CHUGGER)) {
				extra.println("A refreshing draft!");
				addEffect(Effect.BREATHING);
			}
			if (sipped == Effect.CURSE && hasSkill(Skill.TOXIC_BREWS)) {
				extra.println("Their face contorts in power!");
				for (int i = 0; i < 3;i++) {
					addEffect(extra.randList(Effect.minorBuffEffects));
				}
			}
		}
		
		if (hasSkill(Skill.QUICK_START)) {
			addEffect(Effect.ADVANTAGE_STACK);
		}
		if (hasSkill(Skill.STERN_STUFF)) {
			addEffect(Effect.STERN_STUFF);
		}
		if (bodystatus == null || bodystatus.resetToReuse(bodyType)) {
			bodystatus = new TargetHolder(bodyType);//now reused
		}
		
		//HP calculations
		tempMaxHp = getOOB_HP();//curse indicator handled in first player check
		if (this.hasEffect(Effect.HEARTY) || this.hasEffect(Effect.FORGED)) {
			tempMaxHp+= IEffectiveLevel.cleanLHP(level, 0.05);
		}
		
		hp = tempMaxHp;
		//all calculations that impact HP below this point do not change max hp
		
		if (takeBeer()) {
			if (isPlay) {
				Networking.unlockAchievement("drink_beer");
			}
			hp+=IEffectiveLevel.cleanLHP(level, hasSkill(Skill.BEER_BELLY) ? 0.10 : 0.05);
			if (!isPlay) {
				extra.println(getNameNoTitle()+" looks drunk!");
			}
		}
		
		boolean wentFast = false;
		if (hasEffect(Effect.SUDDEN_START)) {
			addEffect(Effect.BONUS_WEAP_ATTACK);
			addEffect(Effect.ADVANTAGE_STACK);
			wentFast = true;
		}
		
		if (hasSkill(Skill.OPENING_MOVE)) {
			addEffect(Effect.BONUS_WEAP_ATTACK);
			addEffect(Effect.BONUS_WEAP_ATTACK);
			wentFast = true;
		}
		if (wentFast && !isPlay && !extra.getPrint()) {
			extra.println(getNameNoTitle() + " springs their trap!");
		}
		
		speedFill = -1;
		isWarmingUp = false;
		int s = this.hasSkill(Skill.ARMOR_MAGE) ? this.getClarity()/60: 0;
		int b = this.hasSkill(Skill.ARMOR_MAGE) ? this.getClarity()/60: 0;
		int p = this.hasSkill(Skill.ARMOR_MAGE) ? this.getClarity()/60: 0;
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
		bag.resetQuals();//MAYBELATER: process qualities by calling the armor below on the inventory
		for (int i = 0; i < 5; i++) {
			Armor a = bag.getArmorSlot(i);
			a.resetArmor(s, b, p);
			if (a.hasArmorQual(ArmorQuality.PADDED)) {
				addEffect(Effect.PADDED);
			}
		}
		if (hasSkill(Skill.ARMOR_TUNING)) {
			bag.buffArmor(1.2f);
		}
		if (hasEffect(Effect.DAMAGED)) {
			bag.buffArmor(.5f);
			if (isPlay) {
				extra.println("Your damaged equipment is pretty beat up!");
			}
		}
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
		if (!extra.getPrint()) {
			extra.println(this.getName() + " has " + xp + "/" + level*level + " xp toward level " + (level+1) + ". +" + x + "xp.");
		}
		return pluslevel > level;
	}
	
	/**
	 * call after leveling up
	 * @param levels
	 */
	public void computeLevels(int levels) {
			//maxHp+=50*levels;
			if (!extra.getPrint() && mainGame.displayFlavorText && getBag().getRace().racialType == Race.RaceType.PERSONABLE) {
				BarkManager.getBoast(this, false);
			}
			addFeatPoint(levels);
			
			boolean autod = autoLevelIf();
			
			if (this.isPlayer()) {
				Networking.send("PlayDelay|sound_magelevel|1|");
				Networking.leaderboard("Highest Level",level);
				
				mainGame.story.levelUp(level+levels);
				Networking.unlockAchievement("level_any");
				if (level < 5 && level+levels >= 5) {//requires starting level (of character) to be lower than 5
					Networking.unlockAchievement("level5");
				}
				if (level < 10 && level+levels >= 10) {//requires starting level (of character) to be lower than 10
					Networking.unlockAchievement("level10");
				}
				if (!autod) {
					Player.player.addFeatPick(levels);//ai cannot delay leveling up, player can
					playerSkillMenu();
				}
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
						return extra.STAT_HEADER+"Classless Menu for "+extra.ITEM_DESC_PROP + getName();
					}});
				String[] attributes = attributeDesc();
				for (int i = 0; i < attributes.length;i++) {
					final String disp = attributes[i];
					list.add(new MenuLine() {
						@Override
						public String title() {
							return disp;
						}});
				}
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Configure Skill Attacks";
					}

					@Override
					public boolean go() {
						return getSuper().configAttacks();
					}});
				if (getSuper().getFeatPicks() > 0 && featPoints > 0) {
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
						if (getSuper().getFeatPicks() > 0) {
							list.add(new MenuLine() {

								@Override
								public String title() {
									return "No feat points, "+getSuper().getFeatPicks()+ " picks.";
								}});
						}else {
							list.add(new MenuLine() {

								@Override
								public String title() {
									return "No feat points or picks.";
								}});
						}
						
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
						extra.println("Display a dump of all skills granted?");
						if (extra.yesNo()) {
							fetchSkills().stream().forEach(s -> extra.println(s.explain()));
						}
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
	}
	/**
	 * will return false if can't get a feat
	 */
	public IHasSkills pickFeatRandom() {//does not consume pick
		List<IHasSkills> hases = Archetype.getFeatChoices(this);
		if (hases.isEmpty()) {
			return null;
		}
		useFeatPoint();
		IHasSkills gain = extra.randList(hases);
		setSkillHas(gain);
		return gain;
	}
	
	public void pickFeats(boolean consumePick) {
		assert isPlayer();
		if (consumePick) {
			getSuper().addFeatPick(-1);
		}
		Person p = this;
		
		if (isPlayer()) {
			while (featPoints > 0) {
				List<IHasSkills> iList = Archetype.getFeatChoices(p);
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
					list.add(new MenuLast() {

						@Override
						public String title() {
							return "delay remaining " + featPoints;
						}

						@Override
						public boolean go() {
							featPoints *= -1;//easy escape
							return true;
						}});
					return list;
				}});
			}
			featPoints *= -1;//we set it to *=-1 to easily get out, now unset it
			//if they ran out, -0 = 0 for us
		}
	}
	
	public static class FeatArchMenuPick extends MenuSelect {

		private final IHasSkills base;
		private boolean picked;
		private final Person pickFor;
		public FeatArchMenuPick(IHasSkills _base, Person p) {
			base = _base;
			pickFor = p;
		}
		
		@Override
		public String title() {
			return base.getBriefText();
		}

		@Override
		public boolean go() {
			picked = false;
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.addAll(IHasSkills.viewMenuItems(base,pickFor));
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
	@Override
	public int getLevel() {
		return level;
	}
	
	/**
	 * Get the full name of this person
	 * @return String - full name
	 */
	public String getName() {
		if (!mainGame.displayOwnName && isPlayer()) {
			return "YOU";
		}
		if (title == "" || title == null) {
			return firstName;
		}
		if (title.startsWith(",")) {
			return firstName+title;
		}
		return firstName + " " + title;
	}
	
	public String getNameNoTitle() {
		if (!mainGame.displayOwnName && isPlayer()) {
			return "YOU";
		}
		return firstName;
	}
	
	public void setFirstName(String str) {
		firstName = str;
	}
	
	public void displayStats() {
		displayStats(true);
	}
	public void displayStatOverview(boolean showHp) {
		extra.println(getName() +": "+extra.ITEM_DESC_PROP+"LvL " +extra.ITEM_WANT_HIGHER+ this.getLevel() +extra.PRE_WHITE+" " + this.getBag().getRace().renderName(false)+".");
		if (showHp) {
			extra.println(" "+extra.ITEM_WANT_HIGHER+getHp()+extra.PRE_WHITE +"/"+ tempMaxHp +extra.ITEM_DESC_PROP+ " HP.");
		}
		extra.println(" "
		+extra.ITEM_WANT_HIGHER+extra.format(bag.getHealth()) + "x "+extra.ITEM_DESC_PROP+"hpm, "
		+extra.ITEM_WANT_HIGHER+extra.format(bag.getAim()) + "x "+extra.ITEM_DESC_PROP+"aim, "
		+extra.ITEM_WANT_HIGHER+extra.format(bag.getDam()) + "x "+extra.ITEM_DESC_PROP+"dam, "
		+extra.ITEM_WANT_HIGHER+extra.format(bag.getSpeed()) + "x "+extra.ITEM_DESC_PROP+"spd, "
		+extra.ITEM_WANT_HIGHER+extra.format(bag.getDodge()) + "x "+extra.ITEM_DESC_PROP+"dodge, "
		+extra.ITEM_DESC_PROP+extra.CHAR_SHARP+"/"+extra.CHAR_PIERCE+"/"+extra.CHAR_BLUNT+
		": "+extra.ITEM_WANT_HIGHER+extra.F_WHOLE.format(bag.getSharpResist())+"/"+extra.F_WHOLE.format(bag.getBluntResist())+"/"+extra.F_WHOLE.format(bag.getPierceResist())
				);
		extra.println(bag.quickInventory());
	}
	
	public void displayStats(boolean inCombat) {
		
		if (inCombat) {
			displayStatOverview(true);
		}else {
			extra.println("This is " + this.getName() +". They are a "+extra.ITEM_DESC_PROP+"level " + extra.ITEM_WANT_HIGHER+this.getLevel() +" "+extra.PRE_WHITE + this.getBag().getRace().renderName(false)+".");
			extra.println("They have "+extra.ITEM_WANT_HIGHER+ getBase_HP() +extra.ITEM_DESC_PROP+ " LHP"+extra.PRE_WHITE+". Their health modifier is " +extra.ITEM_WANT_HIGHER+ extra.format(bag.getHealth()) +extra.PRE_WHITE+ "x. Their expected hp is "+extra.ITEM_DESC_PROP+ getOOB_HP() +extra.PRE_WHITE+".");
			extra.println("They have "+extra.ITEM_WANT_HIGHER+ extra.format(bag.getAim()) + "x "+extra.ITEM_DESC_PROP+"aiming"+extra.PRE_WHITE+", " +extra.ITEM_WANT_HIGHER+extra.format(bag.getDam()) + "x "+extra.ITEM_DESC_PROP+"damage"+extra.PRE_WHITE+", and "+extra.ITEM_WANT_HIGHER+extra.format(bag.getSpeed()) + "x "+extra.ITEM_DESC_PROP+"speed.");
			extra.println("They have " +extra.ITEM_WANT_HIGHER+ extra.format(bag.getDodge()) + "x "+extra.ITEM_DESC_PROP+"dodging"+extra.PRE_WHITE+", "+extra.ITEM_WANT_HIGHER + extra.format(bag.getSharpResist()) +extra.ITEM_DESC_PROP+" sharp resistance"+extra.PRE_WHITE+", " +
					extra.ITEM_WANT_HIGHER+extra.format(bag.getBluntResist()) +extra.ITEM_DESC_PROP+ " blunt resistance"+extra.PRE_WHITE+", and "+ extra.ITEM_WANT_HIGHER+extra.format(bag.getPierceResist())+extra.ITEM_DESC_PROP+ " pierce resistance"+extra.PRE_WHITE+".");
			extra.println("They have " +extra.ITEM_WANT_HIGHER+ xp +extra.PRE_WHITE+ "/" + level*level + extra.ITEM_DESC_PROP+" xp"+extra.PRE_WHITE+" toward "+extra.ITEM_DESC_PROP+"level "+extra.PRE_WHITE + (level+1) + ".");
			if (this.getBag().getRace().racialType == Race.RaceType.PERSONABLE) {
				extra.println("Their inventory includes " + bag.nameInventory());
				if (hasSkill(Skill.BEER_LOVER)) {extra.println("They look drunk.");}
				if (hasSkill(Skill.PARRY)) {extra.println("They have a parrying dagger.");}
				if (hasSkill(Skill.SHIELD)) {extra.println("They have a shield.");}
				attributeDescLongPrint();
			}
		}
		
		
		
	}
	
	public PersonType getPersonType() {
		return personType;
	}
	
	public void setPersonType(PersonType pt) {
		personType = pt;
	}

	public boolean isPlayer() {
		return getFlag(PersonFlag.ISPLAYER);
	}

	public void setPlayer() {
		setFlag(PersonFlag.ISPLAYER,true);
	}
	
	public boolean takeBeer() {
		if (hasSkill(Skill.BEER_LOVER)) {
			return true;
		}
		
		if (isPlayer() && Player.player.beer > 0) {
			Player.player.beer--;
			return true;
		}
		return false;
	}

	public void displayXp() {
		extra.println(this.getName() + " has " + xp + "/" + level*level + " xp toward level " + (level+1) + ".");
	}
	
	public String xpString() {
		return xp + "/" + level*level;
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
	public int healHP(int i) {
		if (hp >= tempMaxHp) {
			return 0;
		}
		int healing = Math.min(tempMaxHp-hp, i);
		hp = hp+healing;
		return healing;
	}
	
	public boolean getFlag(PersonFlag flag) {
		return extra.getEnumShortFlag(flag.ordinal(), flags);
		//return Byte.toUnsignedInt((byte) (flags & (1 << flag.ordinal()))) > 0;
	}
	
	public void setFlag(PersonFlag flag, boolean bool) {
		flags = extra.setEnumShortFlag(flag.ordinal(), flags, bool);
		/*
		if (bool) {
			flags |= (1 << flag.ordinal());
			return;
		}
		flags &= ~(1 << flag.ordinal());*/
	}

	public boolean isRacist() {
		return getFlag(PersonFlag.RACIST);
	}
	
	public void setRacism(boolean bool) {
		//at 0 index
		setFlag(PersonFlag.RACIST,bool);
	}
	
	public boolean isAngry() {
		return getFlag(PersonFlag.ANGRY);
	}
	
	public void setAngry(boolean bool) {
		setFlag(PersonFlag.ANGRY,bool);
		//at 1 index
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
		extra.println(extra.ITEM_DESC_PROP+"Sharp: "+extra.ITEM_WANT_HIGHER+ sharp + "/" +sharpm);
		extra.println(extra.ITEM_DESC_PROP+"Blunt: "+extra.ITEM_WANT_HIGHER+ blunt + "/" +bluntm);
		extra.println(extra.ITEM_DESC_PROP+"Pierce: "+extra.ITEM_WANT_HIGHER+ pierce + "/" +piercem);
	}
	
	public void removeEffectAll(Effect e) {
		effects.put(e, 0);
	}
	
	/**
	 * wipes all effects, use for entirely clean slate
	 */
	public void clearEffects() {
		effects.clear();//will need to be more complex if there ever are positive longterm effects
	}
	
	/**
	 * use for doctor clearing certain effects like curse and burnout
	 */
	public void cureEffects() {
		if (isPlayer()) {
			if (hasEffect(Effect.CURSE)) {
				extra.println(extra.RESULT_GOOD+"Your curse is lifted!");
			}
			if (hasEffect(Effect.BURNOUT)) {
				extra.println(extra.RESULT_GOOD+"Your burnout is treated!");
			}
			/*
			if (hasEffect(Effect.TIRED)) {
				extra.println(extra.RESULT_GOOD+"Your tiredness is treated!");
			}
			if (hasEffect(Effect.BEES)) {
				extra.println(extra.RESULT_GOOD+"Your bees are cured!");
			}*/
		}
		removeEffectAll(Effect.CURSE);
		removeEffectAll(Effect.BURNOUT);
		/*
		removeEffectAll(Effect.TIRED);
		removeEffectAll(Effect.BEES);*/
	}
	
	/**
	 * use for clearing effects that go away with a bath, like bees
	 */
	public void bathEffects() {
		if (isPlayer()) {
			if (hasEffect(Effect.BEES)) {
				extra.println(extra.RESULT_GOOD+"A quick dip makes the bees stop following you.");
			}
		}
		removeEffectAll(Effect.BEES);
	}
	
	/**
	 * used for clearing effects on rest, like burnout and tired
	 */
	public void restEffects() {
		if (isPlayer()) {
			if (hasEffect(Effect.BURNOUT)) {
				extra.println(extra.RESULT_GOOD+"You rest off the burnout.");
			}
			if (hasEffect(Effect.TIRED)) {
				extra.println(extra.RESULT_GOOD+"You rest off your tiredness.");
			}
		}
		removeEffectAll(Effect.BURNOUT);
		removeEffectAll(Effect.TIRED);
	}
	
	/**
	 * use for clearing effects that need to be repaired, like armor damage
	 */
	public void repairEffects() {
		if (isPlayer()) {
			if (hasEffect(Effect.DAMAGED)) {
				extra.println(extra.RESULT_GOOD+"Your armor is repaired.");
			}
		}
		removeEffectAll(Effect.DAMAGED);
	}
	
	/**
	 * currently includes all effects, even removed ones, since last clear
	 */
	public int effectsSize() {
		return effects.size(); 
	}
	
	public boolean displayEffects() {	
		boolean found = false;
		for (Effect e: effects.keySet()) {//listing is slower now
			int num = effects.getOrDefault(e, 0);
			if (num > 0) {
				if (!found) {
					extra.println(extra.STAT_HEADER+"Effects:");
					found = true;
				}
				if (num > 1) {
					extra.println(" " +e.getName() +extra.PRE_WHITE+ " x"+num+": "+ e.getDesc());
				}else {
					extra.println(" " +e.getName() +extra.PRE_WHITE+ ": "+ e.getDesc());
				}
			}
			
		}
		if (!found) {
			extra.println("No Effects.");
		}
		return found;
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
	
	public void clearBattleEffects() {
		//only goes over keyset because those are the ones it bothered to add
		for (Effect e: effects.keySet()) {
			if (!e.lasts()) {
				effects.put(e,0);
			}
		}
	}

	public void displaySkills() {
		for (Skill s: fetchSkills()) {
			if (s.getType() == Type.INTERNAL_USE_ONLY) {
				continue;
			}
			s.display();
		}
		
	}

	public void setTitle(String s) {
		title = s;
	}
	
	/**
	 * how many special attacks they have and are currently in use. Does not include weapon attacks converted
	 * <br>
	 * 3 is the most that can be obtained without starting to eat into normal attack potential
	 * <br>
	 * but 4 is fine if they don't have any sources of bonus weapon attacks
	 */
	public int specialAttackNum() {
		SuperPerson sp = getSuper();
		if (sp == null) {
			return 0;
		}
		//SkillAttackConf[] list = sp.getSpecialAttacks();
		int count = sp.getSAttCount();
		return count;
		/*return count > 0 ? 
				(count)+bag.getHand().getMartialStance().getBonusSkillAttacks()
				: 0;*/
	}

	public int nextWeaponAttacksCount() {
		int i = bag.getHand().getMartialStance().getBaseAttacks();//default 3
		if (i == 0) {
			return 0;//we don't have any base, null wand
		}
		int cap = Math.min(5, 7-specialAttackNum());//max 7 attacks overall
		if (this.hasEffect(Effect.DISARMED) || hasEffect(Effect.MAIMED)) {
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
		effects.put(e,Math.max(0,effects.getOrDefault(e, 0)-1));
	}
	
	public int effectCount(Effect e) {
		return effects.getOrDefault(e,0);
	}
	
	public void setEffectCount(Effect e, int count) {
		effects.put(e, count);
	}
	
	public void addEffectCount(Effect e, int count) {
		effects.put(e, effects.getOrDefault(e,0)+count);
	}

	public double getWoundDodgeCalc() {
		int torn = effects.getOrDefault(Effect.TORN,0);//torn builds up quickly
		int crippled = effects.getOrDefault(Effect.CRIPPLED,0);//crippled is only a condwound
		double mult = 1;
		if (hasEffect(Effect.EXHAUSTED)) {
			mult *=.5;
		}
		if (hasEffect(Effect.TIRED)) {
			mult *=.5;
		}
		return mult*
				(crippled == 0 ? 1 : Math.pow(.8,crippled))
				*(torn == 0 ? 1 : Math.pow(.9,torn));
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
		return RaceFactory.scarLookup(bag.getRaceID(),scar);
	}

	public void setScar(int i) {
		scar = i;
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
		
	}

	public void forceLevelUp(int endLevel) {
		while (getLevel() < endLevel) {
			addXp(getLevel()*getLevel());
		}
	}

	public double getSpeed() {
		return getBag().getSpeed() + (hasEffect(Effect.HASTE) ? 0.05 : 0 );
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
	
	public void updateRaceArch() {
		archSet.removeIf(a -> a.getType() == Archetype.AType.RACIAL);
		Race tRace = bag.getRace();
		if (tRace.archetype != null) {
			archSet.add(tRace.archetype);
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
			String[] attributes = attributeDesc();
			for (int i = 0;i < attributes.length;i++) {
				extra.println(attributes[i]);
			}
		}
	}
	
	public void multBodyStatus(int spot, double mult) {
		bodystatus.multStatus(spot, mult);
	}
	
	public void addBodyStatus(int spot, double add) {
		bodystatus.addStatus(spot, add);
	}
	
	public double getBodyStatus(int spot) {
		return bodystatus.getStatus(spot);
	}
	
	public Wound getAnyWoundForTargetSpot(int spot) {
		Target t =bodystatus.getTargetReturn(spot).tar;
		switch (extra.randRange(0,2)) {
		case 0 :
			return t.rollWound(DamageType.SHARP);
		case 1: default:
			return t.rollWound(DamageType.BLUNT);
		case 2 :
			return t.rollWound(DamageType.PIERCE);
		}
	}
	
	public int guessBodyHP(int spot) {
		return (int) (bodystatus.getRootStatus(spot)*getMaxHp());
	}
	//TODO: could make it detect if the body types are the same
	//naively, an array .equals should probably work, but there's probably a more performant way by using the
	//otherwise unused BodyPlan variable
	public boolean isSameTargets(Person other) {
		return bodyType == other.internalBType();
	}
	
	protected TypeBody internalBType() {
		return bodyType;
	}
	/**
	 * can apply negative discounts- use this to signal that the time didn't actually pass, we're just changing how long it takes
	 */
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
		return (int) (getRawDexterity()*getAgiPenAgainstDex());
	}
	
	public float getAgiPenAgainstDex() {
		return (bag.getAgiPen()*atrBox.getCapAgiPen());
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
	
	public int getStatByIndex(int index) {
		switch (index) {
		case 0:
			return getStrength();
		case 1:
			return getDexterity();
		case 2:
			return getClarity();
		}
		throw new RuntimeException("invalid stat index: " + index);
	}
	
	public float getAttributeAgiPen() {
		//make sure to update capacity
		if (atrBox == null) {
			updateSkills();
		}
		return atrBox.getAttributeAgiPenWithPen(bag.getAgiPen());
	}
	
	public float getTotalAgiPen() {
		//make sure to update capacity
		return getAttributeAgiPen() * bag.getAgiPen();
	}
	
	public float attMultStr() {
		int strength = getStrength();
		if (strength < 100) {
			return extra.lerp(.5f,1, strength/100f);
		}
		return 1f+((strength-100)/1000f);
	}
	/**
	 * note: in most cases you will want to use the agility multiplier penalty instead if you
	 * want to engage with dex < 100
	 * <br>
	 * this formula will return 1x for sub 100 and thus is only fit for benefits
	 * @return
	 */
	public float attMultDex() {
		int dexterity = getDexterity();
		if (dexterity < 100) {
			return 1f;
		}
		return 1f+((dexterity-100)/1000f);
	}
	
	public float attMultCla() {
		int clarity = getClarity();
		if (clarity < 100) {
			return extra.lerp(.5f,1, clarity/100f);
		}
		return 1f+((clarity-100)/1000f);
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
	
	/**
	 * as contested roll, but with no target since it's intended to be usable out of battle
	 * <br>
	 * suffers from Burnout
	 * <br>
	 * typically, should use >= since engager wins ties
	 */
	public int contestedRoll(int mynum, int theirnum) {
		int myroll = extra.randRange(0,mynum);
		if (hasEffect(Effect.BURNOUT)) {
			myroll/=2;
		}
		int theirroll = extra.randRange(0,theirnum);
		return myroll-theirroll;
	}
	
	public String capacityDesc() {
		float val = ((float)bag.getCapacity())/getStrength();
		String frontCol;
		if (val <= 1) {
			int finalVal = (int) extra.lerp(255,120, 1-val);
			frontCol = extra.inlineColor(new Color(finalVal,finalVal,finalVal));
		}else {
			//starts at half, reaches RED when val == 3
			frontCol = extra.inlineColor(extra.colorMix(Color.WHITE,Color.RED,.5f+extra.clamp((val-1)/3,0,.5f)));
		}
		return frontCol+bag.getCapacity() + extra.PRE_WHITE+"/"+extra.ATT_TRUE+getStrength();
	}
	
	public String[] attributeDesc() {
		return 
		new String[]{
				extra.ITEM_DESC_PROP+"LvL: "+extra.PRE_WHITE+getLevel()+", "+xpString()+" xp"
				,
				extra.ITEM_DESC_PROP+extra.DISP_WEIGHT+extra.PRE_WHITE+"/"+extra.ATT_TRUE+"Str"+extra.PRE_WHITE+": "
						+extra.ITEM_WANT_LOWER + bag.getCapacity() +extra.PRE_WHITE+ "/"+extra.ATT_TRUE+getStrength()
							+extra.PRE_WHITE+ ", "+ extra.ITEM_WANT_HIGHER+extra.F_TWO_TRAILING.format(attMultStr())+"x"
			,"("+extra.ATT_TRUE+"Base"+extra.PRE_WHITE+") "+extra.ATT_EFFECTIVE+"Dex"+extra.PRE_WHITE
			+": ("+extra.ATT_TRUE+getRawDexterity()+extra.PRE_WHITE+") "+extra.ATT_EFFECTIVE+getDexterity()
				+extra.PRE_WHITE+ ", "+ extra.ITEM_WANT_HIGHER+ extra.F_TWO_TRAILING.format(attMultDex())+"x"
				,extra.ITEM_DESC_PROP+" Mobility"+extra.PRE_WHITE+": "+extra.ITEM_WANT_HIGHER+ extra.F_TWO_TRAILING.format(getTotalAgiPen())+"x"+extra.PRE_WHITE+", "
				+extra.ITEM_DESC_PROP+" Swiftness"+extra.PRE_WHITE+": "+extra.ITEM_WANT_HIGHER+extra.F_TWO_TRAILING.format(getAttributeAgiPen())+"x"
		,extra.ATT_TRUE+"Cla"+extra.PRE_WHITE+": " +extra.ATT_TRUE+ getClarity()
			+extra.PRE_WHITE+", " +extra.ITEM_WANT_HIGHER+ extra.F_TWO_TRAILING.format(attMultCla())+"x"
				};
	}
	
	public void attributeDescLongPrint() {
		if (Player.getTutorial()) {
			extra.println("Your attributes include Strength, Dexterity, and Clarity. Strength lets you carry more stuff. If you can't carry your stuff, you suffer penalities to Dexterity. Dexterity influences your dodge when factoring in your restrictive equipment, which also impair your Dexterity.");
			extra.println("Attributes above 100 have bonuses. This is a +10% bonus for every 100 above 100. Dexterity is more involved due to being restricted by equipment weight and load, but cannot have a penalty to its pure multiplier.");
			extra.println("Strength mult applies to physical damage from weapons, Dexterity mult applies to hit roll.");
		}
		//str section
		extra.println(extra.STAT_HEADER+"Strength"+extra.PRE_WHITE+": "+extra.ATT_TRUE+getStrength());
			extra.println(
				" "+extra.ITEM_DESC_PROP+"Weight: "+extra.ITEM_WANT_LOWER+bag.getCapacity()
				+extra.ITEM_DESC_PROP+" Used Capacity: "+extra.ITEM_WANT_LOWER+extra.F_WHOLE.format(100f*(bag.getCapacity())/(getStrength()))+"%");
			extra.println(" "+extra.ITEM_DESC_PROP+"Multiplier: "+extra.ITEM_WANT_HIGHER+ extra.F_TWO_TRAILING.format(attMultStr())+"x");
		extra.println(
				extra.ITEM_DESC_PROP+" Encumbrance Multiplier Maximum Penalty: " +extra.ITEM_WANT_HIGHER+ extra.F_TWO_TRAILING.format(atrBox.getDexPen())+"x");
		//dex section
		extra.println(
			extra.STAT_HEADER+"Dexterity"+extra.PRE_WHITE+":" +extra.ATT_TRUE +" Base "+getRawDexterity()+extra.PRE_WHITE+", "
				+extra.ATT_EFFECTIVE+"Effective " +getDexterity() + extra.PRE_WHITE);
		extra.println(extra.ITEM_DESC_PROP+" Multiplier"+extra.PRE_WHITE+": "+extra.ITEM_WANT_HIGHER+ extra.F_TWO_TRAILING.format(attMultDex())+"x");
		extra.println(
				extra.ITEM_DESC_PROP+" Mobility Multiplier: "
				+extra.ITEM_WANT_HIGHER+ extra.F_TWO_TRAILING.format(getTotalAgiPen())+"x"+extra.PRE_WHITE+", "
				+extra.ITEM_DESC_PROP+"Applied to Dex: "+extra.ITEM_WANT_HIGHER+extra.F_TWO_TRAILING.format(getAgiPenAgainstDex()) 
				+ "x"+extra.ITEM_DESC_PROP+" of Encumbrance "+extra.ITEM_WANT_HIGHER+extra.F_TWO_TRAILING.format(atrBox.getCapAgiPen())
				+ "x"+extra.ITEM_DESC_PROP+" and Restriction "+extra.DISP_AMP+" "+extra.ITEM_WANT_HIGHER+extra.F_TWO_TRAILING.format(bag.getAgiPen()) +"x"
				+extra.PRE_WHITE+";"+extra.ITEM_DESC_PROP+" Swiftness Multiplier: "+extra.ITEM_WANT_HIGHER+extra.F_TWO_TRAILING.format(getAttributeAgiPen())+"x");
		//cla section
		extra.println(extra.STAT_HEADER+"Clarity"+extra.PRE_WHITE+": "+extra.ATT_TRUE+getClarity());
		extra.println(extra.ITEM_DESC_PROP+" Multiplier: " +extra.ITEM_WANT_HIGHER+ extra.F_TWO_TRAILING.format(attMultCla())+"x");
	}

	public SuperPerson getSuper() {
		return superperson;
	}

	public void setSuper(SuperPerson p) {
		superperson = p;
	}

	/**
	 * if they can loot, speak, trade, etc etc
	 */
	public boolean isPersonable() {
		return bag.getRace().racialType == RaceType.PERSONABLE;
	}

	public void forceKill() {
		hp = 0;
	}

	

	public String getTitle() {
		if (!mainGame.displayOwnName && isPlayer()) {
			return "YOU";
		}
		return title;
	}
	
	public List<Person> getSelfOrAllies(){
		if (superperson != null) {
			return superperson.getAllies();
		}
		return Collections.singletonList(this);
	}

	/**
	 * get the agent, or if doesn't exist, create a new one with the defined goal
	 * <br>
	 * does not set the goal if already existing agent
	 */
	public Agent getMakeAgent(AgentGoal goal) {
		if (superperson == null) {
			return new Agent(this,goal);//agent sets our own superperson value
		}
		assert superperson instanceof Agent;
		return (Agent) superperson;
	}
	
	/**
	 * sets the agent goal to only be this goal, and creates a superperson if need be
	 */
	public Agent setOrMakeAgentGoal(AgentGoal goal) {
		if (superperson == null) {
			return new Agent(this,goal);//agent sets our own superperson value
		}
		assert superperson instanceof Agent;
		Agent a = (Agent) superperson;
		a.onlyGoal(goal);
		return a;
	}

	public void resistDeath(float percentheal) {
		hp = (int) (getMaxHp()*percentheal);
		hp = Math.max(1,hp);
	}
	
	public void setNearDeath() {
		hp = 1;
	}
	/**
	 * 
	 * @return can be null, but not have null elements
	 */
	public List<Wound> processBodyStatus() {
		return bodystatus.processEffectUpdates();
	}

	public String inlineHPColor() {
		float per = ((float)this.getHp())/(this.getMaxHp());
		String res;
		switch (Math.max(0,(int)Math.ceil((per*100)/25))) {
		case 0:
			res = extra.HP_I_DEAD;
			break;
		case 1: 
			res = extra.HP_I_SOME;
			break;
		case 2: 
			res = extra.HP_I_HALF;
			break;
		case 3: 
			res = extra.HP_I_MOSTLY;
			break;
		default: 
			res = extra.HP_I_FULL;
			break;
		}
		int tval = extra.clamp((int)(extra.lerp(125,256,per)),100,255);
		return extra.inlineColor(new Color(tval,tval,tval))+res;
	}

	public double getMissCalc() {
		double val = .05;
		for (Armor a: bag.getArmor()) {
			if (a.hasArmorQual(ArmorQuality.DISPLACING)) {
				val+=.01;
			}
		}
		return val;
	}
	
	/**
	 * does nothing if not loaded
	 */
	public void resetCapacity() {
		if (atrBox != null) {
			atrBox.setCapacity(bag.getCapacity());
		}
	}

	//FIXME: use this instead, and add a display option to show the inventory of who you're attacking
	public boolean reallyAttack() {
		return reallyFight("Really attack");
	}
	public boolean reallyFight(String verb) {
		graphicalFoe();
		extra.println(extra.PRE_BATTLE+verb+" " + getName() + " level " + getLevel() + "?");
		int i = extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_BATTLE+"Fight.";
					}

					@Override
					public boolean go() {
						return true;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Examine.";
					}

					@Override
					public boolean go() {
						//unsure which display would be best
						displayStatOverview(false);
						return false;
					}});
				list.add(new MenuBack("Leave."));
				return list;
			}});
		Networking.clearSide(1);
		return i == 0;
	}
	
	public void graphicalFoe() {
		bag.graphicalDisplay(1,this);
	}
	
	/**
	 * uses uneffective level, use for npcs stuff
	 * @param fac
	 * @param multFor
	 * @param multAgainst
	 */
	public void setFacLevel(Faction fac, float multFor, float multAgainst) {
		facRep.addFactionRep(fac, multFor*getUnEffectiveLevel(), multAgainst*getUnEffectiveLevel());
	}

}
