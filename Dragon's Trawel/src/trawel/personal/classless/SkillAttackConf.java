package trawel.personal.classless;

import java.util.ArrayList;
import java.util.List;

import trawel.battle.attacks.Attack;
import trawel.battle.attacks.ImpairedAttack;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.personal.Person;

public class SkillAttackConf {

	/**
	 * what skill this conf is for
	 */
	private Skill skill;
	/**
	 * the stance source
	 */
	private IHasSkills source;
	/**
	 * sometimes more than one source is allowed
	 */
	private IHasSkills source2;
	
	private transient List<Attack> attList;
	
	public SkillAttackConf(Skill _skill, IHasSkills _source, IHasSkills _source2) {
		skill = _skill;
		source = _source;
		source2 = _source2;
	}
	
	public void update(Skill _skill, IHasSkills _source, IHasSkills _source2) {
		skill = _skill;
		source = _source;
		source2 = _source2;
	}
	
	/**
	 * do not modify, we own it
	 */
	public List<Attack> getAttackList(){
		if (attList != null) {
			return attList;
		}
		if (source2 == null) {
			attList = WeaponAttackFactory.getStance(source).giveList();
			return attList;
		}else {
			List<Attack> list = new ArrayList<Attack>();
			list.addAll(WeaponAttackFactory.getStance(source).giveList());
			list.addAll(WeaponAttackFactory.getStance(source2).giveList());
			//don't put it in object list yet in case of threading
			//this way even if there's a race condition nothing changes
			//the race doesn't impact the final list contents
			attList = list;
			return list;
		}
	}
	/**
	 * only gives one at a time, you should need 2-3 max
	 */
	public ImpairedAttack randAttack(Person attacker, Person defender) {
		if (source2 != null) {
			return WeaponAttackFactory.rollAttack(source,source2,attacker,defender);
		}else {
			return WeaponAttackFactory.rollAttack(source,attacker,defender);
		}
	}

	public String getText() {
		return skill.getName() + ": " + source.friendlyName();
	}
}
