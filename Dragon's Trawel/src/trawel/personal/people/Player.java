package trawel.personal.people;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.Story;
import trawel.WorldGen;
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

	private static final long serialVersionUID = 1L;
	private Person person;
	private Boolean isAlive;
	public static Player player;
	public static double passTime;
	public static Inventory bag;
	public int animalQuest;
	public int wins = 0;
	/**
	 * the instance copy of the player's world
	 */
	private World world;
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
	
	public List<Integer> moneys;
	public List<World> moneymappings;
	
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
		
		moneys = new ArrayList<Integer>();
		moneymappings = new ArrayList<World>();
	}
	public static World getWorld() {
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
		if (world != getWorld()) {
			Player.player.world = world;
			extra.mainThreadDataUpdate();
		}
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
			Player.player.getPerson().addSkillPoint();;
			fragmentReq+=2;
			extra.println("+1 skillpoint!");
		}
		
		
	}
	public static int getGold() {
		Player p = player;
		World w = getWorld();
		int index = p.moneymappings.indexOf(w);
		if (index == -1) {
			p.moneymappings.add(w);
			p.moneys.add(0);
			return 0;
		}
		return p.moneys.get(index);
	}
	
	public static void addGold(int delta) {
		Player p = player;
		World w = getWorld();
		int index = p.moneymappings.indexOf(w);
		if (index == -1) {
			p.moneymappings.add(w);
			p.moneys.add(Math.max(0, delta));
			return;
		}
		p.moneys.set(index, Math.max(0,p.moneys.get(index)+delta));
	}
	
	/**
	 * subtracts and prints failure for the caller
	 * @param aether
	 * @param money
	 * @return if bought successfully
	 */
	public static boolean doCanBuy(int aether, int money) {
		int hasMoney = getGold();
		int hasAether = Player.bag.getAether();
		if (hasAether > aether) {
			if (hasMoney < money) {
				extra.println("Not enough " + World.currentMoneyString()+"!");
				return false;
			}else {
				addGold(-money);
				Player.bag.addAether(aether);
				return true;
			}
		}else {
			if (hasMoney < money) {
				extra.println("Not enough aether or " + World.currentMoneyString()+"!");
				return false;
			}else {
				extra.println("Not enough aether!");
				return false;
			}
		}
	}
	
	public static boolean getCanBuy(int aether, int money) {
		int hasMoney = getGold();
		int hasAether = Player.bag.getAether();
		if (hasAether > aether) {
			if (hasMoney < money) {
				extra.println("Not enough " + World.currentMoneyString()+"!");
				return false;
			}else {
				return true;
			}
		}else {
			if (hasMoney < money) {
				extra.println("Not enough aether or " + World.currentMoneyString()+"!");
				return false;
			}else {
				extra.println("Not enough aether!");
				return false;
			}
		}
	}
	
	/**
	 * how much aether converts into normal money
	 */
	public static final float PURE_AETHER_RATE = .1f;
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
	
	public static boolean canBuyMoneyAmount(int money,float aetherRate) {
		int hasMoney = getGold();
		int hasAether = Player.bag.getAether();
		return hasMoney+ (int)(hasAether*aetherRate) >= money;
	}
	
	public static boolean canBuyMoneyAmount(int money) {
		return canBuyMoneyAmount(money,NORMAL_AETHER_RATE);
	}
	
	public static int getTotalBuyPower(float aetherRate) {
		return getGold()+ (int)(Player.bag.getAether()*aetherRate);
	}
	public static int getTotalBuyPower() {
		return getTotalBuyPower(NORMAL_AETHER_RATE);
	}
	
	public static void buyMoneyAmount(int money,float aetherRate) {
		int value = money;
		int gold = getGold();
		if (gold >= value) {
			addGold(-value);
			return;
		}
		value-=gold;
		Player.bag.addAether( -((int)(value/aetherRate)));
	}
	
	public static void buyMoneyAmount(int money) {
		buyMoneyAmount(money,NORMAL_AETHER_RATE);
	}
	public static String showGold() {
		int i = getGold();
		return getWorld().moneyString(i);
	}
	/**
	 * player will lose up to i gold, and this will return how much they lose
	 * <br>
	 * if they were broke will return -1
	 * <br>
	 * 0 will be returned if i == 0
	 * @param i
	 */
	public static int loseGold(int i) {
		if (i == 0) {
			return 0;
		}
		int has = getGold();
		if (has == 0) {
			return -1;
		}
		int lose = Math.max(has,i);
		addGold(-lose);
		return lose;
	}
	
	public static String loseGold(int amount,boolean commentBroke) {
		int lost = loseGold(amount);
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
	
	
}
