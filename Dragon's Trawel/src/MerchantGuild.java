import java.awt.Color;

public class MerchantGuild extends Feature {

	public MerchantGuild(String name){
		this.name = name;
		tutorialText = "Merchant quests will make stores willing to sell items higher than your level.";
		color = Color.PINK;
	}
	@Override
	public void go() {
		DrawBane b = null;
		do {
		extra.println("The merchants are willing to take supplies to increase your reputation.");
		b = Player.bag.discardDrawBanes();
		if (b != null) {
		Player.player.addMPoints(b.getMValue());
		}
		}while (b != null);
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub
		//TODO: make certain drawbanes give more depending one what the guild needs
	}

}
