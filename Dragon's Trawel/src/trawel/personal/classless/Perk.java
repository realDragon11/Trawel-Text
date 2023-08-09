package trawel.personal.classless;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import trawel.extra;
import trawel.personal.classless.Skill.Type;

public enum Perk implements IHasSkills{
	EMPTY("","",IHasSkills.emptySkillSet),
	RACIAL_SHIFTS("Flexible","Prone to changing its defense patterns.",EnumSet.of(Skill.RACIAL_SHIFTS)),
	SKY_BLESS_1("Leaf on the Wind","Has a minor blessing from the sky, granting them paranatural speed.",EnumSet.of(Skill.SPEEDDODGE,Skill.BLITZ)),
	SKY_BLESS_2("Growing Storm","Has a blessing from the sky, granting mobility and senses that heal them when used.",EnumSet.of(Skill.DODGEREF),0,4,1)
	,CULT_LEADER_BLOOD("Cult Leader (Blood)","Chosen by the cult of blood.",EnumSet.of(Skill.BLOODTHIRSTY))//also used by npcs
	;
	private final String name, desc;
	private final Set<Skill> skills;
	private final int strength, dexterity, clarity;
	
	Perk(String _name, String description, Set<Skill> skillset, int str, int dex, int cla){
		name = _name;
		desc = description;
		skills = skillset;
		strength = str;
		dexterity = dex;
		clarity = cla;
	}
	
	Perk(String _name, String description, Set<Skill> skillset){
		this(_name,description,skillset,0,0,0);
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
	
	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}
	
	@Override
	public int getClarity() {
		return clarity;
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
