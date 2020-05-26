import java.util.ArrayList;

public class Chase {

	public Person chaser, target;
	public double timer = 5;
	public int chaserPoint = -1, targetPoint = -1;
	
	public double cMom = 0, tMom = 0;//momentum
	
	public ArrayList<Obstacle> oQueue = new ArrayList<Obstacle>();
	public Chase(Person chaser, Person target) {
		this.chaser = chaser;
		this.target = target;
	}
	
	public void nextObstacle() {
		oQueue.add(ObstacleFactory.randObstacle());
	}
	

	public Person go() {
		
		while ((chaserPoint < targetPoint || chaserPoint == -1) && timer < 10) {
			while(timer > 0) {
				targetPoint++;
				nextObstacle();
				timer-=oQueue.get(targetPoint).targetGo(this);
			}
			chaserPoint++;
			timer+=oQueue.get(chaserPoint).chaserGo(this);
		}
		if (timer >= 10) {
			return target;
		}
			
		
		return mainGame.CombatTwo(chaser,target);
	}
	
	
}
