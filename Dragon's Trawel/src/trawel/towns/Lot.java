package trawel.towns;

import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
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
	/**
	 * -1 = can add
	 * -2 = added
	 */
	private double constructTime = -1;
	private LotType construct;
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
		INN("Inn","an Inn",300,2500,Inn.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				return new Inn(Player.player.getPerson().getNameNoTitle()+"'s Inn in " + from.getTown().getName(),from.getLevel(),from.getTown(),Player.player);
			}

			@Override
			public int constructTime() {
				return 24*3;
			}});
		
		public final String realName, nameString;
		public final int mCost, aCost;
		public final LotCreateFunction create;
		public final Class<? extends Feature> blockingType;
		LotType(String _name,String _nameString, int _mCost, int _aCost, Class<? extends Feature> _block,LotCreateFunction _create){
			realName = _name;
			nameString = _nameString;
			mCost = _mCost;
			aCost = _aCost;
			create = _create;
			blockingType = _block;
		}
		
		private int getACost(int tier) {
			return Math.round(IEffectiveLevel.unclean(tier)*aCost);
		}
		private int getMCost(int tier) {
			return Math.round(IEffectiveLevel.unclean(tier)*mCost);
		}
		
		private boolean checkDo(Lot lot) {
			int tier = lot.tier;
			if (blockingType != null) {
				Town t = lot.town;
				for (Feature f: t.getFeatures()) {
					if (f.owner == Player.player && blockingType.isInstance(f)) {
						extra.println("You already have "+nameString+" in "+t.getName()+"!");
						return false;
					}
				}
			}
			if (mCost != 0) {
				if (aCost != 0) {
					int money = getMCost(tier);
					int aether = getACost(tier);
					if (!Player.player.getCanBuy(aether,money)) {
						return false;
					}
					extra.println("Build "+nameString +" for "+aether + " Aether and "+World.currentMoneyDisplay(money)+"?");
				}else {
					//just money no aether
					int money = getMCost(tier);
					if (Player.bag.getGold() < money) {
						extra.println("Not Enough "+World.currentMoneyString() + ", have only " +Player.bag.getGold()+".");
						return false;
					}
					extra.println("Build "+nameString +" for "+World.currentMoneyDisplay(money)+"?");
				}
			}else {
				if (aCost != 0) {//just aether no money
					int aether = getACost(tier);
					if (Player.bag.getAether() < aether) {
						extra.println("Not enough Aether, have only " + Player.bag.getAether()+".");
						return false;
					}
					extra.println("Build "+nameString +" for "+aether + " Aether?");
				}else {
					//is free
					extra.println("Build "+nameString+"?");
				}
				
			}
			//displayed question prior
			return extra.yesNo();
		}
	}
	
	private static interface LotCreateFunction{
		/**
		 * contains code to make the feature and award side effect progress
		 */
		public Feature makeFeature(Lot from);
		/**
		 * default implementation includes buying
		 */
		public default void doNow(Lot from) {
			//the type is contained in from.construct for passing ease
			boolean passed = Player.player.doCanBuy(from.construct.getACost(from.getLevel()), from.construct.getMCost(from.getLevel()));
			if (!passed) {
				throw new RuntimeException("Couldn't afford lot!");
			}
		}
		public void doLater(Lot from);
	}
	private static interface LotCreateFunctionInstant extends LotCreateFunction{
		@Override
		public default void doNow(Lot from) {
			LotCreateFunction.super.doNow(from);
			from.town.replaceFeature(from,makeFeature(from));
		}
		@Override
		public default void doLater(Lot from) {
			//empty
		}
	}
	private static interface LotCreateFunctionLater extends LotCreateFunction{
		@Override
		public default void doNow(Lot from) {
			LotCreateFunction.super.doNow(from);
			from.constructTime = constructTime();
		}
		@Override
		public default void doLater(Lot from) {
			from.getTown().laterReplace(from,makeFeature(from));
			from.constructTime = -2;
		}
		public int constructTime();
	}
	
	private void assemble(LotType type) {
		construct = type;
		type.create.doNow(this);
	}

	@Override
	public void go() {
		if (construct == null) {
			extra.menuGo(new ScrollMenuGenerator(LotType.values().length, "last <>", "next <>") {
				
				@Override
				public List<MenuItem> header() {
					return null;
				}
				
				@Override
				public List<MenuItem> forSlot(int i) {
					final LotType type = LotType.values()[i];
					return Collections.singletonList(
							new MenuSelect() {

								@Override
								public String title() {
									int aether = type.getACost(tier);
									int money = type.getMCost(tier);
									return "Build "+type.nameString
											+ (aether > 0 ? " "+aether +" Aether" :"")
											+ (money > 0 ? " "+World.currentMoneyDisplay(money) :"")
											+".";
								}

								@Override
								public boolean go() {
									if (type.checkDo(Lot.this)) {
										return false;
									}
									assemble(type);
									return true;
								}}
							);
				}
				
				@Override
				public List<MenuItem> footer() {
					return Collections.singletonList(new MenuBack("Leave"));
				}
			});
			
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
