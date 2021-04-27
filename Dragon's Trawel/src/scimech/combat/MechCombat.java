package scimech.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import scimech.mech.Mech;

public class MechCombat {

	public static MechCombat mc;
	int round = 0;
	public List<Mech> turnOrder = new ArrayList<Mech>();
	public List<Mech> totalMechs;
	public List<Mech> activeMechs = new ArrayList<Mech>();
	public Target t;
	
	public MechCombat(Mech...mechs) {
		mc = null;
		totalMechs = Arrays.asList(mechs);
		activeMechs.addAll(totalMechs);
		for (Mech m: totalMechs) {
			m.refreshForBattle();
		}
		while (twoSided()) {
			turnOrder.clear();
			turnOrder.addAll(activeMechs);
			turnOrder.sort(new Comparator<Mech>() {

				@Override
				public int compare(Mech o1, Mech o2) {
					return o1.getSpeed()-o2.getSpeed();
				}});
		}
		mc = this;
	}

	private boolean twoSided() {
		boolean sideF = false;
		int count = 0;
		for (Mech m: activeMechs) {
			if (count == 0) {
			if (m.playerControlled) {
				sideF = true;
			}else {
				sideF = false;
			}}else{
				if ((m.playerControlled && sideF == false) || (!m.playerControlled && sideF == true)) {
					return true;
				}
			}
			
			count++;
		}
		return false;
	}

	public static List<Mech> enemies(Mech currentMech) {
		List<Mech> list = new ArrayList<Mech>();
		for (Mech m :mc.activeMechs) {
			if (m.playerControlled != currentMech.playerControlled) {
				list.add(m);
			}
		}
		return list;
	}
	
	public Mech activeMech() {
		return turnOrder.get(0);
	}
}
