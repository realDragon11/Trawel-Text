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
import trawel.factions.FBox;
import trawel.factions.FBox.FSub;
import trawel.personal.people.Player;
import trawel.factions.Faction;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;

public class RogueGuild extends Feature {
	
	private static final long serialVersionUID = 1L;
	public RogueGuild(String name){
		this.name = name;
		tutorialText = "The rogue's guild allows you to launder gems.";
	}
	
	@Override
	public String getColor() {
		return extra.F_GUILD;
	}
	
	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|store|Rogue's Guild|");
		
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
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder gems";
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
						return "request sapphires";
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 25;
						float spenda = FBox.getSpendableFor(Player.player.getPerson().facRep.getFacRep(Faction.ROGUE));
						extra.println("Buy a sapphire? cost: " +cost + "/"+extra.format2(spenda));
						if (extra.yesNo()) {
							if (cost <= spenda) {
								Player.player.factionSpent.addFactionRep(Faction.ROGUE,cost,0);
								Player.player.sapphires++;
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
						return "donate a sapphire";
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
							}
						}else {
							break;
						}
						}
						return false;
					}
				});
				mList.add(new MenuBack("leave"));
				return mList;
			}});
	}
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		// TODO Auto-generated method stub
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
						return "credits: " + Player.player.launderCredits;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "buy a launder credit for 1000 " + World.currentMoneyString();
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 1000;
						extra.println("Buy a launder credit? cost: " +cost + "/"+Player.player.getGold());
						if (extra.yesNo()) {
							if (cost <= Player.player.getGold()) {
								Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,0.2f,0);
								Player.player.launderCredits++;
								Player.player.addGold(-cost);
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
							break;
						}
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder emeralds";
					}

					@Override
					public boolean go() {
						launderE();
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder rubies";
					}

					@Override
					public boolean go() {
						launderR();
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder sapphires";
					}

					@Override
					public boolean go() {
						launderS();
						return false;
					}});
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
						return "launder a ruby";
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
						return "launder a sapphire";
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

}
