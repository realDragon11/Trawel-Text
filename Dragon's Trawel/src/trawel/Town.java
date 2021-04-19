package trawel;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import trawel.fort.FortQual;
import trawel.townevents.TownFlavorFactory;
import trawel.townevents.TownTag;
import trawel.fort.FortFeature;
import trawel.fort.FortHall;

/**
 * 
 * @author Brian Malone 
 * 5/30/2018
 */

public class Town implements java.io.Serializable{

	private boolean hasPort;
	private boolean hasTeleporters;
	private Point location;
	private Island island;
	private String name;
	private int tier;
	private double timePassed;
	private ArrayList<Connection> connects;
	private ArrayList<Feature> features, removeList, addList;
	private ArrayList<SuperPerson> occupants;
	private PrintEvent goPrinter, newPrinter;
	private boolean hasBeen;
	private List<Person> helpers = new ArrayList<Person>();
	private double defenseTimer = 0;
	private boolean isFort = false;
	private List<FortQual> fQuals;
	public List<TownTag> tTags = new ArrayList<TownTag>();
	private Town leaveTown;
	public int visited = 0;
	
	public Town() {
		connects = new ArrayList<Connection>();
		features = new ArrayList<Feature>();
		removeList = new ArrayList<Feature>();
		addList = new ArrayList<Feature>();
		occupants = new ArrayList<SuperPerson>();
		hasTeleporters = false;
		hasPort = false;
	}
	public Town(String name, int tier, Island island, Point location) {
		this();
		this.name = name;
		this.tier = tier;
		this.island = island;
		this.location = location;
		timePassed = 0;
		int j = extra.randRange(2, 5);
		int i = 0;
		while (i < j) {
			addPerson();
			i++;
		}
		island.addTown(this);
	}
	
	public Town(String name, int tier, Island island, Point location,List<FortQual> fQuals, Town lTown) {
		this();
		this.isFort = true;
		this.name = name;
		this.tier = tier;
		this.island = island;
		this.location = location;
		this.fQuals = fQuals;
		this.leaveTown = lTown;
		timePassed = 0;
		features.add(new FortHall(tier,this));
		island.addTown(this);
	}
	
	public void setGoPrinter(PrintEvent e) {
		goPrinter = e;
	}
	public void setFirstPrinter(PrintEvent e) {
		newPrinter = e;
	}
	
	public void addPerson() {
		Agent o = new Agent(RaceFactory.getDueler(this.getTier()));
		occupants.add(o);
		o.setLocation(this);
	}
	
