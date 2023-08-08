package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.extra;
import trawel.earts.EArt;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public class Library extends Feature {

	private static final long serialVersionUID = 1L;
	//TODO: make these have some real meaning and not just be bad flavor
	//private ArrayList<Book> books = new ArrayList<Book>();
	private byte libFlags = extra.emptyByte;
	
	public Library(String _name, Town _town) {
		start();
		name = _name;
		town = _town;
		tutorialText = "Libraries hold knowledge from all worlds.";
	}
	public enum LibraryFlag{
		HAS_BONUS_FEAT_PICKED
	}
	
	public boolean getLibFlag(LibraryFlag flag) {
		return extra.getEnumByteFlag(flag.ordinal(),libFlags);
	}
	
	public void setLibFlag(LibraryFlag flag, boolean bool) {
		extra.setEnumByteFlag(flag.ordinal(),libFlags,bool);
	}
	
	@Override
	public String getColor() {
		return extra.F_AUX_SERVICE;
	}

	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|library|Library|");
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				/*list.add(new MenuSelect(){

					@Override
					public String title() {
						return "read books";
					}

					@Override
					public boolean go() {
						while (true) {
						int i = 1;
						for (Book b: books) {
							extra.println(i +" " + b.name + " by " + b.author);
							i++;
						}
						extra.println(i + " back");
						int in = extra.inInt(i);
						i = 1;
						for (Book b: books) {
							if (in == i) {
								b.display();
							}
							i++;
						}
						if (in == i) {
							return false;
						}}
					}
					
				});*/
				//doesn't let you hoard picks so you're more likely to find some later if you need to look around
				if (!getLibFlag(LibraryFlag.HAS_BONUS_FEAT_PICKED) && Player.player.getFeatPicks() < 3) {
					list.add(new MenuSelect(){

						@Override
						public String title() {
							return "study general lore";
						}

						@Override
						public boolean go() {
							setLibFlag(LibraryFlag.HAS_BONUS_FEAT_PICKED,true);
							Player.player.addFeatPick(1);
							extra.println("You learn enough to reassess you future. You gained one feat pick as a result.");
							return false;
						}
						
					});
				}
				/*
				if (!hasStudiedArcane && Player.player.eArts.contains(EArt.ARCANIST)) {
					list.add(new MenuSelect(){

						@Override
						public String title() {
							return "study arcane lore";
						}

						@Override
						public boolean go() {
							hasStudiedArcane = true;
							Player.player.eaBox.aSpellPower +=.2;
							return false;
						}
						
					});
				}*/
				if (Player.bag.getDrawBanes().contains(DrawBane.KNOW_FRAG)) {
					int frag_count = (int) Player.bag.getDrawBanes().stream().filter(db -> db == DrawBane.KNOW_FRAG).count();
					list.add(new MenuSelect(){

						@Override
						public String title() {
							return "study knowledge fragments (have: "+frag_count+")";
						}

						@Override
						public boolean go() {
							extra.println("Study your " + frag_count+" fragment(s)? This will destroy them and get you closer to a free feat point."
									+ " You are currently " + Player.player.strKnowFrag());
							if (!extra.yesNo()){
								return false;
							}
							List<DrawBane> dbs = Player.bag.getDrawBanes();
							for (int i = dbs.size()-1;i >= 0;i--) {
								if (dbs.get(i).equals(DrawBane.KNOW_FRAG)) {
									Player.player.addKnowFrag();
								}
								dbs.remove(i);
							}
							extra.println("You are now " + Player.player.strKnowFrag());
							
							return false;
						}
						
					});
				}
				list.add(new MenuBack("leave"));
				return list;
			}});
	}

	private void start() {
		/*while (books.size() < 4) {
			books.add(BookFactory.randBook());
		}
		*/
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		/*if (books.size() > 4 && extra.chanceIn(1,5)) {
			books.remove(0);
		}
		if (books.size() > 7) {
			books.remove(0);
		}
		if (books.size() < 8 && extra.chanceIn(1,3)) {
			books.add(BookFactory.randBook());
		}*/
		
		return null;

	}

}
