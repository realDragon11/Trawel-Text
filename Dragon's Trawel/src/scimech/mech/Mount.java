package scimech.mech;

import java.util.ArrayList;
import java.util.List;

public abstract class Mount {

	protected int slots, heat = 0;
	protected List<Fixture> fixtures = new ArrayList<Fixture>();
}
