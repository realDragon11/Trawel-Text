import java.awt.Color;

public class MerchantGuild extends Feature {

	public MerchantGuild(String name){
		this.name = name;
		tutorialText = "Merchant quests will make stores willing to sell items higher than your level. (current reputation: " + Player.player.merchantLevel+ ")";
		color = Color.PINK;
	}
	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|store|Merchant Guild|");
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
