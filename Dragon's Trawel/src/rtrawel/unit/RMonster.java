package rtrawel.unit;

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getEquipAgility() {
		return 0;
	}

	@Override
	protected int getBaseAgility() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getEquipDexterity() {
		return 0;
	}

	@Override
	protected int getBaseDexterity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getEquipResilence() {
		return 0;
	}

	@Override
	protected int getBaseResilence() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected DamMultMap getEquipDamMultMap() {
		return emptyDM;
	}

	@Override
	public void decide() {
		
	}

}
