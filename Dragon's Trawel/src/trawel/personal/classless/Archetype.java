package trawel.personal.classless;

import java.util.Set;
import java.util.stream.Stream;

import trawel.extra;
import trawel.personal.classless.Skill.Type;

public enum Archetype implements IHasSkills{
	EMPTY("","",false,null,IHasSkills.emptySkillSet);
	
	private final boolean entryLevel;
	private final String name, desc;
	private final Set<Skill> skills;
	private final int strength, dexterity;
	private final AType type;
	Archetype(String _name, String description, boolean entry, AType _type, Set<Skill> skillset){
		name = _name;
		desc = description;
		entryLevel = entry;
		skills = skillset;
		strength = 0;
		dexterity = 0;
		type = _type;
	}
	
	public enum AType{
		RACIAL
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
			if (!local.entryLevel) {
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
