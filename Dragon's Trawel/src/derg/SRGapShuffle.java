package derg;

import com.github.yellowstonegames.core.GapShuffler;

public class SRGapShuffle extends StringResult {

	private GapShuffler<String> gps;
	
	public SRGapShuffle(GapShuffler<String> shuffler) {
		gps = shuffler;
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
