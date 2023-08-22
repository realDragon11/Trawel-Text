package trawel.personal.people;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Effect;
import trawel.Networking;
import trawel.Story;
import trawel.WorldGen;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.earts.EArt;
import trawel.earts.EArtBox;
import trawel.factions.FBox;
import trawel.personal.Person;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.item.Inventory;
import trawel.personal.item.Potion;
import trawel.personal.people.Agent.AgentGoal;
import trawel.quests.BasicSideQuest;
import trawel.quests.Quest;
import trawel.quests.Quest.TriggerType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Town;
import trawel.towns.World;

public class Player extends SuperPerson{

	private static final long serialVersionUID = 1L;
	private Person person;
	private Boolean isAlive;
	public static Player player;
	public static double passTime;
	public static Inventory bag;
	public static transient String lastAttackStringer;
	public int wins = 0;
	public boolean cheating = false;
	/**
	 * the instance copy of the player's world
	 */
	private World world;
	private String animalName;
	private boolean tutorial;
	public int merchantLevel = 1;
	public Town lastTown = null;
	private double merchantPoints = 0;
	public int emeralds = 0, rubies = 0, sapphires = 0;
	public float forceRelation = 0.0f;
	public int forceRewardCount = 0;
	public int merchantBookPasses = 0;
	
	public double globalFindTime = 0;
	
	public short beer;

	
	public int knowledgeFragments = 0, fragmentReq = 5;
	
	public List<Quest> sideQuests = new ArrayList<Quest>();
	//public List<EArt> eArts = new ArrayList<EArt>();
	//public EArtBox eaBox = new EArtBox();
	public boolean hasCult = false;
	
	public double townEventTimer = 10;
	
	public FBox factionSpent = new FBox();
	public int launderCredits = 0;
	public float hSpentOnKno = 0f;
	
	public Story storyHold;
	
	private boolean caresAboutCapacity = true, caresAboutAMP = true;
	
