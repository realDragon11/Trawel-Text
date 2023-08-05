package derg.menus;

import java.text.Format;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Pattern;

import trawel.extra;

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
			/*int[] lastpredict = new int[] {topWindow,botWindow};
			do{
				int[] predict = getTopBot(size,contentslots,i+scrollDir,scrollDir);
				if (//if we aren't displaying our next item, or lose ground
						(scrollDir == 1 && (predict[0] > limiter || lastpredict[1] > predict[1]))
						||
						(scrollDir == -1 && predict[1] < limiter || lastpredict[0] < predict[0])
						) {
					break;
				}
				i+=scrollDir;
				//scrolled++;
				lastpredict = predict;
			}while(i > 0 && i < size);
			topWindow = lastpredict[0];
			botWindow = lastpredict[1];*/
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
	
	private static ScrollSides fromMenus(int size, int top, int bot) {
		ScrollSides predict = ScrollSides.NONE;
		if (bot != size-1) {
			predict = predict.update(ScrollSides.DOWN);
		}
		if (top != 0) {
			predict = predict.update(ScrollSides.UP);
		}
		
		return predict;
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
	
	private static int[] getTopBot(int size, int purecapacity, int current, int direction) {
		int top = direction == -1 ? current : current-purecapacity;
		int bot = direction == -1 ? current+purecapacity : current;
		top = extra.clamp(top,0,size-1);
		bot = extra.clamp(bot,0,size-1);
		ScrollSides ss = fromMenus(size,top,bot);
		if (direction == -1) {
			return new int[] {top+ss.spaces,bot,ss.spaces};
		}else {
			return new int[] {top,bot-ss.spaces,ss.spaces};
		}		
	}
	
	private enum ScrollSides{
		UP(1), DOWN(1), UPDOWN(2), NONE(0);
		public final int spaces;
		ScrollSides(int _spaces){
			spaces = _spaces;
		}
		
		public ScrollSides update(ScrollSides add) {
			switch (add) {
			case UP:
				switch (this) {
				case UPDOWN:
					return UPDOWN;
				case DOWN:
					return UPDOWN;
				}
				return UP;
			case DOWN:
				switch (this) {
				case UPDOWN:
					return UPDOWN;
				case UP:
					return UPDOWN;
				}
				return DOWN;
			case UPDOWN:
				return UPDOWN;
			}
			return NONE;
		}
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
