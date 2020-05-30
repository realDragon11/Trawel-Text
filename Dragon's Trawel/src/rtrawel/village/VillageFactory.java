package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.FightFactory;

public class VillageFactory {

	public static List<Village> villages = new ArrayList<Village>();
	
	public static Village init() {
		Village homa = new Village("homa");
		villages.add(homa);
		homa.conts.add(new Church());
		homa.conts.add(new Inn(5));
		Shop s = new Shop();
		s.items.add("copper sword");
		s.items.add("lumber axe");
		s.items.add("pot lid");
		s.items.add("carpenter hammer");
		s.items.add("cloth hood");
		s.items.add("cloth shirt");
		s.items.add("leather hood");
		s.items.add("leather shirt");
		s.items.add("medicine herb");
		homa.conts.add(s);
		homa.addFight(FightFactory.getFightByName("homa_pup1"));
		homa.addFight(FightFactory.getFightByName("homa_fella1"));
		Village unun = new Village("unun");
		villages.add(unun);
		unun.conts.add(new Church());
		unun.conts.add(new Inn(10));
		new Connection(homa,unun,null);
		return villages.get(0);
	}
}
