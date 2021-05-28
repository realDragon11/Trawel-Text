package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.Armor.ArmorQuality;
import trawel.factions.FBox;
import trawel.fort.FortHall;
import trawel.fort.LSkill;
import trawel.fort.SubSkill;
/**
 * A combat holds some of the more battle-focused commands.
 * @author Brian Malone
 * 2/8/2018
 */

public class Combat {
	//instance variables
	private Person attacker;
	private Person defender;
	public ArrayList<Person> survivors;
	public ArrayList<Person> killed;
	private boolean newTarget = false;
	public long turns = 0;
	public int totalFighters = 2;
	public boolean battleIsLong = false;
	
	public static Combat testCombat = new Combat();
	
	public static int longBattleLength = 50;
	//constructor
	/**
	 * Holds a fight to the death, between two people.
	 * @param manOne(Person)
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
		manTwo.displayStatsShort();
		attacker = manOne;
		defender = manTwo;
		
		boolean playerIsInBattle = attacker.isPlayer() || defender.isPlayer();
		attacker = manTwo;
		defender = manOne;
		setAttack(manTwo,manOne);
		setAttack(manOne,manTwo);
		extra.println("");
		extra.println(extra.choose("Our two fighters square off...","They look tense.","It's time to fight.","They look ready to fight.","The battle has begun."));
		BardSong song = w.startBardSong(manOne,manTwo);
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
			attacker.advanceTime(delay);//TODO: figure out why this wasn't included
			handleTurn( attacker,  defender,  song,playerIsInBattle,delay);
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
		song.addKill(manOne, manTwo);
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
	
	public void handleSkillCons(List<SkillCon> cons, ArrayList<Person> totalList,double timePassed) {
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
	
	public Combat(World w,ArrayList<Person>... people) {
		this(w,null, people);
	}
	public Combat(World w, FortHall hall,ArrayList<Person>[] people) {
		int size = people.length;
		BardSong song = w.startBardSong();
		ArrayList<Person> totalList = new ArrayList<Person>();
		ArrayList<Person> killList = new ArrayList<Person>();
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
		for (ArrayList<Person> peoples: people) {
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
				ArrayList<Person> otherpeople = people[rand];
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
		while(true) {
			Person quickest= null;
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
			handleTurn(quickest,defender,song,playerIsInBattle,lowestDelay);
			if (!defender.isAlive() && wasAlive) {
				extra.println("They die!");
				quickest.getBag().getHand().addKill();
				if (quickest.hasSkill(Skill.KILLHEAL)){
					quickest.addHp(5*quickest.getLevel());
				}
				song.addKill(quickest,defender);
				quickest.addKillStuff();
				totalList.remove(defender);
				killList.add(defender);
				for (ArrayList<Person> list: people) {
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
						ArrayList<Person> otherpeople = people[rand];
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
			
			int sides = 0;
			for (ArrayList<Person> list: people) {
				if (list.size() > 0) {
					sides++;
				}
			}
			
			if (sides == 1) {
				break;//end battle
			}
		
			
			Person otherperson = null;
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
				ArrayList<Person> otherpeople = people[rand];
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
			quickest.getNextAttack().defender = otherperson;
			if (quickest.hasSkill(Skill.LIFE_MAGE)) {
				for (ArrayList<Person> list: people) {
					if (list.contains(quickest)) {
						for (Person p: list) {
							if (p.getHp() < p.getMaxHp()) {p.addHp(1);}
						}
					}
				}
			}
			
		}
		
		survivors = totalList;
		killed = killList;
		
		
	}
	
	public Combat() {
		// TODO empty for tests
	}

	//instance methods
	/**
	 * Calculate if an attack hits, and how much damage it would deal, countered by the armor.
	 * @param att The attack being used (Attack)
	 * @param def the inventory of the defender (Inventory)
	 * @param off the inventory of the attacker (Inventory)
	 * @param armMod the armormod (double)
	 * @param printString (boolean) - if true, print the attack
	 * @return
	 */
	public AttackReturn handleAttack(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		String str = "";
		if (att.getName().contains("examine")){
			if  (!attacker.isPlayer()) {
				str +=("They examine you...");
				return new AttackReturn(-2,str);
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
			return new AttackReturn(-2,str);
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
		if (((def.getDodge()*defender.getTornCalc())/(att.getHitmod()*off.getAim()))*Math.random() > 1.0){
			return new AttackReturn(-1,str);//do a dodge
		}
		if (extra.chanceIn(def.countArmorQuality(ArmorQuality.HAUNTED),80)){
			return new AttackReturn(-1,str +extra.inlineColor(Color.GREEN)+" An occult hand leaps forth, pulling them out of the way![c_white]");
		}
		//return the damage-armor, with each type evaluated individually
		Networking.send("PlayHit|" +def.getSoundType(att.getSlot()) + "|"+att.getSoundIntensity() + "|" +att.getSoundType()+"|");
		switch (mainGame.attackType) {
		default:
			//double depthWeapon2 = .25;
			//double midWeapon2 = .7;
			double depthArmor2 = .25;
			double midArmor2 = .7;
			double armorMinShear = .1;
			//armMod = armMod*(1-(armorMinShear/2));
			
			double sharpA = def.getSharp(att.getSlot())*armMod;
			double bluntA = def.getBlunt(att.getSlot())*armMod;
			double pierceA= def.getPierce(att.getSlot())*armMod;
			return Combat.testCombat.new AttackReturn((int)(
					(extra.zeroOut((att.getSharp())-((armorMinShear*sharpA)+((1-armorMinShear)*sharpA*extra.upDamCurve(depthArmor2,midArmor2)))))
					+extra.zeroOut((att.getBlunt())-((armorMinShear*bluntA)+((1-armorMinShear)*bluntA*extra.upDamCurve(depthArmor2,midArmor2))))
					+extra.zeroOut((att.getPierce())-((armorMinShear*pierceA)+((1-armorMinShear)*pierceA*extra.upDamCurve(depthArmor2,midArmor2))))),str);
		}
	}
	
	public static AttackReturn handleTestAttack(Attack att, Inventory def, double armMod) {
		double damMod = 1;
		if (((def.getDodge())/(att.getHitmod()))*Math.random() > 1.0){
			return Combat.testCombat.new AttackReturn(-1,"");//do a dodge
		}
		//return the damage-armor, with each type evaluated individually
		switch (mainGame.attackType) {
		default:
			//double depthWeapon2 = .25;
			//double midWeapon2 = .7;
			double depthArmor2 = .25;
			double midArmor2 = .7;
			double armorMinShear = .1;
			//armMod = armMod*(1-(armorMinShear/2));
			
			double sharpA = def.getSharp(att.getSlot())*armMod;
			double bluntA = def.getBlunt(att.getSlot())*armMod;
			double pierceA= def.getPierce(att.getSlot())*armMod;
			return Combat.testCombat.new AttackReturn((int)(
					(extra.zeroOut((att.getSharp())-((armorMinShear*sharpA)+((1-armorMinShear)*sharpA*extra.upDamCurve(depthArmor2,midArmor2)))))
					+extra.zeroOut((att.getBlunt())-((armorMinShear*bluntA)+((1-armorMinShear)*bluntA*extra.upDamCurve(depthArmor2,midArmor2))))
					+extra.zeroOut((att.getPierce())-((armorMinShear*pierceA)+((1-armorMinShear)*pierceA*extra.upDamCurve(depthArmor2,midArmor2))))),"");
			}
	}
	
	public class AttackReturn {
		public int damage;
		public String stringer;
		public AttackReturn(int dam, String str) {
			damage = dam;
			stringer = str;
		}
	}
	
	/**
	 * does stuff that can't go in handleAttack because it's permanent and handleAttack is used in ai stuff.
	 * handles elemental damage
	 * should scale of off % of hp damaged or something to avoid confusing the ai
	 */
	public void handleAttackPart2(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender, int damageDone) {
		double percent = ((double)extra.zeroOut(damageDone))/((double)defender.getMaxHp());
		if (off.getHand().isEnchantedHit() && !(att.getName().contains("examine"))) {
			
			def.burn(def.getFire(att.getSlot())*percent*off.getHand().getEnchantHit().getFireMod()/2,att.getSlot());
			
			defender.advanceTime(-(percent*defender.getTime()*off.getHand().getEnchantHit().getFreezeMod()*def.getFreeze(att.getSlot())));
			
			defender.takeDamage((int)(percent*off.getHand().getEnchantHit().getShockMod()/3*def.getShock(att.getSlot())));
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
	public void handleTurn(Person attacker, Person defender, BardSong song,boolean canWait, double delay) {
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
				if (attacker.isRacist() && !attacker.getBag().getRace().equals(defender.getBag().getRace()) && extra.chanceIn(1,3)) 
				{
					extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" +defender.getBag().getRace().randomInsult()+"\"");
				}else {
					BarkManager.getTaunt(attacker);//extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" + attacker.getTaunts().getTaunt()+"\"");
					}				
			}
		}
		
		if (!attacker.getNextAttack().isMagic()) {
		AttackReturn atr = this.handleAttack(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender);
		int damageDone = atr.damage;
		Color inlined_color = Color.WHITE;
		this.handleAttackPart2(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),Armor.armorEffectiveness,attacker,defender,damageDone);
		//armor quality handling
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
			String woundstr = inflictWound(attacker,defender,damageDone);
			song.addAttackHit(attacker,defender);
			if (defender.hasEffect(Effect.R_AIM)) {
				defender.getNextAttack().blind(1 + (percent));
			}
			if (!extra.printMode) {
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
				if (!extra.printMode) {
					inlined_color=extra.colorMix(Color.RED,Color.WHITE,.5f);
					extra.print(extra.inlineColor(inlined_color) +atr.stringer.replace("[*]", extra.inlineColor(inlined_color))+woundstr);
				}
			}else {
				if (!extra.printMode) {
					inlined_color=extra.colorMix(Color.ORANGE,Color.WHITE,.5f);
					extra.print(extra.inlineColor(inlined_color) +atr.stringer.replace("[*]", extra.inlineColor(inlined_color))+woundstr);
				}
			}
		}else {
			if (damageDone == -1) {
				song.addAttackMiss(attacker,defender);
				if (!extra.printMode) {
					inlined_color=extra.colorMix(Color.YELLOW,Color.WHITE,.5f);
					extra.print(extra.inlineColor(inlined_color) +atr.stringer.replace("[*]", extra.inlineColor(inlined_color)));
					Networking.sendStrong("PlayMiss|" + "todo" + "|");
				}
					extra.print(extra.inlineColor(extra.colorMix(Color.YELLOW,Color.WHITE,.3f))+(String)extra.choose(" They miss!"," The attack is dodged!"," It's a miss!"," It goes wide!"," It's not even close!"));
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
					if (damageDone == 0) {
						song.addAttackArmor(attacker,defender);
						if (!extra.printMode) {
							inlined_color = extra.colorMix(Color.BLUE,Color.WHITE,.5f);
							extra.print(extra.inlineColor(inlined_color)+atr.stringer.replace("[*]", extra.inlineColor(inlined_color)));
						}
					extra.print(" "+(String)extra.choose("But it is ineffective...","The armor deflects the blow!","However, the attack fails to deal damage through the armor."));
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
			song.addAttackHit(attacker,defender);
		}
		
			extra.println("");
			
				Person p = defender;
				float hpRatio = ((float)p.getHp())/(p.getMaxHp());
				//extra.println(p.getHp() + p.getMaxHp() +" " + hpRatio);
				if (Math.random()*5 >= 2) {song.addHealth(p);}
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
				defender.getBag().swapRace(RaceFactory.getRace(extra.choose("hiding-mimic","open-mimic")));
				Networking.clearSide(1);
				defender.getBag().graphicalDisplay(1,defender);
			}
		}
		if (defender.hasSkill(Skill.FELL_REAVER)) {
			defender.backupWeapon = defender.getBag().swapWeapon(defender.backupWeapon);
			switch (defender.getBag().getRace().name) {
			case "standing reaver":
				defender.getBag().swapRace(RaceFactory.getRace("crouched reaver"));
				
				break;
			case "crouched reaver":
				defender.getBag().swapRace(RaceFactory.getRace("standing reaver"));
				break;
			}
		}
		//TODO: bleedout death quotes
		boolean bMary = (defender.hasEffect(Effect.B_MARY));
		if (attacker.hasEffect(Effect.I_BLEED)) {
			attacker.takeDamage(1);
			if (bMary) {defender.addHp(1);}
			
		}
		if (attacker.hasEffect(Effect.BLEED)) {
			attacker.takeDamage(1);
			if (bMary) {defender.addHp(1);}
		}
		if (attacker.hasEffect(Effect.MAJOR_BLEED)) {
			attacker.takeDamage(2);
			if (bMary) {defender.addHp(2);}
		}
		if (attacker.hasEffect(Effect.BEES) && extra.chanceIn(1,5)) {
			extra.println("The bees sting!");
			attacker.takeDamage(1);
			
		}
		if (defender.getHp() <= 0) {
			Networking.send("PlayDelay|sound_fallover1|35|");
		}
		if (attacker.getHp() <= 0) {
			Networking.send("PlayDelay|sound_fallover1|35|");
		}
		if (canWait && !attacker.isPlayer()) {
			if (defender.isPlayer()) {
				Networking.waitIfConnected(300L+(long)delay);//was 500L
			}else {
			Networking.waitIfConnected(300L+(long)delay);}
		}
	}
	private String inflictWound(Person attacker2, Person defender2, int damage) {
		if ((defender2.hasSkill(Skill.TA_NAILS) && extra.randRange(1,5) == 1 )|| damage == 0) {//wounds no longer hit if dam=0, if this needs to change, fix woundstring printing as well
			return (" They shrug off the blow!");
		}else {
		defender2.inflictWound(attacker2.getNextAttack().getWound());
		
		switch (attacker2.getNextAttack().getWound()) {
		case CONFUSED:
			newTarget = true;
			break;
		case SLICE: case DICE:
			attacker2.advanceTime(10);
			break;
		case HACK: case TAT:
			defender2.takeDamage(damage/10);
			break;
		case CRUSHED:
			defender2.takeDamage((int)attacker2.getNextAttack().getTotalDam(attacker2.getBag().getHand())/10);
			break;
		case SCALDED: case FROSTBITE:
			defender2.takeDamage(attacker2.getMageLevel());
			break;
		}
		
		}
		return (" " +attacker2.getNextAttack().getWound().active);
	}


	private void handleMagicSpell(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		extra.print(extra.inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f)));
		extra.println(att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));
		if  (att.getSkill() == Skill.ELEMENTAL_MAGE) {
			defender.inflictWound(att.getWound());
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
			defender.takeDamage((int)((att.getBlunt())));}
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
		manOne.getNextAttack().blind(1 + manOne.getNextAttack().getSpeed()/1000.0);
		
	}
		
}
