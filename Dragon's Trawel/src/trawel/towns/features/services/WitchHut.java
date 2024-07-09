package trawel.towns.features.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.battle.Combat;
import trawel.core.Networking;
import trawel.core.Networking.Area;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Skill;
import trawel.personal.item.Potion;
import trawel.personal.item.Seed;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.DrawBane.DrawList;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.events.QuestReactionFactory.QKey;
import trawel.quests.locations.QBMenuItem;
import trawel.quests.locations.QRMenuItem;
import trawel.quests.locations.QuestBoardLocation;
import trawel.quests.locations.QuestR;
import trawel.quests.types.BasicSideQuest;
import trawel.quests.types.CollectSideQuest;
import trawel.quests.types.Quest;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.contexts.Town;

public class WitchHut extends Store implements QuestBoardLocation{
	
	private static final long serialVersionUID = 1L;
	private List<DrawBane> reagents = null;
	private String storename;
	private double timecounter;
	
	private boolean canQuest = true;
	
	private List<Quest> sideQuests = new ArrayList<Quest>();
	
	private static boolean actualPotionMade = false;
	
	public WitchHut(String _name, Town t) {
		super(t.getTier(),WitchHut.class);
		tier = t.getTier();
		storename = name;
		name = _name;
		tutorialText = "Witch Hut";
		town = t;
		timecounter = 0;
		area_type = Area.MISC_SERVICE;
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
		return TrawelColor.F_SERVICE_MAGIC;
	}
	
	@Override
	public void go() {
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
						return TrawelColor.SERVICE_FREE+"Brew a potion" + (reagents.size() > 0 ? " ("+reagents.size()+"/6)" : "");
					}

					@Override
					public boolean go() {
						brew();
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_CURRENCY+"Shop for reagents at '"+storename+"'.";
					}

					@Override
					public boolean go() {
						extra.menuGo(modernStoreFront());
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.FSERVICE_QUEST+"Enter Coven Common Room (Sidequests).";
					}

