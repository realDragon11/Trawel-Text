package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.EventFlag;
import rtrawel.battle.FightFactory;

public class VillageFactory {

	public static List<Village> villages = new ArrayList<Village>();
	
	public static Village init() {
		Village homa = new Village("homa");
		villages.add(homa);
		homa.conts.add(new Church());
		homa.conts.add(new Inn(5));
		Shop s = new Shop("item shop");
		s.items.add("copper sword");
		s.items.add("lumber axe");
		s.items.add("carpenter hammer");
		s.items.add("pointy stick");
		s.items.add("fishing spear");
		s.items.add("pot lid");
		s.items.add("cloth hood");
		s.items.add("cloth shirt");
		s.items.add("leather hood");
		s.items.add("leather shirt");
		s.items.add("medicine herb");
		homa.conts.add(s);
		homa.addFight(FightFactory.getFightByName("homa_pup1"));
		homa.addFight(FightFactory.getFightByName("homa_fella1"));
		homa.addFight(FightFactory.getFightByName("homa_ent1"));
		homa.addFight(FightFactory.getFightByName("homa_fungus1"));
		homa.addFight(FightFactory.getFightByName("homa_ent2"));
		homa.addFight(FightFactory.getFightByName("homa_fungus2"));
		homa.addFight(FightFactory.getFightByName("homa_root1"));
		homa.addLoot("medicine herb",.05);
		homa.addLoot("pot lid",.1);
		Village unun = new Village("unun");
		villages.add(unun);
		unun.conts.add(new Church());
		unun.conts.add(new Inn(10));
		new Connection(homa,unun,null,"homa_unun_boss");
		Village homa_pit = new Village("homan well");
		homa_pit.addFight(FightFactory.getFightByName("well_lurker1"));
		homa_pit.addFight(FightFactory.getFightByName("well_lurker2"));
		homa_pit.addFight(FightFactory.getFightByName("well_root1"));
		homa.addLoot("lucky coin",.05);
		homa.addLoot("band of brilliance",.05);
		homa.addLoot("pot lid",.2);
		if (EventFlag.eventFlag.getEF("homa_unun_boss") == 0) {
		homa_pit.conts.add(new BossContent("ralph the squid",FightFactory.getFightByName("homa_unun_boss"),homa_pit,2));}
		new Connection(homa,homa_pit,null,null);
		return villages.get(0);
	}
}
