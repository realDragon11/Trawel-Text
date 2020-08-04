package trawel.earts;

import java.util.ArrayList;
import java.util.List;

import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuSelect;
import trawel.Player;
import trawel.extra;

public abstract class EArtSkillMenu extends MenuSelect{
	
	EArt art;
	public EArtSkillMenu(EArt ea) {
		art = ea;
	}

	public static EArtSkillMenu construct(EArt ea) {
		switch (ea) {
		case ARCANIST:
			return new EArtSkillMenu(ea) {

				@Override
				public String title() {
					return "Arcanist";
				}

				@Override
				public boolean go() {
					extra.menuGo(new MenuGenerator() {

						@Override
						public List<MenuItem> gen() {
							List<MenuItem> list = new ArrayList<MenuItem>();
							if (Player.player.getPerson().getSkillPoints() > 0) {
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "increase power";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.player.eaBox.aSpellPower+=1;
										return true;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "learn new spell";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().setSkillPoints((Player.player.getPerson().getSkillPoints()-1));
										Player.player.eaBox.aSpellPower+=1;
										return true;
									}});
							}
							
							return list;
						}
						
					});
					return false;
				}
				
			};
		}
		throw new RuntimeException("EArt not found to construct");
	}
}
