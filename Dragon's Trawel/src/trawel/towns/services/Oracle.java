package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import trawel.Networking;
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

	public Oracle(String string, int level) {
		name = string;
		tier = level;
		tutorialText = "Oracles can provide guidance.";
	}

	public Oracle() {
		//just for non-static single commands
		tutorialText = "Oracles can provide guidance.";
	}
	
	@Override
	public String getColor() {
		return extra.F_SPECIAL;
	}

	public static void tip(String mask) {//public so it can be accessed in other stuff		
		extra.println("\""+ extra.randList(tips.get(mask)) + "\"");
	}
	
	public static String tipString(String mask) {//public so it can be accessed in other stuff
		return extra.randList(tips.get(mask));
	}
	
	public static String rescLocation() {
		if (mainGame.inEclipse) {
			return "/resource/";
		}
		return "/resc/resource/";
	}
	
	public void load() {
		tips.clear();
		
		
		//System.out.println(new File(".").getAbsolutePath());
		Scanner fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+"oldTips.txt"));
		
		List<String> list = new ArrayList<String>();
		while (fileInput.hasNextLine()) {
			list.add(fileInput.nextLine());
		}
		tips.put("old", list);
		fileInput.close();
		
		fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+"utterTips.txt"));
		
		list = new ArrayList<String>();
		while (fileInput.hasNextLine()) {
			list.add(fileInput.nextLine());
		}
		tips.put("utter", list);
		fileInput.close();
		//
		fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+"cultTips.txt"));
		
		list = new ArrayList<String>();
		while (fileInput.hasNextLine()) {
			list.add(fileInput.nextLine());
		}
		tips.put("cult", list);
		fileInput.close();
		
		fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+"racistPraiseTips.txt"));
		
		list = new ArrayList<String>();
		while (fileInput.hasNextLine()) {
			list.add(fileInput.nextLine());
		}
		tips.put("racistPraise", list);
		fileInput.close();
		
		fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+"racistShunTips.txt"));
		
		list = new ArrayList<String>();
		while (fileInput.hasNextLine()) {
			list.add(fileInput.nextLine());
		}
		tips.put("racistShun", list);
		fileInput.close();
		
		fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+"equalityTips.txt"));
		
		list = new ArrayList<String>();
		while (fileInput.hasNextLine()) {
			list.add(fileInput.nextLine());
		}
		tips.put("equality", list);
		
		fileInput.close();
		
		fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+"shamanTips.txt"));
		
		list = new ArrayList<String>();
		while (fileInput.hasNextLine()) {
			list.add(fileInput.nextLine());
		}
		tips.put("shaman", list);
		
		fileInput.close();
		
		fileInput = new Scanner (Oracle.class.getResourceAsStream(rescLocation()+"gravediggerTips.txt"));
		
		list = new ArrayList<String>();
		while (fileInput.hasNextLine()) {
			list.add(fileInput.nextLine());
		}
		tips.put("gravedigger", list);
		
		fileInput.close();
		
		
	}

	public void utterance() {
		if (Player.player.getGold() >= tier*1) {
			extra.println("Pay "+ tier*1 +" "+World.currentMoneyString()+" for an utterance?");
			if (extra.yesNo()) {
				Player.player.addGold(-tier*1);
				tip("");
				visits++;
				Networking.unlockAchievement("oracle1");
				if (visits == 5) {
					Player.player.addTitle(this.getName() + " vistor");
				}
				if (visits == 10) {
					Player.player.addTitle(this.getName() + " listener");
				}
				if (visits == 50) {
					Player.player.addTitle(this.getName() + " consulter");
				}
			}
		}else {
			extra.println("You can't afford that!");
		}
	}

	public void utterance2() {
		if (Player.player.getGold() >= tier*100) {
			extra.println("Pay "+ tier*5 +" "+World.currentMoneyString()+" for a premium utterance?");
			if (extra.yesNo()) {
				Player.player.addGold(-tier*5);
				tip("utter");
				int oldVisits = visits;
				visits+=4;
				Networking.unlockAchievement("oracle1");
				if (oldVisits < 5 && visits >= 5) {
					Player.player.addTitle(this.getName() + " vistor");
				}
				if (oldVisits < 10 && visits >= 10) {
					Player.player.addTitle(this.getName() + " listener");
				}
				if (oldVisits < 50 && visits >= 50) {
					Player.player.addTitle(this.getName() + " consulter");
				}
			}
		}else {
			extra.println("You can't afford that!");
		}
	}

	@Override
	public void go() {
		Networking.setArea("shop");
		//TODO types of oracles
		//differnt oracles are for different quests
		Networking.sendStrong("Discord|imagesmall|oracle|Oracle|");
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
		extra.println("1 buy an utterance ("+tier*1+" "+World.currentMoneyString()+")");
		extra.println("2 buy a premium utterance ("+tier*5+" "+World.currentMoneyString()+")");
		extra.println("3 sit around and wait for them to talk to you");
		extra.println("4 leave");
		switch (extra.inInt(4)) {
		case 1: utterance();break;
		case 2: utterance2();break;
		case 3: 
			extra.println("After enough waiting, the oracles start rambling.");
			Player.addTime(extra.randFloat()*5);
			utterance();
			break;
			case 4: return;
		}
		}
	}
}
