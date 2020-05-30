package rtrawel.jobs;

import rtrawel.unit.RPlayer;

public interface Path {


	public String name();
	public String jobName();
	public void apply(RPlayer player,int points, boolean jobActive);
	public void applyOnce(RPlayer player,int points, int formerPoints);
}
