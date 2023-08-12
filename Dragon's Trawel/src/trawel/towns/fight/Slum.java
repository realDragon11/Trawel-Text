package trawel.towns.fight;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.Potion;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
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
import trawel.towns.World;
import trawel.towns.services.Doctor;
import trawel.towns.services.Store;
import trawel.towns.services.WitchHut;

public class Slum extends Feature implements QuestBoardLocation{

	private boolean removable;
	public Agent crimeLord;
	private double timePassed = 0;
	private int crimeRating = 10;
	private int wins = 0;
	
	private boolean canQuest = true;
	
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public QRType getQRType() {
		return QRType.SLUM;
	}

	public Slum(Town t, String name,boolean removable) {
		town = t;
		this.name = name;
		tutorialText = "Slums house crime lords.";
		this.removable = removable;
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;//unsure
	}
	
	@Override
	public void init() {
		try {
			while (sideQuests.size() < 2) {
				generateSideQuest();
			}
			}catch (Exception e) {
				canQuest = false;
			}
	}
	
	private void generateSideQuest() {
		if (sideQuests.size() >= 2) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		BasicSideQuest bsq = BasicSideQuest.getRandomSideQuest(town,this);
		if (bsq != null) {
		sideQuests.add(bsq);
		}
	}
	
	@Override
	public void go() {
		Networking.setArea("dungeon");
		Slum sl = this;
		int removecost = (crimeRating*5)+(town.getTier()*10);
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {
					
					@Override
					public String title() {
						return "hang around (quests)";
					}
	
					@Override
					public boolean go() {
						backroom();
						return false;
					}
				});
				mList.add(new MenuSelect() {
					
					@Override
					public String title() {
						return "backalleys (crime)";
					}
	
					@Override
					public boolean go() {
						crime();
						return false;
					}
				});
				if (crimeLord != null) { 
					mList.add(new MenuSelect() {
						
						@Override
						public String title() {
							return Networking.AGGRO +"attack crime lord";
						}
		
						@Override
						public boolean go() {
							killCrime();
							return false;
						}
					});
				}
				
