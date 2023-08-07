package trawel.personal.item;

import java.util.List;

import trawel.battle.attacks.ImpairedAttack;
import trawel.personal.item.body.Race;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponQual;

public class DummyInventory extends Inventory {
	
	private Double[][][] savedResults = new Double[3][5][4];
	private double myDodge;

	public DummyInventory() {
		super(-1, Race.RaceType.HUMANOID, null, null, null);
		myDodge = super.getDodge();
	}
	public DummyInventory(int i) {
		super(-1, Race.RaceType.HUMANOID, presetSwitch(i), null, null);
		myDodge = super.getDodge();
	}
	
	private static final Material presetSwitch(int i) {
		switch (i) {
		case 0: case 1:
			return MaterialFactory.getMat("cloth");
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
	
	
	public double getSharp10(ImpairedAttack att) {
		int slot = att.getSlot();
		int combo = 0;
		if (att.hasWeaponQual(Weapon.WeaponQual.PINPOINT)) {
			combo =1;
		}
		if (att.hasWeaponQual(Weapon.WeaponQual.PENETRATIVE)) {
			combo = combo == 1 ? 3 : 2;
		}
		Double ret = savedResults[0][slot][combo];
		if (ret != null) {
			return ret;
		}
		ret = super.getSharp(att);
		savedResults[0][slot][combo] = ret;
		return ret;
	}
	
	@Override
	public double getSharp(ImpairedAttack att) {
		return super.getSharp(att);
	}
	
	
	
	public double getBlunt10(ImpairedAttack att) {
		int slot = att.getSlot();
		int combo = 0;
		if (att.hasWeaponQual(Weapon.WeaponQual.PINPOINT)) {
			combo =1;
		}
		if (att.hasWeaponQual(Weapon.WeaponQual.PENETRATIVE)) {
			combo = combo == 1 ? 3 : 2;
		}
		Double ret = savedResults[1][slot][combo];
		if (ret != null) {
			return ret;
		}
		ret = super.getSharp(att);
		savedResults[1][slot][combo] = ret;
		return ret;
	}
	
	@Override
	public double getBlunt(ImpairedAttack att) {
		return super.getBlunt(att);
	}
	
	public double getPierce10(ImpairedAttack att) {
		int slot = att.getSlot();
		int combo = 0;
		if (att.hasWeaponQual(Weapon.WeaponQual.PINPOINT)) {
			combo =1;
		}
		if (att.hasWeaponQual(Weapon.WeaponQual.PENETRATIVE)) {
			combo = combo == 1 ? 3 : 2;
		}
		Double ret = savedResults[2][slot][combo];
		if (ret != null) {
			return ret;
		}
		ret = super.getSharp(att);
		savedResults[2][slot][combo] = ret;
		return ret;
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
