import java.awt.Color;

public class Mountain extends Feature implements java.io.Serializable{

	private int tier;
	private int explores;
	private int exhaust;
	
	public Mountain(String name, int tier) {
		this.tier = tier;
		this.name = name;
		explores = 0;
		exhaust = 0;
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|icon|Mountain|");
		extra.println("1 explore");
		extra.println("2 exit");
		int in =  extra.inInt(2);
		if (in == 1) {
			explores++;
			exhaust++;
			if (explores == 10) {
				Player.player.addTitle(this.getName() + " wanderer");
			}
			if (explores == 50) {
				Player.player.addTitle(this.getName() + " explorer");
			}
			if (explores == 100) {
				Player.player.addTitle(this.getName() + " guide");
			}
			if (exhaust > 10) {
				if (!extra.chanceIn(1,(int)(exhaust/3))) {
					dryMountain();
					return;
				}
			}
			switch (extra.randRange(1,10)) {
			case 1: rockSlide() ;break;
			case 2: ropeBridge() ;break;
			case 3: goldGoat() ;break;
			case 4: mugger1();break;
			case 5: mugger2();break;
			case 6: mugger3();break;
			case 7: wanderingDuelist();break;
			case 8: oldFighter();break;
			case 9: goldRock();break;
			case 10: findEquip();break;
			}
			Player.addTime(.5);
		}
		if (in == 2) {return;}
		Networking.clearSide(1);
		go();
	}

	@Override
	public void passTime(double time) {
		if (exhaust > 0) {
			exhaust--;
		}

	}
	
	private void rockSlide() {
		extra.println("Some rocks start falling down the mountain!");
		extra.println("1 duck and cover");
		extra.println("2 dodge");
		extra.println("3 do nothing");
		switch (extra.inInt(3)) {
		case 1: 
			if (Player.bag.getBluntResist() > tier * 30) {
				extra.println("You survive the rockslide.");
				Player.addXp(tier);
			}else {
				mainGame.die();
			}
			;break;
		case 2: 
			if (Player.bag.getDodge() > 1) {
				extra.println("You survive the rockslide.");
				Player.addXp(tier);
			}else {
				mainGame.die();
			}
			;break;
		case 3:mainGame.die();break;
		
		}
	}
	
	
	private void ropeBridge() {
		extra.println("You come across a rope bridge. Cross it?");
		if (extra.yesNo()) {
			extra.println("You cross the bridge.");
		}else {
			extra.println("You don't cross the bridge.");
		}
	}
	
	private void goldGoat() {
		extra.println("You spot a bag of gold being carried by a mountain goat! Chase it?");
		Boolean result = extra.yesNo();
		extra.linebreak();
		if (result) {
			if (Math.random() > .5) {
				Networking.sendColor(Color.RED);
				extra.println("A fighter runs up and calls you a thief before launching into battle!");
				Person winner = mainGame.CombatTwo(Player.player.getPerson(), new Person(tier));
				if (winner == Player.player.getPerson()) {
					int gold = (int) (tier*(30*Math.random()));
					extra.println("You pick up " + gold + " gold!");
					Player.bag.addGold(gold);
				}else {
					extra.println("They take the gold sack and leave you rolling down the mountain...");
				}
			}else {
				int gold = (int) (tier*(30*Math.random()));
				extra.println("You pick up " + gold + " gold!");
				Player.bag.addGold(gold);
			}
		}else {
			extra.println("You let the goat run away...");
		}
	}
	
	private void mugger2() {
		Networking.sendColor(Color.RED);
		extra.println("You see a mugger charge at you! Prepare for battle!");
		Person winner = mainGame.CombatTwo(Player.player.getPerson(), new Person(tier));
		if (winner == Player.player.getPerson()) {
		}else {
			extra.println("They take some of your gold!");
			Player.bag.addGold((int) (tier*300*Math.random()));
		}
	}
	
	private void mugger1() {
		Networking.sendColor(Color.RED);
		extra.println("You see someone being robbed! Help?");
		Person robber = new Person(tier);
		robber.getBag().graphicalDisplay(1, robber);
		Boolean help = extra.yesNo();
		if (help) {
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),robber);
	
