package trawel.towns.fight;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Effect;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.CultType;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.Perk;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;

public class Mountain extends ExploreFeature{

	private static final long serialVersionUID = 1L;
	private double cleanTime;
	private static WeightedTable roller;
	
	@Override
	public QRType getQRType() {
		return QRType.MOUNTAIN;
	}
	
	public Mountain(String name, int tier) {
		this.tier = tier;
		this.name = name;
		background_area = "mountain";
		background_variant = 1;
		area_type = Area.MOUNTAIN;
	}
	
	@Override
	public String getTutorialText() {
		return "Mountain";
	}
	
	@Override
	public String nameOfType() {
		return "mountain";
	}
	
	@Override
	public List<MenuItem> extraMenu(){
		return Collections.singletonList(new MenuSelect() {

					@Override
					public String title() {
						return extra.SERVICE_FREE+"Visit hot springs.";
					}

					@Override
					public boolean go() {
						Player.player.getPerson().washAll();
						Player.player.getPerson().bathEffects();
						extra.println("You wash the blood off of your armor.");
						Player.bag.graphicalDisplay(-1,Player.player.getPerson());
						return false;
					}
				});
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		super.passTime(time, calling);
		
		cleanTime -= time;
		if (cleanTime < 0) {
			cleanEntireTown();
			cleanTime = 12+(24*extra.randFloat());
		}
		return null;
	}
	
	private void cleanEntireTown() {
		town.getPersonableOccupants().forEach(a -> a.getPerson().washAll());
	}
	
	@Override
	public void onExhaust() {
		extra.println("You don't find anything. You think you may have exhausted this mountain, for now. Maybe come back later?");
	}
	
	@Override
	public void onNoGo() {
		extra.println("The mountain is barren.");
	}
	
	@Override
	public void subExplore(int id) {
		if (explores == 10) {
			Player.player.addAchieve(this, this.getName() + " Wanderer");
		}
		if (explores == 50) {
			Player.player.addAchieve(this, this.getName() + " Explorer");
		}
		if (explores == 100) {
			Player.player.addAchieve(this, this.getName() + " Guide");
		}
		switch (id) {
		case 0: 
			extra.println("You couldn't find anything interesting.");
			;break;
		case 1: rockSlide();break;
		case 2: ropeBridge();break;
		case 3: risky_gold(
				"You spot a bag of "+World.currentMoneyString()+" being carried by a mountain goat! Chase it?",
				"They take the "+World.currentMoneyString()+" sack and leave you rolling down the mountain...",
				"You let the goat run away..."
				);break;
		case 4: mugger_other_person();break;
		case 5: mugger_ambush();break;
		case 6: tollKeeper();break;
		case 7: wanderingDuelist();break;
		case 8: oldFighter("a rock"," Beware, danger lurks on these slopes.",this);break;
		case 9: aetherRock();break;
		case 10: findEquip(null);break;
		case 11: vampireHunter();break;
		case 12: skyCult();break;
		}
	}
	
	@Override
	protected WeightedTable roller() {
		if (roller == null) {
			roller = new WeightedTable(new float[] {
					//nothing
					.5f,
					//rock slide
					.7f,
					//rope bridge
					.3f,
					//gold goat
					1f,
					//interrupt mugging
					1f,
					//get mugged
					2f,
					//toll
					1f,
					//duelist
					1f,
					//old fighter
					.5f,
					//aether rock
					1f,
					//find equip with wolf fight
					1f,
					//vampire hunter
					1f,
					//sky cult
					.5f
			});
		}
		return roller;
	}
	