	public Player(Person p) {
		person = p;
		flask = null;
		isAlive = true;
		player = this;
		bag = p.getBag();
		passTime = 0;
		animalName = randomLists.randomAnimal();
		//rpts = 0;
		tutorial = true;
		
		moneys = new ArrayList<Integer>();
		moneymappings = new ArrayList<World>();
		
		p.setSuper(this);
	}
	public static World getPlayerWorld() {
		if (Player.player == null) {
			return WorldGen.fallBackWorld;
		}
		return Player.player.world;
	}
	/**
	 * can only be called on main thread
	 * @param world
	 */
	public static void updateWorld(World world) {
		if (world != player.getWorld()) {
			Player.player.world = world;
			extra.mainThreadDataUpdate();
		}
	}
	@Override
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
		Player.player.globalFindTime+=addTime;
	}
	
	@Override
	public List<TimeEvent> passTime(double d, TimeContext caller) {
		return null;
	}
	
	public static void addXp(int amount) {
		player.getPerson().addXp(amount);
		}
	
	public static boolean hasSkill(Skill skill) {
		return player.getPerson().hasSkill(skill);
	}
	public String animalName() {
		return animalName;
	}
	
	/*
	public static void toggleTutorial() {
		player.tutorial = !player.tutorial;
	}*/
	
	public static boolean getTutorial() {
		if (player == null) {return false;}
		return mainGame.doTutorial;
	}

	public void addMPoints(double mValue) {
		merchantPoints+=mValue;
		if (merchantPoints >= merchantLevel*merchantLevel) {
			merchantPoints-=merchantLevel*merchantLevel;
			merchantLevel++;
			addMPoints(0);
		}
	}
	
	@Override
	public Effect doSip() {
		if (flask != null) {
			extra.println("Take a sip of your potion? ("+flask.sips+" left)");
			if (extra.yesNo()) {
				Effect e = flask.effect;
				flask.sip(person);
				Networking.sendStrong("PlayDelay|sound_swallow"+extra.randRange(1,5)+"|1|");
				Networking.unlockAchievement("potion1");
				if (flask.sips <=0) {
					flask = null;
				}
				return e;
			}
		}
		return null;
	}
	public Potion getFlask() {
		return flask;
	}
	public void showQuests() {
		
		
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				
				for (Quest q: sideQuests) {
					mList.add(new ExamineQuest(q));
				}
				mList.add(new MenuBack());
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
					mList.add(new MenuBack());
					return mList;
				}
				
			});
			return false;
		}
		
	}
	/*
	public void addEArt(EArt earta) {
		extra.println("You have chosen an Exotic Art. You may now spend skillpoints on it from the skill menu. You can have a max of 2.");
		this.eArts.add(earta);
		switch (earta) {
		case ARCANIST:
			break;
		case EXECUTIONER:
			break;
		
		}
		
	}*/
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
			Player.player.getPerson().addFeatPoint();
			fragmentReq+=2;
			extra.println("Your knowledge has gained you a feat point!");
		}
	}
	
	public String strKnowFrag() {
		return (Player.player.knowledgeFragments + " of "+Player.player.fragmentReq + " to next knowledge level.");
	}

	
	/**
	 * how much aether converts into normal money
	 */
	public static final float PURE_AETHER_RATE = .02f;
	/**
	 * how much aether converts into normal money at shops
	 * <br>
	 * ie how much they charge for using aether instead of real money
	 */
	public static final float NORMAL_AETHER_RATE = PURE_AETHER_RATE*.75f;
	/**
	 * a multiplier on how much items are worth, but only applies to their trade value, not aether value
	 * <br>
	 * this effectively makes having the money itself better when buying
	 */
	public static final float TRADE_VALUE_BONUS = 4f;
	
	public static String showGold() {
		int i = Player.player.getGold();
		return player.getWorld().moneyString(i);
	}
	
	public static String loseGold(int amount,boolean commentBroke) {
		int lost = Player.player.loseGold(amount);
		if (lost == 0) {
			return "";//no change
		}
		if (lost == -1) {
			return commentBroke ? "Seems like you didn't have anything they wanted to take!" : "" ;
		}
		if (lost < amount) {//now broke
			return "They took all your " + World.currentMoneyString() +"! (lost "+lost+")";
		}else {
			return World.currentMoneyDisplay(lost) + " was stolen!";
		}
	}
	
	/**
	 * for now this is a combattwo, but if you want to check victory conditions you should use the 'playerwon' function
	 * <br>
	 *
	public static Combat playerFightWith(Person p) {
		return mainGame.CombatTwo(Player.player.getPerson(),p, Player.getWorld());
	}*/
	public void setCheating() {
		cheating = true;
	}
	public boolean getCheating() {
		return cheating;
	}
	@Override
	public void setGoal(AgentGoal none) {
		throw new RuntimeException("player cannot take agent goals");
	}
	@Override
	public void onlyGoal(AgentGoal none) {
		throw new RuntimeException("player cannot take agent goals");
	}
	@Override
	public boolean removeGoal(AgentGoal none) {
		throw new RuntimeException("player cannot take agent goals");
	}
	@Override
	public boolean hasGoal(AgentGoal goal) {
		return false;
	}
	public double getFindTime() {
		return player.globalFindTime;
	}
	public void delayFind() {
		player.globalFindTime/=2;//half
		player.globalFindTime-=extra.randRange(10,30);//then minus
		//so it doesn't get really high forever
	}
	@Override
	public boolean everDeathCheated() {
		return true;//the player is the biggest deathcheater of them all
	}
	public static void placeAsOccupant(Person p) {
		Player.player.getWorld().addReoccuring(new Agent(p));
	}
	public boolean caresAboutCapacity() {
		return caresAboutCapacity;
	}
	public void setCaresAboutCapacity(boolean caresAboutCapacity) {
		this.caresAboutCapacity = caresAboutCapacity;
	}
	public boolean caresAboutAMP() {
		return caresAboutAMP;
	}
	public void setCaresAboutAMP(boolean caresAboutAMP) {
		this.caresAboutAMP = caresAboutAMP;
	}
	public static void unlockPerk(Perk perk) {
		Player.player.storyHold.perkTrigger(perk);
		Player.player.getPerson().setPerk(perk);
	}
	
	
}
