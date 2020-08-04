package trawel;
import java.util.ArrayList;
import java.util.List;

public class Player extends SuperPerson{
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
	private int rpts;//reincarnatepoints
	private boolean tutorial;
	public int merchantLevel = 1;
	public Town lastTown = null;
	private double merchantPoints = 0;
	public int emeralds = 0;
	private Potion flask;
	
	public ArrayList<Quest> sideQuests = new ArrayList<Quest>();
	
	public Player(Person p) {
		person = p;
		flask = null;
		isAlive = true;
		player = this;
		bag = p.getBag();
		passTime = 0;
		animalQuest = -1;//not started unless another quest starts it
		animalName = randomLists.randomAnimal();
		rpts = 0;
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
		return temp;
	}
	public static void addTime(double addTime) {
		passTime +=addTime;
	}
	
	@Override
	public void passTime(double d) {}
	
	public static void addXp(int amount) {
		player.getPerson().addXp(amount);
		}
	
	
	public static void showMap1() {
		world.displayMap1();
	}
	public static boolean hasSkill(Skill skill) {
		return player.getPerson().getSkills().contains(skill);
	}
	public String animalName() {
		return animalName;
	}
	public void reincarnate() {
		rpts += this.person.getLevel()-1;
		person = new Person(1);
		person.setSkillPoints(rpts);
		Player.bag = person.getBag();
		person.setPlayer(); 
		
	}
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
	
}
