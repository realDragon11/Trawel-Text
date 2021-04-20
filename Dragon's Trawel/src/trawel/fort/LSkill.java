package trawel.fort;

public class LSkill implements java.io.Serializable{

	public SubSkill skill;
	public int value;
	
	public LSkill(SubSkill s, int v) {
		skill = s;
		value = v;
	}
}
