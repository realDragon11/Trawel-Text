package save;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import trawel.extra;
import trawel.personal.Person;
import trawel.personal.item.solid.Weapon;
import trawel.towns.Plane;
import trawel.towns.Town;
import trawel.towns.World;

public class KyroManager {
	
	public static Kryo trawelKryo;
	public static Fury fury;

	public static final void trawelRegister() {
		/**
		 * https://github.com/magro/kryo-serializers/tree/master
		 */
		/*
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
		trawelKryo.register(derg.TwinListMap.class, new FieldSerializer<derg.TwinListMap>(trawelKryo, derg.TwinListMap.class));
		trawelKryo.setReferences(true);
		trawelKryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());*/
		
		//trawelKryo.setAutoReset(true);
		LoggerFactory.disableLogging();
		fury = Fury.builder().withLanguage(Language.JAVA)
				.withRefTracking(true)
		        .requireClassRegistration(false)
		        .build();
	}
	
	public static final void savePlane(Plane plane, FileOutputStream file) {
		Output out = new Output(file);
		trawelKryo.writeObject(out, plane);
		out.flush();
		//trawelKryo.reset();
	}
	
	public static final void savePlaneFury(Plane plane,FileOutputStream file) {
		/*Plane p = (Plane) fury.deserialize(fury.serialize(plane));
		for (Town t: p.getTowns()) {
			extra.println(t.getName());
		}*/
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
	
	public static final Plane readPlane(FileInputStream file) {
		Input in = new Input(file);
		Plane p = trawelKryo.readObject(in,Plane.class);
		Person per = p.getPlayer().getPerson();
		World w = p.getPlayer().getWorld();
		extra.println(per.getName() +": Level " + per.getLevel() + ", "+per.xpString() + " XP");
		extra.println(w.getName() + ": Year " +w.getCalender().dateYear());
		//trawelKryo.reset();
		return p;
	}
	
}
