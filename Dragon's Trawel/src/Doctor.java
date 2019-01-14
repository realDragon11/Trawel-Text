
public class Doctor extends Feature {
	private Town town;

	public Doctor(String name,Town t) {
		this.name = name;
		town = t;
	}
	@Override
	public void go() {
		int dcost = town.getTier()*10;
		int cost = town.getTier()*Player.player.getPerson().effectsSize()*50;
		extra.println("1 diagnois (" + dcost+" gold)");
		extra.println("2 cure (" + cost+" gold)");
		extra.println("3 exit");
		switch (extra.inInt(3)) {
		
		}
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub

	}

}
