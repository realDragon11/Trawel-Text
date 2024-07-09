package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Mech;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.APCannon;
import scimech.units.fixtures.HotLaser;
import scimech.units.fixtures.LightAutocannon;
import scimech.units.mounts.Blunderbuss;
import scimech.units.systems.FusionReactor;
import scimech.units.systems.MiniReactor;
import scimech.units.systems.Plating;
import trawel.helper.methods.randomLists;

public class DebugMech extends Mech {
	
	public DebugMech() {}//serial
	
	public DebugMech(boolean side) {
		playerControlled = side;
		complexityCap = 70;
		weightCap = 80;
		
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
		m.addFixture(new HotLaser());
		m.addFixture(new HotLaser());
		
		this.addSystem(new FusionReactor());
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
		return 7;
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
	
	public static Savable deserialize(String s) throws Exception {
		return Mech.internalDeserial(s,new DebugMech());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
