package trawel.towns;

import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import derg.menus.MenuLine;
import derg.menus.ScrollMenuGenerator;
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
import trawel.towns.nodes.BossNode.BossType;
import trawel.towns.nodes.NodeFeature.Shape;
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
		return getName() + (construct != null ? " ("+construct.realName+" in "+extra.F_WHOLE.format(getConstructTime())+" hours)":"");
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
				return 24*4;
			}})
		,ARENA("Arena","an Arena",100,1000,Arena.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				return new Arena(Player.player.getPerson().getNameNoTitle()+"'s Arena in " + from.getTown().getName(),from.getLevel(),1,24d,0d,0,Player.player);
			}

			@Override
			public int constructTime() {
				return 24*2;
			}})
		,MINE("Mine","a Mine",300,5000,Mine.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				Mine m = new Mine(Player.player.getPerson().getNameNoTitle()+"'s Mine in " + from.getTown().getName(), from.getTown()
						,40,from.getLevel(),Shape.NONE,BossType.NONE);
				m.owner = Player.player;
				return m;
			}

			@Override
			public int constructTime() {
				return 24*7;
			}})
		,GARDEN("Garden","a Garden",20,500,Garden.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				Garden g = new Garden(from.town,Player.player.getPerson().getNameNoTitle()+"'s Garden in " + from.getTown().getName(),0,PlantFill.NONE);
				g.owner = Player.player;
				return g;
			}

			@Override
			public int constructTime() {
				return 24;
			}})
		,TRAVEL("Community Stall","a Community Stall",0,0,null,new LotCreateFunctionInstant() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(3);
				return new TravelingFeature(from.getTown());
			}})
		;
		
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
					if (f instanceof Lot) {
						Lot la = (Lot)f;
						if (la.construct != null && la.construct == this) {
							extra.println("You already have "+nameString+" underway in "+t.getName()+"!");
							return false;
						}
					}
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
		if (constructTime > 0) {
			extra.println("Your "+type.realName + " will be built in " + extra.F_TWO_TRAILING.format(constructTime) + " hours.");
		}else {
			extra.println("Built: " + type.realName);
		}
		
	}

	@Override
	public void go() {
		if (construct == null) {
			extra.menuGo(new ScrollMenuGenerator(LotType.values().length, "last <>", "next <>") {
				
				@Override
				public List<MenuItem> header() {
					return Collections.singletonList(new MenuLine() {

						@Override
						public String title() {
							return "What do you want to build? You have "+Player.bag.getAether() + " aether and " + Player.showGold() + ".";
						}});
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
									if (!type.checkDo(Lot.this)) {
										return false;
									}
									assemble(type);
									return true;
								}
							}
							);
				}

				@Override
				public List<MenuItem> footer() {
					return Collections.singletonList(new MenuBack("Leave"));
				}
			});
		}else {
			extra.println("Your " + construct.realName + " is being built. "+extra.F_TWO_TRAILING.format(constructTime)+" hours remain.");
		}
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (constructTime >= 0) {
			constructTime-=time;
			if (construct != null && constructTime <= 0) {
				construct.create.doLater(this);
				constructTime = -2;
			}
		}
		return null;
	}
	
	public double getConstructTime() {
		return constructTime;
	}

}
