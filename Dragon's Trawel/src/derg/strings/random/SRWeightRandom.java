package derg.strings.random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import derg.strings.fluffer.StringResult;
import derg.strings.misc.StringContext;
import trawel.core.Rand;

public class SRWeightRandom extends StringResult {

	private List<StringFloatWeight> contents;
	private ToleranceFunction<Boolean,Float,Float> tolerance;
	
	@FunctionalInterface
	public interface ToleranceFunction<Return, a, b> {
	    public Return apply(a one, b two);
	}
	
	public SRWeightRandom(List<StringFloatWeight> list,ToleranceFunction<Boolean,Float,Float> tolerance) {
		contents = list;
		this.tolerance = tolerance;
	}
	
	public SRWeightRandom addWeight(String name, String[] strs, Float[] floats) {
		final List<FloatWeight> finfws = new ArrayList<FloatWeight> ();
		for (int i = strs.length-1;i >=0;i--) {
			finfws.add(new FloatWeight(strs[i],floats[i]));
		}
		contents.add(new StringFloatWeight(name,finfws));
		return this;
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
		return contents.stream().map(a -> a.result).collect(Collectors.toList());
	}
	
	private static final Predicate<Object[]> flattener = (Object[] fs) -> {return ((ToleranceFunction<Boolean,Float,Float>)fs[0]).apply((float)fs[1], (float)fs[2]);};
	
	@Override
	public String with(StringContext context) {
		List<StringFloatWeight> list = contents.stream()
		.filter(a -> a.weights.stream().map(base -> {return new Object[] {tolerance,base.f,context.floatMap.getOrDefault(base.s, -2f)};}).allMatch(flattener))
		.collect(Collectors.toList());
		return list.get(Rand.getRand().nextInt(list.size())).result;
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
		public StringFloatWeight(String result, List<FloatWeight> weights) {
			this.result = result;
			this.weights = weights;
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
