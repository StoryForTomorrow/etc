/**
 * 
 */
package cryptic.network.module;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import lombok.Getter;
import static cryptic.network.CrypticMain.get;
import cryptic.network.CrypticMain;
import cryptic.network.lib.References;
import cryptic.network.util.ClassEnumerator;

/**
 * @author 598Johnn897
 *
 */
public class ModuleCore
{
	// DO NOT CHANGE UNLESS TOLD
	@Getter 
	static String VERSION = "0.1-ALPHA";
	
	@Getter 
	static List<Module> modules = new ArrayList<Module>();


	List<Module> found_modules = new ArrayList<Module>(),
			loaded_modules = new ArrayList<Module>(),
			enabled_modules = new ArrayList<Module>();			 

	HashMap<String, Module> byId = new HashMap<String, Module>();
	HashMap<String, ModuleInfo> infoById = new HashMap<String, ModuleInfo>();

	public ModuleCore()
	{
		if (!new File(References.MODULE_DIRECTORY).exists()) new File(
				References.MODULE_DIRECTORY).mkdir();
	}

	public void init() throws ModuleException
	{
		try { found_modules = this.searchForModules(new File(
					References.MODULE_DIRECTORY)); }
		catch (ModuleException e) { e.printStackTrace(); }

		load();
		enable();
	}

	public void load() throws ModuleException
	{
		get().clogger.log(Level.INFO, "Loading modules...");
		for (Module module : found_modules)
			if (!loaded_modules.contains(module))
			{
				loadModule(module);
				
				if (!modules.contains(module))
					modules.add(module);
			}
			else
			{
				get().clogger.log(Level.SEVERE,
						"Duplicate instance of module id {0}", module.getInfo()
								.id());
				this.disableModule(module);
			}
	}

	public void enable() throws ModuleException
	{
		get().clogger.log(Level.INFO, "Enabling modules...");
		for (Module module : loaded_modules) {
			if (!byId.containsKey(module.getInfo().id())) 
			{ 
				enableModule(module);
				
				byId.put(module.getInfo().id(), module);
				infoById.put(module.getInfo().id(), module.getInfo());
				
				if (!modules.contains(module))
					modules.add(module);
			}
			else 
			{
				get().clogger.log(Level.SEVERE,
						"Duplicate instance of Module: {0}", module.getInfo().id());
				this.disableModule(module);
			}
		}
	}

	public void disable() throws ModuleException
	{
		get().clogger.log(Level.INFO, "Disabling modules...");
		for (Module module : enabled_modules)
			disableModule(module);
		
		found_modules.clear();
		loaded_modules.clear();
		enabled_modules.clear();
		
		modules.clear();
		
		byId.clear();
		infoById.clear();
		
	}
	
	public void reload() throws ModuleException 
	{
		disable();
		init();
	}

	void loadModule(Module module) throws ModuleException
	{
		try
		{
			module.load();
		}
		catch (Exception e)
		{
			throw new ModuleException(
					"An Error has occurred while loading module: "
							+ module.getInfo().name());
		}
		finally
		{
			loaded_modules.add(module);
			get().clogger.log(Level.INFO,
					"Loaded module {0} v{1} id{2}!", new Object[]
					{ module.getInfo().name(), module.getInfo().version(),
							module.getInfo().id() });
		}
	}

	void enableModule(Module module) throws ModuleException
	{
		try
		{
			Registry.registerModule(module);
	
			module.enable();
		}
		catch (Exception e)
		{
			throw new ModuleException(
					"An Error has occurred while enabling module: "
							+ module.getInfo().name());
		}
		finally
		{
			enabled_modules.add(module);
			
			get().clogger.log(Level.INFO,
					"Enabled module {0} v{1} id{2}!", new Object[]
					{ module.getInfo().name(), module.getInfo().version(),
							module.getInfo().id() });
		}
	}

	void disableModule(Module module) throws ModuleException
	{
		try
		{
			module.disable();

			Registry.unregisterEvents(module);
			
			if (found_modules.contains(module)) found_modules.remove(module);
			if (loaded_modules.contains(module)) loaded_modules.remove(module);
			// if (enabled_modules.contains(module)) enabled_modules.remove(module);
		}
		catch (Exception e)
		{
			throw new ModuleException(
					"An Error has occurred while disabling module: "
							+ module.getInfo().name());
		}
		finally
		{
			get().clogger.log(Level.INFO,
					"Disabled module {0} v{1} id{2}!", new Object[]
					{ module.getInfo().name(), module.getInfo().version(),
							module.getInfo().id() });
		}
	}
	
	public static ArrayList<String> getModuleNames() throws ModuleException
	{
		ArrayList<String> names = new ArrayList<String>();
		for (Module module : modules)
		{
			try
			{
				names.add(module.getInfo().name());
			}
			catch (ModuleException e)
			{
				throw new ModuleException("An error has occurred while getting a modules information!");
			}
		}
		return names;
	}

	private List<Module> searchForModules(File file) throws ModuleException
	{
		List<Module> modules = new ArrayList<Module>();

		List<Class<?>> classes = ClassEnumerator.getInstance()
				.getClassesFromLocation(file);

		if (classes == null || classes.size() == 0) throw new ModuleException(
				"No modules found!");
		else get().clogger.log(Level.INFO,
				"Found {0} files... searching for modules...", classes.size());

		for (Class<?> c : classes)
			if (Module.class.isAssignableFrom(c) && !c.isInterface()
					&& !c.isEnum() && !c.isAnnotation())
				if (c.getAnnotation(ModuleInfo.class) != null)
					try
					{ modules.add((Module) c.newInstance()); }
					catch (InstantiationException | IllegalAccessException e)
					{ e.printStackTrace(); }
				else
					throw new ModuleException(
							"Module class found but does not have ModuleInfo annotation!: "
									+ c.getPackage());

		get().clogger.log(Level.INFO, "Detected {0} modules!",
				modules.size());

		return modules;
	}
	
}
