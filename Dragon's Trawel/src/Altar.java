import java.awt.Color;

public class Altar extends Feature{

	
	public Altar() {
		name = "sky altar";
		tutorialText = "OwO what's this?";
		color = Color.MAGENTA;
	}
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|altar|Altar|");
		Networking.setArea("mountain");
		mainGame.story.altar();
		while (true) {
		extra.println("1 examine the altar");
		extra.println("2 stand on the altar");
		extra.println("3 reincarnate (Restart character with skillpoints)");
		extra.println("4 leave");
		switch (extra.inInt(4)) {
		case 1: examine();break;
		case 2:swaQuest() //main quest stuff
			;break;
		//case 3: transmute();break;
		case 3: 
			Networking.sendColor(Color.RED);
			extra.println("Really reincarnate? You will lose your items and gold.");if (extra.yesNo()) {Player.player.reincarnate();}break;
		case 4: return;
		}
		}
	}
	

	private void swaQuest() {
		if (Player.player.animalQuest == 1) {
			extra.println("The altar flashes. You feel bloodthirsty.");
			Player.player.animalQuest =2;
		}
		if (Player.player.animalQuest == 2 && Player.player.wins > 10) {
			extra.println("The altar flashes. The bloodlust fades. You crave a beer... and another fight.");
			Player.player.animalQuest =3;
		}
		if (Player.player.animalQuest == 5) {
			extra.println("The altar flashes.");
		}
		
	}


	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub
		
	}
	
	private void examine() {
		extra.println("You examine the altar. It's of a "+Player.player.animalName()+".");
	}
	

}
