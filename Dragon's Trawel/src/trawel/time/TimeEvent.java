package trawel.time;

import java.io.Serializable;

public abstract class TimeEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	public ContextLevel context;
}
