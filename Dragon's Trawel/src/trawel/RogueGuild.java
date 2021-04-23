package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.factions.FBox;
import trawel.factions.FBox.FSub;
import trawel.factions.Faction;

public class RogueGuild extends Feature {
	
	public static int launderCredits = 0;

	public RogueGuild(String name){
		this.name = name;
		tutorialText = "The rogue's guild allows you to launder gems.";
		color = Color.PINK;
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
		// TODO Auto-generated method stub
		//TODO: make certain drawbanes give more depending one what the guild needs
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
						return "current gold: " + Player.bag.getGold();
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
						return "credits: " + RogueGuild.launderCredits;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "buy a launder credit for 1000 gold";
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 1000;
						extra.println("Buy a launder credit? cost: " +cost + "/"+Player.bag.getGold());
						if (extra.yesNo()) {
							if (cost <= Player.bag.getGold()) {
								Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,0.2f,0);
								launderCredits++;
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
								launderCredits++;
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
	
	public void launderE() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "current emeralds and credits: " + Player.player.emeralds + " " + RogueGuild.launderCredits;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder a ruby";
					}

					@Override
					public boolean go() {
						if (Player.player.emeralds > 0 && RogueGuild.launderCredits > 0) {
							Player.player.emeralds--;
							RogueGuild.launderCredits--;
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
						if (Player.player.emeralds > 0 && RogueGuild.launderCredits > 0) {
							Player.player.emeralds--;
							RogueGuild.launderCredits--;
							Player.player.sapphires++;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "quickly sell an emerald (750 gold)";
					}

					@Override
					public boolean go() {
						if (Player.player.emeralds > 0) {
							Player.player.emeralds--;
							Player.bag.addGold(750);
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
	
	public void launderR() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "current rubies and credits: " + Player.player.rubies + " " + RogueGuild.launderCredits;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder an emerald";
					}

					@Override
					public boolean go() {
						if (Player.player.rubies > 0 && RogueGuild.launderCredits > 0) {
							Player.player.rubies--;
							RogueGuild.launderCredits--;
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
						if (Player.player.rubies > 0 && RogueGuild.launderCredits > 0) {
							Player.player.rubies--;
							RogueGuild.launderCredits--;
							Player.player.sapphires++;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "quickly sell a ruby (750 gold)";
					}

					@Override
					public boolean go() {
						if (Player.player.rubies > 0) {
							Player.player.rubies--;
							Player.bag.addGold(750);
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
	public void launderS() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "current sapphires and credits: " + Player.player.sapphires + " " + RogueGuild.launderCredits;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "launder an emerald";
					}

					@Override
					public boolean go() {
						if (Player.player.sapphires > 0 && RogueGuild.launderCredits > 0) {
							Player.player.sapphires--;
							RogueGuild.launderCredits--;
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
						if (Player.player.sapphires > 0 && RogueGuild.launderCredits > 0) {
							Player.player.sapphires--;
							RogueGuild.launderCredits--;
							Player.player.rubies++;
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "quickly sell a sapphire (750 gold)";
					}

					@Override
					public boolean go() {
						if (Player.player.sapphires > 0) {
							Player.player.sapphires--;
							Player.bag.addGold(750);
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

}
