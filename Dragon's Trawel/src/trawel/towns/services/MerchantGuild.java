package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.extra;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.BasicSideQuest;
import trawel.quests.QBMenuItem;
import trawel.quests.QRMenuItem;
import trawel.quests.Quest;
import trawel.quests.QuestBoardLocation;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;

public class MerchantGuild extends Feature implements QuestBoardLocation {

	private static final long serialVersionUID = 1L;
	
	private Person quarterMaster;
	private boolean canQuest = true;
	
	private double timePassed;
	private int nextReset;
	
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	public MerchantGuild(String name){
		this.name = name;
		tutorialText = "Merchant's Guild.";
		timePassed = extra.randRange(1,30);
		nextReset = extra.randRange(8,30);
	}
	
	@Override
	public String getColor() {
		return extra.F_GUILD;
	}
	
	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|store|Merchant Guild|");
		
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "Current Mechant Reputation: " + Player.player.merchantLevel+ ".";
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						int dCount = Player.bag.getDrawBanes().size();
						return "Donate Drawbanes. (" +(dCount == 0 ? "none)" : dCount+")" );
					}

					@Override
					public boolean go() {
						if (Player.bag.getDrawBanes().isEmpty()) {
							extra.println("You have no drawbanes to donate!");
							return false;
						}
						DrawBane b = null;
						do{
							extra.println("The merchants are willing to take supplies to increase your reputation. (current reputation: " + Player.player.merchantLevel+ ")");
							b = Player.bag.playerDiscardDrawBanes(true);
							if (b != null && b != DrawBane.NOTHING) {
								Player.player.addMPoints(b.getMValue());
							}else {
								b = null;
							}
						}while (b != null);
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						if (Player.player.emeralds == 0) {
							return "Donate Emerald. (none)";
						}
						return "Donate Emerald. ("+Player.player.emeralds+")";
					}

					@Override
					public boolean go() {
						if (Player.player.emeralds > 0) {
							Player.player.addMPoints(10);
							extra.println("You donate an emerald.");
							Player.player.emeralds--;
						}else {
							extra.println("You have no emeralds to donate.");
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Buy shipments with "+World.currentMoneyString()+".";
					}

					@Override
					public boolean go() {
						buyGShip();
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Quest Board.";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> mList = new ArrayList<MenuItem>();
								
								for (Quest q: sideQuests) {
									mList.add(new QBMenuItem(q,MerchantGuild.this));
								}
								for (QuestR qr: qrList) {
									mList.add(new QRMenuItem(qr));
								}
								mList.add(new MenuBack());
								return mList;
							}});
						return false;
					}});
				list.add(new MenuBack("leave"));
				return list;
			}});
	}

	private void buyGShip() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "You have " + Player.showGold();
					}});
				int passes = Player.player.merchantBookPasses;
				//Exponential increase in price, starting at 10, then going to 15, then to 30, then to 55
				int merchantBookPrice = 10+(5*(passes + passes));
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "buy a shipment of books ("+World.currentMoneyDisplay(merchantBookPrice)+")";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() < merchantBookPrice) {
							extra.println("You can't afford that many books!");
							return false;
						}
						extra.println("Buying lots of books might find you a feat fragment- buy?");
						if (extra.yesNo()) {
							Player.player.addGold(-merchantBookPrice);
							//chance of success declines from 3/5ths to 1/2th as the number of passes approaches infinity
							if (passes < 3 || extra.chanceIn(3+passes,5+(2*passes))) {
								Player.player.merchantBookPasses++;
								Player.bag.addNewDrawBanePlayer(DrawBane.KNOW_FRAG);
							}else {
								extra.println("There was nothing interesting in this batch.");
							}
						}
						return false;
					}
				});
				int bShipmentCost = (int) getUnEffectiveLevel()*2;
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "buy a shipment of cheap beer ("+World.currentMoneyDisplay(bShipmentCost) +")";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() < bShipmentCost) {
							extra.println("You can't afford that many beers!");
							return false;
						}
						extra.println("Beer increases your starting HP in battle, one use per beer- buy 20 of them?");
						if (extra.yesNo()) {
							Player.player.addGold(-bShipmentCost);
							Player.player.beer+=20;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "exit";
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
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		//TODO: make certain drawbanes give more depending on what the guild needs
		timePassed += time;
		if (timePassed > nextReset) {
			timePassed = 0;
			nextReset = extra.randRange(8,30);
			if (canQuest) {this.generateSideQuest();}
		}
		return null;
	}
	public Person getQuarterMaster() {
		return quarterMaster;
	}
	
	@Override
	public void init() {
		quarterMaster = RaceFactory.makeQuarterMaster(this.town.getTier());
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
		BasicSideQuest bsq = BasicSideQuest.getRandomMerchantQuest(this.town,this);
		if (bsq != null) {
		sideQuests.add(bsq);
		}
	}
	
	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}


}
