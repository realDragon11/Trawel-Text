package derg;

import trawel.mainGame;
import trawel.personal.Person;
import trawel.extra;

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
		
		
		System.out.println("An assertion will now error to make sure you have those on.");
		assert false == true;
	}

}
