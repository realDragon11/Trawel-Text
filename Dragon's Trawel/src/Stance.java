import java.util.ArrayList;

/**
 *
 * @author Brian Malone
 * before 2/11/2018
 * A stance holds different attack instances.
 * Should only be used to hold the attacks of one weapon instance.
 */
public class Stance implements java.io.Serializable{
	//instance variables
	private int attackCount;
	private ArrayList<Attack> attacks = new ArrayList<Attack>();
	//constructor
	/**
	 * Make a stance instance.
	 */
	public Stance() {
	attackCount = 1;
	attacks.add(new Attack("examine",0,50,0,0,0,"You examine your foe..."));
	}
	
	public Stance(ArrayList<Attack> a) {
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
			if (attacks.size() == 3) {
			extra.println("     name                hit    delay    sharp    blunt     pierce");
			int j = 1;
			for(Attack i: attacks) {
				extra.print(j + "    ");
				i.display(1);
				j++;
			}
			}else {
			extra.println("     name\thit\tdelay\tsharp\tblunt\tpierce");
			int j = 1;
			for(Attack i: attacks) {
				extra.print(j + "    ");
				i.display(0);
				j++;
			}
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

	public Stance part(Person p) {
		return new Stance(this.randAtts(p));
	}
	

	private ArrayList<Attack> randAtts(Person p) {
		ArrayList<Attack> a = new ArrayList<Attack>();
		while (a.size() < 3) {
			int i = extra.randRange(0,attacks.size()-1);
			boolean doIt = true;
			Attack newAttack = attacks.get(i);
			for (Attack att: a) {
				if ((att.getName().equals(newAttack.getName()) || p.hasSkill(Skill.BERSERKER)) && newAttack.getName().contains("examine")) {
				doIt = false;
				break;
				}
			}
			if (doIt){a.add(newAttack.impair());
			}
		}
		return a;
	}
	
	public ArrayList<Attack> giveList(){
		return attacks;
	}
	
}
