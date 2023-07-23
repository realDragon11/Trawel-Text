package trawel.towns.fort;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import derg.menus.MenuSelectNumber;
import trawel.extra;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.fort.SubSkill.Active;
import trawel.towns.fort.SubSkill.Type;

public class Forge extends FortFeature {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int tier;
	
	public SubSkill downTimeSkill;
	public SubSkill battleSkill;
	
	public List<SubSkill> pickList = new ArrayList<SubSkill>();
	
	public Forge(int tier) {
		this.tier = tier;
		this.name = "Blacksmith";
		tutorialText = "";
		color = Color.PINK;
		laborer = new Laborer(LaborType.BLACKSMITH);
	}
	@Override
	public int getSize() {
		return 2;
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
		if (this.getOwner() != Player.player) {
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
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		// TODO Auto-generated method stub
		return null;
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
				/*
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
					}*/
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
				if (s.act.equals(Active.DOWNTIME) && s.type.equals(Type.BLACKSMITH)) {
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
			if (downTimeSkill != null) {
				return mList;
			}
			pickList.clear();
			for (SubSkill s: SubSkill.values()) {
				if (s.act.equals(Active.BATTLE) && s.type.equals(Type.BLACKSMITH)) {
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
