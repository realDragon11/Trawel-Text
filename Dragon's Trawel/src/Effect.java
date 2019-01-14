
public enum Effect implements java.io.Serializable {
	
	CURSE("Cursed","Start battles with less hp.");
	
	private String name,desc;
	Effect(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	public String getName() {return name;}
	public String getDesc() {return desc;}
}
