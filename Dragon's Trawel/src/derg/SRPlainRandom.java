package derg;

import java.util.Arrays;
import java.util.List;

import com.github.tommyettinger.random.EnhancedRandom;
import com.github.tommyettinger.random.WhiskerRandom;

import trawel.extra;

public class SRPlainRandom extends StringResult {

	private List<String> contents;
	
	public SRPlainRandom(List<String> list) {
		contents = list;
	}
	
	public SRPlainRandom(String...strings) {
		contents = Arrays.asList(strings);
	}
	
	@Override
	public String stringMethod() {
		return "PlainRandom";
	}
	
	@Override
	public String next() {
		return contents.get(extra.getRand().nextInt(contents.size()));
	}

	@Override
	public String any() {
		return contents.get((int)(Math.random()*contents.size()));
	}
	
	@Override
	public List<String> backing(){
		return contents;
	}
	
}
