package trawel;

public class Well extends Feature implements java.io.Serializable{
	
	public Well(String name) {
		this.name = name;
		tutorialText = "This is a well.";
	}

	@Override
	public void go() {
		extra.println("1 make a wish");
		extra.println("2 exit");
		int in =  extra.inInt(2);
		extra.linebreak();
		if (in == 2) {
			return;
		}
		if (in == 1) {
			if (Player.bag.getGold() > 0) {
				extra.println("You toss a coin into the well... what do you wish?");
				extra.inString();
				extra.println("You hear a small splash and make your wish...");
				Player.bag.addGold(-1);
			}else {
				extra.println("You don't have a coin to wish with!");
			}
		}
		
		go();

	}

	@Override
	public void passTime(double time) {}
	
	

}
