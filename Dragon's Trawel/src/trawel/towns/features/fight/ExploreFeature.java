package trawel.towns.features.fight;

import java.util.ArrayList;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.battle.Combat;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.core.mainGame;
import trawel.helper.constants.TrawelColor;
import trawel.personal.AIClass;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.people.Player;
import trawel.personal.people.Agent.AgentGoal;
import trawel.quests.locations.QRMenuItem;
import trawel.quests.locations.QuestBoardLocation;
import trawel.quests.locations.QuestR;
import trawel.quests.types.Quest.TriggerType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.World;
import trawel.towns.features.Feature;
import trawel.towns.features.services.Oracle;

public abstract class ExploreFeature extends Feature{

	protected int explores = 0;
	protected int exhaust = 0;
	protected int exhaustTimer = 0;
	protected double regenRate = 24.0;
	protected boolean exhausted = false;
	
	protected transient int tempLevel = -1;
	
	
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
		Input.menuGo(new MenuGenerator() {

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
				for (QuestR qr: Player.player.QRFor(ExploreFeature.this)) {
					list.add(new QRMenuItem(qr));
				}
				List<MenuItem> mList = extraMenu();
				if (mList != null) {
					list.addAll(mList);
				}
				list.add(new MenuBack("Leave."));
				return list;
			}
			});
	}
	
	@Override
	public String getTitle() {
		return getName() + (exhausted ? " (Empty)" :"");
	}
	
	public void explore(){
		if (exhausted) {
			if (mainGame.doTutorial) {
				Print.println("Exploration features have a limited amount of interesting things, which replenishes over time. You should pass in game time by doing other things before returning here.");
			}
			onNoGo();
			return;
		}
		explores++;
		exhaust++;
		if (exhaust > 5) {
			if (Rand.randFloat() > exhaust/30f) {
				exahust();
				return;
			}
		}
		Player.addTime(.1 + (Rand.randFloat()*.5));
		TrawelTime.globalPassTime();
		rollTempLevel();
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
		return roller().random(Rand.getRand());
	}
	public List<MenuItem> extraMenu(){
		return null;
	}
	
	/**
	 * will provide a variable level that resets each explore
	 */
	protected int getTempLevel() {
		return tempLevel;
	}
	
	/**
	 * call each explore to roll an encounter level
	 */
	protected void rollTempLevel() {
		tempLevel = Rand.randRange(Math.max(1,tier-1),tier+1);
	}
	
	/**
	 * 
	 * @param restingOn
	 * @param warning
	 * @param location
	 * @return 0 if no fight happened or the player victory number if it did
	 */
	public static int oldFighter(String restingOn, String warning, Feature location) {
		int[] ret = new int[] {0};
		Print.println("You find an old fighter resting on "+restingOn+".");
		Person old = RaceFactory.makeOld(location.getLevel()+4);//doesn't use temp level
		old.getBag().graphicalDisplay(1, old);
		Input.menuGo(new MenuGenerator() {

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
						Input.menuGo(new MenuGenerator() {

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
										return "This "+location.nameOfType()+"?";
									}

									@Override
									public boolean go() {
										Print.println("\"We are at " + location.getName() + " in "+location.getTown().getName()+"."+warning+"\"");
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
						return TrawelColor.PRE_BATTLE+"Attack!";
					}

					@Override
					public boolean go() {
						if (old.reallyAttack()) {
							Combat c = Player.player.fightWith(old);
							ret[0] = c.playerWon();
							return true;
						}else {
							Print.println("You leave the fighter alone.");
						}
						return false;
					}});
				list.add(new MenuBack());
				return list;
			}});
		Networking.clearSide(1);
		return ret[0];
	}
	
	/**
	 * @param calling
	 * @param optionalWolfQualifier - set null to use nameOfType, empty string for no addition, should have a space in front
	 */
	protected void findEquip(String optionalWolfQualifier) {
		if (optionalWolfQualifier == null) {
			optionalWolfQualifier = " "+nameOfType();
		}
		Print.println("A"+optionalWolfQualifier+" wolf is poking over a dead corpse... and it looks like their equipment is intact!");
		Person loot = RaceFactory.makeLootBody(getTempLevel());
		loot.getBag().graphicalDisplay(1,loot);
		Person wolf = RaceFactory.makeAlphaWolf(getTempLevel());
		//extra.println(extra.PRE_BATTLE+"Fight the wolf for the body?");
		if (!wolf.reallyAttack()) {
			Networking.clearSide(1);
			return;
		}
		Combat c = Player.player.fightWith(wolf);
		if (c.playerWon() < 0) {
			Print.println("The wolf drags the body away.");
			return;
		}
		AIClass.playerLoot(loot.getBag(),true);
	}
	
	protected void mugger_other_person() {
		Print.println(TrawelColor.PRE_BATTLE+"You see someone being robbed! Help?");
		Person robber =  RaceFactory.makeMugger(getTempLevel());
		robber.getBag().graphicalDisplay(1, robber);
		if (Input.yesNo()) {
			Combat c = Player.player.fightWith(robber);
			if (c.playerWon() > 0) {
				int gold = IEffectiveLevel.cleanRangeReward(getTempLevel(),2.5f,.5f);
				Print.println("They give you a reward of " +World.currentMoneyDisplay(gold) + " in thanks for saving them.");
				Player.player.addGold(gold);
			}else {
				Player.player.stealCurrencyLeveled(robber,0.5f);
				Player.placeAsOccupant(robber);
			}
		}else {
			Print.println("You walk away.");
			Networking.clearSide(1);
		}
	}
	
	protected void mugger_ambush() {
		Print.println(TrawelColor.PRE_BATTLE+"You see a mugger charge at you! Prepare for battle!");
		Person p = RaceFactory.makeMugger(getTempLevel());
		Combat c = Player.player.fightWith(p);
		if (c.playerWon() > 0) {
		}else {
			Player.player.stealCurrencyLeveled(p,0.5f);
			Player.placeAsOccupant(p);
		}
	}
	
	protected void risky_gold(String intro,String failText, String ignoreText) {
		Print.println(intro);
		Boolean result = Input.yesNo();
		if (result) {
			if (Math.random() > .4) {
				Print.println(TrawelColor.PRE_BATTLE+"A fighter runs up and calls you a thief before launching into battle!");
				Combat c = Player.player.fightWith(RaceFactory.makeMugger(getTempLevel()));
				if (c.playerWon() > 0) {
					if (c.playerWon() == 1) {
						Print.println("You wake up and examine the loot...");
					}
					int gold = IEffectiveLevel.cleanRangeReward(getTempLevel(),3.5f,.6f);
					Print.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
					Player.player.addGold(gold);
				}else {
					Print.println(failText);
				}
			}else {
				int gold = IEffectiveLevel.cleanRangeReward(getTempLevel(),1.5f,.2f);
				Print.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
				Player.player.addGold(gold);
			}
		}else {
			Print.println(ignoreText);
		}
	}

}
