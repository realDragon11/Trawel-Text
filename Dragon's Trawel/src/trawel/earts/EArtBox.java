package trawel.earts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EArtBox implements Serializable {

	public List<ASpell> aSpells = new ArrayList<ASpell>();
	public float aSpellPower = 1;
	
	public ASpell aSpell1, aSpell2;
	public int exeTrainLevel = 0;
	public float exeKillLevel = 1;
	public int getExeExe() {
		int lvl = 1;
		int amount = Math.round(exeKillLevel);
		while (true) {//probably a better way to do this
			if (amount < lvl) {
				amount-=lvl;
				lvl*=2;
			}else {
				return lvl;
			}
		}
		
	}
}
