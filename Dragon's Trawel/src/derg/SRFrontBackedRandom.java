package derg;

import java.util.Arrays;
import java.util.List;

import trawel.extra;

public class SRFrontBackedRandom extends StringNum {

	
	private String[] back;
	
	/**
	 * set the backing array to use this class as a fallthrough for
	 * @param arr
	 * @return this, for fluent interfaces
	 */
	public SRFrontBackedRandom setBack(String[] arr) {
		back = arr;
		return this;
	}
	
	public SRFrontBackedRandom() {
		//empty
	}
	
	@Override
	public String stringMethod() {
		return "FrontBackRandom";
	}
	
	@Override
	public String next() {
		return back[extra.getRand().nextInt(back.length)];
	}

	@Override
	public String any() {
		return back[(int)(Math.random()*back.length)];
	}
	
	@Override
	public List<String> backing(){
		return Arrays.asList(back);
	}

	@Override
	public String getWithNum(int i) {
		if (back.length == 0) {
			return null;
		}
		return back[i%back.length];
	}

	@Override
	public String getWithNumExact(int i) {
		if (!(back.length > i)) {
			return null;
		}
		return back[i];
	}

	@Override
	public int getNum() {
		return extra.getRand().nextInt(back.length);
	}

	@Override
	public int getMaxNum() {
		return back.length-1;
	}
	
}
