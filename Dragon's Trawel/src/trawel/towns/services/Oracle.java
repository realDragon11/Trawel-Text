package trawel.towns.services;
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

import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;

public class Oracle extends Feature{ //extends feature later

	private static final long serialVersionUID = 1L;
	private static Map<String,List<String>> tips = new HashMap<String,List<String>>();
	private int visits = 0;
	
	private static List<String> emptyList = Collections.singletonList("tips not loaded!");
	//MAYBELATER: either need to distribute tips with game, or unpack them, or find a workaround to my jar loading issues with path redirections
	//witness hell: https://stackoverflow.com/a/6247181
	public static String[] tipLocs = new String[] {"utter","cult","equality","gravedigger","old","racistPraise","racistShun","shaman"};
	public static List<String> tipLocsList = Arrays.asList(tipLocs);
	
	public Oracle(String string, int level) {
		name = string;
		tier = level;
		tutorialText = "Oracle";
		area_type = Area.ORACLE;
	}

	public Oracle() {
		//just for non-static single commands
		tutorialText = "Oracles";
		area_type = Area.ORACLE;
	}
	
	@Override
	public String getColor() {
		return extra.F_SPECIAL;
	}

	public static void tip(String mask) {
		extra.println("\""+ extra.randList(tips.getOrDefault(mask, emptyList)) + "\"");
	}
	
	public static String tipString(String mask) {
		return extra.randList(tips.get(mask));
	}
	
	public static String tipStringExt(String mask,String aAn, String self, String selves, String selvian, String town, List<String> otherList) {
		String tip = extra.randList(tips.get(mask))
				.replaceAll(Pattern.quote("<a>"), aAn)
				.replaceAll(Pattern.quote("<self>"), self)
				.replaceAll(Pattern.quote("<selves>"),selves)
				.replaceAll(Pattern.quote("<selvian>"),selvian)
				.replaceAll(Pattern.quote("<town>"), town)
				;
		while (tip.contains("<other>")) {
			tip = tip.replaceFirst(Pattern.quote("<other>"),extra.randList(otherList));
		}
		return tip;
	}
	
	public static String tipRandomOracle(String town) {
		return tipStringExt("","an","oracle","oracles","oracle",town,Collections.singletonList("not-oracle"));
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
		try (Scanner fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+mask+"Tips.txt"))){
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
			String mask = loc.getName().replace("Tips.txt","");
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
					if (arg1.contains("Tips.txt")) {
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
		extra.println("\""+tipRandomOracle(town.getName())+"\"");
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
			extra.println("Pay "+ cheapUtterPrice() +" aether for an utterance?");
			if (extra.yesNo()) {
				Player.bag.addAether(-cheapUtterPrice());
				extra.println("\""+tipRandomOracle(town.getName())+"\"");
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
			}
		}else {
			extra.println(extra.RESULT_ERROR+"You can't afford that!");
		}
	}

	public void utterance2() {
		if (Player.player.getGold() >= utterPrice()) {
			extra.println("Pay "+ (utterPrice()) +" "+World.currentMoneyString()+" for a premium utterance?");
			if (extra.yesNo()) {
				Player.player.addGold(-utterPrice());
				tip("utter");
				int oldVisits = visits;
				visits+=5;
				Networking.unlockAchievement("oracle1");
				if (oldVisits < 5 && visits >= 5) {
					Player.player.addAchieve(this, this.getName() + " vistor");
				}
				if (oldVisits < 10 && visits >= 10) {
					Player.player.addAchieve(this, this.getName() + " listener");
				}
				if (oldVisits < 50 && visits >= 50) {
					Player.player.addAchieve(this, this.getName() + " consulter");
				}
			}
		}else {
			extra.println(extra.RESULT_ERROR+"You can't afford that!");
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
			extra.println("1 "+extra.SERVICE_AETHER+"buy an utterance ("+cheapUtterPrice()+" aether)");
			extra.println("2 "+extra.SERVICE_CURRENCY+"buy a premium utterance ("+(utterPrice())+" "+World.currentMoneyString()+")");
			extra.println("3 "+extra.SERVICE_FREE+"sit around and wait for them to talk to you");
			extra.println("9 leave");
			switch (extra.inInt(4,true,true)) {
				case 1: utterance();break;
				case 2: utterance2();break;
				case 3: 
					extra.println("After enough waiting, the oracles start rambling.");
					Player.addTime(extra.randFloat()*5);
					mainGame.globalPassTime();
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
