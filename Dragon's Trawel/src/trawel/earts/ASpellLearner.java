package trawel.earts;

import trawel.MenuSelect;
import trawel.Player;

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
		Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
		Player.player.eaBox.aSpellPower+=.5;
		Player.player.eaBox.aSpells.add(spell);
		return true;
	}

}