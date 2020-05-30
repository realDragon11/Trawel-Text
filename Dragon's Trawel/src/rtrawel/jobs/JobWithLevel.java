package rtrawel.jobs;

import rtrawel.unit.RPlayer;
import trawel.extra;

public class JobWithLevel implements java.io.Serializable {

	public String jobName;
	public int level;
	public int xp;
	
	public JobWithLevel(String n, int l) {
		jobName = n;
		level = l;
		xp = 0;
	}

	public void addXp(int totalxp, RPlayer r) {
		//TODO add path points
		switch (level) {
		case 1://https://gamefaqs.gamespot.com/ds/937281-dragon-quest-ix-sentinels-of-the-starry-skies/faqs/57795
			if (xp >= 15) {
				xp-=15;
				level++;
				r.cleanAbs();
				addXp(0,r);
				
			}
			break;
		case 2:
			if (xp >= 35) {
				xp-=35;
				level++;
				r.cleanAbs();
				addXp(0,r);
			}
			break;
		case 3:
			if (xp >= 65) {
				xp-=65;
				level++;
				r.cleanAbs();
				addXp(0,r);
			}
			break;
		case 4:
			if (xp >= 112) {
				xp-=112;
				level++;
				addPathPoints(3,r);
				r.cleanAbs();
				addXp(0,r);
			}
			break;
		}
		
	}
	
	public void addPathPoints(int p, RPlayer r) {
		Job j = JobFactory.getJobByName(jobName);
		while (p > 0) {
			extra.println("You have " + p +" points to allocate.");
			extra.println("1 " + j.getPath1() + r.progression.pathLevel(j.getPath1()));
			extra.println("2 " + j.getPath2() + r.progression.pathLevel(j.getPath2()));
			extra.println("3 " + j.getPath3() + r.progression.pathLevel(j.getPath3()));
			int aLeft;
			int in;
			switch (extra.inInt(3)) {
			case 1:
				aLeft = 100-r.progression.pathLevel(j.getPath1());
				if (aLeft > 0) {
					in = extra.inInt(Math.min(aLeft,p));
					r.progression.addPathPoints(j.getPath1(), in,r);
					p-=in;
				}
				break;
			case 2:
				aLeft = 100-r.progression.pathLevel(j.getPath2());
				if (aLeft > 0) {
					in = extra.inInt(Math.min(aLeft,p));
					r.progression.addPathPoints(j.getPath2(), in,r);
					p-=in;
				}
				break;
			case 3:
				aLeft = 100-r.progression.pathLevel(j.getPath3());
				if (aLeft > 0) {
					in = extra.inInt(Math.min(aLeft,p));
					r.progression.addPathPoints(j.getPath3(), in,r);
					p-=in;
				}
				break;
			}
		}
	}
}
