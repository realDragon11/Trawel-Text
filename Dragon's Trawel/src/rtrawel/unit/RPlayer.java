package rtrawel.unit;

import java.util.ArrayList;
import java.util.List;

import rtrawel.items.Armor;
import rtrawel.items.Weapon;
import rtrawel.items.WeaponFactory;
import rtrawel.jobs.JobFactory;
import rtrawel.jobs.PathWithLevel;
import rtrawel.jobs.Progression;
import rtrawel.unit.RUnit.FightingStance;
import trawel.extra;

public class RPlayer extends RUnit {

	private String name;
	
	private Weapon weap, shield;
	
	private Armor head, torso, arms, feet, assec1, assec2;
	
	private List<Action> abs = new ArrayList<Action>();
	private List<Action> spells = new ArrayList<Action>();
	private Progression progression = new Progression();
	private String currentJob;
	
	public RPlayer(String n, String job) {
		name = n;
		currentJob = job;
		//TODO;
		weap = WeaponFactory.getWeaponByName("copper sword");
		fStance = FightingStance.BALANCED;
		cleanAbs();
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
		return JobFactory.getJobByName(currentJob).getStrAtLevel(progression.jobLevel(currentJob));
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
		return JobFactory.getJobByName(currentJob).getKnoAtLevel(progression.jobLevel(currentJob));
	}

	@Override
	public int getMaxHp() {
		return JobFactory.getJobByName(currentJob).getHpAtLevel(progression.jobLevel(currentJob));
	}

	@Override
	public int getMaxMana() {
		return JobFactory.getJobByName(currentJob).getMpAtLevel(progression.jobLevel(currentJob));
	}

	@Override
	public int getMaxTension() {
		return JobFactory.getJobByName(currentJob).getTenAtLevel(progression.jobLevel(currentJob));
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
		return JobFactory.getJobByName(currentJob).getSpdAtLevel(progression.jobLevel(currentJob));
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
		return JobFactory.getJobByName(currentJob).getAgiAtLevel(progression.jobLevel(currentJob));
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
		return JobFactory.getJobByName(currentJob).getDexAtLevel(progression.jobLevel(currentJob));
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
		return JobFactory.getJobByName(currentJob).getResAtLevel(progression.jobLevel(currentJob));
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
		TargetGroup t;
		while (keepGoing) {
			extra.println(this.getName());
			extra.println("1 basic attack");
			extra.println("2 abilities");
			extra.println("3 spells");
			extra.println("4 items");
			extra.println("5 change stance");
		switch (extra.inInt(5)) {
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
		case 2: 
			for (int i = 0;i < abs.size();i++) {
				extra.println((i+1) + " " +abs.get(i).getName());
			}
			extra.println((abs.size()+1 )+" back");
			in = extra.inInt(abs.size()+1);
			if (in == abs.size()+1) {
				break;
			}
			Action ab = abs.get(in-1);
			if (!ab.canCast(this)) {
				extra.println("You can't cast that right now.");
				continue;
			}
			if (ab.getTargetType().equals(Action.TargetType.FOE)) {
				if (ab.getTargetGrouping().equals(Action.TargetGrouping.ALL)) {
					t = new TargetGroup();
					t.targets.addAll(curBattle.foes);
					decideOn(ab,t);
				}else {
					if (ab.getTargetGrouping().equals(Action.TargetGrouping.GROUP)) {
						
						for (int i = 0;i < curBattle.foeGroups.size();i++) {
							t = new TargetGroup();
							for (RUnit u: curBattle.foeGroups.get(i)) {
								t.targets.add(u);
							}
							extra.println((i+1) + " " + t.toString());
						}
						t = new TargetGroup();
						extra.inInt(curBattle.foeGroups.size());
						t.targets.addAll(curBattle.foeGroups.get(in-1));
						decideOn(ab,t);
						keepGoing = false;
					}else {
						valid = false;
						while (!valid) {
							in = extra.inInt(99);
							for (RUnit r: curBattle.foes) {
								if (((RMonster)r).getMonsterNumber() == in) {
									decideOn(ab,new TargetGroup(r));
									valid = true;
									keepGoing = false;
									break;
								}
							}
						}
					}
				}
				
			}else {
				
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
		if (shield == null) {
			return weap.blockChance();
		}
		return weap.blockChance() + shield.blockChance();
	}

	@Override
	public String getName() {
		return getBaseName();
	}

	@Override
	public String getBaseName() {
		return name;
	}
	
	public void cleanAbs() {
		abs.clear();
		spells.clear();
		buffMap.clear();
		for (PathWithLevel pwl: progression.paths) {
			pwl.path.apply(this, pwl.level,pwl.path.jobName().equals(currentJob));
		}
	}

}
