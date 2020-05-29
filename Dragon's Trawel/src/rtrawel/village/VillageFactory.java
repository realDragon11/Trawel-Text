package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

public class VillageFactory {

	public static List<Village> villages = new ArrayList<Village>();
	
	public static Village init() {
		Village homa = new Village("homa");
		villages.add(homa);
		Village unun = new Village("unun");
		villages.add(unun);
		new Connection(homa,unun,null);
		return villages.get(0);
	}
}
