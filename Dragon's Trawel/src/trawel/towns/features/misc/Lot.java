package trawel.towns.features.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.data.FeatureData;
import trawel.towns.features.Feature;
import trawel.towns.features.fight.Arena;
import trawel.towns.features.fight.Forest;
import trawel.towns.features.misc.Garden.PlantFill;
import trawel.towns.features.multi.Inn;
import trawel.towns.features.nodes.Beach;
import trawel.towns.features.nodes.BossNode.BossType;
import trawel.towns.features.nodes.Grove;
import trawel.towns.features.nodes.Mine;
import trawel.towns.features.nodes.NodeFeature.Shape;
import trawel.towns.features.services.Appraiser;
import trawel.towns.features.services.Doctor;
import trawel.towns.features.services.Enchanter;
import trawel.towns.features.services.Oracle;
import trawel.towns.features.services.WitchHut;

public class Lot extends Feature {
	
	static {
		FeatureData.registerFeature(Lot.class, new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" are plots of land that can have other Features built on them for "+TrawelColor.SERVICE_BOTH_PAYMENT+"currencies"+TrawelColor.COLOR_RESET+". "
						+fancyNamePlural()+" can also be "+TrawelColor.SERVICE_FREE+"donated"+TrawelColor.COLOR_RESET+" to the town.");
			}
			
			@Override
			public int priority() {
				return 0;
			}
			
			@Override
			public String name() {
				return "Lot";
			}
			
