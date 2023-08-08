package trawel.battle.attacks;
import java.util.ArrayList;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.extra;
import trawel.battle.attacks.WeaponAttackFactory.AttackMaker;
import trawel.personal.Person;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Skill;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;

/**
 *
 * @author Brian Malone
 * before 2/11/2018
 * A stance holds different attack instances.
 * Should only be used to hold the attacks of one weapon instance.
 */
public class Stance{
	//instance variables
	private WeaponType weap_source;
	private Skill skill_for;
	private IHasSkills skill_source;
	private List<Attack> attacks;
	private List<Float> rarities;
	private WeightedTable roller;
	private float totalWeight;
	/**
	 * if true, elemental damage occurs as if SBP damage wasn't there
	 * <br>
	 * if false, it's a rider
	 * <br>
	 * if true, it's the main course
	 * <br>
	 * <br>
	 * note that this current system wouldn't be able to handle a rock and a magic rock in the same thing
	 */
	public final boolean elementBypass;
	//constructor and initer
	public Stance(WeaponType t) {
		weap_source = t;
		attacks = new ArrayList<Attack>();
		rarities = new ArrayList<Float>();
		elementBypass = false;
	}
	
	public Stance(IHasSkills source, Skill _skill, boolean _elementBypass) {
		skill_source = source;
		skill_for = _skill;
		attacks = new ArrayList<Attack>();
		rarities = new ArrayList<Float>();
		elementBypass = _elementBypass;
	}
	
	public void finish() {
		totalWeight = 0;
		float[] rares = new float[rarities.size()];
		for (int i = 0; i < rares.length;i++) {
			rares[i] = rarities.get(i);
			totalWeight += rares[i];
		}
		roller = new WeightedTable(rares);
	}

	//instance methods
	
	/**
	 * Add an attack to the stance.
	 * @param newAttack (Attack)
	 */
	public void addAttack(Attack newAttack, float rarity) {
		attacks.add(newAttack);
		rarities.add(rarity);
		newAttack.setStance(this);
	}
	public void addAttack(AttackMaker newAttack) {
		addAttack(newAttack.finish(),newAttack.getRarity());
	}
	/**
	 * Returns the number of attacks in the stance.
	 * @return number of attacks in the stance (int)
	 */
	public int getAttackCount() {
		return attacks.size();
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
	

	public List<ImpairedAttack> randAtts(int count, Weapon weapon, Person attacker, Person defender) {
		List<ImpairedAttack> a = new ArrayList<ImpairedAttack>();
		while (a.size() < count) {
			a.add(attacks.get(roller.random(extra.getRand())).impair(attacker, weapon,defender));
		}
		return a;
	}
	
	public List<Attack> giveList(){
		return attacks;
	}

	public WeaponType getWType() {
		return weap_source;
	}

	public float getRarity(int i) {
		return rarities.get(i);
	}

	public float getWeight(int i) {
		return rarities.get(i)/totalWeight;
	}

	public float getRarity(Attack attack) {
		return rarities.get(attacks.indexOf(attack))/totalWeight;
	}

	public Skill getSkill() {
		return skill_for;
	}
	
	public IHasSkills getSkillSource() {
		return skill_source;
	}
	
}
