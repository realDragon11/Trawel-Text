package trawel.towns.features.misc;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.classless.Perk;
import trawel.personal.item.Seed;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.locations.QRMenuItem;
import trawel.quests.locations.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.data.FeatureData;
import trawel.towns.features.Feature;

public class Altar extends Feature{
	
	static {
		FeatureData.registerFeature(Altar.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" accept "+TrawelColor.SERVICE_SPECIAL_PAYMENT+"offerings"+TrawelColor.COLOR_RESET+" of Drawbanes in return for blessings."+
						(Player.isGameMode_NoPunishments() ? "" : " "+fancyNamePlural()+" can provide "+TrawelColor.SERVICE_FREE+"insight"+TrawelColor.COLOR_RESET+" to lift "+Effect.CURSE.getName()+" and overcome "+Effect.BURNOUT.getName()+" to those with a close enough connection."));
			}
			
			@Override
			public int priority() {
				return 12;
			}
			
			@Override
			public String name() {
				return "Altar";
			}
			
			@Override
			public String color() {
				return TrawelColor.F_SPECIAL;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.VITAL_SERVICES;
			}
		});
	}

	private static final long serialVersionUID = 1L;

	private AltarForce force;
	public Altar(String _name, AltarForce _type) {
		name = _name;
		force = _type;
	}
	
	@Override
	public String nameOfType() {
		return "Altar";
	}
	
	@Override
	public Area getArea() {
		return Area.ALTAR;
	}
	
	@Override
	public void go() {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_FLAVOR+"Examine the Altar.";
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
				if (!Player.player.isGameMode_NoPunishments()) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_FREE+"Pray to the altar.";
						}

						@Override
						public boolean go() {
							Print.println("You kneel before the Altar and pray to the primal force it venerates.");
							Player.addTime(0.5d);
							TrawelTime.globalPassTime();
							if (getRelation() >= 2f) {
								Player.player.getPerson().insightEffects();
								Print.println("The Altar gives you its guidance.");
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You do not have a close enough connection with the primal force of the "+getForceName()+" to get guidance here.");
							}
							Player.addTime(0.5d);
							TrawelTime.globalPassTime();
							
							return false;
						}
					});
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Sacrifice something.";
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
				TrawelTime.globalPassTime();
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
			Print.println("You examine the altar. It's long and flat, and made out of a dark, black stone that reflects the sky.");
			break;
		case FOREST:
			Print.println("You examine the altar. Brambles with sharp thorns cluster around a seemingly natural heartwood chair.");
			break;
		}
	}
	
	private void sacrifice() {
		DrawBane inter = Player.bag.playerOfferDrawBane("sacrifice");
		boolean specialInteraction = false;
		boolean accepted = true;
		//special sacrifice: 2+ hours
		//tiny sacrifice: 0.5 hours ADVISE_1 <.1 relation
		//small sacrifice: 0.5 hours ADVISE_2 .1 relation
		//medium sacrifice: 0.5 hours ADVISE_3 .5 relation
		//large sacrifice: 1 hours ADVISE_4 1 relation
		if (inter != null && !inter.equals(DrawBane.EV_NOTHING)) {
			switch (inter) {
			case BEATING_HEART:
				if (force == AltarForce.SKY) {
					addRelation(8f);
					int aReward = (int) ((getRelation()*100)+Rand.randRange(4000,5000));
					Print.println(TrawelColor.RESULT_GOOD+"You spend hours upon hours preparing your great sacrifice, and stab the heart when the time is right. The very heavens seem to look down upon you with favor, and a storm brews... aether rains from the sky. +"+aReward + " aether.");
					Player.addTime(40d);
					Player.bag.addAether(aReward);
					specialInteraction = true;
					break;
				}
				if (force == AltarForce.FOREST) {
					addRelation(10f);
					Print.println(TrawelColor.RESULT_GOOD+"You spend hours upon hours preparing your great sacrifice, and stab the heart when the time is right. Knowledge spills into your mind, and the Forest willingly gives its own flesh to record eldritch lore, in recognition of an equal trade- blood for blood, sap for sap.");
					Player.addTime(40d);
					Player.player.getPerson().addXp(4);
					Player.bag.addNewDrawBanePlayer(DrawBane.KNOW_FRAG);
					Player.bag.addNewDrawBanePlayer(DrawBane.KNOW_FRAG);
					specialInteraction = true;
					break;
				}
				//cannot give special gift back rn
				Print.println(TrawelColor.ADVISE_6+"The "+getForceName()+" seems very unsure of itself...");
				addRelation(5f);
				//accepted = false;
				break;
			case BLOOD: case MEAT: case SINEW: case MIMIC_GUTS: case BAT_WING:
				addRelation(0.1f);
				Print.println(TrawelColor.ADVISE_2+"You offer the gift of flesh- in any form.");
				Player.addTime(0.5d);
				break;
			case CEON_STONE:
				addRelation(1f);
				Print.println(TrawelColor.ADVISE_4+"You offer the gift of decay, unreason, and insane change.");
				Player.addTime(1d);
				break;
			case LIVING_FLAME:
				if (force == AltarForce.FOREST) {//forest doesn't accept fire
					accepted = false;
					break;
				}
				addRelation(1f);
				Print.println(TrawelColor.ADVISE_4+"You offer the gift of an enduring flame, abrim with vitality.");
				Player.addTime(1d);
				break;
			case TELESCOPE:
				if (force == AltarForce.SKY) {//only sky accepts telescope
					addRelation(1f);
					Print.println(TrawelColor.ADVISE_4+"You offer the gift of a venerative tool, peering into the majestic heavens.");
					Player.addTime(1d);
					break;
				}
				accepted = false;
				break;
			case ENT_CORE:
				if (force == AltarForce.FOREST) {//doesn't like how you got this, most likely
					accepted = false;
					break;
				}
				addRelation(2f);
				Print.println(TrawelColor.ADVISE_5+"You offer the gift of primal life.");
				Player.addTime(1d);
				break;
			case TRUFFLE: case PUMPKIN: case GARLIC: case HONEY: case APPLE: case EGGCORN:
				addRelation(0.1f);
				Print.println(TrawelColor.ADVISE_2+"You offer a gift of the harvest.");
				Player.addTime(0.5d);
				if (force == AltarForce.FOREST) {
					Print.println(TrawelColor.RESULT_GOOD+"The Forest gives you a seed, that you may continue the cycle of life.");
					Player.bag.addSeed(Seed.randSeed());
					specialInteraction = true;
				}
				break;
			case GOLD: case SILVER:
				addRelation(0.5f);
				Print.println(TrawelColor.ADVISE_3+"You offer a gift of the earthly viscera, painful taken from the deep bowels of the soil below.");
				Player.addTime(0.5);
				break;
			case VIRGIN:
				addRelation(4f);
				Print.println(TrawelColor.ADVISE_6+"You spend hours preparing your great sacrifice, and stab the innocent when the time is right. The "+getForceName()+" gives a gift back... but the unspoken message that it would accept this gift as a further sacrifice lingers in your mind.");
				Player.addTime(24d);
				Player.bag.addNewDrawBanePlayer(DrawBane.BEATING_HEART);
				specialInteraction = true;
				break;
			case WAX: case WOOD: 
				addRelation(0.05f);
				Print.println(TrawelColor.ADVISE_1+"You offer a gift of the primal forces, a minor piece of a greater whole.");
				Player.addTime(0.5d);
				break;
			case GRAVE_DIRT:
				if (force == AltarForce.FOREST) {
					addRelation(0.5f);
					Print.println(TrawelColor.ADVISE_3+"You offer a gift of nourishment, that the soil may feast on the succor from fruit it has bourne.");
					Player.addTime(0.5);
					break;
				}
				accepted = false;
				break;
			case GRAVE_DUST:
				addRelation(0.5f);
				Print.println(TrawelColor.ADVISE_3+"You offer proof that the primal forces have been respected, that their laws have been enforced.");
				Player.addTime(0.5d);
				break;
			default:
				accepted = false;
				break;
			}
			
			if (accepted == false) {
				Player.bag.giveBackDrawBane(inter,TrawelColor.RESULT_ERROR+"The altar rejects your %.");
				return;
			}
			
			TrawelTime.globalPassTime();
			
			switch (force) {
			case FOREST:
				if (getReward() == 0 && getRelation() >= 5) {
					giveReward();
					Networking.unlockAchievement("bless1");
					Player.player.getPerson().setPerk(Perk.FOREST_BLESS_1);
					Player.player.addAchieve("altar_forest", "Forest's Chosen");
					Print.println(TrawelColor.RESULT_GOOD+"Your skin tenses briefly. You feel blessed.");
					specialInteraction = true;
				}
				
				if (getReward() == 1 && getRelation() >= 14) {
					giveReward();
					Networking.unlockAchievement("bless2");
					Player.player.getPerson().setPerk(Perk.FOREST_BLESS_2);
					Player.player.addAchieve("altar_forest", "Forest's Avatar");
					Print.println(TrawelColor.RESULT_GOOD+"Your skin hardens before becoming supple once more. You feel very blessed.");
					specialInteraction = true;
				}
				break;
			case SKY:
				if (getReward() == 0 && getRelation() >= 5) {
					giveReward();
					Networking.unlockAchievement("bless1");
					Player.player.getPerson().setPerk(Perk.SKY_BLESS_1);
					Player.player.addAchieve("altar_sky", "Sky's Chosen");
					Print.println(TrawelColor.RESULT_GOOD+"The world seems to slow down around you for a brief moment. You feel blessed.");
					specialInteraction = true;
				}
				
				if (getReward() == 1 && getRelation() >= 14) {
					giveReward();
					Networking.unlockAchievement("bless2");
					Player.player.getPerson().setPerk(Perk.SKY_BLESS_2);
					Player.player.addAchieve("altar_sky", "Sky's Avatar");
					Print.println(TrawelColor.RESULT_GOOD+"Your vision swims before returning sharper than ever. You feel very blessed.");
					specialInteraction = true;
				}
				break;
			}
			
			
			if (specialInteraction == false) {
				Print.println(TrawelColor.RESULT_PASS+"The gift disappears, but nothing else happens.");
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
			return TrawelColor.F_SPECIAL+"Forest"+TrawelColor.COLOR_RESET;
		case SKY:
			return TrawelColor.F_SPECIAL+"Sky"+TrawelColor.COLOR_RESET;
		}
		return "ERR";
	}
	
	public enum AltarForce{
		SKY, FOREST
		//TODO: blood
	}
	
	@Override
	public String getIntro() {
		float rel = getRelation()*Rand.randFloat();
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
