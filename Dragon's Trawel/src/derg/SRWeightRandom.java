package derg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.tommyettinger.random.EnhancedRandom;
import com.github.tommyettinger.random.WhiskerRandom;

public class SRWeightRandom extends StringResult {

	private List<StringFloatWeight> contents;
	private ToleranceFunction<Boolean,Float,Float> tolerance;
	
	@FunctionalInterface
	public interface ToleranceFunction<Return, a, b> {
	    public Return apply(a one, b two);
	}
	
	private static EnhancedRandom random = new WhiskerRandom(); 
	
	public SRWeightRandom(List<StringFloatWeight> list,ToleranceFunction<Boolean,Float,Float> tolerance) {
		contents = list;
		this.tolerance = tolerance;
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
	
	@Override
	public List<String> backing(){
		return contents.stream().flatMap(a -> Arrays.asList(new String[] {a.result}).stream()).collect(Collectors.toList());
	}
	
	private static final Predicate<Object[]> flattener = (Object[] fs) -> {return ((ToleranceFunction<Boolean,Float,Float>)fs[0]).apply((float)fs[1], (float)fs[2]);};
	
	@Override
	public String with(StringContext context) {
		//context.floatMap.getOrDefault(fw.weights, -1f)
		contents.stream().filter(a -> a.weights.stream().flatMap(base -> {return Arrays.asList(new Object[][] {new Object[] {tolerance,base.f,context.floatMap.getOrDefault(base.s, -1f)}}).stream();}).allMatch(flattener));
		//.filter((a) -> ((context.floatMap.getOrDefault(fw.weights, -1f))));
		//context.floatMap.getOrDefault(s.weights, -1f)
		//allMatch
		return any();
	}
	
	//not keen to have so much overhead, but tuples
	//could do sync'd lists but that'd be annoying for replication
	public class StringFloatWeight{
		public final String result;
		//public final float[] floats;
		//public final String[] strings;
		public final List<FloatWeight> weights;
		//public final Map<String,Float> map = new HashMap<String,Float>;
		/**
		 * 
		 * @param result
		 * @param floats, use -1 to indicate should not have, 0 to indicate is fine but doesn't have [-2 is used internally for normal doesn't have]
		 * @param strings
		 */
		public StringFloatWeight(String result, FloatWeight[] weights) {
			this.result = result;
			/*
			for (int i = floats.length-1;i>=0;i--) {
				map.put(strings[i], floats[i]);
			}*/
			//this.floats = floats;
			//this.strings = strings;
			this.weights = Arrays.asList(weights);
		}
		
		/*
		//unsure if hashmap would be better given overhead
		public float getWeight(String name) {
			return map.getOrDefault(name,-2f);
		}
		*/
	}
	public class FloatWeight{
		public final float f;
		public final String s;
		
		public FloatWeight(String str, float floa) {
			s = str;
			f = floa;
		}
	}
	
}
