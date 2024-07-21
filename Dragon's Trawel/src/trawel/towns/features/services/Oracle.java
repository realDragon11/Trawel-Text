package trawel.towns.features.services;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.mainGame;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.World;
import trawel.towns.data.FeatureData;
import trawel.towns.data.FeatureData.FeatureTutorialCategory;
import trawel.towns.features.Feature;

public class Oracle extends Feature{ //extends feature later
	
	static {
		FeatureData.registerFeature(Oracle.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" utter random tips and tidbits from everywhere and everyone. If you pay them, the insight might be good enough to lift "+Effect.CURSE.getName()+" and overcome "+Effect.BURNOUT.getName()+".");
			}
			
			@Override
			public int priority() {
				return 30;
			}
			
			@Override
			public String name() {
				return "Oracle";
			}
			
			@Override
			public String color() {
				return TrawelColor.F_SPECIAL;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.VITAL_SERVICES;
			}
		});
	}

	private static final long serialVersionUID = 1L;
	private static Map<String,List<String>> tips = new HashMap<String,List<String>>();
	private int visits = 0;
	
	private static List<String> emptyList = Collections.singletonList("tips not loaded!");
	//MAYBELATER: either need to distribute tips with game, or unpack them, or find a workaround to my jar loading issues with path redirections
	//witness hell: https://stackoverflow.com/a/6247181
	public static String[] tipLocs = new String[] {
			"utter","cult","equality","gravedigger","old","racistPraise","racistShun","shaman"
			,"lootWhaler","lootLetterCloth","lootLetterBlood","lootLetterRepel","racistAttack"};
	public static List<String> tipLocsList = Arrays.asList(tipLocs);
	
	public Oracle(String string, int level) {
		name = string;
		tier = level;
	}

	public Oracle() {
		//just for non-static single commands
	}
	
	@Override
	public String nameOfType() {
		return "Oracle";
	}
	
	@Override
	public Area getArea() {
		return Area.ORACLE;
	}

	public static void tip(String mask) {
		Print.println("\""+ Rand.randList(tips.getOrDefault(mask, emptyList)) + "\"");
	}
	
	public static String tipString(String mask) {
		return Rand.randList(tips.get(mask));
	}
	
	public static String tipStringExt(String mask,String aAn, String self, String selves, String selvian, String town, List<String> otherList) {
		String tip = Rand.randList(tips.get(mask))
				.replaceAll(Pattern.quote("<a>"), aAn)
				.replaceAll(Pattern.quote("<self>"), self)
				.replaceAll(Pattern.quote("<selves>"),selves)
				.replaceAll(Pattern.quote("<selvian>"),selvian)
				.replaceAll(Pattern.quote("<town>"), town)
				;
		while (tip.contains("<other>")) {
			tip = tip.replaceFirst(Pattern.quote("<other>"),Rand.randList(otherList));
		}
		return tip;
	}
	
	public static String tipRandomOracle(String town) {
		return tipStringExt("","an","Oracle","Oracles","Oracle",town,Collections.singletonList("not-Oracle"));
	}
	
	public static String rescLocation() {
		if (mainGame.inEclipse) {
			return "/resource/";
		}
		return "/resc/resource/";
	}
	
	public static Path rescPath() {
		if (mainGame.inEclipse) {
			return Paths.get("resc/resource");
			/*try {
				return Paths.get(Oracle.class.getResource(rescLocation()).toURI());
			} catch (URISyntaxException e) {
				throw new RuntimeException("invalid ide resc path");
			}*/
		}
		//have to put these outside so I can access them sanely, jarinjar loader moment???
		return Paths.get("resource/");
	}
	
	public static void unloadResc() {
		//TODO: should do this automatically
	}
	
	/*
	public void loadTipAt(String mask, String loc) {
		try (Scanner fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+loc+".txt"))){
			List<String> list = new ArrayList<String>();
			while (fileInput.hasNextLine()) {
				list.add(fileInput.nextLine());
			}
			tips.put(mask, list);
		}
	}*/
	public void loadTipInJar(String mask) {
		try (Scanner fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+mask+".tips"))){
			List<String> list = new ArrayList<String>();
			List<String> all = tips.get("");
			while (fileInput.hasNextLine()) {
				String line = fileInput.nextLine();
				list.add(line);
				all.add(line);
			}
			tips.put(mask, list);
		}
	}
	
	public void loadTipAt(File loc) {
		try (Scanner fileInput = new Scanner (loc)){
			List<String> list = new ArrayList<String>();
			List<String> all = tips.get("");
			//would be much easier to remove things from the utter list now
			while (fileInput.hasNextLine()) {
				String line = fileInput.nextLine();
				list.add(line);
				all.add(line);
			}
			String mask = loc.getName().replace(".tips","");
			assert tipLocsList.contains(mask);//so I don't forget when making a new mask in ide
			tips.put(mask, list);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		tips.clear();
		tips.put("",new ArrayList<String>());
		if (mainGame.inEclipse) {
			Path path = rescPath();
			assert !Files.notExists(path);//can be false if unsure?
			assert Files.exists(path);
			File f = path.toFile();
			assert f.exists();
			assert f.isDirectory();
			File[] files = f.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File arg0, String arg1) {
					if (arg1.contains(".tips")) {
						return true;
					}
					return false;
				}});
			for (File file: files) {
				loadTipAt(file);
			}
		}else {
			//FIXME: probably should make a manifest or extractor or anything
			//note that Hell: witness hell: https://stackoverflow.com/a/6247181
			for (String str: tipLocsList) {
				loadTipInJar(str);
			}
		}
	}
	
	public void utterance0() {
		Print.println("\""+tipRandomOracle(town.getName())+"\"");
		visits++;
		Networking.unlockAchievement("oracle1");
		//has different titles if you just listen in
		if (visits == 2) {
			Player.player.addAchieve(this, this.getName() + " groupie");
		}
		if (visits == 5) {
			Player.player.addAchieve(this, this.getName() + " seeker");
		}
		if (visits == 10) {
			Player.player.addAchieve(this, this.getName() + " believer");
		}
		if (visits == 50) {
			Player.player.addAchieve(this, this.getName() + " adherent");
		}
		if (visits == 100) {
			Player.player.addAchieve(this, this.getName() + " acolyte");
		}
	}

	public void utterance() {
		if (Player.bag.getAether() >= cheapUtterPrice()) {
			Print.println("Pay "+ cheapUtterPrice() +" aether for an utterance?");
			if (Input.yesNo()) {
				Player.bag.addAether(-cheapUtterPrice());
				Print.println("\""+tipRandomOracle(town.getName())+"\"");
				visits++;
				Networking.unlockAchievement("oracle1");
				if (visits == 5) {
					Player.player.addAchieve(this, this.getName() + " vistor");
				}
				if (visits == 10) {
					Player.player.addAchieve(this, this.getName() + " listener");
				}
				if (visits == 50) {
					Player.player.addAchieve(this, this.getName() + " consulter");
				}
				if (Rand.chanceIn(2,5)) {
					Player.player.getPerson().insightEffects();
				}
			}
		}else {
			Print.println(TrawelColor.RESULT_ERROR+"You can't afford that!");
		}
	}

	public void utterance2() {
		if (Player.player.getGold() >= utterPrice()) {
			Print.println("Pay "+ (utterPrice()) +" "+World.currentMoneyString()+" for a premium utterance?");
			if (Input.yesNo()) {
				Player.player.addGold(-utterPrice());
				tip("utter");
				int oldVisits = visits;
				visits+=5;
				//doesn't count as an utterance since it is more coherent
				//Networking.unlockAchievement("oracle1");
				if (oldVisits < 5 && visits >= 5) {
					Player.player.addAchieve(this, this.getName() + " vistor");
				}
				if (oldVisits < 10 && visits >= 10) {
					Player.player.addAchieve(this, this.getName() + " listener");
				}
				if (oldVisits < 50 && visits >= 50) {
					Player.player.addAchieve(this, this.getName() + " consulter");
				}
				if (Rand.chanceIn(2,3)) {
					Player.player.getPerson().insightEffects();
				}
			}
		}else {
			Print.println(TrawelColor.RESULT_ERROR+"You can't afford that!");
		}
	}

	@Override
	public void go() {
		//TODO types of oracles
		//differnt oracles are for different quests
		goDelphi();
		
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		// Auto-generated method stub
		return null;
	}
	
	//Main quest guidance
	
	private void goDelphi() {
		while (true) {
			Print.println("1 "+TrawelColor.SERVICE_AETHER+"buy an utterance ("+cheapUtterPrice()+" aether)");
			Print.println("2 "+TrawelColor.SERVICE_CURRENCY+"buy a premium utterance ("+(utterPrice())+" "+World.currentMoneyString()+")");
			Print.println("3 "+TrawelColor.SERVICE_FREE+"sit around and wait for them to talk to you");
			Print.println("9 leave");
			switch (Input.inInt(4,true,true)) {
				case 1: utterance();break;
				case 2: utterance2();break;
				case 3: 
					Print.println("After enough waiting, the oracles start rambling.");
					Player.addTime(Rand.randFloat()*5);
					TrawelTime.globalPassTime();
					utterance0();
					break;
				default: return;
			}
		}
	}
	
	private int utterPrice() {
		return (int) getUnEffectiveLevel();
	}
	
	private int cheapUtterPrice() {
		return getEffectiveLevel();
	}
}
