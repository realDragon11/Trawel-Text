package trawel.personal.item.solid;

import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.personal.people.Player;

public enum Gem {
	EMERALD("emerald","emeralds","E")
	,RUBY("ruby","rubies","R")
	,SAPPHIRE("sapphire","sapphires","S")
	,AMBER("amber","ambers","A")
	;
		
	public final String name, plural, icon;
	Gem(String _name, String _plural, String _icon){
		name = _name;
		plural = _plural;
		icon = _icon;
	}
	
	public boolean knowsGem() {
		return Player.player.gems.containsKey(this);
	}
	public int getGem() {
		return Player.player.gems.getOrDefault(this,0);
	}
	public void changeGem(int i) {
		Player.player.gems.put(this, Player.player.gems.getOrDefault(this,0)+i);
	}
	
	private static String playerStringTogether() {
		String names = "(";
		String values = null;
		boolean first = true;
		for (Gem g: Gem.values()) {
			if (g.getGem() == 0) {
				continue;//doesn't have, continue
			}
			names += g.icon;
			if (values == null) {
				values = ""+g.getGem();
			}else {
				values +=", "+g.getGem();
			}
		}
		return names +") "+values;
	}
	
	public static String playerGems() {
		String ret = "";
		for (Gem g: Gem.values()) {
			if (g.getGem() == 0) {
				continue;//doesn't have, continue
			}
			if (ret != "") {
				ret +=extra.PRE_WHITE+", ";
			}
			ret += extra.ITEM_DESC_PROP+g.icon +": "+ extra.ITEM_VALUE+g.getGem();
		}
		if (ret == "") {
			return "none";
		}
		return ret;
	}
	
	public static List<Gem> knownGems(){
		List<Gem> knows = new ArrayList<Gem>();
		//this way will be in same order every time
		for (Gem g: Gem.values()) {
			if (g.knowsGem()) {
				knows.add(g);
			}
		}
		return knows;
	}
}
