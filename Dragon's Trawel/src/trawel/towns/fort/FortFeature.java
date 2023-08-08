package trawel.towns.fort;

import trawel.extra;
import trawel.personal.people.Player;
import trawel.towns.Feature;
import trawel.towns.World;

public abstract class FortFeature extends Feature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract int getSize();
	public abstract int getDefenceRating();
	public abstract int getOffenseRating();
	public Laborer laborer;
	public void improveSkill(SubSkill skill, float valueMult) {
		int skillIndex = findLSkill(skill);
		int cost = (int)(  Math.pow(skillIndex == -1 ? 1000 :(laborer.lSkills.get(skillIndex).value+1)*1000,valueMult));
		
		if (Player.player.getTotalBuyPower() >= cost) {
			extra.println("This upgrade will cost " + cost + " "+World.currentMoneyString()+". Buy?");
			if (extra.yesNo()) {
				Player.player.buyMoneyAmount(cost);
				if (skillIndex == -1) {
					laborer.lSkills.add(new LSkill(skill,1));
				}else {
					laborer.lSkills.get(skillIndex).value++;
				}
			}
		}else {
			extra.println("You can't afford that upgrade. (" + cost + " "+World.currentMoneyString()+")");
		}
	}
	public int findLSkill(SubSkill skill) {
		for (int i = 0;i < laborer.lSkills.size();i++) {
			if (laborer.lSkills.get(i).skill.equals(skill)) {
				return i;
			}
		}
		return -1;
	}
}
