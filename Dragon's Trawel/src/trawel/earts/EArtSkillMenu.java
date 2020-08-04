package trawel.earts;

import trawel.MenuSelect;

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
					// TODO Auto-generated method stub
					return false;
				}
				
			};
		}
		throw new RuntimeException("EArt not found to construct");
	}
}
