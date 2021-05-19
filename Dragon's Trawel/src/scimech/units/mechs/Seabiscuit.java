package scimech.units.mechs;

import scimech.combat.ResistMap;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Mech;
import scimech.mech.MechPart;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.Clash;
import scimech.units.mounts.Hoof;
import scimech.units.systems.RacerFrame;
import scimech.units.systems.UnshieldedCycleReactor;
import trawel.randomLists;

public class Seabiscuit extends Mech {

	public Seabiscuit(boolean side) {
		playerControlled = side;
		complexityCap = 80;
		weightCap = 60;
		
		callsign = randomLists.randomElement();
		pilot = new Pilot();
		
		Mount m = MechPart.lock(new Hoof());
		m.addFixture(MechPart.lock(new Clash()));
		
		m = MechPart.lock(new Hoof());
		m.addFixture(MechPart.lock(new Clash()));
		
		m = MechPart.lock(new Hoof());
		m.addFixture(MechPart.lock(new Clash()));
		
		this.addSystem(MechPart.lock(new UnshieldedCycleReactor()));
		this.addSystem(MechPart.lock(new UnshieldedCycleReactor()));
		
		
		this.addSystem(MechPart.lock(new RacerFrame()));
		
		hp = this.getMaxHP();
	}
	
	public Seabiscuit() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int baseHP() {
		return 120;
	}

	@Override
	public int baseSpeed() {
		return 14;
	}

	@Override
	public int baseComplexity() {
		return 20;
	}

	@Override
	public String getName() {
		return "Seabiscuit";
	}

	@Override
	public int baseDodge() {
		return 8;
	}

	@Override
	public int baseHeatCap() {
		return 16;
	}

	@Override
	public ResistMap internalResistMap() {
		ResistMap map = new ResistMap();
		map.isSub = true;
		return map;
	}
	
	public static Savable deserialize(String s) throws Exception {
		return Mech.internalDeserial(s,new Seabiscuit());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.SARATOGA_SYSTEMS;
	}

}
