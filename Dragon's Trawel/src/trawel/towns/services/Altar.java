package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.classless.Perk;
import trawel.personal.item.Seed;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.QRMenuItem;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class Altar extends Feature{

	private static final long serialVersionUID = 1L;

	private AltarForce force;
	public Altar(String _name, AltarForce _type) {
		//name = "Sky Slab";
		name = _name;
		force = _type;
		tutorialText = "Altar";
		area_type = Area.ALTAR;
	}
	
	@Override
	public String getColor() {
		return extra.F_SPECIAL;
	}
	
	@Override
	public void go() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Examine the Altar.";
					}

					@Override
					public boolean go() {
						examine();
						return false;
					}
				});
				for (QuestR qr: Player.player.QRFor(Altar.this)) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Stand on the altar.";
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
						return extra.SERVICE_SPECIAL_PAYMENT+"Sacrifice something.";
					}

					@Override
					public boolean go() {
						sacrifice();
						return false;
					}
				});
				mList.add(new MenuBack("Leave."));
				return mList;
			}
			@Override
			public void onRefresh() {
				mainGame.globalPassTime();
			}
			
		});
	}


	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		//Auto-generated method stub
		return null;
	}
	
	private void examine() {
		switch (force) {
		case SKY:
			extra.println("You examine the altar. It's long and flat, and made out of a dark, black stone that reflects the sky.");
			break;
		case FOREST:
			extra.println("You examine the altar. Brambles with sharp thorns cluster around a seemingly natural heartwood chair.");
			break;
		}
	}
	
	private void sacrifice() {
		DrawBane inter = Player.bag.playerOfferDrawBane("sacrifice");
		boolean specialInteraction = false;
		boolean accepted = true;
		if (inter != null && !inter.equals(DrawBane.NOTHING)) {
			switch (inter) {
			case BEATING_HEART:
				if (force == AltarForce.SKY) {
					addRelation(8);
					extra.println(extra.RESULT_GOOD+"You spend hours upon hours preparing your great sacrifice, and stab the heart when the time is right. The very heavens seem to look down upon you with favor, and a storm brews... aether rains from the sky.");
					Player.addTime(40);
					Player.bag.addAether((int) ((getRelation()*100)+extra.randRange(4000,5000)));
					specialInteraction = true;
					break;
				}
				if (force == AltarForce.FOREST) {
					addRelation(10);
					extra.println(extra.RESULT_GOOD+"You spend hours upon hours preparing your great sacrifice, and stab the heart when the time is right. Knowledge spills into your mind, and the Forest willingly gives its own flesh to record eldritch lore, in recognition of an equal trade- blood for blood, sap for sap.");
					Player.addTime(40);
					Player.player.getPerson().addXp(4);
					Player.bag.addNewDrawBanePlayer(DrawBane.KNOW_FRAG);
					Player.bag.addNewDrawBanePlayer(DrawBane.KNOW_FRAG);
					specialInteraction = true;
					break;
				}
				//cannot give special gift back rn
				extra.println("The "+getForceName()+" seems very unsure of itself...");
				addRelation(5);
				//accepted = false;
				break;
			case BLOOD: case MEAT:
				addRelation(0.1f);
				extra.println("You offer the gift of flesh- in any form.");
				Player.addTime(1);
				break;
			case CEON_STONE:
				addRelation(1);
				extra.println("You offer the gift of decay, unreason, and insane change.");
				Player.addTime(1);
				break;
			case ENT_CORE:
				if (force == AltarForce.FOREST) {//doesn't like how you got this, most likely
					accepted = false;
					break;
				}
				addRelation(2);
				extra.println("You offer the gift of primal life.");
				Player.addTime(1);
				break;
			case TRUFFLE: case PUMPKIN: case GARLIC: case HONEY: case APPLE: case EGGCORN:
				addRelation(0.1f);
				extra.println("You offer a gift of the harvest.");
				Player.addTime(.2);
				if (force == AltarForce.FOREST) {
					extra.println(extra.RESULT_GOOD+"The Forest gives you a seed, that you may continue the cycle of life.");
					Player.bag.addSeed(Seed.randSeed());
					specialInteraction = true;
				}
				break;
			case GOLD: case SILVER:
				addRelation(0.5f);
				extra.println("You offer a gift of the earthly viscera, painful taken from the deep bowels of the soil below.");
				Player.addTime(.2);
				break;
			case VIRGIN:
				addRelation(4);
				extra.println(extra.RESULT_GOOD+"You spend hours preparing your great sacrifice, and stab the innocent when the time is right. The "+getForceName()+" gives a gift back... but the unspoken message that it would accept this gift for itself lingers in your mind.");
				Player.addTime(24);
				Player.bag.addNewDrawBanePlayer(DrawBane.BEATING_HEART);
				specialInteraction = true;
				break;
			case WAX: case WOOD:
				addRelation(0.02f);
				extra.println("You offer a gift of the primal forces, a minor piece of a greater whole.");
				Player.addTime(.05);
				break;
			case GRAVE_DIRT:
				if (force != AltarForce.FOREST) {
					accepted = false;
					break;
				}
				addRelation(1f);
				extra.println("You offer a gift of nourishment, that the soil may feast on the succor from fruit it has bourne.");
				Player.addTime(.2);
				break;
			case GRAVE_DUST:
				addRelation(0.5f);
				extra.println("You offer proof that the primal forces have been respected, that their laws have been enforced.");
				Player.addTime(0.05f);
				break;
			default:
				accepted = false;
				break;
			}
			
			if (accepted == false) {
				Player.bag.giveBackDrawBane(inter,extra.RESULT_ERROR+"The altar rejects your %.");
				return;
			}
			
			mainGame.globalPassTime();
			
			switch (force) {
			case FOREST:
				if (getReward() == 0 && getRelation() >= 5) {
					giveReward();
					Player.player.getPerson().setPerk(Perk.FOREST_BLESS_1);
					Player.player.addAchieve("altar_forest", "Forest's Chosen");
					extra.println(extra.RESULT_GOOD+"Your skin tenses briefly. You feel blessed.");
					specialInteraction = true;
				}
				
				if (getReward() == 1 && getRelation() >= 14) {
					giveReward();
					Player.player.getPerson().setPerk(Perk.FOREST_BLESS_2);
					Player.player.addAchieve("altar_forest", "Forest's Avatar");
					extra.println(extra.RESULT_GOOD+"Your skin hardens before becoming supple once more. You feel very blessed.");
					specialInteraction = true;
				}
				break;
			case SKY:
				if (getReward() == 0 && getRelation() >= 5) {
					giveReward();
					Player.player.getPerson().setPerk(Perk.SKY_BLESS_1);
					Player.player.addAchieve("altar_sky", "Sky's Chosen");
					extra.println(extra.RESULT_GOOD+"The world seems to slow down around you for a brief moment. You feel blessed.");
					specialInteraction = true;
				}
				
				if (getReward() == 1 && getRelation() >= 14) {
					giveReward();
					Player.player.getPerson().setPerk(Perk.SKY_BLESS_2);
					Player.player.addAchieve("altar_sky", "Sky's Avatar");
					extra.println(extra.RESULT_GOOD+"Your vision swims before returning sharper than ever. You feel very blessed.");
					specialInteraction = true;
				}
				break;
			}
			
			
			if (specialInteraction == false) {
				extra.println(extra.RESULT_PASS+"The gift disappears, but nothing else happens.");
			}
		}
	}
	
	protected float getRelation() {
		return Player.player.getForceRelation(force);
	}
	
	protected void addRelation(float amount) {
		Player.player.addForceRelation(force, amount);
	}
	
	protected int getReward() {
		return Player.player.getForceReward(force);
	}
	protected void giveReward() {
		Player.player.nextForceReward(force);
	}
	
	protected String getForceName() {
		switch (force) {
		case FOREST:
			return "Forest";
		case SKY:
			return "Sky";
		}
		return "ERR";
	}
	
	public enum AltarForce{
		SKY, FOREST
		//TODO: blood
	}
	
	@Override
	public String getIntro() {
		float rel = getRelation()*extra.randFloat();
		if (rel < 2f) {
			return null;// no response
		}
		//from below, add the highest message at the top and fallback downwards
		if (rel > 12f) {
			switch(force) {
			case FOREST:
				return "The heartwood chair creaks and the thorns turn aside, inviting you to sit.";
			case SKY:
				return "Thunder booms and rain falls around you, but you find yourself in the eye- untouched by the storm.";
			}
		}
		if (rel > 8f) {
			switch(force) {
			case FOREST:
				return "The brambles pulse as if it were a living heart, pleased at your approach.";
			case SKY:
				return "A sudden but refreshing wind blows down on you as you inspect the altar.";
			}
		}
		if (rel > 4f) {
			switch(force) {
			case FOREST:
				return "The leaves bristle in the wind...";
			case SKY:
				return "The wind picks up as you approach the slab.";
			}
		}
		//fallback if nothing is said
		return "You feel a deep connection here.";
	}
	
}
