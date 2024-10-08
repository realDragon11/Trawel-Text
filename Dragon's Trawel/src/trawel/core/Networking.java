package trawel.core;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import trawel.battle.attacks.ImpairedAttack;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.PrintTutorial;
import trawel.helper.methods.extra;
import trawel.personal.Person;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race;
import trawel.personal.item.body.Race.RaceType;
import trawel.personal.people.Player;
import trawel.threads.ThreadData;
import trawel.towns.data.Calender;
import trawel.towns.data.FeatureData;

public class Networking {

	//public static InputStream in;
	public static Socket socket;
	private static boolean connected = false;
	private static BattleType inBattle = BattleType.NONE;
	public static boolean autoconnectSilence = false;
	
	private static InputStream netIn;
	private static Scanner in;

	private static PrintWriter netOut, out;
	private static OutputStream gdxOut;
	private static Process commandPrompt;
	
	private static Person enemyDisplay, friendlyDisplay;
	
	private static boolean simpleTransmit = false;//DOLATER
	
	private static ConnectType type = ConnectType.LEGACY;//just do legacy by default to avoid issues
	
	public static OSNUM os;
	
	public enum OSNUM{
		LINUX,WINDOWS
	}

	public enum BattleType{
		NONE, NORMAL, BOSS;
	}
	
	public enum ConnectType{
		NONE, GDX, LEGACY, GDX_WITH_LEGACY
	}
	
	public Networking() {
		in = new Scanner(System.in);
		out = new PrintWriter(System.out);
	}
	
	public static void handleAnyConnection(ConnectType atype) {
		type = atype;
		switch (atype) {
		case GDX:
			if (mainGame.headless) {
				System.out.println("loud");
				connectGDXHeadless();
			}else {
				System.out.println("normal");
				connectGDX();
			}
			break;
		case LEGACY:
			autoConnect();
		case NONE:
			//in = new Scanner(System.in);
			//out = new PrintWriter(System.out);
			break;
		}
	}
	
	public static void connectGDXHeadless() {
		in = new Scanner(System.in);
		out = new PrintWriter(System.out);
		autoConnect();
		//type = ConnectType.GDX_WITH_LEGACY;
	}
	
