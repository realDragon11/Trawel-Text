import java.util.ArrayList;

public class ObstacleFactory {

	public static ArrayList<Obstacle> fOs = new ArrayList<Obstacle>();//ForestObstacleS
	
	static {
		fOs.add(new Obstacle() {
			//pit
			@Override
			public double targetGo(Chase c) {
				if (c.tMom > .5){
					//jump
					if (Math.random()*c.tMom > .35) {
						//leaps over
						c.tMomMod(.1);
						return .3;
					}else {
						c.tMomMod(-.5);
						return 2;//falls into pit
					}
				}else {
					//go around
					c.tMomMod(-.2);
					return 1.2;
				}
			}

			@Override
			public double chaserGo(Chase c) {
				extra.println("You approach a pit! Jump over it?");
				if (extra.yesNo()){
					//jump
					if (Math.random()*c.tMom > .35) {
						//leaps over
						c.cMomMod(.1);
						return .3;
					}else {
						c.cMomMod(-.5);
						return 2;//falls into pit
					}
				}else {
					//go around
					c.cMomMod(-.2);
					return 1.2;
				}
			}
			
		});
	}
	
	public static Obstacle randObstacle() {
		return extra.randList(fOs);
	}

}
