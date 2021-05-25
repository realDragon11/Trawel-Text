package trawel;
import java.awt.Point;
import java.io.Serializable;

public class Calender implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double timeCounter = 10;//extra.randRange(0,3640)/10.0;
	
	/*public enum LunarPhase{//not nearly as exact as the sun
		NEW_MOON(0f),
		WAX_CRESCENT(0.05f),FIRST_QUARTER(0.1f),WAX_GIBBOUS(0.15f),
		FULL_MOON(0.2f),
		WANE_CRESCENT(0.05f),LAST_QUARTER(0.1f),WANE_GIBBOUS(0.15f);
	
		public float lum;
		LunarPhase(float lum){
			this.lum = lum;
		}
	}*/

	public void passTime(double time) {
		timeCounter +=time;
		
	}
	
	public int getMonth() {
		return (int)((timeCounter%getYearLength())/getMonthLength())+1;
	}

	public double getYear() {
		return (int)(timeCounter/getYearLength())+1;
	}

	public double getYearLength() {
		return 364*24;
	}
	
	public double getMonthLength() {
		return 28*24;
	}
	
	public int getMonthsInYear() {
		return (int)Math.round(getYearLength()/getMonthLength());
	}
	
	/**
	 * gets the day of month
	 * @return
	 */
	public int getDay() {
		return (int)(((timeCounter%getYearLength())%getMonthLength())/24)+1;
	}
	
	public int getDayOfWeek() {
		return (int) ((timeCounter/24)%7)+1;//(getDay()%7)+1;
	}
	
	public String getDayOfWeekName() {
		switch (getDayOfWeek()) {
		case 1:
			return "Week's Dawn";
		case 2:
			return "Firesprite";
		case 3: 
			return "Thunderspark";
		case 4:
			return "Mid-Week";
		case 5:
			return "Waterspirit";
		case 6:
			return "Frostsun";
		case 7:
			return "Week's Set";
		}
		return null;
	}
	
	public String getMonthName() {
		switch (getMonth()) {
		case 1:
			return "Absence";
		case 2:
			return "Collection";
		case 3: 
			return "Kindling";
		case 4:
			return "Burning";
		case 5:
			return "Flame";
		case 6:
			return "Roaring";
		case 7:
			return "Pyre";
		case 8:
			return "Festival";
		case 9:
			return "Receding";
		case 10: 
			return "Dying";
		case 11:
			return "Falling";
		case 12:
			return "Barren";
		case 13:
			return "Wish";
		}
		return null;
	}
	
	public String dateName() {
		String str;
		switch (getDay()/7) {
		case 0:
			str= "1st ";break;
		case 1:
			str= "2nd ";break;
		case 2:
			str= "3rd ";break;
		case 3:
			str= "4th ";break;
		case 4:
			str= "5th ";break;
		default:
			str = "error";break;
		}
		str+=getDayOfWeekName()+" of ";
		str+=getMonthName() +" in the year of our oppressor, ";
		str+=((int)getYear()) + "RC";
		return str;
	}
	
	/**
	 * Won't sync up to year exactly
	 * https://en.wikipedia.org/wiki/Sunrise_equation
	 * @param time
	 * @param riseOrSet
	 * @return
	 */
	public double[] getSunTime(double lata, double longa) {
		double j = ((int)((timeCounter)/24))-(longa/360);
		double m = Math.toRadians((357.5291 + 0.98560028 * j)%360);
		double c = 1.9148*Math.sin(m)+0.02*Math.sin(2*m)+0.0003*Math.sin(3*m);
		double l = Math.toRadians((Math.toDegrees(m)+c+180+102.9372)%360);
		double noon = j+0.0053*Math.sin(m)-0.0069*Math.sin(2*l)/*testing:*/+.5;
		double d = Math.asin(Math.sin(l)*Math.sin(Math.toRadians(23.44)));
		double hour = Math.toDegrees(Math.acos(Math.sin(Math.toRadians(-0.83))-Math.sin(Math.toRadians(lata))*Math.sin(d))/(Math.cos(Math.toRadians(lata))*Math.cos(d)));
		double rise = noon-(hour/(360));
		double set = noon+(hour/360);
		double[] ret = {rise,noon,set};
		return ret;
	}
	
	public double getLocalTime(double time1, double longa) {
		double timeZone =(longa >0 ? 1 : -1)*(extra.lerp(0, 1/2f,(float)Math.abs(longa)/180));
		return ((time1)+2+(timeZone))%1;
	}
	
	
	public static final double sunsetRadius = 1/(double)38;//1/(double)48;//half hour in 1 = 1 day
	
	public float[] getBackTime(double lata, double longa) {
		double hourOfDay = getLocalTime((timeCounter)/24,longa);//(%24)/24;
		double[] rns = this.getSunTime(lata,longa);
		double sunRise = getLocalTime(rns[0],longa);
		double sunSet = getLocalTime(rns[2],longa);
		double noon = getLocalTime(rns[1],longa);
		if (hourOfDay < sunRise-sunsetRadius) {
			return new float[] {4,moonLum(noon,hourOfDay)};
		}
		if (hourOfDay < sunRise) {
			return new float[] {extra.lerp(4,5,(float) ((hourOfDay-(sunRise-sunsetRadius))/(sunsetRadius))),moonLum(noon,hourOfDay)};
		}
		if (hourOfDay < sunRise+sunsetRadius) {
			return new float[] {extra.lerp(1,2,(float) ((hourOfDay-sunRise)/(sunsetRadius))),moonLum(noon,hourOfDay)};
		}
		
		//double noonTime = getLocalTime(rns[1],longa);
		if (hourOfDay < sunSet-sunsetRadius) {
			//double timeToNoon = Math.abs(hourOfDay-noonTime);
			return new float[] {2,getDayLum(sunRise,sunSet,noon,hourOfDay)};
		}
		if (hourOfDay < sunSet) {
			return new float[] {extra.lerp(2,3,(float) ((hourOfDay-(sunSet-sunsetRadius))/(sunsetRadius))),0};
		}
		if (hourOfDay < sunSet+sunsetRadius) {
			return new float[] {extra.lerp(3,4,(float) ((hourOfDay-sunSet)/(sunsetRadius))),moonLum(noon,hourOfDay)};
		}
		return new float[] {4,moonLum(noon,hourOfDay)};
	}
	
	public float getDayLum(double sunRise,double sunSet,double noon, double hourOfDay) {
		return Math.max(moonLum( noon,  hourOfDay), dayLum( sunRise, sunSet,  hourOfDay));
	}
	
	public float dayLum(double sunRise,double sunSet, double hourOfDay) {
		return (float)extra.lerpDepth((float)(sunRise+sunsetRadius),(float)(sunSet-sunsetRadius),(float) ((hourOfDay)),.25f);
	}
	public float moonLum(double noon, double hourOfDay) {
		float phaseProgress = (float) ((timeCounter%getMonthLength())/getMonthLength());
		float maxLum = extra.lerpDepth(0,1,phaseProgress, 0.2f)*0.2f;
		double moonNoon = noon+extra.lerp(0,-1,phaseProgress);
		double hour = 1/4;//6 hours, currently nonseasonal
		double moonRise = moonNoon-hour;
		double moonSet = moonNoon+hour;
		return (float)extra.lerpDepth((float)(moonRise+.5)+2,(float)(moonSet+.5)+2,(float) ((hourOfDay))+2,.25f)*maxLum;
	}

	public static void timeTest() {
		Calender test = new Calender();
		//extra.println(sunsetRadius+"");
		test.timeCounter = 0;
		int longa = -72;
		Networking.setBackground("forest");
		for (int i = 0;i < 99999999;i++) {
			//float[] back = test.getBackTime(30,longa);
			//extra.println(back[0]+""+back[1]);
			//double[] t = test.getSunTime(30,longa);
			//System.out.println((test.timeCounter/24) +": "+ (t[0]) + " " + (t[1]) +" "+ (t[2]));
			//System.out.println(test.getLocalTime(test.timeCounter/24,longa) +": "+ test.getLocalTime(t[0],longa) + " " + test.getLocalTime(t[1],longa) +" "+ test.getLocalTime(t[2],longa));
			
			test.timeCounter+=.1f;
			float[] b = test.getBackTime(42,-72);
			Networking.sendStrong("Backvariant|"+"forest1"+"|"+b[0]+"|"+b[1]+"|");
			Networking.waitIfConnected(50L);
		}
		
	}

	public static double[] lerpLocation(Town t) {
		double[] d = new double[2];
		Point p = t.getLocation();
		World w = t.getIsland().getWorld();
		d[0] = extra.lerp(w.getMinLata(),w.getMaxLata(),p.y/(float)w.getYSize());
		d[1] = extra.lerp(w.getMinLonga(),w.getMaxLonga(),p.y/(float)w.getXSize());
		return d;
	}
	

}
