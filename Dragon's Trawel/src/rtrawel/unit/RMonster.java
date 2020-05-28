package rtrawel.unit;

import java.util.List;

import rtrawel.items.Weapon;
import rtrawel.items.WeaponFactory;
import trawel.extra;

public class RMonster extends RUnit {
	
	private String name;
	private static DamMultMap emptyDM = new DamMultMap();
	
	public RMonster(String name) {
		this.name = name;
		this.dmm = MonsterFactory.getMonsterByName(name).getDamMultMap();
	}
	public String getName() {
		return name;
	}

	@Override
	protected int getEquipStrength() {
		return 0;
	}

	@Override
	protected int getBaseStrength() {
		return  MonsterFactory.getMonsterByName(name).getStrength();
	}

	@Override
	protected int getEquipKnowledge() {
		return 0;
	}

	@Override
	protected int getBaseKnowledge() {
		return  MonsterFactory.getMonsterByName(name).getKnowledge();
	}

	@Override
	public int getMaxHp() {
		return  MonsterFactory.getMonsterByName(name).getMaxHp();
	}

	@Override
	public int getMaxMana() {
		return  MonsterFactory.getMonsterByName(name).getMaxMana();
	}

	@Override
	public int getMaxTension() {
		return  MonsterFactory.getMonsterByName(name).getMaxTension();
	}

	@Override
	protected int getEquipSpeed() {
		return 0;
	}

	@Override
	protected int getBaseSpeed() {
		return  MonsterFactory.getMonsterByName(name).getSpeed();
	}

	@Override
	protected int getEquipAgility() {
		return 0;
	}

	@Override
	protected int getBaseAgility() {
		return  MonsterFactory.getMonsterByName(name).getAgility();
	}

	@Override
	protected int getEquipDexterity() {
		return 0;
	}

	@Override
	protected int getBaseDexterity() {
		return  MonsterFactory.getMonsterByName(name).getDexterity();
	}

	@Override
	protected int getEquipResilence() {
		return 0;
	}

	@Override
	protected int getBaseResilence() {
		return  MonsterFactory.getMonsterByName(name).getResilence();
	}

	@Override
	protected DamMultMap getEquipDamMultMap() {
		return emptyDM;
	}

	@Override
	public void decide() {
		List<Action> list = MonsterFactory.getMonsterByName(name).getActions();
		list.stream().filter(p -> !p.canCast(this)).forEach(list::remove);
		a = extra.randList(list);
		warmUp = a.warmUp();
		upComing = a.coolDown();
	}
	@Override
	public Weapon getWeapon() {
		return WeaponFactory.getWeaponByName(MonsterFactory.getMonsterByName(name).getWeapon());
	}
	
	public int getXpWorth() {
		return  MonsterFactory.getMonsterByName(name).getXp();
	}
	public int getGoldWorth() {
		return  MonsterFactory.getMonsterByName(name).getGold();
	}

}
