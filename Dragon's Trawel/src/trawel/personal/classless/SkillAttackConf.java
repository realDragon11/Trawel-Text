package trawel.personal.classless;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import trawel.battle.attacks.Attack;
import trawel.battle.attacks.ImpairedAttack;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.personal.Person;

public class SkillAttackConf implements Serializable{

	/**
	 * what skill this conf is for
	 */
	private Skill skill;
	/**
	 * the stance source
	 */
	private IHasSkills source;
	
	private transient List<Attack> attList;
	
	public SkillAttackConf(Skill _skill, IHasSkills _source) {
		skill = _skill;
		source = _source;
	}
	
	public void update(Skill _skill, IHasSkills _source) {
		skill = _skill;
		source = _source;
	}
	
	/**
	 * do not modify, we own it
	 */
	public List<Attack> getAttackList(){
		if (attList != null) {
			return attList;
		}
		attList = WeaponAttackFactory.getStance(source).giveList();
		return attList;
	}
	/**
	 * only gives one at a time, you should need 2-3 max
	 */
	public ImpairedAttack randAttack(Person attacker, Person defender) {
		return WeaponAttackFactory.rollAttack(source,attacker,defender);
	}

	public String getText() {
		return skill.getName() + ": " + source.friendlyName();
	}
}
