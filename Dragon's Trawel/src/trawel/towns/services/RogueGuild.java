package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.factions.FBox;
import trawel.factions.FBox.FSub;
import trawel.factions.Faction;
import trawel.personal.people.Player;
import trawel.quests.CleanseSideQuest;
import trawel.quests.FetchSideQuest;
import trawel.quests.KillSideQuest;
import trawel.quests.QBMenuItem;
import trawel.quests.QRMenuItem;
import trawel.quests.Quest;
import trawel.quests.QuestBoardLocation;
import trawel.quests.QuestR;
import trawel.quests.CleanseSideQuest.CleanseType;
import trawel.quests.FetchSideQuest.FetchType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;
import trawel.towns.Feature.QRType;

public class RogueGuild extends Feature implements QuestBoardLocation{
	
	private static final long serialVersionUID = 1L;
	
	private double activityTimer;
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	private boolean canQuest = true;
	
	public RogueGuild(String _name, int _tier){
		name = _name;
		tier = _tier;
		tutorialText = "Rogue's Guild";
		area_type = Area.MISC_SERVICE;
		activityTimer = 24f+extra.randFloat()*24f;
	}
	
	@Override
	public QRType getQRType() {
		return QRType.RGUILD;
	}
	
	@Override
	public String getColor() {
		return extra.F_GUILD;
	}
	
