package trawel;

public class QBMenuItem extends MenuSelect {

	public Quest q;
	public Inn i;
	
	public QBMenuItem(Quest q, Inn i) {
		this.q = q;
		this.i = i;
	}
	
	
	@Override
	public String title() {
		return "Accept Quest: " + q.name();
	}

	@Override
	public boolean go() {
		if (Player.player.sideQuests.size() >= 4) {
			extra.println("You have too many side quests already!");
		}else {
			Player.player.sideQuests.add(q);
			q.take();
			i.sideQuests.remove(q);
		}
		return false;
	}

}
