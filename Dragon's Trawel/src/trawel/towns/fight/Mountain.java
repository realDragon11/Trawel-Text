package trawel.towns.fight;
import java.util.Collections;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.AIClass;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.World;
import trawel.towns.services.Oracle;

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
		return "Mountain.";
	}
	
	@Override
	public List<MenuItem> extraMenu(){
		return Collections.singletonList(new MenuSelect() {

					@Override
					public String title() {
						return "visit hot springs";
					}

					@Override
					public boolean go() {
						Player.player.getPerson().washAll();
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
			Player.player.addAchieve(this, this.getName() + " wanderer");
		}
		if (explores == 50) {
			Player.player.addAchieve(this, this.getName() + " explorer");
		}
		if (explores == 100) {
			Player.player.addAchieve(this, this.getName() + " guide");
		}
		switch (id) {
		case 0: 
			extra.println("You couldn't find anything interesting.");
			;break;
		case 1: rockSlide();break;
		case 2: ropeBridge();break;
		case 3: goldGoat() ;break;
		case 4: mugger1();break;
		case 5: mugger2();break;
		case 6: tollKeeper();break;
		case 7: wanderingDuelist();break;
		case 8: oldFighter();break;
		case 9: aetherRock();break;
		case 10: findEquip();break;
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
					1f,
					//rope bridge
					1f,
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
		extra.println("1 duck and cover");
		extra.println("2 dodge");
		extra.println("9 do nothing");
		switch (extra.inInt(2,true,true)) {
		case 1:
			//MAYBELATER: find expected blunt resist
			if (Player.bag.getBluntResist()*extra.randFloat() > 7 * getUnEffectiveLevel()) {
				extra.println("You survive the rockslide.");
				Player.addXp(Math.max(1,tier/3));
			}else {
				mainGame.die("You pull yourself out from under the rocks.");
			}
			;break;
		case 2: 
			if (Player.bag.getDodge()*extra.randFloat() > .5f) {
				extra.println("You survive the rockslide.");
				Player.addXp(1);
			}else {
				mainGame.die("You pull yourself out from under the rocks.");
			}
			;break;
		case 3:
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
	
	private void goldGoat() {
		extra.println("You spot a bag of "+World.currentMoneyString()+" being carried by a mountain goat! Chase it?");
		Boolean result = extra.yesNo();
		extra.linebreak();
		if (result) {
			if (Math.random() > .5) {
				extra.println(extra.PRE_BATTLE+"A fighter runs up and calls you a thief before launching into battle!");
				Combat c = Player.player.fightWith(RaceFactory.getMugger(tier));
				if (c.playerWon() > 0) {
					if (c.playerWon() == 1) {
						extra.println("You wake up and examine the loot...");
					}
					int gold = Math.round(extra.randRange(2,3)*getUnEffectiveLevel());
					extra.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
					Player.player.addGold(gold);
				}else {
					extra.println("They take the "+World.currentMoneyString()+" sack and leave you rolling down the mountain...");
				}
			}else {
				int gold = Math.round(extra.randRange(0,2)*getUnEffectiveLevel());
				extra.println("You pick up " + World.currentMoneyDisplay(gold) + "!");
				Player.player.addGold(gold);
			}
		}else {
			extra.println("You let the goat run away...");
		}
	}
	
	private void mugger2() {
		extra.println(extra.PRE_BATTLE+"You see a mugger charge at you! Prepare for battle!");
		Combat c = Player.player.fightWith(RaceFactory.getMugger(tier));
		if (c.playerWon() > 0) {
			
		}else {
			extra.println("They fumble through your bags!");
			extra.println(Player.loseGold(50*tier,true));
		}
	}

	private void mugger1() {
		extra.println(extra.PRE_BATTLE+"You see someone being robbed! Help?");
		Person robber =  RaceFactory.getMugger(tier);
		robber.getBag().graphicalDisplay(1, robber);
		if (extra.yesNo()) {
			Combat c = Player.player.fightWith(robber);
			if (c.playerWon() > 0) {
				int gold = extra.randRange(tier,10*tier);
				extra.println("They give you a reward of " +World.currentMoneyDisplay(gold) + " in thanks for saving them.");
				Player.player.addGold(gold);
			}else {
				extra.println("They steal from your bags as well!");
				extra.println(Player.loseGold(50*tier,true));
			}
		}else {
			extra.println("You walk away.");
			Networking.clearSide(1);
		}
	}

	
	private void tollKeeper() {
		extra.println(extra.PRE_BATTLE+"You see a toll road keeper. Mug them for their "+World.currentMoneyString()+"?");
		Person toller = RaceFactory.getPeace(tier);
		int want = Math.round((1f+(extra.randFloat()*2f)*getUnEffectiveLevel()) + extra.randRange(1,3));
		toller.getBag().addLocalGoldIf(extra.randRange(0,3)+(want*extra.randRange(2,4)));
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
		extra.println(extra.PRE_BATTLE+"A duelist approaches and challenges you to a duel. Accept?");
		Person dueler = RaceFactory.getDueler(tier+1);
		dueler.getBag().graphicalDisplay(1, dueler);
		if (extra.yesNo()) {
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

	private void oldFighter() {
		Person old = RaceFactory.makeOld(tier+2);
		old.getBag().graphicalDisplay(1, old);
		while (true) {
			extra.println("You come across an old fighter, resting on a rock.");
			extra.println("1 Leave");//DOLATER: fix menu
			extra.println("2 "+extra.PRE_BATTLE+"Attack them.");
			extra.println("3 Chat with them");
			switch (extra.inInt(3)) {
			default: case 1: extra.println("You leave the fighter alone");return;
			case 2: 
				extra.println(extra.PRE_BATTLE+"Really attack them?");
				if (!extra.yesNo()) {
					break;
				}
				extra.println("You attack the fighter!");
				Combat c = Player.player.fightWith(old);
				if (c.playerWon() > 0) {

				}
				return;
			case 3: extra.println("The old fighter turns and answers your greeting.");
			while (true) {
				extra.println("What would you like to ask about?");
				extra.println("1 tell them goodbye");
				extra.println("2 ask for a tip");
				extra.println("3 this mountain");
				int in = extra.inInt(3);
				switch (in) {
				case 1: extra.println("They wish you well.") ;break;
				case 2: Oracle.tip("old");;break;
				case 3: extra.println("\"We are on " + this.getName() + ". Beware, danger lurks on these slopes.\"");break;
				}
				if (in == 1) {
					break;
				}
			}
			}
		}
	}
	
	private void aetherRock() {
		extra.println("You spot a solidified aether rock rolling down the mountain. Chase it?");
		Boolean result = extra.yesNo();
		extra.linebreak();
		if (result) {
			if (Math.random() > .5) {
				extra.println(extra.PRE_BATTLE+"A fighter runs up and calls you a thief before launching into battle!");
				Combat c = Player.player.fightWith(RaceFactory.getMugger(tier));
				if (c.playerWon() > 0) {
					int aether = Math.round(100+(getUnEffectiveLevel()*extra.randRange(150f,300f)));
					extra.println("You pick up " + aether + " aether!");
					Player.bag.addAether(aether);
				}else {
					extra.println("They take the aether rock and leave you rolling down the mountain...");
				}
			}else {
				int aether = 100+extra.randRange(100*tier,200*tier);
				extra.println("You pick up " + aether + " aether!");
				Player.bag.addAether(aether);
			}
		}else {
			extra.println("You let the rock roll away...");
		}
		if (Math.random() > .5) {
			this.rockSlide();
		}
	}
	
	private void findEquip() {
		extra.println("A mountain wolf is poking over a dead corpse... and it looks like their equipment is intact!");
		Person loot = RaceFactory.makeLootBody(tier);
		loot.getBag().graphicalDisplay(1,loot);
		extra.println(extra.PRE_BATTLE+"Fight the wolf for the body?");
		if (!extra.yesNo()) {
			Networking.clearSide(1);
			return;
		}
		Combat c = Player.player.fightWith(RaceFactory.makeAlphaWolf(tier));
		if (c.playerWon() < 0) {
			extra.println("The wolf drags the body away.");
			return;
		}
		AIClass.playerLoot(loot.getBag(),true);
	}

	private void vampireHunter() {
		extra.println(extra.PRE_BATTLE+"A vampire hunter is walking around. Mug them?");
		Person hunter = RaceFactory.makeHunter(tier);
		hunter.getBag().graphicalDisplay(1,hunter);
		if (extra.yesNo()) {
			Combat c = Player.player.fightWith(hunter);
			if (c.playerWon() > 0) {
				extra.println("One less thing for the vampires to worry about.");
			}else {
				extra.println("They mutter something about vampire attacks.");
			}
		}else {
			extra.println("You walk away. They warn you to be safe from vampire attacks.");
			Networking.clearSide(1);
		}
	}
}
