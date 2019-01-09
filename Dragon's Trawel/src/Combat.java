import java.util.ArrayList;
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
				attacker.advanceTime(1);
			}
			defender.advanceTime(attacker.getTime());
			
			handleTurn( attacker,  defender,  song);
			
			
			if (manOne.isAlive() && manTwo.isAlive()) {
			setAttack(attacker,defender);}
		}
		while(manOne.isAlive() && manTwo.isAlive());
		
		extra.println(extra.choose("The dust settles...","The body drops to the floor.","Death has come.","The battle is over."));
		extra.println(defender.getName() + extra.choose(" lies dead..."," walks the earth no more..."," has been slain."));
		song.addKill(attacker,defender);
		attacker.getBag().getHand().addKill();
		if (extra.chanceIn(1,2) || attacker.getLevel() < defender.getLevel()) {
			attacker.getTaunts().addTaunt("It was I who " + extra.choose("slew","slaughtered","struck down","killed") + " " + defender.getName()+ "!");
		}
		if (extra.chanceIn(1,2)) {
			attacker.getTaunts().addTaunt(defender.getTaunts().getTaunt());
		}
		if (extra.chanceIn(1,2)) {
			attacker.getTaunts().addBoast(defender.getTaunts().getBoast());
		}
		//if (extra.chanceIn(1,2) || attacker.getLevel() < defender.getLevel()) {
			//attacker.getTaunts().addBoast("It was I who " + extra.choose("slew","slaughtered","struck down","killed") + " " + defender.getName()+ "!");
		//}//t'was I who struck down X who slew Y who slew Z!
		if (extra.chanceIn(1,3)) {
			attacker.getTaunts().removeTaunt();
		}
		if (extra.chanceIn(1,4)) {
			attacker.getTaunts().removeBoast();
		}
	}
	
	
	public Combat(World w,ArrayList<Person>... people) {
		int size = people.length;
		BardSong song = w.startBardSong();
		ArrayList<Person> totalList = new ArrayList<Person>();
		ArrayList<Person> killList = new ArrayList<Person>();
		
		for (ArrayList<Person> peoples: people) {
			for (Person p: peoples) {
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
				p.getBag().graphicalDisplay(-1);
				otherperson.getBag().graphicalDisplay(-1);
			}
			setAttack(p,otherperson);
			p.getNextAttack().defender = otherperson;
			}
		}
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
				lowestDelay--;
			}
			
			
			for (Person p: totalList) {
				p.advanceTime(lowestDelay);
			}
			
			Person defender = quickest.getNextAttack().defender;
			boolean wasAlive = defender.isAlive();
			handleTurn(quickest,defender,song);
			if (!defender.isAlive() && wasAlive) {
				extra.println("They die!");
				quickest.getBag().getHand().addKill();
				song.addKill(quickest,defender);
				totalList.remove(defender);
				killList.add(defender);
				for (ArrayList<Person> list: people) {
					if (list.contains(defender)) {
						list.remove(defender);
						break;
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
				int rand = extra.randRange(0,size-1);
				ArrayList<Person> otherpeople = people[rand];
				if (otherpeople.contains(quickest) || otherpeople.size() == 0) {
					continue;
				}
				otherperson = extra.randList(otherpeople);
			}
			if (quickest.isPlayer()) {
				otherperson.displayStatsShort();
				quickest.getBag().graphicalDisplay(-1);
				otherperson.getBag().graphicalDisplay(-1);
			}
			setAttack(quickest,otherperson);
			quickest.getNextAttack().defender = otherperson;
			
		}
		
		survivors = totalList;
		killed = killList;
		
		
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
	public int handleAttack(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		if (att.getName().contains("examine")){
			if  (!attacker.isPlayer()) {
				extra.println("They examine you...");
				return -2;
			}
			attacker.displayStats();
			attacker.displayArmor();
			attacker.displayHp();
			defender.displayStats();
			defender.displayArmor();
			defender.displayHp();
			extra.print(att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));
			return -2;
		}
		if (defender.isAlive()) {
		extra.print(att.attackStringer(attacker.getName(),defender.getName(),off.getHand().getName()));}else {
			extra.print(att.attackStringer(attacker.getName(),defender.getName() + "'s corpse",off.getHand().getName()));	
		}
		double damMod = off.getDam();
		if ((def.getDodge()/(att.getHitmod()*off.getAim()))*Math.random() > 1.0){
			return -1;//do a dodge
		}
		//return the damage-armor, with each type evaluated individually
		return (int)((extra.zeroOut((att.getSharp()*damMod)-(def.getSharp(att.getSlot())*armMod))*Math.random())+extra.zeroOut((att.getBlunt()*damMod)-(def.getBlunt(att.getSlot())*armMod)*Math.random())+extra.zeroOut((att.getPierce()*damMod)-(def.getPierce(att.getSlot())*armMod)*Math.random()));
		
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
	public void handleTurn(Person attacker, Person defender, BardSong song) {
		if (defender.hasSkill(Skill.COUNTER)) {
			defender.advanceTime(1);
		}
		if (extra.chanceIn(1,4)) {
			if (extra.chanceIn(1,3)) {
					extra.println(attacker.getName() + " "+extra.choose("shouts","screams","boasts")+ " \"" + attacker.getTaunts().getBoast()+"\"");		
			}else {
				if (attacker.isRacist() && !attacker.getBag().getRace().equals(defender.getBag().getRace()) && extra.chanceIn(1,3)) 
				{
					extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" +defender.getBag().getRace().randomInsult()+"\"");
				}else {
					extra.println(attacker.getName() + " "+extra.choose("shouts","screams","taunts")+ " \"" + attacker.getTaunts().getTaunt()+"\"");
					}				
			}
		}
		
		if (!attacker.getNextAttack().isMagic()) {
		int damageDone = this.handleAttack(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),0.05,attacker,defender);
		this.handleAttackPart2(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),0.05,attacker,defender,damageDone);
		if (damageDone > 0) {
			song.addAttackHit(attacker,defender);
			if (defender.takeDamage(damageDone)) {
				//extra.print(" " + choose("Striking them down!"," They are struck down."));
			}
		}else {
			if (damageDone == -1) {
				song.addAttackMiss(attacker,defender);
					extra.print((String)extra.choose(" They miss!"," The attack is dodged!"," It's a miss!"," It goes wide!"," It's not even close!"));
					if (defender.hasSkill(Skill.SPEEDDODGE)) {
						defender.advanceTime(10);
						if (defender.hasSkill(Skill.DODGEREF)) {
							defender.addHp(attacker.getLevel());
						}
					}
				}else {
					if (damageDone == 0) {
						song.addAttackArmor(attacker,defender);
					extra.print(" "+(String)extra.choose("But it is ineffective...","The armor deflects the blow!","However, the attack fails to deal damage through the armor."));
					if (defender.hasSkill(Skill.ARMORHEART)) {
						defender.addHp(attacker.getLevel());
					}
					if (defender.hasSkill(Skill.ARMORSPEED)) {
						defender.advanceTime(10);
					}
					}
				}
		}
		}else {
			//the attack is a magic spell
			handleMagicSpell(attacker.getNextAttack(),defender.getBag(),attacker.getBag(),0.05,attacker,defender);
		}
		
			extra.println("");
			
				Person p = defender;
				float hpRatio = ((float)p.getHp())/(p.getMaxHp());
				//extra.println(p.getHp() + p.getMaxHp() +" " + hpRatio);
				if (Math.random()*5 >= 2) {song.addHealth(p);}
				if (hpRatio == 1) {
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
	}
	private void handleMagicSpell(Attack att, Inventory def,Inventory off, double armMod, Person attacker, Person defender) {
		def.burn(def.getFire(att.getSlot())*(att.getSharp()/100),att.getSlot());
		
		defender.advanceTime(-((att.getPierce()/100)*defender.getTime()*def.getFreeze(att.getSlot())));
		
		defender.takeDamage((int)((att.getBlunt())*def.getShock(att.getSlot())));
		
	}


	private void setAttack(Person manOne, Person manTwo) {
		manOne.setAttack(AIClass.chooseAttack(manOne.getStance().part(manOne),manOne.getIntellect(),this,manOne,manTwo));
		
	}
}
