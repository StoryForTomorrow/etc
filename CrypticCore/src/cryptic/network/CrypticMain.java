/**
 * 
 */
package cryptic.network;

import static cryptic.network.lib.References.CRYPTIC_JSON;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import lombok.Getter;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import cryptic.network.cmdframework.CommandFramework;
import cryptic.network.lib.References;
import cryptic.network.module.ModuleCore;
import cryptic.network.module.ModuleException;
import cryptic.network.module.Registry;
import cryptic.network.util.JsonCache;

/**
 * @author 598Johnn897
 *
 */
public class CrypticMain extends JavaPlugin
{
	private static CrypticMain instance;

	public static CrypticMain get()
	{
		Validate.notNull(instance);
		return instance;
	}

	public final CrypticLogger clogger = new CrypticLogger(this);
	@Getter public CommandFramework cmdFramework;

	@Getter public ModuleCore moduleCore;
	
	private int start, end;

	@Override
	public void onLoad()
	{
		instance = this;
		start = (int) System.currentTimeMillis();

		if (!this.getDataFolder().exists())
		{
			File dir = this.getDataFolder();
			dir.mkdir();
		}

		try
		{
			CRYPTIC_JSON = new JsonCache(new File(CrypticMain.get()
					.getDataFolder().getAbsolutePath()
					+ References.CRYPTIC_JSON_FILE), new URL(
					References.CRYPTIC_JSON_URL), 15).getJson();
		}
		catch (IOException | JSONException e)
		{
			e.printStackTrace();
			try { CRYPTIC_JSON = new JSONObject().put("error", e.toString()); }
			catch (JSONException e1) { e1.printStackTrace(); }
		}

		try
		{
			References.MODULE_DIRECTORY = References.WORKING_DIRECTORY
					+ CRYPTIC_JSON.getString("module_directory");
			
			clogger.log(Level.INFO, CRYPTIC_JSON.getJSONObject("messages")
					.getString("loading"), new Object[]
			{ this.getDescription().getName(),
					this.getDescription().getVersion() });
		}
		catch (JSONException e1)
		{
			e1.printStackTrace();
		}
	}

	@Override
	public void onEnable()
	{
		try
		{
			cmdFramework = new CommandFramework(get());
			cmdFramework.registerCommands(get());
			cmdFramework.registerHelp();

			Registry.registerEvents(get());

			moduleCore = new ModuleCore();
			moduleCore.init();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			end = (int) (System.currentTimeMillis() - start);
			try
			{
				clogger.log(Level.INFO, CRYPTIC_JSON.getJSONObject("messages")
						.getString("enabled"), new Object[]
				{ this.getDescription().getName(),
						this.getDescription().getVersion(), end });
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onDisable()
	{
		try
		{
			moduleCore.disable();

			clogger.log(
					Level.INFO,
					CRYPTIC_JSON.getJSONObject("messages")
							.getString("disabled"),
					new Object[]
					{
							this.getDescription().getName(),
							this.getDescription().getVersion(),
							TimeUnit.MILLISECONDS.toMinutes(System
									.currentTimeMillis() - start) });
		}
		catch (JSONException | ModuleException e)
		{
			e.printStackTrace();
		}
	}
}
