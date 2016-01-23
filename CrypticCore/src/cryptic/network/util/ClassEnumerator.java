package cryptic.network.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Util class for searching the project for a list of classes.
 * 
 * @since 0.0.1-SNAPSHOT
 * 
 * @author Not2EXceL
 */
public class ClassEnumerator
{

	private static volatile ClassEnumerator instance;

	/**
	 * This returns the instance of this class. If the instance is null it will
	 * create a new instance and return it.
	 * 
	 * @return The instance of this class
	 */
	public static ClassEnumerator getInstance()
	{
		if (instance == null)
		;
		{
			instance = new ClassEnumerator();
			return instance;
		}
	}

	public List<Class<?>> getClassesFromLocation(File location)
	{
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		if (location.isDirectory())
		{
			for (File file : Arrays.asList(location.listFiles()))
			{
				try
				{
					ClassLoader classLoader = new URLClassLoader(new URL[]
					{ file.toURI().toURL() }, this.getClass().getClassLoader());
					if (file.getName().toLowerCase().trim().endsWith(".class"))
					{
						classes.add(classLoader.loadClass(file.getName()
								.replace(".class", "").replace("/", ".")));
					}
					else if (file.getName().toLowerCase().trim()
							.endsWith(".jar"))
					{
						classes.addAll(getClassesFromJar(file, classLoader));
					}
					else if (file.isDirectory())
					{
						classes.addAll(getClassesFromLocation(file));
					}
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			try
			{
				ClassLoader classLoader = new URLClassLoader(new URL[]
				{ location.toURI().toURL() }, this.getClass().getClassLoader());
				if (location.getName().toLowerCase().trim().endsWith(".class"))
				{
					classes.add(classLoader.loadClass(location.getName()
							.replace(".class", "").replace("/", ".")));
				}
				else if (location.getName().toLowerCase().trim()
						.endsWith(".jar"))
				{
					classes.addAll(getClassesFromJar(location, classLoader));
				}
				else if (location.isDirectory())
				{
					classes.addAll(getClassesFromLocation(location));
				}
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return classes;
	}

	@SuppressWarnings("resource")
	public Class<?>[] getClassesFromThisJar(Object object)
	{
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader classLoader = null;
		URI uri = null;
		try
		{
			uri = object.getClass().getProtectionDomain().getCodeSource()
					.getLocation().toURI();
			classLoader = new URLClassLoader(new URL[]
			{ uri.toURL() }, ClassEnumerator.class.getClassLoader());
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		if (uri == null) { throw new RuntimeException("No uri for "
				+ this.getClass().getProtectionDomain().getCodeSource()
						.getLocation()); }
		if (classLoader == null) { throw new RuntimeException(
				"No classLoader for "
						+ this.getClass().getProtectionDomain().getCodeSource()
								.getLocation()); }
		File file = new File(uri);
		classes.addAll(getClassesFromLocation(file));
		return classes.toArray(new Class[classes.size()]);
	}

	public List<Class<?>> getClassesFromJar(File file, ClassLoader classLoader)
	{
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		try
		{
			final JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> enumeration = jarFile.entries();
			while (enumeration.hasMoreElements())
			{
				final JarEntry jarEntry = enumeration.nextElement();
				if (jarEntry.isDirectory()
						|| !jarEntry.getName().toLowerCase().trim()
								.endsWith(".class"))
				{
					continue;
				}
				classes.add(classLoader.loadClass(jarEntry.getName()
						.replace(".class", "").replace("/", ".")));
			}
			jarFile.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return classes;
	}
}