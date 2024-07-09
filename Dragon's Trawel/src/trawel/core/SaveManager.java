package trawel.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.objenesis.strategy.SerializingInstantiatorStrategy;
import org.apache.fury.io.FuryInputStream;
import org.apache.fury.logging.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import de.javakaffee.kryoserializers.EnumMapSerializer;
import de.javakaffee.kryoserializers.EnumSetSerializer;
import trawel.arc.misc.Changelog;
import trawel.helper.methods.extra;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Player;
import trawel.towns.contexts.Plane;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.data.WorldGen;

public class SaveManager {
	
	public static Kryo trawelKryo;
	public static Fury fury;
	
	public static final void trawelRegisterKyro() {
		/**
		 * https://github.com/magro/kryo-serializers/tree/master
		 */
		trawelKryo = new Kryo(){

		    @Override
		    public Serializer<?> getDefaultSerializer(final Class clazz) {
		        if (EnumSet.class.isAssignableFrom( clazz ) ) {
		            return new EnumSetSerializer();
		        }
		        if (EnumMap.class.isAssignableFrom( clazz ) ) {
		            return new EnumMapSerializer();
		        }
		        return super.getDefaultSerializer( clazz );
		    }
		};
		trawelKryo.setRegistrationRequired(false);
		trawelKryo.register(derg.ds.TwinListMap.class, new FieldSerializer<derg.ds.TwinListMap>(trawelKryo, derg.ds.TwinListMap.class));
		trawelKryo.setReferences(true);
		trawelKryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
		
		//trawelKryo.setAutoReset(true);
	}

	public static final void trawelRegisterFury() {
		LoggerFactory.disableLogging();
		fury = Fury.builder().withLanguage(Language.JAVA)
				.withRefTracking(true)
		        .requireClassRegistration(false)
		        .build();
	}
	
	public static final void savePlaneFury(Plane plane,FileOutputStream file) {
		Output out = new Output(file);
		fury.serialize(out,plane);
		out.flush();
	}
	
	public static final Plane readPlaneFury(FileInputStream file) {
		FuryInputStream in = new FuryInputStream(file);
		Plane p = (Plane) fury.deserialize(in);
		Person per = p.getPlayer().getPerson();
		World w = p.getPlayer().getWorld();
		extra.println(per.getName() +": Level " + per.getLevel() + ", "+per.xpString() + " XP");
		extra.println(w.getName() + ": Year " +w.getCalender().dateYear());
		//trawelKryo.reset();
		return p;
	}
	
	public static final void savePlaneKyro(Plane plane, FileOutputStream file) {
		Output out = new Output(file);
		trawelKryo.writeObject(out, plane);
		out.flush();
		//trawelKryo.reset();
	}
	
	public static final Plane readPlaneKyro(FileInputStream file) {
		Input in = new Input(file);
		Plane p = trawelKryo.readObject(in,Plane.class);
		Person per = p.getPlayer().getPerson();
		World w = p.getPlayer().getWorld();
		extra.println(per.getName() +": Level " + per.getLevel() + ", "+per.xpString() + " XP");
		extra.println(w.getName() + ": Year " +w.getCalender().dateYear());
		//trawelKryo.reset();
		return p;
	}
	
	public static void save(String str) {
		WorldGen.plane.prepareSave();
		try (FileOutputStream fos = new FileOutputStream("trawel"+str+".save");
				PrintWriter pws =new PrintWriter(fos);
				){
			 pws.write(Player.player.getPerson().getName()
					 +", level " + Player.player.getPerson().getLevel()
					 + ": " +new Date().toString()
					 +" "+Changelog.VERSION_STRING+"\0");
			 ;
			 pws.flush();
			 SaveManager.savePlaneFury(WorldGen.plane,fos);
		     extra.println("Saved!");
		     File f = new File("trawel"+str+".save");
		     extra.println("Slot "+str + ": "+f.length() + " bytes.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String checkNameInFile(String str) {
		String ret = "";
		try (FileReader fr = new FileReader("trawel"+str+".save"); BufferedReader br = new BufferedReader(fr);){
			ArrayList<Integer> values = new ArrayList<Integer>();
			while (true) {
				int red = br.read();
				if (red == 0) {
					break;
				}
				if (red == -1) {
					extra.println("Invaild file.");
					break;
				}
				values.add(red);
			}
			for (int i = 0; i < values.size();i++) {
				ret+=(char)(int)values.get(i);
			}
			 br.close();
			 fr.close();
		} catch (Exception e) {
			ret = "n/a";
		}
		return ret;
	}

	public static void load(String str) {
		int len;
		try (FileInputStream fos = new FileInputStream("trawel"+str+".save");){
			while (fos.read() != '\0');
			WorldGen.plane = SaveManager.readPlaneFury(fos);
			Player.player = WorldGen.plane.getPlayer();
			Player.bag = Player.player.getPerson().getBag();
			Player.player.skillUpdate();
			Player.passTime = 0;
			extra.getThreadData().world = Player.player.getWorld();
			fos.close();
			WorldGen.plane.reload();
		} catch (IOException e) {
			if (!mainGame.logStreamIsErr) {
				e.printStackTrace();
			}
			extra.println("Invalid load. Either no save file was found or it was outdated.");
		}
	}
	
}
