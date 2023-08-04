package rtrawel.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtrawel.unit.Buff;
import rtrawel.unit.DamageType;
import rtrawel.unit.RUnit;
import rtrawel.unit.RUnit.RaceType;

public class WeaponFactory {
	private static HashMap<String,Weapon> data = new HashMap<String, Weapon>();
	public static void init() {
		
		data.put("copper sword",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SWORD;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 6;
			}

			@Override
			public String getName() {
				return "copper sword";
			}

			@Override
			public String getDesc() {
				return "Not the best sword out there, but it'll have to do.";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 30;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("lumber axe",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.AXE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return defender.getRaceType(RaceType.PLANT) ? 5 : 0 ;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 7;
			}

			@Override
			public String getName() {
				return "lumber axe";
			}

			@Override
			public String getDesc() {
				return "A simple splitter that's steller against plants.";
			}

			@Override
			public double critChance() {
				return .025;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 30;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("simple stabber",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.KNIFE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 3;
			}

			@Override
			public String getName() {
				return "simple stabber";
			}

			@Override
			public String getDesc() {
				return "Not the best knife out there, but it'll have to do.";
			}

			@Override
			public double critChance() {
				return .15;
			}

			@Override
			public double critMult() {
				return 4;
			}

			@Override
			public int cost() {
				return 10;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		data.put("pot lid",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SHIELD;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 3;
			}

			@Override
			public String getName() {
				return "pot lid";
			}

			@Override
			public String getDesc() {
				return "The lid of a pot. 2.5% block chance.";
			}

			@Override
			public double critChance() {
				return 0;
			}

			@Override
			public double critMult() {
				return 1;
			}

			@Override
			public int cost() {
				return 10;
			}

			@Override
			public double blockChance() {
				return 0.025;
			}
		
			@Override
			public int getResilienceMod() {
				return 3;
			}
		});
		
		data.put("carpenter hammer",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.HAMMER;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return .6;
			}

			@Override
			public int getDamage() {
				return 9;
			}

			@Override
			public String getName() {
				return "carpenter hammer";
			}

			@Override
			public String getDesc() {
				return "A small hammer that's more than likely to miss.";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 2;
			}

			@Override
			public int cost() {
				return 20;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("pointy stick",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SPEAR;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 0.95;
			}

			@Override
			public int getDamage() {
				return 5;
			}

			@Override
			public String getName() {
				return "pointy stick";
			}

			@Override
			public String getDesc() {
				return "Barely a weapon.";
			}

			@Override
			public double critChance() {
				return .07;
			}

			@Override
			public double critMult() {
				return 2;
			}

			@Override
			public int cost() {
				return 5;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("fishing spear",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SPEAR;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return defender.getRaceType(RaceType.FISH) ? 5 : 0 ;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 0.95;
			}

			@Override
			public int getDamage() {
				return 6;
			}

			@Override
			public String getName() {
				return "fishing spear";
			}

			@Override
			public String getDesc() {
				return "Deals extra damage against fish.";
			}

			@Override
			public double critChance() {
				return .07;
			}

			@Override
			public double critMult() {
				return 2;
			}

			@Override
			public int cost() {
				return 30;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("small sling",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SLING;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 3;
			}

			@Override
			public String getName() {
				return "small sling";
			}

			@Override
			public String getDesc() {
				return "A small rock tosser.";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 6;
			}

			@Override
			public int cost() {
				return 5;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("basic bow",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.BOW;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 6;
			}

			@Override
			public String getName() {
				return "basic bow";
			}

			@Override
			public String getDesc() {
				return "A bow that's not too special.";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 2;
			}

			@Override
			public int cost() {
				return 20;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("copper broadsword",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SWORD;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 8;
			}

			@Override
			public String getName() {
				return "copper broadsword";
			}

			@Override
			public String getDesc() {
				return "A sword that's not only fine for fierce fighting, but for blocking as well!";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 80;
			}

			@Override
			public double blockChance() {
				return 0.01;
			}});
		
		data.put("studded leather shield",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SHIELD;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 5;
			}

			@Override
			public String getName() {
				return "studded leather shield";
			}

			@Override
			public String getDesc() {
				return "A metal and leather shield. 3.5% block chance.";
			}

			@Override
			public double critChance() {
				return 0;
			}

			@Override
			public double critMult() {
				return 1;
			}

			@Override
			public int cost() {
				return 100;
			}

			@Override
			public double blockChance() {
				return 0.035;
			}
		
			@Override
			public int getResilienceMod() {
				return 7;
			}
		});
		
		data.put("leather shield",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SHIELD;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 5;
			}

			@Override
			public String getName() {
				return "leather shield";
			}

			@Override
			public String getDesc() {
				return "A wood and leather shield. 3% block chance.";
			}

			@Override
			public double critChance() {
				return 0;
			}

			@Override
			public double critMult() {
				return 1;
			}

			@Override
			public int cost() {
				return 70;
			}

			@Override
			public double blockChance() {
				return 0.03;
			}
		
			@Override
			public int getResilienceMod() {
				return 5;
			}
		});
		
		data.put("iron sword",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SWORD;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 10;
			}

			@Override
			public String getName() {
				return "iron sword";
			}

			@Override
			public String getDesc() {
				return "A decent sword made of iron.";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 140;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		
		//TODO foes
		
		data.put("wolf pup teeth",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 3;
			}

			@Override
			public String getName() {
				return "wolf pup teeth";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("fella knife",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 5;
			}

			@Override
			public String getName() {
				return "fella knife";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return .1;
			}

			@Override
			public double critMult() {
				return 1.25;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("dendroid branch",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1.1;
			}

			@Override
			public int getDamage() {
				return 3;
			}

			@Override
			public String getName() {
				return "dendroid branch";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return 0;
			}

			@Override
			public double critMult() {
				return 1;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("well lurker teeth",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return .95;
			}

			@Override
			public int getDamage() {
				return 5;
			}

			@Override
			public String getName() {
				return "well lurker teeth";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.25;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}
		
			@Override
			public OnHit getOnHit() {
				return new OnHit() {

					@Override
					public void go(RUnit caster, RUnit u) {
						Buff b = new Buff();
						b.isDebuff = true;
						b.mag = .9;
						b.passive = false;
						b.timeLeft = 100;
						b.type = Buff.BuffType.RES_MULT;
						u.addBuff(b);
					}};
			}
	});
		
		data.put("giant squid teeth",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 10;
			}

			@Override
			public String getName() {
				return "giant squid teeth";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return 0.1;
			}

			@Override
			public double critMult() {
				return 1.10;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("wooden wand",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.WAND;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 4;
			}

			@Override
			public String getName() {
				return "wooden wand";
			}

			@Override
			public String getDesc() {
				return "A small wooden wand.";
			}

			@Override
			public double critChance() {
				return 0;
			}

			@Override
			public double critMult() {
				return 1;
			}

			@Override
			public int cost() {
				return 50;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("pole",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.STAFF;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1.2;
			}

			@Override
			public int getDamage() {
				return 5;
			}

			@Override
			public String getName() {
				return "pole";
			}

			@Override
			public String getDesc() {
				return "The start of many weapons.";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 10;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("mushroom masher",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.HAMMER;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return .8;
			}

			@Override
			public int getDamage() {
				return 10;
			}

			@Override
			public String getName() {
				return "mushroom masher";
			}

			@Override
			public String getDesc() {
				return "Two mushrooms... on a stick! What could go wrong?";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 2;
			}

			@Override
			public int cost() {
				return 40;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("armor sword",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 7;
			}

			@Override
			public String getName() {
				return "armor sword";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("iron golem punch",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return .8;
			}

			@Override
			public int getDamage() {
				return 10;
			}

			@Override
			public String getName() {
				return "iron golem punch";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.25;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("mm spear",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1.05;
			}

			@Override
			public int getDamage() {
				return 5;
			}

			@Override
			public String getName() {
				return "mm spear";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return .07;
			}

			@Override
			public double critMult() {
				return 2;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("door",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.BLUNT);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 12;
			}

			@Override
			public String getName() {
				return "door";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return 0;
			}

			@Override
			public double critMult() {
				return 1;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
	}
	
	
	public static Weapon getWeaponByName(String str) {
		if (!data.containsKey(str)) {
			throw new RuntimeException("key not found");
		}
		return data.get(str);
	}


	public static Weapon getWeaponByName(String str, boolean permitsNull) {
		return data.get(str);
	}
}
