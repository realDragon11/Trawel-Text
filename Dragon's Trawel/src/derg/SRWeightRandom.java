package derg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.tommyettinger.random.EnhancedRandom;
import com.github.tommyettinger.random.WhiskerRandom;

public class SRWeightRandom extends StringResult {

	private List<StringFloatWeight> contents;
	
	private static EnhancedRandom random = new WhiskerRandom(); 
	
	public SRWeightRandom(List<StringFloatWeight> list) {
		contents = list;
	}

	@Override
	public String stringMethod() {
		return "PlainRandom";
	}
	
	@Override
	public String next() {
		return any();
	}

	@Override
	public String any() {
		List<String> list = backing();
		return list.get((int)(Math.random()*list.size()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> backing(){
		return contents.stream().flatMap(a -> Arrays.asList(new String[] {a.result}).stream()).collect(Collectors.toList());
	}
	
	@Override
	public String with(StringContext context) {
		return any();
	}
	
	//not keen to have so much overhead, but tuples
	//could do sync'd lists but that'd be annoying for replication
	public class StringFloatWeight{
		public final String result;
		public final float[] floats;
		public final String[] strings;
		public StringFloatWeight(String result, float[] floats, String[] strings) {
			this.result = result;
			this.floats = floats;
			this.strings = strings;
		}
	}
	
}
