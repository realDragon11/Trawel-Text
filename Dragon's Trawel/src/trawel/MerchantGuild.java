package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
		extra.println("3 buy shipments with gold");
		extra.println("4 leave");
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
		b = Player.bag.discardDrawBanes(true);
		if (b != null) {
		Player.player.addMPoints(b.getMValue());
		}
		}while (b != null);
		go();
		break;
		case 3:
			buyGShip();
			go();
		case 4: break; 
		}
	}

	private void buyGShip() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "buy a shipment of books ("+Player.player.merchantBookPrice+" gp)";
					}

					@Override
					public boolean go() {
						if (Player.bag.getGold() < Player.player.merchantBookPrice) {
							extra.println("You can't afford that many books!");
							return false;
						}
						extra.println("Buying lots of books might increase your knowledge- buy?");
						if (extra.yesNo()) {
							Player.bag.addGold(-Player.player.merchantBookPrice);
							if (extra.chanceIn(1, 2)) {
								Player.player.merchantBookPrice*=2;
								Player.bag.addNewDrawBane(DrawBane.KNOW_FRAG);
							}else {
								extra.println("There was nothing interesting in this batch.");
							}
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "exit";
					}

					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}});
		
	}
	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub
		//TODO: make certain drawbanes give more depending one what the guild needs
	}

}
