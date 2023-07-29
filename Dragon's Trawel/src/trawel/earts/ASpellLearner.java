package trawel.earts;

import derg.menus.MenuSelect;
import trawel.personal.people.Player;

public class ASpellLearner extends MenuSelect {

	public ASpell spell;
	public ASpellLearner(ASpell spell) {
		this.spell = spell;
	}
	
	@Override
	public String title() {
		return spell.name + ": " + spell.desc;
	}

	@Override
	public boolean go() {
		Player.player.getPerson().useSkillPoint();
		Player.player.eaBox.aSpellPower+=.5;
		Player.player.eaBox.aSpells.add(spell);
		Player.player.eaBox.arcTrainLevel++;
		return true;
	}

}