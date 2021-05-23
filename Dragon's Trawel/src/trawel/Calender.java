package trawel;
import java.io.Serializable;

public class Calender implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double timeCounter = extra.randRange(0,3640)/10.0;

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
	 * https://en.wikipedia.org/wiki/Sunrise_equation
	 * @param time
	 * @param riseOrSet
	 * @return
	 */
	public double[] getSunTime(double lata, double longa) {
		double j =  ((timeCounter+12)/24)-(longa/360);
		double m = Math.toRadians((357.5291 + 0.98560028  * j)%360);
		double c = 1.9148*Math.sin(m)+0.02*Math.sin(2*m)+0.0003*Math.sin(3*m);
		double l = Math.toRadians((m+c+180+102.9372)%360);
		double noon = j+0.0053*Math.sin(m)+0.0069*Math.sin(2*l);
		double d = Math.asin(Math.sin(l)*Math.sin(Math.toRadians(23.44)));//needs to be unsin-ed
		double hour = Math.acos(Math.sin(Math.toRadians(-0.83)-Math.sin(Math.toRadians(lata))*Math.sin(d))/(Math.cos(Math.toRadians(lata))*Math.cos(d)));
		//needs to be uncos-d
		double rise = noon-(hour/360);
		double set = noon+(hour/360);
		double[] ret = {rise,noon,set};
		return ret;
	}
	
	
	public static final double sunsetRadius = ;
	
	public float getBackTime() {
		double hourOfDay = (timeCounter%24)/24;
		/*
		switch (getMonth()) {
		case 1:
			if (hourOfDay < 5) {
				return 1;
			}
			if (hourOfDay < 5.5f) {
				return extra.lerp(1,2, (hourOfDay-5)*2);
			}
			if (hourOfDay < 6) {
				return extra.lerp(2,3, (hourOfDay-5.5f)*2);
			}
			if (hourOfDay < 17.5f) {
				return 3;
			}
			if (hourOfDay < 18f) {
				return extra.lerp(3,4, (hourOfDay-17)*2);
			}
			if (hourOfDay < 18.5f) {
				return extra.lerp(4,5, (hourOfDay-17.5f)*2);
			}
			return 1;
		case 2:
		case 3: 
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10: 
		case 11:
		case 12:
		case 13:
		}
		throw new RuntimeException("Invalid back time!");*/
		double[] rns = this.getSunTime(20,30);
		if (hourOfDay < rns[0]-sunsetRadius) {
			return 1;
		}
		if (hourOfDay < rns[0]) {
			return extra.lerp(1,2,(float) ((hourOfDay-rns[0])/(sunsetRadius)));
		}
		if (hourOfDay < rns[0]+sunsetRadius) {
			return extra.lerp(1,2,(float) ((hourOfDay-(rns[0]+sunsetRadius))/(sunsetRadius)));
		}
		if (hourOfDay < rns[2]-sunsetRadius) {
			return 2;
		}
		if (hourOfDay < rns[2]) {
			return extra.lerp(2,3,(float) ((hourOfDay-rns[2])/(sunsetRadius)));
		}
		if (hourOfDay < rns[2]+sunsetRadius) {
			return extra.lerp(3,4,(float) ((hourOfDay-(rns[2]+sunsetRadius))/(sunsetRadius)));
		}
		return 1;
	}

	public static void timeTest() {
		Calender test = new Calender();
		test.timeCounter = 0;
		for (int i = 0;i < 365;i++) {
			test.timeCounter+=24;
			double[] t = test.getSunTime(30,30);
			System.out.println(t[0] + " " + t[1] +" "+ t[2]);
		}
		
	}

}
