package rtrawel.unit;

import java.util.ArrayList;

import rtrawel.unit.Buff.BuffType;

public class BuffMap {

	public ArrayList<Buff> buffs = new ArrayList<Buff>();
	
	public void advanceTime(double time) {
		
	}

	public double getTotalBuffMult(BuffType m) {
		double mult = 1;
		for (Buff b: buffs) {
			if (b.type.equals(m)) {
				mult*=b.mag;
			}
		}
		return mult;
	}
	
	public int getTotalBuffMod(BuffType m) {
		double mod = 0;
		for (Buff b: buffs) {
			if (b.type.equals(m)) {
				mod+=b.mag;
			}
		}
		return (int)mod;
	}

	public void clear() {
		buffs.clear();
	}
}
