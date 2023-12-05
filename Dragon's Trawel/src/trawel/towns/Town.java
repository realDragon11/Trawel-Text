package trawel.towns;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import derg.TwinListMap;
import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLast;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.MenuSelectFeature;
import derg.menus.MenuSelectNumber;
import trawel.AIClass;
import trawel.Bumper;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.PrintEvent;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.battle.Combat.SkillCon;
import trawel.personal.Person;
import trawel.personal.Person.PersonType;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Feat;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.Perk;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.QuestReactionFactory;
import trawel.time.ContextLevel;
import trawel.time.ContextType;
import trawel.time.TContextOwner;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Connection.ConnectClass;
import trawel.towns.Connection.ConnectType;
import trawel.towns.Feature.RemoveAgentFromFeatureEvent;
import trawel.towns.events.TownFlavorFactory;
import trawel.towns.events.TownTag;
import trawel.towns.fort.FortFeature;
import trawel.towns.fort.FortHall;
import trawel.towns.misc.Docks;

/**
 * 
 * @author dragon 
 * 5/30/2018
 */

public class Town extends TContextOwner{

	private static final long serialVersionUID = 1L;
	/**
	 * bitmask, use the functions instead
	 * 0 bit = roads
	 * 1 bit = port
	 * 2 bit = teleports
	 * 
	 * 7 bit = is fort
	 */
	private byte hasFlags = 0b00000000;
	private byte locationX, locationY;
	private Island island;
	//WARNING: towns must have unique names within their world
	private final String name;
	private int tier;
	private double timePassed;
	private List<Connection> connects;
	private List<Feature> features;
	private List<Agent> occupants;
	private double defenseTimer = 0;
	public List<TownTag> tTags = new ArrayList<TownTag>();
	public int visited = 0;
	public int background_variant = 1;
	private int openSlots = -1;
	/**
	 * index of connections that ai people will naturally attempt to 'flow' along.
	 * <br>
	 * -1 for none
	 */
	private int connectFlow = -1;
	
	private transient float flowNeed;
	private float occupantDesire;
	
	private int community_helper = 0;
	private String loreText;
	
	//private transient List<TimeEvent> events;
	
	private Town(String name) {
		this.name = name;
		connects = new ArrayList<Connection>();
		features = new ArrayList<Feature>();
		occupants = new ArrayList<Agent>();
	}
	public Town(String name, int tier, Island island, byte x, byte y) {
		this(name);
		this.tier = tier;
		this.island = island;
		locationX = x;
		locationY = y;
		timePassed = 0;
		island.addTown(this);
	}
	public Town(String name, int tier, Island island, Point location) {
		this(name,tier,island,(byte)location.x,(byte)location.y);
	}
	
	public Town(String name, int tier, Island island, byte x, byte y, FortFeature fortOverload) {
		this(name);
		hasFlags |= (1 << 7);//set that it is a fort
		this.tier = tier;
		this.island = island;
		locationX = x;
		locationY = y;
		//this.leaveTown = lTown;
		timePassed = 0;
		features.add(new FortHall(tier,this));
		island.addTown(this);
	}
	
	public float occupantNeed() {
		return flowNeed;
	}
	
	public float occupantGoal() {
		return occupantDesire;
	}
	
	protected void updateOccupantNeed() {
		flowNeed = Math.max(0,1-(occupants.size()/occupantDesire));
	}
	
	@Override
	public void reload() {
		timeScope = new TimeContext(ContextType.LOCAL,this);
		timeSetup();
		updateOccupantNeed();
		//events = new ArrayList<TimeEvent>();
		for (Feature f: features) {
			f.reload();
		}
	}
	
	public void setLoreText(String text) {
		loreText = text;
	}
	
	public void addPerson() {
		Agent o = RaceFactory.getDueler(Math.max(1,getTier()+extra.randRange(-2,2))).setOrMakeAgentGoal(AgentGoal.NONE);
		addOccupant(o);
	}
	
