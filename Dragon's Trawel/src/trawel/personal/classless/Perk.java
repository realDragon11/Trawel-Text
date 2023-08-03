package trawel.personal.classless;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public enum Perk implements HasSkills{
	RACIAL_SHIFTS("Flexible","Prone to changing its defense patterns.",EnumSet.of(Skill.RACIAL_SHIFTS))
	;
	public final String shortname, desc;
	public final Set<Skill> skills;
	Perk(String name, String description, Set<Skill> skillset){
		shortname = name;
		desc = description;
		skills = skillset;
	}
	
	@Override
	public Stream<Skill> collectSkills() {
		return skills.stream();
	}

}
