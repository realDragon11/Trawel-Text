package derg;

import java.util.List;

import com.github.yellowstonegames.core.GapShuffler;

public class SRGapShuffle extends StringResult {

	private GapShuffler<String> gps;
	
	public SRGapShuffle(GapShuffler<String> shuffler) {
		gps = shuffler;
	}
	
	public SRGapShuffle(List<String> list) {
		gps = new GapShuffler<String>(list);
	}
	
	@Override
	public String stringMethod() {
		return "GapShuffler";
	}

	@Override
	public String next() {
		return gps.next();
	}
	
	public GapShuffler<String> getGapShuffler() {
		return gps;
	}
	
}
