import java.util.ArrayList;

public class ObstacleFactory {

	public static ArrayList<Obstacle> fOs = new ArrayList<Obstacle>();//ForestObstacleS
	
	static {
		fOs.add(new Obstacle() {

			@Override
			public double targetGo(Chase c) {
				
				return 0;
			}

			@Override
			public double chaserGo(Chase c) {
				return 0;
			}
			
		});
	}
	
	public static Obstacle randObstacle() {
		return extra.randList(fOs);
	}

}
