/**
 * 
 */
package cryptic.network.game.module;

import static cryptic.network.game.CrypticMain.get;

import org.bukkit.ChatColor;

import mkremins.fanciful.FancyMessage;
import cryptic.network.game.cmdframework.Command;
import cryptic.network.game.cmdframework.CommandArgs;
import cryptic.network.game.cmdframework.CommandListener;
import cryptic.network.game.util.CUtil;

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
				"<green>Running <gold>%s <green>v<gold>%s <green>with <gold>%d <green>module%s loaded.",
				get().getDescription().getName(), get().getDescription()
						.getVersion(), ModuleCore.getModules().size(),
				ModuleCore.getModules().size() == 1 ? "" : "s");
		CUtil.send(info.getSender(),
				"<green>For more information about the modules do \"/module help\"!");
	}

	@Command(name = "module.help", permission = "cryptic.module.help")
	public void modulehelp(CommandArgs info)
	{
		String[] msg = new String[]
		{ "<gold>Module Core Commands:",
				"/module help - Displays this message.",
				"/module list - Lists the current running modules.",
				"/module reload - Reloads the modules.",
				"/module version - Shows the version for the module core.",
				"/module info [id] - Shows the information about a certain module." };

		for (String line : msg)
			CUtil.send(info.getSender(), "<green>" + line);
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
									"Name: " + module.getInfo().name(),
									"ID: " + module.getInfo().id(),
									"Version: " + module.getInfo().version(),
									"Author: " + module.getInfo().author(),
									"Description: "
											+ module.getInfo().description(),
									"",
									CUtil.format("<gray><italics>Click for more information"), })
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
		CUtil.send(info.getSender(), "<green>Module Core was reloaded!");
	}
}
