package de.janmm14.customskins.cmdparts;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CmdPart;

import com.google.common.collect.Lists;
import lombok.NonNull;

public class Info extends CmdPart {

	public static final BaseComponent[] USAGE_INFO = new ComponentBuilder("/customskins info <player>").color(ChatColor.DARK_GRAY).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins info "))
		.append(" - ").color(ChatColor.DARK_GRAY)
		.append("shows informations about the current skin of the given player").color(ChatColor.DARK_GRAY).create();

	public Info() {
		super("customskins.info", "info");
	}

	@Override
	public void onCommand(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		cs.sendMessage("This command is not available yet. Look for future updates of this plugin.");
		// TODO implement
	}

	@NonNull
	@Override
	public List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] args) {
		if (args.length == 0)
			return Lists.newArrayList();

		String s = args[0].toLowerCase();
		List<String> l = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			String name = p.getName();
			if (name.toLowerCase().startsWith(s)) {
				l.add(name);
			}
		}
		return l;
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§8/customskins info <player> - shows informations about the current skin of the given player");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE_INFO);
	}
}
