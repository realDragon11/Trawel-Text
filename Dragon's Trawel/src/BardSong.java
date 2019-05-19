import java.util.ArrayList;

public class BardSong implements java.io.Serializable {

	private ArrayList<String> strings = new ArrayList<String>();

	public BardSong(Person manOne, Person manTwo) {
		if (extra.chanceIn(1, 2)) {
			Person manThree = manTwo;
			manTwo = manOne;
			manOne = manThree;
		}
		add(extra.choose(manOne.getName() + extra.choose(" faced down "," challenged "," fought ") + manTwo.getName() + "."));
	}
	
	public BardSong() {
		add("A battle began.");
	}

	public ArrayList<String> getStrings() {
		return strings;
	}

	public void setStrings(ArrayList<String> strings) {
		this.strings = strings;
	}
	
	public void add(String s) {
		strings.add(s);
	}
	
	public void printSong() {
		for (String s: strings) {
			extra.println(s);
		}
	}


	public void addHealth(Person p) {
		float hpRatio = ((float)p.getHp())/(p.getMaxHp());
		if (hpRatio == 1) {
			add(p.getName() + " was untouched.");
		}else {
		if (hpRatio > .9) {
			add(p.getName() + " looked barely scratched.");
		}else {
			if (hpRatio > .7) {
				add(p.getName() + " looked a little hurt.");
			}else {
				if (hpRatio > .5) {
					add(p.getName() + " looked a bit damaged.");
				}else {
					if (hpRatio > .25) {
						add(p.getName() + " looked moderately damaged.");
					}else {
						if (hpRatio > .1) {
							add(p.getName() + " looked close to death.");
						}else {
							add(p.getName() + " looked like they were dying.");
						}
					}
				}
			}
		}
		}
		
	}

	public void addKill(Person attacker, Person defender) {
		add(extra.choose(attacker.getName() + " slew " + defender.getName()+"."));
		
	}

	public void addAttackHit(Person attacker, Person defender) {
		add(attacker.getNextAttack().attackStringer(attacker.getName(),defender.getName(),attacker.getBag().getHand().getName()));
		
	}

	public void addAttackMiss(Person attacker, Person defender) {
		add(extra.choose(attacker.getName() + " missed " + defender.getName() + ".",defender.getName() + " leaped out of the way!"));
		
	}

	public void addAttackArmor(Person attacker, Person defender) {
		add(extra.choose(attacker.getName() + " couldn't damage the armor."));
	}



}
