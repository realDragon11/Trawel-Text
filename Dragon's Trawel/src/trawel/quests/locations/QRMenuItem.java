/**
 * 
 */
package trawel.quests.locations;

import derg.menus.MenuSelect;

public class QRMenuItem extends MenuSelect {

	public QuestR qr;
	public QRMenuItem(QuestR qr) {
		this.qr = qr;
	}
	@Override
	public String title() {
		return qr.getName() + " ("+qr.overQuest.name()+")";
	}

	@Override
	public boolean go() {
		qr.go();
		return false;
	}

}