					@Override
					public boolean go() {
						backroom();
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_FREE+"Bottle Potion Runoff.";
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
				if (Player.player.hasFlask() && Player.player.getFlaskUses() < 8) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_CURRENCY+"Mix In Potion ("+town.getIsland().getWorld().moneyString(topUpPrice())+")";
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
									}else {
										Player.player.muddyPotion();//don't spoil, but they can't tell
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
				list.add(new MenuBack("Leave."));
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
						return Player.player.getFlask() != null ? TrawelColor.RESULT_WARN+"You already have a potion, brewing one will replace it." :
							reagents.size() == 0 ? "Time to get brewing!" : reagents.size() == 6 ? "The pot is almost boiling over!" : "The pot bubbles...";
					}});
				if (reagents.size() < 6) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Add drawbane to pot ("+reagents.size()+"/6 already)";
						}

						@Override
						public boolean go() {
							DrawBane inter = Player.bag.playerOfferDrawBane("mix in");
							if (inter != null && inter.getCanBrew()) {
								reagents.add(inter);
								Networking.sendStrong("PlayDelay|sound_potionmake"+extra.randRange(1,2)+"|1|");
							}else {
								Player.bag.giveBackDrawBane(inter,"%s isn't brewable!");
							}
							return false;
						}});
				}else {
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "The pot is full (6/6)";
						}});
				}
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Attempt brew of " +listReagents();
					}

					@Override
					public boolean go() {
						actualPotionMade = false;
						boolean ret = finishBrew();
						if (ret) {
							reagents.clear();
							//to make it harder to figure out what you just made without trying it out or with a skill
							//mess up the count of how many charges you have slightly
							if (Player.player.hasFlask() && actualPotionMade) {
								if (Player.player.getFlaskUses() < 3) {
									Player.player.addFlaskUses((byte) extra.randRange(1,3));
								}else {
									Player.player.addFlaskUses((byte) extra.randRange(-1,4));
								}
							}
						}
						return ret;
					}});
				
				
				list.add(new MenuBack("Stop Brewing. (reagents will remain in this pot)") {});
				return list;
			}});
	}
	
	public boolean finishBrew() {
		if (reagents.size() == 0) {
			extra.println(TrawelColor.RESULT_ERROR+"There's nothing in the pot!");
			return false;
		}
		if (reagents.size() < 3) {
			extra.println(TrawelColor.RESULT_ERROR+"You need at least 3 reagents to finish your brew!");
			return false;
		}
		int batWings = 0;
		int mGuts = 0;
		int apples = 0;
		int meats = 0;
		
		int garlics = 0;
		int woods = 0;
		int honeys = 0;
		int pumpkins = 0;
		int ents = 0;
		int waxs = 0;
		int eggcorns = 0;
		int truffles = 0;
		int eons = 0;
		int silvers = 0;
		int bloods = 0;
		int virgins = 0;
		
		int lflames =  0;
		int telescopes = 0;
		int gravedusts = 0;
		int gravedirts = 0;
		int uhorns = 0;
		int sinews = 0;
		for (DrawBane d: reagents) {
			switch (d) {
			case BAT_WING:
				batWings++;
				break;
			case MIMIC_GUTS:
				mGuts++;
				break;
			case APPLE:
				apples++;
				break;
			case MEAT:
				meats++;
				break;
			case GARLIC:
				garlics++;
				break;
			case WOOD:
				woods++;
				break;
			case HONEY:
				honeys++;
				break;
			case PUMPKIN:
				pumpkins++;
				break;
			case ENT_CORE:
				ents++;
				break;
			case WAX:
				waxs++;
				break;
			case EGGCORN:
				eggcorns++;
				break;
			case TRUFFLE:
				truffles++;
				break;
			case CEON_STONE:
				eons++;
				break;
			case SILVER:
				silvers++;
				break;
			case BLOOD:
				bloods++;
				break;
			case VIRGIN:
				virgins++;
				break;
			case LIVING_FLAME:
				lflames++;
				break;
			case TELESCOPE:
				telescopes++;
				break;
			case GRAVE_DIRT:
				gravedirts++;
				break;
			case GRAVE_DUST:
				gravedusts++;
				break;
			case UNICORN_HORN:
				uhorns++;
				break;
			case SINEW:
				sinews++;
				break;
			}
		}
		
		
		meats += (virgins*3)+(sinews/2);
		bloods += meats;
		int botch = woods;
		int filler = apples + woods + waxs;
		int foodless_filler = woods + waxs;//does not add in filler that are food, to inflate food sizes less
		woods += ents;//ent core doesn't add botch/filler but does add normal wood
		int grave_subtance = gravedirts*2 + gravedusts;
		int food = meats + apples + garlics + honeys + (2*pumpkins) + eggcorns+truffles + virgins;//virgin counts 4x due to meat
		int total = reagents.size();
		
		if (Player.hasSkill(Skill.P_BREWER)) {
			filler+=2;
		}
		if (Player.hasSkill(Skill.TOXIC_BREWS)) {
			filler+=1;
		}
		//section used for transmutation
		if ((ents > 0 && meats >= 2) || (eons > 0 && virgins > 0)) {//kinda even more horrifying now
			transmuteMisc();
			Person fGolem = RaceFactory.makeFleshGolem(
					(Player.player.getPerson().getLevel()+town.getTier())/2//near the player and town level
					);
			extra.println(TrawelColor.PRE_BATTLE+"The meat wraps around the heartwood and contorts into a humanoid shape... which then attacks you!");
			Combat c = Player.player.fightWith(fGolem);
			if (c.playerWon() < 0) {//if player lost
				//player gets a fleshy friend now :D
				getTown().getIsland().getWorld().addReoccuring(new Agent(fGolem,AgentGoal.SPOOKY));
			}else {
				
			}
			Player.bag.addNewDrawBanePlayer(DrawBane.SINEW);
			//you get sinew even if you lose. Note that the golem has a beating heart drawbane always
			return true;
		}
		if (eons > 0) {
			if (ents > 0 && uhorns > 0) {
				int make_pwards = Math.min(ents,uhorns);
				transmute("materials",DrawBane.PROTECTIVE_WARD,make_pwards);
				for (int i = 0; i < make_pwards;i++) {
					Player.bag.addNewDrawBanePlayer(DrawBane.PROTECTIVE_WARD);
				}
				return true;
			}
			if (silvers > 0) {
				transmute(DrawBane.SILVER,DrawBane.GOLD,silvers);
				for (int i = 0; i < silvers;i++) {
					Player.bag.addNewDrawBanePlayer(DrawBane.GOLD);
				}
				return true;
			}
			if (woods >= 2) {
				transmute(DrawBane.WOOD,DrawBane.ENT_CORE,1);
				Player.bag.addNewDrawBanePlayer(DrawBane.ENT_CORE);
				return true;
			}
			if (food >= 2) {
				transmuteMisc();
				extra.println("The food morphs!");
				for (int left = food-1; left >= 0 ; left--) {
					if (extra.chanceIn(3,4)) {//make a seeded plant
						switch (extra.randRange(0,3)) {
						case 0:
							//can only make pumpkin with 2 'value', otherwise makes an apple
							if (left > 0) {
								Player.bag.addNewDrawBanePlayer(DrawBane.PUMPKIN);
								Player.bag.addSeed(Seed.SEED_PUMPKIN);
								left--;
								break;
							}
						case 1:
							Player.bag.addNewDrawBanePlayer(DrawBane.APPLE);
							Player.bag.addSeed(Seed.SEED_APPLE);
							break;
						case 2:
							Player.bag.addNewDrawBanePlayer(DrawBane.GARLIC);
							Player.bag.addSeed(Seed.SEED_GARLIC);
							break;
						case 3:
							Player.bag.addNewDrawBanePlayer(DrawBane.EGGCORN);
							Player.bag.addSeed(Seed.SEED_EGGCORN);
							break;
						case 4:
							Player.bag.addNewDrawBanePlayer(DrawBane.TRUFFLE);
							Player.bag.addSeed(Seed.SEED_TRUFFLE);
							break;
						}
					}else {
						if (extra.chanceIn(1,4)) {//make a bee with honey
							Player.bag.addNewDrawBanePlayer(DrawBane.HONEY);
							Player.bag.addSeed(Seed.SEED_BEE);
						}else {
							//else, make meat
							Player.bag.addNewDrawBanePlayer(DrawBane.MEAT);
						}
					}
				}
				return true;
			}
		}
		
		//transmutation ended, roll botching
		if (extra.chanceIn(botch, 10)) {
			Player.player.setFlask(new Potion(Effect.CURSE,1+filler));
			actualPotion();
			return true;
		}
		
		//priority potions
		if (bloods > 0 && virgins > 0) {//note that a ceon stone will prevent this because virgins are food, not friends :D
			Player.player.setFlask(new Potion(Effect.B_MARY,bloods+virgins+filler));
			actualPotion();
			return true;
		}
		
		//this section is for normal potions which have food ingredients, which should bypass the 'stew chance'
		if (waxs > 0 && honeys > 0) {
			Player.player.setFlask(new Potion(Effect.BEE_SHROUD,honeys+filler));
			actualPotion();
			return true;
		}
		if (lflames > 0 && food >=1) {
			Player.player.setFlask(new Potion(Effect.FORGED,lflames+food+filler));
			actualPotion();
			return true;
		}
		if (truffles > 0 && gravedirts >= 1) {
			//note that it requires dirt not dust, to make it more distinct from grave armor
			Player.player.setFlask(new Potion(Effect.STERN_STUFF,truffles+grave_subtance+filler));
			actualPotion();
			return true;
		}
		//'stew chance', any potion with food as an ingredient that isn't above
		//might turn into HEARTY even if it would be something else
		if (food >= 1 && extra.chanceIn(food,20)) {
			Player.player.setFlask(new Potion(Effect.HEARTY,(total+food+foodless_filler) * (Player.hasSkill(Skill.CHEF) ? 2 : 1)));
			actualPotion();
			return true;
		}
		
		//section used for normal potions
		if (lflames > 0 && gravedusts > 0) {
			//note it requires DUST to start but can use all grave substances for amount, which includes 2*dirt
			Player.player.setFlask(new Potion(Effect.SIP_GRAVE_ARMOR,lflames+grave_subtance+filler));
			actualPotion();
			return true;
		}
		if (bloods > 0 && grave_subtance >= 2) {
			Player.player.setFlask(new Potion(Effect.CLOTTER,bloods+grave_subtance+filler));
			actualPotion();
			return true;
		}
		if (batWings > 0 && telescopes > 0) {
			Player.player.setFlask(new Potion(Effect.TELESCOPIC,batWings+telescopes+filler));
			actualPotion();
			return true;
		}
		if (mGuts > 0 && telescopes > 0) {
			Player.player.setFlask(new Potion(Effect.R_AIM,telescopes+mGuts+filler));
			actualPotion();
			return true;
		}
		if (mGuts > 0 && batWings > 0) {
			Player.player.setFlask(new Potion(Effect.SUDDEN_START,mGuts+batWings+filler));
			actualPotion();
			return true;
		}
		//note that 2 or more sinews would run a risk of becoming food
		if (batWings > 0 && sinews > 0) {
			Player.player.setFlask(new Potion(Effect.HASTE,batWings+sinews+filler));
			actualPotion();
			return true;
		}
		
		//final chance for a normal stew
		//if not all reagents are food, chance of failure, unless the ones that are food are the 'heavier' foods
		if (food > 0 && extra.chanceIn(food,total)) {
			Player.player.setFlask(new Potion(Effect.HEARTY,(total+food+foodless_filler) * (Player.hasSkill(Skill.CHEF) ? 2 : 1)));
			actualPotion();
			return true;
		}
		
		//but your stew was actually BEEEEES
		if (honeys > 0) {
			Player.player.setFlask(new Potion(Effect.BEES,total+food+foodless_filler));
			actualPotion();
			return true;
		}
		
		//failed all, so curse
		Player.player.setFlask(new Potion(Effect.CURSE,1+total+filler));
		actualPotion();
		return true;
	}
	
	public void actualPotion() {
		Networking.sendStrong("PlayDelay|sound_potiondone|1|");
		Networking.unlockAchievement("brew1");
		//small chance on brewing an actual potion to collect brew aligned collect quest items
		//will indicate it was an actual brew, but, probably doesn't matter as much
		DrawBane gain = BasicSideQuest.attemptCollectAlign(QKey.BREW_ALIGN,.1f,1);
		if (gain != null) {
			extra.println("You find "+1+" " + gain.getName() + " pieces while brewing!");
		}
		extra.println("You finish brewing your potion, and put it in your flask... now to test it out!");
		actualPotionMade = true;
	}
	
	public void transmute(DrawBane from, DrawBane into,int count) {
		transmuteMisc();
		extra.println("You manage to turn the "+from.getName()+" into " + (count > 1 ? count + " " : "") + into.getName()+"!");
	}
	public void transmute(String from, DrawBane into,int count) {
		transmuteMisc();
		extra.println("You manage to turn the "+from+" into " + (count > 1 ? count + " " : "") + into.getName()+"!");
	}
	
	public void transmuteMisc() {
		Networking.unlockAchievement("transmute1");
		//higher chance since transmutations are harder to activate
		DrawBane gain = BasicSideQuest.attemptCollectAlign(QKey.TRANSMUTE_ALIGN,.3f,1);
		if (gain != null) {
			extra.println("You find "+1+" " + gain.getName() + " pieces while brewing!");
		}
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
			addAnItem();
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
	
	public static final Effect[] randomPotion = new Effect[] {
			Effect.HEARTY,Effect.BEES,Effect.BEE_SHROUD,Effect.CURSE,Effect.FORGED,
			Effect.HASTE,Effect.CLOTTER,Effect.R_AIM,Effect.SUDDEN_START
		};
	
	public static final Effect[] randomQuestionablePotion = new Effect[] {
			Effect.CURSE,Effect.CURSE,Effect.BEES,Effect.BLEED,Effect.MAJOR_BLEED,
			Effect.HEARTY,Effect.BEE_SHROUD,Effect.FORGED,Effect.HASTE,
			Effect.CLOTTER,Effect.R_AIM,Effect.SUDDEN_START
		};
	
	
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
				for (QuestR qr: Player.player.QRFor(WitchHut.this)) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	
	@Override
	public void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		sideQuests.add(CollectSideQuest.generate(this, DrawBane.draw(DrawList.WITCH_STORE)));
	}
	
	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}
	

}
