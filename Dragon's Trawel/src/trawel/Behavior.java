package trawel;

public abstract class Behavior {
	private double timeTo;

	public double getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(double timeTo) {
		this.timeTo = timeTo;
	}
	
	
	public abstract void action(Agent user);

	//TODO: add context
	public void passTime(double d) {
		timeTo-=d;
	}

}