	@Override
	public void go() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.ROGUE);
						return "Current Rogue Reputation: " + (sub == null ? "Unknown" : ""+extra.format2(sub.forFac-sub.againstFac));
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Launder Gems.";
					}

					@Override
					public boolean go() {
						gemLaunder();
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Request Sapphires";
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 25;
						float spenda = FBox.getSpendableFor(Player.player.getPerson().facRep.getFacRep(Faction.ROGUE));
						extra.println("Request a sapphire? cost: " +cost + "/"+extra.format2(spenda));
						if (extra.yesNo()) {
							if (cost <= spenda) {
								Player.player.factionSpent.addFactionRep(Faction.ROGUE,cost,0);
								Player.player.sapphires++;
							}else {
								extra.println("You do not have enough spendable reputation.");
								break;
							}
						}else {
							break;
						}
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Donate a Sapphire.";
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 10;
						extra.println("Donate a sapphire? You have " + Player.player.sapphires);
						if (extra.yesNo()) {
							if (Player.player.sapphires > 0) {
								Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,cost,0);
								Player.player.sapphires--;
							}else {
								extra.println("You do not have any sapphires.");
								break;
							}
						}else {
							break;
						}
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Job Board (Sidequests).";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list = new ArrayList<MenuItem>();
								
								for (Quest q: sideQuests) {
									list.add(new QBMenuItem(q,RogueGuild.this));
								}
								for (QuestR qr: Player.player.QRFor(RogueGuild.this)) {
									list.add(new QRMenuItem(qr));
								}
								list.add(new MenuBack());
								return list;
							}});
						return false;
					}});
				mList.add(new MenuBack("Leave."));
				return mList;
			}});
	}
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (canQuest) {
			activityTimer-=time;
			if (activityTimer <= 0) {
				activityTimer+=12f+(36f*extra.randFloat());
				generateSideQuest();
			}
		}
		return null;
	}
	
	public void gemLaunder() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.ROGUE);
						return "current reputation: " + (sub == null ? "Unknown" : ""+extra.format2(sub.forFac-sub.againstFac));
					}
				});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "current money: " + Player.showGold();
					}
				});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "current gems: (ERS)" + Player.player.emeralds + " " + Player.player.rubies + " " + Player.player.sapphires;
					}
				});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "conversion credits: " + Player.player.launderCredits;
					}
				});
				int cost = 20;
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "buy a launder credit for "+cost+" " + World.currentMoneyString();
					}

					@Override
					public boolean go() {
						while (true) {
						
						extra.println("Buy a launder credit? cost: " +cost + "/"+Player.player.getGold());
						if (extra.yesNo()) {
							if (cost <= Player.player.getGold()) {
								Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,0.2f,0);
								Player.player.launderCredits++;
								Player.player.addGold(-cost);
							}else {
								extra.println("You cannnot afford a credit.");
								break;
							}
						}else {
							break;
						}
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "buy a launder credit for 2 rogue rep";
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 2;
						float spenda = FBox.getSpendableFor(Player.player.getPerson().facRep.getFacRep(Faction.ROGUE));
						extra.println("Buy a launder credit? cost: " +cost + "/"+extra.format2(spenda));
						if (extra.yesNo()) {
							if (cost <= spenda) {
								Player.player.factionSpent.addFactionRep(Faction.ROGUE,cost,0);
								Player.player.launderCredits++;
							}
						}else {
							extra.println("You do not have enough spendable reputation.");
							break;
						}
						}
						return false;
					}
				});
				for (GemType g1: GemType.values()) {
					for (GemType g2: GemType.values()) {
						if (g1 == g2) {
							continue;
						}
						mList.add(new LaunderGem(g1,g2));
					}
				}
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	
	public void launderE() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "current emeralds and credits: " + Player.player.emeralds + " " + Player.player.launderCredits;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder to a ruby";
					}

					@Override
					public boolean go() {
						if (Player.player.emeralds > 0 && Player.player.launderCredits > 0) {
							Player.player.emeralds--;
							Player.player.launderCredits--;
							Player.player.rubies++;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder to a sapphire";
					}

					@Override
					public boolean go() {
						if (Player.player.emeralds > 0 && Player.player.launderCredits > 0) {
							Player.player.emeralds--;
							Player.player.launderCredits--;
							Player.player.sapphires++;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "quickly sell an emerald (750 "+World.currentMoneyString()+")";
					}

					@Override
					public boolean go() {
						if (Player.player.emeralds > 0) {
							Player.player.emeralds--;
							Player.player.addGold(750);
							Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,0.2f,0);
						}
						return false;
					}
				});
				mList.add(new MenuBack());
				return mList;
			}});
	}
	
	public void launderR() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "current rubies and credits: " + Player.player.rubies + " " + Player.player.launderCredits;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder an emerald";
					}

					@Override
					public boolean go() {
						if (Player.player.rubies > 0 && Player.player.launderCredits > 0) {
							Player.player.rubies--;
							Player.player.launderCredits--;
							Player.player.emeralds++;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder a sapphire";
					}

					@Override
					public boolean go() {
						if (Player.player.rubies > 0 && Player.player.launderCredits > 0) {
							Player.player.rubies--;
							Player.player.launderCredits--;
							Player.player.sapphires++;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "quickly sell a ruby (750 "+World.currentMoneyString()+")";
					}

					@Override
					public boolean go() {
						if (Player.player.rubies > 0) {
							Player.player.rubies--;
							Player.player.addGold(750);
							Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,0.2f,0);
						}
						return false;
					}
				});
				mList.add(new MenuBack());
				return mList;
			}});
	}
	public void launderS() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "current sapphires and credits: " + Player.player.sapphires + " " + Player.player.launderCredits;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder an emerald";
					}

					@Override
					public boolean go() {
						if (Player.player.sapphires > 0 && Player.player.launderCredits > 0) {
							Player.player.sapphires--;
							Player.player.launderCredits--;
							Player.player.emeralds++;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder a ruby";
					}

					@Override
					public boolean go() {
						if (Player.player.sapphires > 0 && Player.player.launderCredits > 0) {
							Player.player.sapphires--;
							Player.player.launderCredits--;
							Player.player.rubies++;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "quickly sell a sapphire (750 "+World.currentMoneyString()+")";
					}

					@Override
					public boolean go() {
						if (Player.player.sapphires > 0) {
							Player.player.sapphires--;
							Player.player.addGold(750);
							Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,0.2f,0);
						}
						return false;
					}
				});
				mList.add(new MenuBack());
				return mList;
			}});
	}
	
	public enum GemType{
		EMERALD("emerald","emeralds", new GemFunction() {

			@Override
			public int getGem() {
				return Player.player.emeralds;
			}

			@Override
			public void changeGem(int i) {
				Player.player.emeralds+=i;
			}})
		,RUBY("ruby","rubies", new GemFunction() {

			@Override
			public int getGem() {
				return Player.player.rubies;
			}

			@Override
			public void changeGem(int i) {
				Player.player.rubies+=i;
			}})
		,SAPPHIRE("sapphire","sapphires", new GemFunction() {

			@Override
			public int getGem() {
				return Player.player.sapphires;
			}

			@Override
			public void changeGem(int i) {
				Player.player.sapphires+=i;
			}});
		
		public final String name, plural;
		public final GemFunction func;
		GemType(String _name, String _plural, GemFunction _func){
			name = _name;
			plural = _plural;
			func = _func;
		}
		
		public void sellGem() {
			extra.println();
		}
	}
	
	private interface GemFunction{
		public int getGem();
		public void changeGem(int i);
		
	}
	
	private class LaunderGem implements MenuItem{

		public GemType from, to;
		
		public LaunderGem(GemType _from, GemType _to) {
			from = _from;
			to = _to;
		}
		
		@Override
		public String title() {
			return "Launder " +from.plural +" ("+from.func.getGem()+") to " + to.plural + "("+to.func.getGem()+")" + (Player.player.launderCredits == 0 ? " NO CREDIT" : "");
		}

		@Override
		public boolean go() {
			//must be > 0 to click
			extra.println("You trade a " + from.name + " for a " + to.name);
			from.func.changeGem(-1);
			to.func.changeGem(1);
			Player.player.launderCredits--;
			return false;
		}

		@Override
		public boolean canClick() {
			return Player.player.launderCredits > 0 && from.func.getGem() > 0;
		}

		@Override
		public boolean forceLast() {
			return false;
		}
		
	}
	
	@Override
	public void init() {
		try {
			while (sideQuests.size() < 3) {
				generateSideQuest();
			}
		}catch (Exception e) {
			canQuest = false;
		}
	}
	
	private void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		switch (extra.randRange(1,2)) {
		case 1:
			if (extra.randFloat() > .8f) {//20% chance for a merchant quest instead
				sideQuests.add(FetchSideQuest.generate(this,FetchType.MERCHANT));
				break;
			}
			sideQuests.add(FetchSideQuest.generate(this,FetchType.CRIME));
			break;
		case 2:
			sideQuests.add(KillSideQuest.generate(this,extra.randFloat() > .1f));//90% chance to be a murder quest
			break;
		}
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}

}
