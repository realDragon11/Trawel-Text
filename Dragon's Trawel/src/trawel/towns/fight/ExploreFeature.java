package trawel.towns.fight;

import java.util.ArrayList;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.AIClass;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Player;
import trawel.quests.QRMenuItem;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;
import trawel.towns.services.Oracle;

public abstract class ExploreFeature extends Feature {

	protected int explores = 0;
	protected int exhaust = 0;
	protected int exhaustTimer = 0;
	protected double regenRate = 24.0;
	protected boolean exhausted = false;
	
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		
		if (exhaust > 0) {
			exhaustTimer+=time;
			if (exhaustTimer > regenRate) {
				exhaustTimer-=regenRate;
				exhaust--;
				if (exhaust <= 0) {
					exhausted = false;
					exhaustTimer = 0;
				}
			}
		}
		return null;
	}

	@Override
	protected void go() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Explore!";
					}

					@Override
					public boolean go() {
						explore();
						return false;
					}
				});
				for (QuestR qr: qrList) {
					list.add(new QRMenuItem(qr));
				}
				List<MenuItem> mList = extraMenu();
				if (mList != null) {
					list.addAll(mList);
				}
				list.add(new MenuBack("Exit."));
				return list;
			}
			});
	}
	
	@Override
	public String getTitle() {
		return getName() + (exhausted ? " (Empty)" :"");
	}

	@Override
	public String getColor() {
		return extra.F_COMBAT;
	}
	
	public void explore(){
		if (exhausted) {
			if (mainGame.doTutorial) {
				extra.println("Exploration features have a limited amount of interesting things, which replenishes over time. You should pass in game time by doing other things before returning here.");
			}
			onNoGo();
			return;
		}
		explores++;
		exhaust++;
		if (exhaust > 5) {
			if (extra.randFloat() > exhaust/30f) {
				exahust();
				return;
			}
		}
		Player.addTime(.1 + (extra.randFloat()*.5));
		mainGame.globalPassTime();
		subExplore(roll());
	}
	
	public void exahust() {
		exhausted = true;
		onExhaust();
	}
	
	public abstract void onExhaust();
	public abstract void onNoGo();
	public abstract void subExplore(int id);
	protected abstract WeightedTable roller();
	protected int roll() {
		return roller().random(extra.getRand());
	}
	public List<MenuItem> extraMenu(){
		return null;
	}
	public abstract String nameOfType();
	@Override
	public String getTutorialText() {
		return extra.capFirst(nameOfType())+".";
	}
	
	protected void oldFighter(String restingOn, String warning) {
		extra.println("You find an old fighter resting on "+restingOn+".");
		Person old = RaceFactory.makeOld(getLevel()+4);
		old.getBag().graphicalDisplay(1, old);
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return old.getName();
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Chat.";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list = new ArrayList<MenuItem>();
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "Greetings, " + Player.bag.getRaceID().name + ". What would you like to talk about?";
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Advice?";
									}

									@Override
									public boolean go() {
										Oracle.tip("old");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "This "+nameOfType()+"?";
									}

									@Override
									public boolean go() {
										extra.println("\"We are at " + getName() + " in "+town.getName()+"."+warning+"\"");
										return false;
									}});
								list.add(new MenuBack());
								return list;
							}});
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_BATTLE+"Attack!";
					}

					@Override
					public boolean go() {
						if (old.reallyAttack()) {
							Player.player.fightWith(old);
							return true;
						}else {
							extra.println("You leave the fighter alone.");
						}
						return false;
					}});
				list.add(new MenuBack());
				return list;
			}});
		Networking.clearSide(1);
	}
	
	/**
	 * @param calling
	 * @param optionalWolfQualifier - set null to use nameOfType, empty string for no addition, should have a space in front
	 */
	protected void findEquip(String optionalWolfQualifier) {
		if (optionalWolfQualifier == null) {
			optionalWolfQualifier = " "+nameOfType();
		}
		extra.println("A"+optionalWolfQualifier+" wolf is poking over a dead corpse... and it looks like their equipment is intact!");
		Person loot = RaceFactory.makeLootBody(getLevel());
		loot.getBag().graphicalDisplay(1,loot);
		extra.println(extra.PRE_BATTLE+"Fight the wolf for the body?");
		if (!extra.yesNo()) {
			Networking.clearSide(1);
			return;
		}
		Combat c = Player.player.fightWith(RaceFactory.makeAlphaWolf(getLevel()));
		if (c.playerWon() < 0) {
			extra.println("The wolf drags the body away.");
			return;
		}
		AIClass.playerLoot(loot.getBag(),true);
	}
	
	protected void mugger_other_person() {
		extra.println(extra.PRE_BATTLE+"You see someone being robbed! Help?");
		Person robber =  RaceFactory.getMugger(getLevel());
		robber.getBag().graphicalDisplay(1, robber);
		if (extra.yesNo()) {
			Combat c = Player.player.fightWith(robber);
			if (c.playerWon() > 0) {
				int gold = Math.round(extra.randRange(.5f,2.5f)*getUnEffectiveLevel());
				extra.println("They give you a reward of " +World.currentMoneyDisplay(gold) + " in thanks for saving them.");
				Player.player.addGold(gold);
			}else {
				extra.println("They steal from your bags as well!");
				int lose = Math.round(extra.randRange(2f,3f)*getUnEffectiveLevel());
				extra.println(Player.loseGold(lose,true));
			}
		}else {
			extra.println("You walk away.");
			Networking.clearSide(1);
		}
	}
	
	protected void mugger_ambush() {
		extra.println(extra.PRE_BATTLE+"You see a mugger charge at you! Prepare for battle!");
		Combat c = Player.player.fightWith(RaceFactory.getMugger(getLevel()));
		if (c.playerWon() > 0) {
			
		}else {
			extra.println("They rummage through your bags!");
			int lose = Math.round(extra.randRange(2f,3f)*getUnEffectiveLevel());
			extra.println(Player.loseGold(lose,true));
		}
	}
	
	protected void risky_gold(String intro,String failText, String ignoreText) {
		extra.println(intro);
		Boolean result = extra.yesNo();
		extra.linebreak();
		if (result) {
			if (Math.random() > .4) {
				extra.println(extra.PRE_BATTLE+"A fighter runs up and calls you a thief before launching into battle!");
				Combat c = Player.player.fightWith(RaceFactory.getMugger(tier));
				if (c.playerWon() > 0) {
					if (c.playerWon() == 1) {
						extra.println("You wake up and examine the loot...");
					}
					int gold = Math.round(extra.randRange(2f,3f)*getUnEffectiveLevel());
					extra.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
					Player.player.addGold(gold);
				}else {
					extra.println(failText);
				}
			}else {
				int gold = Math.round(extra.randRange(0f,2f)*getUnEffectiveLevel());
				extra.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
				Player.player.addGold(gold);
			}
		}else {
			extra.println(ignoreText);
		}
	}

}