package scimech.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import scimech.mech.Fixture;
import scimech.mech.Mech;
import scimech.mech.Mount;
import trawel.extra;

public class MechCombat {

	public static MechCombat mc;
	int round = 0;
	public List<Mech> turnOrder = new ArrayList<Mech>();
	public List<Mech> totalMechs;
	public List<Mech> activeMechs = new ArrayList<Mech>();
	public Target t;
	
	public MechCombat(List<Mech> mechs) {
		mc = null;
		totalMechs = mechs;
		activeMechs.addAll(totalMechs);
		for (Mech m: totalMechs) {
			m.refreshForBattle();
		}
		
		mc = this;
	}
	
	public void go() {
		while (twoSided()) {
			for (Mech m: activeMechs) {
				m.roundStart();
			}
			turnOrder.clear();
			turnOrder.addAll(activeMechs);
			turnOrder.sort(new Comparator<Mech>() {

				@Override
				public int compare(Mech o1, Mech o2) {
					return o1.getSpeed()-o2.getSpeed();
				}});
			while (turnOrder.size() > 0) {
				extra.println(activeMech().callsign + " goes!");
				activeMech().activate(null,null);
				turnOrder.remove(0);
			}
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
	
	public static double computeHit(Target t,AimType at, int attackValue) {
		int acc = (int) (at.getMultFor(t.targetType())*attackValue);
		int dodge = (int) (t.dodgeValue());
		double accRoll = acc*Math.random();
		double dodgeRoll = dodge*Math.random();
		//HitDodge hd = new HitDodge(accRoll-dodgeRoll,);
		return accRoll-dodgeRoll;
	}

	public static int averageDamage(Target t, Mount firing,int displayAcc) {
		int total = 0;
		for (int i = 0; i < displayAcc;i++) {
			Dummy d = t.constructDummy();
			firing.activate(d,null);
			total-=d.hp;
		}
		total/=displayAcc;
		return total;
	}
	
	/*
	public class HitDodge {
		private float diff;
		private float magn;
		
		public HitDodge(float diff, float magn){
			this.diff = diff;
			this.magn = magn;
		}

		public float getDiff() {
			return diff;
		}

		public float getMagn() {
			return magn;
		}
		
	}*/
}
