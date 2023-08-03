package trawel.battle.attacks;
import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.personal.Person;
import trawel.personal.classless.Skill;

/**
 *
 * @author Brian Malone
 * before 2/11/2018
 * A stance holds different attack instances.
 * Should only be used to hold the attacks of one weapon instance.
 */
public class Stance implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//instance variables
	private int attackCount;
	private List<Attack> attacks = new ArrayList<Attack>();
	//constructor
	/**
	 * Make a stance instance.
	 */
	public Stance() {
		attackCount = 0;
		//attacks.add(new Attack("examine",0,50,0,0,0,"You examine your foe...",-1,"examine"));//TODO: replace this system
	}
	
	public Stance(List<Attack> a) {
		attackCount = a.size();
		attacks = a;
	}

	//instance methods
	
	/**
	 * Add an attack to the stance.
	 * @param newAttack (Attack)
	 */
	public void addAttack(Attack newAttack) {
		attacks.add(newAttack);
		attackCount++;
	}
	/**
	 * Returns the number of attacks in the stance.
	 * @return number of attacks in the stance (int)
	 */
	public int getAttackCount() {
		return attackCount;
	}
	/**
	 * Returns the reference to the attack in slot slot.
	 * @param slot (int)
	 * @return attack reference (Attack)
	 */
	public Attack getAttack(int slot) {
		return attacks.get(slot);
	}

	public void display(int style) {
		if (style == 1) {
			//extra.println("     name\thit\tdelay\tsharp\tblunt\tpierce");
			extra.println("     name                hit    delay    sharp    blunt     pierce");
			for(Attack i: attacks) {
				extra.print("-    ");
				i.display(1);
			}
		}
		
	}
	
	public Attack getAttack() {;
		while (true) {
		int j = 0;
		int parsed = extra.inInt(3);


		for (Attack i: attacks) {
			if (parsed == j+1) {
				return attacks.get(j);
			}
			j++;
			
		}
		}
	}

	public Stance part(Person p, Person defender) {
		return new Stance(this.randAtts(p,defender));
	}
	

	private List<Attack> randAtts(Person p, Person defender) {
		List<Attack> a = new ArrayList<Attack>();
		int atts = p.attacksThisAttack();
		while (a.size() < atts) {
			int i = extra.randRange(0,attacks.size()-1);
			boolean doIt = true;
			Attack newAttack = attacks.get(i);
			for (Attack att: a) {
				if ((att.getName().equals(newAttack.getName()) || p.hasSkill(Skill.BERSERKER)) && newAttack.getName().equals("examine")) {
				doIt = false;
				if (p.hasSkill(Skill.BERSERKER)){
					//extra.println("it worked");
				}
				break;
				}
			}
			if (doIt){a.add(newAttack.impair(p.getBag().getHand().getLevel(), defender,p.getBag().getHand(),p));
			}
		}
		return a;
	}
	
	public List<Attack> giveList(){
		return attacks;
	}
	
}
