package scimech.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import scimech.mech.Mech;

public class MechCombat {

	int round = 0;
	public List<Mech> turnOrder = new ArrayList<Mech>();
	public List<Mech> totalMechs;
	public List<Mech> activeMechs = new ArrayList<Mech>();
	
	public MechCombat(Mech...mechs) {
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
}