	public boolean hasPort() {
		return hasPort;
	}
	public void setHasPort(boolean hasPort) {
		this.hasPort = hasPort;
	}
	public boolean hasTeleporters() {
		return hasTeleporters;
	}
	public void setHasTeleporters(boolean hasTeleporters) {
		this.hasTeleporters = hasTeleporters;
	}
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}
	public Island getIsland() {
		return island;
	}
	public void setIsland(Island island) {
		this.island = island;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTier() {
		return tier;
	}
	public void setTier(int tier) {
		this.tier = tier;
	}
	public void generate() {
		this.generate(((int)Math.random()*8)+1);
	}
	
	public void generate(int tier) {
		this.name = extra.capFirst((String)extra.choose(randomLists.randomElement(),randomLists.randomColor()));
		this.tier = tier;
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
		
		for (int j = 0;j < tier;j++) {
			addRandomFeature(tier);
		}
	}
	
	private void addRandomFeature(int tier) {
		//TODO: add all possible features
		while (true) {
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
		}
	}
	public ArrayList<Connection> getConnects() {
		return connects;
	}
	public void addConnection(Connection c) {
		this.connects.add(c);
	}

	public void atTown() {
		Player.world = island.getWorld();
		Player.player.world2 = island.getWorld();
		int i = 1;
		if (Player.player.lastTown != this) {
			if (!hasBeen && newPrinter != null) {
				newPrinter.print();
			}else {
				if (goPrinter != null) {
					goPrinter.print();}
			}
		}
		hasBeen = true;
		Player.player.lastTown = this;
		switch (visited) {
		case 0: Networking.sendColor(Color.ORANGE);break;
		case 1: Networking.sendColor(Color.YELLOW);break;
		case 2: Networking.sendColor(Color.BLUE);break;
		case 3: Networking.sendColor(Color.GREEN);break;
		}
		if (visited < 2) {
			visited = 2;
		}
		extra.println("You are in " + extra.capFirst(name) + ", on the " +island.getWorld().getCalender().dateName() + ".");
		Networking.sendStrong("Discord|desc|Adventuring in " + name +"|");
		Networking.sendStrong("Discord|imagesmall|town|Town|");
		Networking.setArea("main");
		Networking.charUpdate();
		if (isFort) {
			doFort();
			return;
		}
		if (Player.player.townEventTimer <=0 && extra.chanceIn(2,3)) {
			if (TownFlavorFactory.go(.5,this.getTier(),this)) {
			Player.player.townEventTimer = extra.randRange(20,24*7);
			}
		}
		if (this.hasTeleporters()) {
			Networking.sendColor(Color.GREEN);
			extra.println(i + " Teleport Shop.");
			i++;
		}
		
		if (this.hasPort()) {
			Networking.sendColor(Color.GREEN);
			extra.println(i + " Shipyard");
			i++;
		}
		for (Feature f: features) {
			if (!TravelingFeature.class.isInstance(f) || ((TravelingFeature)f).hasSomething) {
			Networking.sendColor(f.getColor());
			extra.println(i + " " + extra.capFirst(f.getName()));
			if (Player.getTutorial()) {
				f.printTutorial();}
			i++;
			}
		}
		if (openSlots() > 0) {
			extra.println(i  + " Buy Lot");
			if (Player.getTutorial()) {
				extra.println("Buying a lot will allow you to add a new building to this town, with enough gold.");
			}
			i++;
		}
		
	
		if (hasRoads()) {
			Networking.sendColor(Color.GREEN);
			extra.println(i + " Roads");
			i++;
		
		if (Player.getTutorial()) {
			extra.println("Roads are the most basic way to travel from town to town.");
			extra.println("Try exploring the town a bit before moving on!");
		}
		}
		
		extra.println(i + " You");i++;
		if (Player.getTutorial()) {
			extra.println("The you menu includes your stats and saving, among other things.");
		}
		int in = extra.inInt(i-1);
		i = 1;
		if (this.hasTeleporters()) {
			if (in == i) {
				this.goTeleporters();
				return;
			}
			i++;
		}
		if (this.hasPort()) {
			if (in == i) {
				this.goPort();
				return;
			}
			i++;
		}
		for (Feature f: features) {
			if (!TravelingFeature.class.isInstance(f) || ((TravelingFeature)f).hasSomething) {
			if (in == i) {
				f.go();
				return;
			}
			i++;
			}
		}
		if (openSlots() > 0 ) {
			if (in == i) {
				this.buyLot();
				return;
			}
			i++;
		}
		if (hasRoads()) {
		if (in == i) {
			this.goRoads();
			return;
		}i++;}
		
		if (in == i) {
			this.you();
			return;
		}i++;
		
	}

	private void doFort() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "leave";
					}

					@Override
					public boolean go() {
						extra.println("You return to " + leaveTown.getName());
						Player.player.setLocation(leaveTown);
						return true;
					}
				});
				int i = 0;
				for (Feature f: features) {
					mList.add(new MenuSelectNumber() {

						@Override
						public String title() {
							return features.get(number).getName();
						}

						@Override
						public boolean go() {
							features.get(number).go();
							return false;
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
						return false;
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
		int cost = this.getTier()*250;
		extra.println("Buy a lot? "+ cost + " gold. You have " +Player.bag.getGold() + " gold.");
		if (extra.yesNo()) {
			if (Player.bag.getGold()> cost) {
				Player.bag.addGold(-cost);
			extra.println("You buy a lot.");
			visited = 3;
			Networking.sendStrong("Achievement|buy_lot|");
			this.addFeature(new Lot(this));}else {
				extra.println("Not enough gold.");
			}
			
		}
		
	}
	private void you() {
		extra.println("1 Stats");
		extra.println("2 Inventory + Compass");
		extra.println("3 Titles and Faction Rep");
		extra.println("4 Quests");
		extra.println("5 Skills");
		extra.println("6 Main Menu");
		extra.println("7 Save"
				+ ""
				+ "");
		extra.println("8 Toggle Tutorial " + ( Player.getTutorial() ? "Off" : "On"));
		extra.println("9 Return");
		
		
		switch (extra.inInt(9)) {
		case 1:Player.player.getPerson().displayStats();break;
		case 2:
			extra.println("1 View in More Depth");
			extra.println("2 Drawbanes");
			extra.println("3 Path to Unun");
			extra.println("4 Back");
			switch (extra.inInt(4)){
				case 1: Player.player.getPerson().getBag().display(1);
				extra.println("You have " + Player.player.emeralds + " emeralds.");
				;break;
				case 2: Player.player.getPerson().getBag().discardDrawBanes(false);break;
				case 3: WorldGen.pathToUnun();break;
			}
			
			
			break;
		case 3:Player.player.displayTitles();
		Player.player.getPerson().facRep.display();
		break;
		case 4:
			Player.player.showQuests();
			break;
		case 5: Player.player.getPerson().playerLevelUp();break;
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
			Networking.sendColor(Color.RED);
			extra.println("SAVES ARE NOT COMPATIBLE ACROSS VERSIONS");
			if (extra.yesNo()) {
				extra.println("Saving... (this will take a while)");
				WorldGen.save();
				
			} break;
		case 8: Player.toggleTutorial();break;
		}
		this.you();
	}
	private void goRoads() {
		int i = 1;
		for (Connection c: connects) {
			if (c.getType().equals("road")){
			extra.print(i + " ");
			c.display(1,this);
			i++;}
		}
		extra.println(i + " wander around");
		i++;
		extra.println(i + " exit");i++;
		int j = extra.inInt(i-1);
		i = 1;
		for (Connection c: connects) {
			if (c.getType().equals("road")){
			if (i == j) {
			//extra.print(i + " ");
			Town t = c.otherTown(this);
			extra.println("You start to travel to " + t.getName());
			if (extra.chanceIn(4,5+Player.player.getPerson().getBag().calculateDrawBaneFor(DrawBane.PROTECTIVE_WARD))) {
				wander(3);
			}
			Player.addTime(c.getTime());
			extra.println("You arrive in " + t.getName());
			Player.player.setLocation(t);
			return;}
			i++;
			}
		}
		if (i == j) {
			if (!wander(.5)) {
				extra.println("Nothing interesting happens.");
			}
			return;
		}
		i++;
		if (i == j) {
			return;//exit to town
		}
		extra.println("Please enter one of the above numbers, ie '1'");
		goRoads();
	}

	private void goPort() {
		extra.println("1 protect the port");
		int i = 2;
		Networking.setArea("port");
		for (Connection c: connects) {
			if (c.getType().equals("ship")){
			extra.print(i + " ");
			c.display(1,this);
			i++;}
		}
		extra.println(i + " exit");i++;
		int j = extra.inInt(i-1);
		if (j == 1) {
			if (defenseTimer > 0) {
				extra.println("The port can't be defended right now.");
			}else {
			if (Player.player.getPerson().getLevel() >= tier) {
				extra.println("You help defend the port against the drudger onslaught.");
				int eSize = extra.randRange(2,3);
				ArrayList<Person> oallyList = new ArrayList<Person>();
				ArrayList<Person> allyList = new ArrayList<Person>();
				ArrayList<Person> foeList = new ArrayList<Person>();
				for (int o = 0;o < eSize;o++) {
					oallyList.add(popHelper());
					allyList.add(popHelper());
					foeList.add(RaceFactory.makeDrudgerStock(tier-1));
				}
				oallyList.add(Player.player.getPerson());
				allyList.add(Player.player.getPerson());
				foeList.add(RaceFactory.makeDrudgerTitan(tier));
				ArrayList<Person> survivors = mainGame.HugeBattle(Player.world,foeList,allyList);
				boolean pass = false;
				for (Person p: oallyList) {
					if (survivors.contains(p)) {
						pass = true;
						break;
					}
				}
				if (pass) {
					survivors.remove(Player.player.getPerson());
					helpers.addAll(survivors);
					extra.println("You take back the docks. +"+(100*tier)+" gp");
					Player.bag.addGold(100*tier);
					defenseTimer = 3;
				}else {
					defenseTimer = 1;
					extra.println("The docks are overrun.");
				}
				Player.addTime(5);
			}else {
				extra.println("They size you up and then turn you away.");
			}
			}
		}
		i = 2;
		for (Connection c: connects) {
			if (c.getType().equals("ship")){//ships are free for now
			if (i == j) {
			//extra.print(i + " ");
			Town t = c.otherTown(this);
			Player.addTime(c.getTime());
			extra.println("You travel to " + t.getName());
			if (extra.chanceIn(4,5+Player.player.getPerson().getBag().calculateDrawBaneFor(DrawBane.PROTECTIVE_WARD))) {
				wanderShip(3);
			}
			Player.player.setLocation(t);
			return;
			}i++;
			}
		}
		if (i == j) {
			return;//exit to town
		}
		extra.println("Please enter one of the above numbers, ie '1'");
		goPort();
		
	}

	
	private Person popHelper() {
		if (helpers.size() > 0) {
		return helpers.remove(0);
		}
		return RaceFactory.getDueler(tier-1);
	}
	private void goTeleporters() {
		int i = 1;
		for (Connection c: connects) {
			if (c.getType().equals("teleport")){
			extra.print(i + " ");
			c.display(1,this);
			i++;}
		}
		extra.println(i + " exit");i++;
		int j = extra.inInt(i-1);
		i = 1;
		for (Connection c: connects) {
			if (c.getType().equals("teleport")){//ships are free for now
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

	public ArrayList<Feature> getFeatures() {
		return features;
	}

	public void addFeature(Feature feature) {
		features.add(feature);
		feature.town = this;
	}


	public void passTime(double time) {
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
			f.passTime(time);
		}
		for (Feature f: removeList) {
			features.remove(f);
		}
		removeList = new ArrayList<Feature>();
		for (Feature f: addList) {
			features.add(f);
		}
		addList = new ArrayList<Feature>();
		for (SuperPerson a: occupants) {
			a.passTime(time);
		}
	}
	
	public ArrayList<SuperPerson> getOccupants() {
		return occupants;
	}
	
	public SuperPerson popOccupant(SuperPerson occupant) {
		occupants.remove(occupant);
		return occupant;
	}
	
	public void addOccupant(SuperPerson occupant) {
		occupants.add(occupant);
	}
	
	/**
	 * 
	 * @return open slots
	 */
	public int openSlots(){
		int i = 9-1;
		if (this.hasTeleporters) {i--;}
		if (this.hasPort) {i--;}
		if (this.hasRoads()) {i--;}
		/*
		for (Feature f: features) {
			//like this so if I want to make it so it doesn't count certain features later
			i--;
		}*/
		i-=features.size();
		
		return i;
	}
	
	public void addTravel() {
		this.addFeature(new TravelingFeature(this.getTier()));
	}
	
	public boolean hasRoads() {
		for (Connection c: connects) {
			if (c.getType().equals("road")) {
				return true;
			}
		}
		return false;
	}
	public void enqueneRemove(Feature f) {
		this.removeList.add(f);
	}
	public void enqueneAdd(Feature f) {
		this.addList.add(f);
	}
	
	public boolean wander(double threshold) {
		if (mainGame.bumpEnabled == true) {
			Networking.setArea("forest");
			Networking.sendStrong("Discord|imagesmall|grove|Grove|");
			boolean went = Bumper.go(threshold,tier,0);
			
			if (!went && extra.chanceIn(1,3)) {
				Person p = this.island.getWorld().getDeathCheater(tier);
				if (p != null) {
					went = true;
					Networking.sendColor(Color.red);
					extra.println(p.getName() + " is back!");
					Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
					if (winner != p) {
						this.island.getWorld().removeDeathCheater(p);
					}
				}
			}
			return went;
		}
		return false;
	}
	
	private boolean wanderShip(double d) {
		if (mainGame.bumpEnabled == true) {
			Networking.setArea("port");
			return Bumper.go(d,tier,1);
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
					if (c.getType().equals("teleport")) {
						continue;
					}
					for (Town f: c.getTowns()) {
						if (!tList.contains(f) && !addList.contains(f)) {
							addList.add(f);
						}
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
		if (!isFort) {
			return 0;
		}
		int i = 6;
		for (Feature f: features) {
			i-=((FortFeature)f).getSize();
		}
		return i;
	}
}
