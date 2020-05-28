package rtrawel.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtrawel.items.Item;
import rtrawel.items.WeaponFactory;

public class MonsterFactory {
	private static HashMap<String,MonsterData> data = new HashMap<String, MonsterData>();
	public static void init(){
		data.put("wolf pup",new MonsterData() {

			@Override
			public int getStrength() {
				return 3;
			}

			@Override
			public int getKnowledge() {
				return 0;
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
			public Item getDrop() {
				return null;
			}

			@Override
			public double getDropChance() {
				return 0;
			}

			@Override
			public Item getRareDrop() {
				return null;
			}

			@Override
			public double getRareDropChance() {
				// TODO Auto-generated method stub
				return 0;
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
			}});
	}
	
	
	public static MonsterData getMonsterByName(String str) {
		return data.get(str);
	}
}
