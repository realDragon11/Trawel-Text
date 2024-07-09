package trawel.towns.features.fort;

import derg.menus.MenuLine;
import trawel.core.Input;
import trawel.core.Print;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.towns.contexts.World;
import trawel.towns.features.Feature;
import trawel.towns.features.fort.elements.LSkill;
import trawel.towns.features.fort.elements.Laborer;
import trawel.towns.features.fort.elements.SubSkill;
import trawel.towns.features.fort.features.FortHall;

public abstract class FortFeature extends Feature {

	private static final long serialVersionUID = 1L;
	public abstract int getSize();
	public abstract int getDefenceRating();
	public abstract int getOffenseRating();
	public Laborer laborer;
	
	@Override
	public void goHeader() {
		Player.player.atFeature = this;
	}
	
	public void improveSkill(SubSkill skill, float valueMult) {
		int skillIndex = findLSkill(skill);
		int baseValue = 100;//was 200
		int cost = (int) (baseValue*Math.pow((skillIndex == -1 ? 1 : laborer.lSkills.get(skillIndex).value+1),valueMult));
		
		if (Player.player.getTotalBuyPower() >= cost) {
			Print.println("This upgrade will cost " + cost + " "+World.currentMoneyString()+". Buy?");
			if (Input.yesNo()) {
				Player.player.buyMoneyAmount(cost);
				if (skillIndex == -1) {
					laborer.lSkills.add(new LSkill(skill,1));
				}else {
					laborer.lSkills.get(skillIndex).value++;
				}
			}
		}else {
			Print.println("You can't afford that upgrade. (" + cost + " "+World.currentMoneyString()+")");
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
	
	public static MenuLine getPlayerBuyPower() {
		return new MenuLine() {

			@Override
			public String title() {
				return "You have " +Player.showGold() + " and " + Player.bag.getAether() + " aether for " + Player.player.getTotalBuyPower() + " buying power.";
			}};
	}
	
	@Override
	public SuperPerson getOwner() {
		if (this instanceof FortHall) {
			return super.getOwner();//dumb workaround
		}
		for (Feature f: town.getFeatures()) {
			if (f instanceof FortHall) {
				return f.getOwner();
			}
		}
		throw new RuntimeException("Fort hall not found for "+this.getName() + " in " +town.getName());
	}
}
