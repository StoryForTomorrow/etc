package cryptic.tags.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cryptic.network.cmdframework.Command;
import cryptic.network.cmdframework.CommandArgs;
import cryptic.network.cmdframework.CommandListener;
import cryptic.network.util.CUtil;
import cryptic.network.util.TagUtil;

/**
 * 
 * @author 598Johnn897
 * @since 0.0.1-SNAPSHOT
 */
public class TagCommand implements CommandListener
{
	@Command(name = "tag", permission = "tm.tag", aliases =
	{ "nameplate", "nametag", "nameabovehead" })
	public void tag(CommandArgs info)
	{
		info.getSender().sendMessage(
				CUtil.formatColors("<red>Usage: /tag <set|remove>"));
	}

	@Command(name = "tag.set", permission = "tm.tag.set")
	public void settag(CommandArgs info)
	{
		info.getSender()
				.sendMessage(
						CUtil.formatColors("<red>Usage: /tag set <prefix|suffix|color>"));
	}

	@Command(name = "tag.set.prefix", permission = "tm.tag.set.prefix")
	public void settagprefix(CommandArgs info)
	{
		if (info.getArgs().length == 0)
		{
			info.getSender()
					.sendMessage(
							CUtil.formatColors("<red>Usage: /tag set prefix <player> <prefix...>"));
		}
		else
		{
			if (Bukkit.getPlayer(info.getArgs()[0]) != null)
			{
				Player player = Bukkit.getPlayer(info.getArgs()[0]);
				TagUtil.setTagPrefix(player, info.getFinalArg(1));
				info.getSender()
						.sendMessage(
								CUtil.f(
										"<green>Prefix \"%s\"<green> has been added to player: <aqua>%s<white>.",
										info.getFinalArg(1), player.getName()));
			}
			else
			{
				info.getSender().sendMessage(
						CUtil.f("<red>Player \"%s\" could not be found",
								info.getArgs()[0]));
			}
		}
	}

	@Command(name = "tag.set.suffix", permission = "tm.tag.set.suffix")
	public void settagsuffix(CommandArgs info)
	{
		if (info.getArgs().length == 0)
		{
			info.getSender()
					.sendMessage(
							CUtil.formatColors("<red>Usage: /tag set suffix <player> <suffix...>"));
		}
		else
		{
			if (Bukkit.getPlayer(info.getArgs()[0]) != null)
			{
				Player player = Bukkit.getPlayer(info.getArgs()[0]);
				TagUtil.setTagSuffix(player, info.getFinalArg(1));
				info.getSender()
						.sendMessage(
								CUtil.f(
										"<green>Suffix \"%s\" <green>has been added to player: <aqua>%s<white>.",

										info.getFinalArg(1), player.getName()));
			}
			else
			{
				info.getSender().sendMessage(
						CUtil.f("<red>Player \"%s\" could not be found",
								info.getArgs()[0]));
			}
		}
	}

	@Command(name = "tag.remove", permission = "tm.tag.remove")
	public void tagremove(CommandArgs info)
	{
		info.getSender()
				.sendMessage(
						CUtil.formatColors("<red>Usage: /tag remove <prefix|suffix|all> <player>"));
	}

	@Command(name = "tag.remove.prefix", permission = "tm.tag.remove.prefix")
	public void tagremoveprefix(CommandArgs info)
	{
		if (info.getArgs().length == 0)
		{
			info.getSender()
					.sendMessage(
							CUtil.formatColors("<red>Usage: /tag remove prefix <player>"));
		}
		else
		{
			if (Bukkit.getPlayer(info.getArgs()[0]) != null)
			{
				Player player = Bukkit.getPlayer(info.getArgs()[0]);
				TagUtil.removeTagPrefix(player);
				info.getSender()
						.sendMessage(
								CUtil.f(
										"<green>Prefix has been removed from player: <aqua>%s<white>.",

										player.getName()));
			}
			else
			{
				info.getSender().sendMessage(
						CUtil.f("<red>Player \"%s\" could not be found",
								info.getArgs()[0]));
			}
		}
	}

