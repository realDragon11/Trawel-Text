package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Skill;
import trawel.personal.item.Potion;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.DrawBane.DrawList;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.personal.people.Agent.AgentGoal;
import trawel.quests.BasicSideQuest;
import trawel.quests.QBMenuItem;
import trawel.quests.QRMenuItem;
import trawel.quests.Quest;
import trawel.quests.QuestBoardLocation;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public class WitchHut extends Store implements QuestBoardLocation{
	
	private static final long serialVersionUID = 1L;
	private List<DrawBane> reagents = null;
	private String storename;
	private double timecounter;
	
	private boolean canQuest = true;
	
	private List<Quest> sideQuests = new ArrayList<Quest>();
	
	public WitchHut(String _name, Town t) {
		super(WitchHut.class);
		tier = t.getTier();
		storename = name;
		name = _name;
		tutorialText = "Witch Hut";
		town = t;
		timecounter = 0;
	}
	@Override
	public String getTitle() {
		int amount = playerPotSize();
		return getName() + (amount > 0 ? " ("+amount+" in pot)" : "");
	}
	
	public int playerPotSize() {
		if (reagents == null) {
			return 0;
		}
		return reagents.size();
	}
	
	@Override
	protected String getStoreName() {
		return storename;
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE_MAGIC;
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|hut|Witch Hut|");
		Networking.setArea("shop");
		super.goHeader();
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				if (reagents == null) {
					reagents = new ArrayList<DrawBane>();
				}
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "brew a potion" + (reagents.size() > 0 ? "("+reagents.size()+"/6)" : "");
					}

					@Override
					public boolean go() {
						brew();
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "shop for reagents at '"+storename+"'";
					}

					@Override
					public boolean go() {
						extra.menuGo(modernStoreFront());
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Enter Coven Common Room";
					}

					@Override
					public boolean go() {
						backroom();
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Bottle Potion Runoff";
					}

					@Override
					public boolean go() {
						if (Player.player.hasFlask()) {
							extra.println("Bottling will replace your current potion. It also likely won't be very high quality! Bottle potion runoff?");
						}else {
							extra.println("Potions made from brew waste will be of questionable content. Do it anyway?");
						}
						if (extra.yesNo()) {
							Player.player.setFlask(new Potion(extra.randList(randomQuestionablePotion),8));
							extra.println("You scoop the strange mix of discarded fluid into a glass jar. At least there's a lot of it.");
						}else {
							extra.println("You look away from the goup.");
						}
						return false;
					}});
				if (Player.player.hasFlask() && Player.player.getFlashUses() < 8) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Mix In Potion ("+town.getIsland().getWorld().moneyString(topUpPrice())+")";
						}

						@Override
						public boolean go() {
							extra.println("You can attempt to have the witches here top up your potion, but there's a chance that will ruin it. It will also cost "+town.getIsland().getWorld().moneyString(topUpPrice())+". Pay?");
							if (extra.yesNo()) {
								if (Player.player.canBuyMoneyAmount(topUpPrice())) {
									Player.player.buyMoneyAmount(topUpPrice());
									Player.player.addFlaskUses((byte)3);
									if (extra.chanceIn(1,3)) {
										Player.player.spoilPotion();
									}
									extra.println("The potion bubbles freshly.");
								}else {
									extra.println("You can't afford a refill!");
								}
							}else {
								extra.println("You decide against it.");
							}
							return false;
						}});
				}
				list.add(new MenuBack("leave"));
				return list;
			}});
	}
	
	private int topUpPrice() {
		return (int) getUnEffectiveLevel();
	}

	private void brew() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return Player.player.getFlask() != null ? "You already have a potion, brewing one will replace it." :
							reagents.size() == 0 ? "Time to get brewing!" : reagents.size() == 6 ? "The pot is almost boiling over!" : "The pot bubbles...";
					}});
				if (reagents.size() < 6) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "add drawbane to pot ("+reagents.size()+"/6 already)";
						}

						@Override
						public boolean go() {
							DrawBane inter = Player.bag.discardDrawBanes(true);
							if (inter != null && inter.getCanBrew()) {
								reagents.add(inter);
								Networking.sendStrong("PlayDelay|sound_potionmake"+extra.randRange(1,2)+"|1|");
							}else {
								extra.println("You can't add that!");
							}
							return false;
						}});
				}else {
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "the pot is full (6/6)";
						}});
				}
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "attempt brew of " +listReagents();
					}

					@Override
					public boolean go() {
						boolean ret = finishBrew();
						if (ret) {
							reagents.clear();
						}
						return ret;
					}});
				
				
				list.add(new MenuBack("leave (reagents will remain in this pot)") {});
				return list;
			}});
	}
	
	public boolean finishBrew() {
		if (reagents.size() == 0) {
			extra.println("There's nothing in the pot!");
			return false;
		}
		if (reagents.size() < 3) {
			extra.println("You need at least 3 reagents to finish your brew!");
			return false;
		}
		
		int batWings = (int) reagents.stream().filter(d -> d.equals(DrawBane.BAT_WING)).count();
		int mGuts = (int) reagents.stream().filter(d -> d.equals(DrawBane.MIMIC_GUTS)).count();
		int apples = (int) reagents.stream().filter(d -> d.equals(DrawBane.APPLE)).count();
		int meats = (int) reagents.stream().filter(d -> d.equals(DrawBane.MEAT)).count();
		
		int garlics = (int) reagents.stream().filter(d -> d.equals(DrawBane.GARLIC)).count();
		int woods = (int) reagents.stream().filter(d -> d.equals(DrawBane.WOOD)).count();
		int honeys = (int) reagents.stream().filter(d -> d.equals(DrawBane.HONEY)).count();
		int pumpkins = (int) reagents.stream().filter(d -> d.equals(DrawBane.PUMPKIN)).count();
		int ents = (int) reagents.stream().filter(d -> d.equals(DrawBane.ENT_CORE)).count();
		int waxs = (int) reagents.stream().filter(d -> d.equals(DrawBane.WAX)).count();
		int eggcorns = (int) reagents.stream().filter(d -> d.equals(DrawBane.EGGCORN)).count();
		int truffles = (int) reagents.stream().filter(d -> d.equals(DrawBane.TRUFFLE)).count();
		int eons = (int) reagents.stream().filter(d -> d.equals(DrawBane.CEON_STONE)).count();
		int silvers = (int) reagents.stream().filter(d -> d.equals(DrawBane.SILVER)).count();
		int bloods = (int) reagents.stream().filter(d -> d.equals(DrawBane.BLOOD)).count();
		int virgins = (int) reagents.stream().filter(d -> d.equals(DrawBane.VIRGIN)).count();
		meats += virgins*2;
		int lflames =  (int) reagents.stream().filter(d -> d.equals(DrawBane.LIVING_FLAME)).count();
		int telescopes =  (int) reagents.stream().filter(d -> d.equals(DrawBane.TELESCOPE)).count();
		int gravedusts = (int) reagents.stream().filter(d -> d.equals(DrawBane.GRAVE_DIRT)).count();
		int gravedirts = (int) reagents.stream().filter(d -> d.equals(DrawBane.GRAVE_DUST)).count();
		int grave_subtance = gravedirts*2 + gravedusts;
		int food = meats + apples + garlics + honeys + pumpkins + pumpkins + eggcorns+truffles + (virgins*2);//virgin counts 4x due to meat
		int filler = apples + woods + waxs;
		
		if (Player.hasSkill(Skill.P_BREWER)) {
			filler+=2;
		}
		if (Player.hasSkill(Skill.TOXIC_BREWS)) {
			filler+=1;
		}
		if (ents > 0 && meats > 1) {
			Person fGolem = RaceFactory.getFleshGolem(
					(Player.player.getPerson().getLevel()+town.getTier())/2//near the player and town level
					);
			Combat c = Player.player.fightWith(fGolem);
			if (c.playerWon() < 0) {//if player lost
				//player gets a fleshy friend now :D
				getTown().getIsland().getWorld().addReoccuring(new Agent(fGolem,AgentGoal.SPOOKY));
			}else {
				Player.bag.addNewDrawBane(DrawBane.SINEW);
			}
			return true;
		}
		if (bloods > 0 && virgins > 0) {
			Player.player.setFlask(new Potion(Effect.B_MARY,bloods+virgins+filler));
			actualPotion();
			return true;
		}
		if (eons > 0 && silvers > 0) {
			for (int i = 0; i < silvers;i++) {
				Player.bag.addNewDrawBane(DrawBane.GOLD);
			}
			transmute(DrawBane.SILVER,DrawBane.GOLD,silvers);
			return true;
		}
		if (eons > 0 && woods > 1) {
			Player.bag.addNewDrawBane(DrawBane.ENT_CORE);
			transmute(DrawBane.WOOD,DrawBane.ENT_CORE,1);
			return true;
		}
		if (extra.chanceIn(woods, 10)) {
			Player.player.setFlask(new Potion(Effect.CURSE,1+filler));
			actualPotion();
			return true;
		}
		if (waxs > 0 && honeys > 0) {
			Player.player.setFlask(new Potion(Effect.BEE_SHROUD,honeys+filler));
			actualPotion();
			return true;
		}
		if (mGuts > 0 && telescopes >0) {
			Player.player.setFlask(new Potion(Effect.TELESCOPIC,mGuts+telescopes+filler));
			actualPotion();
			return true;
		}
		if (mGuts > 0 && batWings >0) {
			Player.player.setFlask(new Potion(Effect.R_AIM,batWings+mGuts+filler));
			actualPotion();
			return true;
		}
		if (lflames > 0 && food >=1) {
			Player.player.setFlask(new Potion(Effect.FORGED,lflames+food+filler));
			actualPotion();
			return true;
		}
		if (bloods > 0 && grave_subtance > 1) {
			Player.player.setFlask(new Potion(Effect.CLOTTER,lflames+food+filler));
			actualPotion();
			return true;
		}
		if (food >= 3) {
			Player.player.setFlask(new Potion(Effect.HEARTY,food+filler));
			actualPotion();
			return true;
		}
		if (batWings > 0) {
			Player.player.setFlask(new Potion(Effect.HASTE,batWings+filler));
			actualPotion();
			return true;
		}
		Player.player.setFlask(new Potion(Effect.CURSE,1+filler));
		actualPotion();
		return true;
	}
	
	public void actualPotion() {
		Networking.sendStrong("PlayDelay|sound_potiondone|1|");
		Networking.unlockAchievement("brew1");
		extra.println("You finish brewing your potion, and put it in your flask... time to test it out!");
	}
	
	public void transmute(DrawBane from, DrawBane into,int count) {
		extra.println("You manage to turn the "+from.getName()+" into " + (count > 1 ? count + " " : "") + into.getName()+"!");
	}
	
	public String listReagents() {
		String str = null;
		for (DrawBane d: reagents) {
			if (str == null) {
				str = d.getName();
			}else {
				str += ", " +d.getName();
			}
		}
		return str == null ? "empty" : str;
	}
	
	@Override
	public void init() {
		super.init();
		try {
			while (sideQuests.size() < 3) {
				generateSideQuest();
			}
			}catch (Exception e) {
				canQuest = false;
			}
	}
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		timecounter-=time;
		if (timecounter <= 0) {
			int price = npcPotPrice();
			town.getPersonableOccupants().filter(a -> !a.hasFlask() && a.canBuyMoneyAmount(price)).limit(4)
			.forEach(a -> buyRandomPotion(a,price));
			WitchHut.randomRefillsAtTown(town,tier);
			if (canQuest) {generateSideQuest();}
			timecounter += extra.randRange(20,40);
		}
		return null;
	}
	
	private int npcPotPrice() {
		return (int) (getUnEffectiveLevel()*3);
	}
	
	private void buyRandomPotion(SuperPerson p,int price) {
		p.buyMoneyAmount(price);
		p.setFlask(new Potion(extra.randList(randomPotion),5));
	}
	
	public static final Effect[] randomPotion = new Effect[] {Effect.HEARTY,Effect.BEES,Effect.BEE_SHROUD,Effect.CURSE,Effect.FORGED,Effect.HASTE,Effect.CLOTTER,Effect.R_AIM,Effect.SUDDEN_START} ;
	
	public static final Effect[] randomQuestionablePotion = new Effect[] {
			Effect.CURSE,Effect.CURSE,Effect.BEES,Effect.BLEED,Effect.MAJOR_BLEED,
			Effect.HEARTY,Effect.BEE_SHROUD,Effect.FORGED,Effect.HASTE,
			Effect.CLOTTER,Effect.R_AIM,Effect.SUDDEN_START};
	
	
	public static void randomRefillsAtTown(Town t,int cost) {
		t.getPersonableOccupants().filter(a -> a.wantsRefill() && a.canBuyMoneyAmount(cost)).limit(3).forEach(a -> a.refillWithPrice(cost));
	}
	
	private void backroom() {
		WitchHut hut = this;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				
				for (Quest q: sideQuests) {
					mList.add(new QBMenuItem(q,hut));
				}
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	
	private void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		BasicSideQuest bsq = BasicSideQuest.getWitchCollectQuestPersonal(this.getTown(), this, DrawBane.draw(DrawList.WITCH_STORE));
		if (bsq != null) {
		sideQuests.add(bsq);
		}
	}
	
	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}
	

}
