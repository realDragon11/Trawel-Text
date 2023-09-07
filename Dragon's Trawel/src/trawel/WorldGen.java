package trawel;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import trawel.battle.Combat.SkillCon;
import trawel.factions.FBox.FSub;
import trawel.personal.DummyPerson;
import trawel.personal.Person;
import trawel.personal.item.DummyInventory;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Player;
import trawel.towns.Connection;
import trawel.towns.Connection.ConnectType;
import trawel.towns.Feature;
import trawel.towns.Island;
import trawel.towns.Plane;
import trawel.towns.Town;
import trawel.towns.World;
import trawel.towns.events.TownTag;
import trawel.towns.fight.Arena;
import trawel.towns.fight.Champion;
import trawel.towns.fight.Forest;
import trawel.towns.fight.Mountain;
import trawel.towns.fight.Slum;
import trawel.towns.fight.Slum.ReplaceFeatureInterface;
import trawel.towns.fort.SubSkill;
import trawel.towns.fort.WizardTower;
import trawel.towns.misc.Docks;
import trawel.towns.misc.Garden;
import trawel.towns.misc.Garden.PlantFill;
import trawel.towns.nodes.Dungeon;
import trawel.towns.nodes.Graveyard;
import trawel.towns.nodes.Grove;
import trawel.towns.nodes.Mine;
import trawel.towns.nodes.NodeConnector;
import trawel.towns.nodes.NodeFeature;
import trawel.towns.nodes.NodeFeature.Shape;
import trawel.towns.services.Altar;
import trawel.towns.services.Altar.AltarForce;
import trawel.towns.services.Appraiser;
import trawel.towns.services.Blacksmith;
import trawel.towns.services.Doctor;
import trawel.towns.services.HeroGuild;
import trawel.towns.services.Inn;
import trawel.towns.services.Library;
import trawel.towns.services.MerchantGuild;
import trawel.towns.services.Oracle;
import trawel.towns.services.RogueGuild;
import trawel.towns.services.Store;
import trawel.towns.services.WitchHut;

public class WorldGen {

	
	public static Plane plane;
	
	/**
	 * used mostly for world generation pre character creation
	 */
	public static World fallBackWorld;
	
	static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration(); //.createDefaultConfiguration();
	static{
		conf.registerClass(String.class,//probably already built in
				Armor.class,Weapon.class,Person.class,
				NodeConnector.class
				,FSub.class
				);
		//I think strings are already registered somehow
		//conf.getClassRegistry().dragonDump();
		//could do something with enums where it writes their names to a list and the ordinal uses the SAVED list
		//unsure how FST truly does it but there was a comment about a TCP full name version
	}
	public static final double distanceScale = 2;//average distance between towns is like 1-3 units
	public static final double footTravelPerHour = 3/distanceScale;
	public static final double shipTravelPerHour =  9/distanceScale;
	public static final double teleTravelPerHour =  40/distanceScale;
	public static final double milesInLata = 69;
	public static final double milesInLonga = 54.6;
	public static final float unitsInLata = (float) (milesInLata/distanceScale);
	public static final float unitsInLonga = (float) (milesInLonga/distanceScale);
	
	
	public static List<DummyPerson> initDummyInvs() {
		List<DummyPerson> dumInvs = new ArrayList<DummyPerson>();
		//11 preset armor sets
		for (int j = 0; j < 12;j++) {
			DummyInventory di = new DummyInventory(j);
			di.resetArmor(0,0,0);
			dumInvs.add(new DummyPerson(di));
		}
		//random 9
		for (int i = 0; i < 10;i++) {
			DummyInventory di = new DummyInventory();
			di.resetArmor(0,0,0);
			dumInvs.add(new DummyPerson(di));
		}
		return dumInvs;
	}
	
