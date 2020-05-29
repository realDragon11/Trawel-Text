package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

public class Roads implements Content {

	public List<Connection> connects = new ArrayList<Connection>(); 
	
	@Override
	public boolean go() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String name() {
		return "roads";
	}

}
