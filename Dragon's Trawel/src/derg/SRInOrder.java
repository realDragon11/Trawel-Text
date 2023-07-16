package derg;

import java.util.List;

public class SRInOrder extends StringResult {

	private List<String> contents;
	private int marker = 0;
	
	public SRInOrder(List<String> list) {
		contents = list;
	}
	
	@Override
	public String stringMethod() {
		return "InOrder";
	}

	@Override
	public String next() {
		return contents.get(marker++%contents.size());
	}
	
	public List<String> getContents() {
		return contents;
	}
	
	public int getMarker() {
		return marker;
	}
	
	public void setMarker(int i) {
		marker = i;
	}
	
}
