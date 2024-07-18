package trawel.towns.features.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.Effect;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.data.FeatureData;
import trawel.towns.data.FeatureData.FeatureTutorialCategory;
import trawel.towns.features.Feature;
import trawel.towns.features.elements.MenuMoney;

public class Doctor extends Feature {
	
	static {
		FeatureData.registerFeature(Doctor.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" cure "+Effect.BURNOUT.getName() + " and "+Effect.WOUNDED.getName()+".");
			}
			
			@Override
			public int priority() {
				return 10;
			}
			
			@Override
			public String name() {
				return "Doctor";
			}
			
			@Override
			public String color() {
				return TrawelColor.F_SERVICE;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.VITAL_SERVICES;
			}
		});
	}

	private static final long serialVersionUID = 1L;

	private double timecounter;
	public Doctor(String name,Town t) {
		timecounter = Rand.randRange(5,10);
		this.name = name;
		town = t;
		tier = t.getTier();
	}
	
	@Override
	public String nameOfType() {
		return "Doctor";
	}
	
	@Override
	public Area getArea() {
		return Area.MISC_SERVICE;
	}
	
	@Override
	public void go() {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuMoney());
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_FREE+"Get Diagnosis.";
					}

					@Override
					public boolean go() {
						Player.player.getPerson().displayEffects();
						return false;
					}});
				//costs for all punishments, even the ones they can't cure- it's harder work
				int cost = Math.round(getUnEffectiveLevel()*(1+Player.player.getPerson().punishmentSize()));
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_CURRENCY+"Cure ("+World.currentMoneyDisplay(cost)+")";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() < cost) {
							Print.println(TrawelColor.RESULT_ERROR+"Not enough "+World.currentMoneyString()+"!");
							return false;
						}
						Print.println(TrawelColor.SERVICE_CURRENCY+"Pay for a check up?");
						if (Input.yesNo()) {
							Player.addTime(.5);//treatment time
							TrawelTime.globalPassTime();
							Player.player.addGold(-cost);
							Print.println(TrawelColor.RESULT_PASS+"You pay and receive treatment.");
							Player.player.getPerson().cureEffects();
						}
						return false;
					}});
				list.add(new MenuBack("Leave"));
				return list;
			}});
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		timecounter-=time;
		if (timecounter <= 0) {
			int price = (int) (2*getUnEffectiveLevel());
			//must have been afflicted by at least one effect since last doctor visit/creation
			town.getPersonableOccupants().filter(a -> a.getPerson().effectsSize() > 0 && a.canBuyMoneyAmount(price)).limit(3)
			.forEach(a -> a.getPerson().clearEffects());//uses clear instead of cure because NPCs don't go to blacksmiths and it's better to reduce the size of effect maps on npcs
			timecounter += Rand.randRange(20,40);
		}
		return null;
	}

}
