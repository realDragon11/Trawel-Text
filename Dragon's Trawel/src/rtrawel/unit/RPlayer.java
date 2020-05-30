package rtrawel.unit;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import rtrawel.items.Armor;
import rtrawel.items.Consumable;
import rtrawel.items.ConsumableFactory;
import rtrawel.items.Item;
import rtrawel.items.Weapon;
import rtrawel.items.Weapon.WeaponType;
import rtrawel.items.WeaponFactory;
import rtrawel.items.Item.ItemType;
import rtrawel.jobs.JobFactory;
import rtrawel.jobs.JobWithLevel;
import rtrawel.jobs.PathFactory;
import rtrawel.jobs.PathWithLevel;
import rtrawel.jobs.Progression;
import rtrawel.unit.RUnit.FightingStance;
import trawel.extra;

public class RPlayer extends RUnit {

	private String name;
	
	public Weapon weap, shield;
	
	public Armor head, torso, arms, pants, feet, assec1, assec2;
	
	private List<Action> abs = new ArrayList<Action>();
	public Progression progression = new Progression();
	public String currentJob;
	public List<Item> inventory = new ArrayList<Item>();
	
	public RPlayer(String n, String job) {
		name = n;
		currentJob = job;
		//TODO;
		//progression.jobs.add(new JobWithLevel("warrior",1));
		weap = WeaponFactory.getWeaponByName("copper sword");
		//inventory.add(WeaponFactory.getWeaponByName("lumber axe"));
		//inventory.add(ConsumableFactory.getConsumableByName("medicine herb"));
		fStance = FightingStance.BALANCED;
		cleanAbs();
	}
	
