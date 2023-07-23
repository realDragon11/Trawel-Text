/**
 * 
 */
package trawel.quests;

import derg.menus.MenuSelect;

/**
 * @author realb
 *
 */
public class QRMenuItem extends MenuSelect {

	public QuestR qr;
	public QRMenuItem(QuestR qr) {
		this.qr = qr;
	}
	@Override
	public String title() {
		return qr.getName();
	}

	@Override
	public boolean go() {
		return qr.go();
	}

}