				if (crimeLord == null && removable) {
					
					mList.add(new MenuSelect() {
						
						@Override
						public String title() {
							return "pay to remove slum ("+World.currentMoneyDisplay(removecost)+")";
						}
		
						@Override
						public boolean go() {
							if (Player.player.getGold() > removecost) {
								extra.println("You pay for the reform programs.");
								Player.player.addGold(-removecost);
								//town.enqueneRemove(sl);//TODO: replace with like 'residental district'
								String formerly = " (formerly '"+sl.getName()+"')";
								switch (extra.randRange(0,3)) {
								case 0: case 1:
									town.laterReplace(sl,new WitchHut("Chemist Business"+formerly,sl.town));
									break;
								case 2:
									town.laterReplace(sl,new Store(sl.town,formerly));
									break;
								case 3:
									town.laterReplace(sl,new Doctor("Medical Clinic"+formerly,sl.town));
									break;
								}
								return true;
							}else {
								extra.println("You can't afford to uplift the slum out of poverty, and no one else who could seems to care.");
							}
							return false;
						}
					});
				}
				mList.add(new MenuBack("leave"));
				return mList;
			}});
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		timePassed-=time;
		if (timePassed < 0) {
			if (canQuest) {this.generateSideQuest();}
			if (crimeLord == null){
				timePassed = 24;
				if (town.getPersonableOccupants().count() == 0) {
					return null;
				}
				crimeLord = town.getRandPersonableOccupant();
				town.removeOccupant(crimeLord);
			}else {
				timePassed = 24;
				if (town.getPersonableOccupants().count() == 0) {
					return null;
				}
				Agent sp = town.getRandPersonableOccupant();
				if (sp.getPerson().getLevel() > crimeLord.getPerson().getLevel() && extra.chanceIn(1,3)) {
					town.addOccupant(crimeLord);
					crimeLord = sp;
					town.removeOccupant(sp);
				}else {
					((Agent)crimeLord).getPerson().getBag().addGold(3*town.getTier());
					crimeRating+=((Agent)crimeLord).getPerson().getLevel();
				}
			}
			
		}
		return null;//TODO: might need events
	}
	
	private void backroom() {
		Slum sl = this;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				
				for (Quest q: sideQuests) {
					mList.add(new QBMenuItem(q,sl));
				}
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	
	private void crime() {
		//Slum sl = this;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "buy experimental potions";
					}

					@Override
					public boolean go() {
						int potionCost = town.getTier();
						extra.println(Player.player.getFlask() != null ? "You already have a potion, buying one will replace it." : "Quality not assured.");
						extra.println("You have "+Player.showGold()+".");
						switch (extra.randRange(1, 3)) {
						case 1:
							extra.println("Buy a low-quality potion? ("+World.currentMoneyDisplay(potionCost)+")");
							if (extra.yesNo()) {
								if (Player.player.getGold() < potionCost) {
									extra.println("You cannot afford this. (You have "+Player.showGold()+".)");
									return false;
								}
								Player.player.addGold(-potionCost);
								if (extra.chanceIn(1, 3)) {
									Player.player.setFlask(new Potion(Effect.CURSE,extra.randRange(2, 3)));
								}else {
									Player.player.setFlask(new Potion(extra.randList(Arrays.asList(Effect.estimEffects)),extra.randRange(2, 3)));	
								}
								extra.println("You buy the potion.");
							}
							break;
						case 2:
							potionCost *=2;
							extra.println("Buy a medium-quality potion? ("+World.currentMoneyDisplay(potionCost)+")");
							if (extra.yesNo()) {
								if (Player.player.getGold() < potionCost) {
									extra.println("You cannot afford this. (You have "+Player.showGold()+".)");
									return false;
								}
								Player.player.addGold(-potionCost);
								if (extra.chanceIn(1, 4)) {
									Player.player.setFlask(new Potion(Effect.CURSE,extra.randRange(3, 4)));
								}else {
									Player.player.setFlask(new Potion(extra.randList(Arrays.asList(Effect.estimEffects)),extra.randRange(3, 4)));	
								}
								extra.println("You buy the potion.");
							}
							break;
						case 3:
							potionCost *=4;
							extra.println("Buy a high-quality potion? ("+World.currentMoneyDisplay(potionCost)+")");
							if (extra.yesNo()) {
								if (Player.player.getGold() < potionCost) {
									extra.println("You cannot afford this. (You have "+Player.showGold()+" gold.)");
									return false;
								}
								Player.player.addGold(-potionCost);
								if (extra.chanceIn(1, 6)) {
									Player.player.setFlask(new Potion(Effect.CURSE,extra.randRange(3, 5)));
								}else {
									Player.player.setFlask(new Potion(extra.randList(Arrays.asList(Effect.estimEffects)),extra.randRange(3, 5)));	
								}
								extra.println("You buy the potion.");
							}
							break;
						}
						return false;
					}
				});
				
				if (crimeRating > 0) {
					mList.add(new MenuSelect() {
	
						@Override
						public String title() {
							return Networking.AGGRO +"go vigilante";
						}
	
						@Override
						public boolean go() {
							extra.println("You wait around and find a mugger.");
							Player.addTime(2);
							
							if (mainGame.CombatTwo(Player.player.getPerson(), RaceFactory.getMugger(town.getTier())).equals(Player.player.getPerson())) {
							//crime rating go down
								crimeRating-=town.getTier();
							}
							return false;
						}
					});
				}
				mList.add(new MenuSelect() {
					
					@Override
					public String title() {
						return Networking.AGGRO +"mug someone";
					}

					@Override
					public boolean go() {
						extra.println("You wait around and find someone to rob.");
						Player.addTime(1);
						
						if (mainGame.CombatTwo(Player.player.getPerson(), RaceFactory.getPeace(town.getTier())).equals(Player.player.getPerson())) {
						//crime rating go down
							crimeRating+=Player.player.getPerson().getLevel();
						}
						return false;
					}
				});
				
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	
	private void killCrime() {
		Person p = ((Agent)crimeLord).getPerson();
		extra.println(Networking.AGGRO +"Attack " + p.getName() + "?");
		if (extra.yesNo()) {
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
			if (winner == Player.player.getPerson()) {
				extra.println("You kill the crime lord!");
				wins++;
				crimeRating -=  ((Agent)crimeLord).getPerson().getLevel()*3;
				crimeLord = null;
			}else {
				extra.println("The crime lord kills you.");
			}
		}
		
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}

}
