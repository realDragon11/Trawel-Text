package trawel;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import save.KyroManager;
import trawel.battle.Combat.SkillCon;
import trawel.personal.DummyPerson;
import trawel.personal.item.DummyInventory;
import trawel.personal.people.Player;
import trawel.quests.QuestBoardLocation;
import trawel.towns.Connection;
import trawel.towns.Connection.ConnectType;
import trawel.towns.Feature;
import trawel.towns.Island;
import trawel.towns.Island.IslandType;
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
import trawel.towns.nodes.BossNode.BossType;
import trawel.towns.nodes.Dungeon;
import trawel.towns.nodes.Graveyard;
import trawel.towns.nodes.Grove;
import trawel.towns.nodes.Mine;
import trawel.towns.nodes.NodeFeature;
import trawel.towns.nodes.NodeFeature.Shape;
import trawel.towns.services.Altar;
import trawel.towns.services.Altar.AltarForce;
import trawel.towns.services.Appraiser;
import trawel.towns.services.Blacksmith;
import trawel.towns.services.Doctor;
import trawel.towns.services.Enchanter;
import trawel.towns.services.HeroGuild;
import trawel.towns.services.HunterGuild;
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
	
	//average distance between towns is around 1-3 units
	public static final double distanceScale = 10;//2;
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
		
		Island rona = new Island("Rona",w);
		Town homa = new Town("Homa",1,rona,new Point(3,4));
		homa.addFeature(new Store(1,6));
		homa.addFeature(new Arena("Basena Arena",1,1,24,2,476));
		homa.addFeature(new Grove("The Woody Tangle",homa,30,1));
		homa.addFeature(new Doctor("Melissa's Clinic",homa).setIntro("Melissa greets you, 'Don't get many customers not from the Arena, but if you've got something more exotic like a curse, I can fix that.'"));
		w.setStartTown(homa);
		homa.tTags.add(TownTag.SMALL_TOWN);
		homa.setLoreText("Homa is a small quaint town, with a large maze-like forest. The perfect place to hide.");
		
		Town unun = new Town("Unun",2,rona,new Point(5,4)){
			@Override
			public List<SkillCon> getPassiveSkillCons(int i){
				List<SkillCon> list = new ArrayList<SkillCon>();
				list.add(new SkillCon(SubSkill.FATE,20, i));
				return list;
			}
		};
		addConnection(homa,unun,"road","barrier way");
		unun.addFeature(new Docks("Trade Port",unun));
		unun.addFeature(new Inn("Trailblazer's Tavern",2,unun,null));
		unun.addFeature(new MerchantGuild("Eoano's Merchant Guild Headquarters",2));
		unun.addFeature(new Dungeon("Tower of Fate",unun,Dungeon.Shape.TOWER,BossType.FATESPINNER));
		unun.addFeature(new Slum(unun,"The Ephemeral People's Quarter",true));
		unun.addTravel();
		unun.tTags.add(TownTag.CITY);
		unun.tTags.add(TownTag.ADVENTURE);
		unun.tTags.add(TownTag.MERCHANT);
		unun.setLoreText("The port city of Unun is dominated by a large tower- the Tower of Fate. It's an ancient dungeon home to the mysterious Fatespinner, who seems to ignore the town. Below, on the ground, a prominent inn stands next to the interplanar merchant guild headquarters for this world.");
		
		Town tevar = new Town("Tevar",3,rona,new Point(4,5));
		addConnection(tevar,unun,ConnectType.CARV,"blue road");
		tevar.addFeature(new Store(2,6));
		//tevar.addFeature(new Arena("Epino Arena",5,3,24*30,150,149));
		//addConnection(homa,tevar,"road","red road");//now you must go through unun
		tevar.addFeature(new Forest("The Forest of Vicissitude",2));
		tevar.addFeature(new Mine("Ole' Tevar Mine",tevar,null,NodeFeature.Shape.NONE));
		tevar.tTags.add(TownTag.MINERALS);
		tevar.tTags.add(TownTag.UNSETTLING);
		tevar.setLoreText("Tevar has the most important mine on this island, said to have been dug by the ancient's own ancestors. The Forest of Vicissitude engulfs the town, and fell reavers often appear nearby.");
		
		
		Town hemo = new Town("Hemo",3,rona,new Point(6,6));
		addConnection(hemo,tevar,ConnectType.ROAD,"purple road");
		addConnection(hemo,unun,ConnectType.CARV,"black valley");
		addConnection(hemo,unun,"ship","neglected current");
		Store s = new Store(3,6);
		hemo.addFeature(s);
		hemo.addFeature(new Blacksmith(3,s));
		hemo.addFeature(new Grove("The Odd Grove",hemo,12,3));
		hemo.addFeature(new Garden(hemo,"Communal Garden",1.1f,PlantFill.WITCH).setIntro("A sign reads, 'Please replant any materials you harvest here.'"));
		hemo.addFeature(new WitchHut("Esoteric Ingredients",hemo));
		hemo.tTags.add(TownTag.DRUIDIC);
		hemo.tTags.add(TownTag.ALCHEMY);
		hemo.setLoreText("Hemo is infamous for the clearing near a witch hut, said to be the only place on the island of Rona that can brew potions properly. Rumor has it the proximity to the forest of vicissitude would normally prevent any success, but a dark rite reversed the town's fortunes.");;
		
		Town beal = new Town("Beal",4,rona,(byte)2,(byte)7);
		addConnection(tevar,beal,ConnectType.CARV,"Deadlocked Desert");
		beal.addFeature(new Store("Nomad's Market",4,10).setIntro("A well dressed man who looks different from the rest approaches you, 'Welcome to the market, the herders will trade with anyone, but their customs do not permit haggling. We have aether conversion services in the back.'"));
		//put mountain in middle
		beal.addFeature(new Mountain("Rolling Slopes", 3));
		beal.addFeature(new Store("Plain Plains Treaders",4,4));
		beal.addFeature(new Arena("Makeshift Ring",2,1,12,6,3).setIntro("A crude ring is drawn in the dirt, where some nomads are watching two drunk townspeople wail on each other."));
		beal.addTravel();
		beal.tTags.add(TownTag.SMALL_TOWN);
		beal.tTags.add(TownTag.LIVESTOCK);
		beal.setLoreText("More an outpost for the nomadic people who rear their livestock nearby, Beal lies between a desert and a verdant plain, isolated from the nearby coastal town of Hemo by a wall of small but harsh mountains.");
		
		Town tanak = new Town("Tanak",5,rona,new Point(4,9));
		addConnection(hemo,tanak,ConnectType.ROAD,"'Round the Mountain");
		addConnection(beal,tanak,ConnectType.ROAD,"Wildering Plain");
		tanak.addFeature(new Arena("The Gauntlet Cirque below Tanak",4,6,24*3,24*20,1));//lots of bouts
		tanak.addFeature(new HeroGuild("Chantry of Boundless Heroism",5).setIntro("The building looks more like a church than a guild, but the heavy armor and lack of robes on the people here belie its true intent."));
		tanak.addFeature(new Store(5,6));
		tanak.addFeature(new Inn("Cloud Comforts Inn",5,tanak,null).setIntro("'Welcome to Cloud Comforts, the Tavern at the top!'").setOutro("'Thank you for coming to Cloud Comforts, we hope you enjoy your stay in Tanak.'"));
		tanak.addTravel();
		tanak.addTravel();
		//tanak.addFeature(new Champion(8));
		tanak.tTags.add(TownTag.CITY);
		tanak.tTags.add(TownTag.ARCANE);
		tanak.setLoreText("The city of the sky, Tanak, stands before you- a floating island looming over the terrain. Below lies a teleport station to arrive at the otherwise inaccessible location.");
		
		Town lokan = new Town("Lokan",4,rona,new Point(5,10));
		addConnection(lokan,tanak,"road","flat walk");
		addConnection(lokan,hemo,"ship","two way current");
		lokan.addFeature(new Library("Records of Value", lokan));
		lokan.addFeature(new Oracle("Appraiser of Fortune",3));
		lokan.addFeature(new Appraiser("Appraiser of Steel").setIntro("A clerk raises their head to greet you, 'This place is mostly for sentimental reasons, my mentors have spread their teachings well. We can take over your gear, but I doubt we'll find anything new.'"));
		lokan.addFeature(new Doctor("Appraiser of Wellness",lokan));
		lokan.addTravel();
		lokan.addTravel();
		lokan.tTags.add(TownTag.MYSTIC);
		lokan.tTags.add(TownTag.SERVICES);
		lokan.setLoreText("Lokan is an aberrant arrangement of minor services which cropped up around the oracles that settled here.");
		
		Town haka = new Town("Haka",6,rona,new Point(1,10));
		addConnection(lokan,haka,"road","diamond way");
		addConnection(tanak,haka,"road","circle road");
		haka.addFeature(new Arena("Grand Colosseum (daily bout)",4,1,24,12,74));
		haka.addFeature(new Arena("Grand Colosseum (weekly tourny)",5,4,24*7,24*7,30));
		haka.addFeature(new Mountain("Peerless Mountain",6));
		haka.tTags.add(TownTag.VISTAS);
		haka.setLoreText("A giant mountain looms over Haka's colosseum- both are the largest on this island.");

		Town fortMerida = new Town("Fort Merida",6, rona,(byte) 2,(byte)10, null);
		addConnection(fortMerida,haka,ConnectType.PATH,"mountain pass");
		fortMerida.addFeature(new WizardTower(6));
		fortMerida.tTags.add(TownTag.HIDDEN);
		fortMerida.setLoreText("While one of the most well-known forts in the recent years, Fort Merida leverages its annoying location as much as it can.");
		
		
		Island apa = new Island("Apa",w);
		Town alhax = new Town("Alhax",2,apa,new Point(5,2));
		//alhax.addFeature(new Arena("yenona arena",2,5,24*7,3,37));
		addConnection(alhax,unun,"ship","yellow passageway");
		alhax.addFeature(new Docks("Central Shiphub",alhax));
		alhax.addFeature(new Inn("Lockbox Pub",3,alhax,null).setIntro("'Welcome to Lockbox! All rooms have a safe, and the key is different from your room key.'"));
		alhax.addFeature(new Store("'A Cut Above'",4,5));//high level weapon store
		alhax.addFeature(new Store("'Some of Everything'",3,6).setIntro("'We might not have what you want, but I'm certain we have something you'll need.'"));
		alhax.addFeature(new Store(2,4));
		alhax.addFeature(new Store(2,0));
		alhax.tTags.add(TownTag.MERCHANT);
		alhax.tTags.add(TownTag.RICH);
		alhax.tTags.add(TownTag.CITY);
		alhax.tTags.add(TownTag.LAW);
		alhax.setLoreText("The port city of Alhax links the three islands of the world together. It's island, Apa, is home to many shops and stores. While the merchant's guild is in Unun, the true commerce center is here.");
		
		Town revan = new Town("Revan",4,apa,new Point(3,1));
		addConnection(revan,alhax,"ship","green passageway");
		addConnection(revan,alhax,ConnectType.CARV,"the tops");
		revan.addFeature(new Store(2,5));
		revan.addFeature(new Store(2,1));
		revan.addFeature(new Store(2,2));
		revan.addFeature(new Store(2,3));
		revan.addFeature(new Altar("Sky Slab",AltarForce.SKY));
		addConnection(revan,tanak,"teleport","the red ritual");
		revan.tTags.add(TownTag.MERCHANT);
		revan.tTags.add(TownTag.DRUIDIC);
		revan.setLoreText("The town of Revan is clustered around a great altar, which fell from the sky according to local lore.");
		
		Island pocket = new Island("Eureka",w,Island.IslandType.POCKET);
		Town arona = new Town("Arona",10,pocket,new Point(1,1));
		addConnection(revan,arona,"teleport","the polka-dot ritual");
		arona.addFeature(new Champion(10));
		arona.addFeature(new Champion(7));
		arona.addFeature(new Store(4,11));
		//sneak a species store in the middle 'They Knew You, but not the New You.'
		arona.addFeature(new Store("'kNew You'",10,7)
				.setIntro("'Welcome to kNew You, where they knew you but not the new you! We only take trade ins.'")
				.setOutro("'If you are unsatisfied, come back and try out a new you!'")
				);
		arona.addFeature(new Store(3,11));
		arona.addFeature(new Enchanter("Aetheral Advantage",8));
		arona.tTags.add(TownTag.ARCANE);

		arona.setLoreText("The 'town' of Arona, held in a wizard's pocket dimension, employs powerful fighters as guards.");
		
		Island teran = new Island("Teran",w);
		Town yena = new Town("Yena",5,teran,new Point(8,2));
		addConnection(revan,yena,"ship","blue sea");
		addConnection(alhax,yena,"ship","blue sea");
		yena.addFeature(new Dungeon("Dungeon of Fame", yena,NodeFeature.Shape.RIGGED_DUNGEON,BossType.YORE));
		yena.addTravel();
		yena.addTravel();
		yena.addFeature(new HeroGuild("Third Hero's Guild",6));
		yena.addFeature(new Champion(6));
		yena.tTags.add(TownTag.ADVENTURE);
		yena.tTags.add(TownTag.HISTORY);
		yena.setLoreText("Yena's ancient Dungeon of Fame is the dwelling place of the primordial being, Yore. The Hero's Guild has long since given up on slaying them, and keeps vigil nearby.");
		
		Town denok = new Town("Denok",5,teran,new Point(12,1));
		addConnection(denok,yena,"road","apple road");
		denok.addFeature(new Store(4,6));
		denok.addFeature(new Store(5,5));
		denok.addFeature(new Forest("Outlaying Wilds",4));
		denok.addFeature(new Grove("The Shaman's Clearing",denok,20,5));
		denok.addFeature(new Doctor("The Shaman's Hut",denok));
		denok.addFeature(new Mine("Denok's Mine",denok,null,NodeFeature.Shape.NONE));
		denok.tTags.add(TownTag.DRUIDIC);
		denok.setLoreText("While Denok's forest may not be notable to most worldly people, this very fact is what let it grow into such a landmark to those with druidic inclinations. It is mostly unmarred by mortal attention, yet close enough to civilization to stand with them against the forces of darkness.");
		
		Town erin = new Town("Erin",6,teran,new Point(10,4));
		addConnection(erin,yena,"road","pear road");
		addConnection(erin,denok,"road","orange road");
		erin.addFeature(new Arena("Grandstander's Stands (daily bout)",5,1,24,12,39));
		erin.addFeature(new Inn("Scholar's Respite",5,erin,null));
		erin.addFeature(new Library("Alex's Library",erin));
		erin.addFeature(new Appraiser("Material Patent Offices").setIntro("'We only handle physical patents, if you have new spells or magicks, head to Alex's.'"));
		erin.addFeature(new Enchanter("Enchantment Prototyping",6));
		erin.tTags.add(TownTag.ARCANE);
		erin.setLoreText("Erin has the largest library in this world, and scholars from all over gather in it to debate the newest theories.");
		
		
		Town placka = new Town("Placka",7,teran,new Point(13,3));
		addConnection(erin,placka,"road","peach road");
		addConnection(placka,denok,"road","pineapple road");
		addConnection(yena,placka,"ship","the yellow sea");
		placka.addFeature(new Docks("The Old Docks",placka));
		placka.addTravel();
		placka.addTravel();
		placka.addFeature(new Champion(9));
		placka.addFeature(new Dungeon("The Dungeon of Woe",placka,NodeFeature.Shape.NONE,BossType.NONE).setOutro("You feel like something was trying to say goodbye..."));
		placka.tTags.add(TownTag.ADVENTURE);
		placka.tTags.add(TownTag.HISTORY);//a nameless name, an unrecorded history
		//no lore on purpose
		
		Town tunka = new Town("Tunka",8,teran,new Point(12,5));
		addConnection(erin,tunka,"road","left-over road");
		addConnection(placka,tunka,"road","diamond road");
		tunka.addFeature(new Graveyard("The Boneyard", tunka));
		tunka.addFeature(new Store("'A Quick Find'",7,6).setIntro("'If you can't find anything you like, I'm sure I can put in a request with the Society.'").setOutro("'Bring back some good stuff for me, eh?''"));
		tunka.addFeature(new RogueGuild("Society of Enterprising Nobles",8));
		tunka.addFeature(new Slum(tunka,"Forgettables District",false).setOutro("As you leave, you notice a few hooded figures eyeing you from the rooftops."));
		tunka.tTags.add(TownTag.LAWLESS);
		tunka.tTags.add(TownTag.DISPARITY);
		tunka.setLoreText("Tunka is notable for being the only place in Eoano willing to maintain a full graveyard, and for its Rogue's Guild, which seem to take particular glee in robbing the downtrodden of what little they have left.");
		
		Town owal = new Town("Owal",9,teran,(byte)13,(byte)7);
		//connects added later
		owal.addFeature(new Docks("The Great Bay", owal));
		owal.addFeature(new Store("Edible Exports",8,10));
		owal.addFeature(new Store(owal,"Equipment Imports",10,6,3));//general store with reduced size
		owal.addFeature(new Garden(owal,"Overworked Fields",1.1f,PlantFill.FOOD));
		owal.addTravel();
		owal.tTags.add(TownTag.FARMS);//exports food south
		owal.setLoreText("Newer in the grand scheme of Eoano, Owal was formed to provide food for the now-unsuitable island of Epan south of it.");
		
		Town repa = new Town("Repa",10,teran,new Point(14,6));
		addConnection(repa,tunka,ConnectType.CARV,"right-over road");
		addConnection(repa,owal,ConnectType.CARV,"Former Glory Road");
		//add connection to a new world area
		repa.addFeature(new Mountain("Mountain Teleporter Brace",9).setIntro("The mountain thrums with arcane energy."));
		repa.addTravel();
		repa.addTravel();
		repa.addTravel();
		repa.addTravel();
		repa.addFeature(new Inn("Repa's Rest",8,erin,null));
		repa.tTags.add(TownTag.TRAVEL);
		repa.setLoreText("Repa's giant teleporter brace- once a normal mountain- looms over the ragtag tents here. It teleports to another world entirely, they say.");
		
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
				//MAYBELATER: make a custom anon merchant guild class that says "oh its you" and thanks you for paying to get it built
				return new MerchantGuild("Better Futures Central Office",6).setIntro("'Welcome to Better Futures! We can assign a caseworker, if you'd like?'");
			}
		}).setIntro("'We don't have much work, but if you need food, you can always try to wring life from these barren lands.'"));
		senal.addFeature(new Garden(senal,"Desolate Fields",.2f,PlantFill.BAD_HARVEST));
		senal.tTags.add(TownTag.BARREN);
		senal.tTags.add(TownTag.DETERMINED);
		senal.setLoreText("A few Eras ago, Senal was used as a trade route. But the island of Epan had its land salted, and civilization moved north, to Alhax. The mountain pass to Quen is oft alleged to be untraversable, only to be proven usable by desperate migrants seeking the Better Futures company here.");
		
		Town quen = new Town("Quen",9,epan,(byte)12,(byte)10);
		addConnection(quen,senal,ConnectType.PATH,"Derelict Pass");
		quen.addFeature(new Dungeon("Blasted Palace", quen,30,12, Shape.TOWER,BossType.OLD_QUEEN));
		quen.addFeature(new Library("Empire Records Bookstore", quen).setIntro("'While all our books may be copies, they came straight from the source. Some of which are still cursed to never leave the Blasted Palace.'"));
		quen.addFeature(new Dungeon("Crumbling Fort", quen,40,10, Shape.NONE, BossType.NONE));
		quen.addFeature(new Mine("'The Last Ditch that Failed'", quen, 20,9, Shape.ELEVATOR,BossType.NONE));
		quen.addFeature(new Grove("Dilapidated Hamlet", quen,100,8));
		quen.tTags.add(TownTag.BARREN);
		quen.tTags.add(TownTag.HISTORY);
		quen.setLoreText("Quen was once the mighty city that all others aspired to. It first held that pedestal long before written history remains intact. But the countless wars over it eventually destroyed the island of Epan. And tyrants ceased caring over it a few Eras later.");
		
		Town visan = new Town("Visan",8,epan,(byte)13,(byte)8);
		addConnection(visan,quen,ConnectType.ROAD,"Desolate Plains");
		addConnection(visan,repa,ConnectType.TELE,"Barren Bounce");
		addConnection(visan,senal,ConnectType.TELE,"Through the Fog");
		addConnection(visan,owal,ConnectType.SHIP,"Forgotten Shipping Lane");
		visan.addFeature(new Doctor("Foglung Cure Center", visan).setIntro("As you enter, the doctor enters a coughing fit before responding, 'Don't worry, Foglung is curable, if you catch it soon enough. Check in every few weeks, or else you'll end up like me- stuck sleeping with a magic breathing aid.'"));
		visan.addFeature(new Champion(10));
		visan.addFeature(new Store(7,6).setIntro("'Do you have any news about the monthly shipment route? Over the last year the lateness has added up to weeks!'"));
		visan.addTravel();
		visan.tTags.add(TownTag.BARREN);
		visan.tTags.add(TownTag.TRAVEL);
		visan.setLoreText("Like the other towns on the island of Epan, Visan's land is barren. It's current use is as a staging point to break through the magic fog and allow teleporting from Rona to Teran through Epan, but most traders prefer to take the shipping route to the north.");
		
		addConnection(repa,greap(),ConnectType.TELE,"world teleport (eonao-greap)");
		
		fallBackWorld = null;
		return w;
	}
	/**
	 * 
	 * welcome to uhhhh Australia?
	 * <br>
	Paruku (Lake Gregory) Indigenous Protected Area
	Landmark in Sturt Creek, Australia
	 */
	public static Town greap() {
		World w = new World(15,20,"Greap",-20f,127.5f);//
		fallBackWorld = w;
		
		//slow down leveling rate due to leveling getting slower, also go back and forth
		//greap has no non-world teleporters to prevent teleporting to the end now that you understand them
		
		Island apen = new Island("Apen",w);
		plane.addWorld(w);
		Town holik = new Town("Holik", 10, apen, new Point(10,18));
		holik.addFeature(new Oracle("The Worldgreeter",9).setIntro("A greeter answers you, 'Welcome to Greap! If you're from Repa that is. The oracles don't come out much, but rest assured they watch over their flock, no matter if they're from Eoano, Greap, or elsewhere.'"));
		holik.addFeature(new Doctor("'Universal Compassion'", holik));
		holik.addTravel();
		holik.addTravel();
		holik.addTravel();
		w.setStartTown(holik);
		holik.tTags.add(TownTag.MYSTIC);
		holik.tTags.add(TownTag.RICH);
		holik.tTags.add(TownTag.TRAVEL);
		
		Town yonuen = new Town("Yonuen",11, apen, new Point(8,19));
		addConnection(holik,yonuen,ConnectType.CARV,"bliz road");
		yonuen.addFeature(new Store(10));
		yonuen.addFeature(new Store(9));
		yonuen.addFeature(new Dungeon("Sky-Sundering Tower", yonuen, Shape.TOWER,BossType.FATESPINNER));
		yonuen.addFeature(new RogueGuild("The Open Adventuring Guild",10).setIntro("Outside the Guild, there is an overly long manifesto full of errors about how the other hero guilds aren't willing to take the biggest adventures, but they are."));
		yonuen.addTravel();
		yonuen.tTags.add(TownTag.ADVENTURE);
		yonuen.tTags.add(TownTag.CITY);
		yonuen.tTags.add(TownTag.LAWLESS);
		
		Town unika = new Town("Unika",11, apen, new Point(6,17));
		addConnection(holik,unika,ConnectType.ROAD,"ren road");
		addConnection(yonuen,unika,ConnectType.ROAD,"tenka road");
		unika.addFeature(new Arena("'Lucky Break'",12,1,24,12,135));
		unika.addFeature(new Grove("Unika's Backyard",unika));
		unika.addFeature(new Champion(15));
		unika.tTags.add(TownTag.SMALL_TOWN);
		//unika.addFeature(new Inn("unika inn",10,unika,null));
		
		Town peana = new Town("Peana",12, apen, new Point(9,16));
		addConnection(holik,peana,ConnectType.CARV,"blue road");
		addConnection(unika,peana,ConnectType.ROAD,"green road");
		//trying to make a slight reference to death/hell metal music
		peana.addFeature(new Inn("'Dirges for the Damned'",12,peana,null).setIntro("A bard is playing a loud and harsh magical lute.").setOutro("You leave before the music can grow on you."));
		peana.addFeature(new Store("'Tyrant's Treasures'",11,11).setIntro("'Before you ask, most of this isn't from Hell. But some of it is, and a lot of treasure meant for Hell ends up here instead!'"));//oddity store
		//peana.addFeature(new Arena("Deadsoul's Folly",10,1,24,12,135));
		//peana.addFeature(new Appraiser("Peana Appraiser"));
		peana.addFeature(new Mine("Staircase to Hell", peana,75,10,NodeFeature.Shape.ELEVATOR,BossType.GENERIC_DEMON_OVERLORD));
		peana.tTags.add(TownTag.HELLISH);
		
		peana.setLoreText("Peana is the site of a large Mine with a singular purpose: to breach into hell. Those who completed this task have since long been lost to the ages- but their work remains, and a Throne of Hell was established there.");
		
		Town inka = new Town("Inka",12, apen, new Point(7,14));
		addConnection(unika,inka,ConnectType.ROAD,"youn road");
		addConnection(inka,peana,ConnectType.CARV,"era road");
		inka.addFeature(new Docks("Ironclad Shipments",inka));
		inka.addFeature(new Mine("First Striking Shaft", inka,60,8,NodeFeature.Shape.NONE,BossType.NONE));
		inka.addFeature(new Mine("Motherload Mine", inka,30,14,NodeFeature.Shape.NONE,BossType.NONE));
		inka.addFeature(new Mine("Deep Vein Dig", inka,80,12,NodeFeature.Shape.NONE,BossType.NONE));
		inka.addFeature(new Slum(inka,"Miner's Subtown",true));
		inka.addFeature(new Store("M. Hardhat's Shop",12,0).setIntro("'Here at M. Hardhats, we offer a wide selection of brainbuckets to protect your noggin!'"));
		inka.addTravel();
		inka.tTags.add(TownTag.CITY);
		inka.tTags.add(TownTag.MINERALS);
		
		
		Island opyo = new Island("Opyo", w, IslandType.ISLAND);
		
		Town pipa = new Town("Pipa",13, opyo, new Point(8,12));
		addConnection(inka,pipa,ConnectType.SHIP,"Digger's Leave Current");
		pipa.addFeature(new WitchHut("Oak Coven's Hut",pipa));
		pipa.addFeature(new Grove("Deciduous Sprawl",pipa));
		pipa.addFeature(new Altar("Thorny Throne",AltarForce.FOREST));
		pipa.addFeature(new Library("'Primal Knowledge'", pipa).setIntro("You enter the hut and find yourself face to face with a giant plant lifting countless books. Most are faintly glowing from their paper."));
		pipa.tTags.add(TownTag.ALCHEMY);
		pipa.tTags.add(TownTag.DRUIDIC);
		pipa.tTags.add(TownTag.HIDDEN);
		
		Town xeyn = new Town("Xeyn",13,opyo,new Point(11,14));
		addConnection(inka,xeyn,ConnectType.SHIP,"Picks to Plowshares Sealane");
		addConnection(peana,xeyn,ConnectType.SHIP,"Hellsalts Shipments");
		addConnection(pipa,xeyn,ConnectType.ROAD,"The Bread and Barley Path");
		xeyn.addFeature(new Docks("Ferrysteads",xeyn));
		xeyn.addFeature(new Garden(xeyn,"Communal Gardens", 0, PlantFill.FOOD));
		xeyn.addFeature(new Store("Foodstuffs and Tack",10,10));
		xeyn.addFeature(new Forest("Horizon of Mouths",13));
		xeyn.addFeature(new Store("Homegrown Meals",14,10));
		xeyn.addFeature(new Inn("'The Softer Side'",12, xeyn, null));
		xeyn.tTags.add(TownTag.FARMS);
		xeyn.tTags.add(TownTag.LIVESTOCK);
		xeyn.setLoreText("Xeyn is a fairly idyllic farmstead- as long as you stay out of the forest. Those whose cattle venture there write them off as a loss, and the children of Xeyn will always remember the mouths, even when they grow too old to see them.");
		
		Town mikol = new Town("Mikol",13,opyo,new Point(10,11));
		addConnection(pipa,mikol,ConnectType.CARV,"Twin Thrones Road");
		addConnection(xeyn,mikol,ConnectType.CARV,"Well Traveled Route");
		mikol.addFeature(new Dungeon("Eerie Palace Portal",mikol,40,15, Shape.TOWER,BossType.OLD_QUEEN));
		mikol.addFeature(new Garden(mikol,"Royal Gardens",0,PlantFill.FOOD));
		mikol.addFeature(new HeroGuild("Empyphic Palace",12));//empyphic - empyrean + seraphic
		mikol.addFeature(new MerchantGuild("Central Tariff Offices",13));
		mikol.addFeature(new Store(12));
		mikol.addFeature(new Appraiser("Shipment Inspectors"));
		mikol.tTags.add(TownTag.LAW);
		mikol.tTags.add(TownTag.SERVICES);
		mikol.setLoreText("The Empyphic royals in Mikol are largely bureaucratic, having survived the ages via offering administrative services. Those in the dusk-soaked dimension a portal has opened to seem to be quite the opposite, preferring spears to pens.");
		
		Town reahe = new Town("Reahe",14,opyo,new Point(14,13));
		addConnection(xeyn,reahe,ConnectType.CARV,"TODO");
		addConnection(mikol,reahe,ConnectType.CARV,"TODO");
		Store reaheStore = new Store("Forgeheart Stall",14,6);
		reahe.addFeature(reaheStore);
		reahe.addFeature(new Blacksmith("Forgeheart's Smithy",14,reaheStore));
		reahe.addFeature(new Store("Souvenir Stall",12,11));
		reahe.addFeature(new Store("'Shoes 4 Yous' Stall",15,4));
		reahe.addFeature(new Enchanter("'Custom Enchantments!'",14));
		reahe.addTravel();
		reahe.addTravel();
		reahe.tTags.add(TownTag.MERCHANT);
		reahe.tTags.add(TownTag.TRAVEL);
		
		Town kelo = new Town("Kelo",14,opyo,new Point(13,10));//FIXME
		addConnection(mikol,kelo,ConnectType.ROAD,"TODO");
		addConnection(reahe,kelo,ConnectType.ROAD,"TODO");
		
		Town gopuo = new Town("Gopuo",15,opyo,new Point(11,8));//done?
		addConnection(kelo,gopuo,ConnectType.ROAD,"TODO");
		addConnection(mikol,gopuo,ConnectType.CARV,"TODO");
		gopuo.addFeature(new Inn("Last Landing", 14, gopuo, null));
		gopuo.addFeature(new Dungeon("Lost Lighthouse",gopuo,40,13,Shape.RIGGED_TOWER,BossType.YORE));
		gopuo.addTravel();
		gopuo.addTravel();
		gopuo.addTravel();
		gopuo.tTags.add(TownTag.TRAVEL);
		//ships up
		
		
		Island worea = new Island("Worea",w,IslandType.ISLAND);//island to the west of pipa
		//island is very unsettling
		
		Town lunek = new Town("Lunek",14, worea, new Point(6,9));//almost done
		addConnection(pipa,lunek,ConnectType.SHIP,"TODO");
		addConnection(gopuo,lunek,ConnectType.SHIP,"TODO");
		//triangle shipping lane
		lunek.addFeature(new Docks("Harbor of Worries",lunek));
		lunek.addFeature(new Garden(lunek, "'Spoiled Soil'", 1.0f, PlantFill.WITCH));
		lunek.addFeature(new WitchHut("'Creepy Cauldron", lunek));
		lunek.addFeature(new HunterGuild("Hall of Paranoia", 14));
		lunek.addFeature(new Arena("'Hunter's Proving'",14,2,70d,30d,45));
		lunek.tTags.add(TownTag.LAW);
		lunek.tTags.add(TownTag.DETERMINED);
		//unsettling second
		lunek.tTags.add(TownTag.UNSETTLING);
		lunek.tTags.add(TownTag.ALCHEMY);
		
		Town eaqu = new Town("Eaqu",14,worea,new Point(2,10));//FIXME
		addConnection(lunek,eaqu,ConnectType.PATH,"TODO");
		eaqu.addFeature(new Graveyard("Churchyard", eaqu,15));
		eaqu.addFeature(new Garden(eaqu,"Modest Harvest",.7f,PlantFill.FOOD));
		eaqu.tTags.add(TownTag.SMALL_TOWN);
		eaqu.tTags.add(TownTag.HIDDEN);
		//unsettling last
		eaqu.tTags.add(TownTag.UNSETTLING);
		//small isolated town
		
		Town celen = new Town("Celen",15,worea,new Point (4,7));//done?
		//shipments up
		addConnection(lunek,celen,ConnectType.ROAD,"TODO");
		addConnection(lunek,celen,ConnectType.SHIP,"TODO");
		addConnection(eaqu,celen,ConnectType.PATH,"TODO");
		celen.addFeature(new Inn("'Respite'",15, celen, null));
		celen.addFeature(new Enchanter("'Malicious Magiks'",15));
		celen.addFeature(new Grove("Wildering Weald",celen,40,15));
		celen.addFeature(new Mountain("Perturbing Peaks", 16));
		celen.addTravel();//one travel
		
		celen.tTags.add(TownTag.UNSETTLING);//most unsettling
		celen.tTags.add(TownTag.VISTAS);
		
		
		Island ityl = new Island("Ityl",w,IslandType.ISLAND);
		
		Town beola = new Town("Beola",15,ityl,new Point(8,6));//done
		addConnection(gopuo,beola,ConnectType.SHIP,"TODO");
		addConnection(celen,beola,ConnectType.SHIP,"TODO");
		addConnection(lunek,beola,ConnectType.SHIP,"TODO");
		beola.addFeature(new Docks("Central Landing",beola));
		beola.addFeature(new Inn("Tavern on Main",16,beola,null));
		beola.addFeature(new Inn("Helen's Inn",15,beola,null));
		beola.addFeature(new Store(beola,15,6));//general store
		beola.addFeature(new Store(beola,15,10));//food store
		beola.addFeature(new Doctor("Beolan Clinic", beola));
		beola.addFeature(new Library("'Publics'", beola));
		//crammed full since it's a city
		beola.tTags.add(TownTag.CITY);
		beola.tTags.add(TownTag.TRAVEL);
		
		Town aerna = new Town("Aerna",15,ityl,new Point(12,5));//FIXME
		addConnection(beola,aerna,ConnectType.ROAD,"TODO");
		addConnection(beola,aerna,ConnectType.SHIP,"TODO");
		addConnection(gopuo,aerna,ConnectType.SHIP,"TODO");
		
		Town jenai = new Town("Jenai",15,ityl,new Point(10,4));//FIXME
		addConnection(beola,jenai,ConnectType.ROAD,"TODO");
		addConnection(aerna,jenai,ConnectType.ROAD,"TODO");
		
		Town defal = new Town("Defal",16,ityl,new Point(8,2));//FIXME
		addConnection(jenai,defal,ConnectType.ROAD,"TODO");
		
		Town nowra = new Town("Nowra",16,ityl,new Point(14,3));//FIXME
		addConnection(aerna,nowra,ConnectType.ROAD,"TODO");
		addConnection(jenai,nowra,ConnectType.ROAD,"TODO");
		
		//fort
		Town fortKaol = new Town("Fort Kaol",6,ityl,(byte)15,(byte)3, null);//FIXME
		addConnection(nowra,fortKaol,ConnectType.ROAD,"TODO");
		
		Town jadua = new Town("Jadua",16,ityl,new Point(11,1));//FIXME
		addConnection(defal,jadua,ConnectType.ROAD,"TODO");
		addConnection(nowra,jadua,ConnectType.ROAD,"TODO");
		
		Town ailak = new Town("Ailak",16,ityl,new Point(4,1));//FIXME
		addConnection(defal,ailak,ConnectType.ROAD,"TODO");
		addConnection(jadua,ailak,ConnectType.TELE,"TODO");
		
		Island henak = new Island("Henak",w,IslandType.ISLAND);//has to be teleported to since rocks around it or something
		
		Town orean = new Town("Orean",17,henak,new Point(1,4));//FIXME
		addConnection(jadua,orean,ConnectType.TELE,"TODO");
		addConnection(ailak,orean,ConnectType.TELE,"TODO");
		//TODO: world teleporter
		
		orean.tTags.add(TownTag.HIDDEN);
		orean.setLoreText("Orean sits atop the rocky raised island of Henak, which is inaccessible by boat. Only nearby teleporters can reach it, which makes it isolated due to the rarity of them in Greap.");
		
		return holik;
	}
	
	public static void finishPlane(Plane p) {
		p.reload();
		for (World wor: p.worlds()) {
			townFinal(wor);
			try {
				alignConnectFlowsToInns(wor);
			}catch (Exception e) {
				mainGame.log("WorldGen Align Fail: " +e.getMessage());
			}
		}
	}
	
	private static void townFinal(World w) {
		for (Island i: w.getIslands()) {
			for (Town t: i.getTowns()) {
				t.detectConnectTypes();
				for (Feature f: t.getFeatures()) {
					f.init();
					if (f instanceof QuestBoardLocation) {
						for (int j = 0; j < 3; j++) {
							((QuestBoardLocation)f).generateSideQuest();
						}
					}
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
	/**
	 * @return hours
	 */
	public static double distanceBetweenTowns(Town t1,Town t2,ConnectType connectType) {
		if (!t1.getIsland().getWorld().equals(t2.getIsland().getWorld())) {
			return connectType.startTime+connectType.endTime+(100/connectType.perHourSpeed);
		}
		return connectType.startTime+connectType.endTime+(pointDistance(t1,t2)/connectType.perHourSpeed);
	}
	
	/**
	 * @return hours
	 */
	public static double rawConnectTime(Town t1,Town t2,ConnectType connectType) {
		if (!t1.getIsland().getWorld().equals(t2.getIsland().getWorld())) {
			return 100/connectType.perHourSpeed;
		}
		return pointDistance(t1,t2)/connectType.perHourSpeed;
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
				){
			 pws.write(Player.player.getPerson().getName()
					 +", level " + Player.player.getPerson().getLevel()
					 + ": " +new Date().toString()
					 +" "+mainGame.VERSION_STRING+"\0");
			 ;
			 pws.flush();
			 KyroManager.savePlane(plane,fos);
		     extra.println("Saved!");
		     File f = new File("trawel"+str+".save");
		     extra.println("Slot "+str + ": "+f.length() + " bytes.");
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
			while (fos.read() != '\0');
			plane = KyroManager.readPlane(fos);
			Player.player = plane.getPlayer();
			Player.bag = Player.player.getPerson().getBag();
			Player.player.skillUpdate();
			Player.passTime = 0;
			mainGame.story = Player.player.storyHold;
			extra.getThreadData().world = Player.player.getWorld();
			fos.close();
			plane.reload();
		} catch (IOException e) {
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
	public static void travelToTown(Town dest) {
		
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
				curTown.goConnect(connects.get(i),2f);
				if (Player.player.getLocation() != nextTown) {
					throw new RuntimeException("didn't move to next town " + nextTown.getName() + " from " + curTown.getName() +": at " + Player.player.getLocation());
				}
				curTown = Player.player.getLocation();
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
	
	private static class ScoreTown{
		public final Town forTown;
		public double score;
		public ScoreTown from;
		public ScoreTown(Town t, double _score) {
			forTown = t;
			score = _score;
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
		for (Island is: w.getIslands()) {
			townLoop: for (Town t: is.getTowns()) {
				if (innCheck.test(t)) {
					continue;//has an inn already
				}
				//honestly should probably use hashing or something, idk
				List<ScoreTown> openSet = new ArrayList<ScoreTown>();
				//closed set is things we looked at, so we don't repeatedly create edgeconnection holders
				//and also to store our path
				List<ScoreTown> closedSet = new ArrayList<ScoreTown>();
				
				
				//find the cheapest connection between the town we're checking
				ScoreTown start = new ScoreTown(t,0);
				for (Connection c: t.getConnects()) {
					if (!c.isWorldConnection()) {
						updateST(c.otherTown(t),start,c.getTime(), openSet, closedSet);
					}
				}
				while (!openSet.isEmpty()) {
					ScoreTown examine = popLowestST(openSet);
					if (innCheck.test(examine.forTown)) {//if it has a goal in
						ScoreTown last = examine;
						ScoreTown current = last;
						while (current != start) {
							last = current;
							current = current.from;
						}
						Connection connect = null;
						double bestTime = Double.MAX_VALUE;
						for (Connection c: t.getConnects()) {
							if (c.otherTown(t) == last.forTown && c.getTime() < bestTime) {
								bestTime = c.getTime();
								connect = c;
							}
						}
						t.setConnectFlow(connect);//set
						continue townLoop;//move onto next town
					}
					//if we still need to keep looking
					for (Connection c: examine.forTown.getConnects()) {
						Town other = c.otherTown(examine.forTown);
						//add sts to sets and update any scores
						updateST(other,examine,examine.score+c.getTime(),openSet,closedSet);
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
	
	private static ScoreTown updateST(Town want,ScoreTown from, double score,List<ScoreTown> openSet, List<ScoreTown> closedSet) {
		for (ScoreTown st: openSet) {
			if (st.forTown == want) {
				if (st.score > score) {
					st.score = score;
					st.from = from;
				}
				return st;
			}
		}
		for (int i = 0; i < closedSet.size();i++) {
			ScoreTown st = closedSet.get(i);
			if (st.forTown == want) {
				if (st.score > score) {//found a faster way to a town somehow???
					closedSet.remove(i);
					openSet.add(st);
					st.score = score;
					st.from = from;
				}
				return st;
			}
		}
		ScoreTown st = new ScoreTown(want,score);
		st.from = from;
		openSet.add(st);
		return st;
		
	}
	
	private static ScoreTown popLowestST(List<ScoreTown> openSet) {
		double lowestValue = Double.MAX_VALUE;
		ScoreTown best = null;
		for(ScoreTown e: openSet) {
			if (e.score < lowestValue) {
				best = e;
				lowestValue = e.score;
			}
		}
		openSet.remove(best);
		return best;
	}
}
