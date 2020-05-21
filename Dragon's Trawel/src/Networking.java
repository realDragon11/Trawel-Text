import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Networking {

	 public static PrintWriter out;
	 public static InputStream in;
	 public static Socket socket;
	 private static boolean connected = false;
	 private static BattleType inBattle = BattleType.NONE;
	 private static String songType = "main";
	 
	 public enum BattleType{
		 NONE, NORMAL, BOSS;
	 }
	
	 public static void connect(int port) {
			try {
		    socket = new Socket("127.0.0.1", port);
		    out = new PrintWriter(socket.getOutputStream(), true);

		    in = socket.getInputStream();

		    extra.println("Connected!");
		    connected = true;
		    return;
			}catch(Exception e) {
				extra.println("Connection failed.");
			}
				//e.printStackTrace();
				return;
			}
	
	public static void autoConnect() {
		boolean doit = true;
		int value= 6510;
		extra.println("Connecting...");
		while (doit) {
			try {
			socket = new Socket("127.0.0.1", 6510);
			out = new PrintWriter(socket.getOutputStream(), true);
		    in = socket.getInputStream();
		    doit = false;
			}catch (Exception e) {}
		}
		 extra.println("Connected!");
		 connected = true;
	}
	
	
	public static void send(String str) {
		if (out != null && (!extra.printMode)) {
		out.println(str);
		//out.flush();
		if (mainGame.debug == true) {
			System.err.println(str);
		}
		}
	}
	
	public static void sendStrong(String str) {
		if (out != null) {
		out.println(str);
		//out.flush();
		if (mainGame.debug == true) {
			System.err.println(str);
		}
		}
	}
	
	public static int nextInt() {
		
		try {
			return in.read();
			//(int)(Byte.toUnsignedInt(in.nextByte()));
			//return in.nextInt();
		}catch(Exception e) {
			return -99;
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

	public static void sendColor(Color col) {
		int in = col.getRGB();
		int red = (in >> 16) & 0xFF;
		int green = (in >> 8) & 0xFF;
		int blue = (in >> 0) & 0xFF;
		int out = (blue << 16) | (green << 8) | (red << 0);
		Networking.send("Color|" + out +"|");
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
	
	public static void waitIfConnected(long d) {
		if (connected) {
			try {
				TimeUnit.MILLISECONDS.sleep(d);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void unConnect() {
		try {
			in.close();
		} catch (IOException e) {
		}
		out.close();
		connected = false;
		
	}
}
