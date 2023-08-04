package trawel.personal.classless;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface HasSkills {

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
	
	

}
