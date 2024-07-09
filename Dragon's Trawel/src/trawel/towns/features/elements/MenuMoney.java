package trawel.towns.features.elements;

import derg.menus.MenuLine;
import trawel.personal.people.Player;
import trawel.towns.contexts.World;

public class MenuMoney extends MenuLine {

	@Override
	public String title() {
		return "You have " +Player.bag.getAether() + " aether and " + World.currentMoneyDisplay(Player.player.getGold()) +".";
	}

}
