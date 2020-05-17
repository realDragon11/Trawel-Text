import java.awt.Color;

public class MerchantGuild extends Feature {

	public MerchantGuild(String name){
		this.name = name;
		tutorialText = "Merchant quests will make stores willing to sell items higher than your level.";
		color = Color.PINK;
	}
	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|store|Merchant Guild|");
		DrawBane b = null;
		extra.println("(current reputation: " + Player.player.merchantLevel+ ")");
		extra.println("1 Donate Drawbanes.");
		extra.println("2 Donate emerald. (You have " + Player.player.emeralds + ")");
		extra.println("3 leave");
		switch (extra.inInt(3)) {
		case 2:
			if (Player.player.emeralds > 0) {
			Player.player.addMPoints(10);
			extra.println("You donate an emerald.");
			Player.player.emeralds--;
			}else {
				extra.println("You have no emeralds to donate.");
			}
			go();
			break;
		case 1: do {
		extra.println("The merchants are willing to take supplies to increase your reputation. (current reputation: " + Player.player.merchantLevel+ ")");
		b = Player.bag.discardDrawBanes();
		if (b != null) {
		Player.player.addMPoints(b.getMValue());
		}
		}while (b != null);
		go();
		break;
		case 3: break; 
		}
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub
		//TODO: make certain drawbanes give more depending one what the guild needs
	}

}
