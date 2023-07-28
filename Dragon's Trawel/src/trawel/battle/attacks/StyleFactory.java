package trawel.battle.attacks;
import java.util.ArrayList;
import com.github.yellowstonegames.core.WeightedTable;

import trawel.extra;

public class StyleFactory {

	public static ArrayList<Style> StyleList = new ArrayList<Style>();
	
	public static WeightedTable styleTable;
	
	public StyleFactory() {
		Style s = new Style();
		s.name = "";//normal
		s.damage = 1;
		s.hit = 1;
		s.speed = 1;
		s.rarity = 3;
		StyleList.add(s);
		
		s = new Style();
		s.name = "precise ";
		s.damage = 1;
		s.hit = 1.3;
		s.speed = 1.3;
		s.rarity = 1;
		StyleList.add(s);
		
		s = new Style();
		s.name = "wild ";
		s.damage = 1;
		s.hit = .7;
		s.speed = .7;
		s.rarity = 1;
		StyleList.add(s);
		
		s = new Style();
		s.name = "heavy ";
		s.damage = 1.2;
		s.hit = 1;
		s.speed = 1.2;
		s.rarity = 1;
		StyleList.add(s);
		
		s = new Style();
		s.name = "quick ";
		s.damage = .8;
		s.hit = 1;
		s.speed = .8;
		s.rarity = 1;
		StyleList.add(s);
		
		
		tableSetup();
	}
	
	public static void tableSetup() {
		float[] sWeightList = new float[StyleList.size()];
		for (int i = StyleList.size()-1;i>=0;--i) {
			sWeightList[i] = (float) StyleList.get(i).rarity;
		}
		styleTable = new WeightedTable(sWeightList);
	}
	
	
	public static Style randStyle() {
		return StyleList.get(styleTable.random(extra.getRand()));
	}
}
