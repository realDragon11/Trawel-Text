package trawel.personal.item.solid.variants;

public class NameStyleNum implements StyleNum {

	private String name;
	public NameStyleNum(String name) {
		this.name = name;
	}
	
	@Override
	public int generate() {
		return 0;
	}

	@Override
	public String[] decode(int gen) {
		return new String[] {name};
	}

}