		if (winner == Player.player.getPerson()) {
			int gold = (int) (Math.random()*130*tier);
			extra.println("They give you a reward of " + gold + " gold in thanks for saving them.");
			Player.bag.addGold(gold);
		}else {
			extra.println("They mugged you too!");
			Player.bag.addGold(-tier*134);
		}
		}else {
			extra.println("You walk away.");
		}
	}
	
	
	private void mugger3() {
		
		extra.println("You see a toll road keeper. Challenge them for their gold?");
		Person robber = new Person(tier);
		robber.getBag().graphicalDisplay(1, robber);
		Networking.sendColor(Color.RED);
		Boolean help = extra.yesNo();
		if (help) {
		Person winner = mainGame.CombatTwo(Player.player.getPerson(), robber);
	
		if (winner == Player.player.getPerson()) {
			int gold = (int) (Math.random()*150*tier);
			extra.println("You find " + gold + " gold in tolls.");
			Player.bag.addGold(gold);
		}else {
			extra.println("They make you pay the toll.");
			Player.bag.addGold(-tier*50);
		}
		}else {
			extra.println("You walk away.");
		}
	}
	
	private void wanderingDuelist() {
		extra.println("A duelist approaches and challenges you to a duel. Accept?");
		Person robber = new Person(tier+1);
		robber.getBag().graphicalDisplay(1, robber);
		Networking.sendColor(Color.RED);
		Boolean help = extra.yesNo();
		if (help) {
		Person winner = mainGame.CombatTwo(Player.player.getPerson(), robber);
	
		if (winner == Player.player.getPerson()) {
			extra.println("You have won the duel!");
		}else {
			extra.println("They mutter a poem for your death.");
		}
		}else {
			extra.println("You walk away. They sigh.");
		}
	}
	
	private void oldFighter() {
		Person robber = new Person(tier+2);
		robber.getBag().graphicalDisplay(1, robber);
		while (true) {
		extra.println("You come across an old fighter, resting on a rock.");
		extra.println("1 Leave");
		Networking.sendColor(Color.RED);
		extra.println("2 Attack them.");
		extra.println("3 Chat with them");
		switch (extra.inInt(3)) {
		default: case 1: extra.println("You leave the fighter alone");return;
		case 2: extra.println("You attack the fighter!");mainGame.CombatTwo(Player.player.getPerson(), robber);return;
		case 3: extra.println("The old fighter turns and answers your greeting.");
		while (true) {
		extra.println("What would you like to ask about?");
		extra.println("1 tell them goodbye");
		extra.println("2 ask for a tip");
		extra.println("3 this mountain");
		int in = extra.inInt(3);
		switch (in) {
			case 1: extra.println("They wish you well.") ;break;
			case 2: Oracle.tip("old");;break;
			case 3: extra.println("\"We are on " + this.getName() + ". Beware, danger lurks on these slopes.\"");break;
		}
		if (in == 1) {
			break;
		}
		}
		}
	}}
	
	private void goldRock() {
		extra.println("You spot a gold rock rolling down the mountain. Chase it?");
		Boolean result = extra.yesNo();
		extra.linebreak();
		if (result) {
			if (Math.random() > .5) {
				Networking.sendColor(Color.RED);
				extra.println("A fighter runs up and calls you a thief before launching into battle!");
				Person winner = mainGame.CombatTwo(Player.player.getPerson(), new Person(tier));
				if (winner == Player.player.getPerson()) {
					int gold = (int) (tier*(100*Math.random()));
					extra.println("You pick up " + gold + " gold!");
					Player.bag.addGold(gold);
				}else {
					extra.println("They take the gold rock and leave you rolling down the mountain...");
				}
			}else {
				int gold = (int) (tier*(100*Math.random()));
				extra.println("You pick up " + gold + " gold!");
				Player.bag.addGold(gold);
			}
		}else {
			extra.println("You let the rock roll away...");
		}
		if (Math.random() > .5) {
			this.rockSlide();
		}
	}
	
	private void findEquip() {
		extra.println("You find a rotting body... With their equipment intact!");
		AIClass.loot(new Person(tier).getBag(),Player.bag,Player.player.getPerson().getIntellect(),true);
	}
	
	private void dryMountain() {
		extra.println("You don't find anything. You think you may have exhausted this mountain, for now. Maybe come back later?");
	}
}
