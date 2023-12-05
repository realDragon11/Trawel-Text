package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking.Area;
import trawel.extra;
import trawel.factions.FBox;
import trawel.factions.FBox.FSub;
import trawel.factions.Faction;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.quests.CleanseSideQuest;
import trawel.quests.CleanseSideQuest.CleanseType;
import trawel.quests.FetchSideQuest;
import trawel.quests.FetchSideQuest.FetchType;
import trawel.quests.KillSideQuest;
import trawel.quests.QBMenuItem;
import trawel.quests.QRMenuItem;
import trawel.quests.Quest;
import trawel.quests.QuestBoardLocation;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class HeroGuild extends Feature implements QuestBoardLocation{
	
	private static final long serialVersionUID = 1L;
	
	private double activityTimer;
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	private boolean canQuest = true;

	public HeroGuild(String _name, int _tier){
		name = _name;
		tier = _tier;
		tutorialText = "Hero's Guild";
		area_type = Area.MISC_SERVICE;
		activityTimer = 24f+extra.randFloat()*24f;
	}
	
	@Override
	public QRType getQRType() {
		return QRType.HGUILD;
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
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.HEROIC);
						return "Current Heroic Reputation: " + (sub == null ? "Unknown" : ""+extra.format2(sub.forFac-sub.againstFac));
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Share stories. (Feat Fragments)";
					}

					@Override
					public boolean go() {
						while (true) {
						//FSub spent = Player.player.factionSpent.getFacRep(Faction.HEROIC);
						float spentf = Player.player.hSpentOnKno;
						/*if (spent == null) {
							spentf = 0;
						}else {
							spentf = spent.forFac;
						}*/
						float spenda = FBox.getSpendableFor(Player.player.getPerson().facRep.getFacRep(Faction.HEROIC));
						float cost = (float)Math.pow(((spentf/50f)+1)*10,1.1f);
						extra.println("Buy a feat fragment? cost: " +extra.format2(cost) + " of "+extra.format2(spenda));
						if (extra.yesNo()) {
							if (cost <= spenda) {
								Player.player.hSpentOnKno += cost;
								Player.player.factionSpent.addFactionRep(Faction.HEROIC,cost,0);
								Player.bag.addNewDrawBanePlayer(DrawBane.KNOW_FRAG);
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
						return "Request Rubies.";
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 20;
						float spenda = FBox.getSpendableFor(Player.player.getPerson().facRep.getFacRep(Faction.HEROIC));
						extra.println("Buy a ruby? cost: " +cost + " of "+extra.format2(spenda));
						if (extra.yesNo()) {
							if (cost <= spenda) {
								Player.player.factionSpent.addFactionRep(Faction.HEROIC,cost,0);
								Gem.RUBY.changeGem(1);
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
						return "Assignment Board (Sidequests).";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list = new ArrayList<MenuItem>();
								
								for (Quest q: sideQuests) {
									list.add(new QBMenuItem(q,HeroGuild.this));
								}
								for (QuestR qr: Player.player.QRFor(HeroGuild.this)) {
									list.add(new QRMenuItem(qr));
								}
								list.add(new MenuBack());
								return list;
							}});
						return false;
					}});
				/*mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "donate a ruby";
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 5;
						float spenda = FBox.getSpendableFor(Player.player.getPerson().facRep.getFacRep(Faction.HEROIC));
						extra.println("Donate a ruby? You have " + Player.player.rubies);
						if (extra.yesNo()) {
							if (Player.player.rubies > 0) {
								Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,cost,0);
								Player.player.rubies--;
							}
						}else {
							break;
						}
						}
						return false;
					}
				});*/
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
		return null;//TODO monster bounty quests or something
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
		switch (extra.randRange(1,5)) {
		case 1:
			sideQuests.add(FetchSideQuest.generate(this,FetchType.HERO));
			break;
		case 2:
			sideQuests.add(FetchSideQuest.generate(this,FetchType.COMMUNITY));
			break;
		case 3: case 4:
			sideQuests.add(CleanseSideQuest.generate(this,extra.choose(CleanseType.WOLF,CleanseType.BEAR,CleanseType.VAMPIRE,CleanseType.BANDIT)));
			break;
		case 5:
			sideQuests.add(KillSideQuest.generate(this,extra.randFloat() > .9f));//10% chance to be a murder quest
			break;
		}
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}

}
