package scimech.handlers;

import java.lang.reflect.InvocationTargetException;

public class SaveHandler {

	/*
	switch (getName.toLowerCase()) {
	//fixtures just create new fixtures
	//systems just create new systems
	//mounts create mounts from traits and then call deserialize on all their fixtures
	//mechs do the same but with mounts and systems
	//pilots TODO
	}*/
	public static Savable deserialize(String s) throws Exception {
		String getName = s.substring(0,s.indexOf('&'));
		return (Savable)Class.forName(getName).getMethod("deserialize", String.class).invoke(null,s);
	}
}
