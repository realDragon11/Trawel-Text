package derg.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class ScrollMenuGenerator implements MenuGenerator {

	private int locationTop = 0, locationBottom = 0;
	/**
	 * -1 is up, 0 is no scroll, 1 is down
	 */
	private int scrollDir = 0;
	private int size;
	private String backtext, forwardtext;
	
	/**
	 * size must be defined when the generator is created, if it needs to change, must back out and remake the menu
	 * <br>
	 * no constructor for looking at old windows yet, might make a mapping thing
	 * <br>
	 * the strs use <> to replace for 'items left'
	 */
	public ScrollMenuGenerator(int size, String backtext, String forwardtext) {
		this.size = size;
		this.backtext = backtext;
		this.forwardtext = forwardtext;
		
		//TODO
		List<MenuItem> header = header();
		List<MenuItem> footer = footer();
		int contentslots = 8;
		if (header != null) {
			for (MenuItem m: header) {
				if (m.canClick()) {
					contentslots--;
				}
			}
		}

		if (footer != null) {
			for (MenuItem m: footer) {
				if (m.canClick()) {
					contentslots--;
				}
			}
		}
		
		locationBottom = Math.min(size-1,contentslots -(contentslots == size-1 ? 0 : 1));
	}
	
	
	/**
	 * sanity check: there are actually only 3 cases:
	 * 1: we can display everything up
	 * 2: we are in the middle
	 * 3: we can display everything down
	 * 
	 * if in case 1 or 3, instead of trying to scan, should just...
	 * display from that point on???
	 * and I can detect that by seeing if there's 8 items left in that direction,
	 * from our limiter
	 * this will probably not be a flawed algo like the others
	 */
	
	@Override
	public List<MenuItem> gen() {
		List<MenuItem> list = new ArrayList<MenuItem>();
		List<MenuItem> header = header();
		List<MenuItem> footer = footer();
		int contentslots = 8;
		if (header != null) {
			for (MenuItem m: header) {
				if (m.canClick()) {
					contentslots--;
				}
			}
			list.addAll(header);
		}

		if (footer != null) {
			for (MenuItem m: footer) {
				if (m.canClick()) {
					contentslots--;
				}
			}
		}
		//int scrolled = 1;//always scroll one
		int topWindow = locationTop;
		int botWindow = locationBottom;
		if (scrollDir != 0 && !(scrollDir == -1 && topWindow == 0) && !(scrollDir == 1 && botWindow == size-1)) {
			int limiter = (scrollDir == 1 ? locationBottom+1 : locationTop-1);
			//int limiter = i;
			if (scrollDir == 1) {//going down
				if ((size-1)-(contentslots-1) > limiter) {//if we can't display the bottom and our next element
					//we're in the middle
					topWindow = limiter;
					botWindow = limiter+(contentslots-2);
				}else {
					//we're at the bottom
					topWindow = (size-1)-((contentslots-1));
					botWindow = size-1;
				}
			}else {
				if ((contentslots-1) < limiter) {//if we can't display the top and our next element
					//we're in the middle
					topWindow = limiter-(contentslots-2);
					botWindow = limiter;
				}else {
					//we're at the top
					topWindow = 0;
					botWindow = (contentslots-1);
				}
			}
		}
		locationTop = topWindow;
		locationBottom = botWindow;
		
		if (topWindow > 0) {
			list.add(new ScrollMenuItem(backtext,-1,topWindow));
		}
		
		for (int i = locationTop; i <= locationBottom;i++) {
			list.addAll(forSlot(i));
		}
		
		if (botWindow < size-1) {
			list.add(new ScrollMenuItem(forwardtext,1,(size-1)-botWindow));
		}
		
		if (footer != null) {
			list.addAll(footer);
		}
		return list;
	}
	
	/**
	 * IMPORTANT: can only return one menuselect, but can have no selectable items on either side
	 * <br>
	 * WARNING: all menu items should back out with true if they want to redirect somewhere else
	 * <br>
	 * that may get tricky, but remember that menuGo returns the slot that was picked
	 * <br>
	 * TODO: make it return the item slot if menuscrollgoing
	 */
	public abstract  List<MenuItem> forSlot(int i);
	
	/**
	 * can be null
	 */
	public abstract List<MenuItem> header();
	
	/**
	 * can be null
	 */
	public abstract List<MenuItem> footer();
	
	/**
	 * if negative, that means it's selecting from header or footer, counting through the header first
	 * @param i
	 * @return
	 */
	public int getVal(int i) {
		if (!(locationTop <= i && i <= locationBottom)) {
			int headerLength = header().size();
			//1 <= i <= 9
			if (i > headerLength) {
				int windowsize = locationBottom-locationTop;
				return i-windowsize;
			}else {
				return -i;
			}
		}
		return locationTop+i;
	}
	
	private class ScrollMenuItem extends MenuSelect{
		private int dir, left;
		private String text;
		private ScrollMenuItem(String str, int dir, int left) {
			this.text = str;
			this.dir = dir;
			this.left = left;
		}
		@Override
		public String title() {
			return text.replaceAll(Pattern.quote("<>"), ""+left);
		}
		@Override
		public boolean go() {
			scrollDir = dir;
			return false;
		}
	}

}
