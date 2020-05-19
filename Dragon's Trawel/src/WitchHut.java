import java.awt.Color;
import java.util.ArrayList;

public class WitchHut extends Feature{

	
	public WitchHut() {
		name = "witch hut";
		tutorialText = "A place to brew potions.";
		color = Color.pink;
	}
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|store|Witch Hut|");
		Networking.setArea("store");
		mainGame.story.altar();
		while (true) {
		extra.println("1 brew a potion");
		extra.println("4 leave");
		switch (extra.inInt(2)) {
		case 1: brew();break;
		case 4: return;
		}
		}
	}

	private void brew() {
		ArrayList<DrawBane> dbs = new ArrayList<DrawBane>();
		extra.println(Player.player.getFlask() != null ? "You already have a potion, brewing one will replace it." : "Time to get brewing!");
		int in = extra.inInt(2);
		extra.println("1 put in a drawbane");
		extra.println("2 finish");
		extra.println("3 back/discard");
		while (true) {
			switch (in) {
			case 1: 
				DrawBane inter = Player.bag.discardDrawBanes(true);
				if (inter != null && !inter.equals(DrawBane.NOTHING)) {
					dbs.add(inter);
				}break;
			case 2:
				extra.println("You finish brewing your potion, and put it in your flask... time to test it out!");
				int batWings = (int) dbs.stream().filter(d -> d.equals(DrawBane.BAT_WING)).count();
				if (batWings > 0) {
					Player.player.setFlask(new Potion(Effect.HASTE,batWings));
					return;
				}
				Player.player.setFlask(new Potion(Effect.CURSE,1));
				return;
			case 3: return;
			}
		}
	}
	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub
		
	}
	

}
