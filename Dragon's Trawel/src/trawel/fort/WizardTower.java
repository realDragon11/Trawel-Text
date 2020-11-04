package trawel.fort;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuSelect;
import trawel.QRMenuItem;
import trawel.QuestR;
import trawel.extra;

public class WizardTower extends FortFeature {

	
	public int tier;
	
	public WizardTower(int tier) {
		this.tier = tier;
		this.name = "Wizard Tower";
		tutorialText = "";
		color = Color.PINK;
	}
	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public int getDefenceRating() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOffenseRating() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void go() {
		// TODO Auto-generated method stub
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
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "improve skills";
					}

					@Override
					public boolean go() {
						skills();
						return false;
					}
				});
				return mList;
			}
			
		});
	}

	@Override
	public void passTime(double time) {
		// TODO Auto-generated method stub

	}
	public void skills() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "exit";
					}

					@Override
					public boolean go() {
						return true;
					}
				});
				for (QuestR qr: qrList) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "improve scrying";
					}

					@Override
					public boolean go() {
						improveSkill(SubSkill.SCRYING,1.25f);
						return false;
					}
				});
				return mList;
			}
			
		});
	}

}
