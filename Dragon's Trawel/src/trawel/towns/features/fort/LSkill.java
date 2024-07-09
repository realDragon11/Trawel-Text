package trawel.towns.features.fort;

public class LSkill implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	public SubSkill skill;
	public int value;
	
	public LSkill(SubSkill s, int v) {
		skill = s;
		value = v;
	}
}
