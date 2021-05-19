package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.ResistMap;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Mech;
import scimech.mech.MechPart;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.HeatVent;
import scimech.units.fixtures.HotLaser;
import scimech.units.fixtures.Krakatoa;
import scimech.units.mounts.Blunderbuss;
import scimech.units.mounts.Foil;
import scimech.units.systems.FrostAegis;
import scimech.units.systems.FusionReactor;
import trawel.randomLists;

public class Pyro extends Mech {
	
	public Pyro(boolean side) {
		playerControlled = side;
		complexityCap = 120;
		weightCap = 70;
		
		callsign = randomLists.randomElement();
		pilot = new Pilot();
	
		Mount m = new Blunderbuss();
		this.addMount(MechPart.lock(m));
		m.addFixture(MechPart.lock(new Krakatoa()));
		m.addFixture(new HeatVent());
		
		m = new Blunderbuss();
		this.addMount(MechPart.lock(m));
		m.addFixture(MechPart.lock(new Krakatoa()));
		m.addFixture(new HeatVent());
		
		m = new Foil();
		this.addMount(m);
		m.addFixture(new HotLaser());
		m.addFixture(new HotLaser());
		
		this.addSystem(MechPart.lock(new FusionReactor()));
		this.addSystem(MechPart.lock(new FusionReactor()));
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		
		this.addSystem(MechPart.lock(new FrostAegis()));
		
		
		hp = this.getMaxHP();
	}

	public Pyro() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int baseHP() {
		return 210;
	}

	@Override
	public int baseSpeed() {
		return 6;
	}

	@Override
	public int baseComplexity() {
		return 70;
	}

	@Override
	public String getName() {
		return "Pyro";
	}

	@Override
	public int baseDodge() {
		return 6;
	}

	@Override
	public int baseHeatCap() {
		return 40;
	}

	@Override
	public ResistMap internalResistMap() {
		ResistMap map = new ResistMap();
		map.isSub = true;
		map.put(DamageMods.AP, .7f, 1.3f);
		map.put(DamageMods.NORMAL,1f,1f);
		map.put(DamageMods.HOLLOW,1.5f, 1f);
		
		map.put(DamageTypes.BURN,0.6f,0.7f,0.7f);
		return map;
	}
	
	public static Savable deserialize(String s) throws Exception {
		return Mech.internalDeserial(s,new Pyro());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