	public boolean isFort() {
		//unsure if best way, but I care more about memory storage than speed, so doesn't need to be fast rn
		return Byte.toUnsignedInt((byte) (hasFlags & (1 << 7))) > 0;
	}
	
	public boolean hasTeleporters() {
		//unsure if best way, but I care more about memory storage than speed, so doesn't need to be fast rn
		return Byte.toUnsignedInt((byte) (hasFlags & (1 << 2))) > 0;
	}
	public boolean hasPort() {
		//unsure if best way, but I care more about memory storage than speed, so doesn't need to be fast rn
		return Byte.toUnsignedInt((byte) (hasFlags & (1 << 1))) > 0;
	}
	public boolean hasRoads() {
		//unsure if best way, but I care more about memory storage than speed, so doesn't need to be fast rn
		return Byte.toUnsignedInt((byte) (hasFlags & (1 << 0))) > 0;
	}
	//https://stackoverflow.com/a/4674055 if you need help understanding
	public void detectConnectTypes() {
		boolean hasRoads = false;
		boolean hasPort = false;
		boolean hasTele = false;
		TwinListMap<Town,Integer> listMap = new TwinListMap<Town, Integer>();
		for (Connection c: connects) {
			Town other = c.otherTown(this);
			listMap.put(other, listMap.getOrDefault(other,0)+1);
			switch (c.getType().type) {
			case LAND:
				hasRoads = true;
				break;
			case SEA:
				hasPort = true;
				break;
			case MAGIC:
				hasTele = true;
				break;
			}
		}
		for (Connection c: connects) {
			c.setDupeNum(listMap.get(c.otherTown(this)));
		}
		
		hasFlags = (byte) (hasRoads ? hasFlags | (1 << 0) : hasFlags & ~(1 << 0));
		hasFlags = (byte) (hasPort ? hasFlags | (1 << 1) : hasFlags & ~(1 << 1));
		hasFlags = (byte) (hasTele ? hasFlags | (1 << 2) : hasFlags & ~(1 << 2));
	}
	public byte getLocationX() {
		return locationX;
	}
	public byte getLocationY() {
		return locationY;
	}
	public Island getIsland() {
		return island;
	}
	public void setIsland(Island island) {
		this.island = island;
	}
	/**
	 * WARNING: towns must have unique names
	 * @return
	 */
	public String getName() {
		return name;
	}
	public int getTier() {
		return tier;
	}
	public void setTier(int tier) {
		this.tier = tier;
	}
	
	public static Town generate(int tier, Island island, byte x, byte y) {
		String name = extra.capFirst(extra.choose(randomLists.randomElement(),randomLists.randomColor()));
		switch (tier) {
		case 1:name+= " Enclave";break;
		case 2:name+= " District";break;
		case 3:name+= " Colony";break;
		case 4:name+= " Town";break;
		case 5:name+= " City";break;
		case 6:name+= " Province";break;
		case 7:name+= " Kingdom";break;
		case 8:name+= " Domain";break;
		case 9:name+= " Realm";break;//bpmFunctions.choose("Kingdom","Kingdom","Colony","Domain","Realm")
		}
		Town t = new Town(name,tier, island, x, y);
		for (int j = 0;j < tier;j++) {
			t.addRandomFeature(tier);
		}
		return t;
	}
	
	private void addRandomFeature(int tier) {
		//undo: add all possible features
		/*while (true) {
			if (!this.hasTeleporters() && Math.random()*100+tier > 96) {
				this.setHasTeleporters(true);
				return;
			}
			if (Math.random()*100+tier > 60) {
				//Store store = new Store();
				//store.generate(tier+(int)(Math.random()*5)-2);
				//this.addStore(store);
				return;
			}
		}*/
	}
	public List<Connection> getConnects() {
		return connects;
	}
	public void addConnection(Connection c) {
		connects.add(c);
	}
	
	public void townProcess() {
		//System.err.println("at town processing");
		timeScope.processEvents(this);
	}
	/*
	public void sendBackVariant() {
		Networking.setBackground("forest");
		double[] p = Calender.lerpLocation(Player.player.lastTown);
		float[] b = Player.player.getWorld().getCalender().getBackTime(p[0],p[1]);
		Networking.sendStrong("Backvariant|forest"+background_variant+"|"+b[0]+"|"+b[1]+"|");
	}*/

