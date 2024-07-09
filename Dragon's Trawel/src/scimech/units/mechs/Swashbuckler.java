package scimech.units.mechs;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Mech;
import scimech.mech.MechPart;
import scimech.mech.Mount;
import scimech.people.Pilot;
import scimech.units.fixtures.APCannon;
import scimech.units.fixtures.LightAutocannon;
import scimech.units.mounts.Blunderbuss;
import scimech.units.mounts.Broadside;
import scimech.units.systems.FusionReactor;
import trawel.helper.methods.randomLists;

public class Swashbuckler extends Mech {

	public Swashbuckler(boolean side) {
		playerControlled = side;
		complexityCap = 80;
		weightCap = 80;
		
		callsign = randomLists.randomElement();
		pilot = new Pilot();
		
		Mount m = new Broadside();
		this.addMount(MechPart.lock(m));
		m.addFixture(MechPart.lock(new APCannon()));
		m.addFixture(MechPart.lock(new APCannon()));
		m.addFixture(MechPart.lock(new APCannon()));
		
		m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		
		m = new Blunderbuss();
		this.addMount(m);
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		m.addFixture(new LightAutocannon());
		
		this.addSystem(new FusionReactor());
		this.addSystem(new FusionReactor());
		
		hp = this.getMaxHP();
	}
	
	public Swashbuckler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int baseHP() {
		return 200;
	}

	@Override
	public int baseSpeed() {
		return 11;
	}

	@Override
	public int baseComplexity() {
		return 25;
	}

	@Override
	public String getName() {
		return "Swashbuckler";
	}

	@Override
	public int baseDodge() {
		return 6;
	}

	@Override
	public int baseHeatCap() {
		return 22;
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
		return Mech.internalDeserial(s,new Swashbuckler());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
