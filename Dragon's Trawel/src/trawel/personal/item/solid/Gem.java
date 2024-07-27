package trawel.personal.item.solid;

import java.util.ArrayList;
import java.util.List;

import trawel.helper.constants.TrawelColor;
import trawel.personal.people.Player;

public enum Gem {
	/**
	 * merchant, taken
	 */
	EMERALD("emerald","emeralds","E",TrawelColor.TIMID_GREEN,2)
	/**
	 * hero, granted
	 */
	,RUBY("ruby","rubies","R",TrawelColor.TIMID_RED,3)
	/**
	 * rogue, granted and taken
	 */
	,SAPPHIRE("sapphire","sapphires","S",TrawelColor.TIMID_BLUE,3)
	/**
	 * hunter, granted
	 */
	,AMBER("amber","amber","A",TrawelColor.TIMID_ORANGE,10)
	;
		
	private final String name, plural, icon, color;
	public final int unitSize;
	Gem(String _name, String _plural, String _icon, String _color, int _unitSize){
		name = _name;
		plural = _plural;
		icon = _icon;
		unitSize = _unitSize;
		color = _color;
	}
	
	public String fancyName() {
		return color+name+TrawelColor.COLOR_RESET;
	}
	
	public String fancyNamePlural() {
		return color+plural+TrawelColor.COLOR_RESET;
	}
	
	public String fancyName(boolean plural) {
		if (plural) {
			return fancyNamePlural();
		}
		return fancyName();
	}
	
	public String fancyName(int amount) {
		if (amount != 1) {
			return fancyNamePlural();
		}
		return fancyName();
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
	
	/**
	 * themed is the bonus for the gem being of the type that goes with the reward
	 * <br>
	 * Emerald = trade actions, wealth creation from finding things
	 * <br>
	 * Ruby = slaying strong enemies, bosses, completing exploration tasks
	 * <br>
	 * Sapphire = completing attribute contests
	 * <br>
	 * Amber = completing kill and cleanse quests
	 * <br>
	 * meant to go into IEffectiveLevel.cleanRangeReward
	 */
	public float reward(float mult, boolean themed) {
		return unitSize*mult*(themed ? 1.8f : 1.2f);
	}
	
	private static String playerStringTogether() {
		String names = "(";
		String values = null;
		boolean first = true;
		for (Gem g: Gem.values()) {
			if (g.getGem() == 0) {
				continue;//doesn't have, continue
			}
			names += g.color+g.icon+TrawelColor.COLOR_RESET;
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
				ret +=TrawelColor.PRE_WHITE+", ";
			}
			ret += TrawelColor.ITEM_DESC_PROP+g.icon +": "+ TrawelColor.ITEM_VALUE+g.getGem();
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
