package trawel.personal.item;

import trawel.battle.attacks.ImpairedAttack;
import trawel.personal.item.body.Race;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;

public class DummyInventory extends Inventory {
	
	private Double[][][] savedResults = new Double[3][5][4];
	private double myDodge;
	
	public static DummyInventory dummyAttackInv = null;

	public DummyInventory() {
		this(-1);
	}
	public DummyInventory(int i) {
		super(-1, Race.RaceType.PERSONABLE,i == -1 ? null : presetSwitch(i), null, null);
		myDodge = super.getDodge();
		for (Armor a: armorSlots) {
			a.getQuals().clear();//clear qualities so they don't influence the tests
		}
	}
	
	private static final Material presetSwitch(int i) {
		switch (i) {
		case 0: case 1:
			return MaterialFactory.getMat("patchwork");
		case 2: case 3:
			return MaterialFactory.getMat("iron");
		case 4:
			return MaterialFactory.getMat("copper");
		case 5:
			return MaterialFactory.getMat("tin");
		case 6:
			return MaterialFactory.getMat("bronze");
		case 7:
			return MaterialFactory.getMat("silver");
		case 8:
			return MaterialFactory.getMat("gold");
		case 9:
			return MaterialFactory.getMat("flesh");
		case 10:
			return MaterialFactory.getMat("adamantine");
		case 11:
			return MaterialFactory.getMat("silk");
		}
		throw new RuntimeException("not a valid dummy inv preset");
	}
	
	@Override
	public double getSharp(ImpairedAttack att) {
		return super.getSharp(att);
	}
	
	
	@Override
	public double getBlunt(ImpairedAttack att) {
		return super.getBlunt(att);
	}
	
	@Override
	public double getPierce(ImpairedAttack att) {
		return super.getPierce(att);
	}
	
	public DummyInventory atLevel(int level) {
		for (int i = 0; i < armorSlots.length;i++) {
			armorSlots[i].level = level;
		}
		resetArmor(0,0,0);
		return this;
	}
	
	@Override
	public double getDodge() {
		return myDodge;
	}
	
	@Override
	public double getAim() {
		return 1;
	}

}
