import java.util.ArrayList;

/**
 * @author Brian Malone
 * like Player, but for the ai. can be programmed with behaviors
 */
public class Agent extends SuperPerson{
	private Person person;
	
	private ArrayList<Behavior> behaviors;
	private int aiType = 0;//malop... why is it in my head?
	private Behavior current;
	
	
	public Agent(Person p) {
		setPerson(p);
		behaviors = new ArrayList<Behavior>();
		current = new WanderEndless();
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public ArrayList<Behavior> getBehaviors() {
		return behaviors;
	}

	public void setBehaviors(ArrayList<Behavior> behaviors) {
		this.behaviors = behaviors;
	}
	
	public Behavior popBehave() {
		Behavior b = behaviors.get(0);
		behaviors.remove(0);
		return b;
	}

	@Override
	public void passTime(double d) {
		//will need to look at connections and features
		//a* pathing?
		while(current.getTimeTo() < d) {
			d-=current.getTimeTo();
			current.action(this);
			current = popBehave();
		}
			current.passTime(d);
	}

	public void enqueueBehavior(Behavior b) {
		behaviors.add(b);
	}
	
}
