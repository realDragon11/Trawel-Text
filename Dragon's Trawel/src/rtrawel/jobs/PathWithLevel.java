package rtrawel.jobs;

public class PathWithLevel implements java.io.Serializable{

	public String path;
	public int level;
	
	public PathWithLevel(String n, int l) {
		path = n;
		level = l;
	}
}
