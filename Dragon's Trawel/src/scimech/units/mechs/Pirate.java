package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.ResistMap;
import scimech.mech.Mech;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.APCannon;
import scimech.units.fixtures.ArtemisCannon;
import scimech.units.fixtures.LightAutocannon;
import scimech.units.mounts.Blunderbuss;
import scimech.units.mounts.Foil;
import scimech.units.mounts.Handcannon;
import scimech.units.systems.CoolantRod;
import scimech.units.systems.FusionReactor;
import scimech.units.systems.Ramjet;
import trawel.randomLists;

public class Pirate extends Mech {

	public Pirate(boolean side) {
		playerControlled = side;
		complexityCap = 80;
		weightCap = 75;
		
		callsign = randomLists.randomElement();
		pilot = new Pilot();
		
		Mount m = new Handcannon();
		this.addMount(m);
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		
		m = new Handcannon();
		this.addMount(m);
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		
		m = new Handcannon();
		this.addMount(m);
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		
		this.addSystem(new Ramjet());
		
		this.addSystem(new CoolantRod());
		this.addSystem(new CoolantRod());
		
		hp = this.getMaxHP();
	}
	
	@Override
	public int baseHP() {
		return 220;
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
		return "Pirate";
	}

	@Override
	public int baseDodge() {
		return 6;
	}

	@Override
	public int baseHeatCap() {
		return 18;
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
