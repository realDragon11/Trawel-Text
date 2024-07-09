package trawel.towns.features.fort.features;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import derg.menus.MenuSelectNumber;
import trawel.core.Input;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.features.fort.FortFeature;
import trawel.towns.features.fort.elements.LaborType;
import trawel.towns.features.fort.elements.Laborer;
import trawel.towns.features.fort.elements.SubSkill;
import trawel.towns.features.fort.elements.SubSkill.Active;
import trawel.towns.features.fort.elements.SubSkill.Type;

public class WizardTower extends FortFeature {

	private static final long serialVersionUID = 1L;
	
	public SubSkill downTimeSkill;
	public SubSkill battleSkill;
	
	public WizardTower(int tier) {
		this.tier = tier;
		this.name = "Wizard Tower";
		tutorialText = "Wizard Tower";
		laborer = new Laborer(LaborType.WIZARD);
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_FORT;
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
		if (this.getOwner() != Player.player) {
			Print.println("You do not own this fort.");
			return;
		}
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_CURRENCY+"skills";
					}

					@Override
					public boolean go() {
						skills();
						return false;
					}
				});
				mList.add(new MenuBack("leave"));
				return mList;
			}
			
		});
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		// TODO Auto-generated method stub
		return null;
	}
	public void skills() {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(getPlayerBuyPower());
				if (downTimeSkill != null) {
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Improve "+downTimeSkill.name+" [" + findLSkill(downTimeSkill) + "]";
					}

					@Override
					public boolean go() {
						improveSkill(downTimeSkill,downTimeSkill.costMult);
						return false;
					}
				});}else {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Pick downtime skill";
						}

						@Override
						public boolean go() {
							pickDownTime();
							return false;
						}
					});
				}
				if (battleSkill != null) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Improve "+battleSkill.name+" [" + findLSkill(battleSkill) + "]";
						}

						@Override
						public boolean go() {
							improveSkill(battleSkill,battleSkill.costMult);
							return false;
						}
					});}else {
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return "Pick battle skill";
							}

							@Override
							public boolean go() {
								pickBattle();
								return false;
							}
						});
					}
				mList.add(new MenuBack());
				return mList;
			}
			
		});
	}
	
	public void pickDownTime(){
		Input.menuGo(new MenuGenerator() {
		@Override
		public List<MenuItem> gen() {
			List<MenuItem> mList = new ArrayList<MenuItem>();
			if (downTimeSkill != null) {
				return mList;
			}
			List<SubSkill> pickList = new ArrayList<SubSkill>();
			for (SubSkill s: SubSkill.values()) {
				if (s.act.equals(Active.DOWNTIME) && s.type.equals(Type.WIZARD)) {
					pickList.add(s);
				}
			}
			for (int i = 0;i < pickList.size();i++) {
				mList.add(new MenuSelectNumber() {

					@Override
					public String title() {
						return pickList.get(number).name;
					}

					@Override
					public boolean go() {
						Print.println(pickList.get(number).name + ": " + pickList.get(number).desc + " Buy?");
						if (Input.yesNo()){
							downTimeSkill=(pickList.get(number));
							pickList.clear();
							return true;
						}
						return false;
					}
				});
				((MenuSelectNumber)(mList.get(mList.size()-1))).number = i;
			}
			mList.add(new MenuBack());
			return mList;
		}
		
	});
	}
	public void pickBattle(){
		Input.menuGo(new MenuGenerator() {
		@Override
		public List<MenuItem> gen() {
			List<MenuItem> mList = new ArrayList<MenuItem>();
			if (battleSkill != null) {
				return mList;
			}
			List<SubSkill> pickList = new ArrayList<SubSkill>();
			for (SubSkill s: SubSkill.values()) {
				if (s.act.equals(Active.BATTLE) && s.type.equals(Type.WIZARD)) {
					pickList.add(s);
				}
			}
			for (int i = 0;i < pickList.size();i++) {
				mList.add(new MenuSelectNumber() {

					@Override
					public String title() {
						return pickList.get(number).name;
					}

					@Override
					public boolean go() {
						Print.println(pickList.get(number).name + ": " + pickList.get(number).desc + " Buy?");
						if (Input.yesNo()){
							battleSkill=(pickList.get(number));
							pickList.clear();
							return true;
						}
						return false;
					}
				});
				((MenuSelectNumber)(mList.get(mList.size()-1))).number = i;
			}
			mList.add(new MenuBack());
			return mList;
		}
		
	});
	}
	

}
