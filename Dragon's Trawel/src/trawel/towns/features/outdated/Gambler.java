package trawel.towns.features.outdated;

import java.util.List;

import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.personal.item.Inventory;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.features.Feature;

public class Gambler extends Feature{

	private static final long serialVersionUID = 1L;
	private String type;
	private int gold;
	
	public Gambler(String name, String type, int gold) {
		this.type = type;
		this.name = name;
		this.gold = gold;
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_SERVICE;
	}
	
	@Override
	public String nameOfFeature() {
		return "Gambling Hall";
	}
	
	@Override
	public String nameOfType() {
		return "Gambling Hall";
	}
	
	@Override
	public Area getArea() {
		return Area.MISC_SERVICE;
	}
	
	@Override
	public void go() {
		Print.println("1 play " +type);
		Print.println("2 leave");
		int input =  Input.inInt(2);
		Input.linebreak();
		if (input == 2){
			return;
		}
		if (input == 1) {
		}else {
			go();
		}
		Print.println("How much do you bid? They have " +gold + " gold to win.");
		int in =  Input.inInt(gold);
		Input.linebreak();
		Inventory bag = Player.player.getPerson().getBag();
		int bid = Math.max(Math.min(bag.getGold(),Math.min(gold,in)),0);
		Print.println("You bid " + bid + " gold.");
		switch (type) {
		case "cups":
			Print.println("They place a ball under a cup, then mix it with two other cups...");
			Print.println("Which cup do you pick?");
			Print.println("1 cup");
			Print.println("2 cup");
			Print.println("3 cup");
			in =  Input.inInt(3);
			Input.linebreak();
			if (in == 1 || in ==2 || in ==3) {
				if (Math.random()*3 < 1) {
					Print.println("You win " + bid  + " gold!");
					bag.addGold(bid);
					gold-=bid;
				}else {
					Print.println("You lose!");
					bag.addGold(-bid);
					gold+=bid;
				}
			}else {
				Print.println("That's not a cup! They decide to call it a draw.");
			}
			Print.println("Would you like to play again?");
			go();
			;break;
			case "digger":
			//generate a 8 by 8 grid, ask for width and than height, three tries use taxicab distance
			//int x = extra.randRange(1,8);
			//int y = extra.randRange(1,8);
			//need something to store tries
			
			
			break;
		}
		

	}


	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		gold += (int)time;
		return null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
