package trawel.personal.classless;

import java.util.ArrayList;
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

public interface IHasSkills {

	//NOTE
	//can do EnumSet.of(Skill1,Skill2) etc etc
	
	public Stream<Skill> collectSkills();
	
	public static final Set<Skill> emptySkillSet = EnumSet.noneOf(Skill.class);
	
	public String getText();
	
	public int getStrength();
	public int getDexterity();
	
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
				return has.friendlyName() + ": " + has.getText();
			}});
		has.collectSkills().forEach(Skill::getMenuView);
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

}