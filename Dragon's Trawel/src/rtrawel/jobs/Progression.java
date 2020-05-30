package rtrawel.jobs;

import java.util.ArrayList;
import java.util.List;

import rtrawel.unit.RPlayer;

public class Progression implements java.io.Serializable{

	public List<PathWithLevel> paths = new ArrayList<PathWithLevel>();
	public List<JobWithLevel> jobs = new ArrayList<JobWithLevel>();
	
	public int jobLevel(String j) {
		for (JobWithLevel k: jobs) {
			if (k.jobName.equals(j)) {
				return k.level;
			}
		}
		return -1;
	}
	public int pathLevel(String p) {
		for (PathWithLevel k: paths) {
			if (k.path.equals(p)){
				return k.level;
			}
		}
		return 0;
	}
	
	public void addPathPoints(String p, int points,RPlayer r) {
		if (pathLevel(p) == -1) {
			paths.add(new PathWithLevel(p, points,r));
		}else {
			for (PathWithLevel k: paths) {
				if (k.path.equals(p)){
					k.addPoints(points,r);
					break;
				}
			}
		}
	}
	public void addJobXp(String j, int totalxp, RPlayer r) {
		for (JobWithLevel k: jobs) {
			if (k.jobName.equals(j)) {
				k.addXp(totalxp,r);
				return;
			}
		}
		throw new RuntimeException("leveling a class you don't have!");
		
	}
	public PathWithLevel getPathByName(String str,RPlayer r) {
		if (pathLevel(str) == -1) {
			PathWithLevel p =  new PathWithLevel(str, 0,r);
			paths.add(p);
			return p;
		}else {
			for (PathWithLevel k: paths) {
				if (k.path.equals(str)){
					return k;
				}
			}
			return null;
		}
	}
}
