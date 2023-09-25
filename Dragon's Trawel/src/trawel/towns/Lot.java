package trawel.towns;

import java.util.List;

import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.fight.Arena;
import trawel.towns.misc.Garden;
import trawel.towns.misc.Garden.PlantFill;
import trawel.towns.nodes.Mine;
import trawel.towns.nodes.NodeFeature;
import trawel.towns.services.Inn;

public class Lot extends Feature {

	private static final long serialVersionUID = 1L;
	private int tier;
	/**
	 * -1 = can add
	 * -2 = added
	 */
	private double constructTime = -1;
	private String construct;
	public Lot(Town town) {
		this.town = town;
		tier = town.getTier();
		name = "lot";
		area_type = Area.LOT;
	}
	
	@Override
	public String getTutorialText() {
		if (construct == null) {
			return "Owned Lot, waiting for build order.";
		}
		return "Owned Lot (Constructing).";
	}
	
	@Override
	public String getTitle() {
		return getName() + (construct != null ? " ("+construct+" in "+extra.F_WHOLE.format(getConstructTime())+" hours)":"");
	}
	
	@Override
	public String getColor() {
		return extra.F_BUILDABLE;
	}
	
	private enum LotType{
		INN("Inn",);
		
		public final String makeName;
		public final LotMoneyFunction mCost, aCost;
		public final LotCreateFunction create;
		LotType(String _name, LotMoneyFunction _mCost, LotMoneyFunction _aCost, LotCreateFunction _create){
			makeName = _name;
			mCost = _mCost;
			aCost = _aCost;
			create = _create;
		}
		
		private boolean checkDo(int tier) {
			if (mCost != null) {
				if (aCost != null) {
					if (!Player.player.getCanBuy(mCost.cost(tier),aCost.cost(tier))) {
						return false;
					}
				}
			}else {
				if (aCost != null) {
					int aether = aCost.cost(tier);
					if (Player.bag.getAether() < aether) {
						return false;
					}
					extra.println();
				}//else is free
			}
			return true;
		}
	}
	
	private static interface LotMoneyFunction{
		public int cost(int tier);
	}
	
	private static interface LotCreateFunction{
		
	}

	@Override
	public void go() {
		if (construct == null) {
			
			float costMult = IEffectiveLevel.unclean(tier);
			int inncost = (int) (costMult*300);
			int arenacost = (int) (costMult*100);
			int minecost = (int) (costMult*300);
			int gardencost = (int) (costMult*20);

			int a_inncost = (int) (costMult*2500);
			int a_arenacost = (int) (costMult*1000);
			int a_minecost = (int) (costMult*5000);
			int a_gardencost = (int) (costMult*500);

			extra.println("What do you want to build? You have "+Player.bag.getAether() + " aether and " + Player.showGold() + ".");
			extra.println("1 inn "+a_inncost + " aether, " + inncost + " "+World.currentMoneyString());
			extra.println("2 arena "+a_arenacost + " aether, " + arenacost + " "+World.currentMoneyString());
			extra.println("3 donate to town");
			extra.println("4 mine "+a_minecost + " aether, " + minecost + " "+World.currentMoneyString());
			extra.println("5 garden "+a_gardencost + " aether, " + gardencost + " "+World.currentMoneyString());
			extra.println("6 exit");

			switch(extra.inInt(6)) {
			case 1:
				for (Feature f: town.getFeatures()) {
					if (f.owner == Player.player && f instanceof Inn) {
						extra.println("You already have an Inn in this town!");
						break;
					}
				}
				if (Player.player.getCanBuy(a_inncost,inncost)) {
					extra.println("Build an inn here?");
					if (extra.yesNo()) {	
						Player.player.doCanBuy(a_inncost,inncost);
						construct = "inn";
						constructTime = 24*3;
						name = "inn under construction";
						town.helpCommunity(1);
					}
				}
				break;
			case 2:
				for (Feature f: town.getFeatures()) {
					if (f.owner == Player.player && f instanceof Arena) {
						extra.println("You already have an Arena in this town!");
						break;
					}
				}
				extra.println("Build an arena here?");
				if (Player.player.getCanBuy(a_arenacost,arenacost)) {
					if (extra.yesNo()) {
						Player.player.doCanBuy(a_arenacost,arenacost);
						construct = "arena";
						constructTime = 24*2;
						name = "arena under construction";
						town.helpCommunity(1);
					}
				}break;
			case 3:
				extra.println("Donate to the town?");
				if (extra.yesNo()) {
					town.replaceFeature(this,new TravelingFeature(this.town));
					town.helpCommunity(3);
					return;
				}
				break;
			case 4:
				for (Feature f: town.getFeatures()) {
					if (f.owner == Player.player && f instanceof Mine) {
						extra.println("You already have a Mine in this town!");
						break;
					}
				}
				if (Player.player.getCanBuy(a_minecost,minecost)) {
					extra.println("Build a mine?");
					if (extra.yesNo()) {
						Player.player.doCanBuy(a_minecost,minecost);
						construct = "mine";
						constructTime = 24*7;
						name = "mine under construction";
						town.helpCommunity(1);
					}
				}
				break;
			case 5:
				for (Feature f: town.getFeatures()) {
					if (f.owner == Player.player && f instanceof Garden) {
						extra.println("You already have a Garden in this town!");
						break;
					}
				}
				if (Player.player.getCanBuy(a_gardencost,gardencost)) {
					extra.println("Build a garden?");
					if (extra.yesNo()) {
						Player.player.doCanBuy(a_gardencost,gardencost);
						construct = "garden";
						constructTime = 24;
						name = "garden under construction";
						town.helpCommunity(1);
					}
				}break;
			case 6: return;
			}
			
			if (construct != null) {
				tutorialText = "Lot: " + construct + " under construction.";
			}
		}else {
			extra.println("Your " + construct + " is being built.");
		}
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (constructTime >= 0) {
			constructTime-=time;
		if (construct != null && constructTime <= 0) {
			Feature add = null;
			String name = Player.player.getPerson().getNameNoTitle()+"'s ";
			switch (construct) {
			case "inn": add = (new Inn(name+"Inn in " + town.getName(),tier,town,Player.player));break;
			case "arena":add = (new Arena(name+"Arena in " + town.getName(),tier,1,24,200,1,Player.player));break;
			case "mine": add = (new Mine(name+"Mine in " + town.getName(),town,Player.player,NodeFeature.Shape.NONE));break;
			case "garden":
				add = (new Garden(town,name+"Garden in " + town.getName(),0,PlantFill.NONE));
				add.owner = Player.player;
			}
			town.laterReplace(this,add);
			constructTime = -2;
		}
		}
		return null;
	}
	
	public double getConstructTime() {
		return constructTime;
	}

}
