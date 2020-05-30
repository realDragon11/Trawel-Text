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
		xp +=totalxp;
		if (xp > 15*Math.pow(1.15,level)) {
			xp-=15*Math.pow(1.15,level);
			addLevel(r);
		}
		/*
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
		case 5:
			if (xp >= 185) {
				xp-=185;
				level++;
				addPathPoints(3,r);
				r.cleanAbs();
				addXp(0,r);
			}
			break;
		case 6:
			if (xp >= 312) {
				xp-=312;
				level++;
				r.cleanAbs();
				addXp(0,r);
			}
			break;
		case 7:
			if (xp >= 500) {
				xp-=500;
				level++;
				addPathPoints(3,r);
				r.cleanAbs();
				addXp(0,r);
			}
			break;
		case 8:
			if (xp >= 790) {
				xp-=790;
				level++;
				addPathPoints(3,r);
				r.cleanAbs();
				addXp(0,r);
			}
			break;
		}*/
		
	}
	
	private void addLevel(RPlayer r) {
		level++;
		extra.println(r.getName() + " levels up!");
		if (level < 5) {//(3*2)+(30*3)+(30*2)+(35) + 9
			addPathPoints(2,r);
		}else {
			if (level < 35) {
				addPathPoints(3,r);
			}else {
				if (level < 65) {
					addPathPoints(2,r);
				}else {
					if (level < 99) {
						addPathPoints(1,r);
					}else {
						addPathPoints(9,r);
					}
				}
			}
		}
		/*switch (level) {
		case 5: case 6: case 8: case 9: case 11: case 12: case 47: case 48: addPathPoints(3,r); break;
		case 14: case 15: case 17: case 18: case 20: case 21: case 44: case 45: addPathPoints(4,r); break;
		}*/
		
		r.cleanAbs();
		
	}

	public void addPathPoints(int p, RPlayer r) {
		Job j = JobFactory.getJobByName(jobName);
		while (p > 0) {
			extra.println("You have " + p +" points to allocate.");
			extra.println("1 " + j.getPath1() + ": "+ r.progression.pathLevel(j.getPath1()));
			extra.println("2 " + j.getPath2() + ": "+ r.progression.pathLevel(j.getPath2()));
			extra.println("3 " + j.getPath3() + ": "+ r.progression.pathLevel(j.getPath3()));
			int aLeft;
			int in;
			switch (extra.inInt(3)) {
			case 1:
				aLeft = 100-r.progression.pathLevel(j.getPath1());
				if (aLeft > 0) {
					extra.println("Allocate how many?");
					in = extra.inInt(Math.min(aLeft,p));
					r.progression.addPathPoints(j.getPath1(), in,r);
					p-=in;
				}
				break;
			case 2:
				aLeft = 100-r.progression.pathLevel(j.getPath2());
				if (aLeft > 0) {
					extra.println("Allocate how many?");
					in = extra.inInt(Math.min(aLeft,p));
					r.progression.addPathPoints(j.getPath2(), in,r);
					p-=in;
				}
				break;
			case 3:
				aLeft = 100-r.progression.pathLevel(j.getPath3());
				if (aLeft > 0) {
					extra.println("Allocate how many?");
					in = extra.inInt(Math.min(aLeft,p));
					r.progression.addPathPoints(j.getPath3(), in,r);
					p-=in;
				}
				break;
			}
		}
	}
}
