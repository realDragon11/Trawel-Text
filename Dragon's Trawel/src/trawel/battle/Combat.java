package trawel.battle;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import trawel.AIClass;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.attacks.ImpairedAttack;
import trawel.battle.attacks.TargetFactory;
import trawel.battle.attacks.TargetFactory.BloodType;
import trawel.battle.attacks.Wound;
import trawel.factions.FBox;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.Person.PersonType;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.Skill;
import trawel.personal.classless.SkillAttackConf;
import trawel.personal.item.DummyInventory;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race.RaceType;
import trawel.personal.item.body.SoundBox;
import trawel.personal.item.magic.EnchantHit;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Armor.ArmorQuality;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponQual;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.CleanseSideQuest;
import trawel.quests.Quest.TriggerType;
import trawel.towns.World;
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
	
	public static String indent = " ";
	
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
		
		BattleData data = new BattleData();
		
		dataMap = new HashMap<Person,BattleData>();
		data.side = 0;
		dataMap.put(manOne,data);
		data = new BattleData();
		data.side = 1;
		dataMap.put(manTwo,data);

		attacker = manOne;
		defender = manTwo;

		boolean playerIsInBattle;//= attacker.isPlayer() || defender.isPlayer();
		if (manOne.isPlayer()) {
			if (mainGame.displayTargetSummary) {
				manTwo.displayStats(true);
			}
			playerIsInBattle = true;
		}else {
			if (manTwo.isPlayer()) {
				if (mainGame.displayTargetSummary) {
					manOne.displayStats(true);
				}
				playerIsInBattle = true;
			}else {
				playerIsInBattle = false;
			}
		}

		attacker = manTwo;
		defender = manOne;
		setAttack(manTwo,manOne);
		setAttack(manOne,manTwo);
		if (!extra.getPrint()) {
			extra.println("");
			extra.println(extra.choose("Our two fighters square off...","They look tense.","It's time to fight.","They look ready to fight.","The battle has begun."));
			
		}
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
			
			handleTurn(attacker, defender, playerIsInBattle, delay);
			if (playerIsInBattle) {
				manTwo.getBag().graphicalDisplay(1,manTwo);
				Player.player.getPerson().getBag().graphicalDisplay(-1,Player.player.getPerson());
			}

			if (manOne.isAlive() && manTwo.isAlive()) {
				if (!attacker.isAttacking()){
					setAttack(attacker,defender);
				}

			}
		}while(manOne.isAlive() && manTwo.isAlive());

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
		killData(defender,attacker);
		if (!extra.getPrint()) {
			extra.println(extra.choose("The dust settles...","The body drops to the floor.","Death has come.","The battle is over."));
			extra.println(defender.getName() + extra.choose(" lies dead..."," walks the earth no more..."," has been slain."));
		}
		survivors = Collections.singletonList(attacker);
		killed = Collections.singletonList(defender);
		killList = killed;
		if (attacker.isPlayer() && defender.cleanseType != -1) {
			Player.player.questTrigger(TriggerType.CLEANSE,CleanseSideQuest.CleanseType.values()[defender.cleanseType].trigger, 1);
		}
	}
	
	public static class SkillCon {
		public SkillBase base;
		/**
		 * power should range from 1 to 100, hard caps at 100
		 */
		public final float power;
		public float timer, resetTime;
		public int sideSource;
		public SkillCon(SkillBase _base,float _power, int _timer, int _resetTime, int _side) {
			base = _base;
			power = Math.min(100,_power);
			timer = _timer;
			resetTime = _resetTime;
			sideSource = _side;
		}
		
		public SkillCon(SubSkill skill, float _power, int side) {
			power = Math.min(100,_power);
			switch (skill) {
			case DEATH:
				base = SkillBase.WITHER;
				timer = 50;
				resetTime = 100;
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
				base = SkillBase.SCRY_OR_WATCH;
				timer = 100;
				resetTime = 250;
				break;
			case SMITHING:
				base = null;
				break;
			case WATCH://cheaper to get, first time happens sooner but doesn't repeat as much
				base = SkillBase.SCRY_OR_WATCH;
				timer = 50;
				resetTime = 1000;
				break;
			case FATE:
				base = SkillBase.FATED;
				timer = 50;
				resetTime = 110;
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
		FIREBALLS, WITHER, SCRY_OR_WATCH, BLOCKADE, FATED
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
	
	public void handleSkillCons(List<SkillCon> cons,double timePassed) {
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
					float mult;
					if (sk.power > 50) {
						mult = extra.lerp(.7f,.5f,(sk.power-50f)/50f);
					}else {
						mult = extra.lerp(1f,.7f,sk.power/50f);
					}
					for (int i = 0; i < liveLists.size();i++) {
						if (i == sk.sideSource) {
							continue;
						}
						for (Person p: liveLists.get(i)) {
							if (p.isAttacking()) {
								p.getNextAttack().multPotencyMult(mult);
							}
							p.takeDamage(IEffectiveLevel.cleanLHP(p.getLevel(),.01));
						}
					}
					break;
				case FIREBALLS:
					if (!extra.getPrint()) {
						extra.println("The very air beings to boil! Armor melts away!");
					}
					float burnMult;
					if (sk.power > 50) {
						burnMult = extra.lerp(.2f,.3f,(sk.power-50f)/50f);
					}else {
						burnMult = extra.lerp(0,.2f,sk.power/50f);
					}
					for (int i = 0; i < liveLists.size();i++) {
						if (i == sk.sideSource) {
							continue;
						}
						for (Person p: liveLists.get(i)) {
							p.takeDamage(IEffectiveLevel.cleanLHP(p.getLevel(),.02));
							p.getBag().burnArmor(burnMult);
						}
					}
					break;
				case SCRY_OR_WATCH:
					if (!extra.getPrint()) {
						extra.println("A path is seen!");
					}
					float timeDiscount;
					if (sk.power > 50) {
						timeDiscount = extra.lerp(20f,30f,(sk.power-50f)/50f);
					}else {
						timeDiscount = extra.lerp(0f,20f,sk.power/50f);
					}
					for (Person p: liveLists.get(sk.sideSource)) {
						p.applyDiscount(timeDiscount);
					}
					break;
				case BLOCKADE:
					if (!extra.getPrint()) {
						extra.println("The barricades are holding!");
					}
					float holdTime;
					if (sk.power > 50) {
						holdTime = extra.lerp(80f,120f,(sk.power-50f)/50f);
					}else {
						holdTime = extra.lerp(10f,80f,sk.power/50f);
					}
					for (int i = 0; i < liveLists.size();i++) {
						if (i == sk.sideSource) {
							continue;
						}
						for (Person p: liveLists.get(i)) {
							p.applyDiscount(-holdTime);
						}
					}
					break;
				case FATED:
					List<Person> list = liveLists.get(sk.sideSource);
					if (!list.isEmpty()) {
						float fateTime;
						if (sk.power > 50) {
							fateTime = extra.lerp(35f,50f,(sk.power-50f)/50f);
						}else {
							fateTime = extra.lerp(5f,35f,sk.power/50f);
						}
						Person p = extra.randList(list);
						if (!extra.getPrint()) {
							extra.println("It looks like fate is on " + p.getNameNoTitle()+"'s side!");
						}
						p.advanceTime(fateTime);
						p.addEffect(Effect.ADVANTAGE_STACK);//stack of advantage
					}
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
	
	private class BattleData{//TODO: it'd probably be better to move battle data to a transient variable in people
		public int side;
		public Person lastAttacker = null;
		public Person nextTarget = null;
	}
	
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
				if (mainGame.displayTargetSummary) {
					otherperson.displayStats(true);
				}
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
				this.handleSkillCons(cons, lowestDelay);
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
							defender.removeEffectAll(Effect.CONFUSED_TARGET);
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
					quickest.removeEffectAll(Effect.CONFUSED_TARGET);
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
		
		survivors = tempList;
		killed = killList;
		
		assert survivors.size() > 0;
		
		if (playerIsInBattle && Player.player.hasTrigger("cleanse")) {
			CleanseSideQuest.CleanseType[] vals = CleanseSideQuest.CleanseType.values();
			for (Person p: killList) {
				if (p.cleanseType != -1) {
					//TODO: really scuffed how many data types the triggers is converted between
					Player.player.questTrigger(TriggerType.CLEANSE,vals[p.cleanseType].trigger, 1);
				}
			}
		}
	}
	
	public Combat() {
		//empty for tests
	}
	
	private Person getDefenderFor(Person attacker) {
		BattleData data = dataMap.get(attacker);
		if (data.nextTarget != null) {
			Person t = data.nextTarget;
			data.nextTarget = null;
			return t;
		}
		return extra.randList(targetLists.get(data.side));
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
					extra.println(extra.RESULT_WARN+dead.getName() + " curses the name of " +killer.getNameNoTitle()+", but they seem unaffected.");
				}
			}else {
				if (!extra.getPrint()) {
					extra.println(extra.RESULT_WARN+dead.getName() + " curses the name of " +killer.getNameNoTitle()+"!");
				}
				killer.addEffect(Effect.CURSE);
			}
		}
		if (killer.hasSkill(Skill.CONDEMN_SOUL)) {
			if (dead.hasSkill(Skill.NO_HOSTILE_CURSE)) {
				if (!extra.getPrint()) {
					extra.println(extra.RESULT_WARN+killer.getName() + " condemns the soul of " +dead.getNameNoTitle()+", but they seem unaffected.");
				}
			}else {
				if (!extra.getPrint()) {
					extra.println(extra.RESULT_WARN+killer.getName() + " condemns the soul of " +dead.getNameNoTitle()+"!");
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
		if (killer.hasSkill(Skill.NO_QUARTER)) {
			killer.addEffect(Effect.PLANNED_TAKEDOWN);
			killer.addEffectCount(Effect.ADVANTAGE_STACK,2);
			if (!extra.getPrint()) {
				extra.println(killer.getName() + " will give no quarter!");
			}
		}
		FBox.repCalc(killer,dead);
	}
	
	/**
	 * Redone, write new docs
	 */
	public AttackReturn handleAttack(boolean isReal, ImpairedAttack att, Inventory def,Inventory off, Person attacker, Person defender) {
		String str = "";
		AttackReturn ret;
		
		boolean wasDead = false;
		int wLevel = att.getLevel();
		boolean canDisp = isReal && !extra.getPrint();
		
		if (att.isTacticOnly()) {
			ret= new AttackReturn(ATK_ResultCode.NOT_ATTACK,"",att,null);
			if (canDisp) {
				ret.stringer = att.fluff(ret);
				ret.putPlayerFeedback(ret);
			}
			return ret;
		}
		String preNotes = null;
		if (off == null) {
			off = DummyInventory.dummyAttackInv;
		}
		if (canDisp) {
			if (extra.chanceIn(1, 4)) {
				Networking.send("PlayDelayPitch|"+SoundBox.getSound(off.getRace().voice,SoundBox.Type.SWING) + "|1|" +attacker.getPitch() +"|");
			}
			if (!defender.isAlive()) {
				wasDead = true;//note, can move out if need be
			}
		}
		if (defender.hasSkill(Skill.COUNTER)) {
			defender.applyDiscount(2);
		}
		
		double dodgeBase = def.getDodge()*defender.getWoundDodgeCalc();
		double hitBase = att.getHitMult();
		
		double dodgeRoll = dodgeBase*extra.getRand().nextDouble();
		double hitRoll = hitBase*extra.getRand().nextDouble();
		
		if (defender.hasEffect(Effect.DUCKING) || defender.hasEffect(Effect.ROLLING)) {
			dodgeRoll+=0.2;
		}
		if (attacker != null && attacker.hasEffect(Effect.MIASMA)) {
			if (defender.hasSkill(Skill.FETID_FUMES)) {
				hitRoll*=0.9;
			}
			if (isReal) {
				//ticks here to avoid weird removal cases
				attacker.setEffectCount(Effect.MIASMA,attacker.effectCount(Effect.MIASMA)/2);
			}
		}
		if (isReal) {
			if (attacker.hasEffect(Effect.SHAKY)) {
				//halve shaky stacks on swing
				attacker.setEffectCount(Effect.SHAKY,attacker.effectCount(Effect.SHAKY)/2);
			}
			if (attacker.hasEffect(Effect.ADVANTAGE_STACK)) {
				hitRoll*=1.2;
				attacker.removeEffect(Effect.ADVANTAGE_STACK);
			}
			if (defender.hasEffect(Effect.ADVANTAGE_STACK)) {
				dodgeRoll*=1.2;
				defender.removeEffect(Effect.ADVANTAGE_STACK);
			}
			//skill code that applies on all attacks, not just hits
			if (defender.hasSkill(Skill.FETID_FUMES)) {
				//hit reduction applied above in all cases, not just real attacks
				if (defender.contestedRoll(attacker,defender.getClarity()/2,attacker.getClarity()) >= 0){
					attacker.addEffect(Effect.MIASMA);
					if (canDisp) {
						preNotes = addPreNote(preNotes,"The fumes fester around "+attacker.getNameNoTitle()+"!");
					}
				}
			}
			if (attacker.hasSkill(Skill.FEVER_STRIKE)) {
				if (attacker.contestedRoll(defender,attacker.getClarity(),defender.getClarity()) >= 0){
					defender.addEffectCount(Effect.MIASMA,2);
					if (canDisp) {
						preNotes = addPreNote(preNotes,"Sickness hangs in the air around "+defender.getNameNoTitle()+"!");
					}
				}
			}
		}else {
			if (attacker != null) {
				//modifiers for testing attacks that don't consume stacks
				if (attacker.hasEffect(Effect.ADVANTAGE_STACK)) {
					hitRoll*=1.2;
				}
				if (defender.hasEffect(Effect.ADVANTAGE_STACK)) {
					dodgeRoll*=1.2;
				}
			}
		}
		if (hitRoll <= defender.getMissCalc()) {
			ret= new AttackReturn(ATK_ResultCode.MISS,"",att,preNotes);
			if (canDisp) {
				ret.stringer = att.fluff(ret);
				ret.putPlayerFeedback(ret);
				if (wasDead) {
					ret.addNote("They missed a corpse!");
				}
			}
			return ret;
		}
		//parry is applied
		if (defender.hasEffect(Effect.PARRY)) {
			//since we check to see if we have parry first, we can afford to do a more expensive POW calc to compound it
			dodgeRoll *= Math.pow(1.2d,defender.effectCount(Effect.PARRY));
		}
		
		
		//could move dodge rolls here, but would have to split advantage code
		if (dodgeRoll > hitRoll){
			ret= new AttackReturn(ATK_ResultCode.DODGE,"",att,preNotes);
			if (canDisp) {
				ret.stringer = att.fluff(ret);
				ret.putPlayerFeedback(ret);
				if (wasDead) {
					ret.addNote("What a dodgy corpse!");
				}
			}
			return ret;
		}
		
		ret = new AttackReturn(att,def,str,preNotes);
		if (canDisp && ret.code != ATK_ResultCode.NOT_ATTACK) {
			//Networking.send("PlayHit|" +def.getSoundType(att.getSlot()) + "|"+att.getAttack().getSoundIntensity() + "|" +att.getAttack().getSoundType()+"|");
			//possibly play louder if crit roll, possibly play softer if armor deflect
			Networking.playHitConnect(att,def,hitRoll > dodgeRoll*3, ret.code == ATK_ResultCode.ARMOR);
			if (wasDead) {
				ret.addNote("Beating their corpse!");
			}
		}
		int eHalfLevel = (int) (IEffectiveLevel.effective(wLevel)/2);
		if (ret.type == ATK_ResultType.IMPACT) {//normal damage and killing
			if (att.hasWeaponQual(WeaponQual.REFINED)) {
				ret.damage += eHalfLevel;
				//ret.bonus += eHalfLevel;
				if (canDisp) {ret.addNote("Refined Bonus: " + eHalfLevel);}
			}
			if (att.hasWeaponQual(Weapon.WeaponQual.WEIGHTED)) {
				if (att.getHitMult() < 1.5) {
					int weightBonus = ret.damage;
					//TODO: check this
					ret.damage = (int) Math.round(ret.damage*Math.log10(5+(20-(att.getHitMult()*10))));
					weightBonus = ret.damage-weightBonus;
					//ret.bonus += weightBonus;
					if (canDisp) {ret.addNote("Weighted Bonus: " + weightBonus);}
				}
			}
			
			//attacker can be null for dummy attacks?
			//on crits
			if (attacker != null && hitRoll > dodgeRoll*3) {
				if (attacker.hasSkill(Skill.DEADLY_AIM)) {
					int deadlyBonus = (int)(ret.damage*0.2);
					ret.damage += deadlyBonus;
					//ret.bonus += deadlyBonus;
					if (canDisp) {
						ret.addNote("Deadly Bonus: " + deadlyBonus);
					}
				}
				if (ret.type == ATK_ResultType.IMPACT && attacker.hasSkill(Skill.RUNIC_BLAST)
						&& attacker.getBag().getHand().isEnchantedHit()) {
					EnchantHit rune = (EnchantHit) attacker.getBag().getHand().getEnchant();
					//only picks one
					Wound runeWound = null;
					if (rune.getFireMod() > 0) {//if ignite enchanted
						runeWound =  extra.randList(TargetFactory.fireWounds);
					}else {
						if (rune.getFreezeMod() > 0) {//if frost enchanted
							runeWound =  extra.randList(TargetFactory.freezeWounds);
						}else {
							if (rune.getShockMod() > 0) {//if elec enchanted
								runeWound =  extra.randList(TargetFactory.shockWounds);
							}//else fails
						}
					}
					if (runeWound != null) {
						if (canDisp) {
							ret.addNote("Runic Blast!");
						}
						ret.addBonusWound(runeWound);
					}	
				}
				//real only stack applying below
				if (isReal) {
					if (attacker.hasSkill(Skill.PRESS_ADV)) {
						attacker.addEffect(Effect.ADVANTAGE_STACK);
						if (canDisp) {
							ret.addNote("Pressing the advantage!");
						}
					}
					if (attacker.hasSkill(Skill.AGGRESS_PARRY)) {
						attacker.addEffect(Effect.PARRY);
						if (canDisp) {
							ret.addNote("Parry setup!");
						}
					}
					if (ret.type == ATK_ResultType.IMPACT && attacker.hasSkill(Skill.OPEN_VEIN)) {
						if (defender.hasEffect(Effect.MAJOR_BLEED)) {
							int bStacks = bleedStackAmount(attacker, defender);
							if (defender.hasEffect(Effect.CLOTTER)) {
								bStacks = 0;//clotter makes applied stacks 0 to show something is weird
							}
							defender.addEffectCount(Effect.BLEED,bStacks);
							if (canDisp) {
								ret.addNote("Cut vein: " +bStacks + " stacks!");
							}
						}else {
							defender.addEffect(Effect.MAJOR_BLEED);//add no bleed recovery
							if (canDisp) {
								ret.addNote("Opened vein!");
							}
						}
					}
					if (ret.type == ATK_ResultType.IMPACT && attacker.hasSkill(Skill.SALVAGE)) {
						defender.getBag().buffArmorAdd(0.12d);
						if (canDisp) {
							ret.addNote("Salvaged armor!");
						}
					}
				}
				
			}
			
			if (defender.hasSkill(Skill.RAW_GUTS)) {
				int gResist = IEffectiveLevel.cleanLHP(
						Math.ceil(defender.getLevel() * defender.getConditionForPart(TargetFactory.TORSO_MAPPING))
						,.03);
				int gResisted = ret.damage;
				ret.damage = Math.max(ret.damage/2,ret.damage-gResist);
				gResisted = gResisted-ret.damage;
				if (canDisp) {ret.addNote("Raw Guts Resisted: " + gResisted);}
			}
		}
		if (att.hasWeaponQual(WeaponQual.RELIABLE) && ret.damage < eHalfLevel) 
		{
			//ret.bonus = eHalfLevel-ret.damage;
			ret.damage = eHalfLevel;
			if (canDisp) {ret.addNote("Reliable Damage: "+eHalfLevel);}
		}
		if (canDisp) {
			ret.stringer = str + att.fluff(ret);
			ret.putPlayerFeedback(ret);
		}
		return ret;
	}
	
	/**
	 * only use before attack return is created, and must store the return in prenotes
	 */
	public static String addPreNote(String notes, String add) {
		if (notes == null) {
			notes = add;
		}else {
			notes +="\n "+add;
		}
		return notes;
	}
	
	public static AttackReturn handleTestAttack(ImpairedAttack att, Person p) {
		return Combat.testCombat.handleAttack(false,att,p.getBag(),null,null,p);	
	}
	
	public class AttackReturn {
		public int damage;
		public int[] subDamage;
		public String stringer;
		/**
		 * added to wound effects to help display
		 */
		public int bonus = 0;
		public ImpairedAttack attack;
		public ATK_ResultCode code;
		public ATK_ResultType type;
		private String notes;
		private List<Wound> addWounds = null;
		public AttackReturn(ImpairedAttack att, Inventory def, String str, String _notes) {
			if (att.isTacticOnly()) {//doesn't have true attack components
				stringer = str;
				attack = att;
				notes = _notes;
				subDamage = new int[] {0,0,0,0,0,0};
				damage = 0;
				code = ATK_ResultCode.NOT_ATTACK;
				type = ATK_ResultType.NO_IMPACT;
				return;
			}
			int sdam = att.getSharp();
			int bdam = att.getBlunt();
			int pdam = att.getPierce();
			int idam = att.getIgnite();
			int fdam = att.getFrost();
			int edam = att.getElec();
			int ddam = att.getDecay();
			
			double sarm = def.getSharp(att)*Armor.armorEffectiveness;
			double barm = def.getBlunt(att)*Armor.armorEffectiveness;
			double parm = def.getPierce(att)*Armor.armorEffectiveness;
			
			
			boolean bypass = att.getAttack().isBypass();
			
			stringer = str;
			attack = att;
			notes = _notes;
			//MAYBELATER: can turn this into an array of damage types later if need be
			double rawdam = bypass ? idam+fdam+edam : sdam+bdam+pdam;
			double rawarm =sarm+barm+parm;
			double s_weight = sdam/rawdam;
			double b_weight = bdam/rawdam;
			double p_weight = pdam/rawdam;
			double weight_arm = bypass ? rawarm/3 : (s_weight*sarm)+(b_weight*barm)+(p_weight*parm);
			
			double iarm = weight_arm;//*def.getIgniteMult(att.getSlot());
			double farm = weight_arm;//*def.getFrostMult(att.getSlot());
			double earm = weight_arm;//*def.getElecMult(att.getSlot());
			
			//double guess = ((rawdam+weight_arm)/weight_arm)-1;
			float def_roll = extra.lerp(.05f+(.05f*def.qualityCount(ArmorQuality.RELIABLE)),1f,extra.hrandomFloat());
			float att_roll = extra.lerp(.7f,1f,extra.hrandomFloat());
			double global_roll = (att_roll*rawdam)/(def_roll*weight_arm);
			//if our random damage roll was less than 40% of the armor roll, negate
			if (global_roll < .4+(.02*def.qualityCount(ArmorQuality.BLOCKING))) {
				subDamage = new int[] {0,0,0,0,0,0};
				damage = 0;
				code = ATK_ResultCode.ARMOR;
				type = ATK_ResultType.NO_IMPACT;
			}else {
				//% reductions based on relativeness
				//up to half the damage if the damage roll was less than the total weighted armor roll
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
				
				double d_reduct = damageCompMult(.2f,.9f,1f,ddam,weight_arm,2f,4f);
				int dcomp = (int) (ddam*reductMult*d_reduct);
				
				subDamage = new int[] {scomp,bcomp,pcomp,icomp,fcomp,ecomp,dcomp};
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
				
				if (!extra.getPrint() && mainGame.advancedCombatDisplay) {
					addNote("rawdam: " +rawdam +"("+sdam+"/"+bdam+"/"+pdam+ " " + idam+"/"+fdam+"/"+edam+")"
				+ " rawarm: " + rawarm + "("+sarm+"/"+barm+"/"+parm+" " +iarm+"/"+farm+"/"+earm+")"
							+ " weight_a: " + weight_arm + " groll: " + global_roll 
							+ " comps: " +subDamage[0] +"/"+subDamage[1]+"/"+subDamage[2] + " " + subDamage[3] +"/"+subDamage[4]+"/"+subDamage[5]+"/"+subDamage[6]
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
		 * @param weapon_threshold how many times weapon must be higher than armor to reach maxmult
		 * @param armor_threshold how many times armor must be higher than damage to reach minmult
		 */
		private float damageCompMult(float minMult, float equalMult, float maxMult, double inDam, double inArm, float weapon_threshold, float armor_threshold) {
			if (inDam >= inArm) {
				return extra.lerp(maxMult,equalMult,extra.clamp((float)(inArm/(inDam/weapon_threshold)),0f,1f));
			}
			return extra.lerp(equalMult,maxMult,extra.clamp((float)(inDam/(inArm/armor_threshold)),0f,1f));
		}
		
		public AttackReturn(ATK_ResultCode rcode, String str, ImpairedAttack att, String _notes) {
			type = ATK_ResultType.NO_IMPACT;
			code = rcode;
			damage = 0;
			//null
			stringer = str;
			attack = att;
			notes = _notes;
		}
		
		public AttackReturn() {
			code = ATK_ResultCode.NOT_ATTACK;
			type = ATK_ResultType.IMPACT;
		}
		
		public void addNote(String str) {
			if (notes == null) {
				notes = str;
			}else {
				notes +="\n "+str;
			}
		}
		
		public String getNotes() {
			return notes;
		}
		
		public void stringNotes() {
			if (notes != null) {
				stringer += "\n " + notes;
			}
		}
		
		public void putPlayerFeedback(AttackReturn atr) {
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
					Player.lastAttackStringer += "other";
					break;
				}
			}
		}
		public boolean hasWound() {
			return attack != null && attack.getWound() != null;
		}
		
		public void addBonusWound(Wound w) {
			if (addWounds == null) {
				addWounds = new ArrayList<Wound>();
			}
			addWounds.add(w);
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
	 * 
	 * @param attacker
	 * @param defender
	 * @return damagedone
	 */
	public AttackReturn handleTurn(Person attacker, Person defender,boolean canWait, double delay) {
		if (attacker.isOnCooldown()) {
			if (attacker.hasEffect(Effect.RECOVERING)) {//recovering doesn't stack, but there should only be one at a time
				attacker.removeEffect(Effect.RECOVERING);
				int recover_amount = IEffectiveLevel.cleanLHP(attacker.getLevel(), .1);
				attacker.healHP(recover_amount);
				if (!extra.getPrint()) {
					extra.println(indent +attacker.getName() + " recovers "+ recover_amount +" HP.");
				}
			}
			if (attacker.hasEffect(Effect.EXHAUSTED)) {
				attacker.removeEffect(Effect.EXHAUSTED);
			}
			if (attacker.hasEffect(Effect.ROLLING)) {
				attacker.removeEffectAll(Effect.ROLLING);
				attacker.addEffect(Effect.EXHAUSTED);
				if (!extra.getPrint()) {
					extra.println(indent+attacker.getName()+" finishes their roll.");
				}
			}
			if (attacker.hasEffect(Effect.DUCKING)) {
				attacker.removeEffectAll(Effect.DUCKING);
				attacker.addEffect(Effect.ROLLING);
				attacker.addEffect(Effect.BRISK);
				if (!extra.getPrint()) {
					extra.println(indent+attacker.getName()+" starts to roll.");
				}
			}
			
			attacker.finishTurn();
			
			if (attacker.hasEffect(Effect.BREATHING)) {//only uses one at a time
				attacker.removeEffect(Effect.BREATHING);
				attacker.addEffect(Effect.RECOVERING);
				if (!extra.getPrint()) {
					extra.println(indent +attacker.getName() + " is breathing heavily...");
				}
			}
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
				if (mainGame.debug) {
					for (Person p: completeList) {
						//killList is null in 1v1's, where everyone must be alive
						//System.out.println((killList != null && killList.contains(p)) ? "Dead: " : "Alive: ");
						p.debugCombatStats();
					}
				}
			}
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
					String bark = BarkManager.getBoast(attacker,true);
					if (bark != null) {
						extra.println(indent+bark);
					}
			}else {
				if (attacker.isPersonable() && //must but fully personable to be racist for now
						((attacker.isAngry() && defender.getBag().getRace().racialType == RaceType.BEAST) || (attacker.isRacist() && !attacker.getBag().getRace().equals(defender.getBag().getRace()))) && extra.chanceIn(1,3)) 
				{
					extra.println(indent+attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" +defender.getBag().getRace().randomInsult()+"\"");
				}else {
					String bark = BarkManager.getTaunt(attacker,defender);
					if (bark != null) {
						extra.println(indent+bark);
					}
				}				
			}
		}
		ImpairedAttack attack = attacker.getNextAttack();
		AttackReturn atr = handleAttack(true,attack,defender.getBag(),attacker.getBag(),attacker,defender);
		int damageDone = atr.damage+atr.bonus;
		float percent = 0f;
		if (damageDone > 0) {
			percent = damageDone/(float)defender.getMaxHp();
			//armor quality handling
			//defender.getBag().armorQualDam(percent);
			if (!extra.getPrint() && extra.chanceIn((int)(percent*140) + (defender.getHp() <= 0 ? 20 : 0), 120)) {
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
			double condDamage = percent;
			if (defender.hasEffect(Effect.HIT_VITALS)) {
				condDamage*=2;
				//if this was an impactful attack, and the cond left is nearly 0
				if (atr.type == ATK_ResultType.IMPACT && defender.getBodyStatus(attack.getTargetSpot()) < .01) {
					//roll a bonus wound
					atr.addBonusWound(defender.getAnyWoundForTargetSpot(attack.getTargetSpot()));
				}
			}
			defender.addBodyStatus(atr.attack.getTargetSpot(),-condDamage);
			
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
		String woundStr = "";
		//Wounds can now be inflicted even if not dealing damage
		boolean forceKilled = false;
		boolean doForceKill = false;
		if (atr.type == ATK_ResultType.IMPACT) {
			if (attacker.hasSkill(Skill.SPUNCH)) {
				defender.advanceTime(atr.attack.getTime()/50f);
			}
			if (atr.hasWound()) {
				boolean applied = inflictWound(attacker,defender,atr,attack.getWound());
				if (applied) {
					if (attack.getWeapon() != null && attack.getWeapon().hasQual(Weapon.WeaponQual.DESTRUCTIVE)) {
						defender.getBag().damageArmor(percent/3f, attack.getSlot());
					}
				}
			}
			if (attacker.hasEffect(Effect.PLANNED_TAKEDOWN)) {
				attacker.removeEffect(Effect.PLANNED_TAKEDOWN);
				//knockout
				inflictWound(attacker,defender,atr,Wound.KO);
			}
			if (atr.code == ATK_ResultCode.KILL) {//if force kill
				//might not actually be final death, but this is fine to say
				doForceKill = true;
				if (!extra.getPrint()) {
					atr.addNote("Execute!");
				}
				//extra.println(attacker.getName() + " executes " + defender.getName() +"!");
				if (atr.damage < defender.getHp()) {//if they won't be able to kill them
					defender.forceKill();
					forceKilled = true;
				}
			}
			if (attacker.hasSkill(Skill.BLOODTHIRSTY)) {
				;
				int blood_heal = attacker.healHP(
						IEffectiveLevel.cleanLHP(Math.min(defender.getLevel(),attacker.getLevel()),.01));
				if (!extra.getPrint()) {
					atr.addNote("Bloodthirst Heal: "+blood_heal);
					//extra.println(attacker.getName()+ " heals " + blood_heal + " from their bloodthirst!");
				}
			}
			if (attacker.hasSkill(Skill.NPC_BURN_ARMOR) && !attacker.hasEffect(Effect.DEPOWERED)) {
				//always burns 10% per attack
				defender.getBag().burnArmor(.1f,atr.attack.getSlot());
			}
			//wounded OOB punishment effect
			if (defender.hasEffect(Effect.WOUNDED)) {
				inflictWound(attacker,defender,atr,Wound.BLEED);
			}
			//only processes on an impactful attack, to be consistent with wounds (plus make code easier)
			List<Wound> wounds = defender.processBodyStatus();
			if (wounds != null) {
				for (Wound wo: wounds) {
					inflictWound(attacker,defender,atr,wo);
				}
			}
			
			//bonus wounds applied by skills and such
			if (atr.addWounds != null) {
				for (Wound wo: atr.addWounds) {
					inflictWound(attacker,defender,atr,wo);
				}
			}
		}else {//no impact
			if (defender.hasSkill(Skill.MESMER_ARMOR)) {
				if (defender.contestedRoll(attacker,defender.getClarity(),attacker.getHighestAttribute()) >= 0){
					attacker.addEffect(Effect.CONFUSED_TARGET);
					if (!extra.getPrint()) {
						atr.addNote("Mesmer Armor!");
						//extra.println(indent+defender.getName()+ "'s illusory armor mesmerizes "+attacker.getName() +"...");
					}
				}
			}
		}
		boolean didDisplay = false;
		if (damageDone > 0 || doForceKill) {
			defender.takeDamage(Math.max(1,atr.damage));//bonus has already been dealt
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
										+woundStr+" But they're made of sterner stuff!"
									, extra.ATTACK_DAMAGED, attacker, defender));
							didDisplay = true;
						}
					}
				}
				if (!didDisplay && !extra.getPrint()) {
					extra.print(
							prettyHPColors(atr.stringer+"[C] {"+prettyHPDamage(percent)+damageDone+" damage[C]}"
							+woundStr,extra.ATTACK_KILL, attacker, defender));
					didDisplay = true;
				}
			}else {
				if (!extra.getPrint()) {
					boolean armorDamage = atr.code == ATK_ResultCode.ARMOR;
					extra.print(prettyHPColors(atr.stringer+" {"+prettyHPDamage(percent)+damageDone+" damage[C]}"
							+woundStr
							+ (armorDamage ? " The armor curbs the blow!": "")
							,
							armorDamage ? extra.ATTACK_DAMAGED_WITH_ARMOR : extra.ATTACK_DAMAGED
							,attacker,defender));
					didDisplay = true;
				}
			}
		}
		//if (atr.type == ATK_ResultType.NO_IMPACT) {
		//impact is now more directly if wounds/special effects happen
		switch (atr.code) {
		case NOT_ATTACK:
			if (!extra.getPrint() && !didDisplay) {
				extra.print(prettyHPColors(atr.stringer +woundStr,extra.TIMID_MAGENTA, attacker, defender));
			}
			
			break;
		case DODGE: case MISS:
			if (!extra.getPrint() && !didDisplay) {
				extra.print(prettyHPColors(atr.stringer +woundStr,extra.ATTACK_MISS, attacker, defender));
				Networking.sendStrong("PlayMiss|" + "todo" + "|");
				extra.print(" "+extra.AFTER_ATTACK_MISS+randomLists.attackMissFluff(atr.code)+extra.ATTACK_MISS);
			}
			if (atr.code == ATK_ResultCode.DODGE) {
				if (defender.hasSkill(Skill.SPEEDDODGE)) {
					defender.applyDiscount(10);
					atr.addNote("Speed Dodge!");
					//extra.print(" They dodge closer to the action!");
				}
				if (defender.hasSkill(Skill.DODGEREF)) {
					int dodgeHeal = IEffectiveLevel.cleanLHP(Math.min(defender.getLevel()+2,attacker.getLevel()),.01);
					defender.addHp(dodgeHeal);
					atr.addNote("Refreshing Dodge: "+dodgeHeal);
					//extra.print(" Refreshing Dodge heals " + dodgeHeal +"!");
				}
				if (defender.hasSkill(Skill.REACTIVE_DODGE)) {
					defender.addEffect(Effect.ADVANTAGE_STACK);
					atr.addNote("Reactive Dodge!");
					//extra.print(" They dance to a better position!");
				}
			}
			
			if (defender.hasEffect(Effect.BEE_SHROUD)) {
				if (attacker.hasEffect(Effect.BEES)) {
					if (!extra.getPrint()) {
						extra.println(extra.inlineColor(extra.colorMix(Color.PINK,Color.WHITE,.2f))+" The bees buzz back!");
					}
				}else {
					attacker.addEffect(Effect.BEES);
					if (!extra.getPrint()) {
						extra.println(extra.inlineColor(extra.colorMix(Color.PINK,Color.WHITE,.2f))+" The bees swarm "+attacker.getName()+"!");
					}
				}
			}
			break;
		case ARMOR:
			if (!extra.getPrint() && !didDisplay) {
				extra.print(prettyHPColors(atr.stringer +woundStr,extra.ATTACK_BLOCKED, attacker, defender));
				extra.print(extra.AFTER_ATTACK_BLOCKED+" "+randomLists.attackNegateFluff()+extra.ATTACK_BLOCKED);
			}
			if (defender.hasSkill(Skill.ARMORHEART) && defender.getHp() < defender.getMaxHp()) {
				int armorHeal = IEffectiveLevel.cleanLHP(Math.min(defender.getLevel()+4,attacker.getLevel()),.02);
				defender.healHP(armorHeal);
				atr.addNote("Armor Heart: "+armorHeal);
				//extra.print(" Armor Heart heals " + armorHeal +"!");
			}
			if (defender.hasSkill(Skill.ARMORSPEED)) {
				defender.applyDiscount(10);
				atr.addNote("Glancing Blow!");
				//extra.print(" They advance closer to the action.");
			}
			if (defender.hasSkill(Skill.LIVING_ARMOR)) {
				defender.getBag().buffArmorAdd(.08d);
				atr.addNote("Living Armor!");
				//extra.print(" Their armor reacts to the blow.");
			}
			break;
		}
		
		
		if (!extra.getPrint()) {
			extra.println("");
			if (mainGame.combatFeedbackNotes) {
				if (atr.getNotes() != null) {
					extra.println(" "+atr.getNotes());
				}
			}
			if (canWait && mainGame.delayWaits) {
				Networking.waitIfConnected((long)delay*10);
			}
		}


		
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
		if (defender.hasSkill(Skill.RACIAL_SHIFTS)) {
			float hpRatio = ((float)defender.getHp())/(defender.getMaxHp());
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
		
		float bleed = 0;
		if (attacker.hasEffect(Effect.BLEED)) {
			int baseBleeds = attacker.effectCount(Effect.BLEED);
			bleed+= baseBleeds;
			if (!attacker.hasEffect(Effect.MAJOR_BLEED)) {
				attacker.setEffectCount(Effect.BLEED,(int) Math.ceil(baseBleeds/2f));
			}
		}
		int effectCount = attacker.effectCount(Effect.I_BLEED);
		if (effectCount > 0) {
			bleed += bleedDam(attacker,defender)*effectCount;
		}
		if (bleed > 0 && !extra.getPrint()) {
			int totalBleed = (int) Math.ceil(bleed);
			attacker.takeDamage(totalBleed);
			float leech = (defender.hasEffect(Effect.B_MARY) ? 2 : 0) + (defender.hasSkill(Skill.BLOODDRINKER) ? 0.5f : 0);
			if (leech > 0) {
				int final_blood = Math.round(totalBleed*leech);
				defender.addHp(final_blood);
				extra.println(indent+defender.getName() + " absorbs "
				+ attacker.getNameNoTitle() + "'s blood, healing " + final_blood + " HP off of "
				+ (totalBleed) + " damage!");
			}else {
				extra.println(indent+attacker.getName() + " bleeds for " + totalBleed + " HP.");
			}
		}
		if (attacker.hasEffect(Effect.BEES) && extra.chanceIn(1,5)) {
			int bee_damage = extra.randRange(1,IEffectiveLevel.cleanLHP(attacker.getLevel(), .04));
			if (!extra.getPrint()) {
				extra.println(indent+"The bees sting "+attacker.getName()+" for "+bee_damage+"!");
			}
			attacker.takeDamage(bee_damage);
		}
		
		//clear once per defense abilities
		if (defender.hasEffect(Effect.PARRY)) {
			defender.removeEffectAll(Effect.PARRY);
		}
		
		
		//skill processing for finished tactic
		/*if (attack.getAttack().getSkill_for() != null) {
			switch (attack.getAttack().getSkill_for()) {
				default://most won't have cases
				case TACTIC_SINGLE_OUT:
					break;
				case TACTIC_DUCK_ROLL:
					attacker.addEffect(Effect.ROLLING);
					break;
			}
		}*/
		
		if (defender.hasEffect(Effect.SINGLED_OUT) && defender.isAlive() && dataMap.get(attacker).side != dataMap.get(defender).side && extra.chanceIn(2,3)) {
			dataMap.get(attacker).nextTarget = defender;
		}
		
		attacker.getBag().turnTick();//for now, just armor buff decay
		
		if (defender.getHp() <= 0) {
			Networking.send("PlayDelay|sound_fallover1|35|");
		}
		if (attacker.getHp() <= 0) {
			Networking.send("PlayDelay|sound_fallover1|35|");
		}
		if (canWait && !attacker.isPlayer() && mainGame.combatWaits) {
			if (defender.isPlayer() && totalFighters > 2) {
				Networking.waitIfConnected(800L);//was 500L
			}else {
				Networking.waitIfConnected(500L);//was 300L
			}
		}
		//END TURN TRIGGERS
		if (atr.attack.hasBonusEffect()) {
			switch (atr.attack.getAttack().getRider()) {
			case CHALLENGE:
				//doesn't work on tactics
				if (defender.isAttacking() && defender.getNextAttack().isTacticOnly()) {
					if (!extra.getPrint() && !didDisplay) {
						extra.print(" But they were using a tactic so it didn't work...");
					}
					break;
				}
				double discount =0;
				if (defender.isAttacking() && !defender.isOnCooldown()) {
					discount = Math.max(0,((defender.getTime()-defender.getNextAttack().getWarmup())));
				}
				setAttack(defender,attacker);
				//the previous method adds time
				//we could set it to 0 first, but it's less steps to just
				//get the warmup directly since we already account for how much they get discounted
				double cost = defender.getNextAttack().getWarmup();
				//we set that we're going to attack them immediately, so if they confuse us, it might stick
				dataMap.get(attacker).nextTarget = defender;
				//apply our effect which softens the blow or enhances our attack
				attacker.addEffect(Effect.CHALLENGE_BACK);
				//their turn happens now
				handleTurn(defender, attacker,canWait,0);
				//they suffer a cost penalty to their cooldown
				defender.applyDiscount(-((cost*2)-discount));
				break;
			case CHAR:
				if (atr.type == ATK_ResultType.IMPACT) {
					defender.getBag().burnArmor(.05d);
				}
				break;
			case GLACIATE:
				if (atr.type == ATK_ResultType.IMPACT) {
					defender.applyDiscount(-10);
					defender.addEffectCount(Effect.FLUMMOXED,10);
				}
				break;
			}
		}
		
		return atr;
	}
	
	public static int bleedDam(Person attacker2, Person defender2) {
		if (attacker2 == null) {
			return IEffectiveLevel.cleanLHP(defender2.getLevel(),.01);
		}
		return IEffectiveLevel.cleanLHP(Math.min(defender2.getLevel(),attacker2.getLevel()+2),.01);
	}
	
	public static float bleedStackDam(Person target, int stacks) {
		return (float) IEffectiveLevel.uncleanLHP(target.getLevel(), stacks/200f);
	}
	
	/**
	 * gets how many stacks of bleed to apply
	 */
	public static int bleedStackAmount(Person attacker,Person defender) {
		if (attacker == null) {
			return defender.getMaxHp()/20;//5%
		}
		//5% adjusted by level proportion, from 1% to 10%
		return Math.round(5 * extra.clamp(attacker.getEffectiveLevel()/defender.getEffectiveLevel(),.2f,2f));
		//return Math.round(10 * extra.clamp(attacker.getEffectiveLevel()/defender.getEffectiveLevel(),.2f,2f));
	}
	
	private boolean inflictWound(Person attacker2, Person defender2, AttackReturn retu, Wound w) {
			ImpairedAttack attack = attacker2.getNextAttack();
			Integer[] nums = woundNums(attack,attacker2,defender2,retu,w);
			if (w == null) {
				return false;//fails safely now
			}
			assert defender2 != null;
			if (!w.bypass) {
				if ((defender2.hasSkill(Skill.TA_NAILS) && extra.randRange(1,5) == 1 )) {
					retu.addNote("Negate: "+extra.ATK_WOUND_NEGATE+"Tough as Nails "+w.name+extra.PRE_WHITE);
					return false;
				}
				if (defender2.hasEffect(Effect.PADDED) && extra.chanceIn(defender2.effectCount(Effect.PADDED),5)) {
					defender2.removeEffect(Effect.PADDED);
					retu.addNote("Negate: "+extra.ATK_WOUND_NEGATE+"Padded "+w.name+extra.PRE_WHITE);
					return false;
				}
				if (defender2.hasEffect(Effect.CHALLENGE_BACK)) {
					retu.addNote("Negate: "+extra.ATK_WOUND_NEGATE+"Temerity "+w.name+extra.PRE_WHITE);
					defender2.removeEffectAll(Effect.CHALLENGE_BACK);
					return false;
				}
			}
			
			String desc = null;//null means don't bother adding
			if (!extra.getPrint()) {//only really need to care if play can see
				desc = ((w.injury ? "Injury: ":"Wound: ")+w.getColor()+w.name+extra.PRE_WHITE);
			}
			switch (w) {
			case CONFUSED:
				attacker2.addEffect(Effect.CONFUSED_TARGET);
				defender2.addEffectCount(Effect.SHAKY, nums[0]);
				//newTarget = true;
				break;
			case SLICE:
				attacker2.addEffect(Effect.SLICE);
				attacker2.addEffect(Effect.ADVANTAGE_STACK);
				break;
			case DICE:
				attacker2.applyDiscount(nums[1]);
				attacker2.addEffect(Effect.SLICE);
				break;
			case SCALDED:
				elemBonusEffects(attacker2,defender2,retu);
				defender2.takeDamage(nums[0]);
				retu.bonus+=nums[0];
				defender2.getBag().burnArmor((nums[1]/100f),attack.getSlot());
				
				if (desc != null) {
					desc += "; "+nums[0] + " damage, "+nums[1]+"% burn";
				}
				break;
			case FROSTBITE:
				elemBonusEffects(attacker2,defender2,retu);
				defender2.takeDamage(nums[0]);
				retu.bonus+=nums[0];
				defender2.addEffectCount(Effect.SHAKY, nums[1]);
				
				if (desc != null) {
					desc += "; "+nums[0] + " damage";
				}
				break;
			case HACK: case CRUSHED:
				defender2.takeDamage(nums[0]);
				retu.bonus+=nums[0];
				
				if (desc != null) {
					desc += "; "+nums[0] + " damage";
				}
				break;
			case HAMSTRUNG: case TRIPPED:
				defender2.addEffectCount(Effect.SHAKY, nums[1]);
			case WINDED:
				defender2.applyDiscount(-nums[0]);
				break;
			case FROSTED:
				elemBonusEffects(attacker2,defender2,retu);
				defender2.applyDiscount(-Math.min(50,defender2.getTime()*(nums[0]/100f)));
				break;
			case JOLTED:
				elemBonusEffects(attacker2,defender2,retu);
				defender2.applyDiscount(-nums[0]);
				break;
			case BLACKENED:
				elemBonusEffects(attacker2,defender2,retu);
				defender2.getBag().burnArmor((nums[0]/100f),attack.getSlot());
				if (desc != null) {
					desc += "; "+nums[0] + "% burn";
				}
				break;
			case MAJOR_BLEED: case MAJOR_BLEED_BLUNT:
				defender2.addEffect(Effect.MAJOR_BLEED);//don't need to be clotted
				case BLEED: case BLEED_BLUNT:
				if (!defender2.hasEffect(Effect.CLOTTER)) {
					defender2.addEffectCount(Effect.BLEED,nums[0]);
					if (desc != null) {
						desc += "; "+nums[0] + " bleeds";
					}
				}
				break;
			case FLAYED:
				if (!defender2.hasEffect(Effect.CLOTTER)) {
					defender2.addEffectCount(Effect.BLEED,nums[0]);
					if (desc != null) {
						desc += "; "+nums[0] + " bleeds";
					}
				}
				int fdam = defender2.effectCount(Effect.BLEED);
				defender2.takeDamage(fdam);
				retu.bonus+=fdam;
				if (desc != null) {
					desc += "; "+fdam + " damage";
				}
				break;
			case SCREAMING:
				elemBonusEffects(attacker2,defender2,retu);
				defender2.addEffect(Effect.DISARMED);
				defender2.addEffectCount(Effect.SHAKY,nums[0]);
				break;
			case DISARMED: 
				defender2.addEffect(Effect.DISARMED);
				defender2.addEffectCount(Effect.FLUMMOXED,nums[0]);
				break;
			case KO:
				defender2.takeDamage(nums[0]);
				retu.bonus+=nums[0];
				if (desc != null) {
					desc += "; "+nums[0] + " damage";
				}
				if (!defender2.hasEffect(Effect.BRAINED)) {
					defender2.addEffect(Effect.BREATHING);
				}
				break;
			case I_BLEED:
				if (!defender2.hasEffect(Effect.CLOTTER)) {
					defender2.addEffect(Effect.I_BLEED);
					defender2.addEffect(Effect.I_BLEED);
				}
				break;
			case TEAR:
				defender2.addEffect(Effect.TORN);
				break;
			case BLOODY:
				if (!defender2.hasEffect(Effect.CLOTTER)) {
					defender2.addEffectCount(Effect.BLEED,nums[1]);
					if (desc != null) {
						desc += "; "+nums[1] + " bleeds";
					}
				}
				//fall through
			case BLINDED:
				if (defender2.isAttacking()) {
					defender2.getNextAttack().multiplyHit(1-(nums[0]/100f));
				}else {
					defender2.addEffectCount(Effect.FLUMMOXED,nums[0]/2);
				}
				break;
			case DIZZY:
				if (defender2.isAttacking()) {
					defender2.getNextAttack().multiplyHit(1-(nums[0]/100f));
				}else {
					defender2.addEffectCount(Effect.FLUMMOXED,nums[0]);
				}
				break;
			case SHIVERING:
				elemBonusEffects(attacker2,defender2,retu);
				defender2.addEffectCount(Effect.FLUMMOXED,nums[0]);
				defender2.addEffectCount(Effect.SHAKY, nums[1]);
				break;
			case MANGLED:
				defender2.multBodyStatus(attack.getTargetSpot(), 1-(nums[0]/10f));
				break;
			case DEPOWER:
				defender2.addEffect(Effect.DEPOWERED);
				break;
			case MAIMED:
				defender2.addEffect(Effect.MAIMED);
				break;
			case CRIPPLED:
				defender2.addEffect(Effect.CRIPPLED);
				break;
			case HIT_VITALS:
				defender2.addEffect(Effect.HIT_VITALS);
				break;
			case BRAINED:
				defender2.addEffect(Effect.BRAINED);
				//don't need to apply text since on Brained, so we can skip adding it anywhere and just do the effects
				inflictWound(attacker2,defender2,retu,Wound.KO);
				break;
			case SHINE:
				defender2.takeDamage(nums[0]);
				retu.bonus+=nums[0];
				defender2.getBag().burnArmor((nums[1]/100f),attack.getSlot());
				if (desc != null) {
					desc += "; "+nums[0] + " damage, "+nums[1]+"% burn";
				}
				break;
			case GLOW:
				defender2.addEffectCount(Effect.FLUMMOXED,nums[0]);
				defender2.getBag().burnArmor((nums[1]/100f),attack.getSlot());
				if (desc != null) {
					desc += "; "+nums[1]+"% burn";
				}
				break;
			case PUNCTURED:
				defender2.removeEffect(Effect.PADDED);
			case RUPTURED:
				defender2.getBag().damageArmor((nums[0]/100f),attack.getSlot());
				break;
			case STATIC:
				elemBonusEffects(attacker2,defender2,retu);
				defender2.removeEffectAll(Effect.ADVANTAGE_STACK);
				defender2.removeEffectAll(Effect.BONUS_WEAP_ATTACK);
				defender2.addEffectCount(Effect.SHAKY,nums[0]);
				break;
			case NEGATED://no effect
			case EMPTY:
				if (desc != null) {
					retu.addNote(desc);
				}
				return false;
			}
			if (desc != null) {
				retu.addNote(desc);
			}
			return true;
	}
	
	/**
	 * used by the elementalist series of boosts on inflicting an elemental wound
	 * adds string as notes
	 */
	private void elemBonusEffects(Person attacker2, Person defender2, AttackReturn retu) {
		if (attacker2.hasSkill(Skill.ELEMENTALIST)) {//only check if has elementalist to save on checks
			if (attacker2.hasSkill(Skill.M_PYRO_BOOST)) {
				attacker2.addEffect(Effect.PARRY);
				retu.addNote("Ignite Parry!");
			}
			if (attacker2.hasSkill(Skill.M_CYRO_BOOST)) {
				attacker2.getBag().buffArmorAdd(.1d);
				retu.addNote("Frost armor!");
			}
			if (attacker2.hasSkill(Skill.M_AERO_BOOST)) {
				defender2.applyDiscount(-4);
				retu.addNote("Elec shock!");
			}
		}
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
	public static Integer[] woundNums(ImpairedAttack attack, Person attacker, Person defender, AttackReturn result, Wound w) {
		switch (w) {
		case NEGATED:
		case DEPOWER: case MAIMED: case CRIPPLED: case HIT_VITALS:
			//nothing
			return new Integer[0];
		case SLICE:
			return new Integer[] {10};//10% faster, 10% more accurate, 1 advantage stack
		case DICE:
			return new Integer[] {10,10};//10% faster, 10% more accurate, 10 time units faster
		case CONFUSED:
			return new Integer[] {2};//2 shaky stacks, confused status
		case HACK:
			if (result == null) {
				return new Integer[] {attack.getTotalDam()/4};
			}
			return new Integer[] {result.damage/4};
		case CRUSHED:
			return new Integer[] {attack.getTotalDam()/8};
		case SCALDED:
			return new Integer[] {attack.getTotalDam()/10,Math.min(10,(attack.getTotalDam()*50)/defender.getMaxHp())};//.5% armor burn per MHP threatened, max 10%
		case FROSTBITE:
			return new Integer[] {attack.getTotalDam()/10,2};//2 shaky stacks
		case BLINDED:
			return new Integer[] {50};//50% less accurate now or half that later
		case SCREAMING:
			return new Integer[] {1};//-1 choice and 2 shaky stacks
		case DISARMED:
			return new Integer[] {20};//20% less accurate later and -1 choice
		case DIZZY:
			return new Integer[] {30};//30% less accurate now or later
		case SHIVERING:
			return new Integer[] {20,2};//20% less accurate later, 2 shaky stacks
		case FROSTED:
			return new Integer[] {30,50};//takes 30% longer of current time, cap of +50
		case JOLTED:
			return new Integer[] {20};//-20 time units
		case STATIC:
			return new Integer[] {2};//2 shaky stacks
		case BLACKENED:
			return new Integer[] {Math.min(20,(attack.getTotalDam()*100)/defender.getMaxHp())};//1% armor burn per MHP threatened, max 20%
		case WINDED:
			return new Integer[] {20};//-20 time units
		case HAMSTRUNG:
			return new Integer[] {10,2};//-10 time units, 2 shaky stacks
		case TRIPPED:
			return new Integer[] {16,1};//-16 time units, 1 shaky stack
		case KO: case BRAINED:
			//this does fixed damage on defender because it needs to recover, plus that's part of it's gimmick of felling mighty foes
			return new Integer[] {IEffectiveLevel.cleanLHP(defender.getLevel(),.1)};
		case I_BLEED:
			return new Integer[] {2*bleedDam(attacker,defender),2*bleedDam(null,defender)};//can take null attacker
		case MAJOR_BLEED: case MAJOR_BLEED_BLUNT:
		case BLEED: case BLEED_BLUNT:
			int stacks = bleedStackAmount(attacker, defender);//can take null attacker
			return new Integer[] {stacks};
		case TEAR://WET
			return new Integer[] {10};// %, multiplicative dodge mult penalty
		case MANGLED:
			return new Integer[] {50};//50% reduction in condition
		case BLOODY:
			int bstacks = bleedStackAmount(attacker, defender)/2;//can take null attacker
			return new Integer[] {50,bstacks};//bloody blind
		case FLAYED:
			int fbstacks = bleedStackAmount(attacker, defender)/2;//can take null attacker
			return new Integer[] {fbstacks,fbstacks+defender.effectCount(Effect.BLEED)};//damage based on bleed, just projected
		case SHINE://undead condwound
			return new Integer[] {attack.getTotalDam()/10,10};//10% bonus damage, 10% armor damage
		case GLOW://undead condwound
			return new Integer[] {40,10};//40% less accurate later, 10% armor damage
		case PUNCTURED://*100 so it can be turned into a % later and we don't have decimal issues, also ignores and removes one padded
			//1% armor damage per end result of damage, max 15%, plus ignore/damage padding
			if (result == null) {
				return new Integer[] {Math.min(15,(attack.getTotalDam()*100*2)/defender.getMaxHp())};
			}
			return new Integer[] {Math.min(10,(result.damage*100*2)/defender.getMaxHp())};
		case RUPTURED://1% armor damage per MHP threatened, max 20%
			return new Integer[] {Math.min(20,(attack.getTotalDam()*150)/defender.getMaxHp())};
		}
		return new Integer[0];
	}


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
		//faster to not check and just put most likely
		attacker.removeEffectAll(Effect.BRISK);
		attacker.removeEffectAll(Effect.CHALLENGE_BACK);
		
		if (attacker.hasEffect(Effect.FLUMMOXED)) {
			//double mult = Math.pow(.99f, attacker.effectCount(Effect.FLUMMOXED));//alternate form with different curve but uses math.* instead of just div
			float mult = 1/(1+(attacker.effectCount(Effect.FLUMMOXED)/100f));
			for (ImpairedAttack a: atts) {
				a.multiplyHit(mult);
			}
			attacker.removeEffectAll(Effect.FLUMMOXED);
		}
		
		newAttack = AIClass.chooseAttack(atts,this,attacker,defender);
		attacker.setAttack(newAttack);
		newAttack.setDefender(defender);
		//note that attacks can be retargeted, so the following code might not run with the final defender
		if (newAttack.hasBonusEffect()) {
			switch (newAttack.getAttack().getRider()) {
				default://most won't have cases
				case CHALLENGE://handled later to avoid faux 'race' conditions
					break;
				case ROLL:
					attacker.addEffect(Effect.DUCKING);
					if (!extra.getPrint()) {
						extra.println(prettyHPPerson("[HP]"+attacker.getNameNoTitle()+" ducks!",extra.TIMID_MAGENTA, attacker));
					}
					break;
				case SINGLE_OUT:
					//always applies to the first target even if redirected, which will reduce player frustration
					//and also is just easier to code
					
					//tactic applies before the message displays, and the redirection could be weird
					//but the defualt tactic is 'instant' so it's unlikely display confusion will happen,
					//and any actual damage should apply to the redirected target
					defender.addEffect(Effect.SINGLED_OUT);
					
					/*
					if (!extra.getPrint()) {
						extra.println(prettyHPColors("[HA]"+attacker.getNameNoTitle()+" singles out [HD]"+defender.getNameNoTitle()+"...", extra.TIMID_MAGENTA, attacker, defender));
					}*/
					break;
				case TAKEDOWN:
					attacker.addEffect(Effect.PLANNED_TAKEDOWN);
					break;
			}
		}
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
	public static final String quotedHP = Pattern.quote("[HP]");
	
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
	
	public String prettyHPPerson(String baseString,String normalColor,Person person) {
		return normalColor 
				+ baseString
				.replaceAll(quotedHP,person.inlineHPColor())
				.replaceAll(quotedDefaultColor, normalColor);
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
