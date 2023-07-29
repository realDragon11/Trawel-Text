package trawel.battle;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import trawel.AIClass;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.TargetFactory;
import trawel.battle.attacks.Attack.Wound;
import trawel.factions.FBox;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race;
import trawel.personal.item.body.SoundBox;
import trawel.personal.item.body.Race.RaceType;
import trawel.personal.item.magic.EnchantHit;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponQual;
import trawel.personal.item.solid.Armor.ArmorQuality;
import trawel.personal.people.Player;
import trawel.personal.people.Skill;
import trawel.towns.World;
import trawel.towns.fort.FortHall;
import trawel.towns.fort.LSkill;
import trawel.towns.fort.SubSkill;
/**
 * A combat holds some of the more battle-focused commands.
 * @author Brian Malone
 * 2/8/2018
 */

public class Combat {
	//instance variables
	private Person attacker;
	private Person defender;
	public List<Person> survivors;
	public List<Person> killed;
	private boolean newTarget = false;
	public long turns = 0;
	public int totalFighters = 2;
	public boolean battleIsLong = false;
	
	public static Combat testCombat = new Combat();
	
	public static int longBattleLength = 50;
	//constructor
	/**
	 * Holds a fight to the death, between two people.
	 * @param manOne (Person)
	 * @param manTwo (Person)
	 */
	public Combat(Person manOne, Person manTwo,World w) {
		if (Player.getTutorial()) {
			extra.println("Welcome to a battle! You can turn the tutorial off in the 'you' menu.");
			extra.println("Choose your attack below: higher is better, except in the case of delay!");
			extra.println("Delay is how long an action takes- it determines turn order and skipping.");
			extra.println("For example, two 30 delay actions would go through before one 100 delay action.");
			extra.println("sbp stands for sharp blunt pierce- the three damage types.");
			extra.println("Hp is restored at the start of every battle.");
		}
		//Setup
		manOne.battleSetup();
		//extra.println("");
		manTwo.battleSetup();
		
		attacker = manOne;
		defender = manTwo;
		
		boolean playerIsInBattle;//= attacker.isPlayer() || defender.isPlayer();
		if (manOne.isPlayer()) {
			manTwo.displayStatsShort();
			playerIsInBattle = true;
		}else {
			if (manTwo.isPlayer()) {
				manOne.displayStatsShort();
				playerIsInBattle = true;
			}else {
				playerIsInBattle = false;
			}
		}
		
		attacker = manTwo;
		defender = manOne;
		setAttack(manTwo,manOne);
		setAttack(manOne,manTwo);
		extra.println("");
		extra.println(extra.choose("Our two fighters square off...","They look tense.","It's time to fight.","They look ready to fight.","The battle has begun."));
		//BardSong song = w.startBardSong(manOne,manTwo);
		do {//combat loop
			if (manOne.getTime() < manTwo.getTime()) {
				attacker = manOne;
				defender = manTwo;
			}else {
				attacker = manTwo;
				defender = manOne;
			}
			if (attacker.hasSkill(Skill.BLITZ)) {
				attacker.advanceTime(3);
			}
			if (attacker.hasSkill(Skill.SKY_BLESSING_1)) {
				attacker.advanceTime(1);
			}
			double delay = attacker.getTime();
			defender.advanceTime(delay);
			attacker.advanceTime(delay);
			handleTurn( attacker, defender, playerIsInBattle, delay);
			if (playerIsInBattle) {
				manTwo.getBag().graphicalDisplay(1,manTwo);
				Player.player.getPerson().getBag().graphicalDisplay(-1,Player.player.getPerson());
				}
			
			if (manOne.isAlive() && manTwo.isAlive()) {
			setAttack(attacker,defender);}
		}
		while(manOne.isAlive() && manTwo.isAlive());
		
		if (manOne.isAlive()) {
			if (manTwo.isAlive()) {
				//no
			}else {
				attacker = manOne;
				defender = manTwo;
				//attacker = manOne;
				//defender = manTwo;
				
			}
		}else {
			if (manTwo.isAlive()) {
				attacker = manTwo;
				defender = manOne;
				manOne = attacker;
				manTwo = defender;
			}else {
				//both dead, manOne wins be default
				attacker = manOne;
				defender = manTwo;
			}
		}
		
		extra.println(extra.choose("The dust settles...","The body drops to the floor.","Death has come.","The battle is over."));
		extra.println(manTwo.getName() + extra.choose(" lies dead..."," walks the earth no more..."," has been slain."));
		//song.addKill(manOne, manTwo);
		manOne.getBag().getHand().addKill();
		manOne.addKillStuff();
		FBox.repCalc(manOne,manTwo);
	}
	
	public class SkillCon {
		public LSkill lSkill;
		public float timer, timeTo;
		public SkillCon(LSkill l, int timer, int reset) {
			lSkill = l;
			this.timer = timer;
			timeTo = reset;
		}
	}
	
