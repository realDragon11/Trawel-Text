/**
 * 
 */
package trawel;

/**
 * @author realb
 *
 */
public class QRMenuItem implements MenuItem {

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
