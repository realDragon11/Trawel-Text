package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.handlers.Savable;
import scimech.mech.Mech;
import scimech.mech.MechPart;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.APCannon;
import scimech.units.fixtures.ArcThrower;
import scimech.units.fixtures.HeatVent;
import scimech.units.fixtures.HotLaser;
import scimech.units.fixtures.LightAutocannon;
import scimech.units.fixtures.ZeusRifle;
import scimech.units.mounts.Blunderbuss;
import scimech.units.mounts.Broadside;
import scimech.units.mounts.Pulsar;
import scimech.units.systems.FusionReactor;
import scimech.units.systems.MiniReactor;
import scimech.units.systems.Plating;
import trawel.extra;
import trawel.randomLists;

public class Dynamo extends Mech {
	
	public Dynamo(boolean side) {
		playerControlled = side;
		complexityCap = 120;
		weightCap = 60;
		
		callsign = randomLists.randomElement();
		pilot = new Pilot();
		
		Mount m = new Pulsar();
		this.addMount(MechPart.lock(m));
		m.addFixture(MechPart.lock(new ArcThrower()));
		
		m = new Pulsar();
		this.addMount(m);
		m.addFixture(new HotLaser());
		m.addFixture(new HeatVent());
		
		m = new Broadside();
		this.addMount(MechPart.lock(m));
		m.addFixture(MechPart.lock(new ZeusRifle()));
		m.addFixture(new ZeusRifle());
		m.addFixture(new HeatVent());
		
		
		this.addSystem(MechPart.lock(new FusionReactor()));
		this.addSystem(MechPart.lock(new FusionReactor()));
		this.addSystem(MechPart.lock(new FusionReactor()));
		this.addSystem(new FusionReactor());

		
		hp = this.getMaxHP();
	}

	public Dynamo() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int baseHP() {
		return 200;
	}

	@Override
	public int baseSpeed() {
		return 8;
	}

	@Override
	public int baseComplexity() {
		return 8;
	}

	@Override
	public String getName() {
		return "Dynamo";
	}

	@Override
	public int baseDodge() {
		return 8;
	}

	@Override
	public int baseHeatCap() {
		return 25;
	}

	@Override
	public ResistMap internalResistMap() {
		ResistMap map = new ResistMap();
		map.isSub = true;
		map.put(DamageMods.AP, .7f, 1.3f);
		map.put(DamageMods.NORMAL,1f,1f);
		map.put(DamageMods.HOLLOW,1.5f, 1f);
		return map;
	}
	
	public static Savable deserialize(String s) throws Exception {
		return Mech.internalDeserial(s,new Dynamo());
	}

}