	public static void connectGDX() {		
		//new idea: we assume our standard output is hijacked and we need to make out own console out
		os = OSNUM.LINUX;//testing
		String osTerm = null;
		switch (os) {
		case LINUX:
			osTerm = "xterm";
			break;
		case WINDOWS:
			osTerm = "cmd /c start cmd.exe";
			break;
		}
		netIn = System.in;
		netOut = new PrintWriter(System.out);
		gdxOut = System.out;
		if (mainGame.noTerminal) {
			in = null;
			out = null;
		}else {
			ProcessBuilder builder = new ProcessBuilder(osTerm);
			//MAYBELATER: just do this in the other half
			//SEO has ruined java resources but I could just resort to sockets to pass stuff off again
			//if there is no website willing to tell me how to pass off non-standard streams
			builder.directory(null);//current directory
			try {
				commandPrompt = builder.start();
				in = new Scanner(commandPrompt.getInputStream());
				out = new PrintWriter(commandPrompt.getOutputStream());				
				//System.setErr(new PrintStream(commandPrompt.getOutputStream()));
				mainGame.log("terminal started");
			} catch (IOException e) {
				mainGame.log(e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		connected = true;
		printlnTo(mainGame.headerText());
		//printlnTo("in: "+nextInt());
		mainGame.log("got handoff");
		printlnTo(mainGame.headerText());
	}

	public static boolean connect(int port, boolean printFail) {
		try {
			socket = new Socket("127.0.0.1", port);
			gdxOut = socket.getOutputStream();
			netOut = new PrintWriter(gdxOut);

		    netIn = socket.getInputStream();
		    System.out.println("Connection.");
		    connected = true;
		    System.out.println("Connected!");
		    return true;
		}catch(Exception e) {
			if (printFail) {
				System.out.println("Connection failed.");
			}
		}
		return false;
	}
	
	public static void autoConnect() {
		//autoconnectSilence = true;
		System.out.println("Connecting to localhost Graphical...");
		int fails = 0;
		while (true) {
			try {
				TimeUnit.MILLISECONDS.sleep(500L);
			} catch (InterruptedException e) {
			}
			if (connect(6510,false)) {
				break;
			}
			fails++;
			if (fails == 20) {
				System.out.println("Connecting is taking longer than expected!");
			}
		}
	}
	
	
	public static void send(String str) {
		if (!Print.getPrint()) {
			sendStrong(str);
		}
	}
	
	public static void sendStrong(String str) {
		if (!ThreadData.isMainThread() || !connected) {
			return;
		}
		switch (type) {
		case GDX:
			try {
				sendHeader(OpCode.LEGACY);
				sendStringContent(str);
				commit();
			}catch(Exception e) {
				e.printStackTrace();
				//mainGame.log(e.getMessage());
			}
			break;
		case GDX_WITH_LEGACY:
			debugGDXPrintLegacy(str);
			break;
		case LEGACY:
			if (netOut != null) {
				//Networking.printlocalln(str);
				netOut.println(str);
				//netOut.println(str.replaceAll(Pattern.quote("% s")," percent s").replaceAll(Pattern.quote("% S")," percent S"));
				netOut.flush();
			}
			break;
		}
	}
	
	public static int nextInt() {
		if (!mainGame.headless && ((Networking.connected() && mainGame.GUIInput) || Networking.autoconnectSilence)) {
			//network only
			if (netIn == null && autoconnectSilence) {
				try {
					TimeUnit.MILLISECONDS.sleep(500L);
				} catch (InterruptedException e) {
				}
				return -2;
			}
			if (simpleTransmit) {
				//return in.nextInt();
			}
			try {
				return netIn.read();
			} catch (IOException e) {
				return -99;
			}
		}else {
			//terminal only
			String inString = in.nextLine();
			if (preProcessInput(inString)) {
				return -4;//return was consumed
			}
			try {
				return Math.max(0,Integer.parseInt(inString));
			}catch(NumberFormatException e) {
				return -3;
			}
		}
	}
	
	/**
	 * @return if input was consumed
	 */
	public static final boolean preProcessInput(String in) {
		String string = in.toLowerCase();
		if (Player.isPlaying) {
			if (string.equals("t") || string.equals("tutorial")) {//"tutorial"
				if (inBattle != BattleType.NONE) {
					PrintTutorial.battleTutorial(false);
					return true;
				}
				if (Player.player.atFeature != null) {
					FeatureData data = FeatureData.getData(Player.player.atFeature.getClass());
					if (data != null) {
						data.tutorial();
					}else {
						Print.println(TrawelColor.RESULT_ERROR+"This feature has no tutorial.");
					}
					return true;
				}
			}
			if (string.equals("i") || string.equals("inventory")  || string.equals("friendly")) {//"inventory"
				if (friendlyDisplay != null) {
					friendlyDisplay.displayVisual();
				}else {
					Print.println(TrawelColor.RESULT_ERROR+"There is no friendly to display.");
				}
				return true;
			}
			if (string.equals("o") || string.equals("opponent") || string.equals("enemy")) {
				if (enemyDisplay != null) {
					enemyDisplay.displayVisual();
				}else {
					Print.println(TrawelColor.RESULT_ERROR+"There is no enemy to display.");
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean connected() {
		return connected;
	}

	public static void charUpdate() {
		friendlyDisplay = Player.player.getPerson();
		Player.bag.graphicalDisplay(-1,Player.player.getPerson());
		//Networking.sendStrong("Discord|imagelarge|"+Player.bag.getRace().name+"|" + Player.player.getPerson().getName() + " level "+ Player.player.getPerson().getLevel() +"|/");//replace icon with player.player.race later
		//Networking.sendStrong("Discord|imagelarge|icon|" + Player.player.getPerson().getName() + " level "+ Player.player.getPerson().getLevel() +"|");//replace icon with player.player.race later
	}

	public static void clearSides() {
		Networking.sendStrong("ClearInv|1|");
		enemyDisplay = null;
		Networking.sendStrong("ClearInv|-1|");
		friendlyDisplay = null;
	}

	public static void clearSide(int i) {
		Networking.sendStrong("ClearInv|"+i+"|");
		if (i == 1) {
			enemyDisplay = null;
		}
		if (i == -1) {
			friendlyDisplay = null;
		}
	}
	
	public static void richDesc(String desc) {
		//Networking.sendStrong("Discord|desc|"+desc+"|");
	}
	
	public static void setBattle(BattleType battle) {
		if (inBattle != battle) {
			inBattle = battle;
			if (battle == BattleType.BOSS) {
				Networking.sendStrong("PlaySong|"+ songType +"_boss|");
			}else {
			Networking.sendStrong("PlaySong|" + songType + (battle == BattleType.NORMAL ? "_fight" : "_explore")  + "|");}
		}
	}
	
	public enum Area{
		ROADS("main","forest","grove"),
		PORT("mountain","forest","town"),
		TOWN("main","forest","town"),
		
		LOT("shop","forest","lot"),
		GARDEN("shop","forest","garden"),
		SHOP("shop","mine","store"),
		ALTAR("mountain","mountain","altar"),
		MISC_SERVICE("shop","forest","store"),
		ORACLE("mountain","forest","oracle"),
		INN("inn","forest","inn"),
		SLUM("dungeon","mine","store"),
		
		ARENA("arena","forest","arena"),
		CHAMPION("arena","mountain","champion"),//champs aren't real features anymore tbh
		
		FOREST("forest","forest","grove"),
		MOUNTAIN("mountain","mountain","mountain"),
		
		MINE("mine","mine","mine"),
		CAVE("mine","mine","grove"),
		DUNGEON("dungeon","mine","dungeon"),
		GRAVEYARD("dungeon","forest","dungeon"),
		BEACH("mountain","mountain","mountain")
		;
		public final String musicName, backName, discordImageSmall;
		Area(String _musicName, String _backName,String _imageSmall) {
			musicName = _musicName;
			backName = _backName;
			discordImageSmall = _imageSmall;
		}
	}
	
	private static Area current_area;
	private static String current_background = "";
	private static String current_background_variant = "";
	private static String songType = "";
	
	public static void setArea(Area area_type) {
		if (!current_background.equals(area_type.backName)) {
			current_background = area_type.backName;
			Networking.sendStrong("Background|"+current_background+"|");
			current_background_variant = "1";
		}
		if (!songType.equals(area_type.musicName)) {
			songType = area_type.musicName;
			if (songType.equals("port")) {
				Networking.sendStrong("PlaySong|sound_port|");
				return;
			}
			if (inBattle == BattleType.BOSS) {
				Networking.sendStrong("PlaySong|"+ songType +"_boss|");
			}else {
			Networking.sendStrong("PlaySong|" + songType + (inBattle == BattleType.NORMAL ? "_fight" : "_explore")  + "|");}
		}
		updateTime();
		if (current_area != area_type && Player.player.atFeature != null) {
			//Networking.sendStrong("Discord|imagesmall|store|"+Player.player.atFeature.getName()+"|");
		}
		current_area = area_type;
	}
	
	public static void updateTime() {
		double[] p = Calender.lerpLocation(Player.player.getLocation());
		float[] b = Player.player.getWorld().getCalender().getBackTime(p[0],p[1]);
		Networking.sendStrong("Backvariant|"+current_background+current_background_variant+"|"+b[0]+"|"+b[1]+"|");
	}
	
	public static boolean backgroundMatchesArea(Area area_type) {
		return current_background.equals(area_type.backName);
	}
	
	public static void waitIfConnected(long d) {
		if (connected) {
			try {
				TimeUnit.MILLISECONDS.sleep(d);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void unConnect() {
		if (mainGame.noDisconnect) {
			return;
		}
		try {
			netIn.close();
		} catch (IOException e) {
		}
		netOut.close();
		connected = false;
		
	}
	
	public static void addLight(Color col1,Color col2, int x, int y, int minSize,int maxSize,int spin) {
		int in = col1.getRGB();
		int red = (in >> 16) & 0xFF;
		int green = (in >> 8) & 0xFF;
		int blue = (in >> 0) & 0xFF;
		int out1 = (blue << 16) | (green << 8) | (red << 0);
		in = col1.getRGB();
		red = (in >> 16) & 0xFF;
		green = (in >> 8) & 0xFF;
		blue = (in >> 0) & 0xFF;
		int out2 = (blue << 16) | (green << 8) | (red << 0);
		Networking.sendStrong("AddLight|"+out1 + "|"+out2+ "|" + x +"|"+ y +"|" + minSize+"|"+maxSize+"|"+spin+"|");
	}
	
	public static void addMultiLight(int x,int y) {
		Networking.addLight(TrawelColor.colorMix(Color.RED, Color.WHITE,0.6f) ,TrawelColor.colorMix(Color.RED, Color.WHITE,0.4f), x,y, 200, 300, 1);
		Networking.addLight(TrawelColor.colorMix(Color.RED, Color.WHITE,0.8f) ,TrawelColor.colorMix(Color.RED, Color.WHITE,0.6f), x,y, 100, 200, 2);
		Networking.addLight(TrawelColor.colorMix(Color.RED, Color.WHITE,0.3f) ,TrawelColor.colorMix(Color.RED, Color.WHITE,0.2f), x,y, 50, 100, 1);
	}
	
	public static void clearLights() {
		Networking.sendStrong("ClearLights|");
	}

	public static void printlocalln(String print) {
		if (out == null) {
			return;
		}
		out.println(print);
		out.flush();
	}

	public static void printlnTo(String string) {
		if (!connected) {
			return;
		}
		switch (type) {
		case GDX_WITH_LEGACY:
			debugGDXPrintLegacy(string);
			break;
		case GDX:
			try {
				sendHeader(OpCode.PRINTLN);
				sendStringContent(string);
				commit();
			}catch(Exception e) {
				e.printStackTrace();
			}
			break;
		case LEGACY:
			//FIXME after update text hinting/display code, make the code here unupdate it
			//that or make a dedicated fancy text builder class
			Networking.send("println|"+ string + "|");
			break;		
		}
	}
	
	public enum OpCode{
		//control codes
		HEADER(1),START_CONTENT(2), END_BLOCK(23),
		END_MESSAGE(3),
		//our codes
		LEGACY(33), PRINTLN(34),
		INPUT_NUM(35)
		;
		
		public final int code;
		OpCode(int code) {
			this.code = code;
		}
	}
	
	//for codes:
	//https://en.wikipedia.org/wiki/UTF-8#Codepage_layout
	//https://en.wikipedia.org/wiki/C0_and_C1_control_codes#Basic_ASCII_control_codes
	
	
	private static void sendHeader(OpCode hType)  throws IOException{
		if (simpleTransmit) {
			debugGDXPrint(hType.name());
			return;
		}
		
		gdxOut.write(OpCode.HEADER.code);//start of heading
		switch (hType) {
		default:
			gdxOut.write(hType.code);
			break;
		}
		gdxOut.write(OpCode.START_CONTENT.code);//start of text
	}
	
	private static void sendStringContent(String str) throws IOException {
		if (simpleTransmit) {
			debugGDXPrint(str);
			return;
		}
		
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		gdxOut.write(bytes);
		gdxOut.write(OpCode.END_BLOCK.code);//end transmission block
	}
	
	private static void commit() throws IOException {
		if (simpleTransmit) {
			debugGDXPrint(" |END|");
			gdxOut.write(OpCode.END_MESSAGE.code);//end of text still needed
			gdxOut.flush();
			return;
		}
		
		gdxOut.write(OpCode.END_MESSAGE.code);//end of text
		gdxOut.flush();
	}
	
	public static void debugGDXPrint(String str) throws IOException {
		mainGame.log(str);
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		gdxOut.write(bytes);
	}
	
	public static void debugGDXPrintLegacy(String str) {
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		try {
			gdxOut.write(OpCode.HEADER.code);//start of heading
			gdxOut.write(OpCode.LEGACY.code);
			gdxOut.write(OpCode.START_CONTENT.code);//start of text
			gdxOut.write(bytes);
			gdxOut.write(OpCode.END_BLOCK.code);//end transmission block
			gdxOut.write(OpCode.END_MESSAGE.code);//end of text
		} catch (IOException e) {
		}
	}

	public static void unlockAchievement(String string) {
		if (!Player.player.getCheating()) {
			sendStrong("Achievement|"+string+"|");
		}
		
	}

	public static void leaderboard(String string, int stat) {
		if (!Player.player.getCheating()) {
			Networking.sendStrong("Leaderboard|"+string+"|" + stat+ "|");
		}
	}
	
	public static void statAdd(String stat,int amount) {
		if (!Player.player.getCheating()) {
			sendStrong("StatUp|"+stat+"|"+amount+"|");
		}
	}
	
	public static void statAddUpload(String stat,String leaderboard, int amount) {
		if (!Player.player.getCheating()) {
			sendStrong("StatUpload|"+stat+"|"+amount+"|"+leaderboard+"|");
		}
	}
	
	public static void playHitConnect(ImpairedAttack att, Inventory def, boolean boostSound, boolean deflectSound) {
		Networking.send("PlayHit|" +def.getSoundType(att.getSlot()) + "|"
				+extra.clamp(att.getAttack().getSoundIntensity() + (boostSound ? Rand.randRange(0,1) : 0)+ (deflectSound ? -Rand.randRange(0,1) : 0),0,2)
				+"|" +att.getAttack().getSoundType()+"|");
	}
	
	public static void addGraphicalInv(int side,String spriteName,String mapName,int mapIndex,int bloodSeed,double bloodCount,int enchantStyle, int depth, String clearLayer) {
		sendStrong("AddInv|"+side+"|" +spriteName +"|"+mapName+"|"+mapIndex+"|"+bloodSeed + "|" + bloodCount + "|" +enchantStyle+"|"+depth+"|"+clearLayer+"|");
	}
	
	public static void addGraphicalRace(int side,Race race,int mapIndex,String raceFlag,int bloodSeed,double bloodCount, String clearLayer) {
		String spriteName = race.getWasddSprite();
		String mapName = race.getWasddMap();
		sendStrong("RaceInv|"+side+"|" +spriteName +"_base|"+mapName+"|"+mapIndex+"|"+raceFlag+"|"+bloodSeed + "|" + bloodCount+"|1|"+clearLayer+"|");
		if (race.racialType.equals(RaceType.PERSONABLE)) {
			sendStrong("RaceInv|"+side+"|" +spriteName +"_hands|"+mapName+"|"+mapIndex+"|"+raceFlag+"|"+bloodSeed + "|" + bloodCount+"|-8|"+clearLayer+"|");
			//sendStrong("RaceInv|"+side+"|" +spriteName +"_lefthand|" +mapName+"|"+mapIndex+"|"+raceFlag+"|"+bloodSeed + "|" + bloodCount+"|-8|"+clearLayer+"|");
		}
	}

	public static void setSideAs(int side, Person p) {
		if (side == -1) {
			friendlyDisplay = p;
		}
		if (side == 1) {
			enemyDisplay = p;
		}
	}
	
}
