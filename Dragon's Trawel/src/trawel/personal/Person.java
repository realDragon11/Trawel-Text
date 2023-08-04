package trawel.personal;
import java.util.ArrayList;
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
import trawel.personal.classless.Archetype;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.Feat;
import trawel.personal.classless.HasSkills;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.classless.Skill.Type;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race;
import trawel.personal.item.body.Race.RaceType;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.variants.ArmorStyle;
import trawel.personal.people.Player;
import trawel.towns.services.Store;

/**
 * 
 * @author dragon
 * 2/5/2018
 * A collection of stats, attributes, and an inventory.
 */
public class Person implements java.io.Serializable, HasSkills{

	private static final long serialVersionUID = 2L;

	//inst vars
	private Inventory bag;
	
	private transient ImpairedAttack attackNext;
	private int xp;
	private short level;
	private byte intellect;
	private transient double speedFill;
	private transient boolean isWarmingUp;
	private transient int hp, tempMaxHp;
	private PersonType personType = extra.choose(PersonType.COWARDLY,PersonType.FEARLESS);
	//private String placeOfBirth;
	private int beer;
	/**
	 * bit 1 = racism (0b00000001) <br>
	 * bit 2 = angry (racist to non humanoids) (0b00000010) <br>
	 */
	private byte flags = 0b00000000;//used with bitmasking
	
	private String firstName,title;

	private int skillPoints;
	private int fighterLevel= 0,traderLevel = 0,explorerLevel = 0, mageLevel = 0, magePow = 0, defenderLevel = 0, defPow = 0, fightPow = 0;
	
	public int lightArmorLevel = 0, heavyArmorLevel = 0, edrLevel = 0;
	public boolean hasEnduranceTraining = false;
	
	//private List<Skill> skills = new ArrayList<Skill>();
	private List<Effect> effects;
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
	
	public enum RaceFlag {
		NONE, CRACKS, UNDEAD;
	}
	
	public enum PersonType{
		COWARDLY,FEARLESS,GRIZZLED,DEATHCHEATED,LIFEKEEPER
	}
	
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
		
		xp = 0;
		
		this.job = job;
		rFlag = raceFlag;
		if (level < 1) {
			extra.println("non-fatal (until you run into the level zero person) exception: level is zero on someone");
		}
		//maxHp = 40*level;//doesn't get all the hp it would naturally get
		//hp = maxHp;
		intellect = 2;//advanced looting
		
		
		bag = new Inventory(level,raceType,matType,job,race);
		bag.owner = this;
		if (raceType == RaceType.HUMANOID) {
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
		skillPoints = level-1;
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
			this.AILevelUp();
		}
		//this.noAILevel = !isAI;
		effects = new ArrayList<Effect>();
		
		
		//atrBox = new AttributeBox(this);
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
	@Override
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
	
	@Override
	public String getText() {
		throw new UnsupportedOperationException("people can't give classless text");
	}
	
