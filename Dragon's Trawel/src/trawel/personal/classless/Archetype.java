package trawel.personal.classless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import derg.menus.MenuItem;
import trawel.extra;
import trawel.personal.Person;
import trawel.personal.classless.Feat.FeatType;
import trawel.personal.classless.Skill.Type;

public enum Archetype implements IHasSkills{
	VIRAGO("virago","An expert on toxic incantations, salves, potions, and the like. Adept in supplemental spellcasting.",
			AType.ENTRY,EnumSet.of(AGroup.MAGIC,AGroup.CRAFT),EnumSet.of(FeatType.CURSES,FeatType.POTIONS),EnumSet.of(Skill.TOXIC_BREWS));
	
	private final String name, desc;
	private final Set<Skill> skills;
	private final int strength, dexterity;
	private final AType type;
	private final Set<AGroup> groups;
	private final Set<FeatType> fTypes;
	Archetype(String _name, String description, AType _type, Set<AGroup> _groups,Set<FeatType> _fTypes, Set<Skill> skillset){
		name = _name;
		desc = description;
		skills = skillset;
		strength = 0;
		dexterity = 0;
		type = _type;
		groups = _groups;
		fTypes = _fTypes;
	}
	
	public enum AType{
		RACIAL,//race archetypes
		ENTRY,//can appear as first archetype choice, also later on
		AFTER//can't appear as first choice, but can appear after first choice
	}
	
	public enum AGroup{
		DEXTERITY, STRENGTH,
		MAGIC, CRAFT
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
	 * used for the first set of archetypes, but also the 'not like the ones you have' in the set after
	 */
	public static List<Archetype> getFirst(int desiredAmount, Set<Archetype> has) {
		List<Archetype> list = new ArrayList<Archetype>();
		List<Archetype> options = new ArrayList<Archetype>();
		for (Archetype a: Archetype.values()) {
			if (a.type == AType.ENTRY) {
				options.add(a);
			}
		}
		
		for (int i = 0; i < 30 && list.size() < desiredAmount;i++) {
			Archetype choice = extra.randList(options);
			options.remove(choice);
			for (Archetype blocker: list) {
				if (!choice.canFirstWith(blocker)) {
					continue;
				}
			}
			list.add(choice);
		}
		return list;
	}
	
	/**
	 * used for the 'more like this' in the second set of archetypes
	 * <br>
	 * only makes sense when 'more like this' is one archetype, if you have more pick a random one
	 */
	public static List<Archetype> getAfter(int desiredAmount, Archetype has) {
		List<Archetype> list = new ArrayList<Archetype>();
		List<Archetype> options = new ArrayList<Archetype>();
		for (Archetype a: Archetype.values()) {
			if (a.type == AType.ENTRY || a.type == AType.AFTER) {
				options.add(a);
			}
		}
		
		for (int i = 0; i < 30 && list.size() < desiredAmount;i++) {
			Archetype choice = extra.randList(options);
			options.remove(choice);
			for (Archetype blocker: list) {
				if (!choice.doesAfterWith(blocker)) {
					continue;
				}
			}
			list.add(choice);
		}
		return list;
	}
	
	public boolean canFirstWith(Archetype t) {
		if (t.equals(this) || t.groups.equals(groups)) {//if we have the same exact groups or are the same
			return false;
		}
		//DOLATER: try to prevent overlap as well
		return true;
	}
	
	public boolean doesAfterWith(Archetype t) {
		//if we're not equal and we share one element
		return (!t.equals(this) && !Collections.disjoint(t.groups,groups));
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

	public static List<IHasSkills> getFeatChoices(Person person) {
		List<IHasSkills> list = new ArrayList<IHasSkills>();
		Set<Archetype> pAs = person.getArchSet();
		if (pAs.size() == 0) {
			list.addAll(getFirst(6,pAs));
			return list;
		}
		
		if (pAs.size() == 1) {
			list.addAll(getAfter(2,pAs.iterator().next()));
			list.addAll(getFirst(2,pAs));
			//fall through and fill the rest with normal feats
		}
		Set<Feat> fset = EnumSet.copyOf(person.getFeatSet());
		while (list.size() < 6) {
			Set<FeatType> allowSet = EnumSet.of(FeatType.COMMON);
			for (Archetype a: pAs) {
				allowSet.addAll(a.getFeatTypes());
			}
			Feat f = Feat.randFeat(allowSet,fset);//TODO: just commons for now when prototyping
			list.add(f);
			fset.add(f);
		}
		
		return list;
	}

	public Set<FeatType> getFeatTypes() {
		return fTypes;
	}
}
