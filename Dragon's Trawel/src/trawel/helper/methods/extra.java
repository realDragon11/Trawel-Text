package trawel.helper.methods;


import java.util.List;

import trawel.core.Rand;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;

public final class extra {
	/**
	 * Makes sure that something isn't less than zero, else it returns 0
	 * @param i (int)
	 * @return (int)
	 */
	public static final int zeroOut(int i) {
		return Math.max(i,0);
	}

	/**
	 * Makes sure that something isn't less than zero, else it returns 0
	 * @param i (double)
	 * @return (double)
	 */
	public static final double zeroOut(double i) {
		return Math.max(i,0);
	}
	public static final float clamp(float d, float min, float max) {
		return Math.min(max, Math.max(d, min));
	}
	public static final double clamp(double d, double min, double max) {
		return Math.min(max, Math.max(d, min));
	}
	public static final int clamp(int d, int min, int max) {
		return Math.min(max, Math.max(d, min));
	}


	public static final float lerp(float a, float b, float f) 
	{
		return (a * (1.0f - f)) + (b * f);
	}

	public static final double lerp(double a, double b, double f) 
	{
		return (a * (1.0 - f)) + (b * f);
	}

	/**
	 * https://stackoverflow.com/a/13091759
	 * @param a - How deep the curve is - 0 <-> 1
	 * @return
	 */
	public static float bellCurve(float a){
		double x = Rand.getRand().nextDouble();
		return (float) (4*a*Math.pow(x,3) - 6*a*Math.pow(x,2) + 2*a*x + x);//TODO fix
	}

	public static float curveLerp(float start, float end, float depth) {
		return extra.lerp(start, end, bellCurve(depth));
	}

	public static float lerpDepth(float start, float end, float f,float depth) {
		float midpoint = start+(end-start)/2;
		float x = 1-(2*Math.abs(midpoint-f)/(end-start));//TODO fix
		//System.out.println(start + ", "+end +", " +f+";"+midpoint+": "+x);
		return (float) (4*depth*Math.pow(x,3) - 6*depth*Math.pow(x,2) + 2*depth*x + x);//4*.25*x^3-6*.25*x^2+2*.25*x+x
	}

	public static float lerpSetup(float start, float end, float f) {
		return 1-(2*Math.abs((start+(end-start)/2)-f)/(end-start));//TODO fix
	}
	/*
		public static double upDamCurve(double depth, double midpoint) {
			double rand = Math.random();
			double distance = (Math.abs(rand-midpoint));
			//double x = rand/midpoint;
			double x = (rand < midpoint ? rand/midpoint : (midpoint-distance)/midpoint);
			//double x = (midpoint-(1-(Math.abs(Math.random()-midpoint))))/midpoint;
			return (4*depth*Math.pow(x,3) - 6*depth*Math.pow(x,2) + 2*depth*x + x);
		}*/

	public static final double upDamCurve(double depth, double midpoint) {
		double x = 1-(2*Math.abs(Rand.getRand().nextDouble()-midpoint));
		x = extra.clamp(x,0,1);
		return (4*depth*Math.pow(x,3) - 6*depth*Math.pow(x,2) + 2*depth*x + x);
	}

	/**
	 * must be non empty
	 */
	public static Person getNonAddOrFirst(List<Person> peeps) {
		for (Person p: peeps) {
			if (!p.getFlag(PersonFlag.IS_MOOK)) {
				return p;
			}
		}
		return peeps.get(0);
	}

}

