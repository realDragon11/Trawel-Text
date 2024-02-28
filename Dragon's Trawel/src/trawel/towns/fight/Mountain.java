package trawel.towns.fight;
import java.util.Collections;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Effect;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
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
						return "Visit hot springs.";
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
					1f
					
			});
		}
		return roller;
	}
	
	private void rockSlide() {
		extra.println("Some rocks start falling down the mountain!");
		extra.println("1 Take it head on. [Str]");
		extra.println("2 Attempt to avoid the rocks. [Dex]");
		extra.println("9 Minimize the damage.");
		switch (extra.inInt(2,true,true)) {
		case 1:
			if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getStrength(),7*getEffectiveLevel()) >=0) {
				extra.println("You survive the rockslide.");
				Player.addXp(Math.max(1,tier/3));
			}else {
				extra.println("They rocks crush you!");
				mainGame.die("You pull yourself out from under the rocks, your armor is all dinged up.");
				Player.player.getPerson().addEffect(Effect.DAMAGED);
			}
			;break;
		case 2: 
			if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getStrength(),7*getEffectiveLevel()) >=0) {
				extra.println("You survive the rockslide.");
				Player.addXp(Math.max(1,tier/3));
			}else {
				extra.println("They rocks crush you!");
				mainGame.die("You pull yourself out from under the rocks, your armor is all dinged up.");
				Player.player.getPerson().addEffect(Effect.DAMAGED);
			}
			;break;
		case 3:
			extra.println("They rocks crush you!");
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
		Person toller = RaceFactory.getPeace(tier);
		int want = IEffectiveLevel.cleanRangeReward(tier,3.5f,.8f);
		toller.getBag().addLocalGoldIf(IEffectiveLevel.cleanRangeReward(tier,6f,.5f));
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
		Person dueler = RaceFactory.getDueler(tier+1);
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
				Combat c = Player.player.fightWith(RaceFactory.getMugger(tier));
				if (c.playerWon() > 0) {
					int aether = Math.round((getUnEffectiveLevel()*extra.randRange(200f,400f)));
					extra.println("You pick up " + aether + " aether!");
					Player.bag.addAether(aether);
				}else {
					extra.println("They take the aether rock and leave you rolling down the mountain...");
				}
			}else {
				int aether = Math.round((getUnEffectiveLevel()*extra.randRange(100f,200f)));
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
		Person hunter = RaceFactory.makeHunter(tier);
		hunter.getBag().graphicalDisplay(1,hunter);
		if (hunter.reallyAttack()) {
			Combat c = Player.player.fightWith(hunter);
			if (c.playerWon() > 0) {
				
				int amber = IEffectiveLevel.cleanRangeReward(tier,1.5f,.6f);
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
}
