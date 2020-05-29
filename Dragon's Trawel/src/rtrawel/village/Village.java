package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

public class Village {

	public List<Content> conts = new ArrayList<Content>();
	public String name = "";
	public static Menu menu = new Menu();
	
	public Village() {
		conts.add(menu);
	}
}
