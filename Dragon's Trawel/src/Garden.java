import java.util.ArrayList;

public class Garden extends Feature {

	private Town town;
	private int tier;
	private ArrayList<PlantSpot> plants = new ArrayList<PlantSpot>();
	public Garden(Town town) {
		this.town = town;
		tier = town.getTier();
		name = "garden";
		tutorialText = "Gardens can grow plants.";
		for (int i = 0; i < 4;i++) {
		plants.add(new PlantSpot());}
	}

	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|garden|Garden|");
		while (true) {
			for (int i = 0; i < plants.size();i++) {
				extra.println((i+1) + " garden section containing " + plants.get(i).contains);
			}
			
			int in = extra.inInt(plants.size());
			if (in < plants.size()) {
				plants.get(in-1).go();
			}else {
				break;
			}
		}
	}

	@Override
	public void passTime(double time) {
		
	}

}
