package trawel.earts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import trawel.Person;

public class EArtBox implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<ASpell> aSpells = new ArrayList<ASpell>();
	public float aSpellPower = 1;
	
	public ASpell aSpell1, aSpell2;
	public int exeTrainLevel = 0;
	public float exeKillLevel = 1;
	public int berTrainLevel = 0;
	public int huntTrainLevel = 0;
	public int drunkTrainLevel = 0;
	public Person markTarget = null;
	public int witchTrainLevel = 0;
	public int bloodTrainLevel = 0;
	public int defTrainLevel = 0;
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
