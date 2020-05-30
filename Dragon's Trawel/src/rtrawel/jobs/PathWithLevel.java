package rtrawel.jobs;

import rtrawel.unit.RPlayer;

public class PathWithLevel implements java.io.Serializable{

	public String path;
	public int level;
	
	public PathWithLevel(String n, int l,RPlayer r) {
		path = n;
		level = 0;
		addPoints(l,r);
	}

	public void addPoints(int points,RPlayer r) {
		PathFactory.getPathByName(path).applyOnce(r,level+points,level);
		level+=points;
	}
}
