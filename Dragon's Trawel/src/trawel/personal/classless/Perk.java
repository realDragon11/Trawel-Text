package trawel.personal.classless;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public enum Perk implements HasSkills{
	RACIAL_SHIFTS("Flexible","Prone to changing its defense patterns.",EnumSet.of(Skill.RACIAL_SHIFTS))
	;
	public final String name, desc;
	public final Set<Skill> skills;
	Perk(String _name, String description, Set<Skill> skillset){
		name = _name;
		desc = description;
		skills = skillset;
	}
	
	@Override
	public Stream<Skill> collectSkills() {
		return skills.stream();
	}
	
	@Override
	public String getText() {
		String str = name + ": "+desc;
		for (Skill s: skills) {
			str += "\n "+HasSkills.padNewlines(s.disp());
		}
		return str;
	}

}
