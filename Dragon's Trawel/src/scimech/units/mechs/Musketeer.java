package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.ResistMap;
import scimech.mech.Mech;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.APCannon;
import scimech.units.fixtures.ArtemisCannon;
import scimech.units.fixtures.HotLaser;
import scimech.units.fixtures.LightAutocannon;
import scimech.units.mounts.Blunderbuss;
import scimech.units.systems.CoolantRod;
import scimech.units.systems.FusionReactor;
import scimech.units.systems.MiniReactor;
import scimech.units.systems.Plating;
import scimech.units.systems.Ramjet;
import trawel.randomLists;

public class Musketeer extends Mech {

	public Musketeer(boolean side) {
		playerControlled = side;
		complexityCap = 70;
		weightCap = 60;
		
		callsign = randomLists.randomElement();
		pilot = new Pilot();
		
		Mount m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		
		m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new APCannon());
		
		m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new ArtemisCannon());
		m.addFixture(new ArtemisCannon());
		
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		
		this.addSystem(new Ramjet());
		this.addSystem(new Ramjet());
		
		this.addSystem(new CoolantRod());
		this.addSystem(new CoolantRod());
		
		hp = this.getMaxHP();
	}
	
	@Override
	public int baseHP() {
		return 170;
	}

	@Override
	public int baseSpeed() {
		return 12;
	}

	@Override
	public int baseComplexity() {
		return 7;
	}

	@Override
	public String getName() {
		return "Musketeer";
	}

	@Override
	public int baseDodge() {
		return 10;
	}

	@Override
	public int baseHeatCap() {
		return 14;
	}

	@Override
	public ResistMap internalResistMap() {
		ResistMap map = new ResistMap();
		map.isSub = true;
		map.put(DamageMods.AP, .7f, 1.15f);//slight strength
		map.put(DamageMods.NORMAL,1f,1f);
		map.put(DamageMods.HOLLOW,1.6f, 1.1f);//slight weakness
		
		map.put(DamageTypes.KINETIC,0.5f,0.8f);//strength
		map.put(DamageTypes.BLAST,1.2f,1.4f);//weakness
		return map;
	}

}
