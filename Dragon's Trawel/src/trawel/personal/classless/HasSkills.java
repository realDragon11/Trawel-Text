package trawel.personal.classless;

import java.util.Iterator;
import java.util.stream.Stream;

public interface HasSkills {

	public Stream<Skill> collectSkills();
	
	
	public static Stream<Skill> combine(Stream<Skill>...streams) {
		//https://stackoverflow.com/a/22741520
		//pain
		return Stream.of(streams).reduce(Stream.empty(), Stream::concat);
	}

}
