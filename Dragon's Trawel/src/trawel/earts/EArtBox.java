package trawel.earts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EArtBox implements Serializable {

	public List<ASpell> aSpells = new ArrayList<ASpell>();
	public double aSpellPower = 1;
	
	public ASpell aSpell1, aSpell2;
	public int exeTrainLevel = 0;
}
