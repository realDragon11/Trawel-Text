package trawel;
import java.util.ArrayList;
import java.util.List;

import org.nustaq.serialization.annotations.OneOf;

import trawel.Race.RaceType;
import trawel.earts.EAType;
import trawel.earts.EArt;
import trawel.earts.EArtSkillMenu;
import trawel.factions.FBox;
import trawel.factions.HostileTask;

/**
 * 
 * @author Brian Malone
 * 2/5/2018
 * A collection of a monster and an inventory.
 */
public class Person implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	//inst vars
	private Inventory bag;
	
	private transient Attack attackNext;
	private int xp = 0;
	private short level = 1;
	private byte intellect;
	private transient double speedFill = 0;
	private transient boolean isAttacking =false;
	private transient int hp, tempMaxHp;
	//private Taunts brag;
	@OneOf({"cowardly","fearless","grizzled"})
	public String personType = extra.choose("cowardly","fearless");
	//private String placeOfBirth;
	private int beer;
	private byte flags = 0b00000000;//used with bitmasking
	//bit 1 = racism (0b00000001)
	//bit 2 = angry (racist to non humanoids) (0b00000010)
	
	private String firstName,title;

	private int skillPoints;
	private int fighterLevel= 0,traderLevel = 0,explorerLevel = 0, mageLevel = 0, magePow = 0, defenderLevel = 0, defPow = 0, fightPow = 0;
	
	public int lightArmorLevel = 0, heavyArmorLevel = 0, edrLevel = 0;
	public boolean hasEnduranceTraining = false;
	
	private List<Skill> skills = new ArrayList<Skill>();
	//private boolean noAILevel;
	private List<Effect> effects;
	private RaceFlag rFlag;

	public TargetFactory.TargetType targetOverride = null;
	
	public Weapon backupWeapon = null;
	
	private String scar = "";
	
	private float pitch = 0;
	
	private int pKills = 0, deaths = 0;
	
	private AIJob job;
	
	public FBox facRep = new FBox();
	
	public HostileTask hTask;
	
	public enum RaceFlag {
		NONE, CRACKS, UNDEAD;
	}
	//private boolean isPlayer;
	
	//Constructor
	public Person(int level, boolean isAI, Race.RaceType raceType, Material matType,RaceFlag raceFlag,boolean giveScar,AIJob job,Race race) {
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
		firstName = randomLists.randomFirstName();
		title = randomLists.randomLastName();
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
	}
	
	@Deprecated
	public Person(int level) {
		this(level,true,Race.RaceType.HUMANOID,null,Person.RaceFlag.NONE,true);
	}
	
	public Person(int level, boolean aiLevel, Race.RaceType raceType, Material matType,RaceFlag raceFlag,boolean giveScar) {
		this(level,aiLevel,raceType,matType,raceFlag,giveScar,null,null);
	}
	
	@Deprecated
	public Person(int level,AIJob job) {
		this(level,true,Race.RaceType.HUMANOID,null,Person.RaceFlag.NONE,true,job,null);
	}
	
	public static Person animal(int level,RaceFactory.RaceID race,Material matType,boolean giveScar){
		return new Person(level,true,Race.RaceType.BEAST,matType,RaceFlag.NONE,giveScar,null,RaceFactory.getRace(race));
	}
	

	public enum AIJob{
		KNIGHT(new String[] {"heavy","chainmail"},new String[] {"longsword","mace","axe","lance"}),
		ROGUE(new String[] {"light"},new String[] {"rapier","dagger"}), 
		LUMBERJACK(new String[] {"light"},new String[] {"axe"}), 
		GRAVER(new String[] {"light"},new String[] {"shovel"});
		
		public String[] amatType, weapType;
		AIJob(String[] amatType, String[] weapType) {
			this.amatType = amatType;
			this.weapType = weapType;
		}
	}
	
	//instance methods
	
	public RaceFlag getRaceFlag() {
		return rFlag;
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
	public void setAttack(Attack newAttack){
		attackNext = newAttack;
		speedFill += attackNext.getSpeed()/(bag.getSpeed() + (this.hasEffect(Effect.HASTE) ? 0.1 : 0 ));//TODO: make sure making this += doesn't fuck anything up
		isAttacking = true;
	}
	
	/**
	 * Returns whether there is an attack queued or not
	 * @return has an attack already (boolean)
	 */
	public boolean isAttacking() {
		return isAttacking;
	}
	
	/**
	 * Returns what attack the person wants to use next
	 * @return the next attack (Attack)
	 */
	public Attack getNextAttack() {
		if (isAttacking == false) {
			extra.println("This person isn't attacking!");
			throw new NotAttackingException();
		}
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
		total+=(edrLevel*6)*( hasEnduranceTraining ? 2 :1);
		total+=skillPoints*3;
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
		speedFill = 0;
		tempMaxHp = getOOB_HP();
		hp = tempMaxHp;
		if (takeBeer()) {
			if (Player.player.getPerson() == this) {
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
		isAttacking = false;
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
		if (xp >= pluslevel*pluslevel) {
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
			this.setSkillPoints(this.getSkillPoints() + level);
			if (this.isPlayer()) {
				Networking.send("PlayDelay|sound_magelevel|1|");
				Networking.sendStrong("Leaderboard|Highest Level|" + level+ "|");
				playerLevelUp();
			}else {
				//intellect+=levels;
				this.AILevelUp();
			}
	}
	
	public void playerLevelUp() {
		mainGame.story.levelUp(level);
		if (level == 5) {
		Networking.sendStrong("Achievement|level5|");}
		if (level == 10) {
		Networking.sendStrong("Achievement|level10|");}
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
				
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}});
				return list;
			}
			
		});
		/*
		extra.println("Pick a class to examine:");
		extra.println("1 fighter");
		extra.println("2 trader");
		extra.println("3 explorer");
		extra.println("4 mage");
		extra.println("5 defender");
		extra.println("6 exit");
		ArrayList<Skill> list = new ArrayList<Skill>();
		switch(extra.inInt(6)) {
		case 1: 
			extra.println("Fighter Class Level: " + fighterLevel);
			for (Skill s: Skill.values()) {
			if (s.getLevel() == fighterLevel+1 && s.getType() == Skill.Type.FIGHTER) {
				list.add(s);
			}};break;
		case 2: extra.println("Trader Class Level: " + traderLevel); 
			for (Skill s: Skill.values()) {
			if (s.getLevel() == traderLevel+1 && s.getType() == Skill.Type.TRADER) {
				list.add(s);
			}};break;
		case 3: extra.println("Explorer Class Level: " + explorerLevel); 
		for (Skill s: Skill.values()) {
		if (s.getLevel() == explorerLevel+1 && s.getType() == Skill.Type.EXPLORER) {
			list.add(s);
		}};break;
		
		case 4: extra.println("Mage Class Level: " + mageLevel); 
		for (Skill s: Skill.values()) {
		if (s.getLevel() == mageLevel+1 && s.getType() == Skill.Type.MAGE) {
			list.add(s);
		}};break;
		
		case 5: extra.println("Defender Class Level: " + defenderLevel); 
		for (Skill s: Skill.values()) {
		if (s.getLevel() == defenderLevel+1 && s.getType() == Skill.Type.DEFENDER) {
			list.add(s);
		}};break;
		case 6: return;
		}
		extra.println("Pick a skill to buy:");
		int i = 1;
		for (Skill s: list) {
			extra.println(i+" "+ s.getName() + ": " + s.getDesc());
			i++;
		}
		extra.println(i + " back");
		int in = extra.inInt(i);
		i=1;
		
		for (Skill s: list) {
			
			if (in == i) {
				extra.println("Buy the " + s.getName() + " skill?");
				extra.println(s.getDesc());
				extra.println(s.getLongDesc());
				if (extra.yesNo()) {
				if (skillPoints > 0) {
				extra.println("You spend a skillpoint to gain the "+s.getName() + " skill!");
				skillAdd(s);
				}else {
					extra.println("You don't have any skillpoints!");
				}
			}}
			
			i++;
		}*/
		//playerLevelUp();
	}
	
	private void skillAdd(Skill s) {
		skills.add(s);
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
			skillAdd(s);
			
			
			AILevelUp();//recursive hack
			}
		}
	}
	
	private void eaSubMenu(EAType eat) {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list2 = new ArrayList<MenuItem>();
				if (Player.player.eArts.size() >= 2) {
					list2.add(new MenuSelect() {

						@Override
						public String title() {
							return "back";
						}

						@Override
						public boolean go() {
							return true;
						}});
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
										
										list3.add(new MenuSelect() {

											@Override
											public String title() {
												return "back";
											}

											@Override
											public boolean go() {
												return true;
											}
											
										});
										
										return list3;
									}});
								return false;
							}});
					}
				}
				list2.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}});
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
								list2.add(new MenuLine() {

									@Override
									public String title() {
										return "You have " + Player.player.getPerson().getSkillPoints() + " skillpoint"+ (Player.player.getPerson().getSkillPoints() == 1 ? "" : "s") +".";
									}});
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
											Player.player.getPerson().setSkillPoints(Player.player.getPerson().getSkillPoints()-1);
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
											Player.player.getPerson().setSkillPoints(Player.player.getPerson().getSkillPoints()-1);
											Player.player.getPerson().heavyArmorLevel++;
										}}
										return false;
									}});
								list2.add(new MenuSelect() {

									@Override
									public String title() {
										return "back";
									}

									@Override
									public boolean go() {
										return true;
									}});
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
								list2.add(new MenuLine() {

									@Override
									public String title() {
										return "You have " + Player.player.getPerson().getSkillPoints() + " skillpoint"+ (Player.player.getPerson().getSkillPoints() == 1 ? "" : "s") +".";
									}});
								list2.add(new MenuSelect() {

									@Override
									public String title() {
										return "Endurance: " + Player.player.getPerson().edrLevel;
									}

									@Override
									public boolean go() {
										extra.println("Endurance grants you more hp than you get having the skillpoint unspent. It costs 1 skillpoint per level. Buy?");
										if (extra.yesNo()) {
										if (Player.player.getPerson().getSkillPoints() > 0) {
											Player.player.getPerson().setSkillPoints(Player.player.getPerson().getSkillPoints()-1);
											Player.player.getPerson().edrLevel++;
										}}
										return false;
									}});
								list2.add(new MenuSelect() {

									@Override
									public String title() {
										return "back";
									}

									@Override
									public boolean go() {
										return true;
									}});
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
								list2.add(new MenuLine() {

									@Override
									public String title() {
										return "You have " + Player.player.getPerson().getSkillPoints() + " skillpoint"+ (Player.player.getPerson().getSkillPoints() == 1 ? "" : "s") +".";
									}});
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
											Player.player.getPerson().setSkillPoints(Player.player.getPerson().getSkillPoints()-1);
											Player.player.getPerson().addSkill(Skill.EXPANDER);
										}}
										return false;
									}});
								}
								list2.add(new MenuSelect() {

									@Override
									public String title() {
										return "back";
									}

									@Override
									public boolean go() {
										return true;
									}});
								return list2;
							}});
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}});
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
		if (beer > 0 || skills.contains(Skill.BEER_LOVER)) {extra.println("They look drunk.");}
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
	
	public String getPersonType() {
		return personType;
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
		if (beer > 0 || skills.contains(Skill.BEER_LOVER)) {extra.println("They look drunk.");}
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
		if (beer > 0 || skills.contains(Skill.BEER_LOVER)) {
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
	
	public List<Skill> getSkills(){
		return skills;
	}
	
	public boolean hasSkill(Skill o) {
		return skills.contains(o);
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
			
			sharpm += a.getSharpResist()*a.getResist();
			bluntm += a.getBluntResist()*a.getResist();
			piercem += a.getPierceResist()*a.getResist();
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
	
	//made public 5/11/2020
	public boolean hasEffect(Effect e) {
		return effects.contains(e);
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
		for (Skill s: skills) {
			s.display();
		}
		
	}

	public void addSkill(Skill skill) {
		skills.add(skill);
	}

	public void setTitle(String s) {
		title = s;
	}

	public void inflictWound(Attack.Wound wound) {
		switch (wound) {
		case BLINDED:
			this.attackNext.blind(.5);
			break;
		case HAMSTRUNG:
			this.advanceTime(-8);
			break;
		case DIZZY: case FROSTED:
			this.attackNext.blind(.75);
			break;	
		case WINDED:
			this.advanceTime(-16);
			break;
		case MAJOR_BLEED:
			this.addEffect(Effect.MAJOR_BLEED);
			//major bleed stuff, lack of break is on purpose
		case BLEED:
			this.addEffect(Effect.BLEED);
			break;
		case DISARMED: case SCREAMING:
			this.addEffect(Effect.DISARMED);
			break;
		case TRIPPED:
			this.advanceTime(-20);
			break;
		case KO:
			this.takeDamage(5*level);
			this.addEffect(Effect.RECOVERING);
			break;
		case I_BLEED:
			this.addEffect(Effect.I_BLEED);
			break;
		}
		
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
		this.wash();
		for (Armor a: bag.getArmor()) {
			a.wash();
		}
		bag.getHand().wash();
		if (this.isPlayer()) {
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

}