			@Override
			public String color() {
				return TrawelColor.F_BUILDABLE;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.ADVANCED_SERVICES;
			}
		});
	}

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
		name = "Lot";
	}
	
	@Override
	public String getTitle() {
		return getName() + (construct != null ? " ("+getTypeName(construct)+" in "+Print.F_WHOLE.format(getConstructTime())+" hours)":"");
	}
	
	@Override
	public String nameOfType() {
		if (construct == null) {
			return "Empty Lot";
		}
		return "Construction Site";
	}
	
	@Override
	public Area getArea() {
		return Area.LOT;
	}
	
	private enum LotType{
		TRAVEL("Community Stall","a Community Stall",0,0,null,new LotCreateFunctionInstant() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(3);
				return new TravelingFeature(from.getTown());
			}}, new AlwaysValidTownFunction())
		,DOCTOR("Doctor","a Doctor",200,2000,Doctor.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(2);				
				Doctor d = new Doctor(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Doctor.class,false,false)+" in " + from.getTown().getName(),from.getTown());
				d.setOwner(Player.player);
				return d;
			}

			@Override
			public int constructTime() {
				return 24*5;
			}}, new AlwaysValidTownFunction())
		,INN("Inn","an Inn",300,2500,Inn.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				return new Inn(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Inn.class,false,false)+" in " + from.getTown().getName(),from.getLevel(),from.getTown(),Player.player);
			}

			@Override
			public int constructTime() {
				return 24*7;
			}}, new AlwaysValidTownFunction())
		,APPRAISER("Appraiser","an Appraiser",100,1000,Appraiser.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);				
				Appraiser ap = new Appraiser(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Appraiser.class,false,false)+" in " + from.getTown().getName(),from.getLevel());
				ap.setOwner(Player.player);
				return ap;
			}

			@Override
			public int constructTime() {
				return 24*4;
			}}, new AlwaysValidTownFunction())
		,ORACLE("Oracle","an Oracle",50,1000,Oracle.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);				
				Oracle o = new Oracle(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Oracle.class,false,false)+" in " + from.getTown().getName(),from.getLevel());
				o.setOwner(Player.player);
				return o;
			}

			@Override
			public int constructTime() {
				return 24*3;
			}}, new AlwaysValidTownFunction())
		,WITCH_HUT("Witch Hut","a Witch Hut",150,3000,WitchHut.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);				
				WitchHut wh = new WitchHut(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(WitchHut.class,false,false)+" in " + from.getTown().getName(),from.getTown());
				wh.setOwner(Player.player);
				return wh;
			}

			@Override
			public int constructTime() {
				return 24*5;
			}}, new AlwaysValidTownFunction())
		,ARENA("Arena","an Arena",100,1000,Arena.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				return new Arena(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Arena.class,false,false)+" in " + from.getTown().getName(),from.getLevel(),1,24d,0d,0,Player.player);
			}

			@Override
			public int constructTime() {
				return 24*3;
			}}, new AlwaysValidTownFunction())
		,ENCHANTER("Enchanter","an Enchanter",200,5000,Enchanter.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);				
				Enchanter e = new Enchanter(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Enchanter.class,false,false)+" in " + from.getTown().getName(),from.getLevel());
				e.setOwner(Player.player);
				return e;
			}

			@Override
			public int constructTime() {
				return 24*5;
			}}, new AlwaysValidTownFunction())
		,MINE("Mine","a Mine",300,5000,Mine.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				Mine m = new Mine(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Mine.class,false,false)+" in " + from.getTown().getName(), from.getTown()
						,40,from.getLevel(),Shape.NONE,BossType.NONE);
				m.setOwner(Player.player);
				return m;
			}

			@Override
			public int constructTime() {
				return 24*12;
			}}, new ValidTownFunction() {

				@Override
				public boolean isValid(Lot from) {
					switch (from.getTown().getIsland().getIslandType()) {
						default: case ISLAND:
							return true;
						case POCKET://can't make mines in pocket dimensions
							return false;
					}
				}})
		,BEACH("Beach","a Beach",100,4000,Beach.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				Beach b = new Beach(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Beach.class,false,false)+" in " + from.getTown().getName(),from.getTown(),30,from.getLevel(),Shape.NONE,BossType.NONE);
				b.setOwner(Player.player);
				return b;
			}

			@Override
			public int constructTime() {
				return 24*9;
			}}, new ValidTownFunction() {

				@Override
				public boolean isValid(Lot from) {
					return from.getTown().hasPort();//can only make beaches on towns with port connections
				}})
		,GROVE("Grove","a Grove",100,3000,Grove.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				Grove g = new Grove(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Grove.class,false,false)+" in " + from.getTown().getName(),from.getTown(),30,from.getLevel());
				g.setOwner(Player.player);
				return g;
			}

			@Override
			public int constructTime() {
				return 24*7;
			}}, new ValidTownFunction() {

				@Override
				public boolean isValid(Lot from) {
					for (Feature f: from.getTown().getFeatures()) {
						if (f instanceof Forest) {
							return true;// can only be made in towns with a forest (without a grove already)
						}
					}
					return false;//no forest, can't make
				}})
		,DOCKS("Docks","a Docks",150,1500,Docks.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				Docks d = new Docks(Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Docks.class,false,false)+" in " + from.getTown().getName(),from.getTown());
				d.setOwner(Player.player);
				return d;
			}

			@Override
			public int constructTime() {
				return 24*5;
			}}, new ValidTownFunction() {

				@Override
				public boolean isValid(Lot from) {
					return from.getTown().hasPort();//can only make beaches on towns with port connections
				}})
		,GARDEN("Garden","a Garden",20,500,Garden.class,new LotCreateFunctionLater() {

			@Override
			public Feature makeFeature(Lot from) {
				from.getTown().helpCommunity(1);
				Garden g = new Garden(from.town,Player.player.getPerson().getNameNoTitle()+"'s "+FeatureData.getName(Garden.class,false,false)+" in " + from.getTown().getName(),0,PlantFill.NONE);
				g.setOwner(Player.player);
				return g;
			}

			@Override
			public int constructTime() {
				return 24;
			}}, new AlwaysValidTownFunction())
		;
		
		public final String realName, nameString;
		public final int mCost, aCost;
		public final LotCreateFunction create;
		public final ValidTownFunction check;
		public final Class<? extends Feature> blockingType;
		LotType(String _name,String _nameString, int _mCost, int _aCost, Class<? extends Feature> _block,LotCreateFunction _create,ValidTownFunction _check){
			realName = _name;
			nameString = _nameString;
			mCost = _mCost;
			aCost = _aCost;
			create = _create;
			blockingType = _block;
			check = _check;
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
							Print.println("You already have one "+getTypeName(this)+" underway in "+t.getName()+"!");
							return false;
						}
					}
					if (f.getOwner() == Player.player && blockingType.isInstance(f)) {
						Print.println("You already have one "+getTypeName(this)+" in "+t.getName()+"!");
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
					Print.println("Build one "+getTypeName(this) +" for "+aether + " Aether and "+World.currentMoneyDisplay(money)+"?");
				}else {
					//just money no aether
					int money = getMCost(tier);
					if (Player.bag.getGold() < money) {
						Print.println(TrawelColor.RESULT_ERROR+"Not Enough "+World.currentMoneyString() + ", have only " +Player.bag.getGold()+".");
						return false;
					}
					Print.println("Build one "+getTypeName(this) +" for "+World.currentMoneyDisplay(money)+"?");
				}
			}else {
				if (aCost != 0) {//just aether no money
					int aether = getACost(tier);
					if (Player.bag.getAether() < aether) {
						Print.println(TrawelColor.RESULT_ERROR+"Not enough Aether, have only " + Player.bag.getAether()+".");
						return false;
					}
					Print.println("Build one "+getTypeName(this) +" for "+aether + " Aether?");
				}else {
					//is free
					Print.println("Build one "+getTypeName(this)+"?");
				}
				
			}
			//displayed question prior
			return Input.yesNo();
		}
	}
	
	@FunctionalInterface
	private static interface ValidTownFunction{
		public boolean isValid(Lot from);
	}
	
	private static class AlwaysValidTownFunction implements ValidTownFunction{
		@Override
		public boolean isValid(Lot from) {
			return true;
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
			Feature f = makeFeature(from);
			f.reload();//important to give it it's context
			from.town.replaceFeature(from,f);
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
			Print.println("Your "+getTypeName(type)+ " will be built in " + Print.F_TWO_TRAILING.format(constructTime) + " hours.");
		}else {
			Print.println("Built: " + getTypeName(type));
		}
		
	}
	
	private static String getTypeName(LotType type){
		return type.blockingType == null ? type.realName : FeatureData.getName(type.blockingType,true,false);
	}

	@Override
	public void go() {
		if (construct == null) {
			final List<LotType> validTypes = new ArrayList<Lot.LotType>();
			for (LotType lt: LotType.values()) {
				if (validToBuild(town,lt.blockingType) && lt.check.isValid(this)) {
					validTypes.add(lt);
				}
			}
			Input.menuGo(new ScrollMenuGenerator(validTypes.size(), "last <>", "next <>") {
				
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
					final LotType type = validTypes.get(i);
					return Collections.singletonList(
							new MenuSelect() {

								@Override
								public String title() {
									int aether = type.getACost(tier);
									int money = type.getMCost(tier);
									String color = "";
									if (aether == 0 && money == 0) {
										color = TrawelColor.SERVICE_FREE;
									}else {
										if (aether == 0) {
											color = TrawelColor.SERVICE_CURRENCY;
										}else {
											color = TrawelColor.SERVICE_BOTH_PAYMENT;
										}
									}
									return "Build "+color+getTypeName(type)
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
					return Collections.singletonList(new MenuBack("Leave."));
				}
			});
		}else {
			Print.println("Your " + getTypeName(construct) + " is being built. "+Print.F_TWO_TRAILING.format(constructTime)+" hours remain.");
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
	
	private static boolean validToBuild(Town t, Class<? extends Feature> blockingType) {
		if (blockingType == null) {
			return true;
		}
		for (Feature f: t.getFeatures()) {
			if (f instanceof Lot) {
				Lot la = (Lot)f;
				if (la.construct != null && la.construct.blockingType.equals(blockingType)) {
					return false;
				}
			}
			//no longer needs to own to block making another
			if (blockingType.isInstance(f)) {//f.getOwner() == Player.player && 
				return false;
			}
		}
		return true;
	}

}
