import java.awt.Color;

public class Appraiser extends Feature {

	
	public Appraiser(String name) {
		this.name = name;
		tutorialText = "Appraisers will tell you more about your items.";
		color = Color.BLUE;
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|icon|Appraiser|");
		int in = 0;
		while (in != 7) {
		extra.println("1 head");
		extra.println("2 arms");
		extra.println("3 chest");
		extra.println("4 legs");
		extra.println("5 feet");
		extra.println("6 weapon");
		extra.println("7 exit");
		in = extra.inInt(7);
		if (in < 6) {
			Player.bag.getArmorSlot(in-1).display(2);
		}else {
			if (in == 6) {
				Player.bag.getHand().display(2);
			}
		}
		}
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub

	}

}