	private void rockSlide() {
		extra.println("Some rocks start falling down the mountain!");
		extra.println("1 Take it head on. (Str)");
		extra.println("2 Attempt to avoid the rocks. (Dex)");
		extra.println("9 Minimize the damage.");
		switch (extra.inInt(2,true,true)) {
		case 1:
			if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getStrength(),IEffectiveLevel.attributeChallengeMedium(getTempLevel())) >=0) {
				extra.println(extra.RESULT_PASS+"You survive the rockslide.");
				Player.addXp(Math.max(1,getTempLevel()/3));//can't use temp level due to difficulty thing
			}else {
				extra.println(extra.RESULT_FAIL+"The rocks crush you!");
				mainGame.die("You pull yourself out from under the rocks, your armor is all dinged up.");
				Player.player.getPerson().addEffect(Effect.DAMAGED);
				Player.player.getPerson().addEffect(Effect.BURNOUT);
			}
			;break;
		case 2:
			if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getDexterity(),IEffectiveLevel.attributeChallengeMedium(getTempLevel())) >=0) {
				extra.println(extra.RESULT_PASS+"You survive the rockslide.");
				Player.addXp(Math.max(1,getTempLevel()/3));
			}else {
				extra.println(extra.RESULT_FAIL+"The rocks crush you!");
				mainGame.die("You pull yourself out from under the rocks, your armor is all dinged up.");
				Player.player.getPerson().addEffect(Effect.DAMAGED);
				Player.player.getPerson().addEffect(Effect.BURNOUT);
			}
			;break;
		case 3:
			extra.println(extra.RESULT_FAIL+"They rocks crush you!");
			mainGame.die("You pull yourself out from under the rocks.");
		break;
		
		}
	}
	
	
	private void ropeBridge() {
		extra.println("You come across a rope bridge. Cross it?");
		if (extra.yesNo()) {
			extra.println("You cross the bridge.");
		}else {
			extra.println("You don't cross the bridge.");
		}
	}
	
	private void tollKeeper() {
		extra.println(extra.PRE_BATTLE+"You see a toll road keeper. Mug them for their "+World.currentMoneyString()+"?");
		Person toller = RaceFactory.getPeace(getTempLevel());
		int want = IEffectiveLevel.cleanRangeReward(getTempLevel(),3.5f,.8f);
		toller.getBag().addLocalGoldIf(IEffectiveLevel.cleanRangeReward(getTempLevel(),6f,.5f));
		toller.getBag().graphicalDisplay(1, toller);
		if (extra.yesNo()) {
		
		Combat c = Player.player.fightWith(toller);
		if (c.playerWon() > 0) {
			//have gold base now
		}else {
			int lost = Player.player.loseGold(want);
			if (lost == -1) {
				extra.println("They mutter something about freeloaders.");
			}else {
				if (lost < want) {
					extra.println("They make you pay the toll, but you don't have enough. (-"+World.currentMoneyDisplay(lost)+")");
				}else {
					extra.println("They make you pay the toll. (-"+World.currentMoneyDisplay(lost)+")");
				}
			}
		}
		}else {
			extra.println("You walk away.");
			Networking.clearSide(1);
		}
	}

	private void wanderingDuelist() {
		extra.println(extra.PRE_BATTLE+"A duelist approaches and challenges you to a duel.");
		Person dueler = RaceFactory.getDueler(getTempLevel());
		dueler.getBag().graphicalDisplay(1, dueler);
		if (dueler.reallyFight("Accept a duel with")) {
			Combat c = Player.player.fightWith(dueler);
			if (c.playerWon() > 0) {
				extra.println("You have won the duel!");
			}else {
				extra.println("They mutter a poem for your death.");
			}
		}else {
			extra.println("You walk away. They sigh.");
		}
	}

	
	private void aetherRock() {
		extra.println("You spot a solidified aether rock rolling down the mountain. Chase it?");
		Boolean result = extra.yesNo();
		if (result) {
			if (Math.random() > .5) {
				extra.println(extra.PRE_BATTLE+"A fighter runs up and calls you a thief before launching into battle!");
				Combat c = Player.player.fightWith(RaceFactory.getMugger(getTempLevel()));
				if (c.playerWon() > 0) {
					int aether = Math.round((IEffectiveLevel.unclean(getTempLevel())*extra.randRange(200f,400f)));
					extra.println("You pick up " + aether + " aether!");
					Player.bag.addAether(aether);
				}else {
					extra.println("They take the aether rock and leave you rolling down the mountain...");
				}
			}else {
				int aether = Math.round((IEffectiveLevel.unclean(getTempLevel())*extra.randRange(100f,200f)));
				extra.println("You pick up " + aether + " aether!");
				Player.bag.addAether(aether);
			}
		}else {
			extra.println("You let the rock roll away...");
		}
		if (Math.random() > .5) {
			rockSlide();
		}
	}

	private void vampireHunter() {
		extra.println(extra.PRE_BATTLE+"A vampire hunter is walking around. Mug them?");
		Person hunter = RaceFactory.makeHunter(getTempLevel());
		hunter.getBag().graphicalDisplay(1,hunter);
		if (hunter.reallyAttack()) {
			Combat c = Player.player.fightWith(hunter);
			if (c.playerWon() > 0) {
				
				int amber = IEffectiveLevel.cleanRangeReward(getTempLevel(),1.5f,.6f);
				Gem.AMBER.changeGem(amber);
				extra.println("One less thing for the vampires to worry about. You find "+amber+" amber.");
			}else {
				extra.println("They mutter something about vampire attacks.");
			}
		}else {
			extra.println("You walk away. They warn you to be safe from vampire attacks.");
			Networking.clearSide(1);
		}
	}
	
	private void skyCult() {
		final Person leader = RaceFactory.makeCultistLeader(getTempLevel(), CultType.SKY);
		final int tithe = IEffectiveLevel.cleanRangeReward(getTempLevel(),1500f,.5f);
		final boolean groupSmall = getTempLevel() < 5 || extra.randFloat() > .6f;
		final int addLevel = Math.max(1,getTempLevel()-3);
		final Predicate<Boolean> fight = new Predicate<Boolean>() {

			@Override
			public boolean test(Boolean isAmbushing) {
				if (groupSmall) {
					if (isAmbushing) {
						leader.addEffect(Effect.EXHAUSTED);
					}
					Combat c = Player.player.fightWith(leader);
					if (c.playerWon() > 0) {
						extra.println("The remaining cultist members flee before you get a chance to do anything.");
						return true;
					}else {
						int lost = Player.player.loseAether(tithe);
						extra.println(extra.RESULT_BAD+"They take " + lost + " aether as tithe from your body.");
						Mountain.super.town.addOccupant(leader.setOrMakeAgentGoal(AgentGoal.NONE));
						return false;
					}
				}else {
					List<Person> cultists = new ArrayList<Person>();
					cultists.add(leader);
					for (int i = 0; i < 3;i++) {
						cultists.add(RaceFactory.makeCultist(addLevel,CultType.SKY));
					}
					if (isAmbushing) {
						for (Person p: cultists) {
							p.addEffect(Effect.EXHAUSTED);
						}
					}
					Combat c = Player.player.massFightWith(cultists);
					if (c.playerWon() > 0) {
						extra.println("You defeat the cultist group.");
						return true;
					}else {
						int lost = Player.player.loseAether(tithe);
						extra.println(extra.RESULT_BAD+"They take " + lost + " aether as tithe from your body.");
						Mountain.super.town.addOccupant(leader.setOrMakeAgentGoal(AgentGoal.NONE));
						return false;
					}
				}
			}};
		extra.println("A few cloaked figures are circling around you. After noticing you spotted them, the leader approaches and demand a tithe of "+tithe+" aether for you to be left alone."
				+ (groupSmall ? "" : " The group behind them draws weapons."));
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				//re display leader if needed
				leader.getBag().graphicalDisplay(1,leader);
				
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "They want " + tithe +" of your " + Player.bag.getAether() + " aether.";
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_BATTLE+"Attack them suddenly!";
					}

					@Override
					public boolean go() {
						if (!leader.reallyFight("Really ambush")) {
							return false;//can back out of it
						}
						fight.test(true);
						return true;
						
					}});
				if (Player.bag.getAether() >= tithe) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Pay up.";
						}

						@Override
						public boolean go() {
							int lost = Player.player.loseAether(tithe);
							extra.println(extra.RESULT_PASS+"You pay the tithe of " + lost + " aether and they leave.");
							return true;
						}});
				}else {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_MAYBE_BATTLE+"See if they'll accept what you have. "+AttributeBox.getStatHintByIndex(2);
						}

						@Override
						public boolean go() {
							if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getClarity(),IEffectiveLevel.attributeChallengeEasy(getTempLevel())) >=0){
								int lower = Player.player.loseAether(tithe);
								extra.println(extra.RESULT_PASS+"They accept the reduced tithe of "+lower+" aether.");
							}else {
								extra.println(extra.PRE_BATTLE+"They refuse the reduced tithe and attack!");
								fight.test(false);
							}
							return true;
						}});
				}
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_MAYBE_BATTLE+"Attempt to flee."+AttributeBox.getStatHintByIndex(1);
					}

					@Override
					public boolean go() {
						if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getDexterity(),leader.getDexterity()) >=0){
							extra.println(extra.RESULT_PASS+"You escape!");
							return true;
						}else {
							extra.println(extra.RESULT_FAIL+"They catch up and attack you while you're panting for breath!");
							Player.player.getPerson().addEffect(Effect.EXHAUSTED);
							//doesn't apply burnout since the consequence is the fight with exhausted
							fight.test(false);
							return true;
						}
					}}
				);
				if (!groupSmall) {//can only be chosen by larger cults
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_BATTLE+"Challenge the leader's divinity!"+AttributeBox.getStatHintByIndex(2);
						}

						@Override
						public boolean go() {
							if (!leader.reallyFight("Really challenge")) {
								return false;//can back out of it
							}
							if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getClarity(),IEffectiveLevel.attributeChallengeEasy(getTempLevel())) >=0){
								//challenge successful
								extra.println(extra.RESULT_PASS+"The engage in the ritual battle to determine who is truly chosen!");
								Combat c = Player.player.fightWith(leader);
								if (c.playerWon() > 0) {
									extra.println(extra.RESULT_GOOD+"The remaining cultist members declare you the new chosen one!");
									Player.unlockPerk(Perk.CULT_CHOSEN_SKY);
									return true;
								}else {
									extra.println(extra.RESULT_BAD+"The sky curses you for your hubris!");
									Player.player.getPerson().addEffect(Effect.CURSE);
									Mountain.super.town.addOccupant(leader.setOrMakeAgentGoal(AgentGoal.NONE));
									return true;
								}
							}else {
								//they reject it
								extra.println(extra.RESULT_FAIL+"They reject your challenge and order their cult to attack!");
								fight.test(false);
								return true;
							}
						}});
				}
				
				return list;
			}});
		Networking.clearSide(1);
	}
}
