
public class PlantSpot {

	public String contains = "";
	public double timer = 0;
	public void go() {
		boolean breakit = false;
		while (!breakit) {
			extra.println("Contains: " + contains);
			extra.println(contains.equals("") ? "1 plant" : "1 take");
			extra.println("2 leave");
			switch (extra.inInt(2)) {
			case 1:
				if (contains.equals("")) {
					plant();
				}else {
					take();
				}
			case 2:
				breakit = true;
				break;
			}
		}
	}
	
	private void take() {
		switch (contains) {
		case "garlic": Player.bag.addNewDrawBane(DrawBane.GARLIC);break;
		case "garlic seeds": Player.bag.addSeed(Seed.GARLIC);break;
		case "apple seeds": Player.bag.addSeed(Seed.GARLIC);break;
		default: case "":extra.println("ERROR");break;
		}
		contains = "";
		
	}

	private void plant() {
		timer = 0;
		Seed s = Player.bag.getSeed();
		if (s != null) {
		contains = s.toString().toLowerCase() + " seed";
		}
		if (contains == null) {
			contains = "";
		}
	}

	public void passTime(double t) {
		timer +=t;
		switch (contains) {
		case "garlic seeds": if (timer > 57) { contains = "garlic";timer = 0;}break;
		}
	}
}
