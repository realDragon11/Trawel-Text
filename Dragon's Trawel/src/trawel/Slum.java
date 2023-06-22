package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Slum extends Feature implements QuestBoardLocation{

	private boolean removable;
	public SuperPerson crimeLord;
	private double timePassed = 0;
	private Town town;
	private int crimeRating = 10;
	private int wins = 0;
	
	private boolean canQuest = true;
	
	public ArrayList<Quest> sideQuests = new ArrayList<Quest>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public QRType getQRType() {
		return QRType.SLUM;
	}

	public Slum(Town t, String name,boolean removable) {
		town = t;
		this.name = name;
		tutorialText = "Slums house crime lords.";
		color = Color.RED;
		this.removable = removable;
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
		int removecost = Math.max(100, (crimeRating*50)+(town.getTier()*1000));
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
							return "pay to remove slum ("+removecost+" gold)";
						}
		
						@Override
						public boolean go() {
							if (Player.bag.getGold() > removecost) {
								extra.println("You pay for the reform programs.");
								Player.bag.modGold(-removecost);
								town.enqueneRemove(sl);
								return true;
							}else {
								extra.println("You can't afford to uplift the slum out of poverty.");
							}
							return false;
						}
					});
				}
				mList.add(new MenuSelect() {
	
					@Override
					public String title() {
						return "leave";
					}
	
					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}});
	}

	@Override
	public void passTime(double time) {
		timePassed-=time;
		if (timePassed < 0) {
			if (canQuest) {this.generateSideQuest();}
			if (crimeLord == null){
				crimeLord = extra.randList(town.getOccupants());
				town.getOccupants().remove(crimeLord);
				timePassed = 24;
			}else {
				SuperPerson sp = extra.randList(town.getOccupants());
				if (((Agent)sp).getPerson().getLevel() > ((Agent)crimeLord).getPerson().getLevel() && extra.chanceIn(1,3)) {
					town.getOccupants().add(crimeLord);
					crimeLord = sp;
					town.getOccupants().remove(sp);
					timePassed = 24;
				}else {
					((Agent)crimeLord).getPerson().getBag().modGold(100*town.getTier());
					crimeRating+=((Agent)crimeLord).getPerson().getLevel();
				}
			}
			
		}

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
						int potionCost = 50*town.getTier();
						extra.println(Player.player.getFlask() != null ? "You already have a potion, buying one will replace it." : "Quality not assured.");
						
						switch (extra.randRange(1, 3)) {
						case 1:
							extra.println("Buy a low-quality potion? ("+potionCost+" gold)");
							if (extra.yesNo()) {
								if (Player.bag.getGold() < potionCost) {
									extra.println("You cannot afford this. (You have "+Player.bag.getGold()+" gold.)");
									return false;
								}
								Player.bag.modGold(-potionCost);
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
							extra.println("Buy a medium-quality potion? ("+(potionCost)+" gold)");
							if (extra.yesNo()) {
								if (Player.bag.getGold() < potionCost) {
									extra.println("You cannot afford this. (You have "+Player.bag.getGold()+" gold.)");
									return false;
								}
								Player.bag.modGold(-potionCost);
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
							extra.println("Buy a high-quality potion? ("+(potionCost)+" gold)");
							if (extra.yesNo()) {
								if (Player.bag.getGold() < potionCost) {
									extra.println("You cannot afford this. (You have "+Player.bag.getGold()+" gold.)");
									return false;
								}
								Player.bag.modGold(-potionCost);
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
