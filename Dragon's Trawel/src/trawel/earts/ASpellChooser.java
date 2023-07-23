package trawel.earts;

import derg.menus.MenuSelect;
import trawel.personal.people.Player;

public class ASpellChooser extends MenuSelect {

	public ASpell spell;
	public int slot;
	public ASpellChooser(ASpell spell, int slot) {
		this.spell = spell;
		this.slot = slot;
	}
	
	@Override
	public String title() {
		return spell.name + ": " + spell.desc;
	}

	@Override
	public boolean go() {
		switch (slot) {
		case 1:	
			Player.player.eaBox.aSpell1 = spell;
			break;
		case 2:	
			Player.player.eaBox.aSpell2 = spell;
			break;
		}
		return true;
	}

}
