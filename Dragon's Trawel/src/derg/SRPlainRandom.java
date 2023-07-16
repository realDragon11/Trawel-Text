package derg;

import java.util.Arrays;
import java.util.List;

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
		return contents.get((int)(Math.random()*contents.size()));
	}
	
	public List<String> getContents() {
		return contents;
	}
	
}
