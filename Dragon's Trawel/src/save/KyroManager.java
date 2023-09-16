package save;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.*;

import trawel.extra;
import trawel.towns.Plane;

import java.io.*;
import java.util.EnumMap;
import java.util.EnumSet;

import org.objenesis.strategy.SerializingInstantiatorStrategy;

public class KyroManager {
	
	public static Kryo trawelKryo;

	public static final void trawelRegister() {
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
		trawelKryo.setReferences(true);
		trawelKryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
		
		//trawelKryo.setAutoReset(true);
	}
	
	public static final void savePlane(Plane plane, FileOutputStream file) {
		Output out = new Output(file);
		trawelKryo.writeObject(out, plane);
		extra.println("Depth: " +trawelKryo.getDepth());
		out.flush();
		//trawelKryo.reset();
	}
	
	public static final Plane readPlane(FileInputStream file) {
		Input in = new Input(file);
		Plane p = trawelKryo.readObject(in,Plane.class);
		//trawelKryo.reset();
		return p;
	}
	
}
