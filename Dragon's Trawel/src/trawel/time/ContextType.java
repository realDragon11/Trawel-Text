package trawel.time;

public enum ContextType{
	
	GLOBAL(24.0),//one day
	LOCAL(2.0),//two hours
	UNBOUNDED(Double.MAX_VALUE),//infinite
	SHORT(0.1),//6 minutes
	BACKGROUND(168.0)//one week
	;
	public final double time_span;
	ContextType(double time) {
		time_span = time;
	}
}
