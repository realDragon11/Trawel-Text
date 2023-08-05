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
		locationBottom = Math.min(size-1, size == 9 ? 8 : 7);
	}
	
	@Override
	public List<MenuItem> gen() {
		List<MenuItem> list = new ArrayList<MenuItem>();
		List<MenuItem> header = header();
		List<MenuItem> footer = footer();
		int contentslots = 9;
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
			int i = (scrollDir == 1 ? locationBottom+1 : locationTop-1);
			int limiter = (scrollDir == 1 ? locationBottom+1 : locationTop-1);
			int[] lastpredict = new int[] {topWindow,botWindow};
			do{
				int[] predict = getTopBot(size,contentslots,i+scrollDir,scrollDir);
				if (
						(scrollDir == 1 && predict[0] > limiter)
						||
						(scrollDir == -1 && predict[1] < limiter)
						) {
					break;
				}
				/*if (scrollDir == 1 && predict[0] > locationTop-1) {
					break;
				}
				if (scrollDir == -1 && predict[1] < locationBottom-1) {
					break;
				}*/
				i+=scrollDir;
				//scrolled++;
				lastpredict = predict;
			}while(i > 0 && i < size);
			topWindow = lastpredict[0];
			botWindow = lastpredict[1];
		}
		if (topWindow > 0) {
			list.add(new ScrollMenuItem(backtext,-1,topWindow));
		}

		locationTop = topWindow;
		locationBottom = botWindow;
		
		for (int i = locationTop; i <= locationBottom;i++) {
			list.addAll(forSlot(i));
		}
		
		if (botWindow < size-1) {
			list.add(new ScrollMenuItem(forwardtext,1,botWindow-(size-1)));
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
