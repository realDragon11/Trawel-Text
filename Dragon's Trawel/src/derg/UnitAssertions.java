package derg;

import trawel.WorldGen;
import trawel.mainGame;
import trawel.helper.methods.extra;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.magic.EnchantConstant;
import trawel.towns.Connection.ConnectType;
import trawel.towns.Island;
import trawel.towns.Town;

public class UnitAssertions {
	
	//NOTE that you must use the vm arg "-ea" to enable assertions

	public static void main(String[] args) {
		extra.setMainThread();
		System.out.println("starting");
		mainGame.unitTestSetup();
		System.out.println("setup");
		Person p = RaceFactory.makeGeneric(1);
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
		
		for (int i = 0; i < 20; i++) {
			System.out.print(extra.hrandomFloat() +" ");
		}
		System.out.println();
		
		EnchantConstant.testAsserts();
		
		System.out.println("  -  ");
		long l = 0;
		/*
		System.out.println("long : " +l 
				+" off: "+ pad(extra.setByteInLong(l,0b00000000,8))
				+" on: "+ pad(extra.setByteInLong(l,0b11111111,16))
				+" on then off"+pad(extra.setByteInLong(extra.setByteInLong(l,0b11111111,16),0b00000000,16)));*/
		for (int i = 0;i < 64;i+=8) {
			System.out.println("at " +i);
			long off = extra.setByteInLong(l,0b000000000,i);
			long parton = extra.setByteInLong(l,0b010101010,i);
			long thenoff = extra.setByteInLong(parton,0b000000000,i);
			System.out.println("long : " +l
					+"\n off: "+ pad(off)
					+"\n part on: "+ pad(parton)
					+"\n on then off"+ pad(thenoff));
			assert parton != 0;
			assert off == 0;
			assert thenoff == 0;	
		}
		System.out.println("An assertion will now error to make sure you have those on.");
		assert false == true;
	}
	
	public static String pad(Long l) {
		return pad(Long.toBinaryString(l),"0",64);
	}
	
	public static String pad(String str,String chara, int places) {
		StringBuilder builder = new StringBuilder(str);
		while (builder.length() < places) {
			builder.insert(0,chara);
		}
		return builder.toString();
	}

}
