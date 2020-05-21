
public class PlantSpot {

	public String contains = "";
	public double timer = 0;
	public int level;
	public PlantSpot(int tier) {
		level = tier;
	}
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
		case "garlic": 
			Player.bag.addNewDrawBane(DrawBane.GARLIC);
			Player.bag.addSeed(Seed.GARLIC);
			break;
		case "apple tree": 
			Player.bag.addNewDrawBane(DrawBane.APPLE);
			Player.bag.addNewDrawBane(DrawBane.APPLE);
			Player.bag.addNewDrawBane(DrawBane.WOOD);
			Player.bag.addSeed(Seed.APPLE);
		break;
		case "garlic seed": Player.bag.addSeed(Seed.GARLIC);break;
		case "apple seed": Player.bag.addSeed(Seed.APPLE);break;
		case "bee hive": 
			Player.bag.addNewDrawBane(DrawBane.HONEY);
			Player.bag.addNewDrawBane(DrawBane.WAX);
			Player.bag.addSeed(Seed.BEE);
		break;
		case "bee larva": Player.bag.addSeed(Seed.BEE);break;
		case "ent": 
			mainGame.CombatTwo(Player.player.getPerson(),RaceFactory.makeEnt(level));
		break;
		case "ent sapling": Player.bag.addSeed(Seed.ENT);break;
		case "pumpkin": 
			Player.bag.addNewDrawBane(DrawBane.PUMPKIN);
			Player.bag.addSeed(Seed.PUMPKIN);
		break;
		case "pumpkin seed": Player.bag.addSeed(Seed.PUMPKIN);break;
		case "eggcorn": 
			Player.bag.addNewDrawBane(DrawBane.EGGCORN);
			Player.bag.addSeed(Seed.EGGCORN);
		break;
		case "eggcorn seed": Player.bag.addSeed(Seed.EGGCORN);break;
		case "truffle": 
			Player.bag.addNewDrawBane(DrawBane.TRUFFLE);
		break;
		case "truffle spores":;break;
		default: case "":extra.println("ERROR");break;
		}
		contains = "";
		
	}

	private void plant() {
		timer = 0;
		Seed s = Player.bag.getSeed();
		if (s != null) {
		contains = s.toString().toLowerCase();
		}else {
			contains = "";
		}
		if (contains == null) {
			contains = "";
		}
	}

	public void passTime(double t) {
		timer +=t;
		switch (contains) {
		case "garlic seed": if (timer > 57) { contains = "garlic";timer = 0;}break;
		case "apple seed": if (timer > 323) { contains = "apple tree";timer = 0;}break;
		case "bee larva": if (timer > 98) { contains = "bee hive";timer = 0;}break;
		case "ent sapling": if (timer > 630) { contains = "ent";timer = 0;}break;
		case "pumpkin seed": if (timer > 60) { contains = "pumpkin";timer = 0;}break;
		case "eggcorn seed": if (timer > 33) { contains = "eggcorn";timer = 0;}break;
		case "truffle spores": if (timer > 60) { contains = "truffle";timer = 0;}break;
		}
	}
}
