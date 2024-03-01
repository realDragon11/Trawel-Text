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
import trawel.personal.classless.IEffectiveLevel;
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

public class HunterGuild extends Feature implements QuestBoardLocation{
	
	private static final long serialVersionUID = 1L;
	
	private double activityTimer;
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	private boolean canQuest = true;

	public HunterGuild(String _name, int _tier){
		name = _name;
		tier = _tier;
		tutorialText = "Hunter's Guild";
		area_type = Area.MISC_SERVICE;
		activityTimer = 24f+extra.randFloat()*24f;
	}
	
	@Override
	public QRType getQRType() {
		return QRType.HUNT_GUILD;
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
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.HUNTER);
						return "Current Hunter Reputation: " + (sub == null ? "Unknown" : ""+extra.format2(sub.forFac-sub.againstFac));
					}
				});
				int gemAmount = Math.round(5f*IEffectiveLevel.unclean(tier));
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.SERVICE_SPECIAL_PAYMENT+"Request Amber.";
					}

					@Override
					public boolean go() {
						while (true) {
							float cost = 10*gemAmount;//amount is scaled by level
							float spenda = FBox.getSpendableFor(Faction.HUNTER);
							extra.println("Request "+gemAmount+" "+(gemAmount == 1 ? Gem.AMBER.name : Gem.AMBER.plural)+"? cost: " +cost + " of "+extra.format2(spenda));
							if (extra.yesNo()) {
								if (cost <= spenda) {
									Player.player.factionSpent.addFactionRep(Faction.HUNTER,cost,0);
									Gem.AMBER.changeGem(gemAmount);
									extra.println(extra.RESULT_PASS+"Gained "+gemAmount+" "+(gemAmount == 1 ? Gem.AMBER.name : Gem.AMBER.plural)+", new total: " + Gem.AMBER.getGem()+".");
								}else {
									extra.println(extra.RESULT_ERROR+"You do not have enough spendable reputation.");
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
						return extra.FSERVICE_QUEST+"Assignment Board (Sidequests).";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list = new ArrayList<MenuItem>();
								
								for (Quest q: sideQuests) {
									list.add(new QBMenuItem(q,HunterGuild.this));
								}
								for (QuestR qr: Player.player.QRFor(HunterGuild.this)) {
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
		return null;//TODO monster bounty quests or something
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		switch (extra.randRange(1,5)) {
		case 1:
			if (extra.randFloat() > .8f) {//20% chance for hero fetch
				sideQuests.add(FetchSideQuest.generate(this,FetchType.HERO));
			}else {
				sideQuests.add(FetchSideQuest.generate(this,FetchType.HUNTER));
			}
			break;
		case 2: case 3:
			//special creature cleanse
			sideQuests.add(CleanseSideQuest.generate(this,extra.choose(CleanseType.VAMPIRE,CleanseType.VAMPIRE,CleanseType.VAMPIRE,CleanseType.HARPY,CleanseType.HARPY,CleanseType.UNICORN,CleanseType.MONSTERS)));
			break;
		case 4:
			//more normal cleanse
			sideQuests.add(CleanseSideQuest.generate(this,extra.choose(CleanseType.WOLF,CleanseType.BEAR,CleanseType.VAMPIRE,CleanseType.BANDIT,CleanseType.ANIMALS,CleanseType.MONSTERS)));
			break;
		case 5:
			sideQuests.add(KillSideQuest.generate(this,extra.randFloat() > .75f));//25% chance to be a murder quest
			break;
		}
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}

}