	public void atTown() {
		if (Player.player.atFeature != null) {//for loading inside of features
			//doesn't trigger all entering code since the player saved inside of it
			Player.player.atFeature.enter();
			Player.player.atFeature = null;
			return;
		}
		World w = island.getWorld();
		Player.updateWorld(w);
		if (Player.player.lastTown != this) { 
			if (visited < 2 && mainGame.displayFlavorText && loreText != null) {
				extra.println(loreText);
			}
		}
		townProcess();//works because the menu generator below always backs out
		Player.player.lastTown = this;
		String visitColor = extra.PRE_WHITE;
		switch (visited) {
		case 0: visitColor = extra.VISIT_NEW;break;
		case 1: visitColor = extra.VISIT_SEEN;break;
		case 2: visitColor = extra.VISIT_BEEN;break;
		case 3: visitColor = extra.VISIT_DONE;break;
		}
		if (visited < 2) {
			visited = 2;
		}
		if (mainGame.displayLocationalText) {
			extra.println(visitColor+"You are in " + extra.capFirst(name) + ", on the " +island.getWorld().getCalender().dateName() + ". " + island.getWorld().getCalender().stringLocalTime(this)+".");
		}
		Networking.richDesc("Adventuring in " + name);
		Networking.setArea(Networking.Area.TOWN);
		Networking.updateTime();
		Networking.charUpdate();
		if (isFort()) {
			doFort();
			return;
		}
		if (mainGame.displayFlavorText && Player.player.townEventTimer <=0 && extra.chanceIn(2,3)) {
			if (TownFlavorFactory.go(.5,this.getTier(),this)) {//TODO: look into town flavor again
			Player.player.townEventTimer = extra.randRange(18,24*5);
			}
		}
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				if (hasRoads()) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_ROAD+"Roads";
						}

