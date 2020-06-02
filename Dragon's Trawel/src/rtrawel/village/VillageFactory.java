package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.EventFlag;
import rtrawel.battle.FightFactory;

public class VillageFactory {

	public static List<Village> villages = new ArrayList<Village>();
	
	public static Village init() {
		Village homa = new Village("homa");//plants
		villages.add(homa);
		homa.conts.add(new Church());
		homa.conts.add(new Inn(5));
		Shop s = new Shop("item shop");
		s.items.add("copper sword");
		s.items.add("lumber axe");
		s.items.add("carpenter hammer");
		s.items.add("pointy stick");
		s.items.add("fishing spear");
		s.items.add("small sling");
		s.items.add("basic bow");
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
		homa.addFight(FightFactory.getFightByName("homa_shroom1"));
		homa.addLoot("medicine herb",.05);
		homa.addLoot("pot lid",.1);
		homa.addLoot("much 'o mushroom",.2);
		Village unun = new Village("unun");//animated metals
		villages.add(unun);
		unun.addFight(FightFactory.getFightByName("unun_armor1"));
		unun.addFight(FightFactory.getFightByName("unun_golem1"));
		unun.addFight(FightFactory.getFightByName("unun_golem2"));
		unun.conts.add(new Church());
		unun.conts.add(new Inn(10));
		if (EventFlag.eventFlag.getEF("unun_recruit_1") == 0) {
			unun.conts.add(new RecruitSpot("recruit andrea","andrea", unun,"unun_recruit_1", "cleric","priest"));
		}
		if (EventFlag.eventFlag.getEF("unun_recruit_2") == 0) {
			unun.conts.add(new RecruitSpot("recruit brittney","brit", unun,"unun_recruit_2", "elementalist"));
		}
		s = new Shop("weapon shop");
		s.items.add("copper broadsword");
		s.items.add("iron sword");
		s.items.add("wooden wand");
		s.items.add("leather shield");
		s.items.add("studded leather shield");
		unun.conts.add(s);
		
		s = new Shop("item shop");
		s.items.add("medicine herb");
		s.items.add("basic tincture");
		s.items.add("root of resilience");
		unun.conts.add(s);
		
		s = new Shop("armor shop");
		s.items.add("antimagic circlet");
		s.items.add("mail shirt");
		s.items.add("mail hood");
		unun.conts.add(s);
		
		new Connection(homa,unun,null,"homa_unun_boss");
		Village homa_pit = new Village("homan well");//fish
		villages.add(homa_pit);
		homa_pit.addFight(FightFactory.getFightByName("well_lurker1"));
		homa_pit.addFight(FightFactory.getFightByName("well_lurker2"));
		homa_pit.addFight(FightFactory.getFightByName("well_root1"));
		homa.addLoot("lucky coin",.05);
		homa.addLoot("band of brilliance",.05);
		homa.addLoot("pot lid",.2);
		if (EventFlag.eventFlag.getEF("homa_unun_boss") == 0) {
		homa_pit.conts.add(new BossContent("ralph the squid",FightFactory.getFightByName("homa_unun_boss"),homa_pit,2));}
		new Connection(homa,homa_pit,null,null);
		
		Village hemo = new Village("hemo");//witches
		hemo.conts.add(new Church());
		hemo.conts.add(new Inn(7));
		hemo.conts.add(new WitchHut("hemo_recipes"));
		villages.add(hemo);
		hemo.addFight(FightFactory.getFightByName("hemo_witch1"));
		hemo.addFight(FightFactory.getFightByName("hemo_witch2"));
		new Connection(unun,hemo,null,null);
		
		Village revan = new Village("revan");//???
		revan.conts.add(new Church());
		revan.conts.add(new Inn(10));
		revan.conts.add(new ReJober());
		villages.add(revan);
		new Connection(unun,revan,null,null);
		
		return villages.get(0);
	}
}
