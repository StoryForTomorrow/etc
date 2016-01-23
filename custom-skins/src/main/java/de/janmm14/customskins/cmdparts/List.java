package de.janmm14.customskins.cmdparts;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CmdPart;

import com.google.common.collect.Lists;
import lombok.NonNull;

public class List extends CmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins list [page]").color(ChatColor.DARK_GRAY).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins list"))
		.append(" - ").color(ChatColor.DARK_GRAY)
		.append("lists all downloaded skins").color(ChatColor.DARK_GRAY).create();

	public List() {
		super("customskins.list", "list");
	}

	@Override
	public void onCommand(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		cs.sendMessage("This command is not available yet. Look for future updates of this plugin.");
		// TODO implement
	}

	@NonNull
	@Override
	public java.util.List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		return Lists.newArrayList();
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§8/customskins list [page] - lists all downloaded skins");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
