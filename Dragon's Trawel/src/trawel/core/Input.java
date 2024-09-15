package trawel.core;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuGeneratorPaged;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.helper.methods.extra;
import trawel.personal.people.Player;
import trawel.towns.data.FeatureData;

public class Input {

	private static int backingQueue = 0;

	public static void kickOut() {
		backingQueue = 10;
	}

	public static final void linebreak(int num) {
		if (mainGame.lineSep) {
			Print.println("------------");
		}
		Print.collectInputBucket(num);
	}

	public static final boolean yesNo() {
		if (backingQueue > 0) {
			backingQueue--;
			return false;
		}
		trawel.threads.BlockTaskManager.start();
		while (true) {
			Print.println("1 yes");
			Print.println("9 no");
			Networking.sendStrong("Entry|yesno|");//need to add this coloring behavior to normal inputs
			//if ((Networking.connected() && mainGame.GUIInput)  || Networking.autoconnectSilence) {
			//while(true) {
			int ini = Networking.nextInt();
			while(ini != 1 && ini != 9) {
				if (ini == 0) {
					Networking.sendStrong("Entry|Finish|");
					return false;
				}
				if (ini == 10) {
					backingQueue = 10;
					Networking.sendStrong("Entry|Finish|");
					return false;
				}
				Networking.sendStrong("Entry|Reset|");
				if (ini != -4) {//redo, input was consumed for some other purpose
					Print.println("Please type 1 or 9.");
				}
				Print.println("1 yes");
				Print.println("9 no");
				ini = Networking.nextInt();
				if (ini == -99 || ini == -1) {
					Networking.unConnect();
					throw new RuntimeException("invalid input stream error");
				}
	
			}
			Networking.sendStrong("Entry|Finish|");
			Input.linebreak(ini);
			trawel.threads.BlockTaskManager.halt();
			return ini == 1;
	
			/*}else {
	
					str = mainGame.scanner.next();
					extra.linebreak();
					str = str.toLowerCase();
					//extra.println(str);
					if (str.equals("yes") || str.equals("y")|| str.equals("1")) {
						trawel.threads.BlockTaskManager.halt();
						return true;
					}
					if (str.equals("no") || str.equals("n") || str.equals("0") || str.equals("9")) {
						trawel.threads.BlockTaskManager.halt();
						return false;
					}
					extra.println("Yes or No?");
	
				}*/
		}
	}

	public static final int inInt(int max) {
		return inInt(max,false,false);
	}

	public static final int inInt(int max, boolean alwaysNine, boolean canBack) {
		if (backingQueue > 0) {
			if (canBack) {
				backingQueue--;
				Networking.sendStrong("Entry|Finish|");
				return 9;
			}else {
				backingQueue = 0;
			}
		}
		Networking.sendStrong("Entry|Activate|" + max + "|");
		trawel.threads.BlockTaskManager.start();
		int ini=  Networking.nextInt();
		while((ini < 1 || ini > max)) {
			if (alwaysNine && ini == 9) {
				break;
			}
			if (canBack && ini == 10) {
				backingQueue = 10;
				ini = 9;
				break;
			}
			if (canBack && ini == 0) {
				ini = 9;
				break;
			}
			
			if (ini != -2) {//silent loading
				Networking.sendStrong("Entry|Reset|");
				if (ini == -4) {//redo, input was consumed for some other purpose
					if (max == 0 && alwaysNine) {
						Print.println("Input: 9");
					}else {
						Print.println("Input: 1 to " + max + "." + (alwaysNine ? " (or 9)" : ""));
					}
					
				}else {//normal redo
					if (max == 0 && alwaysNine) {
						Print.println("Please enter the number 9.");
					}else {
						Print.println("Please enter a number from 1 to " + max + "." + (alwaysNine ? " (or 9)" : ""));
					}
				}
			}
			
			ini = Networking.nextInt();
	
			if (ini == -99) {
				Networking.unConnect();
				throw new RuntimeException("invalid input stream error");
			}
			if (ini == -1) {
				Networking.unConnect();
				throw new RuntimeException("input stream ended");
			}
		}
		Networking.sendStrong("Entry|Finish|");
		trawel.threads.BlockTaskManager.halt();
		Input.linebreak(ini);
		return ini;
	}

	public static String inString() {
		return mainGame.scanner.nextLine().toLowerCase();
	}

	public static void inputContinue() {
		Print.println("1 continue");
		Input.inInt(1);
	}

