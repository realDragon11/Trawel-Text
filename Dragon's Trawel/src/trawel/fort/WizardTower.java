package trawel.fort;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuSelect;
import trawel.MenuSelectNumber;
import trawel.Player;
import trawel.extra;
import trawel.fort.SubSkill.Active;
import trawel.fort.SubSkill.Type;

public class WizardTower extends FortFeature {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int tier;
	
	public SubSkill downTimeSkill;
	public SubSkill battleSkill;
	
	public List<SubSkill> pickList = new ArrayList<SubSkill>();
	
	public WizardTower(int tier) {
		this.tier = tier;
		this.name = "Wizard Tower";
		tutorialText = "";
		color = Color.PINK;
		laborer = new Laborer(LaborType.WIZARD);
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
		if (this.getOwner() != Player.player.player) {
			extra.println("You do not own this fort.");
			return;
		}
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
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "skills";
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
				return mList;
			}
			
		});
	}
	
	public void pickDownTime(){
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
			if (downTimeSkill != null) {
				return mList;
			}
			pickList.clear();
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
						extra.println(pickList.get(number).name + ": " + pickList.get(number).desc + " Buy?");
						if (extra.yesNo()){
							downTimeSkill=(pickList.get(number));
							pickList.clear();
							return true;
						}
						return false;
					}
				});
				((MenuSelectNumber)(mList.get(mList.size()-1))).number = i;
			}
			
			return mList;
		}
		
	});
	}
	public void pickBattle(){
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
			if (battleSkill != null) {
				return mList;
			}
			pickList.clear();
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
						extra.println(pickList.get(number).name + ": " + pickList.get(number).desc + " Buy?");
						if (extra.yesNo()){
							battleSkill=(pickList.get(number));
							pickList.clear();
							return true;
						}
						return false;
					}
				});
				((MenuSelectNumber)(mList.get(mList.size()-1))).number = i;
			}
			
			return mList;
		}
		
	});
	}
	

}