	public void handleSkillCons(List<SkillCon> cons, List<Person> totalList,double timePassed) {
		for (SkillCon sk: cons) {
			sk.timer-=timePassed;
			if (sk.timer <=0) {
				switch (sk.lSkill.skill) {
				case DEATH:
					for (Person p: totalList) {
						if (!p.hasSkill(Skill.PLAYERSIDE)) {
							p.getNextAttack().wither(Math.min(20,sk.lSkill.value)/100.0);
							p.takeDamage(1);
						}
					}
					break;
				case ELEMENTAL:
					for (Person p: totalList) {
						if (!p.hasSkill(Skill.PLAYERSIDE)) {
							p.takeDamage(Math.min(20,sk.lSkill.value));
							p.getBag().burn(Math.min(20,sk.lSkill.value/2)/100.0, extra.randRange(0, 4));
						}
					}
					break;
				case SCRYING:
					for (Person p: totalList) {
						if (p.hasSkill(Skill.PLAYERSIDE)) {
							p.advanceTime(sk.lSkill.value/10.0f);
						}
					}
					break;
				case DEFENSE:
					for (Person p: totalList) {
						if (!p.hasSkill(Skill.PLAYERSIDE)) {
							p.advanceTime(-sk.lSkill.value);
						}
					}
					break; 
				}
			}
			sk.timer = Math.max(1,sk.timeTo-extra.randRange(0,Math.min(10,sk.lSkill.value)));//intentionally not perfect times
		}
	}
	
	public Combat(World w,List<Person>... people) {
		this(w,null,Arrays.asList(people));
	}
	public Combat(World w, FortHall hall,List<Person>... people) {
		this(w,hall, Arrays.asList(people));
	}
	public Combat(World w,List<List<Person>> people) {
		this(w,null,people);
	}
	public Combat(World w, FortHall hall,List<List<Person>> people) {
		int size = people.size();
		List<Person> totalList = new ArrayList<Person>();
		List<Person> killList = new ArrayList<Person>();
		List<List<Person>> liveLists = new ArrayList<List<Person>>();
		for (int i = 0; i < people.size();i++) {
			List<Person> newList = new ArrayList<Person>();
			newList.addAll(people.get(i));
			liveLists.add(newList);
		}
		
		boolean playerIsInBattle = false;
		List<SkillCon> cons = new ArrayList<SkillCon>();
		if (hall != null) {
			int temp = hall.getSkillCount(SubSkill.DEATH);
			if (temp > 0) {
				cons.add(new SkillCon(new LSkill(SubSkill.DEATH,temp),50,50));
			}
			temp = hall.getSkillCount(SubSkill.ELEMENTAL);
			if (temp > 0) {
				cons.add(new SkillCon(new LSkill(SubSkill.DEATH,temp),100,100));
			}
			temp = hall.getTotalDefenceRating();
			if (temp > 0) {
				cons.add(new SkillCon(new LSkill(SubSkill.DEFENSE,temp),1,9999999));
			}
			temp = hall.getSkillCount(SubSkill.SCRYING);
			if (temp > 0) {
				cons.add(new SkillCon(new LSkill(SubSkill.SCRYING,temp),100,500));
			}
		}
		for (List<Person> peoples: liveLists) {
			for (Person p: peoples) {
				if (p.isPlayer()) {
					Networking.setBattle(Networking.BattleType.NORMAL);
					playerIsInBattle = true;
				}
				p.battleSetup();
				totalList.add(p);
			Person otherperson = null;
			while (otherperson == null) {
				int rand = extra.randRange(0,size-1);
				List<Person> otherpeople = liveLists.get(rand);
				if (otherpeople.contains(p) || otherpeople.size() == 0) {
					continue;
				}
				otherperson = extra.randList(otherpeople);
			}
			if (p.isPlayer()) {
				otherperson.displayStatsShort();
				p.getBag().graphicalDisplay(-1,p);
				otherperson.getBag().graphicalDisplay(1,otherperson);
			}
			setAttack(p,otherperson);
			p.getNextAttack().defender = otherperson;
			}
		}
		totalFighters = totalList.size();
		Person quickest= null;//moved out so we can have a default last actor to default as alive
		while(true) {
			
			double lowestDelay = Double.MAX_VALUE;
			for (Person p: totalList) {
				if (lowestDelay > p.getTime()) {
					lowestDelay = p.getTime();
					quickest = p;
				}
			}
			if (quickest.hasSkill(Skill.BLITZ)) {
				lowestDelay-=3;
			}
			
			
			for (Person p: totalList) {
				p.advanceTime(lowestDelay);
			}
			
			this.handleSkillCons(cons, totalList, lowestDelay);
			
			Person defender = quickest.getNextAttack().defender;
			boolean wasAlive = defender.isAlive();
			newTarget = false;
			AttackReturn atr = handleTurn(quickest,defender,playerIsInBattle,lowestDelay);
			if (!defender.isAlive() && (wasAlive || totalList.contains(defender))) {
				extra.println("They die!");
				quickest.getBag().getHand().addKill();
				if (quickest.hasSkill(Skill.KILLHEAL)){
					quickest.addHp(5*quickest.getLevel());
				}
				quickest.addKillStuff();
				totalList.remove(defender);
				killList.add(defender);
				for (List<Person> list: liveLists) {
					if (list.contains(defender)) {
						list.remove(defender);
						break;
					}
				}
			}else {
				if (newTarget) {
					//the defender has been befuddled or confused
					Person otherperson = null;
					while (otherperson == null) {
						int rand = extra.randRange(0,size-1);
						List<Person> otherpeople = liveLists.get(rand);
						if ((otherpeople.contains(defender) && extra.chanceIn(3,quickest.getMageLevel()+3)) || otherpeople.size() == 0) {
							continue;
						}
						otherperson = extra.randList(otherpeople);
						if (otherperson == defender) {
							continue;
						}
						defender.getNextAttack().defender = otherperson;
					}
				}
			}
			
			//if the attacker is dead, through bleeding or otherwise, they are force removed- they would not have acted if they were already removed
			if (!quickest.isAlive()) {
				extra.println(quickest.getName() + " falls to the floor!");
				//quickest.addKillStuff();
				totalList.remove(quickest);
				killList.add(quickest);
				for (List<Person> list: liveLists) {
					if (list.contains(quickest)) {
						list.remove(quickest);
						break;
					}
				}
			}
			
			int sides = 0;
			for (List<Person> list: liveLists) {
				if (list.size() > 0) {
					sides++;
				}
			}
			
			if (sides == 1) {
				break;//end battle
			}
		
			
			Person otherperson = null;
			if (defender.isAlive() && quickest.getBag().getHand().qualList.contains(Weapon.WeaponQual.DUELING)) {
				otherperson = defender;
			}
			while (otherperson == null) {
				if (quickest.isPlayer() && Player.player.eaBox.markTarget != null) {
					if (totalList.contains(Player.player.eaBox.markTarget)) {
						otherperson = Player.player.eaBox.markTarget;
						break;
					}else {
						Player.player.eaBox.markTarget = null;
					}
				}
				int rand = extra.randRange(0,size-1);
				List<Person> otherpeople = liveLists.get(rand);
				if (otherpeople.contains(quickest) || otherpeople.size() == 0) {
					continue;
				}
				otherperson = extra.randList(otherpeople);
			}
			if (quickest.isPlayer()) {
				otherperson.displayStatsShort();
				quickest.getBag().graphicalDisplay(-1,quickest);
				otherperson.getBag().graphicalDisplay(1,otherperson);
			}else {
				if (playerIsInBattle) {
					quickest.getBag().graphicalDisplay(1,quickest);
					Player.player.getPerson().getBag().graphicalDisplay(-1,Player.player.getPerson());
				}
			}
			setAttack(quickest,otherperson);
			if (otherperson != defender && atr != null && (atr.code == ATK_ResultCode.MISS || atr.code == ATK_ResultCode.DODGE)
					&& quickest.getBag().getHand().qualList.contains(WeaponQual.CARRYTHROUGH)) 
			{
				quickest.advanceTime(Math.min(10,quickest.getTime()-1));
			}
			quickest.getNextAttack().defender = otherperson;
			if (quickest.hasSkill(Skill.LIFE_MAGE)) {
				for (List<Person> list: liveLists) {
					if (list.contains(quickest)) {
						for (Person p: list) {
							if (p.getHp() < p.getMaxHp()) {p.addHp(1);}
						}
					}
				}
			}
			
		}
		
		if (totalList.size() == 0) {//no survivors, last actor wins
			totalList.add(quickest);
			killList.remove(quickest);
		}
		
		survivors = totalList;
		killed = killList;
		
		
	}
	
