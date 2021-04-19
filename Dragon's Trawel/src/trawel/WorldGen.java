package trawel;
import java.awt.Point;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trawel.fort.FortHall;
import trawel.fort.FortQual;
import trawel.fort.WizardTower;
import trawel.townevents.TownTag;

public class WorldGen {

	
	public static Plane plane;
	public static Town lynchPin;

	public static void eoano(World w) {
		plane = new Plane();
		Player.world = w;
		plane.addWorld(w);
		Island pocket = new Island("pocket dimension",w);
		
		Island rona = new Island("rona",w);
		Town homa = new Town("homa",1,rona,new Point(3,4));
		homa.addFeature(new Store(1,6));
		homa.addFeature(new Arena("basena arena",1,1,24,1,476));
		//homa.addFeature(new Well("tiny well"));
		homa.addFeature(new Grove("homa forest",homa));
		//homa.addFeature(new Graveyard("testing graveyard",homa));
		homa.addTravel();
		homa.addFeature(new Champion(4));
		w.setStartTown(homa);
		homa.tTags.add(TownTag.SMALL_TOWN);
		homa.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("You return to homa, the birthplace of your new self.");
			}
			
		});
		homa.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("You enter the town of homa, a sober expression on your face. It's time to start your life anew.");
			}
			
		});
		
		
		Town unun = new Town("unun",2,rona,new Point(5,4));
		lynchPin = unun;
		unun.setHasPort(true);
		addConnection(homa,unun,"road","barrier way");
		unun.addFeature(new Inn("unun inn",2,unun,null));
		unun.addTravel();
		unun.addTravel();
		unun.addFeature(new MerchantGuild("unun merchant guild"));
		unun.addFeature(new Dungeon("tower of fate",unun,Dungeon.Shape.TOWER,0));
		unun.tTags.add(TownTag.CITY);
		unun.tTags.add(TownTag.ADVENTURE);
		unun.tTags.add(TownTag.MERCHANT);
		unun.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("You return to unun, and the tower of fate.");
			}
			
		});
		unun.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("The port city of unun is dominated by a large tower- the tower of fate. It's an ancient dungeon home to the fatespinner. Below, on the ground, a prominent inn stands.");
			}
			
		});
		
		Town tevar = new Town("tevar",2,rona,new Point(4,5));
		tevar.addFeature(new Store(2));
		tevar.addFeature(new Arena("epino arena",5,3,24*30,150,149));
		addConnection(homa,tevar,"road","red road");
		addConnection(tevar,unun,"road","blue road");
		tevar.addFeature(new Forest("the black forest",2));
		tevar.addFeature(new Mine("tevar mine",tevar,null,Mine.Shape.STANDARD));
		tevar.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("You return to the mining town of tevar.");
			}
			
		});
		tevar.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("Tevar has the most important mine on this island.");
			}
			
		});
		
		
		Town hemo = new Town("hemo",2,rona,new Point(5,7));
		addConnection(hemo,tevar,"road","purple road");
		addConnection(hemo,unun,"road","black valley");
		Store s = new Store(1,6);
		hemo.addFeature(s);
		//hemo.addFeature(new Well("nearly dry well"));
		hemo.addFeature(new Grove("the black forest",hemo));
		hemo.addFeature(new Blacksmith(1,s));
		hemo.addFeature(new Champion(1,4,hemo));
		hemo.addFeature(new WitchHut(hemo));
		hemo.tTags.add(TownTag.DRUDIC);
		hemo.tTags.add(TownTag.ALCHEMY);
		hemo.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("Hemo's forest looms ahead.");
			}
			
		});
		
		Town tanak = new Town("tanak",5,rona,new Point(4,9));
		addConnection(hemo,tanak,"road","windy pass");
		tanak.addFeature(new Arena("tanak colosseum",4,6,24*3,24*20,1));
		tanak.addFeature(new Store(4,6));
		tanak.setHasTeleporters(true);
		tanak.addFeature(new Inn("tanak inn",5,tanak,null));
		tanak.addTravel();
		tanak.addTravel();
		tanak.addFeature(new Champion(10));
		tanak.tTags.add(TownTag.CITY);
		tanak.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("You look up at the city of the sky once more, and make your way to the teleport station.");
				
			}
			
		});
		tanak.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("The city of the sky stands before you- a floating island looming over the terrain. Below lies a teleport station to arrive at the otherwise inaccessible location.");
				
			}
			
		});
		
		Town lokan = new Town("lokan",3,rona,new Point(5,10));
		lokan.setHasPort(true);
		//lokan.addFeature(new Gambler("cup dealer","cups",300));
		addConnection(lokan,tanak,"road","flat walk");
		addConnection(lokan,unun,"ship","two way current");
		lokan.addFeature(new Store(3));
		lokan.addFeature(new Oracle("lokan oracle",3));
		lokan.addFeature(new Appraiser("lokan appraiser"));
		lokan.addFeature(new Doctor("Shaman",lokan));
		lokan.addTravel();
		lokan.addTravel();
		lokan.tTags.add(TownTag.MYSTIC);
		lokan.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("The oracle in lokan awaits.");
				
			}
			
		});
		lokan.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("The lokan oracle is famed throughout the land for their accuracy- if you pay them well.");
				
			}
			
		});
		
		Town haka = new Town("haka",3,rona,new Point(1,10));
		addConnection(lokan,haka,"road","diamond way");
		addConnection(tanak,haka,"road","circle road");
		haka.addFeature(new Arena("haka colosseum (daily bout)",3,1,24,12,74));
		haka.addFeature(new Arena("haka colosseum (weekly tourny)",3,4,24*7,24*7,30));
		haka.addFeature(new Mountain("haka mountain",3));
		
		haka.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("The mountain looms over you.");
				
			}
			
		});
		haka.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("A giant mountain looms over the haka colosseum- the largest on this island.");
				
			}
			
		});
		List<FortQual> fqualist = new ArrayList<FortQual>();
		Town fortMerida = new Town("fort merida",4, rona, new Point(1,11),fqualist, haka);
		fortMerida.addFeature(new WizardTower(4));
		addConnection(fortMerida,haka,"road","mountain pass");
		
		
		Island apa = new Island("apa",w);
		Town alhax = new Town("alhax",2,apa,new Point(5,2));
		alhax.setHasPort(true);
		alhax.addFeature(new Arena("yenona arena",2,5,24*7,3,37));
		addConnection(alhax,unun,"ship","yellow passageway");
		alhax.addFeature(new Inn("alhax bar",2,alhax,null));
		alhax.addFeature(new Store(2,4));
		alhax.addFeature(new Store(2,5));
		//alhax.addFeature(new Store(2,6));
		alhax.tTags.add(TownTag.MERCHANT);
		alhax.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("Alhaxian merchant ships surround the port when you arrive.");
				if (Player.player.merchantLevel > 5) {
					extra.println("A few are flying your colors.");
				}
			}
			
		});
		alhax.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("The port city of alhax links the three islands of the world together. It's island, apa, is home to many shops and stores.");
				
			}
			
		});
		
		Town revan = new Town("revan",3,apa,new Point(3,1));
		revan.setHasPort(true);
		addConnection(revan,alhax,"ship","green passageway");
		addConnection(revan,alhax,"road","the tops");
		revan.addFeature(new Store(2,0));
		revan.addFeature(new Store(2,1));
		revan.addFeature(new Store(2,2));
		revan.addFeature(new Store(2,3));
		revan.setHasTeleporters(true);
		revan.addFeature(new Altar());
		addConnection(revan,tanak,"teleport","the red ritual");
		revan.tTags.add(TownTag.MERCHANT);
		revan.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("You return to the altar town.");
				
			}
			
		});
		revan.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("The town of revan is clustered around a great altar, made to glorify it's nature.");
				
			}
			
		});
		
		
		Town arona = new Town("arona",10,pocket,new Point(1,1));
		//s = new Store(2,7);
		//arona.addFeature(s);
		addConnection(revan,arona,"teleport","the polka-dot ritual");
		arona.setHasTeleporters(true);
		//arona.addFeature(new Blacksmith(0,s));
		arona.addFeature(new Champion(10));
		arona.addFeature(new Store(4,8));
		
		arona.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("You return to the pocket dimension.");
				
			}
			
		});
		arona.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("You've enter the town of arona, held in a wizard's pocket dimension.");
				
			}
			
		});
		
		Island teran = new Island("teran",w);
		Town yena = new Town("yena",4,teran,new Point(8,2));
		yena.setHasPort(true);
		addConnection(revan,yena,"ship","blue sea");
		addConnection(alhax,yena,"ship","blue sea");
		//yena.addFeature(new Gambler("cup master","cups",1000));
		//yena.addFeature(new Well("trinity well"));
		yena.addFeature(new Dungeon("Dungeon of Fame", yena,Dungeon.Shape.STANDARD,-1));
		yena.addTravel();
		yena.addTravel();
		yena.addTravel();
		yena.addFeature(new Champion(4));
		yena.tTags.add(TownTag.ADVENTURE);
		
		Town denok = new Town("denok",4,teran,new Point(12,1));
		addConnection(denok,yena,"road","apple road");
		denok.addFeature(new Store(4,3));
		denok.addFeature(new Store(5,5));
		denok.addFeature(new Forest("the white forest",4));
		denok.addFeature(new Grove("the white grove",denok));
		denok.addFeature(new Mine("denok mine",denok,null,Mine.Shape.STANDARD));
		denok.addFeature(new Doctor("Shaman",denok));
		denok.addTravel();
		denok.tTags.add(TownTag.DRUDIC);
		
		Town erin = new Town("erin",5,teran,new Point(10,4));
		addConnection(erin,yena,"road","pear road");
		addConnection(erin,denok,"road","orange road");
		erin.addFeature(new Arena("erin colosseum (daily bout)",5,1,24,12,39));
		erin.addFeature(new Store(5,1));
		erin.addFeature(new Inn("erin inn",5,erin,null));
		erin.addFeature(new Library("erin library",erin));
		erin.addFeature(new Mountain("the white mountain",5));
		erin.addFeature(new Appraiser("erin appraiser"));
		erin.tTags.add(TownTag.ARCANE);
		erin.setGoPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("You return to erin, the library town.");
				
			}
			
		});
		erin.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("Erin has the largest library in the world, and scholars from all over gather in it to debate the newest theories.");
				
			}
			
		});
		
		
		Town placka = new Town("placka",6,teran,new Point(13,3));
		addConnection(erin,placka,"road","peach road");
		addConnection(placka,denok,"road","pineapple road");
		placka.addFeature(new Forest("the white forest", 6));
		placka.setHasPort(true);
		//addConnection(alhax,placka,"ship","the yellow sea");
		addConnection(yena,placka,"ship","the yellow sea");
		placka.addTravel();
		placka.addTravel();
		placka.addFeature(new Champion(6));
		placka.addFeature(new Dungeon("The Dungeon of Woe",placka,Dungeon.Shape.STANDARD,-1));
		placka.tTags.add(TownTag.ADVENTURE);
		
		Town tunka = new Town("tunka",7,teran,new Point(12,5));
		addConnection(erin,tunka,"road","left-over road");
		addConnection(placka,tunka,"road","diamond road");
		tunka.addFeature(new Arena("tunka arena",7,1,50,17,21));
		tunka.addFeature(new Graveyard("tunka graveyard", tunka));
		tunka.addFeature(new Store(7,6));
		tunka.addTravel();
		tunka.addTravel();
		
		Town repa = new Town("repa",8,teran,new Point(14,6));
		addConnection(repa,tunka,"road","right-over road");
		repa.setHasTeleporters(true);//add connection to a new world area
		repa.addTravel();
		repa.addTravel();
		repa.addTravel();
		repa.addTravel();
		repa.addTravel();
		repa.addFeature(new Inn("repa inn",8,erin,null));
		addConnection(repa,greap(),"teleport","world teleport (eonao-greap)");
		
		
		for (World wor: plane.worlds()) {
			townFinal(wor);
		}
	
	}
	
	public static Town greap() {
		World w = new World(30,20,"greap");
		Island apen = new Island("apen",w);
		plane.addWorld(w);
		Town holik = new Town("holik", 9, apen, new Point(2,3));
		holik.setHasTeleporters(true);;
		holik.addFeature(new Oracle("holik oracle",9));
		holik.addTravel();
		holik.addTravel();
		holik.addTravel();
		w.setStartTown(holik);
		holik.tTags.add(TownTag.MYSTIC);
		
		Town yonuen = new Town("yonuen", 9, apen, new Point(4,3));
		addConnection(holik,yonuen,"road","bliz road");
		yonuen.addFeature(new Store(9));
		yonuen.addFeature(new Arena("yonuen arena",9,1,24,3,24));
		yonuen.addFeature(new Library("yonuen library",yonuen));
		yonuen.addTravel();
		yonuen.addTravel();
		yonuen.tTags.add(TownTag.ARCANE);
		
		Town unika = new Town("unika",10, apen, new Point(3,5));
		addConnection(holik,unika,"road","ren road");
		addConnection(yonuen,unika,"road","tenka road");
		unika.addFeature(new Grove("unika forest",unika));
		unika.addFeature(new Champion(10));
		unika.addTravel();
		unika.addFeature(new Inn("unika inn",10,unika,null));
		
		Town peana = new Town("peana",10, apen, new Point(2,7));
		addConnection(holik,peana,"road","blue road");
		addConnection(unika,peana,"road","green road");
		peana.addFeature(new Arena("peana arena",10,1,24,12,135));
		peana.addFeature(new Appraiser("peana appraiser"));
		peana.addFeature(new Store(10,8));
		peana.addFeature(new Mine("staircase to hell", peana, null,Mine.Shape.HELL));
		
		Town inka = new Town("inka",10, apen, new Point(4,7));
		addConnection(unika,inka,"road","youn road");
		addConnection(inka,peana,"road","era road");
		inka.addFeature(new Mine("left mine", inka, null,Mine.Shape.STANDARD));
		inka.addFeature(new Mine("right mine", inka, null,Mine.Shape.STANDARD));
		inka.addTravel();
		inka.addTravel();
		inka.tTags.add(TownTag.CITY);
		
		Town pipa = new Town("inka",11, apen, new Point(4,7));
		addConnection(inka,pipa,"road","mystery road");
		inka.addFeature(new WitchHut(pipa));
		inka.addFeature(new Store(11,9));
		pipa.addFeature(new Grove("witch forest",pipa));
		pipa.tTags.add(TownTag.ALCHEMY);
		
		return holik;
	}
	
	private static void townFinal(World w) {
		for (Island i: w.getislands()) {
			for (Town t: i.getTowns()) {
				for (Feature f: t.getFeatures()) {
					f.init();
				}
			}
		}
		
	}

	public static void addConnection(Town t1, Town t2,String type, String name) {
		Connection connect = new Connection(t1,t2,distanceBetweenTowns(t1,t2),type);
		t1.addConnection(connect);
		t2.addConnection(connect);
		connect.setName(name);
	}
	
	public static double distanceBetweenTowns(Town t1,Town t2) {
		return pointDistance(t1.getLocation(),t2.getLocation());
	}
	
	public static double pointDistance(Point a, Point b) {
		return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2));		
	}
	
	public static void save() {
		   FileOutputStream fos;
		try {
			fos = new FileOutputStream("trawel.save");//Player.player.getPerson().getName()
			 ObjectOutputStream oos = new ObjectOutputStream(fos);
			 oos.writeObject(plane);
		     oos.close();
		     fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		FileInputStream fos;
		try {
			fos = new FileInputStream("trawel.save");
			 ObjectInputStream oos = new ObjectInputStream(fos);
			 plane = (Plane) oos.readObject();
			 Player.player = plane.getPlayer();
			 //World worlda = world;
			 Player.bag = Player.player.getPerson().getBag();
			 Player.passTime = 0;
			 Player.world = Player.player.world2;
			 oos.close();
			 fos.close();
		} catch (ClassNotFoundException | IOException e) {
			extra.println("Invalid load. Either no save file was found or it was outdated.");
		}
		
	}

	public static void pathToUnun() {
		try {
		List<Connection> connects = WorldGen.aStarTown();
		Town curTown = Player.player.getLocation();
		int i = 0;
		while (curTown != lynchPin) {
			extra.println(curTown.getName() + "->" + connects.get(i).otherTown(curTown).getName() + " (" + connects.get(i).getType() + ": " +connects.get(i).getName()+")");
			curTown = connects.get(i).otherTown(curTown);
			i++;
		}}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class PathTown {
		public Town t;
		public int gScore = Integer.MAX_VALUE;
		public int fScore = Integer.MAX_VALUE;
		public PathTown cameFrom = null;
		public PathTown(Town town) {
			t = town;
		}
	}
	public static List<Connection> aStarTown() {
		List<Town> towns = plane.getTowns();
		WorldGen wg = new WorldGen();
		List<PathTown> nodeList = new ArrayList<PathTown>();
		PathTown start = null;
		for (Town t: towns) {
			nodeList.add(wg.new PathTown(t));
			if (t.equals(Player.player.getLocation())) {
				start = nodeList.get(nodeList.size()-1);
			}
		}
		
		List<PathTown> openSet = new ArrayList<PathTown>();
		List<PathTown> closedSet = new ArrayList<PathTown>();
		openSet.add(start);
		openSet.get(0).gScore = 0;
		
		openSet.get(0).fScore = heuristic_cost_estimate(openSet.get(0));
		
		//AISpaceNode[][] cameFrom = new AISpaceNode[g.getWidth()][g.getHeight()]; 
		//cameFrom[openSet.get(0).x][openSet.get(0).y] = null;
		
		
		while (openSet.size() > 0) {
			int currentIndex = getLowest(openSet);
			PathTown current = openSet.get(currentIndex);
			if (current.t.equals(lynchPin)) {
				return reconstruct_path(current.cameFrom, current,nodeList);
			}
			openSet.remove(currentIndex);
			closedSet.add(current);
			for (Connection c: current.t.getConnects()) {
				PathTown otherTown = null;
				Town ot = c.otherTown(current.t);
				for (PathTown t: nodeList) {
					if (t.t.equals(ot)) {
						otherTown = t;
						break;
					}
				}
				explore(current,otherTown, c,nodeList, openSet, closedSet);
			}
		}
		//we couldn't find a path
		return null;
	
	}
	
	private static List<Connection> reconstruct_path(PathTown cameFrom, PathTown current, List<PathTown> nodeList) {
		List<Connection> cList = new ArrayList<Connection>();
		/*
		for (Connection c: lynchPin.getConnects()) {
			if (c.otherTown(lynchPin).equals(cameFrom.t)){
				cList.add(c);
				break;
			}
		}*/
		
		while(current.cameFrom != null) {
			//add a trail of tiles from the end to the start
			for (Connection c: current.t.getConnects()) {
				if (c.otherTown(current.t).equals(cameFrom.t)) {
					current = cameFrom;
					cameFrom = current.cameFrom;
					cList.add(c);
					break;
				}
			}
		}
		
		Collections.reverse(cList);//reverse them
		return cList;
	}


	private static int getLowest(List<PathTown> list) {
		int lowestValue = Integer.MAX_VALUE;
		PathTown bestIndex = null;
		for(PathTown t: list) {
			if (t.fScore < lowestValue) {
				bestIndex = t;
				lowestValue = t.fScore;
			}
		}
		return list.indexOf(bestIndex);
	} 
	
	private static void explore(PathTown current, PathTown otherTown,Connection c, List<PathTown> nodeList,List<PathTown> openSet,List<PathTown> closedSet) {
		if (closedSet.contains(otherTown)) {
			return;//already explored
		}
		if (!openSet.contains(otherTown)) {
			openSet.add(otherTown);
		}
		int potential_gScore = current.gScore + 1;
		
		if (potential_gScore > otherTown.gScore) {
			return;//going through the last node is not a better choice for this node- it already has a better path attached.
		}
		otherTown.cameFrom = current;
		otherTown.gScore = potential_gScore;
        otherTown.fScore = potential_gScore + heuristic_cost_estimate(otherTown);
	}
	
	private static int heuristic_cost_estimate(PathTown cur) {
		
		return Math.abs((cur.t.getLocation().x-lynchPin.getLocation().x)) + Math.abs((cur.t.getLocation().y-lynchPin.getLocation().y));
	}
}