	@Command(name = "tag.remove.suffix", permission = "tm.tag.remove.suffix")
	public void tagremovesuffix(CommandArgs info)
	{
		if (info.getArgs().length == 0)
		{
			info.getSender()
					.sendMessage(
							CUtil.formatColors("<red>Usage: /tag remove suffix <player>"));
		}
		else
		{
			if (Bukkit.getPlayer(info.getArgs()[0]) != null)
			{
				Player player = Bukkit.getPlayer(info.getArgs()[0]);
				TagUtil.removeTagSuffix(player);
				info.getSender()
						.sendMessage(
								CUtil.f(
										"<green>Suffix has been removed from player: <aqua>%s<white>.",

										player.getName()));
			}
			else
			{
				info.getSender().sendMessage(
						CUtil.f("<red>Player \"%s\" could not be found",
								info.getArgs()[0]));
			}
		}
	}

	@Command(name = "tag.remove.all", permission = "tm.tag.remove.all")
	public void tagremoveall(CommandArgs info)
	{
		if (info.getArgs().length == 0)
		{
			info.getSender().sendMessage(
					CUtil.formatColors("<red>Usage: /tag remove all <player>"));
		}
		else
		{
			if (Bukkit.getPlayer(info.getArgs()[0]) != null)
			{
				Player player = Bukkit.getPlayer(info.getArgs()[0]);
				TagUtil.removeAllTags(player);
				info.getSender()
						.sendMessage(
								CUtil.f(
										"<green>All tags have been removed from player: <aqua>%s<white>.",

										player.getName()));
			}
			else
			{
				info.getSender().sendMessage(
						CUtil.f("<red>Player \"%s\" could not be found",
								info.getArgs()[0]));
			}
		}
	}

	@Command(name = "tag.set.color", permission = "tm.tag.set.color")
	public void setcolor(CommandArgs info)
	{
		if (info.getArgs().length == 0)
		{
			info.getSender()
					.sendMessage(
							CUtil.formatColors("<red>Usage: /tag set color <player> <color>"));
		}
		else
		{
			if (Bukkit.getPlayer(info.getArgs()[0]) != null)
			{
				Player player = Bukkit.getPlayer(info.getArgs()[0]);
				ChatColor color = ChatColor.WHITE;
				try
				{
					color = ChatColor
							.valueOf(info.getFinalArg(1).toUpperCase());
				}
				catch (Exception e)
				{
					info.getSender().sendMessage(
							CUtil.f("<red>Color \"%s\" is invalid!",

							info.getFinalArg(1)));
					return;
				}
				TagUtil.setPlayerColor(player, color);
				info.getSender()
						.sendMessage(
								CUtil.f(
										"<green>Color \"%s%s<green>\" has been added player: <aqua>%s<white>.",

										color.toString(), info.getFinalArg(1),
										player.getName()));
			}
			else
			{
				info.getSender().sendMessage(
						CUtil.f("<red>Player \"%s\" could not be found",
								info.getArgs()[0]));
			}
		}
	}

	@Command(name = "tag.remove.color", permission = "tm.tag.remove.color")
	public void removecolor(CommandArgs info)
	{
		if (info.getArgs().length == 0)
		{
			info.getSender()
					.sendMessage(
							CUtil.formatColors("<red>Usage: /tag remove color <player>"));
		}
		else
		{
			if (Bukkit.getPlayer(info.getArgs()[0]) != null)
			{
				Player player = Bukkit.getPlayer(info.getArgs()[0]);
				TagUtil.resetPlayerColor(player);
				info.getSender()
						.sendMessage(
								CUtil.f(
										"<green>Name color has been reset for player: <aqua>%s<white>.",

										player.getName()));
			}
			else
			{
				info.getSender().sendMessage(
						CUtil.f("<red>Player \"%s\" could not be found",
								info.getArgs()[0]));
			}
		}
	}

	// TODO vv Pretty buggy and doesn't work how intended vv
	/*
	 * @Completer(command = "tag") public List<String> tagCompleter(CommandArgs
	 * args) { List<String> list = new ArrayList<String>(); list.add("set");
	 * list.add("remove"); return list; }
	 * 
	 * @Completer(command = "tag.set") public List<String>
	 * settagCompleter(CommandArgs args) { List<String> list = new
	 * ArrayList<String>(); list.add("prefix"); list.add("suffix"); return list;
	 * }
	 * 
	 * @Completer(command = "tag.remove") public List<String>
	 * removetagCompleter(CommandArgs args) { List<String> list = new
	 * ArrayList<String>(); list.add("prefix"); list.add("suffix");
	 * list.add("all");
	 * 
	 * return list; }
	 */
}