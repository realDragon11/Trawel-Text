package trawel.towns.services;
import java.awt.Color;
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

public class Oracle extends Feature implements java.io.Serializable{ //extends feature later
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Map<String,List<String>> tips = new HashMap<String,List<String>>();
	private int visits = 0;
	private int level;

	public Oracle(String string, int level) {
		name = string;
		this.level = level;
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
		if (Player.bag.getGold() >= level*1) {
		extra.println("Pay "+ level*1 +" gold for an utterance?");
		if (extra.yesNo()) {
			Player.bag.addGold(-level*1);
			tip("");
			visits++;
			Networking.sendStrong("Achievement|oracle1|");
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
		if (Player.bag.getGold() >= level*100) {
		extra.println("Pay "+ level*100 +" gold for an premium utterance?");
		if (extra.yesNo()) {
			Player.bag.addGold(-level*100);
			tip("utter");
			int oldVisits = visits;
			visits+=4;
			Networking.sendStrong("Achievement|oracle1|");
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
		extra.println("1 buy an utterance ("+level*1+" gold)");
		extra.println("2 buy a premium utterance ("+level*100+" gold)");
		extra.println("3 sit around and wait for them to talk to you");
		extra.println("4 leave");
		switch (extra.inInt(4)) {
		case 1: utterance();break;
		case 2: utterance2();break;
		case 3:swaQuest(); Player.addTime(.5);;//main quest stuff
			;break;
		case 4: return;
		}
		}
	}
	
	private void swaQuest() {
		switch(Player.player.animalQuest) {
		case -1: extra.println("The oracles ignore you.");break;
		case 0: extra.println("\"Seek the "+Player.player.animalName() +" in the forest. Look for trees felled in it's wake.\"");break;
		case 1: extra.println("\"You are touched now. Go to the altar, in the north. Revan is what you must seek.\"");break;
		case 2: extra.println("\"Fight- and win...\"");break;
		case 3: extra.println("\"Seek an empty inn... start a barfight.\"");break;
		case 4: extra.println("\"Epino arena... in Tevar...\"");break;
		case 5: extra.println("\"Return to the altar.\"");break;
		}
	}
}
