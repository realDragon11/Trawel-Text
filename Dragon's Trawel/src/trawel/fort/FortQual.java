package trawel.fort;

public enum FortQual {

	RICH("Wealthy","Has more wealth to spend, but more prone to attacks."),
	HIDDEN("Hidden","Much less prone to attack.");
	public String name, desc;
	FortQual(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
}