	public Combat() {
		//empty for tests
	}
	
	private static final double depthArmor2 = .25;
	private static final double midArmor2 = .7;
	private static final double armorMinShear = .1;
	
	private static int attackSub(double dam,double armor) {
		return (int)(
				(dam)
					-(
						(armorMinShear*dam)
						+((1-armorMinShear)*dam*extra.upDamCurve(depthArmor2,midArmor2))
					)
				);
	}

	//instance methods
	/**
	 * Calculate if an attack hits, and how much damage it would deal, countered by the armor.
	 * @param att The attack being used (Attack)
	 * @param def the inventory of the defender (Inventory)
	 * @param off the inventory of the attacker (Inventory)
	 * @param armMod the armormod (double)
	 * @return
	 */
	public AttackReturn handleAttack(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		String str = "";
		if (att.getName().contains("examine")){
			if  (!attacker.isPlayer()) {
				str +=("They examine you...");
				return new AttackReturn(ATK_ResultCode.NOT_ATTACK,str,null);
			}
			attacker.displayStats();
			attacker.displayArmor();
			attacker.displayHp();
			defender.displayStats();
			defender.displayArmor();
			defender.displayHp();
			if (attacker.hasSkill(Skill.HPSENSE)) {
			defender.displaySkills();}
			str +=(att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));
			return new AttackReturn(ATK_ResultCode.NOT_ATTACK,str,null);
		}
		if (extra.chanceIn(1, 5)) {
			Networking.send("PlayDelayPitch|"+SoundBox.getSound(off.getRace().voice,SoundBox.Type.SWING) + "|1|" +attacker.getPitch() +"|");
		}
		if (defender.isAlive()) {
			str +=(att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));
		}else {
			str +=(att.attackStringer(attacker.getName(),defender.getName() + "'s corpse",off.getHand().getName()));	
		}
		double damMod = off.getDam();
		if (((def.getDodge()*defender.getTornCalc())/(att.getHitmod()*off.getAim()))* extra.getRand().nextDouble() > 1.0){
			return new AttackReturn(ATK_ResultCode.DODGE,str,null);
		}
		/*
		if (extra.chanceIn(def.countArmorQuality(ArmorQuality.HAUNTED),80)){
			return new AttackReturn(-1,str +extra.PRE_GREEN+" An occult hand leaps forth, pulling them out of the way![c_white]",null);//DOLATER probably just remove
		}*/
		//return the damage-armor, with each type evaluated individually
		Networking.send("PlayHit|" +def.getSoundType(att.getSlot()) + "|"+att.getSoundIntensity() + "|" +att.getSoundType()+"|");
		
		//double depthWeapon2 = .25;
		//double midWeapon2 = .7;
		
		//armMod = armMod*(1-(armorMinShear/2));
		List<Weapon.WeaponQual> wqL = (att.getWeapon() != null ? att.getWeapon().qualList : new ArrayList<Weapon.WeaponQual>());
		double sharpA = def.getSharp(att.getSlot(),wqL)*armMod;
		double bluntA = def.getBlunt(att.getSlot(),wqL)*armMod;
		double pierceA= def.getPierce(att.getSlot(),wqL)*armMod;
		AttackReturn ret = new AttackReturn(
				attackSub(att.getSharp()*damMod,sharpA),
				attackSub(att.getBlunt()*damMod,bluntA),
				attackSub(att.getPierce()*damMod,pierceA),
				str,att);
		if (att.getWeapon() != null && att.getWeapon().qualList.contains(Weapon.WeaponQual.RELIABLE)
				&& ret.damage <= att.getWeapon().getLevel()) 
		{
			ret.damage = att.getWeapon().getLevel();
			ret.reliable = true;
		} else {
			if (ret.code == ATK_ResultCode.DAMAGE) {//no longer functions on reliable
				if (att.getWeapon() != null && att.getWeapon().qualList.contains(Weapon.WeaponQual.REFINED)) {
					ret.damage += att.getWeapon().getLevel();
				}
				if (att.getWeapon() != null && att.getWeapon().qualList.contains(Weapon.WeaponQual.WEIGHTED)) {
					if (att.getHitmod() < 1.5) {
						ret.damage = (int) Math.round(ret.damage*Math.log10(5+(20-(att.getHitmod()*10))));
					}
				}
			}
		}
		return ret;
	}
	
	public static AttackReturn handleTestAttack(Attack att, Inventory def, double armMod) {
		double damMod = 1;
		if (((def.getDodge())/(att.getHitmod()))*ThreadLocalRandom.current().nextDouble() > 1.0){
			return Combat.testCombat.new AttackReturn(ATK_ResultCode.DODGE,"",null);//do a dodge
		}
		//return the damage-armor, with each type evaluated individually
		//double depthWeapon2 = .25;
		//double midWeapon2 = .7;
		/*double depthArmor2 = .25;
		double midArmor2 = .7;
		double armorMinShear = .1;*/
		//armMod = armMod*(1-(armorMinShear/2));
		List<Weapon.WeaponQual> wqL = (att.getWeapon() != null ? att.getWeapon().qualList : new ArrayList<Weapon.WeaponQual>());
		double sharpA = def.getSharp(att.getSlot(),wqL)*armMod;
		double bluntA = def.getBlunt(att.getSlot(),wqL)*armMod;
		double pierceA= def.getPierce(att.getSlot(),wqL)*armMod;
		AttackReturn ret = Combat.testCombat.new AttackReturn(
				attackSub(att.getSharp()*damMod,sharpA),
				attackSub(att.getBlunt()*damMod,bluntA),
				attackSub(att.getPierce()*damMod,pierceA),
				"",att);
		if (att.getWeapon() != null && att.getWeapon().qualList.contains(Weapon.WeaponQual.RELIABLE) && ret.damage <= att.getWeapon().getLevel()) {
			ret.damage = att.getWeapon().getLevel();
			ret.reliable = true;
		}else {
			if (ret.damage > 0) {
				if (att.getWeapon() != null && att.getWeapon().qualList.contains(Weapon.WeaponQual.REFINED)) {
					ret.damage += att.getWeapon().getLevel();
				}
				if (att.getWeapon() != null && att.getWeapon().qualList.contains(Weapon.WeaponQual.WEIGHTED)) {
					if (att.getHitmod() < 1.5) {
						ret.damage = (int) Math.round(ret.damage*Math.log10(5+(20-(att.getHitmod()*10))));
					}
				}
			}
		}
		return ret;	
		
	}
	
	public class AttackReturn {
		public int damage;
		public int[] subDamage;
		public String stringer;
		public boolean reliable = false;
		public Attack attack;
		public ATK_ResultCode code;
		public AttackReturn(int sdam,int bdam, int pdam, String str, Attack att) {
			code = ATK_ResultCode.DAMAGE;
			damage = sdam+bdam+pdam;
			subDamage = new int[] {sdam,bdam,pdam};
			stringer = str;
			attack = att;
		}
		
		public AttackReturn(ATK_ResultCode rcode, String str, Attack att) {
			code = rcode;
			damage = -1;
			//null
			stringer = str;
			attack = att;
		}
	}
	
	public enum ATK_ResultCode{
		DAMAGE, NOT_ATTACK, MISS, DODGE, ARMOR
	}
	
	/**
	 * does stuff that can't go in handleAttack because it's permanent and handleAttack is used in ai stuff.
	 * handles elemental damage
	 * should scale of off % of hp damaged or something to avoid confusing the ai
	 */
	public void handleAttackPart2(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender, int damageDone) {
		double percent = ((double)extra.zeroOut(damageDone))/((double)defender.getMaxHp());
		if (off.getHand().isEnchantedHit() && !(att.getName().contains("examine"))) {
			EnchantHit ehit = (EnchantHit)off.getHand().getEnchant();
			def.burn(def.getFire(att.getSlot())*percent*ehit.getFireMod()/2,att.getSlot());
			
			defender.advanceTime(-(percent*defender.getTime()*ehit.getFreezeMod()*def.getFreeze(att.getSlot())));
			
			defender.takeDamage((int)(percent*ehit.getShockMod()/3*def.getShock(att.getSlot())));
		}
		if (attacker.hasSkill(Skill.DSTRIKE) && percent >= .80) {
			defender.takeDamage((int) (((1-percent)*defender.getMaxHp())+10));
		}
	}
	
	/**
	 * 
	 * @param attacker
	 * @param defender
	 * @return damagedone
	 */
	public AttackReturn handleTurn(Person attacker, Person defender,boolean canWait, double delay) {
		turns++;
		AttackReturn ret = null;
		if (turns > longBattleLength*totalFighters) {
			//VERY LONG BATTLE
			attacker.takeDamage((int)(turns-(longBattleLength*totalFighters)));
			defender.takeDamage((int)(turns-(longBattleLength*totalFighters)));
			if (!battleIsLong) {
				System.out.println("Resolving long battle...");
				battleIsLong = true;
			}
		}
		if (attacker.hasEffect(Effect.RECOVERING)) {
			attacker.removeEffect(Effect.RECOVERING);
			attacker.addHp(attacker.getLevel()*5);
		}
		if (defender.hasSkill(Skill.COUNTER)) {
			defender.advanceTime(2);
		}
		if (attacker.hasSkill(Skill.SPUNCH)) {
			defender.advanceTime(-2);
		}
		if (attacker.hasSkill(Skill.CURSE_MAGE)) {
			defender.addEffect(Effect.CURSE);
		}
		if (defender.hasEffect(Effect.FORGED)) {
			defender.getBag().restoreArmor(0.1);
		}
		if (attacker.getBag().getRace().racialType != Race.RaceType.BEAST && extra.chanceIn(1,4)) {
			if (extra.chanceIn(1,3)) {
					BarkManager.getBoast(attacker,true);//extra.println(attacker.getName() + " "+extra.choose("shouts","screams","boasts")+ " \"" + attacker.getTaunts().getBoast()+"\"");		
			}else {
				if (((attacker.isAngry() && defender.getBag().getRace().racialType == RaceType.BEAST) || (attacker.isRacist() && !attacker.getBag().getRace().equals(defender.getBag().getRace()))) && extra.chanceIn(1,3)) 
				{
					extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" +defender.getBag().getRace().randomInsult()+"\"");
				}else {
					BarkManager.getTaunt(attacker);//extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" + attacker.getTaunts().getTaunt()+"\"");
				}				
			}
		}
		
		if (!attacker.getNextAttack().isMagic()) {
		AttackReturn atr = this.handleAttack(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender);
		ret = atr;
		int damageDone = atr.damage;
		String inlined_color = extra.PRE_WHITE;
		this.handleAttackPart2(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender,damageDone);
		//armor quality handling
		//FIXME: apply new attack result code system where needed
		defender.getBag().armorQualDam(damageDone);
		//message handling
		if (damageDone > 0) {
			float percent = damageDone/(float)defender.getMaxHp();
			if (extra.chanceIn((int)(percent*100) + (defender.getHp() <= 0 ? 10 : 0), 120)) {
				Networking.send("PlayDelayPitch|"+SoundBox.getSound(defender.getBag().getRace().voice,SoundBox.Type.GRUNT) + "|4|"+ defender.getPitch()+"|");
			}
			//blood
			
			if (defender.getBag().getRace().emitsBlood == true && ( defender.targetOverride != TargetFactory.TargetType.STATUE && defender.targetOverride != TargetFactory.TargetType.UNDEAD_H )) {
				
				attacker.getBag().getHand().addBlood(percent*5);
				defender.getBag().getArmorSlot(attacker.getNextAttack().getSlot()).addBlood(percent*2f);
				defender.addBlood(percent*1f);
				if (damageDone > .025f*defender.getMaxHp()) {
					for (Armor a: defender.getBag().getArmor()) {
						a.addBlood(percent*.5f);
					}
				}
				/*if (damageDone > .05*defender.getMaxHp()) {
				
				
				attacker.getBag().getHand().addBlood(.5f);
				defender.getBag().getArmorSlot(attacker.getNextAttack().getSlot()).addBlood(1);
				if (damageDone > .2*defender.getMaxHp()) {
					attacker.getBag().getHand().addBlood(.5f);
					defender.getBag().getArmorSlot(attacker.getNextAttack().getSlot()).addBlood(.5f);
					defender.addBlood(1);
				}
				if (damageDone > .4*defender.getMaxHp()) {
					attacker.getBag().getHand().addBlood(.5f);
					defender.getBag().getArmorSlot(attacker.getNextAttack().getSlot()).addBlood(1);
					defender.addBlood(1);
				}*/
			}
			//Wound effects
			String woundstr = inflictWound(attacker,defender,atr);
			//song.addAttackHit(attacker,defender);
			if (defender.hasEffect(Effect.R_AIM)) {
				defender.getNextAttack().blind(1 + (percent));
			}
			if (!extra.getPrint()) {
				//extra.print(extra.inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f)));
				if (defender.isPlayer()) {
					int splashes =(damageDone*100)/defender.getMaxHp();
					if (splashes > 0) {
						Networking.sendStrong("Bloodstain|" + splashes + "|");
					}
				}
			}
			
			if (defender.takeDamage(damageDone)) {
				//extra.print(" " + extra.choose("Striking them down!"," They are struck down."));
				if (!extra.getPrint()) {
					inlined_color=extra.ATTACK_KILL;
					extra.print(inlined_color +atr.stringer.replace("[*]", inlined_color)+woundstr);
				}
			}else {
				if (!extra.getPrint()) {
					inlined_color=extra.ATTACK_DAMAGED;
					extra.print(inlined_color +atr.stringer.replace("[*]", inlined_color)+woundstr);
				}
			}
		}else {
			if (atr.code == ATK_ResultCode.DODGE || atr.code == ATK_ResultCode.MISS) {
				//song.addAttackMiss(attacker,defender);
				if (!extra.getPrint()) {
					inlined_color= extra.PRE_YELLOW;
					extra.print(inlined_color +atr.stringer.replace("[*]", inlined_color));
					Networking.sendStrong("PlayMiss|" + "todo" + "|");
					extra.print(" "+extra.AFTER_ATTACK_MISS+randomLists.attackMissFluff());
				}
				if (defender.hasSkill(Skill.SPEEDDODGE)) {
					defender.advanceTime(10);
					if (defender.hasSkill(Skill.DODGEREF)) {
						defender.addHp(attacker.getLevel());
					}
				}
				if (defender.hasEffect(Effect.BEE_SHROUD)) {
					extra.println(extra.inlineColor(extra.colorMix(Color.PINK,Color.WHITE,.2f))+"The bees sting back!");
					attacker.takeDamage(1);
				}
			}else {
				if (atr.code == ATK_ResultCode.ARMOR) {//TODO: now reliable causes this, but we catch it earlier with the damage >0
					//song.addAttackArmor(attacker,defender);
					if (!extra.getPrint()) {
						inlined_color = extra.ATTACK_BLOCKED;
						extra.print(inlined_color+atr.stringer.replace("[*]", inlined_color)+" "+randomLists.attackNegateFluff());
					}
					if (defender.hasSkill(Skill.ARMORHEART)) {
						defender.addHp(attacker.getLevel());
					}
					if (defender.hasSkill(Skill.ARMORSPEED)) {
						defender.advanceTime(10);
					}
				}else {

				}
			}
		}
		}else {
			//the attack is a magic spell
			handleMagicSpell(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender);
			//song.addAttackHit(attacker,defender);
		}

		if (canWait && mainGame.delayWaits) {
			Networking.waitIfConnected(100L+(long)delay*2);
		}

		extra.println("");

		Person p = defender;
		float hpRatio = ((float)p.getHp())/(p.getMaxHp());
		//extra.println(p.getHp() + p.getMaxHp() +" " + hpRatio);
		int tval = extra.clamp((int)(extra.lerp(125,256,hpRatio)),100,255);
		extra.print(extra.inlineColor(new Color(tval,tval,tval)));
		if (hpRatio >= 1) {
			extra.println(p.getName() + " is untouched.");
		}else {
			if (hpRatio > .9) {
				extra.println(p.getName() + " looks barely scratched.");
			}else {
				if (hpRatio > .7) {
					extra.println(p.getName() + " looks a little hurt.");
				}else {
					if (hpRatio > .5) {
						extra.println(p.getName() + " looks a bit damaged.");
					}else {
						if (hpRatio > .25) {
							extra.println(p.getName() + " looks moderately damaged.");
						}else {
							if (hpRatio > .1) {
								extra.println(p.getName() + " looks close to death.");
							}else {
								extra.println(p.getName() + " looks like they're dying.");
							}
						}
					}
				}
			}
		}
		//}
		if (attacker.hasSkill(Skill.BLOODTHIRSTY)) {
			attacker.addHp(attacker.getLevel());
		}
		if (attacker.hasSkill(Skill.HPSENSE) || defender.hasSkill(Skill.HPSENSE)) {
			extra.println(defender.getHp()+"/" + defender.getMaxHp() );
		}
		if (defender.hasSkill(Skill.MIMIC_CHEST)) {
			if (extra.chanceIn(1,2)){
				RaceID rid = defender.getBag().getRaceID();
				if (hpRatio > .6f) {//if healthy, prefer closed, if damaged, prefer open and swap more in general
					if (rid == RaceFactory.RaceID.B_MIMIC_CLOSED) {
						if (extra.randFloat() < .2) {
							defender.getBag().setRace(RaceID.B_MIMIC_OPEN);
						}
					}else {
						if (extra.randFloat() < .6) {
							defender.getBag().setRace(RaceID.B_MIMIC_CLOSED);
						}
					}
				}else {
					if (rid == RaceFactory.RaceID.B_MIMIC_CLOSED) {
						if (extra.randFloat() < .9) {
							defender.getBag().setRace(RaceID.B_MIMIC_OPEN);
						}
					}else {
						if (extra.randFloat() < .5) {
							defender.getBag().setRace(RaceID.B_MIMIC_CLOSED);
						}
					}
				}
				Networking.clearSide(1);
				defender.getBag().graphicalDisplay(1,defender);
			}
		}
		if (defender.hasSkill(Skill.FELL_REAVER)) {
			defender.backupWeapon = defender.getBag().swapWeapon(defender.backupWeapon);
			switch (defender.getBag().getRaceID()) {
			case B_REAVER_TALL:
				defender.getBag().setRace(RaceID.B_REAVER_SHORT);
				
				break;
			case B_REAVER_SHORT:
				defender.getBag().setRace(RaceID.B_REAVER_TALL);
				break;
			}
		}
		//TODO: bleedout death quotes
		boolean bMary = (defender.hasEffect(Effect.B_MARY));
		for (Effect e: attacker.getEffects()) {
			switch (e) {
				case I_BLEED: case BLEED://only I_BLEED stacks, BLEED should only appear once
					attacker.takeDamage(attacker.getLevel());
					if (bMary) {
						defender.addHp(attacker.getLevel());
					}
					break;
				case MAJOR_BLEED://only I_BLEED stacks, others should only appear once
					attacker.takeDamage(2*attacker.getLevel());
					if (bMary) {
						defender.addHp(2*attacker.getLevel());
					}
					break;
				case BEES:
					if (attacker.hasEffect(Effect.BEES) && extra.chanceIn(1,5)) {
						extra.println("The bees sting "+attacker.getName()+"!");
						attacker.takeDamage(extra.randRange(1,attacker.getLevel()*2));
					}
					break;
			}
		}
		
		if (defender.getHp() <= 0) {
			Networking.send("PlayDelay|sound_fallover1|35|");
		}
		if (attacker.getHp() <= 0) {
			Networking.send("PlayDelay|sound_fallover1|35|");
		}
		if (canWait && !attacker.isPlayer()) {
			if (defender.isPlayer()) {
				Networking.waitIfConnected(500L);//was 500L
			}else {
			Networking.waitIfConnected(300L);}
		}
		return ret;
	}
	private String inflictWound(Person attacker2, Person defender2, AttackReturn retu) {
		int damage = retu.damage;
		if (retu.reliable == true) {
			return " The armor deflects the wound.";//reliable hits don't inflict wound effects
		}
		if ((defender2.hasSkill(Skill.TA_NAILS) && extra.randRange(1,5) == 1 )) {//|| damage == 0//wounds no longer hit if dam=0, if this needs to change, fix woundstring printing as well
			return (" They shrug off the blow!");
		}else {
		Attack attack = attacker2.getNextAttack();
		Integer[] nums = woundNums(attack,attacker2,defender2,retu);
		Wound w = attack.getWound();
		if (w == null) {
			throw new RuntimeException("inflicting null wound");
		}
		switch (w) {
		case CONFUSED:
			newTarget = true;
			break;
		case SLICE:
			attacker2.addEffect(Effect.SLICE);
			break;
		 case DICE:
			 attacker2.advanceTime(nums[1]);
			 attacker2.addEffect(Effect.DICE);
			 break;
		case HACK: case TAT:case CRUSHED:
		case SCALDED: case FROSTBITE:
			defender2.takeDamage(nums[0]);
			break;
		case HAMSTRUNG: case WINDED: case TRIPPED:
			defender2.advanceTime(-nums[0]);
			break;
		case DIZZY: case FROSTED: case BLINDED:
			defender2.getNextAttack().blind(1-(nums[0]/10f));
			break;	
		case MAJOR_BLEED:
			defender2.addEffect(Effect.BLEED);
			defender2.addEffect(Effect.MAJOR_BLEED);
			break;
		case BLEED:
			defender2.addEffect(Effect.BLEED);
			break;
		case DISARMED: case SCREAMING:
			defender2.addEffect(Effect.DISARMED);
			break;
		case KO:
			defender2.takeDamage(nums[0]);
			defender2.addEffect(Effect.RECOVERING);
			break;
		case I_BLEED:
			defender2.addEffect(Effect.I_BLEED);
			break;
		case TEAR:
			defender2.addEffect(Effect.TORN);
			break;
			
		}
		if (w != Wound.GRAZE)
			if (attack.getWeapon() != null && attack.getWeapon().qualList.contains(Weapon.WeaponQual.DESTRUCTIVE)) {
				defender2.getBag().burn((retu.damage/defender2.getMaxHp())/3, attack.getSlot());
			}
		}
		return (" " +attacker2.getNextAttack().getWound().active);
	}
	
	//had to convert from double to Double to Integer
	/**
	 * gives the numerical results of the wound being inflicted
	 * used to DRY sync results of wounds with the display text
	 * @param attacker
	 * @param defender
	 * @param result (null if attack didn't happen yet)
	 * @return array of doubles, you can round them if needed
	 */
	public static Integer[] woundNums(Attack attack, Person attacker, Person defender, AttackReturn result) {
		switch (attack.getWound()) {
		case CONFUSED:
			//nothing
			break;
		case SLICE:
			return new Integer[] {10,10};//10% faster, 10% more accurate
		 case DICE:
			 return new Integer[] {10,10};//10% faster, 10 time units faster
		case HACK: 
			return new Integer[] {result.damage/10};
		case TAT:
			if (result == null) {
				return new Integer[] {(int)(attack.getPierce()*extra.clamp(attack.getHitmod(),.5f,3f)/3f)};//this is 'up to'
			}
			return new Integer[] {(int)(result.subDamage[2] *extra.clamp(attack.getHitmod(),.5f,3f)/3f)};
		case CRUSHED:
			return new Integer[] {(int)attack.getTotalDam()/10};
		case SCALDED: case FROSTBITE:
			return new Integer[] {(int)attacker.getMageLevel()};
		case BLINDED:
			return new Integer[] {50};//50% less accurate
		case HAMSTRUNG:
			return new Integer[] {8};//-8 time units
		case DIZZY: case FROSTED:
			return new Integer[] {25};//25% less accurate
		case WINDED:
			return new Integer[] {16};//-16 time units
		case TRIPPED:
			return new Integer[] {20};//-20 time units
		case KO:
			return new Integer[] {5*defender.getLevel()};
		case BLEED: case I_BLEED://bleeds aren't synced, WET :(
			return new Integer[] {defender.getLevel()};
		case MAJOR_BLEED:
			return new Integer[] {2*defender.getLevel(),defender.getLevel()};
		case TEAR://WET
			return new Integer[] {10};// %, multiplicative dodge mult penalty
		}
		return new Integer[0];
	}


	private void handleMagicSpell(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		extra.println(extra.PRE_ORANGE + att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));
		if  (att.getSkill() == Skill.ELEMENTAL_MAGE) {
			//defender.inflictWound(att.getWound());//FIXME readd elemental wounds- but prefer to just make those normal
			//attacks instead, I don't store those so they can have more fields now
		def.burn(def.getFire(att.getSlot())*(att.getSharp()/100),att.getSlot());
		if (att.getSharp() > 0) {
			Networking.send("PlayDelay|sound_fireball|1|");
		}
		defender.advanceTime(-((att.getPierce()/100)*defender.getTime()*def.getFreeze(att.getSlot())));
		if (att.getPierce() > 0) {
			Networking.send("PlayDelay|sound_freeze|1|");
		}
		defender.takeDamage((int)((att.getBlunt())*def.getShock(att.getSlot())));
		if (att.getBlunt() > 0) {
			Networking.send("PlayDelay|sound_thunder|1|");
		}
		}
		if  (att.getSkill() == Skill.DEATH_MAGE) {
			defender.getNextAttack().wither(att.getSharp()/100);
			if (!defender.hasSkill(Skill.LIFE_MAGE)) {
			defender.takeDamage(att.getBlunt());}
			int i = 0;
			while (i < att.getPierce()) {
				defender.addEffect(Effect.BURNOUT);
				i++;
			}
			}
		if(att.getSkill() == Skill.ARMOR_MAGE) {
			off.restoreArmor(((double)att.getSharp())/100);
		}
		if(att.getSkill() == Skill.ILLUSION_MAGE) {
			Networking.send("PlayDelay|sound_befuddle|1|");
			newTarget = true;
		}
		if (att.getSkill() == Skill.EXECUTE_ATTACK) {
			double percent = ((double)extra.zeroOut(defender.getHp()-att.getBlunt()))/((double)defender.getMaxHp());
			if (percent < att.getSharp()/100.0) {
				defender.takeDamage(defender.getMaxHp()*2);
			}
		}
		if(att.getSkill() == Skill.DRUNK_DRINK) {
			if (attacker.hasBeer()) {
			attacker.addHp(Player.player.eaBox.drunkTrainLevel);
			attacker.addEffect(extra.choose(Effect.HASTE,Effect.R_AIM,Effect.BEE_SHROUD));
			attacker.consumeBeer();
			}else {
				extra.println("But they are out of beer!");
			}
		}
		if (att.getSkill() == Skill.MARK_ATTACK) {
			Player.player.eaBox.markTarget = defender;
		}
		
		if (att.getSkill() == Skill.BLOOD_SURGE) {
			attacker.addHp(Player.player.eaBox.bloodTrainLevel*(attacker.getBloodCount()/16));
			attacker.washAll();
		}
		
		if (att.getSkill() == Skill.BLOOD_HARVEST) {
			defender.takeDamage(Player.player.eaBox.bloodTrainLevel*((defender.getBloodCount()/10)));
			defender.washAll();
		}
	}


	private void setAttack(Person manOne, Person manTwo) {
		manOne.setAttack(AIClass.chooseAttack(manOne.getStance().part(manOne, manTwo),manOne.getIntellect(),this,manOne,manTwo));
		//manOne.getNextAttack().blind(1 + manOne.getNextAttack().getSpeed()/1000.0);
		//this blind was here to simulate quicker attacks being harder to dodge, it has been removed
	}
		
}
