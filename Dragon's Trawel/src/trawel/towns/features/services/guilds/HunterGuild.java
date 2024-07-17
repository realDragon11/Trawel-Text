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
import trawel.factions.FBox;
import trawel.factions.FBox.FSub;
import trawel.helper.constants.TrawelColor;
import trawel.factions.Faction;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.quests.locations.QBMenuItem;
import trawel.quests.locations.QRMenuItem;
import trawel.quests.locations.QuestBoardLocation;
import trawel.quests.locations.QuestR;
import trawel.quests.types.CleanseSideQuest;
import trawel.quests.types.FetchSideQuest;
import trawel.quests.types.KillSideQuest;
import trawel.quests.types.Quest;
import trawel.quests.types.CleanseSideQuest.CleanseType;
import trawel.quests.types.FetchSideQuest.FetchType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.features.Feature;

public class HunterGuild extends Feature implements QuestBoardLocation{
	
	private static final long serialVersionUID = 1L;
	
	private double activityTimer;
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	private boolean canQuest = true;

	public HunterGuild(String _name, int _tier){
		name = _name;
		tier = _tier;
		activityTimer = 24f+Rand.randFloat()*24f;
	}
	
	@Override
	public QRType getQRType() {
		return QRType.HUNT_GUILD;
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_GUILD;
	}
	
	@Override
	public String nameOfFeature() {
		return "Hunter Guild";
	}
	
	@Override
	public String nameOfType() {
		return "Hunter Guild";
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
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.HUNTER);
						return "Current Hunter Reputation: " + (sub == null ? "Unknown" : ""+Print.format2(sub.forFac-sub.againstFac));
					}
				});
				int gemAmount = Math.round(5f*IEffectiveLevel.unclean(tier));
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Request Amber.";
					}

					@Override
					public boolean go() {
						while (true) {
							float cost = 10*gemAmount;//amount is scaled by level
							float spenda = FBox.getSpendableFor(Faction.HUNTER);
							Print.println("Request "+gemAmount+" "+(gemAmount == 1 ? Gem.AMBER.name : Gem.AMBER.plural)+"? cost: " +cost + " of "+Print.format2(spenda));
							if (Input.yesNo()) {
								if (cost <= spenda) {
									Player.player.factionSpent.addFactionRep(Faction.HUNTER,cost,0);
									Gem.AMBER.changeGem(gemAmount);
									Print.println(TrawelColor.RESULT_PASS+"Gained "+gemAmount+" "+(gemAmount == 1 ? Gem.AMBER.name : Gem.AMBER.plural)+", new total: " + Gem.AMBER.getGem()+".");
								}else {
									Print.println(TrawelColor.RESULT_ERROR+"You do not have enough spendable reputation.");
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
						return TrawelColor.FSERVICE_QUEST+"Assignment Board (Sidequests).";
					}

					@Override
					public boolean go() {
						Input.menuGo(new MenuGenerator() {

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
				activityTimer+=12f+(36f*Rand.randFloat());
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
			sideQuests.remove(Rand.randList(sideQuests));
		}
		switch (Rand.randRange(1,5)) {
		case 1:
			if (Rand.randFloat() > .8f) {//20% chance for hero fetch
				sideQuests.add(FetchSideQuest.generate(this,FetchType.HERO));
			}else {
				sideQuests.add(FetchSideQuest.generate(this,FetchType.HUNTER));
			}
			break;
		case 2: case 3:
			//special creature cleanse
			sideQuests.add(CleanseSideQuest.generate(this,Rand.choose(CleanseType.VAMPIRE,CleanseType.VAMPIRE,CleanseType.VAMPIRE,CleanseType.HARPY,CleanseType.HARPY,CleanseType.UNICORN,CleanseType.MONSTERS)));
			break;
		case 4:
			//more normal cleanse
			sideQuests.add(CleanseSideQuest.generate(this,Rand.choose(CleanseType.WOLF,CleanseType.BEAR,CleanseType.VAMPIRE,CleanseType.BANDIT,CleanseType.ANIMALS,CleanseType.MONSTERS)));
			break;
		case 5:
			sideQuests.add(KillSideQuest.generate(this,Rand.randFloat() > .75f));//25% chance to be a murder quest
			break;
		}
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}

}
