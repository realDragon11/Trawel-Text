import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

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
	
	public void setGoPrinter(PrintEvent e) {
		goPrinter = e;
	}
	public void setFirstPrinter(PrintEvent e) {
		newPrinter = e;
	}
	
	public void addPerson() {
		Agent o = new Agent(new Person(tier));
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
		extra.println("You are in " + extra.capFirst(name) + ".");
		Networking.sendStrong("Discord|desc|Adventuring in " + name +"|");
		Networking.sendStrong("Discord|imagesmall|town|Town|");
		Networking.setArea("main");
		Networking.charUpdate();
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

	private void buyLot() {
		if (Player.getTutorial()) {
			extra.println("You can build building on lots you own, extending the facilities of the town.");	
		}
		int cost = this.getTier()*250;
		extra.println("Buy a lot? "+ cost + " gold. You have " +Player.bag.getGold() + " gold.");
		if (extra.yesNo()) {
			if (Player.bag.getGold()> cost) {
				Player.bag.addGold(-cost);
			extra.println("You buy a lot.");
			Networking.sendStrong("Achievement|buy_lot|");
			this.addFeature(new Lot(this));}else {
				extra.println("Not enough gold.");
			}
			
		}
		
	}
	private void you() {
		extra.println("1 Stats");
		extra.println("2 Inventory");
		extra.println("3 Titles");
		extra.println("4 Map");
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
			extra.println("3 back");
			switch (extra.inInt(3)){
				case 1: Player.player.getPerson().getBag().display(1);
				extra.println("You have " + Player.player.emeralds + " emeralds.");
				;break;
				case 2: Player.player.getPerson().getBag().discardDrawBanes();break;
			}
			
			
			break;
		case 3:Player.player.displayTitles();break;
		case 4:
			extra.println("This feature no longer works. It will be eventually fixed.");
			//Player.showMap1();
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
			if (extra.yesNo()) {WorldGen.save();} break;
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
		int i = 1;
		for (Connection c: connects) {
			if (c.getType().equals("ship")){
			extra.print(i + " ");
			c.display(1,this);
			i++;}
		}
		extra.println(i + " exit");i++;
		int j = extra.inInt(i-1);
		i = 1;
		for (Connection c: connects) {
			if (c.getType().equals("ship")){//ships are free for now
			if (i == j) {
			//extra.print(i + " ");
			Town t = c.otherTown(this);
			Player.addTime(c.getTime());
			extra.println("You travel to " + t.getName());
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
			return Bumper.go(threshold,tier);
		}
		return false;
	}
}
