package derg;

import java.util.List;
import java.util.ArrayList;

import com.github.yellowstonegames.core.GapShuffler;

public class SRGapShuffle extends StringResult {

	private GapShuffler<String> gps;
	private List<String> dupedList;
	
	public SRGapShuffle(GapShuffler<String> shuffler) {
		gps = shuffler;
		dupedList = new ArrayList<String>();
		gps.fillInto(dupedList);
	}
	
	public SRGapShuffle(List<String> list) {
		gps = new GapShuffler<String>(list);
		dupedList = list;
	}
	
	@Override
	public String stringMethod() {
		return "GapShuffler";
	}

	@Override
	public String next() {
		return gps.next();
	}
	
	@Override
	public String any() {
		return dupedList.get((int)(Math.random()*dupedList.size()));
	}
	
	@Override
	public List<String> backing(){
		return dupedList;
	}
	
	public GapShuffler<String> getGapShuffler() {
		return gps;
	}
	
}
