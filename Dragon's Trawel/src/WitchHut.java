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
		Networking.sendStrong("Discord|imagesmall|hut|Witch Hut|");
		Networking.setArea("store");
		while (true) {
		extra.println("1 brew a potion");
		extra.println("2 leave");
		switch (extra.inInt(2)) {
		case 1: brew();break;
		case 2: return;
		}
		}
	}

	private void brew() {
		ArrayList<DrawBane> dbs = new ArrayList<DrawBane>();
		extra.println(Player.player.getFlask() != null ? "You already have a potion, brewing one will replace it." : "Time to get brewing!");
		while (true) {
			extra.println("1 put in a drawbane");
			extra.println("2 finish");
			extra.println("3 back/discard");
			switch (extra.inInt(3)) {
			case 1: 
				DrawBane inter = Player.bag.discardDrawBanes(true);
				if (inter != null && !inter.equals(DrawBane.NOTHING)) {
					dbs.add(inter);
				}break;
			case 2:
				if (dbs.size() == 0) {
					extra.println("There's nothing in the pot!");
					return;
				}
				Networking.sendStrong("Achievement|brew1|");
				extra.println("You finish brewing your potion, and put it in your flask... time to test it out!");
				int batWings = (int) dbs.stream().filter(d -> d.equals(DrawBane.BAT_WING)).count();
				int mGuts = (int) dbs.stream().filter(d -> d.equals(DrawBane.MIMIC_GUTS)).count();
				int apples = (int) dbs.stream().filter(d -> d.equals(DrawBane.APPLE)).count();
				int meats = (int) dbs.stream().filter(d -> d.equals(DrawBane.MEAT)).count();
				int garlics = (int) dbs.stream().filter(d -> d.equals(DrawBane.GARLIC)).count();
				int woods = (int) dbs.stream().filter(d -> d.equals(DrawBane.WOOD)).count();
				int honeys = (int) dbs.stream().filter(d -> d.equals(DrawBane.HONEY)).count();
				int food = meats + apples + garlics + honeys;
				int filler = apples + woods;
				if (extra.chanceIn(woods, 10)) {
					Player.player.setFlask(new Potion(Effect.CURSE,1+filler));
				}
				if (mGuts > 0 && batWings >0) {
					Player.player.setFlask(new Potion(Effect.R_AIM,batWings+mGuts+filler));
					return;
				}
				if (food >= 3) {
					Player.player.setFlask(new Potion(Effect.HEARTY,food));
					return;
				}
				if (batWings > 0) {
					Player.player.setFlask(new Potion(Effect.HASTE,batWings+filler));
					return;
				}
				Player.player.setFlask(new Potion(Effect.CURSE,1+filler));
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
