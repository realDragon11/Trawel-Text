package trawel.personal.classless;

import java.util.ArrayList;
import derg.ds.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.extra;
import trawel.personal.Person;
import trawel.personal.classless.Feat.FeatType;

public interface IHasSkills {

	//NOTE
	//can do EnumSet.of(Skill1,Skill2) etc etc
	
	public Stream<Skill> collectSkills();
	
	public static final Set<Skill> emptySkillSet = EnumSet.noneOf(Skill.class);
	
	public String getText();
	public String getBriefText();
	public String getOwnText();
	public String getStanceText();
	
	public int getStrength();
	public int getDexterity();
	public int getClarity();
	
	public static Stream<Skill> combine(Stream<Skill>...streams) {
		//https://stackoverflow.com/a/22741520
		//pain
		return Stream.of(streams).reduce(Stream.empty(), Stream::concat);
	}
	/**
	 * chain to pad newlines
	 */
	public static String padNewlines(String input) {
		return input.replaceAll(Pattern.quote("\n"),"\n ");
	}
	
	public static List<MenuItem> viewMenuItems(IHasSkills has, Person person) {
		List<MenuItem> list = new ArrayList<MenuItem>();
		list.add(new MenuLine() {

			@Override
			public String title() {
				if (has instanceof Feat) {
					return extra.ITEM_DESC_PROP+"Feat: " +has.getOwnText();
				}
				if (has instanceof Archetype) {
					return extra.ITEM_DESC_PROP+"Archetype: " +has.getOwnText();
				}
				if (has instanceof Perk) {
					return extra.ITEM_DESC_PROP+"Perk: " +has.getOwnText();
				}
				return has.getOwnText();
			}});
		list.add(new MenuLine() {

			@Override
			public String title() {
				return  extra.ITEM_DESC_PROP+" Strength: "+extra.ITEM_WANT_HIGHER + has.getStrength()+
						extra.ITEM_DESC_PROP+" Dexterity: "+extra.ITEM_WANT_HIGHER+ has.getDexterity()+
						extra.ITEM_DESC_PROP+ " Clarity: " +extra.ITEM_WANT_HIGHER+ has.getClarity();
			}});
		if (has instanceof Archetype) {
			Archetype arch = (Archetype)has;
			String str = extra.STAT_HEADER+" Unlocks:"+extra.ITEM_DESC_PROP;
			for (FeatType ft: arch.getFeatTypes()) {
				str += " "+ft;
			}
			String ftstr = str;//this is hilariously dumb, effectively final
			list.add(new MenuLine() {

				@Override
				public String title() {
					return ftstr;
				}});
		}
		String stanceDesc = has.getStanceText();
		if (stanceDesc != null) {
			list.add(new MenuLine() {

				@Override
				public String title() {
					return extra.ITEM_DESC_PROP+" Stance: "+extra.PRE_WHITE+stanceDesc;
				}});
		}
		//MAYBELATER: will display this even if there aren't any skills granted
		list.add(new MenuLine() {

			@Override
			public String title() {
				return extra.STAT_HEADER+"Skills:";
			}});
		has.collectSkills().forEach(skill -> list.add(skill.getMenuViewForPerson(person)));
		return list;
	}
	
	public static List<MenuItem> dispMenuItem(IHasSkills has) {
		List<MenuItem> list = new ArrayList<MenuItem>();
		list.add(new MenuSelect() {

			@Override
			public String title() {
				return has.menuName();
			}

			@Override
			public boolean go() {
				extra.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.addAll(viewMenuItems(has,null));
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "examine";
							}

							@Override
							public boolean go() {
								return has.goMenuItem();
							}});
						list.add(new MenuBack("back"));
						return list;
					}});
				return false;
			}});
		return list;
	}
	
	public String friendlyName();
	
	public default String menuName() {
		return friendlyName();
	}
	
	public boolean goMenuItem();
	
	/**
	 * mostly useful if you don't need to add them fully and just need this to compute half of something so you can tidy up later
	 */
	public Set<Skill> giveSet();
	
	public static int inCommon(Set<Skill> a, Set<Skill> b) {
		//they're enum sets so this could probably be faster actually?
		int common = 0;
		if (a.size() >= b.size()) {
			for (Skill s: b) {
				if (a.contains(s)) {
					common++;
				}
			}
		}else {
			for (Skill s: a) {
				if (b.contains(s)) {
					common++;
				}
			}
		}
		
		return common;
	}

}
