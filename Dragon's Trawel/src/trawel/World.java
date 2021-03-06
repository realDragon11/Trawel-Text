package trawel;
import java.util.ArrayList;

public class World implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Island> islands;
	private int xSize;
	private int ySize;
	private Town startTown;
	private String name;

	private ArrayList<BardSong> bardSongs;
	private ArrayList<Person> deathCheaters;
	private Calender calender = new Calender();
	private float minLata, maxLata, minLonga, maxLonga;
		
	public World(int x, int y, String _name,float minLata, float minLonga) {
		xSize = x;
		ySize = y;
		islands = new ArrayList<Island>();
		bardSongs = new ArrayList<BardSong>();
		deathCheaters = new ArrayList<Person>();
		name = _name;
		this.minLata = minLata;
		this.maxLata = minLata+y/WorldGen.unitsInLata;
		this.minLonga = minLonga;
		this.maxLonga = minLonga+x/WorldGen.unitsInLonga;;
	}
	
	public void addIsland(Island t) {
		islands.add(t);
	}

	public ArrayList<Island> getislands() {
		return islands;
	}
	
	public void generate() {
		int i = 0;
		while (i < 5) {
			//Island island = new Island();
			//island.generate();
			//islands.add(island);
			i++;
		}
		
		//look for connections, should be careful to not double up on connections, different types is fine though
	}

	public int getXSize() {
		return xSize;
	}

	public void setXSize(int xSize) {
		this.xSize = xSize;
	}

	public int getYSize() {
		return ySize;
	}

	public void setYSize(int ySize) {
		this.ySize = ySize;
	}

	public void passTime(double time) {
		calender.passTime(time);
		for(Island i: islands) {
			i.passTime(time);
		}
		
	}

	public Town getStartTown() {
		return startTown;
	}

	public void setStartTown(Town startTown) {
		this.startTown = startTown;
	}
	
	public void displayMap1() {
		String[][] map = new String[xSize][ySize];
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				map[i][j] = " ";
			}
		}
		for(Island i: islands) {
			for (Town t: i.getTowns()) {
				if (Player.player.getLocation() != t) {
				map[t.getLocation().y-1][t.getLocation().x-1] = Integer.toString(t.getTier()%10);		
				}else {
					map[t.getLocation().y-1][t.getLocation().x-1] = "x";
				}
			}
		}
		
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				extra.print(map[i][j]);
			}
			extra.println();
		}
	}

	public Town getRandom(int level) {
		ArrayList<Town> list = new ArrayList<Town>();
		for(Island i: islands) {
			for (Town t: i.getTowns()) {
				if (t.getTier() == level) {
					list.add(t);
				}
				
				}
			}
		if (list.size() > 0) {
		return list.get((int)(list.size()*Math.random()));}else {
			if (level > 0) {
			return getRandom(level-1);}else {return startTown;}
		}
	}


	
	public BardSong getRandSong() {
		return bardSongs.get(extra.randRange(0,bardSongs.size()-1));
	}

	public BardSong startBardSong(Person manOne, Person manTwo) {
		BardSong b = new BardSong(manOne,manTwo);
		if (!mainGame.noBards) {bardSongs.add(b);}
		return b;
	}
	
	public BardSong startBardSong() {
		BardSong b = new BardSong();
		if (!mainGame.noBards) {bardSongs.add(b);}
		return b;
	}
	
	public void addDeathCheater(Person p) {
		deathCheaters.add(p);
	}
	
	public void removeDeathCheater(Person p) {
		deathCheaters.remove(p);
	}
	
	public Person getDeathCheater(int level) {
		ArrayList<Person> list = new ArrayList<Person>();
		deathCheaters.stream().filter(p -> p.getLevel() == level).forEach(list::add);
		if (list.size() == 0) {
			return null;
		}
		return extra.randList(list);
		
		
	}

	public Calender getCalender() {
		return calender;
	}

	public float getMinLata() {
		return minLata;
	}

	public float getMaxLata() {
		return maxLata;
	}

	public float getMinLonga() {
		return minLonga;
	}

	public float getMaxLonga() {
		return maxLonga;
	}
	

}
