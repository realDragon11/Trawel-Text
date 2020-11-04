package trawel.fort;

import java.util.ArrayList;
import java.util.List;

public abstract class Laborer {

	public String name;
	public LaborType type;
	public List<LSkill> lSkills = new ArrayList<LSkill>();
}
