/**
 * 
 */
package cryptic.network.module;

import static cryptic.network.CrypticMain.get;

import java.util.logging.Level;

import org.bukkit.ChatColor;

import mkremins.fanciful.FancyMessage;
import cryptic.network.cmdframework.Command;
import cryptic.network.cmdframework.CommandArgs;
import cryptic.network.cmdframework.CommandListener;
import cryptic.network.util.CUtil;

/**
 * @author 598Johnn897
 *
 */
public class ModuleCommand implements CommandListener
{
	@Command(name = "module", permission = "cryptic.module", aliases =
	{ "md" })
	public void module(CommandArgs info)
	{
		CUtil.send(
				info.getSender(),
				"Running <green>%s <reset>v<green>%s <reset>with <green>%d <reset>module%s loaded.",
				get().getDescription().getName(), get().getDescription()
						.getVersion(), ModuleCore.getModules().size(),
				ModuleCore.getModules().size() == 1 ? "" : "s");
		CUtil.send(info.getSender(),
				"For more information about the modules do \"<green>/module help<reset>\"!");
	}

	@Command(name = "module.help", permission = "cryptic.module.help")
	public void modulehelp(CommandArgs info)
	{
		String[] msg = new String[]
		{ "Module Core Commands:",
				"/module help <green>- <reset>Displays this message.",
				"/module list <green>- <reset>Lists the current running modules.",
				"/module reload <green>- <reset>Reloads the modules.",
				"/module version <green>- <reset>Shows the version for the module core.",
				"/module info [id] <green>- <reset>Shows the information about a certain module." };

		for (String line : msg)
			CUtil.send(info.getSender(), line);
	}

	@Command(name = "module.list", permission = "cryptic.module.list")
	public void modulelist(CommandArgs info) throws ModuleException
	{
		FancyMessage msg = new FancyMessage("Modules (")
				.then(Integer.toString(ModuleCore.getModules().size()))
				.color(ChatColor.GREEN).then("): ").color(ChatColor.WHITE);
		for (Module module : ModuleCore.getModules())
		{
			msg.then(module.getInfo().name())
					.color(ChatColor.GREEN)
					.tooltip(
							new String[]
							{
									CUtil.f("Name: <green>%s", module.getInfo().name()),
									CUtil.f("ID: <green>%s", module.getInfo().id()),
									CUtil.f("Version: <green>%s", module.getInfo().version()),
									CUtil.f("Author: <green>%s", module.getInfo().author()),
									CUtil.f("Description: <green>%s", module.getInfo().description()),
									"",
									CUtil.f("<gray><italics>Click for more information"), })
					.command("/module info " + module.getInfo().id());
			if (ModuleCore.getModules().size() > 1) msg.then(", ");
		}
		
		msg.send(info.getSender());
	}

	@Command(name = "module.info", permission = "cryptic.module.info")
	public void moduleinfo(CommandArgs info)
	{
		if (info.getArgs().length == 0)
		{
			CUtil.send(info.getSender(),
					"Module Core v%s running on %s with %s modules loaded.",
					ModuleCore.getVERSION(), get().getServer().getVersion(),
					ModuleCore.getModules().size());
		}
		else
		{
			CUtil.send(info.getSender(),
					"<gold>This feature is currently being worked on!");
		}
	}

	@Command(name = "module.reload", permission = "cryptic.module.reload")
	public void modulereload(CommandArgs info) throws ModuleException
	{
		new ModuleCore().reload();
		CUtil.sendop(Level.INFO, info.getSender(), "<green>Module Core was reloaded!");
	}
}
