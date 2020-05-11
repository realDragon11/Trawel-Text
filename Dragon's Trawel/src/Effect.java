
public enum Effect implements java.io.Serializable {
	
	CURSE("Cursed","Start battles with less hp.",true,false),
	BURNOUT("Burnout","Decreased skill.",false,true),
	
	BLEED("Bleeding","Take damage over time.",false,false),
	MAJOR_BLEED("Bleeding even more","Take damage over time.",false,false);
	
	private String name,desc;
	private boolean lasts, stacks;
	Effect(String name, String desc, boolean lasts, boolean stacks) {
		this.name = name;
		this.desc = desc;
		this.lasts = lasts;
		this.stacks = stacks;
	}
	public String getName() {return name;}
	public String getDesc() {return desc;}
	public boolean lasts() {
		return lasts;
	}
	public boolean stacks() {
		return stacks;
	}
}
