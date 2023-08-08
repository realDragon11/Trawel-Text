package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;

import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Skill;
import trawel.personal.item.Potion;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public class WitchHut extends Feature{
	
	private static final long serialVersionUID = 1L;
	
	public WitchHut(Town t) {
		name = "witch hut";
		tutorialText = "A place to brew potions.";
		town = t;
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE_MAGIC;
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|hut|Witch Hut|");
		Networking.setArea("store");
		while (true) {
		extra.println("1 brew a potion");
		extra.println("2 leave");
		switch (extra.inInt(2)) {
		case 1: brew();break;
		case 2: return;
		}
		}
	}

	private void brew() {
		ArrayList<DrawBane> dbs = new ArrayList<DrawBane>();
		extra.println(Player.player.getFlask() != null ? "You already have a potion, brewing one will replace it." : "Time to get brewing!");
		while (true) {
			extra.println("1 put in a drawbane");
			extra.println("2 finish");
			extra.println("3 back/discard");
			switch (extra.inInt(3)) {
			case 1: 
				DrawBane inter = Player.bag.discardDrawBanes(true);
				if (inter != null && !inter.equals(DrawBane.NOTHING)) {
					dbs.add(inter);
					Networking.sendStrong("PlayDelay|sound_potionmake"+extra.randRange(1,2)+"|1|");
				}break;
			case 2:
				if (dbs.size() == 0) {
					extra.println("There's nothing in the pot!");
					return;
				}
				Networking.sendStrong("PlayDelay|sound_potiondone|1|");
				Networking.unlockAchievement("brew1");
				extra.println("You finish brewing your potion, and put it in your flask... time to test it out!");
				int batWings = (int) dbs.stream().filter(d -> d.equals(DrawBane.BAT_WING)).count();
				int mGuts = (int) dbs.stream().filter(d -> d.equals(DrawBane.MIMIC_GUTS)).count();
				int apples = (int) dbs.stream().filter(d -> d.equals(DrawBane.APPLE)).count();
				int meats = (int) dbs.stream().filter(d -> d.equals(DrawBane.MEAT)).count();
				meats += (int) dbs.stream().filter(d -> d.equals(DrawBane.VIRGIN)).count()*2;
				int garlics = (int) dbs.stream().filter(d -> d.equals(DrawBane.GARLIC)).count();
				int woods = (int) dbs.stream().filter(d -> d.equals(DrawBane.WOOD)).count();
				int honeys = (int) dbs.stream().filter(d -> d.equals(DrawBane.HONEY)).count();
				int pumpkins = (int) dbs.stream().filter(d -> d.equals(DrawBane.PUMPKIN)).count();
				int ents = (int) dbs.stream().filter(d -> d.equals(DrawBane.ENT_CORE)).count();
				int waxs = (int) dbs.stream().filter(d -> d.equals(DrawBane.WAX)).count();
				int eggcorns = (int) dbs.stream().filter(d -> d.equals(DrawBane.EGGCORN)).count();
				int truffles = (int) dbs.stream().filter(d -> d.equals(DrawBane.TRUFFLE)).count();
				int eons = (int) dbs.stream().filter(d -> d.equals(DrawBane.CEON_STONE)).count();
				int silvers = (int) dbs.stream().filter(d -> d.equals(DrawBane.SILVER)).count();
				int bloods = (int) dbs.stream().filter(d -> d.equals(DrawBane.BLOOD)).count();
				int virgins = (int) dbs.stream().filter(d -> d.equals(DrawBane.VIRGIN)).count();
				int lflames =  (int) dbs.stream().filter(d -> d.equals(DrawBane.LIVING_FLAME)).count();
				int telescopes =  (int) dbs.stream().filter(d -> d.equals(DrawBane.TELESCOPE)).count();
				int food = meats + apples + garlics + honeys + pumpkins + pumpkins + eggcorns+truffles + (virgins*2);
				int filler = apples + woods + waxs;
				
				/*
				if (Player.player.eaBox.witchTrainLevel > 2) {
					filler+=Player.player.eaBox.witchTrainLevel/3;
				}*/
				if (Player.hasSkill(Skill.P_BREWER)) {
					filler+=2;
				}
				if (Player.hasSkill(Skill.TOXIC_BREWS)) {
					filler+=1;
				}
				if (ents > 0 && meats > 1) {
					mainGame.CombatTwo(Player.player.getPerson(), RaceFactory.getFleshGolem(town.getTier()));
					return;
				}
				if (bloods > 0 && virgins > 0) {
					Player.player.setFlask(new Potion(Effect.B_MARY,bloods+virgins+filler));
					return;
				}
				if (eons > 0 && silvers > 0) {
					for (int i = 0; i < silvers;i++) {
						Player.bag.addNewDrawBane(DrawBane.GOLD);
					}
					return;
				}
				if (eons > 0 && woods > 1) {
					Player.bag.addNewDrawBane(DrawBane.ENT_CORE);
					return;
				}
				if (extra.chanceIn(woods, 10)) {
					Player.player.setFlask(new Potion(Effect.CURSE,1+filler));
					return;
				}
				if (waxs > 0 && honeys > 0) {
					Player.player.setFlask(new Potion(Effect.BEE_SHROUD,honeys+filler));
					return;
				}
				if (mGuts > 0 && telescopes >0) {
					Player.player.setFlask(new Potion(Effect.TELESCOPIC,mGuts+telescopes+filler));
					return;
				}
				if (mGuts > 0 && batWings >0) {
					Player.player.setFlask(new Potion(Effect.R_AIM,batWings+mGuts+filler));
					return;
				}
				if (lflames > 0 && food >=1) {
					Player.player.setFlask(new Potion(Effect.FORGED,lflames+food+filler));
				}
				if (food >= 3) {
					Player.player.setFlask(new Potion(Effect.HEARTY,food+filler));
					return;
				}
				if (batWings > 0) {
					Player.player.setFlask(new Potion(Effect.HASTE,batWings+filler));
					return;
				}
				Player.player.setFlask(new Potion(Effect.CURSE,1+filler));
				return;
			case 3: return;
			}
		}
	}
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
