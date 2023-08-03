package trawel.personal.classless;

import java.util.Set;
import java.util.stream.Stream;

import trawel.extra;

public enum Archetype implements HasSkills{
	;
	
	public final boolean entryLevel;
	public final String shortname, desc;
	public final Set<Skill> skills;
	Archetype(String name, String description, boolean entry, Set<Skill> skillset){
		shortname = name;
		desc = description;
		entryLevel = entry;
		skills = skillset;
	}
	
	@Override
	public Stream<Skill> collectSkills() {
		return skills.stream();
	}
	
	/**
	 * will attempt to return 6 values, containing Archtypes that give a good spread of starting stat choices
	 * @return a size 6 array, possibly partially empty
	 */
	public static Archetype[] getFirst() {
		Archetype[] list = new Archetype[6];
		Archetype[] vals = Archetype.values();
		int has = 1;
		list[0] = vals[extra.randRange(0,vals.length-1)];
		for (int i = 0; i < vals.length;i++) {
			Archetype local = vals[i];
			for (int j = 0; j < has;i++) {
				if (!list[j].canFirstWith(local)) {
					continue;
				}
				list[has++] = local;
			}
		}
		return list;
	}
	
	public boolean canFirstWith(Archetype t) {
		return !t.equals(this);//FIXME
	}
}
