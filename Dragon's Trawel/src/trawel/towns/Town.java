package trawel.towns;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
import trawel.PrintEvent;
import trawel.WorldGen;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.Person.PersonType;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.Feat;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.RaceFactory;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.QuestReactionFactory;
import trawel.time.ContextLevel;
import trawel.time.ContextType;
import trawel.time.TContextOwner;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Connection.ConnectType;
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
	public int background_variant = extra.randRange(1,3);
	
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
		int j = extra.randRange(2, 5);
		int i = 0;
		while (i < j) {
			addPerson();
			i++;
		}
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
	
	@Override
	public void reload() {
		timeScope = new TimeContext(ContextType.LOCAL,this);
		timeSetup();
		//events = new ArrayList<TimeEvent>();
		for (Feature f: features) {
			f.reload();
		}
	}
	
	public void setGoPrinter(PrintEvent e) {
		if (true) {
			return;//DOLATER disabled for now
		}
		getIsland().getWorld().addPrintEvent("g"+this.name,e);
	}
	public void setFirstPrinter(PrintEvent e) {
		getIsland().getWorld().addPrintEvent("n"+this.name,e);
	}
	
	public void addPerson() {
		Agent o = new Agent(RaceFactory.getDueler(this.getTier()));
		occupants.add(o);
		o.setLocation(this);
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
		
		for (Connection c: connects) {
			switch (c.getType()) {
			case ROAD:
				hasRoads = true;
				break;
			case SHIP:
				hasPort = true;
				break;
			case TELE:
				hasTele = true;
				break;
			}
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
		this.connects.add(c);
	}
	
	public void townProcess() {
		//System.err.println("at town processing");
		timeScope.processEvents(this);
	}

	public void atTown() {
		World w = island.getWorld();
		Player.updateWorld(w);
		if (Player.player.lastTown != this) { 
			if (visited < 2 && w.getAndPrint("n"+this.name)) {
			}else {
				w.getAndPrint("g"+this.name);
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
			extra.println(visitColor+"You are in " + extra.capFirst(name) + ", on the " +island.getWorld().getCalender().dateName() + ".");
		}
		Networking.sendStrong("Discord|desc|Adventuring in " + name +"|");
		Networking.sendStrong("Discord|imagesmall|town|Town|");
		Networking.setArea("main");
		Networking.setBackground("main");
		double[] p = Calender.lerpLocation(Player.player.lastTown);
		float[] b = Player.player.getWorld().getCalender().getBackTime(p[0],p[1]);
		Networking.sendStrong("Backvariant|"+"town"+background_variant+"|"+b[0]+"|"+b[1]+"|");
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
							goConnects(ConnectType.ROAD);
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
							goConnects(ConnectType.TELE);
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
								goConnects(ConnectType.SHIP);
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
						if (Player.getTutorial() && f.getTutorialText() != null) {
							mList.add(new MenuSelectFeature(f) {
	
								@Override
								public String title() {
									return " "+feature.getTutorialText();
								}
								@Override
								public boolean canClick() {
									return false;
								}
							});
						}
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
						return "you";
					}

					@Override
					public boolean go() {
						you();
						return true;
					}
				});
				if (Player.getTutorial()) {
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return " The 'you' menu includes options and your stats.";
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
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "return to " + connects.get(0).otherTown(features.get(0).getTown()).getName();
					}

					@Override
					public boolean go() {
						assert connects.size() == 1;
						Connection c = connects.get(0);
						Town t = c.otherTown(features.get(0).getTown());
						extra.println("You return to " + t.getName());
						Player.addTime(c.getTime());
						if (extra.chanceIn(1,5+Player.player.getPerson().getBag().calculateDrawBaneFor(DrawBane.PROTECTIVE_WARD))) {
							wanderForConnect(c);
						}
						Player.player.setLocation(t);
						return true;
					}
				});
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
							features.get(number).go();
							return true;
						}
					});
					((MenuSelectNumber)mList.get(mList.size()-1)).number = i;
					i++;
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "you";
					}

					@Override
					public boolean go() {
						you();
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
				this.laterAdd(new Lot(this));
			}else {
				extra.println("Not enough "+moneyname+".");
			}

		}

	}
	private void you() {
		extra.println("1 Skills (Character Menu)");
		extra.println("2 Inventory + Compass");
		extra.println("3 Titles and Faction Rep");
		extra.println("4 Quests");
		extra.println("5 Raw Stats");
		extra.println("6 Main Menu");
		extra.println("7 Save");
		extra.println("8 Display Options");
		extra.println("9 Return to " + getName());
		
		switch (extra.inInt(9)) {
		case 5:Player.player.getPerson().displayStats(false);break;
		case 2:
			extra.println("1 View in More Depth");
			extra.println("2 Drawbanes");
			extra.println("3 Path to Unun");
			extra.println("4 Back");
			switch (extra.inInt(4)){
				case 1:
					Player.player.getPerson().getBag().display(1);
				extra.println("You have " + Player.player.emeralds + " emeralds, " + Player.player.rubies +" rubies, and " + Player.player.sapphires +" sapphires.");
				;break;
				case 2: Player.player.getPerson().getBag().playerDiscardDrawBanes(false);break;
				case 3: WorldGen.pathToUnun();
				if (Player.player.getCheating()) {
					extra.println("Advance time a lot?");
					if (extra.yesNo()) {
						Player.addTime(24*30*365);//30 years
					}
				}
				break;
			}
			
			
			break;
		case 3:Player.player.displayTitles();
		Player.player.getPerson().facRep.display();
		if (Player.player.getCheating()) {
			extra.println("Get swole?");
			if (extra.yesNo()) {
				Player.player.getPerson().setFlag(PersonFlag.AUTOLEVEL, true);
				Player.player.getPerson().addXp(99999);
				Weapon w = Player.bag.getHand();
				for (int i = 0; i < 50;i++) {
					w.levelUp();
				}
			}else {
				extra.println("At least work out?");
				if (extra.yesNo()) {
					Player.player.getPerson().addXp(9999);
				}
			}
		}
		break;
		case 4:
			Player.player.showQuests();
			break;
		case 1: Player.player.getPerson().playerSkillMenu();break;
		case 6: 
			extra.println("Really quit? Your progress will not be saved.");
			if (extra.yesNo()) {
				Player.player.kill();
				return;
			}
			break;
		case 9: return;
		case 7:
			extra.println("Really save?");
			extra.println(extra.PRE_RED+"SAVES ARE NOT COMPATIBLE ACROSS VERSIONS");
			if (extra.yesNo()) {
				extra.println("Save to which slot?");
				for (int i = 1; i < 9;i++) {
					extra.println(i+ " slot:"+WorldGen.checkNameInFile(""+i));
				}
				int in = extra.inInt(8);
				extra.println("Saving...");
				WorldGen.plane.prepareSave();
				WorldGen.save(in+"");
				
			} break;
		case 8: mainGame.advancedDisplayOptions();break;
		}
		this.you();
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
				case ROAD:
					if (mainGame.displayTravelText) {
						extra.print("You start to travel to " + t.getName()+"... ");
					}
					if (extra.chanceIn(4,5+Player.player.getPerson().getBag().calculateDrawBaneFor(DrawBane.PROTECTIVE_WARD))) {
						wander(3);
					}
					Player.addTime(c.getTime());
					if (mainGame.displayTravelText) {
						extra.println("You arrive in " + t.getName()+".");
					}
					break;
				case SHIP:
					if (mainGame.displayTravelText) {
						extra.print("You start to sail to " + t.getName() +"... ");
					}
					if (extra.chanceIn(4,5+Player.player.getPerson().getBag().calculateDrawBaneFor(DrawBane.PROTECTIVE_WARD))) {
						wanderShip(3);
					}
					Player.addTime(c.getTime());
					if (mainGame.displayTravelText) {
						extra.println("You arrive in " + t.getName()+".");
					}
					break;
				case TELE:
					if (mainGame.displayTravelText) {
						extra.println("You teleport to " + t.getName()+".");
					}
					Networking.sendStrong("PlayDelay|sound_teleport|1|");
					Player.addTime(c.getTime());
					break;

				}
				Player.player.setLocation(t);
				return true;
			}};
	}
	
	private void goConnects(ConnectType type) {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				for (Connection c: connects) {
					if (c.getType() == type){
						list.add(getConnectMenu(c));
					}
				}
				switch (type) {
				case ROAD:
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
				case SHIP:
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

	private void goTeleporters() {
		int i = 1;
		for (Connection c: connects) {
			if (c.getType() == ConnectType.TELE){
			extra.print(i + " ");
			c.display(1,this);
			i++;}
		}
		extra.println(i + " exit");i++;
		int j = extra.inInt(i-1);
		i = 1;
		for (Connection c: connects) {
			if (c.getType()  == ConnectType.TELE){//teles are free for now
			if (i == j) {
			//extra.print(i + " ");
			Town t = c.otherTown(this);
			Player.addTime(c.getTime());
			extra.println("You teleport to " + t.getName());
			Networking.sendStrong("PlayDelay|sound_teleport|1|");
			Player.player.setLocation(t);
			return;}i++;
			}
		}
		if (i == j) {
			return;//exit to town
		}
		extra.println("Please enter one of the above numbers, ie '1'");
		goTeleporters();
		
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void addFeature(Feature feature) {
		features.add(feature);
		feature.setTownInternal(this);
	}
	
	public void replaceFeature(Feature replaceThis, Feature with) {
		features.add(features.indexOf(replaceThis),with);
		features.remove(replaceThis);
		with.setTownInternal(this);
	}


	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		//events.clear();
		timePassed+=time;
		defenseTimer-=time;
		if (defenseTimer < 0) {
			defenseTimer = 0;
		}
		if (timePassed >= extra.randRange(100,1000)){
			timePassed = 0;
			this.addPerson();
		}
		for (Feature f: features) {
			timeScope.localEvents(f.contextTime(time,calling));
		}
		for (SuperPerson a: occupants) {
			a.passTime(time,calling);
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
		occupants.remove(occupant);
		return occupant;
	}
	
	public void addOccupant(Agent occupant) {//MAYBELATER: a time based variant add 'laterAddOccupant'
		assert occupant != null;
		occupant.setLocation(this);
		occupants.add(occupant);
	}
	
	public boolean removeOccupant(Agent occupant) {
		return occupants.remove(occupant);
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
	
	/**
	 * 
	 * @return open slots
	 */
	public int openSlots(){
		int i = 9-1;
		if (this.hasTeleporters()) {i--;}
		if (this.hasPort()) {i--;}
		if (this.hasRoads()) {i--;}
		/*
		for (Feature f: features) {
			//like this so if I want to make it so it doesn't count certain features later
			i--;
		}*/
		i-=features.size();
		
		return i;
	}
	
	/**
	 * world gen only
	 */
	public void addTravel() {
		this.features.add(new TravelingFeature(this));
	}
	
	public boolean wander(double threshold) {
			Networking.setArea("forest");
			Networking.sendStrong("Discord|imagesmall|grove|Grove|");
			
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
						AIClass.loot(p.getBag(), new Inventory(pLevel, Race.RaceType.PERSONABLE, null, null, null), false,p);
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
					extra.println(extra.PRE_RED + sp.getPerson().getName() + " appears to haunt you!");
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
			Networking.setArea("port");
			return Bumper.go(d,tier,1,this);
	}
	/**
	 * use force to indicate that you should always have a wander if possible, ie if far flung traveling
	 */
	public boolean dockWander(boolean force) {
		return wanderShip(force ? 0 : 3);
	}
	
	private boolean wanderForConnect(Connection c) {
		switch (c.getType()) {
		case ROAD:
			return this.wander(3);
		case SHIP:
			return this.wanderShip(3);
		}
		return false;
	}
	
	public ArrayList<Feature> getQuestLocationsInRange(int i){
		ArrayList<Town> tList = new ArrayList<Town>();
		ArrayList<Town> addList = new ArrayList<Town>();
		tList.add(this);
		for (int v = 0; v < i;v++) {
			for (Town t: tList) {
				for (Connection c: t.getConnects()) {
					if (c.getType() == ConnectType.TELE) {
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
	
}
