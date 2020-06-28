package rtrawel.battle;

import java.util.HashMap;

import rtrawel.items.Armor;

public class FightFactory {
	private static HashMap<String,Fight> data = new HashMap<String, Fight>();
	public static void init() {
		Fight f = new Fight();
		f.addFoes("wolf pup",3);
		data.put("homa_pup1",f);
		f = new Fight();
		f.addFoes("fearless fella",1);
		f.addFoes("wolf pup",1);
		data.put("homa_fella1",f);
		f = new Fight();
		f.addFoes("dendroid",2);
		data.put("homa_ent1",f);
		f = new Fight();
		f.addFoes("feral fungus",2);
		data.put("homa_fungus1",f);
		
		f = new Fight();
		f.addFoes("feral fungus",1);
		f.addFoes("living root",2);
		data.put("homa_fungus2",f);
		
		f = new Fight();
		f.addFoes("dendroid",1);
		f.addFoes("living root",2);
		data.put("homa_ent2",f);
		f = new Fight();
		f.addFoes("living root",4);
		data.put("homa_root1",f);
		
		f = new Fight();
		f.addFoes("shroom soldier",1);
		data.put("homa_shroom1",f);
		
		f = new Fight();
		f.addFoes("living root",6);
		data.put("well_root1",f);
		f = new Fight();
		f.addFoes("well lurker",3);
		data.put("well_lurker1",f);
		f = new Fight();
		f.addFoes("well lurker",1);
		f.addFoes("living root",3);
		data.put("well_lurker2",f);
		
		f = new Fight();
		f.addFoes("animated armor",3);
		data.put("unun_armor1",f);
		f = new Fight();
		f.addFoes("iron golem",1);
		f.addFoes("animated armor",1);
		data.put("unun_golem1",f);
		f = new Fight();
		f.addFoes("iron golem",1);
		f.addFoes("metal minion",3);
		data.put("unun_golem2",f);
		
		f = new Fight();
		f.addFoes("whining witch",1);
		f.addFoes("animated broom",4);
		data.put("hemo_witch1",f);
		f = new Fight();
		f.addFoes("whining witch",2);
		f.addFoes("animated broom",1);
		data.put("hemo_witch2",f);
		
		f = new Fight();
		f.addFoes("giant squid",1);
		f.addFlag("homa_unun_boss");
		data.put("homa_unun_boss",f);
		
		f = new Fight();
		f.addFoes("stone rune door",1);
		data.put("stone_runedoor",f);
	}
	
	public static Fight getFightByName(String str) {
		if (!data.containsKey(str)) {
			throw new RuntimeException("key not found");
		}
		return data.get(str);
	}
}
