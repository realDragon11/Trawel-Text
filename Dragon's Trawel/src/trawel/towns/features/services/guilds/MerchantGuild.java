package trawel.towns.features.services.guilds;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.factions.Faction;
import trawel.factions.FBox.FSub;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.quests.events.QuestReactionFactory.QKey;
import trawel.quests.locations.QBMenuItem;
import trawel.quests.locations.QRMenuItem;
import trawel.quests.locations.QuestBoardLocation;
import trawel.quests.locations.QuestR;
import trawel.quests.types.BasicSideQuest;
import trawel.quests.types.CleanseSideQuest;
import trawel.quests.types.FetchSideQuest;
import trawel.quests.types.KillSideQuest;
import trawel.quests.types.Quest;
import trawel.quests.types.CleanseSideQuest.CleanseType;
import trawel.quests.types.FetchSideQuest.FetchType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.contexts.World;
import trawel.towns.features.Feature;

public class MerchantGuild extends Feature implements QuestBoardLocation {

	private static final long serialVersionUID = 1L;
	
	private Person quarterMaster;
	private boolean canQuest = true;
	
	private double timePassed;
	private int nextReset;
	
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	public MerchantGuild(String _name, int _tier){
		name = _name;
		tier = _tier;
		timePassed = Rand.randRange(1,30);
		nextReset = Rand.randRange(8,30);
	}
	
	@Override
	public QRType getQRType() {
		return QRType.MGUILD;
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_GUILD;
	}
	
	@Override
	public String nameOfFeature() {
		return "Merchant Guild";
	}
	
	@Override
	public String nameOfType() {
		return "Merchant Guild";
	}
	
	@Override
	public Area getArea() {
		return Area.MISC_SERVICE;
	}
	