	//TODO: menuGoCategory that takes MenuItems that have categories- (up to 8 usually, but allows nesting)
	//if a category (and it's nested categories) only have on option
	//it is displayed directly, otherwise a new option that just lets you enter the category is created
	//this will have different logic code but maintains the 'store menu until an actual option is picked'
	//logic of menuGo
	public static int menuGo(MenuGenerator mGen) {
		List<MenuItem> mList = new ArrayList<MenuItem>();
		int v;
		boolean forceLast;
		boolean canBack;
		List<MenuItem> subList;
		while (true) {
			mGen.onRefresh();
			mList = mGen.gen();
			if (mList == null) {
				return -1;//used for nodes so they can force interactions cleanly
			}
			v = 1;
			forceLast = false;
			canBack = false;
			subList = new ArrayList<MenuItem>();
			for (MenuItem m: mList) {
				if (m.forceLast()) {
					subList.add(m);
					Print.println("9 " + m.title());
					forceLast = true;
					canBack = m.canBack();
					//force last must be last, and pickable
					//UPDATE: it works with other labels after it now
					continue;
				}else {
					if (m.canClick()) {
						assert forceLast == false;
						Print.println(v + " " + m.title());
						v++;
						subList.add(m);
					}else {
						Print.println(m.title());
					}
				}
			}
	
			//mList.stream().filter(m -> m.canClick() == true).forEach(subList::add);
			int val;
			if (!forceLast) {
				val = Input.inInt(subList.size())-1;
			}else {
				val = Input.inInt(subList.size()-1,true,canBack)-1;
			}
			boolean ret;
			if (val < subList.size()) {
				ret = subList.get(val).go();
			}else {
				ret = subList.get(subList.size()-1).go();
			}
			if (ret) {
				if (mGen instanceof ScrollMenuGenerator) {
					return ((ScrollMenuGenerator)mGen).getVal(val);
				}
				return val;
			}
		}
	}

	public static int menuGoPaged(MenuGeneratorPaged mGen) {
		List<MenuItem> mList = new ArrayList<MenuItem>();
		mList = mGen.gen();
	
		mGen.page = 0;
		while (true) {
			mGen.lists.clear();
			mGen.maxPage = 0;
			int j = 0;
			int count = 0;
			int start = 0;
			mList.add(new MenuLine() {//dummy node
				@Override
				public String title() {
					return "end of pages";
				}});
			while (j < mList.size()) {
	
				if (mList.get(j).canClick() == true) {
					count++;
					j++;
				}else {
					j++;
					continue;
				}
				if (count == 8 && mList.size()-1 > 8) {
					mList.add(j,new MenuLine() {
	
						@Override
						public String title() {
							return (mGen.page+1) + "/" + (mGen.maxPage+1);
						}});
					j++;
					mList.add(j,new MenuSelect() {
	
						@Override
						public String title() {
							return "next page";
						}
	
						@Override
						public boolean go() {
							mGen.page++;
							return false;
						}});
					count++; j++;
					mGen.lists.add(new ArrayList<MenuItem>());
					for (int k = 0;k < j;k++) {
						mGen.lists.get(0).add(mList.get(k));
					}
					start = j;
					mGen.maxPage++;
					count+=2;
				}else {
					if (count > 10 && (count%9 == 0 || j == mList.size()-1)) {
						mList.add(j,new MenuLine() {
	
							@Override
							public String title() {
								return (mGen.page+1) + "/" + (mGen.maxPage+1);
							}});
						j++;
						mList.add(j,new MenuSelect() {
	
							@Override
							public String title() {
								return "last page";
							}
	
							@Override
							public boolean go() {
								mGen.page--;
								return false;
							}});
						count++; j++;
						if (true == true) {//figure out a last page condition
							mList.add(j,new MenuSelect() {
	
								@Override
								public String title() {
									return "next page";
								}
	
								@Override
								public boolean go() {
									mGen.page++;
									return false;
								}});
							count++; j++;
							//int start = 1+mList.indexOf(mGen.lists.get(mGen.maxPage-1).get(mGen.lists.get(mGen.maxPage-1).size()-1));
							mGen.lists.add(new ArrayList<MenuItem>());
	
							for (int k = start;k < j;k++) {
								mGen.lists.get(mGen.maxPage).add(mList.get(k));
							}
							//if (count%9==0) {//doesn't work in every case
							mGen.maxPage++;//}
							start = j;
						}
					}
	
				}
	
			}
	
			if (mGen.maxPage == 0) {
				mGen.lists.add(new ArrayList<MenuItem>());
				for (int k = 0;k < j;k++) {
					mGen.lists.get(0).add(mList.get(k));
				}
			}
	
			if (start == j-1) {
				mGen.maxPage--;
				mGen.lists.get(mGen.maxPage).remove(mGen.lists.get(mGen.maxPage).size()-1);
			}
			mGen.page = extra.clamp(mGen.page,0,mGen.maxPage);
			if (mGen.header != null) {
				Print.println(mGen.header.title());
			}
			int v = 1;
			List<MenuItem> subList = new ArrayList<MenuItem>();
			for (int i = 0;i < mGen.lists.get(mGen.page).size();i++)
				if (mGen.lists.get(mGen.page).get(i).canClick() == true) {
					subList.add(mGen.lists.get(mGen.page).get(i));
					Print.println(v + " " +mGen.lists.get(mGen.page).get(i).title());
					v++;
				}else {
					Print.println(mGen.lists.get(mGen.page).get(i).title());
				}
			int val = Input.inInt(subList.size())-1;
			boolean ret = subList.get(val).go();
			if (ret) {
				return val;
			}
			mList = mGen.gen();
	
		}
	}

	/**
	 * prevents the player from automatically backing out from one case to another
	 * <br>
	 * example: player might want to back out of drawbane selection, inventory selection, etc manually
	 */
	public static final void endBackingSegment() {
		backingQueue = 0;
	}

}
