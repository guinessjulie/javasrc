package introspection;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

/**
 * Demonstration of a ClassLoader
 */
public class ClassLoaderMultiple extends ClassLoader {
	private static final String CLASS_TO_LOAD = "MultiDemo";
	/** The Hashtable to keep track of classes, to avoid re-loading them */
	protected Hashtable cache = new Hashtable();

	/** "load", that is, make up, the data for the class */
	private byte[] genClassData(String name) {
		File f = new File(name + ".class");
		if (!f.exists()) {
			throw new IllegalArgumentException(name);
		}
		long dataLength = f.length();
		byte[] bd = new byte[(int)dataLength];
		try {
			InputStream is = new FileInputStream(f);
			is.read(bd);
			is.close();
		} catch (IOException ex) {
			throw new IllegalArgumentException(ex.toString());
		}
		return bd;
	}

	public synchronized Class loadClass(String name, boolean resolve) 
			throws ClassNotFoundException { 
		/** We can expect to be called to resolve at least demo's
		 * superclass (java.lang.Object). Fortunatetely, we can just
		 * use super.findSystemClass() to load such things...
		 */
		if (name.startsWith("java.")) {
			System.out.println("loadClass: SystemLoading " + name);
			return findSystemClass(name);
		}
		Class c = (Class)cache.get(name);
		if (c == null) {
			System.out.println("loadClass: About to genClassData " + name);
			byte mydata[] = genClassData(name);
			System.out.println("loadClass: About to defineClass " + name);
			c = defineClass(name, mydata, 0, mydata.length);
			System.out.println("loadClass: storing " + name + " in cache.");
			cache.put(name, c);
		} else
			System.out.println("loadClass: found " + name + " in cache.");
		if (resolve) {
			System.out.println("loadClass: About to resolveClass " + name);
			resolveClass(c);
		}
		return c;
	}

	public static void main(String[] argv) {
		System.out.println("ClassLoaderDemo starting");
		ClassLoaderMultiple loader = new ClassLoaderMultiple();
		Class c = null;
		Object demo;
		try {
			/* Load the "Demo" class from memory */

			System.out.println("About to load class  Demo");
			c = loader.loadClass(CLASS_TO_LOAD, true);
			System.out.println("About to instantiate class Demo");
			demo = c.newInstance();
			System.out.println("Got Demo class loaded: " + demo);

			/* Now try to call a method */

			Method mi = c.getMethod("test", null);
			mi.invoke(null, null);
			
			/** Try to instantiate a second ClassLoader */
			System.out.println("Creating second ClassLoader");
			ClassLoaderMultiple loader2 = new ClassLoaderMultiple();
			System.out.println("Instantiated another ClassLoader...");
			
			Class d = loader2.loadClass(CLASS_TO_LOAD, true);
			
			if (c == d) {
				System.out.println("Classloaders returned the same Class object!");
			} else {
				System.out.println("Classloaders returned different Class objects!");
			}
			System.out.println("The equals() comparison on Classes returns " +
				c.equals(d));
			
			Method mi2 = d.getMethod("test", null);
			mi2.invoke(null, null);

		} catch (InvocationTargetException e) {
			// The invoked method threw an exception. We get it
			// wrapped up inside another exception, hence the
			// extra call here:
			e.getTargetException().printStackTrace();
			System.out.println("Could not run test method");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not run test method");
		}
	}
}
