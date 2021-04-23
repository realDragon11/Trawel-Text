package trawel.fort;

import java.util.ArrayList;
import java.util.List;

import trawel.DrawBane;
import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuSelect;
import trawel.Player;
import trawel.extra;

public class FortFoundation extends FortFeature {

	private int size;
	private double timeLeft = -1;
	private int buildCode = -1;
	
	public FortFoundation(int size) {
		this.size = size;
		laborer = new Laborer(LaborType.BUILDER);
		switch (size) {
		case 1: name = "Small Foundation";break;
		case 2: name = "Medium Foundation";break;
		case 3: name = "Large Foundation";break;
		}
	}
	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getDefenceRating() {
		return 0;
	}

	@Override
	public int getOffenseRating() {
		return 0;
	}

	@Override
	public void go() {

		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
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
				if (buildCode == -1) {
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "add drawbane";
					}

					@Override
					public boolean go() {
						DrawBane db = Player.bag.discardDrawBanes(true);
						switch(db) {
						case CEON_STONE:
							if (size == 3) {
								buildCode = 1;
								name = "Wizard Tower in progress";
								timeLeft = 24.0*7;
								return true;
							}
							break;
						case LIVING_FLAME:
							if (size == 2) {
								buildCode = 2;
								name = "Forge in progress";
								timeLeft = 24.0*7;
								return true;
							}
							break;
						case TELESCOPE:
							switch (size) {
							case 1:
								buildCode = 3;
								name = "Small Watchtower in progress";
								timeLeft = 24.0*1;
								return true;
							case 2:
								buildCode = 4;
								name = "Watchtower in progress";
								timeLeft = 24.0*3;
								return true;
							case 3:
								buildCode = 5;
								name = "Large Watchtower in progress";
								timeLeft = 24.0*6;
								return true;
							}
							break;
						case MEAT:
							switch (size) {
							case 1:
								buildCode = 6;
								name = "Small Hunter's Den in progress";
								timeLeft = 24.0*1;
								return true;
							case 2:
								buildCode = 7;
								name = "Hunter's Den in progress";
								timeLeft = 24.0*3;
								return true;
							case 3:
								buildCode = 8;
								name = "Large Hunter's Den in progress";
								timeLeft = 24.0*6;
								return true;
							}
							break;
						}
						Player.bag.addNewDrawBane(db);
						extra.println("You can't build a building with this drawbane and foundation size.");
						return false;
					}
				});
				}
				
				return mList;
			}
			
		});
		
	}

	@Override
	public void passTime(double time) {
		if (timeLeft > 0) {
			timeLeft-=time;
			if (timeLeft <=0) {
				switch (buildCode) {
				case 1:
					town.enqueneAdd(new WizardTower(town.getTier()));
					break;
				case 2:
					town.enqueneAdd(new Forge(town.getTier()));
					break;
				case 3:
					town.enqueneAdd(new Watchtower(town.getTier(),1));
					break;
				case 4:
					town.enqueneAdd(new Watchtower(town.getTier(),2));
					break;
				case 5:
					town.enqueneAdd(new Watchtower(town.getTier(),3));
					break;
				case 6:
					town.enqueneAdd(new Hunter(town.getTier(),1));
					break;
				case 7:
					town.enqueneAdd(new Hunter(town.getTier(),2));
					break;
				case 8:
					town.enqueneAdd(new Hunter(town.getTier(),3));
					break;
				}
				town.enqueneRemove(this);
			}
		}
	}

}
