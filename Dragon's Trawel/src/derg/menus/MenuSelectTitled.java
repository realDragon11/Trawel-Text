package derg.menus;

public abstract class MenuSelectTitled extends MenuSelect {

	public String nameT;
	public MenuSelectTitled(String name) {
		this.nameT = name;
	}
	@Override
	public String title() {
		return nameT;
	}

}
