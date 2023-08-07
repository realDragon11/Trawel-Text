package trawel.battle;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.classless.Skill;
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
	public List<Person> survivors;
	public List<Person> killed;
	public long turns = 0;
	public int totalFighters = 2;
	public boolean battleIsLong = false;
	
	public static Combat testCombat = new Combat();
	
	public static int longBattleLength = 20;
	//constructor
	/**
	 * Holds a fight to the death, between two people.
	 * @param manOne (Person)
	 * @param manTwo (Person)
	 */
	public Combat(Person manOne, Person manTwo,World w) {
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
							p.getNextAttack().multPotencyMult(Math.min(20,sk.lSkill.value)/100.0);
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
	
	public Combat(World w, FortHall hall,List<Person>... people) {
		this(w,hall, Arrays.asList(people));
	}
	public Combat(World w,List<List<Person>> people) {
		this(w,null,people);
	}
	
	private List<List<Person>> liveLists;
	private List<List<Person>> targetLists;
	private Map<Person,BattleData> dataMap;
	private List<Person> totalList, killList;
	private int sides;
	
	private class BattleData{
		public int side;
		public Person lastAttacker = null;
	}
	
	public Combat(World w, FortHall hall,List<List<Person>> people) {
		int size = people.size();
		sides = size;
		totalList = new ArrayList<Person>();
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
					mainGame.story.startFight(true);
				}
				p.battleSetup();
				totalList.add(p);
			}
		}
		for (Person p: totalList) {
			Person otherperson = getDefenderFor(p);
			if (p.isPlayer()) {
				otherperson.displayStatsShort();
				p.getBag().graphicalDisplay(-1,p);
				otherperson.getBag().graphicalDisplay(1,otherperson);
			}
			setAttack(p,otherperson);
		}
		totalFighters = totalList.size();
		Person quickest = null;//moved out so we can have a default last actor to default as alive
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

			Person defender = quickest.getNextAttack().getDefender();
			boolean wasAlive = defender.isAlive();
			dataMap.get(defender).lastAttacker = quickest;
			AttackReturn atr = handleTurn(quickest,defender,playerIsInBattle,lowestDelay);
			if (atr.code != ATK_ResultCode.NOT_ATTACK) {
			if (!defender.isAlive() && wasAlive) {
				killWrapUp(defender, quickest, false);
			}else {
				if (defender.hasEffect(Effect.CONFUSED_TARGET)) {
					//the defender has been befuddled or confused
					if (defender.isOnCooldown()) {//if on cooldown, add the effect for next attack instead
						
					}else {
						defender.removeEffect(Effect.CONFUSED_TARGET);
					Person newDef = getDefenderForConfusion(defender);
					if (defender.getNextAttack().getDefender().isSameTargets(newDef)) {
						defender.getNextAttack().setDefender(newDef);
					}else {
						//targets don't match, need new attack.
						//get discount on next one
						double discount = -Math.max(0,((defender.getTime()-defender.getNextAttack().getWarmup()))/2.0);
						setAttack(defender,newDef);
						defender.applyDiscount(discount);
					}
					}
				}
			}
			}


			//if the attacker is dead, through bleeding or otherwise, they are force removed- they would not have acted if they were already removed
			if (!quickest.isAlive()) {
				killWrapUp(quickest,dataMap.get(quickest).lastAttacker,true);
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
				if (quickest.isPlayer() && Player.player.eaBox.markTarget != null) {
					if (totalList.contains(Player.player.eaBox.markTarget)) {
						otherperson = Player.player.eaBox.markTarget;
					}else {
						Player.player.eaBox.markTarget = null;
					}
				}
				if (otherperson == null) {
					otherperson = getDefenderFor(quickest);
				}
			}
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
					&& quickest.getBag().getHand().hasQual(WeaponQual.CARRYTHROUGH)) 
			{
				quickest.advanceTime(Math.min(10,quickest.getTime()-1));
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
	
	private Person getDefenderFor(Person attacker) {
		return extra.randList(targetLists.get(dataMap.get(attacker).side));
	}
	
	private Person getDefenderForConfusion(Person attacker) {
		//will attempt to target anyone, but if that fails and targets self, revert to old way
		Person defender = extra.randList(totalList);
		if (defender == attacker) {
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
		changed = totalList.remove(dead) ? true : changed ;
		if (!killList.contains(dead)) {
			changed = true;
			killList.add(dead);
		}
		return changed;
	}
	
	private boolean killWrapUp(Person dead, Person killer, boolean bledOut) {
		boolean changed = killRemovePerson(dead);
		if (changed) {
			if (bledOut) {
				extra.println(dead.getName() + " falls to the ground, dead!");
			}else {
				extra.println(dead.getName() + " dies!");
			}
			dead.addDeath();
			//can insert a null check here later if need be
			//DOLATER: need null for starting bleeding, but for now null exceptions are needed
			killer.addKillStuff();
			killer.getBag().getHand().addKill();
			if (dead.isPlayer()) {
				killer.addPlayerKill();
			}
			if (killer.hasSkill(Skill.KILLHEAL)){
				killer.addHp(5*killer.getLevel());
			}
		}
		return changed;
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
	
	//TODO: could store a 'skill resolver' in a local variable, if defender/attacker = null, then set it to a static empty enum set
	//this would make the overhead and problems of null checking people a billion times for skills not a problem anymore
	//it would have a minor performance impact on battlescore since the checks would need to be made
	//but the DRY > WET advantages might be good
	
	/**
	 * Redone, write new docs
	 */
	public AttackReturn handleAttack(boolean isReal, ImpairedAttack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		String str = "";
		AttackReturn ret;
		boolean wasDead = false;
		int wLevel = att.getLevel();
		if (defender == null) {
			defender = DummyPerson.single;
		}
		if (off == null) {
			off = WorldGen.getDummyInvs().get(0);//attacking only
		}
		if (isReal) {
			if (extra.chanceIn(1, 5)) {
				Networking.send("PlayDelayPitch|"+SoundBox.getSound(off.getRace().voice,SoundBox.Type.SWING) + "|1|" +attacker.getPitch() +"|");
			}
			if (!defender.isAlive()) {
				wasDead = true;
			}
		}
		//TODO unsure if should add attacker's aim at impairment phase
		double dodgeBase = def.getDodge()*defender.getTornCalc();
		double hitBase = att.getHitMult()*off.getAim();
		
		double dodgeRoll = dodgeBase*extra.getRand().nextDouble();
		double hitRoll = hitBase*extra.getRand().nextDouble();
		if (dodgeRoll > hitRoll){
			ret= new AttackReturn(ATK_ResultCode.DODGE,"",att);
			if (isReal) {
				ret.stringer = att.fluff(ret);
				if (wasDead) {
					ret.addNote("What a dodgy corpse!");
				}
			}
			return ret;
		}
		if (hitRoll < .05) {//missing now exists
			ret= new AttackReturn(ATK_ResultCode.MISS,"",att);
			if (isReal) {
				ret.stringer = att.fluff(ret);
				if (wasDead) {
					ret.addNote("They missed a corpse!");
				}
			}
			return ret;
		}
		
		double sharpA = def.getSharp(att)*armMod;
		double bluntA = def.getBlunt(att)*armMod;
		double pierceA= def.getPierce(att)*armMod;
		ret = new AttackReturn(att.getSharp(),att.getBlunt(),att.getPierce(),sharpA,bluntA,pierceA,str,att);
		if (isReal) {
			Networking.send("PlayHit|" +def.getSoundType(att.getSlot()) + "|"+att.getAttack().getSoundIntensity() + "|" +att.getAttack().getSoundType()+"|");
			if (wasDead) {
				ret.addNote("Beating their corpse!");
			}
		}
		if (att.hasWeaponQual(WeaponQual.RELIABLE) && ret.damage <= wLevel) 
		{
			ret.damage = wLevel;
			ret.reliable = true;
			if (isReal) {ret.addNote("Reliable Damage: "+wLevel);}
		} else {
			if (ret.code == ATK_ResultCode.DAMAGE) {//no longer functions on reliable
				if (att.hasWeaponQual(WeaponQual.REFINED)) {
					ret.damage += wLevel;
					ret.addNote("Refined Bonus: " + wLevel);
				}
				if (att.hasWeaponQual(Weapon.WeaponQual.WEIGHTED)) {
					if (att.getHitMult() < 1.5) {
						int weightBonus = ret.damage;
						ret.damage = (int) Math.round(ret.damage*Math.log10(5+(20-(att.getHitMult()*10))));
						weightBonus = ret.damage-weightBonus;
						if (isReal) {ret.addNote("Weighted Bonus: " + weightBonus);}
					}
				}
				if (defender.hasSkill(Skill.RAW_GUTS)) {
					int maxGResist = (int) Math.ceil(defender.getLevel() * defender.getConditionForPart());
					int gResisted = ret.damage;
					ret.damage = Math.max(ret.damage/2,ret.damage-extra.randRange(0,maxGResist));
					gResisted = gResisted-ret.damage;
					if (isReal) {ret.addNote("Raw Guts Resisted: " + gResisted);}
				}
			}
		}
		if (isReal) {
			ret.stringer = str + att.fluff(ret);
			ret.putPlayerFeedback();
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
		public boolean reliable = false;
		public ImpairedAttack attack;
		public ATK_ResultCode code;
		private String notes;
		public AttackReturn(int sdam,int bdam, int pdam, double sarm, double barm, double parm, String str, ImpairedAttack att) {
			stringer = str;
			attack = att;
			notes = null;
			//DOLATER: can turn this into an array of damage types later if need be
			int rawdam = sdam+bdam+pdam;
			double rawarm =sarm+barm+parm;
			double s_weight = sdam/rawdam;
			double b_weight = bdam/rawdam;
			double p_weight = pdam/rawdam;
			double weight_arm = (s_weight*sarm)+(b_weight*barm)+(p_weight*parm);
			//double guess = ((rawdam+weight_arm)/weight_arm)-1;
			double def_roll = Math.max(.05,extra.hrandom());
			float att_roll = extra.lerp(.7f,1f,extra.hrandomFloat());
			double global_roll = (att_roll*rawdam)/(def_roll*weight_arm);
			if (global_roll < .4) {//if our random damage roll was less than 40% of the armor roll, negate
				subDamage = new int[] {0,0,0};
				damage = 0;
				code = ATK_ResultCode.ARMOR;
			}else {//TODO: new goal: % reductions based on relativeness with a chance to negate entirely if much higher?
				//up to half the damage if the damage roll was less than the total weighted armor (without roll)
				double reductMult = extra.lerp(1f,.5f,(float) (1f-Math.min(1,(att_roll*rawdam)/weight_arm)));
				//each damage vector can be brought down to 20%, but this is cumulative so the armor has to be 4x higher
				//to achieve that level of reduction
				double s_reduct = extra.lerp(1f,.2f,(float) (1f-Math.min(1,(sdam/(sarm/4)))));
				double b_reduct = extra.lerp(1f,.2f,(float) (1f-Math.min(1,(bdam/(barm/4)))));
				double p_reduct = extra.lerp(1f,.2f,(float) (1f-Math.min(1,(pdam/(parm/4)))));
				int scomp = (int) (sdam*reductMult*s_reduct);
				int bcomp = (int) (bdam*reductMult*b_reduct);
				int pcomp = (int) (pdam*reductMult*p_reduct);
				//DOLATER: not sure if this is done yet
				
				subDamage = new int[] {scomp,bcomp,pcomp};
				damage = Math.min(1,subDamage[0]+subDamage[1]+subDamage[2]);//deals a min of 1 damage
				code = ATK_ResultCode.DAMAGE;
			}
		}
		
		private int damageComp(int dam, double arm, double guess, double roll) {
			double guess2 = ((dam+arm)/arm)-1;
			int result;
			if (guess > 1) {//dam > arm
				//low chance to negate entirely, global damage is high
				return 1;
			}else {//dam <= arm
				//decent chance to negate entirely, global damage is low
				result = (int) (extra.hrandom()*arm);
				return 0;
			}
		}
		
		public AttackReturn(ATK_ResultCode rcode, String str, ImpairedAttack att) {
			code = rcode;
			damage = 0;
			//null
			stringer = str;
			attack = att;
			notes = null;
		}
		
		public AttackReturn() {
			code = ATK_ResultCode.NOT_ATTACK;
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
		
		public void putPlayerFeedback() {
			if (attack == null) {
				extra.getPrint();
			}
			if (attack.getAttacker().isPlayer()) {
				switch (code) {
				case ARMOR:
					Player.lastAttackStringer = "\nYou hit "+attack.getDefender().getName() + 
					" but their armor held!";
					break;
				case DAMAGE:
					Player.lastAttackStringer = "\nYou hit "+attack.getDefender().getName() + 
					" dealing "+ damage+" damage!";
					break;
				case DODGE:
				case MISS:
					Player.lastAttackStringer = "\nYou missed "+attack.getDefender().getName() + 
					(damage > 0 ? " but dealt " + damage+" damage!" : "!");
					break;
				case NOT_ATTACK:
					
					break;
				}
			}
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
	//FIXME: should probably move this to attack returns or something??? and the 'handle turn' code
	public void handleAttackPart2(ImpairedAttack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender, int damageDone) {
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
		ImpairedAttack attack = attacker.getNextAttack();
		AttackReturn atr = handleAttack(true,attack,defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender);
		int damageDone = atr.damage;
		String inlined_color = extra.PRE_WHITE;
		this.handleAttackPart2(attack,defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender,damageDone);
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
			
			//Wound effects
			String woundstr = inflictWound(attacker,defender,atr);
			if (defender.hasEffect(Effect.R_AIM)) {
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
					extra.print(" "+extra.AFTER_ATTACK_MISS+randomLists.attackMissFluff(atr.code));
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
		
		return atr;
	}
	private String inflictWound(Person attacker2, Person defender2, AttackReturn retu) {
		if (retu.code == ATK_ResultCode.ARMOR) {
			return " The armor deflects the wound.";//reliable/other armor hits don't inflict wound effects
		}
		if ((defender2.hasSkill(Skill.TA_NAILS) && extra.randRange(1,5) == 1 )) {
			//|| damage == 0//wounds no longer hit if dam=0, if this needs to change, fix woundstring printing as well
			//DOLATER: fix that?
			//although they'll still not usually apply if damage == 0
			return (" They shrug off the blow!");
		}else {
			ImpairedAttack attack = attacker2.getNextAttack();
			Integer[] nums = woundNums(attack,attacker2,defender2,retu);
			Wound w = attack.getWound();
			if (w == null) {
				throw new RuntimeException("inflicting null wound");
			}
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
			case DIZZY: case FROSTED: case BLINDED:
				defender2.getNextAttack().multiplyHit(1-(nums[0]/10f));
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
			case BLOODY:
				defender2.addEffect(Effect.BLEED);
				defender2.getNextAttack().multiplyHit(1-(nums[0]/10f));
				break;
			case MANGLED:
				defender2.multBodyStatus(attack.getTargetSpot(), 1-(nums[0]/10f));
				break;

			}
			if (w != Wound.GRAZE)
				if (attack.getWeapon() != null && attack.getWeapon().hasQual(Weapon.WeaponQual.DESTRUCTIVE)) {
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
	public static Integer[] woundNums(ImpairedAttack attack, Person attacker, Person defender, AttackReturn result) {
		switch (attack.getWound()) {
		case CONFUSED:
			//nothing
			break;
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
		case MANGLED:
			return new Integer[] {50};//50% reduction in condition
		case BLOODY://bleeds aren't synced, WET :(
			return new Integer[] {50,defender.getLevel()};//bloody blind
		}
		return new Integer[0];
	}

	/*
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
		List<ImpairedAttack> atts = (attacker.getStance().randAtts(3,attacker.getBag().getHand(),attacker,defender));
		newAttack = AIClass.chooseAttack(atts,this,attacker,defender);
		attacker.setAttack(newAttack);
		newAttack.setDefender(defender);
		//manOne.getNextAttack().blind(1 + manOne.getNextAttack().getSpeed()/1000.0);
		//this blind was here to simulate quicker attacks being harder to dodge, it has been removed
	}
	
		
}
