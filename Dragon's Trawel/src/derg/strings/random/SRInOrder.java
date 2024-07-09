package derg.strings.random;

import java.util.Arrays;
import java.util.List;

import derg.strings.fluffer.StringNum;

public class SRInOrder extends StringNum {

	private List<String> contents;
	private int marker = 0;
	
	public SRInOrder(List<String> list) {
		contents = list;
	}
	
	public SRInOrder(String...strings) {
		contents = Arrays.asList(strings);
	}
	
	@Override
	public String stringMethod() {
		return "InOrder";
	}

	@Override
	public String next() {
		return contents.get(marker++%contents.size());
	}
	
	@Override
	public String any(){
		return contents.get((int)(Math.random()*contents.size()));
	}
	
	@Override
	public List<String> backing(){
		return contents;
	}
	
	public int getMarker() {
		return marker;
	}
	
	public void setMarker(int i) {
		marker = i;
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
		return marker++%contents.size();
	}

	@Override
	public int getMaxNum() {
		return contents.size()-1;
	}
	
	
}
