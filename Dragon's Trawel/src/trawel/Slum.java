package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Slum extends Feature {

	private boolean removable;
	public SuperPerson crimeLord;
	private double timePassed = 0;
	private int wins = 0;
	private Town town;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Slum(Town t, String name,boolean removable) {
		town = t;
		this.name = name;
		tutorialText = "Slums house crime lords.";
		color = Color.RED;
		this.removable = removable;
	}
	
	@Override
	public void go() {
		Networking.setArea("dungeon");
		Slum sl = this;
		int removecost = town.getTier()*1000;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
			
				if (crimeLord == null && removable) {
					
					mList.add(new MenuSelect() {
						
						@Override
						public String title() {
							return "pay to remove slum ("+removecost+" gold)";
						}
		
						@Override
						public boolean go() {
							if (Player.bag.getGold() > removecost) {
								extra.println("You pay for the reform programs.");
								Player.bag.modGold(-removecost);
								town.enqueneRemove(sl);
								return true;
							}else {
								extra.println("You can't afford to uplift the slum out of poverty.");
							}
							return false;
						}
					});
				}
				mList.add(new MenuSelect() {
	
					@Override
					public String title() {
						return "leave";
					}
	
					@Override
					public boolean go() {
						return true;
					}
				});
				return mList;
			}});
	}

	@Override
	public void passTime(double time) {
		timePassed-=time;
		if (timePassed < 0) {
			if (crimeLord == null){
				crimeLord = extra.randList(town.getOccupants());
				town.getOccupants().remove(crimeLord);
				timePassed = 24;
			}else {
				SuperPerson sp = extra.randList(town.getOccupants());
				if (((Agent)sp).getPerson().getLevel() > ((Agent)crimeLord).getPerson().getLevel() && extra.chanceIn(1,3)) {
					town.getOccupants().add(crimeLord);
					crimeLord = sp;
					town.getOccupants().remove(sp);
					timePassed = 24;
				}else {
					((Agent)crimeLord).getPerson().getBag().modGold(100*town.getTier());
					
				}
			}
			
		}

	}

}
