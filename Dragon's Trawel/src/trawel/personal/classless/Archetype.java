package trawel.personal.classless;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import trawel.extra;
import trawel.personal.classless.Skill.Type;

public enum Archetype implements IHasSkills{
	EMPTY("","",AType.RACIAL,EnumSet.of(AGroup.STRENGTH),IHasSkills.emptySkillSet);
	
	private final String name, desc;
	private final Set<Skill> skills;
	private final int strength, dexterity;
	private final AType type;
	private final Set<AGroup> groups;
	Archetype(String _name, String description, AType _type, Set<AGroup> _groups, Set<Skill> skillset){
		name = _name;
		desc = description;
		skills = skillset;
		strength = 0;
		dexterity = 0;
		type = _type;
		groups = _groups;
	}
	
	public enum AType{
		RACIAL,//race archetypes
		ENTRY,//can appear as first archetype choice, also later on
		AFTER//can't appear as first choice, but can appear after first choice
	}
	
	public enum AGroup{
		DEXTERITY, STRENGTH,
		MAGIC
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
			str += "\n "+IHasSkills.padNewlines(s.disp());
		}
		return str;
	}
	
	@Override
	public String getOwnText() {
		return name + ": "+desc;
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
			if (local.type != AType.ENTRY) {
				continue;
			}
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

	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}
	
	@Override
	public String friendlyName() {
		return name;
	}

	@Override
	public boolean goMenuItem() {
		extra.println("n/a");
		return false;
	}
}