	@Override
	public void go() {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "Current Merchant Connections: " + Player.player.merchantLevel+ ".";
					}});
				list.add(new MenuLine() {

					@Override
					public String title() {
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.MERCHANT);
						return "Current Merchant Reputation: " + (sub == null ? "Unknown" : ""+Print.format2(sub.forFac-sub.againstFac));
					}
				});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						int dCount = Player.bag.getDrawBanes().size();
						return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Donate Drawbanes. (" +(dCount == 0 ? "none)" : dCount+")" );
					}

					@Override
					public boolean go() {
						if (Player.bag.getDrawBanes().isEmpty()) {
							Print.println("You have no drawbanes to donate!");
							return false;
						}
						DrawBane b = null;
						do{
							Print.println("The merchants are willing to take supplies to increase your reputation. (current reputation: " + Player.player.merchantLevel+ ")");
							b = Player.bag.playerOfferDrawBane("donate");
							if (b != null && b != DrawBane.EV_NOTHING) {
								Player.player.addMPoints(b.getMValue());
								//chance
								DrawBane gain = BasicSideQuest.attemptCollectAlign(QKey.TRADE_ALIGN,(float)b.getMValue()/3f,1);
								if (gain != null) {
									Print.println("You get "+1+" " + gain.getName() + " pieces while trading!");
								}
							}else {
								b = null;
							}
						}while (b != null);
						return false;
					}});
				
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_CURRENCY+"Buy shipments with "+World.currentMoneyString()+".";
					}

					@Override
					public boolean go() {
						buyGShip();
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_QUEST+"Quest Board. (Sidequests)";
					}

					@Override
					public boolean go() {
						Input.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> mList = new ArrayList<MenuItem>();
								
								for (Quest q: sideQuests) {
									mList.add(new QBMenuItem(q,MerchantGuild.this));
								}
								for (QuestR qr: Player.player.QRFor(MerchantGuild.this)) {
									mList.add(new QRMenuItem(qr));
								}
								mList.add(new MenuBack());
								return mList;
							}});
						return false;
					}});
				int gemAmount = Math.round(1.5f*IEffectiveLevel.unclean(tier));
				if (Gem.EMERALD.knowsGem() && Gem.EMERALD.getGem() >= gemAmount) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Donate "+gemAmount+" "+(gemAmount == 1 ? Gem.EMERALD.name : Gem.EMERALD.plural)+". (Have "+Gem.EMERALD.getGem()+")";
						}

						@Override
						public boolean go() {
							if (Gem.EMERALD.getGem() > gemAmount) {
								//scales directly on amount, since the amount scales on eLevel
								Player.player.addMPoints(10*gemAmount);
								Print.println("You donate "+gemAmount+" "+(gemAmount == 1 ? Gem.EMERALD.name : Gem.EMERALD.plural)+".");
								Gem.EMERALD.changeGem(-gemAmount);
								DrawBane gain = BasicSideQuest.attemptCollectAlign(QKey.TRADE_ALIGN,1f,1);
								if (gain != null) {
									Print.println("You get "+1+" " + gain.getName() + " pieces while trading!");
								}
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You have no emeralds to donate.");
							}
							return false;
						}});
				}
				list.add(new MenuBack("Leave."));
				return list;
			}});
	}

	private void buyGShip() {
		Input.menuGo(new MenuGenerator() {

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
						return "Buy a shipment of books. ("+World.currentMoneyDisplay(merchantBookPrice)+")";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() < merchantBookPrice) {
							Print.println(TrawelColor.RESULT_ERROR+"You can't afford that many books!");
							return false;
						}
						Print.println("Buying lots of books might find you a feat fragment- buy?");
						if (Input.yesNo()) {
							Player.player.addGold(-merchantBookPrice);
							//chance of success declines from 3/5ths to 1/2th as the number of passes approaches infinity
							if (passes < 3 || Rand.chanceIn(3+passes,5+(2*passes))) {
								Player.player.merchantBookPasses++;
								Player.bag.addNewDrawBanePlayer(DrawBane.KNOW_FRAG);
							}else {
								Print.println(TrawelColor.RESULT_FAIL+"There was nothing interesting in this batch.");
							}
						}
						return false;
					}
				});
				int bShipmentCost = (int) getUnEffectiveLevel()*2;
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Buy a shipment of cheap beer. ("+World.currentMoneyDisplay(bShipmentCost) +")";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() < bShipmentCost) {
							Print.println(TrawelColor.RESULT_ERROR+"You can't afford that many beers!");
							return false;
						}
						Print.println("Beer increases your starting HP in battle, one use per beer- buy 20 of them?");
						if (Input.yesNo()) {
							Player.player.addGold(-bShipmentCost);
							Player.player.beer+=20;
							Print.println(TrawelColor.RESULT_PASS+"You gain 20 beers.");
						}
						return false;
					}
				});
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		//DOLATER: make certain drawbanes give more depending on what the guild needs
		timePassed += time;
		if (timePassed > nextReset) {
			timePassed = 0;
			nextReset = Rand.randRange(8,30);
			if (canQuest) {this.generateSideQuest();}
		}
		return null;
	}
	public Person getQuarterMaster() {
		return quarterMaster;
	}
	
	@Override
	public void init() {
		super.init();
		quarterMaster = RaceFactory.makeQuarterMaster(this.town.getTier());
	}
	
	@Override
	public void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(Rand.randList(sideQuests));
		}
		switch (Rand.randRange(1,3)) {
		case 1:
			sideQuests.add(FetchSideQuest.generate(this,FetchType.MERCHANT));
			break;
		case 2:
			sideQuests.add(CleanseSideQuest.generate(this,Rand.choose(CleanseType.WOLF,CleanseType.BEAR,CleanseType.HARPY,CleanseType.BANDIT,CleanseType.UNICORN,CleanseType.ANIMALS)));
			break;
		case 3:
			sideQuests.add(KillSideQuest.generate(this,Rand.randFloat() > .7f));//30% chance to be a murder quest
			break;
		}
	}
	
	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}


}
