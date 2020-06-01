package rtrawel.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtrawel.items.ArmorFactory;
import rtrawel.items.Item;
import rtrawel.items.WeaponFactory;
import rtrawel.unit.RUnit.RaceType;

public class MonsterFactory {
	private static HashMap<String,MonsterData> data = new HashMap<String, MonsterData>();
	public static void init(){
		data.put("wolf pup",new MonsterData() {
			

			@Override
			public int getStrength() {
				return 7;
			}

			@Override
			public int getKnowledge() {
				return 0;
			}

			@Override
			public int getMaxHp() {
				return 9;
			}

			@Override
			public int getMaxMana() {
				return 0;
			}

			@Override
			public int getMaxTension() {
				return 0;
			}

			@Override
			public int getSpeed() {
				return 10;
			}

			@Override
			public int getAgility() {
				return 5;
			}

			@Override
			public int getDexterity() {
				return 5;
			}

			@Override
			public int getResilence() {
				return 2;
			}

			@Override
			public List<Action> getActions() {
				List<Action> list = new ArrayList<Action>();
				list.add(ActionFactory.getActionByName("attack"));
				return list;
			}

			@Override
			public String getName() {
				return "wolf pup";
			}

			@Override
			public String getDesc() {
				return "Not quite the fearsome foe, this pup is easily slain.";
			}

			@Override
			public DamMultMap getDamMultMap() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int getXp() {
				return 2;
			}

			@Override
			public int getGold() {
				return 3;
			}

			@Override
			public String getDrop() {
				return "canine's canine";
			}

			@Override
			public double getDropChance() {
				return 1.0/32.0;
			}

			@Override
			public String getRareDrop() {
				return "wolf pelt";
			}

			@Override
			public double getRareDropChance() {
				return 1.0/64.0;
			}

			@Override
			public int getKillsTilKnown() {
				return 5;
			}

			@Override
			public int getKillsTilVeryKnown() {
				return 10;
			}

			@Override
			public String getWeapon() {
				return "wolf pup teeth";
			}

			@Override
			public SpriteData getSpriteData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double shieldBlockChance() {
				return 0;
			}

			@Override
			public void initer(RMonster r) {
				r.addRaceType(RaceType.BEAST);
			}});
		
		data.put("fearless fella",new MonsterData() {
			

			@Override
			public int getStrength() {
				return 12;
			}

			@Override
			public int getKnowledge() {
				return 0;
			}

			@Override
			public int getMaxHp() {
				return 14;
			}

			@Override
			public int getMaxMana() {
				return 0;
			}

			@Override
			public int getMaxTension() {
				return 0;
			}

			@Override
			public int getSpeed() {
				return 2;
			}

			@Override
			public int getAgility() {
				return 0;
			}

			@Override
			public int getDexterity() {
				return 0;
			}

			@Override
			public int getResilence() {
				return 4;
			}

			@Override
			public List<Action> getActions() {
				List<Action> list = new ArrayList<Action>();
				list.add(ActionFactory.getActionByName("attack"));
				return list;
			}

			@Override
			public String getName() {
				return "fearless fella";
			}

			@Override
			public String getDesc() {
				return "Look out, he's got a knife!";
			}

			@Override
			public DamMultMap getDamMultMap() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int getXp() {
				return 4;
			}

			@Override
			public int getGold() {
				return 4;
			}

			@Override
			public String getDrop() {
				return "leather hood";
			}

			@Override
			public double getDropChance() {
				return (1.0/32.0);
			}

			@Override
			public String getRareDrop() {
				return "simple stabber";
			}

			@Override
			public double getRareDropChance() {
				return (1.0/64.0);
			}

			@Override
			public int getKillsTilKnown() {
				return 4;
			}

			@Override
			public int getKillsTilVeryKnown() {
				return 8;
			}

			@Override
			public String getWeapon() {
				return "fella knife";
			}

			@Override
			public SpriteData getSpriteData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double shieldBlockChance() {
				return 0;
			}

			@Override
			public void initer(RMonster r) {
				r.addRaceType(RaceType.HUMANOID);
			}});
		
		data.put("dendroid",new MonsterData() {
			

			@Override
			public int getStrength() {
				return 2;
			}

			@Override
			public int getKnowledge() {
				return 4;
			}

			@Override
			public int getMaxHp() {
				return 12;
			}

			@Override
			public int getMaxMana() {
				return 0;
			}

			@Override
			public int getMaxTension() {
				return 0;
			}

			@Override
			public int getSpeed() {
				return 0;
			}

			@Override
			public int getAgility() {
				return 0;
			}

			@Override
			public int getDexterity() {
				return 10;
			}

			@Override
			public int getResilence() {
				return 24;
			}

			@Override
			public List<Action> getActions() {
				List<Action> list = new ArrayList<Action>();
				list.add(ActionFactory.getActionByName("attack"));
				return list;
			}

			@Override
			public String getName() {
				return "dendroid";
			}

			@Override
			public String getDesc() {
				return "A small ent.";
			}

			@Override
			public DamMultMap getDamMultMap() {
				DamMultMap map = new DamMultMap();
				map.insertOrAdd(DamageType.FIRE,1.5);
				return map;
			}

			@Override
			public int getXp() {
				return 6;
			}

			@Override
			public int getGold() {
				return 2;
			}

			@Override
			public String getDrop() {
				return "medicine herb";
			}

			@Override
			public double getDropChance() {
				return (1.0/16.0);
			}

			@Override
			public String getRareDrop() {
				return "basic tincture";
			}

			@Override
			public double getRareDropChance() {
				return (1.0/64.0);
			}

			@Override
			public int getKillsTilKnown() {
				return 4;
			}

			@Override
			public int getKillsTilVeryKnown() {
				return 8;
			}

			@Override
			public String getWeapon() {
				return "dendroid branch";
			}

			@Override
			public SpriteData getSpriteData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double shieldBlockChance() {
				return 0;
			}

			@Override
			public void initer(RMonster r) {
				r.addRaceType(RaceType.PLANT);
			}});
		
		data.put("feral fungus",new MonsterData() {
			

			@Override
			public int getStrength() {
				return 6;
			}

			@Override
			public int getKnowledge() {
				return 8;
			}

			@Override
			public int getMaxHp() {
				return 10;
			}

			@Override
			public int getMaxMana() {
				return 4;
			}

			@Override
			public int getMaxTension() {
				return 0;
			}

			@Override
			public int getSpeed() {
				return 5;
			}

			@Override
			public int getAgility() {
				return 5;
			}

			@Override
			public int getDexterity() {
				return 10;
			}

			@Override
			public int getResilence() {
				return 2;
			}

			@Override
			public List<Action> getActions() {
				List<Action> list = new ArrayList<Action>();
				list.add(ActionFactory.getActionByName("attack"));
				list.add(ActionFactory.getActionByName("enfeebling spores"));
				return list;
			}

			@Override
			public String getName() {
				return "feral fungus";
			}

			@Override
			public String getDesc() {
				return "Watch out for their enfeebling spores!";
			}

			@Override
			public DamMultMap getDamMultMap() {
				DamMultMap map = new DamMultMap();
				map.insertOrAdd(DamageType.FIRE,1.5);
				return map;
			}

			@Override
			public int getXp() {
				return 4;
			}

			@Override
			public int getGold() {
				return 2;
			}

			@Override
			public String getDrop() {
				return "medicine herb";
			}

			@Override
			public double getDropChance() {
				return (1.0/16.0);
			}

			@Override
			public String getRareDrop() {
				return "basic tincture";
			}

			@Override
			public double getRareDropChance() {
				return (1.0/64.0);
			}

			@Override
			public int getKillsTilKnown() {
				return 5;
			}

			@Override
			public int getKillsTilVeryKnown() {
				return 10;
			}

			@Override
			public String getWeapon() {
				return "dendroid branch";
			}

			@Override
			public SpriteData getSpriteData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double shieldBlockChance() {
				return 0;
			}

			@Override
			public void initer(RMonster r) {
				r.addRaceType(RaceType.PLANT);
			}});
		
		data.put("living root",new MonsterData() {
			

			@Override
			public int getStrength() {
				return 0;
			}

			@Override
			public int getKnowledge() {
				return 8;
			}

			@Override
			public int getMaxHp() {
				return 4;
			}

			@Override
			public int getMaxMana() {
				return 0;
			}

			@Override
			public int getMaxTension() {
				return 0;
			}

			@Override
			public int getSpeed() {
				return 0;
			}

			@Override
			public int getAgility() {
				return 0;
			}

			@Override
			public int getDexterity() {
				return 10;
			}

			@Override
			public int getResilence() {
				return 60;
			}

			@Override
			public List<Action> getActions() {
				List<Action> list = new ArrayList<Action>();
				list.add(ActionFactory.getActionByName("attack"));
				list.add(ActionFactory.getActionByName("root of resilience"));
				return list;
			}

			@Override
			public String getName() {
				return "living root";
			}

			@Override
			public String getDesc() {
				return "A root that's come alive, but holds resilent nutrients.";
			}

			@Override
			public DamMultMap getDamMultMap() {
				DamMultMap map = new DamMultMap();
				map.insertOrAdd(DamageType.FIRE,1.5);
				return map;
			}

			@Override
			public int getXp() {
				return 2;
			}

			@Override
			public int getGold() {
				return 1;
			}

			@Override
			public String getDrop() {
				return "medicine herb";
			}

			@Override
			public double getDropChance() {
				return (1.0/16.0);
			}

			@Override
			public String getRareDrop() {
				return "root of resilence";
			}

			@Override
			public double getRareDropChance() {
				return (1.0/128.0);
			}

			@Override
			public int getKillsTilKnown() {
				return 7;
			}

			@Override
			public int getKillsTilVeryKnown() {
				return 12;
			}

			@Override
			public String getWeapon() {
				return "dendroid branch";
			}

			@Override
			public SpriteData getSpriteData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double shieldBlockChance() {
				return 0;
			}

			@Override
			public void initer(RMonster r) {
				r.addRaceType(RaceType.PLANT);
			}});
		
		data.put("well lurker",new MonsterData() {
			

			@Override
			public int getStrength() {
				return 16;
			}

			@Override
			public int getKnowledge() {
				return 0;
			}

			@Override
			public int getMaxHp() {
				return 16;
			}

			@Override
			public int getMaxMana() {
				return 0;
			}

			@Override
			public int getMaxTension() {
				return 0;
			}

			@Override
			public int getSpeed() {
				return 4;
			}

			@Override
			public int getAgility() {
				return 5;
			}

			@Override
			public int getDexterity() {
				return 0;
			}

			@Override
			public int getResilence() {
				return 6;
			}

			@Override
			public List<Action> getActions() {
				List<Action> list = new ArrayList<Action>();
				list.add(ActionFactory.getActionByName("attack"));
				return list;
			}

			@Override
			public String getName() {
				return "well lurker";
			}

			@Override
			public String getDesc() {
				return "Beware their defense lowering bite.";
			}

			@Override
			public DamMultMap getDamMultMap() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int getXp() {
				return 7;
			}

			@Override
			public int getGold() {
				return 5;
			}

			@Override
			public String getDrop() {
				return "leather hood";
			}

			@Override
			public double getDropChance() {
				return (1.0/16.0);
			}

			@Override
			public String getRareDrop() {
				return "leather shirt";
			}

			@Override
			public double getRareDropChance() {
				return (1.0/32.0);
			}

			@Override
			public int getKillsTilKnown() {
				return 4;
			}

			@Override
			public int getKillsTilVeryKnown() {
				return 8;
			}

			@Override
			public String getWeapon() {
				return "well lurker teeth";
			}

			@Override
			public SpriteData getSpriteData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double shieldBlockChance() {
				return 0;
			}

			@Override
			public void initer(RMonster r) {
				r.addRaceType(RaceType.HUMANOID);
				r.addRaceType(RaceType.FISH);
			}});
		
		data.put("giant squid",new MonsterData() {
			

			@Override
			public int getStrength() {
				return 32;
			}

			@Override
			public int getKnowledge() {
				return 0;
			}

			@Override
			public int getMaxHp() {
				return 40;
			}

			@Override
			public int getMaxMana() {
				return 0;
			}

			@Override
			public int getMaxTension() {
				return 30;
			}

			@Override
			public int getSpeed() {
				return 10;
			}

			@Override
			public int getAgility() {
				return 15;
			}

			@Override
			public int getDexterity() {
				return 0;
			}

			@Override
			public int getResilence() {
				return 15;
			}

			@Override
			public List<Action> getActions() {
				List<Action> list = new ArrayList<Action>();
				list.add(ActionFactory.getActionByName("attack"));
				return list;
			}

			@Override
			public String getName() {
				return "giant squid";
			}

			@Override
			public String getDesc() {
				return "Their ink spray is annoying, to say the least.";
			}

			@Override
			public DamMultMap getDamMultMap() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int getXp() {
				return 30;
			}

			@Override
			public int getGold() {
				return 20;
			}

			@Override
			public String getDrop() {
				return "ink sac";
			}

			@Override
			public double getDropChance() {
				return (1.0/32.0);
			}

			@Override
			public String getRareDrop() {
				return "ink sac";
			}

			@Override
			public double getRareDropChance() {
				return (1.0/32.0);
			}

			@Override
			public int getKillsTilKnown() {
				return 4;
			}

			@Override
			public int getKillsTilVeryKnown() {
				return 8;
			}

			@Override
			public String getWeapon() {
				return "giant squid teeth";
			}

			@Override
			public SpriteData getSpriteData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double shieldBlockChance() {
				return 0;
			}

			@Override
			public void initer(RMonster r) {
				r.addRaceType(RaceType.FISH);
			}});
	}
	
	
	
	
	public static MonsterData getMonsterByName(String str) {
		return data.get(str);
	}
}
