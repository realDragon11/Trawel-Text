package trawel;

import java.util.List;

public abstract class MenuGeneratorPaged {

	
	public abstract List<MenuItem> gen();
	
	public int page = 0;//unfortunately had to make it a class for this
	public int maxPage = 0;
	
	public MenuLine header;
}
