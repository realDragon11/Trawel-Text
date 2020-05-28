package rtrawel.jobs;

import java.util.ArrayList;
import java.util.List;

public class Progression {

	public List<PathWithLevel> paths = new ArrayList<PathWithLevel>();
	public List<JobWithLevel> jobs = new ArrayList<JobWithLevel>();
	
	public int jobLevel(String j) {
		for (JobWithLevel k: jobs) {
			if (k.jobName.equals(j)) {
				return k.level;
			}
		}
		return 0;
	}
}
