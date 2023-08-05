package derg.menus;

import java.util.ArrayList;
import java.util.List;

public abstract class ScrollMenuGenerator implements MenuGenerator {

	private int locationTop = 0, locationBottom = 0;
	private int scrollDir = 0;//-1 is up, 0 is no scroll, 1 is down 
	@Override
	public List<MenuItem> gen() {
		List<MenuItem> list = new ArrayList<MenuItem>();
		List<MenuItem> header = header();
		List<MenuItem> footer = footer();
		int contentslots = 9;
		int size = size();
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
			int i = scrollDir == 1 ? locationBottom+1 : locationTop-1;
			int[] lastpredict = new int[] {topWindow,botWindow};
			do{
				int[] predict = getTopBot(size,contentslots,i+scrollDir,scrollDir);
				if (predict[2] < contentslots) {
					break;
				}
				i+=scrollDir;
				//scrolled++;
				lastpredict = predict;
			}while(i > 0 && i < size);//scrolled < contentslots && 
			
			//if (scrollDir == 1) {
				//botWindow += scrolled;
				//topWindow = botWindow-contentslots;

			//}else {
				//topWindow += scrolled;
				//botWindow = topWindow+contentslots;
			//}
			topWindow = lastpredict[0];
			botWindow = lastpredict[1];
		}

		locationTop = topWindow;
		locationBottom = botWindow;
		
		for (int i = locationTop; i <= locationBottom;i++) {
			list.add(forSlot(i));
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
	
	private static boolean fromAfterCannot(int from, int size, int capacity) {
		return ((size-(from+1)) > capacity);
	}
	
	private static boolean fromBeforeCannot(int from, int size, int capacity) {
		return (from > capacity);
	}
	
	/**
	 * WARNING: all menu items should back out with true if they want to redirect somewhere else
	 * <br>
	 * that may get tricky, but remember that menuGo returns the slot that was picked
	 * <br>
	 * TODO: make it return the item slot if menuscrollgoing
	 */
	public abstract MenuItem forSlot(int i);
	
	public abstract int size();
	
	/**
	 * can be null
	 */
	public abstract List<MenuItem> header();
	
	/**
	 * can be null
	 */
	public abstract List<MenuItem> footer();

}
