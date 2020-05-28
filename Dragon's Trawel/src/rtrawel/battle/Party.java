package rtrawel.battle;

import java.util.ArrayList;
import java.util.List;

import rtrawel.unit.RUnit;

public class Party {

	public static Party party = new Party();
	
	public List<RUnit> list = new ArrayList<RUnit>();
	
	public int gold = 0;
}
