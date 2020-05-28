package rtrawel.jobs;

public abstract class Job {

	public String path1, path2, path3;
	
	public abstract int getHpAtLevel(int level);
	public abstract int getMPAtLevel(int level);
	public abstract int getTenAtLevel(int level);
	public abstract int getStrAtLevel(int level);
	public abstract int getDexAtLevel(int level);
	public abstract int getAgiAtLevel(int level);
	public abstract int getSpdAtLevel(int level);
	public abstract int getKnoAtLevel(int level);
	public abstract int getResAtLevel(int level);
}
