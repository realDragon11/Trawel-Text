package trawel;

public class Gambler extends Feature implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private int gold;
	
	public Gambler(String name, String type, int gold) {
		this.type = type;
		this.name = name;
		this.gold = gold;
		tutorialText = "Lose money at gamblers.";
	}
	
	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|icon|Gambler|");
		extra.println("1 play " +type);
		extra.println("2 leave");
		int input =  extra.inInt(2);
		extra.linebreak();
		if (input == 2){
			return;
		}
		if (input == 1) {
		}else {
			go();
		}
		extra.println("How much do you bid? They have " +gold + " gold to win.");
		int in =  extra.inInt(gold);
		extra.linebreak();
		Inventory bag = Player.player.getPerson().getBag();
		int bid = Math.max(Math.min(bag.getGold(),Math.min(gold,in)),0);
		extra.println("You bid " + bid + " gold.");
		switch (type) {
		case "cups":
			extra.println("They place a ball under a cup, then mix it with two other cups...");
			extra.println("Which cup do you pick?");
			extra.println("1 cup");
			extra.println("2 cup");
			extra.println("3 cup");
			in =  extra.inInt(3);
			extra.linebreak();
			if (in == 1 || in ==2 || in ==3) {
				if (Math.random()*3 < 1) {
					extra.println("You win " + bid  + " gold!");
					bag.addGold(bid);
					gold-=bid;
				}else {
					extra.println("You lose!");
					bag.addGold(-bid);
					gold+=bid;
				}
			}else {
				extra.println("That's not a cup! They decide to call it a draw.");
			}
			extra.println("Would you like to play again?");
			go();
			;break;
			case "digger":
			//generate a 8 by 8 grid, ask for width and than height, three tries use taxicab distance
			int x = extra.randRange(1,8);
			int y = extra.randRange(1,8);
			//need something to store tries
			
			
			break;
		}
		

	}


	@Override
	public void passTime(double time) {
		gold += (int)time;

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
