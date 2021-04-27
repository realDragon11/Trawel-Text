package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.mech.Mech;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.APCannon;
import scimech.units.fixtures.ArcThrower;
import scimech.units.fixtures.HeatVent;
import scimech.units.fixtures.HotLaser;
import scimech.units.fixtures.Krakatoa;
import scimech.units.fixtures.LightAutocannon;
import scimech.units.fixtures.ZeusRifle;
import scimech.units.mounts.Blunderbuss;
import scimech.units.mounts.Broadside;
import scimech.units.mounts.Foil;
import scimech.units.mounts.Pulsar;
import scimech.units.systems.AblativeArmor;
import scimech.units.systems.FrostAegis;
import scimech.units.systems.FusionReactor;
import scimech.units.systems.MiniReactor;
import scimech.units.systems.Plating;
import trawel.extra;
import trawel.randomLists;

public class Pyro extends Mech {
	
	public Pyro(boolean side) {
		playerControlled = side;
		complexityCap = 120;
		weightCap = 70;
		
		callsign = randomLists.randomElement();
		pilot = new Pilot();
	
		Mount m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new Krakatoa());
		m.addFixture(new HeatVent());
		
		m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new Krakatoa());
		m.addFixture(new HeatVent());
		
		m = new Foil();
		this.addMount(m);
		m.addFixture(new HotLaser());
		m.addFixture(new HotLaser());
		
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		
		this.addSystem(new FrostAegis());
		
		
		hp = this.getMaxHP();
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
		return 10;
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
		return map;
	}

}
