package rtrawel.jobs;

public class PathWithLevel implements java.io.Serializable{

	public Path path;
	public int level;
	
	public PathWithLevel(Path n, int l) {
		path = n;
		level = l;
	}
}
