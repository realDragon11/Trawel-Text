package derg;

import java.util.List;

public class SPlainRandom extends StringResult {

	private List<String> contents;
	
	public SPlainRandom(List<String> list) {
		contents = list;
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
