package derg;

import java.util.Arrays;
import java.util.List;

import trawel.helper.methods.extra;

public class SRPlainRandom extends StringNum {

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

	@Override
	public String getWithNum(int i) {
		if (contents.size() == 0) {
			return null;
		}
		return contents.get(i%contents.size());
	}

	@Override
	public String getWithNumExact(int i) {
		if (!(contents.size() > i)) {
			return null;
		}
		return contents.get(i);
	}

	@Override
	public int getNum() {
		return extra.getRand().nextInt(contents.size());
	}

	@Override
	public int getMaxNum() {
		return contents.size()-1;
	}
	
}