	/**
	 * this will be called automatically if the Person does not have one of the base things yet
	 * but you should call it after you assemble them or update them, in case they got it built
	 * while you weren't looking.
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
			hp+=this.getMageLevel();
		}
		total*=bag.getHealth();
		return total;
	}
	
	public int getBase_HP() {
		int total = 20+(50*level);
		total+=(edrLevel*ENDURANCE_HP_BONUS)*( hasEnduranceTraining ? 2 :1);
		total+=skillPoints*SKILLPOINT_HP_BONUS;
		return total;
	}
	
	/**
	 * Clear the person for a new battle.
	 */
	public void battleSetup() {
		
		boolean isPlay = false;
		if (this.isPlayer()) {
			Player.player.doSip();
			isPlay = true;
		}
		bodystatus = new TargetHolder(bodyType);
		tempMaxHp = getOOB_HP();
		hp = tempMaxHp;
		if (takeBeer()) {
			if (isPlay) {
				Networking.sendStrong("Achievement|drink_beer|");
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
		
		
		speedFill = -1;
		isWarmingUp = false;
		int s = this.hasSkill(Skill.ARMOR_MAGE) ? this.getMageLevel(): 0;
		int b = this.hasSkill(Skill.ARMOR_MAGE) ? this.getMageLevel(): 0;
		int p = this.hasSkill(Skill.ARMOR_MAGE) ? this.getMageLevel(): 0;
		int defLvl = this.getDefenderLevel();
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
		if (this.hasEffect(Effect.B_MARY)) {
			this.addEffect(Effect.BLEED);
		}
	}
	


	/**
	 * Take damage. Return true if this caused a death.
	 * @param dam (int)
	 * @return if this caused the person to die. (boolean)
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
			setSkillPoints(getSkillPoints() + levels);
			if (this.isPlayer()) {
				Networking.send("PlayDelay|sound_magelevel|1|");
				Networking.sendStrong("Leaderboard|Highest Level|" + level+ "|");
				mainGame.story.levelUp(level+levels);
				if (level < 5 && level+levels >= 5) {
				Networking.sendStrong("Achievement|level5|");}
				if (level < 10 && level+levels >= 10) {
				Networking.sendStrong("Achievement|level10|");}
				playerSkillMenu();
			}else {
				//intellect+=levels;
				this.AILevelUp();
			}
			level+=levels;
	}
	
	public void playerSkillMenu() {
		if (Player.getTutorial()) {
			extra.println("This is the skill menu.");
			if (skillPoints == 0) {
				extra.println("You don't have any skillpoints, and should probably exit this menu.");
			}
		}
		extra.println();
		Person p = this;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "You have " + p.skillPoints + " skillpoint"+ (p.skillPoints == 1 ? "" : "s") +".";
					}});
				
				for (EArt e: Player.player.eArts) {
					list.add(EArtSkillMenu.construct(e));
				}
				if (Player.player.eArts.size() < 2) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Select a new EArt (Martial)";
						}

						@Override
						public boolean go() {
							eaSubMenu(EAType.MARTIAL);
							return false;
						}});
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Select a new EArt (Magic)";
						}

						@Override
						public boolean go() {
							eaSubMenu(EAType.MAGIC);
							return false;
						}});
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Select a new EArt (Other)";
						}

						@Override
						public boolean go() {
							eaSubMenu(EAType.OTHER);
							return false;
						}});
				}
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Conventional Skills";
					}

					@Override
					public boolean go() {
						csSubMenu();
						return false;
					}});
				list.add(new MenuBack());
				return list;
			}
			
		});
	}
	
	@Deprecated
	private void skillAdd(Skill s) {
		skillSet.add(s);
		skillPoints--;
		switch (s.getType()) {
		case FIGHTER: fighterLevel++;break;
		case TRADER: traderLevel++;break;
		case EXPLORER: explorerLevel++;break;
		case MAGE: mageLevel++;break;
		case DEFENDER: defenderLevel++;break;
		}
		switch (s) {
		case INHERIT:this.bag.addGold(500);break;
		case EXPANDER:Store.INVENTORY_SIZE++;break;
		case SKILLPLUS: skillPoints+=2;break;
		case MAGE_TRAINING: magePow+=3;break;
		case DEFENSIVE_TRAINING: defPow+=3;break;
		case IDEF_TRAINING: defPow+=1; defenderLevel--;break;
		case IOFF_TRAINING: fightPow+=1; fighterLevel--;break;
		case IMAG_TRAINING: magePow+=1; mageLevel--;break;
		case MAGE_POWER: magePow+=3;break;
		case MAGE_FRUGAL: magePow-=2; fightPow+=2; defPow+=2;break;
		default: break;
		}
	}
	
	@Deprecated
	public void AILevelUp() {
		if (skillPoints > 0) {
			ArrayList<Skill> list = new ArrayList<Skill>();
			
			for (Skill s: Skill.values()) {
				if (!s.getAITake()) {
					continue;
				}
				switch (s.getType()) {
				case DEFENDER:
					if (s.getLevel() != defenderLevel+1) {
						continue;
					}
					break;
				case EXPLORER:
					if (s.getLevel() != explorerLevel+1) {
						continue;
					}
					break;
				case FIGHTER:
					if (s.getLevel() != fighterLevel+1) {
						continue;
					}
					break;
				case MAGE:
					if (s.getLevel() != mageLevel+1) {
						continue;
					}
					break;
				case TRADER:
					if (s.getLevel() != traderLevel+1) {
						continue;
					}
					break;
				}
				list.add(s);
			}
			if (list.size() > 0) {
				Skill s;
				int i = 0;
				do {
					i++;
				s = extra.randList(list);
				}while((s.equals(Skill.KUNG_FU) && this.getBag().getRace().racialType != Race.RaceType.HUMANOID) && i < 99);
			//skillAdd(s);
			
			
			//AILevelUp();//recursive hack
			}
		}
	}
	
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
		if (title == "") {
			return firstName;
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


	/**
	 * @return the intellect (int)
	 */
	public byte getIntellect() {
		return intellect;
	}


	/**
	 * @param intellect (int) -  the intellect to set
	 */
	public void setIntellect(byte intellect) {
		this.intellect = intellect;
	}

	public boolean isPlayer() {
		return (intellect < 0);
	}

	public void setPlayer() {
			intellect = -2;
	}
	
	public void autoLootPlayer() {
			intellect = -1;
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

	public int getSkillPoints() {
		return skillPoints;
	}

	public void setSkillPoints(int skillPoints) {
		this.skillPoints = skillPoints;
	}
	
	public void useSkillPoint() {
		skillPoints--;
	}
	public void addSkillPoint() {
		skillPoints++;
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
	public void addFighterLevel() {
		fighterLevel++;
	}

	public boolean isRacist() {
		//unsure if best way, but I care more about memory storage than speed, so doesn't need to be fast rn
		return Byte.toUnsignedInt((byte) (flags & (1 << 0))) > 0;
	}
	
	public void setRacism(boolean bool) {
		//at 0 index
		if (bool) {
			flags |= (1 << 0);
			return;
		}
		flags &= ~(1 << 0);
	}
	
	public boolean isAngry() {
		//unsure if best way, but I care more about memory storage than speed, so doesn't need to be fast rn
		return Byte.toUnsignedInt((byte) (flags & (1 << 1))) > 0;
	}
	
	public void setAngry(boolean bool) {
		//at 1 index
		if (bool) {
			flags |= (1 << 1);
			return;
		}
		flags &= ~(1 << 1);
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
	}
	
	public void removeEffectAll(Effect e) {
		while (effects.contains(e)) {
			effects.remove(e);
		}
	}
	
	public void cureEffects() {
		effects.clear();//will need to be more complex if there ever are positive longterm effects
	}
	
	public int effectsSize() {
		return effects.size(); 
	}
	
	public void displayEffects() {
		if (effects.isEmpty()) {
			extra.println("You're perfectly healthy.");
		}
		for (Effect e: effects) {
			extra.println(e.name() + ": "+ e.getDesc());
		}
	}
	
	
	public void addEffect(Effect e) {
		if (e.stacks()) {
			effects.add(e);
		}else {
			if (!effects.contains(e)) {
				effects.add(e);
			}
		}
	}
	
	public boolean hasEffect(Effect e) {
		return effects.contains(e);
	}
	
	/**
	 * should only be used to iterate over the effects, instead of calling
	 * hasEffect repeatedly.
	 */
	public List<Effect> getEffects() {
		return effects;
	}
	
	public void clearBattleEffects() {
		ArrayList<Effect> removeList = new ArrayList<Effect>();
		for (Effect e: effects) {
			if (!e.lasts()) {
				removeList.add(e);
			}
		}
		for (Effect e: removeList) {
			this.removeEffectAll(e);
		}
	}
	
	private int burnouts() {
		int i = 0;
		for (Effect e: effects) {
			if (e.equals(Effect.BURNOUT)) {
				i++;
			}
		}
		return i;
	}

	public void displaySkills() {
		for (Skill s: fetchSkills()) {
			if (s.getType() == Type.INTERNAL_USE_ONLY) {
				continue;
			}
			s.display();
		}
		
	}

	@Deprecated
	public void addSkill(Skill skill) {
		skillSet.add(skill);
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

	public int attacksThisAttack() {
		int i = 3;
		if (this.hasSkill(Skill.BONUSATTACK_BERSERKER)) {
			i++;
		}
		if (this.hasEffect(Effect.DISARMED)) {
			this.removeEffectAll(Effect.DISARMED);
			i--;
		}
		return Math.max(1,i);
	}

	public void removeEffect(Effect e) {
		effects.remove(e);
		
	}

	public double getTornCalc() {
		double starter = 1;
		int count = 0;
		for (Effect e: effects) {
			if (e.equals(Effect.TORN)) {
				count++;
			}
		}
		for (int i = 0; i < count;i++) {
			starter*=.9;
		}
		return starter;
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
		if (this.isPlayer()) {
			Player.player.eaBox.exeKillLevel += .3;
		}
		
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
			bag.getHand().transmuteWeapType("generic teeth and claws");
			break;
		case B_REAVER_TALL:
			bag.getHand().transmuteWeapType("standing reaver");
			break;
		}
		
	}

	public void debug_print_status(int damageDone) {
		if (extra.getPrint()) {
			return;
		}
		if (damageDone != 0 && mainGame.advancedCombatDisplay) {
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

	public double getConditionForPart() {
		return bodystatus.getStatusOnMapping(TargetFactory.TORSO_MAPPING);
	}

	@Override
	public int getStrength() {
		return fetchAttributes().getStrength();
	}

	@Override
	public int getDexterity() {
		return (int) (getRawDexterity()* bag.getAgiPen());
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
		+ " (total) AMP (" + getTotalAgiPen() + ") " + getAttributeAgiPen();
	}

}
