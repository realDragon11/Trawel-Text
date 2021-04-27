package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.mech.Mech;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.APCannon;
import scimech.units.fixtures.LightAutocannon;
import scimech.units.mounts.Blunderbuss;
import scimech.units.systems.MiniReactor;
import scimech.units.systems.Plating;
import trawel.extra;
import trawel.randomLists;

public class DebugMech extends Mech {
	
	public DebugMech(boolean side) {
		playerControlled = side;
		complexityCap = 30;
		weightCap = 40;
		
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
		
		for (int i = 0; i < 4;i++) {
			this.addSystem(new MiniReactor());
		}
		
		for (int i = 0; i < 2;i++) {
			this.addSystem(new Plating());
		}
		
		hp = this.getMaxHP();
	}

	@Override
	public int baseHP() {
		return 200;
	}

	@Override
	public int baseSpeed() {
		return 10;
	}

	@Override
	public int baseComplexity() {
		return 2;
	}

	@Override
	public String getName() {
		return "debug mech";
	}

	@Override
	public int baseDodge() {
		return 10;
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
		return map;
	}

}
