package derg.menus;

public class MenuBack extends MenuLast {

	private final String text;
	
	public MenuBack() {
		text = "back";
	}
	
	public MenuBack(String backText) {
		text = backText;
	}
	
	@Override
	public String title() {
		return text;
	}

	@Override
	public boolean go() {
		return true;
	}
	
	@Override
	public boolean canBack() {
		return true;
	}

}
