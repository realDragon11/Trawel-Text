package trawel;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import trawel.personal.people.Player;

public class Networking {

	public static final String AGGRO = extra.inlineColor(extra.colorMix(Color.RED, Color.WHITE, 0.5f));//not sure if pre red comes first so
	
	//public static InputStream in;
	public static Socket socket;
	private static boolean connected = false;
	private static BattleType inBattle = BattleType.NONE;
	private static String songType = "main", backtype = "main";
	public static boolean autoconnectSilence = false;
	
	private static InputStream netIn;
	private static Scanner in;

	private static PrintWriter netOut, out;
	private static OutputStream gdxOut;
	private static Process commandPrompt;
	
	private static boolean simpleTransmit = true;//DOLATER
	
	private static ConnectType type = ConnectType.NONE;
	
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
			in = new Scanner(System.in);
			out = new PrintWriter(System.out);
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
			//FIXME: just do this in the other half
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

	public static boolean connect(int port) {
		try {
			socket = new Socket("127.0.0.1", port);
			gdxOut = socket.getOutputStream();
			netOut = new PrintWriter(gdxOut);

		    netIn = socket.getInputStream();
		    System.out.println("Connection.");
		    connected = true;
		    extra.println("Connected!");
		    return true;
			}catch(Exception e) {
				System.out.println("Connection failed.");
			}
			//e.printStackTrace();
			return false;
	}
	
	public static void autoConnect() {
		//autoconnectSilence = true;
		System.out.println("Connecting to localhost Graphical...");
		while (true) {
			try {
				TimeUnit.MILLISECONDS.sleep(20L);
			} catch (InterruptedException e) {
			}
			if (connect(6510)) {
				break;
			}
		}
	}
	
	
	public static void send(String str) {
		if (!extra.getPrint()) {
			sendStrong(str);
		}
	}
	
	public static void sendStrong(String str) {
		if (!extra.isMainThread() || !connected) {
			return;
		}
		switch (type) {
		case GDX:
			try {
				sendHeader(HeaderType.LEGACY);
				sendStringContent(str);
				commit();
			}catch(Exception e) {
				e.printStackTrace();
				mainGame.log(e.getMessage());
			}
			break;
		case GDX_WITH_LEGACY:
			debugGDXPrintLegacy(str);
			break;
		case LEGACY:
			if (netOut != null) {
				netOut.println(str);
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
					TimeUnit.MILLISECONDS.sleep(20L);
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
			try {
				return Math.max(0,Integer.parseInt(in.nextLine()));
			}catch(NumberFormatException e) {
				return 0;
			}
		}
	}
	
	public static boolean connected() {
		return connected;
	}

	public static void charUpdate() {
		Player.bag.graphicalDisplay(-1,Player.player.getPerson());
		//Networking.sendStrong("Discord|imagelarge|"+Player.bag.getRace().name+"|" + Player.player.getPerson().getName() + " level "+ Player.player.getPerson().getLevel() +"|/");//replace icon with player.player.race later
		Networking.sendStrong("Discord|imagelarge|icon|" + Player.player.getPerson().getName() + " level "+ Player.player.getPerson().getLevel() +"|");//replace icon with player.player.race later
	}

	public static void clearSides() {
		Networking.sendStrong("ClearInv|1|");
		Networking.sendStrong("ClearInv|-1|");
		
	}

	public static void clearSide(int i) {
		Networking.sendStrong("ClearInv|"+i+"|");
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
	
	public static void setArea(String area) {
		if (!songType.equals(area)) {
			songType = area;
			if (area.equals("port")) {
				Networking.sendStrong("PlaySong|sound_port|");
				return;
			}
			if (inBattle == BattleType.BOSS) {
				Networking.sendStrong("PlaySong|"+ songType +"_boss|");
			}else {
			Networking.sendStrong("PlaySong|" + songType + (inBattle == BattleType.NORMAL ? "_fight" : "_explore")  + "|");}
		}
	}
	
	public static void setBackground(String background) {
		if (!background.equals(backtype)) {
			backtype = background;
			Networking.sendStrong("Background|"+background+"|");
		}
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
		Networking.addLight(extra.colorMix(Color.RED, Color.WHITE,0.6f) ,extra.colorMix(Color.RED, Color.WHITE,0.4f), x,y, 200, 300, 1);
		Networking.addLight(extra.colorMix(Color.RED, Color.WHITE,0.8f) ,extra.colorMix(Color.RED, Color.WHITE,0.6f), x,y, 100, 200, 2);
		Networking.addLight(extra.colorMix(Color.RED, Color.WHITE,0.3f) ,extra.colorMix(Color.RED, Color.WHITE,0.2f), x,y, 50, 100, 1);
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
				sendHeader(HeaderType.PRINTLN);
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
	
	public enum HeaderType{
		PRINTLN, LEGACY
	}
	
	//for codes:
	//https://en.wikipedia.org/wiki/UTF-8#Codepage_layout
	//https://en.wikipedia.org/wiki/C0_and_C1_control_codes#Basic_ASCII_control_codes
	
	
	private static void sendHeader(HeaderType hType)  throws IOException{
		if (simpleTransmit) {
			debugGDXPrint(hType.name());
			return;
		}
		
		gdxOut.write(1);//start of heading
		switch (hType) {
		default:
			gdxOut.write(hType.ordinal());
			break;
		}
		gdxOut.write(2);//start of text
	}
	
	private static void sendStringContent(String str) throws IOException {
		if (simpleTransmit) {
			debugGDXPrint(str);
			return;
		}
		
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		gdxOut.write(bytes);
		gdxOut.write(23);//end transmission block
	}
	
	private static void commit() throws IOException {
		if (simpleTransmit) {
			debugGDXPrint(" |END|");
			gdxOut.write(3);//end of text still needed
			gdxOut.flush();
			return;
		}
		
		gdxOut.write(3);//end of text
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
			gdxOut.write(bytes);
		} catch (IOException e) {
		}
	}
}
