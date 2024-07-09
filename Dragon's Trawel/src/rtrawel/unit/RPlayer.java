package rtrawel.unit;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import rtrawel.items.Armor;
import rtrawel.items.Armor.ArmorClass;
import rtrawel.items.Consumable;
import rtrawel.items.Item;
import rtrawel.items.Item.ItemType;
import rtrawel.items.Weapon;
import rtrawel.items.Weapon.WeaponType;
import rtrawel.items.WeaponFactory;
import rtrawel.jobs.Job;
import rtrawel.jobs.JobFactory;
import rtrawel.jobs.JobWithLevel;
import rtrawel.jobs.PathFactory;
import rtrawel.jobs.PathWithLevel;
import rtrawel.jobs.Progression;
import rtrawel.unit.Action.TargetType;
import trawel.core.Input;
import trawel.core.Print;

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
		progression.jobs.add(new JobWithLevel(job,1));
		//;
		//progression.jobs.add(new JobWithLevel("warrior",1));
		//weap = WeaponFactory.getWeaponByName("copper sword");
		//inventory.add(WeaponFactory.getWeaponByName("lumber axe"));
		//inventory.add(ConsumableFactory.getConsumableByName("medicine herb"));
		fStance = FightingStance.BALANCED;
		switch (job) {
		case "warrior": weap = WeaponFactory.getWeaponByName("copper sword");break;
		case "ranger": weap = WeaponFactory.getWeaponByName("small sling");break;
		case "cleric": weap = WeaponFactory.getWeaponByName("carpenter hammer");break;
		case "priest": weap = WeaponFactory.getWeaponByName("simple stabber");break;
		case "elementalist": weap = WeaponFactory.getWeaponByName("wooden wand");break;
		default: throw new RuntimeException("default weapon for class not found");
		}
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
		for (Item a: this.getItems()) {
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
		for (Item a: this.getItems()) {
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
		for (Item a: this.getItems()) {
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
		for (Item a: this.getItems()) {
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
		for (Item a: this.getItems()) {
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
		for (Item a: this.getItems()) {
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
		if (!curBattle.stillFight()) {
			return;
		}
		Party.party.displayQuick();
		boolean keepGoing = true;
		int in;
		boolean valid;
		while (keepGoing) {
			Print.println(this.getName() + " HP: " + this.getHp() + "/" + this.getMaxHp() + " MP: " + this.getMana() + "/" + this.getMaxMana() + " Tsn: " + this.getTension() + "/" + this.getMaxTension());
			Print.println("1 basic attack");
			Print.println("2 abilities and spells");
			Print.println("3 items");
			Print.println("4 change stance");
			Print.println("5 defend");
		switch (Input.inInt(5)) {
		case 1:
			for (RUnit r: curBattle.foes) {
				Print.println(r.getName());
			}
			valid = false;
			while (!valid) {
				in = Input.inInt(99);
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
				Print.println((i+1) + " " +abs.get(i).getName()  + ": " + abs.get(i).getDesc() + (abs.get(i).canCast(this) ? "" : " (locked)"));
			}
			Print.println((abs.size()+1 )+" back");
			in = Input.inInt(abs.size()+1);
			if (in == abs.size()+1) {
				continue;
			}
			keepGoing = chooseAbTarget(abs.get(in-1));
			break;
		case 3:
			for (int i = 0;i < inventory.size();i++) {
				Print.println((i+1) + " " +inventory.get(i).getName() + ": " + inventory.get(i).getDesc());//make sure only valid things can go into inventory later
			}
			Print.println((inventory.size()+1 )+" back");
			in = Input.inInt(inventory.size()+1);
			if (in == inventory.size()+1) {
				continue;
			}
			Item it = inventory.get(in-1);
			if (it.getItemType().equals(ItemType.CONSUMABLE)) {
				keepGoing = chooseAbTarget(((Consumable)it).getAction());
			}else {
				if (it.getItemType().equals(ItemType.WEAPON)) {
					if (JobFactory.getJobByName(currentJob).weaponTypes().contains(((Weapon)it).getWeaponType())){
						Print.println("You swap out your " + weap.getName() + " for your " + it.getName() + ".");
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
			Print.println("Current Stance: " + fStance.name().toLowerCase());
			Print.println("1 offensive");
			Print.println("2 balanced");
			Print.println("3 defensive");
			Print.println("4 back");
			switch (Input.inInt(4)) {
			case 1: this.fStance = FightingStance.OFFENSIVE; keepGoing = false;break;
			case 2: this.fStance = FightingStance.BALANCED; keepGoing = false;break;
			case 3: this.fStance = FightingStance.DEFENSIVE; keepGoing = false;break;
			}
			break;
		case 5:
			decideOn(ActionFactory.getActionByName("defend"),new TargetGroup(this));
			valid = true;
			keepGoing = false;
			break;
		}
		}

	}
	
	private boolean chooseAbTarget(Action ab) {
		int in;
		boolean valid;
		TargetGroup t;
		if (!ab.canCast(this)) {
			Print.println("You can't cast that right now.");
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
						Print.println((i+1) + " " + t.toString());
					}
					t = new TargetGroup();
					in = Input.inInt(curBattle.foeGroups.size());
					t.targets.addAll(curBattle.foeGroups.get(in-1));
					decideOn(ab,t);
					return false;
				}else {
					for (RUnit r: curBattle.foes) {
						Print.println(r.getName());
					}
					valid = false;
					while (!valid) {
						in = Input.inInt(99);
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
					Print.println((1+i) + " "+curBattle.party.get(i).getName());
				}
				in = Input.inInt(curBattle.party.size());
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
			PathFactory.getPathByName(pwl.path).apply(this, pwl.level,PathFactory.getPathByName(pwl.path).jobName().equals(currentJob));//TODO make active weapons active
		}
	}

	public void addAbility(Action action) {
		abs.add(action);
	}
	
	@Override
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
			Print.println((i + 1) + " " + inventory.get(i).getName());
		}
		i++;//extra i++
		if (inventory.size() < 3) {
			Print.println((i++) + " empty slot");
		}
		Print.println((i) + " back");
		int in = Input.inInt(i);
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
			Print.println("You have " + points + " weapon points left.");
			for (int i = 0;i < list.size();i++) {
				Print.println((i+1)+ " " + list.get(i).toString().toLowerCase());
			}
			int in = Input.inInt(list.size());
			WeaponType wt = list.get(in-1);
			PathWithLevel p = progression.getPathByName(wt.toString().toLowerCase(),this);
			int aLeft = 100-p.level;
			Print.println("Allocate how many? (Weapons can go over 100.)");
			int take = points;
			take = Input.inInt(take);
			progression.addPathPoints(wt.toString().toLowerCase(),take, this);
			points-=take;
			
		}
		
	}

	public Armor swapArmor(Armor item) {
		Armor hold = null;
		switch (item.getArmorType()) {
		case ASSEC:
			Print.println("slot one or slot two?");
			if (Input.inInt(2) == 1) {
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
		this.displayStats();
		for (Item i: this.getItems()) {
			Print.println(i.display());
		}
		
	}

	public void displayStats() {
		// TODO Auto-generated method stub
		Print.println(this.getName() + ": level "+this.progression.jobLevel(currentJob) + " " + currentJob);
		Print.println("HP: " + this.getHp() + "/" + this.getMaxHp() + " MP: " + this.getMana() + "/" + this.getMaxMana() + " Tsn: " + this.getTension() + "/" + this.getMaxTension());
		Print.println("Str: " + this.getStrength() +" Agi: " +this.getAgility() + " Kno: " +this.getKnowledge() + " Res: " + this.getResilence() + " Dex: " + this.getDexterity() + " Spd: " + this.getSpeed());
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

	public double lootChance() {
		return buffMap.getTotalBuffMult(Buff.BuffType.LOOT_CHANCE);
	}

	public List<Action> getOOCAbs() {
		List<Action> list = new ArrayList<Action>();
		abs.stream().filter(a -> (a.getTargetType().equals(TargetType.HURT_FRIEND) || a.getTargetType().equals(TargetType.OOC))).forEach(list::add);
		return list;
	}

	public TargetGroup decideOOCTargets(Action ab) {
		if (ab.getTargetType().equals(Action.TargetType.SELF_ONLY)) {
			return new TargetGroup(this);
		}
		if (ab.getTargetType().equals(Action.TargetType.FOE)) {
			
		}else {
			if (ab.getTargetGrouping().equals(Action.TargetGrouping.SINGLE)) {
				Print.println("On who?");
				return new TargetGroup(Party.party.getUnit());
			}else {
				TargetGroup t = new TargetGroup();
				t.targets.addAll(Party.party.list);
				return t;
			}
		}
		return null;
	}

	public void deEquipUnfitting() {
		Job j = JobFactory.getJobByName(currentJob);
		List<ArmorClass> jcs = new ArrayList<ArmorClass>();
		if (head != null && !jcs.contains(head.getArmorClass())) {
			Party.party.addItem(head.getName(),1);
			head = null;
		}
		if (torso != null && !jcs.contains(torso.getArmorClass())) {
			Party.party.addItem(torso.getName(),1);
			torso = null;
		}
		if (arms != null && !jcs.contains(arms.getArmorClass())) {
			Party.party.addItem(arms.getName(),1);
			arms = null;
		}
		if (pants != null && !jcs.contains(pants.getArmorClass())) {
			Party.party.addItem(pants.getName(),1);
			pants = null;
		}
		if (feet != null && !jcs.contains(feet.getArmorClass())) {
			Party.party.addItem(feet.getName(),1);
			feet = null;
		}
		if (assec1 != null && !jcs.contains(assec1.getArmorClass())) {
			Party.party.addItem(assec1.getName(),1);
			assec1 = null;
		}
		if (assec2 != null && !jcs.contains(assec2.getArmorClass())) {
			Party.party.addItem(assec2.getName(),1);
			assec2 = null;
		}
		
		if (shield != null && !j.weaponTypes().contains(WeaponType.SHIELD)) {
			Party.party.addItem(shield.getName(),1);
			shield = null;
		}
	}


}
