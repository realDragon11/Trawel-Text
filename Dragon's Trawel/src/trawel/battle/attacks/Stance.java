package trawel.battle.attacks;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.extra;
import trawel.battle.attacks.WeaponAttackFactory.AttackBonus;
import trawel.battle.attacks.WeaponAttackFactory.AttackMaker;
import trawel.personal.Person;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Skill;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;

/**
 *
 * @author dragon
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
	private AttackLevel leveler;
	
	static interface AttackLevel {
		
		public int getEffectiveLevel(Person p);
	}
	
	/**
	 * how many attacks the weapon starts with (if bonus skill attacks > 0, this won't equal the number of weapon attacks)
	 */
	private int baseAttacks;
	/**
	 * how many attacks are bonus attacks of skills instead of a real attack
	 */
	private int bonusSkillAttacks;

	//constructor and initer
	public Stance(WeaponType t) {
		weap_source = t;
		attacks = new ArrayList<Attack>();
		rarities = new ArrayList<Float>();
		baseAttacks = 3;
		bonusSkillAttacks = 0;
	}
	
	public Stance(IHasSkills source, Skill _skill) {
		skill_source = source;
		skill_for = _skill;
		attacks = new ArrayList<Attack>();
		rarities = new ArrayList<Float>();
		baseAttacks = 0;
		bonusSkillAttacks = 0;
	}
	
	public void finish() {
		if (rarities.size() == 0) {
			return;//empty, null wand
		}
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
	public void addAttack(AttackMaker newAttack,AttackBonus _rider) {
		addAttack(newAttack.finish().setRider(_rider),newAttack.getRarity());
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
			for(Attack i: attacks) {
				extra.print("-");
				i.display(0);
			}
		}
		
	}
	public void display(Weapon w) {
		List<Attack> sortedDPIList = new ArrayList<Attack>();
		sortedDPIList.addAll(attacks);
		sortedDPIList.sort(new Comparator<Attack>() {
			@Override
			public int compare(Attack o1, Attack o2) {
				return (int)Math.signum(o2.getDPI()-o1.getDPI());
			}});
		for(Attack a: sortedDPIList) {
			a.display(w);
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

	public int getBaseAttacks() {
		return baseAttacks;
	}

	public int getBonusSkillAttacks() {
		return bonusSkillAttacks;
	}
	/**
	 * default is 3
	 */
	public Stance setBaseAttacks(int set) {
		baseAttacks = set;
		return this;
	}
	/**
	 * is BONUS, takes part of base attacks and tries to give them to skill attacks in order
	 */
	public Stance setBonusSkillAttacks(int set) {
		bonusSkillAttacks = set;
		return this;
	}
	
	public int getEffectiveLevelFor(Person p) {
		return leveler.getEffectiveLevel(p);
	}
	
	public void setLeveler(AttackLevel level) {
		leveler = level;
	}

	protected AttackLevel getLeveler() {
		return leveler;
	}
	
}
