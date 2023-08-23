package trawel.personal.classless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
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
	public String getOwnText();
	
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
	
	public static List<MenuItem> viewMenuItems(IHasSkills has) {
		List<MenuItem> list = new ArrayList<MenuItem>();
		list.add(new MenuLine() {

			@Override
			public String title() {
				if (has instanceof Feat) {
					return "Feat: " +has.getOwnText();
				}
				if (has instanceof Archetype) {
					return "Archetype: " +has.getOwnText();
				}
				if (has instanceof Perk) {
					return "Perk: " +has.getOwnText();
				}
				return has.getOwnText();
			}});
		list.add(new MenuLine() {

			@Override
			public String title() {
				return " Strength: " + has.getStrength() + " Dexterity: " + has.getDexterity() + " Clarity: " + has.getClarity();
			}});
		if (has instanceof Archetype) {
			Archetype arch = (Archetype)has;
			String str = " Unlocks:";
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
		has.collectSkills().forEach(skill -> list.add(skill.getMenuView()));
		return list;
	}
	
	public static List<MenuItem> dispMenuItem(IHasSkills has) {
		List<MenuItem> list = new ArrayList<MenuItem>();
		list.add(new MenuSelect() {

			@Override
			public String title() {
				return has.friendlyName();
			}

			@Override
			public boolean go() {
				extra.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.addAll(viewMenuItems(has));
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
	
	public boolean goMenuItem();
	
	//FIXME: temp attack level for new skill attacks, better formula should be stored in stance somehow
	public default int getAttackLevel(Person attacker) {
		return Math.min(attacker.getClarity()/40,attacker.getLevel());
	}
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
