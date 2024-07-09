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
		activityTimer = 24f+Rand.randFloat()*24f;
	}
	
	@Override
	public QRType getQRType() {
		return QRType.HGUILD;
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_GUILD;
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
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.HEROIC);
						return "Current Heroic Reputation: " + (sub == null ? "Unknown" : ""+Print.format2(sub.forFac-sub.againstFac));
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Share stories. (Feat Fragments)";
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
						float spenda = FBox.getSpendableFor(Faction.HEROIC);
						float cost = (float)Math.pow(((spentf/50f)+1)*10,1.1f);
						Print.println("Request a feat fragment? cost: " +Print.format2(cost) + " of "+Print.format2(spenda));
						if (Input.yesNo()) {
							if (cost <= spenda) {
								Player.player.hSpentOnKno += cost;
								Player.player.factionSpent.addFactionRep(Faction.HEROIC,cost,0);
								Player.bag.addNewDrawBanePlayer(DrawBane.KNOW_FRAG);
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
				int gemAmount = Math.round(1.5f*IEffectiveLevel.unclean(tier));
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Request Rubies.";
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 20*gemAmount;
						float spenda = FBox.getSpendableFor(Faction.HEROIC);
						Print.println("Request "+gemAmount+" "+(gemAmount == 1 ? Gem.RUBY.name : Gem.RUBY.plural)+"? cost: " +cost + " of "+Print.format2(spenda));
						if (Input.yesNo()) {
							if (cost <= spenda) {
								Player.player.factionSpent.addFactionRep(Faction.HEROIC,cost,0);
								Gem.RUBY.changeGem(gemAmount);
								Print.println(TrawelColor.RESULT_PASS+"Gained "+gemAmount+" "+(gemAmount == 1 ? Gem.RUBY.name : Gem.RUBY.plural)+", new total: " + Gem.RUBY.getGem()+".");
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
			sideQuests.add(FetchSideQuest.generate(this,FetchType.HERO));
			break;
		case 2:
			sideQuests.add(FetchSideQuest.generate(this,FetchType.COMMUNITY));
			break;
		case 3: case 4:
			sideQuests.add(CleanseSideQuest.generate(this,Rand.choose(CleanseType.WOLF,CleanseType.BEAR,CleanseType.VAMPIRE,CleanseType.BANDIT,CleanseType.MONSTERS)));
			break;
		case 5:
			sideQuests.add(KillSideQuest.generate(this,Rand.randFloat() > .9f));//10% chance to be a murder quest
			break;
		}
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}

}