	public static World eoano() {
		World w = new World(16,10,"Eoano",41f,-72f);//welcome to new england
		fallBackWorld = w;
		plane = new Plane();
		//Player.updateWorld(w);//we update player world in game start now
		plane.addWorld(w);
		Island pocket = new Island("Pocket Dimension",w);
		
		Island rona = new Island("Rona",w);
		Town homa = new Town("Homa",1,rona,new Point(3,4));
		homa.addFeature(new Store(1,6));
		homa.addFeature(new Arena("Basena Arena",1,1,24,1,476));
		homa.addFeature(new Grove("The Woody Tangle",homa,30,1));
		homa.addFeature(new Champion(4));
		w.setStartTown(homa);
		homa.tTags.add(TownTag.SMALL_TOWN);
		homa.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("Homa is a small quaint town, with a large maze-like forest. The perfect place to hide.");
			}
			
		});
		
		
		Town unun = new Town("Unun",2,rona,new Point(5,4)){
			@Override
			public List<SkillCon> getPassiveSkillCons(int i){
				List<SkillCon> list = new ArrayList<SkillCon>();
				list.add(new SkillCon(SubSkill.FATE,20, i));
				return list;
			}
		};
		addConnection(homa,unun,"road","barrier way");
		unun.addFeature(new Docks("Trade Port (Shipyard)",unun));
		unun.addFeature(new Inn("Trailblazer's Tavern",2,unun,null));
		unun.addTravel();
		unun.addFeature(new MerchantGuild("Eoano's Merchant Guild (Unun)"));
		unun.addFeature(new Dungeon("Tower of Fate",unun,Dungeon.Shape.TOWER,1));
		unun.addFeature(new Slum(unun,"The Ephemeral People's Quarter",true));
		unun.tTags.add(TownTag.CITY);
		unun.tTags.add(TownTag.ADVENTURE);
		unun.tTags.add(TownTag.MERCHANT);
		unun.setFirstPrinter(new PrintEvent(){
			@Override
			public void print() {
				extra.println("The port city of Unun is dominated by a large tower- the Tower of Fate. It's an ancient dungeon home to the mysterious Fatespinner, who seems to ignore the town. Below, on the ground, a prominent inn stands next to the interplanar merchant guild headquarters for this world.");
			}
		});
		
		Town tevar = new Town("Tevar",3,rona,new Point(4,5));
		tevar.addFeature(new Store(2));
		tevar.addFeature(new Arena("Epino Arena",5,3,24*30,150,149));
		//addConnection(homa,tevar,"road","red road");//now you must go through unun
		addConnection(tevar,unun,"road","blue road");
		tevar.addFeature(new Forest("The Forest of Vicissitude",2));
		tevar.addFeature(new Mine("Ole' Tevar Mine",tevar,null,NodeFeature.Shape.NONE));
		tevar.tTags.add(TownTag.MINERALS);
		tevar.setFirstPrinter(new PrintEvent(){
			@Override
			public void print() {
				extra.println("Tevar has the most important mine on this island, said to have been dug by the ancient's own ancestors. The forst of vicissitude engulfs the town, and fell reavers often appear nearby.");
			}
		});
		
		
		Town hemo = new Town("Hemo",3,rona,new Point(5,7));
		addConnection(hemo,tevar,"road","purple road");
		addConnection(hemo,unun,"road","black valley");
		addConnection(hemo,unun,"ship","neglected current");
		Store s = new Store(1,6);
		hemo.addFeature(s);
		hemo.addFeature(new Grove("The Odd Grove",hemo,12,3));
		hemo.addFeature(new Blacksmith(1,s));
		hemo.addFeature(new Garden(hemo,"Communal Garden",1.1f,PlantFill.WITCH));
		hemo.addFeature(new WitchHut("Esoteric Ingredients",hemo));
		hemo.tTags.add(TownTag.DRUDIC);
		hemo.tTags.add(TownTag.ALCHEMY);
		hemo.setFirstPrinter(new PrintEvent(){
			@Override
			public void print() {
				extra.println("Hemo is infamous for the clearing near a witch hut, said to be the only place on the island of Rona that can brew potions properly. Rumor has it the proximity to the forest of vicissitude would normally prevent any success, but a dark rite reversed the town's fortunes.");
			}
			
		});
		
		//TODO: town to the west of hemo that connects tevar and tanak from a landlocked route
		
		Town tanak = new Town("Tanak",5,rona,new Point(4,9));
		addConnection(hemo,tanak,"road","windy pass");
		tanak.addFeature(new Arena("The Gauntlet Cirque below Tanak",4,6,24*3,24*20,1));//lots of bouts
		tanak.addFeature(new Store(4,6));
		tanak.addFeature(new Inn("Cloud Comforts Inn",5,tanak,null));
		tanak.addTravel();
		tanak.addTravel();
		tanak.addFeature(new Champion(8));
		tanak.tTags.add(TownTag.CITY);
		tanak.setFirstPrinter(new PrintEvent(){
			@Override
			public void print() {
				extra.println("The city of the sky, Tanak, stands before you- a floating island looming over the terrain. Below lies a teleport station to arrive at the otherwise inaccessible location.");
				
			}
			
		});
		
		Town lokan = new Town("Lokan",4,rona,new Point(5,10));
		addConnection(lokan,tanak,"road","flat walk");
		addConnection(lokan,hemo,"ship","two way current");
		lokan.addFeature(new Library("Records of Value", lokan));
		lokan.addFeature(new Oracle("Appraiser of Fortune",3));
		lokan.addFeature(new Appraiser("Appraiser of Steel"));
		lokan.addFeature(new Doctor("Appraiser of Wellness",lokan));
		lokan.addTravel();
		lokan.addTravel();
		lokan.tTags.add(TownTag.MYSTIC);
		lokan.setFirstPrinter(new PrintEvent(){
			@Override
			public void print() {
				extra.println("Lokan is an aberrant arrangement of minor services which cropped up around the oracles that settled here.");
			}
		});
		
		Town haka = new Town("Haka",4,rona,new Point(1,10));
		addConnection(lokan,haka,"road","diamond way");
		addConnection(tanak,haka,"road","circle road");
		haka.addFeature(new Arena("Grand Colosseum (daily bout)",3,1,24,12,74));
		haka.addFeature(new Arena("Grand Colosseum (weekly tourny)",3,4,24*7,24*7,30));
		haka.addFeature(new Mountain("Peerless Mountain",3));
		haka.setFirstPrinter(new PrintEvent(){

			@Override
			public void print() {
				extra.println("A giant mountain looms over Haka's colosseum- both are the largest on this island.");
				
			}
			
		});
		Town fortMerida = new Town("Fort Merida",5, rona,(byte) 2,(byte)10, null);
		fortMerida.addFeature(new WizardTower(4));
		addConnection(fortMerida,haka,"road","mountain pass");
		
		
		Island apa = new Island("Apa",w);
		Town alhax = new Town("Alhax",2,apa,new Point(5,2));
		//alhax.addFeature(new Arena("yenona arena",2,5,24*7,3,37));
		addConnection(alhax,unun,"ship","yellow passageway");
		alhax.addFeature(new Docks("Central Shiphub (Shipyard)",alhax));
		alhax.addFeature(new Inn("Lockbox Pub",3,alhax,null));
		alhax.addFeature(new Store("'A Cut Above'",4,5));//high level weapon store
		alhax.addFeature(new Store("'Some of Everything'",3,6));
		alhax.addFeature(new Store(2,4));
		alhax.addFeature(new Store(2,0));
		alhax.tTags.add(TownTag.MERCHANT);
		alhax.tTags.add(TownTag.RICH);
		alhax.tTags.add(TownTag.LAW);
		alhax.setFirstPrinter(new PrintEvent(){
			@Override
			public void print() {
				extra.println("The port city of Alhax links the three islands of the world together. It's island, Apa, is home to many shops and stores. While the merchant's guild is in Unun, the true commerce center is here.");
			}
		});
		
		Town revan = new Town("Revan",4,apa,new Point(3,1));
		addConnection(revan,alhax,"ship","green passageway");
		addConnection(revan,alhax,"road","the tops");
		revan.addFeature(new Store(2,5));
		revan.addFeature(new Store(2,1));
		revan.addFeature(new Store(2,2));
		revan.addFeature(new Store(2,3));
		revan.addFeature(new Altar("Sky Slab",AltarForce.SKY));
		addConnection(revan,tanak,"teleport","the red ritual");
		revan.tTags.add(TownTag.MERCHANT);
		revan.tTags.add(TownTag.DRUDIC);
		revan.setFirstPrinter(new PrintEvent(){
			@Override
			public void print() {
				extra.println("The town of Revan is clustered around a great altar, which fell from the sky according to local lore.");
			}
		});
		
		
		Town arona = new Town("Arona",10,pocket,new Point(1,1));
		addConnection(revan,arona,"teleport","the polka-dot ritual");
		arona.addFeature(new Champion(10));
		arona.addFeature(new Champion(7));
		arona.addFeature(new Store(4,11));
		arona.addFeature(new Store(10,7));//sneak a species store in the middle
		arona.addFeature(new Store(3,11));
		arona.tTags.add(TownTag.ARCANE);

		arona.setFirstPrinter(new PrintEvent(){
			private static final long serialVersionUID = 1L;
			@Override
			public void print() {
				extra.println("The 'town' of Arona, held in a wizard's pocket dimension, employs powerful fighters as guards.");
			}
		});
		
		Island teran = new Island("Teran",w);
		Town yena = new Town("Yena",5,teran,new Point(8,2));
		addConnection(revan,yena,"ship","blue sea");
		addConnection(alhax,yena,"ship","blue sea");
		yena.addFeature(new Dungeon("Dungeon of Fame", yena,NodeFeature.Shape.RIGGED_DUNGEON,3));
		yena.addTravel();
		yena.addTravel();
		yena.addFeature(new HeroGuild("Third Hero's Guild"));
		yena.addFeature(new Champion(6));
		yena.tTags.add(TownTag.ADVENTURE);
		yena.setFirstPrinter(new PrintEvent() {

			@Override
			public void print() {
				extra.println("Yena's ancient Dungeon of Fame is the dwelling place of the primordial being, Yore. The Hero's Guild has long since given up on slaying them, and keeps vigil nearby.");
			}});
		
		Town denok = new Town("Denok",5,teran,new Point(12,1));
		addConnection(denok,yena,"road","apple road");
		denok.addFeature(new Store(4,6));
		denok.addFeature(new Store(5,5));
		denok.addFeature(new Forest("Outlaying Wilds",4));
		denok.addFeature(new Grove("The Shaman's Clearing",denok,20,5));
		denok.addFeature(new Doctor("The Shaman's Hut",denok));
		denok.addFeature(new Mine("Denok's Mine",denok,null,NodeFeature.Shape.NONE));
		denok.tTags.add(TownTag.DRUDIC);
		
		Town erin = new Town("Erin",6,teran,new Point(10,4));
		addConnection(erin,yena,"road","pear road");
		addConnection(erin,denok,"road","orange road");
		erin.addFeature(new Arena("Grandstander's Stands (daily bout)",5,1,24,12,39));
		erin.addFeature(new Inn("Scholar's Respite",5,erin,null));
		erin.addFeature(new Library("Alex's Library",erin));
		erin.addFeature(new Appraiser("Material Patent Offices"));
		erin.tTags.add(TownTag.ARCANE);
		erin.setFirstPrinter(new PrintEvent(){
			@Override
			public void print() {
				extra.println("Erin has the largest library in this world, and scholars from all over gather in it to debate the newest theories.");
				
			}
		});
		
		
		Town placka = new Town("Placka",7,teran,new Point(13,3));
		addConnection(erin,placka,"road","peach road");
		addConnection(placka,denok,"road","pineapple road");
		addConnection(yena,placka,"ship","the yellow sea");
		placka.addFeature(new Docks("The Old Docks (Shipyard)",placka));
		placka.addTravel();
		placka.addTravel();
		placka.addFeature(new Champion(9));
		placka.addFeature(new Dungeon("The Dungeon of Woe",placka,NodeFeature.Shape.NONE,-1));
		placka.tTags.add(TownTag.ADVENTURE);
		
		Town tunka = new Town("Tunka",8,teran,new Point(12,5));
		addConnection(erin,tunka,"road","left-over road");
		addConnection(placka,tunka,"road","diamond road");
		tunka.addFeature(new Graveyard("The Boneyard", tunka));
		tunka.addFeature(new Store("'A Quick Find'",7,6));
		tunka.addFeature(new RogueGuild("Society of Enterprising Nobles"));
		tunka.addFeature(new Slum(tunka,"Forgettables District",false));
		tunka.tTags.add(TownTag.LAWLESS);
		
		//todo port town that just leads to visan
		Town owal = new Town("Owal",9,teran,(byte)13,(byte)7);
		//connects added later
		owal.addFeature(new Docks("The Great Bay", owal));
		
		Town repa = new Town("Repa",10,teran,new Point(14,6));
		addConnection(repa,tunka,ConnectType.ROAD,"right-over road");
		addConnection(repa,owal,ConnectType.ROAD,"Former Glory Road");
		//add connection to a new world area
		erin.addFeature(new Mountain("Mountain Teleporter Brace",9));
		repa.addTravel();
		repa.addTravel();
		repa.addTravel();
		repa.addTravel();
		repa.addFeature(new Inn("Repa's Rest",8,erin,null));
		repa.tTags.add(TownTag.TRAVEL);
		
		
		//TODO: add island to the south of repa and to the east of tanak/lokan
		
		Island epan = new Island("Epan",w);
		//note: all should be barren
		
		Town senal = new Town("Senal",6,epan,(byte)10,(byte)9);
		addConnection(senal,tanak,ConnectType.TELE,"Sea Skip Ritual");
		addConnection(senal,lokan,ConnectType.SHIP,"Bygone Current");
		//keep mountain at *top* to give a sense of it blocking off the rest from the connections, which are always first
		senal.addFeature(new Mountain("Ancient Ridges",8));
		//the rest
		senal.addFeature(new Slum(senal, "Better Futures Company Camp",6, new ReplaceFeatureInterface() {
			
			@Override
			public void printReplaceText() {
				extra.println("You provide the funding needed for the Better Futures company to build a real headquarters.");
			}
			
			@Override
			public Feature generate(Slum from) {
				return new MerchantGuild("Better Futures Central Office");
			}
		}));
		senal.addFeature(new Garden(senal,"Desolate Fields",.2f,PlantFill.BAD_HARVEST));
		
		senal.tTags.add(TownTag.BARREN);
		senal.setFirstPrinter(new PrintEvent() {

			@Override
			public void print() {
				extra.println("A few Eras ago, Senal was used as a trade route. But the island of Epan had its land salted, and civilization moved north, to Alhax. The mountain pass to Quen is oft alleged to be untraversable, only to be proven usable by desperate migrants seeking the Better Futures company here.");
			}});
		
		Town quen = new Town("Quen",9,epan,(byte)12,(byte)10);
		addConnection(quen,senal,ConnectType.ROAD,"Derelict Pass");
		quen.addFeature(new Dungeon("Blasted Palace", quen,30,12, Shape.TOWER, 1));//TODO put some new boss here
		quen.addFeature(new Library("Empire Records Bookstore", quen));
		quen.addFeature(new Dungeon("Crumbling Fort", quen,40,10, Shape.NONE, -1));
		quen.addFeature(new Mine("'The Last Ditch that Failed'", quen, 20,9, Shape.ELEVATOR,-1));
		quen.addFeature(new Grove("Dilapidated Hamlet", quen,100,8));
		quen.tTags.add(TownTag.BARREN);
		quen.tTags.add(TownTag.HISTORY);
		quen.setFirstPrinter(new PrintEvent() {

			@Override
			public void print() {
				extra.println("Quen was once the mighty city that all others aspired to. It first held that pedestal long before written history remains intact. But the countless wars over it eventually destroyed the island of Epan. And tyrants ceased caring over it a few Eras later.");
			}});
		
		Town visan = new Town("Visan",8,epan,(byte)13,(byte)8);
		addConnection(visan,quen,ConnectType.ROAD,"Desolate Plains");
		addConnection(visan,repa,ConnectType.TELE,"Barren Bounce");
		addConnection(visan,senal,ConnectType.TELE,"Through the Fog");
		addConnection(visan,owal,ConnectType.SHIP,"Forgotten Shipping Lane");
		visan.addFeature(new Doctor("Foglung Cure Center", visan));
		visan.addFeature(new Champion(10));
		visan.tTags.add(TownTag.TRAVEL);
		visan.tTags.add(TownTag.BARREN);
		visan.setFirstPrinter(new PrintEvent() {

			@Override
			public void print() {
				extra.println("Like the other towns on the island of Epan, Visan's land is barren. It's current use is as a staging point to break through the magic fog and allow teleporting from Rona to Teran through Epan, but most traders prefer to take the shipping route to the north.");
			}});
		
		addConnection(repa,greap(),ConnectType.TELE,"world teleport (eonao-greap)");
		
		for (World wor: plane.worlds()) {
			townFinal(wor);
		}
		alignConnectFlowsToInns(w);
		
		plane.reload();
		
		return w;
	}
	
	public static Town greap() {
		/**
		 * 
		 * welcome to uhhhh Australia?
		 * <br>
		Paruku (Lake Gregory) Indigenous Protected Area
		Landmark in Sturt Creek, Australia
		 */
		World w = new World(30,20,"Greap",-20f,127.5f);//
		fallBackWorld = w;
		Island apen = new Island("Apen",w);
		plane.addWorld(w);
		Town holik = new Town("Holik", 10, apen, new Point(2,3));
		holik.addFeature(new Oracle("The Worldgreeter",9));
		holik.addFeature(new Doctor("'Universal Compassion'", holik));
		holik.addTravel();
		holik.addTravel();
		holik.addTravel();
		w.setStartTown(holik);
		holik.tTags.add(TownTag.MYSTIC);
		holik.tTags.add(TownTag.RICH);
		holik.tTags.add(TownTag.TRAVEL);
		
		Town yonuen = new Town("Yonuen",11, apen, new Point(4,3));
		addConnection(holik,yonuen,"road","bliz road");
		yonuen.addFeature(new Store(9));
		yonuen.addFeature(new Store(9));
		yonuen.addFeature(new Dungeon("Skysundering Tower", yonuen, Shape.TOWER, 1));
		yonuen.addFeature(new RogueGuild("The Open Adventuring Guild"));
		yonuen.addTravel();
		yonuen.tTags.add(TownTag.ADVENTURE);
		yonuen.tTags.add(TownTag.CITY);
		yonuen.tTags.add(TownTag.LAWLESS);
		
		Town unika = new Town("Unika",12, apen, new Point(3,5));
		addConnection(holik,unika,"road","ren road");
		addConnection(yonuen,unika,"road","tenka road");
		unika.addFeature(new Arena("'Lucky Break'",12,1,24,12,135));
		unika.addFeature(new Grove("Unika's Backyard",unika));
		unika.addFeature(new Champion(15));
		unika.tTags.add(TownTag.SMALL_TOWN);
		//unika.addFeature(new Inn("unika inn",10,unika,null));
		
		Town peana = new Town("Peana",12, apen, new Point(2,7));
		addConnection(holik,peana,"road","blue road");
		addConnection(unika,peana,"road","green road");
		peana.addFeature(new Inn("'Dirges for the Damned'",12,peana,null));
		peana.addFeature(new Store("'Tyrant's Treasures'",10,11));//oddity store
		//peana.addFeature(new Arena("Deadsoul's Folly",10,1,24,12,135));
		//peana.addFeature(new Appraiser("Peana Appraiser"));
		peana.addFeature(new Mine("Staircase to Hell", peana,75,10,NodeFeature.Shape.ELEVATOR,2));
		peana.tTags.add(TownTag.HELLISH);
		
		peana.setFirstPrinter(new PrintEvent(){
			@Override
			public void print() {
				extra.println("Peana is the site of a large Mine with a singular purpose: to breach into hell. Those who completed this task have since long been lost to the ages- but their work remains, and a Throne of Hell was established there.");
			}
		});
		
		Town inka = new Town("Inka",12, apen, new Point(4,7));
		addConnection(unika,inka,"road","youn road");
		addConnection(inka,peana,"road","era road");
		inka.addFeature(new Mine("First Striking Shaft", inka,60,8,NodeFeature.Shape.NONE,-1));
		inka.addFeature(new Mine("Motherload Mine", inka,30,14,NodeFeature.Shape.NONE,-1));
		inka.addFeature(new Mine("Deep Vein Dig", inka,80,12,NodeFeature.Shape.NONE,-1));
		inka.addFeature(new Slum(inka,"Miner's Subtown",true));
		inka.addFeature(new Store("M. Hardhat's Shop",12,0));
		inka.addTravel();
		inka.tTags.add(TownTag.CITY);
		inka.tTags.add(TownTag.MINERALS);
		
		Town pipa = new Town("Pipa",13, apen, new Point(4,7));
		addConnection(inka,pipa,"road","mystery road");
		pipa.addFeature(new WitchHut("Oak Coven's Hut",pipa));
		pipa.addFeature(new Grove("Deciduous Sprawl",pipa));
		pipa.addFeature(new Altar("Thorny Throne",AltarForce.FOREST));
		pipa.addFeature(new Library("'Primal Knowledge'", pipa));
		pipa.tTags.add(TownTag.ALCHEMY);
		pipa.tTags.add(TownTag.DRUDIC);
		pipa.tTags.add(TownTag.HIDDEN);
		
		//TODO: need more islands
		
		return holik;
	}
	
	private static void townFinal(World w) {
		for (Island i: w.getIslands()) {
			for (Town t: i.getTowns()) {
				t.detectConnectTypes();
				for (Feature f: t.getFeatures()) {
					f.init();
				}
				t.postFeatureSetup();
				t.postInit();
			}
		}
		
	}
	
	public static void addConnection(Town t1, Town t2,String type, String name) {
		ConnectType ct;
		switch (type) {
		case "road":
			ct = ConnectType.ROAD;break;
		case "ship":
			ct = ConnectType.SHIP;break;
		case "teleport":
			ct = ConnectType.TELE;break;
		default:
			throw new RuntimeException("invalid connection type " + type);
		}
		addConnection(t1,t2,ct,name);
	}

	public static void addConnection(Town t1, Town t2,ConnectType type, String name) {
		Connection connect = new Connection(name,t1,t2,type);
		t1.addConnection(connect);
		t2.addConnection(connect);
	}
	
	public static double distanceBetweenTowns(Town t1,Town t2,ConnectType connectType) {
		if (!t1.getIsland().getWorld().equals(t2.getIsland().getWorld())) {
			return 100/teleTravelPerHour;
		}
		switch (connectType) {
		case ROAD:
			return pointDistance(t1,t2)/footTravelPerHour;
		case SHIP:
			return 1+pointDistance(t1,t2)/shipTravelPerHour;//ships have 1 hour of starting and ending time
		case TELE:
			return 3+(pointDistance(t1,t2)/teleTravelPerHour);//teleporters have 3 hours of rituals + a distance modifier on that
		}
		//fallback
		return pointDistance(t1,t2);
	}
	
	public static double pointDistance(Point a, Point b) {
		return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2));		
	}
	public static double pointDistance(Town a, Town b) {
		return Math.sqrt(Math.pow(a.getLocationX()-b.getLocationX(),2)+Math.pow(a.getLocationY()-b.getLocationY(),2));		
	}
	
	public static void save(String str) {
		plane.prepareSave();
		try (FileOutputStream fos = new FileOutputStream("trawel"+str+".save");
				PrintWriter pws =new PrintWriter(fos);
				FSTObjectOutput oos = conf.getObjectOutput()
				){//try with resources
			 pws.write(Player.player.getPerson().getName()
					 +", level " + Player.player.getPerson().getLevel()
					 + ": " +Timestamp.from(Instant.now())
					 +" "+mainGame.VERSION_STRING+"\0");
			 ;
			 oos.writeObject(plane);
			 pws.write(oos.getWritten()+"\0");
			 extra.println(oos.getWritten()+" bytes");
			 pws.flush();
			 fos.flush();
			 fos.write(oos.getBuffer(), 0,oos.getWritten());
			 fos.flush();
			 //oos.flush();
		     //oos.close();
		     extra.println("Saved!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String checkNameInFile(String str) {
		String ret = "";
		try (FileReader fr = new FileReader("trawel"+str+".save"); BufferedReader br = new BufferedReader(fr);){
			ArrayList<Integer> values = new ArrayList<Integer>();
			while (true) {
				int red = br.read();
				if (red == 0) {
					break;
				}
				if (red == -1) {
					extra.println("Invaild file.");
					break;
				}
				values.add(red);
			}
			for (int i = 0; i < values.size();i++) {
				ret+=(char)(int)values.get(i);
			}
			 br.close();
			 fr.close();
		} catch (Exception e) {
			ret = "n/a";
		}
		return ret;
	}


	public static void load(String str) {
		int len;
		try (FileInputStream fos = new FileInputStream("trawel"+str+".save");){
			while (true) {
				String ret = "";

				ArrayList<Integer> values = new ArrayList<Integer>();
				while (true) {
					int red = fos.read();
					if (red == 0) {
						break;
					}
					if (red == -1) {
						extra.println("Invaild file.");
						break;
					}
					values.add(red);
				}
				for (int i = 0; i < values.size();i++) {
					ret+=(char)(int)values.get(i);
				}
				try {
					len = Integer.parseInt(ret);
					break;
				}catch(NumberFormatException e) {

				}
			}
			extra.println(""+len);
			byte buffer[] = new byte[len];
			while (len > 0) {
				len -= fos.read(buffer, buffer.length - len, len);}
			try (FSTObjectInput oos = conf.getObjectInput(buffer)){//with resources
				plane = (Plane) oos.readObject();
			} catch (Exception e) {
				throw e;//throw upchain
			}
			Player.player = plane.getPlayer();
			Player.bag = Player.player.getPerson().getBag();
			Player.player.skillUpdate();
			Player.passTime = 0;
			mainGame.story = Player.player.storyHold;
			extra.getThreadData().world = Player.player.getWorld();
			fos.close();
			plane.reload();
		} catch (ClassNotFoundException | IOException e) {
			if (!mainGame.logStreamIsErr) {
				e.printStackTrace();
			}
			extra.println("Invalid load. Either no save file was found or it was outdated.");
		}

	}

	public static void pathToTown(Town dest) {
		
		try {
			Town curTown = Player.player.getLocation();
			if (curTown == dest) {
				extra.println("You are already in " + dest.getName()+".");
				return;
			}
			List<Connection> connects = WorldGen.aStarTown(curTown,dest);
			
			int i = 0;
			while (curTown != dest) {
				Town nextTown = connects.get(i).otherTown(curTown);
				extra.println(curTown.getName() + "->" + nextTown.getName() + " (" + connects.get(i).getType().desc() + ": " +connects.get(i).getName()+")");
				curTown = nextTown;
				i++;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class PathTown {
		public Town t;
		public int gScore = Integer.MAX_VALUE;
		public int fScore = Integer.MAX_VALUE;
		public PathTown cameFrom = null;
		public PathTown(Town town) {
			t = town;
		}
	}
	public static List<Connection> aStarTown(Town from, Town dest) {
		List<Town> towns = plane.getTowns();
		List<PathTown> nodeList = new ArrayList<PathTown>();
		PathTown start = null;
		for (Town t: towns) {
			nodeList.add(new PathTown(t));
			if (t.equals(from)) {
				start = nodeList.get(nodeList.size()-1);
			}
		}
		
		List<PathTown> openSet = new ArrayList<PathTown>();
		List<PathTown> closedSet = new ArrayList<PathTown>();
		openSet.add(start);
		openSet.get(0).gScore = 0;
		
		openSet.get(0).fScore = heuristic_cost_estimate(openSet.get(0),dest);
		
		//AISpaceNode[][] cameFrom = new AISpaceNode[g.getWidth()][g.getHeight()]; 
		//cameFrom[openSet.get(0).x][openSet.get(0).y] = null;
		
		
		while (openSet.size() > 0) {
			int currentIndex = getLowest(openSet);
			PathTown current = openSet.get(currentIndex);
			if (current.t.equals(dest)) {
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
				explore(current,otherTown, c,nodeList, openSet, closedSet,dest);
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
	
	private static void explore(PathTown current, PathTown otherTown,Connection c, List<PathTown> nodeList,List<PathTown> openSet,List<PathTown> closedSet,Town dest) {
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
        otherTown.fScore = potential_gScore + heuristic_cost_estimate(otherTown,dest);
	}
	
	private static int heuristic_cost_estimate(PathTown cur, Town dest) {
		//could also use connect time so it would likely return the shorter of two equal paths?
		//or idk
		return Math.abs((cur.t.getLocationX()-dest.getLocationX())) + Math.abs((cur.t.getLocationY()-dest.getLocationY()));
	}
	
	private static class EdgeConnection {
		public final Connection connect;
		public double gScore = Double.MAX_VALUE;
		public double fScore = Double.MAX_VALUE;
		public EdgeConnection from;
		public EdgeConnection(Connection c, EdgeConnection _from) {
			connect = c;
			from = _from;
			if (_from == null) {
				gScore = c.getTime();
			}else {
				gScore = _from.gScore+c.getTime();
			}
		}
	}
	
	private static void alignConnectFlowsToInns(World w) {
		Predicate<Town> innCheck = new Predicate<Town>() {
			@Override
			public boolean test(Town town) {
				for (Feature f: town.getFeatures()) {
					if (f instanceof Inn) {
						return true;
					}
				}
				return false;
			}};
		Predicate<Connection> eitherCheck = new Predicate<Connection>() {
			
			@Override
			public boolean test(Connection c) {
				for (Town t: c.getTowns()) {
					if (innCheck.test(t)) {
						return true;
					}
				}
				return false;
			}
		};
		for (Island is: w.getIslands()) {
			for (Town t: is.getTowns()) {
				if (innCheck.test(t)) {
					continue;//has an inn already
				}
				//honestly should probably use hashing or something, idk
				List<EdgeConnection> openSet = new ArrayList<EdgeConnection>();
				//closed set is things we looked at, so we don't repeatedly create edgeconnection holders
				//and also to store our path
				List<EdgeConnection> closedSet = new ArrayList<EdgeConnection>();
				for (Connection c: t.getConnects()) {
					if (!c.isWorldConnection()) {
						EdgeConnection ec = new EdgeConnection(c,null);
						ec.gScore = 0;
						ec.fScore = c.getTime();
						openSet.add(ec);
					}
				}
				
				while (openSet.size() > 0) {
					EdgeConnection current = getLowestEdge(openSet);
					if (eitherCheck.test(current.connect)) {
						EdgeConnection last = null;
						while (current != null) {
							last = current;
							current = current.from;
						}
						t.setConnectFlow(last.connect);
						break;
					}
					openSet.remove(current);
					closedSet.add(current);
					current.fScore = current.gScore+current.connect.getTime();
					Iterator<Connection> it =getAdjacent(current.connect).iterator();
					while (it.hasNext()) {
						Connection c = it.next();
						EdgeConnection e = getOrCreateConnection(c,current,openSet,closedSet);
						if (e.from == null) {
							continue;//this connection is a base connection, ie a source
						}
						double gMaybe = current.fScore + c.getTime();
						if (e.gScore < gMaybe) {
							e.from = current;
							e.gScore = gMaybe;
							e.fScore = gMaybe + 0;//heuristic, we don't use one rn
							if (!openSet.contains(e)) {
								openSet.add(e);
								//we don't care if this fails
								closedSet.remove(e);
							}
						}
					}
					
				}
				assert t.getConnectFlow() != null;
				//that we're not looping back
				assert !t.getConnectFlow().otherTown(t).hasConnectFlow() || t.getConnectFlow().otherTown(t).getConnectFlow() != t.getConnectFlow();
				if (mainGame.debug) {
					System.err.println("Town Flow: " + t.getName() + ": " + t.getConnectFlow().getName() + " to " + t.getConnectFlow().otherTown(t).getName());
				}
			}
		}
	}
	
	private static void addToSets(Town t,List<EdgeConnection> openSet,List<EdgeConnection> closedSet,EdgeConnection from) {
		for (Connection c: t.getConnects()) {
			if (!hasConnection(c,openSet) && !hasConnection(c,closedSet)) {
				Town a = c.getTowns()[0];
				Town b = c.getTowns()[1];
				if (a.getIsland().getWorld() != b.getIsland().getWorld()) {
					continue;
				}
				openSet.add(new EdgeConnection(c,from));
			}
		}
	}
	
	private static Stream<Connection> getAdjacent(Connection eFor){
		//c -> c != eFor.connect
		return Arrays.asList(eFor.getTowns()).stream().flatMap(t -> t.getConnects().stream()).filter(c -> c != eFor);
	}
	
	private static EdgeConnection getLowestEdge(List<EdgeConnection> list) {
		double lowestValue = Double.MAX_VALUE;
		EdgeConnection best = null;
		for(EdgeConnection e: list) {
			if (e.fScore < lowestValue) {
				best = e;
				lowestValue = e.fScore;
			}
		}
		return best;
	}
	private static EdgeConnection getOrCreateConnection(Connection c, EdgeConnection from,List<EdgeConnection> openSet,List<EdgeConnection> closedSet) {
		for (EdgeConnection e: openSet) {
			if (e.connect == c) {
				return e;
			}
		}
		for (EdgeConnection e: closedSet) {
			if (e.connect == c) {
				return e;
			}
		}
		return new EdgeConnection(c,from);
	}
	
	private static boolean hasConnection(Connection c,List<EdgeConnection> set) {
		for (EdgeConnection e: set) {
			if (e.connect == c) {
				return true;
			}
		}
		return false;
	}
}