						@Override
						public boolean go() {
							goConnects(ConnectClass.LAND);
							return true;
						}});
					if (Player.getTutorial()) {
						mList.add(new MenuLine() {

							@Override
							public String title() {
								return " Roads are the most basic way to travel from town to town.\nTry exploring the town a bit before moving on!";
							}});
					}
				}
				if (hasTeleporters()) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_TELE+"Teleport Shop";
						}

						@Override
						public boolean go() {
							goConnects(ConnectClass.MAGIC);
							return true;
						}});
					if (Player.getTutorial()) {
						mList.add(new MenuLine() {

							@Override
							public String title() {
								return " Teleporters are like roads, but don't require physical connection. Telport rituals also tend to be much quicker than traveling by foot.";
							}});
					}
				}
				if (hasPort()) {
					Docks d =
							Docks.class.cast(
								features.stream().filter(f -> f instanceof Docks)
								.findAny().orElse(null)
							);
					if (d == null) {
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return extra.PRE_SHIP+"Shipyard";
							}

							@Override
							public boolean go() {
								goConnects(ConnectClass.SEA);
								return true;
							}});
						if (Player.getTutorial()) {
							mList.add(new MenuLine() {

								@Override
								public String title() {
									return " Ports will take you through sea routes to other towns.";
								}});
						}
					}else {
						mList.add(new MenuSelectFeature(d));
					}
				}
				
				for (Feature f: features) {
					if (f.canShow()) {
						mList.add(new MenuSelectFeature(f));
					}
				}
				if (openSlots() > 0 ) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Buy Lot";
						}

						@Override
						public boolean go() {
							buyLot();
							return true;
						}});
					if (Player.getTutorial()) {
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return " Buying a lot will allow you to add a new building to this town, with enough "+World.currentMoneyString()+".";
						}});
					}
				}
				
				mList.add(new MenuLast() {

					@Override
					public String title() {
						return "Player Menu";
					}

					@Override
					public boolean go() {
						Player.player.youMenu();
						return true;
					}
				});
				if (Player.getTutorial()) {
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return " Includes options, saving, and exiting.";
						}});
				}
				return mList;
			}});
		
	}
	

	private void doFort() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(getConnectMenu(connects.get(0)));
				int i = 0;
				int fSize = features.size();//testing this out instead of a foreach
				while (i < fSize) {
					mList.add(new MenuSelectNumber() {

						@Override
						public String title() {
							return features.get(number).getName();
						}

						@Override
						public boolean go() {
							features.get(number).enter();
							Player.player.atFeature = null;
							return true;
						}
					});
					((MenuSelectNumber)mList.get(mList.size()-1)).number = i;
					i++;
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Player Menu";
					}

					@Override
					public boolean go() {
						Player.player.youMenu();
						return true;
					}
				});
				return mList;
			}
			
		});

	}
	private void buyLot() {
		if (Player.getTutorial()) {
			extra.println("You can build buildings on lots you own, extending the facilities of the town.");	
		}
		int cost = (int) (IEffectiveLevel.unclean(getTier())*150);
		String moneyname = World.currentMoneyString();
		extra.println("Buy a lot? "+ cost + " "+moneyname+". You have "
		+ Player.showGold());
		if (extra.yesNo()) {
			if (Player.player.getGold()> cost) {
				Player.player.addGold(-cost);
				extra.println("You buy a lot.");
				visited = 3;
				Networking.unlockAchievement("buy_lot");
				laterAdd(new Lot(this));
				helpCommunity(1);
			}else {
				extra.println("Not enough "+moneyname+".");
			}

		}

	}
	
	public MenuItem getConnectMenu(Connection c) {
		return new MenuSelect() {

			@Override
			public String title() {
				return c.displayLine(Town.this);
			}

			@Override
			public boolean go() {
				Town t = c.otherTown(Town.this);
				switch (c.getType()) {
				case ROAD: case CARV:
					if (c.getType().startTime > 0) {
						Player.addTime(c.getType().startTime);
						mainGame.globalPassTime();
					}
					if (mainGame.displayTravelText) {
						extra.print("You start to travel to " + t.getName()+"... ");
					}
					double wayMark = extra.randFloat()*c.getRawTime();
					Player.addTime(wayMark);
					mainGame.globalPassTime();
					//will get interrupted at random time
					if (extra.chanceIn(4,5+Player.player.getPerson().getBag().calculateDrawBaneFor(DrawBane.PROTECTIVE_WARD))) {
						wander(3);
					}
					Player.addTime(c.getRawTime()-wayMark);
					mainGame.globalPassTime();
					if (mainGame.displayTravelText) {
						extra.println("You arrive in " + t.getName()+".");
					}
					if (c.getType().endTime > 0) {
						Player.addTime(c.getType().endTime);
						mainGame.globalPassTime();
					}
					break;
				case SHIP:
					if (c.getType().startTime > 0) {
						Player.addTime(c.getType().startTime);
						mainGame.globalPassTime();
					}
					if (mainGame.displayTravelText) {
						extra.print("You start to sail to " + t.getName() +"... ");
					}
					//will get interrupted at random time
					double wayMarkS = extra.randFloat()*c.getRawTime();
					Player.addTime(wayMarkS);
					mainGame.globalPassTime();
					if (extra.chanceIn(4,5+Player.player.getPerson().getBag().calculateDrawBaneFor(DrawBane.PROTECTIVE_WARD))) {
						wanderShip(3);
					}
					Player.addTime(c.getRawTime()-wayMarkS);
					mainGame.globalPassTime();
					if (mainGame.displayTravelText) {
						extra.println("You arrive in " + t.getName()+".");
					}
					if (c.getType().endTime > 0) {
						Player.addTime(c.getType().endTime);
						mainGame.globalPassTime();
					}
					break;
				case TELE:
					if (c.getType().startTime > 0) {
						Player.addTime(c.getType().startTime);
						mainGame.globalPassTime();
					}
					if (mainGame.displayTravelText) {
						extra.println("You teleport to " + t.getName()+".");
					}
					Networking.sendStrong("PlayDelay|sound_teleport|1|");
					Player.addTime(c.getRawTime());
					mainGame.globalPassTime();
					//no land teleport message
					if (c.getType().endTime > 0) {
						Player.addTime(c.getType().endTime);
						mainGame.globalPassTime();
					}
					break;

				}
				Player.player.setLocation(t);
				//need to pass time again since we might in a new world which would be behind
				mainGame.globalTimeCatchUp();
				//this is only really an issue in teleport shops, but other connections technically can do this
				//so it's not bad to be robust. note that this doesn't work in docks or forts right now
				return true;
			}};
	}
	
	private void goConnects(ConnectClass type) {
		switch (type) {
		case LAND:
			Networking.setArea(Area.ROADS);
			break;
		case SEA:
			Networking.setArea(Area.PORT);
			break;
		case MAGIC:
			Networking.setArea(Area.MISC_SERVICE);
			break;
		}
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				for (Connection c: connects) {
					if (c.getType().type == type){
						list.add(getConnectMenu(c));
					}
				}
				switch (type) {
				case LAND:
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_MAYBE_BATTLE+"Wander Around";
						}

						@Override
						public boolean go() {
							if (!wander(.5)) {
								extra.println("Nothing interesting happens.");
							}
							return false;
						}});
					break;
				case SEA:
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_MAYBE_BATTLE+"Sail Aimlessly";
						}

						@Override
						public boolean go() {
							if (!wanderShip(1)) {
								extra.println("Nothing interesting happens.");
							}
							return false;
						}});
					break;
				}
				
				list.add(new MenuBack("Back to "+Town.this.getName()));
				return list;
			}});
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void addFeature(Feature feature) {
		features.add(feature);
		feature.setTownInternal(this);
		feature.init();
		postFeatureSetup();
	}
	
	public void replaceFeature(Feature replaceThis, Feature with) {
		features.add(features.indexOf(replaceThis),with);
		features.remove(replaceThis);
		with.setTownInternal(this);
		with.init();
		postFeatureSetup();
	}


	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		//events.clear();
		timePassed-=time;
		/*
		defenseTimer-=time;
		if (defenseTimer < 0) {
			defenseTimer = 0;
		}*/
		if (timePassed <= 0){
			
			if (occupants.size() < occupantDesire) {
				//at less than half capacity, will always add
				if (extra.randFloat() < occupantNeed()+.5) {
					addPerson();
				}
				//check in 0-100 hours
				timePassed = extra.randFloat()*100;
			}else {
				//will potentially fill to double but not more, 50% chance at 1
				if (extra.randFloat() < occupantNeed()*2) {
					addPerson();
				}
				//check in 24-124 hours
				timePassed = 24+(extra.randFloat()*100);
			}
		}
		for (Feature f: features) {
			timeScope.localEvents(f.contextTime(time,calling));
		}
		for (int i = occupants.size()-1; i >=0;i-- ) {
			timeScope.localEvents(occupants.get(i).passTime(time, calling));
		}
		return null;//uses local events
	}
	
	public void laterRemove(Feature f) {
		eventsModified = true;
		timeScope.addEvent(new StructuralFeatureEvent(f,false));
	}
	public void laterAdd(Feature f) {
		eventsModified = true;
		timeScope.addEvent(new StructuralFeatureEvent(f,true));
	}
	public void laterReplace(Feature replaceThis, Feature with) {
		eventsModified = true;
		timeScope.addEvent(new StructuralFeatureEvent(with,replaceThis));
	}
	
	public class StructuralFeatureEvent extends TimeEvent{
		private static final long serialVersionUID = 1L;
		
		public final Feature modify;
		public final boolean adding;
		public final Feature replace;
		
		public StructuralFeatureEvent(Feature modify,boolean adding) {
			this.modify = modify;
			this.adding = adding;
			this.replace = null;
			context = ContextLevel.TOWN;
		}
		
		public StructuralFeatureEvent(Feature add,Feature replace) {
			this.modify = add;
			this.adding = true;
			this.replace = replace;
			context = ContextLevel.TOWN;
		}
		
	}
	
	public List<Agent> getAllOccupants() {
		return occupants;
	}
	
	public Stream<Agent> getPersonableOccupants() {
		return occupants.stream().filter(SuperPerson::isPersonable);
	}
	
	public Agent popAnyOccupant(Agent occupant) {
		return occupants.remove(extra.randRange(0,occupants.size()-1));
	}
	
	public void addOccupant(Agent occupant) {//MAYBELATER: a time based variant add 'laterAddOccupant'
		assert occupant != null;
		occupant.setLocation(this);
		//must add after since set location removes
		occupants.add(occupant);
		updateOccupantNeed();
	}
	
	public boolean removeOccupant(Agent occupant) {
		boolean bool = occupants.remove(occupant);
		if (bool) {
			updateOccupantNeed();
			if (occupant.getLocation() == this) {
				occupant.setLocation(null);
			}
		}
		return bool;
	}
	
	public RemoveAgentFromFeatureEvent laterRemoveAgentAnyFeature(Agent a) {
		for (Feature f: features) {
			RemoveAgentFromFeatureEvent e = f.laterRemoveAgent(a);
			if (e != null) {
				return e;
			}
		}
		return null;
	}
	
	public void removeAllKilled(List<Person> killed) {
		for (Person p: killed) {
			if (p.getSuper() != null) {
				occupants.remove(p.getSuper());
			}
		}
	}
	
	public Agent getRandPersonableOccupant() {
		List<Agent> agentList = new ArrayList<Agent>();
		occupants.stream().filter(SuperPerson::isPersonable).forEach(agentList::add);
		return extra.randList(agentList);
	}
	
	public void resetOpenSlots(){
		int i = 9-1;
		if (this.hasTeleporters()) {i--;}
		if (this.hasPort()) {i--;}
		if (this.hasRoads()) {i--;}
		
		for (Feature f: features) {
			//like this so if I want to make it so it doesn't count certain features later
			if ((f instanceof Docks) && (hasPort())) {//counted in ports
				continue;
			}
			i--;
		}
		assert i >= 0;
		openSlots = i;
	}
	
	public int openSlots(){
		return openSlots;
	}
	
	/**
	 * world gen only
	 */
	public void addTravel() {
		this.features.add(new TravelingFeature(this));
	}
	
	public boolean wander(double threshold) {
			//Quest bumpers
			if (QuestReactionFactory.runMe(this)) {
				return true;
			}
			
			boolean went = Bumper.go(threshold,tier,0,this);
			
			if (!went && extra.chanceIn(1,3)) {
				Agent sp = island.getWorld().getDeathCheater();
				if (sp != null) {
					island.getWorld().removeReoccuringSuperPerson(sp);
					Person p = sp.getPerson();
					went = true;
					int pLevel = Player.player.getPerson().getLevel();
					if (p.getLevel() < pLevel) {
						extra.offPrintStack();
						p.forceLevelUp(pLevel);
						AIClass.loot(p.getBag(), new Inventory(pLevel, Race.RaceType.PERSONABLE, null, null, null), false,p,false);
						extra.popPrintStack();
					}
					int part1 = extra.randRange(0, 1);
					switch (part1) {
					case 0:
						extra.print(extra.PRE_BATTLE+p.getName() +" charges you out of nowhere! They're back for more, and this time they're not fighting fair! ");
						p.setSkillHas(Feat.AMBUSHER);
						break;
					case 1:
						extra.print(extra.PRE_BATTLE+p.getName() +" charges you, screaming bloody murder! Their thirst for blood has not yet been satiated! ");
						p.setSkillHas(Feat.HEMOVORE);
						break;
					default:
						extra.print(extra.PRE_BATTLE+p.getName() + " is back!");
						break;
					}
					switch (extra.randRange(0, 1)) {
					case 0:
						if (p.getPersonType() == PersonType.COWARDLY) {
							extra.println("\"I was once weak and broken... no more! I will be better! I shall not break again!\"");
							p.setTitle(extra.choose(" the Unbreakable"," the Unfettered"));
						}else {
							extra.println("\"You may have broken my body, but not my spirit!\"");
							p.setTitle(extra.choose(" the Unbroken"," the Unfettered"));
						}
						p.setFeat(Feat.UNBREAKABLE);
						p.setPersonType(PersonType.DEATHCHEATED);
						break;
					case 1:
						if (part1 == 1) {
							extra.println("\"Primal forces demand I take back what you took from me!\"");
						}else {
							extra.println("\"I fell, but they picked me back up! Now I stand beside life itself against you!\"");
						}
						p.setTitle(extra.choose(", ","the ") +extra.choose("Life ","Primal ")+extra.choose("Keeper","Defender","Servant","Judge"));
						p.setPerk(Perk.NPC_PRIMAL_MOUNTAIN);//MAYBELATER: more primal types
						p.setPersonType(PersonType.LIFEKEEPER);
						break;
					}
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
						//this.island.getWorld().removeDeathCheater(p);
						//removed earlier, might get re-added in the combat above, which is fine
					}else {
						this.island.getWorld().deathCheaterToChar(sp);
					}
				}
			}
			if (!went && extra.chanceIn(1,10)) {
				Agent sp = island.getWorld().getStalker();
				//does not level up automatically
				if (sp != null) {
					extra.println(extra.PRE_BATTLE + sp.getPerson().getName() + " appears to haunt you!");
					went = true;
					Combat c = Player.player.fightWith(sp.getPerson());
					if (c.playerWon() > 0) {
						island.getWorld().removeReoccuringSuperPerson(sp);
					}//must be slain to get it to go away
				}
			}
			return went;
	}
	
	private boolean wanderShip(double d) {
		//Networking.setArea(Area.PORT);
		return Bumper.go(d,tier,1,this);
	}
	/**
	 * use force to indicate that you should always have a wander if possible, ie if far flung traveling
	 * <br>
	 * or sailingly aimlessly
	 */
	public boolean dockWander(boolean force) {
		return wanderShip(force ? 0 : 3);
	}
	
	public ArrayList<Feature> getQuestLocationsInRange(int i){
		ArrayList<Town> tList = new ArrayList<Town>();
		ArrayList<Town> addList = new ArrayList<Town>();
		tList.add(this);
		for (int v = 0; v < i;v++) {
			for (Town t: tList) {
				for (Connection c: t.getConnects()) {
					if (c.getType().type == ConnectClass.MAGIC) {
						continue;
					}
					Town f = c.otherTown(t);
					if (!tList.contains(f) && !addList.contains(f)) {
						addList.add(f);
					}
				}
			}
			tList.addAll(addList);
			addList.clear();
		}
		ArrayList<Feature> retList = new ArrayList<Feature>();
		for (Town t: tList) {
			for (Feature f: t.getFeatures()){
				if (f.getQRType() != Feature.QRType.NONE) {
					retList.add(f);
				}
			}
		}
		return retList;
	}
	
	public int fortSizeLeft() {
		if (!isFort()) {
			return 0;
		}
		int i = 6;
		for (Feature f: features) {
			i-=((FortFeature)f).getSize();
		}
		return i;
	}
	
	@Override
	public void prepareSave() {
		super.prepareSave();
		for(Feature f: features) {
			f.prepareSave();
		}
	}
	@Override
	public List<TimeEvent> consumeEvents(List<TimeEvent> list) {
		eventsModified = true;
		while (eventsModified) {
			eventsModified = false;
			for (int i = list.size()-1;i >=0;i--) {//backwards for removal reasons
				TimeEvent e = list.get(i);
				if (e.context.tier() <= contextLevel().tier()) {
					if (e instanceof StructuralFeatureEvent) {
						StructuralFeatureEvent sfe = (StructuralFeatureEvent)e;
						if (sfe.adding) {
							if (sfe.replace == null) {
								addFeature(sfe.modify);
							}else {
								replaceFeature(sfe.replace,sfe.modify);
							}
							
							sfe.modify.reload();//important to give it it's context
						}else {
							features.remove(sfe.modify);
						}
						list.remove(i);
					}
				}
			}
		}
		return list;
	}
	@Override
	public ContextLevel contextLevel() {
		return ContextLevel.TOWN;
	}
	
	
	/**
	 * does not count as directly seeing, for port reasons
	 * <br>
	 * a watered down connection
	 */
	public String displayLine(Town from) {
		String visitColor = extra.PRE_WHITE;
		switch (visited) {
		case 0: visitColor = extra.VISIT_NEW;break;
		case 1: visitColor = extra.VISIT_SEEN;break;
		case 2: visitColor = extra.VISIT_BEEN;break;
		case 3: visitColor = extra.VISIT_OWN;break;
		}
		String dirString = "";
		if (from != null) {
			dirString = " ("+Connection.dir(from,this)+")";
		}
		return visitColor + getName() + " {Level: "+getTier()+"}"+dirString;
	}
	public List<SkillCon> getPassiveSkillCons(int forside) {
		//passive skill cons, overridden
		return Collections.emptyList();
	}
	public boolean hasConnectFlow() {
		return connectFlow != -1;
	}
	
	public Connection getConnectFlow() {
		return connects.get(connectFlow);
	}
	
	public void setConnectFlow(Connection c) {
		connectFlow = connects.indexOf(c);//handles 'doesn't have' very nicely
		assert connectFlow < connects.size();
	}
	public void postInit() {
		int j = Math.max(3,(int) (occupantDesire+extra.randRange(-3,3)));
		int i = 0;
		while (i < j) {
			addPerson();
			i++;
		}
	}
	
	public void postFeatureSetup() {
		occupantDesire = 10;
		
		for (Feature f: features) {
			occupantDesire+=f.occupantDesire();
		}
		if (tTags.contains(TownTag.RICH)) {
			occupantDesire+=1;
		}
		if (tTags.contains(TownTag.SMALL_TOWN)) {
			occupantDesire*=.5f;
		}
		if (tTags.contains(TownTag.CITY)) {
			occupantDesire*=1.2f;
			occupantDesire+=4;
		}
		resetOpenSlots();
	}
	
	/**
	 * lower levels can be 'ownership', but higher levels should be only helping the community
	 * <br>
	 * <br>
	 * examples:
	 * <br>
	 * lot ownership: +1
	 * <br>
	 * lot building: +1
	 * <br>
	 * lot donating to town: +3
	 * <br>
	 * most misc side quests: +1
	 * <br>
	 * 'helpful' cleanse/execute quests: +2
	 * <br>
	 * slum reforming: +20
	 * <br>
	 * survive protecting docks: +1 each time
	 */
	public void helpCommunity(int amount) {
		int old = community_helper;
		community_helper+=amount;
		if (old < 1 && community_helper >= 1) {
			Player.player.addAchieve(townHash()+"_helper", getName() + " Member");
		}
		if (old < 5 && community_helper >= 5) {
			Player.player.addAchieve(townHash()+"_helper", getName() + " Minded");
		}
		if (old < 10 && community_helper >= 10) {
			Player.player.addAchieve(townHash()+"_helper", getName() + " Citizen");
		}
		if (old < 20 && community_helper >= 20) {
			Player.player.addAchieve(townHash()+"_helper", getName() + " Community Organizer");
		}
		if (old < 50 && community_helper >= 50) {
			Player.player.addAchieve(townHash()+"_helper", getName() + " Community Leader");
		}
		
	}
	
	public String townHash() {
		//MAYBELATER: for prng generated towns, will need to use an actual hash
		//for these can just use worldname+islandname+name or even just name
		//to dont: could make the hash smaller by storing it as codepoints instead of directly to int digits
		//return getName()+hashCode();
		return island.getName()+getName();
	}
	public boolean hasLore() {
		return loreText != null;
	}
	public String getLore() {
		return loreText;
	}
	
}
