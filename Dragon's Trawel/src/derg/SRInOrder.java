package derg;

import java.util.Arrays;
import java.util.List;

public class SRInOrder extends StringResult {

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
	
}
