package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.ResistMap;
import scimech.mech.Mech;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.APCannon;
import scimech.units.fixtures.AcidFoam;
import scimech.units.fixtures.ArcThrower;
import scimech.units.fixtures.CorrosiveDrill;
import scimech.units.fixtures.HeatVent;
import scimech.units.fixtures.HotLaser;
import scimech.units.fixtures.Krakatoa;
import scimech.units.fixtures.LightAutocannon;
import scimech.units.fixtures.ZeusRifle;
import scimech.units.mounts.Blunderbuss;
import scimech.units.mounts.Broadside;
import scimech.units.mounts.Foil;
import scimech.units.mounts.Handcannon;
import scimech.units.mounts.Pulsar;
import scimech.units.systems.AblativeArmor;
import scimech.units.systems.FrostAegis;
import scimech.units.systems.FusionReactor;
import scimech.units.systems.MiniReactor;
import scimech.units.systems.Plating;
import trawel.extra;
import trawel.randomLists;

public class Hazmat extends Mech {
	
	public Hazmat(boolean side) {
		playerControlled = side;
		complexityCap = 120;
		weightCap = 60;
		
		callsign = randomLists.randomElement();
		pilot = new Pilot();
	
		Mount m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new CorrosiveDrill());
		
		m = new Handcannon();
		this.addMount(m);
		m.addFixture(new AcidFoam());
		
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		
		
		hp = this.getMaxHP();
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
		return 20;
	}

	@Override
	public String getName() {
		return "Hazmat";
	}

	@Override
	public int baseDodge() {
		return 4;
	}

	@Override
	public int baseHeatCap() {
		return 20;
	}

	@Override
	public ResistMap internalResistMap() {
		ResistMap map = new ResistMap();
		map.isSub = true;
		map.put(DamageMods.AP, .7f, 1.3f);
		map.put(DamageMods.NORMAL,1f,1f);
		map.put(DamageMods.HOLLOW,1.5f, 1f);
		
		map.put(DamageTypes.CAUSTIC,0.6f,0.7f,0.5f);
		map.put(DamageTypes.BURN,0.9f,0.9f,0.5f);
		map.put(DamageTypes.KINETIC,1f,1f,0.5f);
		map.put(DamageTypes.BLAST,0.95f,1f,0.5f);
		map.put(DamageTypes.SHOCK,0.8f,0.8f,0.5f);
		return map;
	}

}
