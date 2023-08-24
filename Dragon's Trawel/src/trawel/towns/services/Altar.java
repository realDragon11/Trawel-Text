package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.classless.Perk;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.QRMenuItem;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class Altar extends Feature{

	
	private static final long serialVersionUID = 1L;

	public Altar() {
		name = "Sky Slab";
		tutorialText = "Altar";
	}
	
	@Override
	public String getColor() {
		return extra.F_SPECIAL;
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|altar|Altar|");
		Networking.setArea("mountain");
		MenuGenerator mGen = new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mainGame.globalPassTime();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "examine the altar";
					}

					@Override
					public boolean go() {
						examine();
						return false;
					}
				});
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "stand on the altar";
					}

					@Override
					public boolean go() {
						extra.println("Nothing happens. Nothing ever could happen. You spend an hour staring at your hands, lost in thought.");
						Player.addTime(1);
						mainGame.globalPassTime();
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "sacrifice something";
					}

					@Override
					public boolean go() {
						sacrifice();
						return false;
					}
				});
				mList.add(new MenuBack("leave"));
				return mList;
			}};
			extra.menuGo(mGen);
	}


	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		//Auto-generated method stub
		return null;
	}
	
	private void examine() {
		extra.println("You examine the altar. It's long and flat, and made out of a dark, black stone that reflects the sky.");
	}
	
	private void sacrifice() {
		DrawBane inter = Player.bag.playerOfferDrawBane("sacrifice");
		boolean specialInteraction = false;
		if (inter != null && !inter.equals(DrawBane.NOTHING)) {
			switch (inter) {
			case BEATING_HEART:
				Player.player.forceRelation += 8;
				extra.println("You spend hours upon hours preparing your great sacrifice, and stab the heart when the time is right. The very heavens seem to look down upon you with favor, and a storm brews... aether rains from the sky.");
				Player.addTime(40);
				Player.bag.addAether((int) ((Player.player.forceRelation*100)+extra.randRange(4000,5000)));
				break;
			case BLOOD: case MEAT:
				Player.player.forceRelation += 0.1;
				extra.println("You offer the gift of flesh- in any form.");
				Player.addTime(1);
				break;
			case CEON_STONE:
				Player.player.forceRelation += 1;
				extra.println("You offer the gift of decay, unreason, and insane change.");
				Player.addTime(1);
				break;
			case ENT_CORE:
				Player.player.forceRelation += 2;
				extra.println("You offer the gift of primal life.");
				Player.addTime(1);
				break;
			case TRUFFLE: case PUMPKIN: case GARLIC: case HONEY: case APPLE: case EGGCORN:
				Player.player.forceRelation += 0.1;
				extra.println("You offer a gift of the harvest.");
				Player.addTime(.2);
				break;
			case GOLD: case SILVER:
				Player.player.forceRelation += 0.5;
				extra.println("You offer a gift of the ground, painful taken from the planet.");
				Player.addTime(.2);
				break;
			case VIRGIN:
				Player.player.forceRelation += 4;
				extra.println("You spend hours preparing your great sacrifice, and stab the innocent when the time is right. The Sky gives a gift back... but the unspoken message that it would accept this gift for itself lingers in your mind.");
				Player.addTime(24);
				Player.bag.addNewDrawBanePlayer(DrawBane.BEATING_HEART);
				specialInteraction = true;
				break;
			case WAX: case WOOD:
				Player.player.forceRelation += 0.02;
				extra.println("You offer a gift of the primal forces, a minor piece of a greater whole.");
				Player.addTime(.05);
				break;
			default:
				Player.bag.giveBackDrawBane(inter, "The alter rejects your %.");
				return;
			}
			
			mainGame.globalPassTime();
			
			if (Player.player.forceRewardCount == 0 && Player.player.forceRelation >= 5) {
				Player.player.forceRewardCount++;
				Player.player.getPerson().setPerk(Perk.SKY_BLESS_1);
				extra.println("You feel blessed.");
				specialInteraction = true;
			}
			
			if (Player.player.forceRewardCount == 1 && Player.player.forceRelation >= 14) {
				Player.player.forceRewardCount++;
				Player.player.getPerson().setPerk(Perk.SKY_BLESS_2);
				extra.println("You feel blessed.");
				specialInteraction = true;
			}
			
			if (specialInteraction == false) {
				extra.println("The gift disappears, but nothing else happens.");
			}
		}
	}
	
}
