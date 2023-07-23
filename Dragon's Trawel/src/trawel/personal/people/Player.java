package trawel.personal.people;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.Story;
import trawel.extra;
import trawel.randomLists;
import trawel.earts.EArt;
import trawel.earts.EArtBox;
import trawel.factions.FBox;
import trawel.personal.Person;
import trawel.personal.item.Inventory;
import trawel.personal.item.Potion;
import trawel.quests.BasicSideQuest;
import trawel.quests.Quest;
import trawel.quests.Quest.TriggerType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Town;
import trawel.towns.World;

public class Player extends SuperPerson{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Person person;
	private Boolean isAlive;
	public static Player player;
	public static double passTime;
	public static Inventory bag;
	public int animalQuest;
	public int wins = 0;
	public static World world;
	public World world2;
	private String animalName;
	//private int rpts;//reincarnatepoints
	private boolean tutorial;
	public int merchantLevel = 1;
	public Town lastTown = null;
	private double merchantPoints = 0;
	public int emeralds = 0, rubies = 0, sapphires = 0;
	private Potion flask;
	public float forceRelation = 0.0f;
	public int forceRewardCount = 0;
	public int merchantBookPrice = 1000;
	
	public int knowledgeFragments = 0, fragmentReq = 5;
	
	public ArrayList<Quest> sideQuests = new ArrayList<Quest>();
	public List<EArt> eArts = new ArrayList<EArt>();
	public EArtBox eaBox = new EArtBox();
	public boolean hasCult = false;
	
	public double townEventTimer = 10;
	
	public FBox factionSpent = new FBox();
	public int launderCredits = 0;
	public float hSpentOnKno = 0f;
	
	public Story storyHold;
	
	public Player(Person p) {
		person = p;
		flask = null;
		isAlive = true;
		player = this;
		bag = p.getBag();
		passTime = 0;
		animalQuest = -1;//not started unless another quest starts it
		animalName = randomLists.randomAnimal();
		//rpts = 0;
		tutorial = true;
	}
	public Person getPerson() {
		return person;
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void kill() {
		isAlive = false;
	}
	public static double popTime() {
		double temp = passTime;
		passTime = 0;
		Player.player.townEventTimer-=temp;
		return temp;
	}
	public static void addTime(double addTime) {
		passTime +=addTime;
	}
	
	@Override
	public List<TimeEvent> passTime(double d, TimeContext caller) {
		return null;
	}
	
	public static void addXp(int amount) {
		player.getPerson().addXp(amount);
		}
	
	public static boolean hasSkill(Skill skill) {
		return player.getPerson().getSkills().contains(skill);
	}
	public String animalName() {
		return animalName;
	}
	/*
	public void reincarnate() {
		rpts += this.person.getLevel()-1;
		person = new Person(1);
		person.setSkillPoints(rpts);
		Player.bag = person.getBag();
		person.setPlayer(); 
		
	}*/
	public static void addSkill(Skill skill) {
		if (!Player.hasSkill(skill)) {
		player.getPerson().getSkills().add(skill);
	
		}
	}
	
	public static void toggleTutorial() {
		player.tutorial = !player.tutorial;
	}
	
	public static boolean getTutorial() {
		if (player == null) {return false;}
		return player.tutorial;
	}
	public static ArrayList<Person> list() {
		ArrayList<Person> list = new ArrayList<Person>();
		list.add(Player.player.getPerson());
		return list;
	}
	public void addMPoints(double mValue) {
		merchantPoints+=mValue;
		if (merchantPoints >= merchantLevel*merchantLevel) {
			merchantPoints-=merchantLevel*merchantLevel;
			merchantLevel++;
			addMPoints(0);
			}
		
	}
	
	public void doSip() {
		if (flask != null) {
			extra.println("Take a sip of your potion? ("+flask.sips+" left)");
			if (extra.yesNo()) {
				flask.sip(person);
				Networking.sendStrong("PlayDelay|sound_swallow"+extra.randRange(1,5)+"|1|");
				Networking.sendStrong("Achievement|potion1|");
				if (flask.sips <=0) {
					flask = null;
				}
			}
		}
	}
	public Potion getFlask() {
		return flask;
	}
	
	public void setFlask(Potion p) {
		flask = p;
	}
	public void showQuests() {
		
		
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				
				for (Quest q: sideQuests) {
					mList.add(new ExamineQuest(q));
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}});
		
	}
	
	public class AbandonQuest extends MenuSelect {
		Quest quest;
		public AbandonQuest(Quest q) {
			quest = q;
		}
		@Override
		public String title() {
			return "Abandon";
		}

		@Override
		public boolean go() {
			quest.fail();
			sideQuests.remove(quest);
			return true;
		}
		
	}
	public class ExamineQuest extends MenuSelect {
		Quest quest;
		public ExamineQuest(Quest q) {
			quest = q;
		}
		@Override
		public String title() {
			return quest.name();
		}

		@Override
		public boolean go() {
			extra.println(quest.name() + ":");
			extra.println(quest.desc());
			
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> mList = new ArrayList<MenuItem>();
					mList.add(new AbandonQuest(quest));
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "back";
						}

						@Override
						public boolean go() {
							return true;
						}
					});
					return mList;
				}
				
			});
			return false;
		}
		
	}
	public void addEArt(EArt earta) {
		extra.println("You have chosen an Exotic Art. You may now spend skillpoints on it from the skill menu. You can have a max of 2.");
		this.eArts.add(earta);
		switch (earta) {
		case ARCANIST:
			break;
		case EXECUTIONER:
			break;
		
		}
		
	}
	public void questTrigger(TriggerType type, String string, int count) {
		for (Quest q: sideQuests) {
			q.questTrigger(type,string, count);
		}
		
	}
	
	/**
	 * do not use if not needed
	 * @param type
	 * @param string
	 * @return
	 */
	public BasicSideQuest anyQTrigger(TriggerType type, String string) {
		for (Quest q: sideQuests) {
			if (q instanceof BasicSideQuest) {
				if (((BasicSideQuest) q).trigger == string) {
					return ((BasicSideQuest) q);
				}
			}
		}
		return null;
	}
	
	public List<String> allQTriggers() {
		List<String> ts = new ArrayList<String>();
		for (Quest q: sideQuests) {
			ts.addAll(q.triggers());
		}
		return ts;
	}
	
	public boolean hasTrigger(String string) {
		for (Quest q: sideQuests) {
			if (q.triggers().contains(string)) {
				return true;
			}
		}
		return false;
	}
	
	public void addKnowFrag() {
		if (++this.knowledgeFragments >= this.fragmentReq) {
			knowledgeFragments-=fragmentReq;
			Player.player.getPerson().setSkillPoints(Player.player.getPerson().getSkillPoints()+1);
			fragmentReq+=2;
			extra.println("+1 skillpoint!");
		}
		
		
	}
	
	
}
