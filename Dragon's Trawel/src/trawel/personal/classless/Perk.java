package trawel.personal.classless;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import trawel.personal.classless.Skill.Type;

public enum Perk implements HasSkills{
	RACIAL_SHIFTS("Flexible","Prone to changing its defense patterns.",EnumSet.of(Skill.RACIAL_SHIFTS))
	;
	private final String name, desc;
	private final Set<Skill> skills;
	private final int strength, dexterity;
	Perk(String _name, String description, Set<Skill> skillset){
		name = _name;
		desc = description;
		skills = skillset;
		strength = 0;
		dexterity = 0;
	}
	
	@Override
	public Stream<Skill> collectSkills() {
		return skills.stream();
	}
	
	@Override
	public String getText() {
		String str = name + ": "+desc;
		for (Skill s: skills) {
			if (s.getType() == Type.INTERNAL_USE_ONLY) {
				continue;
			}
			str += "\n "+HasSkills.padNewlines(s.disp());
		}
		return str;
	}
	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}

}
