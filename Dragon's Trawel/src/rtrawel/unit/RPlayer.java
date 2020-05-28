package rtrawel.unit;

import java.util.ArrayList;
import java.util.List;

import rtrawel.items.Armor;
import rtrawel.items.Weapon;
import rtrawel.items.WeaponFactory;
import trawel.extra;

public class RPlayer extends RUnit {

	private String name;
	
	private Weapon weap, shield;
	
	private Armor head, torso, arms, feet, assec1, assec2;
	
	private List<Action> abs = new ArrayList<Action>();
	private List<Action> spells = new ArrayList<Action>();
	
	public RPlayer(String n) {
		name = n;
		//TODO;
		weap = WeaponFactory.getWeaponByName("copper sword");
	}
	
	public List<Armor> listOfArmor(){
		List<Armor> list = new ArrayList<Armor>();
		if (head != null) {
			list.add(head);}
		if (torso != null) {
			list.add(torso);}
		if (arms != null) {
			list.add(arms);}
		if (feet != null) {
			list.add(feet);}
		if (assec1 != null) {
			list.add(assec1);}
		if (assec2 != null) {
			list.add(assec2);}
		return list;
	}
	
	@Override
	protected int getEquipStrength() {
		int total = 0;
		for (Armor a: listOfArmor()) {
			total+=a.getStrengthMod();
		}
		return total;
	}

	@Override
	protected int getBaseStrength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getEquipKnowledge() {
		int total = 0;
		for (Armor a: listOfArmor()) {
			total+=a.getKnowledgeMod();
		}
		return total;
	}

	@Override
	protected int getBaseKnowledge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxHp() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public int getMaxMana() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public int getMaxTension() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	protected int getEquipSpeed() {
		int total = 0;
		for (Armor a: listOfArmor()) {
			total+=a.getSpeedMod();
		}
		return total;
	}

	@Override
	protected int getBaseSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getEquipAgility() {
		int total = 0;
		for (Armor a: listOfArmor()) {
			total+=a.getAgilityMod();
		}
		return total;
	}

	@Override
	protected int getBaseAgility() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getEquipDexterity() {
		int total = 0;
		for (Armor a: listOfArmor()) {
			total+=a.getDexterityMod();
		}
		return total;
	}

	@Override
	protected int getBaseDexterity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getEquipResilence() {
		int total = 0;
		for (Armor a: listOfArmor()) {
			total+=a.getResilenceMod();
		}
		return total;
	}

	@Override
	protected int getBaseResilence() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected DamMultMap getEquipDamMultMap() {
		//TODO: this REALLY needs to be cached
		DamMultMap totalMap = new DamMultMap();
		for (Armor a: listOfArmor()) {
			DamMultMap tempMap = a.getDMM();
			for (DamageType t: tempMap.getKeys()) {
				totalMap.insertOrAdd(t,tempMap.getMult(t));
			}
		}
		return totalMap;
	}

	@Override
	public void decide() {
		boolean keepGoing = true;
		int in;
		boolean valid;
		RUnit picked;
		while (keepGoing) {
			extra.println("1 basic attack");
			extra.println("2 abilities");
			extra.println("3 spells");
		switch (extra.inInt(3)) {
		case 1:
			for (RUnit r: curBattle.foes) {
				extra.println(r.getName());
			}
			valid = false;
			while (!valid) {
				in = extra.inInt(99);
				if (in == 99) {
					break;
				}
				for (RUnit r: curBattle.foes) {
					if (((RMonster)r).getMonsterNumber() == in) {
						decideOn(ActionFactory.getActionByName("attack"),new TargetGroup(r));
						valid = true;
						keepGoing = false;
						break;
					}
				}
			}
			break;
		
		}
		}

	}
	
	private void decideOn(Action a,TargetGroup g) {
		this.a = a;
		warmUp = a.warmUp();
		upComing = a.coolDown();
		savedTarget = g;
	}

	@Override
	public Weapon getWeapon() {
		return weap;
	}

	@Override
	public double shieldBlockChance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return getBaseName();
	}

	@Override
	public String getBaseName() {
		return name;
	}

}
