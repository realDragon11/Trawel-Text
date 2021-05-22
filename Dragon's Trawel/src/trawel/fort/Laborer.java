package trawel.fort;

import java.util.ArrayList;
import java.util.List;

import trawel.randomLists;

public class Laborer implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public LaborType type;
	public List<LSkill> lSkills = new ArrayList<LSkill>();
	public int getSkillCount(SubSkill s) {
		for (LSkill l: lSkills) {
			if (l.skill.equals(s)) {
				return l.value;
			}
		}
		return 0;
		
	}
	
	public Laborer(LaborType type) {
		name = randomLists.randomFirstName();
		this.type = type;
	}
}
