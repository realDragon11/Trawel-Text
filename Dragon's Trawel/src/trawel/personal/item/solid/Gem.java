package trawel.personal.item.solid;

import java.util.ArrayList;
import java.util.List;

import trawel.helper.methods.extra;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.people.Player;

public enum Gem {
	/**
	 * merchant, taken
	 */
	EMERALD("emerald","emeralds","E",2)
	/**
	 * hero, granted
	 */
	,RUBY("ruby","rubies","R",3)
	/**
	 * rogue, granted and taken
	 */
	,SAPPHIRE("sapphire","sapphires","S",3)
	/**
	 * hunter, granted
	 */
	,AMBER("amber","amber","A",10)
	;
		
	public final String name, plural, icon;
	public final int unitSize;
	Gem(String _name, String _plural, String _icon, int _unitSize){
		name = _name;
		plural = _plural;
		icon = _icon;
		unitSize = _unitSize;
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
