package trawel.earts;

public enum EArt implements java.io.Serializable{

	ARCANIST("Arcanist","A combat magic class focused on swapping out spell slots.", EAType.MAGIC);
	
	public String name, desc;
	public EAType type;
	EArt(String nam, String des, EAType typ) {
		this.name = nam;
		this.desc = des;
		this.type = typ;
	}
}
