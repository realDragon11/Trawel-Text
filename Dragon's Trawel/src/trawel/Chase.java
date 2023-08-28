package trawel;
import java.util.ArrayList;

import trawel.personal.Person;
import trawel.personal.people.Player;

public class Chase {

	public Person chaser, target;
	public double timer = 5;
	public int chaserPoint = -1, targetPoint = -1;
	
	public double cMom = 0, tMom = 0;//momentum
	
	public ArrayList<Obstacle> oQueue = new ArrayList<Obstacle>();
	public Chase(Person target) {
		this.chaser = Player.player.getPerson();
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
		extra.println(extra.PRE_BATTLE+"You have caught up to " + target.getName() +"!");
		return null;
	}

	public void tMomMod(double d) {
		tMom = extra.clamp(tMom+d,0,1);
	}
	public void cMomMod(double d) {
		cMom = extra.clamp(cMom+d,0,1);
	}
	
	
}
