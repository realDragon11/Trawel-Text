package trawel.battle;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import trawel.AIClass;
import trawel.Effect;
import trawel.Networking;
import trawel.WorldGen;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.TargetFactory;
import trawel.battle.attacks.TargetFactory.BloodType;
import trawel.battle.attacks.Attack.Wound;
import trawel.battle.attacks.ImpairedAttack;
import trawel.factions.FBox;
import trawel.personal.DummyPerson;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.Person.PersonFlag;
import trawel.personal.Person.PersonType;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.Skill;
import trawel.personal.classless.SkillAttackConf;
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
import trawel.personal.people.SuperPerson;
import trawel.towns.World;
import trawel.towns.fort.FortHall;
import trawel.towns.fort.LSkill;
import trawel.towns.fort.SubSkill;
/**
 * A combat holds some of the more battle-focused commands.
 * @author dragon
 * 2/8/2018
 */

public class Combat {
	//instance variables
	private List<Person> survivors;
	public List<Person> killed;
	public long turns = 0;
	public int totalFighters = 2;
	public boolean battleIsLong = false;
	
	private int winSide;
	
	public static Combat testCombat = new Combat();
	
	public static int longBattleLength = 20;
	
	public int endaether;
	
	private List<Person> completeList, killList;
	
	//constructor
	/**
	 * Holds a fight to the death, between two people.
	 * @param manOne (Person) [if the combat includes the player, they must be in this slot]
	 * @param manTwo (Person)
	 */
	public Combat(Person manOne, Person manTwo,World w) {
		completeList = new ArrayList<Person>();
		completeList.add(manOne);
		completeList.add(manTwo);
		Person attacker;
		Person defender;
		//Setup
		manOne.battleSetup();
		//extra.println("");
		manTwo.battleSetup();

		attacker = manOne;
		defender = manTwo;

		boolean playerIsInBattle;//= attacker.isPlayer() || defender.isPlayer();
		if (manOne.isPlayer()) {
			manTwo.displayStats(true);
			playerIsInBattle = true;
		}else {
			if (manTwo.isPlayer()) {
				manOne.displayStats(true);
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
		do {//combat loop
			if (manOne.getTime() < manTwo.getTime()) {
				attacker = manOne;
				defender = manTwo;
			}else {
				attacker = manTwo;
				defender = manOne;
			}
			if (attacker.hasSkill(Skill.BLITZ)) {
				attacker.applyDiscount(3);
			}
			double delay = attacker.getTime();
			defender.advanceTime(delay);
			attacker.advanceTime(delay);
			if (!attacker.isOnCooldown()) {
				handleTurn(attacker, defender, playerIsInBattle, delay);
				if (playerIsInBattle) {
					manTwo.getBag().graphicalDisplay(1,manTwo);
					Player.player.getPerson().getBag().graphicalDisplay(-1,Player.player.getPerson());
				}
			}

			if (manOne.isAlive() && manTwo.isAlive()) {
				if (!attacker.isAttacking()){
					setAttack(attacker,defender);
				}else {
					attacker.finishTurn();
				}

			}
		}
		while(manOne.isAlive() && manTwo.isAlive());

		if (manOne.isAlive()) {
			if (manTwo.isAlive()) {
				//no
			}else {
				attacker = manOne;
				defender = manTwo;
				winSide = 0;
				//attacker = manOne;
				//defender = manTwo;

			}
		}else {
			if (manTwo.isAlive()) {
				attacker = manTwo;
				defender = manOne;
				manOne = attacker;
				manTwo = defender;
				winSide = 1;
			}else {
				//both dead, manOne wins be default
				attacker = manOne;
				defender = manTwo;
				winSide = 0;
			}
		}
		
		extra.println(extra.choose("The dust settles...","The body drops to the floor.","Death has come.","The battle is over."));
		extra.println(defender.getName() + extra.choose(" lies dead..."," walks the earth no more..."," has been slain."));
		killData(attacker,defender);
		survivors = Collections.singletonList(attacker);
		killed = Collections.singletonList(defender);
		killList = killed;
	}
	
	public static class SkillCon {
		public SkillBase base;
		public float power;
		public float timer, resetTime;
		public int sideSource;
		public SkillCon(SkillBase _base,float _power, int _timer, int _resetTime, int _side) {
			base = _base;
			power = _power;
			timer = _timer;
			resetTime = _resetTime;
			sideSource = _side;
		}
		
		public SkillCon(SubSkill skill, float _power, int side) {
			power = _power;
			switch (skill) {
			case DEATH:
				base = SkillBase.WITHER;
				timer = 50;
				resetTime = 50;
				break;
			case DEFENSE:
				base = SkillBase.BLOCKADE;
				timer = 0;
				resetTime = -1;
				break;
			case ELEMENTAL:
				base = SkillBase.FIREBALLS;
				timer = 100;
				resetTime = 200;
				break;
			case ENCHANTING:
				base = null;
				break;
			case SCRYING:
				base = SkillBase.SCRY;
				timer = 100;
				resetTime = 500;
				break;
			case SMITHING:
				base = null;
				break;
			case WATCH:
				base = null;
				break;
			}
			sideSource = side;
		}
		
		public SkillCon setSide(int i) {
			sideSource = i;
			return this;
		}
	}
	
	public enum SkillBase{
		FIREBALLS, WITHER, SCRY, BLOCKADE
	}
	
	public static List<SkillCon> numberSkillConLists(List<List<SkillCon>> cons){
		List<SkillCon> ret = new ArrayList<Combat.SkillCon>();
		for (int i = 0; i < cons.size();i++) {
			for (SkillCon c: cons.get(i)) {
				ret.add(c.setSide(i));
			}
		}
		return ret;
	}
	
	public void handleSkillCons(List<SkillCon> cons, List<List<Person>> peoples,double timePassed) {
		for (SkillCon sk: cons) {
			int doTimes = 0;
			if (sk.resetTime <= 0 && sk.timer >= 0) {
				sk.timer-=timePassed;
				if (sk.timer < 0) {
					doTimes = 1;
				}
			}
			if (sk.resetTime > 0) {
				sk.timer-=timePassed;
				while (sk.timer <= 0) {
					sk.timer += sk.resetTime*extra.lerp(.8f,1.2f,extra.randFloat());//intentionally not perfect times
					doTimes++;
				}	
			}
			for (;doTimes > 0; doTimes--) {
				switch (sk.base) {
				case WITHER:
					if (!extra.getPrint()) {
						extra.println("The battlefield starts to wither!");
					}
					for (int i = 0; i < peoples.size();i++) {
						if (i == sk.sideSource) {
							continue;
						}
						for (Person p: peoples.get(i)) {
							if (p.isAttacking()) {
								p.getNextAttack().multPotencyMult(Math.min(20,sk.power)/100.0);
							}
							p.takeDamage(1);
						}
					}
					break;
				case FIREBALLS:
					if (!extra.getPrint()) {
						extra.println("The very air beings to boil! Armor melts away!");
					}
					for (int i = 0; i < peoples.size();i++) {
						if (i == sk.sideSource) {
							continue;
						}
						for (Person p: peoples.get(i)) {
							p.takeDamage((int) Math.min(20,sk.power));
							p.getBag().burnArmor(Math.min(20,sk.power*3)/100.0);
						}
					}
					break;
				case SCRY:
					if (!extra.getPrint()) {
						extra.println("A path is seen!");
					}
					for (int i = 0; i < peoples.size();i++) {
						if (i != sk.sideSource) {
							continue;
						}
						for (Person p: peoples.get(i)) {
							p.advanceTime(sk.power/10.0f);
						}
					}
					break;
				case BLOCKADE:
					if (!extra.getPrint()) {
						extra.println("The barricades are holding!");
					}
					for (int i = 0; i < peoples.size();i++) {
						if (i == sk.sideSource) {
							continue;
						}
						for (Person p: peoples.get(i)) {
							p.advanceTime(-sk.power);
						}
					}
					break; 
				}
			}
			
		}
	}
	public Combat(World w,List<List<Person>> people) {
		this(w,null,people);
	}
	
	private List<Person> tempList;
	private List<List<Person>> liveLists;
	private List<List<Person>> targetLists;
	private List<List<Person>> inSides;
	private Map<Person,BattleData> dataMap;
	private int sides;
	
	private class BattleData{
		public int side;
		public Person lastAttacker = null;
	}
	/*
	public Combat(World w,boolean typeErasure, List<List<SkillCon>> cons_lists,List<List<Person>> people) {
		this(w,numberSkillConLists(cons_lists),people);
	}*/
	
	public Combat(World w, List<SkillCon> cons,List<List<Person>> people) {
		inSides = people;
		int size = people.size();
		sides = size;
		completeList = new ArrayList<Person>();
		tempList = new ArrayList<Person>();
		killList = new ArrayList<Person>();
		liveLists = new ArrayList<List<Person>>();
		targetLists = new ArrayList<List<Person>>();
		dataMap = new HashMap<Person,BattleData>();
		for (int i = 0; i < people.size();i++) {
			List<Person> subPList = people.get(i);
			List<Person> newList = new ArrayList<Person>();
			for (int j = 0;j < subPList.size();j++) {
				Person p = subPList.get(j);
				newList.add(p);
				BattleData data = new BattleData();
				data.side = i;
				dataMap.put(p,data);
			}
			liveLists.add(newList);
			targetLists.add(new ArrayList<Person>());
		}
		for (int i = 0; i < people.size();i++) {
			List<Person> subTList = targetLists.get(i);
			for (int j = 0; j < people.size();j++) {
				if (i == j) {
					continue;
				}
				subTList.addAll(people.get(j));
			}
		}
		
		boolean playerIsInBattle = false;
		/*
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
				cons.add(new SkillCon(new LSkill(SubSkill.DEFENSE,temp),0,-1));
			}
			temp = hall.getSkillCount(SubSkill.SCRYING);
			if (temp > 0) {
				cons.add(new SkillCon(new LSkill(SubSkill.SCRYING,temp),100,500));
			}
		}
		*/
		for (List<Person> peoples: liveLists) {
			for (Person p: peoples) {
				if (p.isPlayer()) {
					Networking.setBattle(Networking.BattleType.NORMAL);
					playerIsInBattle = true;
					mainGame.story.startFight(true);
				}
				p.battleSetup();
				tempList.add(p);
				completeList.add(p);
			}
		}
		for (Person p: completeList) {
			Person otherperson = getDefenderFor(p);
			if (p.isPlayer()) {
				otherperson.displayStats(true);
				p.getBag().graphicalDisplay(-1,p);
				otherperson.getBag().graphicalDisplay(1,otherperson);
			}
			setAttack(p,otherperson);
		}
		totalFighters = completeList.size();
		Person quickest = null;//moved out so we can have a default last actor to default as alive
		while(true) {
			double lowestDelay = Double.MAX_VALUE;
			for (Person p: tempList) {
				if (lowestDelay > p.getTime()) {
					lowestDelay = p.getTime();
					quickest = p;
				}
			}
			if (quickest.hasSkill(Skill.BLITZ)) {
				quickest.applyDiscount(3);
				lowestDelay-=3;
			}
			
			
			for (Person p: tempList) {
				p.advanceTime(lowestDelay);
			}

			if (cons != null) {
				this.handleSkillCons(cons, liveLists, lowestDelay);
			}

			Person defender = quickest.getNextAttack().getDefender();
			if (!mainGame.displayOtherCombat && !(quickest.isPlayer() || defender.isPlayer())) {
				extra.offPrintStack();
			}
			boolean wasAlive = defender.isAlive();
			dataMap.get(defender).lastAttacker = quickest;
			AttackReturn atr = handleTurn(quickest,defender,playerIsInBattle,lowestDelay);
			if (atr.code != ATK_ResultCode.NOT_ATTACK) {
				if (!defender.isAlive() && wasAlive) {
					killWrapUp(defender, quickest, false);
				}else {
					if (defender.hasEffect(Effect.CONFUSED_TARGET)) {
						//the defender has been befuddled or confused
						if (!defender.isOnCooldown() && defender.isAttacking()) {//if on cooldown or not attacking, the effect will apply to the next attack instead
							defender.removeEffect(Effect.CONFUSED_TARGET);
							Person newDef = getDefenderForConfusion(defender);
							if (defender.getNextAttack().getDefender().isSameTargets(newDef)) {
								defender.getNextAttack().setDefender(newDef);
								if (!extra.getPrint()) {
									extra.println(defender.getNameNoTitle() + " confuses who they're attacking!");
								}
							}else {
								//targets don't match, need new attack.
								//get discount on next one
								double discount = -Math.max(0,((defender.getTime()-defender.getNextAttack().getWarmup()))/2.0);
								setAttack(defender,newDef);
								defender.applyDiscount(discount);
								if (!extra.getPrint()) {
									extra.println(defender.getNameNoTitle() + " looks around for a new target in confusion!");
								}
							}
						}
					}
				}
			}


			//if the attacker is dead, through bleeding or otherwise, they are force removed- they would not have acted if they were already removed
			if (!quickest.isAlive()) {
				killWrapUp(quickest,dataMap.get(quickest).lastAttacker,true);
			}
			
			if (!mainGame.displayOtherCombat && !(quickest.isPlayer() || defender.isPlayer())) {
				extra.popPrintStack();
			}

			int sidesLeft = 0;
			for (List<Person> list: liveLists) {
				if (list.size() > 0) {
					sidesLeft++;
				}
			}

			if (sidesLeft <= 1) {
				break;//end battle
			}

			if (!quickest.isAttacking()) {
				Person otherperson = null;
				if (quickest.hasEffect(Effect.CONFUSED_TARGET)) {
					quickest.removeEffect(Effect.CONFUSED_TARGET);
					otherperson = getDefenderForConfusion(quickest);
				}else {
					if (defender.isAlive() && quickest.getBag().getHand().hasQual(Weapon.WeaponQual.DUELING)) {
						otherperson = defender;
					}else {
						otherperson = getDefenderFor(quickest);
					}
				}
				if (quickest.isPlayer()) {
					otherperson.displayStats(true);
					quickest.getBag().graphicalDisplay(-1,quickest);
					otherperson.getBag().graphicalDisplay(1,otherperson);
				}else {
					if (playerIsInBattle) {
						quickest.getBag().graphicalDisplay(1,quickest);
						Player.player.getPerson().getBag().graphicalDisplay(-1,Player.player.getPerson());
					}
				}
				setAttack(quickest,otherperson);
				if (otherperson != defender && (atr.code == ATK_ResultCode.MISS || atr.code == ATK_ResultCode.DODGE)
						&& quickest.getBag().getHand().hasQual(WeaponQual.CARRYTHROUGH)) 
				{
					quickest.applyDiscount(quickest.getTime()*.2f);//20% time discount
				}
			}
		}
		
		if (tempList.size() == 0) {//no survivors, last actor wins
			//MAYBELATER: undesirable behavior on summons
			tempList.add(quickest);
			killList.remove(quickest);
		}else {
			if (!tempList.contains(quickest)) {//if the last actor died some other way
				quickest = tempList.get(0);//get first living Person
			}
		}
		
		for (int i = 0; i < sides;i++) {
			if (inSides.get(i).contains(quickest)) {
				winSide = i;
				break;
			}
		}
		/*
		for (List<Person> listoflist: inSides) {
			for (Person p: listoflist) {
				p.setFlag(PersonFlag.PLAYER_SIDE,false);
			}
		}*/
		
		survivors = tempList;
		killed = killList;
		
		assert survivors.size() > 0;
	}
	
	public Combat() {
		//empty for tests
	}
	
	private Person getDefenderFor(Person attacker) {
		return extra.randList(targetLists.get(dataMap.get(attacker).side));
	}
	
	private Person getDefenderForConfusion(Person attacker) {
		//will attempt to target anyone, but if that fails and targets self, revert to old way
		Person defender = extra.randList(tempList);
		if (defender == attacker || defender.hasSkill(Skill.PLOT_ARMOR)) {//plot armor (npcs for now but would work on player sorta) have a reduced chance to get targeted
			return getDefenderFor(attacker);
		}
		return defender;
	}
	
	/**
	 * should not be used outside of killWrapUp
	 * @return if any change happened in backing data due to this call
	 */
	private boolean killRemovePerson(Person dead) {
		int side = dataMap.get(dead).side;
		boolean changed = liveLists.get(side).remove(dead);
		for (int i = 0; i < sides; i++) {
			if (i == side) {
				continue;
			}
			changed = targetLists.get(i).remove(dead) ? true : changed ;
		}
		changed = tempList.remove(dead) ? true : changed ;
		if (!killList.contains(dead)) {
			changed = true;
			killList.add(dead);
		}
		return changed;
	}
	
	/**
	 * wraps killRemovePerson and killData together for mass battles
	 */
	private boolean killWrapUp(Person dead, Person killer, boolean bledOut) {
		boolean changed = killRemovePerson(dead);
		if (changed) {
			if (bledOut) {
				extra.println(dead.getName() + " falls to the ground, dead!");
			}else {
				extra.println(dead.getName() + " dies!");
			}
			killData(dead,killer);
		}
		return changed;
	}
	
	/**
	 * use directly in 1v1s, mass battles should use killWrapUp
	 */
	private void killData(Person dead, Person killer) {
		dead.addDeath();
		
		if (killer == null) {
			return;
		}
		if (dead.hasSkill(Skill.CURSE_MAGE)) {
			if (killer.hasSkill(Skill.NO_HOSTILE_CURSE)) {
				if (!extra.getPrint()) {
					extra.println(dead.getName() + " curses the name of " +killer.getNameNoTitle()+", but they seem unaffected.");
				}
			}else {
				if (!extra.getPrint()) {
					extra.println(dead.getName() + " curses the name of " +killer.getNameNoTitle()+"!");
				}
				killer.addEffect(Effect.CURSE);
			}
		}
		if (killer.hasSkill(Skill.CONDEMN_SOUL)) {
			if (dead.hasSkill(Skill.NO_HOSTILE_CURSE)) {
				if (!extra.getPrint()) {
					extra.println(killer.getName() + " condemns the soul of " +dead.getNameNoTitle()+", but they seem unaffected.");
				}
			}else {
				if (!extra.getPrint()) {
					extra.println(killer.getName() + " condemns the soul of " +dead.getNameNoTitle()+"!");
				}
				dead.addEffect(Effect.CURSE);
			}
		}
		
		killer.addKillStuff();
		killer.getBag().getHand().addKill();
		if (dead.isPlayer()) {
			killer.addPlayerKill();
		}
		if (killer.hasSkill(Skill.KILLHEAL)){
			int restore = IEffectiveLevel.cleanLHP(Math.min(2+killer.getLevel(),dead.getLevel()),.05);
			killer.addHp(restore);
			if (!extra.getPrint()) {
				extra.println(killer.getName() + " heals " + restore+" from the kill!");
			}
		}
		if (killer.hasSkill(Skill.PRESS_ADV)) {
			killer.addEffect(Effect.ADVANTAGE_STACK);
			killer.addEffect(Effect.ADVANTAGE_STACK);
			if (!extra.getPrint()) {
				extra.println(killer.getName() + " presses the advantage!");
			}
		}
		FBox.repCalc(killer,dead);
	}
	
	/**
	 * Redone, write new docs
	 */
	public AttackReturn handleAttack(boolean isReal, ImpairedAttack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		String str = "";
		AttackReturn ret;
		boolean wasDead = false;
		int wLevel = att.getLevel();
		boolean canDisp = isReal && !extra.getPrint();
		if (defender == null) {
			defender = DummyPerson.single;
		}
		if (off == null) {
			off = WorldGen.getDummyInvs().get(0);//attacking only
		}
		if (canDisp) {
			if (extra.chanceIn(1, 5)) {
				Networking.send("PlayDelayPitch|"+SoundBox.getSound(off.getRace().voice,SoundBox.Type.SWING) + "|1|" +attacker.getPitch() +"|");
			}
			if (!defender.isAlive()) {
				wasDead = true;//note, can move out if need be
			}
		}
		//TODO unsure if should add attacker's aim at impairment phase
		double dodgeBase = def.getDodge()*defender.getTornCalc();
		double hitBase = att.getHitMult();
		
		double dodgeRoll = dodgeBase*extra.getRand().nextDouble();
		double hitRoll = hitBase*extra.getRand().nextDouble();
		if (isReal) {
			if (attacker.hasEffect(Effect.ADVANTAGE_STACK)) {
				hitRoll*=1.2f;
				attacker.removeEffect(Effect.ADVANTAGE_STACK);
			}
			if (defender.hasEffect(Effect.ADVANTAGE_STACK)) {
				dodgeRoll*=1.2f;
				defender.removeEffect(Effect.ADVANTAGE_STACK);
			}
		}
		if (hitRoll < .05) {//missing now exists
			ret= new AttackReturn(ATK_ResultCode.MISS,"",att);
			if (canDisp) {
				ret.stringer = att.fluff(ret);
				ret.putPlayerFeedback(ret);
				if (wasDead) {
					ret.addNote("They missed a corpse!");
				}
			}
			return ret;
		}
		if (dodgeRoll > hitRoll){
			ret= new AttackReturn(ATK_ResultCode.DODGE,"",att);
			if (canDisp) {
				ret.stringer = att.fluff(ret);
				ret.putPlayerFeedback(ret);
				if (wasDead) {
					ret.addNote("What a dodgy corpse!");
				}
			}
			return ret;
		}
		
		ret = new AttackReturn(att,def,str);
		if (canDisp) {
			Networking.send("PlayHit|" +def.getSoundType(att.getSlot()) + "|"+att.getAttack().getSoundIntensity() + "|" +att.getAttack().getSoundType()+"|");
			if (wasDead) {
				ret.addNote("Beating their corpse!");
			}
		}
		int eHalfLevel = (int) (IEffectiveLevel.effective(wLevel)/2);
		if (ret.type == ATK_ResultType.IMPACT) {//normal damage and killing
			if (att.hasWeaponQual(WeaponQual.REFINED)) {
				ret.damage += eHalfLevel;
				ret.bonus += eHalfLevel;
				if (canDisp) {ret.addNote("Refined Bonus: " + eHalfLevel);}
			}
			if (att.hasWeaponQual(Weapon.WeaponQual.WEIGHTED)) {
				if (att.getHitMult() < 1.5) {
					int weightBonus = ret.damage;
					//TODO: check this
					ret.damage = (int) Math.round(ret.damage*Math.log10(5+(20-(att.getHitMult()*10))));
					weightBonus = ret.damage-weightBonus;
					ret.bonus += weightBonus;
					if (canDisp) {ret.addNote("Weighted Bonus: " + weightBonus);}
				}
			}
			if (defender.hasSkill(Skill.RAW_GUTS)) {
				int maxGResist = IEffectiveLevel.cleanLHP(
						Math.ceil(defender.getLevel() * defender.getConditionForPart(TargetFactory.TORSO_MAPPING))
						,.03);
				int gResisted = ret.damage;
				ret.damage = Math.max(ret.damage/2,ret.damage-extra.randRange(0,maxGResist));
				gResisted = gResisted-ret.damage;
				if (canDisp) {ret.addNote("Raw Guts Resisted: " + gResisted);}
			}
		}
		if (att.hasWeaponQual(WeaponQual.RELIABLE) && ret.damage < eHalfLevel) 
		{
			ret.bonus = eHalfLevel-ret.damage;
			ret.damage = eHalfLevel;
			if (canDisp) {ret.addNote("Reliable Damage: "+eHalfLevel);}
		}
		if (canDisp) {
			ret.stringer = str + att.fluff(ret);
			ret.putPlayerFeedback(ret);
		}
		return ret;
	}
	
	public static AttackReturn handleTestAttack(ImpairedAttack att, Inventory def, double armMod) {
		return Combat.testCombat.handleAttack(false,att,def,null,armMod,null,null);	
		
	}
	
	public class AttackReturn {
		public int damage;
		public int[] subDamage;
		public String stringer;
		public int bonus = 0;
		public ImpairedAttack attack;
		public ATK_ResultCode code;
		public ATK_ResultType type;
		private String notes;
		public AttackReturn(ImpairedAttack att, Inventory def, String str) {
			int sdam = att.getSharp();
			int bdam = att.getBlunt();
			int pdam = att.getPierce();
			int idam = att.getIgnite();
			int fdam = att.getFrost();
			int edam = att.getElec();
			
			double sarm = def.getSharp(att)*Armor.armorEffectiveness;
			double barm = def.getBlunt(att)*Armor.armorEffectiveness;
			double parm = def.getPierce(att)*Armor.armorEffectiveness;
			
			
			boolean bypass = att.getAttack().isBypass();
			
			stringer = str;
			attack = att;
			notes = null;
			//MAYBELATER: can turn this into an array of damage types later if need be
			double rawdam = bypass ? idam+fdam+edam : sdam+bdam+pdam;
			double rawarm =sarm+barm+parm;
			double s_weight = sdam/rawdam;
			double b_weight = bdam/rawdam;
			double p_weight = pdam/rawdam;
			double weight_arm = bypass ? rawarm/3 : (s_weight*sarm)+(b_weight*barm)+(p_weight*parm);
			
			double iarm = weight_arm*def.getIgniteMult(att.getSlot());
			double farm = weight_arm*def.getFrostMult(att.getSlot());
			double earm = weight_arm*def.getElecMult(att.getSlot());
			
			//double guess = ((rawdam+weight_arm)/weight_arm)-1;
			double def_roll = Math.max(.05,extra.hrandom());
			float att_roll = extra.lerp(.7f,1f,extra.hrandomFloat());
			double global_roll = (att_roll*rawdam)/(def_roll*weight_arm);
			if (global_roll < .4) {//if our random damage roll was less than 40% of the armor roll, negate
				subDamage = new int[] {0,0,0,0,0,0};
				damage = 0;
				code = ATK_ResultCode.ARMOR;
				type = ATK_ResultType.NO_IMPACT;
			}else {//TODO: new goal: % reductions based on relativeness with a chance to negate entirely if much higher?
				//up to half the damage if the damage roll was less than the total weighted armor (without roll)
				double reductMult = damageCompMult(.5f,1f,1f,att_roll*rawdam,def_roll*weight_arm,1f,1.5f);
				//each damage vector can be brought down to 20%, but this is cumulative so the armor has to be 4x higher
				//to achieve that level of reduction
				double s_reduct = damageCompMult(.2f,.9f,1f,sdam,sarm,2f,4f);
				double b_reduct = damageCompMult(.2f,.9f,1f,bdam,barm,2f,4f);
				double p_reduct = damageCompMult(.2f,.9f,1f,pdam,parm,2f,4f);
				int scomp = (int) (sdam*reductMult*s_reduct);
				int bcomp = (int) (bdam*reductMult*b_reduct);
				int pcomp = (int) (pdam*reductMult*p_reduct);
				
				double i_reduct = damageCompMult(.2f,.9f,1f,idam,iarm,2f,4f);
				double f_reduct = damageCompMult(.2f,.9f,1f,fdam,farm,2f,4f);
				double e_reduct = damageCompMult(.2f,.9f,1f,edam,earm,2f,4f);
				
				int icomp = (int) (idam*reductMult*i_reduct);
				int fcomp = (int) (fdam*reductMult*f_reduct);
				int ecomp = (int) (edam*reductMult*e_reduct);
				//DOLATER: not sure if this is done yet
				
				subDamage = new int[] {scomp,bcomp,pcomp,icomp,fcomp,ecomp};
				for (int i = 0; i < subDamage.length; i++) {
					damage += subDamage[i];
				}
				damage = Math.max(1,damage);//deals a min of 1 damage
				code = ATK_ResultCode.DAMAGE;
				type = ATK_ResultType.IMPACT;
				if (att.getAttacker() != null) {
					if (att.getAttacker().hasSkill(Skill.DSTRIKE) && (damage > att.getDefender().getMaxHp()*.7f)) {
						code = ATK_ResultCode.KILL;
					}
				}
				
				if (!extra.getPrint()) {
					addNote("rawdam: " +rawdam +"("+sdam+"/"+bdam+"/"+pdam+ " " + idam+"/"+fdam+"/"+edam+")"
				+ " rawarm: " + rawarm + "("+sarm+"/"+barm+"/"+parm+" " +iarm+"/"+farm+"/"+earm+")"
							+ " weight_a: " + weight_arm + " groll: " + global_roll 
							+ " comps: " +subDamage[0] +"/"+subDamage[1]+"/"+subDamage[2] + " " + subDamage[3] +"/"+subDamage[4]+"/"+subDamage[5]
							+ " reduct: " + reductMult + " ("+s_reduct+"/"+b_reduct+"/"+p_reduct+" " +i_reduct+"/"+f_reduct+"/"+e_reduct +")");
				}
			}
		}
		/**
		 * 
		 * @param minMult minimum multiplier
		 * @param maxMult maximum multiplier
		 * @param inDam damage
		 * @param inArm armor
		 * @param armor_threshold how many times armor must be higher than damage to reach minmult
		 * @param weapon_threshold how many times weapon must be higher than armor to reach maxmult
		 */
		private float damageCompMult(float minMult, float equalMult, float maxMult, double inDam, double inArm, float weapon_threshold, float armor_threshold) {
			if (inDam >= inArm) {
				return extra.lerp(maxMult,equalMult,extra.clamp((float)(inArm/(inDam/weapon_threshold)),0f,1f));
			}
			return extra.lerp(equalMult,maxMult,extra.clamp((float)(inDam/(inArm/armor_threshold)),0f,1f));
		}
		
		public AttackReturn(ATK_ResultCode rcode, String str, ImpairedAttack att) {
			type = ATK_ResultType.NO_IMPACT;
			code = rcode;
			damage = 0;
			//null
			stringer = str;
			attack = att;
			notes = null;
		}
		
		public AttackReturn() {
			code = ATK_ResultCode.NOT_ATTACK;
			type = ATK_ResultType.IMPACT;
		}
		
		public void addNote(String str) {
			if (notes == null) {
				notes = str;
			}else {
				notes +="\n"+str;
			}
		}
		
		public String getNotes() {
			return notes;
		}
		
		public void putPlayerFeedback(AttackReturn atr) {
			/*if (attack == null) {
				extra.getPrint();
			}*/
			if (attack.getAttacker().isPlayer() && attack.getDefender() != null) {
			Player.lastAttackStringer = atr.attack.getName()+": ";
			Person def = attack.getDefender();
			int index = completeList.indexOf(def);
				switch (code) {
				case ARMOR:
					Player.lastAttackStringer += extra.ATTACK_BLOCKED+"You hit "+
					"[HPT"+index+"]"+def.getName() + 
					extra.AFTER_ATTACK_BLOCKED+" but their armor held!"
					+
					(damage > 0 ? extra.ATTACK_DAMAGED_WITH_ARMOR+" and dealt " + damage+" damage!" : "");
					break;
				case DAMAGE: case KILL:
					Player.lastAttackStringer += extra.ATTACK_DAMAGED+"You hit "+
					"[HPT"+index+"]"+def.getName() + extra.ATTACK_DAMAGED+
					" dealing "+prettyHPDamage(attack.getTotalDam(),attack.getDefender())+ damage+" damage!";
					break;
				case DODGE:
				case MISS:
					Player.lastAttackStringer += extra.AFTER_ATTACK_MISS+"You missed "+
					"[HPT"+index+"]"+def.getName() + extra.ATTACK_MISS+
					(damage > 0 ? " but dealt " + damage+" damage!" : "!");
					break;
				case NOT_ATTACK:
					//Player.lastAttackStringer = "";
					break;
				}
			}
		}
		public boolean hasWound() {
			return attack != null && attack.getWound() != null;
		}
	}
	
	public enum ATK_ResultCode{
		DAMAGE, KILL, NOT_ATTACK, MISS, DODGE, ARMOR
	}
	/**
	 * whether special effects should apply (if real)
	 * <br>
	 * can be set to a different value than the base type is supposed to deal and things should still work
	 */
	public enum ATK_ResultType{
		/**
		 * if wounds and other special effects should not be applied
		 * <br>
		 * by default, this occurs on MISS, DODGE, and ARMOR
		 */
		NO_IMPACT,
		/**
		 * if wounds and other special effects should not be applied
		 * <br>
		 * by default, this occurs on DAMAGE, KILL, and NOT_ATTACK
		 */
		IMPACT
	}
	
	/**
	 * does stuff that can't go in handleAttack because it's permanent and handleAttack is used in ai stuff.
	 * handles elemental damage
	 * should scale of off % of hp damaged or something to avoid confusing the ai
	 */
	//FIXME: should probably move this to attack returns or something??? and the 'handle turn' code
	public void handleAttackPart2(ImpairedAttack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender, int damageDone) {
		double percent = ((double)extra.zeroOut(damageDone))/((double)defender.getMaxHp());
		if (off.getHand().isEnchantedHit() && !(att.getName().contains("examine"))) {
			EnchantHit ehit = (EnchantHit)off.getHand().getEnchant();
			//def.burn(def.getFire(att.getSlot())*percent*ehit.getFireMod()/2,att.getSlot());
			
			//defender.advanceTime(-(percent*defender.getTime()*ehit.getFreezeMod()*def.getFreeze(att.getSlot())));
			
			//defender.takeDamage((int)(percent*ehit.getShockMod()/3*def.getShock(att.getSlot())));
		}
	}
	
	/**
	 * 
	 * @param attacker
	 * @param defender
	 * @return damagedone
	 */
	public AttackReturn handleTurn(Person attacker, Person defender,boolean canWait, double delay) {
		if (attacker.isOnCooldown()) {
			attacker.finishTurn();
			return new AttackReturn();
		}
		attacker.finishWarmup();
		turns++;
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
			attacker.healHP(
					IEffectiveLevel.cleanLHP(attacker.getLevel(), .05)
					);
		}
		if (defender.hasSkill(Skill.COUNTER)) {
			defender.advanceTime(2);
		}
		if (defender.hasEffect(Effect.FORGED)) {
			defender.getBag().restoreArmor(0.1);
		}
		if (defender.hasEffect(Effect.B_MARY)) {
			defender.addEffect(Effect.I_BLEED);
			attacker.addEffect(Effect.I_BLEED);
		}
		if (!extra.getPrint() && mainGame.displayFlavorText && attacker.getPersonType() != PersonType.NO_SPEAK && extra.chanceIn(1,4)) {
			if (extra.chanceIn(1,3)) {
					BarkManager.getBoast(attacker,true);//extra.println(attacker.getName() + " "+extra.choose("shouts","screams","boasts")+ " \"" + attacker.getTaunts().getBoast()+"\"");		
			}else {
				if (attacker.isPersonable() && //must but fully personable to be racist for now
						((attacker.isAngry() && defender.getBag().getRace().racialType == RaceType.BEAST) || (attacker.isRacist() && !attacker.getBag().getRace().equals(defender.getBag().getRace()))) && extra.chanceIn(1,3)) 
				{
					extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" +defender.getBag().getRace().randomInsult()+"\"");
				}else {
					BarkManager.getTaunt(attacker,defender);//extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" + attacker.getTaunts().getTaunt()+"\"");
				}				
			}
		}
		ImpairedAttack attack = attacker.getNextAttack();
		AttackReturn atr = handleAttack(true,attack,defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender);
		int damageDone = atr.damage;
		this.handleAttackPart2(attack,defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender,damageDone);
		//armor quality handling
		//FIXME: apply new attack result code system where needed
		defender.getBag().armorQualDam(damageDone);
		//message handling
		float percent = 0f;
		if (damageDone > 0) {
			percent = damageDone/(float)defender.getMaxHp();
			if (extra.chanceIn((int)(percent*100) + (defender.getHp() <= 0 ? 10 : 0), 120)) {
				Networking.send("PlayDelayPitch|"+SoundBox.getSound(defender.getBag().getRace().voice,SoundBox.Type.GRUNT) + "|4|"+ defender.getPitch()+"|");
			}
			//blood
			
			if (defender.getBlood() == BloodType.NORMAL) {
				
				attacker.getBag().getHand().addBlood(percent*5);
				defender.getBag().getArmorSlot(attacker.getNextAttack().getSlot()).addBlood(percent*2f);
				defender.addBlood(percent*1f);
				if (damageDone > .025f*defender.getMaxHp()) {
					for (Armor a: defender.getBag().getArmor()) {
						a.addBlood(percent*.5f);
					}
				}
			}
			//condition damage
			defender.addBodyStatus(atr.attack.getTargetSpot(),-percent);
			
			defender.debug_print_status(damageDone);
			
			if (defender.isAttacking() && defender.hasEffect(Effect.R_AIM)) {
				defender.getNextAttack().multiplyHit(1 + (percent));
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
		}
			
		//Wounds can now be inflicted even if not dealing damage
		String woundstr = "";
		boolean forceKilled = false;
		boolean doForceKill = false;
		if (atr.type == ATK_ResultType.IMPACT) {
			if (attacker.hasSkill(Skill.SPUNCH)) {
				defender.advanceTime(atr.attack.getTime()/50f);
			}
			if (atr.hasWound()) {
				//|| damage == 0//wounds no longer hit if dam=0, if this needs to change, fix woundstring printing as well
				//DOLATER: fix that?
				//although they'll still not usually apply if damage == 0
				if (atr.code == ATK_ResultCode.ARMOR) {
					woundstr = " The armor deflects the wound.";
				}else {
					if (((defender.hasSkill(Skill.TA_NAILS) && extra.randRange(1,5) == 1 ))) {
						woundstr = " They shrug off the blow!";
					}else {
						woundstr = inflictWound(attacker,defender,atr,null);
					}
				}
				
			}
			if (atr.code == ATK_ResultCode.KILL) {//if force kill
				//might not actually be final death, but this is fine to say
				doForceKill = true;
				extra.println(attacker.getName() + " executes " + defender.getName() +"!");
				if (damageDone < defender.getHp()) {//if they won't be able to kill them
					defender.forceKill();
					forceKilled = true;
				}
			}
			if (attacker.hasSkill(Skill.BLOODTHIRSTY)) {
				;
				int blood_heal = attacker.healHP(
						IEffectiveLevel.cleanLHP(Math.min(defender.getLevel(),attacker.getLevel()),.01));
				if (!extra.getPrint()) {
					extra.println(attacker.getName()+ " heals " + blood_heal + " from their bloodthirst!");
				}
			}
			if (attacker.hasSkill(Skill.NPC_BURN_ARMOR)) {
				//always burns at least 5% before diminishing
				defender.getBag().burnArmor(Math.max(0.05f,(percent*2)),atr.attack.getSlot());
			}
			//only processes on an impactful attack, to be consistent with wounds (plus make code easier)
			List<Wound> wounds = defender.processBodyStatus();
			if (wounds != null) {
				for (Wound wo: wounds) {
					woundstr += inflictWound(attacker,defender,atr,wo);
				}
			}
		}else {//no impact
			if (defender.hasSkill(Skill.MESMER_ARMOR)) {
				if (defender.contestedRoll(attacker,defender.getClarity(),attacker.getHighestAttribute()) >= 0){
					attacker.addEffect(Effect.CONFUSED_TARGET);
					if (!extra.getPrint()) {
						extra.println(defender.getName()+ "'s illusory armor mesmerizes "+attacker.getName() +"...");
					}
				}
			}
		}
		boolean didDisplay = false;
		if (damageDone > 0 || doForceKill) {
			defender.takeDamage(Math.max(1,damageDone));
			if (!defender.isAlive()) {
				if (defender.hasEffect(Effect.STERN_STUFF)) {
					defender.removeEffectAll(Effect.STERN_STUFF);
					if (
							doForceKill && //if they wouldn't have died otherwise, they don't need to roll
							(forceKilled || defender.contestedRoll(attacker, 
							defender.getStrength(), attacker.getHighestAttribute())>=0)
							){
						defender.resistDeath(0f);
						if (!extra.getPrint()) {
							extra.print(
									prettyHPColors(atr.stringer+"[C] {"+prettyHPDamage(percent)+damageDone+" damage[C]}"
										+woundstr+" But they're made of sterner stuff!"
									, extra.ATTACK_DAMAGED, attacker, defender));
							didDisplay = true;
						}
					}
				}
				if (!didDisplay && !extra.getPrint()) {
					extra.print(
							prettyHPColors(atr.stringer+"[C] {"+prettyHPDamage(percent)+damageDone+" damage[C]}"
							+woundstr,extra.ATTACK_KILL, attacker, defender));
					didDisplay = true;
				}
			}else {
				if (!extra.getPrint()) {
					
					extra.print(prettyHPColors(atr.stringer+" {"+prettyHPDamage(percent)+damageDone+" damage[C]}"+woundstr
							,
							atr.code == ATK_ResultCode.ARMOR ? extra.ATTACK_DAMAGED_WITH_ARMOR : extra.ATTACK_DAMAGED
							,attacker,defender));
					didDisplay = true;
				}
			}
		}
		//if (atr.type == ATK_ResultType.NO_IMPACT) {
		//impact is now more directly if wounds/special effects happen
		switch (atr.code) {
		case DODGE: case MISS:
			if (!extra.getPrint() && !didDisplay) {
				extra.print(prettyHPColors(atr.stringer +woundstr,extra.ATTACK_MISS, attacker, defender));
				Networking.sendStrong("PlayMiss|" + "todo" + "|");
				extra.print(" "+extra.AFTER_ATTACK_MISS+randomLists.attackMissFluff(atr.code)+extra.ATTACK_MISS);
			}
			if (atr.code == ATK_ResultCode.DODGE) {
				if (defender.hasSkill(Skill.SPEEDDODGE)) {
					defender.applyDiscount(10);
					extra.print(" They dodge closer to the action!");
				}
				if (defender.hasSkill(Skill.DODGEREF)) {
					int dodgeHeal = IEffectiveLevel.cleanLHP(Math.min(defender.getLevel()+2,attacker.getLevel()),.01);
					defender.addHp(dodgeHeal);
					extra.print(" Refreshing Dodge heals " + dodgeHeal +"!");
				}
				if (defender.hasSkill(Skill.REACTIVE_DODGE)) {
					defender.addEffect(Effect.ADVANTAGE_STACK);
					extra.print(" They roll to a better position!");
				}
			}
			
			if (defender.hasEffect(Effect.BEE_SHROUD)) {
				if (attacker.hasEffect(Effect.BEES)) {
					if (!extra.getPrint()) {
						extra.println(extra.inlineColor(extra.colorMix(Color.PINK,Color.WHITE,.2f))+"The bees buzz back!");
					}
				}else {
					attacker.addEffect(Effect.BEES);
					if (!extra.getPrint()) {
						extra.println(extra.inlineColor(extra.colorMix(Color.PINK,Color.WHITE,.2f))+"The bees swarm "+attacker.getName()+"!");
					}
				}
			}
			break;
		case ARMOR:
			if (!extra.getPrint() && !didDisplay) {
				extra.print(prettyHPColors(atr.stringer +woundstr,extra.ATTACK_BLOCKED, attacker, defender));
				extra.print(extra.AFTER_ATTACK_BLOCKED+" "+randomLists.attackNegateFluff()+extra.ATTACK_BLOCKED);
			}
			if (defender.hasSkill(Skill.ARMORHEART)) {
				int armorHeal = IEffectiveLevel.cleanLHP(Math.min(defender.getLevel()+4,attacker.getLevel()),.02);
				defender.addHp(armorHeal);
				extra.print(" Armor Heart heals " + armorHeal +"!");
			}
			if (defender.hasSkill(Skill.ARMORSPEED)) {
				defender.applyDiscount(10);
				extra.print(" They advance closer to the action...");
			}
			break;
		}
		//}

		if (canWait && mainGame.delayWaits) {
			Networking.waitIfConnected(100L+(long)delay*2);
		}

		extra.println("");

		float hpRatio = ((float)defender.getHp())/(defender.getMaxHp());
		//float hpRatio = ((float)p.getHp())/(p.getMaxHp());
		//extra.println(p.getHp() + p.getMaxHp() +" " + hpRatio);
		/*if (!extra.getPrint()) {//should save computions in non player battles
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
								if (hpRatio > .15) {
									extra.println(p.getName() + " looks close to death.");
								}else {
									if (hpRatio > .05) {
										extra.println(p.getName() + " looks like they're dying.");
									}else {
										extra.println(p.getName() + " is at death's door.");
									}
								}
							}
						}
					}
				}
			}
		}*/
		
		
		/*
		if (attacker.hasSkill(Skill.HPSENSE) || defender.hasSkill(Skill.HPSENSE)) {
			extra.println(defender.getHp()+"/" + defender.getMaxHp() );
		}*/
		if (defender.hasSkill(Skill.RACIAL_SHIFTS)) {
			RaceID rid = defender.getBag().getRaceID();
			switch (rid) {
			case B_MIMIC_CLOSED:
				if (hpRatio > .6f) {//if healthy, prefer closed, if damaged, prefer open and swap more in general
					if (extra.randFloat() < .2) {
						defender.getBag().setRace(RaceID.B_MIMIC_OPEN);
					}
				}else {
					if (extra.randFloat() < .9) {
						defender.getBag().setRace(RaceID.B_MIMIC_OPEN);
					}
				}
				Networking.clearSide(1);
				defender.getBag().graphicalDisplay(1,defender);
				break;
			case B_MIMIC_OPEN:
				if (hpRatio > .6f) {//if healthy, prefer closed, if damaged, prefer open and swap more in general
					if (extra.randFloat() < .6) {
						defender.getBag().setRace(RaceID.B_MIMIC_CLOSED);
					}
				}else {
					if (extra.randFloat() < .5) {
						defender.getBag().setRace(RaceID.B_MIMIC_CLOSED);
					}
				}
				Networking.clearSide(1);
				defender.getBag().graphicalDisplay(1,defender);
				break;
			case B_REAVER_TALL:
				defender.getBag().setRace(RaceID.B_REAVER_SHORT);
				defender.updateRaceWeapon();
				break;
			case B_REAVER_SHORT:
				defender.getBag().setRace(RaceID.B_REAVER_TALL);
				defender.updateRaceWeapon();
				break;
			}
		}
		//TODO: bleedout death quotes
		float leech = (defender.hasEffect(Effect.B_MARY) ? 2 : 0) + (defender.hasSkill(Skill.BLOODDRINKER) ? 0.5f : 0);
		int baseBleedDam = bleedDam(attacker,defender);
		int leechNum = (int)(leech * baseBleedDam);
		int totalBleed = 0;
		if (attacker.hasEffect(Effect.BLEED)) {
			attacker.takeDamage(baseBleedDam);
			totalBleed+=baseBleedDam;
			if (leech > 0) {
				defender.addHp(leechNum);
			}
		}
		int effectCount = attacker.effectCount(Effect.I_BLEED);
		if (effectCount > 0) {
			attacker.takeDamage(effectCount*baseBleedDam);
			totalBleed+=baseBleedDam*effectCount;
			if (leech > 0) {
				defender.addHp(effectCount*leechNum);
			}
		}
		if (attacker.hasEffect(Effect.MAJOR_BLEED)) {
			attacker.takeDamage(baseBleedDam);
			totalBleed+=baseBleedDam;
			if (leech > 0) {
				defender.addHp(leechNum);
			}
		}
		if (totalBleed > 0 && !extra.getPrint()) {
			if (leech > 0) {
				extra.println(defender.getName() + " heals off of "
				+ attacker.getNameNoTitle() + "'s blood, healing " + (totalBleed*leech) + " HP off of "
				+ (totalBleed) + " damage!");
			}else {
				extra.println(attacker.getName() + " bleeds for " + totalBleed + " HP.");
			}
		}
		if (attacker.hasEffect(Effect.BEES) && extra.chanceIn(1,5)) {
			int bee_damage = extra.randRange(1,IEffectiveLevel.cleanLHP(attacker.getLevel(), .04));
			if (!extra.getPrint()) {
				extra.println("The bees sting "+attacker.getName()+" for "+bee_damage+"!");
			}
			attacker.takeDamage(bee_damage);
		}
		
		attacker.getBag().turnTick();//for now, just armor buff decay
		
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
		
		return atr;
	}
	
	public static int bleedDam(Person attacker2, Person defender2) {
		if (attacker2 == null) {
			return IEffectiveLevel.cleanLHP(defender2.getLevel(),.02);
		}
		return IEffectiveLevel.cleanLHP(Math.min(defender2.getLevel(),attacker2.getLevel()+2),.02);
		/*
		return //hp is effective level * 10
				//here we want 5% damage per bleed unit at max
				//so we don't times it by 10 and instead divide by 2
				(int)//cast to int
				Math.ceil(IEffectiveLevel.effective(Math.min(attacker2.getLevel(),defender2.getLevel()*2))/2f);*/
	}
	
	private String inflictWound(Person attacker2, Person defender2, AttackReturn retu, Wound w) {
			ImpairedAttack attack = attacker2.getNextAttack();
			Integer[] nums = woundNums(attack,attacker2,defender2,retu);
			boolean notFromAttack = false;
			if (w == null) {
				w = attack.getWound();
			}else {
				notFromAttack = true;//still uses attack's damage and such to apply
			}
			if (w == null) {
				throw new RuntimeException("inflicting null wound");
			}
			assert defender2 != null;
			switch (w) {
			case CONFUSED:
				attacker2.addEffect(Effect.CONFUSED_TARGET);
				//newTarget = true;
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
			case FROSTED:
				defender2.applyDiscount(-Math.min(50,defender2.getTime()*(nums[0]/100f)));
				break;
			case DIZZY: case BLINDED:
				if (defender2.isAttacking()) {
					defender2.getNextAttack().multiplyHit(1-(nums[0]/10f));
				}
				break;	
			case MAJOR_BLEED:
				if (!defender2.hasEffect(Effect.CLOTTER)) {
					defender2.addEffect(Effect.BLEED);
					defender2.addEffect(Effect.MAJOR_BLEED);//never inflicted without also inflicting normal bleed
				}
				break;
			case BLEED:
				if (!defender2.hasEffect(Effect.CLOTTER)) {
					defender2.addEffect(Effect.BLEED);
				}
				break;
			case DISARMED: case SCREAMING:
				defender2.addEffect(Effect.DISARMED);
				break;
			case KO:
				defender2.takeDamage(nums[0]);
				defender2.addEffect(Effect.RECOVERING);
				break;
			case I_BLEED:
				if (!defender2.hasEffect(Effect.CLOTTER)) {
					defender2.addEffect(Effect.I_BLEED);
				}
				break;
			case TEAR:
				defender2.addEffect(Effect.TORN);
				break;
			case BLOODY:
				defender2.addEffect(Effect.BLEED);
				if (defender2.isAttacking()) {
					defender2.getNextAttack().multiplyHit(1-(nums[0]/10f));
				}
				break;
			case MANGLED:
				defender2.multBodyStatus(attack.getTargetSpot(), 1-(nums[0]/10f));
				break;

			}
			if (w != Wound.GRAZE && !notFromAttack) {
				if (attack.getWeapon() != null && attack.getWeapon().hasQual(Weapon.WeaponQual.DESTRUCTIVE)) {
					defender2.getBag().damageArmor((retu.damage/defender2.getMaxHp())/3f, attack.getSlot());
				}
			}
			return (" " +w.active);
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
	public static Integer[] woundNums(ImpairedAttack attack, Person attacker, Person defender, AttackReturn result) {
		switch (attack.getWound()) {
		case CONFUSED: case SCREAMING: case GRAZE: case DISARMED:
			//nothing
			return new Integer[0];
		case SLICE:
			return new Integer[] {10,10};//10% faster, 10% more accurate
		 case DICE:
			 return new Integer[] {10,10};//10% faster, 10 time units faster
		case HACK:
			if (result == null) {
				return new Integer[] {(int)attack.getTotalDam()/10};
			}
			return new Integer[] {result.damage/10};
		case TAT:
			if (result == null) {
				return new Integer[] {(int)(attack.getPierce()*extra.clamp(attack.getHitMult(),.5f,3f)/3f)};//this is 'up to'
			}
			return new Integer[] {(int)(result.subDamage[2] *extra.clamp(attack.getHitMult(),.5f,3f)/3f)};
		case CRUSHED:
		case SCALDED: case FROSTBITE:
			return new Integer[] {(int)attack.getTotalDam()/10};
		case BLINDED:
			return new Integer[] {50};//50% less accurate
		case HAMSTRUNG:
			return new Integer[] {8};//-8 time units
		case DIZZY:
			return new Integer[] {25};//25% less accurate
		case FROSTED:
			return new Integer[] {30,50};//takes 30% longer of current time, cap of +50
		case WINDED:
			return new Integer[] {16};//-16 time units
		case TRIPPED:
			return new Integer[] {20};//-20 time units
		case KO:
			return new Integer[] {IEffectiveLevel.cleanLHP(defender.getLevel(),.05)};
		case BLEED: case I_BLEED://bleeds aren't synced, WET :(
			return new Integer[] {bleedDam(attacker,defender),bleedDam(null,defender)};//can take null attacker
		case MAJOR_BLEED:
			//shows combined with base bleed that is applied at the same time
			//first number shows base value, second shows expected total
			return new Integer[] {bleedDam(attacker,defender),2*bleedDam(null,defender)};//can take null attacker
		case TEAR://WET
			return new Integer[] {10};// %, multiplicative dodge mult penalty
		case MANGLED:
			return new Integer[] {50};//50% reduction in condition
		case BLOODY://bleeds aren't synced, WET :(
			return new Integer[] {50,bleedDam(attacker,defender),bleedDam(null,defender)};//bloody blind
		}
		return new Integer[0];
	}

	/*
	private void handleMagicSpell(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		extra.println(extra.PRE_ORANGE + att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));
		if  (att.getSkill() == Skill.ELEMENTAL_MAGE) {
			//defender.inflictWound(att.getWound());//DOLATER readd elemental wounds- but prefer to just make those normal attacks instead, I don't store those so they can have more fields now
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
			defender.getNextAttack().multPotencyMult(att.getSharp()/100);
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
			//newTarget = true;
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
	}*/


	private void setAttack(Person attacker, Person defender) {
		//manOne.setAttack(AIClass.chooseAttack(manOne.getStance().part(manOne, manTwo),manOne.getIntellect(),this,manOne,manTwo));
		ImpairedAttack newAttack;
		int weapAttCount = attacker.nextWeaponAttacksCount();
		int totalAttCount = weapAttCount;
		List<ImpairedAttack> atts =null;
		if (attacker.getSuper() != null) {
			SuperPerson sp = attacker.getSuper();
			int cap = sp.getSAttCount();//how many skill configs they have
			if (cap > 0) {
				int skillAttCount = attacker.specialAttackNum();
				totalAttCount+=skillAttCount;
				totalAttCount = Math.min(7, totalAttCount);
				int conversions = Math.min(weapAttCount,attacker.getBag().getHand().getMartialStance().getBonusSkillAttacks());
				weapAttCount-=conversions;
				skillAttCount+=conversions;
				atts = (attacker.getStance().randAtts(weapAttCount,attacker.getBag().getHand(),attacker,defender));
				SkillAttackConf[] conf = sp.getSpecialAttacks();
				//we currently allow them to potentially fill more attacks this way if disarmed
				for (int i = 0; i < skillAttCount;i++) {
					atts.add(conf[i%cap].randAttack(attacker, defender));
				}
			}
		}
		if (atts == null) {
			atts = (attacker.getStance().randAtts(totalAttCount,attacker.getBag().getHand(),attacker,defender));
		}
			
		
		newAttack = AIClass.chooseAttack(atts,this,attacker,defender);
		attacker.setAttack(newAttack);
		newAttack.setDefender(defender);
	}
	
	/**
	 * 2 = player survived
	 * <br>
	 * 1 = player did not survive, but their side won
	 * <br>
	 * -1 = player died and their side lost
	 */
	public int playerWon() {
		if (survivors.contains(Player.player.getPerson())) {
			return 2;
		}
		if (totalFighters > 2) {
			if (inSides.get(winSide).contains(Player.player.getPerson())) {
				return 1;
			}
			return -2;
		}else {
			return -2;
		}
	}
	
	public List<Person> getNonSummonSurvivors(){
		List<Person> list = new ArrayList<Person>();
		survivors.stream().filter(s -> !s.getFlag(PersonFlag.IS_SUMMON)).forEach(list::add);
		assert Combat.hasNonNullBag(list);
		return list;
	}
	public Stream<Person> streamAllSurvivors(){
		return survivors.stream();
	}
	public int getVictorySide() {
		return winSide;
	}
	public List<Person> getAllSurvivors() {
		return survivors;
	}
	
	public static final String tagDefenderHP = "[HD]";
	public static final String tagAttackerHP = "[HA]";
	public static final String tagDefaultColor = "[C]";
	
	public static final String quotedDefenderHP = Pattern.quote("[HD]");
	public static final String quotedAttackerHP = Pattern.quote("[HA]");
	public static final String quotedDefaultColor = Pattern.quote("[C]");
	
	public static String prettyHPColors(String baseString, String normalColor, Person attacker,Person defender) {
		return normalColor 
				+ baseString
				.replaceAll(quotedDefenderHP,defender.inlineHPColor())
				.replaceAll(quotedAttackerHP, attacker.inlineHPColor())
				.replaceAll(quotedDefaultColor, normalColor);
	}
	
	public static String prettyHPDamage(float damagePerOfMax) {
		String res;
		switch ((int)Math.ceil((damagePerOfMax*100)/25)) {
		case 0:
			res = extra.DAM_I_NONE;
			break;
		case 1: 
			res = extra.DAM_I_SOME;
			break;
		case 2:
			res = extra.DAM_I_HEAVY;
			break;
		default: 
			res = extra.DAM_I_KILL;
			break;
		}
		return extra.inlineColor(extra.colorMix(Color.white, Color.red,extra.clamp(damagePerOfMax,0,1f)))+ extra.padIf(res);
	}
	public static String prettyHPDamage(float damage, Person defender) {
		return prettyHPDamage(damage/defender.getMaxHp());
	}
	
	public String prettyHPIndex(String str) {
		StringBuilder builder = new StringBuilder(str);
		for(;;) {
			int index = builder.indexOf("[HPT");//don't need regex maybe
			if (index > 0) {
				int subindex = builder.indexOf("]", index+4);
				int spot = Integer.parseInt(builder.substring(index+4, subindex));
				builder.replace(index, subindex+1,completeList.get(spot).inlineHPColor());
			}else {
				break;
			}
		}
		return builder.toString();
	}

	public static boolean hasNonNullBag(List<Person> people) {
		for (Person p: people) {
			Inventory bag = p.getBag();
			if (bag.getHand() == null) {
				throw new RuntimeException(p.getName() + " missing weapon");
			}
			for (int i = 0; i < 5;i++) {
				Armor a = bag.getArmor()[i];
				if (a == null) {
					throw new RuntimeException(p.getName() + " missing armor in slot "+i);
				}
			}
		}
		return true;
	}
	
		
}
