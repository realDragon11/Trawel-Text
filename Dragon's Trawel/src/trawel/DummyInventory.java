package trawel;

import java.util.List;

import trawel.Weapon.WeaponQual;

public class DummyInventory extends Inventory {
	
	private Double[][][] savedResults = new Double[3][5][4];
	private double myDodge;

	public DummyInventory() {
		super(-1, Race.RaceType.HUMANOID, null, null);
		myDodge = super.getDodge();
	}
	public DummyInventory(int i) {
		super(-1, Race.RaceType.HUMANOID, presetSwitch(i), null);
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
	
	@Override
	public double getSharp(int slot, List<WeaponQual> qualList) {
		int combo = 0;
		if (qualList.contains(Weapon.WeaponQual.PINPOINT)) {
			combo =1;
		}
		if (qualList.contains(Weapon.WeaponQual.PENETRATIVE)) {
			combo = combo == 1 ? 3 : 2;
		}
		Double ret = savedResults[0][slot][combo];
		if (ret != null) {
			return ret;
		}
		ret = super.getSharp(slot, qualList);
		savedResults[0][slot][combo] = ret;
		return ret;
	}
	@Override
	public double getBlunt(int slot, List<WeaponQual> qualList) {
		int combo = 0;
		if (qualList.contains(Weapon.WeaponQual.PINPOINT)) {
			combo =1;
		}
		if (qualList.contains(Weapon.WeaponQual.PENETRATIVE)) {
			combo = combo == 1 ? 3 : 2;
		}
		Double ret = savedResults[1][slot][combo];
		if (ret != null) {
			return ret;
		}
		ret = super.getSharp(slot, qualList);
		savedResults[1][slot][combo] = ret;
		return ret;
	}
	@Override
	public double getPierce(int slot, List<WeaponQual> qualList) {
		int combo = 0;
		if (qualList.contains(Weapon.WeaponQual.PINPOINT)) {
			combo =1;
		}
		if (qualList.contains(Weapon.WeaponQual.PENETRATIVE)) {
			combo = combo == 1 ? 3 : 2;
		}
		Double ret = savedResults[2][slot][combo];
		if (ret != null) {
			return ret;
		}
		ret = super.getSharp(slot, qualList);
		savedResults[2][slot][combo] = ret;
		return ret;
	}
	@Override
	public double getDodge() {
		return myDodge;
	}

}
