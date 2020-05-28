package rtrawel.unit;

import java.util.ArrayList;
import java.util.List;

import rtrawel.unit.Buff.BuffType;

public class BuffMap {

	public List<Buff> buffs = new ArrayList<Buff>();
	
	public void advanceTime(double time) {
		List<Buff> removeList = new ArrayList<Buff>();
		for (Buff b: buffs) {
			if (!b.passive) {
				b.timeLeft-=time;
				if (b.timeLeft <=0) {
					removeList.add(b);
				}
			}
		}
		buffs.removeAll(removeList);//works because buffs aren't equal unless the pointer is the same
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
