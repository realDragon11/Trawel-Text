package trawel.fort;

import java.util.ArrayList;
import java.util.List;

public abstract class Laborer {

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
}
