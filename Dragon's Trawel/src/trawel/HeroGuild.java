package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.factions.FBox;
import trawel.factions.FBox.FSub;
import trawel.factions.Faction;

public class HeroGuild extends Feature {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static float hSpentOnKno = 0f;

	public HeroGuild(String name){
		this.name = name;
		tutorialText = "The heroes guild allows you to spend your fame on knowledge.";
		color = Color.PINK;
	}
	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|store|Hero's Guild|");
		
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.HEROIC);
						return "current reputation: " + (sub == null ? "Unknown" : ""+extra.format2(sub.forFac-sub.againstFac));
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "share stories";
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
						extra.println("Buy a knowledge fragment? cost: " +extra.format2(cost) + "/"+extra.format2(spenda));
						if (extra.yesNo()) {
							if (cost <= spenda) {
								Player.player.hSpentOnKno += cost;
								Player.player.factionSpent.addFactionRep(Faction.HEROIC,cost,0);
								Player.bag.addNewDrawBane(DrawBane.KNOW_FRAG);
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
						return "request rubies";
					}

					@Override
					public boolean go() {
						while (true) {
						int cost = 20;
						float spenda = FBox.getSpendableFor(Player.player.getPerson().facRep.getFacRep(Faction.HEROIC));
						extra.println("Buy a ruby? cost: " +cost + "/"+extra.format2(spenda));
						if (extra.yesNo()) {
							if (cost <= spenda) {
								Player.player.factionSpent.addFactionRep(Faction.HEROIC,cost,0);
								Player.player.rubies++;
							}
						}else {
							break;
						}
						}
						return false;
					}
				});
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

}
