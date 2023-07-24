package derg;

import trawel.mainGame;
import trawel.personal.Person;
import trawel.WorldGen;
import trawel.extra;
import trawel.towns.*;
import trawel.towns.Connection.ConnectType;

public class UnitAssertions {
	
	//NOTE that you must use the vm arg "-ea" to enable assertions

	public static void main(String[] args) {
		System.out.println("starting");
		mainGame.unitTestSetup();
		System.out.println("setup");
		Person p = new Person(1);
		System.out.println("Flags: "+p.isRacist() + "-" + p.isAngry());
		p.setRacism(true);
		p.setAngry(false);
		System.out.println("Flags: "+p.isRacist() + "-" + p.isAngry());
		assert p.isRacist() && !p.isAngry();
		p.setRacism(false);
		p.setAngry(false);
		assert !p.isRacist() && !p.isAngry();
		p.setRacism(false);
		p.setAngry(true);
		assert !p.isRacist() && p.isAngry();
		p.setRacism(true);
		p.setAngry(true);
		System.out.println("Flags: "+p.isRacist() + "-" + p.isAngry());
		assert p.isRacist() && p.isAngry();
		
		Island is = new Island(true);
		Town t1 = new Town("test1",1,is,(byte)5,(byte)6);
		Town t2 = new Town("test2",1,is,(byte)5,(byte)6);
		Town t3 = new Town("test3",1,is,(byte)5,(byte)6);
		assert t1.hasRoads() == false;
		assert t1.hasPort() == false;
		assert t1.hasTeleporters() == false;
		
		WorldGen.addConnection(t1, t2, ConnectType.ROAD,"test road");
		t1.detectConnectTypes();
		t2.detectConnectTypes();
		assert t1.hasRoads() == true;
		assert t2.hasRoads() == true;
		assert t1.hasPort() == false;
		assert t1.hasTeleporters() == false;
		
		WorldGen.addConnection(t3, t2, ConnectType.SHIP,"test ship");
		t1.detectConnectTypes();
		t2.detectConnectTypes();
		t3.detectConnectTypes();
		
		assert t3.hasTeleporters() == false;
		assert t3.hasPort() == true;
		assert t2.hasRoads() == true;
		assert t2.hasPort() == true;
		assert t1.hasPort() == false;
		assert t1.hasTeleporters() == false;
		
		System.out.println("An assertion will now error to make sure you have those on.");
		assert false == true;
	}

}