	public RPlayer(String n, String job, Progression p, Weapon weapon, Weapon shield,Armor head, Armor torso, Armor arms,Armor pants, Armor feet, Armor a1, Armor a2,List<Item> inventory, int hp, int mana) {
		name = n;
		currentJob = job;
		weap = weapon;
		this.shield = shield;
		this.head = head;
		this.torso = torso;
		this.arms = arms;
		this.pants = pants;
		this.feet = feet;
		this.assec1 = a1;
		this.assec2 = a2;
		this.inventory = inventory;
		progression = p;
		this.mp = mana;
		this.hp = hp;
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
		if (pants != null) {
			list.add(pants);}
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
		if (curBattle.foes.size() == 0) {
			return;
		}
		boolean keepGoing = true;
		int in;
		boolean valid;
		while (keepGoing) {
			extra.println(this.getName() + " HP: " + this.getHp() + "/" + this.getMaxHp() + " MP: " + this.getMana() + "/" + this.getMaxMana() + " Tsn: " + this.getTension() + "/" + this.getMaxTension());
			extra.println("1 basic attack");
			extra.println("2 abilities and spells");
			extra.println("3 items");
			extra.println("4 change stance");
		switch (extra.inInt(4)) {
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
				extra.println((i+1) + " " +abs.get(i).getName() + (abs.get(i).canCast(this) ? "" : " (locked)"));
			}
			extra.println((abs.size()+1 )+" back");
			in = extra.inInt(abs.size()+1);
			if (in == abs.size()+1) {
				continue;
			}
			keepGoing = chooseAbTarget(abs.get(in-1));
			break;
		case 3:
			for (int i = 0;i < inventory.size();i++) {
				extra.println((i+1) + " " +inventory.get(i).getName());//make sure only valid things can go into inventory later
			}
			extra.println((inventory.size()+1 )+" back");
			in = extra.inInt(inventory.size()+1);
			if (in == inventory.size()+1) {
				continue;
			}
			Item it = inventory.get(in-1);
			if (it.getItemType().equals(ItemType.CONSUMABLE)) {
				keepGoing = chooseAbTarget(((Consumable)it).getAction());
			}else {
				if (it.getItemType().equals(ItemType.WEAPON)) {
					if (JobFactory.getJobByName(currentJob).weaponTypes().contains(((Weapon)it).getWeaponType())){
						extra.println("You swap out your " + weap.getName() + " for your " + it.getName() + ".");
						inventory.add(this.getWeapon());
						inventory.remove(it);
						this.weap = (Weapon)it;
						this.cleanAbs();
					}
				}else {
					
				}
			}
			
			break;
		case 4:
			extra.println("Current Stance: " + fStance.name().toLowerCase());
			extra.println("1 offensive");
			extra.println("2 balanced");
			extra.println("3 defensive");
			extra.println("4 back");
			switch (extra.inInt(4)) {
			case 1: this.fStance = FightingStance.OFFENSIVE; keepGoing = false;break;
			case 2: this.fStance = FightingStance.BALANCED; keepGoing = false;break;
			case 3: this.fStance = FightingStance.DEFENSIVE; keepGoing = false;break;
			}
			break;
			
		}
		}

	}
	
	private boolean chooseAbTarget(Action ab) {
		int in;
		boolean valid;
		TargetGroup t;
		if (!ab.canCast(this)) {
			extra.println("You can't cast that right now.");
			return true;
		}
		if (ab.getTargetType().equals(Action.TargetType.SELF_ONLY)) {
			decideOn(ab,new TargetGroup(this));
			return false;
		}
		if (ab.getTargetType().equals(Action.TargetType.FOE)) {
			if (ab.getTargetGrouping().equals(Action.TargetGrouping.ALL)) {
				t = new TargetGroup();
				t.targets.addAll(curBattle.foes);
				decideOn(ab,t);
				return false;
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
					in = extra.inInt(curBattle.foeGroups.size());
					t.targets.addAll(curBattle.foeGroups.get(in-1));
					decideOn(ab,t);
					return false;
				}else {
					for (RUnit r: curBattle.foes) {
						extra.println(r.getName());
					}
					valid = false;
					while (!valid) {
						in = extra.inInt(99);
						for (RUnit r: curBattle.foes) {
							if (((RMonster)r).getMonsterNumber() == in) {
								decideOn(ab,new TargetGroup(r));
								valid = true;
								return false;
							}
						}
					}
				}
			}
			
		}else {
			if (ab.getTargetGrouping().equals(Action.TargetGrouping.SINGLE)) {
				for (int i = 0;i<curBattle.party.size();i++) {//TODO battle rezes?
					extra.println((1+i) + " "+curBattle.party.get(i).getName());
				}
				in = extra.inInt(curBattle.party.size());
				decideOn(ab,new TargetGroup(curBattle.party.get(in-1)));
				return false;
			}else {
				t = new TargetGroup();
				t.targets.addAll(curBattle.party);
				decideOn(ab,t);
				return false;
			}
		}
		return true;
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
	
	public void cleanAbs() {//needs to be called on level up and equipment swap
		abs.clear();
		buffMap.buffs.clear();
		for (PathWithLevel pwl: progression.paths) {
			PathFactory.getPathByName(pwl.path).apply(this, pwl.level,pwl.path.equals(currentJob));//TODO make active weapons active
		}
	}

	public void addAbility(Action action) {
		abs.add(action);
	}
	
	public void addBuff(Buff b) {
		buffMap.buffs.add(b);
	}

	public void debugAddPathPoints(String string, int i) {
		progression.addPathPoints(string,i,this);
		
	}
	@Override
	public void earnXp(int totalxp) {
		progression.addJobXp(this.currentJob,totalxp,this);
	}

	public String getJob() {
		return this.currentJob;
	}

	public void setWeapon(Weapon item) {
		this.weap = item;
	}

	public boolean assignItems() {
		int i;
		for (i = 0;i < inventory.size();i++) {
			extra.println((i + 1) + " " + inventory.get(i).getName());
		}
		i++;//extra i++
		if (inventory.size() < 3) {
			extra.println((i++) + " empty slot");
		}
		extra.println((i) + " back");
		int in = extra.inInt(i);
		if (in <= inventory.size()+1) {
			if (in <= inventory.size()) {
			Party.party.addItem(inventory.get(in-1).getName(),1);
			inventory.remove(in-1);}
			Item item = Party.party.getPersonItem();
			if (item != null) {
				inventory.add(item);
			}
			return true;
		}
		
		return false;
	}

	public void addWeaponPoints(int points) {
		List<WeaponType> list = JobFactory.getJobByName(currentJob).weaponTypes();
		while (points > 0) {
			extra.println("You have " + points + " weapon points left.");
			for (int i = 0;i < list.size();i++) {
				extra.println((i+1)+ " " + list.get(0).toString().toLowerCase());
			}
			int in = extra.inInt(list.size());
			WeaponType wt = list.get(in-1);
			PathWithLevel p = progression.getPathByName(wt.toString().toLowerCase(),this);
			int aLeft = 100-p.level;
			extra.println("Allocate how many?");
			int take = Math.min(points, aLeft);
			progression.addPathPoints(wt.toString().toLowerCase(),extra.inInt(take), this);
			points-=take;
			
		}
		
	}

	public Armor swapArmor(Armor item) {
		Armor hold = null;
		switch (item.getArmorType()) {
		case ASSEC:
			extra.println("slot one or slot two?");
			if (extra.inInt(2) == 1) {
				hold = this.assec1;
				this.assec1 = item;
			}else {
				hold = this.assec2;
				this.assec2 = item;
			}
			break;
		case FEET:
			hold = this.feet;
			this.feet = item;
			break;
		case HANDS:
			hold = this.arms;
			this.arms = item;
			break;
		case HEAD:
			hold = this.head;
			this.head = item;
			break;
		case PANTS:
			hold = this.pants;
			this.pants = item;
			break;
		case TORSO:
			hold = this.torso;
			this.torso = item;
			break;
		}
		return hold;
	}
	
	public Armor swapArmor(int i) {
		Armor hold = null;
		Armor item = null;
		switch (i) {
		case 7:
			hold = this.assec2;
			this.assec2 = item;
			break;
		case 6:
			hold = this.assec1;
			this.assec1 = item;
			break;
		case 5:
			hold = this.feet;
			this.feet = item;
			break;
		case 3:
			hold = this.arms;
			this.arms = item;
			break;
		case 1:
			hold = this.head;
			this.head = item;
			break;
		case 4:
			hold = this.pants;
			this.pants = item;
			break;
		case 2:
			hold = this.torso;
			this.torso = item;
			break;
		}
		return hold;
	}

	public void display() {
		for (Item i: this.getItems()) {
			i.display();
		}
		this.displayStats();
	}

	public void displayStats() {
		// TODO Auto-generated method stub
		
	}

	public List<Item> getItems() {
		List<Item> list = new ArrayList<Item>();
		list.addAll(this.listOfArmor());
		if (weap != null) {
			list.add(weap);
		}
		if (shield != null) {
			list.add(shield);
		}
		
		return list;
		
		
	}


}
